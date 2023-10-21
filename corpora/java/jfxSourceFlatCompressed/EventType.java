package javafx.event;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
public final class EventType<T extends Event> implements Serializable{
public static final EventType<Event> ROOT =
new EventType<Event>("EVENT", null);
private WeakHashMap<EventType<? extends T>, Void> subTypes;
private final EventType<? super T> superType;
private final String name;
@Deprecated
public EventType() {
this(ROOT, null);
}
public EventType(final String name) {
this(ROOT, name);
}
public EventType(final EventType<? super T> superType) {
this(superType, null);
}
public EventType(final EventType<? super T> superType,
final String name) {
if (superType == null) {
throw new NullPointerException(
"Event super type must not be null!");
}
this.superType = superType;
this.name = name;
superType.register(this);
}
EventType(final String name,
final EventType<? super T> superType) {
this.superType = superType;
this.name = name;
if (superType != null) {
if (superType.subTypes != null) {
for (Iterator i = superType.subTypes.keySet().iterator(); i.hasNext();) {
EventType t = (EventType) i.next();
if (name == null && t.name == null || (name != null && name.equals(t.name))) {
i.remove();
}
}
}
superType.register(this);
}
}
public final EventType<? super T> getSuperType() {
return superType;
}
public final String getName() {
return name;
}
@Override
public String toString() {
return (name != null) ? name : super.toString();
}
private void register(javafx.event.EventType<? extends T> subType) {
if (subTypes == null) {
subTypes = new WeakHashMap<EventType<? extends T>, Void>();
}
for (EventType<? extends T> t : subTypes.keySet()) {
if (((t.name == null && subType.name == null) || (t.name != null && t.name.equals(subType.name)))) {
throw new IllegalArgumentException("EventType \"" + subType + "\""
+ "with parent \"" + subType.getSuperType()+"\" already exists");
}
}
subTypes.put(subType, null);
}
private Object writeReplace() throws ObjectStreamException {
Deque<String> path = new LinkedList<String>();
EventType<?> t = this;
while (t != ROOT) {
path.addFirst(t.name);
t = t.superType;
}
return new EventTypeSerialization(new ArrayList<String>(path));
}
static class EventTypeSerialization implements Serializable {
private List<String> path;
public EventTypeSerialization(List<String> path) {
this.path = path;
}
private Object readResolve() throws ObjectStreamException {
EventType t = ROOT;
for (int i = 0; i < path.size(); ++i) {
String p = path.get(i);
if (t.subTypes != null) {
EventType s = findSubType(t.subTypes.keySet(), p);
if (s == null) {
throw new InvalidObjectException("Cannot find event type \"" + p + "\" (of " + t + ")");
}
t = s;
} else {
throw new InvalidObjectException("Cannot find event type \"" + p + "\" (of " + t + ")");
}
}
return t;
}
private EventType findSubType(Set<EventType> subTypes, String name) {
for (EventType t : subTypes) {
if (((t.name == null && name == null) || (t.name != null && t.name.equals(name)))) {
return t;
}
}
return null;
}
}
}
