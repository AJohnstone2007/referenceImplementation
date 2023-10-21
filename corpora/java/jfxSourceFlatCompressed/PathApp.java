package ensemble.samples.graphics2d.shapes.path;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.VLineTo;
import javafx.stage.Stage;
public class PathApp extends Application {
private Path path1 = new Path();
private Path path2 = new Path();
public Parent createContent() {
Pane root = new Pane();
root.setPrefSize(245, 100);
root.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
path1.getElements().addAll(
new MoveTo(25, 25),
new HLineTo(65),
new VLineTo(65),
new LineTo(25, 65),
new ClosePath());
path1.setFill(null);
path1.setStroke(Color.RED);
path1.setStrokeWidth(2);
path2.getElements().addAll(
new MoveTo(100, 45),
new CubicCurveTo(120, 20, 130, 80, 140, 45),
new QuadCurveTo(150, 0, 160, 45),
new ArcTo(20, 40, 0, 180, 45, true, true));
path2.setFill(null);
path2.setStroke(Color.DODGERBLUE);
path2.setStrokeWidth(2);
path2.setTranslateY(36);
root.getChildren().addAll(path1, path2);
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
