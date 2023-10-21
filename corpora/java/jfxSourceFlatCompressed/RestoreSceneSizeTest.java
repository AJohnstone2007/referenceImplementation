package test.javafx.scene;
import com.sun.javafx.PlatformUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
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
public class RestoreSceneSizeTest {
static CountDownLatch startupLatch;
static Stage stage;
private static final int WIDTH = 234;
private static final int HEIGHT = 255;
private static double scaleX, scaleY;
public static void main(String[] args) throws Exception {
initFX();
try {
RestoreSceneSizeTest test = new RestoreSceneSizeTest();
test.testUnfullscreenSize();
} catch (Throwable e) {
e.printStackTrace();
} finally {
teardown();
}
}
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(new VBox(), WIDTH, HEIGHT));
stage = primaryStage;
stage.setFullScreen(true);
stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e -> {
scaleX = stage.getOutputScaleX();
scaleY = stage.getOutputScaleY();
Platform.runLater(startupLatch::countDown);
});
stage.show();
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
public void testUnfullscreenSize() throws Exception {
assumeTrue(!PlatformUtil.isMac());
Thread.sleep(200);
final double w = (Math.ceil(WIDTH * scaleX)) / scaleX;
final double h = (Math.ceil(HEIGHT * scaleY)) / scaleY;
Assert.assertTrue(stage.isShowing());
Assert.assertTrue(stage.isFullScreen());
CountDownLatch latch = new CountDownLatch(2);
ChangeListener<Number> listenerW = (observable, oldValue, newValue) -> {
if (Math.abs((Double) newValue - w) < 0.1) {
latch.countDown();
};
};
ChangeListener<Number> listenerH = (observable, oldValue, newValue) -> {
if (Math.abs((Double) newValue - h) < 0.1) {
latch.countDown();
};
};
stage.getScene().widthProperty().addListener(listenerW);
stage.getScene().heightProperty().addListener(listenerH);
Platform.runLater(() -> stage.setFullScreen(false));
latch.await(5, TimeUnit.SECONDS);
Thread.sleep(200);
Assert.assertFalse(stage.isFullScreen());
stage.getScene().widthProperty().removeListener(listenerW);
stage.getScene().heightProperty().removeListener(listenerH);
Assert.assertEquals("Scene got wrong width", w, stage.getScene().getWidth(), 0.1);
Assert.assertEquals("Scene got wrong height", h, stage.getScene().getHeight(), 0.1);
}
@AfterClass
public static void teardown() {
Platform.runLater(stage::hide);
Platform.exit();
}
}
