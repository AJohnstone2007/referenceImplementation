package test.sandbox.app;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import static test.sandbox.Constants.*;
public class FXApp extends Application {
public static void main(String[] args) {
Util.setupTimeoutThread();
try {
try {
System.getProperty("sun.something");
System.err.println("*** Did not get expected security exception");
System.exit(ERROR_NO_SECURITY_EXCEPTION);
} catch (SecurityException ex) {
}
Application.launch(args);
} catch (SecurityException ex) {
ex.printStackTrace(System.err);
System.exit(ERROR_SECURITY_EXCEPTION);
} catch (RuntimeException ex) {
ex.printStackTrace(System.err);
Throwable cause = ex.getCause();
if (cause instanceof ExceptionInInitializerError) {
cause = cause.getCause();
if (cause instanceof SecurityException) {
System.exit(ERROR_SECURITY_EXCEPTION);
}
}
System.exit(ERROR_UNEXPECTED_EXCEPTION);
} catch (Error | Exception t) {
t.printStackTrace(System.err);
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
}
@Override
public void start(final Stage stage) {
try {
Scene scene = Util.createScene();
stage.setScene(scene);
stage.setX(0);
stage.setY(0);
stage.show();
KeyFrame kf = new KeyFrame(Duration.millis(SHOWTIME), e -> stage.hide());
Timeline timeline = new Timeline(kf);
timeline.play();
} catch (SecurityException ex) {
ex.printStackTrace(System.err);
System.exit(ERROR_SECURITY_EXCEPTION);
} catch (Error | Exception ex) {
ex.printStackTrace(System.err);
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
}
@Override public void stop() {
System.exit(ERROR_NONE);
}
}
