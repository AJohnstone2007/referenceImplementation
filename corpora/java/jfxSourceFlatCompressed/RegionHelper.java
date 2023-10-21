package com.sun.javafx.scene.layout;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Region;
public class RegionHelper extends ParentHelper {
private static final RegionHelper theInstance;
private static RegionAccessor regionAccessor;
static {
theInstance = new RegionHelper();
Utils.forceInit(Region.class);
}
private static RegionHelper getInstance() {
return theInstance;
}
public static void initHelper(Region region) {
setHelper(region, getInstance());
}
public static BaseBounds superComputeGeomBounds(Node node, BaseBounds bounds,
BaseTransform tx) {
return ((RegionHelper) getHelper(node)).superComputeGeomBoundsImpl(node, bounds, tx);
}
@Override
protected NGNode createPeerImpl(Node node) {
return regionAccessor.doCreatePeer(node);
}
@Override
protected void updatePeerImpl(Node node) {
super.updatePeerImpl(node);
regionAccessor.doUpdatePeer(node);
}
BaseBounds superComputeGeomBoundsImpl(Node node, BaseBounds bounds,
BaseTransform tx) {
return super.computeGeomBoundsImpl(node, bounds, tx);
}
@Override
protected Bounds computeLayoutBoundsImpl(Node node) {
return regionAccessor.doComputeLayoutBounds(node);
}
@Override
protected BaseBounds computeGeomBoundsImpl(Node node, BaseBounds bounds,
BaseTransform tx) {
return regionAccessor.doComputeGeomBounds(node, bounds, tx);
}
@Override
protected boolean computeContainsImpl(Node node, double localX, double localY) {
return regionAccessor.doComputeContains(node, localX, localY);
}
@Override
protected void notifyLayoutBoundsChangedImpl(Node node) {
regionAccessor.doNotifyLayoutBoundsChanged(node);
}
@Override
protected void pickNodeLocalImpl(Node node, PickRay localPickRay,
PickResultChooser result) {
regionAccessor.doPickNodeLocal(node, localPickRay, result);
}
public static void setRegionAccessor(final RegionAccessor newAccessor) {
if (regionAccessor != null) {
throw new IllegalStateException();
}
regionAccessor = newAccessor;
}
public interface RegionAccessor {
void doUpdatePeer(Node node);
NGNode doCreatePeer(Node node);
Bounds doComputeLayoutBounds(Node node);
BaseBounds doComputeGeomBounds(Node node, BaseBounds bounds, BaseTransform tx);
boolean doComputeContains(Node node, double localX, double localY);
void doNotifyLayoutBoundsChanged(Node node);
void doPickNodeLocal(Node node, PickRay localPickRay,
PickResultChooser result);
}
}
