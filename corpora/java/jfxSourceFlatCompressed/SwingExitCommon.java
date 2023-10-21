package test.com.sun.javafx.application;
import com.sun.javafx.application.PlatformImplShim;
import java.awt.BorderLayout;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javax.swing.JFrame;
import junit.framework.AssertionFailedError;
import test.util.Util;
import static org.junit.Assert.*;
import static test.util.Util.TIMEOUT;
public class SwingExitCommon {
private static final int SLEEP_TIME = 1000;
private static final CountDownLatch initialized = new CountDownLatch(1);
private static volatile boolean implicitExit;
private JFrame frame;
private JFXPanel fxPanel;
public void init() {
assertTrue(SwingUtilities.isEventDispatchThread());
assertEquals(1, initialized.getCount());
assertTrue(Platform.isImplicitExit());
if (!implicitExit) {
Platform.setImplicitExit(false);
assertFalse(Platform.isImplicitExit());
}
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
initialized.countDown();
assertEquals(0, initialized.getCount());
}
private void doTestCommon(boolean implicitExit,
boolean reEnableImplicitExit, boolean appShouldExit) {
SwingExitCommon.implicitExit = implicitExit;
final Throwable[] testError = new Throwable[1];
final Thread testThread = Thread.currentThread();
SwingUtilities.invokeLater(() -> {
try {
init();
} catch (Throwable th) {
testError[0] = th;
testThread.interrupt();
}
});
try {
if (!initialized.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for JFXPanel to launch and initialize");
}
Thread.sleep(SLEEP_TIME);
try {
SwingUtilities.invokeAndWait(() -> {
frame.setVisible(false);
frame.dispose();
});
}
catch (InvocationTargetException ex) {
AssertionFailedError err = new AssertionFailedError("Exception while disposing JFrame");
err.initCause(ex.getCause());
throw err;
}
final CountDownLatch exitLatch = PlatformImplShim.test_getPlatformExitLatch();
if (reEnableImplicitExit) {
Thread.sleep(SLEEP_TIME);
assertEquals(1, exitLatch.getCount());
assertFalse(Platform.isImplicitExit());
Platform.setImplicitExit(true);
assertTrue(Platform.isImplicitExit());
}
if (!appShouldExit) {
Thread.sleep(SLEEP_TIME);
assertEquals(1, exitLatch.getCount());
Platform.exit();
}
if (!exitLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Platform to exit");
}
} catch (InterruptedException ex) {
Util.throwError(testError[0]);
}
}
public void doTestImplicitExit() {
doTestCommon(true, false, true);
}
public void doTestExplicitExit() {
doTestCommon(false, false, false);
}
public void doTestExplicitExitReEnable() {
doTestCommon(false, true, true);
}
}
