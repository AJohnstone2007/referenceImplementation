package ensemble.samples.graphics2d.shapes.circle;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
public class CircleApp extends Application {
private Circle circle1 = new Circle(45, 45, 40, Color.RED);
private Circle circle2 = new Circle(135, 45, 40);
public Parent createContent() {
Pane root = new Pane();
root.setPrefSize(245, 100);
root.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
circle2.setStroke(Color.DODGERBLUE);
circle2.setFill(null);
root.getChildren().addAll(circle1, circle2);
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
