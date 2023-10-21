package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevice;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevices;
import org.junit.Test;
import org.junit.runners.Parameterized;
import java.util.Collection;
public class DoubleClickTest extends ParameterizedTestBase {
public DoubleClickTest(TestTouchDevice device) {
super(device);
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
return TestTouchDevices.getTouchDeviceParameters(1);
}
@Test
public void testDoubleClick1() throws Exception {
int x = (int) Math.round(width / 2.0);
int y = (int) Math.round(height / 2.0);
TestApplication.getStage().getScene().setOnMouseClicked((e) -> TestLogShim.format("Mouse clicked: %d, %d: clickCount %d",
(int) e.getScreenX(), (int) e.getScreenY(),
e.getClickCount()));
TestLogShim.reset();
int p = device.addPoint(x, y);
device.sync();
device.removePoint(p);
device.sync();
p = device.addPoint(x, y);
device.sync();
device.removePoint(p);
device.sync();
TestLogShim.waitForLog("Mouse clicked: " + x + ", " + y + ": clickCount 1", 3000l);
TestLogShim.waitForLog("Mouse clicked: " + x + ", " + y + ": clickCount 2", 3000l);
}
@Test
public void testDoubleClick2() throws Exception {
int x1 = (int) Math.round(width / 2.0);
int y1 = (int) Math.round(height / 2.0);
int x2 = x1 + device.getTapRadius();
int y2 = y1 + device.getTapRadius();
TestApplication.getStage().getScene().setOnMouseClicked((e) -> TestLogShim.format("Mouse clicked: %d, %d: clickCount %d",
(int) e.getScreenX(), (int) e.getScreenY(),
e.getClickCount()));
TestLogShim.reset();
int p = device.addPoint(x1, y1);
device.sync();
device.removePoint(p);
device.sync();
p = device.addPoint(x2, y2);
device.sync();
device.removePoint(p);
device.sync();
TestLogShim.waitForLog("Mouse clicked: " + x1 + ", " + y1 + ": clickCount 1", 3000l);
TestLogShim.waitForLog("Mouse clicked: " + x2 + ", " + y2 + ": clickCount 2", 3000l);
}
}
