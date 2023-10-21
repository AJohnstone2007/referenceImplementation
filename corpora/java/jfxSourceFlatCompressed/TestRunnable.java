package test.com.sun.glass.ui.monocle;
import javafx.application.Platform;
import java.util.concurrent.Semaphore;
public abstract class TestRunnable implements Runnable {
private Throwable t;
private final Semaphore done = new Semaphore(1);
public abstract void test() throws Exception;
public final void run() {
t = null;
try {
test();
} catch (Throwable x) {
t = x;
}
done.release();
}
public final void invokeAndWait() throws Exception {
if (Platform.isFxApplicationThread()) {
test();
} else {
done.acquire();
Platform.runLater(this);
done.acquire();
done.release();
rethrow(t);
}
}
private void rethrow(Throwable t) throws Exception {
if (t != null) {
try {
throw t;
} catch (RuntimeException re) {
throw re;
} catch (Error e) {
throw e;
} catch (Throwable x) {
throw (RuntimeException) new RuntimeException().initCause(x);
}
}
}
public final void invokeAndWaitUntilSuccess(long timeout) throws Exception {
long startTime = System.currentTimeMillis();
long endTime = startTime + timeout;
boolean passed = false;
do {
try {
invokeAndWait();
passed = true;
} catch (Throwable pendingThrowable) {
Thread.sleep(100);
}
} while (System.currentTimeMillis() < endTime && !passed);
rethrow(t);
}
public static void invokeAndWaitUntilSuccess(Testable t, long timeout) throws Exception {
new TestRunnable() {
public void test() throws Exception {
t.test();
}
}.invokeAndWaitUntilSuccess(timeout);
}
public static void invokeAndWait(Testable t) throws Exception {
new TestRunnable() {
public void test() throws Exception {
t.test();
}
}.invokeAndWait();
}
public static interface Testable {
public void test() throws Exception;
}
}
