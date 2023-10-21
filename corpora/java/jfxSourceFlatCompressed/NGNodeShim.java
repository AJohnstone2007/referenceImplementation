package com.sun.javafx.sg.prism;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.DirtyRegionPool;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
public abstract class NGNodeShim extends NGNode {
@Override
public boolean hasOpaqueRegion() {
return super.hasOpaqueRegion();
}
public static void clearDirty(NGNode node) {
node.clearDirty();
}
public static RectBounds computeOpaqueRegion(NGNode node, RectBounds opaqueRegion) {
return node.computeOpaqueRegion(opaqueRegion);
}
public static int cullingBits(NGNode node) {
return node.cullingBits;
}
public static boolean childDirty(NGNode node) {
return node.childDirty;
}
public static NGNode.DirtyFlag dirty(NGNode node) {
return node.dirty;
}
public static boolean isOpaqueRegionInvalid(NGNode node) {
return node.isOpaqueRegionInvalid();
}
public static void markCullRegions(
NGNode node,
DirtyRegionContainer drc,
int cullingRegionsBitsOfParent,
BaseTransform tx,
GeneralTransform3D pvTx) {
node.markCullRegions(drc, cullingRegionsBitsOfParent, tx, pvTx);
}
public static void set_dirty(NGNode node, NGNode.DirtyFlag flag) {
node.dirty = flag;
}
public static void set_childDirty(NGNode node, boolean flag) {
node.childDirty = flag;
}
public static boolean supportsOpaqueRegions(NGNode node) {
return node.supportsOpaqueRegions();
}
public static boolean hasOpaqueRegion(NGNode node) {
return node.hasOpaqueRegion();
}
}
