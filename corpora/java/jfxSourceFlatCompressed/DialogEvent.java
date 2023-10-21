package javafx.scene.control;
import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
public class DialogEvent extends Event {
private static final long serialVersionUID = 20140716L;
public static final EventType<DialogEvent> ANY =
new EventType<DialogEvent>(Event.ANY, "DIALOG");
public static final EventType<DialogEvent> DIALOG_SHOWING =
new EventType<DialogEvent>(DialogEvent.ANY, "DIALOG_SHOWING");
public static final EventType<DialogEvent> DIALOG_SHOWN =
new EventType<DialogEvent>(DialogEvent.ANY, "DIALOG_SHOWN");
public static final EventType<DialogEvent> DIALOG_HIDING =
new EventType<DialogEvent>(DialogEvent.ANY, "DIALOG_HIDING");
public static final EventType<DialogEvent> DIALOG_HIDDEN =
new EventType<DialogEvent>(DialogEvent.ANY, "DIALOG_HIDDEN");
public static final EventType<DialogEvent> DIALOG_CLOSE_REQUEST =
new EventType<DialogEvent>(DialogEvent.ANY, "DIALOG_CLOSE_REQUEST");
public DialogEvent(final @NamedArg("source") Dialog<?> source, final @NamedArg("eventType") EventType<? extends Event> eventType) {
super(source, source, eventType);
}
@Override public String toString() {
final StringBuilder sb = new StringBuilder("DialogEvent [");
sb.append("source = ").append(getSource());
sb.append(", target = ").append(getTarget());
sb.append(", eventType = ").append(getEventType());
sb.append(", consumed = ").append(isConsumed());
return sb.append("]").toString();
}
@Override public DialogEvent copyFor(Object newSource, EventTarget newTarget) {
return (DialogEvent) super.copyFor(newSource, newTarget);
}
public DialogEvent copyFor(Object newSource, EventTarget newTarget, EventType<DialogEvent> type) {
DialogEvent e = copyFor(newSource, newTarget);
e.eventType = type;
return e;
}
@SuppressWarnings("unchecked")
@Override public EventType<DialogEvent> getEventType() {
return (EventType<DialogEvent>) super.getEventType();
}
}