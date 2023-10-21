package javafx.concurrent;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;
import static javafx.concurrent.WorkerStateEvent.WORKER_STATE_CANCELLED;
import static javafx.concurrent.WorkerStateEvent.WORKER_STATE_FAILED;
import static javafx.concurrent.WorkerStateEvent.WORKER_STATE_RUNNING;
import static javafx.concurrent.WorkerStateEvent.WORKER_STATE_SCHEDULED;
import static javafx.concurrent.WorkerStateEvent.WORKER_STATE_SUCCEEDED;
public abstract class Task<V> extends FutureTask<V> implements Worker<V>, EventTarget {
private AtomicReference<ProgressUpdate> progressUpdate = new AtomicReference<>();
private AtomicReference<String> messageUpdate = new AtomicReference<>();
private AtomicReference<String> titleUpdate = new AtomicReference<>();
private AtomicReference<V> valueUpdate = new AtomicReference<>();
private volatile boolean started = false;
public Task() {
this(new TaskCallable<V>());
}
private Task(final TaskCallable<V> callableAdapter) {
super(callableAdapter);
callableAdapter.task = this;
}
protected abstract V call() throws Exception;
private ObjectProperty<State> state = new SimpleObjectProperty<>(this, "state", State.READY);
final void setState(State value) {
checkThread();
final State s = getState();
if (s != State.CANCELLED) {
this.state.set(value);
setRunning(value == State.SCHEDULED || value == State.RUNNING);
switch (state.get()) {
case CANCELLED:
fireEvent(new WorkerStateEvent(this, WORKER_STATE_CANCELLED));
cancelled();
break;
case FAILED:
fireEvent(new WorkerStateEvent(this, WORKER_STATE_FAILED));
failed();
break;
case READY:
break;
case RUNNING:
fireEvent(new WorkerStateEvent(this, WORKER_STATE_RUNNING));
running();
break;
case SCHEDULED:
fireEvent(new WorkerStateEvent(this, WORKER_STATE_SCHEDULED));
scheduled();
break;
case SUCCEEDED:
fireEvent(new WorkerStateEvent(this, WORKER_STATE_SUCCEEDED));
succeeded();
break;
default: throw new AssertionError("Should be unreachable");
}
}
}
@Override public final State getState() { checkThread(); return state.get(); }
@Override public final ReadOnlyObjectProperty<State> stateProperty() { checkThread(); return state; }
public final ObjectProperty<EventHandler<WorkerStateEvent>> onScheduledProperty() {
checkThread();
return getEventHelper().onScheduledProperty();
}
public final EventHandler<WorkerStateEvent> getOnScheduled() {
checkThread();
return eventHelper == null ? null : eventHelper.getOnScheduled();
}
public final void setOnScheduled(EventHandler<WorkerStateEvent> value) {
checkThread();
getEventHelper().setOnScheduled(value);
}
protected void scheduled() { }
public final ObjectProperty<EventHandler<WorkerStateEvent>> onRunningProperty() {
checkThread();
return getEventHelper().onRunningProperty();
}
public final EventHandler<WorkerStateEvent> getOnRunning() {
checkThread();
return eventHelper == null ? null : eventHelper.getOnRunning();
}
public final void setOnRunning(EventHandler<WorkerStateEvent> value) {
checkThread();
getEventHelper().setOnRunning(value);
}
protected void running() { }
public final ObjectProperty<EventHandler<WorkerStateEvent>> onSucceededProperty() {
checkThread();
return getEventHelper().onSucceededProperty();
}
public final EventHandler<WorkerStateEvent> getOnSucceeded() {
checkThread();
return eventHelper == null ? null : eventHelper.getOnSucceeded();
}
public final void setOnSucceeded(EventHandler<WorkerStateEvent> value) {
checkThread();
getEventHelper().setOnSucceeded(value);
}
protected void succeeded() { }
public final ObjectProperty<EventHandler<WorkerStateEvent>> onCancelledProperty() {
checkThread();
return getEventHelper().onCancelledProperty();
}
public final EventHandler<WorkerStateEvent> getOnCancelled() {
checkThread();
return eventHelper == null ? null : eventHelper.getOnCancelled();
}
public final void setOnCancelled(EventHandler<WorkerStateEvent> value) {
checkThread();
getEventHelper().setOnCancelled(value);
}
protected void cancelled() { }
public final ObjectProperty<EventHandler<WorkerStateEvent>> onFailedProperty() {
checkThread();
return getEventHelper().onFailedProperty();
}
public final EventHandler<WorkerStateEvent> getOnFailed() {
checkThread();
return eventHelper == null ? null : eventHelper.getOnFailed();
}
public final void setOnFailed(EventHandler<WorkerStateEvent> value) {
checkThread();
getEventHelper().setOnFailed(value);
}
protected void failed() { }
private final ObjectProperty<V> value = new SimpleObjectProperty<>(this, "value");
private void setValue(V v) { checkThread(); value.set(v); }
@Override public final V getValue() { checkThread(); return value.get(); }
@Override public final ReadOnlyObjectProperty<V> valueProperty() { checkThread(); return value; }
private final ObjectProperty<Throwable> exception = new SimpleObjectProperty<>(this, "exception");
private void _setException(Throwable value) { checkThread(); exception.set(value); }
@Override public final Throwable getException() { checkThread(); return exception.get(); }
@Override public final ReadOnlyObjectProperty<Throwable> exceptionProperty() { checkThread(); return exception; }
private final DoubleProperty workDone = new SimpleDoubleProperty(this, "workDone", -1);
private void setWorkDone(double value) { checkThread(); workDone.set(value); }
@Override public final double getWorkDone() { checkThread(); return workDone.get(); }
@Override public final ReadOnlyDoubleProperty workDoneProperty() { checkThread(); return workDone; }
private final DoubleProperty totalWork = new SimpleDoubleProperty(this, "totalWork", -1);
private void setTotalWork(double value) { checkThread(); totalWork.set(value); }
@Override public final double getTotalWork() { checkThread(); return totalWork.get(); }
@Override public final ReadOnlyDoubleProperty totalWorkProperty() { checkThread(); return totalWork; }
private final DoubleProperty progress = new SimpleDoubleProperty(this, "progress", -1);
private void setProgress(double value) { checkThread(); progress.set(value); }
@Override public final double getProgress() { checkThread(); return progress.get(); }
@Override public final ReadOnlyDoubleProperty progressProperty() { checkThread(); return progress; }
private final BooleanProperty running = new SimpleBooleanProperty(this, "running", false);
private void setRunning(boolean value) { checkThread(); running.set(value); }
@Override public final boolean isRunning() { checkThread(); return running.get(); }
@Override public final ReadOnlyBooleanProperty runningProperty() { checkThread(); return running; }
private final StringProperty message = new SimpleStringProperty(this, "message", "");
@Override public final String getMessage() { checkThread(); return message.get(); }
@Override public final ReadOnlyStringProperty messageProperty() { checkThread(); return message; }
private final StringProperty title = new SimpleStringProperty(this, "title", "");
@Override public final String getTitle() { checkThread(); return title.get(); }
@Override public final ReadOnlyStringProperty titleProperty() { checkThread(); return title; }
@Override public final boolean cancel() {
return cancel(true);
}
private static final Permission modifyThreadPerm = new RuntimePermission("modifyThread");
@Override public boolean cancel(boolean mayInterruptIfRunning) {
@SuppressWarnings("removal")
boolean flag = AccessController.doPrivileged(
(PrivilegedAction<Boolean>) () -> super.cancel(mayInterruptIfRunning),
null,
modifyThreadPerm);
if (flag) {
if (isFxApplicationThread()) {
setState(State.CANCELLED);
} else {
runLater(() -> setState(State.CANCELLED));
}
}
return flag;
}
protected void updateProgress(long workDone, long max) {
updateProgress((double)workDone, (double)max);
}
protected void updateProgress(double workDone, double max) {
if (Double.isInfinite(workDone) || Double.isNaN(workDone)) {
workDone = -1;
}
if (Double.isInfinite(max) || Double.isNaN(max)) {
max = -1;
}
if (workDone < 0) {
workDone = -1;
}
if (max < 0) {
max = -1;
}
if (workDone > max) {
workDone = max;
}
if (isFxApplicationThread()) {
_updateProgress(workDone, max);
} else if (progressUpdate.getAndSet(new ProgressUpdate(workDone, max)) == null) {
runLater(() -> {
final ProgressUpdate update = progressUpdate.getAndSet(null);
_updateProgress(update.workDone, update.totalWork);
});
}
}
private void _updateProgress(double workDone, double max) {
setTotalWork(max);
setWorkDone(workDone);
if (workDone == -1) {
setProgress(-1);
} else {
setProgress(workDone / max);
}
}
protected void updateMessage(String message) {
if (isFxApplicationThread()) {
this.message.set(message);
} else {
if (messageUpdate.getAndSet(message) == null) {
runLater(new Runnable() {
@Override public void run() {
final String message = messageUpdate.getAndSet(null);
Task.this.message.set(message);
}
});
}
}
}
protected void updateTitle(String title) {
if (isFxApplicationThread()) {
this.title.set(title);
} else {
if (titleUpdate.getAndSet(title) == null) {
runLater(new Runnable() {
@Override public void run() {
final String title = titleUpdate.getAndSet(null);
Task.this.title.set(title);
}
});
}
}
}
protected void updateValue(V value) {
if (isFxApplicationThread()) {
this.value.set(value);
} else {
if (valueUpdate.getAndSet(value) == null) {
runLater(() -> Task.this.value.set(valueUpdate.getAndSet(null)));
}
}
}
private void checkThread() {
if (started && !isFxApplicationThread()) {
throw new IllegalStateException("Task must only be used from the FX Application Thread");
}
}
void runLater(Runnable r) {
Platform.runLater(r);
}
boolean isFxApplicationThread() {
return Platform.isFxApplicationThread();
}
private EventHelper eventHelper = null;
private EventHelper getEventHelper() {
if (eventHelper == null) {
eventHelper = new EventHelper(this);
}
return eventHelper;
}
public final <T extends Event> void addEventHandler(
final EventType<T> eventType,
final EventHandler<? super T> eventHandler) {
checkThread();
getEventHelper().addEventHandler(eventType, eventHandler);
}
public final <T extends Event> void removeEventHandler(
final EventType<T> eventType,
final EventHandler<? super T> eventHandler) {
checkThread();
getEventHelper().removeEventHandler(eventType, eventHandler);
}
public final <T extends Event> void addEventFilter(
final EventType<T> eventType,
final EventHandler<? super T> eventFilter) {
checkThread();
getEventHelper().addEventFilter(eventType, eventFilter);
}
public final <T extends Event> void removeEventFilter(
final EventType<T> eventType,
final EventHandler<? super T> eventFilter) {
checkThread();
getEventHelper().removeEventFilter(eventType, eventFilter);
}
protected final <T extends Event> void setEventHandler(
final EventType<T> eventType,
final EventHandler<? super T> eventHandler) {
checkThread();
getEventHelper().setEventHandler(eventType, eventHandler);
}
public final void fireEvent(Event event) {
checkThread();
getEventHelper().fireEvent(event);
}
@Override
public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
checkThread();
return getEventHelper().buildEventDispatchChain(tail);
}
private static final class ProgressUpdate {
private final double workDone;
private final double totalWork;
private ProgressUpdate(double p, double m) {
this.workDone = p;
this.totalWork = m;
}
}
private static final class TaskCallable<V> implements Callable<V> {
private Task<V> task;
private TaskCallable() { }
@Override public V call() throws Exception {
task.started = true;
task.runLater(() -> {
task.setState(State.SCHEDULED);
task.setState(State.RUNNING);
});
try {
final V result = task.call();
if (!task.isCancelled()) {
task.runLater(() -> {
task.updateValue(result);
task.setState(State.SUCCEEDED);
});
return result;
} else {
return null;
}
} catch (final Throwable th) {
task.runLater(() -> {
task._setException(th);
task.setState(State.FAILED);
});
if (th instanceof Exception) {
throw (Exception) th;
} else {
throw new Exception(th);
}
}
}
}
}
