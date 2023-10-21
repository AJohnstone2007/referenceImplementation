package com.sun.javafx.event;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
public class RedirectedEvent extends Event {
private static final long serialVersionUID = 20121107L;
public static final EventType<RedirectedEvent> REDIRECTED =
new EventType<RedirectedEvent>(Event.ANY, "REDIRECTED");
private final Event originalEvent;
public RedirectedEvent(final Event originalEvent) {
this(originalEvent, null, null);
}
public RedirectedEvent(final Event originalEvent,
final Object source,
final EventTarget target) {
super(source, target, REDIRECTED);
this.originalEvent = originalEvent;
}
public Event getOriginalEvent() {
return originalEvent;
}
}
