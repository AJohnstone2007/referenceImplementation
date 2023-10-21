package test.javafx.scene.transform;
import static test.javafx.scene.transform.TransformTest.assertTx;
import javafx.scene.shape.Rectangle;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import test.com.sun.javafx.test.TransformHelper;
import com.sun.javafx.geom.transform.BaseTransform;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
public class TranslateTest {
@Test
public void testTranslate() {
final Translate trans = new Translate() {{
setX(25);
setY(52);
}};
final Rectangle n = new Rectangle();
n.getTransforms().add(trans);
assertTx(n, BaseTransform.getTranslateInstance(25, 52));
TransformHelper.assertMatrix(trans,
1, 0, 0, 25,
0, 1, 0, 52,
0, 0, 1, 0);
trans.setX(34);
Assert.assertEquals(34, trans.getX(), 1e-100);
assertTx(n, BaseTransform.getTranslateInstance(34, 52));
TransformHelper.assertMatrix(trans,
1, 0, 0, 34,
0, 1, 0, 52,
0, 0, 1, 0);
trans.setY(67);
assertTx(n, BaseTransform.getTranslateInstance(34, 67));
TransformHelper.assertMatrix(trans,
1, 0, 0, 34,
0, 1, 0, 67,
0, 0, 1, 0);
trans.setZ(33);
TransformHelper.assertMatrix(trans,
1, 0, 0, 34,
0, 1, 0, 67,
0, 0, 1, 33);
}
@Test
public void testCopying() {
final Translate trans = new Translate(34, 67, 33);
Transform copy = trans.clone();
TransformHelper.assertMatrix(copy,
1, 0, 0, 34,
0, 1, 0, 67,
0, 0, 1, 33);
}
@Test public void testToString() {
final Translate trans = new Translate(8, 15);
String s = trans.toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
@Test public void testBoundPropertySynced_X() throws Exception {
TransformTest.checkDoublePropertySynced(new Translate(3, 3, 0), "x", 22.0);
}
@Test public void testBoundPropertySynced_Y() throws Exception {
TransformTest.checkDoublePropertySynced(new Translate(3, 3, 0), "y", 33.0);
}
@Test public void testBoundPropertySynced_Z() throws Exception {
TransformTest.checkDoublePropertySynced(new Translate(3, 3, 0), "z", 44.0);
}
}
