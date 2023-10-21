package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevice;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevices;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Parameterized;
import java.util.Collection;
import test.com.sun.glass.ui.monocle.TestRunnable;
public class SingleTouchTest extends ParameterizedTestBase {
public SingleTouchTest(TestTouchDevice device) {
super(device);
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
return TestTouchDevices.getTouchDeviceParameters(1);
}
@Test
public void tap() throws Exception {
final int x = (int) Math.round(width * 0.5);
final int y = (int) Math.round(height * 0.5);
int p = device.addPoint(x, y);
device.sync();
device.removePoint(p);
device.sync();
TestLogShim.waitForLog("Mouse pressed: %d, %d", x, y);
TestLogShim.waitForLog("Mouse released: %d, %d", x, y);
TestLogShim.waitForLog("Mouse clicked: %d, %d", x, y);
TestLogShim.waitForLog("Touch pressed: %d, %d", x, y);
TestLogShim.waitForLog("Touch released: %d, %d", x, y);
Assert.assertEquals("Expected only one touch point", 0,
TestLogShim.getLog().stream()
.filter(s -> s.startsWith("Touch points count"))
.filter(s -> !s.startsWith("Touch points count: [1]")).count());
}
@Test
public void tapHoldRelease() throws Exception {
final int x = (int) Math.round(width * 0.5);
final int y = (int) Math.round(height * 0.5);
int p = device.addPoint(x, y);
device.sync();
TestLogShim.waitForLog("Mouse pressed: %d, %d", x, y);
TestLogShim.waitForLog("Touch pressed: %d, %d", x, y);
TestLogShim.reset();
device.resendStateAndSync();
device.sync();
device.removePoint(p);
device.sync();
TestLogShim.waitForLog("Mouse released: %d, %d", x, y);
TestLogShim.waitForLog("Mouse clicked: %d, %d", x, y);
TestLogShim.waitForLog("Touch released: %d, %d", x, y);
Assert.assertEquals(0, TestLogShim.countLogContaining("Mouse pressed:"));
Assert.assertEquals(0, TestLogShim.countLogContaining("Touch pressed:"));
Assert.assertEquals("Expected only one touch point", 0,
TestLogShim.getLog().stream()
.filter(s -> s.startsWith("Touch points count"))
.filter(s -> !s.startsWith("Touch points count: [1]")).count());
}
@Test
public void tapAndDrag1() throws Exception {
final int x1 = (int) Math.round(width * 0.5);
final int y1 = (int) Math.round(height * 0.5);
final int x2 = (int) Math.round(width * 0.75);
final int y2 = (int) Math.round(height * 0.75);
int p = device.addPoint(x1, y1);
device.sync();
device.setPoint(p, x2, y2);
device.sync();
device.removePoint(p);
device.sync();
TestLogShim.waitForLog("Mouse pressed: %d, %d", x1, y1);
TestLogShim.waitForLog("Mouse dragged: %d, %d", x2, y2);
TestLogShim.waitForLog("Mouse released: %d, %d", x2, y2);
TestLogShim.waitForLog("Mouse clicked: %d, %d", x2, y2);
TestLogShim.waitForLog("Touch pressed: %d, %d", x1, y1);
TestLogShim.waitForLog("Touch moved: %d, %d", x2, y2);
TestLogShim.waitForLog("Touch released: %d, %d", x2, y2);
Assert.assertEquals("Expected only one touch point", 0,
TestLogShim.getLog().stream()
.filter(s -> s.startsWith("Touch points count"))
.filter(s -> !s.startsWith("Touch points count: [1]")).count());
}
@Test
public void tapAndDrag2() throws Exception {
final int x1 = (int) Math.round(width * 0.5);
final int y1 = (int) Math.round(height * 0.5);
final int x2 = (int) Math.round(width * 0.75);
int p = device.addPoint(x1, y1);
device.sync();
device.setPoint(p, x2, y1);
device.sync();
device.removePoint(p);
device.sync();
TestLogShim.waitForLog("Mouse pressed: %d, %d", x1, y1);
TestLogShim.waitForLog("Mouse dragged: %d, %d", x2, y1);
TestLogShim.waitForLog("Mouse released: %d, %d", x2, y1);
TestLogShim.waitForLog("Mouse clicked: %d, %d", x2, y1);
TestLogShim.waitForLog("Touch pressed: %d, %d", x1, y1);
TestLogShim.waitForLog("Touch moved: %d, %d", x2, y1);
TestLogShim.waitForLog("Touch released: %d, %d", x2, y1);
Assert.assertEquals("Expected only one touch point", 0,
TestLogShim.getLog().stream()
.filter(s -> s.startsWith("Touch points count"))
.filter(s -> !s.startsWith("Touch points count: [1]")).count());
}
@Test
public void tapAndDrag3() throws Exception {
final int x1 = (int) Math.round(width * 0.5);
final int y1 = (int) Math.round(height * 0.5);
final int y2 = (int) Math.round(height * 0.75);
int p = device.addPoint(x1, y1);
device.sync();
device.setPoint(p, x1, y2);
device.sync();
device.removePoint(p);
device.sync();
TestLogShim.waitForLog("Mouse pressed: %d, %d", x1, y1);
TestLogShim.waitForLog("Mouse dragged: %d, %d", x1, y2);
TestLogShim.waitForLog("Mouse released: %d, %d", x1, y2);
TestLogShim.waitForLog("Mouse clicked: %d, %d", x1, y2);
TestLogShim.waitForLog("Touch pressed: %d, %d", x1, y1);
TestLogShim.waitForLog("Touch moved: %d, %d", x1, y2);
TestLogShim.waitForLog("Touch released: %d, %d", x1, y2);
Assert.assertEquals("Expected only one touch point", 0,
TestLogShim.getLog().stream()
.filter(s -> s.startsWith("Touch points count"))
.filter(s -> !s.startsWith("Touch points count: [1]")).count());
}
@Test
public void tapWithTinyDrag() throws Exception {
Assume.assumeTrue(device.getTapRadius() > 1);
final int x1 = (int) Math.round(width * 0.5);
final int y1 = (int) Math.round(height * 0.5);
final int x2 = x1 + 1;
final int y2 = y1 + 1;
int p = device.addPoint(x1, y1);
device.sync();
device.setPoint(p, x2, y2);
device.sync();
device.removePoint(p);
device.sync();
TestLogShim.waitForLog("Mouse pressed: %d, %d", x1, y1);
TestLogShim.waitForLog("Mouse released: %d, %d", x1, y1);
TestLogShim.waitForLog("Mouse clicked: %d, %d", x1, y1);
TestLogShim.waitForLog("Touch pressed: %d, %d", x1, y1);
TestLogShim.waitForLog("Touch released: %d, %d", x1, y1);
Assert.assertEquals(0l, TestLogShim.countLogContaining("Mouse dragged"));
Assert.assertEquals(0l, TestLogShim.countLogContaining("Touch moved"));
Assert.assertEquals("Expected only one touch point", 0,
TestLogShim.getLog().stream()
.filter(s -> s.startsWith("Touch points count"))
.filter(s -> !s.startsWith("Touch points count: [1]")).count());
}
@Test
public void tapDragReleaseTapAgain() throws Exception {
Assume.assumeTrue(device.getTapRadius() < width * 0.2);
final int x1 = (int) Math.round(width * 0.5);
final int y1 = (int) Math.round(height * 0.5);
final int x2 = (int) Math.round(width * 0.7);
final int y2 = (int) Math.round(height * 0.7);
int p = device.addPoint(x1, y1);
device.sync();
device.setPoint(p, x2, y2);
device.sync();
device.removePoint(p);
device.sync();
TestLogShim.waitForLog("Mouse pressed: %d, %d", x1, y1);
TestLogShim.waitForLog("Mouse released: %d, %d", x2, y2);
TestLogShim.waitForLog("Mouse clicked: %d, %d", x2, y2);
TestLogShim.waitForLog("Touch pressed: %d, %d", x1, y1);
TestLogShim.waitForLog("Touch released: %d, %d", x2, y2);
TestLogShim.clear();
p = device.addPoint(x1, y1);
device.sync();
TestLogShim.waitForLog("Mouse pressed: %d, %d", x1, y1);
TestLogShim.waitForLog("Touch pressed: %d, %d", x1, y1);
TestLogShim.clear();
device.removePoint(p);
device.sync();
TestLogShim.waitForLog("Mouse released: %d, %d", x1, y1);
TestLogShim.waitForLog("Mouse clicked: %d, %d", x1, y1);
TestLogShim.waitForLog("Touch released: %d, %d", x1, y1);
}
@Ignore("RT-37283")
@Test
public void testChangeSceneDuringTap() throws Exception {
final int x1 = (int) Math.round(width * 0.3);
final int y1 = (int) Math.round(height * 0.3);
int p1 = device.addPoint(x1, y1);
device.sync();
TestLogShim.waitForLog("Touch pressed: %d, %d", x1, y1);
TestRunnable.invokeAndWait(() ->
{
Rectangle r = new Rectangle(0, 0, width, height);
Group g = new Group();
g.getChildren().add(r);
Scene scene = new Scene(g);
TestApplication.getStage().setScene(scene);
});
device.removePoint(p1);
device.sync();
Assert.assertEquals(1, TestLogShim.countLogContaining("Mouse clicked: " + x1 +", " + y1));
}
}
