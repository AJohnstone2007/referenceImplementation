package com.sun.javafx.event;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
public abstract class BasicEventDispatcher implements EventDispatcher {
private BasicEventDispatcher previousDispatcher;
private BasicEventDispatcher nextDispatcher;
@Override
public Event dispatchEvent(Event event, final EventDispatchChain tail) {
event = dispatchCapturingEvent(event);
if (event.isConsumed()) {
return null;
}
event = tail.dispatchEvent(event);
if (event != null) {
event = dispatchBubblingEvent(event);
if (event.isConsumed()) {
return null;
}
}
return event;
}
public Event dispatchCapturingEvent(Event event) {
return event;
}
public Event dispatchBubblingEvent(Event event) {
return event;
}
public final BasicEventDispatcher getPreviousDispatcher() {
return previousDispatcher;
}
public final BasicEventDispatcher getNextDispatcher() {
return nextDispatcher;
}
public final void insertNextDispatcher(
final BasicEventDispatcher newDispatcher) {
if (nextDispatcher != null) {
nextDispatcher.previousDispatcher = newDispatcher;
}
newDispatcher.nextDispatcher = nextDispatcher;
newDispatcher.previousDispatcher = this;
nextDispatcher = newDispatcher;
}
}
