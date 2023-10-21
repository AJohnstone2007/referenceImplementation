package ensemble.samples.graphics2d.shapes.rectangle;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
public class RectangleApp extends Application {
Rectangle rect1 = new Rectangle(25, 25, 40, 40);
Rectangle rect2 = new Rectangle(135, 25, 40, 40);
public Parent createContent() {
Pane root = new Pane();
root.setPrefSize(200, 100);
root.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
rect1.setFill(Color.RED);
rect2.setStroke(Color.DODGERBLUE);
rect2.setFill(null);
root.getChildren().addAll(rect1, rect2);
return root;
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
