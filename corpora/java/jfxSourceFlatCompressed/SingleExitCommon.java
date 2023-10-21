package test.com.sun.javafx.application;
import com.sun.javafx.application.PlatformImplShim;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import junit.framework.AssertionFailedError;
import test.util.Util;
import static org.junit.Assert.*;
import static test.util.Util.TIMEOUT;
public class SingleExitCommon {
private static final int SLEEP_TIME = 1000;
private static final CountDownLatch initialized = new CountDownLatch(1);
private static final CountDownLatch started = new CountDownLatch(1);
private static final CountDownLatch stopped = new CountDownLatch(1);
private static boolean implicitExit;
private static boolean stageShown;
private static volatile boolean appShouldExit;
private static MyApp myApp;
public enum ThrowableType {
NONE,
EXCEPTION,
ERROR
}
public static class MyApp extends Application {
private Stage primaryStage;
@Override public void init() {
assertEquals(1, initialized.getCount());
assertEquals(1, started.getCount());
assertEquals(1, stopped.getCount());
SingleExitCommon.myApp = this;
assertTrue(Platform.isImplicitExit());
if (!implicitExit) {
Platform.setImplicitExit(false);
assertFalse(Platform.isImplicitExit());
}
initialized.countDown();
assertEquals(0, initialized.getCount());
}
@Override public void start(Stage primaryStage) throws Exception {
assertEquals(0, initialized.getCount());
assertEquals(1, started.getCount());
assertEquals(1, stopped.getCount());
this.primaryStage = primaryStage;
primaryStage.setTitle("Primary stage");
Group root = new Group();
Scene scene = new Scene(root);
scene.setFill(Color.LIGHTYELLOW);
primaryStage.setScene(scene);
primaryStage.setX(0);
primaryStage.setY(0);
primaryStage.setWidth(210);
primaryStage.setHeight(180);
if (stageShown) {
primaryStage.show();
}
started.countDown();
assertEquals(0, started.getCount());
}
@Override public void stop() {
if (appShouldExit) {
assertEquals(0, initialized.getCount());
assertEquals(0, started.getCount());
assertEquals(1, stopped.getCount());
stopped.countDown();
assertEquals(0, stopped.getCount());
} else {
stopped.countDown();
throw new AssertionFailedError("Unexpected call to stop method");
}
}
}
private void doTestCommon(boolean implicitExit,
boolean reEnableImplicitExit, boolean stageShown,
boolean appShouldExit) {
doTestCommon(implicitExit, reEnableImplicitExit, stageShown,
ThrowableType.NONE, appShouldExit);
}
private void doTestCommon(boolean implicitExit,
boolean reEnableImplicitExit, boolean stageShown,
final ThrowableType throwableType, boolean appShouldExit) {
SingleExitCommon.implicitExit = implicitExit;
SingleExitCommon.stageShown = stageShown;
SingleExitCommon.appShouldExit = appShouldExit;
final Throwable[] testError = new Throwable[1];
final CountDownLatch latch = new CountDownLatch(1);
final Thread testThread = Thread.currentThread();
new Thread(() -> {
try {
Application.launch(MyApp.class, (String[])null);
latch.countDown();
} catch (Throwable th) {
testError[0] = th;
testThread.interrupt();
}
}).start();
try {
if (!initialized.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Application to launch and initialize");
}
if (!started.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Application to start");
}
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
if (!rDone.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for runLater, throwableType = "
+ throwableType);
}
if (stageShown) {
Thread.sleep(SLEEP_TIME);
Util.runAndWait(myApp.primaryStage::hide);
}
final CountDownLatch exitLatch = PlatformImplShim.test_getPlatformExitLatch();
if (reEnableImplicitExit) {
Thread.sleep(SLEEP_TIME);
assertEquals(1, stopped.getCount());
assertEquals(1, exitLatch.getCount());
assertEquals(1, latch.getCount());
assertFalse(Platform.isImplicitExit());
Platform.setImplicitExit(true);
assertTrue(Platform.isImplicitExit());
}
if (!appShouldExit) {
Thread.sleep(SLEEP_TIME);
assertEquals(1, stopped.getCount());
assertEquals(1, exitLatch.getCount());
assertEquals(1, latch.getCount());
SingleExitCommon.appShouldExit = true;
Platform.exit();
}
if (!stopped.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Application to stop");
}
if (!exitLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Platform to exit");
}
if (!latch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for launch to return");
}
} catch (InterruptedException ex) {
Util.throwError(testError[0]);
}
}
public void doTestImplicitExit() {
doTestCommon(true, false, true, true);
}
public void doTestExplicitExit() {
doTestCommon(false, false, true, false);
}
public void doTestExplicitExitReEnable() {
doTestCommon(false, true, true, true);
}
public void doTestNoShowImplicit() {
doTestCommon(true, false, false, false);
}
public void doTestNoShowExplicit() {
doTestCommon(false, false, false, false);
}
public void doTestNoShowExplicitReEnable() {
doTestCommon(false, true, false, false);
}
public void doTestImplicitExitWithException() {
doTestCommon(true, false, true, ThrowableType.EXCEPTION, true);
}
public void doTestExplicitExitWithException() {
doTestCommon(false, false, true, ThrowableType.EXCEPTION, false);
}
public void doTestExplicitExitReEnableWithException() {
doTestCommon(false, true, true, ThrowableType.EXCEPTION, true);
}
public void doTestNoShowImplicitWithException() {
doTestCommon(true, false, false, ThrowableType.EXCEPTION, false);
}
public void doTestNoShowExplicitWithException() {
doTestCommon(false, false, false, ThrowableType.EXCEPTION, false);
}
public void doTestNoShowExplicitReEnableWithException() {
doTestCommon(false, true, false, ThrowableType.EXCEPTION, false);
}
public void doTestImplicitExitWithError() {
doTestCommon(true, false, true, ThrowableType.ERROR, true);
}
public void doTestExplicitExitWithError() {
doTestCommon(false, false, true, ThrowableType.ERROR, false);
}
public void doTestExplicitExitReEnableWithError() {
doTestCommon(false, true, true, ThrowableType.ERROR, true);
}
public void doTestNoShowImplicitWithError() {
doTestCommon(true, false, false, ThrowableType.ERROR, false);
}
public void doTestNoShowExplicitWithError() {
doTestCommon(false, false, false, ThrowableType.ERROR, false);
}
public void doTestNoShowExplicitReEnableWithError() {
doTestCommon(false, true, false, ThrowableType.ERROR, false);
}
}
