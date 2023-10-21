package test.com.sun.glass.ui.monocle.headless;
import com.sun.glass.ui.Screen;
import javafx.application.Application;
import javafx.stage.Stage;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
public class HeadlessGeometry1Test {
private static CountDownLatch startupLatch = new CountDownLatch(1);
private static int width;
private static int height;
private static int depth;
public static class TestApp extends Application {
@Override
public void start(Stage t) {
width = Screen.getMainScreen().getWidth();
height = Screen.getMainScreen().getHeight();
depth = Screen.getMainScreen().getDepth();
startupLatch.countDown();
}
}
@BeforeClass
public static void setup() throws Exception {
System.setProperty("glass.platform", "Monocle");
System.setProperty("monocle.platform", "Headless");
System.setProperty("prism.order", "sw");
System.setProperty("headless.geometry", "150x250");
new Thread(() -> Application.launch(TestApp.class)).start();
startupLatch.await(5, TimeUnit.SECONDS);
Assert.assertEquals(0, startupLatch.getCount());
}
@Test
public void setScreenBounds() throws Exception {
Assert.assertEquals(150, width);
Assert.assertEquals(250, height);
Assert.assertEquals(32, depth);
}
}
