package com.sun.scenario.animation.shared;
import com.sun.javafx.util.Utils;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.util.Duration;
public class SingleLoopClipEnvelope extends ClipEnvelope {
private int cycleCount;
protected SingleLoopClipEnvelope(Animation animation) {
super(animation);
if (animation != null) {
cycleCount = animation.getCycleCount();
}
}
@Override
public void setAutoReverse(boolean autoReverse) {
}
@Override
public ClipEnvelope setCycleDuration(Duration cycleDuration) {
if ((cycleCount != 1) && !cycleDuration.isIndefinite()) {
return create(animation);
}
updateCycleTicks(cycleDuration);
return this;
}
@Override
public ClipEnvelope setCycleCount(int cycleCount) {
if ((cycleCount != 1) && (cycleTicks != ClipEnvelope.INDEFINITE)) {
return create(animation);
}
this.cycleCount = cycleCount;
return this;
}
@Override
public void setRate(double newRate) {
final Status status = animation.getStatus();
if (status != Status.STOPPED) {
setInternalCurrentRate((Math.abs(currentRate - rate) < EPSILON) ? newRate : -newRate);
deltaTicks = ticks - ticksRateChange(newRate);
abortCurrentPulse();
}
rate = newRate;
}
@Override
protected double calculateCurrentRate() {
return rate;
}
@Override
public boolean wasSynched() {
return super.wasSynched() && cycleCount != 0;
}
@Override
public void timePulse(long currentTick) {
if (cycleTicks == 0L) {
return;
}
aborted = false;
inTimePulse = true;
try {
long ticksChange = Math.round(currentTick * currentRate);
ticks = Utils.clamp(0, deltaTicks + ticksChange, cycleTicks);
AnimationAccessor.getDefault().playTo(animation, ticks, cycleTicks);
final boolean reachedEnd = (currentRate > 0)? (ticks == cycleTicks) : (ticks == 0);
if(reachedEnd && !aborted) {
AnimationAccessor.getDefault().finished(animation);
}
} finally {
inTimePulse = false;
}
}
@Override
public void jumpTo(long ticks) {
if (cycleTicks == 0L) {
return;
}
final long newTicks = Utils.clamp(0, ticks, cycleTicks);
deltaTicks += (newTicks - this.ticks);
this.ticks = newTicks;
AnimationAccessor.getDefault().jumpTo(animation, newTicks, cycleTicks, false);
abortCurrentPulse();
}
}
