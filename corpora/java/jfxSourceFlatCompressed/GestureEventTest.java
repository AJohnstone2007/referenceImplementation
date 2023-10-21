package test.javafx.scene.input;
import javafx.event.Event;
import javafx.geometry.Point3D;
import javafx.scene.input.GestureEvent;
import javafx.scene.input.GestureEvent;
import javafx.scene.input.GestureEventShim;
import javafx.scene.input.PickResult;
import javafx.scene.input.PickResult;
import javafx.scene.shape.Rectangle;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
public class GestureEventTest {
@Test public void testShortConstructor() {
Rectangle node = new Rectangle();
node.setTranslateX(3);
node.setTranslateY(2);
node.setTranslateZ(50);
PickResult pickRes = new PickResult(node, new Point3D(15, 25, 100), 33);
GestureEvent e = GestureEventShim.getGestureEvent(
GestureEvent.ANY, 10, 20, 30, 40,
false, true, false, true, false, true, pickRes);
assertSame(GestureEvent.ANY, e.getEventType());
assertSame(pickRes, e.getPickResult());
assertEquals(18, e.getX(), 10e-20);
assertEquals(27, e.getY(), 10e-20);
assertEquals(150, e.getZ(), 10e-20);
assertEquals(10, e.getSceneX(), 10e-20);
assertEquals(20, e.getSceneY(), 10e-20);
assertEquals(30, e.getScreenX(), 10e-20);
assertEquals(40, e.getScreenY(), 10e-20);
assertFalse(e.isShiftDown());
assertTrue(e.isControlDown());
assertFalse(e.isAltDown());
assertTrue(e.isMetaDown());
assertFalse(e.isDirect());
assertTrue(e.isInertia());
assertSame(Event.NULL_SOURCE_TARGET, e.getSource());
assertSame(Event.NULL_SOURCE_TARGET, e.getTarget());
assertFalse(e.isConsumed());
e = GestureEventShim.getGestureEvent(
GestureEvent.ANY, 10, 20, 30, 40,
true, false, true, false, true, false, null);
assertTrue(e.isShiftDown());
assertFalse(e.isControlDown());
assertTrue(e.isAltDown());
assertFalse(e.isMetaDown());
assertTrue(e.isDirect());
assertFalse(e.isInertia());
}
@Test public void testShortConstructorWithoutPickResult() {
GestureEvent e = GestureEventShim.getGestureEvent(
GestureEvent.ANY, 10, 20, 30, 40,
false, true, false, true, false, true, null);
assertEquals(10, e.getX(), 10e-20);
assertEquals(20, e.getY(), 10e-20);
assertEquals(0, e.getZ(), 10e-20);
assertEquals(10, e.getSceneX(), 10e-20);
assertEquals(20, e.getSceneY(), 10e-20);
assertEquals(30, e.getScreenX(), 10e-20);
assertEquals(40, e.getScreenY(), 10e-20);
assertNotNull(e.getPickResult());
assertNotNull(e.getPickResult().getIntersectedPoint());
assertEquals(10, e.getPickResult().getIntersectedPoint().getX(), 10e-20);
assertEquals(20, e.getPickResult().getIntersectedPoint().getY(), 10e-20);
assertEquals(0, e.getPickResult().getIntersectedPoint().getZ(), 10e-20);
assertSame(Event.NULL_SOURCE_TARGET, e.getSource());
assertSame(Event.NULL_SOURCE_TARGET, e.getTarget());
}
@Test public void testLongConstructor() {
Rectangle node = new Rectangle(10, 10);
node.setTranslateX(3);
node.setTranslateY(2);
node.setTranslateZ(50);
Rectangle n1 = new Rectangle(10, 10);
Rectangle n2 = new Rectangle(10, 10);
PickResult pickRes = new PickResult(node, new Point3D(15, 25, 100), 33);
GestureEvent e = GestureEventShim.getGestureEvent(
n1, n2,
GestureEvent.ANY, 10, 20, 30, 40,
false, true, false, true, false, true, pickRes);
assertSame(n1, e.getSource());
assertSame(n2, e.getTarget());
assertSame(GestureEvent.ANY, e.getEventType());
assertSame(pickRes, e.getPickResult());
assertEquals(18, e.getX(), 10e-20);
assertEquals(27, e.getY(), 10e-20);
assertEquals(150, e.getZ(), 10e-20);
assertEquals(10, e.getSceneX(), 10e-20);
assertEquals(20, e.getSceneY(), 10e-20);
assertEquals(30, e.getScreenX(), 10e-20);
assertEquals(40, e.getScreenY(), 10e-20);
assertFalse(e.isShiftDown());
assertTrue(e.isControlDown());
assertFalse(e.isAltDown());
assertTrue(e.isMetaDown());
assertFalse(e.isDirect());
assertTrue(e.isInertia());
assertFalse(e.isConsumed());
e = GestureEventShim.getGestureEvent(
n1, n2,
GestureEvent.ANY, 10, 20, 30, 40,
true, false, true, false, true, false, null);
assertTrue(e.isShiftDown());
assertFalse(e.isControlDown());
assertTrue(e.isAltDown());
assertFalse(e.isMetaDown());
assertTrue(e.isDirect());
assertFalse(e.isInertia());
}
@Test public void testLongConstructorWithoutPickResult() {
Rectangle n1 = new Rectangle(10, 10);
Rectangle n2 = new Rectangle(10, 10);
GestureEvent e = GestureEventShim.getGestureEvent(
n1, n2,
GestureEvent.ANY, 10, 20, 30, 40,
false, true, false, true, false, true, null);
assertSame(n1, e.getSource());
assertSame(n2, e.getTarget());
assertEquals(10, e.getX(), 10e-20);
assertEquals(20, e.getY(), 10e-20);
assertEquals(0, e.getZ(), 10e-20);
assertEquals(10, e.getSceneX(), 10e-20);
assertEquals(20, e.getSceneY(), 10e-20);
assertEquals(30, e.getScreenX(), 10e-20);
assertEquals(40, e.getScreenY(), 10e-20);
assertNotNull(e.getPickResult());
assertNotNull(e.getPickResult().getIntersectedPoint());
assertEquals(10, e.getPickResult().getIntersectedPoint().getX(), 10e-20);
assertEquals(20, e.getPickResult().getIntersectedPoint().getY(), 10e-20);
assertEquals(0, e.getPickResult().getIntersectedPoint().getZ(), 10e-20);
}
}
