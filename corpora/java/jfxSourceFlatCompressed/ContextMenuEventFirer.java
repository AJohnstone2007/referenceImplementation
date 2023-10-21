package test.com.sun.javafx.scene.control.infrastructure;
import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.ContextMenuEvent;
public final class ContextMenuEventFirer {
private ContextMenuEventFirer() {
}
public static void fireContextMenuEvent(Node target) {
fireContextMenuEvent(target, 0, 0);
}
public static void fireContextMenuEvent(Node target, double deltaX, double deltaY) {
Bounds screenBounds = target.localToScreen(target.getLayoutBounds());
double screenX = screenBounds.getMaxX() - screenBounds.getWidth() / 2.0 + deltaX;
double screenY = screenBounds.getMaxY() - screenBounds.getHeight() / 2.0 + deltaY;
ContextMenuEvent evt = new ContextMenuEvent(
target,
target,
ContextMenuEvent.CONTEXT_MENU_REQUESTED,
deltaX, deltaY,
screenX, screenY,
false,
null);
Event.fireEvent(target, evt);
}
}
