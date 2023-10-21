package hello;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
public class HelloDeferSetVisible extends Application {
@Override public void start(final Stage stage) {
stage.setTitle("Hello Rectangle (defer setVisible)");
Group root = new Group();
final Scene scene = new Scene(root, 600, 450);
scene.setFill(Color.LIGHTGREEN);
Rectangle rect = new Rectangle();
rect.setX(25);
rect.setY(40);
rect.setWidth(100);
rect.setHeight(50);
rect.setFill(Color.RED);
root.getChildren().add(rect);
stage.setScene(scene);
new Thread(() -> {
System.err.println("Waiting to make stage visible");
try {
Thread.sleep(1000);
} catch (InterruptedException ex) {}
System.err.println("Now make stage visible!");
Platform.runLater(() -> stage.show());
}).start();
}
public static void main(String[] args) {
Application.launch(args);
}
}
