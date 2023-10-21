package javafx.scene.input;
import javafx.beans.NamedArg;
import javafx.event.EventTarget;
import javafx.event.EventType;
public final class MouseDragEvent extends MouseEvent{
private static final long serialVersionUID = 20121107L;
public static final EventType<MouseDragEvent> ANY =
new EventType<MouseDragEvent>(MouseEvent.ANY, "MOUSE-DRAG");
public static final EventType<MouseDragEvent> MOUSE_DRAG_OVER =
new EventType<MouseDragEvent>(MouseDragEvent.ANY, "MOUSE-DRAG_OVER");
public static final EventType<MouseDragEvent> MOUSE_DRAG_RELEASED =
new EventType<MouseDragEvent>(MouseDragEvent.ANY, "MOUSE-DRAG_RELEASED");
public static final EventType<MouseDragEvent> MOUSE_DRAG_ENTERED_TARGET =
new EventType<MouseDragEvent>(MouseDragEvent.ANY, "MOUSE-DRAG_ENTERED_TARGET");
public static final EventType<MouseDragEvent> MOUSE_DRAG_ENTERED =
new EventType<MouseDragEvent>(MouseDragEvent.MOUSE_DRAG_ENTERED_TARGET,
"MOUSE-DRAG_ENTERED");
public static final EventType<MouseDragEvent> MOUSE_DRAG_EXITED_TARGET =
new EventType<MouseDragEvent>(MouseDragEvent.ANY, "MOUSE-DRAG_EXITED_TARGET");
public static final EventType<MouseDragEvent> MOUSE_DRAG_EXITED =
new EventType<MouseDragEvent>(MouseDragEvent.MOUSE_DRAG_EXITED_TARGET,
"MOUSE-DRAG_EXITED");
public MouseDragEvent(@NamedArg("source") Object source, @NamedArg("target") EventTarget target,
@NamedArg("eventType") EventType<MouseDragEvent> eventType,
@NamedArg("x") double x, @NamedArg("y") double y,
@NamedArg("screenX") double screenX, @NamedArg("screenY") double screenY,
@NamedArg("button") MouseButton button, @NamedArg("clickCount") int clickCount,
@NamedArg("shiftDown") boolean shiftDown, @NamedArg("controlDown") boolean controlDown,
@NamedArg("altDown") boolean altDown, @NamedArg("metaDown") boolean metaDown,
@NamedArg("primaryButtonDown") boolean primaryButtonDown,
@NamedArg("middleButtonDown") boolean middleButtonDown,
@NamedArg("secondaryButtonDown") boolean secondaryButtonDown,
@NamedArg("synthesized") boolean synthesized,
@NamedArg("popupTrigger") boolean popupTrigger,
@NamedArg("pickResult") PickResult pickResult,
@NamedArg("gestureSource") Object gestureSource) {
this(source, target, eventType, x, y, screenX, screenY, button,
clickCount, shiftDown, controlDown, altDown, metaDown,
primaryButtonDown, middleButtonDown, secondaryButtonDown,
false, false,
synthesized, popupTrigger, pickResult, gestureSource);
}
public MouseDragEvent(@NamedArg("source") Object source, @NamedArg("target") EventTarget target,
@NamedArg("eventType") EventType<MouseDragEvent> eventType,
@NamedArg("x") double x, @NamedArg("y") double y,
@NamedArg("screenX") double screenX, @NamedArg("screenY") double screenY,
@NamedArg("button") MouseButton button, @NamedArg("clickCount") int clickCount,
@NamedArg("shiftDown") boolean shiftDown, @NamedArg("controlDown") boolean controlDown,
@NamedArg("altDown") boolean altDown, @NamedArg("metaDown") boolean metaDown,
@NamedArg("primaryButtonDown") boolean primaryButtonDown,
@NamedArg("middleButtonDown") boolean middleButtonDown,
@NamedArg("secondaryButtonDown") boolean secondaryButtonDown,
@NamedArg("backButtonDown") boolean backButtonDown,
@NamedArg("forwardButtonDown") boolean forwardButtonDown,
@NamedArg("synthesized") boolean synthesized,
@NamedArg("popupTrigger") boolean popupTrigger,
@NamedArg("pickResult") PickResult pickResult,
@NamedArg("gestureSource") Object gestureSource) {
super(source, target, eventType, x, y, screenX, screenY, button,
clickCount, shiftDown, controlDown, altDown, metaDown,
primaryButtonDown, middleButtonDown, secondaryButtonDown,
backButtonDown, forwardButtonDown,
synthesized, popupTrigger, false, pickResult);
this.gestureSource = gestureSource;
}
public MouseDragEvent(@NamedArg("eventType") EventType<MouseDragEvent> eventType,
@NamedArg("x") double x, @NamedArg("y") double y,
@NamedArg("screenX") double screenX, @NamedArg("screenY") double screenY,
@NamedArg("button") MouseButton button, @NamedArg("clickCount") int clickCount,
@NamedArg("shiftDown") boolean shiftDown, @NamedArg("controlDown") boolean controlDown,
@NamedArg("altDown") boolean altDown, @NamedArg("metaDown") boolean metaDown,
@NamedArg("primaryButtonDown") boolean primaryButtonDown,
@NamedArg("middleButtonDown") boolean middleButtonDown,
@NamedArg("secondaryButtonDown") boolean secondaryButtonDown,
@NamedArg("synthesized") boolean synthesized, @NamedArg("popupTrigger") boolean popupTrigger,
@NamedArg("pickResult") PickResult pickResult,
@NamedArg("gestureSource") Object gestureSource) {
this(null, null, eventType, x, y, screenX, screenY, button, clickCount,
shiftDown, controlDown, altDown, metaDown, primaryButtonDown,
middleButtonDown, secondaryButtonDown, synthesized, popupTrigger,
pickResult, gestureSource);
}
private final transient Object gestureSource;
public Object getGestureSource() {
return gestureSource;
}
@Override public String toString() {
final StringBuilder sb = new StringBuilder("MouseDragEvent [");
sb.append("source = ").append(getSource());
sb.append(", target = ").append(getTarget());
sb.append(", gestureSource = ").append(getGestureSource());
sb.append(", eventType = ").append(getEventType());
sb.append(", consumed = ").append(isConsumed());
sb.append(", x = ").append(getX()).append(", y = ").append(getY())
.append(", z = ").append(getZ());
if (getButton() != null) {
sb.append(", button = ").append(getButton());
}
if (getClickCount() > 1) {
sb.append(", clickCount = ").append(getClickCount());
}
if (isPrimaryButtonDown()) {
sb.append(", primaryButtonDown");
}
if (isMiddleButtonDown()) {
sb.append(", middleButtonDown");
}
if (isSecondaryButtonDown()) {
sb.append(", secondaryButtonDown");
}
if (isBackButtonDown()) {
sb.append(", backButtonDown");
}
if (isForwardButtonDown()) {
sb.append(", forwardButtonDown");
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
if (isSynthesized()) {
sb.append(", synthesized");
}
sb.append(", pickResult = ").append(getPickResult());
return sb.append("]").toString();
}
@Override
public MouseDragEvent copyFor(Object newSource, EventTarget newTarget) {
return (MouseDragEvent) super.copyFor(newSource, newTarget);
}
@Override
public MouseDragEvent copyFor(Object newSource, EventTarget newTarget, EventType<? extends MouseEvent> type) {
return (MouseDragEvent) super.copyFor(newSource, newTarget, type);
}
@Override
public EventType<MouseDragEvent> getEventType() {
return (EventType<MouseDragEvent>) super.getEventType();
}
}
