package test.javafx.scene.shape;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.PathIterator;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGPath;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import test.javafx.scene.NodeTest;
import javafx.scene.Scene;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import org.junit.Test;
import static org.junit.Assert.*;
public class MoveToTest {
@Test public void testAddTo() throws Exception {
}
@Test public void testSetGetX() throws Exception {
TestUtils.testDoublePropertyGetterSetter(new MoveTo(), "x", 123.2, 0.0);
}
@Test public void testSetGetY() throws Exception {
TestUtils.testDoublePropertyGetterSetter(new MoveTo(), "y", 123.2, 0.0);
}
@Test public void testDoubleBoundPropertySynced_X() {
double expected = 123.4;
MoveTo moveTo = new MoveTo(10.0, 10.0);
DoubleProperty v = new SimpleDoubleProperty(10.0);
moveTo.xProperty().bind(v);
Path path = new Path();
path.getElements().add(moveTo);
((Group)new Scene(new Group()).getRoot()).getChildren().add(path);
v.set(expected);
NodeTest.syncNode(path);
NGPath pgPath = NodeHelper.getPeer(path);
Path2D geometry = pgPath.getGeometry();
float[] coords = new float[6];
int segType = geometry.getPathIterator(null).currentSegment(coords);
assertEquals(segType, PathIterator.SEG_MOVETO);
assertEquals(expected, coords[0], 0.001);
}
@Test public void testDoubleBoundPropertySynced_Y() {
double expected = 432.1;
MoveTo moveTo = new MoveTo(10.0, 10.0);
DoubleProperty v = new SimpleDoubleProperty(10.0);
moveTo.yProperty().bind(v);
Path path = new Path();
path.getElements().add(moveTo);
((Group)new Scene(new Group()).getRoot()).getChildren().add(path);
v.set(expected);
NodeTest.syncNode(path);
NGPath pgPath = NodeHelper.getPeer(path);
Path2D geometry = pgPath.getGeometry();
float[] coords = new float[6];
int segType = geometry.getPathIterator(null).currentSegment(coords);
assertEquals(segType, PathIterator.SEG_MOVETO);
assertEquals(expected, coords[1], 0.001);
}
@Test public void toStringShouldReturnNonEmptyString() {
String s = new MoveTo().toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
}
