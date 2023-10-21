package test.javafx.scene.shape;
import com.sun.javafx.sg.prism.NGEllipse;
import com.sun.javafx.sg.prism.NGNode;
import test.com.sun.javafx.test.TestHelper;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import test.javafx.scene.NodeTest;
import javafx.scene.shape.Ellipse;
import org.junit.Test;
import static test.com.sun.javafx.test.TestHelper.assertSimilar;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import test.com.sun.javafx.scene.shape.StubEllipseHelper;
public class EllipseTest {
@Test public void testPropertyPropagation_visible() throws Exception {
final Ellipse node = new StubEllipse();
NodeTest.testBooleanPropertyPropagation(node, "visible", false, true);
}
@Test public void testPropertyPropagation_centerX() throws Exception {
final Ellipse node = new StubEllipse();
NodeTest.testDoublePropertyPropagation(node, "centerX", 100, 200);
}
@Test public void testPropertyPropagation_centerY() throws Exception {
final Ellipse node = new StubEllipse();
NodeTest.testDoublePropertyPropagation(node, "centerY", 100, 200);
}
@Test public void testPropertyPropagation_radiusX() throws Exception {
final Ellipse node = new StubEllipse();
NodeTest.testDoublePropertyPropagation(node, "radiusX", 100, 200);
}
@Test public void testPropertyPropagation_radiusY() throws Exception {
final Ellipse node = new StubEllipse();
NodeTest.testDoublePropertyPropagation(node, "radiusY", 100, 200);
}
@Test public void testBoundPropertySync_radiusX() throws Exception {
NodeTest.assertDoublePropertySynced(
new StubEllipse(300.0, 300.0, 100.0, 100.0),
"radiusX", "radiusX", 150.0);
}
@Test public void testBoundPropertySync_radiusY() throws Exception {
NodeTest.assertDoublePropertySynced(
new StubEllipse(300.0, 300.0, 100.0, 100.0),
"radiusY", "radiusY", 150.0);
}
@Test public void testBoundPropertySync_centerX() throws Exception {
NodeTest.assertDoublePropertySynced(
new StubEllipse(300.0, 300.0, 100.0, 100.0),
"centerX", "centerX", 10.0);
}
@Test public void testBoundPropertySync_centerY() throws Exception {
NodeTest.assertDoublePropertySynced(
new StubEllipse(300.0, 300.0, 100.0, 100.0),
"centerY", "centerY", 10.0);
}
@Test
public void testTransformedBounds_rotation() {
Ellipse e = new StubEllipse(50, 100, 10, 20);
Bounds original = e.getBoundsInParent();
e.setRotate(90);
assertSimilar(TestHelper.box(30, 90,
original.getHeight(), original.getWidth()), e.getBoundsInParent());
}
@Test public void toStringShouldReturnNonEmptyString() {
String s = new StubEllipse().toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
public static final class StubEllipse extends Ellipse {
static {
StubEllipseHelper.setStubEllipseAccessor(new StubEllipseHelper.StubEllipseAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((StubEllipse) node).doCreatePeer();
}
});
}
{
StubEllipseHelper.initHelper(this);
}
public StubEllipse() {
super();
}
public StubEllipse(double radiusX, double radiusY) {
super(radiusX, radiusY);
}
public StubEllipse(double centerX, double centerY, double radiusX, double radiusY) {
super(centerX, centerY, radiusX, radiusY);
}
private NGNode doCreatePeer() {
return new StubNGEllipse();
}
}
public static final class StubNGEllipse extends NGEllipse {
private float centerX;
private float centerY;
private float radiusX;
private float radiusY;
public float getCenterX() {return centerX;}
public float getCenterY() {return centerY;}
public float getRadiusX() {return radiusX;}
public float getRadiusY() {return radiusY;}
@Override
public void updateEllipse(float cx, float cy, float rx, float ry) {
this.centerX = cx;
this.centerY = cy;
this.radiusX = rx;
this.radiusY = ry;
}
}
}
