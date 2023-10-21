package test.javafx.stage;
import com.sun.javafx.PlatformUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
public class StageAtTopPositionTest {
static CountDownLatch startupLatch;
static Stage stage;
public static void main(String[] args) throws Exception {
initFX();
try {
StageAtTopPositionTest test = new StageAtTopPositionTest();
test.testMoveToTopPosition();
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
public static void initFX() throws Exception {
startupLatch = new CountDownLatch(1);
new Thread(() -> Application.launch(TestApp.class, (String[])null)).start();
assertTrue("Timeout waiting for FX runtime to start",
startupLatch.await(15, TimeUnit.SECONDS));
}
@Test
public void testMoveToTopPosition() throws Exception {
assumeTrue(PlatformUtil.isMac());
Thread.sleep(200);
Assert.assertTrue(stage.isShowing());
Assert.assertFalse(stage.isFullScreen());
final double minY = Screen.getPrimary().getVisualBounds().getMinY();
CountDownLatch latch = new CountDownLatch(2);
ChangeListener<Number> listenerY = (observable, oldValue, newValue) -> {
if (Math.abs((Double) newValue - minY) < 0.1) {
latch.countDown();
};
};
stage.yProperty().addListener(listenerY);
Platform.runLater(() -> stage.setY(0));
Thread.sleep(200);
Assert.assertEquals("Window was moved once", minY, stage.getY(), 0.1);
Platform.runLater(() -> stage.setY(0));
latch.await(5, TimeUnit.SECONDS);
stage.xProperty().removeListener(listenerY);
Assert.assertEquals("Window was moved twice", minY, stage.getY(), 0.1);
}
@AfterClass
public static void teardown() {
Platform.runLater(stage::hide);
Platform.exit();
}
}
