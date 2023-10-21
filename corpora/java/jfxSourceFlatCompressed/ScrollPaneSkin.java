package javafx.scene.control.skin;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import com.sun.javafx.util.Utils;
import com.sun.javafx.scene.control.behavior.ScrollPaneBehavior;
import static com.sun.javafx.scene.control.skin.Utils.*;
import javafx.geometry.Insets;
import java.util.function.Consumer;
public class ScrollPaneSkin extends SkinBase<ScrollPane> {
private static final double DEFAULT_PREF_SIZE = 100.0;
private static final double DEFAULT_MIN_SIZE = 36.0;
private static final double DEFAULT_SB_BREADTH = 12.0;
private static final double DEFAULT_EMBEDDED_SB_BREADTH = 8.0;
private static final double PAN_THRESHOLD = 0.5;
private Node scrollNode;
private final BehaviorBase<ScrollPane> behavior;
private double nodeWidth;
private double nodeHeight;
private boolean nodeSizeInvalid = true;
private double posX;
private double posY;
private boolean hsbvis;
private boolean vsbvis;
private double hsbHeight;
private double vsbWidth;
private StackPane viewRect;
private StackPane viewContent;
private double contentWidth;
private double contentHeight;
private StackPane corner;
ScrollBar hsb;
ScrollBar vsb;
double pressX;
double pressY;
double ohvalue;
double ovvalue;
private Cursor saveCursor = null;
private boolean dragDetected = false;
private boolean touchDetected = false;
private boolean mouseDown = false;
Rectangle clipRect;
Timeline sbTouchTimeline;
KeyFrame sbTouchKF1;
KeyFrame sbTouchKF2;
Timeline contentsToViewTimeline;
KeyFrame contentsToViewKF1;
KeyFrame contentsToViewKF2;
KeyFrame contentsToViewKF3;
private boolean tempVisibility;
private final InvalidationListener nodeListener = new InvalidationListener() {
@Override public void invalidated(Observable valueModel) {
if (!nodeSizeInvalid) {
final Bounds scrollNodeBounds = scrollNode.getLayoutBounds();
final double scrollNodeWidth = scrollNodeBounds.getWidth();
final double scrollNodeHeight = scrollNodeBounds.getHeight();
if (vsbvis != determineVerticalSBVisible() || hsbvis != determineHorizontalSBVisible() ||
(scrollNodeWidth != 0.0 && nodeWidth != scrollNodeWidth) ||
(scrollNodeHeight != 0.0 && nodeHeight != scrollNodeHeight)) {
getSkinnable().requestLayout();
} else {
if (!dragDetected) {
updateVerticalSB();
updateHorizontalSB();
}
}
}
}
};
private final ChangeListener<Bounds> boundsChangeListener = new ChangeListener<Bounds>() {
@Override public void changed(ObservableValue<? extends Bounds> observable, Bounds oldBounds, Bounds newBounds) {
double oldHeight = oldBounds.getHeight();
double newHeight = newBounds.getHeight();
if (oldHeight > 0 && oldHeight != newHeight) {
double oldPositionY = (snapPositionY(snappedTopInset() - posY / (vsb.getMax() - vsb.getMin()) * (oldHeight - contentHeight)));
double newPositionY = (snapPositionY(snappedTopInset() - posY / (vsb.getMax() - vsb.getMin()) * (newHeight - contentHeight)));
double newValueY = (oldPositionY/newPositionY)*vsb.getValue();
if (newValueY < 0.0) {
vsb.setValue(0.0);
}
else if (newValueY < 1.0) {
vsb.setValue(newValueY);
}
else if (newValueY > 1.0) {
vsb.setValue(1.0);
}
}
double oldWidth = oldBounds.getWidth();
double newWidth = newBounds.getWidth();
if (oldWidth > 0 && oldWidth != newWidth) {
double oldPositionX = (snapPositionX(snappedLeftInset() - posX / (hsb.getMax() - hsb.getMin()) * (oldWidth - contentWidth)));
double newPositionX = (snapPositionX(snappedLeftInset() - posX / (hsb.getMax() - hsb.getMin()) * (newWidth - contentWidth)));
double newValueX = (oldPositionX/newPositionX)*hsb.getValue();
if (newValueX < 0.0) {
hsb.setValue(0.0);
}
else if (newValueX < 1.0) {
hsb.setValue(newValueX);
}
else if (newValueX > 1.0) {
hsb.setValue(1.0);
}
}
}
};
public ScrollPaneSkin(final ScrollPane control) {
super(control);
behavior = new ScrollPaneBehavior(control);
initialize();
Consumer<ObservableValue<?>> viewportSizeHintConsumer = e -> {
getSkinnable().requestLayout();
};
registerChangeListener(control.contentProperty(), e -> {
if (scrollNode != getSkinnable().getContent()) {
if (scrollNode != null) {
scrollNode.layoutBoundsProperty().removeListener(nodeListener);
scrollNode.layoutBoundsProperty().removeListener(boundsChangeListener);
viewContent.getChildren().remove(scrollNode);
}
scrollNode = getSkinnable().getContent();
if (scrollNode != null) {
nodeWidth = snapSizeX(scrollNode.getLayoutBounds().getWidth());
nodeHeight = snapSizeY(scrollNode.getLayoutBounds().getHeight());
viewContent.getChildren().setAll(scrollNode);
scrollNode.layoutBoundsProperty().addListener(nodeListener);
scrollNode.layoutBoundsProperty().addListener(boundsChangeListener);
}
}
getSkinnable().requestLayout();
});
registerChangeListener(control.fitToWidthProperty(), e -> {
getSkinnable().requestLayout();
viewRect.requestLayout();
});
registerChangeListener(control.fitToHeightProperty(), e -> {
getSkinnable().requestLayout();
viewRect.requestLayout();
});
registerChangeListener(control.hbarPolicyProperty(), e -> {
getSkinnable().requestLayout();
});
registerChangeListener(control.vbarPolicyProperty(), e -> {
getSkinnable().requestLayout();
});
registerChangeListener(control.hvalueProperty(), e -> hsb.setValue(getSkinnable().getHvalue()));
registerChangeListener(control.hmaxProperty(), e -> hsb.setMax(getSkinnable().getHmax()));
registerChangeListener(control.hminProperty(), e -> hsb.setMin(getSkinnable().getHmin()));
registerChangeListener(control.vvalueProperty(), e -> vsb.setValue(getSkinnable().getVvalue()));
registerChangeListener(control.vmaxProperty(), e -> vsb.setMax(getSkinnable().getVmax()));
registerChangeListener(control.vminProperty(), e -> vsb.setMin(getSkinnable().getVmin()));
registerChangeListener(control.prefViewportWidthProperty(), viewportSizeHintConsumer);
registerChangeListener(control.prefViewportHeightProperty(), viewportSizeHintConsumer);
registerChangeListener(control.minViewportWidthProperty(), viewportSizeHintConsumer);
registerChangeListener(control.minViewportHeightProperty(), viewportSizeHintConsumer);
}
private DoubleProperty contentPosX;
private final void setContentPosX(double value) { contentPosXProperty().set(value); }
private final double getContentPosX() { return contentPosX == null ? 0.0 : contentPosX.get(); }
private final DoubleProperty contentPosXProperty() {
if (contentPosX == null) {
contentPosX = new DoublePropertyBase() {
@Override protected void invalidated() {
hsb.setValue(getContentPosX());
getSkinnable().requestLayout();
}
@Override
public Object getBean() {
return ScrollPaneSkin.this;
}
@Override
public String getName() {
return "contentPosX";
}
};
}
return contentPosX;
}
private DoubleProperty contentPosY;
private final void setContentPosY(double value) { contentPosYProperty().set(value); }
private final double getContentPosY() { return contentPosY == null ? 0.0 : contentPosY.get(); }
private final DoubleProperty contentPosYProperty() {
if (contentPosY == null) {
contentPosY = new DoublePropertyBase() {
@Override protected void invalidated() {
vsb.setValue(getContentPosY());
getSkinnable().requestLayout();
}
@Override
public Object getBean() {
return ScrollPaneSkin.this;
}
@Override
public String getName() {
return "contentPosY";
}
};
}
return contentPosY;
}
@Override public void dispose() {
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
public final ScrollBar getHorizontalScrollBar() {
return hsb;
}
public final ScrollBar getVerticalScrollBar() {
return vsb;
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
final ScrollPane sp = getSkinnable();
double vsbWidth = computeVsbSizeHint(sp);
double minWidth = vsbWidth + snappedLeftInset() + snappedRightInset();
if (sp.getPrefViewportWidth() > 0) {
return (sp.getPrefViewportWidth() + minWidth);
}
else if (sp.getContent() != null) {
return (sp.getContent().prefWidth(height) + minWidth);
}
else {
return Math.max(minWidth, DEFAULT_PREF_SIZE);
}
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
final ScrollPane sp = getSkinnable();
double hsbHeight = computeHsbSizeHint(sp);
double minHeight = hsbHeight + snappedTopInset() + snappedBottomInset();
if (sp.getPrefViewportHeight() > 0) {
return (sp.getPrefViewportHeight() + minHeight);
}
else if (sp.getContent() != null) {
return (sp.getContent().prefHeight(width) + minHeight);
}
else {
return Math.max(minHeight, DEFAULT_PREF_SIZE);
}
}
@Override protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
final ScrollPane sp = getSkinnable();
double vsbWidth = computeVsbSizeHint(sp);
double minWidth = vsbWidth + snappedLeftInset() + snappedRightInset();
if (sp.getMinViewportWidth() > 0) {
return (sp.getMinViewportWidth() + minWidth);
} else {
double w = corner.minWidth(-1);
return (w > 0) ? (3 * w) : (DEFAULT_MIN_SIZE);
}
}
@Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
final ScrollPane sp = getSkinnable();
double hsbHeight = computeHsbSizeHint(sp);
double minHeight = hsbHeight + snappedTopInset() + snappedBottomInset();
if (sp.getMinViewportHeight() > 0) {
return (sp.getMinViewportHeight() + minHeight);
} else {
double h = corner.minHeight(-1);
return (h > 0) ? (3 * h) : (DEFAULT_MIN_SIZE);
}
}
@Override protected void layoutChildren(final double x, final double y,
final double w, final double h) {
final ScrollPane control = getSkinnable();
final Insets padding = control.getPadding();
final double rightPadding = snapSizeX(padding.getRight());
final double leftPadding = snapSizeX(padding.getLeft());
final double topPadding = snapSizeY(padding.getTop());
final double bottomPadding = snapSizeY(padding.getBottom());
vsb.setMin(control.getVmin());
vsb.setMax(control.getVmax());
hsb.setMin(control.getHmin());
hsb.setMax(control.getHmax());
contentWidth = w;
contentHeight = h;
double hsbWidth = 0;
double vsbHeight = 0;
computeScrollNodeSize(contentWidth, contentHeight);
computeScrollBarSize();
for (int i = 0; i < 2; ++i) {
vsbvis = determineVerticalSBVisible();
hsbvis = determineHorizontalSBVisible();
if (vsbvis && !Properties.IS_TOUCH_SUPPORTED) {
contentWidth = w - vsbWidth;
}
hsbWidth = w + leftPadding + rightPadding - (vsbvis ? vsbWidth : 0);
if (hsbvis && !Properties.IS_TOUCH_SUPPORTED) {
contentHeight = h - hsbHeight;
}
vsbHeight = h + topPadding + bottomPadding - (hsbvis ? hsbHeight : 0);
}
if (scrollNode != null && scrollNode.isResizable()) {
if (vsbvis && hsbvis) {
computeScrollNodeSize(contentWidth, contentHeight);
} else if (hsbvis && !vsbvis) {
computeScrollNodeSize(contentWidth, contentHeight);
vsbvis = determineVerticalSBVisible();
if (vsbvis) {
contentWidth -= vsbWidth;
hsbWidth -= vsbWidth;
computeScrollNodeSize(contentWidth, contentHeight);
}
} else if (vsbvis && !hsbvis) {
computeScrollNodeSize(contentWidth, contentHeight);
hsbvis = determineHorizontalSBVisible();
if (hsbvis) {
contentHeight -= hsbHeight;
vsbHeight -= hsbHeight;
computeScrollNodeSize(contentWidth, contentHeight);
}
}
}
double cx = snappedLeftInset() - leftPadding;
double cy = snappedTopInset() - topPadding;
vsb.setVisible(vsbvis);
if (vsbvis) {
vsb.resizeRelocate(snappedLeftInset() + w - vsbWidth + (rightPadding < 1 ? 0 : rightPadding - 1) ,
cy, vsbWidth, vsbHeight);
}
updateVerticalSB();
hsb.setVisible(hsbvis);
if (hsbvis) {
hsb.resizeRelocate(cx, snappedTopInset() + h - hsbHeight + (bottomPadding < 1 ? 0 : bottomPadding - 1),
hsbWidth, hsbHeight);
}
updateHorizontalSB();
viewRect.resizeRelocate(snappedLeftInset(), snappedTopInset(), snapSizeX(contentWidth), snapSizeY(contentHeight));
resetClip();
if (vsbvis && hsbvis) {
corner.setVisible(true);
double cornerWidth = vsbWidth;
double cornerHeight = hsbHeight;
corner.resizeRelocate(snapPositionX(vsb.getLayoutX()), snapPositionY(hsb.getLayoutY()), snapSizeX(cornerWidth), snapSizeY(cornerHeight));
} else {
corner.setVisible(false);
}
control.setViewportBounds(new BoundingBox(snapPositionX(viewContent.getLayoutX()), snapPositionY(viewContent.getLayoutY()), snapSizeX(contentWidth), snapSizeY(contentHeight)));
}
@Override protected Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case VERTICAL_SCROLLBAR: return vsb;
case HORIZONTAL_SCROLLBAR: return hsb;
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
private void initialize() {
ScrollPane control = getSkinnable();
scrollNode = control.getContent();
ParentTraversalEngine traversalEngine = new ParentTraversalEngine(getSkinnable());
traversalEngine.addTraverseListener((node, bounds) -> {
scrollBoundsIntoView(bounds);
});
ParentHelper.setTraversalEngine(getSkinnable(), traversalEngine);
if (scrollNode != null) {
scrollNode.layoutBoundsProperty().addListener(nodeListener);
scrollNode.layoutBoundsProperty().addListener(boundsChangeListener);
}
viewRect = new StackPane() {
@Override protected void layoutChildren() {
viewContent.resize(getWidth(), getHeight());
}
};
viewRect.setManaged(false);
viewRect.setCache(true);
viewRect.getStyleClass().add("viewport");
clipRect = new Rectangle();
viewRect.setClip(clipRect);
hsb = new ScrollBar();
vsb = new ScrollBar();
vsb.setOrientation(Orientation.VERTICAL);
EventHandler<MouseEvent> barHandler = ev -> {
if (getSkinnable().isFocusTraversable()) {
getSkinnable().requestFocus();
}
};
hsb.addEventFilter(MouseEvent.MOUSE_PRESSED, barHandler);
vsb.addEventFilter(MouseEvent.MOUSE_PRESSED, barHandler);
corner = new StackPane();
corner.getStyleClass().setAll("corner");
viewContent = new StackPane() {
@Override public void requestLayout() {
nodeSizeInvalid = true;
super.requestLayout();
ScrollPaneSkin.this.getSkinnable().requestLayout();
}
@Override protected void layoutChildren() {
if (nodeSizeInvalid) {
computeScrollNodeSize(getWidth(),getHeight());
}
if (scrollNode != null && scrollNode.isResizable()) {
scrollNode.resize(snapSizeX(nodeWidth), snapSizeY(nodeHeight));
if (vsbvis != determineVerticalSBVisible() || hsbvis != determineHorizontalSBVisible()) {
getSkinnable().requestLayout();
}
}
if (scrollNode != null) {
scrollNode.relocate(0,0);
}
}
};
viewRect.getChildren().add(viewContent);
if (scrollNode != null) {
viewContent.getChildren().add(scrollNode);
viewRect.nodeOrientationProperty().bind(scrollNode.nodeOrientationProperty());
}
getChildren().clear();
getChildren().addAll(viewRect, vsb, hsb, corner);
InvalidationListener vsbListener = valueModel -> {
if (!Properties.IS_TOUCH_SUPPORTED) {
posY = Utils.clamp(getSkinnable().getVmin(), vsb.getValue(), getSkinnable().getVmax());
}
else {
posY = vsb.getValue();
}
updatePosY();
};
vsb.valueProperty().addListener(vsbListener);
InvalidationListener hsbListener = valueModel -> {
if (!Properties.IS_TOUCH_SUPPORTED) {
posX = Utils.clamp(getSkinnable().getHmin(), hsb.getValue(), getSkinnable().getHmax());
}
else {
posX = hsb.getValue();
}
updatePosX();
};
hsb.valueProperty().addListener(hsbListener);
viewRect.setOnMousePressed(e -> {
mouseDown = true;
if (Properties.IS_TOUCH_SUPPORTED) {
startSBReleasedAnimation();
}
pressX = e.getX();
pressY = e.getY();
ohvalue = hsb.getValue();
ovvalue = vsb.getValue();
});
viewRect.setOnDragDetected(e -> {
if (Properties.IS_TOUCH_SUPPORTED) {
startSBReleasedAnimation();
}
if (getSkinnable().isPannable()) {
dragDetected = true;
if (saveCursor == null) {
saveCursor = getSkinnable().getCursor();
if (saveCursor == null) {
saveCursor = Cursor.DEFAULT;
}
getSkinnable().setCursor(Cursor.MOVE);
getSkinnable().requestLayout();
}
}
});
viewRect.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
mouseDown = false;
if (dragDetected == true) {
if (saveCursor != null) {
getSkinnable().setCursor(saveCursor);
saveCursor = null;
getSkinnable().requestLayout();
}
dragDetected = false;
}
if ((posY > getSkinnable().getVmax() || posY < getSkinnable().getVmin() ||
posX > getSkinnable().getHmax() || posX < getSkinnable().getHmin()) && !touchDetected) {
startContentsToViewport();
}
});
viewRect.setOnMouseDragged(e -> {
if (Properties.IS_TOUCH_SUPPORTED) {
startSBReleasedAnimation();
}
if (getSkinnable().isPannable() || Properties.IS_TOUCH_SUPPORTED) {
double deltaX = pressX - e.getX();
double deltaY = pressY - e.getY();
if (hsb.getVisibleAmount() > 0.0 && hsb.getVisibleAmount() < hsb.getMax()) {
if (Math.abs(deltaX) > PAN_THRESHOLD) {
if (isReverseNodeOrientation()) {
deltaX = -deltaX;
}
double newHVal = (ohvalue + deltaX / (nodeWidth - viewRect.getWidth()) * (hsb.getMax() - hsb.getMin()));
if (!Properties.IS_TOUCH_SUPPORTED) {
if (newHVal > hsb.getMax()) {
newHVal = hsb.getMax();
}
else if (newHVal < hsb.getMin()) {
newHVal = hsb.getMin();
}
hsb.setValue(newHVal);
}
else {
hsb.setValue(newHVal);
}
}
}
if (vsb.getVisibleAmount() > 0.0 && vsb.getVisibleAmount() < vsb.getMax()) {
if (Math.abs(deltaY) > PAN_THRESHOLD) {
double newVVal = (ovvalue + deltaY / (nodeHeight - viewRect.getHeight()) * (vsb.getMax() - vsb.getMin()));
if (!Properties.IS_TOUCH_SUPPORTED) {
if (newVVal > vsb.getMax()) {
newVVal = vsb.getMax();
}
else if (newVVal < vsb.getMin()) {
newVVal = vsb.getMin();
}
vsb.setValue(newVVal);
}
else {
vsb.setValue(newVVal);
}
}
}
}
e.consume();
});
final EventDispatcher blockEventDispatcher = (event, tail) -> event;
final EventDispatcher oldHsbEventDispatcher = hsb.getEventDispatcher();
hsb.setEventDispatcher((event, tail) -> {
if (event.getEventType() == ScrollEvent.SCROLL &&
!((ScrollEvent)event).isDirect()) {
tail = tail.prepend(blockEventDispatcher);
tail = tail.prepend(oldHsbEventDispatcher);
return tail.dispatchEvent(event);
}
return oldHsbEventDispatcher.dispatchEvent(event, tail);
});
final EventDispatcher oldVsbEventDispatcher = vsb.getEventDispatcher();
vsb.setEventDispatcher((event, tail) -> {
if (event.getEventType() == ScrollEvent.SCROLL &&
!((ScrollEvent)event).isDirect()) {
tail = tail.prepend(blockEventDispatcher);
tail = tail.prepend(oldVsbEventDispatcher);
return tail.dispatchEvent(event);
}
return oldVsbEventDispatcher.dispatchEvent(event, tail);
});
viewRect.addEventHandler(ScrollEvent.SCROLL, event -> {
if (Properties.IS_TOUCH_SUPPORTED) {
startSBReleasedAnimation();
}
if (vsb.getVisibleAmount() < vsb.getMax()) {
double vRange = getSkinnable().getVmax()-getSkinnable().getVmin();
double hDelta = nodeHeight - contentHeight;
double vPixelValue = hDelta > 0.0 ? vRange / hDelta : 0.0;
double newValue = vsb.getValue()+(-event.getDeltaY())*vPixelValue;
if (!Properties.IS_TOUCH_SUPPORTED) {
if ((event.getDeltaY() > 0.0 && vsb.getValue() > vsb.getMin()) ||
(event.getDeltaY() < 0.0 && vsb.getValue() < vsb.getMax())) {
vsb.setValue(newValue);
event.consume();
}
}
else {
if (!(((ScrollEvent)event).isInertia()) || (((ScrollEvent)event).isInertia()) && (contentsToViewTimeline == null || contentsToViewTimeline.getStatus() == Status.STOPPED)) {
vsb.setValue(newValue);
if ((newValue > vsb.getMax() || newValue < vsb.getMin()) && (!mouseDown && !touchDetected)) {
startContentsToViewport();
}
event.consume();
}
}
}
if (hsb.getVisibleAmount() < hsb.getMax()) {
double hRange = getSkinnable().getHmax()-getSkinnable().getHmin();
double wDelta = nodeWidth - contentWidth;
double hPixelValue = wDelta > 0.0 ? hRange / wDelta : 0.0;
double newValue = hsb.getValue()+(-event.getDeltaX())*hPixelValue;
if (!Properties.IS_TOUCH_SUPPORTED) {
if ((event.getDeltaX() > 0.0 && hsb.getValue() > hsb.getMin()) ||
(event.getDeltaX() < 0.0 && hsb.getValue() < hsb.getMax())) {
hsb.setValue(newValue);
event.consume();
}
}
else {
if (!(((ScrollEvent)event).isInertia()) || (((ScrollEvent)event).isInertia()) && (contentsToViewTimeline == null || contentsToViewTimeline.getStatus() == Status.STOPPED)) {
hsb.setValue(newValue);
if ((newValue > hsb.getMax() || newValue < hsb.getMin()) && (!mouseDown && !touchDetected)) {
startContentsToViewport();
}
event.consume();
}
}
}
});
getSkinnable().addEventHandler(TouchEvent.TOUCH_PRESSED, e -> {
touchDetected = true;
startSBReleasedAnimation();
e.consume();
});
getSkinnable().addEventHandler(TouchEvent.TOUCH_RELEASED, e -> {
touchDetected = false;
e.consume();
});
consumeMouseEvents(false);
hsb.setValue(control.getHvalue());
vsb.setValue(control.getVvalue());
}
void scrollBoundsIntoView(Bounds b) {
double dx = 0.0;
double dy = 0.0;
if (b.getMaxX() > contentWidth) {
dx = b.getMinX() - snappedLeftInset();
}
if (b.getMinX() < snappedLeftInset()) {
dx = b.getMaxX() - contentWidth - snappedLeftInset();
}
if (b.getMaxY() > snappedTopInset() + contentHeight) {
dy = b.getMinY() - snappedTopInset();
}
if (b.getMinY() < snappedTopInset()) {
dy = b.getMaxY() - contentHeight - snappedTopInset();
}
if (dx != 0) {
double wd = nodeWidth - contentWidth;
double sdx = wd > 0.0 ? dx * (hsb.getMax() - hsb.getMin()) / wd : 0;
sdx += -1 * Math.signum(sdx) * hsb.getUnitIncrement() / 5;
hsb.setValue(hsb.getValue() + sdx);
getSkinnable().requestLayout();
}
if (dy != 0) {
double hd = nodeHeight - contentHeight;
double sdy = hd > 0.0 ? dy * (vsb.getMax() - vsb.getMin()) / hd : 0.0;
sdy += -1 * Math.signum(sdy) * vsb.getUnitIncrement() / 5;
vsb.setValue(vsb.getValue() + sdy);
getSkinnable().requestLayout();
}
}
private double computeHsbSizeHint(ScrollPane sp) {
return ((sp.getHbarPolicy() == ScrollBarPolicy.ALWAYS) ||
(sp.getHbarPolicy() == ScrollBarPolicy.AS_NEEDED && (sp.getPrefViewportHeight() > 0 || sp.getMinViewportHeight() > 0)))
? hsb.prefHeight(ScrollBar.USE_COMPUTED_SIZE)
: 0;
}
private double computeVsbSizeHint(ScrollPane sp) {
return ((sp.getVbarPolicy() == ScrollBarPolicy.ALWAYS) ||
(sp.getVbarPolicy() == ScrollBarPolicy.AS_NEEDED && (sp.getPrefViewportWidth() > 0
|| sp.getMinViewportWidth() > 0)))
? vsb.prefWidth(ScrollBar.USE_COMPUTED_SIZE)
: 0;
}
private void computeScrollNodeSize(double contentWidth, double contentHeight) {
if (scrollNode != null) {
if (scrollNode.isResizable()) {
ScrollPane control = getSkinnable();
Orientation bias = scrollNode.getContentBias();
if (bias == null) {
nodeWidth = snapSizeX(boundedSize(control.isFitToWidth()? contentWidth : scrollNode.prefWidth(-1),
scrollNode.minWidth(-1),scrollNode.maxWidth(-1)));
nodeHeight = snapSizeY(boundedSize(control.isFitToHeight()? contentHeight : scrollNode.prefHeight(-1),
scrollNode.minHeight(-1), scrollNode.maxHeight(-1)));
} else if (bias == Orientation.HORIZONTAL) {
nodeWidth = snapSizeX(boundedSize(control.isFitToWidth()? contentWidth : scrollNode.prefWidth(-1),
scrollNode.minWidth(-1),scrollNode.maxWidth(-1)));
nodeHeight = snapSizeY(boundedSize(control.isFitToHeight()? contentHeight : scrollNode.prefHeight(nodeWidth),
scrollNode.minHeight(nodeWidth),scrollNode.maxHeight(nodeWidth)));
} else {
nodeHeight = snapSizeY(boundedSize(control.isFitToHeight()? contentHeight : scrollNode.prefHeight(-1),
scrollNode.minHeight(-1), scrollNode.maxHeight(-1)));
nodeWidth = snapSizeX(boundedSize(control.isFitToWidth()? contentWidth : scrollNode.prefWidth(nodeHeight),
scrollNode.minWidth(nodeHeight),scrollNode.maxWidth(nodeHeight)));
}
} else {
nodeWidth = snapSizeX(scrollNode.getLayoutBounds().getWidth());
nodeHeight = snapSizeY(scrollNode.getLayoutBounds().getHeight());
}
nodeSizeInvalid = false;
}
}
private boolean isReverseNodeOrientation() {
return (scrollNode != null &&
getSkinnable().getEffectiveNodeOrientation() !=
scrollNode.getEffectiveNodeOrientation());
}
private boolean determineHorizontalSBVisible() {
final ScrollPane sp = getSkinnable();
if (Properties.IS_TOUCH_SUPPORTED) {
return (tempVisibility && (nodeWidth > contentWidth));
}
else {
ScrollBarPolicy hbarPolicy = sp.getHbarPolicy();
return (ScrollBarPolicy.NEVER == hbarPolicy) ? false :
((ScrollBarPolicy.ALWAYS == hbarPolicy) ? true :
((sp.isFitToWidth() && scrollNode != null ? scrollNode.isResizable() : false) ?
(nodeWidth > contentWidth && scrollNode.minWidth(-1) > contentWidth) : (nodeWidth > contentWidth)));
}
}
private boolean determineVerticalSBVisible() {
final ScrollPane sp = getSkinnable();
if (Properties.IS_TOUCH_SUPPORTED) {
return (tempVisibility && (nodeHeight > contentHeight));
}
else {
ScrollBarPolicy vbarPolicy = sp.getVbarPolicy();
return (ScrollBarPolicy.NEVER == vbarPolicy) ? false :
((ScrollBarPolicy.ALWAYS == vbarPolicy) ? true :
((sp.isFitToHeight() && scrollNode != null ? scrollNode.isResizable() : false) ?
(nodeHeight > contentHeight && scrollNode.minHeight(-1) > contentHeight) : (nodeHeight > contentHeight)));
}
}
private void computeScrollBarSize() {
vsbWidth = snapSizeX(vsb.prefWidth(-1));
if (vsbWidth == 0) {
if (Properties.IS_TOUCH_SUPPORTED) {
vsbWidth = DEFAULT_EMBEDDED_SB_BREADTH;
}
else {
vsbWidth = DEFAULT_SB_BREADTH;
}
}
hsbHeight = snapSizeY(hsb.prefHeight(-1));
if (hsbHeight == 0) {
if (Properties.IS_TOUCH_SUPPORTED) {
hsbHeight = DEFAULT_EMBEDDED_SB_BREADTH;
}
else {
hsbHeight = DEFAULT_SB_BREADTH;
}
}
}
private void updateHorizontalSB() {
double contentRatio = nodeWidth * (hsb.getMax() - hsb.getMin());
if (contentRatio > 0.0) {
hsb.setVisibleAmount(contentWidth / contentRatio);
hsb.setBlockIncrement(0.9 * hsb.getVisibleAmount());
hsb.setUnitIncrement(0.1 * hsb.getVisibleAmount());
}
else {
hsb.setVisibleAmount(0.0);
hsb.setBlockIncrement(0.0);
hsb.setUnitIncrement(0.0);
}
if (hsb.isVisible()) {
updatePosX();
} else {
if (nodeWidth > contentWidth) {
updatePosX();
} else {
viewContent.setLayoutX(0);
}
}
}
private void updateVerticalSB() {
double contentRatio = nodeHeight * (vsb.getMax() - vsb.getMin());
if (contentRatio > 0.0) {
vsb.setVisibleAmount(contentHeight / contentRatio);
vsb.setBlockIncrement(0.9 * vsb.getVisibleAmount());
vsb.setUnitIncrement(0.1 * vsb.getVisibleAmount());
}
else {
vsb.setVisibleAmount(0.0);
vsb.setBlockIncrement(0.0);
vsb.setUnitIncrement(0.0);
}
if (vsb.isVisible()) {
updatePosY();
} else {
if (nodeHeight > contentHeight) {
updatePosY();
} else {
viewContent.setLayoutY(0);
}
}
}
private double updatePosX() {
final ScrollPane sp = getSkinnable();
double x = isReverseNodeOrientation() ? (hsb.getMax() - (posX - hsb.getMin())) : posX;
double hsbRange = hsb.getMax() - hsb.getMin();
double minX = hsbRange > 0 ? -x / hsbRange * (nodeWidth - contentWidth) : 0;
if (!Properties.IS_TOUCH_SUPPORTED) {
minX = Math.min(minX, 0);
}
viewContent.setLayoutX(snapPositionX(minX));
if (!sp.hvalueProperty().isBound()) sp.setHvalue(Utils.clamp(sp.getHmin(), posX, sp.getHmax()));
return posX;
}
private double updatePosY() {
final ScrollPane sp = getSkinnable();
double vsbRange = vsb.getMax() - vsb.getMin();
double minY = vsbRange > 0 ? -posY / vsbRange * (nodeHeight - contentHeight) : 0;
if (!Properties.IS_TOUCH_SUPPORTED) {
minY = Math.min(minY, 0);
}
viewContent.setLayoutY(snapPositionY(minY));
if (!sp.vvalueProperty().isBound()) sp.setVvalue(Utils.clamp(sp.getVmin(), posY, sp.getVmax()));
return posY;
}
private void resetClip() {
clipRect.setWidth(snapSizeX(contentWidth));
clipRect.setHeight(snapSizeY(contentHeight));
}
private void startSBReleasedAnimation() {
if (sbTouchTimeline == null) {
sbTouchTimeline = new Timeline();
sbTouchKF1 = new KeyFrame(Duration.millis(0), event -> {
tempVisibility = true;
if ((touchDetected == true || mouseDown == true) && NodeHelper.isTreeShowing(getSkinnable())) {
sbTouchTimeline.playFromStart();
}
});
sbTouchKF2 = new KeyFrame(Duration.millis(1000), event -> {
tempVisibility = false;
getSkinnable().requestLayout();
});
sbTouchTimeline.getKeyFrames().addAll(sbTouchKF1, sbTouchKF2);
}
sbTouchTimeline.playFromStart();
}
private void startContentsToViewport() {
double newPosX = posX;
double newPosY = posY;
setContentPosX(posX);
setContentPosY(posY);
if (posY > getSkinnable().getVmax()) {
newPosY = getSkinnable().getVmax();
}
else if (posY < getSkinnable().getVmin()) {
newPosY = getSkinnable().getVmin();
}
if (posX > getSkinnable().getHmax()) {
newPosX = getSkinnable().getHmax();
}
else if (posX < getSkinnable().getHmin()) {
newPosX = getSkinnable().getHmin();
}
if (!Properties.IS_TOUCH_SUPPORTED) {
startSBReleasedAnimation();
}
if (contentsToViewTimeline != null) {
contentsToViewTimeline.stop();
}
contentsToViewTimeline = new Timeline();
contentsToViewKF1 = new KeyFrame(Duration.millis(50));
contentsToViewKF2 = new KeyFrame(Duration.millis(150), event -> {
getSkinnable().requestLayout();
},
new KeyValue(contentPosX, newPosX),
new KeyValue(contentPosY, newPosY)
);
contentsToViewKF3 = new KeyFrame(Duration.millis(1500));
contentsToViewTimeline.getKeyFrames().addAll(contentsToViewKF1, contentsToViewKF2, contentsToViewKF3);
contentsToViewTimeline.playFromStart();
}
}
