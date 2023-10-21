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
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import static org.junit.Assert.*;
public class CubicCurveToTest {
@Test public void testSetGetX() throws Exception {
TestUtils.testDoublePropertyGetterSetter(new CubicCurveTo(), "x", 123.2f, 0.0f);
}
@Test public void testSetGetControlX1() throws Exception {
TestUtils.testDoublePropertyGetterSetter(new CubicCurveTo(), "controlX1", 123.2f, 0.0f);
}
@Test public void testSetGetControlX2() throws Exception {
TestUtils.testDoublePropertyGetterSetter(new CubicCurveTo(), "controlX2", 123.2f, 0.0f);
}
@Test public void testSetGetControlY1() throws Exception {
TestUtils.testDoublePropertyGetterSetter(new CubicCurveTo(), "controlY1", 123.2f, 0.0f);
}
@Test public void testSetGetControlY2() throws Exception {
TestUtils.testDoublePropertyGetterSetter(new CubicCurveTo(), "controlY2", 123.2f, 0.0f);
}
@Test public void testDoublePropertySynced_X() throws Exception {
checkSyncedProperty("x", Coords.X, 123.4);
}
@Test public void testDoublePropertySynced_Y() throws Exception {
checkSyncedProperty("y", Coords.Y, 432.1);
}
@Test public void testDoublePropertySynced_ControlX1() throws Exception {
checkSyncedProperty("controlX1", Coords.CONTROL_X1, 11.1);
}
@Test public void testDoublePropertySynced_ControlY1() throws Exception {
checkSyncedProperty("controlY1", Coords.CONTROL_Y1, 22.2);
}
@Test public void testDoublePropertySynced_ControlX2() throws Exception {
checkSyncedProperty("controlX2", Coords.CONTROL_X2, 1.1);
}
@Test public void testDoublePropertySynced_ControlY2() throws Exception {
checkSyncedProperty("controlY2", Coords.CONTROL_Y2, 2.2);
}
@Test public void toStringShouldReturnNonEmptyString() {
String s = new CubicCurveTo().toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
private void checkSyncedProperty(String propertyName, Coords coord, double expected) throws Exception {
CubicCurveTo ccurveTo = new CubicCurveTo(110.0, 190.0, 290.0, 190.0, 300.0, 200.0);
DoubleProperty v = new SimpleDoubleProperty(10.0);
Method m = CubicCurveTo.class.getMethod(propertyName + "Property", new Class[] {});
((DoubleProperty)m.invoke(ccurveTo)).bind(v);
Path path = new Path();
path.getElements().addAll(new MoveTo(100.0, 200.0), ccurveTo);
((Group)new Scene(new Group()).getRoot()).getChildren().add(path);
v.set(expected);
NodeTest.syncNode(path);
NGPath pgPath = NodeHelper.getPeer(path);
Path2D geometry = pgPath.getGeometry();
float[] coords = new float[6];
PathIterator it = geometry.getPathIterator(null);
it.next();
int segType = it.currentSegment(coords);
assertEquals(segType, PathIterator.SEG_CUBICTO);
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
