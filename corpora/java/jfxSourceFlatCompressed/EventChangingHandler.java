package test.com.sun.javafx.event;
import javafx.event.EventHandler;
public final class EventChangingHandler implements EventHandler<ValueEvent> {
private final Operation operation;
public EventChangingHandler(final Operation operation) {
this.operation = operation;
}
@Override
public void handle(final ValueEvent event) {
event.setValue(operation.applyTo(event.getValue()));
}
}
