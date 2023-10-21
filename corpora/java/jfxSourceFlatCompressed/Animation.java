package javafx.animation;
import java.util.Objects;
import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.util.Utils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoublePropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import com.sun.javafx.animation.TickCalculation;
import com.sun.scenario.animation.AbstractPrimaryTimer;
import com.sun.scenario.animation.shared.ClipEnvelope;
import com.sun.scenario.animation.shared.PulseReceiver;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
public abstract class Animation {
static {
AnimationAccessorImpl.DEFAULT = new AnimationAccessorImpl();
}
public static final int INDEFINITE = -1;
public static enum Status {
PAUSED,
RUNNING,
STOPPED
}
private static final double EPSILON = 1e-12;
static final boolean isNearZero(double rate) {
return Math.abs(rate) < EPSILON;
}
private static boolean areNearEqual(double rate1, double rate2) {
return isNearZero(rate2 - rate1);
}
private long startTime;
private long pauseTime;
private boolean paused = false;
private final AbstractPrimaryTimer timer;
@SuppressWarnings("removal")
private AccessControlContext accessCtrlCtx = null;
private long now() {
return TickCalculation.fromNano(timer.nanos());
}
@SuppressWarnings("removal")
private void addPulseReceiver() {
accessCtrlCtx = AccessController.getContext();
timer.addPulseReceiver(pulseReceiver);
}
void startReceiver(long delay) {
paused = false;
startTime = now() + delay;
addPulseReceiver();
}
void pauseReceiver() {
if (!paused) {
pauseTime = now();
paused = true;
timer.removePulseReceiver(pulseReceiver);
}
}
void resumeReceiver() {
if (paused) {
final long deltaTime = now() - pauseTime;
startTime += deltaTime;
paused = false;
addPulseReceiver();
}
}
final PulseReceiver pulseReceiver = new PulseReceiver() {
@SuppressWarnings("removal")
@Override public void timePulse(long now) {
final long elapsedTime = now - startTime;
if (elapsedTime < 0) {
return;
}
if (accessCtrlCtx == null) {
throw new IllegalStateException("Error: AccessControlContext not captured");
}
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
doTimePulse(elapsedTime);
return null;
}, accessCtrlCtx);
}
};
private class CurrentRateProperty extends ReadOnlyDoublePropertyBase {
private double value;
@Override
public Object getBean() {
return Animation.this;
}
@Override
public String getName() {
return "currentRate";
}
@Override
public double get() {
return value;
}
private void set(double value) {
this.value = value;
fireValueChangedEvent();
}
}
private class AnimationReadOnlyProperty<T> extends ReadOnlyObjectPropertyBase<T> {
private final String name;
private T value;
private AnimationReadOnlyProperty(String name, T value) {
this.name = name;
this.value = value;
}
@Override
public Object getBean() {
return Animation.this;
}
@Override
public String getName() {
return name;
}
@Override
public T get() {
return value;
}
private void set(T value) {
this.value = value;
fireValueChangedEvent();
}
}
Animation parent = null;
ClipEnvelope clipEnvelope;
private boolean lastPlayedFinished = true;
private boolean lastPlayedForward = true;
private DoubleProperty rate;
private static final double DEFAULT_RATE = 1.0;
public final void setRate(double value) {
if (rate != null || !areNearEqual(value, DEFAULT_RATE)) {
rateProperty().set(value);
}
}
public final double getRate() {
return (rate == null)? DEFAULT_RATE : rate.get();
}
public final DoubleProperty rateProperty() {
if (rate == null) {
rate = new DoublePropertyBase(DEFAULT_RATE) {
@Override
public void invalidated() {
final double newRate = getRate();
if (isRunningEmbedded()) {
if (isBound()) {
unbind();
}
set(oldRate);
throw new IllegalArgumentException("Cannot set rate of embedded animation while running.");
}
if (isNearZero(newRate)) {
if (isRunning()) {
lastPlayedForward = areNearEqual(getCurrentRate(), oldRate);
}
doSetCurrentRate(0.0);
pauseReceiver();
} else {
if (isRunning()) {
final double currentRate = getCurrentRate();
if (isNearZero(currentRate)) {
doSetCurrentRate(lastPlayedForward ? newRate : -newRate);
resumeReceiver();
} else {
final boolean playingForward = areNearEqual(currentRate, oldRate);
doSetCurrentRate(playingForward ? newRate : -newRate);
}
}
oldRate = newRate;
}
clipEnvelope.setRate(newRate);
}
@Override
public Object getBean() {
return Animation.this;
}
@Override
public String getName() {
return "rate";
}
};
}
return rate;
}
private boolean isRunningEmbedded() {
if (parent == null) {
return false;
}
return !parent.isStopped() || parent.isRunningEmbedded();
}
private double oldRate = 1.0;
private ReadOnlyDoubleProperty currentRate;
private static final double DEFAULT_CURRENT_RATE = 0.0;
public final double getCurrentRate() {
return (currentRate == null) ? DEFAULT_CURRENT_RATE : currentRate.get();
}
public final ReadOnlyDoubleProperty currentRateProperty() {
if (currentRate == null) {
currentRate = new CurrentRateProperty();
}
return currentRate;
}
void setCurrentRate(double currentRate) {
doSetCurrentRate(currentRate);
}
private void doSetCurrentRate(double value) {
if (currentRate != null || !areNearEqual(value, DEFAULT_CURRENT_RATE)) {
((CurrentRateProperty) currentRateProperty()).set(value);
}
}
private ReadOnlyObjectProperty<Duration> cycleDuration;
private static final Duration DEFAULT_CYCLE_DURATION = Duration.ZERO;
protected final void setCycleDuration(Duration value) {
if (cycleDuration != null || !DEFAULT_CYCLE_DURATION.equals(value)) {
if (value.lessThan(Duration.ZERO)) {
throw new IllegalArgumentException("Cycle duration cannot be negative");
}
((AnimationReadOnlyProperty<Duration>) cycleDurationProperty()).set(value);
updateTotalDuration();
}
}
public final Duration getCycleDuration() {
return (cycleDuration == null) ? DEFAULT_CYCLE_DURATION : cycleDuration.get();
}
public final ReadOnlyObjectProperty<Duration> cycleDurationProperty() {
if (cycleDuration == null) {
cycleDuration = new AnimationReadOnlyProperty<>("cycleDuration", DEFAULT_CYCLE_DURATION);
}
return cycleDuration;
}
private ReadOnlyObjectProperty<Duration> totalDuration;
private static final Duration DEFAULT_TOTAL_DURATION = Duration.ZERO;
public final Duration getTotalDuration() {
return (totalDuration == null) ? DEFAULT_TOTAL_DURATION : totalDuration.get();
}
public final ReadOnlyObjectProperty<Duration> totalDurationProperty() {
if (totalDuration == null) {
totalDuration = new AnimationReadOnlyProperty<>("totalDuration", DEFAULT_TOTAL_DURATION);
}
return totalDuration;
}
private void updateTotalDuration() {
final int cycleCount = getCycleCount();
final Duration cycleDuration = getCycleDuration();
final Duration newTotalDuration;
if (Duration.ZERO.equals(cycleDuration)) newTotalDuration = Duration.ZERO;
else if (cycleCount == INDEFINITE) newTotalDuration = Duration.INDEFINITE;
else if (cycleCount <= 1) newTotalDuration = cycleDuration;
else newTotalDuration = cycleDuration.multiply(cycleCount);
if (totalDuration != null || !DEFAULT_TOTAL_DURATION.equals(newTotalDuration)) {
((AnimationReadOnlyProperty<Duration>) totalDurationProperty()).set(newTotalDuration);
}
if (isStopped()) {
syncClipEnvelope();
if (newTotalDuration.lessThan(getCurrentTime())) {
clipEnvelope.jumpTo(TickCalculation.fromDuration(newTotalDuration));
}
}
}
private CurrentTimeProperty currentTime;
private long currentTicks;
private class CurrentTimeProperty extends ReadOnlyObjectPropertyBase<Duration> {
@Override
public Object getBean() {
return Animation.this;
}
@Override
public String getName() {
return "currentTime";
}
@Override
public Duration get() {
return getCurrentTime();
}
@Override
public void fireValueChangedEvent() {
super.fireValueChangedEvent();
}
}
public final Duration getCurrentTime() {
return TickCalculation.toDuration(currentTicks);
}
public final ReadOnlyObjectProperty<Duration> currentTimeProperty() {
if (currentTime == null) {
currentTime = new CurrentTimeProperty();
}
return currentTime;
}
private ObjectProperty<Duration> delay;
private static final Duration DEFAULT_DELAY = Duration.ZERO;
public final void setDelay(Duration value) {
if (delay != null || !DEFAULT_DELAY.equals(value)) {
delayProperty().set(value);
}
}
public final Duration getDelay() {
return (delay == null) ? DEFAULT_DELAY : delay.get();
}
public final ObjectProperty<Duration> delayProperty() {
if (delay == null) {
delay = new ObjectPropertyBase<>(DEFAULT_DELAY) {
@Override
protected void invalidated() {
final Duration newDuration = get();
if (newDuration.lessThan(Duration.ZERO)) {
if (isBound()) {
unbind();
}
set(Duration.ZERO);
throw new IllegalArgumentException("Cannot set delay to negative value. Setting to Duration.ZERO");
}
}
@Override
public Object getBean() {
return Animation.this;
}
@Override
public String getName() {
return "delay";
}
};
}
return delay;
}
private IntegerProperty cycleCount;
private static final int DEFAULT_CYCLE_COUNT = 1;
public final void setCycleCount(int value) {
if (cycleCount != null || value != DEFAULT_CYCLE_COUNT) {
cycleCountProperty().set(value);
}
}
public final int getCycleCount() {
return (cycleCount == null) ? DEFAULT_CYCLE_COUNT : cycleCount.get();
}
public final IntegerProperty cycleCountProperty() {
if (cycleCount == null) {
cycleCount = new IntegerPropertyBase(DEFAULT_CYCLE_COUNT) {
@Override
public void invalidated() {
updateTotalDuration();
}
@Override
public Object getBean() {
return Animation.this;
}
@Override
public String getName() {
return "cycleCount";
}
};
}
return cycleCount;
}
private BooleanProperty autoReverse;
private static final boolean DEFAULT_AUTO_REVERSE = false;
public final void setAutoReverse(boolean value) {
if (autoReverse != null || value != DEFAULT_AUTO_REVERSE) {
autoReverseProperty().set(value);
}
}
public final boolean isAutoReverse() {
return (autoReverse == null) ? DEFAULT_AUTO_REVERSE : autoReverse.get();
}
public final BooleanProperty autoReverseProperty() {
if (autoReverse == null) {
autoReverse = new BooleanPropertyBase(DEFAULT_AUTO_REVERSE) {
@Override
public Object getBean() {
return Animation.this;
}
@Override
public String getName() {
return "autoReverse";
}
};
}
return autoReverse;
}
private ReadOnlyObjectProperty<Status> status;
private static final Status DEFAULT_STATUS = Status.STOPPED;
protected final void setStatus(Status value) {
if (status != null || !DEFAULT_STATUS.equals(value)) {
((AnimationReadOnlyProperty<Status>) statusProperty()).set(value);
}
}
public final Status getStatus() {
return (status == null) ? DEFAULT_STATUS : status.get();
}
public final ReadOnlyObjectProperty<Status> statusProperty() {
if (status == null) {
status = new AnimationReadOnlyProperty<>("status", Status.STOPPED);
}
return status;
}
boolean isStopped() {
return getStatus() == Status.STOPPED;
}
boolean isPaused() {
return getStatus() == Status.PAUSED;
}
boolean isRunning() {
return getStatus() == Status.RUNNING;
}
private ObjectProperty<EventHandler<ActionEvent>> onFinished;
private static final EventHandler<ActionEvent> DEFAULT_ON_FINISHED = null;
public final void setOnFinished(EventHandler<ActionEvent> value) {
if (onFinished != null || value != DEFAULT_ON_FINISHED) {
onFinishedProperty().set(value);
}
}
public final EventHandler<ActionEvent> getOnFinished() {
return (onFinished == null) ? DEFAULT_ON_FINISHED : onFinished.get();
}
public final ObjectProperty<EventHandler<ActionEvent>> onFinishedProperty() {
if (onFinished == null) {
onFinished = new ObjectPropertyBase<>(DEFAULT_ON_FINISHED) {
@Override
public Object getBean() {
return Animation.this;
}
@Override
public String getName() {
return "onFinished";
}
};
}
return onFinished;
}
private ObservableMap<String, Duration> cuePoints;
public final ObservableMap<String, Duration> getCuePoints() {
if (cuePoints == null) {
cuePoints = FXCollections.observableHashMap();
}
return cuePoints;
}
public void jumpTo(Duration time) {
Objects.requireNonNull(time, "Time needs to be specified");
if (time.isUnknown()) {
throw new IllegalArgumentException("The time is invalid");
}
if (parent != null) {
throw new IllegalStateException("Cannot jump when embedded in another animation");
}
lastPlayedFinished = false;
double millis = time.isIndefinite() ? getCycleDuration().toMillis() :
Utils.clamp(0, time.toMillis(), getTotalDuration().toMillis());
long ticks = TickCalculation.fromMillis(millis);
if (isStopped()) {
syncClipEnvelope();
}
clipEnvelope.jumpTo(ticks);
}
public void jumpTo(String cuePoint) {
Objects.requireNonNull(cuePoint, "CuePoint needs to be specified");
if ("start".equalsIgnoreCase(cuePoint)) {
jumpTo(Duration.ZERO);
} else if ("end".equalsIgnoreCase(cuePoint)) {
jumpTo(getTotalDuration());
} else {
final Duration target = getCuePoints().get(cuePoint);
if (target != null) {
jumpTo(target);
}
}
}
public void playFrom(String cuePoint) {
jumpTo(cuePoint);
play();
}
public void playFrom(Duration time) {
jumpTo(time);
play();
}
public void playFromStart() {
stop();
setRate(Math.abs(getRate()));
jumpTo(Duration.ZERO);
play();
}
public void play() {
if (parent != null) {
throw new IllegalStateException("Cannot start when embedded in another animation");
}
switch (getStatus()) {
case STOPPED:
if (startable(true)) {
final double rate = getRate();
if (lastPlayedFinished) {
jumpTo(rate < 0 ? getTotalDuration() : Duration.ZERO);
}
doStart(true);
startReceiver(TickCalculation.fromDuration(getDelay()));
if (isNearZero(rate)) {
pauseReceiver();
} else {
}
} else {
runHandler(getOnFinished());
}
break;
case PAUSED:
doResume();
if (!isNearZero(getRate())) {
resumeReceiver();
}
break;
case RUNNING:
}
}
void doStart(boolean forceSync) {
sync(forceSync);
setStatus(Status.RUNNING);
clipEnvelope.start();
doSetCurrentRate(clipEnvelope.getCurrentRate());
lastPulse = 0;
}
void doResume() {
setStatus(Status.RUNNING);
doSetCurrentRate(lastPlayedForward ? getRate() : -getRate());
}
public void stop() {
if (parent != null) {
throw new IllegalStateException("Cannot stop when embedded in another animation");
}
if (!isStopped()) {
clipEnvelope.abortCurrentPulse();
doStop();
jumpTo(Duration.ZERO);
lastPlayedFinished = true;
}
}
void doStop() {
if (!paused) {
timer.removePulseReceiver(pulseReceiver);
}
setStatus(Status.STOPPED);
doSetCurrentRate(0.0);
}
public void pause() {
if (parent != null) {
throw new IllegalStateException("Cannot pause when embedded in another animation");
}
if (isRunning()) {
clipEnvelope.abortCurrentPulse();
pauseReceiver();
doPause();
}
}
void doPause() {
final double currentRate = getCurrentRate();
if (!isNearZero(currentRate)) {
lastPlayedForward = areNearEqual(getCurrentRate(), getRate());
}
doSetCurrentRate(0.0);
setStatus(Status.PAUSED);
}
final void finished() {
lastPlayedFinished = true;
doStop();
runHandler(getOnFinished());
}
void runHandler(EventHandler<ActionEvent> handler) {
if (handler != null) {
try {
handler.handle(new ActionEvent(this, null));
} catch (Exception ex) {
Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), ex);
}
}
}
private final double targetFramerate;
private final int resolution;
private long lastPulse;
public final double getTargetFramerate() {
return targetFramerate;
}
protected Animation(double targetFramerate) {
this.targetFramerate = targetFramerate;
this.resolution = (int) Math.max(1, Math.round(TickCalculation.TICKS_PER_SECOND / targetFramerate));
this.clipEnvelope = ClipEnvelope.create(this);
this.timer = Toolkit.getToolkit().getPrimaryTimer();
}
protected Animation() {
this.resolution = 1;
this.targetFramerate = TickCalculation.TICKS_PER_SECOND / Toolkit.getToolkit().getPrimaryTimer().getDefaultResolution();
this.clipEnvelope = ClipEnvelope.create(this);
this.timer = Toolkit.getToolkit().getPrimaryTimer();
}
Animation(AbstractPrimaryTimer timer) {
this.resolution = 1;
this.targetFramerate = TickCalculation.TICKS_PER_SECOND / timer.getDefaultResolution();
this.clipEnvelope = ClipEnvelope.create(this);
this.timer = timer;
}
Animation(AbstractPrimaryTimer timer, ClipEnvelope clipEnvelope, int resolution) {
this.resolution = resolution;
this.targetFramerate = TickCalculation.TICKS_PER_SECOND / resolution;
this.clipEnvelope = clipEnvelope;
this.timer = timer;
}
boolean startable(boolean forceSync) {
return (TickCalculation.fromDuration(getCycleDuration()) > 0L) || (!forceSync && clipEnvelope.wasSynched());
}
void sync(boolean forceSync) {
if (forceSync || !clipEnvelope.wasSynched()) {
syncClipEnvelope();
}
}
private void syncClipEnvelope() {
final int publicCycleCount = getCycleCount();
final int internalCycleCount = (publicCycleCount <= 0)
&& (publicCycleCount != INDEFINITE) ? 1 : publicCycleCount;
clipEnvelope = clipEnvelope.setCycleCount(internalCycleCount);
clipEnvelope.setCycleDuration(getCycleDuration());
clipEnvelope.setAutoReverse(isAutoReverse());
}
void doTimePulse(long elapsedTime) {
if (resolution == 1) {
clipEnvelope.timePulse(elapsedTime);
} else if (elapsedTime - lastPulse >= resolution) {
lastPulse = (elapsedTime / resolution) * resolution;
clipEnvelope.timePulse(elapsedTime);
}
}
abstract void doPlayTo(long currentTicks, long cycleTicks);
abstract void doJumpTo(long currentTicks, long cycleTicks, boolean forceJump);
void setCurrentTicks(long ticks) {
currentTicks = ticks;
if (currentTime != null) {
currentTime.fireValueChangedEvent();
}
}
}
