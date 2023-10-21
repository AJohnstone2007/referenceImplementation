package test.javafx.concurrent;
import javafx.concurrent.Task;
import test.javafx.concurrent.mocks.EpicFailTask;
import test.javafx.concurrent.mocks.InfiniteTask;
import test.javafx.concurrent.mocks.RunAwayTask;
import test.javafx.concurrent.mocks.SimpleTask;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TaskCancelTest {
private InfiniteTask task;
@Before public void setup() {
task = new InfiniteTask();
}
@Test public void cancellingA_READY_TaskShouldChangeStateTo_CANCELLED() {
assertTrue(task.cancel());
assertEquals(Task.State.CANCELLED, task.getState());
assertTrue(task.isDone());
}
@Test public void cancellingA_SCHEDULED_TaskShouldChangeStateTo_CANCELLED() {
task.simulateSchedule();
assertTrue(task.cancel());
assertEquals(Task.State.CANCELLED, task.getState());
assertTrue(task.isDone());
}
@Test public void cancellingA_RUNNING_TaskShouldChangeStateTo_CANCELLED() throws Exception {
Thread th = new Thread(task);
th.start();
task.runningSemaphore.acquire();
assertTrue(task.cancel());
th.join();
assertEquals(Task.State.CANCELLED, task.getState());
assertNull(task.getValue());
assertTrue(task.isDone());
}
@Test public void cancellingA_SUCCEEDED_TaskShouldNotChangeTo_CANCELLED() {
Task t = new SimpleTask();
t.run();
assertFalse(t.cancel());
assertEquals(Task.State.SUCCEEDED, t.getState());
assertTrue(t.isDone());
}
@Test public void cancellingA_FAILED_TaskShouldNotChangeTo_CANCELLED() {
Task t = new EpicFailTask();
t.run();
assertFalse(t.cancel());
assertEquals(Task.State.FAILED, t.getState());
assertTrue(t.isDone());
}
@Test public void aFreeRunningCancelledTaskReturnValueShouldBeIgnored() throws Exception {
RunAwayTask runAway = new RunAwayTask() {
protected void loop(int count) throws Exception {
}
};
Thread th = new Thread(runAway);
th.start();
runAway.runningSemaphore.acquire();
assertTrue(runAway.cancel());
runAway.stopLooping.set(true);
th.join();
assertEquals(Task.State.CANCELLED, runAway.getState());
assertNull(runAway.getValue());
assertTrue(runAway.isDone());
}
}
