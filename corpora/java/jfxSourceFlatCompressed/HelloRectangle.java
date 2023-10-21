package hello;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
public class HelloRectangle extends Application {
@Override public void start(Stage stage) {
stage.setTitle("Hello Rectangle");
Group root = new Group();
Scene scene = new Scene(root, 600, 450);
scene.setFill(Color.LIGHTGREEN);
Rectangle rect = new Rectangle();
rect.setX(25);
rect.setY(40);
rect.setWidth(100);
rect.setHeight(50);
rect.setFill(Color.RED);
rect.setOnMousePressed(e -> System.out.println("Mouse Pressed:" + e));
root.getChildren().add(rect);
stage.setScene(scene);
stage.show();
}
public static void main(String[] args) {
Application.launch(args);
}
}
