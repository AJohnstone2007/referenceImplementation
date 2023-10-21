package javafx.scene.shape;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.sun.javafx.util.Utils;
import com.sun.javafx.beans.event.AbstractNotifyListener;
import com.sun.javafx.collections.TrackableObservableList;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.PaintConverter;
import javafx.css.converter.SizeConverter;
import com.sun.javafx.geom.Area;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PathIterator;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.ShapeHelper;
import com.sun.javafx.sg.prism.NGShape;
import com.sun.javafx.tk.Toolkit;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
public abstract class Shape extends Node {
static {
ShapeHelper.setShapeAccessor(new ShapeHelper.ShapeAccessor() {
@Override
public void doUpdatePeer(Node node) {
((Shape) node).doUpdatePeer();
}
@Override
public void doMarkDirty(Node node, DirtyBits dirtyBit) {
((Shape) node).doMarkDirty(dirtyBit);
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((Shape) node).doComputeGeomBounds(bounds, tx);
}
@Override
public boolean doComputeContains(Node node, double localX, double localY) {
return ((Shape) node).doComputeContains(localX, localY);
}
@Override
public Paint doCssGetFillInitialValue(Shape shape) {
return shape.doCssGetFillInitialValue();
}
@Override
public Paint doCssGetStrokeInitialValue(Shape shape) {
return shape.doCssGetStrokeInitialValue();
}
@Override
public NGShape.Mode getMode(Shape shape) {
return shape.getMode();
}
@Override
public void setMode(Shape shape, NGShape.Mode mode) {
shape.setMode(mode);
}
@Override
public void setShapeChangeListener(Shape shape, Runnable listener) {
shape.setShapeChangeListener(listener);
}
});
}
public Shape() {
}
StrokeLineJoin convertLineJoin(StrokeLineJoin t) {
return t;
}
public final void setStrokeType(StrokeType value) {
strokeTypeProperty().set(value);
}
public final StrokeType getStrokeType() {
return (strokeAttributes == null) ? DEFAULT_STROKE_TYPE
: strokeAttributes.getType();
}
public final ObjectProperty<StrokeType> strokeTypeProperty() {
return getStrokeAttributes().typeProperty();
}
public final void setStrokeWidth(double value) {
strokeWidthProperty().set(value);
}
public final double getStrokeWidth() {
return (strokeAttributes == null) ? DEFAULT_STROKE_WIDTH
: strokeAttributes.getWidth();
}
public final DoubleProperty strokeWidthProperty() {
return getStrokeAttributes().widthProperty();
}
public final void setStrokeLineJoin(StrokeLineJoin value) {
strokeLineJoinProperty().set(value);
}
public final StrokeLineJoin getStrokeLineJoin() {
return (strokeAttributes == null)
? DEFAULT_STROKE_LINE_JOIN
: strokeAttributes.getLineJoin();
}
public final ObjectProperty<StrokeLineJoin> strokeLineJoinProperty() {
return getStrokeAttributes().lineJoinProperty();
}
public final void setStrokeLineCap(StrokeLineCap value) {
strokeLineCapProperty().set(value);
}
public final StrokeLineCap getStrokeLineCap() {
return (strokeAttributes == null) ? DEFAULT_STROKE_LINE_CAP
: strokeAttributes.getLineCap();
}
public final ObjectProperty<StrokeLineCap> strokeLineCapProperty() {
return getStrokeAttributes().lineCapProperty();
}
public final void setStrokeMiterLimit(double value) {
strokeMiterLimitProperty().set(value);
}
public final double getStrokeMiterLimit() {
return (strokeAttributes == null) ? DEFAULT_STROKE_MITER_LIMIT
: strokeAttributes.getMiterLimit();
}
public final DoubleProperty strokeMiterLimitProperty() {
return getStrokeAttributes().miterLimitProperty();
}
public final void setStrokeDashOffset(double value) {
strokeDashOffsetProperty().set(value);
}
public final double getStrokeDashOffset() {
return (strokeAttributes == null) ? DEFAULT_STROKE_DASH_OFFSET
: strokeAttributes.getDashOffset();
}
public final DoubleProperty strokeDashOffsetProperty() {
return getStrokeAttributes().dashOffsetProperty();
}
public final ObservableList<Double> getStrokeDashArray() {
return getStrokeAttributes().dashArrayProperty();
}
private NGShape.Mode computeMode() {
if (getFill() != null && getStroke() != null) {
return NGShape.Mode.STROKE_FILL;
} else if (getFill() != null) {
return NGShape.Mode.FILL;
} else if (getStroke() != null) {
return NGShape.Mode.STROKE;
} else {
return NGShape.Mode.EMPTY;
}
}
NGShape.Mode getMode() {
return mode;
}
void setMode(NGShape.Mode mode) {
mode = mode;
}
private NGShape.Mode mode = NGShape.Mode.FILL;
private void checkModeChanged() {
NGShape.Mode newMode = computeMode();
if (mode != newMode) {
mode = newMode;
NodeHelper.markDirty(this, DirtyBits.SHAPE_MODE);
NodeHelper.geomChanged(this);
}
}
private ObjectProperty<Paint> fill;
public final void setFill(Paint value) {
fillProperty().set(value);
}
public final Paint getFill() {
return fill == null ? Color.BLACK : fill.get();
}
Paint old_fill;
public final ObjectProperty<Paint> fillProperty() {
if (fill == null) {
fill = new StyleableObjectProperty<Paint>(Color.BLACK) {
boolean needsListener = false;
@Override public void invalidated() {
Paint _fill = get();
if (needsListener) {
Toolkit.getPaintAccessor().
removeListener(old_fill, platformImageChangeListener);
}
needsListener = _fill != null &&
Toolkit.getPaintAccessor().isMutable(_fill);
old_fill = _fill;
if (needsListener) {
Toolkit.getPaintAccessor().
addListener(_fill, platformImageChangeListener);
}
NodeHelper.markDirty(Shape.this, DirtyBits.SHAPE_FILL);
checkModeChanged();
}
@Override
public CssMetaData<Shape,Paint> getCssMetaData() {
return StyleableProperties.FILL;
}
@Override
public Object getBean() {
return Shape.this;
}
@Override
public String getName() {
return "fill";
}
};
}
return fill;
}
private ObjectProperty<Paint> stroke;
public final void setStroke(Paint value) {
strokeProperty().set(value);
}
private final AbstractNotifyListener platformImageChangeListener =
new AbstractNotifyListener() {
@Override
public void invalidated(Observable valueModel) {
NodeHelper.markDirty(Shape.this, DirtyBits.SHAPE_FILL);
NodeHelper.markDirty(Shape.this, DirtyBits.SHAPE_STROKE);
NodeHelper.geomChanged(Shape.this);
checkModeChanged();
}
};
public final Paint getStroke() {
return stroke == null ? null : stroke.get();
}
Paint old_stroke;
public final ObjectProperty<Paint> strokeProperty() {
if (stroke == null) {
stroke = new StyleableObjectProperty<Paint>() {
boolean needsListener = false;
@Override public void invalidated() {
Paint _stroke = get();
if (needsListener) {
Toolkit.getPaintAccessor().
removeListener(old_stroke, platformImageChangeListener);
}
needsListener = _stroke != null &&
Toolkit.getPaintAccessor().isMutable(_stroke);
old_stroke = _stroke;
if (needsListener) {
Toolkit.getPaintAccessor().
addListener(_stroke, platformImageChangeListener);
}
NodeHelper.markDirty(Shape.this, DirtyBits.SHAPE_STROKE);
checkModeChanged();
}
@Override
public CssMetaData<Shape,Paint> getCssMetaData() {
return StyleableProperties.STROKE;
}
@Override
public Object getBean() {
return Shape.this;
}
@Override
public String getName() {
return "stroke";
}
};
}
return stroke;
}
private BooleanProperty smooth;
public final void setSmooth(boolean value) {
smoothProperty().set(value);
}
public final boolean isSmooth() {
return smooth == null ? true : smooth.get();
}
public final BooleanProperty smoothProperty() {
if (smooth == null) {
smooth = new StyleableBooleanProperty(true) {
@Override
public void invalidated() {
NodeHelper.markDirty(Shape.this, DirtyBits.NODE_SMOOTH);
}
@Override
public CssMetaData<Shape,Boolean> getCssMetaData() {
return StyleableProperties.SMOOTH;
}
@Override
public Object getBean() {
return Shape.this;
}
@Override
public String getName() {
return "smooth";
}
};
}
return smooth;
}
private Paint doCssGetFillInitialValue() {
return Color.BLACK;
}
private Paint doCssGetStrokeInitialValue() {
return null;
}
private static class StyleableProperties {
private static final CssMetaData<Shape,Paint> FILL =
new CssMetaData<Shape,Paint>("-fx-fill",
PaintConverter.getInstance(), Color.BLACK) {
@Override
public boolean isSettable(Shape node) {
return node.fill == null || !node.fill.isBound();
}
@Override
public StyleableProperty<Paint> getStyleableProperty(Shape node) {
return (StyleableProperty<Paint>)node.fillProperty();
}
@Override
public Paint getInitialValue(Shape node) {
return ShapeHelper.cssGetFillInitialValue(node);
}
};
private static final CssMetaData<Shape,Boolean> SMOOTH =
new CssMetaData<Shape,Boolean>("-fx-smooth",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(Shape node) {
return node.smooth == null || !node.smooth.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(Shape node) {
return (StyleableProperty<Boolean>)node.smoothProperty();
}
};
private static final CssMetaData<Shape,Paint> STROKE =
new CssMetaData<Shape,Paint>("-fx-stroke",
PaintConverter.getInstance()) {
@Override
public boolean isSettable(Shape node) {
return node.stroke == null || !node.stroke.isBound();
}
@Override
public StyleableProperty<Paint> getStyleableProperty(Shape node) {
return (StyleableProperty<Paint>)node.strokeProperty();
}
@Override
public Paint getInitialValue(Shape node) {
return ShapeHelper.cssGetStrokeInitialValue(node);
}
};
private static final CssMetaData<Shape,Number[]> STROKE_DASH_ARRAY =
new CssMetaData<Shape,Number[]>("-fx-stroke-dash-array",
SizeConverter.SequenceConverter.getInstance(),
new Double[0]) {
@Override
public boolean isSettable(Shape node) {
return true;
}
@Override
public StyleableProperty<Number[]> getStyleableProperty(final Shape node) {
return (StyleableProperty<Number[]>)node.getStrokeAttributes().cssDashArrayProperty();
}
};
private static final CssMetaData<Shape,Number> STROKE_DASH_OFFSET =
new CssMetaData<Shape,Number>("-fx-stroke-dash-offset",
SizeConverter.getInstance(), 0.0) {
@Override
public boolean isSettable(Shape node) {
return node.strokeAttributes == null ||
node.strokeAttributes.canSetDashOffset();
}
@Override
public StyleableProperty<Number> getStyleableProperty(Shape node) {
return (StyleableProperty<Number>)node.strokeDashOffsetProperty();
}
};
private static final CssMetaData<Shape,StrokeLineCap> STROKE_LINE_CAP =
new CssMetaData<Shape,StrokeLineCap>("-fx-stroke-line-cap",
new EnumConverter<StrokeLineCap>(StrokeLineCap.class),
StrokeLineCap.SQUARE) {
@Override
public boolean isSettable(Shape node) {
return node.strokeAttributes == null ||
node.strokeAttributes.canSetLineCap();
}
@Override
public StyleableProperty<StrokeLineCap> getStyleableProperty(Shape node) {
return (StyleableProperty<StrokeLineCap>)node.strokeLineCapProperty();
}
};
private static final CssMetaData<Shape,StrokeLineJoin> STROKE_LINE_JOIN =
new CssMetaData<Shape,StrokeLineJoin>("-fx-stroke-line-join",
new EnumConverter<StrokeLineJoin>(StrokeLineJoin.class),
StrokeLineJoin.MITER) {
@Override
public boolean isSettable(Shape node) {
return node.strokeAttributes == null ||
node.strokeAttributes.canSetLineJoin();
}
@Override
public StyleableProperty<StrokeLineJoin> getStyleableProperty(Shape node) {
return (StyleableProperty<StrokeLineJoin>)node.strokeLineJoinProperty();
}
};
private static final CssMetaData<Shape,StrokeType> STROKE_TYPE =
new CssMetaData<Shape,StrokeType>("-fx-stroke-type",
new EnumConverter<StrokeType>(StrokeType.class),
StrokeType.CENTERED) {
@Override
public boolean isSettable(Shape node) {
return node.strokeAttributes == null ||
node.strokeAttributes.canSetType();
}
@Override
public StyleableProperty<StrokeType> getStyleableProperty(Shape node) {
return (StyleableProperty<StrokeType>)node.strokeTypeProperty();
}
};
private static final CssMetaData<Shape,Number> STROKE_MITER_LIMIT =
new CssMetaData<Shape,Number>("-fx-stroke-miter-limit",
SizeConverter.getInstance(), 10.0) {
@Override
public boolean isSettable(Shape node) {
return node.strokeAttributes == null ||
node.strokeAttributes.canSetMiterLimit();
}
@Override
public StyleableProperty<Number> getStyleableProperty(Shape node) {
return (StyleableProperty<Number>)node.strokeMiterLimitProperty();
}
};
private static final CssMetaData<Shape,Number> STROKE_WIDTH =
new CssMetaData<Shape,Number>("-fx-stroke-width",
SizeConverter.getInstance(), 1.0) {
@Override
public boolean isSettable(Shape node) {
return node.strokeAttributes == null ||
node.strokeAttributes.canSetWidth();
}
@Override
public StyleableProperty<Number> getStyleableProperty(Shape node) {
return (StyleableProperty<Number>)node.strokeWidthProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Node.getClassCssMetaData());
styleables.add(FILL);
styleables.add(SMOOTH);
styleables.add(STROKE);
styleables.add(STROKE_DASH_ARRAY);
styleables.add(STROKE_DASH_OFFSET);
styleables.add(STROKE_LINE_CAP);
styleables.add(STROKE_LINE_JOIN);
styleables.add(STROKE_TYPE);
styleables.add(STROKE_MITER_LIMIT);
styleables.add(STROKE_WIDTH);
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
private BaseBounds doComputeGeomBounds(BaseBounds bounds,
BaseTransform tx) {
return computeShapeBounds(bounds, tx, ShapeHelper.configShape(this));
}
private boolean doComputeContains(double localX, double localY) {
return computeShapeContains(localX, localY, ShapeHelper.configShape(this));
}
private static final double MIN_STROKE_WIDTH = 0.0f;
private static final double MIN_STROKE_MITER_LIMIT = 1.0f;
private void updatePGShape() {
final NGShape peer = NodeHelper.getPeer(this);
if (strokeAttributesDirty && (getStroke() != null)) {
final float[] pgDashArray =
(hasStrokeDashArray())
? toPGDashArray(getStrokeDashArray())
: DEFAULT_PG_STROKE_DASH_ARRAY;
peer.setDrawStroke(
(float)Utils.clampMin(getStrokeWidth(),
MIN_STROKE_WIDTH),
getStrokeType(),
getStrokeLineCap(),
convertLineJoin(getStrokeLineJoin()),
(float)Utils.clampMin(getStrokeMiterLimit(),
MIN_STROKE_MITER_LIMIT),
pgDashArray, (float)getStrokeDashOffset());
strokeAttributesDirty = false;
}
if (NodeHelper.isDirty(this, DirtyBits.SHAPE_MODE)) {
peer.setMode(mode);
}
if (NodeHelper.isDirty(this, DirtyBits.SHAPE_FILL)) {
Paint localFill = getFill();
peer.setFillPaint(localFill == null ? null :
Toolkit.getPaintAccessor().getPlatformPaint(localFill));
}
if (NodeHelper.isDirty(this, DirtyBits.SHAPE_STROKE)) {
Paint localStroke = getStroke();
peer.setDrawPaint(localStroke == null ? null :
Toolkit.getPaintAccessor().getPlatformPaint(localStroke));
}
if (NodeHelper.isDirty(this, DirtyBits.NODE_SMOOTH)) {
peer.setSmooth(isSmooth());
}
}
private void doMarkDirty(DirtyBits dirtyBits) {
final Runnable listener = shapeChangeListener != null ? shapeChangeListener.get() : null;
if (listener != null && NodeHelper.isDirtyEmpty(this)) {
listener.run();
}
}
private Reference<Runnable> shapeChangeListener;
void setShapeChangeListener(Runnable listener) {
if (shapeChangeListener != null) shapeChangeListener.clear();
shapeChangeListener = listener != null ? new WeakReference(listener) : null;
}
private void doUpdatePeer() {
updatePGShape();
}
BaseBounds computeBounds(BaseBounds bounds, BaseTransform tx,
double upad, double dpad,
double x, double y,
double w, double h)
{
if (w < 0.0f || h < 0.0f) return bounds.makeEmpty();
double x0 = x;
double y0 = y;
double x1 = w;
double y1 = h;
double _dpad = dpad;
if (tx.isTranslateOrIdentity()) {
x1 += x0;
y1 += y0;
if (tx.getType() == BaseTransform.TYPE_TRANSLATION) {
final double dx = tx.getMxt();
final double dy = tx.getMyt();
x0 += dx;
y0 += dy;
x1 += dx;
y1 += dy;
}
_dpad += upad;
} else {
x0 -= upad;
y0 -= upad;
x1 += upad*2;
y1 += upad*2;
double mxx = tx.getMxx();
double mxy = tx.getMxy();
double myx = tx.getMyx();
double myy = tx.getMyy();
final double mxt = (x0 * mxx + y0 * mxy + tx.getMxt());
final double myt = (x0 * myx + y0 * myy + tx.getMyt());
mxx *= x1;
mxy *= y1;
myx *= x1;
myy *= y1;
x0 = (Math.min(Math.min(0,mxx),Math.min(mxy,mxx+mxy)))+mxt;
y0 = (Math.min(Math.min(0,myx),Math.min(myy,myx+myy)))+myt;
x1 = (Math.max(Math.max(0,mxx),Math.max(mxy,mxx+mxy)))+mxt;
y1 = (Math.max(Math.max(0,myx),Math.max(myy,myx+myy)))+myt;
}
x0 -= _dpad;
y0 -= _dpad;
x1 += _dpad;
y1 += _dpad;
bounds = bounds.deriveWithNewBounds((float)x0, (float)y0, 0.0f,
(float)x1, (float)y1, 0.0f);
return bounds;
}
BaseBounds computeShapeBounds(BaseBounds bounds, BaseTransform tx,
com.sun.javafx.geom.Shape s)
{
if (mode == NGShape.Mode.EMPTY) {
return bounds.makeEmpty();
}
float[] bbox = {
Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY,
};
boolean includeShape = (mode != NGShape.Mode.STROKE);
boolean includeStroke = (mode != NGShape.Mode.FILL);
if (includeStroke && (getStrokeType() == StrokeType.INSIDE)) {
includeShape = true;
includeStroke = false;
}
if (includeStroke) {
final StrokeType type = getStrokeType();
double sw = Utils.clampMin(getStrokeWidth(), MIN_STROKE_WIDTH);
StrokeLineCap cap = getStrokeLineCap();
StrokeLineJoin join = convertLineJoin(getStrokeLineJoin());
float miterlimit =
(float) Utils.clampMin(getStrokeMiterLimit(), MIN_STROKE_MITER_LIMIT);
Toolkit.getToolkit().accumulateStrokeBounds(
s,
bbox, type, sw,
cap, join, miterlimit, tx);
bbox[0] -= 0.5;
bbox[1] -= 0.5;
bbox[2] += 0.5;
bbox[3] += 0.5;
} else if (includeShape) {
com.sun.javafx.geom.Shape.accumulate(bbox, s, tx);
}
if (bbox[2] < bbox[0] || bbox[3] < bbox[1]) {
return bounds.makeEmpty();
}
bounds = bounds.deriveWithNewBounds(bbox[0], bbox[1], 0.0f,
bbox[2], bbox[3], 0.0f);
return bounds;
}
boolean computeShapeContains(double localX, double localY,
com.sun.javafx.geom.Shape s) {
if (mode == NGShape.Mode.EMPTY) {
return false;
}
boolean includeShape = (mode != NGShape.Mode.STROKE);
boolean includeStroke = (mode != NGShape.Mode.FILL);
if (includeStroke && includeShape &&
(getStrokeType() == StrokeType.INSIDE))
{
includeStroke = false;
}
if (includeShape) {
if (s.contains((float)localX, (float)localY)) {
return true;
}
}
if (includeStroke) {
StrokeType type = getStrokeType();
double sw = Utils.clampMin(getStrokeWidth(), MIN_STROKE_WIDTH);
StrokeLineCap cap = getStrokeLineCap();
StrokeLineJoin join = convertLineJoin(getStrokeLineJoin());
float miterlimit =
(float) Utils.clampMin(getStrokeMiterLimit(), MIN_STROKE_MITER_LIMIT);
return Toolkit.getToolkit().strokeContains(s, localX, localY,
type, sw, cap,
join, miterlimit);
}
return false;
}
private boolean strokeAttributesDirty = true;
private StrokeAttributes strokeAttributes;
private StrokeAttributes getStrokeAttributes() {
if (strokeAttributes == null) {
strokeAttributes = new StrokeAttributes();
}
return strokeAttributes;
}
private boolean hasStrokeDashArray() {
return (strokeAttributes != null) && strokeAttributes.hasDashArray();
}
private static float[] toPGDashArray(final List<Double> dashArray) {
final int size = dashArray.size();
final float[] pgDashArray = new float[size];
for (int i = 0; i < size; i++) {
pgDashArray[i] = dashArray.get(i).floatValue();
}
return pgDashArray;
}
private static final StrokeType DEFAULT_STROKE_TYPE = StrokeType.CENTERED;
private static final double DEFAULT_STROKE_WIDTH = 1.0;
private static final StrokeLineJoin DEFAULT_STROKE_LINE_JOIN =
StrokeLineJoin.MITER;
private static final StrokeLineCap DEFAULT_STROKE_LINE_CAP =
StrokeLineCap.SQUARE;
private static final double DEFAULT_STROKE_MITER_LIMIT = 10.0;
private static final double DEFAULT_STROKE_DASH_OFFSET = 0;
private static final float[] DEFAULT_PG_STROKE_DASH_ARRAY = new float[0];
private final class StrokeAttributes {
private ObjectProperty<StrokeType> type;
private DoubleProperty width;
private ObjectProperty<StrokeLineJoin> lineJoin;
private ObjectProperty<StrokeLineCap> lineCap;
private DoubleProperty miterLimit;
private DoubleProperty dashOffset;
private ObservableList<Double> dashArray;
public final StrokeType getType() {
return (type == null) ? DEFAULT_STROKE_TYPE : type.get();
}
public final ObjectProperty<StrokeType> typeProperty() {
if (type == null) {
type = new StyleableObjectProperty<StrokeType>(DEFAULT_STROKE_TYPE) {
@Override
public void invalidated() {
StrokeAttributes.this.invalidated(
StyleableProperties.STROKE_TYPE);
}
@Override
public CssMetaData<Shape,StrokeType> getCssMetaData() {
return StyleableProperties.STROKE_TYPE;
}
@Override
public Object getBean() {
return Shape.this;
}
@Override
public String getName() {
return "strokeType";
}
};
}
return type;
}
public double getWidth() {
return (width == null) ? DEFAULT_STROKE_WIDTH : width.get();
}
public final DoubleProperty widthProperty() {
if (width == null) {
width = new StyleableDoubleProperty(DEFAULT_STROKE_WIDTH) {
@Override
public void invalidated() {
StrokeAttributes.this.invalidated(
StyleableProperties.STROKE_WIDTH);
}
@Override
public CssMetaData<Shape,Number> getCssMetaData() {
return StyleableProperties.STROKE_WIDTH;
}
@Override
public Object getBean() {
return Shape.this;
}
@Override
public String getName() {
return "strokeWidth";
}
};
}
return width;
}
public StrokeLineJoin getLineJoin() {
return (lineJoin == null) ? DEFAULT_STROKE_LINE_JOIN
: lineJoin.get();
}
public final ObjectProperty<StrokeLineJoin> lineJoinProperty() {
if (lineJoin == null) {
lineJoin = new StyleableObjectProperty<StrokeLineJoin>(
DEFAULT_STROKE_LINE_JOIN) {
@Override
public void invalidated() {
StrokeAttributes.this.invalidated(
StyleableProperties.STROKE_LINE_JOIN);
}
@Override
public CssMetaData<Shape,StrokeLineJoin> getCssMetaData() {
return StyleableProperties.STROKE_LINE_JOIN;
}
@Override
public Object getBean() {
return Shape.this;
}
@Override
public String getName() {
return "strokeLineJoin";
}
};
}
return lineJoin;
}
public StrokeLineCap getLineCap() {
return (lineCap == null) ? DEFAULT_STROKE_LINE_CAP
: lineCap.get();
}
public final ObjectProperty<StrokeLineCap> lineCapProperty() {
if (lineCap == null) {
lineCap = new StyleableObjectProperty<StrokeLineCap>(
DEFAULT_STROKE_LINE_CAP) {
@Override
public void invalidated() {
StrokeAttributes.this.invalidated(
StyleableProperties.STROKE_LINE_CAP);
}
@Override
public CssMetaData<Shape,StrokeLineCap> getCssMetaData() {
return StyleableProperties.STROKE_LINE_CAP;
}
@Override
public Object getBean() {
return Shape.this;
}
@Override
public String getName() {
return "strokeLineCap";
}
};
}
return lineCap;
}
public double getMiterLimit() {
return (miterLimit == null) ? DEFAULT_STROKE_MITER_LIMIT
: miterLimit.get();
}
public final DoubleProperty miterLimitProperty() {
if (miterLimit == null) {
miterLimit = new StyleableDoubleProperty(
DEFAULT_STROKE_MITER_LIMIT) {
@Override
public void invalidated() {
StrokeAttributes.this.invalidated(
StyleableProperties.STROKE_MITER_LIMIT);
}
@Override
public CssMetaData<Shape,Number> getCssMetaData() {
return StyleableProperties.STROKE_MITER_LIMIT;
}
@Override
public Object getBean() {
return Shape.this;
}
@Override
public String getName() {
return "strokeMiterLimit";
}
};
}
return miterLimit;
}
public double getDashOffset() {
return (dashOffset == null) ? DEFAULT_STROKE_DASH_OFFSET
: dashOffset.get();
}
public final DoubleProperty dashOffsetProperty() {
if (dashOffset == null) {
dashOffset = new StyleableDoubleProperty(
DEFAULT_STROKE_DASH_OFFSET) {
@Override
public void invalidated() {
StrokeAttributes.this.invalidated(
StyleableProperties.STROKE_DASH_OFFSET);
}
@Override
public CssMetaData<Shape,Number> getCssMetaData() {
return StyleableProperties.STROKE_DASH_OFFSET;
}
@Override
public Object getBean() {
return Shape.this;
}
@Override
public String getName() {
return "strokeDashOffset";
}
};
}
return dashOffset;
}
public ObservableList<Double> dashArrayProperty() {
if (dashArray == null) {
dashArray = new TrackableObservableList<Double>() {
@Override
protected void onChanged(Change<Double> c) {
StrokeAttributes.this.invalidated(
StyleableProperties.STROKE_DASH_ARRAY);
}
};
}
return dashArray;
}
private ObjectProperty<Number[]> cssDashArray = null;
private ObjectProperty<Number[]> cssDashArrayProperty() {
if (cssDashArray == null) {
cssDashArray = new StyleableObjectProperty<Number[]>()
{
@Override
public void set(Number[] v) {
ObservableList<Double> list = dashArrayProperty();
list.clear();
if (v != null && v.length > 0) {
for (int n=0; n<v.length; n++) {
list.add(v[n].doubleValue());
}
}
}
@Override
public Double[] get() {
List<Double> list = dashArrayProperty();
return list.toArray(new Double[list.size()]);
}
@Override
public Object getBean() {
return Shape.this;
}
@Override
public String getName() {
return "cssDashArray";
}
@Override
public CssMetaData<Shape,Number[]> getCssMetaData() {
return StyleableProperties.STROKE_DASH_ARRAY;
}
};
}
return cssDashArray;
}
public boolean canSetType() {
return (type == null) || !type.isBound();
}
public boolean canSetWidth() {
return (width == null) || !width.isBound();
}
public boolean canSetLineJoin() {
return (lineJoin == null) || !lineJoin.isBound();
}
public boolean canSetLineCap() {
return (lineCap == null) || !lineCap.isBound();
}
public boolean canSetMiterLimit() {
return (miterLimit == null) || !miterLimit.isBound();
}
public boolean canSetDashOffset() {
return (dashOffset == null) || !dashOffset.isBound();
}
public boolean hasDashArray() {
return (dashArray != null);
}
private void invalidated(final CssMetaData<Shape, ?> propertyCssKey) {
NodeHelper.markDirty(Shape.this, DirtyBits.SHAPE_STROKEATTRS);
strokeAttributesDirty = true;
if (propertyCssKey != StyleableProperties.STROKE_DASH_OFFSET) {
NodeHelper.geomChanged(Shape.this);
}
}
}
public static Shape union(final Shape shape1, final Shape shape2) {
final Area result = shape1.getTransformedArea();
result.add(shape2.getTransformedArea());
return createFromGeomShape(result);
}
public static Shape subtract(final Shape shape1, final Shape shape2) {
final Area result = shape1.getTransformedArea();
result.subtract(shape2.getTransformedArea());
return createFromGeomShape(result);
}
public static Shape intersect(final Shape shape1, final Shape shape2) {
final Area result = shape1.getTransformedArea();
result.intersect(shape2.getTransformedArea());
return createFromGeomShape(result);
}
private Area getTransformedArea() {
return getTransformedArea(calculateNodeToSceneTransform(this));
}
private Area getTransformedArea(final BaseTransform transform) {
if (mode == NGShape.Mode.EMPTY) {
return new Area();
}
final com.sun.javafx.geom.Shape fillShape = ShapeHelper.configShape(this);
if ((mode == NGShape.Mode.FILL)
|| (mode == NGShape.Mode.STROKE_FILL)
&& (getStrokeType() == StrokeType.INSIDE)) {
return createTransformedArea(fillShape, transform);
}
final StrokeType strokeType = getStrokeType();
final double strokeWidth =
Utils.clampMin(getStrokeWidth(), MIN_STROKE_WIDTH);
final StrokeLineCap strokeLineCap = getStrokeLineCap();
final StrokeLineJoin strokeLineJoin = convertLineJoin(getStrokeLineJoin());
final float strokeMiterLimit =
(float) Utils.clampMin(getStrokeMiterLimit(),
MIN_STROKE_MITER_LIMIT);
final float[] dashArray =
(hasStrokeDashArray())
? toPGDashArray(getStrokeDashArray())
: DEFAULT_PG_STROKE_DASH_ARRAY;
final com.sun.javafx.geom.Shape strokeShape =
Toolkit.getToolkit().createStrokedShape(
fillShape, strokeType, strokeWidth, strokeLineCap,
strokeLineJoin, strokeMiterLimit,
dashArray, (float) getStrokeDashOffset());
if (mode == NGShape.Mode.STROKE) {
return createTransformedArea(strokeShape, transform);
}
final Area combinedArea = new Area(fillShape);
combinedArea.add(new Area(strokeShape));
return createTransformedArea(combinedArea, transform);
}
private static BaseTransform calculateNodeToSceneTransform(Node node) {
final Affine3D cumulativeTransformation = new Affine3D();
do {
cumulativeTransformation.preConcatenate(
NodeHelper.getLeafTransform(node));
node = node.getParent();
} while (node != null);
return cumulativeTransformation;
}
private static Area createTransformedArea(
final com.sun.javafx.geom.Shape geomShape,
final BaseTransform transform) {
return transform.isIdentity()
? new Area(geomShape)
: new Area(geomShape.getPathIterator(transform));
}
private static Path createFromGeomShape(
final com.sun.javafx.geom.Shape geomShape) {
final Path path = new Path();
final ObservableList<PathElement> elements = path.getElements();
final PathIterator iterator = geomShape.getPathIterator(null);
final float coords[] = new float[6];
while (!iterator.isDone()) {
final int segmentType = iterator.currentSegment(coords);
switch (segmentType) {
case PathIterator.SEG_MOVETO:
elements.add(new MoveTo(coords[0], coords[1]));
break;
case PathIterator.SEG_LINETO:
elements.add(new LineTo(coords[0], coords[1]));
break;
case PathIterator.SEG_QUADTO:
elements.add(new QuadCurveTo(coords[0], coords[1],
coords[2], coords[3]));
break;
case PathIterator.SEG_CUBICTO:
elements.add(new CubicCurveTo(coords[0], coords[1],
coords[2], coords[3],
coords[4], coords[5]));
break;
case PathIterator.SEG_CLOSE:
elements.add(new ClosePath());
break;
}
iterator.next();
}
path.setFillRule((iterator.getWindingRule()
== PathIterator.WIND_EVEN_ODD)
? FillRule.EVEN_ODD
: FillRule.NON_ZERO);
path.setFill(Color.BLACK);
path.setStroke(null);
return path;
}
}
