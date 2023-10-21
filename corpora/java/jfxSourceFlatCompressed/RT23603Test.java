package test.robot.javafx.embed.swing;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Robot;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.animation.AnimationTimer;
import javafx.embed.swing.JFXPanel;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.Ignore;
import test.util.Util;
@Ignore("RT-29515")
public class RT23603Test {
volatile JFrame frame;
final CountDownLatch l1 = new CountDownLatch(2);
@Test
public void test() {
SwingUtilities.invokeLater(this::initAndShowGUI);
waitForLatch(l1, 5000);
Util.sleep(1000);
final CountDownLatch l2 = new CountDownLatch(3);
com.sun.javafx.tk.Toolkit.getToolkit().addSceneTkPulseListener(l2::countDown);
waitForLatch(l2, 5000);
Robot r = null;
try {
r = new Robot();
} catch (AWTException ex) {
Assert.fail("unexpected error: couldn't create java.awt.Robot: " + ex);
}
Point pt = frame.getLocationOnScreen();
Color color = r.getPixelColor(pt.x + 100, pt.y + 100);
Assert.assertEquals(color, Color.GREEN);
}
private void waitForLatch(CountDownLatch latch, long ms) {
try {
latch.await(ms, TimeUnit.MILLISECONDS);
} catch (InterruptedException e) {
}
if (latch.getCount() > 0) {
Assert.fail("unexpected error: waiting timeout " + ms + "ms elapsed for " + latch);
}
}
public void initAndShowGUI() {
frame = new JFrame("RT23603");
final JFXPanel fxPanel = new JFXPanel();
Platform.runLater(() -> {
Region rgn = new Region();
Scene scene = new Scene(rgn);
rgn.setStyle("-fx-background-color: #00ff00;");
fxPanel.setScene(scene);
new AnimationTimer() {
@Override public void handle(long l) {}
}.start();
l1.countDown();
});
frame.getContentPane().setBackground(java.awt.Color.RED);
frame.getContentPane().setPreferredSize(new Dimension(400, 300));
frame.pack();
fxPanel.setSize(400, 300);
frame.getContentPane().add(fxPanel);
frame.getContentPane().remove(fxPanel);
frame.getContentPane().add(fxPanel);
frame.setVisible(true);
l1.countDown();
}
public static void main(String[] args) {
new RT23603Test().test();
}
}
