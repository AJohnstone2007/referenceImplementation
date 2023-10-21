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
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.css.Styleable;
import javafx.geometry.HPos;
import javafx.util.Callback;
public class HBox extends Pane {
private boolean biasDirty = true;
private double minBaselineComplement = Double.NaN;
private double prefBaselineComplement = Double.NaN;
private Orientation bias;
private double[][] tempArray;
private static final String MARGIN_CONSTRAINT = "hbox-margin";
private static final String HGROW_CONSTRAINT = "hbox-hgrow";
public static void setHgrow(Node child, Priority value) {
setConstraint(child, HGROW_CONSTRAINT, value);
}
public static Priority getHgrow(Node child) {
return (Priority)getConstraint(child, HGROW_CONSTRAINT);
}
public static void setMargin(Node child, Insets value) {
setConstraint(child, MARGIN_CONSTRAINT, value);
}
public static Insets getMargin(Node child) {
return (Insets)getConstraint(child, MARGIN_CONSTRAINT);
}
private static final Callback<Node, Insets> marginAccessor = n -> getMargin(n);
public static void clearConstraints(Node child) {
setHgrow(child, null);
setMargin(child, null);
}
public HBox() {
super();
}
public HBox(double spacing) {
this();
setSpacing(spacing);
}
public HBox(Node... children) {
super();
getChildren().addAll(children);
}
public HBox(double spacing, Node... children) {
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
public CssMetaData getCssMetaData () {
return StyleableProperties.SPACING;
}
@Override
public Object getBean() {
return HBox.this;
}
@Override
public String getName() {
return "spacing";
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
public CssMetaData<HBox, Pos> getCssMetaData() {
return StyleableProperties.ALIGNMENT;
}
@Override
public Object getBean() {
return HBox.this;
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
public final Pos getAlignment() { return alignment == null ? Pos.TOP_LEFT : alignment.get(); }
private Pos getAlignmentInternal() {
Pos localPos = getAlignment();
return localPos == null ? Pos.TOP_LEFT : localPos;
}
public final BooleanProperty fillHeightProperty() {
if (fillHeight == null) {
fillHeight = new StyleableBooleanProperty(true) {
@Override
public void invalidated() {
requestLayout();
}
@Override
public CssMetaData<HBox, Boolean> getCssMetaData() {
return StyleableProperties.FILL_HEIGHT;
}
@Override
public Object getBean() {
return HBox.this;
}
@Override
public String getName() {
return "fillHeight";
}
};
}
return fillHeight;
}
private BooleanProperty fillHeight;
public final void setFillHeight(boolean value) { fillHeightProperty().set(value); }
public final boolean isFillHeight() { return fillHeight == null ? true : fillHeight.get(); }
private boolean shouldFillHeight() {
return isFillHeight() && getAlignmentInternal().getVpos() != VPos.BASELINE;
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
Insets insets = getInsets();
return snapSpaceX(insets.getLeft()) +
computeContentWidth(getManagedChildren(), height, true) +
snapSpaceX(insets.getRight());
}
@Override protected double computeMinHeight(double width) {
Insets insets = getInsets();
List<Node>managed = getManagedChildren();
double contentHeight = 0;
if (width != -1 && getContentBias() != null) {
double prefWidths[][] = getAreaWidths(managed, -1, false);
adjustAreaWidths(managed, prefWidths, width, -1);
contentHeight = computeMaxMinAreaHeight(managed, marginAccessor, prefWidths[0], getAlignmentInternal().getVpos());
} else {
contentHeight = computeMaxMinAreaHeight(managed, marginAccessor, getAlignmentInternal().getVpos());
}
return snapSpaceY(insets.getTop()) +
contentHeight +
snapSpaceY(insets.getBottom());
}
@Override protected double computePrefWidth(double height) {
Insets insets = getInsets();
return snapSpaceX(insets.getLeft()) +
computeContentWidth(getManagedChildren(), height, false) +
snapSpaceX(insets.getRight());
}
@Override protected double computePrefHeight(double width) {
Insets insets = getInsets();
List<Node>managed = getManagedChildren();
double contentHeight = 0;
if (width != -1 && getContentBias() != null) {
double prefWidths[][] = getAreaWidths(managed, -1, false);
adjustAreaWidths(managed, prefWidths, width, -1);
contentHeight = computeMaxPrefAreaHeight(managed, marginAccessor, prefWidths[0], getAlignmentInternal().getVpos());
} else {
contentHeight = computeMaxPrefAreaHeight(managed, marginAccessor, getAlignmentInternal().getVpos());
}
return snapSpaceY(insets.getTop()) +
contentHeight +
snapSpaceY(insets.getBottom());
}
private double[][] getAreaWidths(List<Node>managed, double height, boolean minimum) {
double[][] temp = getTempArray(managed.size());
final double insideHeight = height == -1? -1 : height -
snapSpaceY(getInsets().getTop()) - snapSpaceY(getInsets().getBottom());
final boolean shouldFillHeight = shouldFillHeight();
for (int i = 0, size = managed.size(); i < size; i++) {
Node child = managed.get(i);
Insets margin = getMargin(child);
if (minimum) {
temp[0][i] = computeChildMinAreaWidth(child, getMinBaselineComplement(), margin, insideHeight, shouldFillHeight);
} else {
temp[0][i] = computeChildPrefAreaWidth(child, getPrefBaselineComplement(), margin, insideHeight, shouldFillHeight);
}
}
return temp;
}
private double adjustAreaWidths(List<Node>managed, double areaWidths[][], double width, double height) {
Insets insets = getInsets();
double top = snapSpaceY(insets.getTop());
double bottom = snapSpaceY(insets.getBottom());
double contentWidth = sum(areaWidths[0], managed.size()) + (managed.size()-1)*snapSpaceX(getSpacing());
double extraWidth = width -
snapSpaceX(insets.getLeft()) - snapSpaceX(insets.getRight()) - contentWidth;
if (extraWidth != 0) {
final double refHeight = shouldFillHeight() && height != -1? height - top - bottom : -1;
double remaining = growOrShrinkAreaWidths(managed, areaWidths, Priority.ALWAYS, extraWidth, refHeight);
remaining = growOrShrinkAreaWidths(managed, areaWidths, Priority.SOMETIMES, remaining, refHeight);
contentWidth += (extraWidth - remaining);
}
return contentWidth;
}
private double growOrShrinkAreaWidths(List<Node>managed, double areaWidths[][], Priority priority, double extraWidth, double height) {
final boolean shrinking = extraWidth < 0;
int adjustingNumber = 0;
double[] usedWidths = areaWidths[0];
double[] temp = areaWidths[1];
final boolean shouldFillHeight = shouldFillHeight();
if (shrinking) {
adjustingNumber = managed.size();
for (int i = 0, size = managed.size(); i < size; i++) {
final Node child = managed.get(i);
temp[i] = computeChildMinAreaWidth(child, getMinBaselineComplement(), getMargin(child), height, shouldFillHeight);
}
} else {
for (int i = 0, size = managed.size(); i < size; i++) {
final Node child = managed.get(i);
if (getHgrow(child) == priority) {
temp[i] = computeChildMaxAreaWidth(child, getMinBaselineComplement(), getMargin(child), height, shouldFillHeight);
adjustingNumber++;
} else {
temp[i] = -1;
}
}
}
double available = extraWidth;
outer:while (Math.abs(available) > 1 && adjustingNumber > 0) {
final double portion = snapPortionX(available / adjustingNumber);
for (int i = 0, size = managed.size(); i < size; i++) {
if (temp[i] == -1) {
continue;
}
final double limit = temp[i] - usedWidths[i];
final double change = Math.abs(limit) <= Math.abs(portion)? limit : portion;
usedWidths[i] += change;
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
private double computeContentWidth(List<Node> managedChildren, double height, boolean minimum) {
return sum(getAreaWidths(managedChildren, height, minimum)[0], managedChildren.size())
+ (managedChildren.size()-1)*snapSpaceX(getSpacing());
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
minBaselineComplement = Double.NaN;
prefBaselineComplement = Double.NaN;
baselineOffset = Double.NaN;
super.requestLayout();
}
private double getMinBaselineComplement() {
if (Double.isNaN(minBaselineComplement)) {
if (getAlignmentInternal().getVpos() == VPos.BASELINE) {
minBaselineComplement = getMinBaselineComplement(getManagedChildren());
} else {
minBaselineComplement = -1;
}
}
return minBaselineComplement;
}
private double getPrefBaselineComplement() {
if (Double.isNaN(prefBaselineComplement)) {
if (getAlignmentInternal().getVpos() == VPos.BASELINE) {
prefBaselineComplement = getPrefBaselineComplement(getManagedChildren());
} else {
prefBaselineComplement = -1;
}
}
return prefBaselineComplement;
}
private double baselineOffset = Double.NaN;
@Override
public double getBaselineOffset() {
List<Node> managed = getManagedChildren();
if (managed.isEmpty()) {
return BASELINE_OFFSET_SAME_AS_HEIGHT;
}
if (Double.isNaN(baselineOffset)) {
VPos vpos = getAlignmentInternal().getVpos();
if (vpos == VPos.BASELINE) {
double max = 0;
for (int i =0, sz = managed.size(); i < sz; ++i) {
final Node child = managed.get(i);
double offset = child.getBaselineOffset();
if (offset == BASELINE_OFFSET_SAME_AS_HEIGHT) {
baselineOffset = BASELINE_OFFSET_SAME_AS_HEIGHT;
break;
} else {
Insets margin = getMargin(child);
double top = margin != null ? margin.getTop() : 0;
max = Math.max(max, top + child.getLayoutBounds().getMinY() + offset);
}
}
baselineOffset = max + snappedTopInset();
} else {
baselineOffset = BASELINE_OFFSET_SAME_AS_HEIGHT;
}
}
return baselineOffset;
}
@Override protected void layoutChildren() {
List<Node> managed = getManagedChildren();
Insets insets = getInsets();
Pos align = getAlignmentInternal();
HPos alignHpos = align.getHpos();
VPos alignVpos = align.getVpos();
double width = getWidth();
double height = getHeight();
double top = snapSpaceY(insets.getTop());
double left = snapSpaceX(insets.getLeft());
double bottom = snapSpaceY(insets.getBottom());
double right = snapSpaceX(insets.getRight());
double space = snapSpaceX(getSpacing());
boolean shouldFillHeight = shouldFillHeight();
final double[][] actualAreaWidths = getAreaWidths(managed, height, false);
double contentWidth = adjustAreaWidths(managed, actualAreaWidths, width, height);
double contentHeight = height - top - bottom;
double x = left + computeXOffset(width - left - right, contentWidth, align.getHpos());
double y = top;
double baselineOffset = -1;
if (alignVpos == VPos.BASELINE) {
double baselineComplement = getMinBaselineComplement();
baselineOffset = getAreaBaselineOffset(managed, marginAccessor, i -> actualAreaWidths[0][i],
contentHeight, shouldFillHeight, baselineComplement);
}
for (int i = 0, size = managed.size(); i < size; i++) {
Node child = managed.get(i);
Insets margin = getMargin(child);
layoutInArea(child, x, y, actualAreaWidths[0][i], contentHeight,
baselineOffset, margin, true, shouldFillHeight,
alignHpos, alignVpos);
x += actualAreaWidths[0][i] + space;
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
private static final CssMetaData<HBox,Pos> ALIGNMENT =
new CssMetaData<HBox,Pos>("-fx-alignment",
new EnumConverter<Pos>(Pos.class),
Pos.TOP_LEFT) {
@Override
public boolean isSettable(HBox node) {
return node.alignment == null || !node.alignment.isBound();
}
@Override
public StyleableProperty<Pos> getStyleableProperty(HBox node) {
return (StyleableProperty<Pos>)node.alignmentProperty();
}
};
private static final CssMetaData<HBox,Boolean> FILL_HEIGHT =
new CssMetaData<HBox,Boolean>("-fx-fill-height",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(HBox node) {
return node.fillHeight == null ||
!node.fillHeight.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(HBox node) {
return (StyleableProperty<Boolean>)node.fillHeightProperty();
}
};
private static final CssMetaData<HBox,Number> SPACING =
new CssMetaData<HBox,Number>("-fx-spacing",
SizeConverter.getInstance(), 0.0){
@Override
public boolean isSettable(HBox node) {
return node.spacing == null || !node.spacing.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(HBox node) {
return (StyleableProperty<Number>)node.spacingProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Pane.getClassCssMetaData());
styleables.add(FILL_HEIGHT);
styleables.add(ALIGNMENT);
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
