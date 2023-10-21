package test.launchertest;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import static test.launchertest.Constants.*;
public class TestStartupJFXPanel {
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
new JFXPanel();
try {
Platform.startup(() -> {
});
System.err.println("ERROR: platform startup unexpectedly succeeded");
System.exit(ERROR_STARTUP_SUCCEEDED);
} catch (IllegalStateException ex) {
System.exit(ERROR_NONE);
}
}
}
