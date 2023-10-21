package ensemble.control;
import java.util.LinkedList;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
public class Popover extends Region implements EventHandler<Event>{
private static final int PAGE_GAP = 15;
private final Region frameBorder = new Region();
private final Button leftButton = new Button("Left");
private final Button rightButton = new Button("Right");
private final LinkedList<Page> pages = new LinkedList<Page>();
private final Pane pagesPane = new Pane();
private final Rectangle pagesClipRect = new Rectangle();
private final Pane titlesPane = new Pane();
private Text title;
private final Rectangle titlesClipRect = new Rectangle();
private final EventHandler<MouseEvent> popoverHideHandler;
private Runnable onHideCallback = null;
private double maxPopupHeight = -1;
private DoubleProperty popoverHeight = new SimpleDoubleProperty(400) {
@Override protected void invalidated() {
requestLayout();
}
};
public Popover() {
getStyleClass().setAll("popover");
frameBorder.getStyleClass().setAll("popover-frame");
frameBorder.setMouseTransparent(true);
leftButton.setOnMouseClicked(this);
leftButton.getStyleClass().add("popover-left-button");
leftButton.setMinWidth(USE_PREF_SIZE);
rightButton.setOnMouseClicked(this);
rightButton.getStyleClass().add("popover-right-button");
rightButton.setMinWidth(USE_PREF_SIZE);
pagesClipRect.setSmooth(false);
pagesPane.setClip(pagesClipRect);
titlesClipRect.setSmooth(false);
titlesPane.setClip(titlesClipRect);
getChildren().addAll(pagesPane, frameBorder, titlesPane, leftButton, rightButton);
setVisible(false);
setOpacity(0);
setScaleX(.8);
setScaleY(.8);
popoverHideHandler = (MouseEvent t) -> {
Point2D mouseInFilterPane = sceneToLocal(t.getX(), t.getY());
if (mouseInFilterPane.getX() < 0 || mouseInFilterPane.getX() > (getWidth()) ||
mouseInFilterPane.getY() < 0 || mouseInFilterPane.getY() > (getHeight())) {
hide();
t.consume();
}
};
}
@Override public void handle(Event event) {
if (event.getSource() == leftButton) {
pages.getFirst().handleLeftButton();
} else if (event.getSource() == rightButton) {
pages.getFirst().handleRightButton();
}
}
@Override protected double computeMinWidth(double height) {
Page page = pages.isEmpty() ? null : pages.getFirst();
if (page != null) {
Node n = page.getPageNode();
if (n != null) {
Insets insets = getInsets();
return insets.getLeft() + n.minWidth(-1) + insets.getRight();
}
}
return 200;
}
@Override protected double computeMinHeight(double width) {
Insets insets = getInsets();
return insets.getLeft() + 100 + insets.getRight();
}
@Override protected double computePrefWidth(double height) {
Page page = pages.isEmpty() ? null : pages.getFirst();
if (page != null) {
Node n = page.getPageNode();
if (n != null) {
Insets insets = getInsets();
return insets.getLeft() + n.prefWidth(-1) + insets.getRight();
}
}
return 400;
}
@Override protected double computePrefHeight(double width) {
double minHeight = minHeight(-1);
double maxHeight = maxHeight(-1);
double prefHeight = popoverHeight.get();
if (prefHeight == -1) {
Page page = pages.getFirst();
if (page != null) {
Insets inset = getInsets();
if (width == -1) {
width = prefWidth(-1);
}
double contentWidth = width - inset.getLeft() - inset.getRight();
double contentHeight = page.getPageNode().prefHeight(contentWidth);
prefHeight = inset.getTop() + contentHeight + inset.getBottom();
popoverHeight.set(prefHeight);
} else {
prefHeight = minHeight;
}
}
return boundedSize(minHeight, prefHeight, maxHeight);
}
static double boundedSize(double min, double pref, double max) {
double a = pref >= min ? pref : min;
double b = min >= max ? min : max;
return a <= b ? a : b;
}
@Override protected double computeMaxWidth(double height) {
return Double.MAX_VALUE;
}
@Override protected double computeMaxHeight(double width) {
Scene scene = getScene();
if (scene != null) {
return scene.getHeight() - 100;
} else {
return Double.MAX_VALUE;
}
}
@Override protected void layoutChildren() {
if (maxPopupHeight == -1) {
maxPopupHeight = (int)getScene().getHeight()-100;
}
final Insets insets = getInsets();
final double width = getWidth();
final double height = getHeight();
final double top = insets.getTop();
final double right = insets.getRight();
final double bottom = insets.getBottom();
final double left = insets.getLeft();
double pageWidth = width - left - right;
double pageHeight = height - top - bottom;
frameBorder.resize(width, height);
pagesPane.resizeRelocate(left, top, pageWidth, pageHeight);
pagesClipRect.setWidth(pageWidth);
pagesClipRect.setHeight(pageHeight);
double pageX = 0;
for (Node page : pagesPane.getChildren()) {
page.resizeRelocate(pageX, 0, pageWidth, pageHeight);
pageX += pageWidth + PAGE_GAP;
}
double buttonHeight = leftButton.prefHeight(-1);
if (buttonHeight < 30) buttonHeight = 30;
final double buttonTop = (top-buttonHeight) / 2.0;
final double leftButtonWidth = snapSizeX(leftButton.prefWidth(-1));
leftButton.resizeRelocate(left, buttonTop,leftButtonWidth,buttonHeight);
final double rightButtonWidth = snapSizeX(rightButton.prefWidth(-1));
rightButton.resizeRelocate(width-right-rightButtonWidth, buttonTop,rightButtonWidth,buttonHeight);
final double leftButtonRight = leftButton.isVisible() ? (left + leftButtonWidth) : left;
final double rightButtonLeft = rightButton.isVisible() ? (right + rightButtonWidth) : right;
titlesClipRect.setX(leftButtonRight);
titlesClipRect.setWidth(pageWidth - leftButtonRight - rightButtonLeft);
titlesClipRect.setHeight(top);
if (title != null) {
title.setTranslateY((int) (top / 2d));
}
}
public final void clearPages() {
while (!pages.isEmpty()) {
pages.pop().handleHidden();
}
pagesPane.getChildren().clear();
titlesPane.getChildren().clear();
pagesClipRect.setX(0);
pagesClipRect.setWidth(400);
pagesClipRect.setHeight(400);
popoverHeight.set(400);
pagesPane.setTranslateX(0);
titlesPane.setTranslateX(0);
titlesClipRect.setTranslateX(0);
}
public final void popPage() {
Page oldPage = pages.pop();
oldPage.handleHidden();
oldPage.setPopover(null);
Page page = pages.getFirst();
leftButton.setVisible(page.leftButtonText() != null);
leftButton.setText(page.leftButtonText());
rightButton.setVisible(page.rightButtonText() != null);
rightButton.setText(page.rightButtonText());
if (pages.size() > 0) {
final Insets insets = getInsets();
final int width = (int)prefWidth(-1);
final int right = (int)insets.getRight();
final int left = (int)insets.getLeft();
int pageWidth = width - left - right;
final int newPageX = (pageWidth+PAGE_GAP) * (pages.size()-1);
new Timeline(
new KeyFrame(Duration.millis(350), (ActionEvent t) -> {
pagesPane.setCache(false);
pagesPane.getChildren().remove(pagesPane.getChildren().size()-1);
titlesPane.getChildren().remove(titlesPane.getChildren().size()-1);
resizePopoverToNewPage(pages.getFirst().getPageNode());
},
new KeyValue(pagesPane.translateXProperty(), -newPageX, Interpolator.EASE_BOTH),
new KeyValue(titlesPane.translateXProperty(), -newPageX, Interpolator.EASE_BOTH),
new KeyValue(pagesClipRect.xProperty(), newPageX, Interpolator.EASE_BOTH),
new KeyValue(titlesClipRect.translateXProperty(), newPageX, Interpolator.EASE_BOTH)
)
).play();
} else {
hide();
}
}
public final void pushPage(final Page page) {
final Node pageNode = page.getPageNode();
pageNode.setManaged(false);
pagesPane.getChildren().add(pageNode);
final Insets insets = getInsets();
final int pageWidth = (int)(prefWidth(-1) - insets.getLeft() - insets.getRight());
final int newPageX = (pageWidth + PAGE_GAP) * pages.size();
leftButton.setVisible(page.leftButtonText() != null);
leftButton.setText(page.leftButtonText());
rightButton.setVisible(page.rightButtonText() != null);
rightButton.setText(page.rightButtonText());
title = new Text(page.getPageTitle());
title.getStyleClass().add("popover-title");
title.setTextOrigin(VPos.CENTER);
title.setTranslateX(newPageX + (int) ((pageWidth - title.getLayoutBounds().getWidth()) / 2d));
titlesPane.getChildren().add(title);
if (!pages.isEmpty() && isVisible()) {
final Timeline timeline = new Timeline(
new KeyFrame(Duration.millis(350), (ActionEvent t) -> {
pagesPane.setCache(false);
resizePopoverToNewPage(pageNode);
},
new KeyValue(pagesPane.translateXProperty(), -newPageX, Interpolator.EASE_BOTH),
new KeyValue(titlesPane.translateXProperty(), -newPageX, Interpolator.EASE_BOTH),
new KeyValue(pagesClipRect.xProperty(), newPageX, Interpolator.EASE_BOTH),
new KeyValue(titlesClipRect.translateXProperty(), newPageX, Interpolator.EASE_BOTH)
)
);
timeline.play();
}
page.setPopover(this);
page.handleShown();
pages.push(page);
}
private void resizePopoverToNewPage(final Node newPageNode) {
final Insets insets = getInsets();
final double width = prefWidth(-1);
final double contentWidth = width - insets.getLeft() - insets.getRight();
double h = newPageNode.prefHeight(contentWidth);
h += insets.getTop() + insets.getBottom();
new Timeline(
new KeyFrame(Duration.millis(200),
new KeyValue(popoverHeight, h, Interpolator.EASE_BOTH)
)
).play();
}
public void show(){
show(null);
}
private Animation fadeAnimation = null;
public void show(Runnable onHideCallback){
if (!isVisible() || fadeAnimation != null) {
this.onHideCallback = onHideCallback;
getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, popoverHideHandler);
if (fadeAnimation != null) {
fadeAnimation.stop();
setVisible(true);
} else {
popoverHeight.set(-1);
setVisible(true);
}
FadeTransition fade = new FadeTransition(Duration.seconds(.1), this);
fade.setToValue(1.0);
fade.setOnFinished((ActionEvent event) -> {
fadeAnimation = null;
});
ScaleTransition scale = new ScaleTransition(Duration.seconds(.1), this);
scale.setToX(1);
scale.setToY(1);
ParallelTransition tx = new ParallelTransition(fade, scale);
fadeAnimation = tx;
tx.play();
}
}
public void hide(){
if (isVisible() || fadeAnimation != null) {
getScene().removeEventFilter(MouseEvent.MOUSE_CLICKED, popoverHideHandler);
if (fadeAnimation != null) {
fadeAnimation.stop();
}
FadeTransition fade = new FadeTransition(Duration.seconds(.1), this);
fade.setToValue(0);
fade.setOnFinished((ActionEvent event) -> {
fadeAnimation = null;
setVisible(false);
clearPages();
if (onHideCallback != null) onHideCallback.run();
});
ScaleTransition scale = new ScaleTransition(Duration.seconds(.1), this);
scale.setToX(.8);
scale.setToY(.8);
ParallelTransition tx = new ParallelTransition(fade, scale);
fadeAnimation = tx;
tx.play();
}
}
public static interface Page {
public void setPopover(Popover popover);
public Popover getPopover();
public Node getPageNode();
public String getPageTitle();
public String leftButtonText();
public void handleLeftButton();
public String rightButtonText();
public void handleRightButton();
public void handleShown();
public void handleHidden();
}
}
