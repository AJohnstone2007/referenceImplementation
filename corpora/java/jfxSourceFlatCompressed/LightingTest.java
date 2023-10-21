package test.javafx.scene.lighting3D;
import java.util.concurrent.CountDownLatch;
import org.junit.AfterClass;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.LightBase;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Box;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
public abstract class LightingTest {
protected static final double DELTA = 10d/255;
protected static final String FAIL_MESSAGE = "Wrong color value";
protected static final int LIGHT_DIST = 60;
protected static LightBase light;
private static final Box BOX = new Box(150, 150, 1);
protected static CountDownLatch startupLatch;
private static Stage stage;
public static class TestApp extends Application {
@Override
public void start(Stage mainStage) {
stage = mainStage;
light.setTranslateZ(-LIGHT_DIST);
stage.setScene(new Scene(new Group(light, BOX)));
stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e -> Platform.runLater(startupLatch::countDown));
stage.show();
}
}
protected WritableImage snapshot() {
return BOX.getScene().snapshot(null);
}
protected double calculateLambertTerm(double x) {
return Math.cos(Math.atan(x/LIGHT_DIST));
}
@AfterClass
public static void teardown() {
Platform.runLater(() -> {
stage.hide();
Platform.exit();
});
}
}
