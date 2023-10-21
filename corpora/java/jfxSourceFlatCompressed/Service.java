package javafx.concurrent;
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.PrivilegedAction;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.sun.javafx.logging.PlatformLogger;
import static javafx.concurrent.WorkerStateEvent.WORKER_STATE_CANCELLED;
import static javafx.concurrent.WorkerStateEvent.WORKER_STATE_FAILED;
import static javafx.concurrent.WorkerStateEvent.WORKER_STATE_READY;
import static javafx.concurrent.WorkerStateEvent.WORKER_STATE_RUNNING;
import static javafx.concurrent.WorkerStateEvent.WORKER_STATE_SCHEDULED;
import static javafx.concurrent.WorkerStateEvent.WORKER_STATE_SUCCEEDED;
public abstract class Service<V> implements Worker<V>, EventTarget {
private static final PlatformLogger LOG = PlatformLogger.getLogger(Service.class.getName());
private static final int THREAD_POOL_SIZE = 32;
private static final long THREAD_TIME_OUT = 1000;
private static final BlockingQueue<Runnable> IO_QUEUE = new LinkedBlockingQueue<Runnable>() {
@Override public boolean offer(Runnable runnable) {
if (EXECUTOR.getPoolSize() < THREAD_POOL_SIZE) {
return false;
}
return super.offer(runnable);
}
};
@SuppressWarnings("removal")
private static final ThreadGroup THREAD_GROUP = AccessController.doPrivileged((PrivilegedAction<ThreadGroup>) () -> new ThreadGroup("javafx concurrent thread pool"));
private static final Thread.UncaughtExceptionHandler UNCAUGHT_HANDLER = (thread, throwable) -> {
if (!(throwable instanceof IllegalMonitorStateException)) {
LOG.warning("Uncaught throwable in " + THREAD_GROUP.getName(), throwable);
}
};
@SuppressWarnings("removal")
private static final ThreadFactory THREAD_FACTORY = run -> AccessController.doPrivileged((PrivilegedAction<Thread>) () -> {
final Thread th = new Thread(THREAD_GROUP, run);
th.setUncaughtExceptionHandler(UNCAUGHT_HANDLER);
th.setPriority(Thread.MIN_PRIORITY);
th.setDaemon(true);
return th;
});
private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
2, THREAD_POOL_SIZE,
THREAD_TIME_OUT, TimeUnit.MILLISECONDS,
IO_QUEUE, THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());
static {
EXECUTOR.allowCoreThreadTimeOut(true);
}
private final ObjectProperty<State> state = new SimpleObjectProperty<>(this, "state", State.READY);
@Override public final State getState() { checkThread(); return state.get(); }
@Override public final ReadOnlyObjectProperty<State> stateProperty() { checkThread(); return state; }
private final ObjectProperty<V> value = new SimpleObjectProperty<>(this, "value");
@Override public final V getValue() { checkThread(); return value.get(); }
@Override public final ReadOnlyObjectProperty<V> valueProperty() { checkThread(); return value; }
private final ObjectProperty<Throwable> exception = new SimpleObjectProperty<>(this, "exception");
@Override public final Throwable getException() { checkThread(); return exception.get(); }
@Override public final ReadOnlyObjectProperty<Throwable> exceptionProperty() { checkThread(); return exception; }
private final DoubleProperty workDone = new SimpleDoubleProperty(this, "workDone", -1);
@Override public final double getWorkDone() { checkThread(); return workDone.get(); }
@Override public final ReadOnlyDoubleProperty workDoneProperty() { checkThread(); return workDone; }
private final DoubleProperty totalWorkToBeDone = new SimpleDoubleProperty(this, "totalWork", -1);
@Override public final double getTotalWork() { checkThread(); return totalWorkToBeDone.get(); }
@Override public final ReadOnlyDoubleProperty totalWorkProperty() { checkThread(); return totalWorkToBeDone; }
private final DoubleProperty progress = new SimpleDoubleProperty(this, "progress", -1);
@Override public final double getProgress() { checkThread(); return progress.get(); }
@Override public final ReadOnlyDoubleProperty progressProperty() { checkThread(); return progress; }
private final BooleanProperty running = new SimpleBooleanProperty(this, "running", false);
@Override public final boolean isRunning() { checkThread(); return running.get(); }
@Override public final ReadOnlyBooleanProperty runningProperty() { checkThread(); return running; }
private final StringProperty message = new SimpleStringProperty(this, "message", "");
@Override public final String getMessage() { checkThread(); return message.get(); }
@Override public final ReadOnlyStringProperty messageProperty() { checkThread(); return message; }
private final StringProperty title = new SimpleStringProperty(this, "title", "");
@Override public final String getTitle() { checkThread(); return title.get(); }
@Override public final ReadOnlyStringProperty titleProperty() { checkThread(); return title; }
private final ObjectProperty<Executor> executor = new SimpleObjectProperty<>(this, "executor");
public final void setExecutor(Executor value) { checkThread(); executor.set(value); }
public final Executor getExecutor() { checkThread(); return executor.get(); }
public final ObjectProperty<Executor> executorProperty() { checkThread(); return executor; }
public final ObjectProperty<EventHandler<WorkerStateEvent>> onReadyProperty() {
checkThread();
return getEventHelper().onReadyProperty();
}
public final EventHandler<WorkerStateEvent> getOnReady() {
checkThread();
return eventHelper == null ? null : eventHelper.getOnReady();
}
public final void setOnReady(EventHandler<WorkerStateEvent> value) {
checkThread();
getEventHelper().setOnReady(value);
}
protected void ready() { }
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
private Task<V> task;
private volatile boolean startedOnce = false;
protected Service() {
state.addListener((observableValue, old, value1) -> {
switch (value1) {
case CANCELLED:
fireEvent(new WorkerStateEvent(Service.this, WORKER_STATE_CANCELLED));
cancelled();
break;
case FAILED:
fireEvent(new WorkerStateEvent(Service.this, WORKER_STATE_FAILED));
failed();
break;
case READY:
fireEvent(new WorkerStateEvent(Service.this, WORKER_STATE_READY));
ready();
break;
case RUNNING:
fireEvent(new WorkerStateEvent(Service.this, WORKER_STATE_RUNNING));
running();
break;
case SCHEDULED:
fireEvent(new WorkerStateEvent(Service.this, WORKER_STATE_SCHEDULED));
scheduled();
break;
case SUCCEEDED:
fireEvent(new WorkerStateEvent(Service.this, WORKER_STATE_SUCCEEDED));
succeeded();
break;
default: throw new AssertionError("Should be unreachable");
}
});
}
@Override public boolean cancel() {
checkThread();
if (task == null) {
if (state.get() == State.CANCELLED || state.get() == State.SUCCEEDED) {
return false;
}
state.set(State.CANCELLED);
return true;
} else {
return task.cancel(true);
}
}
public void restart() {
checkThread();
if (task != null) {
task.cancel();
task = null;
state.unbind();
state.set(State.CANCELLED);
}
reset();
start();
}
public void reset() {
checkThread();
final State s = getState();
if (s == State.SCHEDULED || s == State.RUNNING) {
throw new IllegalStateException();
}
task = null;
state.unbind();
state.set(State.READY);
value.unbind();
value.set(null);
exception.unbind();
exception.set(null);
workDone.unbind();
workDone.set(-1);
totalWorkToBeDone.unbind();
totalWorkToBeDone.set(-1);
progress.unbind();
progress.set(-1);
running.unbind();
running.set(false);
message.unbind();
message.set("");
title.unbind();
title.set("");
}
public void start() {
checkThread();
if (getState() != State.READY) {
throw new IllegalStateException(
"Can only start a Service in the READY state. Was in state " + getState());
}
task = createTask();
state.bind(task.stateProperty());
value.bind(task.valueProperty());
exception.bind(task.exceptionProperty());
workDone.bind(task.workDoneProperty());
totalWorkToBeDone.bind(task.totalWorkProperty());
progress.bind(task.progressProperty());
running.bind(task.runningProperty());
message.bind(task.messageProperty());
title.bind(task.titleProperty());
startedOnce = true;
if (!isFxApplicationThread()) {
runLater(() -> {
task.setState(State.SCHEDULED);
executeTask(task);
});
} else {
task.setState(State.SCHEDULED);
executeTask(task);
}
}
void cancelFromReadyState() {
state.set(State.SCHEDULED);
state.set(State.CANCELLED);
}
@SuppressWarnings("removal")
protected void executeTask(final Task<V> task) {
final AccessControlContext acc = AccessController.getContext();
final Executor e = getExecutor() != null ? getExecutor() : EXECUTOR;
e.execute(() -> {
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
task.run();
return null;
}, acc);
});
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
protected final void fireEvent(Event event) {
checkThread();
getEventHelper().fireEvent(event);
}
@Override
public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
checkThread();
return getEventHelper().buildEventDispatchChain(tail);
}
protected abstract Task<V> createTask();
void checkThread() {
if (startedOnce && !isFxApplicationThread()) {
throw new IllegalStateException("Service must only be used from the FX Application Thread");
}
}
void runLater(Runnable r) {
Platform.runLater(r);
}
boolean isFxApplicationThread() {
return Platform.isFxApplicationThread();
}
}
