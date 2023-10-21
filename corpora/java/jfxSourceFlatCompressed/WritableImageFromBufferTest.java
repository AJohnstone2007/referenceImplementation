package test.javafx.scene.image;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.IntBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import test.util.Util;
public class WritableImageFromBufferTest {
static CountDownLatch startupLatch;
private static final int IMG_WIDTH = 600;
private static final int IMG_HEIGHT = 400;
private PixelBuffer<IntBuffer> pixelBuffer;
private Graphics2D g2d;
private WritableImage fxImage;
private static Scene scene;
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
scene = new Scene(new StackPane(), IMG_WIDTH, IMG_HEIGHT);
primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> startupLatch.countDown());
primaryStage.setScene(scene);
primaryStage.show();
}
}
@Before
public void setUp() throws Exception {
BufferedImage awtImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_ARGB_PRE);
g2d = (Graphics2D) awtImage.getGraphics();
DataBuffer db = awtImage.getRaster().getDataBuffer();
DataBufferInt dbi = (DataBufferInt) db;
int[] rawInts = dbi.getData();
IntBuffer ib = IntBuffer.wrap(rawInts);
PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbPreInstance();
pixelBuffer = new PixelBuffer<>(IMG_WIDTH, IMG_HEIGHT, ib, pixelFormat);
fxImage = new WritableImage(pixelBuffer);
}
@Test
public void test() throws InterruptedException {
PrintStream defaultErrorStream = System.err;
ByteArrayOutputStream out = new ByteArrayOutputStream();
System.setErr(new PrintStream(out, true));
Thread.sleep(1000);
Util.runAndWait(() -> {
StackPane root = (StackPane)scene.getRoot();
root.getChildren().add(new ImageView(fxImage));
requestFullUpdate();
});
Thread.sleep(100);
Util.runAndWait(() -> {
requestEmptyUpdate();
});
Thread.sleep(100);
Util.runAndWait(() -> {
requestPartialUpdate();
});
Thread.sleep(100);
System.setErr(defaultErrorStream);
Assert.assertEquals("No error should be thrown", "", out.toString());
}
private void requestFullUpdate() {
pixelBuffer.updateBuffer(pb -> {
g2d.setBackground(Color.decode("#FF0000"));
g2d.clearRect(0, 0, IMG_WIDTH, IMG_HEIGHT);
return null;
});
}
private void requestEmptyUpdate() {
pixelBuffer.updateBuffer(pb -> {
return Rectangle2D.EMPTY;
});
}
private void requestPartialUpdate() {
pixelBuffer.updateBuffer(pb -> {
g2d.setBackground(Color.decode("#0000FF"));
g2d.clearRect(0, 0, IMG_WIDTH / 2, IMG_HEIGHT);
return new Rectangle2D(0, 0, IMG_WIDTH / 2, IMG_HEIGHT);
});
}
@BeforeClass
public static void initFX() throws Exception {
startupLatch = new CountDownLatch(1);
new Thread(() -> Application.launch(TestApp.class, (String[])null)).start();
Assert.assertTrue("Timeout waiting for FX runtime to start",
startupLatch.await(15, TimeUnit.SECONDS));
}
@AfterClass
public static void tearDown() {
Platform.exit();
}
}
