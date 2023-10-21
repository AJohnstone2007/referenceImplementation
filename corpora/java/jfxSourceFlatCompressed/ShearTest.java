package test.javafx.scene.transform;
import static test.javafx.scene.transform.TransformTest.assertTx;
import javafx.scene.shape.Rectangle;
import org.junit.Test;
import static org.junit.Assert.*;
import test.com.sun.javafx.test.TransformHelper;
import com.sun.javafx.geom.transform.Affine2D;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Transform;
public class ShearTest {
@Test
public void testShear() {
final Shear t = new Shear(112, 114);
final Rectangle rect = new Rectangle();
rect.getTransforms().add(t);
Affine2D expT = new Affine2D();
expT.setToShear(112, 114);
assertTx(rect, expT);
final Shear trans = new Shear() {{
setX(25);
setY(52);
}};
final Rectangle n = new Rectangle();
n.getTransforms().add(trans);
TransformHelper.assertMatrix(trans,
1, 25, 0, 0,
52, 1, 0, 0,
0, 0, 1, 0);
Affine2D expTx1 = new Affine2D();
expTx1.setToShear(25, 52);
assertTx(n, expTx1);
trans.setX(34);
Affine2D expTx2 = new Affine2D();
expTx2.setToShear(34, 52);
assertTx(n, expTx2);
TransformHelper.assertMatrix(trans,
1, 34, 0, 0,
52, 1, 0, 0,
0, 0, 1, 0);
trans.setY(67);
Affine2D expTx3 = new Affine2D();
expTx3.setToShear(34, 67);
assertTx(n, expTx3);
TransformHelper.assertMatrix(trans,
1, 34, 0, 0,
67, 1, 0, 0,
0, 0, 1, 0);
trans.setPivotX(66);
Affine2D expTx = new Affine2D();
expTx.setToTranslation(trans.getPivotX(), trans.getPivotY());
expTx.shear(trans.getX(), trans.getY());
expTx.translate(-trans.getPivotX(), -trans.getPivotY());
assertTx(n, expTx);
TransformHelper.assertMatrix(trans,
1, 34, 0, 0,
67, 1, 0, -67*66,
0, 0, 1, 0);
trans.setPivotY(77);
expTx.setToTranslation(trans.getPivotX(), trans.getPivotY());
expTx.shear(trans.getX(), trans.getY());
expTx.translate(-trans.getPivotX(), -trans.getPivotY());
assertTx(n, expTx);
TransformHelper.assertMatrix(trans,
1, 34, 0, -34*77,
67, 1, 0, -67*66,
0, 0, 1, 0);
}
@Test
public void testCopying() {
final Shear trans = new Shear(34, 67, 66, 77);
Transform copy = trans.clone();
TransformHelper.assertMatrix(copy,
1, 34, 0, -34*77,
67, 1, 0, -67*66,
0, 0, 1, 0);
}
@Test public void testToString() {
final Shear trans = new Shear(8, 15);
String s = trans.toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
@Test public void testBoundPropertySynced_X() throws Exception {
TransformTest.checkDoublePropertySynced(new Shear(3, 3, 0, 0), "x", 123.0);
}
@Test public void testBoundPropertySynced_Y() throws Exception {
TransformTest.checkDoublePropertySynced(new Shear(3, 3, 0, 0), "y", 112.0);
}
@Test public void testBoundPropertySynced_PivotX() throws Exception {
TransformTest.checkDoublePropertySynced(new Shear(3, 3, 0, 0), "pivotX", 22.0);
}
@Test public void testBoundPropertySynced_PivotY() throws Exception {
TransformTest.checkDoublePropertySynced(new Shear(3, 3, 0, 0), "pivotY", 33.0);
}
}
