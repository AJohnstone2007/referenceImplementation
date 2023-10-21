package test.launchertest;
import java.net.URL;
import java.net.URLClassLoader;
import javafx.application.Application;
import javafx.application.Platform;
import static test.launchertest.Constants.*;
public class TestNotApplicationCCL {
private static volatile ClassLoader savedCcl;
static ClassLoader getSavedCcl() {
return savedCcl;
}
public static void main(String[] args) {
try {
Platform.runLater(() -> {
});
System.exit(ERROR_TOOLKIT_IS_RUNNING);
} catch (IllegalStateException ex) {
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
Application.launch(TestNotApplicationCCLApp.class, args);
}
static {
if (Platform.isFxApplicationThread()) {
System.exit(ERROR_CLASS_INIT_WRONG_THREAD);
}
}
}
