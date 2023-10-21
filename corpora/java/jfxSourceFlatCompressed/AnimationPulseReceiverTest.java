package test.javafx.animation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import com.sun.javafx.tk.Toolkit;
import javafx.animation.Animation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import javafx.util.Duration;
public class AnimationPulseReceiverTest {
private static final int DEFAULT_RESOLUTION = Toolkit.getToolkit().getPrimaryTimer().getDefaultResolution();
private static final double TICKS_2_NANOS = 1.0 / 6e-6;
private AbstractPrimaryTimerMock timer;
private AnimationMock animation;
@Before
public void setUp() {
timer = new AbstractPrimaryTimerMock();
animation = new AnimationMock(timer, Duration.INDEFINITE, 1.0, 1, false);
}
@After
public void tearDown() {
animation.doStop();
}
@Test
public void testPlay_DefaultResolution() {
timer.setNanos(Math.round(3 * DEFAULT_RESOLUTION * TICKS_2_NANOS));
animation.startReceiver(0);
assertTrue(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
animation.shim_pulseReceiver().timePulse(7 * DEFAULT_RESOLUTION);
assertEquals(4 * DEFAULT_RESOLUTION, animation.getLastTimePulse());
animation.shim_pulseReceiver().timePulse(16 * DEFAULT_RESOLUTION);
assertEquals(13 * DEFAULT_RESOLUTION, animation.getLastTimePulse());
animation.doStop();
assertFalse(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
animation.doStop();
assertFalse(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
timer.setNanos(Math.round(30 * DEFAULT_RESOLUTION * TICKS_2_NANOS));
animation.startReceiver(0);
assertTrue(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
animation.shim_pulseReceiver().timePulse(43 * DEFAULT_RESOLUTION);
assertEquals(13 * DEFAULT_RESOLUTION, animation.getLastTimePulse());
}
@Test
public void testPause_DefaultResolution() {
timer.setNanos(Math.round(3 * DEFAULT_RESOLUTION * TICKS_2_NANOS));
animation.startReceiver(0);
assertTrue(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
timer.setNanos(Math.round(18 * DEFAULT_RESOLUTION * TICKS_2_NANOS));
animation.pauseReceiver();
assertFalse(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
timer.setNanos(Math.round(27 * DEFAULT_RESOLUTION * TICKS_2_NANOS));
animation.pauseReceiver();
assertFalse(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
timer.setNanos(Math.round(36 * DEFAULT_RESOLUTION * TICKS_2_NANOS));
animation.resumeReceiver();
assertTrue(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
timer.setNanos(Math.round(42 * DEFAULT_RESOLUTION * TICKS_2_NANOS));
animation.resumeReceiver();
assertTrue(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
animation.shim_pulseReceiver().timePulse(51 * DEFAULT_RESOLUTION);
assertEquals(30 * DEFAULT_RESOLUTION, animation.getLastTimePulse());
}
@Test
public void testDelay() {
timer.setNanos(Math.round(3 * DEFAULT_RESOLUTION * TICKS_2_NANOS));
animation.startReceiver(17 * DEFAULT_RESOLUTION);
assertTrue(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
animation.shim_pulseReceiver().timePulse(5 * DEFAULT_RESOLUTION);
assertEquals(0, animation.getLastTimePulse());
timer.setNanos(Math.round(10 * DEFAULT_RESOLUTION * TICKS_2_NANOS));
animation.pauseReceiver();
timer.setNanos(Math.round(37 * DEFAULT_RESOLUTION * TICKS_2_NANOS));
animation.resumeReceiver();
animation.shim_pulseReceiver().timePulse(41 * DEFAULT_RESOLUTION);
assertEquals(0, animation.getLastTimePulse());
animation.shim_pulseReceiver().timePulse(48 * DEFAULT_RESOLUTION);
assertEquals(1 * DEFAULT_RESOLUTION, animation.getLastTimePulse());
}
}
