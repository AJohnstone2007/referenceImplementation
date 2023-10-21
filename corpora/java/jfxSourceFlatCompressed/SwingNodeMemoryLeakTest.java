package test.javafx.embed.swing;
import com.sun.javafx.PlatformUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javax.swing.JLabel;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import test.util.Util;
import static test.util.Util.TIMEOUT;
import junit.framework.AssertionFailedError;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;
public class SwingNodeMemoryLeakTest {
final static int TOTAL_SWINGNODE = 10;
static CountDownLatch launchLatch;
final static int GC_ATTEMPTS = 10;
ArrayList<WeakReference<SwingNode>> weakRefArrSN =
new ArrayList(TOTAL_SWINGNODE);
ArrayList<WeakReference<JLabel>> weakRefArrJL =
new ArrayList(TOTAL_SWINGNODE);
@BeforeClass
public static void setupOnce() {
launchLatch = new CountDownLatch(1);
new Thread(() -> Application.launch(SwingNodeMemoryLeakTest.MyApp.class, (String[])null)).start();
try {
if (!launchLatch.await(5 * TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Application to launch ("+
(5 * TIMEOUT) + " seconds)");
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
@Test
public void testSwingNodeMemoryLeak() {
if (PlatformUtil.isMac()) {
assumeTrue(Boolean.getBoolean("unstable.test"));
}
Util.runAndWait(() -> {
testSwingNodeObjectsInStage();
});
attemptGCSwingNode();
assertEquals(TOTAL_SWINGNODE, getCleanedUpSwingNodeCount());
attemptGCJLabel();
assertEquals(TOTAL_SWINGNODE, getCleanedUpJLabelCount());
}
private void testSwingNodeObjectsInStage() {
Stage tempStage[] = new Stage[TOTAL_SWINGNODE];
for (int i = 0; i < TOTAL_SWINGNODE; i++) {
BorderPane root = new BorderPane();
SwingNode sw = new SwingNode();
JLabel label = new JLabel("SWING");
sw.setContent(label);
weakRefArrSN.add(i, new WeakReference<SwingNode>(sw));
weakRefArrJL.add(i, new WeakReference<JLabel>(label));
root.centerProperty().set(sw);
Stage stage = new Stage();
Scene scene = new Scene(root, 150, 100);
stage.setScene(scene);
tempStage[i] = stage;
}
if (TOTAL_SWINGNODE != weakRefArrSN.size()) {
System.out.println("TOTAL_SWINGNODE != weakRefArr.size()");
}
assertEquals(0, getCleanedUpSwingNodeCount());
assertEquals(0, getCleanedUpJLabelCount());
assertEquals(TOTAL_SWINGNODE, weakRefArrSN.size());
assertEquals(TOTAL_SWINGNODE, weakRefArrJL.size());
for (int i = 0; i < TOTAL_SWINGNODE; i++) {
if (tempStage[i] != null) {
tempStage[i].close();
tempStage[i] = null;
}
}
}
private void attemptGCSwingNode() {
for (int i = 0; i < GC_ATTEMPTS; i++) {
System.gc();
if (getCleanedUpSwingNodeCount() == TOTAL_SWINGNODE) {
break;
}
try {
Thread.sleep(250);
} catch (InterruptedException e) {
System.err.println("InterruptedException occurred during Thread.sleep()");
}
}
}
private void attemptGCJLabel() {
for (int i = 0; i < GC_ATTEMPTS; i++) {
System.gc();
if (getCleanedUpJLabelCount() == TOTAL_SWINGNODE) {
break;
}
try {
Thread.sleep(250);
} catch (InterruptedException e) {
System.err.println("InterruptedException occurred during Thread.sleep()");
}
}
}
private int getCleanedUpSwingNodeCount() {
int count = 0;
for (WeakReference<SwingNode> ref : weakRefArrSN) {
if (ref.get() == null) {
count++;
}
}
return count;
}
private int getCleanedUpJLabelCount() {
int count = 0;
for (WeakReference<JLabel> ref : weakRefArrJL) {
if (ref.get() == null) {
count++;
}
}
return count;
}
public static class MyApp extends Application {
@Override
public void start(Stage stage) throws Exception {
launchLatch.countDown();
}
}
}
