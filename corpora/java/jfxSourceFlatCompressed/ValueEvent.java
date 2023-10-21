package test.com.sun.javafx.event;
import javafx.event.Event;
import javafx.event.EventType;
public final class ValueEvent extends Event {
public static final EventType<ValueEvent> ANY =
new EventType<ValueEvent>(Event.ANY, "VALUE");
public static final EventType<ValueEvent> VALUE_A =
new EventType<ValueEvent>(ValueEvent.ANY, "VALUE_A");
public static final EventType<ValueEvent> VALUE_B =
new EventType<ValueEvent>(ValueEvent.ANY, "VALUE_B");
public static final EventType<ValueEvent> VALUE_C =
new EventType<ValueEvent>(ValueEvent.ANY, "VALUE_C");
private int value;
public ValueEvent() {
this(VALUE_A, 0);
}
public ValueEvent(final int initialValue) {
this(VALUE_A, initialValue);
}
public ValueEvent(final EventType<? extends ValueEvent> eventType,
final int initialValue) {
super(eventType);
value = initialValue;
}
public void setValue(int value) {
this.value = value;
}
public int getValue() {
return value;
}
}
