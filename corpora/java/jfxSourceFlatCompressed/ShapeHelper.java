package com.sun.javafx.scene.shape;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGShape;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
public abstract class ShapeHelper extends NodeHelper {
private static ShapeAccessor shapeAccessor;
static {
Utils.forceInit(Shape.class);
}
public static Paint cssGetFillInitialValue(Shape shape) {
return ((ShapeHelper) getHelper(shape)).cssGetFillInitialValueImpl(shape);
}
public static Paint cssGetStrokeInitialValue(Shape shape) {
return ((ShapeHelper) getHelper(shape)).cssGetStrokeInitialValueImpl(shape);
}
public static com.sun.javafx.geom.Shape configShape(Shape shape) {
return ((ShapeHelper) getHelper(shape)).configShapeImpl(shape);
}
@Override
protected void updatePeerImpl(Node node) {
super.updatePeerImpl(node);
shapeAccessor.doUpdatePeer(node);
}
@Override
protected void markDirtyImpl(Node node, DirtyBits dirtyBit) {
shapeAccessor.doMarkDirty(node, dirtyBit);
super.markDirtyImpl(node, dirtyBit);
}
@Override
protected BaseBounds computeGeomBoundsImpl(Node node, BaseBounds bounds,
BaseTransform tx) {
return shapeAccessor.doComputeGeomBounds(node, bounds, tx);
}
@Override
protected boolean computeContainsImpl(Node node, double localX, double localY) {
return shapeAccessor.doComputeContains(node, localX, localY);
}
protected Paint cssGetFillInitialValueImpl(Shape shape) {
return shapeAccessor.doCssGetFillInitialValue(shape);
}
protected Paint cssGetStrokeInitialValueImpl(Shape shape) {
return shapeAccessor.doCssGetStrokeInitialValue(shape);
}
protected abstract com.sun.javafx.geom.Shape configShapeImpl(Shape shape);
public static NGShape.Mode getMode(Shape shape) {
return shapeAccessor.getMode(shape);
}
public static void setMode(Shape shape, NGShape.Mode mode) {
shapeAccessor.setMode(shape, mode);
}
public static void setShapeChangeListener(Shape shape, Runnable listener) {
shapeAccessor.setShapeChangeListener(shape, listener);
}
public static void setShapeAccessor(final ShapeAccessor newAccessor) {
if (shapeAccessor != null) {
throw new IllegalStateException();
}
shapeAccessor = newAccessor;
}
public interface ShapeAccessor {
void doUpdatePeer(Node node);
void doMarkDirty(Node node, DirtyBits dirtyBit);
BaseBounds doComputeGeomBounds(Node node, BaseBounds bounds, BaseTransform tx);
boolean doComputeContains(Node node, double localX, double localY);
Paint doCssGetFillInitialValue(Shape shape);
Paint doCssGetStrokeInitialValue(Shape shape);
NGShape.Mode getMode(Shape shape);
void setMode(Shape shape, NGShape.Mode mode);
void setShapeChangeListener(Shape shape, Runnable listener);
}
}
