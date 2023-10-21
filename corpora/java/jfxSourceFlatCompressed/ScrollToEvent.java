package javafx.scene.control;
import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
public class ScrollToEvent<T> extends Event {
public static final EventType<ScrollToEvent> ANY =
new EventType<ScrollToEvent> (Event.ANY, "SCROLL_TO");
public static EventType<ScrollToEvent<Integer>> scrollToTopIndex() {
return SCROLL_TO_TOP_INDEX;
}
private static final EventType<ScrollToEvent<Integer>> SCROLL_TO_TOP_INDEX =
new EventType<ScrollToEvent<Integer>>(ScrollToEvent.ANY, "SCROLL_TO_TOP_INDEX");
@SuppressWarnings("unchecked")
public static <T extends TableColumnBase<?, ?>> EventType<ScrollToEvent<T>> scrollToColumn() {
return (EventType<ScrollToEvent<T>>) SCROLL_TO_COLUMN;
}
private static final EventType<?> SCROLL_TO_COLUMN =
new EventType<>(ScrollToEvent.ANY, "SCROLL_TO_COLUMN");
private static final long serialVersionUID = -8557345736849482516L;
private final T scrollTarget;
public ScrollToEvent(@NamedArg("source") Object source, @NamedArg("target") EventTarget target, @NamedArg("type") EventType<ScrollToEvent<T>> type, @NamedArg("scrollTarget") T scrollTarget) {
super(source, target, type);
assert scrollTarget != null;
this.scrollTarget = scrollTarget;
}
public T getScrollTarget() {
return scrollTarget;
}
}
