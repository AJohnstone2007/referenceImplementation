package test.com.sun.javafx.application;
import com.sun.javafx.application.PlatformImplShim;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import org.junit.Assert;
import org.junit.BeforeClass;
import test.util.Util;
import static org.junit.Assert.assertEquals;
import static test.util.Util.TIMEOUT;
public class PlatformExitCommon {
private static final CountDownLatch startupLatch = new CountDownLatch(1);
private final CountDownLatch exitLatch = PlatformImplShim.test_getPlatformExitLatch();
private static final int DELAY = 200;
@BeforeClass
public static void initFX() throws Exception {
Platform.startup(startupLatch::countDown);
Assert.assertTrue("Timeout waiting for FX runtime to start",
startupLatch.await(TIMEOUT, TimeUnit.MILLISECONDS));
}
protected void doTestPlatformExit(boolean again) {
Util.sleep(DELAY);
assertEquals(1, exitLatch.getCount());
Platform.exit();
Util.sleep(DELAY);
assertEquals(0, exitLatch.getCount());
if (again) {
Platform.exit();
assertEquals(0, exitLatch.getCount());
}
}
protected void doTestPlatformExitOnAppThread(boolean again) {
Util.sleep(DELAY);
assertEquals(1, exitLatch.getCount());
Util.runAndWait(Platform::exit);
assertEquals(0, exitLatch.getCount());
if (again) {
Platform.exit();
assertEquals(0, exitLatch.getCount());
}
}
}
