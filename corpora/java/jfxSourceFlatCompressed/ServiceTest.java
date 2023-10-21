package test.javafx.concurrent;
import com.sun.javafx.PlatformUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import test.javafx.concurrent.mocks.SimpleTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.concurrent.Service;
import javafx.concurrent.Service;
import javafx.concurrent.ServiceShim;
import javafx.concurrent.Task;
import javafx.concurrent.Task;
import javafx.concurrent.TaskShim;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
public class ServiceTest {
private Service<String> service;
@Before public void setup() {
service = new ServiceShim<String>() {
@Override public Task<String> createTask() {
return new SimpleTask();
}
@Override public void checkThread() { }
@Override public void runLater(Runnable r) {
r.run();
}
};
}
@Test public void executorDefaultsToNull() {
assertNull(service.getExecutor());
assertNull(service.executorProperty().get());
}
@Test public void executorCanBeSet() {
final Executor e = command -> { };
service.setExecutor(e);
assertSame(e, service.getExecutor());
assertSame(e, service.executorProperty().get());
}
@Test public void executorCanBeBound() {
final Executor e = command -> { };
ObjectProperty<Executor> other = new SimpleObjectProperty<Executor>(e);
service.executorProperty().bind(other);
assertSame(e, service.getExecutor());
assertSame(e, service.executorProperty().get());
other.set(null);
assertNull(service.getExecutor());
assertNull(service.executorProperty().get());
}
@Test public void executorIsUsed() {
final AtomicBoolean results = new AtomicBoolean(false);
final Executor e = command -> results.set(true);
service.setExecutor(e);
service.start();
assertTrue(results.get());
}
@Test public void stateDefaultsTo_READY() {
assertSame(Worker.State.READY, service.getState());
assertSame(Worker.State.READY, service.stateProperty().get());
}
@Test public void valueDefaultsToNull() {
assertNull(service.getValue());
assertNull(service.valueProperty().get());
}
@Test public void exceptionDefaultsToNull() {
assertNull(service.getException());
assertNull(service.exceptionProperty().get());
}
@Test public void workDoneDefaultsTo_NegativeOne() {
assertEquals(-1, service.getWorkDone(), 0);
assertEquals(-1, service.workDoneProperty().get(), 0);
}
@Test public void totalWorkDefaultsTo_NegativeOne() {
assertEquals(-1, service.getTotalWork(), 0);
assertEquals(-1, service.totalWorkProperty().get(), 0);
}
@Test public void progressDefaultsTo_NegativeOne() {
assertEquals(-1, service.getProgress(), 0);
assertEquals(-1, service.progressProperty().get(), 0);
}
@Test public void runningDefaultsToFalse() {
assertFalse(service.isRunning());
assertFalse(service.runningProperty().get());
}
@Test public void messageDefaultsToEmptyString() {
assertEquals("", service.getMessage());
assertEquals("", service.messageProperty().get());
}
@Test public void titleDefaultsToEmptyString() {
assertEquals("", service.getTitle());
assertEquals("", service.titleProperty().get());
}
@Test(timeout = 2000) public void testManyServicesRunConcurrently() throws Exception {
if (PlatformUtil.isWindows()) {
assumeTrue(Boolean.getBoolean("unstable.test"));
}
final CountDownLatch latch = new CountDownLatch(32);
for (int i=0; i<32; i++) {
Service<Void> s = new ServiceShim<Void>() {
@Override public void checkThread() { }
@Override public void runLater(Runnable r) { r.run(); }
@Override protected Task<Void> createTask() {
return new TaskShim<Void>() {
@Override protected Void call() throws Exception {
Thread.sleep(1000);
latch.countDown();
return null;
}
@Override public void runLater(Runnable r) {
r.run();
}
@Override public boolean isFxApplicationThread() {
return true;
}
};
}
};
s.start();
}
latch.await();
}
}
