package ensemble.samples.graphics2d.shapes.quadcurve;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.QuadCurve;
import javafx.stage.Stage;
public class QuadCurveApp extends Application {
QuadCurve quadCurve = new QuadCurve();
public Parent createContent() {
Pane root = new Pane();
root.setPrefSize(184, 100);
root.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
quadCurve.setStartX(0);
quadCurve.setStartY(45);
quadCurve.setControlX(50);
quadCurve.setControlY(10);
quadCurve.setEndX(180);
quadCurve.setEndY(45);
quadCurve.setStroke(Color.RED);
quadCurve.setFill(Color.ROSYBROWN);
quadCurve.setStrokeWidth(2d);
root.getChildren().add(quadCurve);
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
