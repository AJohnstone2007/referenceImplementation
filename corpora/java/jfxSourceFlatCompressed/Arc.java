package javafx.scene.shape;
import com.sun.javafx.geom.Arc2D;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.ArcHelper;
import com.sun.javafx.sg.prism.NGArc;
import com.sun.javafx.sg.prism.NGNode;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
public class Arc extends Shape {
static {
ArcHelper.setArcAccessor(new ArcHelper.ArcAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((Arc) node).doCreatePeer();
}
@Override
public void doUpdatePeer(Node node) {
((Arc) node).doUpdatePeer();
}
@Override
public com.sun.javafx.geom.Shape doConfigShape(Shape shape) {
return ((Arc) shape).doConfigShape();
}
});
}
private final Arc2D shape = new Arc2D();
{
ArcHelper.initHelper(this);
}
public Arc() {
}
public Arc(double centerX, double centerY, double radiusX, double radiusY, double startAngle, double length) {
setCenterX(centerX);
setCenterY(centerY);
setRadiusX(radiusX);
setRadiusY(radiusY);
setStartAngle(startAngle);
setLength(length);
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
NodeHelper.markDirty(Arc.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(Arc.this);
}
@Override
public Object getBean() {
return Arc.this;
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
NodeHelper.markDirty(Arc.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(Arc.this);
}
@Override
public Object getBean() {
return Arc.this;
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
NodeHelper.markDirty(Arc.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(Arc.this);
}
@Override
public Object getBean() {
return Arc.this;
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
NodeHelper.markDirty(Arc.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(Arc.this);
}
@Override
public Object getBean() {
return Arc.this;
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
private DoubleProperty startAngle;
public final void setStartAngle(double value) {
if (startAngle != null || value != 0.0) {
startAngleProperty().set(value);
}
}
public final double getStartAngle() {
return startAngle == null ? 0.0 : startAngle.get();
}
public final DoubleProperty startAngleProperty() {
if (startAngle == null) {
startAngle = new DoublePropertyBase() {
@Override
public void invalidated() {
NodeHelper.markDirty(Arc.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(Arc.this);
}
@Override
public Object getBean() {
return Arc.this;
}
@Override
public String getName() {
return "startAngle";
}
};
}
return startAngle;
}
private final DoubleProperty length = new DoublePropertyBase() {
@Override
public void invalidated() {
NodeHelper.markDirty(Arc.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(Arc.this);
}
@Override
public Object getBean() {
return Arc.this;
}
@Override
public String getName() {
return "length";
}
};
public final void setLength(double value) {
length.set(value);
}
public final double getLength() {
return length.get();
}
public final DoubleProperty lengthProperty() {
return length;
}
private ObjectProperty<ArcType> type;
public final void setType(ArcType value) {
if (type != null || value != ArcType.OPEN) {
typeProperty().set(value);
}
}
public final ArcType getType() {
return type == null ? ArcType.OPEN : type.get();
}
public final ObjectProperty<ArcType> typeProperty() {
if (type == null) {
type = new ObjectPropertyBase<ArcType>(ArcType.OPEN) {
@Override
public void invalidated() {
NodeHelper.markDirty(Arc.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(Arc.this);
}
@Override
public Object getBean() {
return Arc.this;
}
@Override
public String getName() {
return "type";
}
};
}
return type;
}
private NGNode doCreatePeer() {
return new NGArc();
}
private Arc2D doConfigShape() {
short tmpType;
switch (getTypeInternal()) {
case OPEN:
tmpType = 0;
break;
case CHORD:
tmpType = 1;
break;
default:
tmpType = 2;
break;
}
shape.setArc(
(float)(getCenterX() - getRadiusX()),
(float)(getCenterY() - getRadiusY()),
(float)(getRadiusX() * 2.0),
(float)(getRadiusY() * 2.0),
(float)getStartAngle(),
(float)getLength(),
tmpType);
return shape;
}
private final ArcType getTypeInternal() {
ArcType t = getType();
return t == null ? ArcType.OPEN : t;
}
private void doUpdatePeer() {
if (NodeHelper.isDirty(this, DirtyBits.NODE_GEOMETRY)) {
final NGArc peer = NodeHelper.getPeer(this);
peer.updateArc((float)getCenterX(),
(float)getCenterY(),
(float)getRadiusX(),
(float)getRadiusY(),
(float)getStartAngle(),
(float)getLength(),
getTypeInternal());
}
}
@Override
public String toString() {
final StringBuilder sb = new StringBuilder("Arc[");
String id = getId();
if (id != null) {
sb.append("id=").append(id).append(", ");
}
sb.append("centerX=").append(getCenterX());
sb.append(", centerY=").append(getCenterY());
sb.append(", radiusX=").append(getRadiusX());
sb.append(", radiusY=").append(getRadiusY());
sb.append(", startAngle=").append(getStartAngle());
sb.append(", length=").append(getLength());
sb.append(", type=").append(getType());
sb.append(", fill=").append(getFill());
Paint stroke = getStroke();
if (stroke != null) {
sb.append(", stroke=").append(stroke);
sb.append(", strokeWidth=").append(getStrokeWidth());
}
return sb.append("]").toString();
}
}
