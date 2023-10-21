package fx83dfeatures;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;
public class SimpleMovingCamera extends Application {
PointLight pointLight;
Sphere sphere;
PhongMaterial material;
PerspectiveCamera camera;
Group cameraGroup;
TranslateTransition transTrans;
double fovValue;
double rotateCamera = 0.0;
double translateCamera = 0.0;
private Scene buildScene() {
material = new PhongMaterial();
material.setDiffuseColor(Color.GOLD);
material.setSpecularColor(Color.rgb(30, 30, 30));
sphere = new Sphere(300);
sphere.setTranslateX(400);
sphere.setTranslateY(400);
sphere.setTranslateZ(20);
sphere.setMaterial(material);
sphere.setDrawMode(DrawMode.FILL);
pointLight = new PointLight(Color.ANTIQUEWHITE);
pointLight.setTranslateX(150);
pointLight.setTranslateY(-100);
pointLight.setTranslateZ(-1000);
camera = createCamera();
cameraGroup = new Group(camera);
Group root = new Group(sphere, pointLight, cameraGroup);
Scene scene = new Scene(root, 800, 800, true);
scene.setFill(Color.GRAY);
scene.setCamera(camera);
System.err.println("Camera FOV = " + (fovValue = camera.getFieldOfView()));
transTrans = new TranslateTransition(Duration.seconds(5), cameraGroup);
transTrans.setAutoReverse(true);
transTrans.setCycleCount(Timeline.INDEFINITE);
transTrans.setByZ(-400);
scene.setOnKeyTyped(e -> {
switch (e.getCharacter()) {
case "[":
fovValue -= 2.0;
if (fovValue < 10.0) {
fovValue = 10.0;
}
camera.setFieldOfView(fovValue);
break;
case "]":
fovValue += 2.0;
if (fovValue > 60.0) {
fovValue = 60.0;
}
camera.setFieldOfView(fovValue);
break;
case "r":
rotateCamera += 5.0;
if (rotateCamera > 360.0) {
rotateCamera = 0.0;
}
camera.setRotate(rotateCamera);
break;
case "t":
if (transTrans.getStatus() == Timeline.Status.RUNNING) {
transTrans.pause();
} else {
transTrans.play();
}
break;
}
});
return scene;
}
private PerspectiveCamera createCamera() {
PerspectiveCamera perspectiveCamera = new PerspectiveCamera(true);
perspectiveCamera.setTranslateX(400);
perspectiveCamera.setTranslateY(400);
perspectiveCamera.setTranslateZ(-1500);
perspectiveCamera.setFarClip(2000);
return perspectiveCamera;
}
@Override
public void start(Stage primaryStage) {
Scene scene = buildScene();
primaryStage.setTitle("SimpleMovingCamera");
primaryStage.setScene(scene);
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
