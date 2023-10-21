package javafx.scene.shape;
import com.sun.javafx.util.Logging;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.SVGPathHelper;
import com.sun.javafx.scene.shape.ShapeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGSVGPath;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
public class SVGPath extends Shape {
static {
SVGPathHelper.setSVGPathAccessor(new SVGPathHelper.SVGPathAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((SVGPath) node).doCreatePeer();
}
@Override
public void doUpdatePeer(Node node) {
((SVGPath) node).doUpdatePeer();
}
@Override
public com.sun.javafx.geom.Shape doConfigShape(Shape shape) {
return ((SVGPath) shape).doConfigShape();
}
});
}
private ObjectProperty<FillRule> fillRule;
private Path2D path2d;
{
SVGPathHelper.initHelper(this);
}
public SVGPath() {
}
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
NodeHelper.markDirty(SVGPath.this, DirtyBits.SHAPE_FILLRULE);
NodeHelper.geomChanged(SVGPath.this);
}
@Override
public Object getBean() {
return SVGPath.this;
}
@Override
public String getName() {
return "fillRule";
}
};
}
return fillRule;
}
private StringProperty content;
public final void setContent(String value) {
contentProperty().set(value);
}
public final String getContent() {
return content == null ? "" : content.get();
}
public final StringProperty contentProperty() {
if (content == null) {
content = new StringPropertyBase("") {
@Override
public void invalidated() {
NodeHelper.markDirty(SVGPath.this, DirtyBits.NODE_CONTENTS);
NodeHelper.geomChanged(SVGPath.this);
path2d = null;
}
@Override
public Object getBean() {
return SVGPath.this;
}
@Override
public String getName() {
return "content";
}
};
}
return content;
}
private Object svgPathObject;
private NGNode doCreatePeer() {
return new NGSVGPath();
}
private Path2D doConfigShape() {
if (path2d == null) {
path2d = createSVGPath2D();
} else {
path2d.setWindingRule(getFillRule() == FillRule.NON_ZERO ?
Path2D.WIND_NON_ZERO : Path2D.WIND_EVEN_ODD);
}
return path2d;
}
private void doUpdatePeer() {
if (NodeHelper.isDirty(this, DirtyBits.SHAPE_FILLRULE) ||
NodeHelper.isDirty(this, DirtyBits.NODE_CONTENTS))
{
final NGSVGPath peer = NodeHelper.getPeer(this);
if (peer.acceptsPath2dOnUpdate()) {
if (svgPathObject == null) {
svgPathObject = new Path2D();
}
Path2D tempPathObject = (Path2D) svgPathObject;
tempPathObject.setTo((Path2D) ShapeHelper.configShape(this));
} else {
svgPathObject = createSVGPathObject();
}
peer.setContent(svgPathObject);
}
}
@Override
public String toString() {
final StringBuilder sb = new StringBuilder("SVGPath[");
String id = getId();
if (id != null) {
sb.append("id=").append(id).append(", ");
}
sb.append("content=\"").append(getContent()).append("\"");
sb.append(", fill=").append(getFill());
sb.append(", fillRule=").append(getFillRule());
Paint stroke = getStroke();
if (stroke != null) {
sb.append(", stroke=").append(stroke);
sb.append(", strokeWidth=").append(getStrokeWidth());
}
return sb.append("]").toString();
}
private Path2D createSVGPath2D() {
try {
return Toolkit.getToolkit().createSVGPath2D(this);
} catch (final RuntimeException e) {
Logging.getJavaFXLogger().warning(
"Failed to configure svg path \"{0}\": {1}",
getContent(), e.getMessage());
return Toolkit.getToolkit().createSVGPath2D(new SVGPath());
}
}
private Object createSVGPathObject() {
try {
return Toolkit.getToolkit().createSVGPathObject(this);
} catch (final RuntimeException e) {
Logging.getJavaFXLogger().warning(
"Failed to configure svg path \"{0}\": {1}",
getContent(), e.getMessage());
return Toolkit.getToolkit().createSVGPathObject(new SVGPath());
}
}
}
