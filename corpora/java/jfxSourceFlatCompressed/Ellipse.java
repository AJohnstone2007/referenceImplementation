package javafx.scene.shape;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Ellipse2D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.EllipseHelper;
import com.sun.javafx.scene.shape.ShapeHelper;
import com.sun.javafx.sg.prism.NGEllipse;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGShape;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
public class Ellipse extends Shape {
static {
EllipseHelper.setEllipseAccessor(new EllipseHelper.EllipseAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((Ellipse) node).doCreatePeer();
}
@Override
public void doUpdatePeer(Node node) {
((Ellipse) node).doUpdatePeer();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((Ellipse) node).doComputeGeomBounds(bounds, tx);
}
@Override
public com.sun.javafx.geom.Shape doConfigShape(Shape shape) {
return ((Ellipse) shape).doConfigShape();
}
});
}
private final Ellipse2D shape = new Ellipse2D();
private static final int NON_RECTILINEAR_TYPE_MASK = ~(
BaseTransform.TYPE_TRANSLATION |
BaseTransform.TYPE_QUADRANT_ROTATION |
BaseTransform.TYPE_MASK_SCALE |
BaseTransform.TYPE_FLIP);
{
EllipseHelper.initHelper(this);
}
public Ellipse() {
}
public Ellipse(double radiusX, double radiusY) {
setRadiusX(radiusX);
setRadiusY(radiusY);
}
public Ellipse(double centerX, double centerY, double radiusX, double radiusY) {
this(radiusX, radiusY);
setCenterX(centerX);
setCenterY(centerY);
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
centerX = new DoublePropertyBase() {
@Override
public void invalidated() {
NodeHelper.markDirty(Ellipse.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(Ellipse.this);
}
@Override
public Object getBean() {
return Ellipse.this;
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
centerY = new DoublePropertyBase() {
@Override
public void invalidated() {
NodeHelper.markDirty(Ellipse.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(Ellipse.this);
}
@Override
public Object getBean() {
return Ellipse.this;
}
@Override
public String getName() {
return "centerY";
}
};
}
return centerY;
}
private final DoubleProperty radiusX = new DoublePropertyBase() {
@Override
public void invalidated() {
NodeHelper.markDirty(Ellipse.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(Ellipse.this);
}
@Override
public Object getBean() {
return Ellipse.this;
}
@Override
public String getName() {
return "radiusX";
}
};
public final void setRadiusX(double value) {
radiusX.set(value);
}
public final double getRadiusX() {
return radiusX.get();
}
public final DoubleProperty radiusXProperty() {
return radiusX;
}
private final DoubleProperty radiusY = new DoublePropertyBase() {
@Override
public void invalidated() {
NodeHelper.markDirty(Ellipse.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(Ellipse.this);
}
@Override
public Object getBean() {
return Ellipse.this;
}
@Override
public String getName() {
return "radiusY";
}
};
public final void setRadiusY(double value) {
radiusY.set(value);
}
public final double getRadiusY() {
return radiusY.get();
}
public final DoubleProperty radiusYProperty() {
return radiusY;
}
private NGNode doCreatePeer() {
return new NGEllipse();
}
@Override StrokeLineJoin convertLineJoin(StrokeLineJoin t) {
return StrokeLineJoin.BEVEL;
}
private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) {
if (getMode() == NGShape.Mode.EMPTY) {
return bounds.makeEmpty();
}
if ((tx.getType() & NON_RECTILINEAR_TYPE_MASK) != 0) {
return computeShapeBounds(bounds, tx, ShapeHelper.configShape(this));
}
final double x = getCenterX() - getRadiusX();
final double y = getCenterY() - getRadiusY();
final double width = 2.0f * getRadiusX();
final double height = 2.0f * getRadiusY();
double upad;
double dpad;
if (getMode() == NGShape.Mode.FILL || getStrokeType() == StrokeType.INSIDE) {
upad = dpad = 0.0f;
} else {
upad = getStrokeWidth();
if (getStrokeType() == StrokeType.CENTERED) {
upad /= 2.0f;
}
dpad = 0.0f;
}
return computeBounds(bounds, tx, upad, dpad, x, y, width, height);
}
private Ellipse2D doConfigShape() {
shape.setFrame(
(float)(getCenterX() - getRadiusX()),
(float)(getCenterY() - getRadiusY()),
(float)(getRadiusX() * 2.0),
(float)(getRadiusY() * 2.0));
return shape;
}
private void doUpdatePeer() {
if (NodeHelper.isDirty(this, DirtyBits.NODE_GEOMETRY)) {
NGEllipse peer = NodeHelper.getPeer(this);
peer.updateEllipse((float)getCenterX(),
(float)getCenterY(),
(float)getRadiusX(),
(float)getRadiusY());
}
}
@Override
public String toString() {
final StringBuilder sb = new StringBuilder("Ellipse[");
String id = getId();
if (id != null) {
sb.append("id=").append(id).append(", ");
}
sb.append("centerX=").append(getCenterX());
sb.append(", centerY=").append(getCenterY());
sb.append(", radiusX=").append(getRadiusX());
sb.append(", radiusY=").append(getRadiusY());
sb.append(", fill=").append(getFill());
Paint stroke = getStroke();
if (stroke != null) {
sb.append(", stroke=").append(stroke);
sb.append(", strokeWidth=").append(getStrokeWidth());
}
return sb.append("]").toString();
}
}
