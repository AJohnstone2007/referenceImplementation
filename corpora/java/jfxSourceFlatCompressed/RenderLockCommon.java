package test.renderlock;
import com.sun.javafx.PlatformUtil;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javafx.animation.FillTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import junit.framework.AssertionFailedError;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import test.util.Util;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static test.util.Util.TIMEOUT;
public class RenderLockCommon {
private static final int SLEEP_TIME = 1000;
private static final CountDownLatch launchLatch = new CountDownLatch(1);
static MyApp myApp;
public static class MyApp extends Application {
Stage primaryStage;
@Override public void init() {
RenderLockCommon.myApp = this;
}
@Override public void start(Stage primaryStage) throws Exception {
assertTrue(Platform.isFxApplicationThread());
primaryStage.setTitle("Primary stage");
Rectangle rect = new Rectangle(100, 50);
FillTransition trans = new FillTransition(Duration.millis(500),
rect, Color.BLUE, Color.VIOLET);
trans.setCycleCount(Timeline.INDEFINITE);
trans.setAutoReverse(true);
trans.play();
Group root = new Group(rect);
Scene scene = new Scene(root);
scene.setFill(Color.LIGHTYELLOW);
primaryStage.setScene(scene);
primaryStage.setX(0);
primaryStage.setY(0);
primaryStage.setWidth(210);
primaryStage.setHeight(180);
assertFalse(primaryStage.isShowing());
primaryStage.show();
assertTrue(primaryStage.isShowing());
this.primaryStage = primaryStage;
launchLatch.countDown();
}
}
@BeforeClass
public static void doSetupOnce() throws Exception {
assumeTrue(PlatformUtil.isMac() || PlatformUtil.isWindows());
new Thread(() -> Application.launch(MyApp.class, (String[])null)).start();
if (!launchLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
fail("Timeout waiting for Application to launch");
}
assertEquals(0, launchLatch.getCount());
}
@AfterClass
public static void doTeardownOnce() {
Platform.exit();
}
private Stage testStage;
protected void doWindowCloseTest() throws Exception {
final CountDownLatch alertDoneLatch = new CountDownLatch(1);
final AtomicReference<ButtonType> alertResult = new AtomicReference<>();
Util.runAndWait(() -> {
Button button1 = new Button("The Button");
button1.focusedProperty().addListener((obs, oldValue, newValue) -> {
if (!newValue) {
final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
Thread t = new Thread(() -> {
Util.sleep(SLEEP_TIME);
Platform.runLater(() -> {
alert.hide();
});
});
t.start();
ButtonType result = alert.showAndWait().get();
alertResult.set(result);
alertDoneLatch.countDown();
}
});
Button button2 = new Button("Other Button");
testStage = new Stage();
testStage.setScene(new Scene(new VBox(button1, button2), 400, 300));
button1.requestFocus();
testStage.requestFocus();
testStage.show();
});
Util.sleep(SLEEP_TIME);
Platform.runLater(() -> {
testStage.hide();
});
if (!alertDoneLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
fail("Timeout waiting for alert to be hidden");
}
assertSame(ButtonType.CANCEL, alertResult.get());
}
}
