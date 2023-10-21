package javafx.scene.input;
import javafx.beans.NamedArg;
import javafx.event.EventTarget;
import javafx.event.EventType;
public final class ScrollEvent extends GestureEvent {
private static final long serialVersionUID = 20121107L;
public static final EventType<ScrollEvent> ANY =
new EventType<ScrollEvent> (GestureEvent.ANY, "ANY_SCROLL");
public static final EventType<ScrollEvent> SCROLL =
new EventType<ScrollEvent> (ScrollEvent.ANY, "SCROLL");
public static final EventType<ScrollEvent> SCROLL_STARTED =
new EventType<ScrollEvent> (ScrollEvent.ANY, "SCROLL_STARTED");
public static final EventType<ScrollEvent> SCROLL_FINISHED =
new EventType<ScrollEvent> (ScrollEvent.ANY, "SCROLL_FINISHED");
private ScrollEvent(Object source, EventTarget target,
final EventType<ScrollEvent> eventType,
double x, double y,
double screenX, double screenY,
boolean shiftDown,
boolean controlDown,
boolean altDown,
boolean metaDown,
boolean direct,
boolean inertia,
double deltaX, double deltaY,
double totalDeltaX, double totalDeltaY,
double multiplierX, double multiplierY,
HorizontalTextScrollUnits textDeltaXUnits, double textDeltaX,
VerticalTextScrollUnits textDeltaYUnits, double textDeltaY,
int touchCount, PickResult pickResult) {
super(source, target, eventType, x, y, screenX, screenY,
shiftDown, controlDown, altDown, metaDown, direct, inertia,
pickResult);
this.deltaX = deltaX;
this.deltaY = deltaY;
this.totalDeltaX = totalDeltaX;
this.totalDeltaY = totalDeltaY;
this.textDeltaXUnits = textDeltaXUnits;
this.textDeltaX = textDeltaX;
this.textDeltaYUnits = textDeltaYUnits;
this.textDeltaY = textDeltaY;
this.touchCount = touchCount;
this.multiplierX = multiplierX;
this.multiplierY = multiplierY;
}
public ScrollEvent(@NamedArg("source") Object source, @NamedArg("target") EventTarget target,
final @NamedArg("eventType") EventType<ScrollEvent> eventType,
@NamedArg("x") double x, @NamedArg("y") double y,
@NamedArg("screenX") double screenX, @NamedArg("screenY") double screenY,
@NamedArg("shiftDown") boolean shiftDown,
@NamedArg("controlDown") boolean controlDown,
@NamedArg("altDown") boolean altDown,
@NamedArg("metaDown") boolean metaDown,
@NamedArg("direct") boolean direct,
@NamedArg("inertia") boolean inertia,
@NamedArg("deltaX") double deltaX, @NamedArg("deltaY") double deltaY,
@NamedArg("totalDeltaX") double totalDeltaX, @NamedArg("totalDeltaY") double totalDeltaY,
@NamedArg("textDeltaXUnits") HorizontalTextScrollUnits textDeltaXUnits, @NamedArg("textDeltaX") double textDeltaX,
@NamedArg("textDeltaYUnits") VerticalTextScrollUnits textDeltaYUnits, @NamedArg("textDeltaY") double textDeltaY,
@NamedArg("touchCount") int touchCount, @NamedArg("pickResult") PickResult pickResult) {
this(source, target, eventType, x, y, screenX, screenY, shiftDown, controlDown,
altDown, metaDown, direct, inertia, deltaX, deltaY, totalDeltaX,
totalDeltaY, 1.0, 1.0, textDeltaXUnits, textDeltaX, textDeltaYUnits, textDeltaY,
touchCount, pickResult);
}
public ScrollEvent(final @NamedArg("eventType") EventType<ScrollEvent> eventType,
@NamedArg("x") double x, @NamedArg("y") double y,
@NamedArg("screenX") double screenX, @NamedArg("screenY") double screenY,
@NamedArg("shiftDown") boolean shiftDown,
@NamedArg("controlDown") boolean controlDown,
@NamedArg("altDown") boolean altDown,
@NamedArg("metaDown") boolean metaDown,
@NamedArg("direct") boolean direct,
@NamedArg("inertia") boolean inertia,
@NamedArg("deltaX") double deltaX, @NamedArg("deltaY") double deltaY,
@NamedArg("totalDeltaX") double totalDeltaX, @NamedArg("totalDeltaY") double totalDeltaY,
@NamedArg("textDeltaXUnits") HorizontalTextScrollUnits textDeltaXUnits, @NamedArg("textDeltaX") double textDeltaX,
@NamedArg("textDeltaYUnits") VerticalTextScrollUnits textDeltaYUnits, @NamedArg("textDeltaY") double textDeltaY,
@NamedArg("touchCount") int touchCount,
@NamedArg("pickResult") PickResult pickResult) {
this(null, null, eventType, x, y, screenX, screenY, shiftDown, controlDown,
altDown, metaDown, direct, inertia, deltaX, deltaY, totalDeltaX,
totalDeltaY, 1.0, 1.0, textDeltaXUnits, textDeltaX, textDeltaYUnits, textDeltaY,
touchCount, pickResult);
}
public ScrollEvent(final @NamedArg("eventType") EventType<ScrollEvent> eventType,
@NamedArg("x") double x, @NamedArg("y") double y,
@NamedArg("screenX") double screenX, @NamedArg("screenY") double screenY,
@NamedArg("shiftDown") boolean shiftDown,
@NamedArg("controlDown") boolean controlDown,
@NamedArg("altDown") boolean altDown,
@NamedArg("metaDown") boolean metaDown,
@NamedArg("direct") boolean direct,
@NamedArg("inertia") boolean inertia,
@NamedArg("deltaX") double deltaX, @NamedArg("deltaY") double deltaY,
@NamedArg("totalDeltaX") double totalDeltaX, @NamedArg("totalDeltaY") double totalDeltaY,
@NamedArg("multiplierX") double multiplierX, @NamedArg("multiplierY") double multiplierY,
@NamedArg("textDeltaXUnits") HorizontalTextScrollUnits textDeltaXUnits, @NamedArg("textDeltaX") double textDeltaX,
@NamedArg("textDeltaYUnits") VerticalTextScrollUnits textDeltaYUnits, @NamedArg("textDeltaY") double textDeltaY,
@NamedArg("touchCount") int touchCount,
@NamedArg("pickResult") PickResult pickResult) {
this(null, null, eventType, x, y, screenX, screenY, shiftDown, controlDown,
altDown, metaDown, direct, inertia, deltaX, deltaY, totalDeltaX,
totalDeltaY, multiplierX, multiplierY, textDeltaXUnits, textDeltaX,
textDeltaYUnits, textDeltaY, touchCount, pickResult);
}
private final double deltaX;
public double getDeltaX() {
return deltaX;
}
private final double deltaY;
public double getDeltaY() {
return deltaY;
}
private double totalDeltaX;
public double getTotalDeltaX() {
return totalDeltaX;
}
private final double totalDeltaY;
public double getTotalDeltaY() {
return totalDeltaY;
}
private final HorizontalTextScrollUnits textDeltaXUnits;
public HorizontalTextScrollUnits getTextDeltaXUnits() {
return textDeltaXUnits;
}
private final VerticalTextScrollUnits textDeltaYUnits;
public VerticalTextScrollUnits getTextDeltaYUnits() {
return textDeltaYUnits;
}
private final double textDeltaX;
public double getTextDeltaX() {
return textDeltaX;
}
private final double textDeltaY;
public double getTextDeltaY() {
return textDeltaY;
}
private final int touchCount;
public int getTouchCount() {
return touchCount;
}
private final double multiplierX;
public double getMultiplierX() {
return multiplierX;
}
private final double multiplierY;
public double getMultiplierY() {
return multiplierY;
}
@Override public String toString() {
final StringBuilder sb = new StringBuilder("ScrollEvent [");
sb.append("source = ").append(getSource());
sb.append(", target = ").append(getTarget());
sb.append(", eventType = ").append(getEventType());
sb.append(", consumed = ").append(isConsumed());
sb.append(", deltaX = ").append(getDeltaX())
.append(", deltaY = ").append(getDeltaY());
sb.append(", totalDeltaX = ").append(getTotalDeltaX())
.append(", totalDeltaY = ").append(getTotalDeltaY());
sb.append(", textDeltaXUnits = ").append(getTextDeltaXUnits())
.append(", textDeltaX = ").append(getTextDeltaX());
sb.append(", textDeltaYUnits = ").append(getTextDeltaYUnits())
.append(", textDeltaY = ").append(getTextDeltaY());
sb.append(", touchCount = ").append(getTouchCount());
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
@Override
public ScrollEvent copyFor(Object newSource, EventTarget newTarget) {
return (ScrollEvent) super.copyFor(newSource, newTarget);
}
public ScrollEvent copyFor(Object newSource, EventTarget newTarget, EventType<ScrollEvent> type) {
ScrollEvent e = copyFor(newSource, newTarget);
e.eventType = type;
return e;
}
@Override
public EventType<ScrollEvent> getEventType() {
return (EventType<ScrollEvent>) super.getEventType();
}
public static enum HorizontalTextScrollUnits {
NONE,
CHARACTERS
}
public static enum VerticalTextScrollUnits {
NONE,
LINES,
PAGES
}
}
