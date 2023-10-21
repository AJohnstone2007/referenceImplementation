package test.javafx.scene;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.stage.Stage;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import test.util.Util;
import test.util.memory.JMemoryBuddy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
public class StyleMemoryLeakTest {
@BeforeClass
public static void initFX() throws Exception {
CountDownLatch startupLatch = new CountDownLatch(1);
Platform.startup(() -> {
Platform.setImplicitExit(false);
startupLatch.countDown();
});
assertTrue("Timeout waiting for FX runtime to start", startupLatch.await(15, TimeUnit.SECONDS));
}
@Test
public void testRootNodeMemoryLeak() throws Exception {
JMemoryBuddy.memoryTest((checker) -> {
CountDownLatch showingLatch = new CountDownLatch(1);
Button toBeRemoved = new Button();
Group root = new Group();
AtomicReference<Stage> stage = new AtomicReference<>();
Util.runAndWait(() -> {
stage.set(new Stage());
stage.get().setOnShown(l -> {
Platform.runLater(() -> showingLatch.countDown());
});
stage.get().setScene(new Scene(root));
stage.get().show();
});
try {
assertTrue("Timeout waiting test stage", showingLatch.await(15, TimeUnit.SECONDS));
} catch (InterruptedException e) {
throw new RuntimeException(e);
}
Util.runAndWait(() -> {
root.getChildren().clear();
stage.get().hide();
});
checker.assertCollectable(stage.get());
checker.setAsReferenced(toBeRemoved);
});
}
@AfterClass
public static void teardownOnce() {
Platform.exit();
}
}
