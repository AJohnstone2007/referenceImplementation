package test.com.sun.scenario.animation.shared;
import com.sun.scenario.animation.shared.AnimationAccessor;
import com.sun.scenario.animation.shared.ClipEnvelope;
import javafx.animation.Animation;
import javafx.util.Duration;
public class ClipEnvelopeMock extends ClipEnvelope {
public long getTimelineTicks() {
return cycleTicks;
}
public double getRate() {
return rate;
}
private boolean autoReverse;
public boolean getAutoReverse() {
return autoReverse;
}
@Override
public void setAutoReverse(boolean autoReverse) {
this.autoReverse = autoReverse;
}
private int cycleCount;
public int getCycleCount() {
return cycleCount;
}
private long lastJumpTo;
public long getLastJumpTo() {
final long v = lastJumpTo;
lastJumpTo = 0L;
return v;
}
private long lastTimePulse;
public long getLastTimePulse() {
final long v = lastTimePulse;
lastTimePulse = 0L;
return v;
}
public ClipEnvelopeMock() {
super(null);
}
public void setAnimation(Animation animation) {
this.animation = animation;
setCycleDuration(animation.getCycleDuration());
}
@Override
public boolean wasSynched() {
return true;
}
@Override
public ClipEnvelope setCycleCount(int cycleCount) {
this.cycleCount = cycleCount;
return this;
}
@Override
public void timePulse(long currentTick) {
lastTimePulse = currentTick;
}
@Override
public void jumpTo(long ticks) {
lastJumpTo = ticks;
while (ticks > cycleTicks) {
ticks -= cycleTicks;
}
AnimationAccessor.getDefault().jumpTo(animation, ticks, cycleTicks, false);
}
@Override
public ClipEnvelope setCycleDuration(Duration cycleDuration) {
updateCycleTicks(cycleDuration);
return this;
}
@Override
public void setRate(double rate) {
this.rate = rate;
}
@Override
protected double calculateCurrentRate() {
return rate;
}
}
