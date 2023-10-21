package test.robot.javafx.scene;
import com.sun.javafx.PlatformUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.robot.Robot;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;
public class AfterModalClosedTest {
static CountDownLatch startupLatch;
static volatile Stage stage;
private Robot robot;
private int x, y;
private boolean w, h;
public static void main(String[] args) throws Exception {
initFX();
new AfterModalClosedTest().testResizability();
teardown();
}
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(new VBox()));
primaryStage.setResizable(true);
primaryStage.show();
stage = primaryStage;
Stage modalStage = new Stage();
modalStage.setScene(new Scene(new VBox()));
modalStage.initModality(Modality.APPLICATION_MODAL);
modalStage.addEventHandler(WindowEvent.WINDOW_SHOWN, e ->
Platform.runLater(modalStage::hide));
modalStage.addEventHandler(WindowEvent.WINDOW_HIDDEN, e ->
Platform.runLater(startupLatch::countDown));
modalStage.show();
}
}
@BeforeClass
public static void initFX() {
startupLatch = new CountDownLatch(1);
new Thread(() -> Application.launch(TestApp.class, (String[])null)).start();
try {
if (!startupLatch.await(15, TimeUnit.SECONDS)) {
fail("Timeout waiting for FX runtime to start");
}
} catch (InterruptedException ex) {
fail("Unexpected exception: " + ex);
}
}
@Test
public void testResizability() throws Exception {
assumeTrue(Boolean.getBoolean("unstable.test"));
Assert.assertTrue(stage.isResizable());
CountDownLatch resizeLatch = new CountDownLatch(2);
Platform.runLater(() -> {
stage.widthProperty().addListener((ov, o, n) -> {
if (!w && o != n) {
w = true;
resizeLatch.countDown();
}
});
stage.heightProperty().addListener((ov, o, n) -> {
if (!h && o != n) {
h = true;
resizeLatch.countDown();
}
});
robot = new Robot();
x = (int) (stage.getX() + stage.getWidth());
y = (int) (stage.getY() + stage.getHeight());
int d = PlatformUtil.isLinux() ? -1 : 2;
robot.mouseMove(x - d, y - d);
robot.mousePress(MouseButton.PRIMARY);
});
Thread.sleep(100);
Platform.runLater(() -> {
robot.mouseMove(x + 20, y + 20);
robot.mouseRelease(MouseButton.PRIMARY);
});
resizeLatch.await(5, TimeUnit.SECONDS);
Assert.assertTrue("Window is not resized", w && h);
}
@AfterClass
public static void teardown() {
Platform.runLater(stage::hide);
Platform.exit();
}
}
