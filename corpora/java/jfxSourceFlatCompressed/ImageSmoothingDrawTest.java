package test.robot.javafx.scene.canvas;
import java.io.FileInputStream;
import java.net.URL;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.fail;
import test.util.Util;
public class ImageSmoothingDrawTest {
static CountDownLatch startupLatch = new CountDownLatch(1);
static Robot robot;
static ImageCanvas imageCanvas;
static volatile Stage stage;
static volatile Scene scene;
Color lastWhitePixelColor;
Color whitePixelColor;
static final int scaleFactor = 20;
public void getPixelColors() {
int lastWhiteX = (int) (stage.getX() + scene.getX() +
imageCanvas.getLayoutX() + (imageCanvas.getWidth() / 2) - 2);
int heightCenter = (int) (stage.getY() + scene.getY() +
imageCanvas.getLayoutY() + (imageCanvas.getHeight() / 2));
Util.runAndWait(() -> {
lastWhitePixelColor = robot.getPixelColor(lastWhiteX, heightCenter);
whitePixelColor = robot.getPixelColor(
lastWhiteX - (imageCanvas.getWidth() / 4), heightCenter);
});
}
@Test
public void testImageSmoothingEnabled() {
imageCanvas.setImageSmoothing(true);
Util.sleep(1000);
getPixelColors();
Assert.assertEquals(Color.WHITE, whitePixelColor);
Assert.assertFalse(whitePixelColor.equals(lastWhitePixelColor));
}
@Test
public void testImageSmoothingDisabled() {
imageCanvas.setImageSmoothing(false);
Util.sleep(1000);
getPixelColors();
Assert.assertEquals(Color.WHITE, whitePixelColor);
Assert.assertEquals(whitePixelColor, lastWhitePixelColor);
}
@BeforeClass
public static void initFX() {
new Thread(() -> Application.launch(TestApp.class, (String[])null)).start();
waitForLatch(startupLatch, 10, "Timeout waiting for FX runtime to start");
}
@AfterClass
public static void exit() {
Platform.runLater(() -> {
stage.hide();
});
Platform.exit();
}
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
robot = new Robot();
stage = primaryStage;
URL resource = this.getClass().getResource("image_smoothing_draw_test.png");
FileInputStream inFile = new FileInputStream(resource.getFile());
Image whiteBlack = new Image(inFile);
inFile.close();
imageCanvas = new ImageCanvas(whiteBlack,
whiteBlack.getWidth() * scaleFactor,
whiteBlack.getHeight() * scaleFactor);
VBox root = new VBox();
root.getChildren().add(imageCanvas);
scene = new Scene(root);
stage.setScene(scene);
stage.initStyle(StageStyle.UNDECORATED);
stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e ->
Platform.runLater(startupLatch::countDown));
stage.setAlwaysOnTop(true);
stage.show();
}
}
public static void waitForLatch(CountDownLatch latch, int seconds, String msg) {
try {
if (!latch.await(seconds, TimeUnit.SECONDS)) {
fail(msg);
}
} catch (Exception ex) {
fail("Unexpected exception: " + ex);
}
}
static class ImageCanvas extends Canvas {
private Image image = null;
public ImageCanvas(Image img, double width, double height) {
super(width, height);
image = img;
}
public void render() {
GraphicsContext gc = getGraphicsContext2D();
if (image != null) {
gc.drawImage(image,
0, 0, image.getWidth(), image.getHeight(),
0, 0, getWidth(), getHeight());
}
}
public void setImageSmoothing(boolean smooth) {
GraphicsContext gc = getGraphicsContext2D();
gc.setImageSmoothing(smooth);
render();
}
}
}
