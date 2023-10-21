package fx83dfeatures;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
public class PNTICOSphereViewer extends Application {
Group root;
PointLight pointLight;
MeshView meshView;
TriangleMesh triMesh;
PhongMaterial material;
float resolution = 0.1f;
float rotateAngle = 0.0f;
private PerspectiveCamera addCamera(Scene scene) {
PerspectiveCamera perspectiveCamera = new PerspectiveCamera();
scene.setCamera(perspectiveCamera);
return perspectiveCamera;
}
private Scene buildScene(int width, int height, boolean depthBuffer) {
triMesh = createICOSphere(100);
material = new PhongMaterial();
material.setDiffuseColor(Color.LIGHTGRAY);
material.setSpecularColor(Color.WHITE);
material.setSpecularPower(64);
meshView = new MeshView(triMesh);
meshView.setMaterial(material);
meshView.setDrawMode(DrawMode.FILL);
meshView.setCullFace(CullFace.BACK);
final Group grp1 = new Group(meshView);
grp1.setRotate(0);
grp1.setRotationAxis(Rotate.X_AXIS);
Group grp2 = new Group(grp1);
grp2.setRotate(-30);
grp2.setRotationAxis(Rotate.X_AXIS);
Group grp3 = new Group(grp2);
grp3.setTranslateX(400);
grp3.setTranslateY(400);
grp3.setTranslateZ(10);
pointLight = new PointLight(Color.ANTIQUEWHITE);
pointLight.setTranslateX(300);
pointLight.setTranslateY(-50);
pointLight.setTranslateZ(-1000);
root = new Group(grp3, pointLight);
Scene scene = new Scene(root, width, height, depthBuffer);
scene.setOnKeyTyped(new EventHandler<KeyEvent>() {
@Override
public void handle(KeyEvent e) {
switch (e.getCharacter()) {
case "l":
System.err.print("l ");
boolean wireframe = meshView.getDrawMode() == DrawMode.LINE;
meshView.setDrawMode(wireframe ? DrawMode.FILL : DrawMode.LINE);
break;
case "<":
grp1.setRotate(rotateAngle -= (resolution * 5));
break;
case ">":
grp1.setRotate(rotateAngle += (resolution * 5));
break;
case "X":
grp1.setRotationAxis(Rotate.X_AXIS);
break;
case "Y":
grp1.setRotationAxis(Rotate.Y_AXIS);
break;
case "Z":
grp1.setRotationAxis(Rotate.Z_AXIS);
break;
case "P":
rotateAngle = 0;
grp1.setRotate(rotateAngle);
case " ":
root.getChildren().add(new Button("Button"));
break;
}
}
});
return scene;
}
@Override
public void start(Stage primaryStage) {
Scene scene = buildScene(800, 800, true);
scene.setFill(Color.rgb(10, 10, 40));
addCamera(scene);
primaryStage.setTitle("PNT ICOSphere Viewer");
primaryStage.setScene(scene);
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
TriangleMesh createICOSphere(float scale) {
final int pointSize = 3;
final int normalSize = 3;
final int texCoordSize = 2;
final int faceSize = 9;
int numVerts = 12;
float points[] = new float[numVerts * pointSize];
float normals[] = new float[numVerts * normalSize];
float texCoords[] = new float[numVerts * texCoordSize];
ToysVec3f[] arrV = new ToysVec3f[numVerts];
float t = (float) ((1.0 + Math.sqrt(5.0)) / 2.0);
arrV[0] = new ToysVec3f(-1, t, 0);
arrV[1] = new ToysVec3f(1, t, 0);
arrV[2] = new ToysVec3f(-1, -t, 0);
arrV[3] = new ToysVec3f(1, -t, 0);
arrV[4] = new ToysVec3f(0, -1, t);
arrV[5] = new ToysVec3f(0, 1, t);
arrV[6] = new ToysVec3f(0, -1, -t);
arrV[7] = new ToysVec3f(0, 1, -t);
arrV[8] = new ToysVec3f(t, 0, -1);
arrV[9] = new ToysVec3f(t, 0, 1);
arrV[10] = new ToysVec3f(-t, 0, -1);
arrV[11] = new ToysVec3f(-t, 0, 1);
for(int i = 0; i < numVerts; i++) {
int pointIndex = i * pointSize;
points[pointIndex] = scale * arrV[i].x;
points[pointIndex + 1] = scale * arrV[i].y;
points[pointIndex + 2] = scale * arrV[i].z;
int normalIndex = i * normalSize;
arrV[i].normalize();
normals[normalIndex] = arrV[i].x;
normals[normalIndex + 1] = arrV[i].y;
normals[normalIndex + 2] = arrV[i].z;
int texCoordIndex = i * texCoordSize;
texCoords[texCoordIndex] = 0f;
texCoords[texCoordIndex + 1] = 0f;
}
int faces[] = {
0, 0, 0, 11, 11, 0, 5, 5, 0,
0, 0, 0, 5, 5, 0, 1, 1, 0,
0, 0, 0, 1, 1, 0, 7, 7, 0,
0, 0, 0, 7, 7, 0, 10, 10, 0,
0, 0, 0, 10, 10, 0, 11, 11, 0,
1, 1, 0, 5, 5, 0, 9, 9, 0,
5, 5, 0, 11, 11, 0, 4, 4, 0,
11, 11, 0, 10, 10, 0, 2, 2, 0,
10, 10, 0, 7, 7, 0, 6, 6, 0,
7, 7, 0, 1, 1, 0, 8, 8, 0,
3, 3, 0, 9, 9, 0, 4, 4, 0,
3, 3, 0, 4, 4, 0, 2, 2, 0,
3, 3, 0, 2, 2, 0, 6, 6, 0,
3, 3, 0, 6, 6, 0, 8, 8, 0,
3, 3, 0, 8, 8, 0, 9, 9, 0,
4, 4, 0, 9, 9, 0, 5, 5, 0,
2, 2, 0, 4, 4, 0, 11, 11, 0,
6, 6, 0, 2, 2, 0, 10, 10, 0,
8, 8, 0, 6, 6, 0, 7, 7, 0,
9, 9, 0, 8, 8, 0, 1, 1, 0
};
TriangleMesh triangleMesh = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
triangleMesh.getPoints().setAll(points);
triangleMesh.getNormals().setAll(normals);
triangleMesh.getTexCoords().setAll(texCoords);
triangleMesh.getFaces().setAll(faces);
return triangleMesh;
}
}
