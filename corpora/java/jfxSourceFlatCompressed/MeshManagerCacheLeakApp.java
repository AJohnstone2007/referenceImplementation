package test.javafx.scene.shape.meshmanagercacheleaktest;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import static test.javafx.scene.shape.meshmanagercacheleaktest.Constants.*;
public class MeshManagerCacheLeakApp {
static String shapeType;
static int numShapes;
CountDownLatch latch;
static CountDownLatch startupLatch = new CountDownLatch(1);
static Group container;
static volatile Stage stage;
public static class TestApp extends Application {
@Override
public void start(Stage pStage) {
stage = pStage;
HBox root = new HBox();
container = new Group();
root.getChildren().add(container);
Scene scene = new Scene(root);
stage.setScene(scene);
stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e ->
Platform.runLater(startupLatch::countDown));
stage.show();
}
}
public void testOOM() {
System.gc();
try {
byte[] mem = null;
if (shapeType.equals("Sphere")) {
mem = new byte[1024 * 1024 * 8];
} else if (shapeType.equals("Cylinder")) {
mem = new byte[1024 * 1024 * 8];
} else if (shapeType.equals("Box")) {
mem = new byte[1024 * 1024 * 11];
}
float radius = 20.0f;
float height = 20;
int sphereDivisions = 70;
int cylinderDivisions = 512;
int boxWHD = 300;
for (int i = 0; i < numShapes; ++i) {
Shape3D shape = null;
if (shapeType.equals("Sphere")) {
shape = new Sphere(radius++, sphereDivisions);
} else if (shapeType.equals("Cylinder")) {
shape = new Cylinder(radius++, height, cylinderDivisions);
} else if (shapeType.equals("Box")) {
shape = new Box(boxWHD++, boxWHD, boxWHD);
}
latch = new CountDownLatch(1);
Shape3D shp = shape;
Platform.runLater(() -> {
try {
container.getChildren().add(shp);
latch.countDown();
} catch (OutOfMemoryError e) {
System.exit(ERROR_OOM);
} catch (Exception e) {
System.exit(ERROR_OOM);
}
});
waitForLatch(latch, 5, -1);
Thread.sleep(35);
latch = new CountDownLatch(1);
Platform.runLater(() -> {
container.getChildren().clear();
latch.countDown();
});
waitForLatch(latch, 5, -1);
}
} catch (OutOfMemoryError e) {
System.exit(ERROR_OOM);
} catch (Exception e) {
System.exit(ERROR_OOM);
}
System.exit(ERROR_NONE);
}
public void waitForLatch(CountDownLatch cdLatch, int seconds, int error) {
try {
if (!cdLatch.await(seconds, TimeUnit.SECONDS)) {
System.exit(error);
}
} catch (Exception ex) {
System.exit(error);
}
}
public static void main(String[] args) {
shapeType = args[0];
numShapes = Integer.parseInt(args[1]);
MeshManagerCacheLeakApp test = new MeshManagerCacheLeakApp();
new Thread(() -> Application.launch(TestApp.class, (String[])null)).start();
test.waitForLatch(startupLatch, 10, ERROR_LAUNCH);
test.testOOM();
}
}
