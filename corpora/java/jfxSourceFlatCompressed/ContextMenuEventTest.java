package test.javafx.scene.input;
import com.sun.javafx.scene.SceneHelper;
import test.com.sun.javafx.pgstub.StubScene;
import javafx.event.Event;
import javafx.scene.Group;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.PickResult;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
public class ContextMenuEventTest {
@Test public void testShortConstructor() {
Rectangle node = new Rectangle(10, 10);
node.setTranslateX(3);
node.setTranslateY(2);
node.setTranslateZ(50);
PickResult pickRes = new PickResult(node, new Point3D(15, 25, 100), 33);
ContextMenuEvent e = new ContextMenuEvent(
ContextMenuEvent.CONTEXT_MENU_REQUESTED, 10, 20, 30, 40,
false, pickRes);
assertSame(ContextMenuEvent.CONTEXT_MENU_REQUESTED, e.getEventType());
assertEquals(18, e.getX(), 10e-20);
assertEquals(27, e.getY(), 10e-20);
assertEquals(150, e.getZ(), 10e-20);
assertEquals(10, e.getSceneX(), 10e-20);
assertEquals(20, e.getSceneY(), 10e-20);
assertEquals(30, e.getScreenX(), 10e-20);
assertEquals(40, e.getScreenY(), 10e-20);
assertFalse(e.isKeyboardTrigger());
assertFalse(e.isConsumed());
assertSame(pickRes, e.getPickResult());
assertSame(Event.NULL_SOURCE_TARGET, e.getSource());
assertSame(Event.NULL_SOURCE_TARGET, e.getTarget());
e = new ContextMenuEvent(
ContextMenuEvent.CONTEXT_MENU_REQUESTED, 10, 20, 30, 40,
true, pickRes);
assertTrue(e.isKeyboardTrigger());
}
@Test public void testShortConstructorWithoutPickResult() {
ContextMenuEvent e = new ContextMenuEvent(
ContextMenuEvent.CONTEXT_MENU_REQUESTED, 10, 20, 30, 40,
false, null);
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
ContextMenuEvent e = new ContextMenuEvent(n1, n2,
ContextMenuEvent.CONTEXT_MENU_REQUESTED, 10, 20, 30, 40,
false, pickRes);
assertSame(n1, e.getSource());
assertSame(n2, e.getTarget());
assertSame(ContextMenuEvent.CONTEXT_MENU_REQUESTED, e.getEventType());
assertEquals(18, e.getX(), 10e-20);
assertEquals(27, e.getY(), 10e-20);
assertEquals(150, e.getZ(), 10e-20);
assertEquals(10, e.getSceneX(), 10e-20);
assertEquals(20, e.getSceneY(), 10e-20);
assertEquals(30, e.getScreenX(), 10e-20);
assertEquals(40, e.getScreenY(), 10e-20);
assertFalse(e.isKeyboardTrigger());
assertFalse(e.isConsumed());
assertSame(pickRes, e.getPickResult());
e = new ContextMenuEvent(
ContextMenuEvent.CONTEXT_MENU_REQUESTED, 10, 20, 30, 40,
true, null);
assertTrue(e.isKeyboardTrigger());
}
@Test public void testLongConstructorWithoutPickResult() {
Rectangle n1 = new Rectangle(10, 10);
Rectangle n2 = new Rectangle(10, 10);
ContextMenuEvent e = new ContextMenuEvent(n1, n2,
ContextMenuEvent.CONTEXT_MENU_REQUESTED, 10, 20, 30, 40,
false, null);
assertSame(n1, e.getSource());
assertSame(n2, e.getTarget());
assertSame(ContextMenuEvent.CONTEXT_MENU_REQUESTED, e.getEventType());
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
@Test public void mouseTriggerKeepsCoordinates() {
Rectangle rect = new Rectangle(100, 100);
rect.setTranslateX(100);
rect.setTranslateY(100);
Group root = new Group(rect);
Scene scene = new Scene(root);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
rect.requestFocus();
rect.setOnContextMenuRequested(event -> {
Assert.assertEquals(1.0, event.getX(), 0.0001);
Assert.assertEquals(101, event.getSceneX(), 0.0001);
Assert.assertEquals(201, event.getScreenX(), 0.0001);
Assert.assertEquals(2.0, event.getY(), 0.0001);
Assert.assertEquals(102, event.getSceneY(), 0.0001);
Assert.assertEquals(202, event.getScreenY(), 0.0001);
assertFalse(event.isKeyboardTrigger());
});
((StubScene) SceneHelper.getPeer(scene)).getListener().menuEvent(
101, 102, 201, 202, false);
}
@Test public void keyTriggerSetsCoordinatesToFocusOwner() {
Rectangle rect = new Rectangle(100, 100);
rect.setTranslateX(100);
rect.setTranslateY(100);
Group root = new Group(rect);
Scene scene = new Scene(root);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
rect.requestFocus();
rect.setOnContextMenuRequested(event -> {
Assert.assertEquals(25.0, event.getX(), 0.0001);
Assert.assertEquals(125, event.getSceneX(), 0.0001);
Assert.assertEquals(225, event.getScreenX(), 0.0001);
Assert.assertEquals(50.0, event.getY(), 0.0001);
Assert.assertEquals(150, event.getSceneY(), 0.0001);
Assert.assertEquals(250, event.getScreenY(), 0.0001);
assertTrue(event.isKeyboardTrigger());
});
((StubScene) SceneHelper.getPeer(scene)).getListener().menuEvent(
101, 102, 201, 202, true);
}
@Test
public void shouldCompute3dCoordinates() {
Rectangle rect = new Rectangle(100, 100);
rect.setTranslateX(100);
rect.setTranslateY(100);
rect.setTranslateZ(50);
Group root = new Group(rect, new Rectangle(0, 0));
Scene scene = new Scene(root);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
rect.requestFocus();
rect.setOnContextMenuRequested(event -> {
Assert.assertEquals(1.0, event.getX(), 0.0001);
Assert.assertEquals(101, event.getSceneX(), 0.0001);
Assert.assertEquals(201, event.getScreenX(), 0.0001);
Assert.assertEquals(2.0, event.getY(), 0.0001);
Assert.assertEquals(102, event.getSceneY(), 0.0001);
Assert.assertEquals(202, event.getScreenY(), 0.0001);
Assert.assertEquals(0, event.getZ(), 0.0001);
assertFalse(event.isKeyboardTrigger());
});
scene.setOnContextMenuRequested(event -> {
Assert.assertEquals(101.0, event.getX(), 0.0001);
Assert.assertEquals(101, event.getSceneX(), 0.0001);
Assert.assertEquals(201, event.getScreenX(), 0.0001);
Assert.assertEquals(102.0, event.getY(), 0.0001);
Assert.assertEquals(102, event.getSceneY(), 0.0001);
Assert.assertEquals(202, event.getScreenY(), 0.0001);
Assert.assertEquals(50, event.getZ(), 0.0001);
assertFalse(event.isKeyboardTrigger());
});
((StubScene) SceneHelper.getPeer(scene)).getListener().menuEvent(
101, 102, 201, 202, false);
}
@Test public void pickResultIsFromEventCoordinates() {
final Rectangle rect = new Rectangle(100, 100);
rect.setTranslateX(100);
rect.setTranslateY(100);
Group root = new Group(rect);
Scene scene = new Scene(root);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
rect.requestFocus();
rect.setOnContextMenuRequested(event -> {
PickResult pickRes = event.getPickResult();
assertNotNull(pickRes);
assertSame(rect, pickRes.getIntersectedNode());
assertEquals(25, pickRes.getIntersectedPoint().getX(), 0.00001);
assertEquals(50, pickRes.getIntersectedPoint().getY(), 0.00001);
assertEquals(0, pickRes.getIntersectedPoint().getZ(), 0.00001);
});
((StubScene) SceneHelper.getPeer(scene)).getListener().menuEvent(
1330, 1350, 1340, 1360, true);
}
}
