package ensemble.samples.graphics2d.shapes.ellipse;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;
public class EllipseApp extends Application {
private Ellipse ellipse1 = new Ellipse(45, 45, 30, 45);
private Ellipse ellipse2 = new Ellipse(135, 45, 30, 45);
public Parent createContent() {
Pane root = new Pane();
root.setPrefSize(200, 100);
root.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
ellipse1.setFill(Color.RED);
ellipse2.setStroke(Color.DODGERBLUE);
ellipse2.setFill(null);
root.getChildren().addAll(ellipse1, ellipse2);
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
