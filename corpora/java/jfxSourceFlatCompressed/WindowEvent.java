package javafx.stage;
import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
public class WindowEvent extends Event {
private static final long serialVersionUID = 20121107L;
public static final EventType<WindowEvent> ANY =
new EventType<WindowEvent>(Event.ANY, "WINDOW");
public static final EventType<WindowEvent> WINDOW_SHOWING =
new EventType<WindowEvent>(WindowEvent.ANY, "WINDOW_SHOWING");
public static final EventType<WindowEvent> WINDOW_SHOWN =
new EventType<WindowEvent>(WindowEvent.ANY, "WINDOW_SHOWN");
public static final EventType<WindowEvent> WINDOW_HIDING =
new EventType<WindowEvent>(WindowEvent.ANY, "WINDOW_HIDING");
public static final EventType<WindowEvent> WINDOW_HIDDEN =
new EventType<WindowEvent>(WindowEvent.ANY, "WINDOW_HIDDEN");
public static final EventType<WindowEvent> WINDOW_CLOSE_REQUEST =
new EventType<WindowEvent>(WindowEvent.ANY, "WINDOW_CLOSE_REQUEST");
public WindowEvent(final @NamedArg("source") Window source, final @NamedArg("eventType") EventType<? extends Event> eventType) {
super(source, source, eventType);
}
@Override public String toString() {
final StringBuilder sb = new StringBuilder("WindowEvent [");
sb.append("source = ").append(getSource());
sb.append(", target = ").append(getTarget());
sb.append(", eventType = ").append(getEventType());
sb.append(", consumed = ").append(isConsumed());
return sb.append("]").toString();
}
@Override
public WindowEvent copyFor(Object newSource, EventTarget newTarget) {
return (WindowEvent) super.copyFor(newSource, newTarget);
}
public WindowEvent copyFor(Object newSource, EventTarget newTarget, EventType<WindowEvent> type) {
WindowEvent e = copyFor(newSource, newTarget);
e.eventType = type;
return e;
}
@Override
public EventType<WindowEvent> getEventType() {
return (EventType<WindowEvent>) super.getEventType();
}
}
