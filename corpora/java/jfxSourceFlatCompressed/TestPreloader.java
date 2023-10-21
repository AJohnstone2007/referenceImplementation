package test.launchertest;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.stage.Stage;
import static test.launchertest.Constants.*;
public class TestPreloader extends Preloader {
public TestPreloader() {
if (!Platform.isFxApplicationThread()) {
System.exit(ERROR_PRELOADER_CONSTRUCTOR_WRONG_THREAD);
}
}
@Override public void init() {
if (Platform.isFxApplicationThread()) {
System.exit(ERROR_PRELOADER_INIT_WRONG_THREAD);
}
}
@Override public void start(Stage stage) throws Exception {
if (!Platform.isFxApplicationThread()) {
System.exit(ERROR_PRELOADER_START_WRONG_THREAD);
}
Platform.runLater(() -> {
});
}
@Override public void stop() {
if (!Platform.isFxApplicationThread()) {
System.exit(ERROR_PRELOADER_STOP_WRONG_THREAD);
}
}
static {
if (!Platform.isFxApplicationThread()) {
Thread.dumpStack();
System.exit(ERROR_PRELOADER_CLASS_INIT_WRONG_THREAD);
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
