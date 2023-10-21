package test.com.sun.prism.impl;
import com.sun.javafx.sg.prism.NGTriangleMesh;
import com.sun.javafx.sg.prism.NGTriangleMeshShim;
import com.sun.prism.impl.BaseMesh;
import com.sun.prism.impl.BaseMeshShim;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.TriangleMeshShim;
import javafx.scene.shape.VertexFormat;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import junit.framework.AssertionFailedError;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import test.util.Util;
import static test.util.Util.TIMEOUT;
public class PNTMeshVertexBufferLengthTest {
private static final int SLEEP_TIME = 1000;
private static final int VERTEX_SIZE = 9;
private static final float meshScale = 15;
private static final float minX = -10;
private static final float minY = -10;
private static final float maxX = 10;
private static final float maxY = 10;
private static final float funcValue = -10.0f;
private static final Vec3f v1 = new Vec3f();
private static final Vec3f v2 = new Vec3f();
private static void computeNormal(Vec3f pa, Vec3f pb, Vec3f pc, Vec3f normal) {
v1.sub(pb, pa);
v2.sub(pc, pa);
normal.cross(v1, v2);
normal.normalize();
}
private static double getSinDivX(double x, double y) {
double r = Math.sqrt(x * x + y * y);
return funcValue * (r == 0 ? 1 : Math.sin(r) / r);
}
private static void buildTriangleMesh(MeshView meshView,
int subDivX, int subDivY, float scale) {
final int pointSize = 3;
final int normalSize = 3;
final int texCoordSize = 2;
final int faceSize = 9;
int numDivX = subDivX + 1;
int numVerts = (subDivY + 1) * numDivX;
float points[] = new float[numVerts * pointSize];
float texCoords[] = new float[numVerts * texCoordSize];
int faceCount = subDivX * subDivY * 2;
float normals[] = new float[faceCount * normalSize];
int faces[] = new int[faceCount * faceSize];
for (int y = 0; y <= subDivY; y++) {
float dy = (float) y / subDivY;
double fy = (1 - dy) * minY + dy * maxY;
for (int x = 0; x <= subDivX; x++) {
float dx = (float) x / subDivX;
double fx = (1 - dx) * minX + dx * maxX;
int index = y * numDivX * pointSize + (x * pointSize);
points[index] = (float) fx * scale;
points[index + 1] = (float) fy * scale;
points[index + 2] = (float) getSinDivX(fx, fy) * scale;
index = y * numDivX * texCoordSize + (x * texCoordSize);
texCoords[index] = dx;
texCoords[index + 1] = dy;
}
}
int normalCount = 0;
Vec3f[] triPoints = new Vec3f[3];
triPoints[0] = new Vec3f();
triPoints[1] = new Vec3f();
triPoints[2] = new Vec3f();
Vec3f normal = new Vec3f();
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
int ii = p00 * 3;
triPoints[0].x = points[ii];
triPoints[0].y = points[ii + 1];
triPoints[0].z = points[ii + 2];
ii = p10 * 3;
triPoints[1].x = points[ii];
triPoints[1].y = points[ii + 1];
triPoints[1].z = points[ii + 2];
ii = p11 * 3;
triPoints[2].x = points[ii];
triPoints[2].y = points[ii + 1];
triPoints[2].z = points[ii + 2];
computeNormal(triPoints[0], triPoints[1], triPoints[2], normal);
int normalIndex = normalCount * normalSize;
normals[normalIndex] = normal.x;
normals[normalIndex + 1] = normal.y;
normals[normalIndex + 2] = normal.z;
int index = (y * subDivX * faceSize + (x * faceSize)) * 2;
faces[index + 0] = p00;
faces[index + 1] = normalCount;
faces[index + 2] = tc00;
faces[index + 3] = p10;
faces[index + 4] = normalCount;
faces[index + 5] = tc10;
faces[index + 6] = p11;
faces[index + 7] = normalCount++;
faces[index + 8] = tc11;
index += faceSize;
ii = p11 * 3;
triPoints[0].x = points[ii];
triPoints[0].y = points[ii + 1];
triPoints[0].z = points[ii + 2];
ii = p01 * 3;
triPoints[1].x = points[ii];
triPoints[1].y = points[ii + 1];
triPoints[1].z = points[ii + 2];
ii = p00 * 3;
triPoints[2].x = points[ii];
triPoints[2].y = points[ii + 1];
triPoints[2].z = points[ii + 2];
computeNormal(triPoints[0], triPoints[1], triPoints[2], normal);
normalIndex = normalCount * normalSize;
normals[normalIndex] = normal.x;
normals[normalIndex + 1] = normal.y;
normals[normalIndex + 2] = normal.z;
faces[index + 0] = p11;
faces[index + 1] = normalCount;
faces[index + 2] = tc11;
faces[index + 3] = p01;
faces[index + 4] = normalCount;
faces[index + 5] = tc01;
faces[index + 6] = p00;
faces[index + 7] = normalCount++;
faces[index + 8] = tc00;
}
}
TriangleMesh triangleMesh = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
triangleMesh.getPoints().setAll(points);
triangleMesh.getNormals().setAll(normals);
triangleMesh.getTexCoords().setAll(texCoords);
triangleMesh.getFaces().setAll(faces);
meshView.setMesh(triangleMesh);
}
private static final CountDownLatch launchLatch = new CountDownLatch(1);
static MyApp myApp;
private CountDownLatch latch = new CountDownLatch(1);
public static class MyApp extends Application {
Stage primaryStage = null;
MeshView meshView;
@Override
public void init() {
PNTMeshVertexBufferLengthTest.myApp = this;
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setTitle("PNTMeshVertexBufferLengthTest");
TriangleMesh triangleMesh = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
meshView = new MeshView(triangleMesh);
Group rotateGrp = new Group(meshView);
rotateGrp.setRotate(-30);
rotateGrp.setRotationAxis(Rotate.X_AXIS);
Group translateGrp = new Group(rotateGrp);
translateGrp.setTranslateX(200);
translateGrp.setTranslateY(200);
translateGrp.setTranslateZ(100);
Group root = new Group(translateGrp);
Scene scene = new Scene(root);
primaryStage.setScene(scene);
primaryStage.setX(0);
primaryStage.setY(0);
primaryStage.setWidth(400);
primaryStage.setHeight(400);
PerspectiveCamera perspectiveCamera = new PerspectiveCamera();
scene.setCamera(perspectiveCamera);
primaryStage.show();
this.primaryStage = primaryStage;
launchLatch.countDown();
}
}
@BeforeClass
public static void setupOnce() {
new Thread(() -> Application.launch(MyApp.class, (String[]) null)).start();
try {
if (!launchLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Application to launch");
}
} catch (InterruptedException ex) {
AssertionFailedError err = new AssertionFailedError("Unexpected exception");
err.initCause(ex);
throw err;
}
assertEquals(0, launchLatch.getCount());
}
@AfterClass
public static void teardownOnce() {
Platform.exit();
}
@Before
public void setupEach() {
assumeTrue(Platform.isSupported(ConditionalFeature.SCENE3D));
}
@Test(timeout = 15000)
public void testMeshWithZeroDiv() throws InterruptedException {
Util.runAndWait(() -> {
Scene scene = myApp.primaryStage.getScene();
buildTriangleMesh(myApp.meshView, 0, 0, meshScale);
});
Thread.sleep(SLEEP_TIME);
NGTriangleMesh ngTriMesh = TriangleMeshShim.getNGMesh(myApp.meshView.getMesh());
assertNotNull(ngTriMesh);
BaseMesh baseMesh = NGTriangleMeshShim.test_getMesh(ngTriMesh);
assertNotNull(baseMesh);
assertEquals(0, BaseMeshShim.test_getNumberOfVertices(baseMesh));
assertTrue(BaseMeshShim.test_isVertexBufferNull(baseMesh));
}
@Test(timeout = 15000)
public void testMeshWithOneDiv() throws InterruptedException {
Util.runAndWait(() -> {
Scene scene = myApp.primaryStage.getScene();
buildTriangleMesh(myApp.meshView, 1, 1, meshScale);
});
Thread.sleep(SLEEP_TIME);
NGTriangleMesh ngTriMesh = TriangleMeshShim.getNGMesh(myApp.meshView.getMesh());
assertNotNull(ngTriMesh);
BaseMesh baseMesh = NGTriangleMeshShim.test_getMesh(ngTriMesh);
assertNotNull(baseMesh);
assertEquals(6, BaseMeshShim.test_getNumberOfVertices(baseMesh));
assertEquals(10 * VERTEX_SIZE, BaseMeshShim.test_getVertexBufferLength(baseMesh));
}
@Test(timeout = 15000)
public void testMeshWithTwoDiv() throws InterruptedException {
Util.runAndWait(() -> {
Scene scene = myApp.primaryStage.getScene();
buildTriangleMesh(myApp.meshView, 2, 2, meshScale);
});
Thread.sleep(SLEEP_TIME);
NGTriangleMesh ngTriMesh = TriangleMeshShim.getNGMesh(myApp.meshView.getMesh());
assertNotNull(ngTriMesh);
BaseMesh baseMesh = NGTriangleMeshShim.test_getMesh(ngTriMesh);
assertNotNull(baseMesh);
assertEquals(24, BaseMeshShim.test_getNumberOfVertices(baseMesh));
assertEquals(27 * VERTEX_SIZE, BaseMeshShim.test_getVertexBufferLength(baseMesh));
}
@Test(timeout = 15000)
public void testMeshWithThreeDiv() throws InterruptedException {
Util.runAndWait(() -> {
Scene scene = myApp.primaryStage.getScene();
buildTriangleMesh(myApp.meshView, 7, 7, meshScale);
});
Thread.sleep(SLEEP_TIME);
NGTriangleMesh ngTriMesh = TriangleMeshShim.getNGMesh(myApp.meshView.getMesh());
assertNotNull(ngTriMesh);
BaseMesh baseMesh = NGTriangleMeshShim.test_getMesh(ngTriMesh);
assertNotNull(baseMesh);
assertEquals(294, BaseMeshShim.test_getNumberOfVertices(baseMesh));
assertEquals(325 * VERTEX_SIZE, BaseMeshShim.test_getVertexBufferLength(baseMesh));
}
@Test(timeout = 15000)
public void testMeshWithFiveDiv() throws InterruptedException {
Util.runAndWait(() -> {
Scene scene = myApp.primaryStage.getScene();
buildTriangleMesh(myApp.meshView, 50, 50, meshScale);
});
Thread.sleep(SLEEP_TIME);
NGTriangleMesh ngTriMesh = TriangleMeshShim.getNGMesh(myApp.meshView.getMesh());
assertNotNull(ngTriMesh);
BaseMesh baseMesh = NGTriangleMeshShim.test_getMesh(ngTriMesh);
assertNotNull(baseMesh);
assertEquals(15000, BaseMeshShim.test_getNumberOfVertices(baseMesh));
assertEquals(15201 * VERTEX_SIZE, BaseMeshShim.test_getVertexBufferLength(baseMesh));
}
}
