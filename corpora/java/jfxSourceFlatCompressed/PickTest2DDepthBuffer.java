package picktest;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
public class PickTest2DDepthBuffer extends Application {
public static void main(String[] args) {
launch(args);
}
@Override
public void start(Stage primaryStage) {
primaryStage.setTitle("Detph buffer 2D picking");
final Rectangle red = new Rectangle(100, 100, 200, 200);
red.setFill(Color.RED);
red.setOnMouseEntered(new EventHandler<MouseEvent>() {
@Override public void handle(MouseEvent event) {
red.setFill(Color.ORANGE);
}
});
red.setOnMouseExited(new EventHandler<MouseEvent>() {
@Override public void handle(MouseEvent event) {
red.setFill(Color.RED);
}
});
final Rectangle blue = new Rectangle(200, 200, 200, 200);
blue.setFill(Color.BLUE);
blue.setOnMouseEntered(new EventHandler<MouseEvent>() {
@Override public void handle(MouseEvent event) {
blue.setFill(Color.LIGHTBLUE);
}
});
blue.setOnMouseExited(new EventHandler<MouseEvent>() {
@Override public void handle(MouseEvent event) {
blue.setFill(Color.BLUE);
}
});
red.setTranslateZ(-1);
Group root = new Group(red, blue);
Scene scene = new Scene(root, 500, 500, true);
root.setDepthTest(DepthTest.ENABLE);
scene.setCamera(new PerspectiveCamera());
primaryStage.setScene(scene);
primaryStage.show();
}
}
