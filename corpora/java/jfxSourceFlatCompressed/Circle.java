package javafx.scene.shape;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Ellipse2D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.CircleHelper;
import com.sun.javafx.scene.shape.ShapeHelper;
import com.sun.javafx.sg.prism.NGCircle;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGShape;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
public class Circle extends Shape {
static {
CircleHelper.setCircleAccessor(new CircleHelper.CircleAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((Circle) node).doCreatePeer();
}
@Override
public void doUpdatePeer(Node node) {
((Circle) node).doUpdatePeer();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((Circle) node).doComputeGeomBounds(bounds, tx);
}
@Override
public com.sun.javafx.geom.Shape doConfigShape(Shape shape) {
return ((Circle) shape).doConfigShape();
}
});
}
private final Ellipse2D shape = new Ellipse2D();
{
CircleHelper.initHelper(this);
}
public Circle(double radius) {
setRadius(radius);
}
public Circle(double radius, Paint fill) {
setRadius(radius);
setFill(fill);
}
public Circle() {
}
public Circle(double centerX, double centerY, double radius) {
setCenterX(centerX);
setCenterY(centerY);
setRadius(radius);
}
public Circle(double centerX, double centerY, double radius, Paint fill) {
setCenterX(centerX);
setCenterY(centerY);
setRadius(radius);
setFill(fill);
}
private DoubleProperty centerX;
public final void setCenterX(double value) {
if (centerX != null || value != 0.0) {
centerXProperty().set(value);
}
}
public final double getCenterX() {
return centerX == null ? 0.0 : centerX.get();
}
public final DoubleProperty centerXProperty() {
if (centerX == null) {
centerX = new DoublePropertyBase(0.0) {
@Override
public void invalidated() {
NodeHelper.markDirty(Circle.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(Circle.this);
}
@Override
public Object getBean() {
return Circle.this;
}
@Override
public String getName() {
return "centerX";
}
};
}
return centerX;
}
private DoubleProperty centerY;
public final void setCenterY(double value) {
if (centerY != null || value != 0.0) {
centerYProperty().set(value);
}
}
public final double getCenterY() {
return centerY == null ? 0.0 : centerY.get();
}
public final DoubleProperty centerYProperty() {
if (centerY == null) {
centerY = new DoublePropertyBase(0.0) {
@Override
public void invalidated() {
NodeHelper.markDirty(Circle.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(Circle.this);
}
@Override
public Object getBean() {
return Circle.this;
}
@Override
public String getName() {
return "centerY";
}
};
}
return centerY;
}
private final DoubleProperty radius = new DoublePropertyBase() {
@Override
public void invalidated() {
NodeHelper.markDirty(Circle.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(Circle.this);
}
@Override
public Object getBean() {
return Circle.this;
}
@Override
public String getName() {
return "radius";
}
};
public final void setRadius(double value) {
radius.set(value);
}
public final double getRadius() {
return radius.get();
}
public final DoubleProperty radiusProperty() {
return radius;
}
@Override StrokeLineJoin convertLineJoin(StrokeLineJoin t) {
return StrokeLineJoin.BEVEL;
}
private NGNode doCreatePeer() {
return new NGCircle();
}
private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) {
if (getMode() == NGShape.Mode.EMPTY) {
return bounds.makeEmpty();
}
final double cX = getCenterX();
final double cY = getCenterY();
if ((tx.getType() & ~(BaseTransform.TYPE_MASK_ROTATION | BaseTransform.TYPE_TRANSLATION)) == 0) {
double tCX = cX * tx.getMxx() + cY * tx.getMxy() + tx.getMxt();
double tCY = cX * tx.getMyx() + cY * tx.getMyy() + tx.getMyt();
double r = getRadius();
if (getMode() != NGShape.Mode.FILL && getStrokeType() != StrokeType.INSIDE) {
double upad = getStrokeWidth();
if (getStrokeType() == StrokeType.CENTERED) {
upad /= 2.0f;
}
r += upad;
}
return bounds.deriveWithNewBounds((float) (tCX - r), (float) (tCY - r), 0,
(float) (tCX + r), (float) (tCY + r), 0);
} else if ((tx.getType() & ~(BaseTransform.TYPE_MASK_SCALE | BaseTransform.TYPE_TRANSLATION | BaseTransform.TYPE_FLIP)) == 0) {
final double r = getRadius();
final double x = getCenterX() - r;
final double y = getCenterY() - r;
final double width = 2.0 * r;
final double height = width;
double upad;
if (getMode() == NGShape.Mode.FILL || getStrokeType() == StrokeType.INSIDE) {
upad = 0.0f;
} else {
upad = getStrokeWidth();
}
return computeBounds(bounds, tx, upad, 0, x, y, width, height);
}
return computeShapeBounds(bounds, tx, ShapeHelper.configShape(this));
}
private Ellipse2D doConfigShape() {
double r = getRadius();
shape.setFrame(
(float)(getCenterX() - r),
(float)(getCenterY() - r),
(float)(r * 2.0),
(float)(r * 2.0));
return shape;
}
private void doUpdatePeer() {
if (NodeHelper.isDirty(this, DirtyBits.NODE_GEOMETRY)) {
final NGCircle peer = NodeHelper.getPeer(this);
peer.updateCircle((float)getCenterX(),
(float)getCenterY(),
(float)getRadius());
}
}
@Override
public String toString() {
final StringBuilder sb = new StringBuilder("Circle[");
String id = getId();
if (id != null) {
sb.append("id=").append(id).append(", ");
}
sb.append("centerX=").append(getCenterX());
sb.append(", centerY=").append(getCenterY());
sb.append(", radius=").append(getRadius());
sb.append(", fill=").append(getFill());
Paint stroke = getStroke();
if (stroke != null) {
sb.append(", stroke=").append(stroke);
sb.append(", strokeWidth=").append(getStrokeWidth());
}
return sb.append("]").toString();
}
}
