package test.shutdowntest;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import test.util.Util;
import static test.shutdowntest.Constants.*;
public class ShutdownHookApp extends Application {
private static Socket socket;
private static OutputStream out;
private static boolean statusWritten = false;
private static void initSocket(String[] args) throws Exception {
int port = Integer.parseInt(args[0]);
socket = new Socket((String)null, port);
out = socket.getOutputStream();
out.write(SOCKET_HANDSHAKE);
out.flush();
}
private synchronized static void writeStatus(int status) {
if (!statusWritten) {
statusWritten = true;
try {
out.write(status);
out.flush();
} catch (IOException ex) {
ex.printStackTrace(System.err);
}
}
}
@Override
public void start(Stage stage) throws Exception {
Runtime.getRuntime().addShutdownHook(new Thread() {
@Override public void run() {
AtomicInteger err = new AtomicInteger(STATUS_OK);
try {
Platform.runLater(() -> {
err.set(STATUS_RUNNABLE_EXECUTED);
});
Util.sleep(500);
} catch (IllegalStateException ex) {
err.set(STATUS_ILLEGAL_STATE);
} catch (Throwable t) {
t.printStackTrace(System.err);
err.set(STATUS_UNEXPECTED_EXCEPTION);
}
writeStatus(err.get());
}
});
Scene scene = new Scene(new Group(), 300, 200);
stage.setScene(scene);
stage.show();
KeyFrame keyFrame = new KeyFrame(Duration.millis(500), e -> {
System.exit(ERROR_NONE);
});
Timeline timeline = new Timeline(keyFrame);
timeline.play();
}
public static void main(String[] args) {
try {
initSocket(args);
} catch (Exception ex) {
ex.printStackTrace(System.err);
System.exit(Constants.ERROR_SOCKET);
}
Application.launch(args);
}
}
