package ensemble.samples.graphics2d.shapes.polygon;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
public class PolygonApp extends Application {
private Polygon polygon1 = new Polygon(new double[]{
45 , 10 ,
10 , 80 ,
80 , 80 ,
});
private Polygon polygon2 = new Polygon(new double[]{
135, 15,
160, 30,
160, 60,
135, 75,
110, 60,
110, 30
});
public Parent createContent() {
Pane root = new Pane();
root.setPrefSize(180, 100);
root.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
polygon1.setFill(Color.RED);
polygon2.setStroke(Color.DODGERBLUE);
polygon2.setStrokeWidth(2);
polygon2.setFill(null);
root.getChildren().addAll(polygon1, polygon2);
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
