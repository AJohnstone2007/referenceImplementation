package ensemble.samples.animation.timelineevents;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
public class TimelineEventsApp extends Application {
private Timeline timeline;
private AnimationTimer timer;
private int frameCount = 0;
public Parent createContent() {
final Circle circle = new Circle(20, Color.rgb(156,216,255));
circle.setEffect(new Lighting());
final Text text = new Text(Integer.toString(frameCount));
text.setStroke(Color.BLACK);
final StackPane stack = new StackPane();
stack.getChildren().addAll(circle, text);
stack.setLayoutX(30);
stack.setLayoutY(30);
final Pane pane = new Pane(stack);
pane.setPrefSize(300, 100);
pane.setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
timeline = new Timeline();
timeline.setCycleCount(Timeline.INDEFINITE);
timeline.setAutoReverse(true);
timer = new AnimationTimer() {
@Override
public void handle(long l) {
text.setText(String.format("%d", frameCount++));
}
};
KeyValue keyValueX = new KeyValue(stack.scaleXProperty(), 2);
KeyValue keyValueY = new KeyValue(stack.scaleYProperty(), 2);
Duration duration = Duration.seconds(2);
EventHandler<ActionEvent> onFinished = (ActionEvent t) -> {
stack.setTranslateX(java.lang.Math.random() * 200);
frameCount = 0;
};
KeyFrame keyFrame = new KeyFrame(duration, onFinished,
keyValueX, keyValueY);
timeline.getKeyFrames().add(keyFrame);
return pane;
}
public void play() {
timeline.play();
timer.start();
}
@Override public void stop() {
timeline.stop();
timer.stop();
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setResizable(false);
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
play();
}
public static void main(String[] args) {
launch(args);
}
}
