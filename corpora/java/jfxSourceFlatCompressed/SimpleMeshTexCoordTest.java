package fx83dfeatures;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.stage.Stage;
public class SimpleMeshTexCoordTest extends Application {
Group root;
PointLight pointLight;
MeshView meshView;
TriangleMesh triMesh;
PhongMaterial material;
final Image diffuseMap = new Image("resources/cup_diffuseMap_1024.png");
static TriangleMesh buildTriangleMesh() {
float points[] = {
0.0f, 0.0f, 0.0f,
400.0f, 0.0f, 0.0f,
0.0f, 400.0f, 0.0f,
400.0f, 400.0f, 0.0f};
float texCoords[] = {
0.0f, 0.0f,
1.0f, 0.0f,
0.0f, 1.0f,
1.0f, 1.0f
};
int faces[] = {
0, 0, 2, 2, 3, 3,
3, 3, 1, 1, 0, 0
};
TriangleMesh triangleMesh = new TriangleMesh();
triangleMesh.getPoints().setAll(points);
triangleMesh.getTexCoords().setAll(texCoords);
triangleMesh.getFaces().setAll(faces);
return triangleMesh;
}
private Group buildScene() {
triMesh = buildTriangleMesh();
material = new PhongMaterial();
material.setDiffuseMap(diffuseMap);
material.setSpecularColor(Color.rgb(30, 30, 30));
meshView = new MeshView(triMesh);
meshView.setTranslateX(200);
meshView.setTranslateY(200);
meshView.setTranslateZ(20);
meshView.setMaterial(material);
meshView.setDrawMode(DrawMode.FILL);
meshView.setCullFace(CullFace.BACK);
pointLight = new PointLight(Color.ANTIQUEWHITE);
pointLight.setTranslateX(150);
pointLight.setTranslateY(-100);
pointLight.setTranslateZ(-1000);
root = new Group(meshView, pointLight);
return root;
}
private PerspectiveCamera addCamera(Scene scene) {
PerspectiveCamera perspectiveCamera = new PerspectiveCamera();
scene.setCamera(perspectiveCamera);
return perspectiveCamera;
}
@Override
public void start(Stage primaryStage) {
Scene scene = new Scene(buildScene(), 800, 800, true);
scene.setFill(Color.GRAY);
addCamera(scene);
primaryStage.setTitle("SimpleMeshTexCoordTest");
primaryStage.setScene(scene);
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
