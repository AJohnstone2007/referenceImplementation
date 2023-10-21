package javafx.scene.input;
import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
public class InputEvent extends Event {
private static final long serialVersionUID = 20121107L;
public static final EventType<InputEvent> ANY =
new EventType<InputEvent> (Event.ANY, "INPUT");
public InputEvent(final @NamedArg("eventType") EventType<? extends InputEvent> eventType) {
super(eventType);
}
public InputEvent(final @NamedArg("source") Object source,
final @NamedArg("target") EventTarget target,
final @NamedArg("eventType") EventType<? extends InputEvent> eventType) {
super(source, target, eventType);
}
@Override
public EventType<? extends InputEvent> getEventType() {
return (EventType<? extends InputEvent>) super.getEventType();
}
}
