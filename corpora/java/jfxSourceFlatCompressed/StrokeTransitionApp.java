package ensemble.samples.animation.transitions.stroketransition;
import javafx.animation.StrokeTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
public class StrokeTransitionApp extends Application {
private StrokeTransition stroke;
public Parent createContent() {
final Pane root = new Pane();
root.setPrefSize(200, 200);
root.setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
root.setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
Rectangle rect = new Rectangle(0, 0, 150, 150);
rect.setArcHeight(20);
rect.setArcWidth(20);
rect.setFill(null);
rect.setStroke(Color.DODGERBLUE);
rect.setStrokeWidth(10);
root.getChildren().add(rect);
stroke = new StrokeTransition(Duration.seconds(3), rect,
Color.RED, Color.DODGERBLUE);
stroke.setCycleCount(Timeline.INDEFINITE);
stroke.setAutoReverse(true);
return root;
}
public void play() {
stroke.play();
}
@Override
public void stop() {
stroke.stop();
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
