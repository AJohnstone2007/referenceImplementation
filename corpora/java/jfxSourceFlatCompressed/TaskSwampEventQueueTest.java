package test.javafx.concurrent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import javafx.concurrent.Task;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
public class TaskSwampEventQueueTest {
private CyclicBarrier barrier;
private List<Runnable> eventQueue;
private Task task;
private Thread th;
@Before public void setup() {
barrier = new CyclicBarrier(2);
eventQueue = new ArrayList<>();
task = new AbstractTask() {
@Override protected String call() throws Exception {
for (int i=0; i<1000; i++) {
updateProgress(i, 2000);
}
barrier.await();
barrier.await();
for (int i=1000; i<=2000; i++) {
updateProgress(i, 2000);
}
barrier.await();
return "Sentinel";
}
@Override public boolean isFxApplicationThread() {
return Thread.currentThread() != th;
}
@Override public void runLater(Runnable r) {
eventQueue.add(r);
}
};
}
@Test public void numberOfEventsOnTheEventQueueShouldNeverBeLarge() throws Exception {
assumeTrue(Boolean.getBoolean("unstable.test"));
th = new Thread(task);
th.start();
barrier.await();
assertTrue(eventQueue.size() == 2 || eventQueue.size() == 1);
while (eventQueue.size() > 0) eventQueue.remove(0).run();
assertEquals(1000 - 1, task.getWorkDone(), 0);
assertEquals(2000, task.getTotalWork(), 0);
barrier.await();
barrier.await();
assertTrue(eventQueue.size() == 2 || eventQueue.size() == 1);
while (eventQueue.size() > 0) eventQueue.remove(0).run();
assertEquals(2000, task.getWorkDone(), 0);
assertEquals(2000, task.getTotalWork(), 0);
}
}
