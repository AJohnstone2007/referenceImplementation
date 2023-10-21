package javafx.scene.shape;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.scene.shape.CubicCurveToHelper;
import com.sun.javafx.sg.prism.NGPath;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
public class CubicCurveTo extends PathElement {
static {
CubicCurveToHelper.setCubicCurveToAccessor(new CubicCurveToHelper.CubicCurveToAccessor() {
@Override
public void doAddTo(PathElement pathElement, Path2D path) {
((CubicCurveTo) pathElement).doAddTo(path);
}
});
}
public CubicCurveTo() {
CubicCurveToHelper.initHelper(this);
}
public CubicCurveTo(double controlX1, double controlY1, double controlX2,
double controlY2, double x, double y)
{
setControlX1(controlX1);
setControlY1(controlY1);
setControlX2(controlX2);
setControlY2(controlY2);
setX(x);
setY(y);
CubicCurveToHelper.initHelper(this);
}
private DoubleProperty controlX1;
public final void setControlX1(double value) {
if (controlX1 != null || value != 0.0) {
controlX1Property().set(value);
}
}
public final double getControlX1() {
return controlX1 == null ? 0.0 : controlX1.get();
}
public final DoubleProperty controlX1Property() {
if (controlX1 == null) {
controlX1 = new DoublePropertyBase() {
@Override
public void invalidated() {
u();
}
@Override
public Object getBean() {
return CubicCurveTo.this;
}
@Override
public String getName() {
return "controlX1";
}
};
}
return controlX1;
}
private DoubleProperty controlY1;
public final void setControlY1(double value) {
if (controlY1 != null || value != 0.0) {
controlY1Property().set(value);
}
}
public final double getControlY1() {
return controlY1 == null ? 0.0 : controlY1.get();
}
public final DoubleProperty controlY1Property() {
if (controlY1 == null) {
controlY1 = new DoublePropertyBase() {
@Override
public void invalidated() {
u();
}
@Override
public Object getBean() {
return CubicCurveTo.this;
}
@Override
public String getName() {
return "controlY1";
}
};
}
return controlY1;
}
private DoubleProperty controlX2;
public final void setControlX2(double value) {
if (controlX2 != null || value != 0.0) {
controlX2Property().set(value);
}
}
public final double getControlX2() {
return controlX2 == null ? 0.0 : controlX2.get();
}
public final DoubleProperty controlX2Property() {
if (controlX2 == null) {
controlX2 = new DoublePropertyBase() {
@Override
public void invalidated() {
u();
}
@Override
public Object getBean() {
return CubicCurveTo.this;
}
@Override
public String getName() {
return "controlX2";
}
};
}
return controlX2;
}
private DoubleProperty controlY2;
public final void setControlY2(double value) {
if (controlY2 != null || value != 0.0) {
controlY2Property().set(value);
}
}
public final double getControlY2() {
return controlY2 == null ? 0.0 : controlY2.get();
}
public final DoubleProperty controlY2Property() {
if (controlY2 == null) {
controlY2 = new DoublePropertyBase() {
@Override
public void invalidated() {
u();
}
@Override
public Object getBean() {
return CubicCurveTo.this;
}
@Override
public String getName() {
return "controlY2";
}
};
}
return controlY2;
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
return CubicCurveTo.this;
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
return CubicCurveTo.this;
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
pgPath.addCubicTo((float)getControlX1(), (float)getControlY1(),
(float)getControlX2(), (float)getControlY2(),
(float)getX(), (float)getY());
} else {
final double dx = pgPath.getCurrentX();
final double dy = pgPath.getCurrentY();
pgPath.addCubicTo((float)(getControlX1()+dx), (float)(getControlY1()+dy),
(float)(getControlX2()+dx), (float)(getControlY2()+dy),
(float)(getX()+dx), (float)(getY()+dy));
}
}
private void doAddTo(Path2D path) {
if (isAbsolute()) {
path.curveTo((float)getControlX1(), (float)getControlY1(),
(float)getControlX2(), (float)getControlY2(),
(float)getX(), (float)getY());
} else {
final double dx = path.getCurrentX();
final double dy = path.getCurrentY();
path.curveTo((float)(getControlX1()+dx), (float)(getControlY1()+dy),
(float)(getControlX2()+dx), (float)(getControlY2()+dy),
(float)(getX()+dx), (float)(getY()+dy));
}
}
@Override
public String toString() {
final StringBuilder sb = new StringBuilder("CubicCurveTo[");
sb.append("x=").append(getX());
sb.append(", y=").append(getY());
sb.append(", controlX1=").append(getControlX1());
sb.append(", controlY1=").append(getControlY1());
sb.append(", controlX2=").append(getControlX2());
sb.append(", controlY2=").append(getControlY2());
return sb.append("]").toString();
}
}
