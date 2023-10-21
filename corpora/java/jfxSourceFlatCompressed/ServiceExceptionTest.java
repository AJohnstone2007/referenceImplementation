package test.javafx.concurrent;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import test.javafx.concurrent.mocks.EpicFailTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class ServiceExceptionTest extends ServiceTestBase {
@Parameterized.Parameters public static Collection implementations() {
return Arrays.asList(new Object[][]{
{ new Exception("Exception") },
{ new IllegalArgumentException("IAE") },
{ new NullPointerException("NPE") },
{ new RuntimeException("RuntimeException") }
});
}
private Exception exception;
public ServiceExceptionTest(Exception th) {
this.exception = th;
}
@Override protected TestServiceFactory setupServiceFactory() {
return new TestServiceFactory() {
@Override public AbstractTask createTestTask() {
return new EpicFailTask(ServiceExceptionTest.this.exception);
}
};
}
@Test public void exceptionShouldBeSet() {
service.start();
handleEvents();
assertSame(exception, service.getException());
assertSame(exception, service.exceptionProperty().get());
}
@Test public void exceptionPropertyNotification() {
final AtomicBoolean passed = new AtomicBoolean(false);
service.exceptionProperty().addListener((o, oldValue, newValue) -> passed.set(newValue == exception));
service.start();
handleEvents();
assertTrue(passed.get());
}
@Test public void valueShouldBeNull() {
service.start();
handleEvents();
assertNull(service.getValue());
assertNull(service.valueProperty().get());
}
@Test public void runningShouldBeFalse() {
service.start();
handleEvents();
assertFalse(service.isRunning());
assertFalse(service.runningProperty().get());
}
@Test public void runningPropertyNotification() {
final AtomicBoolean passed = new AtomicBoolean(false);
service.runningProperty().addListener((o, oldValue, newValue) -> passed.set(!newValue));
service.start();
handleEvents();
assertTrue(passed.get());
}
@Test public void workDoneShouldBeTen() {
service.start();
handleEvents();
assertEquals(10, service.getWorkDone(), 0);
assertEquals(10, service.workDoneProperty().get(), 0);
}
@Test public void workDonePropertyNotification() {
final AtomicBoolean passed = new AtomicBoolean(false);
service.workDoneProperty().addListener((observable, oldValue, newValue) -> passed.set(newValue.doubleValue() == 10));
service.start();
handleEvents();
assertTrue(passed.get());
}
@Test public void totalWorkShouldBeTwenty() {
service.start();
handleEvents();
assertEquals(20, service.getTotalWork(), 0);
assertEquals(20, service.totalWorkProperty().get(), 0);
}
@Test public void totalWorkPropertyNotification() {
final AtomicBoolean passed = new AtomicBoolean(false);
service.totalWorkProperty().addListener((observable, oldValue, newValue) -> passed.set(newValue.doubleValue() == 20));
service.start();
handleEvents();
assertTrue(passed.get());
}
@Test public void afterRunningProgressShouldBe_FiftyPercent() {
service.start();
handleEvents();
assertEquals(.5, service.getProgress(), 0);
assertEquals(.5, service.progressProperty().get(), 0);
}
@Test public void progressPropertyNotification() {
final AtomicBoolean passed = new AtomicBoolean(false);
service.progressProperty().addListener((observable, oldValue, newValue) -> passed.set(newValue.doubleValue() == .5));
service.start();
handleEvents();
assertTrue(passed.get());
}
@Test public void stateShouldBe_FAILED() {
service.start();
handleEvents();
assertSame(Worker.State.FAILED, service.getState());
assertSame(Worker.State.FAILED, service.stateProperty().get());
}
@Test public void statePropertyNotification() {
final AtomicBoolean passed = new AtomicBoolean(false);
service.stateProperty().addListener((observable, oldValue, newValue) -> passed.set(newValue == Worker.State.FAILED));
service.start();
handleEvents();
assertTrue(passed.get());
}
@Test public void messageShouldBeLastSetValue() {
service.start();
handleEvents();
assertEquals("About to fail", service.getMessage());
assertEquals("About to fail", service.messageProperty().get());
}
@Test public void messagePropertyNotification() {
final AtomicBoolean passed = new AtomicBoolean(false);
service.messageProperty().addListener((observable, oldValue, newValue) -> passed.set("About to fail".equals(service.getMessage())));
service.start();
handleEvents();
assertTrue(passed.get());
}
@Test public void titleShouldBeLastSetValue() {
service.start();
handleEvents();
assertEquals("Epic Fail", service.getTitle());
assertEquals("Epic Fail", service.titleProperty().get());
}
@Test public void titlePropertyNotification() {
final AtomicBoolean passed = new AtomicBoolean(false);
service.titleProperty().addListener((observable, oldValue, newValue) -> passed.set("Epic Fail".equals(service.getTitle())));
service.start();
handleEvents();
assertTrue(passed.get());
}
}
