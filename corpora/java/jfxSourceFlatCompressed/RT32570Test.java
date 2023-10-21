package test.robot.javafx.embed.swing;
import java.awt.Dimension;
import javafx.embed.swing.SwingNode;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.junit.Ignore;
import org.junit.Test;
import test.robot.testharness.VisualTestBase;
public class RT32570Test extends VisualTestBase {
private static final double TOLERANCE = 0.07;
private static final int WIDTH = 100;
private static final int HEIGHT = 50;
private volatile SwingNode swingNode;
private Scene testScene;
private JPopupMenu popup;
private JLabel label;
private volatile boolean popped;
@Ignore("JDK-8153542")
@Test(timeout = 15000)
public void test() throws Exception {
runAndWait(() -> {
swingNode = new SwingNode();
Group group = new Group();
group.getChildren().add(swingNode);
testScene = new Scene(group, WIDTH, HEIGHT);
Stage stage = getStage();
stage.setScene(testScene);
stage.show();
});
SwingUtilities.invokeAndWait(() -> {
label = new JLabel();
label.setMinimumSize(new Dimension(WIDTH, HEIGHT));
label.setBackground(java.awt.Color.GREEN);
label.setOpaque(true);
popup = new JPopupMenu();
JMenuItem item = new JMenuItem();
item.setPreferredSize(new Dimension(WIDTH, HEIGHT));
item.setBackground(java.awt.Color.RED);
popup.add(item);
swingNode.setContent(label);
});
waitFirstFrame();
SwingUtilities.invokeAndWait(() -> popup.show(label, 0, 0));
SwingUtilities.invokeAndWait(() -> popup.setVisible(false));
SwingUtilities.invokeAndWait(() -> popup.show(label, 0, 0));
while (!popped) {
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 2, HEIGHT / 2);
popped = !testColorEquals(Color.GREEN, color, TOLERANCE);
});
try { Thread.sleep(100); } catch(Exception e) {}
}
runAndWait(() -> {
Color color = getColor(testScene, WIDTH / 2, HEIGHT / 2);
assertColorEquals(Color.RED, color, TOLERANCE);
});
}
}
