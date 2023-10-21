package com.sun.scenario.animation.shared;
import javafx.animation.Animation;
import javafx.util.Duration;
import com.sun.javafx.animation.TickCalculation;
public abstract class ClipEnvelope {
protected static final long INDEFINITE = Long.MAX_VALUE;
protected static final double EPSILON = 1e-12;
protected Animation animation;
protected double rate = 1;
protected long cycleTicks = 0;
protected long deltaTicks = 0;
protected long ticks = 0;
protected double currentRate = rate;
protected boolean inTimePulse = false;
protected boolean aborted = false;
protected ClipEnvelope(Animation animation) {
this.animation = animation;
if (animation != null) {
cycleTicks = TickCalculation.fromDuration(animation.getCycleDuration());
rate = animation.getRate();
}
}
public static ClipEnvelope create(Animation animation) {
if ((animation.getCycleCount() == 1) || (animation.getCycleDuration().isIndefinite())) {
return new SingleLoopClipEnvelope(animation);
} else if (animation.getCycleCount() == Animation.INDEFINITE) {
return new InfiniteClipEnvelope(animation);
} else {
return new FiniteClipEnvelope(animation);
}
}
public abstract void setAutoReverse(boolean autoReverse);
public abstract ClipEnvelope setCycleDuration(Duration cycleDuration);
public abstract ClipEnvelope setCycleCount(int cycleCount);
public abstract void setRate(double rate);
protected abstract double calculateCurrentRate();
protected void setInternalCurrentRate(double currentRate) {
this.currentRate = currentRate;
}
protected void setCurrentRate(double currentRate) {
this.currentRate = currentRate;
AnimationAccessor.getDefault().setCurrentRate(animation, currentRate);
}
public double getCurrentRate() {
return currentRate;
}
protected long ticksRateChange(double newRate) {
return Math.round((ticks - deltaTicks) * newRate / rate);
}
protected void updateCycleTicks(Duration cycleDuration) {
cycleTicks = TickCalculation.fromDuration(cycleDuration);
}
public boolean wasSynched() {
return cycleTicks != 0;
}
public void start() {
setCurrentRate(calculateCurrentRate());
deltaTicks = ticks;
}
public abstract void timePulse(long currentTick);
public abstract void jumpTo(long ticks);
public final void abortCurrentPulse() {
if (inTimePulse) {
aborted = true;
inTimePulse = false;
}
}
}
