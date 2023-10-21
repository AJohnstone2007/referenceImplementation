package test.javafx.scene.control;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import test.util.Util;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
public class AccordionTitlePaneLeakTest {
static private CountDownLatch startupLatch;
static private Accordion accordion;
static private StackPane root;
static private Stage stage;
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
stage = primaryStage;
accordion = new Accordion();
root = new StackPane(accordion);
stage.setScene(new Scene(root));
stage.setOnShown(l -> {
Platform.runLater(() -> startupLatch.countDown());
});
stage.show();
}
}
@BeforeClass
public static void initFX() throws Exception {
startupLatch = new CountDownLatch(1);
new Thread(() -> Application.launch(TestApp.class, (String[])null)).start();
Assert.assertTrue("Timeout waiting for FX runtime to start", startupLatch.await(15, TimeUnit.SECONDS));
}
@AfterClass
public static void teardownOnce() {
Platform.runLater(() -> {
stage.hide();
Platform.exit();
});
}
@Test
public void testForTitledPaneLeak() throws Exception {
TitledPane pane = new TitledPane();
accordion.getPanes().add(pane);
WeakReference<TitledPane> weakRefToPane = new WeakReference<>(pane);
pane = null;
accordion.getPanes().clear();
for (int i = 0; i < 10; i++) {
System.gc();
if (weakRefToPane.get() == null) {
break;
}
Util.sleep(500);
}
Assert.assertNull("Couldn't collect TitledPane", weakRefToPane.get());
}
}
