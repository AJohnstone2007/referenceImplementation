package com.sun.javafx.event;
import javafx.event.Event;
public abstract class CompositeEventDispatcher extends BasicEventDispatcher {
public abstract BasicEventDispatcher getFirstDispatcher();
public abstract BasicEventDispatcher getLastDispatcher();
@Override
public final Event dispatchCapturingEvent(Event event) {
BasicEventDispatcher childDispatcher = getFirstDispatcher();
while (childDispatcher != null) {
event = childDispatcher.dispatchCapturingEvent(event);
if (event.isConsumed()) {
break;
}
childDispatcher = childDispatcher.getNextDispatcher();
}
return event;
}
@Override
public final Event dispatchBubblingEvent(Event event) {
BasicEventDispatcher childDispatcher = getLastDispatcher();
while (childDispatcher != null) {
event = childDispatcher.dispatchBubblingEvent(event);
if (event.isConsumed()) {
break;
}
childDispatcher = childDispatcher.getPreviousDispatcher();
}
return event;
}
}
