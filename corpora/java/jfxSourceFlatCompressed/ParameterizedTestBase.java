package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevice;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.TestName;
import org.junit.rules.TestWatchman;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.model.FrameworkMethod;
@RunWith(Parameterized.class)
public abstract class ParameterizedTestBase {
protected final TestTouchDevice device;
protected final Rectangle2D stageBounds;
private Throwable exception;
protected double width;
protected double height;
@Rule
public TestWatchman monitor = new TestWatchman() {
@Override
public void failed(Throwable e, FrameworkMethod method) {
if (!(e instanceof AssumptionViolatedException)) {
System.err.format("Failed %s.%s[%s]\n",
method.getMethod().getDeclaringClass().getName(),
method.getName(),
device);
}
}
};
@Rule public TestName name = new TestName();
public ParameterizedTestBase(TestTouchDevice device, Rectangle2D stageBounds) {
this.device = device;
this.stageBounds = stageBounds;
}
public ParameterizedTestBase(TestTouchDevice device) {
this(device, null);
}
@Before
public void createDevice() throws Exception {
TestApplication.showScene(stageBounds);
TestLogShim.log("Starting " + name.getMethodName() + "[" + device + "]");
Rectangle2D r = TestApplication.getScreenBounds();
width = r.getWidth();
height = r.getHeight();
TestLogShim.reset();
device.create();
TestApplication.addMouseListeners();
TestApplication.addTouchListeners();
TestApplication.addGestureListeners();
TestLogShim.reset();
Platform.runLater(
() -> Thread.currentThread().setUncaughtExceptionHandler(
(t, e) -> exception = e));
}
@After
public void destroyDevice() throws Throwable {
if (device != null) {
device.destroy();
}
TestApplication.waitForNextPulse();
if (exception != null) {
RuntimeException rte = new RuntimeException("Uncaught exception");
rte.setStackTrace(new StackTraceElement[0]);
rte.initCause(exception);
throw rte;
}
}
}
