package test.javafx.scene.shape;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import test.util.Util;
import static org.junit.Assert.assertTrue;
public class ShapeViewOrderLeakTest {
private static CountDownLatch startupLatch;
private static StackPane root;
private static Stage stage;
private static Group group;
private static WeakReference<Shape> shapeWeakRef;
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
stage = primaryStage;
Shape shape1 = new Rectangle();
Shape shape2 = new Circle();
shape1.setViewOrder(1);
shape2.setViewOrder(0);
shapeWeakRef = new WeakReference<>(shape1);
group = new Group(shape1, shape2);
shape1 = null;
shape2 = null;
root = new StackPane(group);
Scene scene = new Scene(root);
primaryStage.setScene(scene);
primaryStage.setOnShown(l -> {
Platform.runLater(() -> startupLatch.countDown());
});
primaryStage.show();
}
}
@BeforeClass
public static void initFX() throws Exception {
startupLatch = new CountDownLatch(1);
new Thread(() -> Application.launch(TestApp.class, (String[]) null)).start();
assertTrue("Timeout waiting for FX runtime to start",
startupLatch.await(15, TimeUnit.SECONDS));
}
@Test
public void testShapeViewOrderLeak() throws Exception {
Util.sleep(100);
Util.runAndWait(() -> {
group.getChildren().clear();
root.getChildren().clear();
});
for (int i = 0; i < 10; i++) {
System.gc();
if (shapeWeakRef.get() == null) {
break;
}
Util.sleep(500);
}
Assert.assertNull("Couldn't collect Shape", shapeWeakRef.get());
}
@AfterClass
public static void teardownOnce() {
Platform.runLater(() -> {
stage.hide();
Platform.exit();
});
}
}
