package test.robot.test3d;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import test.robot.testharness.VisualTestBase;
import static org.junit.Assume.assumeTrue;
public class AABalanceFlipTest extends VisualTestBase {
private Stage testStage;
private Scene testScene;
private static final double TOLERANCE = 0.07;
WritableImage selfIllumMap;
@Before
public void setupEach() {
assumeTrue(Platform.isSupported(ConditionalFeature.SCENE3D));
}
@Test(timeout = 15000)
public void testAABalanceFlip() {
final int WIDTH = 600;
final int HEIGHT = 600;
selfIllumMap = new WritableImage(64, 64);
PixelWriter pWriter = selfIllumMap.getPixelWriter();
setArgb(pWriter, 0, 32, 0, 32, 0Xff000000);
setArgb(pWriter, 32, 64, 0, 32, 0Xffffffff);
setArgb(pWriter, 0, 32, 32, 64, 0Xffffffff);
setArgb(pWriter, 32, 64, 32, 64, 0Xff000000);
runAndWait(() -> {
testScene = buildScene(WIDTH, HEIGHT, true);
testScene.setFill(Color.rgb(10, 10, 40));
addCamera(testScene);
testStage = getStage();
testStage.setTitle("SceneAntialiasing.BALANCED Flip Test");
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
runAndWait(() -> {
Color color;
Color blackColor = new Color(0, 0, 0, 1);
Color whiteColor = new Color(1, 1, 1, 1);
color = getColor(testScene, WIDTH / 4, HEIGHT / 4);
assertColorEquals(blackColor, color, TOLERANCE);
color = getColor(testScene, (WIDTH - 100), HEIGHT / 4);
assertColorEquals(whiteColor, color, TOLERANCE);
color = getColor(testScene, WIDTH / 4, HEIGHT - 100);
assertColorEquals(whiteColor, color, TOLERANCE);
color = getColor(testScene, WIDTH - 100, HEIGHT - 100);
assertColorEquals(blackColor, color, TOLERANCE);
});
}
Group root;
MeshView meshView;
TriangleMesh triMesh;
PhongMaterial material;
int divX = 8;
int divY = 8;
static final float MESH_SCALE = 20;
static final float MIN_X = -20;
static final float MIN_Y = -20;
static final float MAX_X = 20;
static final float MAX_Y = 20;
private TriangleMesh buildTriangleMesh(int subDivX, int subDivY,
float scale) {
final int pointSize = 3;
final int texCoordSize = 2;
final int faceSize = 6;
int numDivX = subDivX + 1;
int numVerts = (subDivY + 1) * numDivX;
float points[] = new float[numVerts * pointSize];
float texCoords[] = new float[numVerts * texCoordSize];
int faceCount = subDivX * subDivY * 2;
int faces[] = new int[faceCount * faceSize];
for (int y = 0; y <= subDivY; y++) {
float dy = (float) y / subDivY;
double fy = (1 - dy) * MIN_Y + dy * MAX_Y;
for (int x = 0; x <= subDivX; x++) {
float dx = (float) x / subDivX;
double fx = (1 - dx) * MIN_X + dx * MAX_X;
int index = y * numDivX * pointSize + (x * pointSize);
points[index] = (float) fx * scale;
points[index + 1] = (float) fy * scale;
points[index + 2] = (float) 0.0;
index = y * numDivX * texCoordSize + (x * texCoordSize);
texCoords[index] = dx * subDivX / 8;
texCoords[index + 1] = dy * subDivY / 8;
}
}
for (int y = 0; y < subDivY; y++) {
for (int x = 0; x < subDivX; x++) {
int p00 = y * numDivX + x;
int p01 = p00 + 1;
int p10 = p00 + numDivX;
int p11 = p10 + 1;
int tc00 = y * numDivX + x;
int tc01 = tc00 + 1;
int tc10 = tc00 + numDivX;
int tc11 = tc10 + 1;
int index = (y * subDivX * faceSize + (x * faceSize)) * 2;
faces[index + 0] = p00;
faces[index + 1] = tc00;
faces[index + 2] = p10;
faces[index + 3] = tc10;
faces[index + 4] = p11;
faces[index + 5] = tc11;
index += faceSize;
faces[index + 0] = p11;
faces[index + 1] = tc11;
faces[index + 2] = p01;
faces[index + 3] = tc01;
faces[index + 4] = p00;
faces[index + 5] = tc00;
}
}
TriangleMesh triangleMesh = new TriangleMesh();
triangleMesh.getPoints().setAll(points);
triangleMesh.getTexCoords().setAll(texCoords);
triangleMesh.getFaces().setAll(faces);
return triangleMesh;
}
private Scene buildScene(int width, int height, boolean depthBuffer) {
triMesh = buildTriangleMesh(divX, divY, MESH_SCALE);
material = new PhongMaterial();
material.setSelfIlluminationMap(selfIllumMap);
meshView = new MeshView(triMesh);
meshView.setMaterial(material);
meshView.setDrawMode(DrawMode.FILL);
meshView.setCullFace(CullFace.BACK);
final Group grp = new Group(meshView);
grp.setTranslateX(width/2);
grp.setTranslateY(height/2);
grp.setTranslateZ(10);
root = new Group(grp, new AmbientLight(Color.BLACK));
Scene scene = new Scene(root, width, height, depthBuffer, SceneAntialiasing.BALANCED);
return scene;
}
private PerspectiveCamera addCamera(Scene scene) {
PerspectiveCamera perspectiveCamera = new PerspectiveCamera();
scene.setCamera(perspectiveCamera);
return perspectiveCamera;
}
private void setArgb(PixelWriter pWriter,
int startX, int endX, int startY, int endY, int value) {
for (int x = startX; x < endX; x++) {
for (int y = startY; y < endY; y++) {
pWriter.setArgb(x, y, value);
}
}
}
}
