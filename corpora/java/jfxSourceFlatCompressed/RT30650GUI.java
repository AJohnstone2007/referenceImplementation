package test.robot.javafx.embed.swing;
import static com.sun.javafx.application.PlatformImpl.runAndWait;
import com.sun.javafx.tk.TKPulseListener;
import java.awt.Color;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javax.swing.SwingUtilities;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javax.swing.JPanel;
public class RT30650GUI extends Application {
SwingNode swingNode;
private final static int SIZE = 400;
private static int pulseCount = 400;
private static volatile boolean passed;
public static boolean test() {
launch(new String[]{});
return passed;
}
private AnimationTimer animationTimer;
private TKPulseListener pulseListener;
@Override
public void start(final Stage stage) {
animationTimer = new AnimationTimer() {
@Override public void handle(long l) {}
};
animationTimer.start();
swingNode = new SwingNode();
Pane pane = new StackPane();
pane.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.GREEN, null, null)));
pane.getChildren().add(swingNode);
Scene scene = new Scene(pane, SIZE, SIZE);
stage.setScene(scene);
stage.setTitle("RT-30650");
stage.show();
SwingUtilities.invokeLater(() -> {
JPanel panel = new JPanel();
panel.setBackground(Color.RED);
swingNode.setContent(panel);
Platform.runLater(() -> {
pulseListener = () -> {
if (--pulseCount == 0) {
SwingUtilities.invokeLater(() -> {
passed = testColor(stage);
Platform.runLater(stage::close);
});
}
};
com.sun.javafx.tk.Toolkit.getToolkit().addSceneTkPulseListener(pulseListener);
});
});
}
public boolean testColor(Stage stage) {
int x = (int)stage.getX();
int y = (int)stage.getY();
final javafx.scene.paint.Color rgb[] = new javafx.scene.paint.Color[1];
runAndWait(() -> {
Robot r = new Robot();
rgb[0] = r.getPixelColor(x + SIZE/2, y + SIZE/2);
});
System.out.println("detected color: " + rgb[0].toString());
return rgb[0].getRed() * 255d > 200 && rgb[0].getGreen() * 255d < 100;
}
}
