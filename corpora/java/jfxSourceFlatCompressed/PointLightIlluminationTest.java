package test.robot.test3d;
import static org.junit.Assume.assumeTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;
import test.robot.testharness.VisualTestBase;
public class PointLightIlluminationTest extends VisualTestBase {
private static final int SCENE_WIDTH_HEIGHT = 100;
private static final int BACKGROUND_PIXEL_X = 1;
private static final int BACKGROUND_PIXEL_Y = 1;
private static final double CORNER_FACTOR = 0.218;
private static final int LEFT_CORNER_X = (int) (SCENE_WIDTH_HEIGHT * CORNER_FACTOR);
private static final int RIGHT_CORNER_X = (int) (SCENE_WIDTH_HEIGHT * (1 - CORNER_FACTOR));
private static final int UPPER_CORNER_Y = (int) (SCENE_WIDTH_HEIGHT * CORNER_FACTOR);
private static final int LOWER_CORNER_Y = (int) (SCENE_WIDTH_HEIGHT * (1 - CORNER_FACTOR));
private static final double COLOR_TOLERANCE = 0.07;
private static volatile Scene testScene = null;
@Before
public void setupEach() {
assumeTrue(Platform.isSupported(ConditionalFeature.SCENE3D));
if (testScene == null) {
runAndWait(() -> {
Stage testStage = getStage();
testScene = createTestScene();
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
}
}
@Test(timeout = 15000)
public void sceneBackgroundColorShouldBeBlue() {
runAndWait(() -> {
assertColorEquals(
Color.BLUE,
getColor(testScene, BACKGROUND_PIXEL_X, BACKGROUND_PIXEL_Y),
COLOR_TOLERANCE);
});
}
@Test(timeout = 15000)
public void sphereUpperLeftPixelColorShouldBeDarkRed() {
runAndWait(() -> {
Color color = getColor(testScene, LEFT_CORNER_X, UPPER_CORNER_Y);
assertColorEquals(Color.DARKRED, color, COLOR_TOLERANCE);
});
}
@Test(timeout = 15000)
public void sphereUpperRightPixelColorShouldBeDarkRed() {
runAndWait(() -> {
Color color = getColor(testScene, RIGHT_CORNER_X, UPPER_CORNER_Y);
assertColorEquals(Color.DARKRED, color, COLOR_TOLERANCE);
});
}
@Test(timeout = 15000)
public void sphereLowerRightPixelColorShouldBeDarkRed() {
runAndWait(() -> {
Color color = getColor(testScene, RIGHT_CORNER_X, LOWER_CORNER_Y);
assertColorEquals(Color.DARKRED, color, COLOR_TOLERANCE);
});
}
@Test(timeout = 15000)
public void sphereLowerLeftPixelColorShouldBeDarkRed() {
runAndWait(() -> {
Color color = getColor(testScene, LEFT_CORNER_X, LOWER_CORNER_Y);
assertColorEquals(Color.DARKRED, color, COLOR_TOLERANCE);
});
}
@Test(timeout = 15000)
public void sphereCenterPixelColorShouldBeRed() {
runAndWait(() -> {
Color color = getColor(testScene, SCENE_WIDTH_HEIGHT / 2, SCENE_WIDTH_HEIGHT / 2);
assertColorEquals(Color.RED, color, COLOR_TOLERANCE);
});
}
@Override
@After
public void doTeardown() {
}
private Scene createTestScene() {
Sphere sphere = new Sphere(SCENE_WIDTH_HEIGHT / 2);
sphere.setTranslateX(SCENE_WIDTH_HEIGHT);
sphere.setTranslateY(SCENE_WIDTH_HEIGHT);
sphere.setMaterial(new PhongMaterial(Color.RED));
SubScene subScene = new SubScene(
new Group(sphere),
SCENE_WIDTH_HEIGHT,
SCENE_WIDTH_HEIGHT,
true,
SceneAntialiasing.DISABLED);
subScene.setFill(Color.BLUE);
PerspectiveCamera perspectiveCamera = new PerspectiveCamera(true);
perspectiveCamera.setTranslateX(SCENE_WIDTH_HEIGHT);
perspectiveCamera.setTranslateY(SCENE_WIDTH_HEIGHT);
perspectiveCamera.setTranslateZ(-2 * SCENE_WIDTH_HEIGHT);
perspectiveCamera.setFarClip(4 * SCENE_WIDTH_HEIGHT);
subScene.setCamera(perspectiveCamera);
return new Scene(new Group(subScene), SCENE_WIDTH_HEIGHT, SCENE_WIDTH_HEIGHT);
}
}
