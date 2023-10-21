package ensemble.samples.graphics3d.cubesystem;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
public class Xsphere extends Sphere {
final Rotate rx = new Rotate(0, Rotate.X_AXIS);
final Rotate ry = new Rotate(0, Rotate.Y_AXIS);
final Rotate rz = new Rotate(0, Rotate.Z_AXIS);
public Xsphere(double size, Color color) {
super(size);
setMaterial(new PhongMaterial(color));
getTransforms().addAll(rz, ry, rx);
}
}
