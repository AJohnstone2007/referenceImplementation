package hello;
import java.util.Optional;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
public class HelloAlert extends Application {
@Override public void start(final Stage stage) {
stage.setTitle("Alert Test");
Group root = new Group();
Scene scene = new Scene(root, 600, 450);
scene.setFill(Color.ANTIQUEWHITE);
final Rectangle rect = new Rectangle();
rect.setX(100);
rect.setY(40);
rect.setWidth(100);
rect.setHeight(50);
rect.setFill(Color.RED);
root.getChildren().add(rect);
final Timeline timeline = new Timeline();
timeline.setCycleCount(Timeline.INDEFINITE);
timeline.setAutoReverse(true);
final KeyValue kv = new KeyValue(rect.xProperty(), 200);
final KeyFrame kf = new KeyFrame(Duration.millis(4000), kv);
timeline.getKeyFrames().add(kf);
timeline.play();
final Button button1 = new Button();
button1.setText("Toggle color");
button1.setLayoutX(25);
button1.setLayoutY(40);
button1.setOnAction(e -> {
Alert alert = new Alert(AlertType.CONFIRMATION,
"Really toggle the color?");
alert.setTitle("Verify Change");
alert.initOwner(stage);
ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
if (result == ButtonType.OK) {
Color newColor = Color.RED.equals(rect.getFill())
? Color.GREEN : Color.RED;
rect.setFill(newColor);
} else {
System.err.println("Color change canceled");
}
});
root.getChildren().add(button1);
final Button button2 = new Button();
button2.setText("Question");
button2.setLayoutX(25);
button2.setLayoutY(80);
button2.setOnAction(e -> {
Alert alert = new Alert(AlertType.CONFIRMATION,
"How about those Giants?",
ButtonType.YES, ButtonType.NO);
alert.setTitle("SF Giants");
alert.initOwner(stage);
ButtonType result = alert.showAndWait().get();
if (result == ButtonType.YES) {
System.err.println("Good answer");
} else {
System.err.println("What do you mean 'NO' ???");
}
});
root.getChildren().add(button2);
final Button button3 = new Button();
button3.setText("Dialog");
button3.setLayoutX(25);
button3.setLayoutY(120);
button3.setOnAction(e -> {
Alert alert = new Alert(AlertType.INFORMATION,
"Hi, I'll be your modal dialog today");
alert.initOwner(stage);
alert.showAndWait();
System.err.println("Continue");
});
root.getChildren().add(button3);
final Button button4 = new Button();
button4.setText("Name");
button4.setLayoutX(25);
button4.setLayoutY(160);
button4.setOnAction(e -> {
TextInputDialog dlg = new TextInputDialog("Mud");
dlg.setTitle("What is your name?");
dlg.initOwner(stage);
Optional<String> result = dlg.showAndWait();
if (result.isPresent()) {
System.err.println("Your name is: " + result.get());
} else {
System.err.println("Canceled");
}
});
root.getChildren().add(button4);
final Button button5 = new Button();
button5.setText("Abort/Retry/Fail");
button5.setLayoutX(25);
button5.setLayoutY(200);
button5.setOnAction(e -> {
ButtonType abort = new ButtonType("Abort");
ButtonType retry = new ButtonType("Retry");
ButtonType fail = new ButtonType("Fail");
Alert alert = new Alert(AlertType.ERROR, "So sorry for you!",
abort, retry, fail);
alert.setTitle("You lose");
alert.initOwner(stage);
ButtonType result = alert.showAndWait().orElse(fail);
if (result == abort) {
System.err.println("ABORT! This is very, very bad!");
} else if (result == retry) {
System.err.println("RETRY? You're joking, right?");
} else if (result == fail) {
System.err.println("FAIL! So what else is new?");
}
});
root.getChildren().add(button5);
stage.setScene(scene);
stage.show();
}
public static void main(String[] args) {
Application.launch(args);
}
}
