package test.com.sun.javafx.event;
import javafx.event.Event;
import javafx.event.EventHandler;
public class EventCountingHandler<T extends Event> implements EventHandler<T> {
private int eventCount;
public int getEventCount() {
return eventCount;
}
@Override
public void handle(final T event) {
++eventCount;
}
}
