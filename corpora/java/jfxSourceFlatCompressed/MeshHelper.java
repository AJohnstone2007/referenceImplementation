package com.sun.javafx.scene.shape;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Mesh;
public abstract class MeshHelper {
private static MeshAccessor meshAccessor;
static {
Utils.forceInit(Mesh.class);
}
protected MeshHelper() {
}
private static MeshHelper getHelper(Mesh mesh) {
return meshAccessor.getHelper(mesh);
}
protected static void setHelper(Mesh mesh, MeshHelper meshHelper) {
meshAccessor.setHelper(mesh, meshHelper);
}
public static boolean computeIntersects(Mesh mesh,
PickRay pickRay, PickResultChooser pickResult, Node candidate,
CullFace cullFace, boolean reportFace) {
return getHelper(mesh).computeIntersectsImpl(mesh,
pickRay, pickResult, candidate, cullFace, reportFace);
}
protected abstract boolean computeIntersectsImpl(Mesh mesh,
PickRay pickRay, PickResultChooser pickResult, Node candidate,
CullFace cullFace, boolean reportFace);
public static void setMeshAccessor(final MeshAccessor newAccessor) {
if (meshAccessor != null) {
throw new IllegalStateException();
}
meshAccessor = newAccessor;
}
public interface MeshAccessor {
MeshHelper getHelper(Mesh mesh);
void setHelper(Mesh mesh, MeshHelper meshHelper);
}
}
