package test.robot.javafx.scene.tableview;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.beans.property.SimpleObjectProperty;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.fail;
public class TableViewResizeColumnToFitContentTest {
static Robot robot;
static TableView<TableObject> table;
static volatile Stage stage;
static volatile Scene scene;
static final int SCENE_WIDTH = 450;
static final int SCENE_HEIGHT = 100;
static CountDownLatch startupLatch;
public static void main(String[] args) {
TableViewResizeColumnToFitContentTest test =
new TableViewResizeColumnToFitContentTest();
test.resizeColumnToFitContentTest();
}
@Test
public void resizeColumnToFitContentTest() {
double colOneWidth = table.getColumns().get(0).getWidth();
double colTwoWidth = table.getColumns().get(1).getWidth();
double colThreeWidth = table.getColumns().get(2).getWidth();
double colsWidthBeforeResize = colOneWidth + colTwoWidth + colThreeWidth;
double colHeaderHeight = 25;
double posX = scene.getWindow().getX() + table.getLayoutX() +
colOneWidth + colTwoWidth;
double posY = scene.getWindow().getY() + table.getLayoutY() +
colHeaderHeight / 2;
CountDownLatch latch = new CountDownLatch(1);
Platform.runLater(() -> {
robot.mouseMove((int) posX, (int) posY);
robot.mousePress(MouseButton.PRIMARY);
robot.mouseRelease(MouseButton.PRIMARY);
robot.mousePress(MouseButton.PRIMARY);
robot.mouseRelease(MouseButton.PRIMARY);
latch.countDown();
});
waitForLatch(latch, 5, "Timeout while waiting for mouse double click");
try {
Thread.sleep(1000);
} catch (Exception e) {
fail("Thread was interrupted." + e);
}
Assert.assertTrue("resizeColumnToFitContent failed",
(colTwoWidth != table.getColumns().get(1).getWidth()));
colTwoWidth = table.getColumns().get(1).getWidth();
colThreeWidth = table.getColumns().get(2).getWidth();
double colsWidthAfterResize = colOneWidth + colTwoWidth + colThreeWidth;
Assert.assertEquals("TableView.CONSTRAINED_RESIZE_POLICY ignored.",
colsWidthBeforeResize, colsWidthAfterResize, 0);
}
@BeforeClass
public static void initFX() {
startupLatch = new CountDownLatch(1);
new Thread(() -> Application.launch(
TableViewResizeColumnToFitContentTest.TestApp.class,
(String[]) null)).start();
waitForLatch(startupLatch, 10, "Timeout waiting for FX runtime to start");
}
@AfterClass
public static void exit() {
Platform.runLater(() -> {
stage.hide();
});
Platform.exit();
}
public static void waitForLatch(CountDownLatch latch,
int seconds, String msg) {
try {
if (!latch.await(seconds, TimeUnit.SECONDS)) {
fail(msg);
}
} catch (Exception ex) {
fail("Unexpected exception: " + ex);
}
}
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) {
robot = new Robot();
stage = primaryStage;
table = new TableView<>();
TableColumn<TableObject, String> column;
column = new TableColumn<>("First Name");
column.setCellValueFactory((d) -> d.getValue().firstNameProperty);
table.getColumns().add(column);
column = new TableColumn<>("Description");
column.setCellValueFactory((d) -> d.getValue().descriptionProperty);
table.getColumns().add(column);
column = new TableColumn<>("Last Name");
column.setCellValueFactory((d) -> d.getValue().lastNameProperty);
table.getColumns().add(column);
table.getItems().add(
new TableObject("John", "Doe",
"Currently wearing brown pants"));
table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
scene = new Scene(table, SCENE_WIDTH, SCENE_HEIGHT);
stage.setScene(scene);
stage.initStyle(StageStyle.UNDECORATED);
stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e
-> Platform.runLater(startupLatch::countDown));
stage.setAlwaysOnTop(true);
stage.show();
}
}
private static final class TableObject {
private final SimpleObjectProperty<String> firstNameProperty;
private final SimpleObjectProperty<String> lastNameProperty;
private final SimpleObjectProperty<String> descriptionProperty;
public TableObject(String firstName, String lastName,
String description) {
this.firstNameProperty = new SimpleObjectProperty<>(firstName);
this.lastNameProperty = new SimpleObjectProperty<>(lastName);
this.descriptionProperty = new SimpleObjectProperty<>(description);
}
}
}
