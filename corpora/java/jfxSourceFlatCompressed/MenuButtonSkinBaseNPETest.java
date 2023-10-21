package test.javafx.scene.control;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import test.util.Util;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
public class MenuButtonSkinBaseNPETest {
static CountDownLatch startupLatch;
static Menu menu;
static MenuBar menuBar;
public static void main(String[] args) throws Exception {
initFX();
try {
var test = new MenuButtonSkinBaseNPETest();
test.testMenuButtonNPE();
} catch (Throwable e) {
e.printStackTrace();
} finally {
tearDown();
}
}
@Test
public void testMenuButtonNPE() throws Exception {
PrintStream defaultErrorStream = System.err;
ByteArrayOutputStream out = new ByteArrayOutputStream();
System.setErr(new PrintStream(out, true));
Thread.sleep(1000);
Util.runAndWait(() -> {
menu.hide();
menuBar.getMenus().clear();
});
Thread.sleep(100);
System.setErr(defaultErrorStream);
Assert.assertEquals("No error should be thrown", "", out.toString());
}
public static class TestApp extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
menu = new Menu("Menu", null, new MenuItem("Press '_a'"));
menuBar = new MenuBar(menu);
Scene scene = new Scene(menuBar);
primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> startupLatch.countDown());
primaryStage.setScene(scene);
primaryStage.show();
menu.show();
}
}
@BeforeClass
public static void initFX() throws Exception {
startupLatch = new CountDownLatch(1);
new Thread(() -> Application.launch(TestApp.class, (String[])null)).start();
Assert.assertTrue("Timeout waiting for FX runtime to start",
startupLatch.await(15, TimeUnit.SECONDS));
}
@AfterClass
public static void tearDown() {
Platform.exit();
}
}
