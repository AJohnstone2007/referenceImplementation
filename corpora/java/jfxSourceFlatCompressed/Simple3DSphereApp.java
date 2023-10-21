package ensemble.samples.graphics3d.sphere;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;
public class Simple3DSphereApp extends Application {
private Sphere earth;
private PhongMaterial material;
private PointLight sun;
private final DoubleProperty sunDistance = new SimpleDoubleProperty(100);
public final DoubleProperty sunDistanceProperty() {
return sunDistance;
}
private final BooleanProperty sunLight = new SimpleBooleanProperty(true);
public final BooleanProperty sunLightProperty() {
return sunLight;
}
private final BooleanProperty diffuseMap = new SimpleBooleanProperty(true);
public final BooleanProperty diffuseMapProperty() {
return diffuseMap;
}
private final BooleanProperty specularMap = new SimpleBooleanProperty(true);
public final BooleanProperty specularMapProperty() {
return specularMap;
}
private final BooleanProperty bumpMap = new SimpleBooleanProperty(true);
public final BooleanProperty bumpMapProperty() {
return bumpMap;
}
private final BooleanProperty selfIlluminationMap = new SimpleBooleanProperty(true);
public final BooleanProperty selfIlluminationMapProperty() {
return selfIlluminationMap;
}
public Parent createContent() throws Exception {
String base = "/ensemble/samples/graphics3d/sphere/earth-";
Image dImage = new Image(getClass().getResource(base + "d.jpg").toString());
Image nImage = new Image(getClass().getResource(base + "n.jpg").toString());
Image sImage = new Image(getClass().getResource(base + "s.jpg").toString());
Image siImage = new Image(getClass().getResource(base + "l.jpg").toString());
material = new PhongMaterial();
material.setDiffuseColor(Color.WHITE);
material.diffuseMapProperty().bind(
Bindings.when(diffuseMap).then(dImage).otherwise((Image) null));
material.setSpecularColor(Color.TRANSPARENT);
material.specularMapProperty().bind(
Bindings.when(specularMap).then(sImage).otherwise((Image) null));
material.bumpMapProperty().bind(
Bindings.when(bumpMap).then(nImage).otherwise((Image) null));
material.selfIlluminationMapProperty().bind(
Bindings.when(selfIlluminationMap).then(siImage).otherwise((Image) null));
earth = new Sphere(5);
earth.setMaterial(material);
earth.setRotationAxis(Rotate.Y_AXIS);
PerspectiveCamera camera = new PerspectiveCamera(true);
camera.getTransforms().addAll(
new Rotate(-20, Rotate.Y_AXIS),
new Rotate(-20, Rotate.X_AXIS),
new Translate(0, 0, -20));
sun = new PointLight(Color.rgb(255, 243, 234));
sun.translateXProperty().bind(sunDistance.multiply(-0.82));
sun.translateYProperty().bind(sunDistance.multiply(-0.41));
sun.translateZProperty().bind(sunDistance.multiply(-0.41));
sun.lightOnProperty().bind(sunLight);
AmbientLight ambient = new AmbientLight(Color.rgb(1, 1, 1));
Group root = new Group();
root.getChildren().add(camera);
root.getChildren().add(earth);
root.getChildren().add(sun);
root.getChildren().add(ambient);
RotateTransition rt = new RotateTransition(Duration.seconds(24), earth);
rt.setByAngle(360);
rt.setInterpolator(Interpolator.LINEAR);
rt.setCycleCount(Animation.INDEFINITE);
rt.play();
SubScene subScene = new SubScene(root, 400, 300,
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
