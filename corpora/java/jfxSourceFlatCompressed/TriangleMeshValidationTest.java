package test.robot.test3d;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import test.robot.testharness.VisualTestBase;
import static org.junit.Assume.assumeTrue;
public class TriangleMeshValidationTest extends VisualTestBase {
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
public void testEmptyMesh() {
runAndWait(() -> {
testStage = getStage();
testStage.setTitle("TriangleMesh Validation Test");
testScene = new Scene(buildScene(), WIDTH, HEIGHT, true);
testScene.setFill(bgColor);
addCamera(testScene);
triMesh = new TriangleMesh();
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
public void testInvalidPointsLength() {
runAndWait(() -> {
testStage = getStage();
testStage.setTitle("TriangleMesh Validation Test");
testScene = new Scene(buildScene(), 800, 800, true);
testScene.setFill(bgColor);
addCamera(testScene);
buildBox();
triMesh.getPoints().setAll(1, 1, 1,
1, 1, -1,
1, -1, 1,
1, -1, -1,
-1, 1, 1,
-1, 1, -1,
-1,
-1, -1, -1);
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
public void testInvalidTexCoordLength() {
runAndWait(() -> {
testStage = getStage();
testStage.setTitle("TriangleMesh Validation Test");
testScene = new Scene(buildScene(), 800, 800, true);
testScene.setFill(bgColor);
addCamera(testScene);
buildBox();
triMesh.getTexCoords().setAll(0, 0,
0, 1,
1,
1, 1);
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
public void testInvalidFacesLength() {
runAndWait(() -> {
testStage = getStage();
testStage.setTitle("TriangleMesh Validation Test");
testScene = new Scene(buildScene(), 800, 800, true);
testScene.setFill(bgColor);
addCamera(testScene);
buildBox();
triMesh.getFaces().setAll(0, 0, 2, 2, 1, 1,
2, 2, 3, 3, 1, 1,
4, 0, 5, 1, 6, 2,
6, 2, 5, 1, 7, 3,
0, 0, 1, 1, 4, 2,
4, 2, 1, 1, 5,
2, 0, 6, 2, 3, 1,
3, 1, 6, 2, 7, 3,
0, 0, 4, 1, 2, 2,
2, 2, 4, 1, 6, 3,
1, 0, 3, 1, 5, 2,
5, 2, 3, 1, 7, 3);
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
public void testInvalidFacesIndex() {
runAndWait(() -> {
testStage = getStage();
testStage.setTitle("TriangleMesh Validation Test");
testScene = new Scene(buildScene(), 800, 800, true);
testScene.setFill(bgColor);
addCamera(testScene);
buildBox();
triMesh.getFaces().setAll(0, 0, 2, 2, 1, 1,
2, 2, 3, 3, 1, 1,
4, 0, 5, 1, 6, 2,
6, 2, 5, 1, 7, 3,
0, 0, 1, 1, 4, 2,
4, 2, 1, 1, 5, 8,
2, 0, 6, 2, 3, 1,
3, 1, 6, 2, 7, 3,
0, 0, 4, 1, 2, 2,
2, 2, 4, 1, 6, 3,
1, 0, 3, 1, 5, 2,
5, 2, 3, 1, 7, 3);
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
public void testInvalidFaceSmoothingGroupsLength() {
runAndWait(() -> {
testStage = getStage();
testStage.setTitle("TriangleMesh Validation Test");
testScene = new Scene(buildScene(), 800, 800, true);
testScene.setFill(bgColor);
addCamera(testScene);
buildBox();
triMesh.getFaceSmoothingGroups().setAll(1, 1, 1, 1, 2, 2, 4, 4, 4, 4);
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
public void testPointsLengthChange() {
runAndWait(() -> {
testStage = getStage();
testStage.setTitle("TriangleMesh Validation Test");
testScene = new Scene(buildScene(), 800, 800, true);
testScene.setFill(bgColor);
addCamera(testScene);
buildBox();
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 3, WIDTH / 3);
assertColorEquals(Color.RED, color, TOLERANCE);
triMesh.getPoints().setAll(1, 1, 1);
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 3, WIDTH / 3);
assertColorEquals(bgColor, color, TOLERANCE);
});
}
@Test(timeout = 15000)
public void testTexCoordsLengthChange() {
runAndWait(() -> {
testStage = getStage();
testStage.setTitle("TriangleMesh Validation Test");
testScene = new Scene(buildScene(), 800, 800, true);
testScene.setFill(bgColor);
addCamera(testScene);
buildBox();
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 3, WIDTH / 3);
assertColorEquals(Color.RED, color, TOLERANCE);
triMesh.getTexCoords().setAll(0, 0, 1, 1);
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 3, WIDTH / 3);
assertColorEquals(bgColor, color, TOLERANCE);
});
}
@Test(timeout = 15000)
public void testFaceLengthChange() {
runAndWait(() -> {
testStage = getStage();
testStage.setTitle("TriangleMesh Validation Test");
testScene = new Scene(buildScene(), 800, 800, true);
testScene.setFill(bgColor);
addCamera(testScene);
buildBox();
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 4, WIDTH / 4);
assertColorEquals(Color.RED, color, TOLERANCE);
triMesh.getFaces().setAll( 5, 2, 3, 1, 7, 3);
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 4, WIDTH / 4);
assertColorEquals(bgColor, color, TOLERANCE);
triMesh.getFaceSmoothingGroups().setAll();
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 4, WIDTH / 4);
assertColorEquals(Color.RED, color, TOLERANCE);
assertColorDoesNotEqual(bgColor, color, TOLERANCE);
});
}
@Test(timeout = 15000)
public void testResetFaceSmoothingGroup() {
runAndWait(() -> {
testStage = getStage();
testStage.setTitle("TriangleMesh Validation Test");
testScene = new Scene(buildScene(), 800, 800, true);
testScene.setFill(bgColor);
addCamera(testScene);
buildBox();
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 3, WIDTH / 3);
assertColorEquals(Color.RED, color, TOLERANCE);
triMesh.getFaceSmoothingGroups().setAll();
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 3, WIDTH / 3);
assertColorEquals(Color.RED, color, TOLERANCE);
});
}
@Test(timeout = 15000)
public void testUpdateMesh() {
runAndWait(() -> {
testStage = getStage();
testStage.setTitle("TriangleMesh Validation Test");
testScene = new Scene(buildScene(), 800, 800, true);
testScene.setFill(bgColor);
addCamera(testScene);
buildBox();
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 3, WIDTH / 3);
assertColorEquals(Color.RED, color, TOLERANCE);
color = getColor(testScene, WIDTH / 5, WIDTH / 5);
assertColorEquals(bgColor, color, TOLERANCE);
triMesh.getPoints().setAll(1.5f, 1.5f, 1.5f,
1.5f, 1.5f, -1.5f,
1.5f, -1.5f, 1.5f,
1.5f, -1.5f, -1.5f,
-1.5f, 1.5f, 1.5f,
-1.5f, 1.5f, -1.5f,
-1.5f, -1.5f, 1.5f,
-1.5f, -1.5f, -1.5f);
triMesh.getTexCoords().setAll(0, 0,
1, 0,
0, 1,
1, 1);
triMesh.getFaces().setAll(5, 2, 3, 1, 7, 3);
triMesh.getFaceSmoothingGroups().setAll(1);
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 5, WIDTH / 5);
assertColorEquals(Color.RED, color, TOLERANCE);
});
}
@Test(timeout = 15000)
public void testDegeneratedMeshUpdateFaces() {
runAndWait(() -> {
testStage = getStage();
testStage.setTitle("TriangleMesh Validation Test");
testScene = new Scene(buildScene(), 800, 800, true);
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
triMesh.getFaces().setAll(
2, 0, 1, 0, 3, 0,
2, 0, 1, 0, 1, 0);
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 3, WIDTH / 3);
assertColorEquals(Color.RED, color, TOLERANCE);
color = getColor(testScene, WIDTH / 2 + 10, WIDTH / 2 + 10);
assertColorEquals(bgColor, color, TOLERANCE);
});
}
@Test(timeout = 15000)
public void testDegeneratedMeshUpdatePoints() {
runAndWait(() -> {
testStage = getStage();
testStage.setTitle("TriangleMesh Validation Test");
testScene = new Scene(buildScene(), 800, 800, true);
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
1.5f, -1.5f, 0f,
1.5f, -1.5f, 0f,
-1.5f, 1.5f, 0f,
-1.5f, -1.5f, 0f);
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 3, WIDTH / 3);
assertColorEquals(Color.RED, color, TOLERANCE);
color = getColor(testScene, WIDTH / 2 + 10, WIDTH / 2 + 10);
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
float texCoords[] = {0, 0
};
int faceSmoothingGroups[] = {
1, 1
};
int faces[] = {
2, 0, 1, 0, 3, 0,
2, 0, 0, 0, 1, 0
};
triMesh.getPoints().setAll(points);
triMesh.getTexCoords().setAll(texCoords);
triMesh.getFaces().setAll(faces);
triMesh.getFaceSmoothingGroups().setAll(faceSmoothingGroups);
}
void buildBox() {
float points[] = {
1, 1, 1,
1, 1, -1,
1, -1, 1,
1, -1, -1,
-1, 1, 1,
-1, 1, -1,
-1, -1, 1,
-1, -1, -1,};
float texCoords[] = {0, 0,
0, 1,
1, 0,
1, 1};
int faceSmoothingGroups[] = {
1, 1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 4
};
int faces[] = {
0, 0, 2, 2, 1, 1,
2, 2, 3, 3, 1, 1,
4, 0, 5, 1, 6, 2,
6, 2, 5, 1, 7, 3,
0, 0, 1, 1, 4, 2,
4, 2, 1, 1, 5, 3,
2, 0, 6, 2, 3, 1,
3, 1, 6, 2, 7, 3,
0, 0, 4, 1, 2, 2,
2, 2, 4, 1, 6, 3,
1, 0, 3, 1, 5, 2,
5, 2, 3, 1, 7, 3,};
triMesh.getPoints().setAll(points);
triMesh.getTexCoords().setAll(texCoords);
triMesh.getFaces().setAll(faces);
triMesh.getFaceSmoothingGroups().setAll(faceSmoothingGroups);
}
private Group buildScene() {
triMesh = new TriangleMesh();
material = new PhongMaterial();
material.setDiffuseColor(Color.RED);
material.setSpecularColor(Color.rgb(30, 30, 30));
meshView = new MeshView(triMesh);
meshView.setMaterial(material);
meshView.setScaleX(200);
meshView.setScaleY(200);
meshView.setScaleZ(200);
meshView.setTranslateX(400);
meshView.setTranslateY(400);
meshView.setTranslateZ(10);
root = new Group(meshView, new AmbientLight());
return root;
}
private PerspectiveCamera addCamera(Scene scene) {
PerspectiveCamera perspectiveCamera = new PerspectiveCamera(false);
scene.setCamera(perspectiveCamera);
return perspectiveCamera;
}
}
