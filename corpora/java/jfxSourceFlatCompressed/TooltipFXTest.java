package test.robot.javafx.web;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.scene.robot.Robot;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import junit.framework.AssertionFailedError;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import test.util.Util;
import static org.junit.Assert.*;
import static test.util.Util.TIMEOUT;
public class TooltipFXTest {
private static final String html = "<html>" +
"<head><style> button { position:relative; height: 100%; width: 100%; }</style></head> " +
"<body> <button id=\"mybtn\" title=\"Tooltip\" type=\"button\">Show tooltip</button></body>" +
"</html>";
private WeakReference<WebView> webViewRef;
private static final int SLEEP_TIME = 1000;
private static final int TOOLTIP_SLEEP_TIME = 3000;
private static CountDownLatch startupLatch;
private Scene scene;
private Stage stageToHide;
static Robot robot;
private int offset = 30;
public static void main(String[] args) throws Exception {
initFX();
new TooltipFXTest().testTooltipLeak();
teardown();
}
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
robot = new Robot();
primaryStage.setTitle("Primary Stage");
BorderPane root = new BorderPane();
Scene scene = new Scene(root);
primaryStage.setScene(scene);
primaryStage.setX(20);
primaryStage.setY(20);
primaryStage.setWidth(100);
primaryStage.setHeight(100);
Platform.runLater(startupLatch::countDown);
primaryStage.show();
}
}
@BeforeClass
public static void initFX() {
startupLatch = new CountDownLatch(1);
new Thread(() -> Application.launch(TestApp.class, (String[])null)).start();
try {
if (!startupLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
fail("Timeout waiting for FX runtime to start");
}
} catch (InterruptedException ex) {
fail("Unexpected exception: " + ex);
}
}
@AfterClass
public static void teardown() {
Platform.exit();
}
@Test(timeout = 20000) public void testTooltipLeak() throws Exception {
final CountDownLatch latch = new CountDownLatch(1);
Util.runAndWait(() -> {
final Stage stage = new Stage();
stage.setAlwaysOnTop(true);
stageToHide = stage;
stage.setTitle("Stage ");
WebView webview = new WebView();
webViewRef = new WeakReference<WebView>(webview);
scene = new Scene(webview);
scene.setFill(Color.LIGHTYELLOW);
stage.setWidth(610);
stage.setHeight(580);
stage.setScene(scene);
webview.getEngine().getLoadWorker().stateProperty().addListener((ov, o, n) -> {
if (n == Worker.State.SUCCEEDED) {
latch.countDown();
}
});
webview.getEngine().loadContent(html);
stage.show();
});
if (!latch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
fail("Timeout waiting for web content to load");
}
Util.runAndWait(() -> {
robot.mouseMove((int)(scene.getWindow().getX() + scene.getX()),
(int)(scene.getWindow().getY() + scene.getY()));
robot.mouseClick(MouseButton.PRIMARY);
});
Util.sleep(SLEEP_TIME);
for (int i = 0; i < 3; ++i) {
Util.runAndWait(() -> {
robot.mouseMove((int)(scene.getWindow().getX() + scene.getX() + offset),
(int)(scene.getWindow().getY() + scene.getY() + offset));
offset += 20;
});
}
Util.sleep(TOOLTIP_SLEEP_TIME);
Util.runAndWait(() -> {
stageToHide.hide();
stageToHide = null;
scene = null;
});
for (int j = 0; j < 5; ++j) {
System.gc();
if (webViewRef.get() == null) {
break;
}
Util.sleep(SLEEP_TIME);
}
assertNull("webViewRef is not null", webViewRef.get());
}
}
