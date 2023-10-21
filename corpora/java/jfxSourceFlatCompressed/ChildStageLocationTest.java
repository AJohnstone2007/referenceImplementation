package test.javafx.stage;
import java.util.TimerTask;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.BeforeClass;
import org.junit.Test;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;
public class ChildStageLocationTest {
static CountDownLatch startupLatch;
static Timer timer;
static Runnable runNext;
static volatile Stage stage;
static volatile Stage childStage;
static double x, y;
public static void main(String[] args) {
initFX();
new ChildStageLocationTest().testLocation();
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
public void testLocation() {
assumeTrue(Boolean.getBoolean("unstable.test"));
Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
Window window = stage.getScene().getWindow();
double minX = bounds.getMinX() + (bounds.getWidth() - window.getWidth())/2 - 50;
double minY = bounds.getMinY() + (bounds.getHeight() - window.getHeight())/3 - 100;
System.out.println("Primary stage location: " + stage.getX() + " " + stage.getY());
assertTrue("Primary stage X location should be >" + minX, stage.getX() > minX);
assertTrue("Primary stage Y location should be >" + minY, stage.getY() > minY);
window = childStage.getScene().getWindow();
minX = bounds.getMinX() + (bounds.getWidth() - window.getWidth())/2 - 50;
minY = bounds.getMinY() + (bounds.getHeight() - window.getHeight())/3 - 100;
System.out.println("Child stage location: " + childStage.getX() + " " + childStage.getY());
assertTrue("Child stage X location should be >" + minX, childStage.getX() > minX);
assertTrue("Child stage Y location should be >" + minY, childStage.getY() > minY);
}
public static class TestApp extends Application implements ChangeListener {
@Override
public void start(Stage stage) throws Exception {
stage.setScene(new Scene(new Label("Label")));
stage.xProperty().addListener(this);
stage.yProperty().addListener(this);
stage.widthProperty().addListener(this);
stage.heightProperty().addListener(this);
ChildStageLocationTest.stage = stage;
runNext = () -> {
runNext = () -> {};
Platform.runLater(this::createChildStage);
};
stage.show();
}
@Override
public void changed(ObservableValue observable, Object oldValue, Object newValue) {
if (timer != null) {
timer.cancel();
}
timer = new Timer();
timer.schedule(new TimerTask() {
@Override
public void run() {
runNext.run();
}
}, 1500);
}
void createChildStage() {
childStage = new Stage();
childStage.setScene(new Scene(new Label("Label")));
childStage.sizeToScene();
childStage.xProperty().addListener(this);
childStage.yProperty().addListener(this);
childStage.widthProperty().addListener(this);
childStage.heightProperty().addListener(this);
runNext = startupLatch::countDown;
childStage.showAndWait();
}
}
}
