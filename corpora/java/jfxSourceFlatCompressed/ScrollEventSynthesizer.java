package ensemble;
import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;
public class ScrollEventSynthesizer implements EventHandler {
private static final int INERTIA_DURATION = 2400;
private static final double CLICK_THRESHOLD = 20;
private static final double CLICK_TIME_THRESHOLD = Integer.parseInt(System.getProperty("click", "400"));
private long startDrag;
private long lastDrag;
private long lastDragDelta;
private int startDragX;
private int startDragY;
private int lastDragX;
private int lastDragY;
private int lastDragStepX;
private int lastDragStepY;
private double dragVelocityX;
private double dragVelocityY;
private boolean clickThresholdBroken;
private Timeline inertiaTimeline = null;
private long lastClickTime = -1;
private boolean isFiredByMe = false;
public ScrollEventSynthesizer(Scene scene) {
scene.addEventFilter(MouseEvent.ANY, this);
scene.addEventFilter(ScrollEvent.ANY, this);
}
@Override public void handle(final Event e) {
if (isFiredByMe) return;
if (e instanceof ScrollEvent) {
final ScrollEvent se = (ScrollEvent)e;
if (se.getTouchCount() != 0) se.consume();
} else if (e instanceof MouseEvent) {
final MouseEvent me = (MouseEvent)e;
if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {
lastDragX = startDragX = (int)me.getX();
lastDragY = startDragY = (int)me.getY();
lastDrag = startDrag = System.currentTimeMillis();
lastDragDelta = 0;
if(inertiaTimeline != null) inertiaTimeline.stop();
clickThresholdBroken = false;
} else if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
lastDragStepX = (int)me.getX() - lastDragX;
lastDragStepY = (int)me.getY() - lastDragY;
lastDragDelta = System.currentTimeMillis() - lastDrag;
dragVelocityX = (double)lastDragStepX/(double)lastDragDelta;
dragVelocityY = (double)lastDragStepY/(double)lastDragDelta;
lastDragX = (int)me.getX();
lastDragY = (int)me.getY();
lastDrag = System.currentTimeMillis();
final int dragX = (int)me.getX() - startDragX;
final int dragY = (int)me.getY() - startDragY;
double distance = Math.abs(Math.sqrt((dragX*dragX) + (dragY*dragY)));
int scrollDistX = lastDragStepX;
int scrollDistY = lastDragStepY;
if (!clickThresholdBroken && distance > CLICK_THRESHOLD) {
clickThresholdBroken = true;
scrollDistX = dragX;
scrollDistY = dragY;
}
if (clickThresholdBroken) {
Event.fireEvent(e.getTarget(), new ScrollEvent(
ScrollEvent.SCROLL,
me.getX(), me.getY(),
me.getSceneX(), me.getSceneY(),
me.isShiftDown(), me.isControlDown(), me.isAltDown(), me.isMetaDown(), true, false,
scrollDistX, scrollDistY,
scrollDistX, scrollDistY,
ScrollEvent.HorizontalTextScrollUnits.NONE, 0,
ScrollEvent.VerticalTextScrollUnits.NONE, 0,
0,new PickResult(me.getTarget(), me.getSceneX(), me.getSceneY())
));
}
} else if (e.getEventType() == MouseEvent.MOUSE_RELEASED) {
handleRelease(me);
} else if (e.getEventType() == MouseEvent.MOUSE_CLICKED) {
final long time = System.currentTimeMillis();
if (clickThresholdBroken || (lastClickTime != -1 && (time-lastClickTime) < CLICK_TIME_THRESHOLD)) e.consume();
lastClickTime = time;
}
}
}
private void handleRelease(final MouseEvent me) {
if (clickThresholdBroken) {
final long time = System.currentTimeMillis() - lastDrag;
dragVelocityX = (double)lastDragStepX/(time + lastDragDelta);
dragVelocityY = (double)lastDragStepY/(time + lastDragDelta);
final int dragX = (int)me.getX() - startDragX;
final int dragY = (int)me.getY() - startDragY;
final long totalTime = System.currentTimeMillis() - startDrag;
final boolean quick = totalTime < 300;
double velocityX = quick ? (double)dragX / (double)totalTime : dragVelocityX;
double velocityY = quick ? (double)dragY / (double)totalTime : dragVelocityY;
final int distanceX = (int)(velocityX * INERTIA_DURATION);
final int distanceY = (int)(velocityY * INERTIA_DURATION);
DoubleProperty animatePosition = new SimpleDoubleProperty() {
double lastMouseX = me.getX();
double lastMouseY = me.getY();
@Override protected void invalidated() {
final double mouseX = me.getX() + (distanceX * get());
final double mouseY = me.getY() + (distanceY * get());
final double dragStepX = mouseX - lastMouseX;
final double dragStepY = mouseY - lastMouseY;
if (Math.abs(dragStepX) >= 1.0 || Math.abs(dragStepY) >= 1.0) {
Event.fireEvent(me.getTarget(), new ScrollEvent(
ScrollEvent.SCROLL,
me.getX(), me.getY(),
me.getSceneX(), me.getSceneY(),
me.isShiftDown(), me.isControlDown(), me.isAltDown(), me.isMetaDown(),
true, true,
dragStepX, dragStepY,
(distanceX * get()), (distanceY * get()),
ScrollEvent.HorizontalTextScrollUnits.NONE, 0,
ScrollEvent.VerticalTextScrollUnits.NONE, 0,
0,new PickResult(me.getTarget(), me.getSceneX(), me.getSceneY())
));
}
lastMouseX = mouseX;
lastMouseY = mouseY;
}
};
inertiaTimeline = new Timeline(
new KeyFrame(Duration.ZERO, new KeyValue(animatePosition, 0)),
new KeyFrame(Duration.millis(INERTIA_DURATION), new KeyValue(animatePosition, 1d,
Interpolator.SPLINE(0.0513, 0.1131, 0.1368, 1.0000)))
);
inertiaTimeline.play();
}
}
}
