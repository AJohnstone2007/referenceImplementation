package test.javafx.stage;
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
public class RestoreStagePositionTest {
static CountDownLatch startupLatch;
static Stage stage;
public static void main(String[] args) throws Exception {
initFX();
try {
RestoreStagePositionTest test = new RestoreStagePositionTest();
test.testUfullscreenPosition();
test.testDemaximizedPosition();
} catch (Throwable e) {
e.printStackTrace();
} finally {
teardown();
}
}
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(new VBox()));
stage = primaryStage;
stage.setX(300);
stage.setY(400);
stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e ->
Platform.runLater(startupLatch::countDown));
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
public void testUfullscreenPosition() throws Exception {
assumeTrue(!PlatformUtil.isMac());
Thread.sleep(200);
Assert.assertTrue(stage.isShowing());
Assert.assertFalse(stage.isFullScreen());
double x = stage.getX();
double y = stage.getY();
Platform.runLater(() -> stage.setFullScreen(true));
Thread.sleep(400);
Assert.assertTrue(stage.isFullScreen());
CountDownLatch latch = new CountDownLatch(2);
ChangeListener<Number> listenerX = (observable, oldValue, newValue) -> {
if (Math.abs((Double) newValue - x) < 0.1) {
latch.countDown();
};
};
ChangeListener<Number> listenerY = (observable, oldValue, newValue) -> {
if (Math.abs((Double) newValue - y) < 0.1) {
latch.countDown();
};
};
stage.xProperty().addListener(listenerX);
stage.yProperty().addListener(listenerY);
Platform.runLater(() -> stage.setFullScreen(false));
latch.await(5, TimeUnit.SECONDS);
stage.xProperty().removeListener(listenerX);
stage.xProperty().removeListener(listenerY);
Assert.assertEquals("Window was moved", x, stage.getX(), 0.1);
Assert.assertEquals("Window was moved", y, stage.getY(), 0.1);
}
@Test
public void testDemaximizedPosition() throws Exception {
assumeTrue(!PlatformUtil.isMac());
Thread.sleep(200);
Assert.assertTrue(stage.isShowing());
Assert.assertFalse(stage.isMaximized());
double x = stage.getX();
double y = stage.getY();
Platform.runLater(() -> stage.setMaximized(true));
Thread.sleep(200);
Assert.assertTrue(stage.isMaximized());
CountDownLatch latch = new CountDownLatch(2);
ChangeListener<Number> listenerX = (observable, oldValue, newValue) -> {
if (Math.abs((Double) newValue - x) < 0.1) {
latch.countDown();
};
};
ChangeListener<Number> listenerY = (observable, oldValue, newValue) -> {
if (Math.abs((Double) newValue - y) < 0.1) {
latch.countDown();
};
};
stage.xProperty().addListener(listenerX);
stage.yProperty().addListener(listenerY);
Platform.runLater(() -> stage.setMaximized(false));
latch.await(5, TimeUnit.SECONDS);
stage.xProperty().removeListener(listenerX);
stage.xProperty().removeListener(listenerY);
Assert.assertEquals("Window was moved", x, stage.getX(), 0.1);
Assert.assertEquals("Window was moved", y, stage.getY(), 0.1);
}
@AfterClass
public static void teardown() {
Platform.runLater(stage::hide);
Platform.exit();
}
}
