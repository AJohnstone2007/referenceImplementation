package test.javafx.stage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import test.util.Util;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.fail;
public class InitialSizeTest {
static CountDownLatch startupLatch;
static Stage stage;
private static final double INIT_SIZE = 200.d;
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(new Group()));
stage = primaryStage;
stage.setWidth(INIT_SIZE);
stage.setHeight(INIT_SIZE);
stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e ->
Platform.runLater(startupLatch::countDown));
stage.show();
}
}
@BeforeClass
public static void initFX() {
startupLatch = new CountDownLatch(1);
new Thread(() -> Application.launch(TestApp.class, (String[]) null)).start();
try {
if (!startupLatch.await(15, TimeUnit.SECONDS)) {
fail("Timeout waiting for FX runtime to start");
}
} catch (InterruptedException ex) {
fail("Unexpected exception: " + ex);
}
}
@Test
public void testInitialSize() throws Exception {
Util.sleep(200);
Assert.assertTrue(stage.isShowing());
Assert.assertEquals("Stage height", INIT_SIZE, stage.getHeight(), .1d);
Assert.assertEquals("Stage width", INIT_SIZE, stage.getWidth(), .1d);
}
@AfterClass
public static void teardown() {
Platform.runLater(stage::hide);
Platform.exit();
}
}
