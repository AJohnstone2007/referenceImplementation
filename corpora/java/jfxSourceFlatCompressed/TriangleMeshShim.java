package javafx.scene.shape;
import com.sun.javafx.sg.prism.NGTriangleMesh;
public class TriangleMeshShim {
public static NGTriangleMesh getNGMesh(Mesh mesh) {
return (NGTriangleMesh) mesh.getPGMesh();
}
}
