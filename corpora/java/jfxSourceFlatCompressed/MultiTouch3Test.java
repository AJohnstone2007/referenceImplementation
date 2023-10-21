package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.ParameterizedTestBase;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevice;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevices;
import org.junit.Test;
import org.junit.runners.Parameterized;
import java.util.Collection;
public class MultiTouch3Test extends ParameterizedTestBase {
public MultiTouch3Test(TestTouchDevice device) {
super(device);
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
return TestTouchDevices.getTouchDeviceParameters(3);
}
@Test
public void touchSequence() throws Exception {
final int x1 = (int) Math.round(width * 0.5f);
final int y1 = (int) Math.round(height * 0.5f);
final int x2 = (int) Math.round(width * 0.75f);
final int y2 = (int) Math.round(height * 0.75f);
final int x3 = (int) Math.round(width * 0.25f);
final int y3 = (int) Math.round(height * 0.25f);
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
int p3 = device.addPoint(x3, y3);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: STATIONARY %d, %d", x1, y1);
TestLogShim.waitForLogContaining("TouchPoint: STATIONARY %d, %d", x2, y2);
TestLogShim.waitForLogContaining("TouchPoint: PRESSED %d, %d", x3, y3);
for (int i = 1; i < 10; i++) {
TestLogShim.reset();
device.setPoint(p1, x1 + dx * i, y1 + dy * i);
device.setPoint(p2, x2 + dx * i, y2 + dy * i);
device.setPoint(p3, x3 + dx * i, y3 + dy * i);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d",
x1 + dx * i, y1 + dy * i);
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d",
x2 + dx * i, y2 + dy * i);
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d",
x3 + dx * i, y3 + dy * i);
}
for (int i = 8; i >= 0; i--) {
TestLogShim.reset();
device.setPoint(p1, x1 + dx * i, y1 + dy * i);
device.setPoint(p2, x2 + dx * i, y2 + dy * i);
device.setPoint(p3, x3 + dx * i, y3 + dy * i);
device.sync();
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d",
x1 + dx * i, y1 + dy * i);
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d",
x2 + dx * i, y2 + dy * i);
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d",
x3 + dx * i, y3 + dy * i);
}
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d", x1, y1);
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d", x2, y2);
TestLogShim.waitForLogContaining("TouchPoint: MOVED %d, %d", x3, y3);
TestLogShim.reset();
device.removePoint(p1);
device.sync();
TestLogShim.waitForLogContaining("Touch released: %d, %d", x1, y1);
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", x1, y1);
TestLogShim.waitForLogContaining("TouchPoint: STATIONARY %d, %d", x2, y2);
TestLogShim.waitForLogContaining("TouchPoint: STATIONARY %d, %d", x3, y3);
TestLogShim.reset();
device.removePoint(p2);
device.sync();
TestLogShim.waitForLogContaining("Touch released: %d, %d", x2, y2);
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", x2, y2);
TestLogShim.waitForLogContaining("TouchPoint: STATIONARY %d, %d", x3, y3);
device.removePoint(p3);
device.sync();
TestLogShim.waitForLog("Touch released: %d, %d", x3, y3);
TestLogShim.waitForLogContaining("TouchPoint: RELEASED %d, %d", x3, y3);
}
}
