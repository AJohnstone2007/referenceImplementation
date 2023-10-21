package test.robot.javafx.scene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.robot.Robot;
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
public class ColorPickerTest {
static CountDownLatch startupLatch = new CountDownLatch(1);
static Robot robot;
static volatile Stage stage;
static volatile Scene scene;
static final int SCENE_WIDTH = 250;
static final int SCENE_HEIGHT = SCENE_WIDTH;
static VBox root;
int onShownCount = 0;
int onActionCount = 0;
ColorPicker colorPicker;
CountDownLatch onShownLatch;
CountDownLatch onActionLatch;
private void mouseClick(double x, double y) {
Util.runAndWait(() -> {
robot.mouseMove((int) (scene.getWindow().getX() + scene.getX() + x),
(int) (scene.getWindow().getY() + scene.getY() + y));
robot.mousePress(MouseButton.PRIMARY);
robot.mouseRelease(MouseButton.PRIMARY);
});
}
private void showColorPickerPalette() throws Exception {
onShownLatch = new CountDownLatch(1);
mouseClick(colorPicker.getLayoutX() + colorPicker.getWidth() - 15,
colorPicker.getLayoutY() + colorPicker.getHeight() / 2);
Thread.sleep(400);
waitForLatch(onShownLatch, 10, "Failed to show color palette.");
}
private void clickColorPickerPalette(int yFactor) throws Exception {
onActionLatch = new CountDownLatch(1);
mouseClick(colorPicker.getLayoutX() + colorPicker.getWidth() / 2,
colorPicker.getLayoutY() + colorPicker.getHeight() * yFactor);
Thread.sleep(400);
waitForLatch(onActionLatch, 10, "Failed to receive onAction callback.");
}
@Test
public void testColorPickerSceneChange() throws Exception {
Thread.sleep(1000);
onShownLatch = new CountDownLatch(1);
Util.runAndWait(() -> {
root.getChildren().clear();
colorPicker.show();
root.getChildren().add(colorPicker);
});
waitForLatch(onShownLatch, 10, "Failed to show color palette.");
Thread.sleep(400);
Assert.assertEquals("ColorPicker palette should be shown once.", 1, onShownCount);
clickColorPickerPalette(5);
Assert.assertEquals("ColorPicker palette should be clicked once.", 1, onActionCount);
showColorPickerPalette();
Assert.assertEquals("ColorPicker palette should have been shown two times.", 2, onShownCount);
clickColorPickerPalette(6);
Assert.assertEquals("ColorPicker palette have been clicked two times.", 2, onActionCount);
showColorPickerPalette();
Assert.assertEquals("ColorPicker palette should have been shown three times.", 3, onShownCount);
Util.runAndWait(() -> {
root.getChildren().clear();
root.getChildren().add(colorPicker);
});
Thread.sleep(400);
clickColorPickerPalette(5);
Assert.assertEquals("ColorPicker palette should have been clicked three times.", 3, onActionCount);
}
@After
public void resetUI() {
Platform.runLater(() -> {
colorPicker.setOnShown(null);
colorPicker.setOnAction(null);
root.getChildren().clear();
});
}
@Before
public void setupUI() {
Platform.runLater(() -> {
colorPicker = new ColorPicker();
colorPicker.setOnShown(event -> {
onShownCount++;
onShownLatch.countDown();
});
colorPicker.setOnAction(event -> {
onActionCount++;
onActionLatch.countDown();
});
root.getChildren().add(colorPicker);
});
}
@BeforeClass
public static void initFX() throws Exception {
new Thread(() -> Application.launch(TestApp.class, (String[])null)).start();
waitForLatch(startupLatch, 10, "FX runtime failed to start.");
}
@AfterClass
public static void exit() {
Platform.runLater(() -> {
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
