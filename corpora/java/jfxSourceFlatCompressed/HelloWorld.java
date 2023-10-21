package testapp;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import pkg2.Util;
public class HelloWorld extends Application {
private static final int SHOWTIME = 2500;
private static final int ERROR_NONE = 2;
private static final int ERROR_UNEXPECTED_EXCEPTION = 4;
public static void main(String[] args) {
try {
Application.launch(args);
} catch (Throwable t) {
t.printStackTrace(System.err);
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
}
@Override
public void start(final Stage stage) {
try {
stage.setTitle("JavaFX Legacy App");
Scene scene = new Scene(Util.createGraph(), 400, 300);
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
}
