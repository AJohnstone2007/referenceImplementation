package test.javafx.scene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import test.util.Util;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import static org.junit.Assert.assertTrue;
public class QuadraticCssTimeTest {
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
new Thread(() -> Application.launch(QuadraticCssTimeTest.TestApp.class, (String[]) null)).start();
assertTrue("Timeout waiting for FX runtime to start", startupLatch.await(15, TimeUnit.SECONDS));
}
@Test
public void testTimeForAdding500NodesToScene() throws Exception {
Util.runAndWait(() -> {
long startTime = System.currentTimeMillis();
HBox hbox = new HBox();
for (int i = 0; i < 500; i++) {
hbox = new HBox(new Text("y"), hbox);
final HBox h = hbox;
h.setPadding(new Insets(1));
}
rootPane.setCenter(hbox);
long endTime = System.currentTimeMillis();
System.out.println("Time to create and add 500 nodes to a Scene = " +
(endTime - startTime) + " mSec");
assertTrue("Time to add 500 Nodes is more than 800 mSec", (endTime - startTime) < 800);
});
}
@AfterClass
public static void teardownOnce() {
Platform.runLater(() -> {
stage.hide();
Platform.exit();
});
}
}
