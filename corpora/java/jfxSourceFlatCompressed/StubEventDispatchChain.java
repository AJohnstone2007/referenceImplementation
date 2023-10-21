package test.com.sun.javafx.event;
import java.util.LinkedList;
import java.util.List;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
public final class StubEventDispatchChain implements EventDispatchChain {
private final LinkedList<EventDispatcher> eventDispatchers;
public static final EventDispatchChain EMPTY_CHAIN =
new StubEventDispatchChain();
private StubEventDispatchChain() {
this(new LinkedList<EventDispatcher>());
}
private StubEventDispatchChain(
final LinkedList<EventDispatcher> eventDispatchers) {
this.eventDispatchers = eventDispatchers;
}
@Override
public EventDispatchChain append(final EventDispatcher eventDispatcher) {
final LinkedList<EventDispatcher> newDispatchers =
copyDispatchers(eventDispatchers);
newDispatchers.addLast(eventDispatcher);
return new StubEventDispatchChain(newDispatchers);
}
@Override
public EventDispatchChain prepend(final EventDispatcher eventDispatcher) {
final LinkedList<EventDispatcher> newDispatchers =
copyDispatchers(eventDispatchers);
newDispatchers.addFirst(eventDispatcher);
return new StubEventDispatchChain(newDispatchers);
}
@Override
public Event dispatchEvent(final Event event) {
if (eventDispatchers.isEmpty()) {
return event;
}
final LinkedList<EventDispatcher> tailDispatchers =
copyDispatchers(eventDispatchers.subList(
1, eventDispatchers.size()));
return eventDispatchers.element().dispatchEvent(
event, new StubEventDispatchChain(tailDispatchers));
}
private static LinkedList<EventDispatcher> copyDispatchers(
final List<EventDispatcher> dispatchers) {
return new LinkedList<EventDispatcher>(dispatchers);
}
}
