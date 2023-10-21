package test.com.sun.javafx.test;
import javafx.event.EventType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
public final class MouseEventGenerator {
public static MouseEvent generateMouseEvent(EventType<MouseEvent> type,
double x, double y) {
MouseButton button = MouseButton.NONE;
if (type == MouseEvent.MOUSE_PRESSED ||
type == MouseEvent.MOUSE_RELEASED ||
type == MouseEvent.MOUSE_DRAGGED) {
button = MouseButton.PRIMARY;
}
boolean primaryButtonDown = false;
if (type == MouseEvent.MOUSE_PRESSED ||
type == MouseEvent.MOUSE_DRAGGED) {
primaryButtonDown = true;
}
if (type == MouseEvent.MOUSE_RELEASED) {
primaryButtonDown = false;
}
MouseEvent event = new MouseEvent(type, x, y, x, y, button,
1, false, false, false, false, primaryButtonDown,
false, false, false, false, false, null);
return event;
}
}
