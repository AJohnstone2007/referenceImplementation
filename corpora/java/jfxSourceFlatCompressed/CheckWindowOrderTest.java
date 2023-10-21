package test.robot.javafx.stage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
public class CheckWindowOrderTest {
static Scene scene;
static Stage stage;
static Stage firstWindow;
static Stage secondWindow;
static Stage lastWindow;
static CountDownLatch startupLatch = new CountDownLatch(4);
@Test(timeout = 15000)
public void topWindowShouldBeTheLast() throws Exception {
Thread.sleep(400);
Assert.assertTrue("Last Window Should be Focused", lastWindow.isFocused());
}
@BeforeClass
public static void initFX() throws Exception {
new Thread(() -> Application.launch(TestApp.class, (String[]) null)).start();
waitForLatch(startupLatch, 10, "FX runtime failed to start.");
}
@AfterClass
public static void exit() {
Platform.runLater(() -> {
lastWindow.hide();
secondWindow.hide();
firstWindow.hide();
stage.hide();
});
Platform.exit();
}
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) {
stage = primaryStage;
scene = new Scene(new Label("Primary Stage"), 640, 480);
primaryStage.setScene(scene);
primaryStage.setOnShown(e -> Platform.runLater(startupLatch::countDown));
primaryStage.show();
firstWindow = new TestStage(primaryStage, "First Window");
firstWindow.show();
secondWindow = new TestStage(primaryStage, "Second Window");
secondWindow.show();
lastWindow = openLastWindow(secondWindow);
lastWindow.show();
}
TestStage openLastWindow(Window owner) {
TestStage stage = new TestStage(owner, "Last Window");
stage.initModality(Modality.WINDOW_MODAL);
return stage;
}
}
public static void waitForLatch(CountDownLatch latch, int seconds, String msg) throws Exception {
Assert.assertTrue("Timeout: " + msg, latch.await(seconds, TimeUnit.SECONDS));
}
static class TestStage extends Stage {
TestStage(Window owner, String title) {
initOwner(owner);
setTitle(title);
this.setScene(new Scene(new Label("Hello World!"), 400, 400));
setOnShown(e -> Platform.runLater(startupLatch::countDown));
}
}
}
