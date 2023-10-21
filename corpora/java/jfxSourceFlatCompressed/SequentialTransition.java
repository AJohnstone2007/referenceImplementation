package javafx.animation;
import com.sun.javafx.animation.TickCalculation;
import static com.sun.javafx.animation.TickCalculation.*;
import java.util.Arrays;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;
import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.collections.VetoableListDecorator;
import com.sun.scenario.animation.AbstractPrimaryTimer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
public final class SequentialTransition extends Transition {
private static final Animation[] EMPTY_ANIMATION_ARRAY = new Animation[0];
private static final int BEFORE = -1;
private static final double EPSILON = 1e-12;
private Animation[] cachedChildren = EMPTY_ANIMATION_ARRAY;
private long[] startTimes;
private long[] durations;
private long[] delays;
private double[] rates;
private boolean[] forceChildSync;
private int end;
private int curIndex = BEFORE;
private long oldTicks = 0L;
private long offsetTicks;
private boolean childrenChanged = true;
private boolean toggledRate;
private final InvalidationListener childrenListener = observable -> {
childrenChanged = true;
if (getStatus() == Status.STOPPED) {
setCycleDuration(computeCycleDuration());
}
};
private final ChangeListener<Number> rateListener = new ChangeListener<Number>() {
@Override
public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
if (oldValue.doubleValue() * newValue.doubleValue() < 0) {
for (int i = 0; i < cachedChildren.length; ++i) {
Animation child = cachedChildren[i];
child.clipEnvelope.setRate(rates[i] * Math.signum(getCurrentRate()));
}
toggledRate = true;
}
}
};
private ObjectProperty<Node> node;
private static final Node DEFAULT_NODE = null;
public final void setNode(Node value) {
if ((node != null) || (value != null )) {
nodeProperty().set(value);
}
}
public final Node getNode() {
return (node == null)? DEFAULT_NODE : node.get();
}
public final ObjectProperty<Node> nodeProperty() {
if (node == null) {
node = new SimpleObjectProperty<Node>(this, "node", DEFAULT_NODE);
}
return node;
}
private final Set<Animation> childrenSet = new HashSet<Animation>();
private final ObservableList<Animation> children = new VetoableListDecorator<Animation>(new TrackableObservableList<Animation>() {
@Override
protected void onChanged(Change<Animation> c) {
while (c.next()) {
for (final Animation animation : c.getRemoved()) {
animation.parent = null;
animation.rateProperty().removeListener(childrenListener);
animation.totalDurationProperty().removeListener(childrenListener);
animation.delayProperty().removeListener(childrenListener);
}
for (final Animation animation : c.getAddedSubList()) {
animation.parent = SequentialTransition.this;
animation.rateProperty().addListener(childrenListener);
animation.totalDurationProperty().addListener(childrenListener);
animation.delayProperty().addListener(childrenListener);
}
}
childrenListener.invalidated(children);
}
}) {
@Override
protected void onProposedChange(List<Animation> toBeAdded, int... indexes) {
IllegalArgumentException exception = null;
for (int i = 0; i < indexes.length; i+=2) {
for (int idx = indexes[i]; idx < indexes[i+1]; ++idx) {
childrenSet.remove(children.get(idx));
}
}
for (Animation child : toBeAdded) {
if (child == null) {
exception = new IllegalArgumentException("Child cannot be null");
break;
}
if (!childrenSet.add(child)) {
exception = new IllegalArgumentException("Attempting to add a duplicate to the list of children");
break;
}
if (checkCycle(child, SequentialTransition.this)) {
exception = new IllegalArgumentException("This change would create cycle");
break;
}
}
if (exception != null) {
childrenSet.clear();
childrenSet.addAll(children);
throw exception;
}
}
};
private static boolean checkCycle(Animation child, Animation parent) {
Animation a = parent;
while (a != child) {
if (a.parent != null) {
a = a.parent;
} else {
return false;
}
}
return true;
}
public final ObservableList<Animation> getChildren() {
return children;
}
public SequentialTransition(Node node, Animation... children) {
setInterpolator(Interpolator.LINEAR);
setNode(node);
getChildren().setAll(children);
}
public SequentialTransition(Animation... children) {
this(null, children);
}
public SequentialTransition(Node node) {
setInterpolator(Interpolator.LINEAR);
setNode(node);
}
public SequentialTransition() {
this((Node) null);
}
SequentialTransition(AbstractPrimaryTimer timer) {
super(timer);
setInterpolator(Interpolator.LINEAR);
}
@Override
protected Node getParentTargetNode() {
final Node _node = getNode();
return (_node != null) ? _node : ((parent != null && parent instanceof Transition) ?
((Transition)parent).getParentTargetNode() : null);
}
private Duration computeCycleDuration() {
Duration currentDur = Duration.ZERO;
for (final Animation animation : getChildren()) {
currentDur = currentDur.add(animation.getDelay());
final double absRate = Math.abs(animation.getRate());
currentDur = currentDur.add((absRate < EPSILON) ?
animation.getTotalDuration() : animation.getTotalDuration().divide(absRate));
if (currentDur.isIndefinite()) {
break;
}
}
return currentDur;
}
private double calculateFraction(long currentTicks, long cycleTicks) {
final double frac = (double) currentTicks / cycleTicks;
return (frac <= 0.0) ? 0 : (frac >= 1.0) ? 1.0 : frac;
}
private int findNewIndex(long ticks) {
if ((curIndex != BEFORE)
&& (curIndex != end)
&& (startTimes[curIndex] <= ticks)
&& (ticks <= startTimes[curIndex + 1])) {
return curIndex;
}
final boolean indexUndefined = (curIndex == BEFORE) || (curIndex == end);
final int fromIndex = (indexUndefined || (ticks < oldTicks)) ? 0 : curIndex + 1;
final int toIndex = (indexUndefined || (oldTicks < ticks)) ? end : curIndex;
final int index = Arrays.binarySearch(startTimes, fromIndex, toIndex, ticks);
return (index < 0) ? -index - 2 : (index > 0) ? index - 1 : 0;
}
@Override
void sync(boolean forceSync) {
super.sync(forceSync);
if ((forceSync && childrenChanged) || (startTimes == null)) {
cachedChildren = getChildren().toArray(EMPTY_ANIMATION_ARRAY);
end = cachedChildren.length;
startTimes = new long[end + 1];
durations = new long[end];
delays = new long[end];
rates = new double[end];
forceChildSync = new boolean[end];
long cycleTicks = 0L;
int i = 0;
for (final Animation animation : cachedChildren) {
startTimes[i] = cycleTicks;
rates[i] = Math.abs(animation.getRate());
if (rates[i] < EPSILON) {
rates[i] = 1;
}
durations[i] = fromDuration(animation.getTotalDuration(), rates[i]);
delays[i] = fromDuration(animation.getDelay());
if ((durations[i] == Long.MAX_VALUE) || (delays[i] == Long.MAX_VALUE) || (cycleTicks == Long.MAX_VALUE)) {
cycleTicks = Long.MAX_VALUE;
} else {
cycleTicks = add(cycleTicks, add(durations[i], delays[i]));
}
forceChildSync[i] = true;
i++;
}
startTimes[end] = cycleTicks;
childrenChanged = false;
} else if (forceSync) {
final int n = forceChildSync.length;
for (int i=0; i<n; i++) {
forceChildSync[i] = true;
}
}
}
@Override
void doStart(boolean forceSync) {
super.doStart(forceSync);
toggledRate = false;
rateProperty().addListener(rateListener);
offsetTicks = 0L;
double curRate = getCurrentRate();
final long currentTicks = TickCalculation.fromDuration(getCurrentTime());
if (curRate < 0) {
jumpToEnd();
curIndex = end;
if (currentTicks < startTimes[end]) {
doJumpTo(currentTicks, startTimes[end], false);
}
} else {
jumpToBefore();
curIndex = BEFORE;
if (currentTicks > 0) {
doJumpTo(currentTicks, startTimes[end], false);
}
}
}
@Override
void doPause() {
super.doPause();
if ((curIndex != BEFORE) && (curIndex != end)) {
final Animation current = cachedChildren[curIndex];
if (current.getStatus() == Status.RUNNING) {
current.doPause();
}
}
}
@Override
void doResume() {
super.doResume();
if ((curIndex != BEFORE) && (curIndex != end)) {
final Animation current = cachedChildren[curIndex];
if (current.getStatus() == Status.PAUSED) {
current.doResume();
current.clipEnvelope.setRate(rates[curIndex] * Math.signum(getCurrentRate()));
}
}
}
@Override
void doStop() {
super.doStop();
if ((curIndex != BEFORE) && (curIndex != end)) {
final Animation current = cachedChildren[curIndex];
if (current.getStatus() != Status.STOPPED) {
current.doStop();
}
}
if (childrenChanged) {
setCycleDuration(computeCycleDuration());
}
rateProperty().removeListener(rateListener);
}
private boolean startChild(Animation child, int index) {
final boolean forceSync = forceChildSync[index];
if (child.startable(forceSync)) {
child.clipEnvelope.setRate(rates[index] * Math.signum(getCurrentRate()));
child.doStart(forceSync);
forceChildSync[index] = false;
return true;
}
return false;
}
@Override void doPlayTo(long currentTicks, long cycleTicks) {
setCurrentTicks(currentTicks);
final double frac = calculateFraction(currentTicks, cycleTicks);
final long newTicks = Math.max(0, Math.min(getCachedInterpolator().interpolate(0, cycleTicks, frac), cycleTicks));
final int newIndex = findNewIndex(newTicks);
final Animation current = ((curIndex == BEFORE) || (curIndex == end)) ? null : cachedChildren[curIndex];
if (toggledRate) {
if (current != null && current.getStatus() == Status.RUNNING) {
offsetTicks -= Math.signum(getCurrentRate()) * (durations[curIndex] - 2 * (oldTicks - delays[curIndex] - startTimes[curIndex]));
}
toggledRate = false;
}
if (curIndex == newIndex) {
if (getCurrentRate() > 0) {
final long currentDelay = add(startTimes[curIndex], delays[curIndex]);
if (newTicks >= currentDelay) {
if ((oldTicks <= currentDelay) || (current.getStatus() == Status.STOPPED)) {
final boolean enteringCycle = oldTicks <= currentDelay;
if (enteringCycle) {
current.clipEnvelope.jumpTo(0);
}
if (!startChild(current, curIndex)) {
if (enteringCycle) {
final EventHandler<ActionEvent> handler = current.getOnFinished();
if (handler != null) {
handler.handle(new ActionEvent(this, null));
}
}
oldTicks = newTicks;
return;
}
}
if (newTicks >= startTimes[curIndex+1]) {
current.doTimePulse(sub(durations[curIndex], offsetTicks));
if (newTicks == cycleTicks) {
curIndex = end;
}
} else {
final long localTicks = sub(newTicks - currentDelay, offsetTicks);
current.doTimePulse(localTicks);
}
}
} else {
final long currentDelay = add(startTimes[curIndex], delays[curIndex]);
if ((oldTicks >= startTimes[curIndex+1]) || ((oldTicks >= currentDelay) && (current.getStatus() == Status.STOPPED))){
final boolean enteringCycle = oldTicks >= startTimes[curIndex+1];
if (enteringCycle) {
current.clipEnvelope.jumpTo(Math.round(durations[curIndex] * rates[curIndex]));
}
if (!startChild(current, curIndex)) {
if (enteringCycle) {
final EventHandler<ActionEvent> handler = current.getOnFinished();
if (handler != null) {
handler.handle(new ActionEvent(this, null));
}
}
oldTicks = newTicks;
return;
}
}
if (newTicks <= currentDelay) {
current.doTimePulse(sub(durations[curIndex], offsetTicks));
if (newTicks == 0) {
curIndex = BEFORE;
}
} else {
final long localTicks = sub(startTimes[curIndex + 1] - newTicks, offsetTicks);
current.doTimePulse(localTicks);
}
}
} else {
if (curIndex < newIndex) {
if (current != null) {
final long oldDelay = add(startTimes[curIndex], delays[curIndex]);
if ((oldTicks <= oldDelay) || ((current.getStatus() == Status.STOPPED) && (oldTicks != startTimes[curIndex + 1]))) {
final boolean enteringCycle = oldTicks <= oldDelay;
if (enteringCycle) {
current.clipEnvelope.jumpTo(0);
}
if (!startChild(current, curIndex)) {
if (enteringCycle) {
final EventHandler<ActionEvent> handler = current.getOnFinished();
if (handler != null) {
handler.handle(new ActionEvent(this, null));
}
}
}
}
if (current.getStatus() == Status.RUNNING) {
current.doTimePulse(sub(durations[curIndex], offsetTicks));
}
oldTicks = startTimes[curIndex + 1];
}
offsetTicks = 0;
curIndex++;
for (; curIndex < newIndex; curIndex++) {
final Animation animation = cachedChildren[curIndex];
animation.clipEnvelope.jumpTo(0);
if (startChild(animation, curIndex)) {
animation.doTimePulse(durations[curIndex]);
} else {
final EventHandler<ActionEvent> handler = animation.getOnFinished();
if (handler != null) {
handler.handle(new ActionEvent(this, null));
}
}
oldTicks = startTimes[curIndex + 1];
}
final Animation newAnimation = cachedChildren[curIndex];
newAnimation.clipEnvelope.jumpTo(0);
if (startChild(newAnimation, curIndex)) {
if (newTicks >= startTimes[curIndex+1]) {
newAnimation.doTimePulse(durations[curIndex]);
if (newTicks == cycleTicks) {
curIndex = end;
}
} else {
final long localTicks = sub(newTicks, add(startTimes[curIndex], delays[curIndex]));
newAnimation.doTimePulse(localTicks);
}
} else {
final EventHandler<ActionEvent> handler = newAnimation.getOnFinished();
if (handler != null) {
handler.handle(new ActionEvent(this, null));
}
}
} else {
if (current != null) {
final long oldDelay = add(startTimes[curIndex], delays[curIndex]);
if ((oldTicks >= startTimes[curIndex+1]) || ((oldTicks > oldDelay) && (current.getStatus() == Status.STOPPED))){
final boolean enteringCycle = oldTicks >= startTimes[curIndex+1];
if (enteringCycle) {
current.clipEnvelope.jumpTo(Math.round(durations[curIndex] * rates[curIndex]));
}
if (!startChild(current, curIndex)) {
if (enteringCycle) {
final EventHandler<ActionEvent> handler = current.getOnFinished();
if (handler != null) {
handler.handle(new ActionEvent(this, null));
}
}
}
}
if (current.getStatus() == Status.RUNNING) {
current.doTimePulse(sub(durations[curIndex], offsetTicks));
}
oldTicks = startTimes[curIndex];
}
offsetTicks = 0;
curIndex--;
for (; curIndex > newIndex; curIndex--) {
final Animation animation = cachedChildren[curIndex];
animation.clipEnvelope.jumpTo(Math.round(durations[curIndex] * rates[curIndex]));
if (startChild(animation, curIndex)) {
animation.doTimePulse(durations[curIndex]);
} else {
final EventHandler<ActionEvent> handler = animation.getOnFinished();
if (handler != null) {
handler.handle(new ActionEvent(this, null));
}
}
oldTicks = startTimes[curIndex];
}
final Animation newAnimation = cachedChildren[curIndex];
newAnimation.clipEnvelope.jumpTo(Math.round(durations[curIndex] * rates[curIndex]));
if (startChild(newAnimation, curIndex)) {
if (newTicks <= add(startTimes[curIndex], delays[curIndex])) {
newAnimation.doTimePulse(durations[curIndex]);
if (newTicks == 0) {
curIndex = BEFORE;
}
} else {
final long localTicks = sub(startTimes[curIndex + 1], newTicks);
newAnimation.doTimePulse(localTicks);
}
} else {
final EventHandler<ActionEvent> handler = newAnimation.getOnFinished();
if (handler != null) {
handler.handle(new ActionEvent(this, null));
}
}
}
}
oldTicks = newTicks;
}
@Override void doJumpTo(long currentTicks, long cycleTicks, boolean forceJump) {
setCurrentTicks(currentTicks);
final Status status = getStatus();
if (status == Status.STOPPED && !forceJump) {
return;
}
sync(false);
final double frac = calculateFraction(currentTicks, cycleTicks);
final long newTicks = Math.max(0, Math.min(getCachedInterpolator().interpolate(0, cycleTicks, frac), cycleTicks));
final int oldIndex = curIndex;
curIndex = findNewIndex(newTicks);
final Animation newAnimation = cachedChildren[curIndex];
final double currentRate = getCurrentRate();
final long currentDelay = add(startTimes[curIndex], delays[curIndex]);
if (curIndex != oldIndex) {
if (status != Status.STOPPED) {
if ((oldIndex != BEFORE) && (oldIndex != end)) {
final Animation oldChild = cachedChildren[oldIndex];
if (oldChild.getStatus() != Status.STOPPED) {
cachedChildren[oldIndex].doStop();
}
}
if (curIndex < oldIndex) {
for (int i = oldIndex == end ? end - 1 : oldIndex; i > curIndex; --i) {
cachedChildren[i].doJumpTo(0, durations[i], true);
}
} else {
for (int i = oldIndex == BEFORE? 0 : oldIndex; i < curIndex; ++i) {
cachedChildren[i].doJumpTo(durations[i], durations[i], true);
}
}
if (newTicks >= currentDelay) {
startChild(newAnimation, curIndex);
if (status == Status.PAUSED) {
newAnimation.doPause();
}
}
}
}
if (oldIndex == curIndex) {
if (currentRate == 0) {
offsetTicks += (newTicks - oldTicks) * Math.signum(this.clipEnvelope.getCurrentRate());
} else {
offsetTicks += currentRate > 0 ? newTicks - oldTicks : oldTicks - newTicks;
}
} else {
if (currentRate == 0) {
if (this.clipEnvelope.getCurrentRate() > 0) {
offsetTicks = Math.max(0, newTicks - currentDelay);
} else {
offsetTicks = startTimes[curIndex] + durations[curIndex] - newTicks;
}
} else {
offsetTicks = currentRate > 0 ? Math.max(0, newTicks - currentDelay) : startTimes[curIndex + 1] - newTicks;
}
}
newAnimation.clipEnvelope.jumpTo(Math.round(sub(newTicks, currentDelay) * rates[curIndex]));
oldTicks = newTicks;
}
private void jumpToEnd() {
for (int i = 0 ; i < end; ++i) {
if (forceChildSync[i]) {
cachedChildren[i].sync(true);
}
cachedChildren[i].doJumpTo(durations[i], durations[i], true);
}
}
private void jumpToBefore() {
for (int i = end - 1 ; i >= 0; --i) {
if (forceChildSync[i]) {
cachedChildren[i].sync(true);
}
cachedChildren[i].doJumpTo(0, durations[i], true);
}
}
@Override
protected void interpolate(double frac) {
}
}
