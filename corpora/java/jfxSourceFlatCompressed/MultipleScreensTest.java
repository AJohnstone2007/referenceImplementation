package test.javafx.stage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import test.util.Util;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
public class MultipleScreensTest {
static CountDownLatch startupLatch = new CountDownLatch(1);
static ObservableList<Screen> screens;
static Screen primaryScreen;
static Screen otherScreen;
Stage stage;
private static void waitForLatch(CountDownLatch latch, int seconds, String msg) throws Exception {
assertTrue("Timeout: " + msg, latch.await(seconds, TimeUnit.SECONDS));
}
@BeforeClass
public static void initFX() throws Exception {
Platform.setImplicitExit(false);
Platform.startup(startupLatch::countDown);
waitForLatch(startupLatch, 10, "FX runtime failed to start");
primaryScreen = Screen.getPrimary();
assertNotNull("Primary screen is null", primaryScreen);
screens = Screen.getScreens();
assertNotNull("List of screens is null", screens);
assumeTrue(screens.size() > 1);
otherScreen = screens.stream()
.filter(s -> !primaryScreen.equals(s))
.findFirst()
.orElseThrow();
assertNotNull("Secondary screen is null", otherScreen);
}
@AfterClass
public static void exitFX() {
Platform.exit();
}
@Before
public void initTest() {
Util.runAndWait(() -> stage = new Stage());
}
@After
public void cleanupTest() {
if (stage != null) {
Platform.runLater(stage::hide);
}
}
private void createAndShowStage(Screen screen, boolean hasScene) throws Exception {
assertNotNull("Stage is null", stage);
final CountDownLatch shownLatch = new CountDownLatch(1);
Util.runAndWait(() -> {
stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e -> shownLatch.countDown());
Rectangle2D bounds = screen.getBounds();
stage.setX(bounds.getMinX());
stage.setY(bounds.getMinY());
stage.setWidth(bounds.getWidth());
stage.setHeight(bounds.getHeight());
if (hasScene) {
stage.setScene(new Scene(new Group()));
}
stage.show();
});
waitForLatch(shownLatch, 5, "Stage failed to show");
}
@Test(timeout = 15000)
public void showStageNoScenePrimaryScreen() throws Exception {
createAndShowStage(primaryScreen, false);
}
@Test(timeout = 15000)
public void showStageNoSceneOtherScreen() throws Exception {
createAndShowStage(otherScreen, false);
}
@Test(timeout = 15000)
public void showStageScenePrimaryScreen() throws Exception {
createAndShowStage(primaryScreen, true);
}
@Test(timeout = 15000)
public void showStageSceneOtherScreen() throws Exception {
createAndShowStage(otherScreen, true);
}
}
