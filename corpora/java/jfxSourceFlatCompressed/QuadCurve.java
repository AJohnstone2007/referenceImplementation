package javafx.scene.shape;
import com.sun.javafx.geom.QuadCurve2D;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.QuadCurveHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGQuadCurve;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
public class QuadCurve extends Shape {
static {
QuadCurveHelper.setQuadCurveAccessor(new QuadCurveHelper.QuadCurveAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((QuadCurve) node).doCreatePeer();
}
@Override
public void doUpdatePeer(Node node) {
((QuadCurve) node).doUpdatePeer();
}
@Override
public com.sun.javafx.geom.Shape doConfigShape(Shape shape) {
return ((QuadCurve) shape).doConfigShape();
}
});
}
private final QuadCurve2D shape = new QuadCurve2D();
{
QuadCurveHelper.initHelper(this);
}
public QuadCurve() {
}
public QuadCurve(double startX, double startY, double controlX, double controlY, double endX, double endY) {
setStartX(startX);
setStartY(startY);
setControlX(controlX);
setControlY(controlY);
setEndX(endX);
setEndY(endY);
}
private DoubleProperty startX;
public final void setStartX(double value) {
if (startX != null || value != 0.0) {
startXProperty().set(value);
}
}
public final double getStartX() {
return startX == null ? 0.0 : startX.get();
}
public final DoubleProperty startXProperty() {
if (startX == null) {
startX = new DoublePropertyBase() {
@Override
public void invalidated() {
NodeHelper.markDirty(QuadCurve.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(QuadCurve.this);
}
@Override
public Object getBean() {
return QuadCurve.this;
}
@Override
public String getName() {
return "startX";
}
};
}
return startX;
}
private DoubleProperty startY;
public final void setStartY(double value) {
if (startY != null || value != 0.0) {
startYProperty().set(value);
}
}
public final double getStartY() {
return startY == null ? 0.0 : startY.get();
}
public final DoubleProperty startYProperty() {
if (startY == null) {
startY = new DoublePropertyBase() {
@Override
public void invalidated() {
NodeHelper.markDirty(QuadCurve.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(QuadCurve.this);
}
@Override
public Object getBean() {
return QuadCurve.this;
}
@Override
public String getName() {
return "startY";
}
};
}
return startY;
}
private DoubleProperty controlX = new DoublePropertyBase() {
@Override
public void invalidated() {
NodeHelper.markDirty(QuadCurve.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(QuadCurve.this);
}
@Override
public Object getBean() {
return QuadCurve.this;
}
@Override
public String getName() {
return "controlX";
}
};
public final void setControlX(double value) {
controlX.set(value);
}
public final double getControlX() {
return controlX.get();
}
public final DoubleProperty controlXProperty() {
return controlX;
}
private DoubleProperty controlY = new DoublePropertyBase() {
@Override
public void invalidated() {
NodeHelper.markDirty(QuadCurve.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(QuadCurve.this);
}
@Override
public Object getBean() {
return QuadCurve.this;
}
@Override
public String getName() {
return "controlY";
}
};
public final void setControlY(double value) {
controlY.set(value);
}
public final double getControlY() {
return controlY.get();
}
public final DoubleProperty controlYProperty() {
return controlY;
}
private DoubleProperty endX;
public final void setEndX(double value) {
if (endX != null || value != 0.0) {
endXProperty().set(value);
}
}
public final double getEndX() {
return endX == null ? 0.0 : endX.get();
}
public final DoubleProperty endXProperty() {
if (endX == null) {
endX = new DoublePropertyBase() {
@Override
public void invalidated() {
NodeHelper.markDirty(QuadCurve.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(QuadCurve.this);
}
@Override
public Object getBean() {
return QuadCurve.this;
}
@Override
public String getName() {
return "endX";
}
};
}
return endX;
}
private DoubleProperty endY;
public final void setEndY(double value) {
if (endY != null || value != 0.0) {
endYProperty().set(value);
}
}
public final double getEndY() {
return endY == null ? 0.0 : endY.get();
}
public final DoubleProperty endYProperty() {
if (endY == null) {
endY = new DoublePropertyBase() {
@Override
public void invalidated() {
NodeHelper.markDirty(QuadCurve.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(QuadCurve.this);
}
@Override
public Object getBean() {
return QuadCurve.this;
}
@Override
public String getName() {
return "endY";
}
};
}
return endY;
}
private NGNode doCreatePeer() {
return new NGQuadCurve();
}
private QuadCurve2D doConfigShape() {
shape.x1 = (float)getStartX();
shape.y1 = (float)getStartY();
shape.ctrlx = (float)getControlX();
shape.ctrly = (float)getControlY();
shape.x2 = (float)getEndX();
shape.y2 = (float)getEndY();
return shape;
}
private void doUpdatePeer() {
if (NodeHelper.isDirty(this, DirtyBits.NODE_GEOMETRY)) {
final NGQuadCurve peer = NodeHelper.getPeer(this);
peer.updateQuadCurve((float)getStartX(),
(float)getStartY(),
(float)getEndX(),
(float)getEndY(),
(float)getControlX(),
(float)getControlY());
}
}
@Override
public String toString() {
final StringBuilder sb = new StringBuilder("QuadCurve[");
String id = getId();
if (id != null) {
sb.append("id=").append(id).append(", ");
}
sb.append("startX=").append(getStartX());
sb.append(", startY=").append(getStartY());
sb.append(", controlX=").append(getControlX());
sb.append(", controlY=").append(getControlY());
sb.append(", endX=").append(getEndX());
sb.append(", endY=").append(getEndY());
sb.append(", fill=").append(getFill());
Paint stroke = getStroke();
if (stroke != null) {
sb.append(", stroke=").append(stroke);
sb.append(", strokeWidth=").append(getStrokeWidth());
}
return sb.append("]").toString();
}
}
