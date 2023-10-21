package test.javafx.scene.shape;
import test.com.sun.javafx.scene.shape.StubPolygonHelper;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.ShapeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGPolygon;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import test.javafx.scene.NodeTest;
import javafx.scene.shape.Polygon;
import org.junit.Test;
import static test.com.sun.javafx.test.TestHelper.assertBoundsEqual;
import static test.com.sun.javafx.test.TestHelper.box;
import static org.junit.Assert.*;
public class PolygonTest {
@Test public void testPropertyPropagation_emptyPoints() {
final StubPolygon polygon = new StubPolygon();
NodeTest.callSyncPGNode(polygon);
assertPGPolygonPointsEquals(polygon, new double[0]);
}
@Test public void testPropertyPropagation_pointsEvenLength() {
final double[] initialPoints = { 10, 20, 100, 200, 200, 100, 50, 10 };
final StubPolygon polygon = new StubPolygon(initialPoints);
NodeTest.callSyncPGNode(polygon);
assertPGPolygonPointsEquals(polygon, initialPoints);
final ObservableList<Double> polygonPoints = polygon.getPoints();
polygonPoints.remove(1);
polygonPoints.remove(2);
NodeTest.callSyncPGNode(polygon);
assertPGPolygonPointsEquals(polygon, 10, 100, 200, 100, 50, 10);
}
@Test public void testPropertyPropagation_pointsOddLength() {
final double[] initialPoints = { 10, 20, 100, 200, 200 };
final StubPolygon polygon = new StubPolygon(initialPoints);
NodeTest.callSyncPGNode(polygon);
assertPGPolygonPointsEquals(polygon, initialPoints);
final ObservableList<Double> polygonPoints = polygon.getPoints();
polygonPoints.add(100.0);
polygonPoints.add(50.0);
NodeTest.callSyncPGNode(polygon);
assertPGPolygonPointsEquals(polygon, 10, 20, 100, 200, 200, 100, 50);
}
@Test public void testPropertyPropagation_visible() throws Exception {
final StubPolygon polygon = new StubPolygon();
NodeTest.testBooleanPropertyPropagation(polygon, "visible", false, true);
}
@Test public void testBounds_emptyPoints() {
final StubPolygon polygon = new StubPolygon();
assertBoundsEqual(box(0, 0, -1, -1), polygon.getBoundsInLocal());
}
@Test public void testBounds_evenPointsLength() {
final double[] initialPoints = { 100, 100, 200, 100, 200, 200 };
final StubPolygon polygon = new StubPolygon(initialPoints);
assertBoundsEqual(box(100, 100, 100, 100), polygon.getBoundsInLocal());
final ObservableList<Double> polygonPoints = polygon.getPoints();
polygonPoints.add(150.0);
polygonPoints.add(300.0);
assertBoundsEqual(box(100, 100, 100, 200), polygon.getBoundsInLocal());
}
@Test public void testBounds_oddPointsLength() {
final double[] initialPoints = {
100, 100, 200, 100, 200, 200, 150, 300
};
final StubPolygon polygon = new StubPolygon(initialPoints);
assertBoundsEqual(box(100, 100, 100, 200), polygon.getBoundsInLocal());
final ObservableList<Double> polygonPoints = polygon.getPoints();
polygonPoints.remove(6);
assertBoundsEqual(box(100, 100, 100, 100), polygon.getBoundsInLocal());
}
@Test public void testConfigShape() throws Exception {
final StubPolygon polygon =
new StubPolygon(new double[] { 0, 0, 0, 1, 1, 1, 1, 0 });
final Path2D path = (Path2D) ShapeHelper.configShape(polygon);
assertTrue(path.contains(0.5f, 0.5f));
assertFalse(path.contains(0, 2));
}
private static void assertPGPolygonPointsEquals(
final StubPolygon polygon,
final double... expectedPoints) {
final StubNGPolygon stubPolygon = NodeHelper.getPeer(polygon);
final float[] pgPoints = stubPolygon.points;
final int minLength = expectedPoints.length & ~1;
final int maxLength = expectedPoints.length;
assertTrue(pgPoints.length >= minLength);
assertTrue(pgPoints.length <= maxLength);
int i;
for (i = 0; i < minLength; ++i) {
assertEquals(expectedPoints[i], pgPoints[i], 0);
}
for (; i < pgPoints.length; ++i) {
assertEquals(expectedPoints[i], pgPoints[i], 0);
}
}
@Test public void toStringShouldReturnNonEmptyString() {
String s = new Polygon().toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
public static final class StubPolygon extends Polygon {
static {
StubPolygonHelper.setStubPolygonAccessor(new StubPolygonHelper.StubPolygonAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((StubPolygon) node).doCreatePeer();
}
});
}
{
StubPolygonHelper.initHelper(this);
}
public StubPolygon(double... initialPoints) {
super(initialPoints);
}
public StubPolygon() {
super();
}
private NGNode doCreatePeer() {
return new StubNGPolygon();
}
}
public static final class StubNGPolygon extends NGPolygon {
private float[] points;
@Override
public void updatePolygon(float[] points) {
super.updatePolygon(points);
this.points = points;
}
}
}
