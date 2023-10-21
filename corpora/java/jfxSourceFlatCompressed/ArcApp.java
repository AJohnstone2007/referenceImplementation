package ensemble.samples.graphics2d.shapes.arc;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.stage.Stage;
public class ArcApp extends Application {
private Arc arc1 = new Arc(45, 60, 45, 45, 40, 100);
private Arc arc2 = new Arc(155, 60, 45, 45, 40, 100);
public Parent createContent() {
Pane root = new Pane();
root.setPrefSize(245, 100);
root.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
arc1.setFill(Color.RED);
arc2.setStroke(Color.DODGERBLUE);
arc2.setFill(null);
root.getChildren().addAll(arc1, arc2);
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
