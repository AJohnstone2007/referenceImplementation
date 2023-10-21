package test.com.sun.javafx.scene.control.infrastructure;
import java.util.Arrays;
import java.util.List;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.stage.Window;
public final class MouseEventFirer {
private final EventTarget target;
private final Scene scene;
private final Bounds targetBounds;
private StageLoader sl;
private boolean alternative;
public MouseEventFirer(EventTarget target) {
this.target = target;
if (target instanceof Node) {
Node n = (Node)target;
Scene s = n.getScene();
Window w = s == null ? null : s.getWindow();
if (w == null || w.getScene() == null) {
sl = new StageLoader(n);
scene = n.getScene();
targetBounds = n.getLayoutBounds();
} else {
scene = w.getScene();
targetBounds = n.getLayoutBounds();
}
} else if (target instanceof Scene) {
scene = (Scene)target;
sl = new StageLoader(scene);
targetBounds = new BoundingBox(0, 0, scene.getWidth(), scene.getHeight());
} else {
throw new RuntimeException("EventTarget of invalid type (" + target + ")");
}
}
public MouseEventFirer(Node target, boolean alternative) {
this(target);
this.alternative = alternative;
}
public void dispose() {
if (sl != null) {
sl.dispose();
}
}
public void fireMousePressAndRelease(KeyModifier... modifiers) {
fireMouseEvent(MouseEvent.MOUSE_PRESSED, modifiers);
fireMouseEvent(MouseEvent.MOUSE_RELEASED, modifiers);
}
public void fireMousePressAndRelease(int clickCount, KeyModifier... modifiers) {
fireMousePressAndRelease(clickCount, 0, 0, modifiers);
}
public void fireMousePressAndRelease(int clickCount, double deltaX, double deltaY, KeyModifier... modifiers) {
fireMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, clickCount, deltaX, deltaY, modifiers);
fireMouseEvent(MouseEvent.MOUSE_RELEASED, MouseButton.PRIMARY, clickCount, deltaX, deltaY, modifiers);
}
public void fireMouseClicked() {
fireMouseEvent(MouseEvent.MOUSE_CLICKED);
}
public void fireMouseClicked(MouseButton button) {
fireMouseEvent(MouseEvent.MOUSE_CLICKED, button, 0, 0);
}
public void fireMouseClicked(double deltaX, double deltaY) {
fireMouseEvent(MouseEvent.MOUSE_CLICKED, deltaX, deltaY);
}
public void fireMouseClicked(double deltaX, double deltaY, KeyModifier... modifiers) {
fireMouseEvent(MouseEvent.MOUSE_CLICKED, deltaX, deltaY, modifiers);
}
public void fireMousePressed() {
fireMouseEvent(MouseEvent.MOUSE_PRESSED);
}
public void fireMousePressed(MouseButton button) {
fireMouseEvent(MouseEvent.MOUSE_PRESSED, button, 0, 0);
}
public void fireMousePressed(double deltaX, double deltaY) {
fireMouseEvent(MouseEvent.MOUSE_PRESSED, deltaX, deltaY);
}
public void fireMousePressed(double deltaX, double deltaY, KeyModifier... modifiers) {
fireMouseEvent(MouseEvent.MOUSE_PRESSED, deltaX, deltaY, modifiers);
}
public void fireMousePressed(int clickCount, double deltaX, double deltaY, KeyModifier... modifiers) {
fireMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, clickCount, deltaX, deltaY, modifiers);
}
public void fireMouseReleased() {
fireMouseEvent(MouseEvent.MOUSE_RELEASED);
}
public void fireMouseReleased(MouseButton button) {
fireMouseEvent(MouseEvent.MOUSE_RELEASED, button, 0, 0);
}
public void fireMouseReleased(double deltaX, double deltaY) {
fireMouseEvent(MouseEvent.MOUSE_RELEASED, deltaX, deltaY);
}
public void fireMouseReleased(double deltaX, double deltaY, KeyModifier... modifiers) {
fireMouseEvent(MouseEvent.MOUSE_RELEASED, deltaX, deltaY, modifiers);
}
public void fireMouseEvent(EventType<MouseEvent> evtType, KeyModifier... modifiers) {
fireMouseEvent(evtType, 0, 0 , modifiers);
}
public void fireMouseEvent(EventType<MouseEvent> evtType, double deltaX, double deltaY, KeyModifier... modifiers) {
fireMouseEvent(evtType, MouseButton.PRIMARY, deltaX, deltaY, modifiers);
}
public void fireMouseEvent(EventType<MouseEvent> evtType, MouseButton button, double deltaX, double deltaY, KeyModifier... modifiers) {
fireMouseEvent(evtType, button, 1, deltaX, deltaY, modifiers);
}
private void fireMouseEvent(EventType<MouseEvent> evtType, MouseButton button, int clickCount, double deltaX, double deltaY, KeyModifier... modifiers) {
if (alternative) {
fireMouseEventAlternative(evtType, button, clickCount, deltaX, deltaY, modifiers);
return;
}
final Window window = scene.getWindow();
final double w = targetBounds.getWidth();
final double h = targetBounds.getHeight();
final double x = w / 2.0 + deltaX;
final double y = h / 2.0 + deltaY;
final double sceneX = x + scene.getX() + deltaX;
final double sceneY = y + scene.getY() + deltaY;
final double screenX = sceneX + window.getX();
final double screenY = sceneY + window.getY();
final List<KeyModifier> ml = Arrays.asList(modifiers);
final PickResult pickResult = new PickResult(target, sceneX, sceneY);
MouseEvent evt = new MouseEvent(
target,
target,
evtType,
x, y,
screenX, screenY,
button,
clickCount,
ml.contains(KeyModifier.SHIFT),
ml.contains(KeyModifier.CTRL),
ml.contains(KeyModifier.ALT),
ml.contains(KeyModifier.META),
button == MouseButton.PRIMARY,
button == MouseButton.MIDDLE,
button == MouseButton.SECONDARY,
button == MouseButton.BACK,
button == MouseButton.FORWARD,
false,
button == MouseButton.SECONDARY,
true,
pickResult);
Event.fireEvent(target, evt);
}
private void fireMouseEventAlternative(EventType<MouseEvent> evtType, MouseButton button, int clickCount, double deltaX, double deltaY, KeyModifier... modifiers) {
final double w = targetBounds.getWidth();
final double h = targetBounds.getHeight();
final double x = w / 2.0 + deltaX;
final double y = h / 2.0 + deltaY;
Node node = (Node) target;
Point2D localP = new Point2D(x, y);
Point2D sceneP = node.localToScene(localP);
Point2D screenP = node.localToScreen(localP);
final List<KeyModifier> ml = Arrays.asList(modifiers);
MouseEvent evt = new MouseEvent(
target,
null,
evtType,
sceneP.getX(), sceneP.getY(),
screenP.getX(), screenP.getY(),
button,
clickCount,
ml.contains(KeyModifier.SHIFT),
ml.contains(KeyModifier.CTRL),
ml.contains(KeyModifier.ALT),
ml.contains(KeyModifier.META),
button == MouseButton.PRIMARY,
button == MouseButton.MIDDLE,
button == MouseButton.SECONDARY,
button == MouseButton.BACK,
button == MouseButton.FORWARD,
false,
button == MouseButton.SECONDARY,
true,
null
);
Event.fireEvent(target, evt);
}
}
