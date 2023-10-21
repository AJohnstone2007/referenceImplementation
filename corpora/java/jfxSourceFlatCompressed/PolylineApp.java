package ensemble.samples.graphics2d.shapes.polyline;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;
public class PolylineApp extends Application {
Polyline polyline1 = new Polyline(new double[]{
45, 10,
10, 80,
80, 80,});
Polyline polyline2 = new Polyline(new double[]{
135, 10,
100, 80,
170, 80,
135, 10,});
public Parent createContent() {
Pane root = new Pane();
root.setPrefSize(184, 100);
root.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
polyline1.setFill(Color.TRANSPARENT);
polyline1.setStroke(Color.RED);
polyline2.setStroke(Color.DODGERBLUE);
polyline2.setStrokeWidth(2);
polyline2.setFill(null);
root.getChildren().addAll(polyline1, polyline2);
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
