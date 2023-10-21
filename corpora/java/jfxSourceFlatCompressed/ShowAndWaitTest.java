package test.javafx.stage;
import javafx.stage.StageShim;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageShim;
import javafx.stage.Window;
import javafx.util.Duration;
import junit.framework.AssertionFailedError;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.util.Util;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static test.util.Util.TIMEOUT;
@RunWith(Parameterized.class)
public class ShowAndWaitTest {
private static final int MAX_STAGES = 10;
private static final CountDownLatch launchLatch = new CountDownLatch(1);
private static MyApp myApp;
public static class MyApp extends Application {
private Stage primaryStage;
@Override public void init() {
ShowAndWaitTest.myApp = this;
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setTitle("Primary stage");
Group root = new Group();
Scene scene = new Scene(root);
scene.setFill(Color.LIGHTYELLOW);
primaryStage.setScene(scene);
primaryStage.setX(0);
primaryStage.setY(0);
primaryStage.setWidth(210);
primaryStage.setHeight(180);
this.primaryStage = primaryStage;
launchLatch.countDown();
}
}
private static class TestStage extends Stage {
private TestStage(Modality modality) {
this(modality, modality == Modality.WINDOW_MODAL ? myApp.primaryStage : null);
}
private TestStage(Modality modality, Window owner) {
this.setTitle("Test stage");
this.initModality(modality);
this.initOwner(owner);
Group root = new Group();
Scene scene = new Scene(root);
this.setScene(scene);
this.setWidth(200);
this.setHeight(150);
this.setX(225);
this.setY(0);
}
}
@BeforeClass
public static void setupOnce() {
new Thread(() -> Application.launch(MyApp.class, (String[])null)).start();
try {
if (!launchLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Application to launch");
}
} catch (InterruptedException ex) {
AssertionFailedError err = new AssertionFailedError("Unexpected exception");
err.initCause(ex);
throw err;
}
}
@AfterClass
public static void teardownOnce() {
Platform.exit();
}
final Modality modality;
private HashSet<Stage> stages = new HashSet<Stage>();
private Stage tmpStage1 = null;
private Stage tmpStage2 = null;
@Parameters
public static Collection getParams() {
return Arrays.asList(new Object[][] {
{ Modality.NONE },
{ Modality.WINDOW_MODAL },
{ Modality.APPLICATION_MODAL },
});
}
public ShowAndWaitTest(Modality modality) {
this.modality = modality;
}
@Before
public void setupEach() {
assertNotNull(myApp);
assertNotNull(myApp.primaryStage);
}
@After
public void teardownEach() {
for (final Stage stage : stages) {
if (stage.isShowing()) {
System.err.println("Cleaning up stage after a failed test...");
try {
Util.runAndWait(stage::hide);
} catch (Throwable t) {
System.err.println("WARNING: unable to hide stage after test failure");
t.printStackTrace(System.err);
}
}
}
}
private static boolean test1Run = false;
public void ensureTest1() {
if (!test1Run) {
test1();
}
}
@Test
public void test1() {
if (test1Run) {
return;
}
test1Run = true;
assertEquals(0, launchLatch.getCount());
Util.runAndWait(() -> {
assertTrue(Platform.isFxApplicationThread());
assertTrue(StageShim.isPrimary(myApp.primaryStage));
assertFalse(myApp.primaryStage.isShowing());
try {
myApp.primaryStage.showAndWait();
throw new AssertionFailedError("Expected IllegalStateException was not thrown");
} catch (IllegalStateException ex) {
}
myApp.primaryStage.show();
});
}
@Test (expected=IllegalStateException.class)
public void testConstructWrongThread() {
ensureTest1();
assertFalse(Platform.isFxApplicationThread());
tmpStage1 = new TestStage(modality);
stages.add(tmpStage1);
}
@Test (expected=IllegalStateException.class)
public void testShowWaitWrongThread() {
ensureTest1();
assertFalse(Platform.isFxApplicationThread());
Util.runAndWait(() -> {
tmpStage1 = new TestStage(modality);
stages.add(tmpStage1);
assertFalse(StageShim.isPrimary(tmpStage1));
assertFalse(tmpStage1.isShowing());
});
assertNotNull(tmpStage1);
tmpStage1.showAndWait();
}
@Test (expected=IllegalStateException.class)
public void testVisibleThrow() {
ensureTest1();
Util.runAndWait(() -> {
tmpStage1 = new TestStage(modality);
stages.add(tmpStage1);
assertFalse(StageShim.isPrimary(tmpStage1));
assertFalse(tmpStage1.isShowing());
tmpStage1.show();
assertTrue(tmpStage1.isShowing());
try {
tmpStage1.showAndWait();
} finally {
tmpStage1.hide();
}
});
}
@Test
public void testNotBlocking() {
ensureTest1();
final AtomicBoolean stageShowReturned = new AtomicBoolean(false);
final AtomicBoolean hideActionReached = new AtomicBoolean(false);
Runnable rShow = () -> {
tmpStage1 = new TestStage(modality);
stages.add(tmpStage1);
assertFalse(StageShim.isPrimary(tmpStage1));
assertFalse(tmpStage1.isShowing());
tmpStage1.show();
stageShowReturned.set(true);
assertTrue(tmpStage1.isShowing());
assertFalse(hideActionReached.get());
};
Runnable rHide = () -> {
assertNotNull(tmpStage1);
assertTrue(tmpStage1.isShowing());
assertTrue(stageShowReturned.get());
hideActionReached.set(true);
tmpStage1.hide();
};
Util.runAndWait(rShow, rHide);
assertFalse(tmpStage1.isShowing());
}
@Test
public void testSingle() {
ensureTest1();
final AtomicBoolean stage1ShowReturned = new AtomicBoolean(false);
final AtomicBoolean hide1EventReached = new AtomicBoolean(false);
final AtomicBoolean nextRunnableReached = new AtomicBoolean(false);
Runnable rShow1 = () -> {
tmpStage1 = new TestStage(modality);
stages.add(tmpStage1);
assertFalse(StageShim.isPrimary(tmpStage1));
assertFalse(tmpStage1.isShowing());
tmpStage1.showAndWait();
stage1ShowReturned.set(true);
assertFalse(tmpStage1.isShowing());
assertTrue(hide1EventReached.get());
assertFalse(nextRunnableReached.get());
};
Runnable rHide1 = () -> {
hide1EventReached.set(true);
assertFalse(stage1ShowReturned.get());
assertNotNull(tmpStage1);
tmpStage1.hide();
Util.sleep(1);
assertFalse(stage1ShowReturned.get());
};
Runnable rNext = () -> {
nextRunnableReached.set(true);
};
Util.runAndWait(rShow1, rHide1, rNext);
assertFalse(tmpStage1.isShowing());
}
@Test
public void testSingle_Chained() {
ensureTest1();
final AtomicBoolean stage1ShowReturned = new AtomicBoolean(false);
final AtomicBoolean hide1EventReached = new AtomicBoolean(false);
final AtomicBoolean nextRunnableReached = new AtomicBoolean(false);
Runnable rShow1 = () -> {
tmpStage1 = new TestStage(modality);
stages.add(tmpStage1);
assertFalse(StageShim.isPrimary(tmpStage1));
assertFalse(tmpStage1.isShowing());
tmpStage1.showAndWait();
stage1ShowReturned.set(true);
assertFalse(tmpStage1.isShowing());
assertTrue(hide1EventReached.get());
assertFalse(nextRunnableReached.get());
};
Runnable rHide1 = () -> {
hide1EventReached.set(true);
assertFalse(stage1ShowReturned.get());
assertNotNull(tmpStage1);
tmpStage1.hide();
Util.sleep(1);
assertFalse(stage1ShowReturned.get());
Platform.runLater(() -> {
nextRunnableReached.set(true);
});
};
Util.runAndWait(rShow1, rHide1);
assertFalse(tmpStage1.isShowing());
}
@Test
public void testTwoNested() {
ensureTest1();
final AtomicBoolean stage1ShowReturned = new AtomicBoolean(false);
final AtomicBoolean hide1EventReached = new AtomicBoolean(false);
final AtomicBoolean stage2ShowReturned = new AtomicBoolean(false);
final AtomicBoolean hide2EventReached = new AtomicBoolean(false);
Runnable rShow1 = () -> {
tmpStage1 = new TestStage(modality);
stages.add(tmpStage1);
assertFalse(StageShim.isPrimary(tmpStage1));
assertFalse(tmpStage1.isShowing());
tmpStage1.showAndWait();
stage1ShowReturned.set(true);
assertFalse(tmpStage1.isShowing());
assertTrue(stage2ShowReturned.get());
assertTrue(hide1EventReached.get());
assertTrue(hide2EventReached.get());
};
Runnable rShow2 = () -> {
tmpStage2 = new TestStage(modality);
stages.add(tmpStage2);
assertFalse(StageShim.isPrimary(tmpStage2));
assertFalse(tmpStage2.isShowing());
tmpStage2.showAndWait();
stage2ShowReturned.set(true);
assertFalse(stage1ShowReturned.get());
assertFalse(tmpStage2.isShowing());
assertTrue(hide2EventReached.get());
assertFalse(hide1EventReached.get());
};
Runnable rHide1 = () -> {
hide1EventReached.set(true);
assertFalse(stage1ShowReturned.get());
assertTrue(stage2ShowReturned.get());
assertTrue(hide2EventReached.get());
assertNotNull(tmpStage1);
tmpStage1.hide();
Util.sleep(1);
assertFalse(stage1ShowReturned.get());
};
Runnable rHide2 = () -> {
hide2EventReached.set(true);
assertFalse(stage2ShowReturned.get());
assertFalse(stage1ShowReturned.get());
assertFalse(hide1EventReached.get());
assertNotNull(tmpStage2);
tmpStage2.hide();
Util.sleep(1);
assertFalse(stage2ShowReturned.get());
};
Util.runAndWait(rShow1, rShow2, rHide2, rHide1);
assertFalse(tmpStage1.isShowing());
assertFalse(tmpStage2.isShowing());
}
@Test
public void testTwoInterleaved() {
ensureTest1();
final AtomicBoolean stage1ShowReturned = new AtomicBoolean(false);
final AtomicBoolean hide1EventReached = new AtomicBoolean(false);
final AtomicBoolean stage2ShowReturned = new AtomicBoolean(false);
final AtomicBoolean hide2EventReached = new AtomicBoolean(false);
Runnable rShow1 = () -> {
tmpStage1 = new TestStage(modality);
stages.add(tmpStage1);
assertFalse(StageShim.isPrimary(tmpStage1));
assertFalse(tmpStage1.isShowing());
tmpStage1.showAndWait();
stage1ShowReturned.set(true);
assertFalse(tmpStage1.isShowing());
assertTrue(stage2ShowReturned.get());
assertTrue(hide1EventReached.get());
assertTrue(hide2EventReached.get());
};
Runnable rShow2 = () -> {
tmpStage2 = new TestStage(modality);
stages.add(tmpStage2);
assertFalse(StageShim.isPrimary(tmpStage2));
assertFalse(tmpStage2.isShowing());
tmpStage2.showAndWait();
stage2ShowReturned.set(true);
assertFalse(tmpStage2.isShowing());
assertFalse(stage1ShowReturned.get());
assertTrue(hide2EventReached.get());
assertTrue(hide1EventReached.get());
};
Runnable rHide1 = () -> {
hide1EventReached.set(true);
assertFalse(stage1ShowReturned.get());
assertFalse(stage2ShowReturned.get());
assertFalse(hide2EventReached.get());
assertNotNull(tmpStage1);
tmpStage1.hide();
Util.sleep(1);
assertFalse(stage1ShowReturned.get());
};
Runnable rHide2 = () -> {
hide2EventReached.set(true);
assertFalse(stage2ShowReturned.get());
assertFalse(stage1ShowReturned.get());
assertTrue(hide1EventReached.get());
assertNotNull(tmpStage2);
tmpStage2.hide();
Util.sleep(1);
assertFalse(stage2ShowReturned.get());
};
Util.runAndWait(rShow1, rShow2, rHide1, rHide2);
assertFalse(tmpStage1.isShowing());
assertFalse(tmpStage2.isShowing());
}
@Test
public void testMultipleNested() {
ensureTest1();
final int N = MAX_STAGES;
final Stage[] tmpStage = new Stage[N];
final AtomicBoolean[] stageShowReturned = new AtomicBoolean[N];
final AtomicBoolean[] hideEventReached = new AtomicBoolean[N];
final Runnable[] rShow = new Runnable[N];
final Runnable[] rHide = new Runnable[N];
for (int i = 0; i < N; i++) {
final int idx = i;
stageShowReturned[idx] = new AtomicBoolean(false);
hideEventReached[idx] = new AtomicBoolean(false);
rShow[idx] = () -> {
tmpStage[idx] = new TestStage(modality);
stages.add(tmpStage[idx]);
assertFalse(tmpStage[idx].isShowing());
tmpStage[idx].showAndWait();
stageShowReturned[idx].set(true);
assertFalse(tmpStage[idx].isShowing());
assertTrue(hideEventReached[idx].get());
for (int j = 0; j < idx; j++) {
assertFalse(stageShowReturned[j].get());
assertFalse(hideEventReached[j].get());
}
for (int j = idx+1; j < N; j++) {
assertTrue(stageShowReturned[j].get());
assertTrue(hideEventReached[j].get());
}
};
rHide[idx] = () -> {
hideEventReached[idx].set(true);
assertFalse(stageShowReturned[idx].get());
for (int j = 0; j < idx; j++) {
assertFalse(stageShowReturned[j].get());
assertFalse(hideEventReached[j].get());
}
for (int j = idx+1; j < N; j++) {
assertTrue(stageShowReturned[j].get());
assertTrue(hideEventReached[j].get());
}
assertNotNull(tmpStage[idx]);
tmpStage[idx].hide();
Util.sleep(1);
assertFalse(stageShowReturned[idx].get());
};
}
final Runnable[] runnables = new Runnable[2*N];
for (int i = 0; i < N; i++) {
runnables[i] = rShow[i];
runnables[(2*N - i - 1)] = rHide[i];
}
Util.runAndWait(runnables);
for (int i = 0; i < N; i++) {
assertFalse(tmpStage[i].isShowing());
}
}
@Test
public void testMultipleInterleaved() {
ensureTest1();
final int N = MAX_STAGES;
final Stage[] tmpStage = new Stage[N];
final AtomicBoolean[] stageShowReturned = new AtomicBoolean[N];
final AtomicBoolean[] hideEventReached = new AtomicBoolean[N];
final Runnable[] rShow = new Runnable[N];
final Runnable[] rHide = new Runnable[N];
for (int i = 0; i < N; i++) {
final int idx = i;
stageShowReturned[idx] = new AtomicBoolean(false);
hideEventReached[idx] = new AtomicBoolean(false);
rShow[idx] = () -> {
tmpStage[idx] = new TestStage(modality);
stages.add(tmpStage[idx]);
assertFalse(tmpStage[idx].isShowing());
tmpStage[idx].showAndWait();
stageShowReturned[idx].set(true);
assertFalse(tmpStage[idx].isShowing());
assertTrue(hideEventReached[idx].get());
for (int j = 0; j < idx; j++) {
assertFalse(stageShowReturned[j].get());
assertTrue(hideEventReached[j].get());
}
for (int j = idx+1; j < N; j++) {
assertTrue(stageShowReturned[j].get());
assertTrue(hideEventReached[j].get());
}
};
rHide[idx] = () -> {
hideEventReached[idx].set(true);
assertFalse(stageShowReturned[idx].get());
for (int j = 0; j < idx; j++) {
assertFalse(stageShowReturned[j].get());
assertTrue(hideEventReached[j].get());
}
for (int j = idx+1; j < N; j++) {
assertFalse(stageShowReturned[j].get());
assertFalse(hideEventReached[j].get());
}
assertNotNull(tmpStage[idx]);
tmpStage[idx].hide();
Util.sleep(1);
assertFalse(stageShowReturned[idx].get());
};
}
final Runnable[] runnables = new Runnable[2*N];
for (int i = 0; i < N; i++) {
runnables[i] = rShow[i];
runnables[N+i] = rHide[i];
}
Util.runAndWait(runnables);
for (int i = 0; i < N; i++) {
assertFalse(tmpStage[i].isShowing());
}
}
@Test
public void testTimeline() throws Throwable {
ensureTest1();
final CountDownLatch animationDone = new CountDownLatch(1);
final AtomicReference<Throwable> error = new AtomicReference<>(null);
KeyFrame kf = new KeyFrame(Duration.millis(200), e -> {
try {
tmpStage1 = new TestStage(modality);
stages.add(tmpStage1);
assertFalse(StageShim.isPrimary(tmpStage1));
assertFalse(tmpStage1.isShowing());
try {
tmpStage1.showAndWait();
fail("Did not get expected exception from showAndWait");
} catch (IllegalStateException ex) {
}
assertFalse(tmpStage1.isShowing());
} catch (Throwable t) {
error.set(t);
}
animationDone.countDown();
});
Timeline timeline = new Timeline(kf);
timeline.play();
try {
if (!animationDone.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
fail("Timeout waiting for animation");
}
} catch (InterruptedException ex) {
fail("Unexpected exception: " + ex);
}
final Throwable t = error.get();
if (t != null) {
throw t;
}
assertFalse(tmpStage1.isShowing());
}
@Test
public void testTimelineDialog() throws Throwable {
ensureTest1();
final CountDownLatch animationDone = new CountDownLatch(1);
final AtomicReference<Throwable> error = new AtomicReference<>(null);
KeyFrame kf = new KeyFrame(Duration.millis(200), e -> {
Alert alert = null;
try {
alert = new Alert(Alert.AlertType.INFORMATION);
assertFalse(alert.isShowing());
try {
alert.showAndWait();
fail("Did not get expected exception from showAndWait");
} catch (IllegalStateException ex) {
}
assertFalse(alert.isShowing());
} catch (Throwable t) {
error.set(t);
try {
if (alert.isShowing()) {
alert.close();
}
} catch (RuntimeException ex) {}
}
animationDone.countDown();
});
Timeline timeline = new Timeline(kf);
timeline.play();
try {
if (!animationDone.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
fail("Timeout waiting for animation");
}
} catch (InterruptedException ex) {
fail("Unexpected exception: " + ex);
}
final Throwable t = error.get();
if (t != null) {
throw t;
}
}
@Test
public void testTimelinePrint() throws Throwable {
assumeNotNull(PrinterJob.createPrinterJob());
ensureTest1();
final CountDownLatch animationDone = new CountDownLatch(1);
final AtomicReference<Throwable> error = new AtomicReference<>(null);
KeyFrame kf = new KeyFrame(Duration.millis(200), e -> {
try {
PrinterJob job = PrinterJob.createPrinterJob();
try {
job.showPrintDialog(myApp.primaryStage);
fail("Did not get expected exception from showPrintDialog");
} catch (IllegalStateException ex) {
}
try {
job.showPageSetupDialog(myApp.primaryStage);
fail("Did not get expected exception from showPageSetupDialog");
} catch (IllegalStateException ex) {
}
try {
Rectangle rect = new Rectangle(200, 100, Color.GREEN);
job.printPage(rect);
fail("Did not get expected exception from printPage");
} catch (IllegalStateException ex) {
}
} catch (Throwable t) {
error.set(t);
}
animationDone.countDown();
});
Timeline timeline = new Timeline(kf);
timeline.play();
try {
if (!animationDone.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
fail("Timeout waiting for animation");
}
} catch (InterruptedException ex) {
fail("Unexpected exception: " + ex);
}
final Throwable t = error.get();
if (t != null) {
throw t;
}
}
}
