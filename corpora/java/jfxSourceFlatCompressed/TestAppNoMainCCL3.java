package test.launchertest;
import com.sun.javafx.application.PlatformImpl;
import java.net.URL;
import java.net.URLClassLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import static test.launchertest.Constants.*;
public class TestAppNoMainCCL3 extends Application {
private static volatile ClassLoader initCcl;
private static volatile ClassLoader savedCcl;
public TestAppNoMainCCL3() {
ClassLoader ccl = Thread.currentThread().getContextClassLoader();
if (ccl != savedCcl) {
System.err.println("Unexpected CCL in constructor: " + ccl);
System.exit(ERROR_CONSTRUCTOR_WRONG_CCL);
}
}
@Override public void init() {
initCcl = TestAppNoMainCCL3.class.getClassLoader();
PlatformImpl.runAndWait(() -> {
Thread.currentThread().setContextClassLoader(initCcl);
});
}
@Override public void start(Stage stage) {
ClassLoader ccl = Thread.currentThread().getContextClassLoader();
if (ccl != initCcl) {
System.err.println("Unexpected CCL in start: " + ccl);
System.exit(ERROR_START_WRONG_CCL);
}
Platform.runLater(() -> Platform.exit());
}
@Override public void stop() {
System.exit(ERROR_NONE);
}
static {
try {
Platform.runLater(() -> {
});
} catch (IllegalStateException ex) {
ex.printStackTrace();
System.exit(ERROR_TOOLKIT_NOT_RUNNING);
} catch (RuntimeException ex) {
ex.printStackTrace();
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
try {
savedCcl = new URLClassLoader(new URL[] { new URL("file:.") });
Thread.currentThread().setContextClassLoader(savedCcl);
} catch (Exception ex) {
ex.printStackTrace();
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
}
}
