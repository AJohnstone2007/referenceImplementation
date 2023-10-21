package test.com.sun.javafx.iio;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import test.util.Util;
public class LoadCorruptJPEGTest {
static CountDownLatch startupLatch;
static HBox root;
static volatile Scene scene;
static volatile Stage stage;
static final int SCENE_WIDTH = 200;
static final int SCENE_HEIGHT = 200;
@Test
public void testCorruptJPEGImage() {
Util.runAndWait(() -> {
URL resource = LoadCorruptJPEGTest.class.getResource("corrupt.jpg");
FileInputStream input = null;
try {
input = new FileInputStream(resource.getFile());
} catch (FileNotFoundException e) {
fail("File not found: corrupt.jpg");
}
Image image = new Image(input);
ImageView iv = new ImageView(image);
root.getChildren().add(iv);
});
}
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) {
stage = primaryStage;
root = new HBox();
scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
stage.setScene(scene);
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
assertTrue("Timeout waiting for FX runtime to start",
startupLatch.await(15, TimeUnit.SECONDS));
}
@AfterClass
public static void exit() {
Util.runAndWait(() -> {
stage.hide();
});
Platform.exit();
}
@After
public void resetTest() {
Util.runAndWait(() -> {
root.getChildren().clear();
});
}
}
