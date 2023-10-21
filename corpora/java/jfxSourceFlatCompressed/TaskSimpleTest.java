package test.javafx.concurrent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import test.javafx.concurrent.mocks.SimpleTask;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TaskSimpleTest {
private Task<String> task;
@Before public void setup() {
task = new SimpleTask();
task.runningProperty().addListener((o, oldValue, newValue) -> {
Worker.State s = task.getState();
if (newValue) {
assertTrue(s == Worker.State.SCHEDULED || s == Worker.State.RUNNING);
} else {
assertTrue(s != Worker.State.SCHEDULED && s != Worker.State.RUNNING);
}
});
}
@Test public void stateShouldBe_READY_ByDefault() {
assertEquals(Task.State.READY, task.getState());
}
@Test public void workDoneShouldBe_Indeterminate_ByDefault() {
assertEquals(-1, task.getWorkDone(), 0);
}
@Test public void totalWorkShouldBe_Indeterminate_ByDefault() {
assertEquals(-1, task.getTotalWork(), 0);
}
@Test public void progressShouldBe_Indeterminate_ByDefault() {
assertEquals(-1, task.getWorkDone(), 0);
}
@Test public void valueShouldBe_Null_ByDefault() {
assertNull(task.getValue());
}
@Test public void exceptionShouldBe_Null_ByDefault() {
assertNull(task.getException());
}
@Test public void runningShouldBe_False_ByDefault() {
assertEquals(false, task.isRunning());
}
@Test public void messageShouldBe_EmptyString_ByDefault() {
assertEquals("", task.getMessage());
}
@Test public void titleShouldBe_EmptyString_ByDefault() {
assertEquals("", task.getTitle());
}
@Test public void isCancelledShouldBe_False_ByDefault() {
assertEquals(false, task.isCancelled());
}
@Test public void isDoneShouldBe_False_ByDefault() {
assertEquals(false, task.isDone());
}
@Test public void afterRunningStatesShouldHaveBeen_SCHEDULED_RUNNING_SUCCEEDED() {
final List<Worker.State> states = new ArrayList<Worker.State>();
task.stateProperty().addListener((observable, oldValue, newValue) -> {
states.add(newValue);
});
task.run();
assertArrayEquals(states.toArray(), new Worker.State[]{
Worker.State.SCHEDULED,
Worker.State.RUNNING,
Worker.State.SUCCEEDED
});
}
@Test public void afterRunningWorkDoneShouldBe_Indeterminate() {
task.run();
assertEquals(-1, task.getWorkDone(), 0);
}
@Test public void afterRunningTotalWorkShouldBe_Indeterminate() {
task.run();
assertEquals(-1, task.getTotalWork(), 0);
}
@Test public void afterRunningProgressShouldBe_Indeterminate() {
task.run();
assertEquals(-1, task.getWorkDone(), 0);
}
@Test public void afterRunningValueShouldBe_Finished() {
task.run();
assertEquals("Sentinel", task.getValue());
}
@Test public void afterRunningExceptionShouldBe_Null() {
task.run();
assertNull(task.getException());
}
@Test public void afterRunningMessageShouldBe_EmptyString() {
task.run();
assertEquals("", task.getMessage());
}
@Test public void afterRunningTitleShouldBe_EmptyString() {
task.run();
assertEquals("", task.getTitle());
}
@Test public void afterRunning_isCancelled_ShouldBe_False() {
task.run();
assertEquals(false, task.isCancelled());
}
@Test public void afterRunning_isDone_ShouldBe_True() {
task.run();
assertEquals(true, task.isDone());
}
}
