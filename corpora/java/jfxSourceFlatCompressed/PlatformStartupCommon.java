package test.com.sun.javafx.application;
import com.sun.javafx.application.PlatformImplShim;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import junit.framework.AssertionFailedError;
import test.util.Util;
import static org.junit.Assert.*;
import static test.util.Util.TIMEOUT;
public class PlatformStartupCommon {
private static final int SLEEP_TIME = 1000;
private final CountDownLatch startupLatch = new CountDownLatch(1);
private Stage mainStage;
private void createMainStage() {
mainStage = new Stage();
mainStage.setTitle("Primary stage");
Group root = new Group();
Scene scene = new Scene(root);
scene.setFill(Color.LIGHTYELLOW);
mainStage.setScene(scene);
mainStage.setX(0);
mainStage.setY(0);
mainStage.setWidth(210);
mainStage.setHeight(180);
}
private void doTestCommon(final boolean implicitExit) {
final Throwable[] testError = new Throwable[1];
final Thread testThread = Thread.currentThread();
assertFalse(Platform.isFxApplicationThread());
assertEquals(1, startupLatch.getCount());
Platform.setImplicitExit(implicitExit);
Platform.startup(() -> {
try {
assertTrue(Platform.isFxApplicationThread());
startupLatch.countDown();
assertEquals(0, startupLatch.getCount());
} catch (Throwable th) {
testError[0] = th;
testThread.interrupt();
}
});
assertFalse(Platform.isFxApplicationThread());
try {
if (!startupLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Toolkit to start");
}
final CountDownLatch rDone = new CountDownLatch(1);
Platform.runLater(() -> {
try {
throw new RuntimeException("this exception is expected");
} finally {
rDone.countDown();
}
});
if (!rDone.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for runLater with Exception");
}
Util.runAndWait(() -> {
createMainStage();
mainStage.show();
});
Thread.sleep(SLEEP_TIME);
Util.runAndWait(mainStage::hide);
Thread.sleep(SLEEP_TIME);
final CountDownLatch exitLatch = PlatformImplShim.test_getPlatformExitLatch();
if (implicitExit) {
assertEquals(0, exitLatch.getCount());
final AtomicBoolean isAlive = new AtomicBoolean(false);
Platform.runLater(() -> isAlive.set(true));
Thread.sleep(SLEEP_TIME);
assertFalse(isAlive.get());
} else {
assertEquals(1, exitLatch.getCount());
AtomicBoolean isAlive = new AtomicBoolean(false);
Util.runAndWait(() -> isAlive.set(true));
assertTrue(isAlive.get());
Platform.exit();
if (!exitLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Platform to exit");
}
}
} catch (InterruptedException ex) {
if (testError[0] != null) {
Util.throwError(testError[0]);
} else {
fail("Unexpected exception: " + ex);
}
}
}
protected void doTestStartupExplicitExit() {
doTestCommon(false);
}
protected void doTestStartupImplicitExit() {
doTestCommon(true);
}
}
