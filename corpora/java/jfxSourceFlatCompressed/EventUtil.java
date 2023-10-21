package com.sun.javafx.event;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventTarget;
public final class EventUtil {
private static final EventDispatchChainImpl eventDispatchChain =
new EventDispatchChainImpl();
private static final AtomicBoolean eventDispatchChainInUse =
new AtomicBoolean();
public static Event fireEvent(EventTarget eventTarget, Event event) {
if (event.getTarget() != eventTarget) {
event = event.copyFor(event.getSource(), eventTarget);
}
if (eventDispatchChainInUse.getAndSet(true)) {
return fireEventImpl(new EventDispatchChainImpl(),
eventTarget, event);
}
try {
return fireEventImpl(eventDispatchChain, eventTarget, event);
} finally {
eventDispatchChain.reset();
eventDispatchChainInUse.set(false);
}
}
public static Event fireEvent(Event event, EventTarget... eventTargets) {
return fireEventImpl(new EventDispatchTreeImpl(),
new CompositeEventTargetImpl(eventTargets),
event);
}
private static Event fireEventImpl(EventDispatchChain eventDispatchChain,
EventTarget eventTarget,
Event event) {
final EventDispatchChain targetDispatchChain =
eventTarget.buildEventDispatchChain(eventDispatchChain);
return targetDispatchChain.dispatchEvent(event);
}
}
