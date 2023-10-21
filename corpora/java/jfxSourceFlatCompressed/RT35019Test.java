package test.robot.test3d;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import test.robot.testharness.VisualTestBase;
import static org.junit.Assume.assumeTrue;
public class RT35019Test extends VisualTestBase {
private Stage testStage;
private Scene testScene;
private static final double TOLERANCE = 0.07;
@Before
public void setupEach() {
assumeTrue(Platform.isSupported(ConditionalFeature.SCENE3D));
}
@Test(timeout = 15000)
public void testEmptyShapes() {
final int WIDTH = 400;
final int HEIGHT = 300;
runAndWait(() -> {
Circle emptyCircle = new Circle(10, 10, 0);
Text emptyText = new Text(10, 10, "");
Circle circle = new Circle(100, 100, 10);
circle.setFill(Color.DARKBLUE);
Group root = new Group(emptyCircle, emptyText, circle);
root.setRotationAxis(Rotate.Y_AXIS);
root.setRotate(1);
testStage = getStage();
testStage.setTitle("Empty Shapes + 3D Transform");
testScene = new Scene(root, WIDTH, HEIGHT);
testScene.setCamera(new PerspectiveCamera());
testScene.setFill(Color.WHITE);
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, 10, 10);
assertColorEquals(Color.WHITE, color, TOLERANCE);
color = getColor(testScene, 100, 100);
assertColorEquals(Color.DARKBLUE, color, TOLERANCE);
});
}
}
