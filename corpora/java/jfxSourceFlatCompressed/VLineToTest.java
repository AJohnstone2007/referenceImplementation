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
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.VLineTo;
import org.junit.Test;
import static org.junit.Assert.*;
public class VLineToTest {
@Test public void testAddTo() throws Exception {
Path2D path = new Path2D();
path.moveTo(0f, 0f);
final VLineTo vLineTo = new VLineTo(1f);
PathElementHelper.addTo(vLineTo, path);
assertEquals(0.0, path.getCurrentPoint().x, 0.0001f);
assertEquals(1.0, path.getCurrentPoint().y, 0.0001f);
}
@Test public void testDoublePropertySynced_Y() {
double expected = 123.4;
VLineTo vlineTo = new VLineTo(100.0);
DoubleProperty v = new SimpleDoubleProperty(100.0);
vlineTo.yProperty().bind(v);
Path path = new Path();
path.getElements().addAll(new MoveTo(1.0, 1.0), vlineTo);
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
String s = new VLineTo().toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
}
