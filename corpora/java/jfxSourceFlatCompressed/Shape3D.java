package javafx.scene.shape;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.BoxBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.paint.MaterialHelper;
import com.sun.javafx.scene.shape.Shape3DHelper;
import com.sun.javafx.sg.prism.NGShape3D;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.Node;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import com.sun.javafx.logging.PlatformLogger;
public abstract class Shape3D extends Node {
static {
Shape3DHelper.setShape3DAccessor(new Shape3DHelper.Shape3DAccessor() {
@Override
public void doUpdatePeer(Node node) {
((Shape3D) node).doUpdatePeer();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((Shape3D) node).doComputeGeomBounds(bounds, tx);
}
@Override
public boolean doComputeContains(Node node, double localX, double localY) {
return ((Shape3D) node).doComputeContains(localX, localY);
}
});
}
private static final PhongMaterial DEFAULT_MATERIAL = new PhongMaterial();
protected Shape3D() {
if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
String logname = Shape3D.class.getName();
PlatformLogger.getLogger(logname).warning("System can't support "
+ "ConditionalFeature.SCENE3D");
}
}
PredefinedMeshManager manager = PredefinedMeshManager.getInstance();
Key key;
abstract static class Key {
@Override
public abstract boolean equals(Object obj);
@Override
public abstract int hashCode();
}
private ObjectProperty<Material> material;
public final void setMaterial(Material value) {
materialProperty().set(value);
}
public final Material getMaterial() {
return material == null ? null : material.get();
}
public final ObjectProperty<Material> materialProperty() {
if (material == null) {
material = new SimpleObjectProperty<Material>(Shape3D.this,
"material") {
private Material old = null;
private final ChangeListener<Boolean> materialChangeListener =
(observable, oldValue, newValue) -> {
if (newValue) {
NodeHelper.markDirty(Shape3D.this, DirtyBits.MATERIAL);
}
};
private final WeakChangeListener<Boolean> weakMaterialChangeListener =
new WeakChangeListener(materialChangeListener);
@Override protected void invalidated() {
if (old != null) {
MaterialHelper.dirtyProperty(old).removeListener(weakMaterialChangeListener);
}
Material newMaterial = get();
if (newMaterial != null) {
MaterialHelper.dirtyProperty(newMaterial).addListener(weakMaterialChangeListener);
}
NodeHelper.markDirty(Shape3D.this, DirtyBits.MATERIAL);
NodeHelper.geomChanged(Shape3D.this);
old = newMaterial;
}
};
}
return material;
}
private ObjectProperty<DrawMode> drawMode;
public final void setDrawMode(DrawMode value) {
drawModeProperty().set(value);
}
public final DrawMode getDrawMode() {
return drawMode == null ? DrawMode.FILL : drawMode.get();
}
public final ObjectProperty<DrawMode> drawModeProperty() {
if (drawMode == null) {
drawMode = new SimpleObjectProperty<DrawMode>(Shape3D.this,
"drawMode", DrawMode.FILL) {
@Override
protected void invalidated() {
NodeHelper.markDirty(Shape3D.this, DirtyBits.NODE_DRAWMODE);
}
};
}
return drawMode;
}
private ObjectProperty<CullFace> cullFace;
public final void setCullFace(CullFace value) {
cullFaceProperty().set(value);
}
public final CullFace getCullFace() {
return cullFace == null ? CullFace.BACK : cullFace.get();
}
public final ObjectProperty<CullFace> cullFaceProperty() {
if (cullFace == null) {
cullFace = new SimpleObjectProperty<CullFace>(Shape3D.this,
"cullFace", CullFace.BACK) {
@Override
protected void invalidated() {
NodeHelper.markDirty(Shape3D.this, DirtyBits.NODE_CULLFACE);
}
};
}
return cullFace;
}
private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) {
return new BoxBounds(0, 0, 0, 0, 0, 0);
}
private boolean doComputeContains(double localX, double localY) {
return false;
}
private void doUpdatePeer() {
final NGShape3D peer = NodeHelper.getPeer(this);
if (NodeHelper.isDirty(this, DirtyBits.MATERIAL)) {
Material mat = getMaterial() == null ? DEFAULT_MATERIAL : getMaterial();
MaterialHelper.updatePG(mat);
peer.setMaterial(MaterialHelper.getNGMaterial(mat));
}
if (NodeHelper.isDirty(this, DirtyBits.NODE_DRAWMODE)) {
peer.setDrawMode(getDrawMode() == null ? DrawMode.FILL : getDrawMode());
}
if (NodeHelper.isDirty(this, DirtyBits.NODE_CULLFACE)) {
peer.setCullFace(getCullFace() == null ? CullFace.BACK : getCullFace());
}
}
}
