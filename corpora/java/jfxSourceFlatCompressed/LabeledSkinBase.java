package javafx.scene.control.skin;
import com.sun.javafx.scene.control.LabeledText;
import com.sun.javafx.scene.control.behavior.MnemonicInfo;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.geometry.HPos;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.SkinBase;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.Mnemonic;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import static javafx.scene.control.ContentDisplay.BOTTOM;
import static javafx.scene.control.ContentDisplay.LEFT;
import static javafx.scene.control.ContentDisplay.RIGHT;
import static javafx.scene.control.ContentDisplay.TOP;
import static javafx.scene.control.OverrunStyle.CLIP;
public abstract class LabeledSkinBase<C extends Labeled> extends SkinBase<C> {
LabeledText text;
boolean invalidText = true;
Node graphic;
double textWidth = Double.NEGATIVE_INFINITY;
double ellipsisWidth = Double.NEGATIVE_INFINITY;
final InvalidationListener graphicPropertyChangedListener = valueModel -> {
invalidText = true;
getSkinnable().requestLayout();
};
private Rectangle textClip;
private double wrapWidth;
private double wrapHeight;
private MnemonicInfo mnemonicInfo;
private Line mnemonic_underscore;
private boolean containsMnemonic = false;
private Scene mnemonicScene = null;
private KeyCombination mnemonicCode;
private Node labeledNode = null;
public LabeledSkinBase(final C labeled) {
super(labeled);
text = new LabeledText(labeled);
updateChildren();
registerChangeListener(labeled.ellipsisStringProperty(), o -> {
textMetricsChanged();
invalidateWidths();
ellipsisWidth = Double.NEGATIVE_INFINITY;
});
registerChangeListener(labeled.widthProperty(), o -> {
updateWrappingWidth();
invalidText = true;
});
registerChangeListener(labeled.heightProperty(), o -> {
invalidText = true;
});
registerChangeListener(labeled.fontProperty(), o -> {
textMetricsChanged();
invalidateWidths();
ellipsisWidth = Double.NEGATIVE_INFINITY;
});
registerChangeListener(labeled.graphicProperty(), o -> {
updateChildren();
textMetricsChanged();
});
registerChangeListener(labeled.contentDisplayProperty(), o -> {
updateChildren();
textMetricsChanged();
});
registerChangeListener(labeled.labelPaddingProperty(), o -> textMetricsChanged());
registerChangeListener(labeled.graphicTextGapProperty(), o -> textMetricsChanged());
registerChangeListener(labeled.alignmentProperty(), o -> {
getSkinnable().requestLayout();
});
registerChangeListener(labeled.mnemonicParsingProperty(), o -> {
containsMnemonic = false;
textMetricsChanged();
});
registerChangeListener(labeled.textProperty(), o -> {
updateChildren();
textMetricsChanged();
invalidateWidths();
});
registerChangeListener(labeled.textAlignmentProperty(), o -> { });
registerChangeListener(labeled.textOverrunProperty(), o -> textMetricsChanged());
registerChangeListener(labeled.wrapTextProperty(), o -> {
updateWrappingWidth();
textMetricsChanged();
});
registerChangeListener(labeled.underlineProperty(), o -> textMetricsChanged());
registerChangeListener(labeled.lineSpacingProperty(), o -> textMetricsChanged());
registerChangeListener(labeled.sceneProperty(), o -> sceneChanged());
}
@Override
public void dispose() {
if (graphic != null) {
graphic.layoutBoundsProperty().removeListener(graphicPropertyChangedListener);
graphic = null;
}
super.dispose();
}
protected void updateChildren() {
final Labeled labeled = getSkinnable();
if (graphic != null) {
graphic.layoutBoundsProperty().removeListener(graphicPropertyChangedListener);
}
graphic = labeled.getGraphic();
if (graphic instanceof ImageView) {
graphic.setMouseTransparent(true);
}
if (isIgnoreGraphic()) {
if (labeled.getContentDisplay() == ContentDisplay.GRAPHIC_ONLY) {
getChildren().clear();
} else {
getChildren().setAll(text);
}
} else {
graphic.layoutBoundsProperty().addListener(graphicPropertyChangedListener);
if (isIgnoreText()) {
getChildren().setAll(graphic);
} else {
getChildren().setAll(graphic, text);
}
}
}
@Override protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return computeMinLabeledPartWidth(height, topInset, rightInset, bottomInset, leftInset);
}
@Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return computeMinLabeledPartHeight(width, topInset, rightInset, bottomInset, leftInset);
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
final Labeled labeled = getSkinnable();
final Font font = text.getFont();
String cleanText = getCleanText();
boolean emptyText = cleanText == null || cleanText.isEmpty();
double widthPadding = leftInset + rightInset;
if (!isIgnoreText()) {
widthPadding += leftLabelPadding() + rightLabelPadding();
}
double textWidth = 0.0;
if (!emptyText) {
textWidth = Utils.computeTextWidth(font, cleanText, 0);
}
double graphicWidth = graphic == null ? 0.0 :
Utils.boundedSize(graphic.prefWidth(-1), graphic.minWidth(-1), graphic.maxWidth(-1));
if (isIgnoreGraphic()) {
return textWidth + widthPadding;
} else if (isIgnoreText()) {
return graphicWidth + widthPadding;
} else if (labeled.getContentDisplay() == ContentDisplay.LEFT
|| labeled.getContentDisplay() == ContentDisplay.RIGHT) {
return textWidth + labeled.getGraphicTextGap() + graphicWidth + widthPadding;
} else {
return Math.max(textWidth, graphicWidth) + widthPadding;
}
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
final Labeled labeled = getSkinnable();
final Font font = text.getFont();
final ContentDisplay contentDisplay = labeled.getContentDisplay();
final double gap = labeled.getGraphicTextGap();
width -= leftInset + rightInset;
if (!isIgnoreText()) {
width -= leftLabelPadding() + rightLabelPadding();
}
String cleanText = getCleanText();
if (cleanText != null && cleanText.endsWith("\n")) {
cleanText = cleanText.substring(0, cleanText.length() - 1);
}
double textWidth = width;
if (!isIgnoreGraphic() &&
(contentDisplay == LEFT || contentDisplay == RIGHT)) {
textWidth -= (graphic.prefWidth(-1) + gap);
}
final double textHeight = Utils.computeTextHeight(font, cleanText,
labeled.isWrapText() ? textWidth : 0,
labeled.getLineSpacing(), text.getBoundsType());
double h = textHeight;
if (!isIgnoreGraphic()) {
final Node graphic = labeled.getGraphic();
if (contentDisplay == TOP || contentDisplay == BOTTOM) {
h = graphic.prefHeight(width) + gap + textHeight;
} else {
h = Math.max(textHeight, graphic.prefHeight(width));
}
}
double padding = topInset + bottomInset;
if (!isIgnoreText()) {
padding += topLabelPadding() + bottomLabelPadding();
}
return h + padding;
}
@Override protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return getSkinnable().prefWidth(height);
}
@Override protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return getSkinnable().prefHeight(width);
}
@Override public double computeBaselineOffset(double topInset, double rightInset, double bottomInset, double leftInset) {
double textBaselineOffset = text.getBaselineOffset();
double h = textBaselineOffset;
final Labeled labeled = getSkinnable();
final Node g = labeled.getGraphic();
if (!isIgnoreGraphic()) {
ContentDisplay contentDisplay = labeled.getContentDisplay();
if (contentDisplay == ContentDisplay.TOP) {
h = g.prefHeight(-1) + labeled.getGraphicTextGap() + textBaselineOffset;
} else if (contentDisplay == ContentDisplay.LEFT || contentDisplay == RIGHT) {
h = textBaselineOffset + (g.prefHeight(-1) - text.prefHeight(-1)) / 2;
}
}
double offset = topInset + h;
if (!isIgnoreText()) {
offset += topLabelPadding();
}
return offset;
}
@Override protected void layoutChildren(final double x, final double y,
final double w, final double h) {
layoutLabelInArea(x, y, w, h);
}
protected void layoutLabelInArea(double x, double y, double w, double h) {
layoutLabelInArea(x, y, w, h, null);
}
protected void layoutLabelInArea(double x, double y, double w, double h, Pos alignment) {
final Labeled labeled = getSkinnable();
final ContentDisplay contentDisplay = labeled.getContentDisplay();
if (alignment == null) {
alignment = labeled.getAlignment();
}
final HPos hpos = alignment == null ? HPos.LEFT : alignment.getHpos();
final VPos vpos = alignment == null ? VPos.CENTER : alignment.getVpos();
final boolean ignoreGraphic = isIgnoreGraphic();
final boolean ignoreText = isIgnoreText();
if (!ignoreText) {
x += leftLabelPadding();
y += topLabelPadding();
w -= leftLabelPadding() + rightLabelPadding();
h -= topLabelPadding() + bottomLabelPadding();
}
double graphicWidth;
double graphicHeight;
double textWidth;
double textHeight;
if (ignoreGraphic) {
graphicWidth = graphicHeight = 0;
} else if (ignoreText) {
if (graphic.isResizable()) {
Orientation contentBias = graphic.getContentBias();
if (contentBias == Orientation.HORIZONTAL) {
graphicWidth = Utils.boundedSize(w, graphic.minWidth(-1), graphic.maxWidth(-1));
graphicHeight = Utils.boundedSize(h, graphic.minHeight(graphicWidth), graphic.maxHeight(graphicWidth));
} else if (contentBias == Orientation.VERTICAL) {
graphicHeight = Utils.boundedSize(h, graphic.minHeight(-1), graphic.maxHeight(-1));
graphicWidth = Utils.boundedSize(w, graphic.minWidth(graphicHeight), graphic.maxWidth(graphicHeight));
} else {
graphicWidth = Utils.boundedSize(w, graphic.minWidth(-1), graphic.maxWidth(-1));
graphicHeight = Utils.boundedSize(h, graphic.minHeight(-1), graphic.maxHeight(-1));
}
graphic.resize(graphicWidth, graphicHeight);
} else {
graphicWidth = graphic.getLayoutBounds().getWidth();
graphicHeight = graphic.getLayoutBounds().getHeight();
}
} else {
graphic.autosize();
graphicWidth = graphic.getLayoutBounds().getWidth();
graphicHeight = graphic.getLayoutBounds().getHeight();
}
if (ignoreText) {
textWidth = textHeight = 0;
text.setText("");
} else {
updateDisplayedText(w, h);
textWidth = snapSizeX(Math.min(text.getLayoutBounds().getWidth(), wrapWidth));
textHeight = snapSizeY(Math.min(text.getLayoutBounds().getHeight(), wrapHeight));
}
final double gap = (ignoreText || ignoreGraphic) ? 0 : labeled.getGraphicTextGap();
double contentWidth = Math.max(graphicWidth, textWidth);
double contentHeight = Math.max(graphicHeight, textHeight);
if (contentDisplay == ContentDisplay.TOP || contentDisplay == ContentDisplay.BOTTOM) {
contentHeight = graphicHeight + gap + textHeight;
} else if (contentDisplay == ContentDisplay.LEFT || contentDisplay == ContentDisplay.RIGHT) {
contentWidth = graphicWidth + gap + textWidth;
}
double contentX;
if (hpos == HPos.LEFT) {
contentX = x;
} else if (hpos == HPos.RIGHT) {
contentX = x + (w - contentWidth);
} else {
contentX = (x + ((w - contentWidth) / 2.0));
}
double contentY;
if (vpos == VPos.TOP) {
contentY = y;
} else if (vpos == VPos.BOTTOM) {
contentY = (y + (h - contentHeight));
} else {
contentY = (y + ((h - contentHeight) / 2.0));
}
Point2D mnemonicPos = null;
double mnemonicWidth = 0.0;
double mnemonicHeight = 0.0;
if (containsMnemonic) {
final Font font = text.getFont();
String preSt = mnemonicInfo.getText();
boolean isRTL = (labeledNode.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT);
mnemonicPos = Utils.computeMnemonicPosition(font, preSt, mnemonicInfo.getMnemonicIndex(), this.wrapWidth, labeled.getLineSpacing(), isRTL);
mnemonicWidth = Utils.computeTextWidth(font, preSt.substring(mnemonicInfo.getMnemonicIndex(), mnemonicInfo.getMnemonicIndex() + 1), 0);
mnemonicHeight = Utils.computeTextHeight(font, "_", 0, text.getBoundsType());
}
if ((!ignoreGraphic || !ignoreText) && !text.isManaged()) {
text.setManaged(true);
}
if (ignoreGraphic && ignoreText) {
if (text.isManaged()) {
text.setManaged(false);
}
text.relocate(snapPositionX(contentX), snapPositionY(contentY));
} else if (ignoreGraphic) {
text.relocate(snapPositionX(contentX), snapPositionY(contentY));
if (containsMnemonic && (mnemonicPos != null)) {
mnemonic_underscore.setEndX(mnemonicWidth-2.0);
mnemonic_underscore.relocate(snapPositionX(contentX + mnemonicPos.getX()),
snapPositionY(contentY + mnemonicPos.getY()));
}
} else if (ignoreText) {
text.relocate(snapPositionX(contentX), snapPositionY(contentY));
graphic.relocate(snapPositionX(contentX), snapPositionY(contentY));
if (containsMnemonic && (mnemonicPos != null)) {
mnemonic_underscore.setEndX(mnemonicWidth);
mnemonic_underscore.setStrokeWidth(mnemonicHeight/10.0);
mnemonic_underscore.relocate(snapPositionX(contentX + mnemonicPos.getX()),
snapPositionY(contentY + mnemonicPos.getY()));
}
} else {
double graphicX = 0;
double graphicY = 0;
double textX = 0;
double textY = 0;
if (contentDisplay == ContentDisplay.TOP) {
graphicX = contentX + ((contentWidth - graphicWidth) / 2.0);
textX = contentX + ((contentWidth - textWidth) / 2.0);
graphicY = contentY;
textY = graphicY + graphicHeight + gap;
} else if (contentDisplay == ContentDisplay.RIGHT) {
textX = contentX;
graphicX = textX + textWidth + gap;
graphicY = contentY + ((contentHeight - graphicHeight) / 2.0);
textY = contentY + ((contentHeight - textHeight) / 2.0);
} else if (contentDisplay == ContentDisplay.BOTTOM) {
graphicX = contentX + ((contentWidth - graphicWidth) / 2.0);
textX = contentX + ((contentWidth - textWidth) / 2.0);
textY = contentY;
graphicY = textY + textHeight + gap;
} else if (contentDisplay == ContentDisplay.LEFT) {
graphicX = contentX;
textX = graphicX + graphicWidth + gap;
graphicY = contentY + ((contentHeight - graphicHeight) / 2.0);
textY = contentY + ((contentHeight - textHeight) / 2.0);
} else if (contentDisplay == ContentDisplay.CENTER) {
graphicX = contentX + ((contentWidth - graphicWidth) / 2.0);
textX = contentX + ((contentWidth - textWidth) / 2.0);
graphicY = contentY + ((contentHeight - graphicHeight) / 2.0);
textY = contentY + ((contentHeight - textHeight) / 2.0);
}
text.relocate(snapPositionX(textX), snapPositionY(textY));
if (containsMnemonic && (mnemonicPos != null)) {
mnemonic_underscore.setEndX(mnemonicWidth);
mnemonic_underscore.setStrokeWidth(mnemonicHeight/10.0);
mnemonic_underscore.relocate(snapPositionX(textX + mnemonicPos.getX()),
snapPositionY(textY + mnemonicPos.getY()));
}
graphic.relocate(snapPositionX(graphicX), snapPositionY(graphicY));
}
if ((text != null) &&
((text.getLayoutBounds().getHeight() > wrapHeight) ||
(text.getLayoutBounds().getWidth() > wrapWidth))) {
if (textClip == null) {
textClip = new Rectangle();
}
if (labeled.getEffectiveNodeOrientation() == NodeOrientation.LEFT_TO_RIGHT) {
textClip.setX(text.getLayoutBounds().getMinX());
} else {
textClip.setX(text.getLayoutBounds().getMaxX() - wrapWidth);
}
textClip.setY(text.getLayoutBounds().getMinY());
textClip.setWidth(wrapWidth);
textClip.setHeight(wrapHeight);
if (text.getClip() == null) {
text.setClip(textClip);
}
}
else {
if (text.getClip() != null) {
text.setClip(null);
}
}
}
@Override protected Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case TEXT: {
Labeled labeled = getSkinnable();
String accText = labeled.getAccessibleText();
if (accText != null && !accText.isEmpty()) return accText;
String cleanText = getCleanText();
if (cleanText != null && !cleanText.isEmpty()) {
return cleanText;
}
if (graphic != null) {
Object result = graphic.queryAccessibleAttribute(AccessibleAttribute.TEXT);
if (result != null) return result;
}
return null;
}
case MNEMONIC: {
if (mnemonicInfo != null) {
return mnemonicInfo.getMnemonic();
}
return null;
}
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
private double computeMinLabeledPartWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
final Labeled labeled = getSkinnable();
final ContentDisplay contentDisplay = labeled.getContentDisplay();
final double gap = labeled.getGraphicTextGap();
double minTextWidth = 0;
final Font font = text.getFont();
OverrunStyle truncationStyle = labeled.getTextOverrun();
String ellipsisString = labeled.getEllipsisString();
final String cleanText = getCleanText();
final boolean emptyText = cleanText == null || cleanText.isEmpty();
if (!emptyText) {
if (truncationStyle == CLIP) {
if (textWidth == Double.NEGATIVE_INFINITY) {
textWidth = Utils.computeTextWidth(font, cleanText.substring(0, 1), 0);
}
minTextWidth = textWidth;
} else {
if (textWidth == Double.NEGATIVE_INFINITY) {
textWidth = Utils.computeTextWidth(font, cleanText, 0);
}
if (ellipsisWidth == Double.NEGATIVE_INFINITY) {
ellipsisWidth = Utils.computeTextWidth(font, ellipsisString, 0);
}
minTextWidth = Math.min(textWidth, ellipsisWidth);
}
}
final Node graphic = labeled.getGraphic();
double width;
if (isIgnoreGraphic()) {
width = minTextWidth;
} else if (isIgnoreText()) {
width = graphic.minWidth(-1);
} else if (contentDisplay == LEFT || contentDisplay == RIGHT){
width = (minTextWidth + graphic.minWidth(-1) + gap);
} else {
width = Math.max(minTextWidth, graphic.minWidth(-1));
}
double padding = leftInset + rightInset;
if (!isIgnoreText()) {
padding += leftLabelPadding() + rightLabelPadding();
}
return width + padding;
}
private double computeMinLabeledPartHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
final Labeled labeled = getSkinnable();
final Font font = text.getFont();
String cleanText = getCleanText();
if (cleanText != null && cleanText.length() > 0) {
int newlineIndex = cleanText.indexOf('\n');
if (newlineIndex >= 0) {
cleanText = cleanText.substring(0, newlineIndex);
}
}
double s = labeled.getLineSpacing();
final double textHeight = Utils.computeTextHeight(font, cleanText, 0, s, text.getBoundsType());
double h = textHeight;
if (!isIgnoreGraphic()) {
final Node graphic = labeled.getGraphic();
if (labeled.getContentDisplay() == ContentDisplay.TOP
|| labeled.getContentDisplay() == ContentDisplay.BOTTOM) {
h = graphic.minHeight(width) + labeled.getGraphicTextGap() + textHeight;
} else {
h = Math.max(textHeight, graphic.minHeight(width));
}
}
double padding = topInset + bottomInset;
if (!isIgnoreText()) {
padding += topLabelPadding() - bottomLabelPadding();
}
return h + padding;
}
double topLabelPadding() {
return snapSizeY(getSkinnable().getLabelPadding().getTop());
}
double bottomLabelPadding() {
return snapSizeY(getSkinnable().getLabelPadding().getBottom());
}
double leftLabelPadding() {
return snapSizeX(getSkinnable().getLabelPadding().getLeft());
}
double rightLabelPadding() {
return snapSizeX(getSkinnable().getLabelPadding().getRight());
}
private void textMetricsChanged() {
invalidText = true;
getSkinnable().requestLayout();
}
void mnemonicTargetChanged() {
if (containsMnemonic) {
removeMnemonic();
Control control = getSkinnable();
if (control instanceof Label) {
labeledNode = ((Label)control).getLabelFor();
addMnemonic();
}
else {
labeledNode = null;
}
}
}
private void sceneChanged() {
final Labeled labeled = getSkinnable();
Scene scene = labeled.getScene();
if (scene != null && containsMnemonic) {
addMnemonic();
}
}
private void invalidateWidths() {
textWidth = Double.NEGATIVE_INFINITY;
}
void updateDisplayedText() {
updateDisplayedText(-1, -1);
}
private void updateDisplayedText(double w, double h) {
if (invalidText) {
final Labeled labeled = getSkinnable();
String cleanText = getCleanText();
int mnemonicIndex = -1;
if (cleanText != null && cleanText.length() > 0
&& mnemonicInfo != null
&& !com.sun.javafx.PlatformUtil.isMac()
&& getSkinnable().isMnemonicParsing()) {
if (labeled instanceof Label) {
labeledNode = ((Label)labeled).getLabelFor();
} else {
labeledNode = labeled;
}
if (labeledNode == null) {
labeledNode = labeled;
}
mnemonicIndex = mnemonicInfo.getMnemonicIndex() ;
}
if (containsMnemonic) {
if (mnemonicScene != null) {
if (mnemonicIndex == -1 ||
(mnemonicInfo != null && !mnemonicInfo.getMnemonicKeyCombination().equals(mnemonicCode))) {
removeMnemonic();
containsMnemonic = false;
}
}
}
else {
removeMnemonic();
}
if (cleanText != null && cleanText.length() > 0
&& mnemonicIndex >= 0 && !containsMnemonic) {
containsMnemonic = true;
mnemonicCode = mnemonicInfo.getMnemonicKeyCombination();
addMnemonic();
}
if (containsMnemonic) {
if (mnemonic_underscore == null) {
mnemonic_underscore = new Line();
mnemonic_underscore.setStartX(0.0f);
mnemonic_underscore.setStartY(0.0f);
mnemonic_underscore.setEndY(0.0f);
mnemonic_underscore.getStyleClass().clear();
mnemonic_underscore.getStyleClass().setAll("mnemonic-underline");
}
if (!getChildren().contains(mnemonic_underscore)) {
getChildren().add(mnemonic_underscore);
}
} else if (mnemonic_underscore != null && getChildren().contains(mnemonic_underscore)) {
Platform.runLater(() -> {
getChildren().remove(mnemonic_underscore);
mnemonic_underscore = null;
});
}
int len = cleanText != null ? cleanText.length() : 0;
boolean multiline = false;
if (cleanText != null && len > 0) {
int i = cleanText.indexOf('\n');
if (i > -1 && i < len - 1) {
multiline = true;
}
}
String result;
boolean horizontalPosition =
(labeled.getContentDisplay() == ContentDisplay.LEFT ||
labeled.getContentDisplay() == ContentDisplay.RIGHT);
double availableWidth = labeled.getWidth() -
snappedLeftInset() - snappedRightInset();
if (!isIgnoreText()) {
availableWidth -= leftLabelPadding() + rightLabelPadding();
}
availableWidth = Math.max(availableWidth, 0);
if (w == -1) {
w = availableWidth;
}
double minW = Math.min(computeMinLabeledPartWidth(-1, snappedTopInset() , snappedRightInset(), snappedBottomInset(), snappedLeftInset()), availableWidth);
if (horizontalPosition && !isIgnoreGraphic()) {
double graphicW = (labeled.getGraphic().getLayoutBounds().getWidth() + labeled.getGraphicTextGap());
w -= graphicW;
minW -= graphicW;
}
wrapWidth = Math.max(minW, w);
boolean verticalPosition =
(labeled.getContentDisplay() == ContentDisplay.TOP ||
labeled.getContentDisplay() == ContentDisplay.BOTTOM);
double availableHeight = labeled.getHeight() -
snappedTopInset() - snappedBottomInset();
if (!isIgnoreText()) {
availableHeight -= topLabelPadding() + bottomLabelPadding();
}
availableHeight = Math.max(availableHeight, 0);
if (h == -1) {
h = availableHeight;
}
double minH = Math.min(computeMinLabeledPartHeight(wrapWidth, snappedTopInset() , snappedRightInset(), snappedBottomInset(), snappedLeftInset()), availableHeight);
if (verticalPosition && labeled.getGraphic() != null) {
double graphicH = labeled.getGraphic().getLayoutBounds().getHeight() + labeled.getGraphicTextGap();
h -= graphicH;
minH -= graphicH;
}
wrapHeight = Math.max(minH, h);
updateWrappingWidth();
Font font = text.getFont();
OverrunStyle truncationStyle = labeled.getTextOverrun();
String ellipsisString = labeled.getEllipsisString();
if (labeled.isWrapText()) {
result = Utils.computeClippedWrappedText(font, cleanText, wrapWidth, wrapHeight, labeled.getLineSpacing(), truncationStyle, ellipsisString, text.getBoundsType());
} else if (multiline) {
StringBuilder sb = new StringBuilder();
String[] splits = cleanText.split("\n");
for (int i = 0; i < splits.length; i++) {
sb.append(Utils.computeClippedText(font, splits[i], wrapWidth, truncationStyle, ellipsisString));
if (i < splits.length - 1) {
sb.append('\n');
}
}
result = sb.toString();
} else {
result = Utils.computeClippedText(font, cleanText, wrapWidth, truncationStyle, ellipsisString);
}
if (result != null && result.endsWith("\n")) {
result = result.substring(0, result.length() - 1);
}
text.setText(result);
updateWrappingWidth();
invalidText = false;
}
}
private String getCleanText() {
Labeled labeled = getSkinnable();
String sourceText = labeled.getText();
if (sourceText != null && labeled.isMnemonicParsing()) {
if (mnemonicInfo == null) {
mnemonicInfo = new MnemonicInfo(sourceText);
} else {
mnemonicInfo.update(sourceText);
}
return mnemonicInfo.getText();
}
return sourceText;
}
private void addMnemonic() {
if (labeledNode != null) {
mnemonicScene = labeledNode.getScene();
if (mnemonicScene != null) {
mnemonicScene.addMnemonic(new Mnemonic(labeledNode, mnemonicCode));
}
}
}
private void removeMnemonic() {
if (mnemonicScene != null && labeledNode != null) {
mnemonicScene.removeMnemonic(new Mnemonic(labeledNode, mnemonicCode));
mnemonicScene = null;
}
}
private void updateWrappingWidth() {
final Labeled labeled = getSkinnable();
text.setWrappingWidth(0);
if (labeled.isWrapText()) {
double w = Math.min(text.prefWidth(-1), wrapWidth);
text.setWrappingWidth(w);
}
}
boolean isIgnoreGraphic() {
return (graphic == null ||
!graphic.isManaged() ||
getSkinnable().getContentDisplay() == ContentDisplay.TEXT_ONLY);
}
boolean isIgnoreText() {
final Labeled labeled = getSkinnable();
final String txt = getCleanText();
return (txt == null ||
txt.equals("") ||
labeled.getContentDisplay() == ContentDisplay.GRAPHIC_ONLY);
}
}
