package ensemble.samples.animation.transitions.translatetransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
public class TranslateTransitionApp extends Application {
private TranslateTransition translate;
public Parent createContent() {
final Pane root = new Pane();
root.setPrefSize(245, 80);
root.setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
root.setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
Circle circle = new Circle(20, Color.CRIMSON);
circle.setTranslateX(20);
circle.setTranslateY(20);
root.getChildren().add(circle);
translate = new TranslateTransition(Duration.seconds(4), circle);
translate.setFromX(20);
translate.setToX(220);
translate.setCycleCount(Timeline.INDEFINITE);
translate.setAutoReverse(true);
return root;
}
public void play() {
translate.play();
}
@Override
public void stop() {
translate.stop();
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
