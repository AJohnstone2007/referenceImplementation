package test.javafx.scene;
import com.sun.javafx.scene.SceneHelper;
import test.com.sun.javafx.pgstub.StubScene;
import test.com.sun.javafx.pgstub.StubToolkit;
import com.sun.javafx.tk.Toolkit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.ParentShim;
import javafx.scene.Scene;
import javafx.scene.SceneShim;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
public class FocusTest {
private Stage stage;
private Scene scene;
private List<Node> nodes;
private int nodeIndex;
private StubToolkit toolkit;
private boolean actionTaken;
@Before
public void setUp() {
stage = new Stage();
scene = new Scene(new Group(), 500, 500);
stage.setScene(scene);
stage.show();
stage.requestFocus();
nodes = new ArrayList();
nodeIndex = 0;
toolkit = (StubToolkit) Toolkit.getToolkit();
}
@After
public void tearDown() {
stage.hide();
stage = null;
scene = null;
}
void fireTestPulse() {
SceneShim.focusCleanup(scene);
}
boolean T = true;
boolean F = false;
Node n(boolean trav, boolean vis, boolean enable) {
Rectangle node = new Rectangle();
node.setId("Rect-" + nodeIndex);
node.setFocusTraversable(trav);
node.setVisible(vis);
node.setDisable(!enable);
nodes.add(node);
nodeIndex++;
return node;
}
Node n() {
return n(T, T, T);
}
private void assertIsFocused(Scene s, Node n) {
assertEquals(n, s.getFocusOwner());
assertTrue(n.isFocused());
}
private void assertNotFocused(Scene s, Node n) {
assertTrue(n != s.getFocusOwner());
assertFalse(n.isFocused());
}
private void assertNullFocus(Scene s) {
assertNull(s.getFocusOwner());
}
@Test
public void testInitial() {
assertNullFocus(scene);
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).add(n());
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
}
@Test
public void testRequest() {
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).addAll(
n(T, T, T),
n(F, T, T),
n(T, T, F),
n(F, T, F),
n(T, F, T),
n(F, F, T),
n(T, F, F),
n(F, F, F)
);
n();
nodes.get(0).requestFocus();
assertIsFocused(scene, nodes.get(0));
nodes.get(1).requestFocus();
assertIsFocused(scene, nodes.get(1));
nodes.get(2).requestFocus();
assertNotFocused(scene, nodes.get(2));
nodes.get(3).requestFocus();
assertNotFocused(scene, nodes.get(3));
nodes.get(4).requestFocus();
assertNotFocused(scene, nodes.get(4));
nodes.get(5).requestFocus();
assertNotFocused(scene, nodes.get(5));
nodes.get(6).requestFocus();
assertNotFocused(scene, nodes.get(6));
nodes.get(7).requestFocus();
assertNotFocused(scene, nodes.get(7));
nodes.get(8).requestFocus();
assertNotFocused(scene, nodes.get(8));
}
@Test
public void testRemove1() {
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).add(n());
nodes.get(0).requestFocus();
ParentShim.getChildren(scene.getRoot()).remove(nodes.get(0));
fireTestPulse();
assertNotFocused(scene, nodes.get(0));
assertNullFocus(scene);
}
@Test
public void testRemove2() {
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).addAll(n(), n());
nodes.get(0).requestFocus();
ParentShim.getChildren(scene.getRoot()).remove(nodes.get(0));
fireTestPulse();
assertNotFocused(scene, nodes.get(0));
assertIsFocused(scene, nodes.get(1));
}
@Test
public void testRemove_ClearsFocusOnRemovedNode1() {
Node n = n();
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).add(n);
n.requestFocus();
ParentShim.getChildren(scene.getRoot()).remove(n);
fireTestPulse();
assertNotFocused(scene, n);
}
@Test
public void testRemove_ClearsFocusOnRemovedNode2() {
Node n = n();
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).addAll(n, n());
n.requestFocus();
ParentShim.getChildren(scene.getRoot()).remove(n);
fireTestPulse();
assertNotFocused(scene, n);
assertIsFocused(scene, ParentShim.getChildren(scene.getRoot()).get(0));
}
@Test
public void testRemoveChildOfGroup_ClearsFocusOnRemovedNode1() {
Node n = n();
Group g = new Group(n);
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).add(g);
n.requestFocus();
ParentShim.getChildren(g).remove(n);
fireTestPulse();
assertNotFocused(scene, n);
}
@Test
public void testInvisible1() {
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).add(n());
nodes.get(0).requestFocus();
assertIsFocused(scene, nodes.get(0));
nodes.get(0).setVisible(false);
fireTestPulse();
assertNotFocused(scene, nodes.get(0));
assertNullFocus(scene);
}
@Test
public void testInvisible2() {
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).addAll(n(), n());
nodes.get(0).requestFocus();
assertIsFocused(scene, nodes.get(0));
nodes.get(0).setVisible(false);
fireTestPulse();
assertNotFocused(scene, nodes.get(0));
assertIsFocused(scene, nodes.get(1));
}
@Test
public void testDisable1() {
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).add(n());
nodes.get(0).requestFocus();
nodes.get(0).setDisable(true);
fireTestPulse();
assertNotFocused(scene, nodes.get(0));
assertNullFocus(scene);
}
@Test
public void testDisable2() {
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).addAll(n(), n());
nodes.get(0).requestFocus();
nodes.get(0).setDisable(true);
fireTestPulse();
assertNotFocused(scene, nodes.get(0));
assertIsFocused(scene, nodes.get(1));
}
@Test
public void testAddEligible() {
fireTestPulse();
assertNullFocus(scene);
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).add(n());
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
}
@Test
public void testBecomeTraversable() {
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).addAll(n(F, T, T), n(F, T, T));
fireTestPulse();
assertNullFocus(scene);
toolkit.clearPulseRequested();
assertFalse(toolkit.isPulseRequested());
nodes.get(0).setFocusTraversable(true);
assertTrue(toolkit.isPulseRequested());
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
}
@Test
public void testBecomeVisible() {
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).add(n(T, F, T));
fireTestPulse();
assertNullFocus(scene);
nodes.get(0).setVisible(true);
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
}
@Test
public void testBecomeEnabled() {
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).add(n(T, T, F));
fireTestPulse();
assertNullFocus(scene);
nodes.get(0).setDisable(false);
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
}
@Test
public void testAddEligible2() {
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).add(n());
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
ParentShim.getChildren(scene.getRoot()).add(n());
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
}
@Test
public void testBecomeTraversable2() {
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).addAll(n(), n(F, T, T));
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
nodes.get(1).setFocusTraversable(true);
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
}
@Test
public void testBecomeVisible2() {
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).addAll(n(), n(T, F, T));
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
nodes.get(1).setVisible(true);
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
}
@Test
public void testBecomeEnabled2() {
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).addAll(n(), n(T, T, F));
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
nodes.get(1).setDisable(false);
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
}
@Test
public void testMoveWithinScene() {
Group g1 = new Group(n());
Group g2 = new Group();
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).addAll(g1, g2);
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
ParentShim.getChildren(g2).add(nodes.get(0));
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
}
@Test
public void testMoveIntoInvisible() {
Group g1 = new Group(n());
Group g2 = new Group();
g2.setVisible(false);
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).addAll(g1, g2);
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
ParentShim.getChildren(g2).add(nodes.get(0));
fireTestPulse();
assertNullFocus(scene);
}
@Test
public void testMoveIntoInvisible2() {
Group g1 = new Group(n());
Group g2 = new Group();
g2.setVisible(false);
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).addAll(g1, g2, n());
nodes.get(0).requestFocus();
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
ParentShim.getChildren(g2).add(nodes.get(0));
fireTestPulse();
assertIsFocused(scene, nodes.get(1));
}
@Test
public void testMoveIntoDisabled() {
Group g1 = new Group(n());
Group g2 = new Group();
g2.setDisable(true);
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).addAll(g1, g2);
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
ParentShim.getChildren(g2).add(nodes.get(0));
fireTestPulse();
assertNullFocus(scene);
}
@Test
public void testMoveIntoDisabled2() {
Group g1 = new Group(n());
Group g2 = new Group();
g2.setDisable(true);
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).addAll(g1, g2, n());
nodes.get(0).requestFocus();
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
ParentShim.getChildren(g2).add(nodes.get(0));
fireTestPulse();
assertIsFocused(scene, nodes.get(1));
}
@Test
public void testMakeParentDisabled() {
Group g1 = new Group(n());
Group g2 = new Group();
g2.setVisible(false);
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).addAll(g1, g2);
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
g1.setDisable(true);
fireTestPulse();
assertNotFocused(scene, nodes.get(0));
assertNullFocus(scene);
}
@Test
public void testMakeParentDisabled2() {
Group g1 = new Group(n());
Group g2 = new Group();
g2.setVisible(false);
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).addAll(g1, g2, n());
nodes.get(0).requestFocus();
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
g1.setDisable(true);
fireTestPulse();
assertNotFocused(scene, nodes.get(0));
assertIsFocused(scene, nodes.get(1));
}
@Test
public void testMakeParentInvisible() {
Group g1 = new Group(n());
Group g2 = new Group();
g2.setVisible(false);
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).addAll(g1, g2);
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
g1.setVisible(false);
fireTestPulse();
assertNotFocused(scene, nodes.get(0));
assertNullFocus(scene);
}
@Test
public void testMakeParentInvisible2() {
Group g1 = new Group(n());
Group g2 = new Group();
g2.setVisible(false);
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).addAll(g1, g2, n());
nodes.get(0).requestFocus();
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
g1.setVisible(false);
fireTestPulse();
assertNotFocused(scene, nodes.get(0));
assertIsFocused(scene, nodes.get(1));
}
@Test
public void testToFront() {
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).addAll(n(), n());
nodes.get(0).requestFocus();
assertIsFocused(scene, nodes.get(0));
assertNotFocused(scene, nodes.get(1));
nodes.get(0).toFront();
assertIsFocused(scene, nodes.get(0));
assertNotFocused(scene, nodes.get(1));
nodes.get(0).toBack();
assertIsFocused(scene, nodes.get(0));
assertNotFocused(scene, nodes.get(1));
}
@Test
public void testMoveIntoInactiveScene() {
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).add(n());
nodes.get(0).requestFocus();
assertIsFocused(scene, nodes.get(0));
Scene scene2 = new Scene(new Group());
ParentShim.getChildren(scene2.getRoot()).add(nodes.get(0));
fireTestPulse();
assertNullFocus(scene);
assertNullFocus(scene2);
nodes.get(0).requestFocus();
fireTestPulse();
assertNullFocus(scene);
assertEquals(nodes.get(0), scene2.getFocusOwner());
assertFalse(nodes.get(0).isFocused());
stage.setScene(scene2);
fireTestPulse();
assertNullFocus(scene);
assertIsFocused(scene2, nodes.get(0));
}
@Test
public void testInvisibleStage() {
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).add(n());
nodes.get(0).requestFocus();
stage.show();
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
stage.hide();
nodes.get(0).requestFocus();
fireTestPulse();
assertEquals(nodes.get(0), scene.getFocusOwner());
assertFalse(nodes.get(0).isFocused());
}
@Test
public void testSwitchScenes(){
scene.setRoot(new Group());
ParentShim.getChildren(scene.getRoot()).add(n());
nodes.get(0).requestFocus();
Scene scene2 = new Scene(new Group());
ParentShim.getChildren(scene2.getRoot()).add(n());
nodes.get(1).requestFocus();
fireTestPulse();
assertIsFocused(scene, nodes.get(0));
assertFalse(nodes.get(1).isFocused());
assertEquals(nodes.get(1), scene2.getFocusOwner());
stage.setScene(scene2);
fireTestPulse();
assertFalse(nodes.get(0).isFocused());
assertEquals(nodes.get(0), scene.getFocusOwner());
assertIsFocused(scene2, nodes.get(1));
}
@Test public void nestedFocusRequestsShouldResultInOneFocusedNode() {
final Node n1 = n();
final Node n2 = n();
scene.setRoot(new Group(n1, n2));
n1.focusedProperty().addListener((ov, lostFocus, getFocus) -> {
if (lostFocus) {
n1.requestFocus();
}
});
n2.focusedProperty().addListener(o -> {
assertTrue(n1.isFocused());
assertFalse(n2.isFocused());
});
n2.focusedProperty().addListener((ov, lostFocus, getFocus) -> fail("n2 should never get focus"));
stage.show();
n1.requestFocus();
assertTrue(n1.isFocused());
assertFalse(n2.isFocused());
n2.requestFocus();
assertTrue(n1.isFocused());
assertFalse(n2.isFocused());
}
@Test public void shouldCancelInputMethodWhenLoosingFocus() {
final Node n1 = n();
final Node n2 = n();
scene.setRoot(new Group(n1, n2));
stage.show();
Toolkit.getToolkit().firePulse();
n1.requestFocus();
assertSame(n1, scene.getFocusOwner());
actionTaken = false;
((StubScene) SceneHelper.getPeer(scene)).setInputMethodCompositionFinishDelegate(
() -> {
assertSame(n1, scene.getFocusOwner());
actionTaken = true;
}
);
n2.requestFocus();
((StubScene) SceneHelper.getPeer(scene)).setInputMethodCompositionFinishDelegate(
null);
assertSame(n2, scene.getFocusOwner());
assertTrue(actionTaken);
}
}
