package fx83dfeatures;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;
public class Snapshot3D extends Application {
private Sphere sphere;
private Scene buildScene() {
PointLight pointLight;
PhongMaterial material;
material = new PhongMaterial();
material.setDiffuseColor(Color.WHITE);
material.setSpecularColor(null);
sphere = new Sphere(150);
sphere.setTranslateX(200);
sphere.setTranslateY(200);
sphere.setTranslateZ(10);
sphere.setMaterial(material);
sphere.setDrawMode(DrawMode.FILL);
pointLight = new PointLight(Color.PALEGREEN);
pointLight.setTranslateX(75);
pointLight.setTranslateY(-50);
pointLight.setTranslateZ(-200);
PerspectiveCamera camera = new PerspectiveCamera(true);
camera.setTranslateX(400);
camera.setTranslateY(200);
camera.setTranslateZ(-750);
camera.setFarClip(2000);
Group cameraGroup = new Group(camera);
Group root = new Group(sphere, pointLight, cameraGroup);
Scene scene = new Scene(root, 800, 400, true);
scene.setFill(Color.GRAY);
scene.setCamera(camera);
return scene;
}
@Override
public void start(Stage primaryStage) {
primaryStage.setTitle("Snapshot 3D");
Scene scene = buildScene();
primaryStage.setScene(scene);
primaryStage.show();
Group root = (Group)scene.getRoot();
Image image = scene.snapshot(null);
ImageView iv = new ImageView(image);
iv.setLayoutX(400);
root.getChildren().add(iv);
}
public static void main(String[] args) {
Application.launch(args);
}
}
