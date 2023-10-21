package com.sun.javafx.scene.shape;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
public class SVGPathHelper extends ShapeHelper {
private static final SVGPathHelper theInstance;
private static SVGPathAccessor svgPathAccessor;
static {
theInstance = new SVGPathHelper();
Utils.forceInit(SVGPath.class);
}
private static SVGPathHelper getInstance() {
return theInstance;
}
public static void initHelper(SVGPath svgPath) {
setHelper(svgPath, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return svgPathAccessor.doCreatePeer(node);
}
@Override
protected void updatePeerImpl(Node node) {
super.updatePeerImpl(node);
svgPathAccessor.doUpdatePeer(node);
}
@Override
protected com.sun.javafx.geom.Shape configShapeImpl(Shape shape) {
return svgPathAccessor.doConfigShape(shape);
}
public static void setSVGPathAccessor(final SVGPathAccessor newAccessor) {
if (svgPathAccessor != null) {
throw new IllegalStateException();
}
svgPathAccessor = newAccessor;
}
public interface SVGPathAccessor {
NGNode doCreatePeer(Node node);
void doUpdatePeer(Node node);
com.sun.javafx.geom.Shape doConfigShape(Shape shape);
}
}
