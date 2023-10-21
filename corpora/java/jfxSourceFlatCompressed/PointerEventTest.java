package test.robot.javafx.web;
import com.sun.javafx.PlatformUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.concurrent.Worker;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.robot.Robot;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.lang.Integer;
import java.lang.Number;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import test.util.Util;
import static org.junit.Assert.*;
public class PointerEventTest {
private static final int LEFT_BUTTON_DRAG = 1;
private static final int MIDDLE_BUTTON_DRAG = 4;
private static final int RIGHT_BUTTON_DRAG = 2;
private static final int SCENE_WIDTH = 250;
private static final int SCENE_HEIGHT = SCENE_WIDTH;
private static final int SLEEP_TIME = 500;
private final int DRAG_DISTANCE = 15;
private final int DX = 125;
private final int DY = 125;
private static CountDownLatch startupLatch;
static Document document;
static Element element;
static Robot robot;
static WebView webView;
static WebEngine webEngine;
static volatile Stage stage;
static volatile Scene scene;
private String buttonMask;
private int result = 0;
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) {
robot = new Robot();
stage = primaryStage;
stage.setTitle("Mouse Drag Test");
webView = new WebView();
webEngine = webView.getEngine();
String URL = this.getClass().getResource("pointerEvent.html").toString();
webView.getEngine().getLoadWorker().stateProperty().addListener((ov, o, n) -> {
if (n == Worker.State.SUCCEEDED) {
document = webEngine.getDocument();
element = document.getElementById("buttonPressed");
startupLatch.countDown();
}
});
webEngine.load(URL);
scene = new Scene(new StackPane(webView), SCENE_WIDTH, SCENE_HEIGHT);
stage.setScene(scene);
stage.setAlwaysOnTop(true);
stage.setOnShown(e -> startupLatch.countDown());
stage.show();
}
}
public int mouseButtonDrag(MouseButton... buttons) {
Util.runAndWait(() -> {
robot.mouseMove((int)(scene.getWindow().getX() + scene.getX() + DX),
(int)(scene.getWindow().getY() + scene.getY() + DY));
robot.mousePress(buttons);
});
Util.runAndWait(() -> {
for (int i = 0; i < DRAG_DISTANCE; i++) {
robot.mouseMove((int)(scene.getWindow().getX() + scene.getX() + DX - i),
(int)(scene.getWindow().getY() + scene.getY() + DY));
}
});
Util.sleep(SLEEP_TIME);
Util.runAndWait(() -> {
buttonMask = element.getTextContent();
robot.mouseRelease(buttons);
});
return Integer.parseInt(buttonMask);
}
@Test
public void testLeftButtonDrag() {
int result = mouseButtonDrag(MouseButton.PRIMARY);
assertEquals(LEFT_BUTTON_DRAG, result);
}
@Test
public void testRightButtonDrag() {
int result = mouseButtonDrag(MouseButton.SECONDARY);
assertEquals(RIGHT_BUTTON_DRAG, result);
}
@Test
public void testMiddleButtonDrag() {
int result = mouseButtonDrag(MouseButton.MIDDLE);
assertEquals(MIDDLE_BUTTON_DRAG, result);
}
@Test
public void testLeftMiddleButtonDrag() {
int result = mouseButtonDrag(MouseButton.PRIMARY, MouseButton.MIDDLE);
assertEquals((LEFT_BUTTON_DRAG | MIDDLE_BUTTON_DRAG), result);
}
@Test
public void testMiddleRightButtonDrag() {
int result = mouseButtonDrag(MouseButton.MIDDLE, MouseButton.SECONDARY);
assertEquals((MIDDLE_BUTTON_DRAG | RIGHT_BUTTON_DRAG), result);
}
@Test
public void testLeftRightButtonDrag() {
int result = mouseButtonDrag(MouseButton.PRIMARY, MouseButton.SECONDARY);
assertEquals((LEFT_BUTTON_DRAG | RIGHT_BUTTON_DRAG), result);
}
@Test
public void testLeftMiddleRightButtonDrag() {
int result = mouseButtonDrag(MouseButton.PRIMARY, MouseButton.MIDDLE, MouseButton.SECONDARY);
assertEquals((LEFT_BUTTON_DRAG | MIDDLE_BUTTON_DRAG | RIGHT_BUTTON_DRAG), result);
}
@BeforeClass
public static void initFX() {
startupLatch = new CountDownLatch(2);
new Thread(() -> Application.launch(TestApp.class, (String[])null)).start();
waitForLatch(startupLatch, 15, "Timeout waiting for FX runtime to start");
}
@AfterClass
public static void exit() {
Platform.runLater(() -> {
stage.hide();
});
Platform.exit();
}
@After
public void resetTest() {
Util.runAndWait(() -> {
robot.mouseRelease(MouseButton.PRIMARY, MouseButton.MIDDLE, MouseButton.SECONDARY);
robot.keyType(KeyCode.ESCAPE);
});
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
