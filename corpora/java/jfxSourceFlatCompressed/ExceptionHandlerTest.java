package test.com.sun.glass.ui;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
public class ExceptionHandlerTest {
private static final CountDownLatch startupLatch = new CountDownLatch(1);
private static volatile Throwable exception;
public static class TestApp extends Application {
@Override
public void start(Stage t) {
Thread.currentThread().setUncaughtExceptionHandler((t2, e) -> {
exception = e;
System.out.println("Exception caught: " + e);
System.out.flush();
});
startupLatch.countDown();
}
}
private class TestException extends RuntimeException {
public TestException(String msg) {
super(msg);
}
}
@BeforeClass
public static void setup() throws Exception {
new Thread(() -> Application.launch(TestApp.class)).start();
startupLatch.await();
}
@Test
public void test1() throws Throwable {
exception = null;
final CountDownLatch l = new CountDownLatch(1);
Platform.runLater(() -> {
throw new TestException("test1");
});
Platform.runLater(l::countDown);
l.await(10000, TimeUnit.MILLISECONDS);
if (exception == null) {
throw new RuntimeException("Test FAILED: TestException is not caught");
}
if (!(exception instanceof TestException)) {
throw new RuntimeException("Test FAILED: unexpected exception is caught: " + exception);
}
}
}
