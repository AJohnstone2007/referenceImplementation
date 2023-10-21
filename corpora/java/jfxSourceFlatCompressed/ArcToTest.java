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
import org.junit.Test;
import java.lang.reflect.Method;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import static org.junit.Assert.*;
public class ArcToTest {
@Test public void testSetGetX() throws Exception {
TestUtils.testDoublePropertyGetterSetter(new ArcTo(), "x", 123.2, 0.0);
}
@Test public void testSetGetY() throws Exception {
TestUtils.testDoublePropertyGetterSetter(new ArcTo(), "y", 123.2, 0.0);
}
@Test public void testSetGetRadiusX() throws Exception {
TestUtils.testDoublePropertyGetterSetter(new ArcTo(), "radiusX", 123.2, 0.0);
}
@Test public void testSetGetRadiusY() throws Exception {
TestUtils.testDoublePropertyGetterSetter(new ArcTo(), "radiusY", 123.2, 0.0);
}
@Test public void testSetGetXAxisRotation() throws Exception {
TestUtils.testDoublePropertyGetterSetter(new ArcTo(), "xAxisRotation", 123.2, 0.0);
}
@Test public void testSetGetLargeArcFlag() throws Exception {
TestUtils.testBooleanPropertyGetterSetter(new ArcTo(), "largeArcFlag");
}
@Test public void testSetGetSweepFlag() throws Exception {
TestUtils.testBooleanPropertyGetterSetter(new ArcTo(), "sweepFlag");
}
@Test public void testDoublePropertySynced_X() throws Exception {
checkSyncedProperty("x", Coords.X, 200.0);
}
@Test public void testDoublePropertySynced_Y() throws Exception {
checkSyncedProperty("y", Coords.Y, 200.0);
}
@Test public void toStringShouldReturnNonEmptyString() {
String s = new ArcTo().toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
private void checkSyncedProperty(String propertyName, Coords coord, double expected)
throws Exception {
ArcTo arcTo = new ArcTo();
arcTo.setRadiusX(40.0);arcTo.setRadiusY(50.0);
arcTo.setX(100.0); arcTo.setY(90.0);
DoubleProperty v = new SimpleDoubleProperty(100.0);
Method m = ArcTo.class.getMethod(propertyName + "Property", new Class[] {});
((DoubleProperty)m.invoke(arcTo)).bind(v);
Path path = new Path();
path.getElements().addAll(new MoveTo(1.0, 1.0), arcTo);
((Group)new Scene(new Group()).getRoot()).getChildren().add(path);
v.set(expected);
NodeTest.syncNode(path);
NGPath pgPath = NodeHelper.getPeer(path);
Path2D geometry = pgPath.getGeometry();
float[] coords = new float[6];
PathIterator it = (PathIterator)geometry.getPathIterator(null);
it.next(); it.next();
int segType = it.currentSegment(coords);
assertEquals(PathIterator.SEG_CUBICTO, segType);
assertEquals(expected, coords[coord.ordinal()], 0.001);
}
static enum Coords {
CONTROL_X1,
CONTROL_Y1,
CONTROL_X2,
CONTROL_Y2,
X,
Y
}
}
