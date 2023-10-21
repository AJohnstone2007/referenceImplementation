package test.javafx.concurrent;
import java.util.Arrays;
import java.util.Collection;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import test.javafx.concurrent.mocks.EpicFailTask;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class TaskExceptionTest {
@Parameterized.Parameters public static Collection implementations() {
return Arrays.asList(new Object[][]{
{new Exception("Exception")},
{new IllegalArgumentException("IAE")},
{new NullPointerException("NPE")},
{new RuntimeException("RuntimeException")}
});
}
private Exception exception;
private Task task;
public TaskExceptionTest(Exception th) {
this.exception = th;
}
@Before public void setup() {
task = new EpicFailTask(exception);
}
@Test public void afterRunningExceptionShouldBeSet() {
task.run();
assertNotNull(task.getException());
}
@Test public void afterRunningValueShouldBe_Null() {
task.run();
assertNull(task.getValue());
}
@Test public void afterRunningWorkDoneShouldBe_10() {
task.run();
assertEquals(10, task.getWorkDone(), 0);
}
@Test public void afterRunningTotalWorkShouldBe_20() {
task.run();
assertEquals(20, task.getTotalWork(), 0);
}
@Test public void afterRunningProgressShouldBe_FiftyPercent() {
task.run();
assertEquals(.5, task.getProgress(), 0);
}
@Test public void afterRunningStateShouldBe_FAILED() {
task.run();
assertEquals(Worker.State.FAILED, task.getState());
}
}
