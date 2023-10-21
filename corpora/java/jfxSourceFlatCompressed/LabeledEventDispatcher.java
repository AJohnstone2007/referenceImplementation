package test.com.sun.javafx.event;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
public class LabeledEventDispatcher implements EventDispatcher {
private final String label;
public LabeledEventDispatcher() {
this(null);
}
public LabeledEventDispatcher(final String label) {
this.label = label;
}
@Override
public Event dispatchEvent(final Event event,
final EventDispatchChain tail) {
return tail.dispatchEvent(event);
}
@Override
public String toString() {
return label;
}
}
