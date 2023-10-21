package test.robot.testharness;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import junit.framework.AssertionFailedError;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import test.util.Util;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static test.util.Util.TIMEOUT;
public abstract class VisualTestBase {
private static final CountDownLatch launchLatch = new CountDownLatch(1);
private static MyApp myApp;
private List<Stage> stages = new ArrayList<>();
Robot robot;
protected Robot getRobot() {
return robot;
}
public static class MyApp extends Application {
@Override
public void init() {
VisualTestBase.myApp = this;
}
@Override
public void start(Stage primaryStage) throws Exception {
Platform.setImplicitExit(false);
assertTrue(Platform.isFxApplicationThread());
assertNotNull(primaryStage);
launchLatch.countDown();
}
}
@BeforeClass
public static void doSetupOnce() {
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
}
@AfterClass
public static void doTeardownOnce() {
Platform.exit();
}
@Before
public void doSetup() {
runAndWait(() -> robot = new Robot());
}
@After
public void doTeardown() {
runAndWait(() -> {
if (!stages.isEmpty()) {
for (final Stage stage : stages) {
if (stage.isShowing()) {
stage.hide();
}
}
stages.clear();
}
});
}
protected void runAndWait(final Runnable r) {
Util.runAndWait(r);
}
protected Stage getStage() {
return getStage(true);
}
protected Stage getStage(boolean alwaysOnTop) {
Stage stage = new Stage();
stage.initStyle(StageStyle.UNDECORATED);
if (alwaysOnTop) {
stage.setAlwaysOnTop(true);
}
stages.add(stage);
return stage;
}
protected void sleep(long millis) {
try {
Thread.sleep(millis);
} catch (InterruptedException ex) {
throw new AssertionFailedError("Unexpected exception: " + ex);
}
}
protected Color getColor(Scene scene, int x, int y) {
x += scene.getX() + scene.getWindow().getX();
y += scene.getY() + scene.getWindow().getY();
return getColor(x, y);
}
protected Color getColor(int x, int y) {
return robot.getPixelColor(x, y);
}
private static String colorToString(Color c) {
int r = (int)(c.getRed() * 255.0);
int g = (int)(c.getGreen() * 255.0);
int b = (int)(c.getBlue() * 255.0);
int a = (int)(c.getOpacity() * 255.0);
return "rgba(" + r + "," + g + "," + b + "," + a + ")";
}
protected void assertColorEquals(Color expected, Color actual, double delta) {
if (!testColorEquals(expected, actual, delta)) {
throw new AssertionFailedError("expected:" + colorToString(expected)
+ " but was:" + colorToString(actual));
}
}
protected boolean testColorEquals(Color expected, Color actual, double delta) {
double deltaRed = Math.abs(expected.getRed() - actual.getRed());
double deltaGreen = Math.abs(expected.getGreen() - actual.getGreen());
double deltaBlue = Math.abs(expected.getBlue() - actual.getBlue());
double deltaOpacity = Math.abs(expected.getOpacity() - actual.getOpacity());
return (deltaRed <= delta && deltaGreen <= delta && deltaBlue <= delta && deltaOpacity <= delta);
}
protected void assertColorDoesNotEqual(Color notExpected, Color actual, double delta) {
double deltaRed = Math.abs(notExpected.getRed() - actual.getRed());
double deltaGreen = Math.abs(notExpected.getGreen() - actual.getGreen());
double deltaBlue = Math.abs(notExpected.getBlue() - actual.getBlue());
double deltaOpacity = Math.abs(notExpected.getOpacity() - actual.getOpacity());
if (deltaRed < delta && deltaGreen < delta && deltaBlue < delta && deltaOpacity < delta) {
throw new AssertionFailedError("not expected:" + colorToString(notExpected)
+ " but was:" + colorToString(actual));
}
}
private AnimationTimer timer;
private void frameWait(int n) {
final CountDownLatch frameCounter = new CountDownLatch(n);
Platform.runLater(() -> {
timer = new AnimationTimer() {
@Override public void handle(long l) {
frameCounter.countDown();
}
};
timer.start();
});
try {
frameCounter.await();
} catch (InterruptedException ex) {
throw new AssertionFailedError("Unexpected exception: " + ex);
} finally {
runAndWait(() -> {
if (timer != null) {
timer.stop();
}
});
}
}
protected void waitFirstFrame() {
frameWait(100);
}
protected void waitNextFrame() {
frameWait(5);
}
}
