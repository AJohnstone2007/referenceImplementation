package test.javafx.stage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.stage.Screen;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import test.util.Util;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
public class ScreenTest {
static CountDownLatch startupLatch = new CountDownLatch(1);
static ObservableList<Screen> screens;
static volatile boolean screensListenerCalled = false;
static volatile boolean screensSizeIsZero = false;
private static void waitForLatch(CountDownLatch latch, int seconds, String msg) throws Exception {
assertTrue("Timeout: " + msg, latch.await(seconds, TimeUnit.SECONDS));
}
@BeforeClass
public static void initFX() throws Exception {
Platform.setImplicitExit(false);
Platform.startup(() -> {
screens = Screen.getScreens();
screens.addListener((Change<?> change) -> {
final int size = screens.size();
System.err.println("Screens list changed, size = " + size);
if (size == 0) {
screensSizeIsZero = true;
}
screensListenerCalled = true;
});
Platform.runLater(startupLatch::countDown);
});
waitForLatch(startupLatch, 10, "FX runtime failed to start");
}
@AfterClass
public static void exitFX() {
Platform.exit();
}
@Test
public void testScreensNotEmpty() {
assertNotNull(screens);
assertFalse("Screens list is empty", screens.size() == 0);
}
@Test
public void testScreensNotEmptyInListener() {
Util.sleep(2000);
if (!screensListenerCalled) {
System.err.println("Skipping test: Screens listener not called");
}
assumeTrue(screensListenerCalled);
assertFalse("Screens list is empty in listener", screensSizeIsZero);
}
}
