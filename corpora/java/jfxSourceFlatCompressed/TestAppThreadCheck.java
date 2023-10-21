package test.launchertest;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import static test.launchertest.Constants.*;
public class TestAppThreadCheck extends Application {
public TestAppThreadCheck() {
if (!Platform.isFxApplicationThread()) {
System.exit(ERROR_CONSTRUCTOR_WRONG_THREAD);
}
}
@Override public void init() {
if (Platform.isFxApplicationThread()) {
System.exit(ERROR_INIT_WRONG_THREAD);
}
}
@Override public void start(Stage stage) throws Exception {
if (!Platform.isFxApplicationThread()) {
System.exit(ERROR_START_WRONG_THREAD);
}
Platform.runLater(Platform::exit);
}
@Override public void stop() {
if (!Platform.isFxApplicationThread()) {
System.exit(ERROR_STOP_WRONG_THREAD);
}
System.exit(ERROR_NONE);
}
public static void main(String[] args) {
if (Platform.isFxApplicationThread()) {
System.exit(ERROR_MAIN_WRONG_THREAD);
}
Application.launch(args);
}
static {
if (!Platform.isFxApplicationThread()) {
System.exit(ERROR_CLASS_INIT_WRONG_THREAD);
}
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
