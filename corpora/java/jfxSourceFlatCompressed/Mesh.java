package javafx.scene.shape;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.scene.shape.MeshHelper;
import com.sun.javafx.sg.prism.NGTriangleMesh;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import com.sun.javafx.logging.PlatformLogger;
public abstract class Mesh {
private MeshHelper meshHelper = null;
static {
MeshHelper.setMeshAccessor(new MeshHelper.MeshAccessor() {
@Override
public MeshHelper getHelper(Mesh mesh) {
return mesh.meshHelper;
}
@Override
public void setHelper(Mesh mesh, MeshHelper meshHelper) {
mesh.meshHelper = meshHelper;
}
});
}
protected Mesh() {
if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
String logname = Mesh.class.getName();
PlatformLogger.getLogger(logname).warning("System can't support "
+ "ConditionalFeature.SCENE3D");
}
}
private final BooleanProperty dirty = new SimpleBooleanProperty(true);
final boolean isDirty() {
return dirty.getValue();
}
void setDirty(boolean value) {
dirty.setValue(value);
}
final BooleanProperty dirtyProperty() {
return dirty;
}
abstract NGTriangleMesh getPGMesh();
abstract void updatePG();
abstract BaseBounds computeBounds(BaseBounds b);
}
