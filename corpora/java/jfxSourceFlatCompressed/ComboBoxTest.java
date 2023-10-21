package test.robot.javafx.scene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.robot.Robot;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.fail;
import test.util.Util;
public class ComboBoxTest {
static CountDownLatch startupLatch = new CountDownLatch(1);
static Robot robot;
static volatile Stage stage;
static volatile Scene scene;
static final int SCENE_WIDTH = 200;
static final int SCENE_HEIGHT = SCENE_WIDTH;
static VBox root;
int onShownCount = 0;
int onSelectedCount = 0;
final int ITEM_COUNT = 2;
ComboBox comboBox;
CountDownLatch showLatch;
CountDownLatch selectedLatch;
private void mouseClick(double x, double y) {
Util.runAndWait(() -> {
robot.mouseMove((int) (scene.getWindow().getX() + scene.getX() + x),
(int) (scene.getWindow().getY() + scene.getY() + y));
robot.mousePress(MouseButton.PRIMARY);
robot.mouseRelease(MouseButton.PRIMARY);
});
}
@Test
public void testComboBoxSceneChange1() throws Exception {
Thread.sleep(1000);
comboBox.setOnShown(event -> {
onShownCount++;
showLatch.countDown();
});
ChangeListener chListener = (observable, oldValue, newValue) -> {
onSelectedCount++;
root.getChildren().clear();
comboBox.show();
root.getChildren().add(comboBox);
selectedLatch.countDown();
};
comboBox.getSelectionModel().selectedItemProperty().addListener(chListener);
showLatch = new CountDownLatch(1);
mouseClick(comboBox.getLayoutX() + comboBox.getWidth() / 2,
comboBox.getLayoutY() + comboBox.getHeight() / 2);
for (int i = 0; i < ITEM_COUNT; i++) {
Thread.sleep(300);
waitForLatch(showLatch, 10, "Failed to show ComboBox popup list. " + i);
showLatch = new CountDownLatch(1);
selectedLatch = new CountDownLatch(1);
final int k = i;
mouseClick(comboBox.getLayoutX() + comboBox.getWidth() / 2,
comboBox.getLayoutY() + comboBox.getHeight() * (k + 1.2f));
waitForLatch(selectedLatch, 10, "Failed to select " + i + "th choice.");
}
Assert.assertEquals("ComboBox popup list should have been displayed " +
(ITEM_COUNT + 1) + " times.", (ITEM_COUNT + 1), onShownCount);
Assert.assertEquals("ComboBox choice should have been selected " +
ITEM_COUNT + " times.", ITEM_COUNT, onSelectedCount);
}
@Test
public void testComboBoxSceneChange2() throws Exception {
Thread.sleep(1000);
comboBox.setOnShown(event -> {
onShownCount++;
showLatch.countDown();
});
ChangeListener chListener = (observable, oldValue, newValue) -> {
onSelectedCount++;
root.getChildren().clear();
root.getChildren().add(comboBox);
selectedLatch.countDown();
};
comboBox.getSelectionModel().selectedItemProperty().addListener(chListener);
for (int i = 0; i < ITEM_COUNT; i++) {
Thread.sleep(300);
showLatch = new CountDownLatch(1);
selectedLatch = new CountDownLatch(1);
mouseClick(comboBox.getLayoutX() + comboBox.getWidth() / 2,
comboBox.getLayoutY() + comboBox.getHeight() / 2);
Thread.sleep(200);
waitForLatch(showLatch, 10, "Failed to show ComboBox popup list. " + i);
final int k = i;
mouseClick(comboBox.getLayoutX() + comboBox.getWidth() / 2,
comboBox.getLayoutY() + comboBox.getHeight() * (k + 1.2f));
waitForLatch(selectedLatch, 10, "Failed to select " + i + "th choice.");
}
Assert.assertEquals("ComboBox popup list should be displayed " +
ITEM_COUNT + " times.", ITEM_COUNT, onShownCount);
Assert.assertEquals("ComboBox choice should have been selected " +
ITEM_COUNT + " times.", ITEM_COUNT, onSelectedCount);
}
@After
public void resetUI() {
Util.runAndWait(() -> {
root.getChildren().clear();
});
}
@Before
public void setupUI() {
Util.runAndWait(() -> {
comboBox = new ComboBox();
for (int i = 0; i < ITEM_COUNT; i++) {
comboBox.getItems().add("Op" + i);
}
root.getChildren().add(comboBox);
onShownCount = 0;
onSelectedCount = 0;
});
}
@BeforeClass
public static void initFX() throws Exception {
new Thread(() -> Application.launch(TestApp.class, (String[])null)).start();
waitForLatch(startupLatch, 10, "FX runtime failed to start.");
}
@AfterClass
public static void exit() {
Util.runAndWait(() -> {
stage.hide();
});
Platform.exit();
}
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) {
robot = new Robot();
stage = primaryStage;
root = new VBox();
scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
stage.setScene(scene);
stage.initStyle(StageStyle.UNDECORATED);
stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e ->
Platform.runLater(startupLatch::countDown));
stage.setAlwaysOnTop(true);
stage.show();
}
}
public static void waitForLatch(CountDownLatch latch, int seconds, String msg) throws Exception {
Assert.assertTrue("Timeout: " + msg, latch.await(seconds, TimeUnit.SECONDS));
}
}
