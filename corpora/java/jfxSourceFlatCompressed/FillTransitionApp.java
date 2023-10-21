package ensemble.samples.animation.transitions.filltransition;
import javafx.animation.FillTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
public class FillTransitionApp extends Application {
private FillTransition fill;
public Parent createContent() {
final Pane root = new Pane();
root.setPrefSize(105, 105);
root.setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
root.setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
Rectangle rect = new Rectangle(0, 0, 100, 100);
rect.setArcHeight(20);
rect.setArcWidth(20);
rect.setFill(Color.DODGERBLUE);
root.getChildren().add(rect);
fill = new FillTransition(Duration.seconds(3), rect,
Color.RED, Color.DODGERBLUE);
fill.setCycleCount(Timeline.INDEFINITE);
fill.setAutoReverse(true);
return root;
}
public void play() {
fill.play();
}
@Override
public void stop() {
fill.stop();
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
