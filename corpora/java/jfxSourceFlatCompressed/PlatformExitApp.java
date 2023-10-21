package test.launchertest;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import static test.launchertest.Constants.*;
public class PlatformExitApp extends Application {
private static final int TIMEOUT = 20000;
public static void sleep(long msec) {
try {
Thread.sleep(msec);
} catch (InterruptedException ex) {
ex.printStackTrace();
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
}
public static void setupTimeoutThread() {
Thread th = new Thread(() -> {
sleep(TIMEOUT);
System.exit(ERROR_TIMEOUT);
});
th.setDaemon(true);
th.start();
}
@Override public void start(Stage stage) throws Exception {
StackPane root = new StackPane();
Scene scene = new Scene(root, 400, 300);
final Label label = new Label("Hello");
root.getChildren().add(label);
stage.setScene(scene);
stage.show();
Thread thr = new Thread(() -> {
sleep(1000);
Platform.exit();
});
thr.start();
}
public static void main(String[] args) {
setupTimeoutThread();
Application.launch(args);
sleep(500);
System.exit(ERROR_NONE);
}
}
