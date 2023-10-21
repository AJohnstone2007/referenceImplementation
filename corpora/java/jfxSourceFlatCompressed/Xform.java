package ensemble.samples.graphics3d.xylophone;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
public class Xform extends Group {
final Rotate rx = new Rotate(0, Rotate.X_AXIS);
final Rotate ry = new Rotate(0, Rotate.Y_AXIS);
final Rotate rz = new Rotate(0, Rotate.Z_AXIS);
public Scale s = new Scale();
public Xform() {
super();
getTransforms().addAll(rz, ry, rx, s);
}
public void setScale(double scaleFactor) {
s.setX(scaleFactor);
s.setY(scaleFactor);
s.setZ(scaleFactor);
}
}
