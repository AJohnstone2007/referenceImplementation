package test.com.sun.javafx.event;
import javafx.event.Event;
import javafx.event.EventType;
public final class EmptyEvent extends Event {
public static final EventType<EmptyEvent> EMPTY =
new EventType<EmptyEvent>(Event.ANY, "EMPTY");
public EmptyEvent() {
super(EMPTY);
}
}
