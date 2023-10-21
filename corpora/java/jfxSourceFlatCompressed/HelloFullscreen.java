package hello;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
public class HelloFullscreen extends Application {
@Override public void start(final Stage stage) {
stage.setFullScreen(true);
stage.setTitle("Hello Fullscreen");
Group root = new Group();
Scene scene = new Scene(root, 600, 450);
scene.setFill(Color.LIGHTGREEN);
Rectangle rect = new Rectangle();
rect.setX(15);
rect.setY(20);
rect.setWidth(100);
rect.setHeight(50);
rect.setFill(Color.RED);
root.getChildren().add(rect);
Button button = new Button();
button.setText("Exit");
button.setLayoutX(15);
button.setLayoutY(100);
button.setOnAction(e -> Platform.exit());
root.getChildren().add(button);
Button button2 = new Button();
button2.setText("Close");
button2.setLayoutX(75);
button2.setLayoutY(100);
button2.setOnAction(e -> stage.hide());
root.getChildren().add(button2);
stage.setScene(scene);
stage.show();
}
public static void main(String[] args) {
Application.launch(args);
}
}
