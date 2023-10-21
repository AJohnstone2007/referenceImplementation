package test.robot.javafx.embed.swing;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import junit.framework.AssertionFailedError;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import test.util.Util;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.AWTException;
import java.awt.Frame;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static test.util.Util.TIMEOUT;
public class SwingNodeBase {
public static final int BASE_LOCATION = 200;
public static final int BASE_SIZE = 200;
public static final int WAIT_TIME = 300;
public static final int LONG_WAIT_TIME = 2500;
protected static Robot robot;
private static final CountDownLatch launchLatch = new CountDownLatch(1);
static MyApp myApp;
@BeforeClass
public static void setupOnce() throws AWTException, InvocationTargetException, InterruptedException {
robot = new Robot();
robot.setAutoDelay(100);
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
CountDownLatch paintLatch = new CountDownLatch(1);
SwingUtilities.invokeAndWait(()->{
myApp.swingNode.setContent(new MyApp.ColorPanel(java.awt.Color.RED));
myApp.swingNode.setVisible(true);
});
paintLatch.countDown();
}
@AfterClass
public static void teardownOnce() {
Platform.runLater(() -> {
Platform.exit();
});
}
public static void runWaitSleep(Runnable run) {
Util.runAndWait(run);
Util.sleep(WAIT_TIME);
}
public static void invokeWaitSleep(Runnable run) throws InvocationTargetException, InterruptedException {
SwingUtilities.invokeAndWait(run);
Util.sleep(WAIT_TIME);
}
public static class MyApp extends Application {
private Stage stage;
private SwingNode swingNode;
private VBox vbox;
private Scene scene;
@Override public void init() {
SwingNodeBase.myApp = this;
Platform.setImplicitExit(false);
}
@Override public void start(Stage primaryStage) throws Exception {
swingNode = new SwingNode();
vbox = new VBox(swingNode);
scene = new Scene(vbox, BASE_SIZE, BASE_SIZE,
javafx.scene.paint.Color.GREEN);
launchLatch.countDown();
}
public void closeStage() {
runWaitSleep(() -> {
stage.close();
stage = null;
});
}
public void createAndShowStage() {
runWaitSleep(() -> {
stage = new Stage();
stage.setScene(scene);
stage.setTitle("JFX toplevel");
stage.setX(BASE_LOCATION);
stage.setY(BASE_LOCATION);
stage.show();
stage.requestFocus();
});
}
public void createStageAndDialog() throws InvocationTargetException, InterruptedException {
createAndShowStage();
createDialog();
}
public void attachSwingNode() {
runWaitSleep(() -> vbox.getChildren().add(swingNode));
}
public void detachSwingNode() {
runWaitSleep(() -> vbox.getChildren().remove(swingNode));
}
static JDialog dialog;
public Runnable createDialogRunnable = () -> {
Frame frame = JOptionPane.getFrameForComponent(myApp.swingNode.getContent());
dialog = new JDialog(frame);
dialog.setTitle("JDialog");
dialog.getContentPane().add(new ColorPanel(java.awt.Color.BLUE));
dialog.setLocationRelativeTo(dialog.getParent());
dialog.setBounds(BASE_LOCATION + BASE_SIZE / 2,
BASE_LOCATION, BASE_SIZE,
BASE_SIZE * 2);
};
public void createDialog() throws InvocationTargetException, InterruptedException {
dialog = null;
invokeWaitSleep(createDialogRunnable);
}
public void disposeDialog() throws InvocationTargetException, InterruptedException {
invokeWaitSleep(() -> dialog.dispose());
}
public void showDialog() throws InvocationTargetException, InterruptedException {
invokeWaitSleep(() -> dialog.setVisible(true));
}
public void closeStageAndDialog() throws InvocationTargetException, InterruptedException {
disposeDialog();
closeStage();
}
public static class ColorPanel extends JPanel {
private CountDownLatch latch;
public ColorPanel(java.awt.Color color) {
super();
setBackground(color);
}
}
}
public void testAbove(boolean above) {
int checkLoc = BASE_LOCATION + 3 * BASE_SIZE /4;
int clickLoc = BASE_LOCATION + BASE_SIZE / 4;
if (myApp.stage != null && myApp.stage.isShowing()) {
robot.mouseMove(clickLoc, clickLoc);
robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
}
if (above) {
Assert.assertEquals("JDialog is not above JavaFX stage",
java.awt.Color.BLUE, robot.getPixelColor(checkLoc, checkLoc));
} else {
Assert.assertFalse("JDialog is above JavaFX stage",
java.awt.Color.BLUE.equals(robot.getPixelColor(checkLoc, checkLoc)));
}
}
}
