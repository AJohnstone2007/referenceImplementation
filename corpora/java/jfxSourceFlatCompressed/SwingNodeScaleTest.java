package test.javafx.embed.swing;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import junit.framework.AssertionFailedError;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static test.util.Util.TIMEOUT;
public class SwingNodeScaleTest {
static CountDownLatch launchLatch;
static Dimension request;
static Rectangle result;
static JButton b;
@BeforeClass
public static void setupOnce() {
System.setProperty("glass.win.uiScale", "2");
System.setProperty("glass.gtk.uiScale", "2");
launchLatch = new CountDownLatch(1);
request = new Dimension(150, 100);
new Thread(() -> Application.launch(SwingNodeScaleTest.MyApp.class, (String[])null)).start();
try {
if (!launchLatch.await(5 * TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Application to launch ("+
(5 * TIMEOUT) + " seconds)");
}
} catch (InterruptedException ex) {
AssertionFailedError err = new AssertionFailedError("Unexpected exception");
err.initCause(ex);
throw err;
}
}
@AfterClass
public static void teardownOnce() {
Platform.exit();
}
@Test
public void testScale() throws Exception {
SwingUtilities.invokeAndWait(() -> result = b.getBounds());
System.out.println(result);
Assert.assertEquals(2 * request.width, 2 * result.x + result.width);
Assert.assertEquals(2 * request.height, 2 * result.y + result.height);
}
public static class MyApp extends Application {
SwingNode node;
@Override
public void start(Stage stage) throws Exception {
StackPane root = new StackPane();
stage.setScene(new Scene(root, 2 * request.width,
2 * request.height));
SwingUtilities.invokeLater(() -> {
node = new SwingNode();
JPanel panel = new JPanel();
panel.setLayout(new GridBagLayout());
b = new JButton("Swing Button");
b.setIcon(UIManager.getDefaults()
.getIcon("FileView.computerIcon"));
b.setPreferredSize(request);
b.addFocusListener(new FocusAdapter() {
@Override
public void focusGained(FocusEvent e) {
launchLatch.countDown();
}
});
panel.add(b);
node.setContent(panel);
Platform.runLater(() -> {
root.getChildren().add(node);
stage.show();
});
});
}
}
}
