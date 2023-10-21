package test.robot.javafx.scene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.robot.Robot;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.fail;
import test.util.Util;
public class PixelBufferDrawTest {
private static HBox root;
private static Stage stage;
private static Scene scene;
private static Robot robot;
private static CountDownLatch startupLatch;
private static final int DELAY = 500;
private static final int NUM_IMAGES = 4;
private static final int IMAGE_WIDTH = 24;
private static final int IMAGE_HEIGHT = IMAGE_WIDTH;
private static final int SCENE_WIDTH = IMAGE_WIDTH * NUM_IMAGES + NUM_IMAGES - 1;
private static final int SCENE_HEIGHT = IMAGE_HEIGHT;
private static final Color TEST_COLOR = new Color(0.2, 0.6, 0.8, 1);
private static final Color INIT_COLOR = new Color(0.92, 0.56, 0.1, 1);
private volatile Color actualColor = Color.BLACK;
private PixelBuffer<ByteBuffer> bytePixelBuffer;
private PixelBuffer<IntBuffer> intPixelBuffer;
private IntBuffer sourceIntBuffer;
private ByteBuffer sourceByteBuffer;
private Callback<PixelBuffer<ByteBuffer>, Rectangle2D> byteBufferCallback = pixelBuffer -> {
ByteBuffer dst = pixelBuffer.getBuffer();
dst.put(sourceByteBuffer);
sourceByteBuffer.rewind();
dst.rewind();
return new Rectangle2D(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT / 2);
};
private static ByteBuffer createByteBuffer(int w, int h, boolean isDirect, Color c) {
byte red = (byte) Math.round(c.getRed() * 255.0);
byte green = (byte) Math.round(c.getGreen() * 255.0);
byte blue = (byte) Math.round(c.getBlue() * 255.0);
byte alpha = (byte) Math.round(c.getOpacity() * 255.0);
ByteBuffer byteBuffer;
if (isDirect) {
byteBuffer = ByteBuffer.allocateDirect(w * h * 4);
} else {
byteBuffer = ByteBuffer.allocate(w * h * 4);
}
for (int y = 0; y < h; y++) {
for (int x = 0; x < w; x++) {
byteBuffer.put(blue);
byteBuffer.put(green);
byteBuffer.put(red);
byteBuffer.put(alpha);
}
}
byteBuffer.rewind();
return byteBuffer;
}
private void createBytePixelBuffer(boolean isDirect) {
ByteBuffer sharedBuffer = createByteBuffer(IMAGE_WIDTH, IMAGE_HEIGHT, isDirect, INIT_COLOR);
sourceByteBuffer = createByteBuffer(IMAGE_WIDTH, IMAGE_HEIGHT / 2, isDirect, TEST_COLOR);
PixelFormat<ByteBuffer> pixelFormat = PixelFormat.getByteBgraPreInstance();
bytePixelBuffer = new PixelBuffer<>(IMAGE_WIDTH, IMAGE_HEIGHT, sharedBuffer, pixelFormat);
}
private Callback<PixelBuffer<IntBuffer>, Rectangle2D> intBufferCallback = pixelBuffer -> {
IntBuffer dst = pixelBuffer.getBuffer();
dst.put(sourceIntBuffer);
sourceIntBuffer.rewind();
dst.rewind();
return new Rectangle2D(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT / 2);
};
private static IntBuffer createIntBuffer(int w, int h, boolean isDirect, Color c) {
int red = (int) Math.round(c.getRed() * 255.0);
int green = (int) Math.round(c.getGreen() * 255.0);
int blue = (int) Math.round(c.getBlue() * 255.0);
int alpha = (int) Math.round(c.getOpacity() * 255.0);
int color = alpha << 24 | red << 16 | green << 8 | blue;
IntBuffer intBuffer;
if (isDirect) {
ByteBuffer bf = ByteBuffer.allocateDirect(w * h * 4).order(ByteOrder.nativeOrder());
intBuffer = bf.asIntBuffer();
} else {
intBuffer = IntBuffer.allocate(w * h);
}
for (int y = 0; y < h; y++) {
for (int x = 0; x < w; x++) {
intBuffer.put(color);
}
}
intBuffer.rewind();
return intBuffer;
}
private void createIntPixelBuffer(boolean isDirect) {
IntBuffer sharedBuffer = createIntBuffer(IMAGE_WIDTH, IMAGE_HEIGHT, isDirect, INIT_COLOR);
sourceIntBuffer = createIntBuffer(IMAGE_WIDTH, IMAGE_HEIGHT / 2, isDirect, TEST_COLOR);
PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbPreInstance();
intPixelBuffer = new PixelBuffer<>(IMAGE_WIDTH, IMAGE_HEIGHT, sharedBuffer, pixelFormat);
}
private ImageView createImageViewPB(PixelBuffer<? extends Buffer> pixelBuffer) {
return new ImageView(new WritableImage(pixelBuffer));
}
private void compareColor(Color exp, Color act) {
Assert.assertEquals(exp.getRed(), act.getRed(), 0.005);
Assert.assertEquals(exp.getBlue(), act.getBlue(), 0.005);
Assert.assertEquals(exp.getGreen(), act.getGreen(), 0.005);
Assert.assertEquals(exp.getOpacity(), act.getOpacity(), 0.005);
}
private void verifyColor(Color color1, Color color2) {
for (int i = 0; i < root.getChildren().size(); i++) {
final int x = (int) (scene.getWindow().getX() + scene.getX() +
root.getChildren().get(i).getLayoutX() + IMAGE_WIDTH / 2);
final int y = (int) (scene.getWindow().getY() + scene.getY() +
root.getChildren().get(i).getLayoutY() + IMAGE_HEIGHT * 0.45);
Util.runAndWait(() -> actualColor = robot.getPixelColor(x, y));
compareColor(color1, actualColor);
final int x1 = (int) (scene.getWindow().getX() + scene.getX() +
root.getChildren().get(i).getLayoutX() + IMAGE_WIDTH / 2);
final int y1 = (int) (scene.getWindow().getY() + scene.getY() +
root.getChildren().get(i).getLayoutY() + IMAGE_HEIGHT * 0.55);
Util.runAndWait(() -> actualColor = robot.getPixelColor(x1, y1));
compareColor(color2, actualColor);
}
}
private <T extends Buffer> void performTest(PixelBuffer<T> pixelBuffer, Callback<PixelBuffer<T>, Rectangle2D> callback) {
Util.runAndWait(() -> {
for (int i = 0; i < NUM_IMAGES; i++) {
root.getChildren().add(createImageViewPB(pixelBuffer));
}
});
delay();
verifyColor(INIT_COLOR, INIT_COLOR);
Util.runAndWait(() -> pixelBuffer.updateBuffer(callback));
delay();
verifyColor(TEST_COLOR, INIT_COLOR);
}
@Test
public void testIntArgbPreDirectBuffer() {
createIntPixelBuffer(true);
performTest(intPixelBuffer, intBufferCallback);
}
@Test
public void testIntArgbPreIndirectBuffer() {
createIntPixelBuffer(false);
performTest(intPixelBuffer, intBufferCallback);
}
@Test
public void testByteBgraPreDirectBuffer() {
createBytePixelBuffer(true);
performTest(bytePixelBuffer, byteBufferCallback);
}
@Test
public void testByteBgraPreIndirectBuffer() {
createBytePixelBuffer(false);
performTest(bytePixelBuffer, byteBufferCallback);
}
@Test
public void testUpdateBufferOnNonFxAppThread() {
createBytePixelBuffer(true);
try {
bytePixelBuffer.updateBuffer(byteBufferCallback);
fail("Expected IllegalStateException");
} catch (IllegalStateException e) {
}
}
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) {
stage = primaryStage;
robot = new Robot();
root = new HBox(1);
scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
stage.setScene(scene);
stage.initStyle(StageStyle.UNDECORATED);
stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e ->
Platform.runLater(startupLatch::countDown));
stage.setAlwaysOnTop(true);
stage.show();
}
}
@BeforeClass
public static void initFX() throws Exception {
startupLatch = new CountDownLatch(1);
new Thread(() -> Application.launch(TestApp.class, (String[]) null)).start();
waitForLatch(startupLatch, 10, "Timeout waiting for FX runtime to start");
}
@After
public void cleanupTest() {
Util.runAndWait(() -> {
root = new HBox(1);
scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
stage.setScene(scene);
});
}
@AfterClass
public static void exit() {
Platform.runLater(() -> stage.hide());
Platform.exit();
}
private static void delay() {
try {
Thread.sleep(DELAY);
} catch (Exception ex) {
fail("Thread was interrupted." + ex);
}
}
public static void waitForLatch(CountDownLatch latch, int seconds, String msg) throws Exception {
Assert.assertTrue(msg, latch.await(seconds, TimeUnit.SECONDS));
}
}
