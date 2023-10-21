package test.javafx.scene;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.fail;
public class NewSceneSizeTest {
static CountDownLatch startupLatch;
static volatile Stage stage;
private static double scaleX, scaleY;
public static void main(String[] args) throws Exception {
initFX();
try {
NewSceneSizeTest test = new NewSceneSizeTest();
test.testNewSceneSize();
} catch (Throwable e) {
e.printStackTrace();
} finally {
teardown();
}
}
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(new VBox()));
stage = primaryStage;
stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e -> {
scaleX = stage.getOutputScaleX();
scaleY = stage.getOutputScaleY();
Platform.runLater(startupLatch::countDown);
});
stage.show();
}
}
@BeforeClass
public static void initFX() {
startupLatch = new CountDownLatch(1);
new Thread(() -> Application.launch(TestApp.class, (String[])null)).start();
try {
if (!startupLatch.await(15, TimeUnit.SECONDS)) {
fail("Timeout waiting for FX runtime to start");
}
} catch (InterruptedException ex) {
fail("Unexpected exception: " + ex);
}
}
@Test
public void testNewSceneSize() throws Exception {
Thread.sleep(200);
final int nTries = 100;
Stage childStage[] = new Stage[nTries];
double w[] = new double[nTries];
double h[] = new double[nTries];
CountDownLatch latch = new CountDownLatch(2 * nTries);
for (int i = 0; i < nTries; i++) {
int fI = i;
Platform.runLater(new Runnable() {
ChangeListener<Number> listenerW;
ChangeListener<Number> listenerH;
@Override
public void run() {
Stage stage = new Stage();
childStage[fI] = stage;
stage.setResizable(fI % 2 == 0);
Scene scene = new Scene(new VBox(), 300 - fI, 200 - fI);
stage.setScene(scene);
w[fI] = (Math.ceil((300 - fI) * scaleX)) / scaleX;
h[fI] = (Math.ceil((200 - fI) * scaleY)) / scaleY;
Assert.assertTrue(w[fI] > 1);
Assert.assertTrue(h[fI] > 1);
stage.widthProperty().addListener(listenerW = (v, o, n) -> {
if (Math.abs((Double) n - w[fI]) < 0.1) {
stage.widthProperty().removeListener(listenerW);
Platform.runLater(latch::countDown);
}
});
stage.heightProperty().addListener(listenerH = (v, o, n) -> {
if (Math.abs((Double) n - h[fI]) < 0.1) {
stage.heightProperty().removeListener(listenerH);
Platform.runLater(latch::countDown);
}
});
stage.show();
}
});
}
latch.await(5, TimeUnit.SECONDS);
Thread.sleep(200);
for (int i = 0; i < nTries; i++) {
Assert.assertEquals("Wrong scene " + i + " width", w[i],
childStage[i].getScene().getWidth(), 0.1);
Assert.assertEquals("Wrong scene " + i + " height", h[i],
childStage[i].getScene().getHeight(), 0.1);
}
}
@AfterClass
public static void teardown() {
Platform.runLater(stage::hide);
Platform.exit();
}
}
