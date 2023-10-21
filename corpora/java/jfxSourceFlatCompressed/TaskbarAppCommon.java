package test.com.sun.javafx.application;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.application.PlatformImplShim;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import junit.framework.AssertionFailedError;
import static org.junit.Assert.*;
import static test.util.Util.TIMEOUT;
public class TaskbarAppCommon {
private final CountDownLatch launchLatch = new CountDownLatch(1);
private void startup() {
new Thread(() -> PlatformImpl.startup(() -> {
assertTrue(Platform.isFxApplicationThread());
launchLatch.countDown();
})).start();
try {
if (!launchLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Platform to start");
}
} catch (InterruptedException ex) {
AssertionFailedError err = new AssertionFailedError("Unexpected exception");
err.initCause(ex);
throw err;
}
assertEquals(0, launchLatch.getCount());
final CountDownLatch exitLatch = PlatformImplShim.test_getPlatformExitLatch();
assertEquals(1, exitLatch.getCount());
}
public void doTestTaskbarAppDefault() {
assertTrue(PlatformImpl.isTaskbarApplication());
}
public void doTestTaskbarAppSetGet() {
PlatformImpl.setTaskbarApplication(false);
assertFalse(PlatformImpl.isTaskbarApplication());
PlatformImpl.setTaskbarApplication(true);
assertTrue(PlatformImpl.isTaskbarApplication());
}
public void doTestTaskbarAppStartDefault() {
assertTrue(PlatformImpl.isTaskbarApplication());
String taskbarAppProp = System.getProperty("glass.taskbarApplication");
assertNull(taskbarAppProp);
startup();
taskbarAppProp = System.getProperty("glass.taskbarApplication");
assertNull(taskbarAppProp);
boolean isTaskbarApp = !"false".equalsIgnoreCase(taskbarAppProp);
assertTrue(isTaskbarApp);
taskbarAppProp = System.getProperty("glass.taskbarApplication", "true");
isTaskbarApp = !"false".equalsIgnoreCase(taskbarAppProp);
assertTrue(isTaskbarApp);
Platform.exit();
}
public void doTestTaskbarAppStartFalse() {
PlatformImpl.setTaskbarApplication(false);
assertFalse(PlatformImpl.isTaskbarApplication());
String taskbarAppProp = System.getProperty("glass.taskbarApplication");
assertNull(taskbarAppProp);
startup();
taskbarAppProp = System.getProperty("glass.taskbarApplication");
assertNotNull(taskbarAppProp);
boolean isTaskbarApp = !"false".equalsIgnoreCase(taskbarAppProp);
assertFalse(isTaskbarApp);
taskbarAppProp = System.getProperty("glass.taskbarApplication", "true");
isTaskbarApp = !"false".equalsIgnoreCase(taskbarAppProp);
assertFalse(isTaskbarApp);
Platform.exit();
}
public void doTestTaskbarAppStartTrue() {
PlatformImpl.setTaskbarApplication(true);
assertTrue(PlatformImpl.isTaskbarApplication());
String taskbarAppProp = System.getProperty("glass.taskbarApplication");
assertNull(taskbarAppProp);
startup();
taskbarAppProp = System.getProperty("glass.taskbarApplication");
assertNull(taskbarAppProp);
boolean isTaskbarApp = !"false".equalsIgnoreCase(taskbarAppProp);
assertTrue(isTaskbarApp);
taskbarAppProp = System.getProperty("glass.taskbarApplication", "true");
isTaskbarApp = !"false".equalsIgnoreCase(taskbarAppProp);
assertTrue(isTaskbarApp);
Platform.exit();
}
}
