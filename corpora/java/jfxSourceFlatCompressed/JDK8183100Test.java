package test.robot.javafx.scene;
import test.com.sun.javafx.scene.control.infrastructure.KeyEventFirer;
import test.com.sun.javafx.scene.control.infrastructure.KeyModifier;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import test.robot.testharness.VisualTestBase;
import static org.junit.Assert.assertEquals;
public class JDK8183100Test extends VisualTestBase {
private Stage testStage;
private Scene testScene;
private int count = 0;
private final double TOLERANCE = 0.07;
static List<TabContents> TAB_CONTENTS = new ArrayList<>();
class TabContents extends StackPane {
public TabContents() {
setStyle("-fx-background-color: blue");
Platform.runLater(() -> {
TAB_CONTENTS.add(this);
TAB_CONTENTS.forEach(TabContents::fillWithFreshYellowPane);
});
}
void fillWithFreshYellowPane() {
Pane yellowPane = new Pane();
yellowPane.setStyle("-fx-background-color: yellow");
getChildren().setAll(yellowPane);
}
}
@Test(timeout = 15000)
public void stackPaneColorTest() {
final int WIDTH = 200;
final int HEIGHT = 100;
Button addTabButton = new Button("Add tab");
HBox tabBar = new HBox(addTabButton);
StackPane container = new StackPane();
container.setStyle("-fx-background-color: red");
VBox.setVgrow(container, Priority.ALWAYS);
VBox root = new VBox(tabBar, container);
ToggleGroup group = new ToggleGroup();
addTabButton.setOnAction(unused -> {
ToggleButton tb = new ToggleButton("Tab "+count);
count++;
TabContents contents = new TabContents();
runAndWait(() -> {
tb.setToggleGroup(group);
tb.setSelected(true);
tabBar.getChildren().add(tb);
container.getChildren().setAll(contents);
});
if (count == 1) {
tb.getScene().getAccelerators().put(
new KeyCodeCombination(KeyCode.DIGIT0, KeyCodeCombination.CONTROL_DOWN),
new Runnable() {
@Override public void run() {
tb.fire();
}
});
}
tb.setOnAction(actionEvent -> {
container.getChildren().setAll(contents);
});
});
runAndWait(() -> {
testStage = getStage();
testScene = new Scene(root, WIDTH, HEIGHT);
testStage.setScene(testScene);
addTabButton.getScene().getAccelerators().put(
new KeyCodeCombination(KeyCode.A, KeyCodeCombination.CONTROL_DOWN),
new Runnable() {
@Override public void run() {
addTabButton.fire();
}
});
testStage.show();
});
waitFirstFrame();
KeyEventFirer keyboard = new KeyEventFirer(testScene);
for (int i = 0; i < 3; i++) {
keyboard.doKeyPress(KeyCode.A, KeyModifier.CTRL);
assertEquals(i+1, count);
}
runAndWait(() -> {
keyboard.doKeyPress(KeyCode.DIGIT0, KeyModifier.CTRL);
});
runAndWait(() -> {
Color color = getColor(testScene, 100, 50);
assertColorEquals(Color.YELLOW, color, TOLERANCE);
});
}
}
