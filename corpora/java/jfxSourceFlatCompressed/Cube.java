package ensemble.samples.graphics3d.cubesystem;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
public class Cube extends Group {
final Rotate rx = new Rotate(0, Rotate.X_AXIS);
final Rotate ry = new Rotate(0, Rotate.Y_AXIS);
final Rotate rz = new Rotate(0, Rotate.Z_AXIS);
public Cube(double size, Color color, double shade) {
getTransforms().addAll(rz, ry, rx);
Rectangle r1 = new Rectangle(size, size,
color.deriveColor(0.0, 1.0,
(1 - 0.5 * shade), 1.0));
r1.setTranslateX(-0.5 * size);
r1.setTranslateY(-0.5 * size);
r1.setTranslateZ(0.5 * size);
Rectangle r2 = new Rectangle(size, size,
color.deriveColor(0.0, 1.0,
(1 - 0.4 * shade), 1.0));
r2.setTranslateX(-0.5 * size);
r2.setTranslateY(0);
r2.setRotationAxis(Rotate.X_AXIS);
r2.setRotate(90);
Rectangle r3 = new Rectangle(size, size,
color.deriveColor(0.0, 1.0,
(1 - 0.3 * shade), 1.0));
r3.setTranslateX(-1 * size);
r3.setTranslateY(-0.5 * size);
r3.setRotationAxis(Rotate.Y_AXIS);
r3.setRotate(90);
Rectangle r4 = new Rectangle(size, size,
color.deriveColor(0.0, 1.0,
(1 - 0.2 * shade), 1.0));
r4.setTranslateX(0);
r4.setTranslateY(-0.5 * size);
r4.setRotationAxis(Rotate.Y_AXIS);
r4.setRotate(90);
Rectangle r5 = new Rectangle(size, size,
color.deriveColor(0.0, 1.0,
(1 - 0.1 * shade), 1.0));
r5.setTranslateX(-0.5 * size);
r5.setTranslateY(-1 * size);
r5.setRotationAxis(Rotate.X_AXIS);
r5.setRotate(90);
Rectangle r6 = new Rectangle(size, size, color);
r6.setTranslateX(-0.5 * size);
r6.setTranslateY(-0.5 * size);
r6.setTranslateZ(-0.5 * size);
getChildren().addAll(r1, r2, r3, r4, r5, r6);
}
}
