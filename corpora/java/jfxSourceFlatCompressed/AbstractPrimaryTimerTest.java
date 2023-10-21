package test.com.sun.scenario.animation;
import javafx.animation.AnimationTimer;
import com.sun.scenario.DelayedRunnable;
import com.sun.scenario.animation.AbstractPrimaryTimer;
import com.sun.scenario.animation.AbstractPrimaryTimerShim;
import com.sun.scenario.animation.shared.PulseReceiver;
import com.sun.scenario.animation.shared.TimerReceiver;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class AbstractPrimaryTimerTest {
private AbstractPrimaryTimerStub timer;
@Before
public void setUp() {
timer = new AbstractPrimaryTimerStub();
}
@Test
public void testPauseResume() {
timer.setNanos(2L);
assertEquals(2L, timer.nanos());
timer.pause();
assertEquals(2L, timer.nanos());
timer.setNanos(5L);
assertEquals(2L, timer.nanos());
timer.setNanos(10L);
timer.pause();
assertEquals(2L, timer.nanos());
timer.setNanos(17L);
timer.resume();
assertEquals(2L, timer.nanos());
timer.setNanos(28L);
assertEquals(13L, timer.nanos());
timer.setNanos(41L);
timer.resume();
assertEquals(26L, timer.nanos());
timer.setNanos(58L);
assertEquals(43L, timer.nanos());
timer.pause();
assertEquals(43L, timer.nanos());
timer.setNanos(77L);
assertEquals(43L, timer.nanos());
timer.setNanos(100L);
timer.resume();
assertEquals(43L, timer.nanos());
timer.setNanos(129L);
assertEquals(72L, timer.nanos());
}
@Test
public void testPulseReceiver() {
final Flag flag = new Flag();
final PulseReceiver pulseReceiver = now -> flag.flag();
timer.addPulseReceiver(pulseReceiver);
timer.simulatePulse();
assertTrue(flag.isFlagged());
flag.unflag();
timer.removePulseReceiver(pulseReceiver);
timer.simulatePulse();
assertFalse(flag.isFlagged());
}
@Test
public void testAnimationTimers() {
final Flag flag = new Flag();
final AnimationTimer animationTimer = new AnimationTimer() {
@Override
public void handle(long now) {
flag.flag();
}
};
final TimerReceiver timerReceiver = l -> animationTimer.handle(l);
timer.addAnimationTimer(timerReceiver);
timer.simulatePulse();
assertTrue(flag.isFlagged());
flag.unflag();
timer.removeAnimationTimer(timerReceiver);
timer.simulatePulse();
assertFalse(flag.isFlagged());
}
private static class Flag {
private boolean flagged;
public void flag() {
flagged = true;
}
public void unflag() {
flagged = false;
}
public boolean isFlagged() {
return flagged;
}
}
private static class AbstractPrimaryTimerStub extends AbstractPrimaryTimer {
private long nanos;
private DelayedRunnable animationRunnable;
public void setNanos(long nanos) {
this.nanos = nanos;
}
public void simulatePulse() {
if (animationRunnable != null) {
animationRunnable.run();
}
}
@Override public long nanos() {
return AbstractPrimaryTimerShim.isPaused(this) ?
AbstractPrimaryTimerShim.getStartPauseTime(this) :
nanos - AbstractPrimaryTimerShim.getTotalPausedTime(this);
}
@Override
protected void postUpdateAnimationRunnable(
DelayedRunnable animationRunnable) {
this.animationRunnable = animationRunnable;
}
@Override
protected int getPulseDuration(int precision) {
return precision / 60;
}
};
}
