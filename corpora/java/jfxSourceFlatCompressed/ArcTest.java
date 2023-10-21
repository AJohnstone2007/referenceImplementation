package test.javafx.scene.shape;
import com.sun.javafx.sg.prism.NGArc;
import com.sun.javafx.sg.prism.NGNode;
import javafx.scene.Node;
import test.javafx.scene.NodeTest;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import org.junit.Test;
import static org.junit.Assert.*;
import test.com.sun.javafx.scene.shape.StubArcHelper;
public class ArcTest {
@Test public void testPropertyPropagation_visible() throws Exception {
final Arc node = new StubArc();
NodeTest.testBooleanPropertyPropagation(node, "visible", false, true);
}
@Test public void testPropertyPropagation_centerX() throws Exception {
final Arc node = new StubArc();
NodeTest.testDoublePropertyPropagation(node, "centerX", 100, 200);
}
@Test public void testPropertyPropagation_centerY() throws Exception {
final Arc node = new StubArc();
NodeTest.testDoublePropertyPropagation(node, "centerY", 100, 200);
}
@Test public void testPropertyPropagation_radiusX() throws Exception {
final Arc node = new StubArc();
NodeTest.testDoublePropertyPropagation(node, "radiusX", 100, 200);
}
@Test public void testPropertyPropagation_radiusY() throws Exception {
final Arc node = new StubArc();
NodeTest.testDoublePropertyPropagation(node, "radiusY", 100, 200);
}
@Test public void testPropertyPropagation_startAngle() throws Exception {
final Arc node = new StubArc();
NodeTest.testDoublePropertyPropagation(node, "startAngle", "angleStart", 30, 60);
}
@Test public void testPropertyPropagation_length() throws Exception {
final Arc node = new StubArc();
NodeTest.testDoublePropertyPropagation(node, "length", "angleExtent", 30, 45);
}
@Test public void testBoundPropertySync_length() throws Exception {
NodeTest.assertDoublePropertySynced(
new StubArc(10.0, 10.0, 100.0, 100.0, 0.0, 0.0),
"length", "angleExtent", 100.0);
}
@Test public void testBoundProperySync_startAngle() throws Exception {
NodeTest.assertDoublePropertySynced(
new StubArc(10.0, 10.0, 100.0, 100.0, 0.0, 0.0),
"startAngle", "angleStart", 270.0);
}
@Test public void testBoundPropertySync_radiusY() throws Exception {
NodeTest.assertDoublePropertySynced(
new StubArc(10.0, 10.0, 100.0, 100.0, 0.0, 0.0),
"radiusY", "radiusY", 200.0);
}
@Test public void testBoundPropertySync_radiusX() throws Exception {
NodeTest.assertDoublePropertySynced(
new StubArc(10.0, 10.0, 100.0, 100.0, 0.0, 0.0),
"radiusX", "radiusX", 150.0);
}
@Test public void testBoundPropertySync_centerY() throws Exception {
NodeTest.assertDoublePropertySynced(
new StubArc(10.0, 10.0, 100.0, 100.0, 0.0, 0.0),
"centerY", "centerY", 250.0);
}
@Test public void testBoundPropertySync_centerX() throws Exception {
NodeTest.assertDoublePropertySynced(
new StubArc(10.0, 10.0, 100.0, 100.0, 0.0, 0.0),
"centerX", "centerX", 350.0);
}
@Test public void toStringShouldReturnNonEmptyString() {
String s = new StubArc().toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
@Test public void testNullType() {
Arc arc = new StubArc(10.0, 10.0, 100.0, 100.0, 0.0, 0.0);
arc.setType(null);
assertNull(arc.getType());
assertNull(arc.typeProperty().get());
}
public static final class StubArc extends Arc {
static {
StubArcHelper.setStubArcAccessor(new StubArcHelper.StubArcAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((StubArc) node).doCreatePeer();
}
});
}
{
StubArcHelper.initHelper(this);
}
public StubArc() {
super();
}
public StubArc(double centerX, double centerY, double radiusX, double radiusY, double startAngle, double length) {
super(centerX, centerY, radiusX, radiusY, startAngle, length);
}
private NGNode doCreatePeer() {
return new StubNGArc();
}
}
public static final class StubNGArc extends NGArc {
private float cx, cy, rx, ry, start, extent;
private ArcType type;
@Override
public void updateArc(float cx, float cy, float rx, float ry, float start, float extent, ArcType type) {
super.updateArc(cx, cy, rx, ry, start, extent, type);
this.cx = cx;
this.cy = cy;
this.rx = rx;
this.ry = ry;
this.start = start;
this.extent = extent;
this.type = type;
}
public float getAngleExtent() {return extent;}
public float getAngleStart() {return start;}
public float getCenterX() {return cx;}
public float getCenterY() {return cy;}
public float getRadiusX() {return rx;}
public float getRadiusY() {return ry;}
public ArcType getArcType() { return type;}
}
}
