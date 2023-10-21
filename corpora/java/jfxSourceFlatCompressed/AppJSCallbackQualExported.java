package myapp5;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import myapp5.pkg3.MyCallback;
import static myapp5.Constants.*;
public class AppJSCallbackQualExported extends Application {
private static int callbackCount = -1;
private static final CountDownLatch launchLatch = new CountDownLatch(1);
private static final CountDownLatch contentLatch = new CountDownLatch(1);
private final MyCallback callback = new MyCallback();
private WebEngine webEngine;
public static void main(String[] args) {
Thread thr = new Thread(() -> {
try {
Application.launch(args);
} catch (Throwable t) {
System.err.println("ERROR: caught unexpected exception: " + t);
t.printStackTrace(System.err);
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
});
thr.start();
waitForLatch(launchLatch, 10, "waiting for FX startup");
waitForLatch(contentLatch, 5, "loading web content");
try {
Util.assertEquals(0, callbackCount);
System.exit(ERROR_NONE);
} catch (Throwable t) {
t.printStackTrace(System.err);
System.exit(ERROR_ASSERTION_FAILURE);
}
}
@Override
public void start(Stage stage) throws Exception {
try {
launchLatch.countDown();
webEngine = new WebView().getEngine();
webEngine.getLoadWorker().stateProperty().addListener((ov, o, n) -> {
if (n == Worker.State.SUCCEEDED) {
try {
final JSObject window = (JSObject) webEngine.executeScript("window");
Util.assertNotNull(window);
window.setMember("javaCallback", callback);
webEngine.executeScript("document.getElementById(\"mybtn1\").click()");
callbackCount = callback.getCount();
contentLatch.countDown();
} catch (Throwable t) {
t.printStackTrace(System.err);
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
}
});
webEngine.loadContent(Util.content);
} catch (Error | Exception ex) {
System.err.println("ERROR: caught unexpected exception: " + ex);
ex.printStackTrace(System.err);
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
}
public static void waitForLatch(CountDownLatch latch, int seconds, String msg) {
try {
if (!latch.await(seconds, TimeUnit.SECONDS)) {
System.err.println("Timeout: " + msg);
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
} catch (InterruptedException ex) {
System.err.println("ERROR: caught unexpected exception: " + ex);
ex.printStackTrace(System.err);
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
}
}
