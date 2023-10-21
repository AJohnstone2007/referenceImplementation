package test.robot.test3d;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import test.robot.testharness.VisualTestBase;
import static org.junit.Assume.assumeTrue;
public class NearAndFarClipTest extends VisualTestBase {
private Stage testStage;
private Scene testScene;
private static final double TOLERANCE = 0.07;
private static final double OFFSET_PERCENT = 0.01;
@Before
public void setupEach() {
assumeTrue(Platform.isSupported(ConditionalFeature.SCENE3D));
}
@Test(timeout = 15000)
public void testNearAndFarClips() {
final int WIDTH = 500;
final int HEIGHT = 500;
final double FOV = 30.0;
final double NEAR = 0.1;
final double FAR = 10.0;
runAndWait(() -> {
testStage = getStage();
testStage.setTitle("Near and Far Clip Test");
final double tanOfHalfFOV = Math.tan(Math.toRadians(FOV) / 2.0);
final double halfHeight = HEIGHT / 2;
final double focalLength = halfHeight / tanOfHalfFOV;
final double eyePositionZ = -1.0 * focalLength;
final double nearClipDistance = focalLength * NEAR + eyePositionZ;
final double farClipDistance = focalLength * FAR + eyePositionZ;
final double nearClipDistanceOffset = Math.abs(nearClipDistance * OFFSET_PERCENT);
final double farClipDistanceOffset = Math.abs(farClipDistance * OFFSET_PERCENT);
Rectangle insideRect = new Rectangle(220, 220, Color.GREEN);
insideRect.setLayoutX(140);
insideRect.setLayoutY(140);
Rectangle insideNearClip = new Rectangle(16, 16, Color.BLUE);
insideNearClip.setLayoutX(242);
insideNearClip.setLayoutY(242);
insideNearClip.setTranslateZ(nearClipDistance + nearClipDistanceOffset);
Rectangle outsideNearClip = new Rectangle(16, 16, Color.YELLOW);
outsideNearClip.setLayoutX(242);
outsideNearClip.setLayoutY(242);
outsideNearClip.setTranslateZ(nearClipDistance - nearClipDistanceOffset);
Rectangle insideFarClip = new Rectangle(3000, 3000, Color.RED);
insideFarClip.setTranslateX(-1250);
insideFarClip.setTranslateY(-1250);
insideFarClip.setTranslateZ(farClipDistance - farClipDistanceOffset);
Rectangle outsideFarClip = new Rectangle(4000, 4000, Color.CYAN);
outsideFarClip.setTranslateX(-1750);
outsideFarClip.setTranslateY(-1750);
outsideFarClip.setTranslateZ(farClipDistance + farClipDistanceOffset);
Group root = new Group();
root.getChildren().addAll(outsideFarClip, insideFarClip, insideRect, insideNearClip, outsideNearClip);
testScene = new Scene(root, WIDTH, HEIGHT, false);
PerspectiveCamera camera = new PerspectiveCamera();
camera.setFieldOfView(FOV);
camera.setNearClip(NEAR);
camera.setFarClip(FAR);
testScene.setCamera(camera);
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
runAndWait(() -> {
Color color;
color = getColor(testScene, WIDTH / 2, HEIGHT / 2);
assertColorEquals(Color.BLUE, color, TOLERANCE);
color = getColor(testScene, (WIDTH / 3), HEIGHT / 2);
assertColorEquals(Color.GREEN, color, TOLERANCE);
color = getColor(testScene, WIDTH / 5, HEIGHT / 2);
assertColorEquals(Color.RED, color, TOLERANCE);
color = getColor(testScene, WIDTH / 8, HEIGHT / 2);
assertColorEquals(Color.WHITE, color, TOLERANCE);
});
}
}
