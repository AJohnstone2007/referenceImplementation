package picktest;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
public class PickTestFixedEye extends Application {
double ax, ay;
@Override public void start(Stage stage) {
stage.setTitle("Pick test fixed eye");
Group root = new Group();
Scene scene = new Scene(root, 600, 450);
scene.setFill(Color.LIGHTGREEN);
final PerspectiveCamera cam = new PerspectiveCamera(true);
scene.setCamera(cam);
PhongMaterial pm = new PhongMaterial();
pm.setDiffuseColor(Color.RED);
pm.setSpecularColor(Color.ORANGE);
final Box b = new Box(20, 20, 20);
b.setMaterial(pm);
b.setTranslateZ(70);
b.setRotationAxis(Rotate.Y_AXIS);
b.setRotate(25);
root.getChildren().add(b);
scene.setOnMousePressed(new EventHandler<MouseEvent>() {
@Override public void handle(MouseEvent event) {
ay = event.getSceneY();
}
});
scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
@Override public void handle(MouseEvent event) {
b.setTranslateZ(b.getTranslateZ() + (event.getSceneY() - ay) / 3);
ay = event.getSceneY();
}
});
b.setOnMouseEntered(new EventHandler<MouseEvent>() {
@Override public void handle(MouseEvent event) {
((PhongMaterial) b.getMaterial()).setDiffuseColor(Color.YELLOW);
((PhongMaterial) b.getMaterial()).setSpecularColor(Color.WHITE);
}
});
b.setOnMouseExited(new EventHandler<MouseEvent>() {
@Override public void handle(MouseEvent event) {
((PhongMaterial) b.getMaterial()).setDiffuseColor(Color.RED);
((PhongMaterial) b.getMaterial()).setSpecularColor(Color.ORANGE);
}
});
stage.setScene(scene);
stage.show();
}
public static void main(String[] args) {
Application.launch(args);
}
}
