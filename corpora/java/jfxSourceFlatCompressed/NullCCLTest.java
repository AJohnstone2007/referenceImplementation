package test.com.sun.javafx.application;
import com.sun.javafx.application.PlatformImpl;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEngine;
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
public class NullCCLTest {
private static final CountDownLatch launchLatch = new CountDownLatch(1);
@BeforeClass
public static void setupOnce() {
new Thread(() -> {
Thread.currentThread().setContextClassLoader(null);
Platform.setImplicitExit(false);
PlatformImpl.startup(() -> {
launchLatch.countDown();
});
}).start();
try {
if (!launchLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Application to launch");
}
} catch (InterruptedException ex) {
AssertionFailedError err = new AssertionFailedError("Unexpected exception");
err.initCause(ex);
throw err;
}
Util.runAndWait(() -> {
assertTrue(Platform.isFxApplicationThread());
assertNull(Thread.currentThread().getContextClassLoader());
});
}
@AfterClass
public static void teardownOnce() {
Platform.exit();
}
private Stage stage;
@After
public void cleanup() {
Thread.setDefaultUncaughtExceptionHandler(null);
if (stage != null) {
Platform.runLater(stage::hide);
stage = null;
}
}
private void doTest(Callable<Node> loadContent) {
final AtomicReference<Throwable> uce = new AtomicReference<>(null);
Thread.setDefaultUncaughtExceptionHandler((t, e) -> uce.set(e));
Util.runAndWait(() -> {
assertTrue(Platform.isFxApplicationThread());
assertNull(Thread.currentThread().getContextClassLoader());
StackPane root = new StackPane();
Scene scene = new Scene(root);
Node content = null;
try {
content = loadContent.call();
} catch (RuntimeException ex) {
throw (RuntimeException) ex;
} catch (Exception ex) {
fail("Unexpected exception: " + ex);
}
assertNotNull(content);
root.getChildren().add(content);
stage = new Stage();
stage.setScene(scene);
stage.show();
});
Util.sleep(2000);
Util.runAndWait(() -> {
stage.hide();
stage = null;
});
final Throwable e = uce.get();
if (e != null) {
throw new RuntimeException("UncaughtException", e);
}
}
@Test
public void testFonts() {
Util.runAndWait(() -> {
assertTrue(Platform.isFxApplicationThread());
assertNull(Thread.currentThread().getContextClassLoader());
List<String> fontFamilies = Font.getFamilies();
assertNotNull(fontFamilies);
assertFalse(fontFamilies.isEmpty());
List<String> fontNames = Font.getFontNames();
assertNotNull(fontNames);
assertFalse(fontNames.isEmpty());
});
}
@Test
public void testLabel() {
doTest(() -> {
Label label = new Label("This is a JavaFX label");
return label;
});
}
@Test
public void testHTMLEditor() {
doTest(() -> {
HTMLEditor htmlEditor = new HTMLEditor();
htmlEditor.setHtmlText("<html><body>Hello, World!</body></html>");
return htmlEditor;
});
}
@Test
public void testWebView() throws Exception {
final String HTML_FILE_NAME = "test.html";
URL url = NullCCLTest.class.getResource(HTML_FILE_NAME);
assertNotNull(url);
URLConnection conn = url.openConnection();
InputStream stream = conn.getInputStream();
stream.close();
final String webURLString = url.toExternalForm();
doTest(() -> {
WebView webView = new WebView();
WebEngine webEngine = webView.getEngine();
webEngine.load(webURLString);
return webView;
});
}
}
