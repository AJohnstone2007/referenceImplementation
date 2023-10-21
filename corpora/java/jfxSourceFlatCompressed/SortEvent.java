package javafx.scene.control;
import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
public class SortEvent<C> extends Event {
public static final EventType<SortEvent> ANY =
new EventType<SortEvent> (Event.ANY, "SORT");
@SuppressWarnings("unchecked")
public static <C> EventType<SortEvent<C>> sortEvent() {
return (EventType<SortEvent<C>>) SORT_EVENT;
}
private static final EventType<?> SORT_EVENT = new EventType<>(SortEvent.ANY, "SORT_EVENT");
public SortEvent(@NamedArg("source") C source, @NamedArg("target") EventTarget target) {
super(source, target, sortEvent());
}
@SuppressWarnings("unchecked")
@Override public C getSource() {
return (C) super.getSource();
}
}
