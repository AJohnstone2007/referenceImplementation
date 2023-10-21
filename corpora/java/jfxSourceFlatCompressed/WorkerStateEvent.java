package javafx.concurrent;
import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
public class WorkerStateEvent extends Event {
public static final EventType<WorkerStateEvent> ANY =
new EventType<WorkerStateEvent>(Event.ANY, "WORKER_STATE");
public static final EventType<WorkerStateEvent> WORKER_STATE_READY =
new EventType<WorkerStateEvent>(WorkerStateEvent.ANY, "WORKER_STATE_READY");
public static final EventType<WorkerStateEvent> WORKER_STATE_SCHEDULED =
new EventType<WorkerStateEvent>(WorkerStateEvent.ANY, "WORKER_STATE_SCHEDULED");
public static final EventType<WorkerStateEvent> WORKER_STATE_RUNNING =
new EventType<WorkerStateEvent>(WorkerStateEvent.ANY, "WORKER_STATE_RUNNING");
public static final EventType<WorkerStateEvent> WORKER_STATE_SUCCEEDED =
new EventType<WorkerStateEvent>(WorkerStateEvent.ANY, "WORKER_STATE_SUCCEEDED");
public static final EventType<WorkerStateEvent> WORKER_STATE_CANCELLED =
new EventType<WorkerStateEvent>(WorkerStateEvent.ANY, "WORKER_STATE_CANCELLED");
public static final EventType<WorkerStateEvent> WORKER_STATE_FAILED =
new EventType<WorkerStateEvent>(WorkerStateEvent.ANY, "WORKER_STATE_FAILED");
public WorkerStateEvent(@NamedArg("worker") Worker worker, @NamedArg("eventType") EventType<? extends WorkerStateEvent> eventType) {
super(worker, worker instanceof EventTarget ? (EventTarget) worker : null, eventType);
}
@Override public Worker getSource() {
return (Worker) super.getSource();
}
}
