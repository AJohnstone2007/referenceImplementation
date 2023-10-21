package test.javafx.scene.web;
import java.util.concurrent.CountDownLatch;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import test.util.Util;
import static javafx.concurrent.Worker.State.SUCCEEDED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
public class CanvasTest {
private static final CountDownLatch launchLatch = new CountDownLatch(1);
static CanvasTestApp canvasTestApp;
private WebView webView;
public static class CanvasTestApp extends Application {
Stage primaryStage = null;
@Override
public void init() {
CanvasTest.canvasTestApp = this;
}
@Override
public void start(Stage primaryStage) throws Exception {
Platform.setImplicitExit(false);
this.primaryStage = primaryStage;
launchLatch.countDown();
}
}
@BeforeClass
public static void setupOnce() {
new Thread(() -> Application.launch(CanvasTestApp.class, (String[])null)).start();
assertTrue("Timeout waiting for FX runtime to start", Util.await(launchLatch));
}
@AfterClass
public static void tearDownOnce() {
Platform.exit();
}
@Before
public void setupTestObjects() {
Platform.runLater(() -> {
webView = new WebView();
canvasTestApp.primaryStage.setScene(new Scene(webView));
canvasTestApp.primaryStage.show();
});
}
@Test
public void testCanvasRect() throws Exception {
final CountDownLatch webViewStateLatch = new CountDownLatch(1);
final String htmlCanvasContent = "\n"
+ "<canvas id='canvas' width='100' height='100'></canvas>\n"
+ "<script>\n"
+ "var ctx = document.getElementById('canvas').getContext('2d');\n"
+ "ctx.fillStyle = 'red';\n"
+ "ctx.fillRect(0, 0, 100, 100);\n"
+ "</script>\n";
Util.runAndWait(() -> {
webView.getEngine().getLoadWorker().stateProperty().
addListener((observable, oldValue, newValue) -> {
if (newValue == SUCCEEDED) {
webView.requestFocus();
}
});
assertNotNull(webView);
webView.getEngine().loadContent(htmlCanvasContent);
webView.focusedProperty().
addListener((observable, oldValue, newValue) -> {
if (newValue) {
webViewStateLatch.countDown();
}
});
});
assertTrue("Timeout when waiting for focus change ", Util.await(webViewStateLatch));
Util.runAndWait(() -> {
int redColor = 255;
assertEquals("Rect top-left corner", redColor, (int) webView.getEngine().executeScript(
"document.getElementById('canvas').getContext('2d').getImageData(1, 1, 1, 1).data[0]"));
assertEquals("Rect bottom-right corner", redColor, (int) webView.getEngine().executeScript(
"document.getElementById('canvas').getContext('2d').getImageData(99, 99, 1, 1).data[0]"));
});
}
}
