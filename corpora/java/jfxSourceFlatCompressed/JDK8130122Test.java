package test.robot.scenegraph;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.junit.Test;
import test.robot.testharness.VisualTestBase;
public class JDK8130122Test extends VisualTestBase {
private Stage testStage;
private Scene testScene;
private static final double TOLERANCE = 0.07;
@Test(timeout = 15000)
public void testEmptyShapes() {
final int WIDTH = 800;
final int HEIGHT = 400;
final ObservableList<Rectangle> data = FXCollections.<Rectangle>observableArrayList();
data.addAll(new Rectangle(100, 100, Color.RED), new Rectangle(100, 100, Color.BLUE),
new Rectangle(100, 100, Color.RED), new Rectangle(100, 100, Color.BLUE),
new Rectangle(100, 100, Color.RED), new Rectangle(100, 100, Color.BLUE),
new Rectangle(100, 100, Color.RED), new Rectangle(100, 100, Color.BLUE));
final ListView<Rectangle> horizontalListView = new ListView<Rectangle>();
runAndWait(() -> {
final GridPane gridPane = new GridPane();
gridPane.setPrefWidth(WIDTH);
gridPane.setPrefHeight(HEIGHT);
horizontalListView.setOrientation(Orientation.HORIZONTAL);
horizontalListView.setItems(data);
gridPane.add(horizontalListView, 0, 0);
horizontalListView.setVisible(false);
GridPane.setVgrow(horizontalListView, Priority.ALWAYS);
GridPane.setHgrow(horizontalListView, Priority.ALWAYS);
Group root = new Group(gridPane);
testStage = getStage();
testStage.setTitle("Test bounds update of invisible node");
testScene = new Scene(root, WIDTH, HEIGHT);
testScene.setCamera(new PerspectiveCamera());
testScene.setFill(Color.WHITE);
testStage.setScene(testScene);
testStage.show();
});
waitFirstFrame();
runAndWait(() -> {
Color color = getColor(testScene, 200, 250);
assertColorEquals(Color.WHITE, color, TOLERANCE);
data.add(0, new Rectangle(250, 150, Color.GREEN));
});
waitNextFrame();
runAndWait(() -> {
Color color = getColor(testScene, 200, 250);
assertColorEquals(Color.WHITE, color, TOLERANCE);
horizontalListView.setVisible(true);
});
waitNextFrame();
runAndWait(() -> {
Color color = getColor(testScene, 200, 250);
assertColorEquals(Color.GREEN, color, TOLERANCE);
});
}
}
