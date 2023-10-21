package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevice;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevices;
import javafx.application.Platform;
import javafx.scene.input.InputEvent;
import org.junit.Test;
import org.junit.runners.Parameterized;
import java.util.Collection;
public class TouchExceptionTest extends ParameterizedTestBase {
public TouchExceptionTest(TestTouchDevice device) {
super(device);
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
return TestTouchDevices.getTouchDeviceParameters(1);
}
@Test
public void testRuntimeException() throws Exception {
Platform.runLater(
() -> Thread.currentThread()
.setUncaughtExceptionHandler((t, e) -> TestLogShim.log(e.toString()))
);
TestApplication.getStage().getScene().addEventHandler(
InputEvent.ANY,
(e) -> { throw new RuntimeException(e.toString()); });
final int x = (int) Math.round(width * 0.5);
final int y = (int) Math.round(height * 0.5);
int p = device.addPoint(x, y);
device.sync();
device.removePoint(p);
device.sync();
TestLogShim.waitForLog("Touch pressed: %d, %d", x, y);
TestLogShim.waitForLog("Touch released: %d, %d", x, y);
}
}
