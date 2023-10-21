package test.javafx.animation;
import com.sun.javafx.tk.Toolkit;
import test.com.sun.scenario.animation.shared.ClipEnvelopeMock;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
public class AnimationTest {
private static final Duration ONE_SEC = Duration.millis(1000);
private static final Duration TWO_SECS = Duration.millis(2000);
private static final Duration THREE_SECS = Duration.millis(3000);
private static final Duration FOUR_SECS = Duration.millis(4000);
private static final double DEFAULT_RATE = 1.0;
private static final int DEFAULT_REPEAT_COUNT = 1;
private static final boolean DEFAULT_AUTO_REVERSE = false;
private static final double EPSILON = 1e-12;
private AbstractPrimaryTimerMock timer;
private AnimationImpl animation;
private ClipEnvelopeMock clipEnvelope;
@Before
public void setUp() {
timer = new AbstractPrimaryTimerMock();
clipEnvelope = new ClipEnvelopeMock();
animation = new AnimationImpl(timer, clipEnvelope, 1);
animation.shim_setCycleDuration(ONE_SEC);
clipEnvelope.setAnimation(animation);
}
@After
public void tearDown() {
animation.stop();
}
@Test
public void testConstructors() {
final Animation animation0 = new AnimationImpl();
assertEquals(DEFAULT_RATE, animation0.getRate(), EPSILON);
assertEquals(0.0, animation0.getCurrentRate(), EPSILON);
assertEquals(Duration.ZERO, animation0.getCycleDuration());
assertEquals(Duration.ZERO, animation0.getTotalDuration());
assertEquals(Duration.ZERO, animation0.getCurrentTime());
assertEquals(DEFAULT_REPEAT_COUNT, animation0.getCycleCount());
assertEquals(DEFAULT_AUTO_REVERSE, animation0.isAutoReverse());
assertEquals(Status.STOPPED, animation0.getStatus());
assertEquals(6000.0 / Toolkit.getToolkit().getPrimaryTimer().getDefaultResolution(), animation0.getTargetFramerate(), EPSILON);
assertEquals(null, animation0.getOnFinished());
assertEquals(0, animation0.getCuePoints().size());
final Animation animation1 = new AnimationImpl(timer, clipEnvelope, 600);
assertEquals(10.0, animation1.getTargetFramerate(), EPSILON);
}
@Test
public void testReadOnlyProperties() {
assertEquals("currentRate", animation.currentRateProperty().getName());
assertEquals(animation, animation.currentRateProperty().getBean());
assertEquals("cycleDuration", animation.cycleDurationProperty().getName());
assertEquals(animation, animation.cycleDurationProperty().getBean());
assertEquals("totalDuration", animation.totalDurationProperty().getName());
assertEquals(animation, animation.totalDurationProperty().getBean());
assertEquals("currentTime", animation.currentTimeProperty().getName());
assertEquals(animation, animation.currentTimeProperty().getBean());
assertEquals("status", animation.statusProperty().getName());
assertEquals(animation, animation.statusProperty().getBean());
}
@Test
public void testCalculationOfTotalDuration() {
assertEquals(ONE_SEC, animation.getTotalDuration());
animation.setCycleCount(0);
assertEquals(ONE_SEC, animation.getTotalDuration());
animation.setCycleCount(7);
assertEquals(ONE_SEC.multiply(7), animation.getTotalDuration());
animation.setCycleCount(Animation.INDEFINITE);
assertEquals(Duration.INDEFINITE, animation.getTotalDuration());
animation.setCycleCount(1);
animation.shim_setCycleDuration(Duration.ZERO);
assertEquals(Duration.ZERO, animation.getTotalDuration());
animation.setCycleCount(0);
assertEquals(Duration.ZERO, animation.getTotalDuration());
animation.setCycleCount(7);
assertEquals(Duration.ZERO, animation.getTotalDuration());
animation.setCycleCount(Animation.INDEFINITE);
assertEquals(Duration.ZERO, animation.getTotalDuration());
animation.setCycleCount(1);
animation.shim_setCycleDuration(Duration.INDEFINITE);
assertEquals(Duration.INDEFINITE, animation.getTotalDuration());
animation.setCycleCount(0);
assertEquals(Duration.INDEFINITE, animation.getTotalDuration());
animation.setCycleCount(7);
assertEquals(Duration.INDEFINITE, animation.getTotalDuration());
animation.setCycleCount(Animation.INDEFINITE);
assertEquals(Duration.INDEFINITE, animation.getTotalDuration());
animation.setCycleCount(1);
}
@Test
public void testDecreaseTotalDuration() {
animation.jumpTo(ONE_SEC);
animation.shim_setCycleDuration(ONE_SEC.divide(2));
assertEquals(ONE_SEC.divide(2), animation.getCurrentTime());
animation.shim_setCycleDuration(Duration.ZERO);
assertEquals(Duration.ZERO, animation.getCurrentTime());
}
@Test
public void testJumpTo() {
animation.shim_setCycleDuration(TWO_SECS);
assertEquals(TWO_SECS,animation.getCycleDuration());
animation.jumpTo(ONE_SEC);
assertEquals(ONE_SEC, animation.getCurrentTime());
assertEquals(6000, clipEnvelope.getLastJumpTo());
animation.jumpTo(TWO_SECS);
assertEquals(TWO_SECS, animation.getCurrentTime());
assertEquals(12000, clipEnvelope.getLastJumpTo());
animation.jumpTo(Duration.ZERO);
assertEquals(Duration.ZERO, animation.getCurrentTime());
assertEquals(0, clipEnvelope.getLastJumpTo());
animation.jumpTo(TWO_SECS.add(Duration.ONE));
assertEquals(TWO_SECS, animation.getCurrentTime());
assertEquals(12000, clipEnvelope.getLastJumpTo());
animation.jumpTo(Duration.ONE.negate());
assertEquals(Duration.ZERO, animation.getCurrentTime());
assertEquals(0, clipEnvelope.getLastJumpTo());
animation.setCycleCount(2);
animation.jumpTo(ONE_SEC);
assertEquals(ONE_SEC, animation.getCurrentTime());
assertEquals(6000, clipEnvelope.getLastJumpTo());
animation.jumpTo(TWO_SECS);
assertEquals(TWO_SECS, animation.getCurrentTime());
assertEquals(12000, clipEnvelope.getLastJumpTo());
animation.jumpTo(THREE_SECS);
assertEquals(ONE_SEC, animation.getCurrentTime());
assertEquals(18000, clipEnvelope.getLastJumpTo());
animation.jumpTo(FOUR_SECS);
assertEquals(TWO_SECS, animation.getCurrentTime());
assertEquals(24000, clipEnvelope.getLastJumpTo());
animation.jumpTo(Duration.ZERO);
assertEquals(Duration.ZERO, animation.getCurrentTime());
assertEquals(0, clipEnvelope.getLastJumpTo());
animation.jumpTo(FOUR_SECS.add(Duration.ONE));
assertEquals(TWO_SECS, animation.getCurrentTime());
assertEquals(24000, clipEnvelope.getLastJumpTo());
animation.jumpTo(Duration.ONE.negate());
assertEquals(Duration.ZERO, animation.getCurrentTime());
assertEquals(0, clipEnvelope.getLastJumpTo());
}
@Test
public void testJumpTo_ZeroLengthAnimation() {
animation.shim_setCycleDuration(Duration.ZERO);
animation.jumpTo(Duration.ZERO);
assertEquals(Duration.ZERO, animation.getCurrentTime());
assertEquals(0, clipEnvelope.getLastJumpTo());
animation.jumpTo(ONE_SEC);
assertEquals(Duration.ZERO, animation.getCurrentTime());
assertEquals(0, clipEnvelope.getLastJumpTo());
animation.setCycleCount(2);
animation.jumpTo(Duration.ZERO);
assertEquals(Duration.ZERO, animation.getCurrentTime());
assertEquals(0, clipEnvelope.getLastJumpTo());
animation.jumpTo(ONE_SEC);
assertEquals(Duration.ZERO, animation.getCurrentTime());
assertEquals(0, clipEnvelope.getLastJumpTo());
}
@Test
public void testDurationRoundingError() {
final Duration duration = Duration.millis(0.01);
animation.shim_setCycleDuration(duration);
assertTrue(animation.getCycleDuration().greaterThan(Duration.ZERO));
assertFalse(animation.startable(true));
animation.jumpTo(Duration.ZERO);
assertEquals(Duration.ZERO, animation.getCurrentTime());
assertEquals(0, clipEnvelope.getLastJumpTo());
}
@Test(expected=NullPointerException.class)
public void testJumpTo_Null() {
animation.jumpTo((Duration)null);
}
@Test(expected=IllegalArgumentException.class)
public void testJumpTo_UNKNOWN() {
animation.jumpTo(Duration.UNKNOWN);
}
@Test
public void testJumpTo_IndefiniteCycles() {
animation.shim_setCycleDuration(TWO_SECS);
animation.setCycleCount(Animation.INDEFINITE);
animation.jumpTo("end");
assertEquals(TWO_SECS, animation.getCurrentTime());
}
@Test
public void testJumpTo_IndefiniteCycleDuration() {
animation.shim_setCycleDuration(Duration.INDEFINITE);
animation.jumpTo("end");
assertEquals(Duration.millis(Long.MAX_VALUE / 6), animation.getCurrentTime());
}
@Test
public void testJumpToCuePoint_Default() {
animation.getCuePoints().put("ONE_SEC", ONE_SEC);
animation.getCuePoints().put("THREE_SECS", THREE_SECS);
animation.shim_setCycleDuration(TWO_SECS);
animation.jumpTo("end");
assertEquals(TWO_SECS, animation.getCurrentTime());
animation.jumpTo("start");
assertEquals(Duration.ZERO, animation.getCurrentTime());
animation.jumpTo("ONE_SEC");
assertEquals(ONE_SEC, animation.getCurrentTime());
animation.jumpTo("undefined");
assertEquals(ONE_SEC, animation.getCurrentTime());
animation.jumpTo("THREE_SECS");
assertEquals(TWO_SECS, animation.getCurrentTime());
}
@Test
public void testJumpToCuePoint_ZeroLengthAnimation() {
animation.getCuePoints().put("ONE_SEC", ONE_SEC);
animation.shim_setCycleDuration(Duration.ZERO);
animation.jumpTo("start");
assertEquals(Duration.ZERO, animation.getCurrentTime());
animation.jumpTo("end");
assertEquals(Duration.ZERO, animation.getCurrentTime());
animation.jumpTo("ONE_SEC");
assertEquals(Duration.ZERO, animation.getCurrentTime());
}
@Test(expected=NullPointerException.class)
public void testJumpToCuePoint_Null() {
animation.jumpTo((String)null);
}
@Test
public void testPlay() {
final OnFinishedListener listener = new OnFinishedListener();
animation.setOnFinished(listener);
listener.wasCalled = false;
animation.play();
assertEquals(Status.RUNNING, animation.getStatus());
assertFalse(listener.wasCalled);
assertTrue(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
animation.play();
assertEquals(Status.RUNNING, animation.getStatus());
assertFalse(listener.wasCalled);
assertTrue(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
animation.stop();
listener.wasCalled = false;
animation.setRate(0.0);
animation.play();
assertEquals(Status.RUNNING, animation.getStatus());
assertFalse(listener.wasCalled);
assertFalse(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
animation.stop();
listener.wasCalled = false;
animation.setRate(-1.0);
animation.play();
assertEquals(ONE_SEC.toMillis(), animation.getCurrentTime().toMillis(), EPSILON);
assertFalse(listener.wasCalled);
assertTrue(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
animation.stop();
listener.wasCalled = false;
animation.setRate(1.0);
animation.shim_setCycleDuration(Duration.ZERO);
animation.play();
assertEquals(Status.STOPPED, animation.getStatus());
assertTrue(listener.wasCalled);
assertFalse(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
animation.stop();
animation.shim_setCycleDuration(ONE_SEC);
animation.play();
animation.pause();
animation.play();
assertEquals(Status.RUNNING, animation.getStatus());
assertTrue(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
animation.stop();
animation.play();
animation.pause();
animation.setRate(0.0);
animation.play();
assertEquals(Status.RUNNING, animation.getStatus());
assertFalse(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
}
@Test
public void testStop() {
animation.jumpTo(ONE_SEC);
animation.stop();
assertEquals(Status.STOPPED, animation.getStatus());
assertEquals(0.0, animation.getCurrentRate(), EPSILON);
assertEquals(ONE_SEC, animation.getCurrentTime());
assertFalse(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
animation.jumpTo(ONE_SEC);
animation.play();
animation.stop();
assertEquals(Status.STOPPED, animation.getStatus());
assertEquals(0.0, animation.getCurrentRate(), EPSILON);
assertEquals(Duration.ZERO, animation.getCurrentTime());
assertFalse(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
animation.jumpTo(ONE_SEC);
animation.play();
animation.pause();
animation.stop();
assertEquals(Status.STOPPED, animation.getStatus());
assertEquals(0.0, animation.getCurrentRate(), EPSILON);
assertEquals(Duration.ZERO, animation.getCurrentTime());
assertFalse(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
}
@Test
public void testPause() {
animation.jumpTo(ONE_SEC);
animation.pause();
assertEquals(Status.STOPPED, animation.getStatus());
assertEquals(0.0, animation.getCurrentRate(), EPSILON);
assertFalse(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
animation.jumpTo(ONE_SEC);
animation.play();
animation.pause();
assertEquals(Status.PAUSED, animation.getStatus());
assertEquals(0.0, animation.getCurrentRate(), EPSILON);
assertFalse(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
animation.jumpTo(ONE_SEC);
animation.play();
animation.pause();
animation.pause();
assertEquals(Status.PAUSED, animation.getStatus());
assertEquals(0.0, animation.getCurrentRate(), EPSILON);
assertFalse(timer.containsPulseReceiver(animation.shim_pulseReceiver()));
}
@Test
public void testStart() {
assertTrue(animation.startable(true));
animation.doStart(true);
assertEquals(Status.RUNNING, animation.getStatus());
assertEquals(1.0, animation.getCurrentRate(), EPSILON);
assertEquals(6000, clipEnvelope.getTimelineTicks());
assertEquals(1.0, clipEnvelope.getRate(), EPSILON);
assertEquals(false, clipEnvelope.getAutoReverse());
assertEquals(1, clipEnvelope.getCycleCount());
animation.shim_finished();
animation.shim_setCycleDuration(TWO_SECS);
animation.setRate(-2.0);
animation.setAutoReverse(true);
animation.setCycleCount(Animation.INDEFINITE);
assertTrue(animation.startable(true));
animation.doStart(true);
assertEquals(Status.RUNNING, animation.getStatus());
assertEquals(-2.0, animation.getCurrentRate(), EPSILON);
assertEquals(12000, clipEnvelope.getTimelineTicks());
assertEquals(-2.0, clipEnvelope.getRate(), EPSILON);
assertEquals(true, clipEnvelope.getAutoReverse());
assertEquals(Animation.INDEFINITE, clipEnvelope.getCycleCount());
animation.shim_finished();
animation.shim_setCycleDuration(Duration.ZERO);
assertFalse(animation.startable(true));
}
@Test
public void testChangeCycleDurationAfterFinish_RT32657() {
animation.shim_setCycleDuration(TWO_SECS);
animation.play();
assertEquals(Status.RUNNING, animation.getStatus());
assertEquals(Duration.ZERO, animation.getCurrentTime());
animation.setCurrentTicks(12000);
assertEquals(TWO_SECS, animation.getCurrentTime());
animation.shim_finished();
animation.shim_setCycleDuration(ONE_SEC);
animation.play();
assertEquals(Status.RUNNING, animation.getStatus());
assertEquals(Duration.ZERO, animation.getCurrentTime());
}
@Test
public void testFinished() {
final OnFinishedListener listener = new OnFinishedListener();
animation.setOnFinished(listener);
animation.shim_finished();
assertEquals(Status.STOPPED, animation.getStatus());
assertEquals(0.0, animation.getCurrentRate(), EPSILON);
assertTrue(listener.wasCalled);
animation.play();
animation.shim_finished();
assertEquals(Status.STOPPED, animation.getStatus());
assertEquals(0.0, animation.getCurrentRate(), EPSILON);
assertTrue(listener.wasCalled);
animation.play();
animation.pause();
animation.shim_finished();
assertEquals(Status.STOPPED, animation.getStatus());
assertEquals(0.0, animation.getCurrentRate(), EPSILON);
assertTrue(listener.wasCalled);
}
@Test
public void testFinished_ThrowsException() {
final OnFinishedExceptionListener listener = new OnFinishedExceptionListener();
final PrintStream defaultErrorStream = System.err;
final PrintStream nirvana = new PrintStream(new OutputStream() {
@Override
public void write(int i) throws IOException {
}
});
animation.setOnFinished(listener);
try {
System.setErr(nirvana);
} catch (SecurityException ex) {
}
animation.shim_finished();
try {
System.setErr(defaultErrorStream);
} catch (SecurityException ex) {
}
assertEquals(Status.STOPPED, animation.getStatus());
assertEquals(0.0, animation.getCurrentRate(), EPSILON);
assertTrue(listener.wasCalled);
animation.play();
try {
System.setErr(nirvana);
} catch (SecurityException ex) {
}
animation.shim_finished();
try {
System.setErr(defaultErrorStream);
} catch (SecurityException ex) {
}
assertEquals(Status.STOPPED, animation.getStatus());
assertEquals(0.0, animation.getCurrentRate(), EPSILON);
assertTrue(listener.wasCalled);
animation.play();
animation.pause();
try {
System.setErr(nirvana);
} catch (SecurityException ex) {
}
animation.shim_finished();
try {
System.setErr(defaultErrorStream);
} catch (SecurityException ex) {
}
assertEquals(Status.STOPPED, animation.getStatus());
assertEquals(0.0, animation.getCurrentRate(), EPSILON);
assertTrue(listener.wasCalled);
}
@Test
public void testFullSpeedResolution() {
final int resolution = Toolkit.getToolkit().getPrimaryTimer().getDefaultResolution();
animation.doTimePulse(4 * resolution);
assertEquals(4 * resolution, clipEnvelope.getLastTimePulse());
animation.doTimePulse(Math.round(4.5 * resolution));
assertEquals(Math.round(4.5 * resolution), clipEnvelope.getLastTimePulse());
animation.doTimePulse(Math.round(5.5 * resolution));
assertEquals(Math.round(5.5 * resolution), clipEnvelope.getLastTimePulse());
animation.doTimePulse(6 * resolution);
assertEquals(6 * resolution, clipEnvelope.getLastTimePulse());
}
@Test
public void testCustomResolution() {
final int resolution = 100;
animation = new AnimationImpl(timer, clipEnvelope, resolution);
animation.doTimePulse(4 * resolution);
assertEquals(4 * resolution, clipEnvelope.getLastTimePulse());
animation.doTimePulse(Math.round(4.5 * resolution));
assertEquals(0, clipEnvelope.getLastTimePulse());
animation.doTimePulse(Math.round(5.5 * resolution));
assertEquals(Math.round(5.5 * resolution), clipEnvelope.getLastTimePulse());
animation.doTimePulse(6 * resolution);
assertEquals(6 * resolution, clipEnvelope.getLastTimePulse());
}
private static class OnFinishedListener implements EventHandler<ActionEvent> {
private boolean wasCalled = false;
@Override
public void handle(ActionEvent event) {
wasCalled = true;
}
}
private static class OnFinishedExceptionListener implements EventHandler<ActionEvent> {
private boolean wasCalled = false;
@Override
public void handle(ActionEvent event) {
wasCalled = true;
throw new RuntimeException("Test Exception");
}
}
}
