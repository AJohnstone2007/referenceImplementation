package javafx.scene.input;
import java.util.EnumSet;
import java.util.Set;
import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Point3D;
import com.sun.javafx.scene.input.InputEventUtils;
import java.io.IOException;
public final class DragEvent extends InputEvent {
private static final long serialVersionUID = 20121107L;
public static final EventType<DragEvent> ANY =
new EventType<DragEvent>(InputEvent.ANY, "DRAG");
public static final EventType<DragEvent> DRAG_ENTERED_TARGET =
new EventType<DragEvent>(DragEvent.ANY, "DRAG_ENTERED_TARGET");
public static final EventType<DragEvent> DRAG_ENTERED =
new EventType<DragEvent>(DragEvent.DRAG_ENTERED_TARGET, "DRAG_ENTERED");
public static final EventType<DragEvent> DRAG_EXITED_TARGET =
new EventType<DragEvent>(DragEvent.ANY, "DRAG_EXITED_TARGET");
public static final EventType<DragEvent> DRAG_EXITED =
new EventType<DragEvent>(DragEvent.DRAG_EXITED_TARGET, "DRAG_EXITED");
public static final EventType<DragEvent> DRAG_OVER =
new EventType<DragEvent>(DragEvent.ANY, "DRAG_OVER");
public static final EventType<DragEvent> DRAG_DROPPED =
new EventType<DragEvent>(DragEvent.ANY, "DRAG_DROPPED");
public static final EventType<DragEvent> DRAG_DONE =
new EventType<DragEvent>(DragEvent.ANY, "DRAG_DONE");
public DragEvent copyFor(Object source, EventTarget target,
Object gestureSource, Object gestureTarget,
EventType<DragEvent> eventType) {
DragEvent copyEvent = copyFor(source, target, eventType);
recomputeCoordinatesToSource(copyEvent, source);
copyEvent.gestureSource = gestureSource;
copyEvent.gestureTarget = gestureTarget;
return copyEvent;
}
public DragEvent(@NamedArg("source") Object source, @NamedArg("target") EventTarget target, @NamedArg("eventType") EventType<DragEvent> eventType, @NamedArg("dragboard") Dragboard dragboard,
@NamedArg("x") double x, @NamedArg("y") double y,
@NamedArg("screenX") double screenX, @NamedArg("screenY") double screenY, @NamedArg("transferMode") TransferMode transferMode,
@NamedArg("gestureSource") Object gestureSource, @NamedArg("gestureTarget") Object gestureTarget, @NamedArg("pickResult") PickResult pickResult) {
super(source, target, eventType);
this.gestureSource = gestureSource;
this.gestureTarget = gestureTarget;
this.x = x;
this.y = y;
this.screenX = screenX;
this.screenY = screenY;
this.sceneX = x;
this.sceneY = y;
this.transferMode = transferMode;
this.dragboard = dragboard;
if (eventType == DragEvent.DRAG_DROPPED
|| eventType == DragEvent.DRAG_DONE) {
state.accepted = transferMode != null;
state.acceptedTransferMode = transferMode;
state.acceptingObject = state.accepted ? source : null;
}
this.pickResult = pickResult != null ? pickResult : new PickResult(
eventType == DRAG_DONE ? null : target, x, y);
final Point3D p = InputEventUtils.recomputeCoordinates(this.pickResult, null);
this.x = p.getX();
this.y = p.getY();
this.z = p.getZ();
}
public DragEvent(@NamedArg("eventType") EventType<DragEvent> eventType, @NamedArg("dragboard") Dragboard dragboard,
@NamedArg("x") double x, @NamedArg("y") double y,
@NamedArg("screenX") double screenX, @NamedArg("screenY") double screenY, @NamedArg("transferMode") TransferMode transferMode,
@NamedArg("gestureSource") Object gestureSource, @NamedArg("gestureTarget") Object gestureTarget, @NamedArg("pickResult") PickResult pickResult) {
this(null, null, eventType, dragboard, x, y, screenX, screenY, transferMode,
gestureSource, gestureTarget, pickResult);
}
private void recomputeCoordinatesToSource(DragEvent newEvent, Object newSource) {
if (newEvent.getEventType() == DRAG_DONE) {
return;
}
final Point3D newCoordinates = InputEventUtils.recomputeCoordinates(
pickResult, newSource);
newEvent.x = newCoordinates.getX();
newEvent.y = newCoordinates.getY();
newEvent.z = newCoordinates.getZ();
}
@Override
public DragEvent copyFor(Object newSource, EventTarget newTarget) {
DragEvent e = (DragEvent) super.copyFor(newSource, newTarget);
recomputeCoordinatesToSource(e, newSource);
return e;
}
public DragEvent copyFor(Object source, EventTarget target, EventType<DragEvent> type) {
DragEvent e = (DragEvent) copyFor(source, target);
e.eventType = type;
return e;
}
@Override
public EventType<DragEvent> getEventType() {
return (EventType<DragEvent>) super.getEventType();
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
public final Object getGestureSource() { return gestureSource; }
private Object gestureSource;
public final Object getGestureTarget() { return gestureTarget; }
private Object gestureTarget;
public final TransferMode getTransferMode() { return transferMode; }
private TransferMode transferMode;
private final State state = new State();
public final boolean isAccepted() { return state.accepted; }
public final TransferMode getAcceptedTransferMode() {
return state.acceptedTransferMode;
}
public final Object getAcceptingObject() {
return state.acceptingObject;
}
public final Dragboard getDragboard() {
return dragboard;
}
private transient Dragboard dragboard;
private static TransferMode chooseTransferMode(Set<TransferMode> supported,
TransferMode[] accepted, TransferMode proposed) {
TransferMode result = null;
Set<TransferMode> intersect = EnumSet.noneOf(TransferMode.class);
for (TransferMode tm : InputEventUtils.safeTransferModes(accepted)) {
if (supported.contains(tm)) {
intersect.add(tm);
}
}
if (intersect.contains(proposed)) {
result = proposed;
} else {
if (intersect.contains(TransferMode.MOVE)) {
result = TransferMode.MOVE;
} else if (intersect.contains(TransferMode.COPY)) {
result = TransferMode.COPY;
} else if (intersect.contains(TransferMode.LINK)) {
result = TransferMode.LINK;
}
}
return result;
}
public void acceptTransferModes(TransferMode... transferModes) {
if (dragboard == null || dragboard.getTransferModes() == null ||
transferMode == null) {
state.accepted = false;
return;
}
TransferMode tm = chooseTransferMode(dragboard.getTransferModes(),
transferModes, transferMode);
if (tm == null && getEventType() == DRAG_DROPPED) {
throw new IllegalStateException("Accepting unsupported transfer "
+ "modes inside DRAG_DROPPED handler");
}
state.accepted = tm != null;
state.acceptedTransferMode = tm;
state.acceptingObject = state.accepted ? source : null;
}
public void setDropCompleted(boolean isTransferDone) {
if (getEventType() != DRAG_DROPPED) {
throw new IllegalStateException("setDropCompleted can be called " +
"only from DRAG_DROPPED handler");
}
state.dropCompleted = isTransferDone;
}
public boolean isDropCompleted() {
return state.dropCompleted;
}
private void readObject(java.io.ObjectInputStream in)
throws IOException, ClassNotFoundException {
in.defaultReadObject();
x = sceneX;
y = sceneY;
}
private static class State {
boolean accepted = false;
boolean dropCompleted = false;
TransferMode acceptedTransferMode = null;
Object acceptingObject = null;
}
}
