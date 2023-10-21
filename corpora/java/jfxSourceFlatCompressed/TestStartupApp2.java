package test.launchertest;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import static test.launchertest.Constants.*;
public class TestStartupApp2 extends Application {
@Override public void start(Stage stage) throws Exception {
try {
Platform.startup(() -> {
});
System.err.println("ERROR: platform startup unexpectedly succeeded");
System.exit(ERROR_STARTUP_SUCCEEDED);
} catch (IllegalStateException ex) {
System.exit(ERROR_NONE);
}
}
public static void main(String[] args) {
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
}
}
