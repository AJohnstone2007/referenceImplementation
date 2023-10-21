package javafx.scene.shape;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.scene.shape.QuadCurveToHelper;
import com.sun.javafx.sg.prism.NGPath;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
public class QuadCurveTo extends PathElement {
static {
QuadCurveToHelper.setQuadCurveToAccessor(new QuadCurveToHelper.QuadCurveToAccessor() {
@Override
public void doAddTo(PathElement pathElement, Path2D path) {
((QuadCurveTo) pathElement).doAddTo(path);
}
});
}
public QuadCurveTo() {
QuadCurveToHelper.initHelper(this);
}
public QuadCurveTo(double controlX, double controlY, double x, double y) {
setControlX(controlX);
setControlY(controlY);
setX(x);
setY(y);
QuadCurveToHelper.initHelper(this);
}
private DoubleProperty controlX = new DoublePropertyBase() {
@Override
public void invalidated() {
u();
}
@Override
public Object getBean() {
return QuadCurveTo.this;
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
u();
}
@Override
public Object getBean() {
return QuadCurveTo.this;
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
u();
}
@Override
public Object getBean() {
return QuadCurveTo.this;
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
u();
}
@Override
public Object getBean() {
return QuadCurveTo.this;
}
@Override
public String getName() {
return "y";
}
};
}
return y;
}
@Override
void addTo(NGPath pgPath) {
if (isAbsolute()) {
pgPath.addQuadTo(
(float)getControlX(),
(float)getControlY(),
(float)getX(),
(float)getY());
} else {
final double dx = pgPath.getCurrentX();
final double dy = pgPath.getCurrentY();
pgPath.addQuadTo(
(float)(getControlX()+dx),
(float)(getControlY()+dy),
(float)(getX()+dx),
(float)(getY()+dy));
}
}
private void doAddTo(Path2D path) {
if (isAbsolute()) {
path.quadTo(
(float)getControlX(),
(float)getControlY(),
(float)getX(),
(float)getY());
} else {
final double dx = path.getCurrentX();
final double dy = path.getCurrentY();
path.quadTo(
(float)(getControlX()+dx),
(float)(getControlY()+dy),
(float)(getX()+dx),
(float)(getY()+dy));
}
}
@Override
public String toString() {
final StringBuilder sb = new StringBuilder("CubicCurveTo[");
sb.append("x=").append(getX());
sb.append(", y=").append(getY());
sb.append(", controlX=").append(getControlX());
sb.append(", controlY=").append(getControlY());
return sb.append("]").toString();
}
}
