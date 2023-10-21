package test.javafx.scene.shape;
import com.sun.javafx.scene.NodeHelper;
import test.com.sun.javafx.scene.shape.StubPolylineHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGPolyline;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import test.javafx.scene.NodeTest;
import javafx.scene.shape.Polyline;
import org.junit.Test;
import static test.com.sun.javafx.test.TestHelper.*;
import static org.junit.Assert.*;
public final class PolylineTest {
@Test public void testVarargConstructor() {
final StubPolyline polyline = new StubPolyline(1, 2, 3, 4);
assertEquals(4, polyline.getPoints().size());
assertEquals(1, polyline.getPoints().get(0), 0.0001);
assertEquals(2, polyline.getPoints().get(1), 0.0001);
assertEquals(3, polyline.getPoints().get(2), 0.0001);
assertEquals(4, polyline.getPoints().get(3), 0.0001);
}
@Test public void testPropertyPropagation_emptyPoints() {
final StubPolyline polyline = new StubPolyline();
NodeTest.callSyncPGNode(polyline);
assertPGPolylinePointsEquals(polyline, new double[0]);
}
@Test public void testPropertyPropagation_pointsEvenLength() {
final double[] initialPoints = { 10, 20, 100, 200, 200, 100, 50, 10 };
final StubPolyline polyline = new StubPolyline(initialPoints);
NodeTest.callSyncPGNode(polyline);
assertPGPolylinePointsEquals(polyline, initialPoints);
final ObservableList<Double> polylinePoints = polyline.getPoints();
polylinePoints.remove(1);
polylinePoints.remove(2);
NodeTest.callSyncPGNode(polyline);
assertPGPolylinePointsEquals(polyline, 10, 100, 200, 100, 50, 10);
}
@Test public void testPropertyPropagation_pointsOddLength() {
final double[] initialPoints = { 10, 20, 100, 200, 200 };
final StubPolyline polyline = new StubPolyline(initialPoints);
NodeTest.callSyncPGNode(polyline);
assertPGPolylinePointsEquals(polyline, initialPoints);
final ObservableList<Double> polylinePoints = polyline.getPoints();
polylinePoints.add(100.0);
polylinePoints.add(50.0);
NodeTest.callSyncPGNode(polyline);
assertPGPolylinePointsEquals(polyline, 10, 20, 100, 200, 200, 100, 50);
}
@Test public void testBounds_emptyPoints() {
final StubPolyline polyline = new StubPolyline();
assertBoundsEqual(box(0, 0, -1, -1), polyline.getBoundsInLocal());
}
@Test public void testBounds_evenPointsLength() {
final double[] initialPoints = { 100, 100, 200, 100, 200, 200 };
final StubPolyline polyline = new StubPolyline(initialPoints);
assertSimilar(box(100, 100, 100, 100), polyline.getBoundsInLocal());
final ObservableList<Double> polylinePoints = polyline.getPoints();
polylinePoints.add(200.0);
polylinePoints.add(300.0);
assertSimilar(box(100, 100, 100, 200), polyline.getBoundsInLocal());
}
@Test public void testBounds_oddPointsLength() {
final double[] initialPoints = {
100, 100, 200, 100, 200, 200, 200, 300
};
final StubPolyline polyline = new StubPolyline(initialPoints);
assertSimilar(box(100, 100, 100, 200), polyline.getBoundsInLocal());
final ObservableList<Double> polylinePoints = polyline.getPoints();
polylinePoints.remove(6);
assertSimilar(box(100, 100, 100, 100), polyline.getBoundsInLocal());
}
private static void assertPGPolylinePointsEquals(
final StubPolyline polyline,
final double... expectedPoints) {
final StubNGPolyline stubPolyline = NodeHelper.getPeer(polyline);
final float[] pgPoints = stubPolyline.points;
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
String s = new StubPolyline().toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
public static final class StubPolyline extends Polyline {
static {
StubPolylineHelper.setStubPolylineAccessor(new StubPolylineHelper.StubPolylineAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((StubPolyline) node).doCreatePeer();
}
});
}
{
StubPolylineHelper.initHelper(this);
}
public StubPolyline(double... initialPoints) {
super(initialPoints);
}
public StubPolyline() {
super();
}
private NGNode doCreatePeer() {
return new StubNGPolyline();
}
}
public static final class StubNGPolyline extends NGPolyline {
private float[] points;
@Override
public void updatePolyline(float[] points) {
super.updatePolyline(points);
this.points = points;
}
}
}
