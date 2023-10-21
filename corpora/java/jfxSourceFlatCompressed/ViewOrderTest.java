package test.robot.scenegraph;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.junit.Test;
import test.robot.testharness.VisualTestBase;
public class ViewOrderTest extends VisualTestBase {
private Stage testStage;
private Scene testScene;
private static final double TOLERANCE = 0.07;
@Test(timeout = 15000)
public void testViewOrder() {
final int WIDTH = 300;
final int HEIGHT = 300;
final Pane rectsPane = new Pane();
final Rectangle redRect = new Rectangle(150, 150, Color.RED);
redRect.setViewOrder(0);
redRect.relocate(20, 10);
final Rectangle greenRect = new Rectangle(150, 150, Color.GREEN);
greenRect.setViewOrder(-1);
greenRect.relocate(100, 50);
final Rectangle blueRect = new Rectangle(150, 150, Color.BLUE);
blueRect.setViewOrder(1);
blueRect.relocate(60, 100);
rectsPane.getChildren().addAll(redRect, greenRect, blueRect);
runAndWait(() -> {
Group root = new Group(rectsPane);
testStage = getStage();
testStage.setTitle("Test View Order");
testScene = new Scene(root, WIDTH, HEIGHT);
testScene.setFill(Color.WHITE);
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, 30, 20);
assertColorEquals(Color.RED, color, TOLERANCE);
color = getColor(testScene, 120, 100);
assertColorEquals(Color.GREEN, color, TOLERANCE);
color = getColor(testScene, 120, 240);
assertColorEquals(Color.BLUE, color, TOLERANCE);
redRect.setViewOrder(-1.5);
});
waitNextFrame();
runAndWait(() -> {
Color color = getColor(testScene, 120, 100);
assertColorEquals(Color.RED, color, TOLERANCE);
redRect.setViewOrder(1.5);
greenRect.setViewOrder(1);
});
waitNextFrame();
runAndWait(() -> {
Color color = getColor(testScene, 120, 100);
assertColorEquals(Color.BLUE, color, TOLERANCE);
});
}
@Test(timeout = 15000)
public void testViewOrderHBox() {
final int WIDTH = 500;
final int HEIGHT = 200;
final HBox rectsPane = new HBox();
final Rectangle redRect = new Rectangle(150, 150, Color.RED);
redRect.setViewOrder(0);
final Rectangle greenRect = new Rectangle(150, 150, Color.GREEN);
greenRect.setViewOrder(-1);
final Rectangle blueRect = new Rectangle(150, 150, Color.BLUE);
blueRect.setViewOrder(1);
rectsPane.getChildren().addAll(redRect, greenRect, blueRect);
runAndWait(() -> {
Group root = new Group(rectsPane);
testStage = getStage();
testStage.setTitle("Test View Order in HBox");
testScene = new Scene(root, WIDTH, HEIGHT);
testScene.setFill(Color.WHITE);
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, 75, 75);
assertColorEquals(Color.RED, color, TOLERANCE);
color = getColor(testScene, 225, 75);
assertColorEquals(Color.GREEN, color, TOLERANCE);
color = getColor(testScene, 380, 75);
assertColorEquals(Color.BLUE, color, TOLERANCE);
redRect.setViewOrder(-1.5);
});
waitNextFrame();
runAndWait(() -> {
Color color = getColor(testScene, 75, 75);
assertColorEquals(Color.RED, color, TOLERANCE);
color = getColor(testScene, 225, 75);
assertColorEquals(Color.GREEN, color, TOLERANCE);
color = getColor(testScene, 380, 75);
assertColorEquals(Color.BLUE, color, TOLERANCE);
});
}
}
