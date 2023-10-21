package javafx.scene.input;
import java.util.Collections;
import java.util.List;
import javafx.beans.NamedArg;
import javafx.event.EventTarget;
import javafx.event.EventType;
public final class TouchEvent extends InputEvent {
private static final long serialVersionUID = 20121107L;
public static final EventType<TouchEvent> ANY =
new EventType<TouchEvent>(InputEvent.ANY, "TOUCH");
public static final EventType<TouchEvent> TOUCH_PRESSED =
new EventType<TouchEvent>(ANY, "TOUCH_PRESSED");
public static final EventType<TouchEvent> TOUCH_MOVED =
new EventType<TouchEvent>(ANY, "TOUCH_MOVED");
public static final EventType<TouchEvent> TOUCH_RELEASED =
new EventType<TouchEvent>(ANY, "TOUCH_RELEASED");
public static final EventType<TouchEvent> TOUCH_STATIONARY =
new EventType<TouchEvent>(ANY, "TOUCH_STATIONARY");
public TouchEvent(@NamedArg("source") Object source, @NamedArg("target") EventTarget target, @NamedArg("eventType") EventType<TouchEvent> eventType,
@NamedArg("touchPoint") TouchPoint touchPoint, @NamedArg("touchPoints") List<TouchPoint> touchPoints, @NamedArg("eventSetId") int eventSetId,
@NamedArg("shiftDown") boolean shiftDown, @NamedArg("controlDown") boolean controlDown, @NamedArg("altDown") boolean altDown,
@NamedArg("metaDown") boolean metaDown) {
super(source, target, eventType);
this.touchPoints = touchPoints != null ? Collections.unmodifiableList(touchPoints) : null;
this.eventSetId = eventSetId;
this.shiftDown = shiftDown;
this.controlDown = controlDown;
this.altDown = altDown;
this.metaDown = metaDown;
this.touchPoint = touchPoint;
}
public TouchEvent(@NamedArg("eventType") EventType<TouchEvent> eventType,
@NamedArg("touchPoint") TouchPoint touchPoint, @NamedArg("touchPoints") List<TouchPoint> touchPoints, @NamedArg("eventSetId") int eventSetId,
@NamedArg("shiftDown") boolean shiftDown, @NamedArg("controlDown") boolean controlDown, @NamedArg("altDown") boolean altDown,
@NamedArg("metaDown") boolean metaDown) {
this(null, null, eventType, touchPoint, touchPoints, eventSetId,
shiftDown, controlDown, altDown, metaDown);
}
public int getTouchCount() {
return touchPoints.size();
}
private static void recomputeToSource(TouchEvent event, Object oldSource,
Object newSource) {
for (TouchPoint tp : event.touchPoints) {
tp.recomputeToSource(oldSource, newSource);
}
}
@Override
public TouchEvent copyFor(Object newSource, EventTarget newTarget) {
TouchEvent e = (TouchEvent) super.copyFor(newSource, newTarget);
recomputeToSource(e, getSource(), newSource);
return e;
}
public TouchEvent copyFor(Object newSource, EventTarget newTarget, EventType<TouchEvent> type) {
TouchEvent e = copyFor(newSource, newTarget);
e.eventType = type;
return e;
}
@Override
public EventType<TouchEvent> getEventType() {
return (EventType<TouchEvent>) super.getEventType();
}
private final int eventSetId;
public final int getEventSetId() {
return eventSetId;
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
private final TouchPoint touchPoint;
public TouchPoint getTouchPoint() {
return touchPoint;
}
private final List<TouchPoint> touchPoints;
public List<TouchPoint> getTouchPoints() {
return touchPoints;
}
@Override public String toString() {
final StringBuilder sb = new StringBuilder("TouchEvent [");
sb.append("source = ").append(getSource());
sb.append(", target = ").append(getTarget());
sb.append(", eventType = ").append(getEventType());
sb.append(", consumed = ").append(isConsumed());
sb.append(", touchCount = ").append(getTouchCount());
sb.append(", eventSetId = ").append(getEventSetId());
sb.append(", touchPoint = ").append(getTouchPoint().toString());
return sb.append("]").toString();
}
}
