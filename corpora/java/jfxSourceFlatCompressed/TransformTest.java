package test.javafx.scene.transform;
import com.sun.javafx.geom.transform.Affine2D;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.transform.TransformUtils;
import test.com.sun.javafx.test.TransformHelper;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import test.javafx.scene.NodeTest;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import test.javafx.scene.shape.RectangleTest;
import org.junit.Assert;
import org.junit.Test;
import java.lang.reflect.Method;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.TransformShim;
import static org.junit.Assert.*;
public class TransformTest {
public static void assertTx(Node n, BaseTransform trans) {
Assert.assertEquals(trans, NodeHelper.getLeafTransform(n));
}
@Test
public void testTranslate() {
Transform tx = Transform.translate(25, 52);
final Rectangle n = new Rectangle();
n.getTransforms().add(tx);
assertTx(n, BaseTransform.getTranslateInstance(25, 52));
}
@Test
public void testScale1() {
Transform tx = Transform.scale(25, 52);
final Rectangle n = new Rectangle();
n.getTransforms().add(tx);
assertTx(n, BaseTransform.getScaleInstance(25, 52));
}
@Test
public void testScale2() {
Transform tx = Transform.scale(25, 52, 66, 77);
final Rectangle n = new Rectangle();
n.getTransforms().add(tx);
Affine2D expTx = new Affine2D();
expTx.setToTranslation(66, 77);
expTx.scale(25, 52);
expTx.translate(-66, -77);
assertTx(n, expTx);
}
@Test
public void testRotate() {
Rotate tx = Transform.rotate(90, 3, 5);
final Rectangle n = new Rectangle();
n.getTransforms().add(tx);
Affine2D expTx = new Affine2D();
expTx.quadrantRotate(1, 3, 5);
assertTx(n, expTx);
}
@Test
public void testShear() {
Transform tx = Transform.shear(25, 52);
final Rectangle n = new Rectangle();
n.getTransforms().add(tx);
Affine2D expTx1 = new Affine2D();
expTx1.setToShear(25, 52);
assertTx(n, expTx1);
}
@Test
public void testShearWithPivot() {
Transform tx = Transform.shear(25, 52, 35, 53);
final Rectangle n = new Rectangle();
n.getTransforms().add(tx);
Affine2D expTx1 = new Affine2D();
expTx1.translate(35, 53);
expTx1.shear(25, 52);
expTx1.translate(-35, -53);
assertTx(n, expTx1);
}
@Test
public void testAffine() {
final Transform trans = Transform.affine(11, 22, 33, 44, 55, 66);
final Rectangle n = new Rectangle();
n.getTransforms().add(trans);
final Affine2D affine2D = new Affine2D(11, 22, 33, 44, 55, 66);
final Affine3D affine3D = new Affine3D(affine2D);
assertTx(n, affine3D);
}
@Test
public void testAffine3D() {
final Transform trans = Transform.affine(11, 22, 33, 44, 55, 66, 77, 88, 99, 111, 222, 333);
final Rectangle n = new Rectangle();
n.getTransforms().add(trans);
final Affine3D affine3D = new Affine3D(11, 22, 33, 44, 55, 66, 77, 88, 99, 111, 222, 333);
assertTx(n, affine3D);
}
@Test
public void defaultTransformShouldBeIdentity() {
final Transform t = TransformShim.createRawTransform(
1, 0, 0, 0,
0, 1, 0, 0,
0, 0, 1, 0);
TransformHelper.assertMatrix(t,
1, 0, 0, 0,
0, 1, 0, 0,
0, 0, 1, 0);
}
@Test
public void testTransform() {
Transform t = TransformUtils.immutableTransform(
1, 2, 3, 4,
5, 6, 7, 8,
9, 10, 11, 12);
Point3D point = new Point3D(2, 5, 7);
Point3D transformed = t.transform(point);
assertEquals(37, transformed.getX(), 0.0001);
assertEquals(97, transformed.getY(), 0.0001);
assertEquals(157, transformed.getZ(), 0.0001);
}
@Test
public void eachCornerShouldBeConsideredBySimilarTo3D() {
final Transform t1 = new Affine();
final Transform t2 = new Scale(11, 11, 11);
final double limit = Math.sqrt(200);
assertTrue(t1.similarTo(t2, new BoundingBox(0, 0, 0, 0, 0, 0), limit));
assertTrue(t1.similarTo(t2, new BoundingBox(0, 0, 0, 0.1, 0.1, 0.1), limit));
assertFalse(t1.similarTo(t2, new BoundingBox(-1, -1, -1, 0, 0, 0), limit));
assertFalse(t1.similarTo(t2, new BoundingBox(0, -1, -1, 1, 0, 0), limit));
assertFalse(t1.similarTo(t2, new BoundingBox(-1, 0, -1, 0, 1, 0), limit));
assertFalse(t1.similarTo(t2, new BoundingBox(0, 0, -1, 1, 1, 0), limit));
assertFalse(t1.similarTo(t2, new BoundingBox(-1, -1, 0, 0, 0, 1), limit));
assertFalse(t1.similarTo(t2, new BoundingBox(0, -1, 0, 1, 0, 1), limit));
assertFalse(t1.similarTo(t2, new BoundingBox(-1, 0, 0, 0, 1, 1), limit));
assertFalse(t1.similarTo(t2, new BoundingBox(0, 0, 0, 1, 1, 1), limit));
}
@Test
public void eachCornerShouldBeConsideredBySimilarTo2D() {
final Transform t1 = new Affine();
final Transform t2 = new Scale(11, 11);
final double limit = Math.sqrt(200) - 1;
assertTrue(t1.similarTo(t2, new BoundingBox(0, 0, 0, 0, 0, 0), limit));
assertTrue(t1.similarTo(t2, new BoundingBox(0, 0, 0, 0.1, 0.1, 0.1), limit));
assertFalse(t1.similarTo(t2, new BoundingBox(-1, -1, 0, 0, 0, 0), limit));
assertFalse(t1.similarTo(t2, new BoundingBox(0, -1, 0, 1, 0, 0), limit));
assertFalse(t1.similarTo(t2, new BoundingBox(-1, 0, 0, 0, 1, 0), limit));
assertFalse(t1.similarTo(t2, new BoundingBox(0, 0, 0, 1, 1, 0), limit));
}
public static void checkDoublePropertySynced(Transform tr, String propertyName, double val)
throws Exception {
final Rectangle r = new RectangleTest.StubRectangle(200, 200, 200, 200);
DoubleProperty v = new SimpleDoubleProperty(0.0);
Method m = tr.getClass().getMethod(propertyName + "Property", new Class[] {});
((DoubleProperty)m.invoke(tr)).bind(v);
r.getTransforms().add(tr);
((Group)new Scene(new Group()).getRoot()).getChildren().add(r);
v.set(val);
NodeTest.syncNode(r);
RectangleTest.StubNGRectangle pgR = NodeHelper.getPeer(r);
assertTx(r, pgR.getTransformMatrix());
}
}
