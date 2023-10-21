package test.robot.test3d;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import test.robot.testharness.VisualTestBase;
import static org.junit.Assume.assumeTrue;
public class TriangleMeshPNTValidationTest extends VisualTestBase {
private Stage testStage;
private Scene testScene;
private MeshView meshView;
private TriangleMesh triMesh;
private PhongMaterial material;
private Group root;
private static final double TOLERANCE = 0.07;
private static final int WIDTH = 800;
private static final int HEIGHT = 800;
private Color bgColor = Color.rgb(10, 10, 40);
@Before
public void setupEach() {
assumeTrue(Platform.isSupported(ConditionalFeature.SCENE3D));
}
@Test(timeout = 15000)
public void testInvalidNormalsLength() {
runAndWait(() -> {
testStage = getStage();
testStage.setTitle("TriangleMesh PNT Validation Test");
testScene = new Scene(buildScene(), WIDTH, HEIGHT, true);
testScene.setFill(bgColor);
addCamera(testScene);
buildSquare();
triMesh.getNormals().setAll(0f, 0.0f );
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 3, WIDTH / 3);
assertColorEquals(bgColor, color, TOLERANCE);
});
}
@Test(timeout = 15000)
public void testNormalsLengthChange() {
runAndWait(() -> {
testStage = getStage();
testStage.setTitle("TriangleMesh PNT Validation Test");
testScene = new Scene(buildScene(), WIDTH, HEIGHT, true);
testScene.setFill(bgColor);
addCamera(testScene);
buildSquare();
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 3, WIDTH / 3);
assertColorEquals(Color.RED, color, TOLERANCE);
triMesh.getPoints().setAll(0, 0, 1);
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 3, WIDTH / 3);
assertColorEquals(bgColor, color, TOLERANCE);
});
}
@Test(timeout = 15000)
public void testDegeneratedMeshUpdateNormals() {
runAndWait(() -> {
testStage = getStage();
testStage.setTitle("TriangleMesh PNT Validation Test");
testScene = new Scene(buildScene(), WIDTH, HEIGHT, true);
testScene.setFill(bgColor);
addCamera(testScene);
buildSquare();
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 3, WIDTH / 3);
assertColorEquals(Color.RED, color, TOLERANCE);
color = getColor(testScene, WIDTH / 2 + 10, WIDTH / 2 + 10);
assertColorEquals(Color.RED, color, TOLERANCE);
triMesh.getNormals().setAll(0, 0, 0);
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 2 + 10, WIDTH / 2 + 10);
assertColorEquals(Color.BLACK, color, TOLERANCE);
});
}
@Test(timeout = 15000)
public void testDegeneratedMeshUpdatePoints() {
runAndWait(() -> {
testStage = getStage();
testStage.setTitle("TriangleMesh Validation Test");
testScene = new Scene(buildScene(), WIDTH, HEIGHT, true);
testScene.setFill(bgColor);
addCamera(testScene);
buildSquare();
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 3, WIDTH / 3);
assertColorEquals(Color.RED, color, TOLERANCE);
color = getColor(testScene, WIDTH / 2 + 10, WIDTH / 2 + 10);
assertColorEquals(Color.RED, color, TOLERANCE);
triMesh.getPoints().setAll(
0.5f, -1.5f, 0f,
0.5f, -1.5f, 0f,
0.5f, 1.5f, 0f,
0.5f, -1.5f, 0f);
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 2 + 10, WIDTH / 2 + 10);
assertColorEquals(bgColor, color, TOLERANCE);
});
}
void buildSquare() {
float points[] = {
1.5f, 1.5f, 0f,
1.5f, -1.5f, 0f,
-1.5f, 1.5f, 0f,
-1.5f, -1.5f, 0f
};
float normals[] = {
0f, 0f, -1f, 0f, 0f, 1f
};
float texCoords[] = {0, 0
};
int faces[] = {
2, 0, 0, 1, 0, 0, 3, 0, 0,
2, 0, 0, 0, 0, 0, 1, 0, 0,};
triMesh.getPoints().setAll(points);
triMesh.getNormals().setAll(normals);
triMesh.getTexCoords().setAll(texCoords);
triMesh.getFaces().setAll(faces);
}
private Group buildScene() {
triMesh = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
material = new PhongMaterial();
material.setDiffuseColor(Color.RED);
meshView = new MeshView(triMesh);
meshView.setMaterial(material);
meshView.setScaleX(200);
meshView.setScaleY(200);
meshView.setScaleZ(200);
meshView.setTranslateX(400);
meshView.setTranslateY(400);
meshView.setTranslateZ(10);
root = new Group(meshView);
return root;
}
private PerspectiveCamera addCamera(Scene scene) {
PerspectiveCamera perspectiveCamera = new PerspectiveCamera(false);
scene.setCamera(perspectiveCamera);
return perspectiveCamera;
}
}
