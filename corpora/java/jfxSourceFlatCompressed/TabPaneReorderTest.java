package test.robot.javafx.scene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.geometry.Side;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import test.util.Util;
public class TabPaneReorderTest {
static CountDownLatch selectionLatch;
static CountDownLatch startupLatch;
static Robot robot;
static HBox root;
static volatile Scene scene;
static volatile Stage stage;
TabPane tabPane;
int tabPaneWidth;
int tabPaneHeight;
int dragDistance;
static final int SCENE_WIDTH = 400;
static final int SCENE_HEIGHT = 400;
static final float DRAG_DISTANCE_PERCENTAGE = 0.25f;
static final float DRAG_TAB = 4.0f;
static final int DX = 15;
static final int DY = DX;
static final int TAB_COUNT = 9;
String tabOrder;
String currentTabOrder;
boolean isTabListReorderd;
ListChangeListener<Tab> reorderListener = c -> {
while (c.next()) {
if (c.wasPermutated()) {
isTabListReorderd = true;
}
}
};
@Test
public void testReorderTop() {
tabPane.getSelectionModel().select(0);
setDragPolicyAndSide(TabPane.TabDragPolicy.REORDER, Side.TOP);
dragDistance = (int)(tabPaneWidth * DRAG_DISTANCE_PERCENTAGE);
testReorder((int)(tabPaneWidth / TAB_COUNT * DRAG_TAB), DY, true);
}
@Test
public void testReorderBottom() {
tabPane.getSelectionModel().select(8);
setDragPolicyAndSide(TabPane.TabDragPolicy.REORDER, Side.BOTTOM);
dragDistance = (int)(tabPaneWidth * DRAG_DISTANCE_PERCENTAGE);
testReorder((int)(tabPaneWidth / TAB_COUNT * DRAG_TAB),
tabPaneHeight - DY, true);
}
@Test
public void testReorderLeft() {
tabPane.getSelectionModel().select(8);
setDragPolicyAndSide(TabPane.TabDragPolicy.REORDER, Side.LEFT);
dragDistance = (int)(tabPaneHeight * DRAG_DISTANCE_PERCENTAGE);
testReorder(DX, (int)(tabPaneHeight / TAB_COUNT * DRAG_TAB), false);
}
@Test
public void testReorderRight() {
tabPane.getSelectionModel().select(0);
setDragPolicyAndSide(TabPane.TabDragPolicy.REORDER, Side.RIGHT);
dragDistance = (int)(tabPaneHeight * DRAG_DISTANCE_PERCENTAGE);
testReorder(tabPaneWidth - DX,
(int)(tabPaneHeight / TAB_COUNT * DRAG_TAB), false);
}
private void testReorder(int dX, int dY, boolean isDragInXDir) {
InvalidationListener selectionChangeListener = e -> {
selectionLatch.countDown();
};
tabPane.getSelectionModel().selectedItemProperty().
addListener(selectionChangeListener);
selectionLatch = new CountDownLatch(1);
Util.runAndWait(() -> {
robot.mouseMove((int)(scene.getWindow().getX() + scene.getX() + dX),
(int)(scene.getWindow().getY() + scene.getY() + dY));
robot.mousePress(MouseButton.PRIMARY);
});
waitForLatch(selectionLatch, 5, "Timeout waiting for the tab to get selected.");
tabPane.getSelectionModel().selectedItemProperty().
removeListener(selectionChangeListener);
int direction = dragDistance / Math.abs(dragDistance);
for (int i = 0; i != dragDistance; i += direction) {
moveMouse(dX, dY, isDragInXDir, i);
}
for (int i = dragDistance; i != 0; i -= direction) {
moveMouse(dX, dY, isDragInXDir, i);
}
Util.runAndWait(() -> {
robot.mouseRelease(MouseButton.PRIMARY);
});
currentTabOrder = "";
selectionChangeListener = e -> {
currentTabOrder += tabPane.getSelectionModel().getSelectedItem().getText();
selectionLatch.countDown();
};
tabPane.getSelectionModel().selectedItemProperty().
addListener(selectionChangeListener);
selectionLatch = new CountDownLatch(1);
tabPane.getSelectionModel().select(0);
waitForLatch(selectionLatch, 5, "Timeout waiting for tab[0] to get selected.");
for (int i = 1; i < TAB_COUNT; i++) {
Util.runAndWait(() -> {
selectionLatch = new CountDownLatch(1);
robot.keyPress(KeyCode.RIGHT);
robot.keyRelease(KeyCode.RIGHT);
});
waitForLatch(selectionLatch, 5, "Timeout waiting for tab[" +
i + "] to get selected.");
}
tabPane.getSelectionModel().selectedItemProperty().
removeListener(selectionChangeListener);
Assert.assertFalse("Tabs should not be reordered.", isTabListReorderd);
Assert.assertEquals(tabOrder, currentTabOrder);
}
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) {
stage = primaryStage;
robot = new Robot();
root = new HBox();
scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
stage.setScene(scene);
stage.initStyle(StageStyle.UNDECORATED);
stage.setOnShown(l -> {
Platform.runLater(() -> startupLatch.countDown());
});
stage.setAlwaysOnTop(true);
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
Platform.runLater(() -> {
stage.hide();
});
Platform.exit();
}
@Before
public void setupTest() {
CountDownLatch tabPaneLayoutLatch = new CountDownLatch(2);
Util.runAndWait(() -> {
tabOrder = "";
tabPane = new TabPane();
for (int i = 0 ; i < TAB_COUNT; ++i) {
tabPane.getTabs().add(new Tab("tab" + i));
tabOrder += "tab" + i;
}
tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
tabPane.widthProperty().addListener(l -> {
tabPaneWidth = (int)Math.floor(tabPane.getWidth());
tabPaneLayoutLatch.countDown();
});
tabPane.heightProperty().addListener(l -> {
tabPaneHeight = (int)Math.floor(tabPane.getHeight());
tabPaneLayoutLatch.countDown();
});
tabPane.getTabs().addListener(reorderListener);
root.getChildren().add(tabPane);
});
waitForLatch(tabPaneLayoutLatch, 5, "Timeout waiting for TabPane layout.");
}
@After
public void resetTest() {
isTabListReorderd = false;
Util.runAndWait(() -> {
root.getChildren().clear();
tabPane.getTabs().removeListener(reorderListener);
tabPane.getTabs().clear();
tabPane = null;
});
}
private static void waitForLatch(CountDownLatch latch, int seconds, String msg) {
try {
if (!latch.await(seconds, TimeUnit.SECONDS)) {
fail(msg);
}
} catch (Exception ex) {
fail("Unexpected exception: " + ex);
}
}
private void setDragPolicyAndSide(TabPane.TabDragPolicy dragPolicy, Side side) {
Util.runAndWait(() -> {
tabPane.setTabDragPolicy(dragPolicy);
tabPane.setSide(side);
});
}
private static void moveMouse(int dX, int dY, boolean isDragInXDir, int d) {
CountDownLatch moveLatch = new CountDownLatch(1);
Platform.runLater(() -> {
if (isDragInXDir) {
robot.mouseMove((int)(scene.getWindow().getX() + scene.getX() + dX) + d,
(int)(scene.getWindow().getY() + scene.getY() + dY));
} else {
robot.mouseMove((int)(scene.getWindow().getX() + scene.getX() + dX),
(int)(scene.getWindow().getY() + scene.getY() + dY) + d);
}
moveLatch.countDown();
});
waitForLatch(moveLatch, 5, "Timeout waiting for robot.mouseMove().");
}
}
