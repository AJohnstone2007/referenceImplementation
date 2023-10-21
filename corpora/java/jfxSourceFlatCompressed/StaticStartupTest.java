package test.com.sun.javafx.application;
import javafx.application.Platform;
import junit.framework.AssertionFailedError;
import org.junit.AfterClass;
import org.junit.Test;
import test.util.Util;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
public class StaticStartupTest {
@Test (timeout=15000)
public void testStartupFromClinit() throws Exception {
Thread thr = new Thread(() -> {
try {
Thread.sleep(20000);
} catch (InterruptedException ex) {}
System.err.println("Test timeout exceeded -- calling System.exit");
System.exit(1);
});
thr.setDaemon(true);
thr.start();
StaticClass.doSomething();
}
@AfterClass
public static void teardown() {
Platform.exit();
}
}
class StaticClass {
static CountDownLatch staticLatch = new CountDownLatch(1);
static Throwable err = null;
static {
Platform.startup(() -> {
staticLatch.countDown();
});
}
static void doSomething() {
Platform.runLater(() -> {
try {
assertEquals(staticLatch.getCount(), 0);
} catch (Throwable th) {
throw new AssertionFailedError ("Static latch couldn't be read");
}
});
}
}
