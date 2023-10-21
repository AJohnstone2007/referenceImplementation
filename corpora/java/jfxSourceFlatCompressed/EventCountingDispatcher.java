package test.com.sun.javafx.event;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
public final class EventCountingDispatcher extends LabeledEventDispatcher {
private int capturingEventCount;
private int bubblingEventCount;
private boolean consumeCapturingEvent;
private boolean consumeBubblingEvent;
public EventCountingDispatcher() {
}
public EventCountingDispatcher(final String label) {
super(label);
}
public int getCapturingEventCount() {
return capturingEventCount;
}
public int getBubblingEventCount() {
return bubblingEventCount;
}
public void setConsumeCapturingEvent(final boolean consume) {
consumeCapturingEvent = consume;
}
public void setConsumeBubblingEvent(final boolean consume) {
consumeBubblingEvent = consume;
}
@Override
public Event dispatchEvent(final Event event,
final EventDispatchChain tail) {
++capturingEventCount;
if (consumeCapturingEvent) {
return null;
}
final Event returnEvent = tail.dispatchEvent(event);
if (returnEvent != null) {
++bubblingEventCount;
}
return consumeBubblingEvent ? null : returnEvent;
}
}
