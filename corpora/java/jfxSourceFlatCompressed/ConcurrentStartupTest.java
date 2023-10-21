package test.com.sun.javafx.application;
import javafx.application.Platform;
import org.junit.AfterClass;
import org.junit.Test;
import test.util.Util;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
public class ConcurrentStartupTest {
CountDownLatch startupLatch;
CountDownLatch mainLatch;
volatile Throwable error = null;
@Test (timeout=15000)
public void testStartupReturnBeforeRunnableComplete() throws Exception {
startupLatch = new CountDownLatch(2);
mainLatch = new CountDownLatch(1);
Platform.startup(() -> {
try {
if (!mainLatch.await(10, TimeUnit.SECONDS)) {
error = new AssertionError("Timeout waiting for main latch");
}
try {
assertEquals("Runnable executed out of order", 2, startupLatch.getCount());
} catch (Throwable err) {
error = err;
}
} catch (InterruptedException ex) {
error = ex;
}
startupLatch.countDown();
});
Platform.runLater(() -> {
try {
assertEquals("Runnable executed out of order", 1, startupLatch.getCount());
} catch (Throwable err) {
error = err;
}
startupLatch.countDown();
});
mainLatch.countDown();
assertTrue(startupLatch.await(10, TimeUnit.SECONDS));
if (error != null) {
if (error instanceof Error) {
throw (Error) error;
} else {
throw (Exception) error;
}
}
}
@AfterClass
public static void teardown() {
Platform.exit();
}
}
