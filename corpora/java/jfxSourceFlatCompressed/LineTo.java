package javafx.scene.shape;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.scene.shape.CubicCurveToHelper;
import com.sun.javafx.scene.shape.LineToHelper;
import com.sun.javafx.sg.prism.NGPath;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
public class LineTo extends PathElement {
static {
LineToHelper.setLineToAccessor(new LineToHelper.LineToAccessor() {
@Override
public void doAddTo(PathElement pathElement, Path2D path) {
((LineTo) pathElement).doAddTo(path);
}
});
}
public LineTo() {
LineToHelper.initHelper(this);
}
public LineTo(double x, double y) {
setX(x);
setY(y);
LineToHelper.initHelper(this);
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
return LineTo.this;
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
return LineTo.this;
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
pgPath.addLineTo((float)getX(), (float)getY());
} else {
pgPath.addLineTo((float)(pgPath.getCurrentX() + getX()),
(float)(pgPath.getCurrentY() + getY()));
}
}
private void doAddTo(Path2D path) {
if (isAbsolute()) {
path.lineTo((float)getX(), (float)getY());
} else {
path.lineTo((float)(path.getCurrentX() + getX()),
(float)(path.getCurrentY() + getY()));
}
}
@Override
public String toString() {
final StringBuilder sb = new StringBuilder("LineTo[");
sb.append("x=").append(getX());
sb.append(", y=").append(getY());
return sb.append("]").toString();
}
}
