package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import com.sun.glass.ui.monocle.TouchFilterShim.FlushingFilter;
import com.sun.glass.ui.monocle.TouchFilterShim.LoggingFilter;
import com.sun.glass.ui.monocle.TouchFilterShim.NoMultiplesOfTenOnXFilter;
import com.sun.glass.ui.monocle.TouchFilterShim.OverrideIDFilter;
import com.sun.glass.ui.monocle.TouchFilterShim.TranslateFilter;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
public class TouchPipelineTest extends TouchTestBase {
@Before
public void createDevice() throws Exception {
Assume.assumeTrue(TestApplication.isMonocle());
ui = new UInput();
TestApplication.showFullScreenScene();
TestApplication.addMouseListeners();
TestApplication.addTouchListeners();
TestLogShim.reset();
System.setProperty("monocle.input.ca/fe/ba/be.touchFilters",
TranslateFilter.class.getName() + ","
+ OverrideIDFilter.class.getName() + ","
+ FlushingFilter.class.getName() + ","
+ LoggingFilter.class.getName() + ","
+ NoMultiplesOfTenOnXFilter.class.getName());
Rectangle2D r = Screen.getPrimary().getBounds();
ui.processLine("OPEN");
ui.processLine("EVBIT EV_SYN");
ui.processLine("EVBIT EV_KEY");
ui.processLine("KEYBIT BTN_TOUCH");
ui.processLine("EVBIT EV_ABS");
ui.processLine("ABSBIT ABS_X");
ui.processLine("ABSBIT ABS_Y");
ui.processLine("ABSMIN ABS_X 0");
ui.processLine("ABSMAX ABS_X " + (int) (r.getWidth() - 1));
ui.processLine("ABSMIN ABS_Y 0");
ui.processLine("ABSMAX ABS_Y " + (int) (r.getHeight() - 1));
ui.processLine("PROPBIT INPUT_PROP_POINTER");
ui.processLine("PROPBIT INPUT_PROP_DIRECT");
ui.processLine("PROPERTY ID_INPUT_TOUCHSCREEN 1");
ui.processLine("BUS 0xCA");
ui.processLine("VENDOR 0xFE");
ui.processLine("PRODUCT 0xBA");
ui.processLine("VERSION 0xBE");
ui.processLine("CREATE");
}
@Test
public void testFilters() throws Exception {
ui.processLine("EV_KEY BTN_TOUCH 1");
ui.processLine("EV_ABS ABS_X 195");
ui.processLine("EV_ABS ABS_Y 200");
ui.processLine("EV_SYN");
ui.processLine("EV_ABS ABS_X 200");
ui.processLine("EV_ABS ABS_Y 200");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Touch pressed: 203, 195");
TestLogShim.waitForLog("Touch point id=5 at 203,195");
TestLogShim.waitForLog("Touch moved: 413, 95");
TestLogShim.waitForLog("Touch moved: 313, 95");
TestLogShim.waitForLog("Touch moved: 213, 95");
Assert.assertEquals(0, TestLogShim.countLog("Touch Pressed: 208, 195"));
ui.processLine("EV_KEY BTN_TOUCH 0");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Touch released: 213, 95");
}
}
