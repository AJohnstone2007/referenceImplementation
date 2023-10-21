package ensemble.control;
import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import static ensemble.control.BendingPages.State.*;
import static ensemble.control.BendingPages.AnimationState.*;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
public class BendingPages extends Region {
private BookBend bookBend;
private Path frontPageBack = new Path();
private Path shadow = new Path();
private ObjectProperty<Node> frontPage = new SimpleObjectProperty<Node>(new Region());
public ObjectProperty<Node> frontPageProperty() {
return frontPage;
}
public Node getFrontPage() {
return frontPage.get();
}
public void setFrontPage(Node frontPage) {
this.frontPage.set(frontPage);
}
private ObjectProperty<Node> backPage = new SimpleObjectProperty<Node>(new Region());
public ObjectProperty<Node> backPageProperty() {
return backPage;
}
public Node getBackPage() {
return backPage.get();
}
public void setBackPage(Node backPage) {
this.backPage.set(backPage);
}
public void reset() {
if (animation != null) {
animation.stop();
}
state = State.CLOSED;
setTarget();
update();
fixMouseTransparency();
}
@Override
protected void layoutChildren() {
super.layoutChildren();
layoutInArea(frontPage.get(), 0, 0, getWidth(), getHeight(), 0, HPos.LEFT, VPos.TOP);
layoutInArea(backPage.get(), 0, 0, getWidth(), getHeight(), 0, HPos.LEFT, VPos.TOP);
}
private ObjectProperty<Point2D> closedOffset = new SimpleObjectProperty<>(new Point2D(50, 100));
public ObjectProperty<Point2D> closedOffsetProperty() {
return closedOffset;
}
public Point2D getClosedOffset() {
return closedOffset.get();
}
public void setClosedOffset(Point2D from) {
this.closedOffset.set(from);
}
private ObjectProperty<Point2D> openedOffset = new SimpleObjectProperty<>(new Point2D(250, 250));
public ObjectProperty<Point2D> openedOffsetProperty() {
return openedOffset;
}
public Point2D getOpenedOffset() {
return openedOffset.get();
}
public void setOpenedOffset(Point2D openedOffset) {
this.openedOffset.set(openedOffset);
}
private DoubleProperty gripSize = new SimpleDoubleProperty(100);
public DoubleProperty gripSizeProperty() {
return gripSize;
}
public double getGripSize() {
return gripSize.get();
}
public void setGripSize(double gripSize) {
this.gripSize.set(gripSize);
}
private void fixMouseTransparency() {
if (state == State.OPENED) {
frontPage.get().setMouseTransparent(true);
} else if (state == State.CLOSED) {
frontPage.get().setMouseTransparent(false);
}
}
static enum AnimationState { NO_ANIMATION, FOLLOWING_MOVING_MOUSE,
FOLLOWING_DRAGGING_MOUSE, ANIMATION
};
static enum State { CLOSED, OPENED };
private State state = CLOSED;
private AnimationState animState = NO_ANIMATION;
private Timeline animation;
public BendingPages() {
frontPageBack.setStroke(null);
frontPageBack.setId("frontPageBack");
shadow.setStroke(null);
shadow.setId("frontPageShadow");
shadow.setMouseTransparent(true);
getChildren().setAll(backPage.get(), frontPage.get(), frontPageBack, shadow);
backPage.addListener((ObservableValue<? extends Node> arg0, Node arg1, Node arg2) -> {
getChildren().set(0, arg2);
});
frontPage.addListener((ObservableValue<? extends Node> arg0, Node oldPage, Node newPage) -> {
if (bookBend != null) {
bookBend.detach();
}
getChildren().set(1, newPage);
bookBend = new BookBend(newPage, frontPageBack, shadow);
setTarget();
});
addEventFilter(MouseEvent.MOUSE_MOVED, (MouseEvent me) -> {
if (withinGrip(me)) {
animState = FOLLOWING_MOVING_MOUSE;
setTarget(me);
update();
} else if (animState == FOLLOWING_MOVING_MOUSE) {
endFollowingMouse();
}
});
setOnMousePressed((MouseEvent me) -> {
if (withinGrip(me)) {
animState = FOLLOWING_DRAGGING_MOUSE;
me.consume();
} else if (withinPath(me)) {
animState = FOLLOWING_DRAGGING_MOUSE;
me.consume();
}
});
setOnMouseDragged((MouseEvent me) -> {
if (animState == FOLLOWING_DRAGGING_MOUSE) {
setTarget(me);
deltaX = targetX - bookBend.getTargetX();
deltaY = targetY - bookBend.getTargetY();
update();
me.consume();
}
});
setOnMouseExited((MouseEvent me) -> {
if (animState == FOLLOWING_MOVING_MOUSE) {
endFollowingMouse();
me.consume();
}
});
setOnMouseReleased((MouseEvent me) -> {
if (animState == FOLLOWING_DRAGGING_MOUSE && !me.isStillSincePress()) {
endFollowingMouse();
me.consume();
}
});
setOnMouseClicked((MouseEvent me) -> {
if (me.isStillSincePress() && (withinGrip(me) || withinPath(me))) {
if (state == OPENED) {
state = CLOSED;
} else {
state = OPENED;
}
setTarget();
animateTo();
me.consume();
}
});
layoutBoundsProperty().addListener((ObservableValue<? extends Bounds> arg0, Bounds arg1, Bounds arg2) -> {
if (state == State.CLOSED) {
setTarget();
bookBend.update(targetX, targetY);
} else if (state == State.OPENED) {
setTarget();
bookBend.update(targetX, targetY);
}
});
}
private boolean withinGrip(MouseEvent me) {
if (state == CLOSED) {
return getWidth() - me.getX() <= gripSize.doubleValue()
&& getHeight() - me.getY() <= gripSize.doubleValue();
} else {
return me.getX() <= gripSize.doubleValue()
&& me.getY() <= gripSize.doubleValue();
}
}
private boolean withinPath(MouseEvent me) {
boolean contains = frontPageBack.contains(me.getX(), me.getY());
return contains;
}
private void endFollowingMouse() {
if (animState != FOLLOWING_MOVING_MOUSE) {
if (deltaX >= 0 && deltaY >= 0) {
state = CLOSED;
} else {
state = OPENED;
}
}
setTarget();
animateTo();
}
private double targetX, targetY;
private double deltaX, deltaY;
private void setTarget(MouseEvent me) {
if (state == OPENED) {
targetX = me.getX() - (getWidth() - me.getX()) * 0.8;
targetY = me.getY() - (getHeight() - me.getY()) * 0.8;
} else {
targetX = me.getX();
targetY = me.getY();
}
}
private void setTarget() {
if (state == State.CLOSED) {
targetX = getWidth() - closedOffset.get().getX();
targetY = getHeight() - closedOffset.get().getY();
} else if (state == State.OPENED) {
targetX = -getWidth() + openedOffset.get().getX();
targetY = -getHeight() + openedOffset.get().getY();
}
}
private void update() {
bookBend.update(targetX, targetY);
}
private void animateTo() {
final double fx = bookBend.getTargetX();
final double fy = bookBend.getTargetY();
if (animation != null) {
animation.stop();
}
DoubleProperty t = new SimpleDoubleProperty();
t.addListener((ObservableValue<? extends Number> arg0, Number arg1, Number t1) -> {
bookBend.update(fx + (targetX - fx) * t1.doubleValue(), fy + (targetY - fy) * t1.doubleValue());
});
Timeline animation = new Timeline(
new KeyFrame(Duration.millis(200), (ActionEvent arg0) -> { animState = NO_ANIMATION; },
new KeyValue(t, 1, Interpolator.EASE_OUT))
);
animation.play();
animState = ANIMATION;
fixMouseTransparency();
}
public void setColors(Color pathColor, Color bendStartColor, Color bendEndColor) {
bookBend.setColors(pathColor, bendStartColor, bendEndColor);
}
}
