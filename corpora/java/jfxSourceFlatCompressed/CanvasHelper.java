package com.sun.javafx.scene.canvas;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
public class CanvasHelper extends NodeHelper {
private static final CanvasHelper theInstance;
private static CanvasAccessor canvasAccessor;
static {
theInstance = new CanvasHelper();
Utils.forceInit(Canvas.class);
}
private static CanvasHelper getInstance() {
return theInstance;
}
public static void initHelper(Canvas canvas) {
setHelper(canvas, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return canvasAccessor.doCreatePeer(node);
}
@Override
protected void updatePeerImpl(Node node) {
super.updatePeerImpl(node);
canvasAccessor.doUpdatePeer(node);
}
@Override
protected BaseBounds computeGeomBoundsImpl(Node node, BaseBounds bounds,
BaseTransform tx) {
return canvasAccessor.doComputeGeomBounds(node, bounds, tx);
}
@Override
protected boolean computeContainsImpl(Node node, double localX, double localY) {
return canvasAccessor.doComputeContains(node, localX, localY);
}
public static void setCanvasAccessor(final CanvasAccessor newAccessor) {
if (canvasAccessor != null) {
throw new IllegalStateException();
}
canvasAccessor = newAccessor;
}
public interface CanvasAccessor {
NGNode doCreatePeer(Node node);
void doUpdatePeer(Node node);
BaseBounds doComputeGeomBounds(Node node, BaseBounds bounds, BaseTransform tx);
boolean doComputeContains(Node node, double localX, double localY);
}
}
