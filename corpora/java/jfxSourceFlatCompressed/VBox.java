package javafx.scene.layout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.css.Styleable;
import javafx.util.Callback;
public class VBox extends Pane {
private boolean biasDirty = true;
private Orientation bias;
private double[][] tempArray;
private static final String MARGIN_CONSTRAINT = "vbox-margin";
private static final String VGROW_CONSTRAINT = "vbox-vgrow";
public static void setVgrow(Node child, Priority value) {
setConstraint(child, VGROW_CONSTRAINT, value);
}
public static Priority getVgrow(Node child) {
return (Priority)getConstraint(child, VGROW_CONSTRAINT);
}
public static void setMargin(Node child, Insets value) {
setConstraint(child, MARGIN_CONSTRAINT, value);
}
public static Insets getMargin(Node child) {
return (Insets)getConstraint(child, MARGIN_CONSTRAINT);
}
private static final Callback<Node, Insets> marginAccessor = n -> getMargin(n);
public static void clearConstraints(Node child) {
setVgrow(child, null);
setMargin(child, null);
}
public VBox() {
super();
}
public VBox(double spacing) {
this();
setSpacing(spacing);
}
public VBox(Node... children) {
super();
getChildren().addAll(children);
}
public VBox(double spacing, Node... children) {
this();
setSpacing(spacing);
getChildren().addAll(children);
}
public final DoubleProperty spacingProperty() {
if (spacing == null) {
spacing = new StyleableDoubleProperty() {
@Override
public void invalidated() {
requestLayout();
}
@Override
public Object getBean() {
return VBox.this;
}
@Override
public String getName() {
return "spacing";
}
@Override
public CssMetaData<VBox, Number> getCssMetaData() {
return StyleableProperties.SPACING;
}
};
}
return spacing;
}
private DoubleProperty spacing;
public final void setSpacing(double value) { spacingProperty().set(value); }
public final double getSpacing() { return spacing == null ? 0 : spacing.get(); }
public final ObjectProperty<Pos> alignmentProperty() {
if (alignment == null) {
alignment = new StyleableObjectProperty<Pos>(Pos.TOP_LEFT) {
@Override
public void invalidated() {
requestLayout();
}
@Override
public Object getBean() {
return VBox.this;
}
@Override
public String getName() {
return "alignment";
}
@Override
public CssMetaData<VBox, Pos> getCssMetaData() {
return StyleableProperties.ALIGNMENT;
}
};
}
return alignment;
}
private ObjectProperty<Pos> alignment;
public final void setAlignment(Pos value) { alignmentProperty().set(value); }
public final Pos getAlignment() { return alignment == null ? Pos.TOP_LEFT : alignment.get(); }
private Pos getAlignmentInternal() {
Pos localPos = getAlignment();
return localPos == null ? Pos.TOP_LEFT : localPos;
}
public final BooleanProperty fillWidthProperty() {
if (fillWidth == null) {
fillWidth = new StyleableBooleanProperty(true) {
@Override
public void invalidated() {
requestLayout();
}
@Override
public Object getBean() {
return VBox.this;
}
@Override
public String getName() {
return "fillWidth";
}
@Override
public CssMetaData<VBox, Boolean> getCssMetaData() {
return StyleableProperties.FILL_WIDTH;
}
};
}
return fillWidth;
}
private BooleanProperty fillWidth;
public final void setFillWidth(boolean value) { fillWidthProperty().set(value); }
public final boolean isFillWidth() { return fillWidth == null ? true : fillWidth.get(); }
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
Insets insets = getInsets();
List<Node>managed = getManagedChildren();
double contentWidth = 0;
if (height != -1 && getContentBias() != null) {
double prefHeights[][] = getAreaHeights(managed, -1, false);
adjustAreaHeights(managed, prefHeights, height, -1);
contentWidth = computeMaxMinAreaWidth(managed, marginAccessor, prefHeights[0], false);
} else {
contentWidth = computeMaxMinAreaWidth(managed, marginAccessor);
}
return snapSpaceX(insets.getLeft()) + contentWidth + snapSpaceX(insets.getRight());
}
@Override protected double computeMinHeight(double width) {
Insets insets = getInsets();
return snapSpaceY(insets.getTop()) +
computeContentHeight(getManagedChildren(), width, true) +
snapSpaceY(insets.getBottom());
}
@Override protected double computePrefWidth(double height) {
Insets insets = getInsets();
List<Node>managed = getManagedChildren();
double contentWidth = 0;
if (height != -1 && getContentBias() != null) {
double prefHeights[][] = getAreaHeights(managed, -1, false);
adjustAreaHeights(managed, prefHeights, height, -1);
contentWidth = computeMaxPrefAreaWidth(managed, marginAccessor, prefHeights[0], false);
} else {
contentWidth = computeMaxPrefAreaWidth(managed, marginAccessor);
}
return snapSpaceX(insets.getLeft()) + contentWidth + snapSpaceX(insets.getRight());
}
@Override protected double computePrefHeight(double width) {
Insets insets = getInsets();
double d = snapSpaceY(insets.getTop()) +
computeContentHeight(getManagedChildren(), width, false) +
snapSpaceY(insets.getBottom());
return d;
}
private double[][] getAreaHeights(List<Node>managed, double width, boolean minimum) {
double[][] temp = getTempArray(managed.size());
final double insideWidth = width == -1? -1 : width -
snapSpaceX(getInsets().getLeft()) - snapSpaceX(getInsets().getRight());
final boolean isFillWidth = isFillWidth();
for (int i = 0, size = managed.size(); i < size; i++) {
Node child = managed.get(i);
Insets margin = getMargin(child);
if (minimum) {
if (insideWidth != -1 && isFillWidth) {
temp[0][i] = computeChildMinAreaHeight(child, -1, margin, insideWidth);
} else {
temp[0][i] = computeChildMinAreaHeight(child, -1, margin, -1);
}
} else {
if (insideWidth != -1 && isFillWidth) {
temp[0][i] = computeChildPrefAreaHeight(child, -1, margin, insideWidth);
} else {
temp[0][i] = computeChildPrefAreaHeight(child, -1, margin, -1);
}
}
}
return temp;
}
private double adjustAreaHeights(List<Node>managed, double areaHeights[][], double height, double width) {
Insets insets = getInsets();
double left = snapSpaceX(insets.getLeft());
double right = snapSpaceX(insets.getRight());
double contentHeight = sum(areaHeights[0], managed.size()) + (managed.size()-1)*snapSpaceY(getSpacing());
double extraHeight = height -
snapSpaceY(insets.getTop()) - snapSpaceY(insets.getBottom()) - contentHeight;
if (extraHeight != 0) {
final double refWidth = isFillWidth()&& width != -1? width - left - right : -1;
double remaining = growOrShrinkAreaHeights(managed, areaHeights, Priority.ALWAYS, extraHeight, refWidth);
remaining = growOrShrinkAreaHeights(managed, areaHeights, Priority.SOMETIMES, remaining, refWidth);
contentHeight += (extraHeight - remaining);
}
return contentHeight;
}
private double growOrShrinkAreaHeights(List<Node>managed, double areaHeights[][], Priority priority, double extraHeight, double width) {
final boolean shrinking = extraHeight < 0;
int adjustingNumber = 0;
double[] usedHeights = areaHeights[0];
double[] temp = areaHeights[1];
if (shrinking) {
adjustingNumber = managed.size();
for (int i = 0, size = managed.size(); i < size; i++) {
final Node child = managed.get(i);
temp[i] = computeChildMinAreaHeight(child, -1, getMargin(child), width);
}
} else {
for (int i = 0, size = managed.size(); i < size; i++) {
final Node child = managed.get(i);
if (getVgrow(child) == priority) {
temp[i] = computeChildMaxAreaHeight(child, -1, getMargin(child), width);
adjustingNumber++;
} else {
temp[i] = -1;
}
}
}
double available = extraHeight;
outer: while (Math.abs(available) > 1 && adjustingNumber > 0) {
final double portion = snapPortionY(available / adjustingNumber);
for (int i = 0, size = managed.size(); i < size; i++) {
if (temp[i] == -1) {
continue;
}
final double limit = temp[i] - usedHeights[i];
final double change = Math.abs(limit) <= Math.abs(portion)? limit : portion;
usedHeights[i] += change;
available -= change;
if (Math.abs(available) < 1) {
break outer;
}
if (Math.abs(change) < Math.abs(portion)) {
temp[i] = -1;
adjustingNumber--;
}
}
}
return available;
}
private double computeContentHeight(List<Node> managedChildren, double width, boolean minimum) {
return sum(getAreaHeights(managedChildren, width, minimum)[0], managedChildren.size())
+ (managedChildren.size()-1)*snapSpaceY(getSpacing());
}
private static double sum(double[] array, int size) {
int i = 0;
double res = 0;
while (i != size) {
res += array[i++];
}
return res;
}
@Override public void requestLayout() {
biasDirty = true;
bias = null;
super.requestLayout();
}
@Override protected void layoutChildren() {
List<Node> managed = getManagedChildren();
Insets insets = getInsets();
double width = getWidth();
double height = getHeight();
double top = snapSpaceY(insets.getTop());
double left = snapSpaceX(insets.getLeft());
double bottom = snapSpaceY(insets.getBottom());
double right = snapSpaceX(insets.getRight());
double space = snapSpaceY(getSpacing());
HPos hpos = getAlignmentInternal().getHpos();
VPos vpos = getAlignmentInternal().getVpos();
boolean isFillWidth = isFillWidth();
double[][] actualAreaHeights = getAreaHeights(managed, width, false);
double contentWidth = width - left - right;
double contentHeight = adjustAreaHeights(managed, actualAreaHeights, height, width);
double x = left;
double y = top + computeYOffset(height - top - bottom, contentHeight, vpos);
for (int i = 0, size = managed.size(); i < size; i++) {
Node child = managed.get(i);
layoutInArea(child, x, y, contentWidth, actualAreaHeights[0][i],
actualAreaHeights[0][i],
getMargin(child), isFillWidth, true,
hpos, vpos);
y += actualAreaHeights[0][i] + space;
}
}
private double[][] getTempArray(int size) {
if (tempArray == null) {
tempArray = new double[2][size];
} else if (tempArray[0].length < size) {
tempArray = new double[2][Math.max(tempArray.length * 3, size)];
}
return tempArray;
}
private static class StyleableProperties {
private static final CssMetaData<VBox,Pos> ALIGNMENT =
new CssMetaData<VBox,Pos>("-fx-alignment",
new EnumConverter<Pos>(Pos.class), Pos.TOP_LEFT){
@Override
public boolean isSettable(VBox node) {
return node.alignment == null || !node.alignment.isBound();
}
@Override
public StyleableProperty<Pos> getStyleableProperty(VBox node) {
return (StyleableProperty<Pos>)node.alignmentProperty();
}
};
private static final CssMetaData<VBox,Boolean> FILL_WIDTH =
new CssMetaData<VBox,Boolean>("-fx-fill-width",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(VBox node) {
return node.fillWidth == null || !node.fillWidth.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(VBox node) {
return (StyleableProperty<Boolean>)node.fillWidthProperty();
}
};
private static final CssMetaData<VBox,Number> SPACING =
new CssMetaData<VBox,Number>("-fx-spacing",
SizeConverter.getInstance(), 0d) {
@Override
public boolean isSettable(VBox node) {
return node.spacing == null || !node.spacing.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(VBox node) {
return (StyleableProperty<Number>)node.spacingProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Region.getClassCssMetaData());
styleables.add(ALIGNMENT);
styleables.add(FILL_WIDTH);
styleables.add(SPACING);
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
