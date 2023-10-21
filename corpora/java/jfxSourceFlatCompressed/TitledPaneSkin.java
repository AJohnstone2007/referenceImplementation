package javafx.scene.control.skin;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import com.sun.javafx.scene.control.behavior.TitledPaneBehavior;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.Labeled;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Font;
public class TitledPaneSkin extends LabeledSkinBase<TitledPane> {
private static final Duration TRANSITION_DURATION = new Duration(350.0);
private static final boolean CACHE_ANIMATION = PlatformUtil.isEmbedded();
private final TitledPaneBehavior behavior;
private final TitleRegion titleRegion;
private final StackPane contentContainer;
private Node content;
private Timeline timeline;
private double transitionStartValue;
private Rectangle clipRect;
private Pos pos;
private HPos hpos;
private VPos vpos;
public TitledPaneSkin(final TitledPane control) {
super(control);
this.behavior = new TitledPaneBehavior(control);
clipRect = new Rectangle();
transitionStartValue = 0;
titleRegion = new TitleRegion();
content = getSkinnable().getContent();
contentContainer = new StackPane() {
{
getStyleClass().setAll("content");
if (content != null) {
getChildren().setAll(content);
}
}
};
contentContainer.setClip(clipRect);
updateClip();
if (control.isExpanded()) {
setTransition(1.0f);
setExpanded(control.isExpanded());
} else {
setTransition(0.0f);
if (content != null) {
content.setVisible(false);
}
}
getChildren().setAll(contentContainer, titleRegion);
registerChangeListener(control.contentProperty(), e -> {
content = getSkinnable().getContent();
if (content == null) {
contentContainer.getChildren().clear();
} else {
contentContainer.getChildren().setAll(content);
}
});
registerChangeListener(control.expandedProperty(), e -> setExpanded(getSkinnable().isExpanded()));
registerChangeListener(control.collapsibleProperty(), e -> titleRegion.update());
registerChangeListener(control.alignmentProperty(), e -> {
pos = getSkinnable().getAlignment();
hpos = pos.getHpos();
vpos = pos.getVpos();
});
registerChangeListener(control.widthProperty(), e -> updateClip());
registerChangeListener(control.heightProperty(), e -> updateClip());
registerChangeListener(titleRegion.alignmentProperty(), e -> {
pos = titleRegion.getAlignment();
hpos = pos.getHpos();
vpos = pos.getVpos();
});
pos = control.getAlignment();
hpos = pos == null ? HPos.LEFT : pos.getHpos();
vpos = pos == null ? VPos.CENTER : pos.getVpos();
}
private DoubleProperty transition;
private final void setTransition(double value) { transitionProperty().set(value); }
private final double getTransition() { return transition == null ? 0.0 : transition.get(); }
private final DoubleProperty transitionProperty() {
if (transition == null) {
transition = new SimpleDoubleProperty(this, "transition", 0.0) {
@Override protected void invalidated() {
contentContainer.requestLayout();
}
};
}
return transition;
}
@Override public void dispose() {
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override protected void updateChildren() {
if (titleRegion != null) {
titleRegion.update();
}
}
@Override protected void layoutChildren(final double x, double y,
final double w, final double h) {
double headerHeight = snapSizeY(titleRegion.prefHeight(-1));
titleRegion.resize(w, headerHeight);
positionInArea(titleRegion, x, y,
w, headerHeight, 0, HPos.LEFT, VPos.CENTER);
titleRegion.requestLayout();
double contentHeight = (h - headerHeight) * getTransition();
if (isInsideAccordion()) {
if (prefHeightFromAccordion != 0) {
contentHeight = (prefHeightFromAccordion - headerHeight) * getTransition();
}
}
contentHeight = snapSizeY(contentHeight);
y += headerHeight;
contentContainer.resize(w, contentHeight);
clipRect.setHeight(contentHeight);
positionInArea(contentContainer, x, y,
w, contentHeight, 0, HPos.CENTER, VPos.CENTER);
}
@Override protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
double titleWidth = snapSizeX(titleRegion.prefWidth(height));
double contentWidth = snapSizeX(contentContainer.minWidth(height));
return Math.max(titleWidth, contentWidth) + leftInset + rightInset;
}
@Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
double headerHeight = snapSizeY(titleRegion.prefHeight(width));
double contentHeight = contentContainer.minHeight(width) * getTransition();
return headerHeight + snapSizeY(contentHeight) + topInset + bottomInset;
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
double titleWidth = snapSizeX(titleRegion.prefWidth(height));
double contentWidth = snapSizeX(contentContainer.prefWidth(height));
return Math.max(titleWidth, contentWidth) + leftInset + rightInset;
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
double headerHeight = snapSizeY(titleRegion.prefHeight(width));
double contentHeight = contentContainer.prefHeight(width) * getTransition();
return headerHeight + snapSizeY(contentHeight) + topInset + bottomInset;
}
@Override protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return Double.MAX_VALUE;
}
private void updateClip() {
clipRect.setWidth(getSkinnable().getWidth());
clipRect.setHeight(contentContainer.getHeight());
}
private void setExpanded(boolean expanded) {
if (! getSkinnable().isCollapsible()) {
setTransition(1.0f);
return;
}
if (getSkinnable().isAnimated()) {
transitionStartValue = getTransition();
doAnimationTransition();
} else {
if (expanded) {
setTransition(1.0f);
} else {
setTransition(0.0f);
}
if (content != null) {
content.setVisible(expanded);
}
getSkinnable().requestLayout();
}
}
private boolean isInsideAccordion() {
return getSkinnable().getParent() != null && getSkinnable().getParent() instanceof Accordion;
}
double getTitleRegionSize(double width) {
return snapSizeY(titleRegion.prefHeight(width)) + snappedTopInset() + snappedBottomInset();
}
private double prefHeightFromAccordion = 0;
void setMaxTitledPaneHeightForAccordion(double height) {
this.prefHeightFromAccordion = height;
}
double getTitledPaneHeightForAccordion() {
double headerHeight = snapSizeY(titleRegion.prefHeight(-1));
double contentHeight = (prefHeightFromAccordion - headerHeight) * getTransition();
return headerHeight + snapSizeY(contentHeight) + snappedTopInset() + snappedBottomInset();
}
private void doAnimationTransition() {
if (content == null) {
return;
}
Duration duration;
if (timeline != null && (timeline.getStatus() != Status.STOPPED)) {
duration = timeline.getCurrentTime();
timeline.stop();
} else {
duration = TRANSITION_DURATION;
}
timeline = new Timeline();
timeline.setCycleCount(1);
KeyFrame k1, k2;
if (getSkinnable().isExpanded()) {
k1 = new KeyFrame(
Duration.ZERO,
event -> {
if (CACHE_ANIMATION) content.setCache(true);
content.setVisible(true);
},
new KeyValue(transitionProperty(), transitionStartValue)
);
k2 = new KeyFrame(
duration,
event -> {
if (CACHE_ANIMATION) content.setCache(false);
},
new KeyValue(transitionProperty(), 1, Interpolator.LINEAR)
);
} else {
k1 = new KeyFrame(
Duration.ZERO,
event -> {
if (CACHE_ANIMATION) content.setCache(true);
},
new KeyValue(transitionProperty(), transitionStartValue)
);
k2 = new KeyFrame(
duration,
event -> {
content.setVisible(false);
if (CACHE_ANIMATION) content.setCache(false);
},
new KeyValue(transitionProperty(), 0, Interpolator.LINEAR)
);
}
timeline.getKeyFrames().setAll(k1, k2);
timeline.play();
}
class TitleRegion extends StackPane {
private final StackPane arrowRegion;
public TitleRegion() {
getStyleClass().setAll("title");
arrowRegion = new StackPane();
arrowRegion.setId("arrowRegion");
arrowRegion.getStyleClass().setAll("arrow-button");
StackPane arrow = new StackPane();
arrow.setId("arrow");
arrow.getStyleClass().setAll("arrow");
arrowRegion.getChildren().setAll(arrow);
arrow.rotateProperty().bind(new DoubleBinding() {
{ bind(transitionProperty()); }
@Override protected double computeValue() {
return -90 * (1.0 - getTransition());
}
});
setAlignment(Pos.CENTER_LEFT);
setOnMouseReleased(e -> {
if( e.getButton() != MouseButton.PRIMARY ) return;
ContextMenu contextMenu = getSkinnable().getContextMenu() ;
if (contextMenu != null) {
contextMenu.hide() ;
}
if (getSkinnable().isCollapsible() && getSkinnable().isFocused()) {
behavior.toggle();
}
});
update();
}
private void update() {
getChildren().clear();
final TitledPane titledPane = getSkinnable();
if (titledPane.isCollapsible()) {
getChildren().add(arrowRegion);
}
if (graphic != null) {
graphic.layoutBoundsProperty().removeListener(graphicPropertyChangedListener);
}
graphic = titledPane.getGraphic();
if (isIgnoreGraphic()) {
if (titledPane.getContentDisplay() == ContentDisplay.GRAPHIC_ONLY) {
getChildren().clear();
getChildren().add(arrowRegion);
} else {
getChildren().add(text);
}
} else {
graphic.layoutBoundsProperty().addListener(graphicPropertyChangedListener);
if (isIgnoreText()) {
getChildren().add(graphic);
} else {
getChildren().addAll(graphic, text);
}
}
setCursor(getSkinnable().isCollapsible() ? Cursor.HAND : Cursor.DEFAULT);
}
@Override protected double computePrefWidth(double height) {
double left = snappedLeftInset();
double right = snappedRightInset();
double arrowWidth = 0;
double labelPrefWidth = labelPrefWidth(height);
if (arrowRegion != null) {
arrowWidth = snapSizeX(arrowRegion.prefWidth(height));
}
return left + arrowWidth + labelPrefWidth + right;
}
@Override protected double computePrefHeight(double width) {
double top = snappedTopInset();
double bottom = snappedBottomInset();
double arrowHeight = 0;
double labelPrefHeight = labelPrefHeight(width);
if (arrowRegion != null) {
arrowHeight = snapSizeY(arrowRegion.prefHeight(width));
}
return top + Math.max(arrowHeight, labelPrefHeight) + bottom;
}
@Override protected void layoutChildren() {
final double top = snappedTopInset();
final double bottom = snappedBottomInset();
final double left = snappedLeftInset();
final double right = snappedRightInset();
double width = getWidth() - (left + right);
double height = getHeight() - (top + bottom);
double arrowWidth = snapSizeX(arrowRegion.prefWidth(-1));
double arrowHeight = snapSizeY(arrowRegion.prefHeight(-1));
double labelWidth = snapSizeX(Math.min(width - arrowWidth / 2.0, labelPrefWidth(-1)));
double labelHeight = snapSizeY(labelPrefHeight(-1));
double x = left + arrowWidth + Utils.computeXOffset(width - arrowWidth, labelWidth, hpos);
if (HPos.CENTER == hpos) {
x = left + Utils.computeXOffset(width, labelWidth, hpos);
}
double y = top + Utils.computeYOffset(height, Math.max(arrowHeight, labelHeight), vpos);
arrowRegion.resize(arrowWidth, arrowHeight);
positionInArea(arrowRegion, left, top, arrowWidth, height,
0, HPos.CENTER, VPos.CENTER);
layoutLabelInArea(x, y, labelWidth, height, pos);
}
private double labelPrefWidth(double height) {
final Labeled labeled = getSkinnable();
final Font font = text.getFont();
final String string = labeled.getText();
boolean emptyText = string == null || string.isEmpty();
Insets labelPadding = labeled.getLabelPadding();
double widthPadding = labelPadding.getLeft() + labelPadding.getRight();
double textWidth = emptyText ? 0 : Utils.computeTextWidth(font, string, 0);
final Node graphic = labeled.getGraphic();
if (isIgnoreGraphic()) {
return textWidth + widthPadding;
} else if (isIgnoreText()) {
return graphic.prefWidth(-1) + widthPadding;
} else if (labeled.getContentDisplay() == ContentDisplay.LEFT
|| labeled.getContentDisplay() == ContentDisplay.RIGHT) {
return textWidth + labeled.getGraphicTextGap() + graphic.prefWidth(-1) + widthPadding;
} else {
return Math.max(textWidth, graphic.prefWidth(-1)) + widthPadding;
}
}
private double labelPrefHeight(double width) {
final Labeled labeled = getSkinnable();
final Font font = text.getFont();
final ContentDisplay contentDisplay = labeled.getContentDisplay();
final double gap = labeled.getGraphicTextGap();
final Insets labelPadding = labeled.getLabelPadding();
final double widthPadding = snappedLeftInset() + snappedRightInset() + labelPadding.getLeft() + labelPadding.getRight();
String str = labeled.getText();
if (str != null && str.endsWith("\n")) {
str = str.substring(0, str.length() - 1);
}
if (!isIgnoreGraphic() &&
(contentDisplay == ContentDisplay.LEFT || contentDisplay == ContentDisplay.RIGHT)) {
width -= (graphic.prefWidth(-1) + gap);
}
width -= widthPadding;
final double textHeight = Utils.computeTextHeight(font, str,
labeled.isWrapText() ? width : 0, text.getBoundsType());
double h = textHeight;
if (!isIgnoreGraphic()) {
final Node graphic = labeled.getGraphic();
if (contentDisplay == ContentDisplay.TOP || contentDisplay == ContentDisplay.BOTTOM) {
h = graphic.prefHeight(-1) + gap + textHeight;
} else {
h = Math.max(textHeight, graphic.prefHeight(-1));
}
}
return h + labelPadding.getTop() + labelPadding.getBottom();
}
}
}
