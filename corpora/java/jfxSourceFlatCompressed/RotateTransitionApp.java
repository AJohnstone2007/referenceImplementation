package ensemble.samples.animation.transitions.rotatetransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
public class RotateTransitionApp extends Application {
private RotateTransition rotate;
public Parent createContent() {
Pane root = new Pane();
root.setPrefSize(140, 140);
root.setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
root.setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
Rectangle rect = new Rectangle(20, 20, 100, 100);
rect.setArcHeight(20);
rect.setArcWidth(20);
rect.setFill(Color.ORANGE);
root.getChildren().add(rect);
rotate = new RotateTransition(Duration.seconds(4), rect);
rotate.setFromAngle(0);
rotate.setToAngle(720);
rotate.setCycleCount(Timeline.INDEFINITE);
rotate.setAutoReverse(true);
return root;
}
public void play() {
rotate.play();
}
@Override
public void stop() {
rotate.stop();
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
