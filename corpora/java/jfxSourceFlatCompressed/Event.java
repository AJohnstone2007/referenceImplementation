package javafx.event;
import java.util.EventObject;
import com.sun.javafx.event.EventUtil;
import java.io.IOException;
import javafx.beans.NamedArg;
public class Event extends EventObject implements Cloneable {
private static final long serialVersionUID = 20121107L;
public static final EventTarget NULL_SOURCE_TARGET = tail -> tail;
public static final EventType<Event> ANY = EventType.ROOT;
protected EventType<? extends Event> eventType;
protected transient EventTarget target;
protected boolean consumed;
public Event(final @NamedArg("eventType") EventType<? extends Event> eventType) {
this(null, null, eventType);
}
public Event(final @NamedArg("source") Object source,
final @NamedArg("target") EventTarget target,
final @NamedArg("eventType") EventType<? extends Event> eventType) {
super((source != null) ? source : NULL_SOURCE_TARGET);
this.target = (target != null) ? target : NULL_SOURCE_TARGET;
this.eventType = eventType;
}
public EventTarget getTarget() {
return target;
}
public EventType<? extends Event> getEventType() {
return eventType;
}
public Event copyFor(final Object newSource, final EventTarget newTarget) {
final Event newEvent = (Event) clone();
newEvent.source = (newSource != null) ? newSource : NULL_SOURCE_TARGET;
newEvent.target = (newTarget != null) ? newTarget : NULL_SOURCE_TARGET;
newEvent.consumed = false;
return newEvent;
}
public boolean isConsumed() {
return consumed;
}
public void consume() {
consumed = true;
}
@Override
public Object clone() {
try {
return super.clone();
} catch (final CloneNotSupportedException e) {
throw new RuntimeException("Can't clone Event");
}
}
private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
in.defaultReadObject();
source = NULL_SOURCE_TARGET;
target = NULL_SOURCE_TARGET;
}
public static void fireEvent(EventTarget eventTarget, Event event) {
if (eventTarget == null) {
throw new NullPointerException("Event target must not be null!");
}
if (event == null) {
throw new NullPointerException("Event must not be null!");
}
EventUtil.fireEvent(eventTarget, event);
}
}
