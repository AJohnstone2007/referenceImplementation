package test.com.sun.javafx.event;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
public final class PathChangingDispatcher implements EventDispatcher {
private final EventDispatcher prependDispatcher;
private final EventDispatcher appendDispatcher;
private final int dispatchCount;
public PathChangingDispatcher(final EventDispatcher prependDispatcher,
final EventDispatcher appendDispatcher,
final int dispatchCount) {
this.prependDispatcher = prependDispatcher;
this.appendDispatcher = appendDispatcher;
this.dispatchCount = dispatchCount;
}
@Override
public Event dispatchEvent(Event event, EventDispatchChain tail) {
if (prependDispatcher != null) {
tail = tail.prepend(prependDispatcher);
}
if (appendDispatcher != null) {
tail = tail.append(appendDispatcher);
}
for (int i = 0; i < dispatchCount; ++i) {
event = tail.dispatchEvent(event);
}
return event;
}
}
