package com.sun.javafx.scene.shape;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.sg.prism.NGTriangleMesh;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.TriangleMesh;
public class TriangleMeshHelper extends MeshHelper {
private static final TriangleMeshHelper theInstance;
private static TriangleMeshAccessor triangleMeshAccessor;
static {
theInstance = new TriangleMeshHelper();
Utils.forceInit(TriangleMesh.class);
}
private static TriangleMeshHelper getInstance() {
return theInstance;
}
public static void initHelper(TriangleMesh triangleMesh) {
setHelper(triangleMesh, getInstance());
}
@Override
protected boolean computeIntersectsImpl(Mesh mesh,
PickRay pickRay, PickResultChooser pickResult, Node candidate,
CullFace cullFace, boolean reportFace) {
return triangleMeshAccessor.doComputeIntersects(mesh, pickRay, pickResult,
candidate, cullFace, reportFace);
}
public static void setTriangleMeshAccessor(final TriangleMeshAccessor newAccessor) {
if (triangleMeshAccessor != null) {
throw new IllegalStateException();
}
triangleMeshAccessor = newAccessor;
}
public interface TriangleMeshAccessor {
boolean doComputeIntersects(Mesh mesh,
PickRay pickRay, PickResultChooser pickResult, Node candidate,
CullFace cullFace, boolean reportFace);
}
}
