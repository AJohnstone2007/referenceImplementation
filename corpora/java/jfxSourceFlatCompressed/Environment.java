package attenuation;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.DirectionalLight;
import javafx.scene.Group;
import javafx.scene.LightBase;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.SpotLight;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
class Environment extends CameraScene3D {
private final static double LIGHT_REP_RADIUS = 2;
private final static double LIGHT_Z_DIST = 50;
private final static double LIGHT_X_DIST = 50;
private final static double SPHERE_RADIUS = 50;
private final AmbientLight ambientLight1 = new AmbientLight(Color.WHITE);
private final AmbientLight ambientLight2 = new AmbientLight(Color.RED);
private final AmbientLight ambientLight3 = new AmbientLight(Color.BLACK);
final AmbientLight[] ambientLights = new AmbientLight[] {ambientLight1, ambientLight2, ambientLight3};
private final DirectionalLight directionalLight1 = new DirectionalLight(Color.RED);
private final DirectionalLight directionalLight2 = new DirectionalLight(Color.BLUE);
private final DirectionalLight directionalLight3 = new DirectionalLight(Color.MAGENTA);
final DirectionalLight[] directionalLights = new DirectionalLight[] {directionalLight1, directionalLight2, directionalLight3};
private final PointLight pointLight1 = new PointLight(Color.RED);
private final PointLight pointLight2 = new PointLight(Color.BLUE);
private final PointLight pointLight3 = new PointLight(Color.MAGENTA);
final PointLight[] pointLights = new PointLight[] {pointLight1, pointLight2, pointLight3};
private final SpotLight spotLight1 = new SpotLight(Color.RED);
private final SpotLight spotLight2 = new SpotLight(Color.BLUE);
private final SpotLight spotLight3 = new SpotLight(Color.MAGENTA);
final SpotLight[] spotLights = new SpotLight[] {spotLight1, spotLight2, spotLight3};
private Group shapeGroup = new Group();
Environment() {
setStyle("-fx-background-color: teal");
farClip.set(1000);
zoom.set(-350);
for (var light : ambientLights) {
addLight(light);
}
for (var light : directionalLights) {
addLight(light);
}
for (var light : pointLights) {
setupLight(light);
}
for (var light : spotLights) {
setupLight(light);
}
pointLight1.setTranslateX(LIGHT_X_DIST);
spotLight1.setTranslateX(LIGHT_X_DIST);
pointLight2.setTranslateX(-LIGHT_X_DIST);
spotLight2.setTranslateX(-LIGHT_X_DIST);
directionalLight1.setDirection(new Point3D(-LIGHT_X_DIST, 0, LIGHT_Z_DIST));
directionalLight2.setDirection(new Point3D(LIGHT_X_DIST, 0, LIGHT_Z_DIST));
rootGroup.getChildren().add(shapeGroup);
rootGroup.setMouseTransparent(true);
}
private void setupLight(PointLight light) {
light.setTranslateZ(-LIGHT_Z_DIST);
addLight(light);
var lightRep = new Sphere(LIGHT_REP_RADIUS);
var lightRepMat = new PhongMaterial();
lightRepMat.setSelfIlluminationMap(Boxes.createMapImage(light.colorProperty()));
lightRep.setMaterial(lightRepMat);
lightRep.translateXProperty().bind(light.translateXProperty());
lightRep.translateYProperty().bind(light.translateYProperty());
lightRep.translateZProperty().bind(light.translateZProperty());
lightRep.visibleProperty().bind(light.lightOnProperty());
rootGroup.getChildren().add(lightRep);
}
private void addLight(LightBase light) {
light.getScope().add(shapeGroup);
rootGroup.getChildren().add(light);
}
Group createBoxes() {
return new Boxes(LIGHT_Z_DIST);
}
Sphere createSphere(int subdivisions) {
return new Sphere(SPHERE_RADIUS, subdivisions);
}
MeshView createMeshView(int quadNum) {
final float[] points = {
-75.0f, 75.0f, 0.0f,
75.0f, 75.0f, 0.0f,
75.0f, -75.0f, 0.0f,
-75.0f, -75.0f, 0.0f
};
final float[] texCoords = {
0.0f, 0.0f,
1.0f, 0.0f,
1.0f, 1.0f,
0.0f, 1.0f
};
var face = List.of(
0, 0, 1, 1, 2, 2,
0, 0, 2, 2, 3, 3
);
var faces = new ArrayList<Integer>(quadNum * face.size());
for (int i = 0; i < quadNum; i++) {
faces.addAll(face);
}
var mesh = new TriangleMesh();
mesh.getPoints().setAll(points);
mesh.getTexCoords().setAll(texCoords);
int[] array = faces.stream().mapToInt(i -> i).toArray();
mesh.getFaces().setAll(array);
var mv = new MeshView(mesh);
return mv;
}
void switchTo(Node node) {
shapeGroup.getChildren().setAll(node);
}
}
