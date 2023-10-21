package test.com.sun.javafx.application;
import com.sun.javafx.application.PlatformImplShim;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import junit.framework.AssertionFailedError;
import test.util.Util;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.Assert;
public class SwingNoExit {
private static final int SLEEP_TIME = 1000;
private JFrame frame;
private JFXPanel fxPanel;
public void init() {
frame = new JFrame("JFXPanel 1");
frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
frame.setLayout(new BorderLayout());
fxPanel = new JFXPanel();
fxPanel.setPreferredSize(new Dimension(210, 180));
frame.getContentPane().add(fxPanel, BorderLayout.CENTER);
Util.runAndWait(() -> {
Group root = new Group();
Scene scene = new Scene(root);
scene.setFill(Color.LIGHTYELLOW);
fxPanel.setScene(scene);
});
frame.setLocationRelativeTo(null);
frame.pack();
frame.setVisible(true);
}
@Test
public void doTestImplicitExit() throws Throwable {
final AtomicReference<Throwable> error = new AtomicReference<>(null);
final CountDownLatch initLatch = new CountDownLatch(1);
SwingUtilities.invokeLater(() -> {
try {
init();
initLatch.countDown();
} catch (Throwable th) {
error.set(th);
}
});
if (!initLatch.await(Util.TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for JFXPanel to launch and initialize");
}
Throwable t = error.get();
if (t != null) {
throw t;
}
final CountDownLatch runAndWait = new CountDownLatch(1);
Platform.runLater(() -> {
Platform.exit();
runAndWait.countDown();
});
if (!runAndWait.await(Util.TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Platform.exit()");
}
final CountDownLatch exitLatch = PlatformImplShim.test_getPlatformExitLatch();
Thread.sleep(SLEEP_TIME);
Assert.assertEquals("Platform.exit() caused FX to exit, while JFXPanel is alive",
1, exitLatch.getCount());
try {
SwingUtilities.invokeAndWait(() -> {
frame.setVisible(false);
frame.dispose();
});
}
catch (InvocationTargetException ex) {
throw new AssertionFailedError("Exception while disposing JFrame");
}
Thread.sleep(SLEEP_TIME);
Assert.assertEquals("FX is not exited, when the last JFXPanel is disposed",
0, exitLatch.getCount());
}
}
