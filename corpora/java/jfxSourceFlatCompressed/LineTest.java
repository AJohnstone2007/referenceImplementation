package test.javafx.scene.shape;
import com.sun.javafx.sg.prism.NGLine;
import com.sun.javafx.sg.prism.NGNode;
import javafx.scene.Node;
import test.javafx.scene.NodeTest;
import javafx.scene.shape.Line;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import test.com.sun.javafx.scene.shape.StubLineHelper;
public class LineTest {
@Test
public void testPropertyPropagation_visible() throws Exception {
final Line node = new StubLine();
NodeTest.testBooleanPropertyPropagation(node, "visible", false, true);
}
@Test
public void testPropertyPropagation_startX() throws Exception {
final Line node = new StubLine();
NodeTest.testDoublePropertyPropagation(node, "startX", "x1", 100, 200);
}
@Test
public void testPropertyPropagation_startY() throws Exception {
final Line node = new StubLine();
NodeTest.testDoublePropertyPropagation(node, "startY", "y1", 100, 200);
}
@Test
public void testPropertyPropagation_endX() throws Exception {
final Line node = new StubLine();
NodeTest.testDoublePropertyPropagation(node, "endX", "x2", 100, 200);
}
@Test
public void testPropertyPropagation_endY() throws Exception {
final Line node = new StubLine();
NodeTest.testDoublePropertyPropagation(node, "endY", "y2", 100, 200);
}
@Test public void testBoundPropertySync_startX() throws Exception {
NodeTest.assertDoublePropertySynced(
new StubLine(0.0 ,0.0, 100.0, 100.0),
"startX", "x1", 10.0);
}
@Test public void testBoundPropertySync_startY() throws Exception {
NodeTest.assertDoublePropertySynced(
new StubLine(0.0 ,0.0, 100.0, 100.0),
"startY", "y1", 50.0);
}
@Test public void testBoundPropertySync_endX() throws Exception {
NodeTest.assertDoublePropertySynced(
new StubLine(0.0 ,0.0, 100.0, 100.0),
"endX", "x2", 200.0);
}
@Test public void testBoundPropertySync_endY() throws Exception {
NodeTest.assertDoublePropertySynced(
new StubLine(0.0 ,0.0, 100.0, 100.0),
"endY", "y2", 300.0);
}
@Test public void toStringShouldReturnNonEmptyString() {
String s = new StubLine().toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
public static final class StubLine extends Line {
static {
StubLineHelper.setStubLineAccessor(new StubLineHelper.StubLineAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((StubLine) node).doCreatePeer();
}
});
}
{
StubLineHelper.initHelper(this);
}
public StubLine() {
super();
}
public StubLine(double startX, double startY, double endX, double endY) {
super(startX, startY, endX, endY);
}
private NGNode doCreatePeer() {
return new StubNGLine();
}
}
public static final class StubNGLine extends NGLine {
private float x1;
private float y1;
private float x2;
private float y2;
public float getX1() {return x1;}
public float getX2() {return x2;}
public float getY1() {return y1;}
public float getY2() {return y2;}
@Override
public void updateLine(float x1, float y1, float x2, float y2) {
this.x1 = x1;
this.y1 = y1;
this.x2 = x2;
this.y2 = y2;
}
}
}
