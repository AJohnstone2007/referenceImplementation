package hello;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
public class HelloButton extends Application {
public static void main(String[] args) {
Application.launch(args);
}
@Override public void start(Stage stage) {
stage.setTitle("Hello Button");
Scene scene = new Scene(new Group(), 600, 450);
Button button1 = new Button();
button1.setText("Click Me");
button1.setLayoutX(25);
button1.setLayoutY(40);
button1.setOnAction(e -> System.out.println("Event: " + e));
((Group)scene.getRoot()).getChildren().add(button1);
Button button2 = new Button();
button2.setText("Click Me Too");
button2.setLayoutX(25);
button2.layoutYProperty().bind(button1.heightProperty().add(button1.layoutYProperty()));
((Group)scene.getRoot()).getChildren().add(button2);
stage.setScene(scene);
stage.show();
}
}
