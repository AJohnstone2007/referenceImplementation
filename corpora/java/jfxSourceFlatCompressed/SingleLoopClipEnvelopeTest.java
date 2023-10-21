package test.com.sun.scenario.animation.shared;
import com.sun.javafx.tk.Toolkit;
import com.sun.scenario.animation.shared.ClipEnvelope;
import com.sun.scenario.animation.shared.FiniteClipEnvelope;
import com.sun.scenario.animation.shared.SingleLoopClipEnvelopeShim;
import javafx.animation.Animation.Status;
import test.javafx.animation.AnimationMock;
import test.javafx.animation.AnimationMock.Command;
import javafx.util.Duration;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class SingleLoopClipEnvelopeTest {
private final long CYCLE_TICKS = Math.round(6.0 * AnimationMock.DEFAULT_DURATION.toMillis());
private ClipEnvelope clip;
private AnimationMock animation;
@Before
public void setUp() {
animation = new AnimationMock(Toolkit.getToolkit().getPrimaryTimer(), AnimationMock.DEFAULT_DURATION, AnimationMock.DEFAULT_RATE, 1, AnimationMock.DEFAULT_AUTOREVERSE);
clip = new SingleLoopClipEnvelopeShim(animation);
}
@Test
public void testSetValues() {
ClipEnvelope c;
animation.setCycleCount(2);
animation.mockCycleDuration(AnimationMock.DEFAULT_DURATION);
c = clip.setCycleCount(2);
assertNotSame(clip, c);
assertTrue(c instanceof FiniteClipEnvelope);
animation.setCycleCount(1);
animation.mockCycleDuration(Duration.INDEFINITE);
c = clip.setCycleDuration(Duration.INDEFINITE);
assertSame(clip, c);
animation.setCycleCount(2);
animation.mockCycleDuration(Duration.INDEFINITE);
c = clip.setCycleCount(2);
assertSame(clip, c);
animation.setCycleCount(2);
animation.mockCycleDuration(AnimationMock.DEFAULT_DURATION);
c = clip.setCycleDuration(AnimationMock.DEFAULT_DURATION);
assertNotSame(clip, c);
assertTrue(c instanceof FiniteClipEnvelope);
animation.setCycleCount(1);
animation.mockCycleDuration(AnimationMock.DEFAULT_DURATION);
c = clip.setCycleCount(1);
assertSame(clip, c);
}
@Test
public void testJump() {
clip.jumpTo(0);
animation.check(Command.JUMP, 0, CYCLE_TICKS);
clip.jumpTo(6 * 300);
animation.check(Command.JUMP, 6 * 300, CYCLE_TICKS);
clip.jumpTo(6 * 300);
animation.check(Command.JUMP, 6 * 300, CYCLE_TICKS);
clip.jumpTo(0);
animation.check(Command.JUMP, 0, CYCLE_TICKS);
clip.jumpTo(6 * 1000);
animation.check(Command.JUMP, 6 * 1000, CYCLE_TICKS);
clip.jumpTo(-1);
animation.check(Command.JUMP, 0, CYCLE_TICKS);
clip.jumpTo(6 * 1000 + 1);
animation.check(Command.JUMP, 6 * 1000, CYCLE_TICKS);
}
@Test
public void testTimePulseForward() {
animation.mockStatus(Status.RUNNING);
clip.start();
clip.timePulse(1);
animation.check(Command.PLAY, 1, CYCLE_TICKS);
assertFalse(animation.finishCalled());
clip.timePulse(6 * 1000 - 1);
animation.check(Command.PLAY, 6 * 1000 - 1, CYCLE_TICKS);
assertFalse(animation.finishCalled());
clip.timePulse(6 * 1000);
animation.check(Command.PLAY, 6 * 1000, CYCLE_TICKS);
assertTrue(animation.finishCalled());
}
@Test
public void testTimePulseBackward() {
clip.jumpTo(6 * 1000);
clip.setRate(-1.0);
animation.mockStatus(Status.RUNNING);
clip.start();
clip.timePulse(1);
animation.check(Command.PLAY, 6 * 1000 - 1, CYCLE_TICKS);
assertFalse(animation.finishCalled());
clip.timePulse(6 * 1000 - 1);
animation.check(Command.PLAY, 1, CYCLE_TICKS);
assertFalse(animation.finishCalled());
clip.timePulse(6 * 1000);
animation.check(Command.PLAY, 0, CYCLE_TICKS);
assertTrue(animation.finishCalled());
}
@Test
public void testJumpAndPulseForward() {
animation.mockStatus(Status.RUNNING);
clip.start();
clip.jumpTo(6 * 300);
animation.check(Command.JUMP, 6 * 300, CYCLE_TICKS);
clip.timePulse(6 * 700 - 1);
animation.check(Command.PLAY, 6 * 1000 - 1, CYCLE_TICKS);
assertFalse(animation.finishCalled());
clip.jumpTo(6 * 500);
animation.check(Command.JUMP, 6 * 500, CYCLE_TICKS);
clip.timePulse(6 * 700 - 1 + 6 * 500 - 1);
animation.check(Command.PLAY, 6 * 1000 - 1, CYCLE_TICKS);
assertFalse(animation.finishCalled());
clip.timePulse(6 * 700 - 1 + 6 * 500 - 1 + 1);
animation.check(Command.PLAY, 6 * 1000, CYCLE_TICKS);
assertTrue(animation.finishCalled());
}
@Test
public void testJumpAndPulseBackward() {
clip.jumpTo(6 * 1000);
clip.setRate(-1.0);
animation.mockStatus(Status.RUNNING);
clip.start();
clip.jumpTo(6 * 300);
animation.check(Command.JUMP, 6 * 300, CYCLE_TICKS);
clip.timePulse(6 * 300 - 1);
animation.check(Command.PLAY, 1, CYCLE_TICKS);
assertFalse(animation.finishCalled());
clip.jumpTo(6 * 500);
animation.check(Command.JUMP, 6 * 500, CYCLE_TICKS);
clip.timePulse(6 * 300 - 1 + 6 * 500 - 1);
animation.check(Command.PLAY, 1, CYCLE_TICKS);
assertFalse(animation.finishCalled());
clip.timePulse(6 * 300 - 1 + 6 * 500 - 1 + 1);
animation.check(Command.PLAY, 0, CYCLE_TICKS);
assertTrue(animation.finishCalled());
}
@Test
public void testRate() {
clip.setRate(0.5);
animation.mockStatus(Status.RUNNING);
clip.start();
clip.timePulse(6 * 200);
animation.check(Command.PLAY, 6 * 100, CYCLE_TICKS);
clip.setRate(3.0);
clip.timePulse(6 * 300);
animation.check(Command.PLAY, 6 * 400, CYCLE_TICKS);
clip.setRate(2.0);
clip.timePulse(6 * 500);
animation.check(Command.PLAY, 6 * 800, CYCLE_TICKS);
clip.setRate(-0.5);
clip.timePulse(6 * 1100);
animation.check(Command.PLAY, 6 * 500, CYCLE_TICKS);
clip.setRate(-3.0);
clip.timePulse(6 * 1200);
animation.check(Command.PLAY, 6 * 200, CYCLE_TICKS);
clip.setRate(0.5);
clip.timePulse(6 * 2100);
animation.check(Command.PLAY, 6 * 650, CYCLE_TICKS);
}
}
