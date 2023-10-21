package javafx.scene.input;
import com.sun.javafx.scene.input.InputEventUtils;
import java.io.IOException;
import javafx.beans.NamedArg;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Point3D;
import javafx.scene.Node;
public class ContextMenuEvent extends InputEvent {
private static final long serialVersionUID = 20121107L;
public static final EventType<ContextMenuEvent> CONTEXT_MENU_REQUESTED =
new EventType<ContextMenuEvent>(InputEvent.ANY, "CONTEXTMENUREQUESTED");
public static final EventType<ContextMenuEvent> ANY = CONTEXT_MENU_REQUESTED;
public ContextMenuEvent(@NamedArg("source") Object source, @NamedArg("target") EventTarget target, @NamedArg("eventType") EventType<ContextMenuEvent> eventType, @NamedArg("x") double x, @NamedArg("y") double y,
@NamedArg("screenX") double screenX, @NamedArg("screenY") double screenY, @NamedArg("keyboardTrigger") boolean keyboardTrigger,
@NamedArg("pickResult") PickResult pickResult) {
super(source, target, eventType);
this.screenX = screenX;
this.screenY = screenY;
this.sceneX = x;
this.sceneY = y;
this.x = x;
this.y = y;
this.pickResult = pickResult != null ? pickResult : new PickResult(target, x, y);
final Point3D p = InputEventUtils.recomputeCoordinates(this.pickResult, null);
this.x = p.getX();
this.y = p.getY();
this.z = p.getZ();
this.keyboardTrigger = keyboardTrigger;
}
public ContextMenuEvent(@NamedArg("eventType") EventType<ContextMenuEvent> eventType, @NamedArg("x") double x, @NamedArg("y") double y,
@NamedArg("screenX") double screenX, @NamedArg("screenY") double screenY, @NamedArg("keyboardTrigger") boolean keyboardTrigger,
@NamedArg("pickResult") PickResult pickResult) {
this(null, null, eventType, x, y, screenX, screenY, keyboardTrigger,
pickResult);
}
private void recomputeCoordinatesToSource(ContextMenuEvent newEvent, Object newSource) {
final Point3D newCoordinates = InputEventUtils.recomputeCoordinates(
pickResult, newSource);
newEvent.x = newCoordinates.getX();
newEvent.y = newCoordinates.getY();
newEvent.z = newCoordinates.getZ();
}
@Override
public ContextMenuEvent copyFor(Object newSource, EventTarget newTarget) {
ContextMenuEvent e = (ContextMenuEvent) super.copyFor(newSource, newTarget);
recomputeCoordinatesToSource(e, newSource);
return e;
}
@Override
public EventType<ContextMenuEvent> getEventType() {
return (EventType<ContextMenuEvent>) super.getEventType();
}
private final boolean keyboardTrigger;
public boolean isKeyboardTrigger() {
return keyboardTrigger;
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
private PickResult pickResult;
public final PickResult getPickResult() {
return pickResult;
}
@Override public String toString() {
final StringBuilder sb = new StringBuilder("ContextMenuEvent [");
sb.append("source = ").append(getSource());
sb.append(", target = ").append(getTarget());
sb.append(", eventType = ").append(getEventType());
sb.append(", consumed = ").append(isConsumed());
sb.append(", x = ").append(getX()).append(", y = ").append(getY())
.append(", z = ").append(getZ());
sb.append(", pickResult = ").append(getPickResult());
return sb.append("]").toString();
}
private void readObject(java.io.ObjectInputStream in)
throws IOException, ClassNotFoundException {
in.defaultReadObject();
x = sceneX;
y = sceneY;
}
}
