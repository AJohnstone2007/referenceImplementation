package test.launchertest;
import javafx.application.Application;
import javafx.application.Platform;
import static test.launchertest.Constants.*;
public class TestNotApplicationThreadCheck {
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
Application.launch(TestAppNoMainThreadCheck.class, args);
}
static {
if (Platform.isFxApplicationThread()) {
System.exit(ERROR_CLASS_INIT_WRONG_THREAD);
}
}
}
