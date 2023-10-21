package test.javafx.scene.transform;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
import javafx.geometry.Point3D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Translate;
@RunWith(Parameterized.class)
public class Transform_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final Affine a = new Affine(
1, 2, 3, 4,
5, 6, 7, 8,
9, 10, 11, 12);
final Rotate r = new Rotate();
final Shear s = new Shear();
final Translate t = new Translate();
final Scale c = new Scale();
return Arrays.asList(new Object[] {
config(a, "mxx", 10.0, 20.0),
config(a, "mxy", 10.0, 20.0),
config(a, "mxz", 10.0, 20.0),
config(a, "tx", 10.0, 20.0),
config(a, "myx", 10.0, 20.0),
config(a, "myy", 10.0, 20.0),
config(a, "myz", 10.0, 20.0),
config(a, "ty", 10.0, 20.0),
config(a, "mzx", 10.0, 20.0),
config(a, "mzy", 10.0, 20.0),
config(a, "mzz", 10.0, 20.0),
config(a, "tz", 10.0, 20.0),
config(r, "angle", 10.0, 20.0),
config(r, "axis", new Point3D(10, 20, 30), new Point3D(30, 20, 10)),
config(r, "pivotX", 10.0, 20.0),
config(r, "pivotY", 10.0, 20.0),
config(r, "pivotZ", 10.0, 20.0),
config(s, "x", 10.0, 20.0),
config(s, "y", 10.0, 20.0),
config(s, "pivotX", 10.0, 20.0),
config(s, "pivotY", 10.0, 20.0),
config(t, "x", 10.0, 20.0),
config(t, "y", 10.0, 20.0),
config(t, "z", 10.0, 20.0),
config(c, "x", 10.0, 20.0),
config(c, "y", 10.0, 20.0),
config(c, "z", 10.0, 20.0),
config(c, "pivotX", 10.0, 20.0),
config(c, "pivotY", 10.0, 20.0),
config(c, "pivotZ", 10.0, 20.0),
});
}
public Transform_properties_Test(final Configuration configuration) {
super(configuration);
}
}
