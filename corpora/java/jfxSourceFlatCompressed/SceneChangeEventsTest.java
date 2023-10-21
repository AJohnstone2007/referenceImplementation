package test.robot.javafx.scene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.fail;
public class SceneChangeEventsTest {
static CountDownLatch startupLatch;
static Robot robot;
static volatile Stage stage;
boolean mouseExited = false;
boolean mouseWindowEventOrder = false;
boolean windowChanged = false;
public static void main(String[] args) {
SceneChangeEventsTest test = new SceneChangeEventsTest();
test.testSceneChange();
exit();
}
@Test
public void testSceneChange() {
Button button = new Button("onAction");
CountDownLatch onActionLatch = new CountDownLatch(1);
button.setOnAction(event -> {
stage.setScene(new Scene(new HBox()));
onActionLatch.countDown();
});
HBox root = new HBox();
root.getChildren().add(button);
Scene scene = new Scene(root);
CountDownLatch setSceneLatch = new CountDownLatch(1);
stage.sceneProperty().addListener(observable -> setSceneLatch.countDown());
Platform.runLater(() -> {
stage.setScene(scene);
});
waitForLatch(setSceneLatch, 5, "Timeout while waiting for scene to be set on stage.");
scene.setOnMouseExited(event -> {
mouseExited = true;
});
scene.windowProperty().addListener(observable -> {
mouseWindowEventOrder = mouseExited;
windowChanged = true;
});
Platform.runLater(() -> {
robot.mouseMove((int)(scene.getWindow().getX() + scene.getX() + button.getLayoutX() + button.getLayoutBounds().getWidth() / 2),
(int)(scene.getWindow().getY() + scene.getY() + button.getLayoutY() + button.getLayoutBounds().getHeight() / 2));
robot.mousePress(MouseButton.PRIMARY);
robot.mouseRelease(MouseButton.PRIMARY);
});
waitForLatch(onActionLatch, 5, "Timeout while waiting for button.onAction().");
Assert.assertTrue("MOUSE_EXITED should be received when scene is " +
" changed.", mouseExited);
Assert.assertTrue("scene.windowProperty() listener should be received" +
"on scene change.", windowChanged);
Assert.assertTrue("MOUSE_EXITED should have been received before " +
"scene.windowProperty().", mouseWindowEventOrder);
}
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) {
robot = new Robot();
stage = primaryStage;
stage.initStyle(StageStyle.UNDECORATED);
stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e ->
Platform.runLater(startupLatch::countDown));
stage.setAlwaysOnTop(true);
stage.show();
}
}
@BeforeClass
public static void initFX() {
startupLatch = new CountDownLatch(1);
new Thread(() -> Application.launch(TestApp.class, (String[])null)).start();
waitForLatch(startupLatch, 10, "Timeout waiting for FX runtime to start");
}
@AfterClass
public static void exit() {
Platform.runLater(() -> {
stage.hide();
});
Platform.exit();
}
public static void waitForLatch(CountDownLatch latch, int seconds, String msg) {
try {
if (!latch.await(seconds, TimeUnit.SECONDS)) {
fail(msg);
}
} catch (Exception ex) {
fail("Unexpected exception: " + ex);
}
}
}
