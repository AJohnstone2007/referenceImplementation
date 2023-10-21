package javafx.scene.shape;
import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.PolylineHelper;
import com.sun.javafx.scene.shape.ShapeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGPolyline;
import com.sun.javafx.sg.prism.NGShape;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.css.StyleableProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
public class Polyline extends Shape {
static {
PolylineHelper.setPolylineAccessor(new PolylineHelper.PolylineAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((Polyline) node).doCreatePeer();
}
@Override
public void doUpdatePeer(Node node) {
((Polyline) node).doUpdatePeer();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((Polyline) node).doComputeGeomBounds(bounds, tx);
}
@Override
public Paint doCssGetFillInitialValue(Shape shape) {
return ((Polyline) shape).doCssGetFillInitialValue();
}
@Override
public Paint doCssGetStrokeInitialValue(Shape shape) {
return ((Polyline) shape).doCssGetStrokeInitialValue();
}
@Override
public com.sun.javafx.geom.Shape doConfigShape(Shape shape) {
return ((Polyline) shape).doConfigShape();
}
});
}
private final Path2D shape = new Path2D();
{
PolylineHelper.initHelper(this);
((StyleableProperty)fillProperty()).applyStyle(null, null);
((StyleableProperty)strokeProperty()).applyStyle(null, Color.BLACK);
}
public Polyline() {
}
public Polyline(double... points) {
if (points != null) {
for (double p : points) {
this.getPoints().add(p);
}
}
}
private final ObservableList<Double> points = new TrackableObservableList<Double>() {
@Override
protected void onChanged(Change<Double> c) {
NodeHelper.markDirty(Polyline.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(Polyline.this);
}
};
public final ObservableList<Double> getPoints() { return points; }
private NGNode doCreatePeer() {
return new NGPolyline();
}
private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) {
if (getMode() == NGShape.Mode.EMPTY || getPoints().size() <= 1) {
return bounds.makeEmpty();
}
if (getPoints().size() == 2) {
if (getMode() == NGShape.Mode.FILL || getStrokeType() == StrokeType.INSIDE) {
return bounds.makeEmpty();
}
double upad = getStrokeWidth();
if (getStrokeType() == StrokeType.CENTERED) {
upad /= 2.0f;
}
return computeBounds(bounds, tx, upad, 0.5f,
getPoints().get(0), getPoints().get(1), 0.0f, 0.0f);
} else {
return computeShapeBounds(bounds, tx, ShapeHelper.configShape(this));
}
}
private Path2D doConfigShape() {
double p1 = getPoints().get(0);
double p2 = getPoints().get(1);
shape.reset();
shape.moveTo((float)p1, (float)p2);
final int numValidPoints = getPoints().size() & ~1;
for (int i = 2; i < numValidPoints; i += 2) {
p1 = getPoints().get(i); p2 = getPoints().get(i+1);
shape.lineTo((float)p1, (float)p2);
}
return shape;
}
private void doUpdatePeer() {
if (NodeHelper.isDirty(this, DirtyBits.NODE_GEOMETRY)) {
final int numValidPoints = getPoints().size() & ~1;
float points_array[] = new float[numValidPoints];
for (int i = 0; i < numValidPoints; i++) {
points_array[i] = (float)getPoints().get(i).doubleValue();
}
final NGPolyline peer = NodeHelper.getPeer(this);
peer.updatePolyline(points_array);
}
}
private Paint doCssGetFillInitialValue() {
return null;
}
private Paint doCssGetStrokeInitialValue() {
return Color.BLACK;
}
@Override
public String toString() {
final StringBuilder sb = new StringBuilder("Polyline[");
String id = getId();
if (id != null) {
sb.append("id=").append(id).append(", ");
}
sb.append("points=").append(getPoints());
sb.append(", fill=").append(getFill());
Paint stroke = getStroke();
if (stroke != null) {
sb.append(", stroke=").append(stroke);
sb.append(", strokeWidth=").append(getStrokeWidth());
}
return sb.append("]").toString();
}
}
