package test.javafx.scene.web;
import java.util.concurrent.CountDownLatch;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.web.WebEngineShim;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import test.util.Util;
import com.sun.webkit.WebPage;
import com.sun.webkit.WebPageShim;
import static javafx.concurrent.Worker.State.SUCCEEDED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
public class WebPageTest {
private static final CountDownLatch launchLatch = new CountDownLatch(1);
static WebPageTestApp webPageTestApp;
private WebView webView;
public static class WebPageTestApp extends Application {
Stage primaryStage = null;
@Override
public void init() {
WebPageTest.webPageTestApp = this;
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
new Thread(() -> Application.launch(WebPageTestApp.class, (String[])null)).start();
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
webPageTestApp.primaryStage.setScene(new Scene(webView));
webPageTestApp.primaryStage.show();
});
}
@Test public void testScroll() {
final CountDownLatch webViewStateLatch = new CountDownLatch(1);
final String htmlContent = "\n"
+ "<html>\n"
+ "<body style='height:1500px'>\n"
+ "<p id='test'>Fail</p>\n"
+ "<script>\n"
+ "window.onscroll = function() {scrollFunc()};\n"
+ "function scrollFunc() {\n"
+ "document.getElementById('test').innerHTML = 'Pass';\n"
+ "}\n"
+ "</script>\n"
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
webView.getEngine().loadContent(htmlContent);
});
assertTrue("Timeout when waiting for focus change ", Util.await(webViewStateLatch));
Util.sleep(1000);
Util.runAndWait(() -> {
final WebPage page = WebEngineShim.getPage(webView.getEngine());
assertNotNull(page);
WebPageShim.scroll(page, 1, 1, 0, 100);
});
Util.sleep(500);
Util.runAndWait(() -> {
assertEquals("WebPage should display pass: ", "Pass", webView.getEngine().executeScript("document.getElementById('test').innerHTML"));
});
}
}
