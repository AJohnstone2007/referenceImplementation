package test.launchertest;
import java.net.URL;
import java.net.URLClassLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import static test.launchertest.Constants.*;
public class TestNotApplicationCCLApp extends Application {
private static volatile ClassLoader savedCcl;
public TestNotApplicationCCLApp() {
savedCcl = TestNotApplicationCCL.getSavedCcl();
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
}
}
