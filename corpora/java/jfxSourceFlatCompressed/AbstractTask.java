package test.javafx.concurrent;
import java.util.concurrent.Semaphore;
import javafx.concurrent.Task;
import javafx.concurrent.TaskShim;
public abstract class AbstractTask extends TaskShim<String> {
public final Semaphore scheduledSemaphore = new Semaphore(0);
public final Semaphore runningSemaphore = new Semaphore(0);
public final Semaphore succeededSemaphore = new Semaphore(0);
public final Semaphore cancelledSemaphore = new Semaphore(0);
public final Semaphore failedSemaphore = new Semaphore(0);
Thread appThread;
ServiceTestBase test;
public void simulateSchedule() {
shim_setState(State.SCHEDULED);
}
@Override public boolean isFxApplicationThread() {
return appThread == null || Thread.currentThread() == appThread;
}
@Override public void runLater(Runnable r) {
if (test != null) {
test.eventQueue.add(r);
} else {
r.run();
}
}
@Override protected void scheduled() {
scheduledSemaphore.release();
}
@Override protected void running() {
runningSemaphore.release();
}
@Override protected void succeeded() {
succeededSemaphore.release();
}
@Override protected void cancelled() {
cancelledSemaphore.release();
}
@Override protected void failed() {
failedSemaphore.release();
}
public ServiceTestBase set_test(ServiceTestBase v) {
return test = v;
}
}
