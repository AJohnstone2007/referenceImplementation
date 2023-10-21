package test.robot.com.sun.glass.ui.monocle;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevice;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevices;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import junit.framework.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;
import java.util.Collection;
import test.com.sun.glass.ui.monocle.TestRunnable;
public class InputDevicePropertyTest extends ParameterizedTestBase {
public InputDevicePropertyTest(TestTouchDevice device) {
super(device);
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
return TestTouchDevices.getTouchDeviceParameters(1);
}
@Before
public void checkPlatform() throws Exception {
Assume.assumeTrue(TestApplication.isMonocle() || TestApplication.isLens());
}
@Test
public void testTouch() throws Exception {
TestRunnable.invokeAndWait(() -> Assert.assertTrue(Platform.isSupported(ConditionalFeature.INPUT_TOUCH)));
}
@Test
public void testMultiTouch() throws Exception {
TestRunnable.invokeAndWait(() -> Assert.assertEquals(device.getPointCount() > 1,
Platform.isSupported(
ConditionalFeature.INPUT_MULTITOUCH)));
}
@Test
public void testPointer() throws Exception {
TestRunnable.invokeAndWait(() -> Assert.assertFalse(
Platform.isSupported(ConditionalFeature.INPUT_POINTER)));
}
}
