package testapp;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
public class TestAppNoMain extends Application {
private static final int SHOWTIME = 2500;
private static final int ERROR_NONE = 2;
private static final int ERROR_TOOLKIT_NOT_RUNNING = 3;
private static final int ERROR_UNEXPECTED_EXCEPTION = 4;
public static Parent createGraph() {
Label label = new Label("JavaFX Modular App Test");
label.setStyle("-fx-font-size: 24; -fx-text-fill: orange");
StackPane root = new StackPane();
root.getChildren().add(label);
return root;
}
@Override
public void start(final Stage stage) {
try {
stage.setTitle("JavaFX Modular App (no main)");
Scene scene = new Scene(createGraph(), 400, 300);
stage.setScene(scene);
stage.setX(0);
stage.setY(0);
stage.show();
KeyFrame kf = new KeyFrame(Duration.millis(SHOWTIME), e -> stage.hide());
Timeline timeline = new Timeline(kf);
timeline.play();
} catch (Error | Exception ex) {
ex.printStackTrace(System.err);
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
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
