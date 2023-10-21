package test.javafx.scene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import test.util.Util;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import static org.junit.Assert.assertTrue;
public class NodeTreeShowingTest {
private static CountDownLatch startupLatch;
private static Stage stage;
private static BorderPane rootPane;
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
stage = primaryStage;
rootPane = new BorderPane();
stage.setScene(new Scene(rootPane));
stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e -> {
Platform.runLater(() -> startupLatch.countDown());
});
stage.show();
}
}
@BeforeClass
public static void initFX() throws Exception {
startupLatch = new CountDownLatch(1);
new Thread(() -> Application.launch(NodeTreeShowingTest.TestApp.class, (String[]) null)).start();
assertTrue("Timeout waiting for FX runtime to start", startupLatch.await(15, TimeUnit.SECONDS));
}
private StackPane createNodesRecursively(int count, int level) {
StackPane pane = new StackPane();
for (int i = 0; i < count; i++) {
pane.getChildren().add(level == 1 ? new StackPane() : createNodesRecursively(count, level - 1));
}
return pane;
}
@Test
public void testAddRemovalSpeedInHugeScene() throws Exception {
Random rnd = new Random(0);
int loopCount = 10000;
int levels = 13;
int nodesPerLevel = 2;
int leafCount = (int)Math.pow(nodesPerLevel, levels);
int total = leafCount * 2 - 1;
StackPane testNode = new StackPane();
StackPane root = createNodesRecursively(nodesPerLevel, levels);
AtomicLong bestMillis = new AtomicLong(Long.MAX_VALUE);
Util.runAndWait(() -> {
rootPane.setCenter(root);
});
for (int j = 0; j < 5; j++) {
int loopNumber = j + 1;
Util.runAndWait(() -> {
long startTime = System.currentTimeMillis();
for (int i = 0; i < loopCount; i++) {
int index = rnd.nextInt(leafCount);
StackPane current = root;
while (index >= nodesPerLevel) {
current = (StackPane) current.getChildren().get(index % nodesPerLevel);
index /= nodesPerLevel;
}
current.getChildren().add(current.getChildren().remove(index));
}
long endTime = System.currentTimeMillis();
bestMillis.set(Math.min(endTime - startTime, bestMillis.get()));
System.out.println("Loop " + loopNumber + ": Time to add/remove " + loopCount + " nodes in "
+ "a Scene consisting of " + total + " nodes = " + (endTime - startTime) + " mSec");
});
}
assertTrue("Time to add/remove " + loopCount + " nodes in a large Scene is more than 800 mSec (" + bestMillis.get() + ")", bestMillis.get() <= 800);
}
@AfterClass
public static void teardownOnce() {
Platform.runLater(() -> {
stage.hide();
Platform.exit();
});
}
}
