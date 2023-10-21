package hello;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
public class HelloFullscreenToggle extends Application {
@Override public void start(final Stage stage) {
stage.setTitle("Hello Fullscreen Toggle");
Group root = new Group();
Scene scene = new Scene(root, 600, 450);
scene.setFill(Color.LIGHTBLUE);
Button button = new Button();
button.setText("Toggle Fullscreen");
button.setLayoutX(25);
button.setLayoutY(40);
button.setOnAction(e -> stage.setFullScreen(!stage.isFullScreen()));
root.getChildren().add(button);
stage.setScene(scene);
stage.show();
}
public static void main(String[] args) {
Application.launch(args);
}
}
