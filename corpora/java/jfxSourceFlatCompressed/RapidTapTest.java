package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevice;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevices;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.Parameterized;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import test.com.sun.glass.ui.monocle.TestRunnable;
public class RapidTapTest extends ParameterizedTestBase {
public RapidTapTest(TestTouchDevice device) {
super(device);
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
return TestTouchDevices.getTouchDeviceParameters(1);
}
@Test
public void tapTwentyTimes() throws Exception {
for (int i = 0; i < 20; i++) {
int p = device.addPoint(width / 2, height / 2);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: PRESSED", 3000);
TestLogShim.waitForLogContaining("Mouse pressed", 3000);
device.removePoint(p);
device.sync();
}
TestRunnable.invokeAndWaitUntilSuccess(() -> {
Assert.assertEquals(20, TestLogShim.countLogContaining(
"TouchPoint: PRESSED"));
Assert.assertEquals(20, TestLogShim.countLogContaining(
"TouchPoint: RELEASED"));
Assert.assertEquals(20,
TestLogShim.countLogContaining("Mouse pressed"));
Assert.assertEquals(20,
TestLogShim.countLogContaining("Mouse released"));
Assert.assertEquals(20,
TestLogShim.countLogContaining("Mouse clicked"));
}, 3000);
}
@Test
public void tapTwentyTimesUnderStress() throws Exception {
final CountDownLatch latch = new CountDownLatch(1);
final AnimationTimer a = new AnimationTimer() {
@Override
public void handle(long now) {
double spinTime = Math.round(50000000.0 * TestApplication.getTimeScale());
long end = now + Math.round(spinTime);
latch.countDown();
while (System.nanoTime() < end) { }
}
};
Platform.runLater(a::start);
latch.await();
try {
for (int i = 0; i < 20; i++) {
int p = device.addPoint(width / 2, height / 2);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: PRESSED", 3000);
TestLogShim.waitForLogContaining("Mouse pressed", 3000);
device.removePoint(p);
device.sync();
}
TestRunnable.invokeAndWaitUntilSuccess(() -> {
Assert.assertEquals(20, TestLogShim.countLogContaining("TouchPoint: PRESSED"));
Assert.assertEquals(20, TestLogShim.countLogContaining("TouchPoint: RELEASED"));
Assert.assertEquals(20, TestLogShim.countLogContaining("Mouse pressed"));
Assert.assertEquals(20, TestLogShim.countLogContaining("Mouse released"));
Assert.assertEquals(20, TestLogShim.countLogContaining("Mouse clicked"));
}, 10000);
} finally {
Platform.runLater(a::stop);
}
}
}
