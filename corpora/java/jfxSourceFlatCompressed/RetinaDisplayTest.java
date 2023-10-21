package fx83dfeatures;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;
public class RetinaDisplayTest extends Application {
public static void main(String[] args) {
launch(args);
}
@Override
public void start(Stage primaryStage) {
primaryStage.setTitle("RetinaDisplayLightBug");
PhongMaterial material = new PhongMaterial();
material.setSpecularColor(Color.AQUA);
material.setSpecularPower(1.5);
final Sphere sphere = new Sphere(150);
sphere.setMaterial(material);
final Group parent = new Group(sphere);
parent.setTranslateX(200);
parent.setTranslateY(200);
final Group root = new Group();
root.getChildren().add(parent);
final Scene scene = new Scene(root, 400, 400, true);
scene.setFill(Color.BLACK);
PointLight pointLight = new PointLight(Color.WHITE);
pointLight.setTranslateX(200);
pointLight.setTranslateY(200);
pointLight.setTranslateZ(-1500);
scene.setCamera(new PerspectiveCamera(false));
root.getChildren().addAll(pointLight);
primaryStage.setScene(scene);
primaryStage.show();
}
}
