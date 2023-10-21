package javafx.scene.shape;
import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.PathElementHelper;
import com.sun.javafx.scene.shape.PathHelper;
import com.sun.javafx.scene.shape.PathUtils;
import com.sun.javafx.scene.shape.ShapeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGPath;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.css.StyleableProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.util.Collection;
import java.util.List;
import javafx.scene.Node;
public class Path extends Shape {
static {
PathHelper.setPathAccessor(new PathHelper.PathAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((Path) node).doCreatePeer();
}
@Override
public void doUpdatePeer(Node node) {
((Path) node).doUpdatePeer();
}
@Override
public Bounds doComputeLayoutBounds(Node node) {
return ((Path) node).doComputeLayoutBounds();
}
@Override
public Paint doCssGetFillInitialValue(Shape shape) {
return ((Path) shape).doCssGetFillInitialValue();
}
@Override
public Paint doCssGetStrokeInitialValue(Shape shape) {
return ((Path) shape).doCssGetStrokeInitialValue();
}
@Override
public com.sun.javafx.geom.Shape doConfigShape(Shape shape) {
return ((Path) shape).doConfigShape();
}
});
}
private Path2D path2d = null;
{
PathHelper.initHelper(this);
((StyleableProperty)fillProperty()).applyStyle(null, null);
((StyleableProperty)strokeProperty()).applyStyle(null, Color.BLACK);
}
public Path() {
}
public Path(PathElement... elements) {
if (elements != null) {
this.elements.addAll(elements);
}
}
public Path(Collection<? extends PathElement> elements) {
if (elements != null) {
this.elements.addAll(elements);
}
}
void markPathDirty() {
path2d = null;
NodeHelper.markDirty(this, DirtyBits.NODE_CONTENTS);
NodeHelper.geomChanged(this);
}
private ObjectProperty<FillRule> fillRule;
public final void setFillRule(FillRule value) {
if (fillRule != null || value != FillRule.NON_ZERO) {
fillRuleProperty().set(value);
}
}
public final FillRule getFillRule() {
return fillRule == null ? FillRule.NON_ZERO : fillRule.get();
}
public final ObjectProperty<FillRule> fillRuleProperty() {
if (fillRule == null) {
fillRule = new ObjectPropertyBase<FillRule>(FillRule.NON_ZERO) {
@Override
public void invalidated() {
NodeHelper.markDirty(Path.this, DirtyBits.NODE_CONTENTS);
NodeHelper.geomChanged(Path.this);
}
@Override
public Object getBean() {
return Path.this;
}
@Override
public String getName() {
return "fillRule";
}
};
}
return fillRule;
}
private boolean isPathValid;
private final ObservableList<PathElement> elements = new TrackableObservableList<PathElement>() {
@Override
protected void onChanged(Change<PathElement> c) {
List<PathElement> list = c.getList();
boolean firstElementChanged = false;
while (c.next()) {
List<PathElement> removed = c.getRemoved();
for (int i = 0; i < c.getRemovedSize(); ++i) {
removed.get(i).removeNode(Path.this);
}
for (int i = c.getFrom(); i < c.getTo(); ++i) {
list.get(i).addNode(Path.this);
}
firstElementChanged |= c.getFrom() == 0;
}
if (path2d != null) {
c.reset();
c.next();
if (c.getFrom() == c.getList().size() && !c.wasRemoved() && c.wasAdded()) {
for (int i = c.getFrom(); i < c.getTo(); ++i) {
PathElementHelper.addTo(list.get(i), path2d);
}
} else {
path2d = null;
}
}
if (firstElementChanged) {
isPathValid = isFirstPathElementValid();
}
NodeHelper.markDirty(Path.this, DirtyBits.NODE_CONTENTS);
NodeHelper.geomChanged(Path.this);
}
};
public final ObservableList<PathElement> getElements() { return elements; }
private NGNode doCreatePeer() {
return new NGPath();
}
private Path2D doConfigShape() {
if (isPathValid) {
if (path2d == null) {
path2d = PathUtils.configShape(getElements(), getFillRule() == FillRule.EVEN_ODD);
} else {
path2d.setWindingRule(getFillRule() == FillRule.NON_ZERO ?
Path2D.WIND_NON_ZERO : Path2D.WIND_EVEN_ODD);
}
return path2d;
} else {
return new Path2D();
}
}
private Bounds doComputeLayoutBounds() {
if (isPathValid) {
return null;
}
return new BoundingBox(0, 0, -1, -1);
}
private boolean isFirstPathElementValid() {
ObservableList<PathElement> _elements = getElements();
if (_elements != null && _elements.size() > 0) {
PathElement firstElement = _elements.get(0);
if (!firstElement.isAbsolute()) {
System.err.printf("First element of the path can not be relative. Path: %s\n", this);
return false;
} else if (firstElement instanceof MoveTo) {
return true;
} else {
System.err.printf("Missing initial moveto in path definition. Path: %s\n", this);
return false;
}
}
return true;
}
private void doUpdatePeer() {
if (NodeHelper.isDirty(this, DirtyBits.NODE_CONTENTS)) {
NGPath peer = NodeHelper.getPeer(this);
if (peer.acceptsPath2dOnUpdate()) {
peer.updateWithPath2d((Path2D) ShapeHelper.configShape(this));
} else {
peer.reset();
if (isPathValid) {
peer.setFillRule(getFillRule());
for (final PathElement elt : getElements()) {
elt.addTo(peer);
}
peer.update();
}
}
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
final StringBuilder sb = new StringBuilder("Path[");
String id = getId();
if (id != null) {
sb.append("id=").append(id).append(", ");
}
sb.append("elements=").append(getElements());
sb.append(", fill=").append(getFill());
sb.append(", fillRule=").append(getFillRule());
Paint stroke = getStroke();
if (stroke != null) {
sb.append(", stroke=").append(stroke);
sb.append(", strokeWidth=").append(getStrokeWidth());
}
return sb.append("]").toString();
}
}
