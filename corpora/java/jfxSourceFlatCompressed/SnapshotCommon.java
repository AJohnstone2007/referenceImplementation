package test.javafx.scene;
import test.util.Util;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.SnapshotResult;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import junit.framework.AssertionFailedError;
import static org.junit.Assert.*;
import static test.util.Util.TIMEOUT;
public class SnapshotCommon {
private static final CountDownLatch launchLatch = new CountDownLatch(1);
static MyApp myApp;
public static class MyApp extends Application {
Stage primaryStage;
@Override public void init() {
SnapshotCommon.myApp = this;
}
@Override public void start(Stage primaryStage) throws Exception {
assertTrue(Platform.isFxApplicationThread());
primaryStage.setTitle("Primary stage");
Group root = new Group();
Scene scene = new Scene(root);
scene.setFill(Color.LIGHTYELLOW);
primaryStage.setScene(scene);
primaryStage.setX(0);
primaryStage.setY(0);
primaryStage.setWidth(210);
primaryStage.setHeight(180);
assertFalse(primaryStage.isShowing());
primaryStage.show();
assertTrue(primaryStage.isShowing());
this.primaryStage = primaryStage;
launchLatch.countDown();
}
}
static class TestStage extends Stage {
TestStage(Scene scene) {
this(StageStyle.UNDECORATED, scene);
}
TestStage(StageStyle style, Scene scene) {
this.setTitle("Test stage");
initStyle(style);
this.setScene(scene);
if (scene.getWidth() <= 0) {
this.setWidth(200);
this.setHeight(150);
}
this.setX(225);
this.setY(0);
}
}
static void doSetupOnce() {
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
static void doTeardownOnce() {
Platform.exit();
}
protected void runDeferredSnapshotWait(final Node node,
final Callback<SnapshotResult, Void> cb,
final SnapshotParameters params,
final WritableImage img,
final Runnable runAfter) {
final Throwable[] testError = new Throwable[1];
final CountDownLatch latch = new CountDownLatch(1);
Util.runAndWait(() -> {
node.snapshot(result -> {
try {
cb.call(result);
} catch (Throwable th) {
testError[0] = th;
} finally {
latch.countDown();
}
return null;
}, params, img);
assertEquals(1, latch.getCount());
if (runAfter != null) {
runAfter.run();
}
});
try {
if (!latch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for snapshot callback");
}
} catch (InterruptedException ex) {
AssertionFailedError err = new AssertionFailedError("Unexpected exception");
err.initCause(ex);
throw err;
}
if (testError[0] != null) {
if (testError[0] instanceof Error) {
throw (Error)testError[0];
} else if (testError[0] instanceof RuntimeException) {
throw (RuntimeException)testError[0];
} else {
AssertionFailedError err = new AssertionFailedError("Unknown execution exception");
err.initCause(testError[0].getCause());
throw err;
}
}
}
protected void runDeferredSnapshotWait(final Node node,
final Callback<SnapshotResult, Void> cb,
final SnapshotParameters params,
final WritableImage img) {
runDeferredSnapshotWait(node, cb, params, img, null);
}
protected void runDeferredSnapshotWait(final Scene scene,
final Callback<SnapshotResult, Void> cb,
final WritableImage img) {
final Throwable[] testError = new Throwable[1];
final CountDownLatch latch = new CountDownLatch(1);
Util.runAndWait(() -> {
scene.snapshot(result -> {
try {
cb.call(result);
} catch (Throwable th) {
testError[0] = th;
} finally {
latch.countDown();
}
return null;
}, img);
assertEquals(1, latch.getCount());
});
try {
if (!latch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for snapshot callback");
}
} catch (InterruptedException ex) {
AssertionFailedError err = new AssertionFailedError("Unexpected exception");
err.initCause(ex);
throw err;
}
if (testError[0] != null) {
if (testError[0] instanceof Error) {
throw (Error)testError[0];
} else if (testError[0] instanceof RuntimeException) {
throw (RuntimeException)testError[0];
} else {
AssertionFailedError err = new AssertionFailedError("Unknown execution exception");
err.initCause(testError[0].getCause());
throw err;
}
}
}
}
