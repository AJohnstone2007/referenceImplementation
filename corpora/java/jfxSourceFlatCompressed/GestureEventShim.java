package javafx.scene.input;
import javafx.event.EventTarget;
import javafx.event.EventType;
public class GestureEventShim {
public static GestureEvent getGestureEvent(
final EventType<? extends GestureEvent> eventType,
double x, double y, double screenX, double screenY,
boolean shiftDown, boolean controlDown, boolean altDown,
boolean metaDown, boolean direct, boolean inertia,
PickResult pickResult) {
return new GestureEvent(eventType,
x, y, screenX, screenY,
shiftDown, controlDown, altDown,
metaDown, direct, inertia,
pickResult);
}
@SuppressWarnings("deprecation")
public static GestureEvent getGestureEvent(
Object source, EventTarget target,
final EventType<? extends GestureEvent> eventType) {
return new GestureEvent(source, target, eventType);
}
public static GestureEvent getGestureEvent(
Object source, EventTarget target, final EventType<? extends GestureEvent> eventType,
double x, double y, double screenX, double screenY,
boolean shiftDown, boolean controlDown, boolean altDown,
boolean metaDown, boolean direct, boolean inertia, PickResult pickResult) {
return new GestureEvent(
source, target, eventType,
x, y, screenX, screenY,
shiftDown, controlDown, altDown,
metaDown, direct, inertia, pickResult);
}
}
