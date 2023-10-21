package javafx.scene.input;
import javafx.beans.NamedArg;
import javafx.event.EventTarget;
import javafx.event.EventType;
public final class RotateEvent extends GestureEvent {
private static final long serialVersionUID = 20121107L;
public static final EventType<RotateEvent> ANY =
new EventType<RotateEvent>(GestureEvent.ANY, "ANY_ROTATE");
public static final EventType<RotateEvent> ROTATE =
new EventType<RotateEvent>(RotateEvent.ANY, "ROTATE");
public static final EventType<RotateEvent> ROTATION_STARTED =
new EventType<RotateEvent>(RotateEvent.ANY, "ROTATION_STARTED");
public static final EventType<RotateEvent> ROTATION_FINISHED =
new EventType<RotateEvent>(RotateEvent.ANY, "ROTATION_FINISHED");
public RotateEvent(@NamedArg("source") Object source, @NamedArg("target") EventTarget target,
final @NamedArg("eventType") EventType<RotateEvent> eventType,
@NamedArg("x") double x, @NamedArg("y") double y,
@NamedArg("screenX") double screenX, @NamedArg("screenY") double screenY,
@NamedArg("shiftDown") boolean shiftDown,
@NamedArg("controlDown") boolean controlDown,
@NamedArg("altDown") boolean altDown,
@NamedArg("metaDown") boolean metaDown,
@NamedArg("direct") boolean direct,
@NamedArg("inertia") boolean inertia, @NamedArg("angle") double angle, @NamedArg("totalAngle") double totalAngle,
@NamedArg("pickResult") PickResult pickResult) {
super(source, target, eventType, x, y, screenX, screenY,
shiftDown, controlDown, altDown, metaDown, direct, inertia,
pickResult);
this.angle = angle;
this.totalAngle = totalAngle;
}
public RotateEvent(final @NamedArg("eventType") EventType<RotateEvent> eventType,
@NamedArg("x") double x, @NamedArg("y") double y,
@NamedArg("screenX") double screenX, @NamedArg("screenY") double screenY,
@NamedArg("shiftDown") boolean shiftDown,
@NamedArg("controlDown") boolean controlDown,
@NamedArg("altDown") boolean altDown,
@NamedArg("metaDown") boolean metaDown,
@NamedArg("direct") boolean direct,
@NamedArg("inertia") boolean inertia, @NamedArg("angle") double angle, @NamedArg("totalAngle") double totalAngle,
@NamedArg("pickResult") PickResult pickResult) {
this(null, null, eventType, x, y, screenX, screenY, shiftDown, controlDown,
altDown, metaDown, direct, inertia, angle, totalAngle, pickResult);
}
private final double angle;
public double getAngle() {
return angle;
}
private final double totalAngle;
public double getTotalAngle() {
return totalAngle;
}
@Override public String toString() {
final StringBuilder sb = new StringBuilder("RotateEvent [");
sb.append("source = ").append(getSource());
sb.append(", target = ").append(getTarget());
sb.append(", eventType = ").append(getEventType());
sb.append(", consumed = ").append(isConsumed());
sb.append(", angle = ").append(getAngle());
sb.append(", totalAngle = ").append(getTotalAngle());
sb.append(", x = ").append(getX()).append(", y = ").append(getY())
.append(", z = ").append(getZ());
sb.append(isDirect() ? ", direct" : ", indirect");
if (isInertia()) {
sb.append(", inertia");
}
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
public RotateEvent copyFor(Object newSource, EventTarget newTarget) {
return (RotateEvent) super.copyFor(newSource, newTarget);
}
public RotateEvent copyFor(Object newSource, EventTarget newTarget, EventType<RotateEvent> type) {
RotateEvent e = copyFor(newSource, newTarget);
e.eventType = type;
return e;
}
@Override
public EventType<RotateEvent> getEventType() {
return (EventType<RotateEvent>) super.getEventType();
}
}
