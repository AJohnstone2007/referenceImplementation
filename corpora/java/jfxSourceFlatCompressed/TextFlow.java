package javafx.scene.text;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.PathElement;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.scene.text.GlyphList;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.scene.text.TextLayoutFactory;
import com.sun.javafx.scene.text.TextSpan;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.css.Styleable;
import javafx.css.StyleableIntegerProperty;
import javafx.css.StyleableProperty;
public class TextFlow extends Pane {
private TextLayout layout;
private boolean needsContent;
private boolean inLayout;
public TextFlow() {
super();
effectiveNodeOrientationProperty().addListener(observable -> checkOrientation());
setAccessibleRole(AccessibleRole.TEXT);
}
public TextFlow(Node... children) {
this();
getChildren().addAll(children);
}
private void checkOrientation() {
NodeOrientation orientation = getEffectiveNodeOrientation();
boolean rtl = orientation == NodeOrientation.RIGHT_TO_LEFT;
int dir = rtl ? TextLayout.DIRECTION_RTL : TextLayout.DIRECTION_LTR;
TextLayout layout = getTextLayout();
if (layout.setDirection(dir)) {
requestLayout();
}
}
public final HitInfo hitTest(javafx.geometry.Point2D point) {
if (point != null) {
TextLayout layout = getTextLayout();
double x = point.getX() ;
double y = point.getY() ;
TextLayout.Hit layoutHit = layout.getHitInfo((float)x, (float)y);
return new HitInfo(layoutHit.getCharIndex(), layoutHit.getInsertionIndex(),
layoutHit.isLeading(), null );
} else {
return null;
}
}
public PathElement[] caretShape(int charIndex, boolean leading) {
return getTextLayout().getCaretShape(charIndex, leading, 0, 0);
}
public final PathElement[] rangeShape(int start, int end) {
return getRange(start, end, TextLayout.TYPE_TEXT);
}
@Override
public boolean usesMirroring() {
return false;
}
@Override protected void setWidth(double value) {
if (value != getWidth()) {
TextLayout layout = getTextLayout();
Insets insets = getInsets();
double left = snapSpaceX(insets.getLeft());
double right = snapSpaceX(insets.getRight());
double width = Math.max(1, value - left - right);
layout.setWrapWidth((float)width);
super.setWidth(value);
}
}
@Override protected double computePrefWidth(double height) {
TextLayout layout = getTextLayout();
layout.setWrapWidth(0);
double width = layout.getBounds().getWidth();
Insets insets = getInsets();
double left = snapSpaceX(insets.getLeft());
double right = snapSpaceX(insets.getRight());
double wrappingWidth = Math.max(1, getWidth() - left - right);
layout.setWrapWidth((float)wrappingWidth);
return left + width + right;
}
@Override protected double computePrefHeight(double width) {
TextLayout layout = getTextLayout();
Insets insets = getInsets();
double left = snapSpaceX(insets.getLeft());
double right = snapSpaceX(insets.getRight());
if (width == USE_COMPUTED_SIZE) {
layout.setWrapWidth(0);
} else {
double wrappingWidth = Math.max(1, width - left - right);
layout.setWrapWidth((float)wrappingWidth);
}
double height = layout.getBounds().getHeight();
double wrappingWidth = Math.max(1, getWidth() - left - right);
layout.setWrapWidth((float)wrappingWidth);
double top = snapSpaceY(insets.getTop());
double bottom = snapSpaceY(insets.getBottom());
return top + height + bottom;
}
@Override protected double computeMinHeight(double width) {
return computePrefHeight(width);
}
@Override public void requestLayout() {
if (inLayout) return;
needsContent = true;
super.requestLayout();
}
@Override public Orientation getContentBias() {
return Orientation.HORIZONTAL;
}
@Override protected void layoutChildren() {
inLayout = true;
Insets insets = getInsets();
double top = snapSpaceY(insets.getTop());
double left = snapSpaceX(insets.getLeft());
GlyphList[] runs = getTextLayout().getRuns();
for (int j = 0; j < runs.length; j++) {
GlyphList run = runs[j];
TextSpan span = run.getTextSpan();
if (span instanceof EmbeddedSpan) {
Node child = ((EmbeddedSpan)span).getNode();
Point2D location = run.getLocation();
double baselineOffset = -run.getLineBounds().getMinY();
layoutInArea(child, left + location.x, top + location.y,
run.getWidth(), run.getHeight(),
baselineOffset, null, true, true,
HPos.CENTER, VPos.BASELINE);
}
}
List<Node> managed = getManagedChildren();
for (Node node: managed) {
if (node instanceof Text) {
Text text = (Text)node;
text.layoutSpan(runs);
BaseBounds spanBounds = text.getSpanBounds();
text.relocate(left + spanBounds.getMinX(),
top + spanBounds.getMinY());
}
}
inLayout = false;
}
private PathElement[] getRange(int start, int end, int type) {
TextLayout layout = getTextLayout();
return layout.getRange(start, end, type, 0, 0);
}
private static class EmbeddedSpan implements TextSpan {
RectBounds bounds;
Node node;
public EmbeddedSpan(Node node, double baseline, double width, double height) {
this.node = node;
bounds = new RectBounds(0, (float)-baseline,
(float)width, (float)(height - baseline));
}
@Override public String getText() {
return "\uFFFC";
}
@Override public Object getFont() {
return null;
}
@Override public RectBounds getBounds() {
return bounds;
}
public Node getNode() {
return node;
}
}
TextLayout getTextLayout() {
if (layout == null) {
TextLayoutFactory factory = Toolkit.getToolkit().getTextLayoutFactory();
layout = factory.createLayout();
layout.setTabSize(getTabSize());
needsContent = true;
}
if (needsContent) {
List<Node> children = getManagedChildren();
TextSpan[] spans = new TextSpan[children.size()];
for (int i = 0; i < spans.length; i++) {
Node node = children.get(i);
if (node instanceof Text) {
spans[i] = ((Text)node).getTextSpan();
} else {
double baseline = node.getBaselineOffset();
if (baseline == BASELINE_OFFSET_SAME_AS_HEIGHT) {
baseline = node.getLayoutBounds().getHeight();
}
double width = computeChildPrefAreaWidth(node, null);
double height = computeChildPrefAreaHeight(node, null);
spans[i] = new EmbeddedSpan(node, baseline, width, height);
}
}
layout.setContent(spans);
needsContent = false;
}
return layout;
}
private ObjectProperty<TextAlignment> textAlignment;
public final void setTextAlignment(TextAlignment value) {
textAlignmentProperty().set(value);
}
public final TextAlignment getTextAlignment() {
return textAlignment == null ? TextAlignment.LEFT : textAlignment.get();
}
public final ObjectProperty<TextAlignment> textAlignmentProperty() {
if (textAlignment == null) {
textAlignment =
new StyleableObjectProperty<TextAlignment>(TextAlignment.LEFT) {
@Override public Object getBean() { return TextFlow.this; }
@Override public String getName() { return "textAlignment"; }
@Override public CssMetaData<TextFlow, TextAlignment> getCssMetaData() {
return StyleableProperties.TEXT_ALIGNMENT;
}
@Override public void invalidated() {
TextAlignment align = get();
if (align == null) align = TextAlignment.LEFT;
TextLayout layout = getTextLayout();
layout.setAlignment(align.ordinal());
requestLayout();
}
};
}
return textAlignment;
}
private DoubleProperty lineSpacing;
public final void setLineSpacing(double spacing) {
lineSpacingProperty().set(spacing);
}
public final double getLineSpacing() {
return lineSpacing == null ? 0 : lineSpacing.get();
}
public final DoubleProperty lineSpacingProperty() {
if (lineSpacing == null) {
lineSpacing =
new StyleableDoubleProperty(0) {
@Override public Object getBean() { return TextFlow.this; }
@Override public String getName() { return "lineSpacing"; }
@Override public CssMetaData<TextFlow, Number> getCssMetaData() {
return StyleableProperties.LINE_SPACING;
}
@Override public void invalidated() {
TextLayout layout = getTextLayout();
if (layout.setLineSpacing((float)get())) {
requestLayout();
}
}
};
}
return lineSpacing;
}
private IntegerProperty tabSize;
public final IntegerProperty tabSizeProperty() {
if (tabSize == null) {
tabSize = new StyleableIntegerProperty(TextLayout.DEFAULT_TAB_SIZE) {
@Override public Object getBean() { return TextFlow.this; }
@Override public String getName() { return "tabSize"; }
@Override public CssMetaData getCssMetaData() {
return StyleableProperties.TAB_SIZE;
}
@Override protected void invalidated() {
TextLayout layout = getTextLayout();
if (layout.setTabSize(get())) {
requestLayout();
}
}
};
}
return tabSize;
}
public final int getTabSize() {
return tabSize == null ? TextLayout.DEFAULT_TAB_SIZE : tabSize.get();
}
public final void setTabSize(int spaces) {
tabSizeProperty().set(spaces);
}
@Override public final double getBaselineOffset() {
Insets insets = getInsets();
double top = snapSpaceY(insets.getTop());
return top - getTextLayout().getBounds().getMinY();
}
private static class StyleableProperties {
private static final
CssMetaData<TextFlow, TextAlignment> TEXT_ALIGNMENT =
new CssMetaData<TextFlow,TextAlignment>("-fx-text-alignment",
new EnumConverter<TextAlignment>(TextAlignment.class),
TextAlignment.LEFT) {
@Override public boolean isSettable(TextFlow node) {
return node.textAlignment == null || !node.textAlignment.isBound();
}
@Override public StyleableProperty<TextAlignment> getStyleableProperty(TextFlow node) {
return (StyleableProperty<TextAlignment>)node.textAlignmentProperty();
}
};
private static final
CssMetaData<TextFlow,Number> LINE_SPACING =
new CssMetaData<TextFlow,Number>("-fx-line-spacing",
SizeConverter.getInstance(), 0) {
@Override public boolean isSettable(TextFlow node) {
return node.lineSpacing == null || !node.lineSpacing.isBound();
}
@Override public StyleableProperty<Number> getStyleableProperty(TextFlow node) {
return (StyleableProperty<Number>)node.lineSpacingProperty();
}
};
private static final CssMetaData<TextFlow, Number> TAB_SIZE =
new CssMetaData<TextFlow,Number>("-fx-tab-size",
SizeConverter.getInstance(), TextLayout.DEFAULT_TAB_SIZE) {
@Override
public boolean isSettable(TextFlow node) {
return node.tabSize == null || !node.tabSize.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(TextFlow node) {
return (StyleableProperty<Number>)node.tabSizeProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Pane.getClassCssMetaData());
styleables.add(TEXT_ALIGNMENT);
styleables.add(LINE_SPACING);
styleables.add(TAB_SIZE);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
static double boundedSize(double min, double pref, double max) {
double a = pref >= min ? pref : min;
double b = min >= max ? min : max;
return a <= b ? a : b;
}
double computeChildPrefAreaWidth(Node child, Insets margin) {
return computeChildPrefAreaWidth(child, margin, -1);
}
double computeChildPrefAreaWidth(Node child, Insets margin, double height) {
double top = margin != null? snapSpaceY(margin.getTop()) : 0;
double bottom = margin != null? snapSpaceY(margin.getBottom()) : 0;
double left = margin != null? snapSpaceX(margin.getLeft()) : 0;
double right = margin != null? snapSpaceX(margin.getRight()) : 0;
double alt = -1;
if (child.getContentBias() == Orientation.VERTICAL) {
alt = snapSizeY(boundedSize(
child.minHeight(-1), height != -1? height - top - bottom :
child.prefHeight(-1), child.maxHeight(-1)));
}
return left + snapSizeX(boundedSize(child.minWidth(alt), child.prefWidth(alt), child.maxWidth(alt))) + right;
}
double computeChildPrefAreaHeight(Node child, Insets margin) {
return computeChildPrefAreaHeight(child, margin, -1);
}
double computeChildPrefAreaHeight(Node child, Insets margin, double width) {
double top = margin != null? snapSpaceY(margin.getTop()) : 0;
double bottom = margin != null? snapSpaceY(margin.getBottom()) : 0;
double left = margin != null? snapSpaceX(margin.getLeft()) : 0;
double right = margin != null? snapSpaceX(margin.getRight()) : 0;
double alt = -1;
if (child.getContentBias() == Orientation.HORIZONTAL) {
alt = snapSizeX(boundedSize(
child.minWidth(-1), width != -1? width - left - right :
child.prefWidth(-1), child.maxWidth(-1)));
}
return top + snapSizeY(boundedSize(child.minHeight(alt), child.prefHeight(alt), child.maxHeight(alt))) + bottom;
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case TEXT: {
String accText = getAccessibleText();
if (accText != null && !accText.isEmpty()) return accText;
StringBuilder title = new StringBuilder();
for (Node node: getChildren()) {
Object text = node.queryAccessibleAttribute(AccessibleAttribute.TEXT, parameters);
if (text != null) {
title.append(text.toString());
}
}
return title.toString();
}
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
}
