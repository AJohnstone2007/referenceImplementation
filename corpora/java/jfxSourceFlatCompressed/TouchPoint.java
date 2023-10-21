package javafx.scene.input;
import com.sun.javafx.scene.input.InputEventUtils;
import com.sun.javafx.scene.input.TouchPointHelper;
import java.io.IOException;
import java.io.Serializable;
import javafx.beans.NamedArg;
import javafx.event.EventTarget;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.Scene;
public final class TouchPoint implements Serializable{
static {
TouchPointHelper.setTouchPointAccessor(new TouchPointHelper.TouchPointAccessor() {
@Override
public void reset(TouchPoint touchPoint) {
touchPoint.reset();
}
});
}
private transient EventTarget target;
private transient Object source;
public TouchPoint(@NamedArg("id") int id, @NamedArg("state") State state, @NamedArg("x") double x, @NamedArg("y") double y, @NamedArg("screenX") double screenX,
@NamedArg("screenY") double screenY, @NamedArg("target") EventTarget target, @NamedArg("pickResult") PickResult pickResult) {
this.target = target;
this.id = id;
this.state = state;
this.x = x;
this.y = y;
this.sceneX = x;
this.sceneY = y;
this.screenX = screenX;
this.screenY = screenY;
this.pickResult = pickResult != null ? pickResult : new PickResult(target, x, y);
final Point3D p = InputEventUtils.recomputeCoordinates(this.pickResult, null);
this.x = p.getX();
this.y = p.getY();
this.z = p.getZ();
}
void recomputeToSource(Object oldSource, Object newSource) {
final Point3D newCoordinates = InputEventUtils.recomputeCoordinates(
pickResult, newSource);
x = newCoordinates.getX();
y = newCoordinates.getY();
z = newCoordinates.getZ();
source = newSource;
}
public boolean belongsTo(EventTarget target) {
if (this.target instanceof Node) {
Node n = (Node) this.target;
if (target instanceof Scene) {
return n.getScene() == target;
}
while (n != null) {
if (n == target) {
return true;
}
n = n.getParent();
}
}
return target == this.target;
}
void reset() {
final Point3D p = InputEventUtils.recomputeCoordinates(pickResult, null);
x = p.getX();
y = p.getY();
z = p.getZ();
}
private EventTarget grabbed = null;
public EventTarget getGrabbed() {
return grabbed;
}
public void grab() {
if (source instanceof EventTarget) {
grabbed = (EventTarget) source;
} else {
throw new IllegalStateException("Cannot grab touch point, "
+ "source is not an instance of EventTarget: " + source);
}
}
public void grab(EventTarget target) {
grabbed = target;
}
public void ungrab() {
grabbed = null;
}
private int id;
public final int getId() {
return id;
}
private State state;
public final State getState() {
return state;
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
private double screenX;
public final double getScreenX() {
return screenX;
}
private double screenY;
public final double getScreenY() {
return screenY;
}
private double sceneX;
public final double getSceneX() {
return sceneX;
}
private double sceneY;
public final double getSceneY() {
return sceneY;
}
private PickResult pickResult;
public final PickResult getPickResult() {
return pickResult;
}
public EventTarget getTarget() {
return target;
}
@Override public String toString() {
final StringBuilder sb = new StringBuilder("TouchPoint [");
sb.append("state = ").append(getState());
sb.append(", id = ").append(getId());
sb.append(", target = ").append(getTarget());
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
public enum State {
PRESSED,
MOVED,
STATIONARY,
RELEASED
}
}
