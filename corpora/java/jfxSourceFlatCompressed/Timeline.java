package javafx.animation;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import com.sun.javafx.collections.TrackableObservableList;
import com.sun.scenario.animation.AbstractPrimaryTimer;
import com.sun.scenario.animation.shared.TimelineClipCore;
public final class Timeline extends Animation {
final TimelineClipCore clipCore;
public final ObservableList<KeyFrame> getKeyFrames() {
return keyFrames;
}
private final ObservableList<KeyFrame> keyFrames = new TrackableObservableList<KeyFrame>() {
@Override
protected void onChanged(Change<KeyFrame> c) {
while (c.next()) {
if (!c.wasPermutated()) {
for (final KeyFrame keyFrame : c.getRemoved()) {
final String cuePoint = keyFrame.getName();
if (cuePoint != null) {
getCuePoints().remove(cuePoint);
}
}
for (final KeyFrame keyFrame : c.getAddedSubList()) {
final String cuePoint = keyFrame.getName();
if (cuePoint != null) {
getCuePoints().put(cuePoint, keyFrame.getTime());
}
}
final Duration duration = clipCore.setKeyFrames(getKeyFrames());
setCycleDuration(duration);
}
}
}
};
public Timeline(double targetFramerate, KeyFrame... keyFrames) {
super(targetFramerate);
clipCore = new TimelineClipCore(this);
getKeyFrames().setAll(keyFrames);
}
public Timeline(KeyFrame... keyFrames) {
super();
clipCore = new TimelineClipCore(this);
getKeyFrames().setAll(keyFrames);
}
public Timeline(double targetFramerate) {
super(targetFramerate);
clipCore = new TimelineClipCore(this);
}
public Timeline() {
super();
clipCore = new TimelineClipCore(this);
}
Timeline(final AbstractPrimaryTimer timer) {
super(timer);
clipCore = new TimelineClipCore(this);
}
@Override
void doPlayTo(long currentTicks, long cycleTicks) {
clipCore.playTo(currentTicks);
}
@Override
void doJumpTo(long currentTicks, long cycleTicks, boolean forceJump) {
sync(false);
setCurrentTicks(currentTicks);
clipCore.jumpTo(currentTicks, forceJump);
}
@Override
void setCurrentRate(double currentRate) {
super.setCurrentRate(currentRate);
clipCore.notifyCurrentRateChanged();
}
@Override
void doStart(boolean forceSync) {
super.doStart(forceSync);
clipCore.start(forceSync);
}
@Override
public void stop() {
if (parent != null) {
throw new IllegalStateException("Cannot stop when embedded in another animation");
}
if (getStatus() == Status.RUNNING) {
clipCore.abort();
}
super.stop();
}
}
