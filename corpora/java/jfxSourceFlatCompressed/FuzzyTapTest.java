package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.ParameterizedTestBase;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevice;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevices;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.Parameterized;
import java.util.Collection;
public class FuzzyTapTest extends ParameterizedTestBase {
public FuzzyTapTest(TestTouchDevice device) {
super(device);
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
return TestTouchDevices.getTouchDeviceParameters(1);
}
@Test
public void tap1() throws Exception {
final int x = (int) width / 2;
final int y = (int) height / 2;
final int tapRadius = device.getTapRadius();
final int x1 = x + tapRadius / 2;
final int y1 = y + tapRadius / 2;
int p = device.addPoint(x, y);
device.sync();
device.setAndRemovePoint(p, x1, y1);
device.sync();
TestLogShim.waitForLog("Mouse pressed: " + x + ", " + y, 3000);
TestLogShim.waitForLog("Mouse clicked: " + x + ", " + y, 3000);
TestLogShim.waitForLog("Mouse released: " + x + ", " + y, 3000);
Assert.assertEquals(0, TestLogShim.countLogContaining("Mouse dragged:"));
Assert.assertEquals(0, TestLogShim.countLogContaining("Touch moved:"));
}
@Test
public void tap1a() throws Exception {
final int x = (int) width / 2;
final int y = (int) height / 2;
final int tapRadius = device.getTapRadius();
final int x1 = x + tapRadius / 2;
final int y1 = y + tapRadius / 2;
int p = device.addPoint(x, y);
device.sync();
device.setPoint(p, x1, y1);
device.sync();
device.removePoint(p);
device.sync();
TestLogShim.waitForLog("Mouse clicked: " + x + ", " + y, 3000);
TestLogShim.waitForLog("Mouse released: " + x + ", " + y, 3000);
Assert.assertEquals(0, TestLogShim.countLogContaining("Mouse dragged:"));
Assert.assertEquals(0, TestLogShim.countLogContaining("Touch moved:"));
}
@Test
public void tap2() throws Exception {
final int x = (int) width / 2;
final int y = (int) height / 2;
final int tapRadius = device.getTapRadius();
final int x1 = x + tapRadius;
final int y1 = y + tapRadius;
int p = device.addPoint(x, y);
device.sync();
device.setAndRemovePoint(p, x1, y1);
device.sync();
TestLogShim.waitForLog("Mouse pressed: " + x + ", " + y, 3000);
TestLogShim.waitForLog("Mouse released: " + x + ", " + y, 3000);
TestLogShim.waitForLog("Mouse clicked: " + x + ", " + y, 3000);
TestLogShim.waitForLog("Touch pressed: " + x + ", " + y, 3000);
TestLogShim.waitForLog("Touch released: " + x + ", " + y, 3000);
Assert.assertEquals(1, TestLogShim.countLogContaining("Mouse clicked:"));
}
@Test
public void tap2a() throws Exception {
final int x = (int) width / 2;
final int y = (int) height / 2;
final int tapRadius = device.getTapRadius();
final int x1 = x + tapRadius;
final int y1 = y + tapRadius;
int p = device.addPoint(x, y);
device.sync();
device.setPoint(p, x1, y1);
device.sync();
device.removePoint(p);
device.sync();
TestLogShim.waitForLog("Mouse pressed: " + x + ", " + y, 3000);
TestLogShim.waitForLog("Mouse dragged: " + x1 + ", " + y1, 3000);
TestLogShim.waitForLog("Mouse released: " + x1 + ", " + y1, 3000);
TestLogShim.waitForLog("Mouse clicked: " + x1 + ", " + y1, 3000);
TestLogShim.waitForLog("Touch pressed: " + x + ", " + y, 3000);
TestLogShim.waitForLog("Touch moved: " + x1 + ", " + y1, 3000);
TestLogShim.waitForLog("Touch released: " + x1 + ", " + y1, 3000);
Assert.assertEquals(1, TestLogShim.countLogContaining("Mouse clicked: " + x1 + ", " + y1));
}
@Test
public void tap3b() throws Exception {
final int x = (int) width / 2;
final int y = (int) height / 2;
final int tapRadius = device.getTapRadius();
final int x1 = x + tapRadius * 2;
final int y1 = y + tapRadius * 2;
final int x2 = x + tapRadius * 3;
final int y2 = y + tapRadius * 3;
int p = device.addPoint(x, y);
device.sync();
for (int i = 0; i < tapRadius * 2; i++) {
device.setPoint(p, x + i, y + i);
device.sync();
}
device.setPoint(p, 0, 0);
device.sync();
device.setPoint(p, x2, y2);
device.sync();
device.removePoint(p);
device.sync();
TestLogShim.waitForLog("Mouse pressed: " + x + ", " + y, 3000);
TestLogShim.waitForLog("Mouse dragged: " + x2 + ", " + y2, 3000);
TestLogShim.waitForLog("Mouse released: " + x2 + ", " + y2, 3000);
TestLogShim.waitForLog("Mouse clicked: " + x2 + ", " + y2, 3000);
TestLogShim.waitForLog("Touch pressed: " + x + ", " + y, 3000);
TestLogShim.waitForLog("Touch moved: " + x2 + ", " + y2, 3000);
TestLogShim.waitForLog("Touch released: " + x2 + ", " + y2, 3000);
Assert.assertEquals(1, TestLogShim.countLogContaining("Mouse clicked: " + x2 + ", " + y2));
}
}
