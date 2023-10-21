package ensemble.samples.graphics2d.shapes.cubiccurve;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.stage.Stage;
public class CubicCurveApp extends Application {
private CubicCurve cubicCurve = new CubicCurve();
public Parent createContent() {
Pane root = new Pane();
root.setPrefSize(245, 100);
root.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
cubicCurve.setStartX(0);
cubicCurve.setStartY(45);
cubicCurve.setControlX1(30);
cubicCurve.setControlY1(10);
cubicCurve.setControlX2(150);
cubicCurve.setControlY2(80);
cubicCurve.setEndX(180);
cubicCurve.setEndY(45);
cubicCurve.setStroke(Color.RED);
cubicCurve.setFill(Color.ROSYBROWN);
cubicCurve.setStrokeWidth(2d);
root.getChildren().add(cubicCurve);
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
