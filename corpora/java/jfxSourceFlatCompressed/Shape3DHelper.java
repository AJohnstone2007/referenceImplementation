package com.sun.javafx.scene.shape;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.Shape3D;
public abstract class Shape3DHelper extends NodeHelper {
private static Shape3DAccessor shape3DAccessor;
static {
Utils.forceInit(Shape3D.class);
}
@Override
protected void updatePeerImpl(Node node) {
super.updatePeerImpl(node);
shape3DAccessor.doUpdatePeer(node);
}
@Override
protected BaseBounds computeGeomBoundsImpl(Node node, BaseBounds bounds,
BaseTransform tx) {
return shape3DAccessor.doComputeGeomBounds(node, bounds, tx);
}
@Override
protected boolean computeContainsImpl(Node node, double localX, double localY) {
return shape3DAccessor.doComputeContains(node, localX, localY);
}
public static void setShape3DAccessor(final Shape3DAccessor newAccessor) {
if (shape3DAccessor != null) {
throw new IllegalStateException();
}
shape3DAccessor = newAccessor;
}
public interface Shape3DAccessor {
void doUpdatePeer(Node node);
BaseBounds doComputeGeomBounds(Node node, BaseBounds bounds, BaseTransform tx);
boolean doComputeContains(Node node, double localX, double localY);
}
}
