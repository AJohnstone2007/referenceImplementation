package ensemble.samples.controls.progressindicator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
public class ProgressIndicatorApp extends Application {
final Timeline timeline = new Timeline();
public Parent createContent() {
GridPane gridPane = new GridPane();
ProgressIndicator p1 = new ProgressIndicator();
p1.setPrefSize(50, 50);
ProgressIndicator p2 = new ProgressIndicator(0.25);
p2.setPrefSize(50, 50);
ProgressIndicator p3 = new ProgressIndicator(0.5);
p3.setPrefSize(50, 50);
ProgressIndicator p4 = new ProgressIndicator(1.0);
p4.setPrefSize(50, 50);
final ProgressIndicator p5 = new ProgressIndicator();
p5.setPrefSize(100, 100);
p5.styleProperty().bind(Bindings.createStringBinding(
() -> {
final double percent = p5.getProgress();
if (percent < 0) {
return null;
}
final double m = (2d * percent);
final int n = (int) m;
final double f = m - n;
final int t = (int) (255 * f);
int r = 0, g = 0, b = 0;
switch (n) {
case 0:
r = 255;
g = t;
b = 0;
break;
case 1:
r = 255 - t;
g = 255;
b = 0;
break;
case 2:
r = 0;
g = 255;
b = 0;
break;
}
final String style =
String.format("-fx-progress-color: rgb(%d,%d,%d)",
r, g, b);
return style;
},
p5.progressProperty()
));
timeline.setCycleCount(Timeline.INDEFINITE);
timeline.setAutoReverse(true);
final KeyValue kv0 = new KeyValue(p5.progressProperty(), 0);
final KeyValue kv1 = new KeyValue(p5.progressProperty(), 1);
final KeyFrame kf0 = new KeyFrame(Duration.ZERO, kv0);
final KeyFrame kf1 = new KeyFrame(Duration.millis(3000), kv1);
timeline.getKeyFrames().addAll(kf0, kf1);
gridPane.add(p1, 1, 0);
gridPane.add(p2, 0, 1);
gridPane.add(p3, 1, 1);
gridPane.add(p4, 2, 1);
gridPane.add(p5, 1, 2);
gridPane.setHgap(20);
gridPane.setVgap(20);
gridPane.setAlignment(Pos.CENTER);
return gridPane;
}
public void play() {
timeline.play();
}
@Override
public void stop() {
timeline.stop();
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
play();
}
public static void main(String[] args) {
launch(args);
}
}
