package javafx.animation;
import com.sun.scenario.animation.AbstractPrimaryTimer;
import com.sun.scenario.animation.shared.ClipEnvelope;
import com.sun.scenario.animation.shared.PulseReceiver;
import javafx.util.Duration;
public abstract class AnimationShim extends Animation {
public AnimationShim() {
super();
}
public AnimationShim(AbstractPrimaryTimer timer) {
super(timer);
}
public AnimationShim(AbstractPrimaryTimer timer, ClipEnvelope clipEnvelope, int resolution) {
super(timer, clipEnvelope, resolution);
}
public ClipEnvelope get_clipEnvelope() {
return clipEnvelope;
}
public void setClipEnvelope(ClipEnvelope clipEnvelope) {
this.clipEnvelope= clipEnvelope;
}
@Override
public void doPause() {
super.doPause();
}
@Override
public void doStart(boolean forceSync) {
super.doStart(forceSync);
}
@Override
public void setCurrentRate(double currentRate) {
super.setCurrentRate(currentRate);
}
@Override
public void setCurrentTicks(long ticks) {
super.setCurrentTicks(ticks);
}
@Override
public boolean startable(boolean forceSync) {
return super.startable(forceSync);
}
@Override
public void doStop() {
super.doStop();
}
@Override
public void sync(boolean forceSync) {
super.sync(forceSync);
}
@Override
public void doTimePulse(long elapsedTime) {
super.doTimePulse(elapsedTime);
}
@Override
public void pauseReceiver() {
super.pauseReceiver();
}
@Override
public void resumeReceiver() {
super.resumeReceiver();
}
public void shim_setCycleDuration(Duration value) {
setCycleDuration(value);
}
@Override
public void startReceiver(long delay) {
super.startReceiver(delay);
}
public PulseReceiver shim_pulseReceiver() {
return pulseReceiver;
}
public void shim_finished() {
finished();
}
@Override
abstract public void doPlayTo(long currentTicks, long cycleTicks);
@Override
abstract public void doJumpTo(long currentTicks, long cycleTicks, boolean forceJump);
public static void finished(Animation a) {
a.finished();
}
public static void doStart(Animation a, boolean forceSync) {
a.doStart(forceSync);
}
public static boolean startable(Animation a, boolean forceSync) {
return a.startable(forceSync);
}
}
