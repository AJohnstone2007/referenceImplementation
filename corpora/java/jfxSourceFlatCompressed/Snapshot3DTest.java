package test.robot.test3d;
import java.util.ArrayList;
import java.util.Collection;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import test.robot.testharness.VisualTestBase;
import static org.junit.Assume.assumeTrue;
@RunWith(Parameterized.class)
public class Snapshot3DTest extends VisualTestBase {
private static Collection params = null;
private static final Object[] pUseSphere = { Boolean.FALSE, Boolean.TRUE };
private static final Object[] pNumLights = { 0, 1, 2, 3 };
@Parameterized.Parameters
public static Collection getParams() {
if (params == null) {
params = new ArrayList();
for (Object o0 : pUseSphere) {
for (Object o1 : pNumLights) {
params.add(new Object[] { o0, o1 });
}
}
}
return params;
}
private static final double TOLERANCE = 0.07;
private static final int WIDTH = 400;
private static final int HEIGHT = 400;
private static final int SAMPLE_X1 = 100;
private static final int SAMPLE_Y1 = 100;
private static final int SAMPLE_X2 = 200;
private static final int SAMPLE_Y2 = 200;
private static final int SAMPLE_X3 = 300;
private static final int SAMPLE_Y3 = 300;
private static final Color bgColor = Color.rgb(10, 10, 40);
private Stage testStage;
private Scene testScene;
private WritableImage wImage;
private boolean createSphere;
private int numLights;
public Snapshot3DTest(boolean createSphere, int numLights) {
this.createSphere = createSphere;
this.numLights = numLights;
}
private Scene buildScene() {
Group root = new Group();
PhongMaterial material = new PhongMaterial();
material.setDiffuseColor(Color.WHITE);
material.setSpecularColor(null);
Shape3D shape;
if (createSphere) {
shape = new Sphere(200);
} else {
shape = new Box(300, 300, 300);
}
shape.setTranslateX(200);
shape.setTranslateY(200);
shape.setTranslateZ(10);
shape.setMaterial(material);
root.getChildren().add(shape);
if (numLights >= 1) {
AmbientLight ambLight = new AmbientLight(Color.LIMEGREEN);
root.getChildren().add(ambLight);
}
if (numLights >= 2) {
PointLight pointLight = new PointLight(Color.RED);
pointLight.setTranslateX(75);
pointLight.setTranslateY(-50);
pointLight.setTranslateZ(-200);
root.getChildren().add(pointLight);
}
if (numLights >= 3) {
PointLight pointLight = new PointLight(Color.BLUE);
pointLight.setTranslateX(225);
pointLight.setTranslateY(50);
pointLight.setTranslateZ(-300);
root.getChildren().add(pointLight);
}
PerspectiveCamera camera = new PerspectiveCamera();
Scene scene = new Scene(root, WIDTH, HEIGHT, false);
scene.setFill(bgColor);
scene.setCamera(camera);
return scene;
}
private void compareColors(Scene scene, WritableImage wImage, int x, int y) {
Color exColor = getColor(scene, x, y);
Color sColor = wImage.getPixelReader().getColor(x, y);
assertColorEquals(exColor, sColor, TOLERANCE);
}
@Before
public void setupEach() {
assumeTrue(Platform.isSupported(ConditionalFeature.SCENE3D));
}
@Test(timeout = 15000)
public void testSnapshot3D() {
runAndWait(() -> {
testStage = getStage();
testStage.setTitle("Snapshot 3D Test");
testScene = buildScene();
wImage = testScene.snapshot(null);
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
runAndWait(() -> {
compareColors(testScene, wImage, SAMPLE_X1, SAMPLE_Y1);
compareColors(testScene, wImage, SAMPLE_X2, SAMPLE_Y2);
compareColors(testScene, wImage, SAMPLE_X3, SAMPLE_Y3);
});
}
}
