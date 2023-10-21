package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevice;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevices;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runners.Parameterized;
import java.util.Collection;
import test.com.sun.glass.ui.monocle.TestRunnable;
public class TouchEventLookaheadTest extends ParameterizedTestBase {
public TouchEventLookaheadTest(TestTouchDevice device) {
super(device);
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
return TestTouchDevices.getTouchDeviceParameters(1);
}
@Test
public void mergeMoves() throws Exception {
Assume.assumeTrue(TestApplication.isMonocle());
TestApplication.showFullScreenScene();
TestApplication.addMouseListeners();
TestApplication.addTouchListeners();
TestLogShim.reset();
Rectangle2D r = Screen.getPrimary().getBounds();
final int width = (int) r.getWidth();
final int height = (int) r.getHeight();
final int x1 = (int) Math.round(width * 0.1);
final int y1 = (int) Math.round(height * 0.1);
final int x2 = (int) Math.round(width * 0.9);
final int y2 = (int) Math.round(height * 0.9);
final int x3 = (int) Math.round(width * 0.5);
final int y3 = (int) Math.round(height * 0.5);
TestRunnable.invokeAndWait(() -> {
int p = device.addPoint(x1, y1);
device.sync();
for (int x = x1; x <= x2; x += (x2 - x1) / 100) {
device.setPoint(p, x, y1);
device.sync();
}
for (int y = y1; y <= y2; y += (y2 - y1) / 100) {
device.setPoint(p, x2, y);
device.sync();
}
device.setPoint(p, x3, y3);
device.sync();
device.removePoint(p);
device.sync();
});
TestLogShim.waitForLog("Mouse pressed: " + x1 + ", " + y1, 3000);
TestLogShim.waitForLog("Touch pressed: " + x1 + ", " + y1, 3000);
TestLogShim.waitForLog("Mouse released: " + x3 + ", " + y3, 3000);
TestLogShim.waitForLog("Touch released: " + x3 + ", " + y3, 3000);
TestLogShim.waitForLog("Mouse dragged: " + x3 + ", " + y3, 3000);
TestLogShim.waitForLog("Touch moved: " + x3 + ", " + y3, 3000);
Assert.assertTrue(TestLogShim.countLogContaining("Mouse dragged") <= 3);
Assert.assertTrue(TestLogShim.countLogContaining("Touch moved") <= 3);
}
}
