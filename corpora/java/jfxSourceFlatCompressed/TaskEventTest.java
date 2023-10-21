package test.javafx.concurrent;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.concurrent.Task;
import javafx.concurrent.TaskShim;
import javafx.concurrent.WorkerStateEvent;
import test.javafx.concurrent.mocks.EpicFailTask;
import test.javafx.concurrent.mocks.InfiniteTask;
import test.javafx.concurrent.mocks.MythicalEvent;
import test.javafx.concurrent.mocks.SimpleTask;
import javafx.event.EventHandler;
import javafx.event.EventType;
import org.junit.Test;
import static org.junit.Assert.*;
public class TaskEventTest {
@Test public void onScheduledPropertyNameShouldMatchMethodName() {
Task task = new SimpleTask();
assertEquals("onScheduled", task.onScheduledProperty().getName());
}
@Test public void onScheduledBeanShouldMatchTask() {
Task task = new SimpleTask();
assertSame(task, task.onScheduledProperty().getBean());
}
@Test public void onScheduledIsInitializedToNull() {
Task task = new SimpleTask();
assertNull(task.getOnScheduled());
assertNull(task.onScheduledProperty().get());
}
@Test public void onScheduledCalledWhenSetViaProperty() {
final AtomicBoolean handlerCalled = new AtomicBoolean(false);
SimpleTask task = new SimpleTask();
task.onScheduledProperty().set(workerStateEvent -> handlerCalled.set(true));
task.simulateSchedule();
assertTrue(handlerCalled.get());
}
@Test public void onScheduledFilterCalledBefore_onScheduled() {
SimpleTask task = new SimpleTask();
final AtomicBoolean filterCalled = new AtomicBoolean(false);
final AtomicBoolean filterCalledFirst = new AtomicBoolean(false);
task.addEventFilter(WorkerStateEvent.WORKER_STATE_SCHEDULED, workerStateEvent -> filterCalled.set(true));
task.setOnScheduled(workerStateEvent -> filterCalledFirst.set(filterCalled.get()));
task.simulateSchedule();
assertTrue(filterCalledFirst.get());
}
@Test public void scheduledCalledAfterHandler() {
final AtomicBoolean handlerCalled = new AtomicBoolean(false);
final AtomicBoolean scheduledCalledLast = new AtomicBoolean(false);
SimpleTask task = new SimpleTask() {
@Override protected void scheduled() {
scheduledCalledLast.set(handlerCalled.get());
}
};
task.setOnScheduled(workerStateEvent -> handlerCalled.set(true));
task.simulateSchedule();
assertTrue(scheduledCalledLast.get());
}
@Test public void scheduledCalledAfterHandlerEvenIfConsumed() {
final AtomicBoolean scheduledCalled = new AtomicBoolean(false);
SimpleTask task = new SimpleTask() {
@Override protected void scheduled() {
scheduledCalled.set(true);
}
};
task.setOnScheduled(workerStateEvent -> workerStateEvent.consume());
task.simulateSchedule();
assertTrue(scheduledCalled.get());
}
@Test public void onScheduledHandlerCalled() {
final AtomicBoolean handlerCalled = new AtomicBoolean(false);
SimpleTask task = new SimpleTask();
task.addEventHandler(WorkerStateEvent.WORKER_STATE_SCHEDULED, workerStateEvent -> handlerCalled.set(true));
task.simulateSchedule();
assertTrue(handlerCalled.get());
}
@Test public void removed_onScheduledHandlerNotCalled() {
final AtomicBoolean handlerCalled = new AtomicBoolean(false);
final AtomicBoolean sanity = new AtomicBoolean(false);
SimpleTask task = new SimpleTask();
EventHandler<WorkerStateEvent> handler = workerStateEvent -> handlerCalled.set(true);
task.addEventHandler(WorkerStateEvent.WORKER_STATE_SCHEDULED, handler);
task.removeEventHandler(WorkerStateEvent.WORKER_STATE_SCHEDULED, handler);
task.addEventHandler(WorkerStateEvent.WORKER_STATE_SCHEDULED, workerStateEvent -> sanity.set(true));
task.simulateSchedule();
assertTrue(sanity.get());
assertFalse(handlerCalled.get());
}
@Test public void removed_onScheduledFilterNotCalled() {
final AtomicBoolean filterCalled = new AtomicBoolean(false);
final AtomicBoolean sanity = new AtomicBoolean(false);
SimpleTask task = new SimpleTask();
EventHandler<WorkerStateEvent> filter = workerStateEvent -> filterCalled.set(true);
task.addEventFilter(WorkerStateEvent.WORKER_STATE_SCHEDULED, filter);
task.removeEventFilter(WorkerStateEvent.WORKER_STATE_SCHEDULED, filter);
task.addEventFilter(WorkerStateEvent.WORKER_STATE_SCHEDULED, workerStateEvent -> sanity.set(true));
task.simulateSchedule();
assertTrue(sanity.get());
assertFalse(filterCalled.get());
}
@Test public void onRunningPropertyNameShouldMatchMethodName() {
Task task = new SimpleTask();
assertEquals("onRunning", task.onRunningProperty().getName());
}
@Test public void onRunningBeanShouldMatchTask() {
Task task = new SimpleTask();
assertSame(task, task.onRunningProperty().getBean());
}
@Test public void onRunningIsInitializedToNull() {
Task task = new SimpleTask();
assertNull(task.getOnRunning());
assertNull(task.onRunningProperty().get());
}
@Test public void onRunningCalledWhenSetViaProperty() {
final AtomicBoolean handlerCalled = new AtomicBoolean(false);
SimpleTask task = new SimpleTask();
task.onRunningProperty().set(workerStateEvent -> handlerCalled.set(true));
task.run();
assertTrue(handlerCalled.get());
}
@Test public void onRunningFilterCalledBefore_onRunning() {
SimpleTask task = new SimpleTask();
final AtomicBoolean filterCalled = new AtomicBoolean(false);
final AtomicBoolean filterCalledFirst = new AtomicBoolean(false);
task.addEventFilter(WorkerStateEvent.WORKER_STATE_RUNNING, workerStateEvent -> filterCalled.set(true));
task.setOnRunning(workerStateEvent -> filterCalledFirst.set(filterCalled.get()));
task.run();
assertTrue(filterCalledFirst.get());
}
@Test public void runningCalledAfterHandler() {
final AtomicBoolean handlerCalled = new AtomicBoolean(false);
final AtomicBoolean runningCalledLast = new AtomicBoolean(false);
SimpleTask task = new SimpleTask() {
@Override protected void running() {
runningCalledLast.set(handlerCalled.get());
}
};
task.setOnRunning(workerStateEvent -> handlerCalled.set(true));
task.run();
assertTrue(runningCalledLast.get());
}
@Test public void runningCalledAfterHandlerEvenIfConsumed() {
final AtomicBoolean runningCalled = new AtomicBoolean(false);
SimpleTask task = new SimpleTask() {
@Override protected void running() {
runningCalled.set(true);
}
};
task.setOnRunning(workerStateEvent -> workerStateEvent.consume());
task.run();
assertTrue(runningCalled.get());
}
@Test public void onSucceededPropertyNameShouldMatchMethodName() {
Task task = new SimpleTask();
assertEquals("onSucceeded", task.onSucceededProperty().getName());
}
@Test public void onSucceededBeanShouldMatchTask() {
Task task = new SimpleTask();
assertSame(task, task.onSucceededProperty().getBean());
}
@Test public void onSucceededIsInitializedToNull() {
Task task = new SimpleTask();
assertNull(task.getOnSucceeded());
assertNull(task.onSucceededProperty().get());
}
@Test public void onSucceededCalledWhenSetViaProperty() {
final AtomicBoolean handlerCalled = new AtomicBoolean(false);
SimpleTask task = new SimpleTask();
task.onSucceededProperty().set(workerStateEvent -> handlerCalled.set(true));
task.run();
assertTrue(handlerCalled.get());
}
@Test public void onSucceededFilterCalledBefore_onSucceeded() {
SimpleTask task = new SimpleTask();
final AtomicBoolean filterCalled = new AtomicBoolean(false);
final AtomicBoolean filterCalledFirst = new AtomicBoolean(false);
task.addEventFilter(WorkerStateEvent.WORKER_STATE_SUCCEEDED, workerStateEvent -> filterCalled.set(true));
task.setOnSucceeded(workerStateEvent -> filterCalledFirst.set(filterCalled.get()));
task.run();
assertTrue(filterCalledFirst.get());
}
@Test public void succeededCalledAfterHandler() {
final AtomicBoolean handlerCalled = new AtomicBoolean(false);
final AtomicBoolean succeededCalledLast = new AtomicBoolean(false);
SimpleTask task = new SimpleTask() {
@Override protected void succeeded() {
succeededCalledLast.set(handlerCalled.get());
}
};
task.setOnSucceeded(workerStateEvent -> handlerCalled.set(true));
task.run();
assertTrue(succeededCalledLast.get());
}
@Test public void succeededCalledAfterHandlerEvenIfConsumed() {
final AtomicBoolean succeededCalled = new AtomicBoolean(false);
SimpleTask task = new SimpleTask() {
@Override protected void succeeded() {
succeededCalled.set(true);
}
};
task.setOnSucceeded(workerStateEvent -> workerStateEvent.consume());
task.run();
assertTrue(succeededCalled.get());
}
@Test public void onCancelledPropertyNameShouldMatchMethodName() {
Task task = new SimpleTask();
assertEquals("onCancelled", task.onCancelledProperty().getName());
}
@Test public void onCancelledBeanShouldMatchTask() {
Task task = new SimpleTask();
assertSame(task, task.onCancelledProperty().getBean());
}
@Test public void onCancelledIsInitializedToNull() {
Task task = new SimpleTask();
assertNull(task.getOnCancelled());
assertNull(task.onCancelledProperty().get());
}
@Test public void onCancelledCalledWhenSetViaProperty() throws Exception {
final AtomicBoolean handlerCalled = new AtomicBoolean(false);
InfiniteTask task = new InfiniteTask();
task.onCancelledProperty().set(workerStateEvent -> handlerCalled.set(true));
Thread th = new Thread(task);
th.start();
task.cancel();
th.join();
assertTrue(handlerCalled.get());
}
@Test public void onCancelledFilterCalledBefore_onCancelled() throws Exception {
InfiniteTask task = new InfiniteTask();
final AtomicBoolean filterCalled = new AtomicBoolean(false);
final AtomicBoolean filterCalledFirst = new AtomicBoolean(false);
task.addEventFilter(WorkerStateEvent.WORKER_STATE_CANCELLED, workerStateEvent -> filterCalled.set(true));
task.setOnCancelled(workerStateEvent -> filterCalledFirst.set(filterCalled.get()));
Thread th = new Thread(task);
th.start();
task.cancel();
th.join();
assertTrue(filterCalledFirst.get());
}
@Test public void cancelledCalledAfterHandler() throws Exception {
final AtomicBoolean handlerCalled = new AtomicBoolean(false);
final AtomicBoolean cancelledCalledLast = new AtomicBoolean(false);
InfiniteTask task = new InfiniteTask() {
@Override protected void cancelled() {
cancelledCalledLast.set(handlerCalled.get());
}
};
task.setOnCancelled(workerStateEvent -> handlerCalled.set(true));
Thread th = new Thread(task);
th.start();
task.cancel();
th.join();
assertTrue(cancelledCalledLast.get());
}
@Test public void cancelledCalledAfterHandlerEvenIfConsumed() throws Exception {
final AtomicBoolean cancelledCalled = new AtomicBoolean(false);
InfiniteTask task = new InfiniteTask() {
@Override protected void cancelled() {
cancelledCalled.set(true);
}
};
task.setOnCancelled(workerStateEvent -> workerStateEvent.consume());
Thread th = new Thread(task);
th.start();
task.cancel();
th.join();
assertTrue(cancelledCalled.get());
}
@Test public void onFailedPropertyNameShouldMatchMethodName() {
Task task = new SimpleTask();
assertEquals("onFailed", task.onFailedProperty().getName());
}
@Test public void onFailedBeanShouldMatchTask() {
Task task = new SimpleTask();
assertSame(task, task.onFailedProperty().getBean());
}
@Test public void onFailedIsInitializedToNull() {
Task task = new SimpleTask();
assertNull(task.getOnFailed());
assertNull(task.onFailedProperty().get());
}
@Test public void onFailedCalledWhenSetViaProperty() {
final AtomicBoolean handlerCalled = new AtomicBoolean(false);
Task<String> task = new EpicFailTask();
task.onFailedProperty().set(workerStateEvent -> handlerCalled.set(true));
task.run();
assertTrue(handlerCalled.get());
}
@Test public void onFailedFilterCalledBefore_onFailed() {
Task<String> task = new EpicFailTask();
final AtomicBoolean filterCalled = new AtomicBoolean(false);
final AtomicBoolean filterCalledFirst = new AtomicBoolean(false);
task.addEventFilter(WorkerStateEvent.WORKER_STATE_FAILED, workerStateEvent -> filterCalled.set(true));
task.setOnFailed(workerStateEvent -> filterCalledFirst.set(filterCalled.get()));
task.run();
assertTrue(filterCalledFirst.get());
}
@Test public void failedCalledAfterHandler() {
final AtomicBoolean handlerCalled = new AtomicBoolean(false);
final AtomicBoolean failedCalledLast = new AtomicBoolean(false);
EpicFailTask task = new EpicFailTask() {
@Override protected void failed() {
failedCalledLast.set(handlerCalled.get());
}
};
task.setOnFailed(workerStateEvent -> handlerCalled.set(true));
task.run();
assertTrue(failedCalledLast.get());
}
@Test public void failedCalledAfterHandlerEvenIfConsumed() {
final AtomicBoolean failedCalled = new AtomicBoolean(false);
EpicFailTask task = new EpicFailTask() {
@Override protected void failed() {
failedCalled.set(true);
}
};
task.setOnFailed(workerStateEvent -> workerStateEvent.consume());
task.run();
assertTrue(failedCalled.get());
}
@Test public void eventFiredOnSubclassWorks() {
final AtomicBoolean result = new AtomicBoolean(false);
MythicalTask task = new MythicalTask();
task.setHandler(mythicalEvent -> result.set(true));
task.fireEvent(new MythicalEvent());
assertTrue(result.get());
}
private static final class MythicalTask extends SimpleTask {
public void setHandler(EventHandler<MythicalEvent> h) {
TaskShim.setEventHandler(this, MythicalEvent.ANY, h);
}
}
}
