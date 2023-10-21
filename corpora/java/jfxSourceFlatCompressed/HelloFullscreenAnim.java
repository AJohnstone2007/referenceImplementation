package hello;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
public class HelloFullscreenAnim extends Application {
@Override public void start(Stage stage) {
stage.setTitle("Hello Animation");
stage.setFullScreen(true);
Rectangle2D screenBounds = Screen.getPrimary().getBounds();
Group root = new Group();
final Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
scene.setFill(Color.LIGHTGREEN);
final Rectangle rect1 = new Rectangle();
rect1.setX(25);
rect1.setY(200);
rect1.setWidth(100);
rect1.setHeight(50);
rect1.setFill(Color.BLUEVIOLET);
root.getChildren().add(rect1);
final Rectangle rect2 = new Rectangle();
rect2.setX(700);
rect2.setY(475);
rect2.setWidth(100);
rect2.setHeight(50);
rect2.setFill(Color.RED);
root.getChildren().add(rect2);
Button button = new Button();
button.setText("Exit");
button.setLayoutX(15);
button.setLayoutY(100);
button.setOnAction(e -> Platform.exit());
root.getChildren().add(button);
stage.setScene(scene);
stage.show();
final Timeline timeline = new Timeline();
timeline.setCycleCount(Timeline.INDEFINITE);
timeline.setAutoReverse(true);
final KeyValue kv1 = new KeyValue(rect1.xProperty(), 200);
final KeyValue kv2 = new KeyValue(rect2.xProperty(), 1100);
final KeyFrame kf = new KeyFrame(Duration.millis(1000), kv1, kv2);
timeline.getKeyFrames().add(kf);
timeline.play();
System.out.println(Double.POSITIVE_INFINITY * 2);
}
public static void main(String[] args) {
Application.launch(args);
}
}
