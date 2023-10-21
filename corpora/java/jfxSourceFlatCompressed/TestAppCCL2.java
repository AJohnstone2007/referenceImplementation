package test.launchertest;
import java.net.URL;
import java.net.URLClassLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import static test.launchertest.Constants.*;
public class TestAppCCL2 extends Application {
private static volatile ClassLoader savedCcl;
public TestAppCCL2() {
ClassLoader ccl = Thread.currentThread().getContextClassLoader();
if (ccl != savedCcl) {
System.err.println("Unexpected CCL in constructor: " + ccl);
System.exit(ERROR_CONSTRUCTOR_WRONG_CCL);
}
}
@Override public void start(Stage stage) {
ClassLoader ccl = Thread.currentThread().getContextClassLoader();
if (ccl != savedCcl) {
System.err.println("Unexpected CCL in start: " + ccl);
System.exit(ERROR_START_WRONG_CCL);
}
Platform.runLater(() -> Platform.exit());
}
@Override public void stop() {
System.exit(ERROR_NONE);
}
public static void main(String[] args) {
try {
savedCcl = new URLClassLoader(new URL[] { new URL("file:.") });
Thread.currentThread().setContextClassLoader(savedCcl);
} catch (Exception ex) {
ex.printStackTrace();
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
Application.launch(args);
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
ClassLoader tmpCcl = new URLClassLoader(new URL[] { new URL("file:.") });
Thread.currentThread().setContextClassLoader(tmpCcl);
} catch (Exception ex) {
ex.printStackTrace();
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
}
}
