package javafx.scene.shape;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.scene.paint.Paint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.css.converter.SizeConverter;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.RoundRectangle2D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.RectangleHelper;
import com.sun.javafx.scene.shape.ShapeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGRectangle;
import com.sun.javafx.sg.prism.NGShape;
import javafx.scene.Node;
public class Rectangle extends Shape {
static {
RectangleHelper.setRectangleAccessor(new RectangleHelper.RectangleAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((Rectangle) node).doCreatePeer();
}
@Override
public void doUpdatePeer(Node node) {
((Rectangle) node).doUpdatePeer();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((Rectangle) node).doComputeGeomBounds(bounds, tx);
}
@Override
public com.sun.javafx.geom.Shape doConfigShape(Shape shape) {
return ((Rectangle) shape).doConfigShape();
}
});
}
private final RoundRectangle2D shape = new RoundRectangle2D();
private static final int NON_RECTILINEAR_TYPE_MASK = ~(
BaseTransform.TYPE_TRANSLATION |
BaseTransform.TYPE_MASK_SCALE |
BaseTransform.TYPE_QUADRANT_ROTATION |
BaseTransform.TYPE_FLIP);
{
RectangleHelper.initHelper(this);
}
public Rectangle() {
}
public Rectangle(double width, double height) {
setWidth(width);
setHeight(height);
}
public Rectangle(double width, double height, Paint fill) {
setWidth(width);
setHeight(height);
setFill(fill);
}
public Rectangle(double x, double y, double width, double height) {
this(width, height);
setX(x);
setY(y);
}
private DoubleProperty x;
public final void setX(double value) {
if (x != null || value != 0.0) {
xProperty().set(value);
}
}
public final double getX() {
return x == null ? 0.0 : x.get();
}
public final DoubleProperty xProperty() {
if (x == null) {
x = new DoublePropertyBase() {
@Override
public void invalidated() {
NodeHelper.markDirty(Rectangle.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(Rectangle.this);
}
@Override
public Object getBean() {
return Rectangle.this;
}
@Override
public String getName() {
return "x";
}
};
}
return x;
}
private DoubleProperty y;
public final void setY(double value) {
if (y != null || value != 0.0) {
yProperty().set(value);
}
}
public final double getY() {
return y == null ? 0.0 : y.get();
}
public final DoubleProperty yProperty() {
if (y == null) {
y = new DoublePropertyBase() {
@Override
public void invalidated() {
NodeHelper.markDirty(Rectangle.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(Rectangle.this);
}
@Override
public Object getBean() {
return Rectangle.this;
}
@Override
public String getName() {
return "y";
}
};
}
return y;
}
private final DoubleProperty width = new DoublePropertyBase() {
@Override
public void invalidated() {
NodeHelper.markDirty(Rectangle.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(Rectangle.this);
}
@Override
public Object getBean() {
return Rectangle.this;
}
@Override
public String getName() {
return "width";
}
};
public final void setWidth(double value) {
width.set(value);
}
public final double getWidth() {
return width.get();
}
public final DoubleProperty widthProperty() {
return width;
}
private final DoubleProperty height = new DoublePropertyBase() {
@Override
public void invalidated() {
NodeHelper.markDirty(Rectangle.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(Rectangle.this);
}
@Override
public Object getBean() {
return Rectangle.this;
}
@Override
public String getName() {
return "height";
}
};
public final void setHeight(double value) {
height.set(value);
}
public final double getHeight() {
return height.get();
}
public final DoubleProperty heightProperty() {
return height;
}
private DoubleProperty arcWidth;
public final void setArcWidth(double value) {
if (arcWidth != null || value != 0.0) {
arcWidthProperty().set(value);
}
}
public final double getArcWidth() {
return arcWidth == null ? 0.0 : arcWidth.get();
}
public final DoubleProperty arcWidthProperty() {
if (arcWidth == null) {
arcWidth = new StyleableDoubleProperty() {
@Override
public void invalidated() {
NodeHelper.markDirty(Rectangle.this, DirtyBits.NODE_GEOMETRY);
}
@Override
public CssMetaData<Rectangle, Number> getCssMetaData() {
return StyleableProperties.ARC_WIDTH;
}
@Override
public Object getBean() {
return Rectangle.this;
}
@Override
public String getName() {
return "arcWidth";
}
};
}
return arcWidth;
}
private DoubleProperty arcHeight;
public final void setArcHeight(double value) {
if (arcHeight != null || value != 0.0) {
arcHeightProperty().set(value);
}
}
public final double getArcHeight() {
return arcHeight == null ? 0.0 : arcHeight.get();
}
public final DoubleProperty arcHeightProperty() {
if (arcHeight == null) {
arcHeight = new StyleableDoubleProperty() {
@Override
public void invalidated() {
NodeHelper.markDirty(Rectangle.this, DirtyBits.NODE_GEOMETRY);
}
@Override
public CssMetaData<Rectangle, Number> getCssMetaData() {
return StyleableProperties.ARC_HEIGHT;
}
@Override
public Object getBean() {
return Rectangle.this;
}
@Override
public String getName() {
return "arcHeight";
}
};
}
return arcHeight;
}
private NGNode doCreatePeer() {
return new NGRectangle();
}
private static class StyleableProperties {
private static final CssMetaData<Rectangle,Number> ARC_HEIGHT =
new CssMetaData<Rectangle,Number>("-fx-arc-height",
SizeConverter.getInstance(), 0.0) {
@Override
public boolean isSettable(Rectangle node) {
return node.arcHeight == null || !node.arcHeight.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(Rectangle node) {
return (StyleableProperty<Number>)node.arcHeightProperty();
}
};
private static final CssMetaData<Rectangle,Number> ARC_WIDTH =
new CssMetaData<Rectangle,Number>("-fx-arc-width",
SizeConverter.getInstance(), 0.0) {
@Override
public boolean isSettable(Rectangle node) {
return node.arcWidth == null || !node.arcWidth.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(Rectangle node) {
return (StyleableProperty<Number>)node.arcWidthProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Shape.getClassCssMetaData());
styleables.add(ARC_HEIGHT);
styleables.add(ARC_WIDTH);
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
@Override StrokeLineJoin convertLineJoin(StrokeLineJoin t) {
if ((getArcWidth() > 0) && (getArcHeight() > 0)) {
return StrokeLineJoin.BEVEL;
}
return t;
}
private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) {
if (getMode() == NGShape.Mode.EMPTY) {
return bounds.makeEmpty();
}
if ((getArcWidth() > 0) && (getArcHeight() > 0)
&& ((tx.getType() & NON_RECTILINEAR_TYPE_MASK) != 0)) {
return computeShapeBounds(bounds, tx, ShapeHelper.configShape(this));
}
double upad;
double dpad;
if ((getMode() == NGShape.Mode.FILL) || (getStrokeType() == StrokeType.INSIDE)) {
upad = dpad = 0;
} else {
upad = getStrokeWidth();
if (getStrokeType() == StrokeType.CENTERED) {
upad /= 2.0;
}
dpad = 0.0f;
}
return computeBounds(bounds, tx, upad, dpad, getX(), getY(), getWidth(), getHeight());
}
private RoundRectangle2D doConfigShape() {
if ((getArcWidth() > 0) && (getArcHeight() > 0)) {
shape.setRoundRect((float)getX(), (float)getY(),
(float)getWidth(), (float)getHeight(),
(float)getArcWidth(), (float)getArcHeight());
} else {
shape.setRoundRect(
(float)getX(), (float)getY(),
(float)getWidth(), (float)getHeight(), 0, 0);
}
return shape;
}
private void doUpdatePeer() {
if (NodeHelper.isDirty(this, DirtyBits.NODE_GEOMETRY)) {
final NGRectangle peer = NodeHelper.getPeer(this);
peer.updateRectangle((float)getX(),
(float)getY(),
(float)getWidth(),
(float)getHeight(),
(float)getArcWidth(),
(float)getArcHeight());
}
}
@Override
public String toString() {
final StringBuilder sb = new StringBuilder("Rectangle[");
String id = getId();
if (id != null) {
sb.append("id=").append(id).append(", ");
}
sb.append("x=").append(getX());
sb.append(", y=").append(getY());
sb.append(", width=").append(getWidth());
sb.append(", height=").append(getHeight());
sb.append(", fill=").append(getFill());
Paint stroke = getStroke();
if (stroke != null) {
sb.append(", stroke=").append(stroke);
sb.append(", strokeWidth=").append(getStrokeWidth());
}
return sb.append("]").toString();
}
}
