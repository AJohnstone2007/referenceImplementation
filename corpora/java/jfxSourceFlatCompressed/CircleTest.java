package test.javafx.scene.shape;
import test.com.sun.javafx.scene.shape.StubCircleHelper;
import com.sun.javafx.sg.prism.NGCircle;
import com.sun.javafx.sg.prism.NGNode;
import test.com.sun.javafx.test.TestHelper;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import test.javafx.scene.NodeTest;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import org.junit.Test;
import static test.com.sun.javafx.test.TestHelper.assertSimilar;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
public class CircleTest {
@Test public void testPropertyPropagation_visible() throws Exception {
final Circle node = new StubCircle();
NodeTest.testBooleanPropertyPropagation(node, "visible", false, true);
}
@Test public void testPropertyPropagation_centerX() throws Exception {
final Circle node = new StubCircle();
NodeTest.testDoublePropertyPropagation(node, "centerX", 100, 200);
}
@Test public void testPropertyPropagation_centerY() throws Exception {
final Circle node = new StubCircle();
NodeTest.testDoublePropertyPropagation(node, "centerY", 100, 200);
}
@Test public void testPropertyPropagation_radius() throws Exception {
final Circle node = new StubCircle();
NodeTest.testDoublePropertyPropagation(node, "radius", 100, 200);
}
@Test public void testBoundPropertySync_centerX() throws Exception {
NodeTest.assertDoublePropertySynced(
new StubCircle(100, 200, 50),
"centerX", "centerX",
350.0);
}
@Test public void testBoundPropertySync_centerY() throws Exception {
NodeTest.assertDoublePropertySynced(
new StubCircle(100, 200, 50),
"centerY", "centerY",
250.0);
}
@Test public void testBoundPropertySync_radius() throws Exception {
NodeTest.assertDoublePropertySynced(
new StubCircle(100, 200, 50),
"radius", "radius",
100.0);
}
@Test
public void testTransformedBounds_rotation() {
Circle c = new StubCircle(50, 100, 10, Color.RED);
Bounds original = c.getBoundsInParent();
c.setRotate(15);
assertSimilar(original, c.getBoundsInParent());
}
@Test
public void testTransformedBounds_rotation2() {
final int centerX = 50;
final int centerY = 200;
Circle c = new StubCircle(centerX, centerY, 10, Color.RED);
Bounds original = c.getBoundsInParent();
Rotate r = new Rotate();
final double angle = Math.asin((38.0/2)/181)*2;
r.setAngle(Math.toDegrees(angle));
r.setPivotX(centerX + 181);
r.setPivotY(centerY);
c.getTransforms().add(r);
final double centerXDelta = Math.cos((Math.PI - angle)/2) * 38;
final double centerYDelta = - (Math.sin((Math.PI - angle)/2) * 38);
assertSimilar(TestHelper.box(original.getMinX() + centerXDelta, original.getMinY() + centerYDelta,
original.getWidth(), original.getHeight()), c.getBoundsInParent());
}
@Test
public void testTransformedBounds_translate() {
Circle c = new StubCircle(50, 100, 10, Color.RED);
Bounds original = c.getBoundsInParent();
c.setTranslateX(10);
c.setTranslateY(20);
assertSimilar(TestHelper.box(original.getMinX() + 10, original.getMinY() + 20,
original.getWidth(), original.getHeight()), c.getBoundsInParent());
}
@Test public void testTransformedBounds_scale() {
Circle c = new StubCircle(50, 100, 10, Color.RED);
double scalePivotX = (c.getCenterX() + c.getRadius()) / 2;
double scalePivotY = (c.getCenterY() + c.getRadius()) / 2;
Bounds original = c.getBoundsInParent();
Scale s = new Scale(2.0, 1.5, scalePivotX, scalePivotY);
c.getTransforms().setAll(s);
assertSimilar(TestHelper.box(2 * original.getMinX() - scalePivotX, 1.5 * original.getMinY() - 0.5 * scalePivotY,
2 * original.getWidth(), 1.5 * original.getHeight()), c.getBoundsInParent());
}
@Test public void toStringShouldReturnNonEmptyString() {
String s = new StubCircle().toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
public static final class StubCircle extends Circle {
static {
StubCircleHelper.setStubCircleAccessor(new StubCircleHelper.StubCircleAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((StubCircle) node).doCreatePeer();
}
});
}
{
StubCircleHelper.initHelper(this);
}
public StubCircle() {
super();
}
public StubCircle(double radius) {
super(radius);
}
public StubCircle(double centerX, double centerY, double radius) {
super(centerX, centerY, radius);
}
public StubCircle(double centerX, double centerY, double radius, Paint fill) {
super(centerX, centerY, radius, fill);
}
private NGNode doCreatePeer() {
return new StubNGCircle();
}
}
public static final class StubNGCircle extends NGCircle {
private float centerX;
private float centerY;
private float radius;
public float getCenterX() {return centerX;}
public float getCenterY() {return centerY;}
public float getRadius() {return radius;}
@Override
public void updateCircle(float cx, float cy, float radius) {
this.centerX = cx;
this.centerY = cy;
this.radius = radius;
}
}
}
