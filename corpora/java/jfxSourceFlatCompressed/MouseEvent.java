package javafx.scene.input;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import com.sun.javafx.scene.input.InputEventUtils;
import java.io.IOException;
public class MouseEvent extends InputEvent {
private static final long serialVersionUID = 20121107L;
public static final EventType<MouseEvent> ANY =
new EventType<MouseEvent>(InputEvent.ANY, "MOUSE");
public static final EventType<MouseEvent> MOUSE_PRESSED =
new EventType<MouseEvent>(MouseEvent.ANY, "MOUSE_PRESSED");
public static final EventType<MouseEvent> MOUSE_RELEASED =
new EventType<MouseEvent>(MouseEvent.ANY, "MOUSE_RELEASED");
public static final EventType<MouseEvent> MOUSE_CLICKED =
new EventType<MouseEvent>(MouseEvent.ANY, "MOUSE_CLICKED");
public static final EventType<MouseEvent> MOUSE_ENTERED_TARGET =
new EventType<MouseEvent>(MouseEvent.ANY, "MOUSE_ENTERED_TARGET");
public static final EventType<MouseEvent> MOUSE_ENTERED =
new EventType<MouseEvent>(MouseEvent.MOUSE_ENTERED_TARGET, "MOUSE_ENTERED");
public static final EventType<MouseEvent> MOUSE_EXITED_TARGET =
new EventType<MouseEvent>(MouseEvent.ANY, "MOUSE_EXITED_TARGET");
public static final EventType<MouseEvent> MOUSE_EXITED =
new EventType<MouseEvent>(MouseEvent.MOUSE_EXITED_TARGET, "MOUSE_EXITED");
public static final EventType<MouseEvent> MOUSE_MOVED =
new EventType<MouseEvent>(MouseEvent.ANY, "MOUSE_MOVED");
public static final EventType<MouseEvent> MOUSE_DRAGGED =
new EventType<MouseEvent>(MouseEvent.ANY, "MOUSE_DRAGGED");
public static final EventType<MouseEvent> DRAG_DETECTED =
new EventType<MouseEvent>(MouseEvent.ANY, "DRAG_DETECTED");
void recomputeCoordinatesToSource(MouseEvent oldEvent, Object newSource) {
final Point3D newCoordinates = InputEventUtils.recomputeCoordinates(
pickResult, newSource);
x = newCoordinates.getX();
y = newCoordinates.getY();
z = newCoordinates.getZ();
}
@Override
public EventType<? extends MouseEvent> getEventType() {
return (EventType<? extends MouseEvent>) super.getEventType();
}
@Override
public MouseEvent copyFor(Object newSource, EventTarget newTarget) {
MouseEvent e = (MouseEvent) super.copyFor(newSource, newTarget);
e.recomputeCoordinatesToSource(this, newSource);
return e;
}
public MouseEvent copyFor(Object newSource, EventTarget newTarget, EventType<? extends MouseEvent> eventType) {
MouseEvent e = copyFor(newSource, newTarget);
e.eventType = eventType;
return e;
}
public MouseEvent(
@NamedArg("eventType") EventType<? extends MouseEvent> eventType,
@NamedArg("x") double x, @NamedArg("y") double y,
@NamedArg("screenX") double screenX, @NamedArg("screenY") double screenY,
@NamedArg("button") MouseButton button,
@NamedArg("clickCount") int clickCount,
@NamedArg("shiftDown") boolean shiftDown,
@NamedArg("controlDown") boolean controlDown,
@NamedArg("altDown") boolean altDown,
@NamedArg("metaDown") boolean metaDown,
@NamedArg("primaryButtonDown") boolean primaryButtonDown,
@NamedArg("middleButtonDown") boolean middleButtonDown,
@NamedArg("secondaryButtonDown") boolean secondaryButtonDown,
@NamedArg("synthesized") boolean synthesized,
@NamedArg("popupTrigger") boolean popupTrigger,
@NamedArg("stillSincePress") boolean stillSincePress,
@NamedArg("pickResult") PickResult pickResult) {
this(null, null, eventType, x, y, screenX, screenY, button, clickCount,
shiftDown, controlDown, altDown, metaDown,
primaryButtonDown, middleButtonDown, secondaryButtonDown,
synthesized, popupTrigger, stillSincePress, pickResult);
}
public MouseEvent(
@NamedArg("eventType") EventType<? extends MouseEvent> eventType,
@NamedArg("x") double x, @NamedArg("y") double y,
@NamedArg("screenX") double screenX, @NamedArg("screenY") double screenY,
@NamedArg("button") MouseButton button,
@NamedArg("clickCount") int clickCount,
@NamedArg("shiftDown") boolean shiftDown,
@NamedArg("controlDown") boolean controlDown,
@NamedArg("altDown") boolean altDown,
@NamedArg("metaDown") boolean metaDown,
@NamedArg("primaryButtonDown") boolean primaryButtonDown,
@NamedArg("middleButtonDown") boolean middleButtonDown,
@NamedArg("secondaryButtonDown") boolean secondaryButtonDown,
@NamedArg("backButtonDown") boolean backButtonDown,
@NamedArg("forwardButtonDown") boolean forwardButtonDown,
@NamedArg("synthesized") boolean synthesized,
@NamedArg("popupTrigger") boolean popupTrigger,
@NamedArg("stillSincePress") boolean stillSincePress,
@NamedArg("pickResult") PickResult pickResult) {
this(null, null, eventType, x, y, screenX, screenY, button, clickCount,
shiftDown, controlDown, altDown, metaDown,
primaryButtonDown, middleButtonDown, secondaryButtonDown,
backButtonDown, forwardButtonDown,
synthesized, popupTrigger, stillSincePress, pickResult);
}
public MouseEvent(@NamedArg("source") Object source, @NamedArg("target") EventTarget target,
@NamedArg("eventType") EventType<? extends MouseEvent> eventType,
@NamedArg("x") double x, @NamedArg("y") double y,
@NamedArg("screenX") double screenX, @NamedArg("screenY") double screenY,
@NamedArg("button") MouseButton button,
@NamedArg("clickCount") int clickCount,
@NamedArg("shiftDown") boolean shiftDown,
@NamedArg("controlDown") boolean controlDown,
@NamedArg("altDown") boolean altDown,
@NamedArg("metaDown") boolean metaDown,
@NamedArg("primaryButtonDown") boolean primaryButtonDown,
@NamedArg("middleButtonDown") boolean middleButtonDown,
@NamedArg("secondaryButtonDown") boolean secondaryButtonDown,
@NamedArg("synthesized") boolean synthesized,
@NamedArg("popupTrigger") boolean popupTrigger,
@NamedArg("stillSincePress") boolean stillSincePress,
@NamedArg("pickResult") PickResult pickResult) {
this(source, target, eventType, x, y, screenX, screenY, button, clickCount,
shiftDown, controlDown, altDown, metaDown,
primaryButtonDown, middleButtonDown, secondaryButtonDown, false, false,
synthesized, popupTrigger, stillSincePress, pickResult);
}
public MouseEvent(@NamedArg("source") Object source, @NamedArg("target") EventTarget target,
@NamedArg("eventType") EventType<? extends MouseEvent> eventType,
@NamedArg("x") double x, @NamedArg("y") double y,
@NamedArg("screenX") double screenX, @NamedArg("screenY") double screenY,
@NamedArg("button") MouseButton button,
@NamedArg("clickCount") int clickCount,
@NamedArg("shiftDown") boolean shiftDown,
@NamedArg("controlDown") boolean controlDown,
@NamedArg("altDown") boolean altDown,
@NamedArg("metaDown") boolean metaDown,
@NamedArg("primaryButtonDown") boolean primaryButtonDown,
@NamedArg("middleButtonDown") boolean middleButtonDown,
@NamedArg("secondaryButtonDown") boolean secondaryButtonDown,
@NamedArg("backButtonDown") boolean backButtonDown,
@NamedArg("forwardButtonDown") boolean forwardButtonDown,
@NamedArg("synthesized") boolean synthesized,
@NamedArg("popupTrigger") boolean popupTrigger,
@NamedArg("stillSincePress") boolean stillSincePress,
@NamedArg("pickResult") PickResult pickResult) {
super(source, target, eventType);
this.x = x;
this.y = y;
this.screenX = screenX;
this.screenY = screenY;
this.sceneX = x;
this.sceneY = y;
this.button = button;
this.clickCount = clickCount;
this.shiftDown = shiftDown;
this.controlDown = controlDown;
this.altDown = altDown;
this.metaDown = metaDown;
this.primaryButtonDown = primaryButtonDown;
this.middleButtonDown = middleButtonDown;
this.secondaryButtonDown = secondaryButtonDown;
this.backButtonDown = backButtonDown;
this.forwardButtonDown = forwardButtonDown;
this.synthesized = synthesized;
this.stillSincePress = stillSincePress;
this.popupTrigger = popupTrigger;
this.pickResult = pickResult;
this.pickResult = pickResult != null ? pickResult : new PickResult(target, x, y);
final Point3D p = InputEventUtils.recomputeCoordinates(this.pickResult, null);
this.x = p.getX();
this.y = p.getY();
this.z = p.getZ();
}
public static MouseDragEvent copyForMouseDragEvent(
MouseEvent e,
Object source, EventTarget target,
EventType<MouseDragEvent> type,
Object gestureSource, PickResult pickResult) {
MouseDragEvent ev = new MouseDragEvent(source, target,
type, e.sceneX, e.sceneY, e.screenX, e.screenY,
e.button, e.clickCount, e.shiftDown, e.controlDown,
e.altDown, e.metaDown, e.primaryButtonDown, e.middleButtonDown,
e.secondaryButtonDown, e.backButtonDown, e.forwardButtonDown, e.synthesized, e.popupTrigger,
pickResult, gestureSource);
ev.recomputeCoordinatesToSource(e, source);
return ev;
}
private final Flags flags = new Flags();
public boolean isDragDetect() {
return flags.dragDetect;
}
public void setDragDetect(boolean dragDetect) {
flags.dragDetect = dragDetect;
}
private transient double x;
public final double getX() {
return x;
}
private transient double y;
public final double getY() {
return y;
}
private transient double z;
public final double getZ() {
return z;
}
private final double screenX;
public final double getScreenX() {
return screenX;
}
private final double screenY;
public final double getScreenY() {
return screenY;
}
private final double sceneX;
public final double getSceneX() {
return sceneX;
}
private final double sceneY;
public final double getSceneY() {
return sceneY;
}
private final MouseButton button;
public final MouseButton getButton() {
return button;
}
private final int clickCount;
public final int getClickCount() {
return clickCount;
}
private final boolean stillSincePress;
public final boolean isStillSincePress() {
return stillSincePress;
}
private final boolean shiftDown;
public final boolean isShiftDown() {
return shiftDown;
}
private final boolean controlDown;
public final boolean isControlDown() {
return controlDown;
}
private final boolean altDown;
public final boolean isAltDown() {
return altDown;
}
private final boolean metaDown;
public final boolean isMetaDown() {
return metaDown;
}
private final boolean synthesized;
public boolean isSynthesized() {
return synthesized;
}
public final boolean isShortcutDown() {
switch (Toolkit.getToolkit().getPlatformShortcutKey()) {
case SHIFT:
return shiftDown;
case CONTROL:
return controlDown;
case ALT:
return altDown;
case META:
return metaDown;
default:
return false;
}
}
private final boolean popupTrigger;
public final boolean isPopupTrigger() {
return popupTrigger;
}
private final boolean primaryButtonDown;
public final boolean isPrimaryButtonDown() {
return primaryButtonDown;
}
private final boolean secondaryButtonDown;
public final boolean isSecondaryButtonDown() {
return secondaryButtonDown;
}
private final boolean middleButtonDown;
public final boolean isMiddleButtonDown() {
return middleButtonDown;
}
private final boolean backButtonDown;
public final boolean isBackButtonDown() {
return backButtonDown;
}
private final boolean forwardButtonDown;
public final boolean isForwardButtonDown() {
return forwardButtonDown;
}
@Override public String toString() {
final StringBuilder sb = new StringBuilder("MouseEvent [");
sb.append("source = ").append(getSource());
sb.append(", target = ").append(getTarget());
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
private PickResult pickResult;
public final PickResult getPickResult() {
return pickResult;
}
private static class Flags implements Cloneable {
boolean dragDetect = true;
@Override
public Flags clone() {
try {
return (Flags) super.clone();
} catch (CloneNotSupportedException e) {
return null;
}
}
}
private void readObject(java.io.ObjectInputStream in)
throws IOException, ClassNotFoundException {
in.defaultReadObject();
x = sceneX;
y = sceneY;
}
}
