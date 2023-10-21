package javafx.scene.input;
import javafx.beans.NamedArg;
import javafx.event.EventTarget;
import javafx.event.EventType;
public final class ZoomEvent extends GestureEvent {
private static final long serialVersionUID = 20121107L;
public static final EventType<ZoomEvent> ANY =
new EventType<ZoomEvent>(GestureEvent.ANY, "ANY_ZOOM");
public static final EventType<ZoomEvent> ZOOM =
new EventType<ZoomEvent>(ZoomEvent.ANY, "ZOOM");
public static final EventType<ZoomEvent> ZOOM_STARTED =
new EventType<ZoomEvent>(ZoomEvent.ANY, "ZOOM_STARTED");
public static final EventType<ZoomEvent> ZOOM_FINISHED =
new EventType<ZoomEvent>(ZoomEvent.ANY, "ZOOM_FINISHED");
public ZoomEvent(@NamedArg("source") Object source, @NamedArg("target") EventTarget target, final @NamedArg("eventType") EventType<ZoomEvent> eventType,
@NamedArg("x") double x, @NamedArg("y") double y,
@NamedArg("screenX") double screenX, @NamedArg("screenY") double screenY,
@NamedArg("shiftDown") boolean shiftDown,
@NamedArg("controlDown") boolean controlDown,
@NamedArg("altDown") boolean altDown,
@NamedArg("metaDown") boolean metaDown,
@NamedArg("direct") boolean direct,
@NamedArg("inertia") boolean inertia,
@NamedArg("zoomFactor") double zoomFactor,
@NamedArg("totalZoomFactor") double totalZoomFactor,
@NamedArg("pickResult") PickResult pickResult) {
super(source, target, eventType, x, y, screenX, screenY,
shiftDown, controlDown, altDown, metaDown, direct, inertia, pickResult);
this.zoomFactor = zoomFactor;
this.totalZoomFactor = totalZoomFactor;
}
public ZoomEvent(final @NamedArg("eventType") EventType<ZoomEvent> eventType,
@NamedArg("x") double x, @NamedArg("y") double y,
@NamedArg("screenX") double screenX, @NamedArg("screenY") double screenY,
@NamedArg("shiftDown") boolean shiftDown,
@NamedArg("controlDown") boolean controlDown,
@NamedArg("altDown") boolean altDown,
@NamedArg("metaDown") boolean metaDown,
@NamedArg("direct") boolean direct,
@NamedArg("inertia") boolean inertia,
@NamedArg("zoomFactor") double zoomFactor,
@NamedArg("totalZoomFactor") double totalZoomFactor,
@NamedArg("pickResult") PickResult pickResult) {
this(null, null, eventType, x, y, screenX, screenY, shiftDown, controlDown,
altDown, metaDown, direct, inertia, zoomFactor, totalZoomFactor,
pickResult);
}
private final double zoomFactor;
public double getZoomFactor() {
return zoomFactor;
}
private final double totalZoomFactor;
public double getTotalZoomFactor() {
return totalZoomFactor;
}
@Override public String toString() {
final StringBuilder sb = new StringBuilder("ZoomEvent [");
sb.append("source = ").append(getSource());
sb.append(", target = ").append(getTarget());
sb.append(", eventType = ").append(getEventType());
sb.append(", consumed = ").append(isConsumed());
sb.append(", zoomFactor = ").append(getZoomFactor());
sb.append(", totalZoomFactor = ").append(getTotalZoomFactor());
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
public ZoomEvent copyFor(Object newSource, EventTarget newTarget) {
return (ZoomEvent) super.copyFor(newSource, newTarget);
}
public ZoomEvent copyFor(Object newSource, EventTarget newTarget, EventType<ZoomEvent> type) {
ZoomEvent e = copyFor(newSource, newTarget);
e.eventType = type;
return e;
}
@Override
public EventType<ZoomEvent> getEventType() {
return (EventType<ZoomEvent>) super.getEventType();
}
}
