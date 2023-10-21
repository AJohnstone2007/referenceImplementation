package javafx.scene.layout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.css.converter.EnumConverter;
import javafx.css.Styleable;
import javafx.geometry.HPos;
import javafx.util.Callback;
public class StackPane extends Pane {
private boolean biasDirty = true;
private Orientation bias;
private static final String MARGIN_CONSTRAINT = "stackpane-margin";
private static final String ALIGNMENT_CONSTRAINT = "stackpane-alignment";
public static void setAlignment(Node child, Pos value) {
setConstraint(child, ALIGNMENT_CONSTRAINT, value);
}
public static Pos getAlignment(Node child) {
return (Pos)getConstraint(child, ALIGNMENT_CONSTRAINT);
}
public static void setMargin(Node child, Insets value) {
setConstraint(child, MARGIN_CONSTRAINT, value);
}
public static Insets getMargin(Node child) {
return (Insets)getConstraint(child, MARGIN_CONSTRAINT);
}
private static final Callback<Node, Insets> marginAccessor = n -> getMargin(n);
public static void clearConstraints(Node child) {
setAlignment(child, null);
setMargin(child, null);
}
public StackPane() {
super();
}
public StackPane(Node... children) {
super();
getChildren().addAll(children);
}
public final ObjectProperty<Pos> alignmentProperty() {
if (alignment == null) {
alignment = new StyleableObjectProperty<Pos>(Pos.CENTER) {
@Override
public void invalidated() {
requestLayout();
}
@Override
public CssMetaData<StackPane, Pos> getCssMetaData() {
return StyleableProperties.ALIGNMENT;
}
@Override
public Object getBean() {
return StackPane.this;
}
@Override
public String getName() {
return "alignment";
}
};
}
return alignment;
}
private ObjectProperty<Pos> alignment;
public final void setAlignment(Pos value) { alignmentProperty().set(value); }
public final Pos getAlignment() { return alignment == null ? Pos.CENTER : alignment.get(); }
private Pos getAlignmentInternal() {
Pos localPos = getAlignment();
return localPos == null ? Pos.CENTER : localPos;
}
@Override public Orientation getContentBias() {
if (biasDirty) {
bias = null;
final List<Node> children = getManagedChildren();
for (Node child : children) {
Orientation contentBias = child.getContentBias();
if (contentBias != null) {
bias = contentBias;
if (contentBias == Orientation.HORIZONTAL) {
break;
}
}
}
biasDirty = false;
}
return bias;
}
@Override protected double computeMinWidth(double height) {
List<Node>managed = getManagedChildren();
return getInsets().getLeft() +
computeMaxMinAreaWidth(managed, marginAccessor, height, true) +
getInsets().getRight();
}
@Override protected double computeMinHeight(double width) {
List<Node>managed = getManagedChildren();
return getInsets().getTop() +
computeMaxMinAreaHeight(managed, marginAccessor, getAlignmentInternal().getVpos(), width) +
getInsets().getBottom();
}
@Override protected double computePrefWidth(double height) {
List<Node>managed = getManagedChildren();
Insets padding = getInsets();
return padding.getLeft() +
computeMaxPrefAreaWidth(managed, marginAccessor,
(height == -1) ? -1 : (height - padding.getTop() - padding.getBottom()), true) +
padding.getRight();
}
@Override protected double computePrefHeight(double width) {
List<Node>managed = getManagedChildren();
Insets padding = getInsets();
return padding.getTop() +
computeMaxPrefAreaHeight(managed, marginAccessor,
(width == -1) ? -1 : (width - padding.getLeft() - padding.getRight()),
getAlignmentInternal().getVpos()) +
padding.getBottom();
}
@Override public void requestLayout() {
biasDirty = true;
bias = null;
super.requestLayout();
}
@Override protected void layoutChildren() {
List<Node> managed = getManagedChildren();
Pos align = getAlignmentInternal();
HPos alignHpos = align.getHpos();
VPos alignVpos = align.getVpos();
final double width = getWidth();
double height = getHeight();
double top = getInsets().getTop();
double right = getInsets().getRight();
double left = getInsets().getLeft();
double bottom = getInsets().getBottom();
double contentWidth = width - left - right;
double contentHeight = height - top - bottom;
double baselineOffset = alignVpos == VPos.BASELINE ?
getAreaBaselineOffset(managed, marginAccessor, i -> width, contentHeight, true)
: 0;
for (int i = 0, size = managed.size(); i < size; i++) {
Node child = managed.get(i);
Pos childAlignment = StackPane.getAlignment(child);
layoutInArea(child, left, top,
contentWidth, contentHeight,
baselineOffset, getMargin(child),
childAlignment != null? childAlignment.getHpos() : alignHpos,
childAlignment != null? childAlignment.getVpos() : alignVpos);
}
}
private static class StyleableProperties {
private static final CssMetaData<StackPane,Pos> ALIGNMENT =
new CssMetaData<StackPane,Pos>("-fx-alignment",
new EnumConverter<Pos>(Pos.class),
Pos.CENTER) {
@Override
public boolean isSettable(StackPane node) {
return node.alignment == null ||
!node.alignment.isBound();
}
@Override
public StyleableProperty<Pos> getStyleableProperty(StackPane node) {
return (StyleableProperty<Pos>)node.alignmentProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Region.getClassCssMetaData());
styleables.add(ALIGNMENT);
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
}
