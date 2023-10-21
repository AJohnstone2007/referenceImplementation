package test.com.sun.marlin;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import junit.framework.AssertionFailedError;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;
import test.util.Util;
import static test.util.Util.TIMEOUT;
public class ScaleClipTest {
static final int SIZE = 50;
enum SCALE_MODE {
ORTHO,
NON_ORTHO,
COMPLEX
};
private static final CountDownLatch launchLatch = new CountDownLatch(1);
static MyApp myApp;
public static class MyApp extends Application {
Stage stage = null;
public MyApp() {
super();
}
@Override
public void init() {
ScaleClipTest.myApp = this;
}
@Override
public void start(Stage primaryStage) throws Exception {
this.stage = primaryStage;
stage.setScene(new Scene(new Group()));
stage.setTitle("ScaleClipTest");
stage.show();
launchLatch.countDown();
}
}
@BeforeClass
public static void setupOnce() {
new Thread(() -> Application.launch(MyApp.class, (String[]) null)).start();
try {
if (!launchLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Application to launch");
}
} catch (InterruptedException ex) {
AssertionFailedError err = new AssertionFailedError("Unexpected exception");
err.initCause(ex);
throw err;
}
assertEquals(0, launchLatch.getCount());
System.out.println("ScaleClipTest: size = " + SIZE);
}
@AfterClass
public static void teardownOnce() {
Platform.exit();
}
@Test(timeout = 10000)
public void TestNegativeScaleClipPath() throws InterruptedException {
final AtomicBoolean fail = new AtomicBoolean();
for (SCALE_MODE mode : SCALE_MODE.values()) {
Util.runAndWait(() -> {
try {
testNegativeScale(mode);
} catch (AssertionError ae) {
System.err.println("testNegativeScale[" + mode + "] failed:");
ae.printStackTrace();
fail.set(true);
}
});
}
if (fail.get()) {
fail("TestNegativeScaleClipPath has failures.");
}
}
@Test(timeout = 10000)
public void TestMarginScaleClipPath() throws InterruptedException {
final AtomicBoolean fail = new AtomicBoolean();
for (SCALE_MODE mode : SCALE_MODE.values()) {
Util.runAndWait(() -> {
try {
testMarginScale(mode);
} catch (AssertionError ae) {
System.err.println("testMarginScale[" + mode + "] failed:");
ae.printStackTrace();
fail.set(true);
}
});
}
if (fail.get()) {
fail("TestMarginScaleClipPath has failures.");
}
}
private void testNegativeScale(final SCALE_MODE mode) {
final double scale = -1.0;
final Transform t;
switch (mode) {
default:
case ORTHO:
t = Transform.scale(scale, scale);
break;
case NON_ORTHO:
t = Transform.scale(scale, scale + 1e-5);
break;
case COMPLEX:
t = Transform.affine(scale, 1e-4, 1e-4, scale, 0, 0);
break;
}
final Path p = new Path();
p.getElements().addAll(
new MoveTo(scale * 10, scale * 10),
new LineTo(scale * (SIZE - 10), scale * (SIZE - 10))
);
p.setFill(null);
p.setStroke(javafx.scene.paint.Color.BLACK);
p.setStrokeWidth(2);
p.setStrokeLineCap(StrokeLineCap.BUTT);
p.setStrokeLineJoin(StrokeLineJoin.BEVEL);
Scene scene = new Scene(new Group(p));
myApp.stage.setScene(scene);
final SnapshotParameters sp = new SnapshotParameters();
sp.setViewport(new Rectangle2D(0, 0, SIZE, SIZE));
sp.setTransform(t);
final WritableImage img = scene.getRoot().snapshot(sp, new WritableImage(SIZE, SIZE));
checkPixel(img.getPixelReader(), 25, 25, Color.BLACK.getRGB());
}
private static void testMarginScale(final SCALE_MODE mode) {
final double scale = 1e-2;
final Transform t;
switch (mode) {
default:
case ORTHO:
t = Transform.scale(scale, scale);
break;
case NON_ORTHO:
t = Transform.scale(scale, scale + 1e-5);
break;
case COMPLEX:
t = Transform.affine(scale, 1e-4, 1e-4, scale, 0, 0);
break;
}
final double invScale = 1.0 / scale;
final Path p = new Path();
p.getElements().addAll(
new MoveTo(invScale * -0.5, invScale * 10),
new LineTo(invScale * -0.5, invScale * (SIZE - 10))
);
p.setFill(null);
p.setStroke(javafx.scene.paint.Color.BLACK);
p.setStrokeWidth(3.0 * invScale);
p.setStrokeLineCap(StrokeLineCap.BUTT);
p.setStrokeLineJoin(StrokeLineJoin.BEVEL);
Scene scene = new Scene(new Group(p));
myApp.stage.setScene(scene);
final SnapshotParameters sp = new SnapshotParameters();
sp.setViewport(new Rectangle2D(0, 0, SIZE, SIZE));
sp.setTransform(t);
final WritableImage img = scene.getRoot().snapshot(sp, new WritableImage(SIZE, SIZE));
checkPixel(img.getPixelReader(), 0, 25, Color.BLACK.getRGB());
}
private static void checkPixel(final PixelReader pr,
final int x, final int y,
final int expected) {
final int rgb = pr.getArgb(x, y);
if (rgb != expected) {
fail("bad pixel at (" + x + ", " + y
+ ") = " + rgb + " expected: " + expected);
}
}
}
