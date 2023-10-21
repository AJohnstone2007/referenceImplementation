package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevice;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevices;
import javafx.geometry.Rectangle2D;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runners.Parameterized;
import java.util.Collection;
import test.com.sun.glass.ui.monocle.TestRunnable;
public class DragTouchInAndOutAWindowTest extends ParameterizedTestBase {
public DragTouchInAndOutAWindowTest(TestTouchDevice device) {
super(device);
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
return TestTouchDevices.getTouchDeviceParameters(1);
}
@Before
public void setUpScreen() throws Exception {
TestApplication.showInMiddleOfScreen();
TestApplication.addTouchListeners();
int p = device.addPoint(0, 0);
device.sync();
device.removePoint(p);
device.sync();
TestLogShim.reset();
}
@Test
public void singleTouch_dragPointIntoTheWindow() throws Exception {
Stage stage = TestApplication.getStage();
int windowRightEnd = (int)(stage.getX() + stage.getWidth());
int windowMiddleHeight = (int)(stage.getY() + (stage.getHeight() / 2));
int p = device.addPoint(windowRightEnd + 50, windowMiddleHeight);
device.sync();
for (int i = 49; i >= -50 ; i -= 3) {
device.setPoint(p, windowRightEnd + i, windowMiddleHeight);
device.sync();
}
device.removePoint(p);
device.sync();
Assert.assertEquals(0, TestLogShim.countLogContaining("TouchPoint: PRESSED"));
Assert.assertEquals(0, TestLogShim.countLogContaining("TouchPoint: MOVED"));
Assert.assertEquals(0, TestLogShim.countLogContaining("TouchPoint: RELEASED"));
}
@Test
public void singleTouch_dragPointoutsideAwindow() throws Exception {
Stage stage = TestApplication.getStage();
int windowMiddleWidth = (int)(stage.getX() + stage.getWidth() / 2);
int windowMiddleHeight = (int)(stage.getY() + (stage.getHeight() / 2));
int p = device.addPoint(windowMiddleWidth, windowMiddleHeight);
device.sync();
for (int i = 0; i + windowMiddleWidth < width ; i += 5) {
device.setPoint(p, windowMiddleWidth + i, windowMiddleHeight);
device.sync();
}
TestLogShim.waitForLogContaining("TouchPoint: PRESSED", 3000);
device.removePoint(p);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: RELEASED", 3000);
}
@Test
public void singleTouch_dragPointInandOutAwindow() throws Exception {
Stage stage = TestApplication.getStage();
int windowMiddleWidth = (int)(stage.getX() + stage.getWidth() / 2);
int windowMiddleHeight = (int)(stage.getY() + (stage.getHeight() / 2));
int windowRightEnd = (int)(stage.getX() + stage.getWidth());
int i;
int p = device.addPoint(windowMiddleWidth, windowMiddleHeight);
device.sync();
for (i = windowMiddleWidth; i <= windowRightEnd + 100 ; i += 10) {
device.setPoint(p, i, windowMiddleHeight);
device.sync();
}
TestLogShim.waitForLogContaining("TouchPoint: PRESSED", 3000);
TestLogShim.waitForLogContaining("TouchPoint: MOVED", 3000);
for (; i >= windowMiddleWidth ; i -= 10) {
device.setPoint(p, i, windowMiddleHeight);
device.sync();
}
device.removePoint(p);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: RELEASED", 3000);
}
@Test
public void multiTouch_dragPointInandOutAwindow() throws Exception {
Assume.assumeTrue(device.getPointCount() >= 2);
Stage stage = TestApplication.getStage();
int windowMiddleWidth = (int)(stage.getX() + stage.getWidth() / 2);
int windowMiddleHeight = (int)(stage.getY() + (stage.getHeight() / 2));
int windowRightEnd = (int)(stage.getX() + stage.getWidth());
int i;
int p1 = device.addPoint(windowRightEnd + 15, windowMiddleHeight);
int p2 = device.addPoint(windowRightEnd + 15, windowMiddleHeight + 10);
device.sync();
for (i = windowRightEnd + 12; i >= windowMiddleWidth ; i -= 3) {
device.setPoint(p1, i, windowMiddleHeight);
device.setPoint(p2, i, windowMiddleHeight + 10);
device.sync();
}
for (; i + windowMiddleWidth < width ; i += 5) {
device.setPoint(p1, i, windowMiddleHeight);
device.setPoint(p2, i, windowMiddleHeight + 10);
device.sync();
}
device.removePoint(p1);
device.removePoint(p2);
device.sync();
Assert.assertEquals(0, TestLogShim.countLogContaining("TouchPoint: PRESSED"));
Assert.assertEquals(0, TestLogShim.countLogContaining("TouchPoint: MOVED"));
Assert.assertEquals(0, TestLogShim.countLogContaining("TouchPoint: RELEASED"));
}
@Ignore("RT-38482")
@Test
public void multiTouch_dragTwoPointsIntoTheWindow() throws Exception {
Assume.assumeTrue(device.getPointCount() >= 2);
Stage stage = TestApplication.getStage();
double[] bounds = {0.0, 0.0, 0.0, 0.0};
TestRunnable.invokeAndWait(() -> {
bounds[0] = stage.getX();
bounds[1] = stage.getY();
bounds[2] = stage.getWidth();
bounds[3] = stage.getHeight();
});
Rectangle2D stageBounds = new Rectangle2D(bounds[0], bounds[1],
bounds[2], bounds[3]);
int windowX = (int) (stageBounds.getMinX());
int windowY = (int) (stageBounds.getMinY());
int windowMiddleX = (int) (stageBounds.getMinX() + stageBounds.getWidth() / 2);
int windowMiddleY = (int) (stageBounds.getMinY() + stageBounds.getHeight() / 2);
int windowRightEnd = (int) (stageBounds.getMaxX());
int distance = device.getTapRadius() + 2;
int x1 = windowRightEnd + distance;
int y1 = windowMiddleY;
int x2 = windowRightEnd + distance * 2;
int y2 = y1;
Assert.assertTrue(x1 < width && x2 < width);
int p1 = device.addPoint(x1, y1);
int p2 = device.addPoint(x2, y2);
device.sync();
for (int i = x1 - 3; i >= windowMiddleX; i -= 3) {
device.setPoint(p1, i, windowMiddleY);
device.setPoint(p2, i + distance, windowMiddleY);
device.sync();
}
device.removePoint(p1);
device.removePoint(p2);
device.sync();
int x3 = windowX;
int y3 = windowY;
int p = device.addPoint(x3, y3);
device.sync();
device.removePoint(p);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: PRESSED %d, %d", x3, y3);
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", x3, y3);
Assert.assertEquals(1, TestLogShim.countLogContaining("TouchPoint: PRESSED"));
Assert.assertEquals(1, TestLogShim.countLogContaining("TouchPoint: RELEASED"));
Assert.assertEquals(0, TestLogShim.countLogContaining("TouchPoint: MOVED"));
}
}
