package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.ParameterizedTestBase;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevice;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevices;
import org.junit.Test;
import org.junit.runners.Parameterized;
import java.util.Collection;
public class MultiTouch2Test extends ParameterizedTestBase {
public MultiTouch2Test(TestTouchDevice device) {
super(device);
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
return TestTouchDevices.getTouchDeviceParameters(2);
}
@Test
public void twoFingerTap() throws Exception {
final int x1 = (int) Math.round(width * 0.5f);
final int y1 = (int) Math.round(height * 0.5f);
final int x2 = (int) Math.round(width * 0.75f);
final int y2 = (int) Math.round(height * 0.75f);
TestLogShim.reset();
int p1 = device.addPoint(x1, y1);
int p2 = device.addPoint(x2, y2);
device.sync();
TestLogShim.waitForLog("Touch pressed: "
+ x1 + ", " + y1, 3000);
TestLogShim.waitForLogContaining("TouchPoint: PRESSED " + x1 + ", " + y1, 3000);
TestLogShim.waitForLogContaining("TouchPoint: PRESSED " + x2 + ", " + y2, 3000);
TestLogShim.reset();
device.removePoint(p1);
device.removePoint(p2);
device.sync();
TestLogShim.waitForLog("Touch released: " + x1 + ", " + y1, 3000);
}
@Test
public void pressTwoFingersReleaseOne() throws Exception {
final int x1 = (int) Math.round(width / 8.0);
final int y1 = (int) Math.round(height / 8.0);
final int x2 = (int) Math.round(width / 5.0);
final int y2 = (int) Math.round(height / 5.0);
final int x3 = (int) Math.round(width / 3.0);
final int y3 = (int) Math.round(height / 3.0);
TestLogShim.reset();
int p1 = device.addPoint(x1, y1);
int p2 = device.addPoint(x2, y2);
device.sync();
TestLogShim.waitForLog("Mouse pressed: " + x1 + ", " + y1, 3000l);
TestLogShim.waitForLog("Touch pressed: " + x1 + ", " + y1, 3000l);
TestLogShim.waitForLogContaining("TouchPoint: PRESSED " + x1 + ", " + y1, 3000l);
TestLogShim.waitForLogContaining("TouchPoint: PRESSED " + x2 + ", " + y2, 3000l);
TestLogShim.waitForLog("Touch pressed: " + x2 + ", " + y2, 3000l);
TestLogShim.reset();
device.removePoint(p1);
device.sync();
TestLogShim.waitForLog("Touch released: " + x1 + ", " + y1, 3000l);
TestLogShim.waitForLogContaining("TouchPoint: RELEASED " + x1 + ", " + y1, 3000l);
TestLogShim.waitForLogContaining("TouchPoint: STATIONARY " + x2 + ", " + y2, 3000l);
TestLogShim.reset();
device.setPoint(p2, x3, y3);
device.sync();
TestLogShim.waitForLog("Mouse dragged: " + x3 + ", " + y3, 3000l);
TestLogShim.reset();
device.removePoint(p2);
device.sync();
TestLogShim.waitForLogContaining("Mouse released: " + x3 + ", " + y3, 3000l);
TestLogShim.waitForLogContaining("TouchPoint: RELEASED " + x3 + ", " + y3, 3000l);
}
@Test
public void twoFingerDrag() throws Exception {
final int x1 = (int) Math.round(width * 0.5f);
final int y1 = (int) Math.round(height * 0.5f);
final int x2 = (int) Math.round(width * 0.75f);
final int y2 = (int) Math.round(height * 0.75f);
final int dx = device.getTapRadius();
final int dy = device.getTapRadius();
int p1 = device.addPoint(x1, y1);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: PRESSED %d, %d", x1, y1);
int p2 = device.addPoint(x2, y2);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: STATIONARY %d, %d", x1, y1);
TestLogShim.waitForLogContaining("TouchPoint: PRESSED %d, %d", x2, y2);
for (int i = 1; i < 10; i++) {
TestLogShim.reset();
device.setPoint(p1, x1 + dx * i, y1 + dy * i);
device.setPoint(p2, x2 + dx * i, y2 + dy * i);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d",
x1 + dx * i, y1 + dy * i);
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d",
x2 + dx * i, y2 + dy * i);
}
for (int i = 8; i >= 0; i--) {
TestLogShim.reset();
device.setPoint(p1, x1 + dx * i, y1 + dy * i);
device.setPoint(p2, x2 + dx * i, y2 + dy * i);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d",
x1 + dx * i, y1 + dy * i);
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d",
x2 + dx * i, y2 + dy * i);
}
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d", x1, y1);
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d", x2, y2);
TestLogShim.reset();
device.removePoint(p1);
device.sync();
TestLogShim.waitForLogContaining("Touch released: %d, %d", x1, y1);
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", x1, y1);
TestLogShim.waitForLogContaining("TouchPoint: STATIONARY %d, %d", x2, y2);
TestLogShim.reset();
device.removePoint(p2);
device.sync();
TestLogShim.waitForLogContaining("Touch released: %d, %d", x2, y2);
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", x2, y2);
TestLogShim.reset();
int p3 = device.addPoint(x1, y1);
device.sync();
device.removePoint(p3);
device.sync();
TestLogShim.waitForLog("Mouse clicked: %d, %d", x1, y1);
}
@Test
public void pressReleasePressTest() throws Exception {
int x1 = (int) Math.round(width / 2);
int y1 = (int) Math.round(height * 0.3);
int x2 = (int) Math.round(width / 2);
int y2 = (int) Math.round(height * 0.7);
TestLogShim.reset();
int p1 = device.addPoint(x1, y1);
int p2 = device.addPoint(x2, y2);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: PRESSED %d, %d", x1, y1);
TestLogShim.waitForLogContaining("TouchPoint: PRESSED %d, %d", x2, y2);
TestLogShim.reset();
device.removePoint(p1);
device.removePoint(p2);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", x1, y1);
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", x2, y2);
TestLogShim.reset();
p1 = device.addPoint(x1, y1);
p2 = device.addPoint(x2, y2);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: PRESSED %d, %d", x1, y1);
TestLogShim.waitForLogContaining("TouchPoint: PRESSED %d, %d", x2, y2);
TestLogShim.reset();
device.removePoint(p1);
device.removePoint(p2);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", x1, y1);
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", x2, y2);
}
@Test
public void twoFingerDragSingleFingerTap() throws Exception {
final int x1 = (int) Math.round(width * 0.5f);
final int y1 = (int) Math.round(height * 0.5f);
final int x2 = (int) Math.round(width * 0.75f);
final int y2 = (int) Math.round(height * 0.75f);
final int dx = device.getTapRadius();
final int dy = device.getTapRadius();
int p1 = device.addPoint(x1, y1);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: PRESSED %d, %d", x1, y1);
int p2 = device.addPoint(x2, y2);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: STATIONARY %d, %d", x1, y1);
TestLogShim.waitForLogContaining("TouchPoint: PRESSED %d, %d", x2, y2);
device.setPoint(p1, x1 + dx, y1);
device.setPoint(p2, x2 + dx, y2);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d", x1 + dx, y1);
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d", x2 + dx, y2);
TestLogShim.reset();
device.removePoint(p1);
device.sync();
TestLogShim.waitForLogContaining("Touch released: %d, %d", x1 + dx, y1);
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", x1 + dx, y1);
TestLogShim.waitForLogContaining("TouchPoint: STATIONARY %d, %d", x2 + dx, y2);
TestLogShim.reset();
device.removePoint(p2);
device.sync();
TestLogShim.waitForLogContaining("Touch released: %d, %d", x2 + dx, y2);
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", x2 + dx, y2);
int p3 = device.addPoint(x1, y1 + dy);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: PRESSED %d, %d", x1, y1 + dy);
device.removePoint(p3);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", x1, y1 + dy);
}
@Test
public void twoFingersPressDragOne() throws Exception {
int delta = device.getTapRadius() + 1;
TestLogShim.reset();
int x1 = (int) Math.round(width * 0.2);
int y1 = (int) Math.round(height * 0.2);
int x2 = (int) Math.round(width * 0.5);
int y2 = (int) Math.round(height * 0.5);
int p1 = device.addPoint(x1, y1);
int p2 = device.addPoint(x2, y2);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: PRESSED %d, %d", x1, y1);
TestLogShim.waitForLogContaining("TouchPoint: PRESSED %d, %d", x2, y2);
TestLogShim.reset();
for (int i = 1; i < 4; i++) {
x2 += delta;
device.setPoint(p2, x2, y2);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d", x2, y2);
}
TestLogShim.reset();
device.removePoint(p1);
device.removePoint(p2);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", x1, y1);
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", x2, y2);
TestLogShim.reset();
int p3 = device.addPoint(x1, y1);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: PRESSED %d, %d", x1, y1);
TestLogShim.waitForLogContaining("Mouse pressed: %d, %d", x1, y1);
TestLogShim.waitForLogContaining("Touch points count: [1]");
device.removePoint(p3);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", x1, y1);
TestLogShim.waitForLogContaining("Mouse released: %d, %d", x1, y1);
TestLogShim.waitForLogContaining("Mouse clicked: %d, %d", x1, y1);
}
}
