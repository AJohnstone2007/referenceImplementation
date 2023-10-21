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
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import static org.junit.Assert.*;
public class QuadCurveToTest {
@Test public void testSetGetX() throws Exception {
TestUtils.testDoublePropertyGetterSetter(new QuadCurveTo(), "x", 123.2, 0.0);
}
@Test public void testSetGetControlX() throws Exception {
TestUtils.testDoublePropertyGetterSetter(new QuadCurveTo(), "controlX", 123.2, 0.0);
}
@Test public void testSetGetControlY() throws Exception {
TestUtils.testDoublePropertyGetterSetter(new QuadCurveTo(), "controlY", 123.2, 0.0);
}
@Test public void testDoublePropertySynced_X() throws Exception {
checkSyncedProperty("x", Coords.X, 123.4);
}
@Test public void testDoublePropertySynced_Y() throws Exception {
checkSyncedProperty("y", Coords.Y, 432.1);
}
@Test public void testDoublePropertySynced_ControlX() throws Exception {
checkSyncedProperty("controlX", Coords.CONTROL_X, 11.1);
}
@Test public void testDoublePropertySynced_ControlY() throws Exception {
checkSyncedProperty("controlY", Coords.CONTROL_Y, 22.2);
}
@Test public void toStringShouldReturnNonEmptyString() {
String s = new QuadCurveTo().toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
private void checkSyncedProperty(String propertyName, Coords coord, double expected) throws Exception {
QuadCurveTo qcurveTo = new QuadCurveTo(100.0, 200.0, 10.0, 20.0);
DoubleProperty v = new SimpleDoubleProperty(100.0);
Method m = QuadCurveTo.class.getMethod(propertyName + "Property", new Class[] {});
((DoubleProperty)m.invoke(qcurveTo)).bind(v);
Path path = new Path();
path.getElements().addAll(new MoveTo(1.0, 1.0), qcurveTo);
((Group)new Scene(new Group()).getRoot()).getChildren().add(path);
v.set(expected);
NodeTest.syncNode(path);
NGPath pgPath = NodeHelper.getPeer(path);
Path2D geometry = pgPath.getGeometry();
float[] coords = new float[6];
PathIterator it = geometry.getPathIterator(null);
it.next();
int segType = it.currentSegment(coords);
assertEquals(segType, PathIterator.SEG_QUADTO);
assertEquals(expected, coords[coord.ordinal()], 0.001);
}
static enum Coords {
CONTROL_X,
CONTROL_Y,
X,
Y
}
}
