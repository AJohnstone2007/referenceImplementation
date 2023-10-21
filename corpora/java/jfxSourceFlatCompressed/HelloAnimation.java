package hello;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
public class HelloAnimation extends Application {
@Override public void start(Stage stage) {
stage.setTitle("Hello Animation");
final Scene scene = new Scene(new Group(), 600, 450);
scene.setFill(Color.LIGHTGREEN);
final Rectangle rect = new Rectangle();
rect.setX(25);
rect.setY(40);
rect.setWidth(100);
rect.setHeight(50);
rect.setFill(Color.RED);
((Group)scene.getRoot()).getChildren().add(rect);
stage.setScene(scene);
stage.show();
final Timeline timeline = new Timeline();
timeline.setCycleCount(Timeline.INDEFINITE);
timeline.setAutoReverse(true);
final KeyValue kv = new KeyValue(rect.xProperty(), 200);
final KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
timeline.getKeyFrames().add(kf);
timeline.play();
System.out.println(Double.POSITIVE_INFINITY * 2);
}
public static void main(String[] args) {
Application.launch(args);
}
}
