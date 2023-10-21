package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import test.com.sun.glass.ui.monocle.TestRunnable;
public class SimpleMouseTest {
private UInput ui;
@Rule public TestName name = new TestName();
@Before public void setUpScreen() throws Exception {
TestLogShim.reset();
TestLogShim.log(name.getMethodName());
TestApplication.showFullScreenScene();
TestApplication.addMouseListeners();
TestApplication.movePointerTo(300, 300);
initDevice();
}
public void initDevice() throws Exception {
ui = new UInput();
ui.processLine("OPEN");
ui.processLine("EVBIT EV_KEY");
ui.processLine("EVBIT EV_SYN");
ui.processLine("KEYBIT BTN_LEFT");
ui.processLine("EVBIT EV_REL");
ui.processLine("RELBIT REL_X");
ui.processLine("RELBIT REL_Y");
ui.processLine("RELBIT REL_WHEEL");
ui.processLine("PROPERTY ID_INPUT_MOUSE 1");
ui.processLine("CREATE");
}
@After public void destroyDevice() throws Exception {
if (ui != null) {
ui.waitForQuiet();
try {
ui.processLine("DESTROY");
} catch (RuntimeException e) { }
ui.processLine("CLOSE");
ui.dispose();
}
}
@Test
public void testRelativeMove() throws Exception {
ui.processLine("EV_REL REL_X -100");
ui.processLine("EV_REL REL_Y -50");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Mouse moved: 200, 250", 3000);
}
@Test
public void testRelativeDrag() throws Exception {
ui.processLine("EV_KEY BTN_LEFT 1");
ui.processLine("EV_SYN");
ui.processLine("EV_REL REL_X -100");
ui.processLine("EV_REL REL_Y -50");
ui.processLine("EV_SYN");
ui.processLine("EV_KEY BTN_LEFT 0");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Mouse pressed: 300, 300", 3000);
TestLogShim.waitForLog("Mouse dragged: 200, 250", 3000);
TestLogShim.waitForLog("Mouse released: 200, 250", 3000);
}
@Test
public void testWheel() throws Exception {
TestApplication.getStage().getScene().setOnScroll(
(e) -> TestLogShim.format("Scroll: %.0g",
Math.signum(e.getDeltaY())));
ui.processLine("EV_SYN");
ui.processLine("EV_REL REL_WHEEL 1");
ui.processLine("EV_SYN");
ui.processLine("EV_REL REL_WHEEL 0");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Scroll: 1");
TestLogShim.reset();
ui.processLine("EV_REL REL_WHEEL -1");
ui.processLine("EV_SYN");
ui.processLine("EV_REL REL_WHEEL 0");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Scroll: -1");
}
@Test
public void testWheelSequence() throws Exception {
TestApplication.getStage().getScene().setOnScroll(
(e) -> TestLogShim.format("Scroll: %.0g",
Math.signum(e.getDeltaY())));
ui.processLine("EV_REL REL_WHEEL 1");
ui.processLine("EV_SYN");
ui.processLine("EV_REL REL_WHEEL 1");
ui.processLine("EV_SYN");
ui.processLine("EV_REL REL_WHEEL 1");
ui.processLine("EV_SYN");
new TestRunnable() {
@Override
public void test() {
Assert.assertEquals(3, TestLogShim.countLogContaining("Scroll: 1"));
}
}.invokeAndWaitUntilSuccess(3000l);
TestLogShim.reset();
ui.processLine("EV_REL REL_WHEEL -1");
ui.processLine("EV_SYN");
ui.processLine("EV_REL REL_WHEEL -1");
ui.processLine("EV_SYN");
ui.processLine("EV_REL REL_WHEEL -1");
ui.processLine("EV_SYN");
new TestRunnable() {
@Override
public void test() {
Assert.assertEquals(3, TestLogShim.countLogContaining("Scroll: -1"));
}
}.invokeAndWaitUntilSuccess(3000l);
}
@Test
public void testClickLeft() throws Exception {
ui.processLine("EV_KEY BTN_LEFT 1");
ui.processLine("EV_SYN SYN_REPORT 0");
ui.processLine("EV_KEY BTN_LEFT 0");
ui.processLine("EV_SYN SYN_REPORT 0");
TestLogShim.waitForLogContaining("Mouse pressed: 300, 300");
TestLogShim.waitForLogContaining("Mouse released: 300, 300");
TestLogShim.waitForLogContaining("Mouse clicked: 300, 300");
}
@Test
public void testClickRight() throws Exception {
ui.processLine("EV_KEY BTN_RIGHT 1");
ui.processLine("EV_SYN SYN_REPORT 0");
ui.processLine("EV_KEY BTN_RIGHT 0");
ui.processLine("EV_SYN SYN_REPORT 0");
TestLogShim.waitForLogContaining("Mouse pressed: 300, 300");
TestLogShim.waitForLogContaining("Mouse released: 300, 300");
TestLogShim.waitForLogContaining("Mouse clicked: 300, 300");
}
@Test
public void testDragLookahead() throws Exception {
Assume.assumeTrue(TestApplication.isMonocle());
TestApplication.showFullScreenScene();
TestApplication.addMouseListeners();
TestLogShim.reset();
Rectangle2D r = Screen.getPrimary().getBounds();
final int width = (int) r.getWidth();
final int height = (int) r.getHeight();
final int x1 = (int) Math.round(width * 0.1);
final int y1 = (int) Math.round(height * 0.1);
final int delta = (int) Math.min(width / 2.0, height / 2.0);
final int x2 = x1 + delta;
final int y2 = y1 + delta;
final int x3 = (int) Math.round(width * 0.9);
final int y3 = (int) Math.round(height * 0.9);
ui.processLine("EV_REL REL_X " + -width);
ui.processLine("EV_REL REL_Y " + -height);
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Mouse moved: 0, 0");
ui.processLine("EV_REL REL_X " + x1);
ui.processLine("EV_REL REL_Y " + y1);
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Mouse moved: %d, %d", x1, y1);
TestRunnable.invokeAndWait(() -> {
ui.processLine("EV_KEY BTN_LEFT 1");
ui.processLine("EV_SYN");
for (int i = 0; i < delta; i++) {
ui.processLine("EV_REL REL_X 1");
ui.processLine("EV_REL REL_Y 1");
ui.processLine("EV_SYN");
}
ui.processLine("EV_REL REL_X " + (x3 - x2));
ui.processLine("EV_REL REL_Y " + (y3 - y2));
ui.processLine("EV_SYN");
ui.processLine("EV_KEY BTN_LEFT 0");
ui.processLine("EV_SYN");
});
TestLogShim.waitForLog("Mouse pressed: %d, %d", x1, y1);
TestLogShim.waitForLog("Mouse released: %d, %d", x3, y3);
TestLogShim.waitForLog("Mouse dragged: %d, %d", x3, y3);
Assert.assertTrue(TestLogShim.countLogContaining("Mouse dragged") <= (x2 - x1) / 10);
}
@Test
public void testMoveLookahead() throws Exception {
Assume.assumeTrue(TestApplication.isMonocle());
TestApplication.showFullScreenScene();
TestApplication.addMouseListeners();
TestLogShim.reset();
Rectangle2D r = Screen.getPrimary().getBounds();
final int width = (int) r.getWidth();
final int height = (int) r.getHeight();
final int x1 = (int) Math.round(width * 0.1);
final int y1 = (int) Math.round(height * 0.1);
final int delta = (int) Math.min(width / 2.0, height / 2.0);
final int x2 = x1 + delta;
final int y2 = y1 + delta;
final int x3 = (int) Math.round(width * 0.9);
final int y3 = (int) Math.round(height * 0.9);
ui.processLine("EV_REL REL_X " + -width);
ui.processLine("EV_REL REL_Y " + -height);
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Mouse moved: 0, 0");
ui.processLine("EV_REL REL_X " + x1);
ui.processLine("EV_REL REL_Y " + y1);
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Mouse moved: %d, %d", x1, y1);
TestRunnable.invokeAndWait(() -> {
for (int i = 0; i < delta; i++) {
ui.processLine("EV_REL REL_X 1");
ui.processLine("EV_REL REL_Y 1");
ui.processLine("EV_SYN");
}
ui.processLine("EV_REL REL_X " + (x3 - x2));
ui.processLine("EV_REL REL_Y " + (y3 - y2));
ui.processLine("EV_SYN");
});
TestLogShim.waitForLog("Mouse moved: %d, %d", x3, y3);
Assert.assertTrue(TestLogShim.countLogContaining("Mouse moved") <= (x2 - x1) / 10);
Assert.assertEquals(0, TestLogShim.countLogContaining("Mouse pressed"));
Assert.assertEquals(0, TestLogShim.countLogContaining("Mouse released"));
Assert.assertEquals(0, TestLogShim.countLogContaining("Mouse clicked"));
}
@Test
public void testGrab1() throws Exception {
TestApplication.showInMiddleOfScreen();
TestApplication.addMouseListeners();
Rectangle2D r = TestApplication.getScreenBounds();
final int width = (int) r.getWidth();
final int height = (int) r.getHeight();
final int x1 = (int) Math.round(width * 0.5);
final int y1 = (int) Math.round(height * 0.5);
final int x2 = (int) Math.round(width * 0.7);
final int y2 = (int) Math.round(height * 0.7);
final int x3 = (int) Math.round(width * 0.9);
final int y3 = (int) Math.round(height * 0.9);
TestApplication.movePointerTo(x1, y1);
ui.processLine("EV_KEY BTN_LEFT 1");
ui.processLine("EV_SYN");
ui.processLine("EV_REL REL_X " + (x2 - x1));
ui.processLine("EV_REL REL_Y " + (y2 - y1));
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Mouse dragged: %d, %d", x2, y2);
ui.processLine("EV_REL REL_X " + (x3 - x2));
ui.processLine("EV_REL REL_Y " + (y3 - y2));
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Mouse dragged: %d, %d", x3, y3);
TestLogShim.waitForLog("Mouse exited: %d, %d", x3, y3);
ui.processLine("EV_REL REL_X " + (x2 - x3));
ui.processLine("EV_REL REL_Y " + (y2 - y3));
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Mouse dragged: %d, %d", x2, y2);
TestLogShim.waitForLog("Mouse entered: %d, %d", x2, y2);
ui.processLine("EV_KEY BTN_LEFT 0");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Mouse released: %d, %d", x2, y2);
TestLogShim.waitForLog("Mouse clicked: %d, %d", x2, y2);
}
@Test
public void testGrab2() throws Exception {
TestApplication.showInMiddleOfScreen();
TestApplication.addMouseListeners();
Assume.assumeTrue(TestApplication.isMonocle());
Rectangle2D r = TestApplication.getScreenBounds();
final int width = (int) r.getWidth();
final int height = (int) r.getHeight();
final int x1 = (int) Math.round(width * 0.5);
final int y1 = (int) Math.round(height * 0.5);
final int x2 = (int) Math.round(width * 0.7);
final int y2 = (int) Math.round(height * 0.7);
final int x3 = (int) Math.round(width * 0.9);
final int y3 = (int) Math.round(height * 0.9);
TestApplication.movePointerTo(x1, y1);
ui.processLine("EV_KEY BTN_LEFT 1");
ui.processLine("EV_SYN");
ui.processLine("EV_REL REL_X " + (x2 - x1));
ui.processLine("EV_REL REL_Y " + (y2 - y1));
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Mouse dragged: %d, %d", x2, y2);
ui.processLine("EV_REL REL_X " + (x3 - x2));
ui.processLine("EV_REL REL_Y " + (y3 - y2));
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Mouse dragged: %d, %d", x3, y3);
TestLogShim.waitForLog("Mouse exited: %d, %d", x3, y3);
ui.processLine("EV_KEY BTN_LEFT 0");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Mouse released: %d, %d", x3, y3);
}
}
