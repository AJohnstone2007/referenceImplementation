package test.javafx.scene.web;
import java.util.concurrent.CountDownLatch;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
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
public class SVGTest {
private static final CountDownLatch launchLatch = new CountDownLatch(1);
static SVGTestApp svgTestApp;
private WebView webView;
public static class SVGTestApp extends Application {
Stage primaryStage = null;
@Override
public void init() {
SVGTest.svgTestApp = this;
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
new Thread(() -> Application.launch(SVGTestApp.class, (String[])null)).start();
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
svgTestApp.primaryStage.setScene(new Scene(webView));
svgTestApp.primaryStage.show();
});
}
@Test public void testSVGRenderingWithPattern() {
final CountDownLatch webViewStateLatch = new CountDownLatch(1);
final String htmlSVGContent = "\n"
+ "<html>\n"
+ "<body style='margin: 0px 0px;'>\n"
+ "<svg width='400' height='150'>\n"
+ "<defs>\n"
+ "<pattern id='pattern1' x='0' y='0' width='30' height='30' patternUnits='userSpaceOnUse'>\n"
+ "<rect width='20' height='20' fill='red' />\n"
+ "</pattern>\n"
+ "</defs>\n"
+ "<rect width='400' height='150' fill='url(#pattern1)' />\n"
+ "</svg>\n"
+ "</body>\n"
+ "</html>";
Util.runAndWait(() -> {
assertNotNull(webView);
webView.getEngine().getLoadWorker().stateProperty().
addListener((observable, oldValue, newValue) -> {
if (newValue == SUCCEEDED) {
webView.requestFocus();
}
});
webView.focusedProperty().
addListener((observable, oldValue, newValue) -> {
if (newValue) {
webViewStateLatch.countDown();
}
});
webView.getEngine().loadContent(htmlSVGContent);
});
assertTrue("Timeout when waiting for focus change ", Util.await(webViewStateLatch));
Util.sleep(1000);
Util.runAndWait(() -> {
WritableImage snapshot = svgTestApp.primaryStage.getScene().snapshot(null);
PixelReader pr = snapshot.getPixelReader();
Color redColor = Color.color(1, 0, 0);
assertEquals("Color should be opaque red:", redColor, pr.getColor(0, 0));
assertEquals("Color should be opaque red:", redColor, pr.getColor(30, 30));
assertEquals("Color should be opaque red:", redColor, pr.getColor(49, 49));
});
}
}
