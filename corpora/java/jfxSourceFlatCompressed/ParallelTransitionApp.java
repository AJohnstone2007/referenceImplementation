package ensemble.samples.animation.transitions.paralleltransition;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
public class ParallelTransitionApp extends Application {
private ParallelTransition parallel;
public Parent createContent() {
final Pane root = new Pane();
root.setPrefSize(400, 200);
root.setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
root.setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
Rectangle rect = new Rectangle(-25,-25,50, 50);
rect.setArcHeight(15);
rect.setArcWidth(15);
rect.setFill(Color.CRIMSON);
rect.setTranslateX(50);
rect.setTranslateY(75);
root.getChildren().add(rect);
FadeTransition fade = new FadeTransition(Duration.seconds(3), rect);
fade.setFromValue(1);
fade.setToValue(0.3);
fade.setAutoReverse(true);
TranslateTransition translate =
new TranslateTransition(Duration.seconds(2));
translate.setFromX(50);
translate.setToX(320);
translate.setCycleCount(2);
translate.setAutoReverse(true);
RotateTransition rotate = new RotateTransition(Duration.seconds(3));
rotate.setByAngle(180);
rotate.setCycleCount(4);
rotate.setAutoReverse(true);
ScaleTransition scale = new ScaleTransition(Duration.seconds(2));
scale.setToX(2);
scale.setToY(2);
scale.setCycleCount(2);
scale.setAutoReverse(true);
parallel = new ParallelTransition(rect,
fade, translate, rotate, scale);
parallel.setCycleCount(Timeline.INDEFINITE);
parallel.setAutoReverse(true);
return root;
}
public void play() {
parallel.play();
}
@Override
public void stop() {
parallel.stop();
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
