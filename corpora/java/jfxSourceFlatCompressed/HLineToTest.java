package test.javafx.scene.shape;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.PathIterator;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.PathElementHelper;
import com.sun.javafx.sg.prism.NGPath;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import test.javafx.scene.NodeTest;
import javafx.scene.Scene;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import org.junit.Test;
import static org.junit.Assert.*;
public class HLineToTest {
@Test public void testAddTo() throws Exception {
Path2D path = new Path2D();
path.moveTo(0f, 0f);
final HLineTo hLineTo = new HLineTo(1f);
PathElementHelper.addTo(hLineTo, path);
assertEquals(1.0, path.getCurrentPoint().x, 0.0001f);
assertEquals(0.0, path.getCurrentPoint().y, 0.0001f);
}
@Test public void testDoublePropertySynced_X() {
double expected = 123.4;
HLineTo hlineTo = new HLineTo(100.0);
DoubleProperty v = new SimpleDoubleProperty(100.0);
hlineTo.xProperty().bind(v);
Path path = new Path();
path.getElements().addAll(new MoveTo(1.0, 1.0), hlineTo);
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
@Test public void toStringShouldReturnNonEmptyString() {
String s = new HLineTo().toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
}
