package ensemble.samples.animation.transitions.fadetransition;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
public class FadeTransitionApp extends Application {
private FadeTransition fade;
public Parent createContent() {
Pane root = new Pane();
root.setPrefSize(105, 105);
root.setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
root.setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
Rectangle rect = new Rectangle(0, 0, 100, 100);
rect.setArcHeight(20);
rect.setArcWidth(20);
rect.setFill(Color.DODGERBLUE);
root.getChildren().add(rect);
fade = new FadeTransition(Duration.seconds(4), rect);
fade.setFromValue(1);
fade.setToValue(0.2);
fade.setCycleCount(Timeline.INDEFINITE);
fade.setAutoReverse(true);
return root;
}
public void play() {
fade.play();
}
@Override
public void stop() {
fade.stop();
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
