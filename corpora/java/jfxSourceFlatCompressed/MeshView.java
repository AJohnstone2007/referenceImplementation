package javafx.scene.shape;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.scene.shape.MeshHelper;
import com.sun.javafx.scene.shape.MeshViewHelper;
import com.sun.javafx.sg.prism.NGMeshView;
import com.sun.javafx.sg.prism.NGNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.Node;
public class MeshView extends Shape3D {
static {
MeshViewHelper.setMeshViewAccessor(new MeshViewHelper.MeshViewAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((MeshView) node).doCreatePeer();
}
@Override
public void doUpdatePeer(Node node) {
((MeshView) node).doUpdatePeer();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((MeshView) node).doComputeGeomBounds(bounds, tx);
}
@Override
public boolean doComputeContains(Node node, double localX, double localY) {
return ((MeshView) node).doComputeContains(localX, localY);
}
@Override
public boolean doComputeIntersects(Node node, PickRay pickRay,
PickResultChooser pickResult) {
return ((MeshView) node).doComputeIntersects(pickRay, pickResult);
}
});
}
{
MeshViewHelper.initHelper(this);
}
public MeshView() {
}
public MeshView(Mesh mesh) {
setMesh(mesh);
}
private ObjectProperty<Mesh> mesh;
public final void setMesh(Mesh value) {
meshProperty().set(value);
}
public final Mesh getMesh() {
return mesh == null ? null : mesh.get();
}
public final ObjectProperty<Mesh> meshProperty() {
if (mesh == null) {
mesh = new SimpleObjectProperty<Mesh>(MeshView.this, "mesh") {
private Mesh old = null;
private final ChangeListener<Boolean> meshChangeListener =
(observable, oldValue, newValue) -> {
if (newValue) {
NodeHelper.markDirty(MeshView.this, DirtyBits.MESH_GEOM);
NodeHelper.geomChanged(MeshView.this);
}
};
private final WeakChangeListener<Boolean> weakMeshChangeListener =
new WeakChangeListener(meshChangeListener);
@Override
protected void invalidated() {
if (old != null) {
old.dirtyProperty().removeListener(weakMeshChangeListener);
}
Mesh newMesh = get();
if (newMesh != null) {
newMesh.dirtyProperty().addListener(weakMeshChangeListener);
}
NodeHelper.markDirty(MeshView.this, DirtyBits.MESH);
NodeHelper.markDirty(MeshView.this, DirtyBits.MESH_GEOM);
NodeHelper.geomChanged(MeshView.this);
old = newMesh;
}
};
}
return mesh;
}
private void doUpdatePeer() {
NGMeshView peer = NodeHelper.getPeer(this);
if (NodeHelper.isDirty(this, DirtyBits.MESH_GEOM) && getMesh() != null) {
getMesh().updatePG();
}
if (NodeHelper.isDirty(this, DirtyBits.MESH)) {
peer.setMesh((getMesh() == null) ? null : getMesh().getPGMesh());
}
}
private NGNode doCreatePeer() {
return new NGMeshView();
}
private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) {
if (getMesh() != null) {
bounds = getMesh().computeBounds(bounds);
bounds = tx.transform(bounds, bounds);
} else {
bounds.makeEmpty();
}
return bounds;
}
private boolean doComputeContains(double localX, double localY) {
throw new UnsupportedOperationException("Not supported yet.");
}
private boolean doComputeIntersects(PickRay pickRay, PickResultChooser pickResult) {
return MeshHelper.computeIntersects(getMesh(), pickRay, pickResult, this, getCullFace(), true);
}
}
