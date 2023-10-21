package com.sun.javafx.event;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.event.Event;
import javafx.event.EventDispatcher;
import javafx.event.EventType;
public class EventRedirector extends BasicEventDispatcher {
private final EventDispatchChainImpl eventDispatchChain;
private final List<EventDispatcher> eventDispatchers;
private final Object eventSource;
public EventRedirector(final Object eventSource) {
this.eventDispatchers = new CopyOnWriteArrayList<EventDispatcher>();
this.eventDispatchChain = new EventDispatchChainImpl();
this.eventSource = eventSource;
}
protected void handleRedirectedEvent(
final Object eventSource,
final Event event) {
}
public final void addEventDispatcher(
final EventDispatcher eventDispatcher) {
eventDispatchers.add(eventDispatcher);
}
public final void removeEventDispatcher(
final EventDispatcher eventDispatcher) {
eventDispatchers.remove(eventDispatcher);
}
@Override
public final Event dispatchCapturingEvent(Event event) {
final EventType<?> eventType = event.getEventType();
if (eventType == DirectEvent.DIRECT) {
event = ((DirectEvent) event).getOriginalEvent();
} else {
redirectEvent(event);
if (eventType == RedirectedEvent.REDIRECTED) {
handleRedirectedEvent(
event.getSource(),
((RedirectedEvent) event).getOriginalEvent());
}
}
return event;
}
private void redirectEvent(final Event event) {
if (!eventDispatchers.isEmpty()) {
final RedirectedEvent redirectedEvent =
(event.getEventType() == RedirectedEvent.REDIRECTED)
? (RedirectedEvent) event
: new RedirectedEvent(event, eventSource, null);
for (final EventDispatcher eventDispatcher: eventDispatchers) {
eventDispatchChain.reset();
eventDispatcher.dispatchEvent(
redirectedEvent, eventDispatchChain);
}
}
}
}
