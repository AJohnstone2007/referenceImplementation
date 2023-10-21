package javafx.scene.shape;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.scene.shape.VLineToHelper;
import com.sun.javafx.sg.prism.NGPath;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
public class VLineTo extends PathElement {
static {
VLineToHelper.setVLineToAccessor(new VLineToHelper.VLineToAccessor() {
@Override
public void doAddTo(PathElement pathElement, Path2D path) {
((VLineTo) pathElement).doAddTo(path);
}
});
}
public VLineTo() {
VLineToHelper.initHelper(this);
}
public VLineTo(double y) {
setY(y);
VLineToHelper.initHelper(this);
}
private DoubleProperty y = new DoublePropertyBase() {
@Override
public void invalidated() {
u();
}
@Override
public Object getBean() {
return VLineTo.this;
}
@Override
public String getName() {
return "y";
}
};
public final void setY(double value) {
y.set(value);
}
public final double getY() {
return y.get();
}
public final DoubleProperty yProperty() {
return y;
}
@Override
void addTo(NGPath pgPath) {
if (isAbsolute()) {
pgPath.addLineTo(pgPath.getCurrentX(), (float)getY());
} else {
pgPath.addLineTo(pgPath.getCurrentX(), (float)(pgPath.getCurrentY() + getY()));
}
}
private void doAddTo(Path2D path) {
if (isAbsolute()) {
path.lineTo(path.getCurrentX(), (float)getY());
} else {
path.lineTo(path.getCurrentX(), (float)(path.getCurrentY() + getY()));
}
}
@Override
public String toString() {
final StringBuilder sb = new StringBuilder("VLineTo[");
sb.append("y=").append(getY());
return sb.append("]").toString();
}
}
