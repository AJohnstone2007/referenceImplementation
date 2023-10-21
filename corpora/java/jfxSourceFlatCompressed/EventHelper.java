package javafx.concurrent;
import com.sun.javafx.event.EventHandlerManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.*;
import static javafx.concurrent.WorkerStateEvent.*;
import static javafx.concurrent.WorkerStateEvent.WORKER_STATE_CANCELLED;
import static javafx.concurrent.WorkerStateEvent.WORKER_STATE_FAILED;
class EventHelper {
private final EventTarget target;
private final ObjectProperty<EventHandler<WorkerStateEvent>> onReady;
final ObjectProperty<EventHandler<WorkerStateEvent>> onReadyProperty() { return onReady; }
final EventHandler<WorkerStateEvent> getOnReady() { return onReady.get(); }
final void setOnReady(EventHandler<WorkerStateEvent> value) { onReady.set(value); }
private final ObjectProperty<EventHandler<WorkerStateEvent>> onScheduled;
final ObjectProperty<EventHandler<WorkerStateEvent>> onScheduledProperty() { return onScheduled; }
final EventHandler<WorkerStateEvent> getOnScheduled() { return onScheduled.get(); }
final void setOnScheduled(EventHandler<WorkerStateEvent> value) { onScheduled.set(value); }
private final ObjectProperty<EventHandler<WorkerStateEvent>> onRunning;
final ObjectProperty<EventHandler<WorkerStateEvent>> onRunningProperty() { return onRunning; }
final EventHandler<WorkerStateEvent> getOnRunning() { return onRunning.get(); }
final void setOnRunning(EventHandler<WorkerStateEvent> value) { onRunning.set(value); }
private final ObjectProperty<EventHandler<WorkerStateEvent>> onSucceeded;
final ObjectProperty<EventHandler<WorkerStateEvent>> onSucceededProperty() { return onSucceeded; }
final EventHandler<WorkerStateEvent> getOnSucceeded() { return onSucceeded.get(); }
final void setOnSucceeded(EventHandler<WorkerStateEvent> value) { onSucceeded.set(value); }
private final ObjectProperty<EventHandler<WorkerStateEvent>> onCancelled;
final ObjectProperty<EventHandler<WorkerStateEvent>> onCancelledProperty() { return onCancelled; }
final EventHandler<WorkerStateEvent> getOnCancelled() { return onCancelled.get(); }
final void setOnCancelled(EventHandler<WorkerStateEvent> value) { onCancelled.set(value); }
private final ObjectProperty<EventHandler<WorkerStateEvent>> onFailed;
final ObjectProperty<EventHandler<WorkerStateEvent>> onFailedProperty() { return onFailed; }
final EventHandler<WorkerStateEvent> getOnFailed() { return onFailed.get(); }
final void setOnFailed(EventHandler<WorkerStateEvent> value) { onFailed.set(value); }
private EventHandlerManager internalEventDispatcher;
EventHelper(EventTarget bean) {
this.target = bean;
onReady = new SimpleObjectProperty<EventHandler<WorkerStateEvent>>(bean, "onReady") {
@Override protected void invalidated() {
EventHandler<WorkerStateEvent> handler = get();
setEventHandler(WORKER_STATE_READY, handler);
}
};
onScheduled = new SimpleObjectProperty<EventHandler<WorkerStateEvent>>(bean, "onScheduled") {
@Override protected void invalidated() {
EventHandler<WorkerStateEvent> handler = get();
setEventHandler(WORKER_STATE_SCHEDULED, handler);
}
};
onRunning = new SimpleObjectProperty<EventHandler<WorkerStateEvent>>(bean, "onRunning") {
@Override protected void invalidated() {
EventHandler<WorkerStateEvent> handler = get();
setEventHandler(WORKER_STATE_RUNNING, handler);
}
};
onSucceeded = new SimpleObjectProperty<EventHandler<WorkerStateEvent>>(bean, "onSucceeded") {
@Override protected void invalidated() {
EventHandler<WorkerStateEvent> handler = get();
setEventHandler(WORKER_STATE_SUCCEEDED, handler);
}
};
onCancelled = new SimpleObjectProperty<EventHandler<WorkerStateEvent>>(bean, "onCancelled") {
@Override protected void invalidated() {
EventHandler<WorkerStateEvent> handler = get();
setEventHandler(WORKER_STATE_CANCELLED, handler);
}
};
onFailed = new SimpleObjectProperty<EventHandler<WorkerStateEvent>>(bean, "onFailed") {
@Override protected void invalidated() {
EventHandler<WorkerStateEvent> handler = get();
setEventHandler(WORKER_STATE_FAILED, handler);
}
};
}
final <T extends Event> void addEventHandler(
final EventType<T> eventType,
final EventHandler<? super T> eventHandler) {
getInternalEventDispatcher()
.addEventHandler(eventType, eventHandler);
}
final <T extends Event> void removeEventHandler(
final EventType<T> eventType,
final EventHandler<? super T> eventHandler) {
getInternalEventDispatcher()
.removeEventHandler(eventType, eventHandler);
}
final <T extends Event> void addEventFilter(
final EventType<T> eventType,
final EventHandler<? super T> eventFilter) {
getInternalEventDispatcher()
.addEventFilter(eventType, eventFilter);
}
final <T extends Event> void removeEventFilter(
final EventType<T> eventType,
final EventHandler<? super T> eventFilter) {
getInternalEventDispatcher()
.removeEventFilter(eventType, eventFilter);
}
final <T extends Event> void setEventHandler(
final EventType<T> eventType,
final EventHandler<? super T> eventHandler) {
getInternalEventDispatcher()
.setEventHandler(eventType, eventHandler);
}
private EventHandlerManager getInternalEventDispatcher() {
if (internalEventDispatcher == null) {
internalEventDispatcher = new EventHandlerManager(target);
}
return internalEventDispatcher;
}
final void fireEvent(Event event) {
Event.fireEvent(target, event);
}
EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
return internalEventDispatcher == null ? tail : tail.append(getInternalEventDispatcher());
}
}
