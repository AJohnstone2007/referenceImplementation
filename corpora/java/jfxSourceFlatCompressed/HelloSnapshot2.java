package hello;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
public class HelloSnapshot2 extends Application {
@Override
public void start(Stage stage) {
stage.setTitle("HelloSnapshot2");
Group g = new Group();
Ellipse ellipse = new Ellipse(25, 20);
ellipse.setTranslateX(25);
ellipse.setTranslateY(25);
ellipse.setFill(Color.PALEGREEN);
g.getChildren().add(ellipse);
Group root = new Group(g);
final Scene ellipseScene = new Scene(root);
ellipseScene.setFill(Color.DARKBLUE);
SnapshotParameters params = new SnapshotParameters();
params.setCamera(new PerspectiveCamera());
params.setFill(Color.DARKBLUE);
params.setTransform(Transform.rotate(30, 25, 25));
final Image image2 = ellipse.snapshot(params, null);
final Image image1 = ellipseScene.snapshot(null);
Scene scene = new Scene(new Group(), 400, 300);
scene.setFill(Color.BROWN);
final HBox container = new HBox();
container.getChildren().add(new ImageView(image1));
container.getChildren().add(new ImageView(image2));
ellipse.snapshot(r -> {
System.err.println("callback: image = " + r.getImage()
+ "  source = " + r.getSource()
+ "  params = " + r.getSnapshotParameters());
container.getChildren().add(new ImageView(r.getImage()));
return null;
}, params, null);
params.setFill(Color.YELLOW);
ellipse.setStroke(Color.RED);
ellipse.setStrokeWidth(3);
scene.setRoot(container);
stage.setScene(scene);
stage.show();
}
public static void main(String[] args) {
javafx.application.Application.launch(args);
}
}
