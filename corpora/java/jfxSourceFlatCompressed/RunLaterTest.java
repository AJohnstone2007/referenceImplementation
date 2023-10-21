package test.com.sun.javafx.application;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import junit.framework.AssertionFailedError;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import test.util.Util;
import static org.junit.Assert.*;
import static test.util.Util.TIMEOUT;
public class RunLaterTest {
private static final CountDownLatch launchLatch = new CountDownLatch(1);
public static class MyApp extends Application {
@Override public void start(Stage primaryStage) throws Exception {
Platform.setImplicitExit(false);
launchLatch.countDown();
}
}
@BeforeClass
public static void setupOnce() {
new Thread(() -> Application.launch(MyApp.class, (String[])null)).start();
try {
if (!launchLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Application to launch");
}
} catch (InterruptedException ex) {
AssertionFailedError err = new AssertionFailedError("Unexpected exception");
err.initCause(ex);
throw err;
}
}
@AfterClass
public static void teardownOnce() {
Platform.exit();
}
private AtomicInteger seqNum = new AtomicInteger(0);
protected void doTestRunLater(final int numRunnables) {
final boolean DELAY = true;
Runnable[] runnables = new Runnable[numRunnables];
for (int i = 0; i < numRunnables; i++) {
final int idx = i;
runnables[idx] = () -> {
if (idx == 0) {
Util.sleep(100);
}
int seq = seqNum.getAndIncrement();
assertEquals(idx, seq);
};
}
Util.runAndWait(DELAY, runnables);
assertEquals(numRunnables, seqNum.get());
}
@Test
public void testRunLater1() {
doTestRunLater(1);
}
@Test
public void testRunLater2() {
doTestRunLater(2);
}
@Test
public void testRunLater10() {
doTestRunLater(10);
}
@Test
public void testRunLater100() {
doTestRunLater(100);
}
@Test
public void testRunLater1000() {
doTestRunLater(1000);
}
@Test
public void testRunLater10000() {
doTestRunLater(10000);
}
@Test
public void testRunLater15000() {
doTestRunLater(15000);
}
@Test
public void testRunLater20000() {
doTestRunLater(20000);
}
}
