package javafx.scene.layout;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.util.Callback;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import com.sun.javafx.util.Logging;
import com.sun.javafx.util.TempState;
import com.sun.javafx.binding.ExpressionHelper;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.InsetsConverter;
import javafx.css.converter.ShapeConverter;
import javafx.css.converter.SizeConverter;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Vec2d;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.scene.layout.RegionHelper;
import com.sun.javafx.scene.shape.ShapeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGRegion;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.Scene;
import javafx.stage.Window;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
public class Region extends Parent {
static {
RegionHelper.setRegionAccessor(new RegionHelper.RegionAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((Region) node).doCreatePeer();
}
@Override
public void doUpdatePeer(Node node) {
((Region) node).doUpdatePeer();
}
@Override
public Bounds doComputeLayoutBounds(Node node) {
return ((Region) node).doComputeLayoutBounds();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((Region) node).doComputeGeomBounds(bounds, tx);
}
@Override
public boolean doComputeContains(Node node, double localX, double localY) {
return ((Region) node).doComputeContains(localX, localY);
}
@Override
public void doNotifyLayoutBoundsChanged(Node node) {
((Region) node).doNotifyLayoutBoundsChanged();
}
@Override
public void doPickNodeLocal(Node node, PickRay localPickRay,
PickResultChooser result) {
((Region) node).doPickNodeLocal(localPickRay, result);
}
});
}
public static final double USE_PREF_SIZE = Double.NEGATIVE_INFINITY;
public static final double USE_COMPUTED_SIZE = -1;
static Vec2d TEMP_VEC2D = new Vec2d();
private static final double EPSILON = 1e-14;
static double boundedSize(double min, double pref, double max) {
double a = pref >= min ? pref : min;
double b = min >= max ? min : max;
return a <= b ? a : b;
}
double adjustWidthByMargin(double width, Insets margin) {
if (margin == null || margin == Insets.EMPTY) {
return width;
}
boolean isSnapToPixel = isSnapToPixel();
return width - snapSpaceX(margin.getLeft(), isSnapToPixel) - snapSpaceX(margin.getRight(), isSnapToPixel);
}
double adjustHeightByMargin(double height, Insets margin) {
if (margin == null || margin == Insets.EMPTY) {
return height;
}
boolean isSnapToPixel = isSnapToPixel();
return height - snapSpaceY(margin.getTop(), isSnapToPixel) - snapSpaceY(margin.getBottom(), isSnapToPixel);
}
private static double getSnapScaleX(Node n) {
return _getSnapScaleXimpl(n.getScene());
}
private static double _getSnapScaleXimpl(Scene scene) {
if (scene == null) return 1.0;
Window window = scene.getWindow();
if (window == null) return 1.0;
return window.getRenderScaleX();
}
private static double getSnapScaleY(Node n) {
return _getSnapScaleYimpl(n.getScene());
}
private static double _getSnapScaleYimpl(Scene scene) {
if (scene == null) return 1.0;
Window window = scene.getWindow();
if (window == null) return 1.0;
return window.getRenderScaleY();
}
private double getSnapScaleX() {
return _getSnapScaleXimpl(getScene());
}
private double getSnapScaleY() {
return _getSnapScaleYimpl(getScene());
}
private static double scaledRound(double value, double scale) {
return Math.round(value * scale) / scale;
}
private static double scaledFloor(double value, double scale) {
return Math.floor(value * scale + EPSILON) / scale;
}
private static double scaledCeil(double value, double scale) {
return Math.ceil(value * scale - EPSILON) / scale;
}
private double snapSpaceX(double value, boolean snapToPixel) {
return snapToPixel ? scaledRound(value, getSnapScaleX()) : value;
}
private double snapSpaceY(double value, boolean snapToPixel) {
return snapToPixel ? scaledRound(value, getSnapScaleY()) : value;
}
private static double snapSpace(double value, boolean snapToPixel, double snapScale) {
return snapToPixel ? scaledRound(value, snapScale) : value;
}
private double snapSizeX(double value, boolean snapToPixel) {
return snapToPixel ? scaledCeil(value, getSnapScaleX()) : value;
}
private double snapSizeY(double value, boolean snapToPixel) {
return snapToPixel ? scaledCeil(value, getSnapScaleY()) : value;
}
private static double snapSize(double value, boolean snapToPixel, double snapScale) {
return snapToPixel ? scaledCeil(value, snapScale) : value;
}
private double snapPositionX(double value, boolean snapToPixel) {
return snapToPixel ? scaledRound(value, getSnapScaleX()) : value;
}
private double snapPositionY(double value, boolean snapToPixel) {
return snapToPixel ? scaledRound(value, getSnapScaleY()) : value;
}
private static double snapPosition(double value, boolean snapToPixel, double snapScale) {
return snapToPixel ? scaledRound(value, snapScale) : value;
}
private double snapPortionX(double value, boolean snapToPixel) {
if (!snapToPixel || value == 0) return value;
double s = getSnapScaleX();
value *= s;
if (value > 0) {
value = Math.max(1, Math.floor(value + EPSILON));
} else {
value = Math.min(-1, Math.ceil(value - EPSILON));
}
return value / s;
}
private double snapPortionY(double value, boolean snapToPixel) {
if (!snapToPixel || value == 0) return value;
double s = getSnapScaleY();
value *= s;
if (value > 0) {
value = Math.max(1, Math.floor(value + EPSILON));
} else {
value = Math.min(-1, Math.ceil(value - EPSILON));
}
return value / s;
}
double getAreaBaselineOffset(List<Node> children, Callback<Node, Insets> margins,
Function<Integer, Double> positionToWidth,
double areaHeight, boolean fillHeight) {
return getAreaBaselineOffset(children, margins, positionToWidth, areaHeight, fillHeight, isSnapToPixel());
}
static double getAreaBaselineOffset(List<Node> children, Callback<Node, Insets> margins,
Function<Integer, Double> positionToWidth,
double areaHeight, boolean fillHeight, boolean snapToPixel) {
return getAreaBaselineOffset(children, margins, positionToWidth, areaHeight, fillHeight,
getMinBaselineComplement(children), snapToPixel);
}
double getAreaBaselineOffset(List<Node> children, Callback<Node, Insets> margins,
Function<Integer, Double> positionToWidth,
double areaHeight, final boolean fillHeight, double minComplement) {
return getAreaBaselineOffset(children, margins, positionToWidth, areaHeight, fillHeight, minComplement, isSnapToPixel());
}
static double getAreaBaselineOffset(List<Node> children, Callback<Node, Insets> margins,
Function<Integer, Double> positionToWidth,
double areaHeight, final boolean fillHeight, double minComplement, boolean snapToPixel) {
return getAreaBaselineOffset(children, margins, positionToWidth, areaHeight, t -> fillHeight, minComplement, snapToPixel);
}
double getAreaBaselineOffset(List<Node> children, Callback<Node, Insets> margins,
Function<Integer, Double> positionToWidth,
double areaHeight, Function<Integer, Boolean> fillHeight, double minComplement) {
return getAreaBaselineOffset(children, margins, positionToWidth, areaHeight, fillHeight, minComplement, isSnapToPixel());
}
static double getAreaBaselineOffset(List<Node> children, Callback<Node, Insets> margins,
Function<Integer, Double> positionToWidth,
double areaHeight, Function<Integer, Boolean> fillHeight, double minComplement, boolean snapToPixel) {
double b = 0;
double snapScaleV = 0.0;
for (int i = 0;i < children.size(); ++i) {
Node n = children.get(i);
if (snapToPixel && i == 0) snapScaleV = getSnapScaleY(n.getParent());
Insets margin = margins.call(n);
double top = margin != null ? snapSpace(margin.getTop(), snapToPixel, snapScaleV) : 0;
double bottom = (margin != null ? snapSpace(margin.getBottom(), snapToPixel, snapScaleV) : 0);
final double bo = n.getBaselineOffset();
if (bo == BASELINE_OFFSET_SAME_AS_HEIGHT) {
double alt = -1;
if (n.getContentBias() == Orientation.HORIZONTAL) {
alt = positionToWidth.apply(i);
}
if (fillHeight.apply(i)) {
b = Math.max(b, top + boundedSize(n.minHeight(alt), areaHeight - minComplement - top - bottom,
n.maxHeight(alt)));
} else {
b = Math.max(b, top + boundedSize(n.minHeight(alt), n.prefHeight(alt),
Math.min(n.maxHeight(alt), areaHeight - minComplement - top - bottom)));
}
} else {
b = Math.max(b, top + bo);
}
}
return b;
}
static double getMinBaselineComplement(List<Node> children) {
return getBaselineComplement(children, true, false);
}
static double getPrefBaselineComplement(List<Node> children) {
return getBaselineComplement(children, false, false);
}
static double getMaxBaselineComplement(List<Node> children) {
return getBaselineComplement(children, false, true);
}
private static double getBaselineComplement(List<Node> children, boolean min, boolean max) {
double bc = 0;
for (Node n : children) {
final double bo = n.getBaselineOffset();
if (bo == BASELINE_OFFSET_SAME_AS_HEIGHT) {
continue;
}
if (n.isResizable()) {
bc = Math.max(bc, (min ? n.minHeight(-1) : max ? n.maxHeight(-1) : n.prefHeight(-1)) - bo);
} else {
bc = Math.max(bc, n.getLayoutBounds().getHeight() - bo);
}
}
return bc;
}
static double computeXOffset(double width, double contentWidth, HPos hpos) {
switch(hpos) {
case LEFT:
return 0;
case CENTER:
return (width - contentWidth) / 2;
case RIGHT:
return width - contentWidth;
default:
throw new AssertionError("Unhandled hPos");
}
}
static double computeYOffset(double height, double contentHeight, VPos vpos) {
switch(vpos) {
case BASELINE:
case TOP:
return 0;
case CENTER:
return (height - contentHeight) / 2;
case BOTTOM:
return height - contentHeight;
default:
throw new AssertionError("Unhandled vPos");
}
}
static double[] createDoubleArray(int length, double value) {
double[] array = new double[length];
for (int i = 0; i < length; i++) {
array[i] = value;
}
return array;
}
private InvalidationListener imageChangeListener = observable -> {
final ReadOnlyObjectPropertyBase imageProperty = (ReadOnlyObjectPropertyBase) observable;
final Image image = (Image) imageProperty.getBean();
final Toolkit.ImageAccessor acc = Toolkit.getImageAccessor();
if (image.getProgress() == 1 && !acc.isAnimation(image)) {
removeImageListener(image);
}
NodeHelper.markDirty(this, DirtyBits.NODE_CONTENTS);
};
{
RegionHelper.initHelper(this);
}
public Region() {
super();
setPickOnBounds(true);
}
private BooleanProperty snapToPixel;
private boolean _snapToPixel = true;
public final boolean isSnapToPixel() { return _snapToPixel; }
public final void setSnapToPixel(boolean value) {
if (snapToPixel == null) {
if (_snapToPixel != value) {
_snapToPixel = value;
updateSnappedInsets();
requestParentLayout();
}
} else {
snapToPixel.set(value);
}
}
public final BooleanProperty snapToPixelProperty() {
if (snapToPixel == null) {
snapToPixel = new StyleableBooleanProperty(_snapToPixel) {
@Override public Object getBean() { return Region.this; }
@Override public String getName() { return "snapToPixel"; }
@Override public CssMetaData<Region, Boolean> getCssMetaData() {
return StyleableProperties.SNAP_TO_PIXEL;
}
@Override public void invalidated() {
boolean value = get();
if (_snapToPixel != value) {
_snapToPixel = value;
updateSnappedInsets();
requestParentLayout();
}
}
};
}
return snapToPixel;
}
private ObjectProperty<Insets> padding = new StyleableObjectProperty<Insets>(Insets.EMPTY) {
private Insets lastValidValue = Insets.EMPTY;
@Override public Object getBean() { return Region.this; }
@Override public String getName() { return "padding"; }
@Override public CssMetaData<Region, Insets> getCssMetaData() {
return StyleableProperties.PADDING;
}
@Override public void invalidated() {
final Insets newValue = get();
if (newValue == null) {
if (isBound()) {
unbind();
}
set(lastValidValue);
throw new NullPointerException("cannot set padding to null");
} else if (!newValue.equals(lastValidValue)) {
lastValidValue = newValue;
insets.fireValueChanged();
}
}
};
public final void setPadding(Insets value) { padding.set(value); }
public final Insets getPadding() { return padding.get(); }
public final ObjectProperty<Insets> paddingProperty() { return padding; }
private final ObjectProperty<Background> background = new StyleableObjectProperty<Background>(null) {
private Background old = null;
@Override public Object getBean() { return Region.this; }
@Override public String getName() { return "background"; }
@Override public CssMetaData<Region, Background> getCssMetaData() {
return StyleableProperties.BACKGROUND;
}
@Override protected void invalidated() {
final Background b = get();
if(old != null ? !old.equals(b) : b != null) {
if (old == null || b == null || !old.getOutsets().equals(b.getOutsets())) {
NodeHelper.geomChanged(Region.this);
insets.fireValueChanged();
}
if (b != null) {
for (BackgroundImage i : b.getImages()) {
final Image image = i.image;
final Toolkit.ImageAccessor acc = Toolkit.getImageAccessor();
if (acc.isAnimation(image) || image.getProgress() < 1) {
addImageListener(image);
}
}
}
if (old != null) {
for (BackgroundImage i : old.getImages()) {
removeImageListener(i.image);
}
}
NodeHelper.markDirty(Region.this, DirtyBits.SHAPE_FILL);
cornersValid = false;
old = b;
}
}
};
public final void setBackground(Background value) { background.set(value); }
public final Background getBackground() { return background.get(); }
public final ObjectProperty<Background> backgroundProperty() { return background; }
private final ObjectProperty<Border> border = new StyleableObjectProperty<Border>(null) {
private Border old = null;
@Override public Object getBean() { return Region.this; }
@Override public String getName() { return "border"; }
@Override public CssMetaData<Region, Border> getCssMetaData() {
return StyleableProperties.BORDER;
}
@Override protected void invalidated() {
final Border b = get();
if(old != null ? !old.equals(b) : b != null) {
if (old == null || b == null || !old.getOutsets().equals(b.getOutsets())) {
NodeHelper.geomChanged(Region.this);
}
if (old == null || b == null || !old.getInsets().equals(b.getInsets())) {
insets.fireValueChanged();
}
if (b != null) {
for (BorderImage i : b.getImages()) {
final Image image = i.image;
final Toolkit.ImageAccessor acc = Toolkit.getImageAccessor();
if (acc.isAnimation(image) || image.getProgress() < 1) {
addImageListener(image);
}
}
}
if (old != null) {
for (BorderImage i : old.getImages()) {
removeImageListener(i.image);
}
}
NodeHelper.markDirty(Region.this, DirtyBits.SHAPE_STROKE);
cornersValid = false;
old = b;
}
}
};
public final void setBorder(Border value) { border.set(value); }
public final Border getBorder() { return border.get(); }
public final ObjectProperty<Border> borderProperty() { return border; }
void addImageListener(Image image) {
final Toolkit.ImageAccessor acc = Toolkit.getImageAccessor();
acc.getImageProperty(image).addListener(imageChangeListener);
}
void removeImageListener(Image image) {
final Toolkit.ImageAccessor acc = Toolkit.getImageAccessor();
acc.getImageProperty(image).removeListener(imageChangeListener);
}
public final ObjectProperty<Insets> opaqueInsetsProperty() {
if (opaqueInsets == null) {
opaqueInsets = new StyleableObjectProperty<Insets>() {
@Override public Object getBean() { return Region.this; }
@Override public String getName() { return "opaqueInsets"; }
@Override public CssMetaData<Region, Insets> getCssMetaData() {
return StyleableProperties.OPAQUE_INSETS;
}
@Override protected void invalidated() {
NodeHelper.markDirty(Region.this, DirtyBits.SHAPE_FILL);
}
};
}
return opaqueInsets;
}
private ObjectProperty<Insets> opaqueInsets;
public final void setOpaqueInsets(Insets value) { opaqueInsetsProperty().set(value); }
public final Insets getOpaqueInsets() { return opaqueInsets == null ? null : opaqueInsets.get(); }
private final InsetsProperty insets = new InsetsProperty();
public final Insets getInsets() { return insets.get(); }
public final ReadOnlyObjectProperty<Insets> insetsProperty() { return insets; }
private final class InsetsProperty extends ReadOnlyObjectProperty<Insets> {
private Insets cache = null;
private ExpressionHelper<Insets> helper = null;
@Override public Object getBean() { return Region.this; }
@Override public String getName() { return "insets"; }
@Override public void addListener(InvalidationListener listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override public void removeListener(InvalidationListener listener) {
helper = ExpressionHelper.removeListener(helper, listener);
}
@Override public void addListener(ChangeListener<? super Insets> listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override public void removeListener(ChangeListener<? super Insets> listener) {
helper = ExpressionHelper.removeListener(helper, listener);
}
void fireValueChanged() {
cache = null;
updateSnappedInsets();
requestLayout();
ExpressionHelper.fireValueChangedEvent(helper);
}
@Override public Insets get() {
if (_shape != null) return getPadding();
final Border b = getBorder();
if (b == null || Insets.EMPTY.equals(b.getInsets())) {
return getPadding();
}
if (cache == null) {
final Insets borderInsets = b.getInsets();
final Insets paddingInsets = getPadding();
cache = new Insets(
borderInsets.getTop() + paddingInsets.getTop(),
borderInsets.getRight() + paddingInsets.getRight(),
borderInsets.getBottom() + paddingInsets.getBottom(),
borderInsets.getLeft() + paddingInsets.getLeft()
);
}
return cache;
}
};
private double snappedTopInset = 0;
private double snappedRightInset = 0;
private double snappedBottomInset = 0;
private double snappedLeftInset = 0;
private double lastUsedSnapScaleY = 0;
private double lastUsedSnapScaleX = 0;
private void updateSnappedInsets() {
lastUsedSnapScaleX = getSnapScaleX();
lastUsedSnapScaleY = getSnapScaleY();
final Insets insets = getInsets();
final boolean snap = isSnapToPixel();
snappedTopInset = snapSpaceY(insets.getTop(), snap);
snappedRightInset = snapSpaceX(insets.getRight(), snap);
snappedBottomInset = snapSpaceY(insets.getBottom(), snap);
snappedLeftInset = snapSpaceX(insets.getLeft(), snap);
}
private ReadOnlyDoubleWrapper width;
private double _width;
protected void setWidth(double value) {
if(width == null) {
widthChanged(value);
} else {
width.set(value);
}
}
private void widthChanged(double value) {
if (value != _width) {
_width = value;
cornersValid = false;
boundingBox = null;
NodeHelper.layoutBoundsChanged(this);
NodeHelper.geomChanged(this);
NodeHelper.markDirty(this, DirtyBits.NODE_GEOMETRY);
setNeedsLayout(true);
requestParentLayout();
}
}
public final double getWidth() { return width == null ? _width : width.get(); }
public final ReadOnlyDoubleProperty widthProperty() {
if (width == null) {
width = new ReadOnlyDoubleWrapper(_width) {
@Override protected void invalidated() { widthChanged(get()); }
@Override public Object getBean() { return Region.this; }
@Override public String getName() { return "width"; }
};
}
return width.getReadOnlyProperty();
}
private ReadOnlyDoubleWrapper height;
private double _height;
protected void setHeight(double value) {
if (height == null) {
heightChanged(value);
} else {
height.set(value);
}
}
private void heightChanged(double value) {
if (_height != value) {
_height = value;
cornersValid = false;
boundingBox = null;
NodeHelper.geomChanged(this);
NodeHelper.layoutBoundsChanged(this);
NodeHelper.markDirty(this, DirtyBits.NODE_GEOMETRY);
setNeedsLayout(true);
requestParentLayout();
}
}
public final double getHeight() { return height == null ? _height : height.get(); }
public final ReadOnlyDoubleProperty heightProperty() {
if (height == null) {
height = new ReadOnlyDoubleWrapper(_height) {
@Override protected void invalidated() { heightChanged(get()); }
@Override public Object getBean() { return Region.this; }
@Override public String getName() { return "height"; }
};
}
return height.getReadOnlyProperty();
}
private final class MinPrefMaxProperty extends StyleableDoubleProperty {
private final String name;
private final CssMetaData<? extends Styleable, Number> cssMetaData;
MinPrefMaxProperty(String name, double initialValue, CssMetaData<? extends Styleable, Number> cssMetaData) {
super(initialValue);
this.name = name;
this.cssMetaData = cssMetaData;
}
@Override public void invalidated() { requestParentLayout(); }
@Override public Object getBean() { return Region.this; }
@Override public String getName() { return name; }
@Override
public CssMetaData<? extends Styleable, Number> getCssMetaData() {
return cssMetaData;
}
}
private DoubleProperty minWidth;
private double _minWidth = USE_COMPUTED_SIZE;
public final void setMinWidth(double value) {
if (minWidth == null) {
_minWidth = value;
requestParentLayout();
} else {
minWidth.set(value);
}
}
public final double getMinWidth() { return minWidth == null ? _minWidth : minWidth.get(); }
public final DoubleProperty minWidthProperty() {
if (minWidth == null) minWidth = new MinPrefMaxProperty("minWidth", _minWidth, StyleableProperties.MIN_WIDTH);
return minWidth;
}
private DoubleProperty minHeight;
private double _minHeight = USE_COMPUTED_SIZE;
public final void setMinHeight(double value) {
if (minHeight == null) {
_minHeight = value;
requestParentLayout();
} else {
minHeight.set(value);
}
}
public final double getMinHeight() { return minHeight == null ? _minHeight : minHeight.get(); }
public final DoubleProperty minHeightProperty() {
if (minHeight == null) minHeight = new MinPrefMaxProperty("minHeight", _minHeight, StyleableProperties.MIN_HEIGHT);
return minHeight;
}
public void setMinSize(double minWidth, double minHeight) {
setMinWidth(minWidth);
setMinHeight(minHeight);
}
private DoubleProperty prefWidth;
private double _prefWidth = USE_COMPUTED_SIZE;
public final void setPrefWidth(double value) {
if (prefWidth == null) {
_prefWidth = value;
requestParentLayout();
} else {
prefWidth.set(value);
}
}
public final double getPrefWidth() { return prefWidth == null ? _prefWidth : prefWidth.get(); }
public final DoubleProperty prefWidthProperty() {
if (prefWidth == null) prefWidth = new MinPrefMaxProperty("prefWidth", _prefWidth, StyleableProperties.PREF_WIDTH);
return prefWidth;
}
private DoubleProperty prefHeight;
private double _prefHeight = USE_COMPUTED_SIZE;
public final void setPrefHeight(double value) {
if (prefHeight == null) {
_prefHeight = value;
requestParentLayout();
} else {
prefHeight.set(value);
}
}
public final double getPrefHeight() { return prefHeight == null ? _prefHeight : prefHeight.get(); }
public final DoubleProperty prefHeightProperty() {
if (prefHeight == null) prefHeight = new MinPrefMaxProperty("prefHeight", _prefHeight, StyleableProperties.PREF_HEIGHT);
return prefHeight;
}
public void setPrefSize(double prefWidth, double prefHeight) {
setPrefWidth(prefWidth);
setPrefHeight(prefHeight);
}
private DoubleProperty maxWidth;
private double _maxWidth = USE_COMPUTED_SIZE;
public final void setMaxWidth(double value) {
if (maxWidth == null) {
_maxWidth = value;
requestParentLayout();
} else {
maxWidth.set(value);
}
}
public final double getMaxWidth() { return maxWidth == null ? _maxWidth : maxWidth.get(); }
public final DoubleProperty maxWidthProperty() {
if (maxWidth == null) maxWidth = new MinPrefMaxProperty("maxWidth", _maxWidth, StyleableProperties.MAX_WIDTH);
return maxWidth;
}
private DoubleProperty maxHeight;
private double _maxHeight = USE_COMPUTED_SIZE;
public final void setMaxHeight(double value) {
if (maxHeight == null) {
_maxHeight = value;
requestParentLayout();
} else {
maxHeight.set(value);
}
}
public final double getMaxHeight() { return maxHeight == null ? _maxHeight : maxHeight.get(); }
public final DoubleProperty maxHeightProperty() {
if (maxHeight == null) maxHeight = new MinPrefMaxProperty("maxHeight", _maxHeight, StyleableProperties.MAX_HEIGHT);
return maxHeight;
}
public void setMaxSize(double maxWidth, double maxHeight) {
setMaxWidth(maxWidth);
setMaxHeight(maxHeight);
}
private ObjectProperty<Shape> shape = null;
private Shape _shape;
public final Shape getShape() { return shape == null ? _shape : shape.get(); }
public final void setShape(Shape value) { shapeProperty().set(value); }
public final ObjectProperty<Shape> shapeProperty() {
if (shape == null) {
shape = new ShapeProperty();
}
return shape;
}
private final class ShapeProperty extends StyleableObjectProperty<Shape> implements Runnable {
@Override public Object getBean() { return Region.this; }
@Override public String getName() { return "shape"; }
@Override public CssMetaData<Region, Shape> getCssMetaData() {
return StyleableProperties.SHAPE;
}
@Override protected void invalidated() {
final Shape value = get();
if (_shape != value) {
if (_shape != null) ShapeHelper.setShapeChangeListener(_shape, null);
if (value != null) ShapeHelper.setShapeChangeListener(value, this);
run();
if (_shape == null || value == null) {
insets.fireValueChanged();
}
_shape = value;
}
}
@Override public void run() {
NodeHelper.geomChanged(Region.this);
NodeHelper.markDirty(Region.this, DirtyBits.REGION_SHAPE);
}
};
private BooleanProperty scaleShape = null;
public final void setScaleShape(boolean value) { scaleShapeProperty().set(value); }
public final boolean isScaleShape() { return scaleShape == null ? true : scaleShape.get(); }
public final BooleanProperty scaleShapeProperty() {
if (scaleShape == null) {
scaleShape = new StyleableBooleanProperty(true) {
@Override public Object getBean() { return Region.this; }
@Override public String getName() { return "scaleShape"; }
@Override public CssMetaData<Region, Boolean> getCssMetaData() {
return StyleableProperties.SCALE_SHAPE;
}
@Override public void invalidated() {
NodeHelper.geomChanged(Region.this);
NodeHelper.markDirty(Region.this, DirtyBits.REGION_SHAPE);
}
};
}
return scaleShape;
}
private BooleanProperty centerShape = null;
public final void setCenterShape(boolean value) { centerShapeProperty().set(value); }
public final boolean isCenterShape() { return centerShape == null ? true : centerShape.get(); }
public final BooleanProperty centerShapeProperty() {
if (centerShape == null) {
centerShape = new StyleableBooleanProperty(true) {
@Override public Object getBean() { return Region.this; }
@Override public String getName() { return "centerShape"; }
@Override public CssMetaData<Region, Boolean> getCssMetaData() {
return StyleableProperties.POSITION_SHAPE;
}
@Override public void invalidated() {
NodeHelper.geomChanged(Region.this);
NodeHelper.markDirty(Region.this, DirtyBits.REGION_SHAPE);
}
};
}
return centerShape;
}
private BooleanProperty cacheShape = null;
public final void setCacheShape(boolean value) { cacheShapeProperty().set(value); }
public final boolean isCacheShape() { return cacheShape == null ? true : cacheShape.get(); }
public final BooleanProperty cacheShapeProperty() {
if (cacheShape == null) {
cacheShape = new StyleableBooleanProperty(true) {
@Override public Object getBean() { return Region.this; }
@Override public String getName() { return "cacheShape"; }
@Override public CssMetaData<Region, Boolean> getCssMetaData() {
return StyleableProperties.CACHE_SHAPE;
}
};
}
return cacheShape;
}
@Override public boolean isResizable() {
return true;
}
@Override public void resize(double width, double height) {
setWidth(width);
setHeight(height);
PlatformLogger logger = Logging.getLayoutLogger();
if (logger.isLoggable(Level.FINER)) {
logger.finer(this.toString() + " resized to " + width + " x " + height);
}
}
@Override public final double minWidth(double height) {
final double override = getMinWidth();
if (override == USE_COMPUTED_SIZE) {
return super.minWidth(height);
} else if (override == USE_PREF_SIZE) {
return prefWidth(height);
}
return Double.isNaN(override) || override < 0 ? 0 : override;
}
@Override public final double minHeight(double width) {
final double override = getMinHeight();
if (override == USE_COMPUTED_SIZE) {
return super.minHeight(width);
} else if (override == USE_PREF_SIZE) {
return prefHeight(width);
}
return Double.isNaN(override) || override < 0 ? 0 : override;
}
@Override public final double prefWidth(double height) {
final double override = getPrefWidth();
if (override == USE_COMPUTED_SIZE) {
return super.prefWidth(height);
}
return Double.isNaN(override) || override < 0 ? 0 : override;
}
@Override public final double prefHeight(double width) {
final double override = getPrefHeight();
if (override == USE_COMPUTED_SIZE) {
return super.prefHeight(width);
}
return Double.isNaN(override) || override < 0 ? 0 : override;
}
@Override public final double maxWidth(double height) {
final double override = getMaxWidth();
if (override == USE_COMPUTED_SIZE) {
return computeMaxWidth(height);
} else if (override == USE_PREF_SIZE) {
return prefWidth(height);
}
return Double.isNaN(override) || override < 0 ? 0 : override;
}
@Override public final double maxHeight(double width) {
final double override = getMaxHeight();
if (override == USE_COMPUTED_SIZE) {
return computeMaxHeight(width);
} else if (override == USE_PREF_SIZE) {
return prefHeight(width);
}
return Double.isNaN(override) || override < 0 ? 0 : override;
}
@Override protected double computeMinWidth(double height) {
return getInsets().getLeft() + getInsets().getRight();
}
@Override protected double computeMinHeight(double width) {
return getInsets().getTop() + getInsets().getBottom();
}
@Override protected double computePrefWidth(double height) {
final double w = super.computePrefWidth(height);
return getInsets().getLeft() + w + getInsets().getRight();
}
@Override protected double computePrefHeight(double width) {
final double h = super.computePrefHeight(width);
return getInsets().getTop() + h + getInsets().getBottom();
}
protected double computeMaxWidth(double height) {
return Double.MAX_VALUE;
}
protected double computeMaxHeight(double width) {
return Double.MAX_VALUE;
}
@Deprecated(since="9")
protected double snapSpace(double value) {
return snapSpaceX(value, isSnapToPixel());
}
public double snapSpaceX(double value) {
return snapSpaceX(value, isSnapToPixel());
}
public double snapSpaceY(double value) {
return snapSpaceY(value, isSnapToPixel());
}
@Deprecated(since="9")
protected double snapSize(double value) {
return snapSizeX(value, isSnapToPixel());
}
public double snapSizeX(double value) {
return snapSizeX(value, isSnapToPixel());
}
public double snapSizeY(double value) {
return snapSizeY(value, isSnapToPixel());
}
@Deprecated(since="9")
protected double snapPosition(double value) {
return snapPositionX(value, isSnapToPixel());
}
public double snapPositionX(double value) {
return snapPositionX(value, isSnapToPixel());
}
public double snapPositionY(double value) {
return snapPositionY(value, isSnapToPixel());
}
double snapPortionX(double value) {
return snapPortionX(value, isSnapToPixel());
}
double snapPortionY(double value) {
return snapPortionY(value, isSnapToPixel());
}
public final double snappedTopInset() {
if (lastUsedSnapScaleY != getSnapScaleY()) {
updateSnappedInsets();
}
return snappedTopInset;
}
public final double snappedBottomInset() {
if (lastUsedSnapScaleY != getSnapScaleY()) {
updateSnappedInsets();
}
return snappedBottomInset;
}
public final double snappedLeftInset() {
if (lastUsedSnapScaleX != getSnapScaleX()) {
updateSnappedInsets();
}
return snappedLeftInset;
}
public final double snappedRightInset() {
if (lastUsedSnapScaleX != getSnapScaleX()) {
updateSnappedInsets();
}
return snappedRightInset;
}
double computeChildMinAreaWidth(Node child, Insets margin) {
return computeChildMinAreaWidth(child, -1, margin, -1, false);
}
double computeChildMinAreaWidth(Node child, double baselineComplement, Insets margin, double height, boolean fillHeight) {
final boolean snap = isSnapToPixel();
double left = margin != null? snapSpaceX(margin.getLeft(), snap) : 0;
double right = margin != null? snapSpaceX(margin.getRight(), snap) : 0;
double alt = -1;
if (height != -1 && child.isResizable() && child.getContentBias() == Orientation.VERTICAL) {
double top = margin != null? snapSpaceY(margin.getTop(), snap) : 0;
double bottom = (margin != null? snapSpaceY(margin.getBottom(), snap) : 0);
double bo = child.getBaselineOffset();
final double contentHeight = bo == BASELINE_OFFSET_SAME_AS_HEIGHT && baselineComplement != -1 ?
height - top - bottom - baselineComplement :
height - top - bottom;
if (fillHeight) {
alt = snapSizeY(boundedSize(
child.minHeight(-1), contentHeight,
child.maxHeight(-1)));
} else {
alt = snapSizeY(boundedSize(
child.minHeight(-1),
child.prefHeight(-1),
Math.min(child.maxHeight(-1), contentHeight)));
}
}
return left + snapSizeX(child.minWidth(alt)) + right;
}
double computeChildMinAreaHeight(Node child, Insets margin) {
return computeChildMinAreaHeight(child, -1, margin, -1);
}
double computeChildMinAreaHeight(Node child, double minBaselineComplement, Insets margin, double width) {
final boolean snap = isSnapToPixel();
double top =margin != null? snapSpaceY(margin.getTop(), snap) : 0;
double bottom = margin != null? snapSpaceY(margin.getBottom(), snap) : 0;
double alt = -1;
if (child.isResizable() && child.getContentBias() == Orientation.HORIZONTAL) {
double left = margin != null? snapSpaceX(margin.getLeft(), snap) : 0;
double right = margin != null? snapSpaceX(margin.getRight(), snap) : 0;
alt = snapSizeX(width != -1? boundedSize(child.minWidth(-1), width - left - right, child.maxWidth(-1)) :
child.maxWidth(-1));
}
if (minBaselineComplement != -1) {
double baseline = child.getBaselineOffset();
if (child.isResizable() && baseline == BASELINE_OFFSET_SAME_AS_HEIGHT) {
return top + snapSizeY(child.minHeight(alt)) + bottom
+ minBaselineComplement;
} else {
return baseline + minBaselineComplement;
}
} else {
return top + snapSizeY(child.minHeight(alt)) + bottom;
}
}
double computeChildPrefAreaWidth(Node child, Insets margin) {
return computeChildPrefAreaWidth(child, -1, margin, -1, false);
}
double computeChildPrefAreaWidth(Node child, double baselineComplement, Insets margin, double height, boolean fillHeight) {
final boolean snap = isSnapToPixel();
double left = margin != null? snapSpaceX(margin.getLeft(), snap) : 0;
double right = margin != null? snapSpaceX(margin.getRight(), snap) : 0;
double alt = -1;
if (height != -1 && child.isResizable() && child.getContentBias() == Orientation.VERTICAL) {
double top = margin != null? snapSpaceY(margin.getTop(), snap) : 0;
double bottom = margin != null? snapSpaceY(margin.getBottom(), snap) : 0;
double bo = child.getBaselineOffset();
final double contentHeight = bo == BASELINE_OFFSET_SAME_AS_HEIGHT && baselineComplement != -1 ?
height - top - bottom - baselineComplement :
height - top - bottom;
if (fillHeight) {
alt = snapSizeY(boundedSize(
child.minHeight(-1), contentHeight,
child.maxHeight(-1)));
} else {
alt = snapSizeY(boundedSize(
child.minHeight(-1),
child.prefHeight(-1),
Math.min(child.maxHeight(-1), contentHeight)));
}
}
return left + snapSizeX(boundedSize(child.minWidth(alt), child.prefWidth(alt), child.maxWidth(alt))) + right;
}
double computeChildPrefAreaHeight(Node child, Insets margin) {
return computeChildPrefAreaHeight(child, -1, margin, -1);
}
double computeChildPrefAreaHeight(Node child, double prefBaselineComplement, Insets margin, double width) {
final boolean snap = isSnapToPixel();
double top = margin != null? snapSpaceY(margin.getTop(), snap) : 0;
double bottom = margin != null? snapSpaceY(margin.getBottom(), snap) : 0;
double alt = -1;
if (child.isResizable() && child.getContentBias() == Orientation.HORIZONTAL) {
double left = margin != null ? snapSpaceX(margin.getLeft(), snap) : 0;
double right = margin != null ? snapSpaceX(margin.getRight(), snap) : 0;
alt = snapSizeX(boundedSize(
child.minWidth(-1), width != -1 ? width - left - right
: child.prefWidth(-1), child.maxWidth(-1)));
}
if (prefBaselineComplement != -1) {
double baseline = child.getBaselineOffset();
if (child.isResizable() && baseline == BASELINE_OFFSET_SAME_AS_HEIGHT) {
return top + snapSizeY(boundedSize(child.minHeight(alt), child.prefHeight(alt), child.maxHeight(alt))) + bottom
+ prefBaselineComplement;
} else {
return top + baseline + prefBaselineComplement + bottom;
}
} else {
return top + snapSizeY(boundedSize(child.minHeight(alt), child.prefHeight(alt), child.maxHeight(alt))) + bottom;
}
}
double computeChildMaxAreaWidth(Node child, double baselineComplement, Insets margin, double height, boolean fillHeight) {
double max = child.maxWidth(-1);
if (max == Double.MAX_VALUE) {
return max;
}
final boolean snap = isSnapToPixel();
double left = margin != null? snapSpaceX(margin.getLeft(), snap) : 0;
double right = margin != null? snapSpaceX(margin.getRight(), snap) : 0;
double alt = -1;
if (height != -1 && child.isResizable() && child.getContentBias() == Orientation.VERTICAL) {
double top = margin != null? snapSpaceY(margin.getTop(), snap) : 0;
double bottom = (margin != null? snapSpaceY(margin.getBottom(), snap) : 0);
double bo = child.getBaselineOffset();
final double contentHeight = bo == BASELINE_OFFSET_SAME_AS_HEIGHT && baselineComplement != -1 ?
height - top - bottom - baselineComplement :
height - top - bottom;
if (fillHeight) {
alt = snapSizeY(boundedSize(
child.minHeight(-1), contentHeight,
child.maxHeight(-1)));
} else {
alt = snapSizeY(boundedSize(
child.minHeight(-1),
child.prefHeight(-1),
Math.min(child.maxHeight(-1), contentHeight)));
}
max = child.maxWidth(alt);
}
return left + snapSizeX(boundedSize(child.minWidth(alt), max, Double.MAX_VALUE)) + right;
}
double computeChildMaxAreaHeight(Node child, double maxBaselineComplement, Insets margin, double width) {
double max = child.maxHeight(-1);
if (max == Double.MAX_VALUE) {
return max;
}
final boolean snap = isSnapToPixel();
double top = margin != null? snapSpaceY(margin.getTop(), snap) : 0;
double bottom = margin != null? snapSpaceY(margin.getBottom(), snap) : 0;
double alt = -1;
if (child.isResizable() && child.getContentBias() == Orientation.HORIZONTAL) {
double left = margin != null? snapSpaceX(margin.getLeft(), snap) : 0;
double right = margin != null? snapSpaceX(margin.getRight(), snap) : 0;
alt = snapSizeX(width != -1? boundedSize(child.minWidth(-1), width - left - right, child.maxWidth(-1)) :
child.minWidth(-1));
max = child.maxHeight(alt);
}
if (maxBaselineComplement != -1) {
double baseline = child.getBaselineOffset();
if (child.isResizable() && baseline == BASELINE_OFFSET_SAME_AS_HEIGHT) {
return top + snapSizeY(boundedSize(child.minHeight(alt), child.maxHeight(alt), Double.MAX_VALUE)) + bottom
+ maxBaselineComplement;
} else {
return top + baseline + maxBaselineComplement + bottom;
}
} else {
return top + snapSizeY(boundedSize(child.minHeight(alt), max, Double.MAX_VALUE)) + bottom;
}
}
double computeMaxMinAreaWidth(List<Node> children, Callback<Node, Insets> margins) {
return getMaxAreaWidth(children, margins, new double[] { -1 }, false, true);
}
double computeMaxMinAreaWidth(List<Node> children, Callback<Node, Insets> margins, double height, boolean fillHeight) {
return getMaxAreaWidth(children, margins, new double[] { height }, fillHeight, true);
}
double computeMaxMinAreaWidth(List<Node> children, Callback<Node, Insets> childMargins, double childHeights[], boolean fillHeight) {
return getMaxAreaWidth(children, childMargins, childHeights, fillHeight, true);
}
double computeMaxMinAreaHeight(List<Node>children, Callback<Node, Insets> margins, VPos valignment) {
return getMaxAreaHeight(children, margins, null, valignment, true);
}
double computeMaxMinAreaHeight(List<Node>children, Callback<Node, Insets> margins, VPos valignment, double width) {
return getMaxAreaHeight(children, margins, new double[] { width }, valignment, true);
}
double computeMaxMinAreaHeight(List<Node>children, Callback<Node, Insets> childMargins, double childWidths[], VPos valignment) {
return getMaxAreaHeight(children, childMargins, childWidths, valignment, true);
}
double computeMaxPrefAreaWidth(List<Node>children, Callback<Node, Insets> margins) {
return getMaxAreaWidth(children, margins, new double[] { -1 }, false, false);
}
double computeMaxPrefAreaWidth(List<Node>children, Callback<Node, Insets> margins, double height,
boolean fillHeight) {
return getMaxAreaWidth(children, margins, new double[] { height }, fillHeight, false);
}
double computeMaxPrefAreaWidth(List<Node>children, Callback<Node, Insets> childMargins,
double childHeights[], boolean fillHeight) {
return getMaxAreaWidth(children, childMargins, childHeights, fillHeight, false);
}
double computeMaxPrefAreaHeight(List<Node>children, Callback<Node, Insets> margins, VPos valignment) {
return getMaxAreaHeight(children, margins, null, valignment, false);
}
double computeMaxPrefAreaHeight(List<Node>children, Callback<Node, Insets> margins, double width, VPos valignment) {
return getMaxAreaHeight(children, margins, new double[] { width }, valignment, false);
}
double computeMaxPrefAreaHeight(List<Node>children, Callback<Node, Insets> childMargins, double childWidths[], VPos valignment) {
return getMaxAreaHeight(children, childMargins, childWidths, valignment, false);
}
static Vec2d boundedNodeSizeWithBias(Node node, double areaWidth, double areaHeight,
boolean fillWidth, boolean fillHeight, Vec2d result) {
if (result == null) {
result = new Vec2d();
}
Orientation bias = node.getContentBias();
double childWidth = 0;
double childHeight = 0;
if (bias == null) {
childWidth = boundedSize(
node.minWidth(-1), fillWidth ? areaWidth
: Math.min(areaWidth, node.prefWidth(-1)),
node.maxWidth(-1));
childHeight = boundedSize(
node.minHeight(-1), fillHeight ? areaHeight
: Math.min(areaHeight, node.prefHeight(-1)),
node.maxHeight(-1));
} else if (bias == Orientation.HORIZONTAL) {
childWidth = boundedSize(
node.minWidth(-1), fillWidth ? areaWidth
: Math.min(areaWidth, node.prefWidth(-1)),
node.maxWidth(-1));
childHeight = boundedSize(
node.minHeight(childWidth), fillHeight ? areaHeight
: Math.min(areaHeight, node.prefHeight(childWidth)),
node.maxHeight(childWidth));
} else {
childHeight = boundedSize(
node.minHeight(-1), fillHeight ? areaHeight
: Math.min(areaHeight, node.prefHeight(-1)),
node.maxHeight(-1));
childWidth = boundedSize(
node.minWidth(childHeight), fillWidth ? areaWidth
: Math.min(areaWidth, node.prefWidth(childHeight)),
node.maxWidth(childHeight));
}
result.set(childWidth, childHeight);
return result;
}
private double getMaxAreaHeight(List<Node> children, Callback<Node,Insets> childMargins, double childWidths[], VPos valignment, boolean minimum) {
final double singleChildWidth = childWidths == null ? -1 : childWidths.length == 1 ? childWidths[0] : Double.NaN;
if (valignment == VPos.BASELINE) {
double maxAbove = 0;
double maxBelow = 0;
for (int i = 0, maxPos = children.size(); i < maxPos; i++) {
final Node child = children.get(i);
final double childWidth = Double.isNaN(singleChildWidth) ? childWidths[i] : singleChildWidth;
Insets margin = childMargins.call(child);
final double top = margin != null? snapSpaceY(margin.getTop()) : 0;
final double bottom = margin != null? snapSpaceY(margin.getBottom()) : 0;
final double baseline = child.getBaselineOffset();
final double childHeight = minimum? snapSizeY(child.minHeight(childWidth)) : snapSizeY(child.prefHeight(childWidth));
if (baseline == BASELINE_OFFSET_SAME_AS_HEIGHT) {
maxAbove = Math.max(maxAbove, childHeight + top);
} else {
maxAbove = Math.max(maxAbove, baseline + top);
maxBelow = Math.max(maxBelow,
snapSpaceY(minimum?snapSizeY(child.minHeight(childWidth)) : snapSizeY(child.prefHeight(childWidth))) -
baseline + bottom);
}
}
return maxAbove + maxBelow;
} else {
double max = 0;
for (int i = 0, maxPos = children.size(); i < maxPos; i++) {
final Node child = children.get(i);
Insets margin = childMargins.call(child);
final double childWidth = Double.isNaN(singleChildWidth) ? childWidths[i] : singleChildWidth;
max = Math.max(max, minimum?
computeChildMinAreaHeight(child, -1, margin, childWidth) :
computeChildPrefAreaHeight(child, -1, margin, childWidth));
}
return max;
}
}
private double getMaxAreaWidth(List<javafx.scene.Node> children,
Callback<Node, Insets> childMargins, double childHeights[], boolean fillHeight, boolean minimum) {
final double singleChildHeight = childHeights == null ? -1 : childHeights.length == 1 ? childHeights[0] : Double.NaN;
double max = 0;
for (int i = 0, maxPos = children.size(); i < maxPos; i++) {
final Node child = children.get(i);
final Insets margin = childMargins.call(child);
final double childHeight = Double.isNaN(singleChildHeight) ? childHeights[i] : singleChildHeight;
max = Math.max(max, minimum?
computeChildMinAreaWidth(children.get(i), -1, margin, childHeight, fillHeight) :
computeChildPrefAreaWidth(child, -1, margin, childHeight, fillHeight));
}
return max;
}
protected void positionInArea(Node child, double areaX, double areaY, double areaWidth, double areaHeight,
double areaBaselineOffset, HPos halignment, VPos valignment) {
positionInArea(child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset,
Insets.EMPTY, halignment, valignment, isSnapToPixel());
}
public static void positionInArea(Node child, double areaX, double areaY, double areaWidth, double areaHeight,
double areaBaselineOffset, Insets margin, HPos halignment, VPos valignment, boolean isSnapToPixel) {
Insets childMargin = margin != null? margin : Insets.EMPTY;
double snapScaleX = isSnapToPixel ? getSnapScaleX(child) : 1.0;
double snapScaleY = isSnapToPixel ? getSnapScaleY(child) : 1.0;
position(child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset,
snapSpace(childMargin.getTop(), isSnapToPixel, snapScaleY),
snapSpace(childMargin.getRight(), isSnapToPixel, snapScaleX),
snapSpace(childMargin.getBottom(), isSnapToPixel, snapScaleY),
snapSpace(childMargin.getLeft(), isSnapToPixel, snapScaleX),
halignment, valignment, isSnapToPixel);
}
protected void layoutInArea(Node child, double areaX, double areaY,
double areaWidth, double areaHeight,
double areaBaselineOffset,
HPos halignment, VPos valignment) {
layoutInArea(child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset,
Insets.EMPTY, halignment, valignment);
}
protected void layoutInArea(Node child, double areaX, double areaY,
double areaWidth, double areaHeight,
double areaBaselineOffset,
Insets margin,
HPos halignment, VPos valignment) {
layoutInArea(child, areaX, areaY, areaWidth, areaHeight,
areaBaselineOffset, margin, true, true, halignment, valignment);
}
protected void layoutInArea(Node child, double areaX, double areaY,
double areaWidth, double areaHeight,
double areaBaselineOffset,
Insets margin, boolean fillWidth, boolean fillHeight,
HPos halignment, VPos valignment) {
layoutInArea(child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset, margin, fillWidth, fillHeight, halignment, valignment, isSnapToPixel());
}
public static void layoutInArea(Node child, double areaX, double areaY,
double areaWidth, double areaHeight,
double areaBaselineOffset,
Insets margin, boolean fillWidth, boolean fillHeight,
HPos halignment, VPos valignment, boolean isSnapToPixel) {
Insets childMargin = margin != null ? margin : Insets.EMPTY;
double snapScaleX = isSnapToPixel ? getSnapScaleX(child) : 1.0;
double snapScaleY = isSnapToPixel ? getSnapScaleY(child) : 1.0;
double top = snapSpace(childMargin.getTop(), isSnapToPixel, snapScaleY);
double bottom = snapSpace(childMargin.getBottom(), isSnapToPixel, snapScaleY);
double left = snapSpace(childMargin.getLeft(), isSnapToPixel, snapScaleX);
double right = snapSpace(childMargin.getRight(), isSnapToPixel, snapScaleX);
if (valignment == VPos.BASELINE) {
double bo = child.getBaselineOffset();
if (bo == BASELINE_OFFSET_SAME_AS_HEIGHT) {
if (child.isResizable()) {
bottom += snapSpace(areaHeight - areaBaselineOffset, isSnapToPixel, snapScaleY);
} else {
top = snapSpace(areaBaselineOffset - child.getLayoutBounds().getHeight(), isSnapToPixel, snapScaleY);
}
} else {
top = snapSpace(areaBaselineOffset - bo, isSnapToPixel, snapScaleY);
}
}
if (child.isResizable()) {
Vec2d size = boundedNodeSizeWithBias(child, areaWidth - left - right, areaHeight - top - bottom,
fillWidth, fillHeight, TEMP_VEC2D);
child.resize(snapSize(size.x, isSnapToPixel, snapScaleX),
snapSize(size.y, isSnapToPixel, snapScaleX));
}
position(child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset,
top, right, bottom, left, halignment, valignment, isSnapToPixel);
}
private static void position(Node child, double areaX, double areaY, double areaWidth, double areaHeight,
double areaBaselineOffset,
double topMargin, double rightMargin, double bottomMargin, double leftMargin,
HPos hpos, VPos vpos, boolean isSnapToPixel) {
final double xoffset = leftMargin + computeXOffset(areaWidth - leftMargin - rightMargin,
child.getLayoutBounds().getWidth(), hpos);
final double yoffset;
if (vpos == VPos.BASELINE) {
double bo = child.getBaselineOffset();
if (bo == BASELINE_OFFSET_SAME_AS_HEIGHT) {
yoffset = areaBaselineOffset - child.getLayoutBounds().getHeight();
} else {
yoffset = areaBaselineOffset - bo;
}
} else {
yoffset = topMargin + computeYOffset(areaHeight - topMargin - bottomMargin,
child.getLayoutBounds().getHeight(), vpos);
}
double x = areaX + xoffset;
double y = areaY + yoffset;
if (isSnapToPixel) {
x = snapPosition(x, true, getSnapScaleX(child));
y = snapPosition(y, true, getSnapScaleY(child));
}
child.relocate(x,y);
}
private void doUpdatePeer() {
if (_shape != null) NodeHelper.syncPeer(_shape);
NGRegion pg = NodeHelper.getPeer(this);
if (!cornersValid) {
validateCorners();
}
final boolean sizeChanged = NodeHelper.isDirty(this, DirtyBits.NODE_GEOMETRY);
if (sizeChanged) {
pg.setSize((float)getWidth(), (float)getHeight());
}
final boolean shapeChanged = NodeHelper.isDirty(this, DirtyBits.REGION_SHAPE);
if (shapeChanged) {
pg.updateShape(_shape, isScaleShape(), isCenterShape(), isCacheShape());
}
pg.updateFillCorners(normalizedFillCorners);
final boolean backgroundChanged = NodeHelper.isDirty(this, DirtyBits.SHAPE_FILL);
final Background bg = getBackground();
if (backgroundChanged) {
pg.updateBackground(bg);
}
if (NodeHelper.isDirty(this, DirtyBits.NODE_CONTENTS)) {
pg.imagesUpdated();
}
pg.updateStrokeCorners(normalizedStrokeCorners);
if (NodeHelper.isDirty(this, DirtyBits.SHAPE_STROKE)) {
pg.updateBorder(getBorder());
}
if (sizeChanged || backgroundChanged || shapeChanged) {
final Insets i = getOpaqueInsets();
if (_shape != null) {
if (i != null) {
pg.setOpaqueInsets((float) i.getTop(), (float) i.getRight(),
(float) i.getBottom(), (float) i.getLeft());
} else {
pg.setOpaqueInsets(Float.NaN, Float.NaN, Float.NaN, Float.NaN);
}
} else {
if (bg == null || bg.isEmpty()) {
pg.setOpaqueInsets(Float.NaN, Float.NaN, Float.NaN, Float.NaN);
} else {
final double[] trbl = new double[4];
bg.computeOpaqueInsets(getWidth(), getHeight(), trbl);
if (i != null) {
trbl[0] = Double.isNaN(trbl[0]) ? i.getTop() : Double.isNaN(i.getTop()) ? trbl[0] : Math.min(trbl[0], i.getTop());
trbl[1] = Double.isNaN(trbl[1]) ? i.getRight() : Double.isNaN(i.getRight()) ? trbl[1] : Math.min(trbl[1], i.getRight());
trbl[2] = Double.isNaN(trbl[2]) ? i.getBottom() : Double.isNaN(i.getBottom()) ? trbl[2] : Math.min(trbl[2], i.getBottom());
trbl[3] = Double.isNaN(trbl[3]) ? i.getLeft() : Double.isNaN(i.getLeft()) ? trbl[3] : Math.min(trbl[3], i.getLeft());
}
pg.setOpaqueInsets((float) trbl[0], (float) trbl[1], (float) trbl[2], (float) trbl[3]);
}
}
}
}
private NGNode doCreatePeer() {
return new NGRegion();
}
private boolean shapeContains(com.sun.javafx.geom.Shape shape,
final double x, final double y,
double topOffset, double rightOffset, double bottomOffset, double leftOffset) {
double resX = x;
double resY = y;
final RectBounds bounds = shape.getBounds();
if (isScaleShape()) {
resX -= leftOffset;
resY -= topOffset;
resX *= bounds.getWidth() / (getWidth() - leftOffset - rightOffset);
resY *= bounds.getHeight() / (getHeight() - topOffset - bottomOffset);
if (isCenterShape()) {
resX += bounds.getMinX();
resY += bounds.getMinY();
}
} else if (isCenterShape()) {
double boundsWidth = bounds.getWidth();
double boundsHeight = bounds.getHeight();
double scaleFactorX = boundsWidth / (boundsWidth - leftOffset - rightOffset);
double scaleFactorY = boundsHeight / (boundsHeight - topOffset - bottomOffset);
resX = scaleFactorX * (resX -(leftOffset + (getWidth() - boundsWidth) / 2)) + bounds.getMinX();
resY = scaleFactorY * (resY -(topOffset + (getHeight() - boundsHeight) / 2)) + bounds.getMinY();
} else if (topOffset != 0 || rightOffset != 0 || bottomOffset != 0 || leftOffset != 0) {
double scaleFactorX = bounds.getWidth() / (bounds.getWidth() - leftOffset - rightOffset);
double scaleFactorY = bounds.getHeight() / (bounds.getHeight() - topOffset - bottomOffset);
resX = scaleFactorX * (resX - leftOffset - bounds.getMinX()) + bounds.getMinX();
resY = scaleFactorY * (resY - topOffset - bounds.getMinY()) + bounds.getMinY();
}
return shape.contains((float)resX, (float)resY);
}
private boolean doComputeContains(double localX, double localY) {
final double x2 = getWidth();
final double y2 = getHeight();
final Background background = getBackground();
if (_shape != null) {
if (background != null && !background.getFills().isEmpty()) {
final List<BackgroundFill> fills = background.getFills();
double topO = Double.MAX_VALUE;
double leftO = Double.MAX_VALUE;
double bottomO = Double.MAX_VALUE;
double rightO = Double.MAX_VALUE;
for (int i = 0, max = fills.size(); i < max; i++) {
BackgroundFill bf = fills.get(0);
topO = Math.min(topO, bf.getInsets().getTop());
leftO = Math.min(leftO, bf.getInsets().getLeft());
bottomO = Math.min(bottomO, bf.getInsets().getBottom());
rightO = Math.min(rightO, bf.getInsets().getRight());
}
return shapeContains(ShapeHelper.configShape(_shape), localX, localY, topO, leftO, bottomO, rightO);
}
return false;
}
if (background != null) {
final List<BackgroundFill> fills = background.getFills();
for (int i = 0, max = fills.size(); i < max; i++) {
final BackgroundFill bgFill = fills.get(i);
if (contains(localX, localY, 0, 0, x2, y2, bgFill.getInsets(), getNormalizedFillCorner(i))) {
return true;
}
}
}
final Border border = getBorder();
if (border != null) {
final List<BorderStroke> strokes = border.getStrokes();
for (int i=0, max=strokes.size(); i<max; i++) {
final BorderStroke strokeBorder = strokes.get(i);
if (contains(localX, localY, 0, 0, x2, y2, strokeBorder.getWidths(), false, strokeBorder.getInsets(),
getNormalizedStrokeCorner(i))) {
return true;
}
}
final List<BorderImage> images = border.getImages();
for (int i = 0, max = images.size(); i < max; i++) {
final BorderImage borderImage = images.get(i);
if (contains(localX, localY, 0, 0, x2, y2, borderImage.getWidths(), borderImage.isFilled(),
borderImage.getInsets(), CornerRadii.EMPTY)) {
return true;
}
}
}
return false;
}
private boolean contains(final double px, final double py,
final double x1, final double y1, final double x2, final double y2,
BorderWidths widths, boolean filled,
final Insets insets, final CornerRadii rad) {
if (filled) {
if (contains(px, py, x1, y1, x2, y2, insets, rad)) {
return true;
}
} else {
boolean insideOuterEdge = contains(px, py, x1, y1, x2, y2, insets, rad);
if (insideOuterEdge) {
boolean outsideInnerEdge = !contains(px, py,
x1 + (widths.isLeftAsPercentage() ? getWidth() * widths.getLeft() : widths.getLeft()),
y1 + (widths.isTopAsPercentage() ? getHeight() * widths.getTop() : widths.getTop()),
x2 - (widths.isRightAsPercentage() ? getWidth() * widths.getRight() : widths.getRight()),
y2 - (widths.isBottomAsPercentage() ? getHeight() * widths.getBottom() : widths.getBottom()),
insets, rad);
if (outsideInnerEdge) return true;
}
}
return false;
}
private boolean contains(final double px, final double py,
final double x1, final double y1, final double x2, final double y2,
final Insets insets, CornerRadii rad) {
final double rrx0 = x1 + insets.getLeft();
final double rry0 = y1 + insets.getTop();
final double rrx1 = x2 - insets.getRight();
final double rry1 = y2 - insets.getBottom();
if (px >= rrx0 && py >= rry0 && px <= rrx1 && py <= rry1) {
final double tlhr = rad.getTopLeftHorizontalRadius();
if (rad.isUniform() && tlhr == 0) {
return true;
} else {
final double tlvr = rad.getTopLeftVerticalRadius();
final double trhr = rad.getTopRightHorizontalRadius();
final double trvr = rad.getTopRightVerticalRadius();
final double blhr = rad.getBottomLeftHorizontalRadius();
final double blvr = rad.getBottomLeftVerticalRadius();
final double brhr = rad.getBottomRightHorizontalRadius();
final double brvr = rad.getBottomRightVerticalRadius();
double centerX, centerY, a, b;
if (px <= rrx0 + tlhr && py <= rry0 + tlvr) {
centerX = rrx0 + tlhr;
centerY = rry0 + tlvr;
a = tlhr;
b = tlvr;
} else if (px >= rrx1 - trhr && py <= rry0 + trvr) {
centerX = rrx1 - trhr;
centerY = rry0 + trvr;
a = trhr;
b = trvr;
} else if (px >= rrx1 - brhr && py >= rry1 - brvr) {
centerX = rrx1 - brhr;
centerY = rry1 - brvr;
a = brhr;
b = brvr;
} else if (px <= rrx0 + blhr && py >= rry1 - blvr) {
centerX = rrx0 + blhr;
centerY = rry1 - blvr;
a = blhr;
b = blvr;
} else {
return true;
}
double x = px - centerX;
double y = py - centerY;
double result = ((x*x)/(a*a) + (y*y)/(b*b));
if (result - .0000001 <= 1) return true;
}
}
return false;
}
private boolean cornersValid;
private List<CornerRadii> normalizedFillCorners;
private List<CornerRadii> normalizedStrokeCorners;
private CornerRadii getNormalizedFillCorner(int i) {
if (!cornersValid) {
validateCorners();
}
return (normalizedFillCorners == null
? getBackground().getFills().get(i).getRadii()
: normalizedFillCorners.get(i));
}
private CornerRadii getNormalizedStrokeCorner(int i) {
if (!cornersValid) {
validateCorners();
}
return (normalizedStrokeCorners == null
? getBorder().getStrokes().get(i).getRadii()
: normalizedStrokeCorners.get(i));
}
private void validateCorners() {
final double width = getWidth();
final double height = getHeight();
List<CornerRadii> newFillCorners = null;
List<CornerRadii> newStrokeCorners = null;
final Background background = getBackground();
final List<BackgroundFill> fills = background == null ? Collections.EMPTY_LIST : background.getFills();
for (int i = 0; i < fills.size(); i++) {
final BackgroundFill fill = fills.get(i);
final CornerRadii origRadii = fill.getRadii();
final Insets origInsets = fill.getInsets();
final CornerRadii newRadii = normalize(origRadii, origInsets, width, height);
if (origRadii != newRadii) {
if (newFillCorners == null) {
newFillCorners = Arrays.asList(new CornerRadii[fills.size()]);
}
newFillCorners.set(i, newRadii);
}
}
final Border border = getBorder();
final List<BorderStroke> strokes = (border == null ? Collections.EMPTY_LIST : border.getStrokes());
for (int i = 0; i < strokes.size(); i++) {
final BorderStroke stroke = strokes.get(i);
final CornerRadii origRadii = stroke.getRadii();
final Insets origInsets = stroke.getInsets();
final CornerRadii newRadii = normalize(origRadii, origInsets, width, height);
if (origRadii != newRadii) {
if (newStrokeCorners == null) {
newStrokeCorners = Arrays.asList(new CornerRadii[strokes.size()]);
}
newStrokeCorners.set(i, newRadii);
}
}
if (newFillCorners != null) {
for (int i = 0; i < fills.size(); i++) {
if (newFillCorners.get(i) == null) {
newFillCorners.set(i, fills.get(i).getRadii());
}
}
newFillCorners = Collections.unmodifiableList(newFillCorners);
}
if (newStrokeCorners != null) {
for (int i = 0; i < strokes.size(); i++) {
if (newStrokeCorners.get(i) == null) {
newStrokeCorners.set(i, strokes.get(i).getRadii());
}
}
newStrokeCorners = Collections.unmodifiableList(newStrokeCorners);
}
normalizedFillCorners = newFillCorners;
normalizedStrokeCorners = newStrokeCorners;
cornersValid = true;
}
private static CornerRadii normalize(CornerRadii radii, Insets insets, double width, double height) {
width -= insets.getLeft() + insets.getRight();
height -= insets.getTop() + insets.getBottom();
if (width <= 0 || height <= 0) return CornerRadii.EMPTY;
double tlvr = radii.getTopLeftVerticalRadius();
double tlhr = radii.getTopLeftHorizontalRadius();
double trvr = radii.getTopRightVerticalRadius();
double trhr = radii.getTopRightHorizontalRadius();
double brvr = radii.getBottomRightVerticalRadius();
double brhr = radii.getBottomRightHorizontalRadius();
double blvr = radii.getBottomLeftVerticalRadius();
double blhr = radii.getBottomLeftHorizontalRadius();
if (radii.hasPercentBasedRadii) {
if (radii.isTopLeftVerticalRadiusAsPercentage()) tlvr *= height;
if (radii.isTopLeftHorizontalRadiusAsPercentage()) tlhr *= width;
if (radii.isTopRightVerticalRadiusAsPercentage()) trvr *= height;
if (radii.isTopRightHorizontalRadiusAsPercentage()) trhr *= width;
if (radii.isBottomRightVerticalRadiusAsPercentage()) brvr *= height;
if (radii.isBottomRightHorizontalRadiusAsPercentage()) brhr *= width;
if (radii.isBottomLeftVerticalRadiusAsPercentage()) blvr *= height;
if (radii.isBottomLeftHorizontalRadiusAsPercentage()) blhr *= width;
}
double scale = 1.0;
if (tlhr + trhr > width) { scale = Math.min(scale, width / (tlhr + trhr)); }
if (blhr + brhr > width) { scale = Math.min(scale, width / (blhr + brhr)); }
if (tlvr + blvr > height) { scale = Math.min(scale, height / (tlvr + blvr)); }
if (trvr + brvr > height) { scale = Math.min(scale, height / (trvr + brvr)); }
if (scale < 1.0) {
tlvr *= scale; tlhr *= scale;
trvr *= scale; trhr *= scale;
brvr *= scale; brhr *= scale;
blvr *= scale; blhr *= scale;
}
if (radii.hasPercentBasedRadii || scale < 1.0) {
return new CornerRadii(tlhr, tlvr, trvr, trhr, brhr, brvr, blvr, blhr,
false, false, false, false, false, false, false, false);
}
return radii;
}
private void doPickNodeLocal(PickRay pickRay, PickResultChooser result) {
double boundsDistance = NodeHelper.intersectsBounds(this, pickRay);
if (!Double.isNaN(boundsDistance) && ParentHelper.pickChildrenNode(this, pickRay, result)) {
NodeHelper.intersects(this, pickRay, result);
}
}
private Bounds boundingBox;
private Bounds doComputeLayoutBounds() {
if (boundingBox == null) {
boundingBox = new BoundingBox(0, 0, 0, getWidth(), getHeight(), 0);
}
return boundingBox;
}
private void doNotifyLayoutBoundsChanged() {
}
private BaseBounds computeShapeBounds(BaseBounds bounds)
{
com.sun.javafx.geom.Shape s = ShapeHelper.configShape(_shape);
float[] bbox = {
Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY,
};
Background bg = getBackground();
if (bg != null) {
final RectBounds sBounds = s.getBounds();
final Insets bgOutsets = bg.getOutsets();
bbox[0] = sBounds.getMinX() - (float) bgOutsets.getLeft();
bbox[1] = sBounds.getMinY() - (float) bgOutsets.getTop();
bbox[2] = sBounds.getMaxX() + (float) bgOutsets.getBottom();
bbox[3] = sBounds.getMaxY() + (float) bgOutsets.getRight();
}
final Border b = getBorder();
if (b != null && b.getStrokes().size() > 0) {
for (BorderStroke bs : b.getStrokes()) {
BorderStrokeStyle bss = bs.getTopStyle() != null ? bs.getTopStyle() :
bs.getLeftStyle() != null ? bs.getLeftStyle() :
bs.getBottomStyle() != null ? bs.getBottomStyle() :
bs.getRightStyle() != null ? bs.getRightStyle() : null;
if (bss == null || bss == BorderStrokeStyle.NONE) {
continue;
}
final StrokeType type = bss.getType();
double sw = Math.max(bs.getWidths().top, 0d);
StrokeLineCap cap = bss.getLineCap();
StrokeLineJoin join = bss.getLineJoin();
float miterlimit = (float) Math.max(bss.getMiterLimit(), 1d);
Toolkit.getToolkit().accumulateStrokeBounds(
s,
bbox, type, sw,
cap, join, miterlimit, BaseTransform.IDENTITY_TRANSFORM);
}
}
if (bbox[2] < bbox[0] || bbox[3] < bbox[1]) {
return bounds.makeEmpty();
}
return bounds.deriveWithNewBounds(bbox[0], bbox[1], 0.0f,
bbox[2], bbox[3], 0.0f);
}
private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) {
double bx1 = 0;
double by1 = 0;
double bx2 = getWidth();
double by2 = getHeight();
if (_shape != null && isScaleShape() == false) {
final BaseBounds shapeBounds = computeShapeBounds(bounds);
final double shapeWidth = shapeBounds.getWidth();
final double shapeHeight = shapeBounds.getHeight();
if (isCenterShape()) {
bx1 = (bx2 - shapeWidth) / 2;
by1 = (by2 - shapeHeight) / 2;
bx2 = bx1 + shapeWidth;
by2 = by1 + shapeHeight;
} else {
bx1 = shapeBounds.getMinX();
by1 = shapeBounds.getMinY();
bx2 = shapeBounds.getMaxX();
by2 = shapeBounds.getMaxY();
}
} else {
final Background background = getBackground();
final Border border = getBorder();
final Insets backgroundOutsets = background == null ? Insets.EMPTY : background.getOutsets();
final Insets borderOutsets = border == null ? Insets.EMPTY : border.getOutsets();
bx1 -= Math.max(backgroundOutsets.getLeft(), borderOutsets.getLeft());
by1 -= Math.max(backgroundOutsets.getTop(), borderOutsets.getTop());
bx2 += Math.max(backgroundOutsets.getRight(), borderOutsets.getRight());
by2 += Math.max(backgroundOutsets.getBottom(), borderOutsets.getBottom());
}
BaseBounds cb = RegionHelper.superComputeGeomBounds(this, bounds, tx);
if (cb.isEmpty()) {
bounds = bounds.deriveWithNewBounds(
(float)bx1, (float)by1, 0.0f,
(float)bx2, (float)by2, 0.0f);
bounds = tx.transform(bounds, bounds);
return bounds;
} else {
BaseBounds tempBounds = TempState.getInstance().bounds;
tempBounds = tempBounds.deriveWithNewBounds(
(float)bx1, (float)by1, 0.0f,
(float)bx2, (float)by2, 0.0f);
BaseBounds bb = tx.transform(tempBounds, tempBounds);
cb = cb.deriveWithUnion(bb);
return cb;
}
}
public String getUserAgentStylesheet() {
return null;
}
private static class StyleableProperties {
private static final CssMetaData<Region,Insets> PADDING =
new CssMetaData<Region,Insets>("-fx-padding",
InsetsConverter.getInstance(), Insets.EMPTY) {
@Override public boolean isSettable(Region node) {
return node.padding == null || !node.padding.isBound();
}
@Override public StyleableProperty<Insets> getStyleableProperty(Region node) {
return (StyleableProperty<Insets>)node.paddingProperty();
}
};
private static final CssMetaData<Region,Insets> OPAQUE_INSETS =
new CssMetaData<Region,Insets>("-fx-opaque-insets",
InsetsConverter.getInstance(), null) {
@Override
public boolean isSettable(Region node) {
return node.opaqueInsets == null || !node.opaqueInsets.isBound();
}
@Override
public StyleableProperty<Insets> getStyleableProperty(Region node) {
return (StyleableProperty<Insets>)node.opaqueInsetsProperty();
}
};
private static final CssMetaData<Region,Background> BACKGROUND =
new CssMetaData<Region,Background>("-fx-region-background",
BackgroundConverter.INSTANCE,
null,
false,
Background.getClassCssMetaData()) {
@Override public boolean isSettable(Region node) {
return !node.background.isBound();
}
@Override public StyleableProperty<Background> getStyleableProperty(Region node) {
return (StyleableProperty<Background>)node.background;
}
};
private static final CssMetaData<Region,Border> BORDER =
new CssMetaData<Region,Border>("-fx-region-border",
BorderConverter.getInstance(),
null,
false,
Border.getClassCssMetaData()) {
@Override public boolean isSettable(Region node) {
return !node.border.isBound();
}
@Override public StyleableProperty<Border> getStyleableProperty(Region node) {
return (StyleableProperty<Border>)node.border;
}
};
private static final CssMetaData<Region,Shape> SHAPE =
new CssMetaData<Region,Shape>("-fx-shape",
ShapeConverter.getInstance()) {
@Override public boolean isSettable(Region node) {
return node.shape == null || !node.shape.isBound();
}
@Override public StyleableProperty<Shape> getStyleableProperty(Region node) {
return (StyleableProperty<Shape>)node.shapeProperty();
}
};
private static final CssMetaData<Region, Boolean> SCALE_SHAPE =
new CssMetaData<Region,Boolean>("-fx-scale-shape",
BooleanConverter.getInstance(), Boolean.TRUE){
@Override public boolean isSettable(Region node) {
return node.scaleShape == null || !node.scaleShape.isBound();
}
@Override public StyleableProperty<Boolean> getStyleableProperty(Region node) {
return (StyleableProperty<Boolean>)node.scaleShapeProperty();
}
};
private static final CssMetaData<Region,Boolean> POSITION_SHAPE =
new CssMetaData<Region,Boolean>("-fx-position-shape",
BooleanConverter.getInstance(), Boolean.TRUE){
@Override public boolean isSettable(Region node) {
return node.centerShape == null || !node.centerShape.isBound();
}
@Override public StyleableProperty<Boolean> getStyleableProperty(Region node) {
return (StyleableProperty<Boolean>)node.centerShapeProperty();
}
};
private static final CssMetaData<Region,Boolean> CACHE_SHAPE =
new CssMetaData<Region,Boolean>("-fx-cache-shape",
BooleanConverter.getInstance(), Boolean.TRUE){
@Override public boolean isSettable(Region node) {
return node.cacheShape == null || !node.cacheShape.isBound();
}
@Override public StyleableProperty<Boolean> getStyleableProperty(Region node) {
return (StyleableProperty<Boolean>)node.cacheShapeProperty();
}
};
private static final CssMetaData<Region, Boolean> SNAP_TO_PIXEL =
new CssMetaData<Region,Boolean>("-fx-snap-to-pixel",
BooleanConverter.getInstance(), Boolean.TRUE){
@Override public boolean isSettable(Region node) {
return node.snapToPixel == null ||
!node.snapToPixel.isBound();
}
@Override public StyleableProperty<Boolean> getStyleableProperty(Region node) {
return (StyleableProperty<Boolean>)node.snapToPixelProperty();
}
};
private static final CssMetaData<Region, Number> MIN_HEIGHT =
new CssMetaData<Region,Number>("-fx-min-height",
SizeConverter.getInstance(), USE_COMPUTED_SIZE){
@Override public boolean isSettable(Region node) {
return node.minHeight == null ||
!node.minHeight.isBound();
}
@Override public StyleableProperty<Number> getStyleableProperty(Region node) {
return (StyleableProperty<Number>)node.minHeightProperty();
}
};
private static final CssMetaData<Region, Number> PREF_HEIGHT =
new CssMetaData<Region,Number>("-fx-pref-height",
SizeConverter.getInstance(), USE_COMPUTED_SIZE){
@Override public boolean isSettable(Region node) {
return node.prefHeight == null ||
!node.prefHeight.isBound();
}
@Override public StyleableProperty<Number> getStyleableProperty(Region node) {
return (StyleableProperty<Number>)node.prefHeightProperty();
}
};
private static final CssMetaData<Region, Number> MAX_HEIGHT =
new CssMetaData<Region,Number>("-fx-max-height",
SizeConverter.getInstance(), USE_COMPUTED_SIZE){
@Override public boolean isSettable(Region node) {
return node.maxHeight == null ||
!node.maxHeight.isBound();
}
@Override public StyleableProperty<Number> getStyleableProperty(Region node) {
return (StyleableProperty<Number>)node.maxHeightProperty();
}
};
private static final CssMetaData<Region, Number> MIN_WIDTH =
new CssMetaData<Region,Number>("-fx-min-width",
SizeConverter.getInstance(), USE_COMPUTED_SIZE){
@Override public boolean isSettable(Region node) {
return node.minWidth == null ||
!node.minWidth.isBound();
}
@Override public StyleableProperty<Number> getStyleableProperty(Region node) {
return (StyleableProperty<Number>)node.minWidthProperty();
}
};
private static final CssMetaData<Region, Number> PREF_WIDTH =
new CssMetaData<Region,Number>("-fx-pref-width",
SizeConverter.getInstance(), USE_COMPUTED_SIZE){
@Override public boolean isSettable(Region node) {
return node.prefWidth == null ||
!node.prefWidth.isBound();
}
@Override public StyleableProperty<Number> getStyleableProperty(Region node) {
return (StyleableProperty<Number>)node.prefWidthProperty();
}
};
private static final CssMetaData<Region, Number> MAX_WIDTH =
new CssMetaData<Region,Number>("-fx-max-width",
SizeConverter.getInstance(), USE_COMPUTED_SIZE){
@Override public boolean isSettable(Region node) {
return node.maxWidth == null ||
!node.maxWidth.isBound();
}
@Override public StyleableProperty<Number> getStyleableProperty(Region node) {
return (StyleableProperty<Number>)node.maxWidthProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Parent.getClassCssMetaData());
styleables.add(PADDING);
styleables.add(BACKGROUND);
styleables.add(BORDER);
styleables.add(OPAQUE_INSETS);
styleables.add(SHAPE);
styleables.add(SCALE_SHAPE);
styleables.add(POSITION_SHAPE);
styleables.add(SNAP_TO_PIXEL);
styleables.add(MIN_WIDTH);
styleables.add(PREF_WIDTH);
styleables.add(MAX_WIDTH);
styleables.add(MIN_HEIGHT);
styleables.add(PREF_HEIGHT);
styleables.add(MAX_HEIGHT);
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
