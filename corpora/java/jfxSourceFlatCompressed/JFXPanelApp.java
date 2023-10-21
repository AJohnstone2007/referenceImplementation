package test.sandbox.app;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import static test.sandbox.Constants.*;
public class JFXPanelApp {
private final AtomicBoolean createSceneDone = new AtomicBoolean(false);
private final AtomicReference<Throwable> err = new AtomicReference<>(null);
private void initApp(final boolean implicitExit) throws Exception {
final JFrame frame = new JFrame("JFXPanel Test");
frame.setLayout(new BorderLayout());
JPanel swingPanel = new JPanel();
swingPanel.setLayout(new FlowLayout());
frame.getContentPane().add(swingPanel, BorderLayout.NORTH);
JButton swingButton = new JButton("Swing Button");
swingButton.addActionListener(e -> System.err.println("Hi"));
swingPanel.add(swingButton);
final JFXPanel jfxPanel = new JFXPanel();
jfxPanel.setPreferredSize(new Dimension(400,300));
frame.getContentPane().add(jfxPanel, BorderLayout.CENTER);
createScene(jfxPanel);
if (!implicitExit) {
Platform.setImplicitExit(false);
}
frame.pack();
frame.setVisible(true);
Timer timer = new Timer(SHOWTIME, e -> {
if (!createSceneDone.get()) {
System.exit(ERROR_TIMEOUT);
}
Throwable t = err.get();
if (t != null) {
if (t instanceof SecurityException) {
System.exit(ERROR_SECURITY_EXCEPTION);
} else if (t instanceof ExceptionInInitializerError) {
Throwable cause = t.getCause();
if (cause instanceof SecurityException) {
System.exit(ERROR_SECURITY_EXCEPTION);
}
}
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
if (implicitExit) {
frame.setVisible(false);
frame.dispose();
} else {
System.exit(ERROR_NONE);
}
});
timer.setRepeats(false);
timer.start();
}
private void createScene(final JFXPanel jfxPanel) throws Exception {
Platform.runLater(() -> {
try {
final Scene scene = Util.createScene();
jfxPanel.setScene(scene);
} catch (Error | Exception t) {
t.printStackTrace();
err.set(t);
} finally {
createSceneDone.set(true);
}
});
}
public JFXPanelApp(boolean implicitExit) {
try {
try {
System.getProperty("sun.something");
System.err.println("*** Did not get expected security exception");
System.exit(ERROR_NO_SECURITY_EXCEPTION);
} catch (SecurityException ex) {
}
initApp(implicitExit);
} catch (SecurityException ex) {
ex.printStackTrace(System.err);
System.exit(ERROR_SECURITY_EXCEPTION);
} catch (ExceptionInInitializerError ex) {
Throwable cause = ex.getCause();
if (cause instanceof SecurityException) {
System.exit(ERROR_SECURITY_EXCEPTION);
}
System.exit(ERROR_UNEXPECTED_EXCEPTION);
} catch (Error | Exception ex) {
ex.printStackTrace(System.err);
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
}
public static void runTest(final boolean implicitExit) {
Util.setupTimeoutThread();
SwingUtilities.invokeLater(() -> new JFXPanelApp(implicitExit));
}
public static void main(String[] args) {
runTest(false);
}
}
