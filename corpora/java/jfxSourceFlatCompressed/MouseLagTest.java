package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
public class MouseLagTest {
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
ui.processLine("PROPERTY ID_INPUT_MOUSE 1");
ui.processLine("CREATE");
}
@After public void destroyDevice() throws Exception {
if (ui != null) {
try {
ui.processLine("DESTROY");
} catch (RuntimeException e) { }
ui.processLine("CLOSE");
ui.dispose();
}
}
@Test
public void testMouseLag() throws Exception {
byte[] moveLeft = new byte[256];
int offset;
offset = ui.writeLine(moveLeft, 0, "EV_REL REL_X -1");
offset = ui.writeLine(moveLeft, offset, "EV_REL REL_Y 0");
int moveLeftLength = ui.writeLine(moveLeft, offset, "EV_SYN");
byte[] moveRight = new byte[256];
offset = ui.writeLine(moveRight, 0, "EV_REL REL_X 1");
offset = ui.writeLine(moveRight, offset, "EV_REL REL_Y 0");
int moveRightLength = ui.writeLine(moveRight, offset, "EV_SYN");
byte[] moveDown = new byte[256];
offset = ui.writeLine(moveDown, 0, "EV_REL REL_X 0");
offset = ui.writeLine(moveDown, offset, "EV_REL REL_Y 1");
int moveDownLength = ui.writeLine(moveDown, offset, "EV_SYN");
long startTime = System.currentTimeMillis();
for (int i = 0; i < 10; i++) {
for (int j = 0; j < 150; j++) {
ui.write(moveLeft, 0, moveLeftLength);
}
ui.write(moveDown, 0, moveDownLength);
for (int j = 0; j < 150; j++) {
ui.write(moveRight, 0, moveRightLength);
}
}
long t = System.currentTimeMillis() - startTime;
Assert.assertTrue("Took " + t + "ms to send 3000 events, of which "
+ TestLogShim.countLogContaining("moved")
+ " were received",
t < 6000l * TestApplication.getTimeScale());
TestLogShim.log("Sent 3000 events in " + t + "ms");
TestLogShim.waitForLog("Mouse moved: 300, 310", 6000l - t);
}
}
