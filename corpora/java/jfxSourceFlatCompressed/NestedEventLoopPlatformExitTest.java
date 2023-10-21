package test.javafx.stage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import test.util.Util;
public class NestedEventLoopPlatformExitTest {
private static final CountDownLatch launchLatch = new CountDownLatch(1);
public static class TestApp extends Application {
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setTitle("Primary stage");
Group root = new Group();
Scene scene = new Scene(root);
primaryStage.setScene(scene);
primaryStage.setWidth(100);
primaryStage.setHeight(100);
primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN,
e -> Platform.runLater(launchLatch::countDown));
primaryStage.show();
}
}
@BeforeClass
public static void initFX() throws InterruptedException {
new Thread(() -> Application.launch(TestApp.class, (String[])null)).start();
Assert.assertTrue (launchLatch.await(15, TimeUnit.SECONDS));
}
@Test(timeout = 20000)
public void testPlatformExitWithNestedEventLoop() {
Util.runAndWait(
() -> {
final long nestedLoopEventKey = 1024L;
Assert.assertFalse(Platform.isNestedLoopRunning());
Platform.enterNestedEventLoop(nestedLoopEventKey);
},
() -> {
Assert.assertTrue(Platform.isNestedLoopRunning());
Platform.exit();
}
);
}
}
