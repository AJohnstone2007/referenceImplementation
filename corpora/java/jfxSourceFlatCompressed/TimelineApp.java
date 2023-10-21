package ensemble.samples.animation.timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
public class TimelineApp extends Application {
private Timeline timeline;
public Parent createContent() {
final Pane root = new Pane();
root.setPrefSize(253, 100);
root.setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
root.setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
final Circle circle = new Circle(25, 25, 20, Color.web("1c89f4"));
circle.setEffect(new Lighting());
timeline = new Timeline();
timeline.setCycleCount(Timeline.INDEFINITE);
timeline.setAutoReverse(true);
timeline.getKeyFrames().addAll(new KeyFrame(Duration.ZERO,
new KeyValue(circle.translateXProperty(), 0)),
new KeyFrame(new Duration(4000),
new KeyValue(circle.translateXProperty(), 205)));
root.getChildren().addAll(createNavigation(), circle);
return root;
}
private VBox createNavigation() {
Button buttonStart = new Button("Start");
buttonStart.setOnAction((ActionEvent t) -> {
timeline.play();
});
Button buttonStop = new Button("Stop");
buttonStop.setOnAction((ActionEvent t) -> {
timeline.stop();
});
Button buttonPlayFromStart = new Button("Restart");
buttonPlayFromStart.setOnAction((ActionEvent t) -> {
timeline.playFromStart();
});
Button buttonPause = new Button("Pause");
buttonPause.setOnAction((ActionEvent t) -> {
timeline.pause();
});
final TextFlow flow = new TextFlow();
final Text current = new Text("Current time: ");
final Text rate = new Text();
final Text ms = new Text(" ms");
current.setBoundsType(TextBoundsType.VISUAL);
ms.setBoundsType(TextBoundsType.VISUAL);
rate.setFont(Font.font("Courier", FontWeight.BOLD, 14));
rate.setText(String.format("%4d", 0));
timeline.currentTimeProperty().addListener((Observable ov) -> {
rate.setText(String.format("%4.0f",
timeline.getCurrentTime().toMillis()));
flow.requestLayout();
});
flow.getChildren().addAll(current, rate, ms);
final CheckBox checkBoxAutoReverse = new CheckBox("Auto Reverse");
checkBoxAutoReverse.setSelected(true);
checkBoxAutoReverse.selectedProperty().addListener((Observable ov) -> {
timeline.setAutoReverse(checkBoxAutoReverse.isSelected());
});
HBox hBox1 = new HBox(10);
hBox1.setPadding(new Insets(5, 10, 0, 5));
hBox1.getChildren().addAll(buttonStart, buttonPause,
buttonStop, buttonPlayFromStart);
hBox1.setAlignment(Pos.CENTER_LEFT);
VBox controls = new VBox(10);
controls.setPadding(new Insets(0, 0, 0, 5));
controls.getChildren().addAll(checkBoxAutoReverse, flow);
controls.setAlignment(Pos.CENTER_LEFT);
VBox vBox = new VBox(20);
vBox.setLayoutY(60);
vBox.getChildren().addAll(hBox1, controls);
return vBox;
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setResizable(false);
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
