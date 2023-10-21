package ensemble.samples.graphics3d.simple3dbox;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
public class Simple3DBoxApp extends Application {
private Box testBox;
private PhongMaterial material;
public Parent createContent() throws Exception {
testBox = new Box(5, 5, 5);
material = new PhongMaterial(Color.RED);
testBox.setMaterial(material);
PerspectiveCamera camera = new PerspectiveCamera(true);
camera.getTransforms().addAll(
new Rotate(-20, Rotate.Y_AXIS),
new Rotate(-20, Rotate.X_AXIS),
new Translate(0, 0, -15));
Group root = new Group();
root.getChildren().add(camera);
root.getChildren().add(testBox);
SubScene subScene = new SubScene(root, 300, 300,
true, SceneAntialiasing.BALANCED);
subScene.setFill(Color.TRANSPARENT);
subScene.setCamera(camera);
return new Group(subScene);
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setResizable(false);
Scene scene = new Scene(createContent());
primaryStage.setScene(scene);
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
