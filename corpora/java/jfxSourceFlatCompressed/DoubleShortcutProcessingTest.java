package test.robot.javafx.scene;
import com.sun.javafx.PlatformUtil;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.robot.Robot;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
public class DoubleShortcutProcessingTest {
static CountDownLatch startupLatch = new CountDownLatch(1);
static CountDownLatch dialogLatch = new CountDownLatch(1);
static volatile Stage stage;
static volatile TestApp testApp;
@Test
void testDoubleShortcut() {
Assumptions.assumeTrue(PlatformUtil.isMac());
testApp.startTest();
waitForLatch(dialogLatch, 5, "Dialog never received shortcut");
if (testApp.failed()) {
Assertions.fail("performKeyEquivalent was handled twice in separate windows");
}
}
@BeforeAll
static void initFX() throws Exception {
new Thread(() -> Application.launch(TestApp.class, (String[])null)).start();
waitForLatch(startupLatch, 10, "FX runtime failed to start.");
}
@AfterAll
static void exit() {
Platform.runLater(stage::hide);
Platform.exit();
}
public static class TestApp extends Application {
private boolean failure = false;
private Dialog dialog = null;
@Override
public void start(Stage primaryStage) {
testApp = this;
stage = primaryStage;
Label label = new Label("Testing double performKeyEquivalent");
Scene scene = new Scene(new VBox(label), 200, 200);
scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
if (event.getCode() == KeyCode.ENTER && event.isShortcutDown()) {
failure = true;
event.consume();
}
});
stage.setScene(scene);
VBox pane = new VBox(new Label("Pressing Cmd+Enter"));
dialog = new Dialog(stage, pane);
stage.setOnShown(e -> { startupLatch.countDown(); });
stage.show();
}
public void startTest() {
Platform.runLater(() -> {
dialog.setOnShown(e -> {
Robot robot = new Robot();
robot.keyPress(KeyCode.COMMAND);
robot.keyPress(KeyCode.ENTER);
robot.keyRelease(KeyCode.ENTER);
robot.keyRelease(KeyCode.COMMAND);
});
dialog.showAndWait();
dialogLatch.countDown();
});
}
public boolean failed() {
return failure;
}
private static class Dialog extends Stage {
public Dialog(Stage owner, Parent layout) {
super(StageStyle.DECORATED);
Scene layoutScene = new Scene(layout, 100, 100);
this.setScene(layoutScene);
layoutScene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
if (event.getCode() == KeyCode.ENTER && event.isShortcutDown()) {
close();
event.consume();
}
});
this.hide();
this.initModality(Modality.APPLICATION_MODAL);
this.initOwner(owner);
this.setResizable(true);
}
}
}
private static void waitForLatch(CountDownLatch latch, int seconds, String msg) {
try {
if (!latch.await(seconds, TimeUnit.SECONDS)) {
Assertions.fail(msg);
}
} catch (Exception ex) {
Assertions.fail("Unexpected exception: " + ex);
}
}
}
