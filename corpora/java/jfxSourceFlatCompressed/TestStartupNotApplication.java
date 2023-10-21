package test.launchertest;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import javafx.application.Platform;
import static test.launchertest.Constants.*;
public class TestStartupNotApplication {
private static void assertEquals(String expected, String actual) {
if (expected == null && actual == null) return;
if (expected != null && expected.equals(actual)) return;
System.err.println("Assertion failed: expected (" + expected + ") != actual (" + actual + ")");
System.exit(ERROR_ASSERTION_FAILURE);
}
public static void main(String[] args) {
try {
Platform.runLater(() -> {
});
System.exit(ERROR_TOOLKIT_IS_RUNNING);
} catch (IllegalStateException ex) {
} catch (RuntimeException ex) {
ex.printStackTrace();
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
final Semaphore sem = new Semaphore(0);
final ArrayList<String> list = new ArrayList<>();
final String keyStartup = "Startup runnable";
final String keyRunLater0 = "runLater #0";
final String keyRunLater1 = "runLater #1";
try {
Platform.startup(() -> {
list.add(keyStartup);
sem.release();
});
Platform.runLater(() -> {
list.add(keyRunLater0);
sem.release();
});
sem.acquire(2);
} catch (IllegalStateException ex) {
ex.printStackTrace();
System.exit(ERROR_STARTUP_FAILED);
} catch (InterruptedException ex) {
ex.printStackTrace();
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
Platform.runLater(() -> {
list.add(keyRunLater1);
sem.release();
});
try {
sem.acquire();
} catch (InterruptedException ex) {
ex.printStackTrace();
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
assertEquals(keyStartup, list.get(0));
assertEquals(keyRunLater0, list.get(1));
assertEquals(keyRunLater1, list.get(2));
System.exit(ERROR_NONE);
}
}
