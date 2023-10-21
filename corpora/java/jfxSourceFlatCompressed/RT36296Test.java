package test.com.sun.javafx.sg.prism;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import junit.framework.AssertionFailedError;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import static test.util.Util.TIMEOUT;
public class RT36296Test {
CountDownLatch latch = new CountDownLatch(1);
private static final CountDownLatch launchLatch = new CountDownLatch(1);
static MyApp myApp;
public static class MyApp extends Application {
@Override public void init() {
RT36296Test.myApp = this;
}
@Override public void start(Stage primaryStage) throws Exception {
launchLatch.countDown();
}
}
@BeforeClass
public static void setupOnce() {
new Thread(() -> Application.launch(MyApp.class, (String[])null)).start();
try {
if (!launchLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Application to launch");
}
} catch (InterruptedException ex) {
AssertionFailedError err = new AssertionFailedError("Unexpected exception");
err.initCause(ex);
throw err;
}
assertEquals(0, launchLatch.getCount());
}
@AfterClass
public static void teardownOnce() {
Platform.exit();
}
@Test(timeout = 15000)
public void TestBug() {
Label label = new Label();
label.setStyle(" -fx-border-style:dashed; -fx-border-width:0; ");
label.setText("test");
SnapshotParameters params = new SnapshotParameters();
params.setViewport(new Rectangle2D(0, 0, 100, 100));
Platform.runLater(() -> {
Scene scene = new Scene(new Group(label));
label.snapshot(p -> done(), params, new WritableImage(100, 100));
});
try {
latch.await();
} catch (InterruptedException ex) {
Logger.getLogger(RT36296Test.class.getName()).log(Level.SEVERE, null, ex);
}
}
public Void done() {
latch.countDown();
return null;
}
}
