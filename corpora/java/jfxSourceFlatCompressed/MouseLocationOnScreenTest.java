package test.robot.javafx.scene;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.robot.Robot;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import test.util.Util;
public class MouseLocationOnScreenTest {
static CountDownLatch startupLatch;
static Robot robot;
private static int DELAY_TIME = 1;
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
robot = new Robot();
startupLatch.countDown();
}
}
@BeforeClass
public static void initFX() {
startupLatch = new CountDownLatch(1);
new Thread(() -> Application.launch(TestApp.class, (String[]) null))
.start();
try {
if (!startupLatch.await(15, TimeUnit.SECONDS)) {
Assert.fail("Timeout waiting for FX runtime to start");
}
} catch (InterruptedException ex) {
Assert.fail("Unexpected exception: " + ex);
}
}
@Test(timeout = 120000)
public void testMouseLocation() throws Exception {
Screen screen = Screen.getPrimary();
Rectangle2D bounds = screen.getBounds();
int x1 = (int) bounds.getMinX();
int x2 = (int) (x1 + bounds.getWidth() - 1);
int y1 = (int) bounds.getMinY();
int y2 = (int) (y1 + bounds.getHeight() - 1);
Util.runAndWait(() -> {
edge(robot, x1, y1, x2, y1);
});
Util.runAndWait(() -> {
edge(robot, x1, y1 + 1, x2, y1 + 1);
});
Util.runAndWait(() -> {
edge(robot, x2, y1, x2, y2);
});
Util.runAndWait(() -> {
edge(robot, x2 - 1, y1, x2 - 1, y2);
});
Util.runAndWait(() -> {
edge(robot, x1, y1, x1, y2);
});
Util.runAndWait(() -> {
edge(robot, x1 + 1, y1, x1 + 1, y2);
});
Util.runAndWait(() -> {
edge(robot, x1, y2, x2, y2);
});
Util.runAndWait(() -> {
edge(robot, x1, y2 - 1, x2, y2 - 1);
});
Util.runAndWait(() -> {
cross(robot, x1, y1, x2, y2);
});
Util.runAndWait(() -> {
cross(robot, x1, y2, x2, y1);
});
}
@AfterClass
public static void teardown() {
Platform.exit();
}
static void validate(Robot robot, int x, int y) {
Assert.assertEquals(x, (int) robot.getMouseX());
Assert.assertEquals(y, (int) robot.getMouseY());
}
private static void edge(Robot robot, int x1, int y1, int x2, int y2) {
for (int x = x1; x <= x2; x++) {
for (int y = y1; y <= y2; y++) {
robot.mouseMove(x, y);
Util.sleep(DELAY_TIME);
validate(robot, x, y);
}
}
}
private static void cross(Robot robot, int x0, int y0, int x1, int y1) {
double dmax = (double) Math.max(Math.abs(x1 - x0), Math.abs(y1 - y0));
double dx = (x1 - x0) / dmax;
double dy = (y1 - y0) / dmax;
robot.mouseMove(x0, y0);
Util.sleep(DELAY_TIME);
validate(robot, x0, y0);
for (int i = 1; i <= dmax; i++) {
int x = (int) (x0 + dx * i);
int y = (int) (y0 + dy * i);
robot.mouseMove(x, y);
Util.sleep(DELAY_TIME);
validate(robot, x, y);
}
}
}
