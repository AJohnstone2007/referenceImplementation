package test.com.sun.javafx.application;
import com.sun.javafx.application.PlatformImpl;
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
public class ListenerTestCommon {
private static final int DELAY = 10;
private static final int SLEEP_TIME = 1000;
private final CountDownLatch launchLatch = new CountDownLatch(1);
private CountDownLatch exitLatch;
private PlatformImpl.FinishListener listener = null;
private final CountDownLatch idleNotification = new CountDownLatch(1);
private final CountDownLatch exitNotification = new CountDownLatch(1);
private final AtomicBoolean implicitExit = new AtomicBoolean();
private Stage stage;
public enum ThrowableType {
NONE,
EXCEPTION,
ERROR
}
private void setup() {
new Thread(() -> PlatformImpl.startup(() -> {
assertTrue(Platform.isFxApplicationThread());
launchLatch.countDown();
})).start();
try {
if (!launchLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Platform to start");
}
} catch (InterruptedException ex) {
AssertionFailedError err = new AssertionFailedError("Unexpected exception");
err.initCause(ex);
throw err;
}
exitLatch = PlatformImplShim.test_getPlatformExitLatch();
assertEquals(1, exitLatch.getCount());
assertEquals(0, launchLatch.getCount());
assertEquals(1, exitLatch.getCount());
assertNull(listener);
listener = new PlatformImpl.FinishListener() {
public void idle(boolean flag) {
implicitExit.set(flag);
idleNotification.countDown();
}
public void exitCalled() {
exitNotification.countDown();
}
};
PlatformImpl.addListener(listener);
}
private Stage makeStage() {
Stage stg = new Stage();
stg.setTitle("Primary stage");
Group root = new Group();
Scene scene = new Scene(root);
scene.setFill(Color.LIGHTYELLOW);
stg.setScene(scene);
stg.setX(0);
stg.setY(0);
stg.setWidth(210);
stg.setHeight(180);
return stg;
}
public void doTestExit() {
setup();
assertNotNull(listener);
Util.runAndWait(() -> {
assertTrue(Platform.isFxApplicationThread());
assertTrue(Platform.isImplicitExit());
});
Util.sleep(DELAY);
assertEquals(1, exitNotification.getCount());
assertEquals(1, idleNotification.getCount());
Platform.exit();
try {
if (!exitNotification.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for exit notification");
}
} catch (InterruptedException ex) {
AssertionFailedError err = new AssertionFailedError("Unexpected exception");
err.initCause(ex);
throw err;
}
assertEquals(0, exitNotification.getCount());
Util.sleep(DELAY);
assertEquals(1, idleNotification.getCount());
assertEquals(1, exitLatch.getCount());
PlatformImpl.removeListener(listener);
listener = null;
}
public void doTestIdleImplicit(final boolean implicit,
final ThrowableType throwableType) {
setup();
assertNotNull(listener);
Util.runAndWait(() -> {
assertTrue(Platform.isFxApplicationThread());
assertTrue(Platform.isImplicitExit());
if (!implicit) {
Platform.setImplicitExit(false);
}
PlatformImpl.addListener(listener);
});
Util.sleep(DELAY);
assertEquals(1, exitNotification.getCount());
assertEquals(1, idleNotification.getCount());
Util.runAndWait(() -> {
stage = makeStage();
stage.show();
});
Util.sleep(SLEEP_TIME);
assertEquals(1, exitNotification.getCount());
assertEquals(1, idleNotification.getCount());
final CountDownLatch rDone = new CountDownLatch(1);
Platform.runLater(() -> {
try {
if (throwableType == ThrowableType.EXCEPTION) {
throw new RuntimeException("this exception is expected");
} else if (throwableType == ThrowableType.ERROR) {
throw new InternalError("this error is expected");
}
} finally {
rDone.countDown();
}
});
try {
if (!rDone.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for runLater, throwableType = "
+ throwableType);
}
} catch (InterruptedException ex) {
throw new AssertionFailedError("Unexpected exception waiting for runLater, throwableType = "
+ throwableType);
}
Util.runAndWait(stage::hide);
try {
if (!idleNotification.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for exit notification");
}
} catch (InterruptedException ex) {
AssertionFailedError err = new AssertionFailedError("Unexpected exception");
err.initCause(ex);
throw err;
}
assertEquals(0, idleNotification.getCount());
assertEquals(implicit, implicitExit.get());
Util.sleep(DELAY);
assertEquals(1, exitNotification.getCount());
assertEquals(1, exitLatch.getCount());
PlatformImpl.removeListener(listener);
listener = null;
}
}
