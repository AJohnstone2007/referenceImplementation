package test.javafx.stage;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import junit.framework.AssertionFailedError;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.util.Util;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static test.util.Util.TIMEOUT;
public class NestedEventLoopTest {
private static final CountDownLatch launchLatch = new CountDownLatch(1);
private static MyApp myApp;
public static class MyApp extends Application {
private Stage primaryStage;
@Override public void init() {
NestedEventLoopTest.myApp = this;
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setTitle("Primary stage");
Group root = new Group();
Scene scene = new Scene(root);
scene.setFill(Color.LIGHTYELLOW);
primaryStage.setScene(scene);
primaryStage.setX(0);
primaryStage.setY(0);
primaryStage.setWidth(210);
primaryStage.setHeight(180);
this.primaryStage = primaryStage;
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
}
@AfterClass
public static void teardownOnce() {
Platform.exit();
}
@Test (expected=IllegalStateException.class)
public void testMustRunOnAppThread() {
assertFalse(Platform.isFxApplicationThread());
Platform.enterNestedEventLoop(new Object());
}
@Test public void testCanEnterAndExitNestedEventLoop() {
final long key = 1024L;
final long result = 2048L;
final AtomicLong returnedValue = new AtomicLong();
Util.runAndWait(
() -> {
assertFalse(Platform.isNestedLoopRunning());
Long actual = (Long) Platform.enterNestedEventLoop(key);
returnedValue.set(actual);
},
() -> {
assertTrue(Platform.isNestedLoopRunning());
Platform.exitNestedEventLoop(key, result);
},
() -> {
assertFalse(Platform.isNestedLoopRunning());
assertEquals(result, returnedValue.get());
}
);
}
@Test (expected=IllegalArgumentException.class)
public void testUniqueKeyRequired() {
final Object key = new Object();
Util.runAndWait(
() -> Platform.enterNestedEventLoop(key),
() -> Platform.enterNestedEventLoop(key),
() -> Platform.exitNestedEventLoop(key, null)
);
}
@Test (expected=NullPointerException.class)
public void testNonNullKeyRequired() {
Util.runAndWait(
() -> Platform.enterNestedEventLoop(null)
);
}
@Test (expected=NullPointerException.class)
public void testNonNullExitKeyRequired() {
Util.runAndWait(
() -> Platform.enterNestedEventLoop("validKey"),
() -> Platform.exitNestedEventLoop(null, null),
() -> Platform.exitNestedEventLoop("validKey", null)
);
}
@Test (expected=IllegalArgumentException.class)
public void testExitLoopKeyHasBeenRegistered() {
Util.runAndWait(
() -> Platform.enterNestedEventLoop("validKey"),
() -> Platform.exitNestedEventLoop("invalidKey", null),
() -> Platform.exitNestedEventLoop("validKey", null)
);
}
@Test public void testCanEnterMultipleNestedLoops_andExitInOrder() {
final long key1 = 1024L;
final long key2 = 1025L;
final long result1 = 2048L;
final long result2 = 2049L;
final AtomicLong returnedValue1 = new AtomicLong();
final AtomicLong returnedValue2 = new AtomicLong();
final AtomicBoolean loopOneRunning = new AtomicBoolean(false);
final AtomicBoolean loopTwoRunning = new AtomicBoolean(false);
Util.runAndWait(
() -> {
assertFalse(Platform.isNestedLoopRunning());
loopOneRunning.set(true);
Long actual = (Long) Platform.enterNestedEventLoop(key1);
loopOneRunning.set(false);
returnedValue1.set(actual);
},
() -> {
assertTrue(Platform.isNestedLoopRunning());
loopTwoRunning.set(true);
Long actual = (Long) Platform.enterNestedEventLoop(key2);
loopTwoRunning.set(false);
returnedValue2.set(actual);
},
() -> {
assertTrue(Platform.isNestedLoopRunning());
Platform.exitNestedEventLoop(key2, result2);
},
() -> {
assertTrue(Platform.isNestedLoopRunning());
assertTrue(loopOneRunning.get());
assertFalse(loopTwoRunning.get());
assertEquals(result2, returnedValue2.get());
},
() -> {
assertTrue(Platform.isNestedLoopRunning());
Platform.exitNestedEventLoop(key1, result1);
},
() -> {
assertFalse(Platform.isNestedLoopRunning());
assertFalse(loopOneRunning.get());
assertFalse(loopTwoRunning.get());
assertEquals(result1, returnedValue1.get());
}
);
}
@Test public void testCanEnterMultipleNestedLoops_andExitOutOfOrder() {
final long key1 = 1024L;
final long key2 = 1025L;
final long key3 = 1026L;
final long result1 = 2048L;
final long result2 = 2049L;
final long result3 = 2050L;
final AtomicLong returnedValue1 = new AtomicLong();
final AtomicLong returnedValue2 = new AtomicLong();
final AtomicLong returnedValue3 = new AtomicLong();
final AtomicBoolean loopOneRunning = new AtomicBoolean(false);
final AtomicBoolean loopTwoRunning = new AtomicBoolean(false);
final AtomicBoolean loopThreeRunning = new AtomicBoolean(false);
Util.runAndWait(
() -> {
assertFalse(Platform.isNestedLoopRunning());
loopOneRunning.set(true);
Long actual = (Long) Platform.enterNestedEventLoop(key1);
loopOneRunning.set(false);
returnedValue1.set(actual);
},
() -> {
assertTrue(Platform.isNestedLoopRunning());
loopTwoRunning.set(true);
Long actual = (Long) Platform.enterNestedEventLoop(key2);
loopTwoRunning.set(false);
returnedValue2.set(actual);
},
() -> {
assertTrue(Platform.isNestedLoopRunning());
loopThreeRunning.set(true);
Long actual = (Long) Platform.enterNestedEventLoop(key3);
loopThreeRunning.set(false);
returnedValue3.set(actual);
},
() -> {
assertTrue(Platform.isNestedLoopRunning());
Platform.exitNestedEventLoop(key2, result2);
},
() -> {
assertTrue(Platform.isNestedLoopRunning());
assertTrue(loopOneRunning.get());
assertTrue(loopTwoRunning.get());
assertTrue(loopThreeRunning.get());
assertEquals(0, returnedValue2.get());
},
() -> {
assertTrue(Platform.isNestedLoopRunning());
Platform.exitNestedEventLoop(key3, result3);
},
() -> {
assertTrue(Platform.isNestedLoopRunning());
assertTrue(loopOneRunning.get());
},
() -> {
assertTrue(Platform.isNestedLoopRunning());
Platform.exitNestedEventLoop(key1, result1);
}
);
Util.runAndWait(() -> {
assertFalse(loopTwoRunning.get());
assertFalse(loopThreeRunning.get());
assertEquals(result2, returnedValue2.get());
assertEquals(result3, returnedValue3.get());
});
Util.runAndWait(() -> {
assertFalse(Platform.isNestedLoopRunning());
assertFalse(loopOneRunning.get());
assertFalse(loopTwoRunning.get());
assertFalse(loopThreeRunning.get());
assertEquals(result1, returnedValue1.get());
});
}
}
