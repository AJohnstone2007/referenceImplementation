package com.sun.javafx.event;
import java.util.HashMap;
import java.util.Map;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
public class EventHandlerManager extends BasicEventDispatcher {
private final Map<EventType<? extends Event>,
CompositeEventHandler<? extends Event>> eventHandlerMap;
private final Object eventSource;
public EventHandlerManager(final Object eventSource) {
this.eventSource = eventSource;
eventHandlerMap =
new HashMap<EventType<? extends Event>,
CompositeEventHandler<? extends Event>>();
}
public final <T extends Event> void addEventHandler(
final EventType<T> eventType,
final EventHandler<? super T> eventHandler) {
validateEventType(eventType);
validateEventHandler(eventHandler);
final CompositeEventHandler<T> compositeEventHandler =
createGetCompositeEventHandler(eventType);
compositeEventHandler.addEventHandler(eventHandler);
}
public final <T extends Event> void removeEventHandler(
final EventType<T> eventType,
final EventHandler<? super T> eventHandler) {
validateEventType(eventType);
validateEventHandler(eventHandler);
final CompositeEventHandler<T> compositeEventHandler =
(CompositeEventHandler<T>) eventHandlerMap.get(eventType);
if (compositeEventHandler != null) {
compositeEventHandler.removeEventHandler(eventHandler);
}
}
public final <T extends Event> void addEventFilter(
final EventType<T> eventType,
final EventHandler<? super T> eventFilter) {
validateEventType(eventType);
validateEventFilter(eventFilter);
final CompositeEventHandler<T> compositeEventHandler =
createGetCompositeEventHandler(eventType);
compositeEventHandler.addEventFilter(eventFilter);
}
public final <T extends Event> void removeEventFilter(
final EventType<T> eventType,
final EventHandler<? super T> eventFilter) {
validateEventType(eventType);
validateEventFilter(eventFilter);
final CompositeEventHandler<T> compositeEventHandler =
(CompositeEventHandler<T>) eventHandlerMap.get(eventType);
if (compositeEventHandler != null) {
compositeEventHandler.removeEventFilter(eventFilter);
}
}
public final <T extends Event> void setEventHandler(
final EventType<T> eventType,
final EventHandler<? super T> eventHandler) {
validateEventType(eventType);
CompositeEventHandler<T> compositeEventHandler =
(CompositeEventHandler<T>) eventHandlerMap.get(eventType);
if (compositeEventHandler == null) {
if (eventHandler == null) {
return;
}
compositeEventHandler = new CompositeEventHandler<T>();
eventHandlerMap.put(eventType, compositeEventHandler);
}
compositeEventHandler.setEventHandler(eventHandler);
}
public final <T extends Event> EventHandler<? super T> getEventHandler(
final EventType<T> eventType) {
final CompositeEventHandler<T> compositeEventHandler =
(CompositeEventHandler<T>) eventHandlerMap.get(eventType);
return (compositeEventHandler != null)
? compositeEventHandler.getEventHandler()
: null;
}
@Override
public final Event dispatchCapturingEvent(Event event) {
EventType<? extends Event> eventType = event.getEventType();
do {
event = dispatchCapturingEvent(eventType, event);
eventType = eventType.getSuperType();
} while (eventType != null);
return event;
}
@Override
public final Event dispatchBubblingEvent(Event event) {
EventType<? extends Event> eventType = event.getEventType();
do {
event = dispatchBubblingEvent(eventType, event);
eventType = eventType.getSuperType();
} while (eventType != null);
return event;
}
private <T extends Event> CompositeEventHandler<T>
createGetCompositeEventHandler(final EventType<T> eventType) {
CompositeEventHandler<T> compositeEventHandler =
(CompositeEventHandler<T>) eventHandlerMap.get(eventType);
if (compositeEventHandler == null) {
compositeEventHandler = new CompositeEventHandler<T>();
eventHandlerMap.put(eventType, compositeEventHandler);
}
return compositeEventHandler;
}
protected Object getEventSource() {
return eventSource;
}
private Event dispatchCapturingEvent(
final EventType<? extends Event> handlerType, Event event) {
final CompositeEventHandler<? extends Event> compositeEventHandler =
eventHandlerMap.get(handlerType);
if (compositeEventHandler != null && compositeEventHandler.hasFilter()) {
event = fixEventSource(event, eventSource);
compositeEventHandler.dispatchCapturingEvent(event);
}
return event;
}
private Event dispatchBubblingEvent(
final EventType<? extends Event> handlerType, Event event) {
final CompositeEventHandler<? extends Event> compositeEventHandler =
eventHandlerMap.get(handlerType);
if (compositeEventHandler != null && compositeEventHandler.hasHandler()) {
event = fixEventSource(event, eventSource);
compositeEventHandler.dispatchBubblingEvent(event);
}
return event;
}
private static Event fixEventSource(final Event event,
final Object eventSource) {
return (event.getSource() != eventSource)
? event.copyFor(eventSource, event.getTarget())
: event;
}
private static void validateEventType(final EventType<?> eventType) {
if (eventType == null) {
throw new NullPointerException("Event type must not be null");
}
}
private static void validateEventHandler(
final EventHandler<?> eventHandler) {
if (eventHandler == null) {
throw new NullPointerException("Event handler must not be null");
}
}
private static void validateEventFilter(
final EventHandler<?> eventFilter) {
if (eventFilter == null) {
throw new NullPointerException("Event filter must not be null");
}
}
}
