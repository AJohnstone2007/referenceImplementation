package colorcube;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.PerspectiveCamera;
import javafx.animation.Animation.Status;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import static javafx.scene.transform.Rotate.*;
public class JFXPanelColorCube {
private static final Color[] colors = {
Color.color(1, 0, 0),
Color.color(0, 1, 0),
Color.color(0, 0, 1),
Color.color(1, 1, 0),
Color.color(1, 0, 1),
Color.color(0, 1, 1)
};
private static final int SIZE = 300;
private static final float OFFSET = SIZE * 0.5F;
private static final float EXPLODED_OFFSET = SIZE * 0.6F;
private boolean exploded = false;
private static final int[] rotates = {
0, -90, -180, -270, -90, 90
};
private static final Point3D[] axes = {
Y_AXIS, Y_AXIS, Y_AXIS, Y_AXIS, X_AXIS, X_AXIS
};
private static final int[] translates = {
0, 0, -1,
1, 0, 0,
0, 0, 1,
-1, 0, 0,
0, -1, 0,
0, 1, 0
};
private Timeline timeline;
private Rectangle cubeFace(int i) {
Rectangle cube = new Rectangle(SIZE, SIZE, colors[i]);
cube.setTranslateX(translates[i * 3 + 0] * OFFSET);
cube.setTranslateY(translates[i * 3 + 1] * OFFSET);
cube.setTranslateZ(translates[i * 3 + 2] * OFFSET);
cube.setRotate(rotates[i]);
cube.setRotationAxis(axes[i]);
return cube;
}
public JFXPanelColorCube() {
JFrame frame = new JFrame("Hello JFXPanel");
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
frame.setLayout(new BorderLayout());
JPanel swingPanel = new JPanel();
swingPanel.setLayout(new FlowLayout());
frame.getContentPane().add(swingPanel, BorderLayout.NORTH);
JButton swingButton = new JButton("A Swing Button");
swingPanel.add(swingButton);
final JFXPanel fxPanel = new JFXPanel();
fxPanel.setPreferredSize(new Dimension(800,600));
frame.getContentPane().add(fxPanel, BorderLayout.CENTER);
createScene(fxPanel);
frame.pack();
frame.setLocationRelativeTo(null);
frame.setVisible(true);
}
private void createScene(final JFXPanel fxPanel) {
System.err.println("createScene: calling Platform.runLater");
Platform.runLater(new Runnable() {
public void run() {
System.err.println("Platform.runLater :: run");
final ArrayList<Node> cube = new ArrayList<Node>();
for (int i = 0; i < colors.length; i++) {
cube.add(cubeFace(i));
}
Group group = new Group(cube);
group.setTranslateX(250);
group.setTranslateY(150);
group.setRotate(30);
group.setRotationAxis(new Point3D(1, 1, 1));
final Group mainGroup = new Group(group);
mainGroup.setRotate(0);
mainGroup.setRotationAxis(Y_AXIS);
mainGroup.setOnKeyTyped(new EventHandler<KeyEvent>() {
@Override
public void handle(KeyEvent e) {
if (e.getCharacter().equals("x")) {
mainGroup.setRotationAxis(X_AXIS);
} else if (e.getCharacter().equals("y")) {
mainGroup.setRotationAxis(Y_AXIS);
} else if (e.getCharacter().equals("z")) {
mainGroup.setRotationAxis(Z_AXIS);
} else if (e.getCharacter().equals("e")) {
exploded = !exploded;
float offset = (exploded) ? (EXPLODED_OFFSET) : (OFFSET);
int i = 0;
for (Node face : cube) {
face.setTranslateX(translates[i * 3 + 0] * offset);
face.setTranslateY(translates[i * 3 + 1] * offset);
face.setTranslateZ(translates[i * 3 + 2] * offset);
++i;
}
} else if (e.getCharacter().equals("p")) {
if (timeline.getStatus() == Status.RUNNING) {
timeline.pause();
} else {
timeline.play();
}
}
}
});
Scene scene = new Scene(mainGroup, 800, 600, true);
PerspectiveCamera camera = new PerspectiveCamera();
camera.setFieldOfView(30);
scene.setCamera(camera);
LinearGradient sceneFill = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
new Stop[]{
new Stop(0, Color.web("#e0e0e0")),
new Stop(1, Color.web("#a0a0a0"))});
scene.setFill(sceneFill);
mainGroup.requestFocus();
KeyValue kv = new KeyValue(mainGroup.rotateProperty(), Float.valueOf(-360));
KeyFrame kf = new KeyFrame(Duration.seconds(4), kv);
timeline = new Timeline(kf);
timeline.setCycleCount(Timeline.INDEFINITE);
timeline.play();
fxPanel.setScene(scene);
}
});
System.err.println("PlatformImpl.runLater returns");
}
public static void main(String[] args) {
SwingUtilities.invokeLater(new Runnable() {
public void run() {
new JFXPanelColorCube();
}
});
}
}
