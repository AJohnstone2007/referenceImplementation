package test.javafx.embed.swing;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.awt.dnd.DropTarget;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.swing.JLabel;
import test.util.Util;
import static test.util.Util.TIMEOUT;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
public class SwingNodeDnDMemoryLeakTest {
final static int TOTAL_SWINGNODE = 10;
static CountDownLatch launchLatch;
final static int GC_ATTEMPTS = 10;
ArrayList<WeakReference<SwingNode>> weakRefArrSN =
new ArrayList(TOTAL_SWINGNODE);
@BeforeClass
public static void setupOnce() throws Exception {
launchLatch = new CountDownLatch(1);
new Thread(() -> Application.launch(SwingNodeDnDMemoryLeakTest.MyApp.class,
(String[])null)).start();
assertTrue("Timeout waiting for Application to launch",
launchLatch.await(10, TimeUnit.SECONDS));
}
@AfterClass
public static void teardownOnce() {
Platform.exit();
}
@Test
public void testSwingNodeMemoryLeak() {
Util.runAndWait(() -> {
testSwingNodeObjectsInStage();
});
attemptGCSwingNode();
assertEquals(TOTAL_SWINGNODE, getCleanedUpSwingNodeCount());
}
private void testSwingNodeObjectsInStage() {
Stage tempStage[] = new Stage[TOTAL_SWINGNODE];
for (int i = 0; i < TOTAL_SWINGNODE; i++) {
BorderPane root = new BorderPane();
SwingNode sw = new SwingNode();
JLabel label = new JLabel("SWING");
label.setDropTarget(new DropTarget());
sw.setContent(label);
WeakReference<SwingNode> ref = new WeakReference<SwingNode>(sw);
weakRefArrSN.add(i, ref);
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
assertEquals(TOTAL_SWINGNODE, weakRefArrSN.size());
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
private int getCleanedUpSwingNodeCount() {
int count = 0;
for (WeakReference<SwingNode> ref : weakRefArrSN) {
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
