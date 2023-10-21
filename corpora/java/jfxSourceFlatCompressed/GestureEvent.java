package javafx.scene.input;
import com.sun.javafx.scene.input.InputEventUtils;
import com.sun.javafx.tk.Toolkit;
import java.io.IOException;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Point3D;
public class GestureEvent extends InputEvent {
private static final long serialVersionUID = 20121107L;
public static final EventType<GestureEvent> ANY =
new EventType<GestureEvent>(InputEvent.ANY, "GESTURE");
@Deprecated(since="8")
protected GestureEvent(final EventType<? extends GestureEvent> eventType) {
this(eventType, 0, 0, 0, 0, false, false, false, false, false, false, null);
}
@Deprecated(since="8")
protected GestureEvent(Object source, EventTarget target,
final EventType<? extends GestureEvent> eventType) {
super(source, target, eventType);
x = y = screenX = screenY = sceneX = sceneY = 0;
shiftDown = controlDown = altDown = metaDown = direct = inertia = false;
}
protected GestureEvent(Object source, EventTarget target, final EventType<? extends GestureEvent> eventType,
double x, double y, double screenX, double screenY,
boolean shiftDown, boolean controlDown, boolean altDown,
boolean metaDown, boolean direct, boolean inertia, PickResult pickResult) {
super(source, target, eventType);
this.x = x;
this.y = y;
this.screenX = screenX;
this.screenY = screenY;
this.sceneX = x;
this.sceneY = y;
this.shiftDown = shiftDown;
this.controlDown = controlDown;
this.altDown = altDown;
this.metaDown = metaDown;
this.direct = direct;
this.inertia = inertia;
this.pickResult = pickResult != null ? pickResult : new PickResult(target, x, y);
final Point3D p = InputEventUtils.recomputeCoordinates(this.pickResult, null);
this.x = p.getX();
this.y = p.getY();
this.z = p.getZ();
}
protected GestureEvent(final EventType<? extends GestureEvent> eventType,
double x, double y, double screenX, double screenY,
boolean shiftDown, boolean controlDown, boolean altDown,
boolean metaDown, boolean direct, boolean inertia,
PickResult pickResult) {
this(null, null, eventType, x, y, screenX, screenY, shiftDown, controlDown,
altDown, metaDown, direct, inertia, pickResult);
}
private void recomputeCoordinatesToSource(GestureEvent newEvent, Object newSource) {
final Point3D newCoordinates = InputEventUtils.recomputeCoordinates(
pickResult, newSource);
newEvent.x = newCoordinates.getX();
newEvent.y = newCoordinates.getY();
newEvent.z = newCoordinates.getZ();
}
@Override
public GestureEvent copyFor(Object newSource, EventTarget newTarget) {
GestureEvent e = (GestureEvent) super.copyFor(newSource, newTarget);
recomputeCoordinatesToSource(e, newSource);
return e;
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
private final boolean direct;
public final boolean isDirect() {
return direct;
}
private final boolean inertia;
public boolean isInertia() {
return inertia;
}
private PickResult pickResult;
public final PickResult getPickResult() {
return pickResult;
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
@Override public String toString() {
final StringBuilder sb = new StringBuilder("GestureEvent [");
sb.append("source = ").append(getSource());
sb.append(", target = ").append(getTarget());
sb.append(", eventType = ").append(getEventType());
sb.append(", consumed = ").append(isConsumed());
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
private void readObject(java.io.ObjectInputStream in)
throws IOException, ClassNotFoundException {
in.defaultReadObject();
x = sceneX;
y = sceneY;
}
@Override
public EventType<? extends GestureEvent> getEventType() {
return (EventType<? extends GestureEvent>) super.getEventType();
}
}
