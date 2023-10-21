package test.com.sun.javafx.event;
import com.sun.javafx.event.BasicEventDispatcher;
import javafx.event.Event;
public final class StubBasicEventDispatcher extends BasicEventDispatcher {
private int capturingEventCount;
private int bubblingEventCount;
private ConsumeEvent consumeNextEvent;
public enum ConsumeEvent {
CAPTURING, BUBBLING
}
public int getCapturingEventCount() {
return capturingEventCount;
}
public int getBubblingEventCount() {
return bubblingEventCount;
}
public void setConsumeNextEvent(final ConsumeEvent consumeNextEvent) {
this.consumeNextEvent = consumeNextEvent;
}
@Override
public Event dispatchCapturingEvent(final Event event) {
++capturingEventCount;
if (consumeNextEvent == ConsumeEvent.CAPTURING) {
event.consume();
consumeNextEvent = null;
}
return event;
}
@Override
public Event dispatchBubblingEvent(final Event event) {
++bubblingEventCount;
if (consumeNextEvent == ConsumeEvent.BUBBLING) {
event.consume();
consumeNextEvent = null;
}
return event;
}
}
