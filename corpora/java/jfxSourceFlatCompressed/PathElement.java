package javafx.scene.shape;
import com.sun.javafx.util.WeakReferenceQueue;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.scene.shape.PathElementHelper;
import com.sun.javafx.sg.prism.NGPath;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.scene.Node;
import java.util.Iterator;
public abstract class PathElement {
private PathElementHelper pathElementHelper = null;
static {
PathElementHelper.setPathElementAccessor(new PathElementHelper.PathElementAccessor() {
@Override
public PathElementHelper getHelper(PathElement pathElement) {
return pathElement.pathElementHelper;
}
@Override
public void setHelper(PathElement pathElement, PathElementHelper pathElementHelper) {
pathElement.pathElementHelper = pathElementHelper;
}
});
}
WeakReferenceQueue nodes = new WeakReferenceQueue();
public PathElement() {
}
void addNode(final Node n) {
nodes.add(n);
}
void removeNode(final Node n) {
nodes.remove(n);
}
void u() {
final Iterator iterator = nodes.iterator();
while (iterator.hasNext()) {
((Path) iterator.next()).markPathDirty();
}
}
abstract void addTo(NGPath pgPath);
private BooleanProperty absolute;
public final void setAbsolute(boolean value) {
absoluteProperty().set(value);
}
public final boolean isAbsolute() {
return absolute == null || absolute.get();
}
public final BooleanProperty absoluteProperty() {
if (absolute == null) {
absolute = new BooleanPropertyBase(true) {
@Override protected void invalidated() {
u();
}
@Override
public Object getBean() {
return PathElement.this;
}
@Override
public String getName() {
return "absolute";
}
};
}
return absolute;
}
}
