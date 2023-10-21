package test.javafx.scene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import test.util.Util;
import static org.junit.Assert.fail;
import test.util.memory.JMemoryBuddy;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
public class InitialNodesMemoryLeakTest {
static CountDownLatch startupLatch;
static WeakReference<Group> groupWRef;
static Stage stage;
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
stage = primaryStage;
Group group = new Group();
groupWRef = new WeakReference<>(group);
Group root = new Group(group);
stage.setScene(new Scene(root));
stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e -> {
Platform.runLater(() -> {
root.getChildren().clear();
startupLatch.countDown();
});
});
stage.show();
}
}
@BeforeClass
public static void initFX() {
startupLatch = new CountDownLatch(1);
new Thread(() -> Application.launch(InitialNodesMemoryLeakTest.TestApp.class, (String[])null)).start();
try {
if (!startupLatch.await(15, TimeUnit.SECONDS)) {
fail("Timeout waiting for FX runtime to start");
}
} catch (InterruptedException ex) {
fail("Unexpected exception: " + ex);
}
}
@Test
public void testRootNodeMemoryLeak() throws Exception {
JMemoryBuddy.assertCollectable(groupWRef);
}
@AfterClass
public static void teardownOnce() {
Platform.runLater(() -> {
stage.hide();
Platform.exit();
});
}
}
