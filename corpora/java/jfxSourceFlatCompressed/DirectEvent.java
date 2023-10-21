package com.sun.javafx.event;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
public class DirectEvent extends Event {
private static final long serialVersionUID = 20121107L;
public static final EventType<DirectEvent> DIRECT =
new EventType<DirectEvent>(Event.ANY, "DIRECT");
private final Event originalEvent;
public DirectEvent(final Event originalEvent) {
this(originalEvent, null, null);
}
public DirectEvent(final Event originalEvent,
final Object source,
final EventTarget target) {
super(source, target, DIRECT);
this.originalEvent = originalEvent;
}
public Event getOriginalEvent() {
return originalEvent;
}
}
