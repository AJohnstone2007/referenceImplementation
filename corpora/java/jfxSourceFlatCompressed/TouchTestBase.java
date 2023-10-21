package test.robot.com.sun.glass.ui.monocle;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import org.junit.After;
import org.junit.Before;
import java.io.OutputStream;
import java.io.PrintStream;
import test.com.sun.glass.ui.monocle.TestRunnable;
public class TouchTestBase {
protected UInput ui;
private static final PrintStream systemErr = System.err;
private SystemErrFilter systemErrFilter;
protected Rectangle2D screen;
private double absXMax, absYMax;
protected static final double UNDEFINED = Double.MAX_VALUE / Math.PI;
static class SystemErrFilter extends PrintStream {
private boolean foundException = false;
public SystemErrFilter(OutputStream out) {
super(out);
}
@Override
public synchronized void print(String s) {
System.out.flush();
if (s.indexOf("Exception") >= 0) {
foundException = true;
}
super.print(s);
}
void checkException() throws InterruptedException {
TestApplication.waitForNextPulse();
synchronized (this) {
if (!foundException) {
return;
}
}
throw new AssertionError("Found exception");
}
}
protected void setAbsScale(int absXMax, int absYMax) {
this.absXMax = (double) absXMax;
this.absYMax = (double) absYMax;
}
@Before
public void initDevice() throws Exception {
TestApplication.getStage();
ui = new UInput();
systemErrFilter = new SystemErrFilter(System.err);
System.setErr(systemErrFilter);
TestRunnable.invokeAndWait(() -> screen = Screen.getPrimary().getBounds());
}
@After
public void destroyDevice() throws InterruptedException {
try {
ui.waitForQuiet();
} catch (InterruptedException e) {
}
try {
ui.processLine("DESTROY");
} catch (RuntimeException e) {
}
try {
ui.processLine("CLOSE");
} catch (RuntimeException e) {
}
ui.dispose();
System.setErr(systemErr);
if (systemErrFilter != null) {
systemErrFilter.checkException();
}
}
protected void absMTPosition(double x, double y) {
if (x != UNDEFINED) {
ui.processLine("EV_ABS ABS_MT_POSITION_X "
+ Math.round(x * absXMax / screen.getWidth()));
}
if (y != UNDEFINED) {
ui.processLine("EV_ABS ABS_MT_POSITION_Y "
+ Math.round(y * absYMax / screen.getHeight()));
}
}
protected void absPosition(double x, double y) {
if (x != UNDEFINED) {
ui.processLine("EV_ABS ABS_X "
+ Math.round(x * absXMax / screen.getWidth()));
}
if (y != UNDEFINED) {
ui.processLine("EV_ABS ABS_Y "
+ Math.round(y * absYMax / screen.getHeight()));
}
}
}
