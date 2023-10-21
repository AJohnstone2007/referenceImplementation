package test.javafx.scene;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import javafx.scene.paint.Color;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotResult;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import junit.framework.AssertionFailedError;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import test.util.Util;
import static org.junit.Assert.*;
import static test.util.Util.TIMEOUT;
public class Snapshot1Test extends SnapshotCommon {
static final int SLEEP_TIME = 100;
static final int SHORT_TIMEOUT = 1000;
@BeforeClass
public static void setupOnce() {
doSetupOnce();
}
@AfterClass
public static void teardownOnce() {
doTeardownOnce();
}
private Scene tmpScene = null;
private Node tmpNode = null;
@Before
public void setupEach() {
assertNotNull(myApp);
assertNotNull(myApp.primaryStage);
assertTrue(myApp.primaryStage.isShowing());
}
@After
public void teardownEach() {
}
@Test
public void testConstructSceneWrongThread() {
assertFalse(Platform.isFxApplicationThread());
Group root = new Group();
tmpScene = new Scene(root);
assertNotNull(tmpScene);
}
public void testConstructGraphOffThreadOk() {
assertFalse(Platform.isFxApplicationThread());
Group root = new Group();
Rectangle rect = new Rectangle(10, 10);
rect.setFill(Color.RED);
root.getChildren().add(rect);
}
@Test (expected=IllegalStateException.class)
public void testSnapshotSceneImmediateWrongThread() {
assertFalse(Platform.isFxApplicationThread());
Util.runAndWait(() -> tmpScene = new Scene(new Group(), 200, 100));
tmpScene.snapshot(null);
}
@Test (expected=IllegalStateException.class)
public void testSnapshotSceneDeferredWrongThread() {
assertFalse(Platform.isFxApplicationThread());
Util.runAndWait(() -> tmpScene = new Scene(new Group(), 200, 100));
tmpScene.snapshot(p -> {
throw new AssertionFailedError("Should never get here");
}, null);
}
@Test (expected=IllegalStateException.class)
public void testSnapshotNodeImmediateWrongThread() {
assertFalse(Platform.isFxApplicationThread());
tmpNode = new Rectangle(10, 10);
tmpNode.snapshot(null, null);
}
@Test (expected=IllegalStateException.class)
public void testSnapshotNodeDeferredWrongThread() {
assertFalse(Platform.isFxApplicationThread());
tmpNode = new Rectangle(10, 10);
tmpNode.snapshot(p -> {
throw new AssertionFailedError("Should never get here");
}, null, null);
}
@Test
public void testSceneImmediate() {
Util.runAndWait(() -> {
tmpScene = new Scene(new Group(), 200, 100);
WritableImage img = tmpScene.snapshot(null);
assertNotNull(img);
});
}
@Test
public void testSceneCallback() {
final CountDownLatch latch = new CountDownLatch(1);
Util.runAndWait(() -> {
tmpScene = new Scene(new Group(), 200, 100);
Callback<SnapshotResult, Void> cb = param -> {
assertNotNull(param);
latch.countDown();
return null;
};
tmpScene.snapshot(cb, null);
Util.sleep(SLEEP_TIME);
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
assertEquals(0, latch.getCount());
}
@Test
public void testBadSceneCallback1() {
final CountDownLatch latch = new CountDownLatch(1);
Util.runAndWait(() -> {
tmpScene = new Scene(new Group(), 200, 100);
Callback<SnapshotResult, Void> cb = new Callback() {
@Override public Object call(Object param) {
assertNotNull(param);
latch.countDown();
return "";
}
};
tmpScene.snapshot(cb, null);
Util.sleep(SLEEP_TIME);
assertEquals(1, latch.getCount());
System.err.println("testBadSceneCallback1: a ClassCastException warning message is expected here");
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
assertEquals(0, latch.getCount());
}
@Test
public void testBadSceneCallback2() {
final CountDownLatch latch = new CountDownLatch(1);
Util.runAndWait(() -> {
tmpScene = new Scene(new Group(), 200, 100);
Callback cb = (Callback<String, Integer>) param -> {
latch.countDown();
throw new AssertionFailedError("Should never get here");
};
tmpScene.snapshot(cb, null);
Util.sleep(SLEEP_TIME);
assertEquals(1, latch.getCount());
System.err.println("testBadSceneCallback2: a ClassCastException warning message is expected here");
});
try {
if (latch.await(SHORT_TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Snapshot callback unexpectedly called");
}
} catch (InterruptedException ex) {
AssertionFailedError err = new AssertionFailedError("Unexpected exception");
err.initCause(ex);
throw err;
}
assertEquals(1, latch.getCount());
}
@Test (expected=NullPointerException.class)
public void testNullSceneCallback() {
Util.runAndWait(() -> {
tmpScene = new Scene(new Group(), 200, 100);
tmpScene.snapshot(null, null);
});
}
@Test
public void testNodeImmediate() {
Util.runAndWait(() -> {
tmpNode = new Rectangle(10, 10);
WritableImage img = tmpNode.snapshot(null, null);
assertNotNull(img);
});
}
@Test
public void testNodeCallback() {
final CountDownLatch latch = new CountDownLatch(1);
Util.runAndWait(() -> {
tmpNode = new Rectangle(10, 10);
Callback<SnapshotResult, Void> cb = param -> {
assertNotNull(param);
latch.countDown();
return null;
};
tmpNode.snapshot(cb, null, null);
Util.sleep(SLEEP_TIME);
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
assertEquals(0, latch.getCount());
}
@Test
public void testBadNodeCallback1() {
final CountDownLatch latch = new CountDownLatch(1);
Util.runAndWait(() -> {
tmpNode = new Rectangle(10, 10);
Callback<SnapshotResult, Void> cb = new Callback() {
@Override public Object call(Object param) {
assertNotNull(param);
latch.countDown();
return "";
}
};
tmpNode.snapshot(cb, null, null);
Util.sleep(SLEEP_TIME);
assertEquals(1, latch.getCount());
System.err.println("testBadNodeCallback1: a ClassCastException warning message is expected here");
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
assertEquals(0, latch.getCount());
}
@Test
public void testBadNodeCallback2() {
final CountDownLatch latch = new CountDownLatch(1);
Util.runAndWait(() -> {
tmpNode = new Rectangle(10, 10);
Callback cb = (Callback<String, Integer>) param -> {
latch.countDown();
throw new AssertionFailedError("Should never get here");
};
tmpNode.snapshot(cb, null, null);
Util.sleep(SLEEP_TIME);
assertEquals(1, latch.getCount());
System.err.println("testBadNodeCallback2: a ClassCastException warning message is expected here");
});
try {
if (latch.await(SHORT_TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Snapshot callback unexpectedly called");
}
} catch (InterruptedException ex) {
AssertionFailedError err = new AssertionFailedError("Unexpected exception");
err.initCause(ex);
throw err;
}
assertEquals(1, latch.getCount());
}
@Test (expected=NullPointerException.class)
public void testNullNodeCallback() {
Util.runAndWait(() -> {
tmpNode = new Rectangle(10, 10);
tmpNode.snapshot(null, null, null);
});
}
@Test (expected=IllegalArgumentException.class)
public void testCreateImageZero() {
WritableImage wimg = new WritableImage(0, 0);
}
@Test
public void testCreateImageNonZero() {
WritableImage wimg = new WritableImage(1, 1);
}
}
