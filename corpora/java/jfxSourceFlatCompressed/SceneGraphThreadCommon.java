package test.com.sun.javafx.application;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import junit.framework.AssertionFailedError;
import org.junit.AfterClass;
import test.util.Util;
import static org.junit.Assert.*;
import static test.util.Util.TIMEOUT;
public class SceneGraphThreadCommon {
private static final int SLEEP_TIME = 1000;
private static final CountDownLatch launchLatch = new CountDownLatch(1);
private static final CountDownLatch doneLatch = new CountDownLatch(1);
static volatile Callable<Node> initCallable;
static MyApp myApp;
public static class MyApp extends Application {
Node content;
Stage primaryStage;
public MyApp() {
Platform.setImplicitExit(false);
assertTrue(Platform.isFxApplicationThread());
}
@Override public void init() throws Exception {
assertFalse(Platform.isFxApplicationThread());
SceneGraphThreadCommon.myApp = this;
content = initCallable.call();
assertNotNull(content);
}
@Override public void start(Stage primaryStage) throws Exception {
assertTrue(Platform.isFxApplicationThread());
primaryStage.setTitle("Primary stage");
StackPane root = new StackPane(content);
Scene scene = new Scene(root, 300, 200);
assertFalse(primaryStage.isShowing());
primaryStage.setScene(scene);
primaryStage.show();
assertTrue(primaryStage.isShowing());
this.primaryStage = primaryStage;
launchLatch.countDown();
}
}
private void doTest(Callable<Node> callable) {
initCallable = callable;
final Thread testThread = Thread.currentThread();
final AtomicReference<Throwable> launchErr = new AtomicReference<>(null);
new Thread(() -> {
try {
Application.launch(MyApp.class, (String[])null);
doneLatch.countDown();
} catch (Throwable t) {
launchErr.set(t);
testThread.interrupt();
}
}).start();
try {
if (!launchLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Application to launch");
}
} catch (InterruptedException ex) {
Throwable t = launchErr.get();
if (t instanceof RuntimeException) {
throw (RuntimeException)t;
}
else if (t instanceof Error) {
throw (Error)t;
} else {
throw new RuntimeException(t);
}
}
assertNotNull(myApp);
assertNotNull(myApp.content);
assertNotNull(myApp.primaryStage);
Util.sleep(SLEEP_TIME);
Util.runAndWait(() -> myApp.primaryStage.hide());
Util.sleep(SLEEP_TIME);
Platform.exit();
try {
if (!doneLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Application to finish");
}
} catch (InterruptedException ex) {
Throwable t = launchErr.get();
if (t instanceof RuntimeException) {
throw (RuntimeException)t;
}
else if (t instanceof Error) {
throw (Error)t;
} else {
throw new RuntimeException(t);
}
}
}
protected void doTestShape() {
doTest(() -> new Circle(75, 75, 50));
}
protected void doTestContextMenu() {
doTest(() -> {
Label label = new Label("My Label");
ContextMenu contextMenu = new ContextMenu();
label.setContextMenu(contextMenu);
return label;
});
}
protected void doTestTooltip() {
doTest(() -> {
Button button = new Button("My Button");
Tooltip tooltip = new Tooltip("My Tooltip");
button.setTooltip(tooltip);
return button;
});
}
protected void doTestScene() {
doTest(() -> {
Group root1 = new Group();
Group root2 = new Group();
Scene theScene = new Scene(root1);
Rectangle theNode = new Rectangle(75, 50);
root1.getChildren().add(theNode);
root1.getChildren().clear();
root2.getChildren().add(theNode);
theScene.setRoot(root2);
root2.getChildren().clear();
return theNode;
});
}
}
