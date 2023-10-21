package javafx.scene.shape;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.scene.shape.HLineToHelper;
import com.sun.javafx.sg.prism.NGPath;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
public class HLineTo extends PathElement {
static {
HLineToHelper.setHLineToAccessor(new HLineToHelper.HLineToAccessor() {
@Override
public void doAddTo(PathElement pathElement, Path2D path) {
((HLineTo) pathElement).doAddTo(path);
}
});
}
public HLineTo() {
HLineToHelper.initHelper(this);
}
public HLineTo(double x) {
setX(x);
HLineToHelper.initHelper(this);
}
private DoubleProperty x = new DoublePropertyBase() {
@Override
public void invalidated() {
u();
}
@Override
public Object getBean() {
return HLineTo.this;
}
@Override
public String getName() {
return "x";
}
};
public final void setX(double value) {
x.set(value);
}
public final double getX() {
return x.get();
}
public final DoubleProperty xProperty() {
return x;
}
@Override
void addTo(NGPath pgPath) {
if (isAbsolute()) {
pgPath.addLineTo((float)getX(), pgPath.getCurrentY());
} else {
pgPath.addLineTo((float)(pgPath.getCurrentX() + getX()), pgPath.getCurrentY());
}
}
private void doAddTo(Path2D path) {
if (isAbsolute()) {
path.lineTo((float)getX(), path.getCurrentY());
} else {
path.lineTo((float)(path.getCurrentX() + getX()), path.getCurrentY());
}
}
@Override
public String toString() {
final StringBuilder sb = new StringBuilder("HLineTo[");
sb.append("x=").append(getX());
return sb.append("]").toString();
}
}
