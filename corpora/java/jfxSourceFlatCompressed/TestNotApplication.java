package testapp;
import javafx.application.Application;
import javafx.application.Platform;
public class TestNotApplication {
private static final int ERROR_UNEXPECTED_EXCEPTION = 4;
private static final int ERROR_TOOLKIT_IS_RUNNING = 5;
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
Application.launch(TestAppNoMain.class, args);
}
}
