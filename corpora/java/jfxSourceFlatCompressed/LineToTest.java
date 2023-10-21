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
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import org.junit.Test;
import static org.junit.Assert.*;
public class LineToTest {
@Test public void testAddTo() throws Exception {
}
@Test public void testDoublePropertySynced_X() {
double expected = 123.4;
LineTo lineTo = new LineTo(100.0, 100.0);
DoubleProperty v = new SimpleDoubleProperty(100.0);
lineTo.xProperty().bind(v);
Path path = new Path();
path.getElements().addAll(new MoveTo(1.0, 1.0), lineTo);
((Group)new Scene(new Group()).getRoot()).getChildren().add(path);
v.set(expected);
NodeTest.syncNode(path);
NGPath pgPath = NodeHelper.getPeer(path);
Path2D geometry = pgPath.getGeometry();
float[] coords = new float[6];
PathIterator it = geometry.getPathIterator(null);
it.next();
int segType = it.currentSegment(coords);
assertEquals(segType, PathIterator.SEG_LINETO);
assertEquals(expected, coords[0], 0.001);
}
@Test public void testDoublePropertySynced_Y() {
double expected = 432.1;
LineTo lineTo = new LineTo(100.0, 200.0);
DoubleProperty v = new SimpleDoubleProperty(200.0);
lineTo.yProperty().bind(v);
Path path = new Path();
path.getElements().addAll(new MoveTo(1.0, 1.0), lineTo);
((Group)new Scene(new Group()).getRoot()).getChildren().add(path);
v.set(expected);
NodeTest.syncNode(path);
NGPath pgPath = NodeHelper.getPeer(path);
Path2D geometry = pgPath.getGeometry();
float[] coords = new float[6];
PathIterator it = geometry.getPathIterator(null);
it.next();
int segType = it.currentSegment(coords);
assertEquals(segType, PathIterator.SEG_LINETO);
assertEquals(expected, coords[1], 0.001);
}
@Test public void toStringShouldReturnNonEmptyString() {
String s = new LineTo().toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
}
