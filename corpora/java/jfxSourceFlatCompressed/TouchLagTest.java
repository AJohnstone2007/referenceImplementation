package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
public class TouchLagTest {
private UInput ui;
@Rule public TestName name = new TestName();
@Before public void setUpScreen() throws Exception {
TestLogShim.reset();
TestLogShim.log(name.getMethodName());
TestApplication.showFullScreenScene();
TestApplication.addTouchListeners();
TestApplication.addMouseListeners();
initDevice();
}
public void initDevice() throws Exception {
ui = new UInput();
Rectangle2D r = Screen.getPrimary().getBounds();
final int width = (int) r.getWidth();
final int height = (int) r.getHeight();
ui.processLine("OPEN");
ui.processLine("EVBIT EV_SYN");
ui.processLine("EVBIT EV_KEY");
ui.processLine("KEYBIT BTN_TOUCH");
ui.processLine("EVBIT EV_ABS");
ui.processLine("ABSBIT ABS_X");
ui.processLine("ABSBIT ABS_Y");
ui.processLine("ABSBIT ABS_MT_POSITION_X");
ui.processLine("ABSBIT ABS_MT_POSITION_Y");
ui.processLine("ABSBIT ABS_MT_ORIENTATION");
ui.processLine("ABSBIT ABS_MT_TOUCH_MAJOR");
ui.processLine("ABSBIT ABS_MT_TOUCH_MINOR");
ui.processLine("ABSMIN ABS_X 0");
ui.processLine("ABSMAX ABS_X " + width);
ui.processLine("ABSMIN ABS_Y 0");
ui.processLine("ABSMAX ABS_Y " + height);
ui.processLine("ABSMIN ABS_MT_POSITION_X 0");
ui.processLine("ABSMAX ABS_MT_POSITION_X " + width);
ui.processLine("ABSMIN ABS_MT_POSITION_Y 0");
ui.processLine("ABSMAX ABS_MT_POSITION_Y " + height);
ui.processLine("ABSMIN ABS_MT_ORIENTATION 0");
ui.processLine("ABSMAX ABS_MT_ORIENTATION 1");
ui.processLine("PROPBIT INPUT_PROP_POINTER");
ui.processLine("PROPBIT INPUT_PROP_DIRECT");
ui.processLine("PROPERTY ID_INPUT_TOUCHSCREEN 1");
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
public void testTouchLag() throws Exception {
TestLogShim.reset();
ui.processLine("EV_ABS ABS_X 300");
ui.processLine("EV_ABS ABS_Y 300");
ui.processLine("EV_KEY BTN_TOUCH 1");
ui.processLine("EV_ABS ABS_MT_POSITION_X 300");
ui.processLine("EV_ABS ABS_MT_POSITION_Y 300");
ui.processLine("EV_SYN SYN_MT_REPORT 0");
ui.processLine("EV_SYN SYN_REPORT 0");
TestLogShim.waitForLogContaining("TouchPoint: PRESSED", 3000l);
byte[] b = new byte[1024];
int offset = 0;
int[] xs = new int[2];
int[] ys = new int[2];
offset = ui.writeLine(b, offset, "EV_ABS ABS_X 0");
xs[0] = offset - 4;
offset = ui.writeLine(b, offset, "EV_ABS ABS_Y 0");
ys[0] = offset - 4;
offset = ui.writeLine(b, offset, "EV_ABS ABS_MT_POSITION_X 0");
xs[1] = offset - 4;
offset = ui.writeLine(b, offset, "EV_ABS ABS_MT_POSITION_Y 0");
ys[1] = offset - 4;
offset = ui.writeLine(b, offset, "EV_SYN SYN_MT_REPORT 0");
offset = ui.writeLine(b, offset, "EV_SYN SYN_REPORT 0");
int moveLength = offset;
long startTime = System.currentTimeMillis();
for (int y = 300; y < 310; y++) {
for (int x = 300; x > 150; x--) {
ui.writeValue(b, xs[0], x);
ui.writeValue(b, xs[1], x);
ui.writeValue(b, ys[0], y);
ui.writeValue(b, ys[1], y);
ui.write(b, 0, moveLength);
}
for (int x = 150; x < 300; x++) {
ui.writeValue(b, xs[0], x);
ui.writeValue(b, xs[1], x);
ui.writeValue(b, ys[0], y);
ui.writeValue(b, ys[1], y);
ui.write(b, 0, moveLength);
}
}
long t = System.currentTimeMillis() - startTime;
Assert.assertTrue("Took " + t + "ms to send 3000 events",
t < (long) (3000l * TestApplication.getTimeScale()));
TestLogShim.log("Sent 3000 events in " + t + "ms");
ui.writeValue(b, xs[0], 400);
ui.writeValue(b, xs[1], 400);
ui.writeValue(b, ys[0], 410);
ui.writeValue(b, ys[1], 410);
ui.write(b, 0, moveLength);
ui.processLine("EV_KEY BTN_TOUCH 0");
ui.processLine("EV_SYN SYN_MT_REPORT 0");
ui.processLine("EV_SYN SYN_REPORT 0");
TestLogShim.waitForLog("Touch moved: 400, 410", 3000l - t);
}
@Test
public void testMultitouchLag() throws Exception {
TestLogShim.reset();
ui.processLine("EV_ABS ABS_X 300");
ui.processLine("EV_ABS ABS_Y 300");
ui.processLine("EV_KEY BTN_TOUCH 1");
ui.processLine("EV_ABS ABS_MT_POSITION_X 300");
ui.processLine("EV_ABS ABS_MT_POSITION_Y 300");
ui.processLine("EV_SYN SYN_MT_REPORT 0");
ui.processLine("EV_SYN SYN_REPORT 0");
TestLogShim.waitForLogContaining("TouchPoint: PRESSED", 3000);
byte[] b = new byte[1024];
int offset = 0;
int baseX, baseY;
int[] xs = new int[2];
int[] ys = new int[2];
offset = ui.writeLine(b, offset, "EV_ABS ABS_X 0");
baseX = offset - 4;
offset = ui.writeLine(b, offset, "EV_ABS ABS_Y 0");
baseY = offset - 4;
offset = ui.writeLine(b, offset, "EV_ABS ABS_MT_POSITION_X 0");
xs[0] = offset - 4;
offset = ui.writeLine(b, offset, "EV_ABS ABS_MT_POSITION_Y 0");
ys[0] = offset - 4;
offset = ui.writeLine(b, offset, "EV_SYN SYN_MT_REPORT 0");
offset = ui.writeLine(b, offset, "EV_ABS ABS_MT_POSITION_X 0");
xs[1] = offset - 4;
offset = ui.writeLine(b, offset, "EV_ABS ABS_MT_POSITION_Y 0");
ys[1] = offset - 4;
offset = ui.writeLine(b, offset, "EV_SYN SYN_MT_REPORT 0");
offset = ui.writeLine(b, offset, "EV_SYN SYN_REPORT 0");
int moveLength = offset;
long startTime = System.currentTimeMillis();
for (int y = 300; y < 310; y++) {
for (int x = 300; x > 150; x--) {
ui.writeValue(b, baseX, x);
ui.writeValue(b, baseY, y);
ui.writeValue(b, xs[0], x);
ui.writeValue(b, xs[1], (x * 3) / 2);
ui.writeValue(b, ys[0], y);
ui.writeValue(b, ys[1], (y * 2) / 3);
ui.write(b, 0, moveLength);
}
for (int x = 150; x < 300; x++) {
ui.writeValue(b, baseX, x);
ui.writeValue(b, baseY, y);
ui.writeValue(b, xs[0], x);
ui.writeValue(b, xs[1], (x * 3) / 2);
ui.writeValue(b, ys[0], y);
ui.writeValue(b, ys[1], (y * 2) / 3);
ui.write(b, 0, moveLength);
}
}
long t = System.currentTimeMillis() - startTime;
Assert.assertTrue("Took " + t + "ms to send 3000 events",
t < (long) (3000l * TestApplication.getTimeScale()));
TestLogShim.log("Sent 3000 events in " + t + "ms");
ui.writeValue(b, baseX, 400);
ui.writeValue(b, baseY, 410);
ui.writeValue(b, xs[0], 400);
ui.writeValue(b, ys[0], 410);
ui.writeValue(b, xs[1], 350);
ui.writeValue(b, ys[1], 360);
ui.write(b, 0, moveLength);
ui.processLine("EV_ABS ABS_X 400");
ui.processLine("EV_ABS ABS_Y 410");
ui.processLine("EV_ABS ABS_MT_POSITION_X 400");
ui.processLine("EV_ABS ABS_MT_POSITION_Y 410");
ui.processLine("EV_SYN SYN_MT_REPORT 0");
ui.processLine("EV_SYN SYN_REPORT 0");
ui.processLine("EV_ABS ABS_X 400");
ui.processLine("EV_ABS ABS_Y 410");
ui.processLine("EV_KEY BTN_TOUCH 0");
ui.processLine("EV_SYN SYN_MT_REPORT 0");
ui.processLine("EV_SYN SYN_REPORT 0");
TestLogShim.waitForLog("Touch released: 400, 410", 3000l - t);
}
}
