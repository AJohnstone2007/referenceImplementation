package fx83dfeatures;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.Stage;
public class RepeatTextureTest extends Application {
public static final float D = 200;
final TriangleMesh mesh = new TriangleMesh();
@Override
public void start(Stage primaryStage) throws Exception {
mesh.getPoints().setAll(new float[]{
0, 0, 0,
D, 0, 0,
D, D, 0,
0, D, 0,});
mesh.getTexCoords().setAll(new float[]{
0, 0,
1, 0,
1, 1,
0, 1,});
mesh.getFaces().setAll(new int[]{
0, 0, 2, 2, 1, 1,
0, 0, 3, 3, 2, 2,});
PhongMaterial material = new PhongMaterial();
material.setDiffuseMap(new Image("resources/cone-stripes.jpg"));
MeshView meshView = new MeshView(mesh);
meshView.setMaterial(material);
Group root = new Group();
root.getChildren().addAll(meshView);
Scene scene = new Scene(root, D, D, true);
scene.setOnKeyTyped(e -> {
switch (e.getCharacter()) {
case "1":
mesh.getTexCoords().setAll(new float[]{
0, 0,
1, 0,
1, 1,
0, 1,});
break;
case "2":
mesh.getTexCoords().setAll(new float[]{
0, 0,
2, 0,
2, 2,
0, 2,});
break;
case "3":
mesh.getTexCoords().setAll(new float[]{
-1, -1,
2, -1,
2, 2,
-1, 2,});
break;
case "4":
mesh.getTexCoords().setAll(new float[]{
-2, -2,
2, -2,
2, 2,
-2, 2,});
break;
}
});
primaryStage.setScene(scene);
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
