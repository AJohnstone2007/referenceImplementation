package test.robot.com.sun.glass.ui.monocle;
import test.robot.com.sun.glass.ui.monocle.ScrollTestBase;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevice;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
public class ScrollThresholdTest extends ScrollTestBase {
private int delta;
public ScrollThresholdTest(TestTouchDevice device) {
super(device);
}
@BeforeClass
public static void beforeInit() {
int threshold =
Integer.getInteger("com.sun.javafx.gestures.scroll.threshold", 10);
Assume.assumeTrue(threshold > 1);
System.setProperty("monocle.input.touchRadius",
Integer.toString(threshold - 2));
}
@Before
public void init() {
super.init();
Assume.assumeTrue(device.getTapRadius() < getScrollThreshold());
delta = getScrollThreshold() - 1;
}
@Test
public void testMoveUpCheckThreshold() throws Exception {
pressFirstFinger();
moveOneFinger(0, -delta , 1, true);
releaseFirstFinger();
tapToStopInertia();
}
@Test
public void testMoveDownCheckThreshold() throws Exception {
pressFirstFinger();
moveOneFinger(0, delta , 3, true);
releaseFirstFinger();
tapToStopInertia();
}
}
