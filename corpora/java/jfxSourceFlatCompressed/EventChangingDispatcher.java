package test.com.sun.javafx.event;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import org.junit.Assert;
public final class EventChangingDispatcher extends LabeledEventDispatcher {
private final Operation capturingPhaseOperation;
private final Operation bubblingPhaseOperation;
public EventChangingDispatcher(final Operation capturingPhaseOperation,
final Operation bubblingPhaseOperation) {
this(null, capturingPhaseOperation, bubblingPhaseOperation);
}
public EventChangingDispatcher(final String label,
final Operation capturingPhaseOperation,
final Operation bubblingPhaseOperation) {
super(label);
this.capturingPhaseOperation = capturingPhaseOperation;
this.bubblingPhaseOperation = bubblingPhaseOperation;
}
@Override
public Event dispatchEvent(final Event event,
final EventDispatchChain tail) {
Assert.assertTrue(event instanceof ValueEvent);
ValueEvent valueEvent = (ValueEvent) event;
if (capturingPhaseOperation != null) {
valueEvent.setValue(capturingPhaseOperation.applyTo(
valueEvent.getValue()));
}
valueEvent = (ValueEvent) tail.dispatchEvent(valueEvent);
if (bubblingPhaseOperation != null) {
valueEvent.setValue(bubblingPhaseOperation.applyTo(
valueEvent.getValue()));
}
return valueEvent;
}
}
