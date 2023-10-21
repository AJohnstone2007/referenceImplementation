package test.com.sun.javafx.event;
import javafx.event.Event;
import javafx.event.EventHandler;
public final class EventConsumingHandler implements EventHandler<Event> {
@Override
public void handle(final Event event) {
event.consume();
}
}
