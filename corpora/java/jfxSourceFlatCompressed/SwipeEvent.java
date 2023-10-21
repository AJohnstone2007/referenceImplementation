package javafx.scene.input;
import javafx.beans.NamedArg;
import javafx.event.EventTarget;
import javafx.event.EventType;
public final class SwipeEvent extends GestureEvent {
private static final long serialVersionUID = 20121107L;
public static final EventType<SwipeEvent> ANY =
new EventType<SwipeEvent>(GestureEvent.ANY, "ANY_SWIPE");
public static final EventType<SwipeEvent> SWIPE_LEFT =
new EventType<SwipeEvent>(SwipeEvent.ANY, "SWIPE_LEFT");
public static final EventType<SwipeEvent> SWIPE_RIGHT =
new EventType<SwipeEvent>(SwipeEvent.ANY, "SWIPE_RIGHT");
public static final EventType<SwipeEvent> SWIPE_UP =
new EventType<SwipeEvent>(SwipeEvent.ANY, "SWIPE_UP");
public static final EventType<SwipeEvent> SWIPE_DOWN =
new EventType<SwipeEvent>(SwipeEvent.ANY, "SWIPE_DOWN");
public SwipeEvent(@NamedArg("source") Object source, @NamedArg("target") EventTarget target,
final @NamedArg("eventType") EventType<SwipeEvent> eventType,
@NamedArg("x") double x, @NamedArg("y") double y,
@NamedArg("screenX") double screenX, @NamedArg("screenY") double screenY,
@NamedArg("shiftDown") boolean shiftDown,
@NamedArg("controlDown") boolean controlDown,
@NamedArg("altDown") boolean altDown,
@NamedArg("metaDown") boolean metaDown,
@NamedArg("direct") boolean direct,
@NamedArg("touchCount") int touchCount,
@NamedArg("pickResult") PickResult pickResult) {
super(source, target, eventType, x, y, screenX, screenY,
shiftDown, controlDown, altDown, metaDown, direct, false,
pickResult);
this.touchCount = touchCount;
}
public SwipeEvent(final @NamedArg("eventType") EventType<SwipeEvent> eventType,
@NamedArg("x") double x, @NamedArg("y") double y,
@NamedArg("screenX") double screenX, @NamedArg("screenY") double screenY,
@NamedArg("shiftDown") boolean shiftDown,
@NamedArg("controlDown") boolean controlDown,
@NamedArg("altDown") boolean altDown,
@NamedArg("metaDown") boolean metaDown,
@NamedArg("direct") boolean direct,
@NamedArg("touchCount") int touchCount,
@NamedArg("pickResult") PickResult pickResult) {
this(null, null, eventType, x, y, screenX, screenY, shiftDown, controlDown,
altDown, metaDown, direct, touchCount, pickResult);
}
private final int touchCount;
public int getTouchCount() {
return touchCount;
}
@Override public String toString() {
final StringBuilder sb = new StringBuilder("SwipeEvent [");
sb.append("source = ").append(getSource());
sb.append(", target = ").append(getTarget());
sb.append(", eventType = ").append(getEventType());
sb.append(", consumed = ").append(isConsumed());
sb.append(", touchCount = ").append(getTouchCount());
sb.append(", x = ").append(getX()).append(", y = ").append(getY())
.append(", z = ").append(getZ());
sb.append(isDirect() ? ", direct" : ", indirect");
if (isShiftDown()) {
sb.append(", shiftDown");
}
if (isControlDown()) {
sb.append(", controlDown");
}
if (isAltDown()) {
sb.append(", altDown");
}
if (isMetaDown()) {
sb.append(", metaDown");
}
if (isShortcutDown()) {
sb.append(", shortcutDown");
}
sb.append(", pickResult = ").append(getPickResult());
return sb.append("]").toString();
}
@Override
public SwipeEvent copyFor(Object newSource, EventTarget newTarget) {
return (SwipeEvent) super.copyFor(newSource, newTarget);
}
public SwipeEvent copyFor(Object newSource, EventTarget newTarget, EventType<SwipeEvent> type) {
SwipeEvent e = copyFor(newSource, newTarget);
e.eventType = type;
return e;
}
@Override
public EventType<SwipeEvent> getEventType() {
return (EventType<SwipeEvent>) super.getEventType();
}
}
