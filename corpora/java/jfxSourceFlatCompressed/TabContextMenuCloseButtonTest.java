package test.robot.javafx.scene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.fail;
import test.util.Util;
public class TabContextMenuCloseButtonTest {
static CountDownLatch startupLatch = new CountDownLatch(1);
static Robot robot;
static TabPane tabPane;
static volatile Stage stage;
static volatile Scene scene;
static final int SCENE_WIDTH = 250;
static final int SCENE_HEIGHT = SCENE_WIDTH;
final int DX = 17;
final int DY = DX;
final int NUM_TABS = 3;
ContextMenu contextMenu;
CountDownLatch cmlatch = new CountDownLatch(1);
@Test
public void testCloseButton() {
Util.sleep(1000);
MouseButton mouseButtons[] = { MouseButton.MIDDLE, MouseButton.SECONDARY, MouseButton.PRIMARY };
int expectedTabCount[] = {NUM_TABS - 1, NUM_TABS - 1, NUM_TABS - 2};
for (int i = 0; i < mouseButtons.length; ++i) {
final int ic = i;
Util.runAndWait(() -> {
robot.mouseMove((int) (scene.getWindow().getX() + scene.getX() + DX),
(int) (scene.getWindow().getY() + scene.getY() + DY));
robot.mousePress(mouseButtons[ic]);
robot.mouseRelease(mouseButtons[ic]);
});
Util.sleep(1000);
Assert.assertEquals(expectedTabCount[i], tabPane.getTabs().size());
}
}
@Test
public void testContextMenu() {
Util.sleep(1000);
contextMenu = new ContextMenu(new MenuItem("MI 1"));
contextMenu.setOnShown(event -> {
cmlatch.countDown();
});
for (Tab tab : tabPane.getTabs()) {
tab.setContextMenu(contextMenu);
}
Util.runAndWait(() -> {
robot.mouseMove((int)(scene.getWindow().getX() + scene.getX() + DX),
(int)(scene.getWindow().getY() + scene.getY() + DY));
robot.mousePress(MouseButton.SECONDARY);
robot.mouseRelease(MouseButton.SECONDARY);
});
waitForLatch(cmlatch, 5, "Timeout waiting for ContextMenu to be shown.");
}
@After
public void resetUI() {
Util.runAndWait(() -> {
tabPane.getTabs().remove(0, tabPane.getTabs().size());
contextMenu = null;
});
}
@Before
public void setupUI() {
Util.runAndWait(() -> {
for (int i = 0; i < NUM_TABS; ++i) {
tabPane.getTabs().add(new Tab(""));
}
});
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
public void start(Stage primaryStage) {
robot = new Robot();
stage = primaryStage;
tabPane = new TabPane();
tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
scene = new Scene(tabPane, SCENE_WIDTH, SCENE_HEIGHT);
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
}
