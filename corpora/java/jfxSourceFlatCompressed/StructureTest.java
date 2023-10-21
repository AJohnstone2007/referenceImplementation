package test.javafx.scene;
import javafx.collections.ObservableList;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGGroup;
import com.sun.javafx.sg.prism.NGNode;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.NodeShim;
import javafx.scene.Parent;
import javafx.scene.ParentShim;
import javafx.scene.Scene;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import test.com.sun.javafx.scene.StubNodeHelper;
import test.com.sun.javafx.scene.StubParentHelper;
public class StructureTest {
@Rule
public ExpectedException thrown = ExpectedException.none();
int occurs(Node child, ObservableList<Node> content) {
int count = 0;
if (content != null) {
for (Node node : content) {
if (node == child) {
count++;
}
}
}
return count;
}
boolean occursOnce(Node child, ObservableList<Node> content) {
return 1 == occurs(child, content);
}
boolean occursZero(Node child, ObservableList<Node> content) {
return 0 == occurs(child, content);
}
boolean isChild(Node child, Group group) {
return occursOnce(child, ParentShim.getChildren(group));
}
boolean isRoot(Parent root, Scene scene) {
return root == scene.getRoot();
}
boolean notChild(Node child, Group group) {
return occursZero(child, ParentShim.getChildren(group));
}
boolean notRoot(Parent root, Scene scene) {
return root != scene.getRoot();
}
@Test
public void testOrphan() {
Node n = new StubNode();
assertNull("clipParent is null", NodeShim.getClipParent(n));
assertNull("parent is null", n.getParent());
assertNull("scene is null", n.getScene());
}
@Test
public void testSimpleCL() {
StubNode parent = new StubNode();
StubNode child = new StubNode();
parent.setClip(child);
assertSame("parent.clip is child", child, parent.getClip());
assertSame("child.clipParent is parent", parent, NodeShim.getClipParent(child));
assertNull("child.parent is null", child.getParent());
assertNull("scene is null", child.getScene());
}
@Test
public void testSimpleG() {
StubNode child = new StubNode();
Group group = new Group(child);
assertNull("group.clip is null", group.getClip());
assertTrue("isChild of group", isChild(child, group));
assertNull("child.clipParent is null", NodeShim.getClipParent(child));
assertSame("child.parent is parent", group, child.getParent());
assertNull("child.getScene() is null", child.getScene());
}
@Test
public void testSimpleS() {
StubParent root = new StubParent();
Scene scene = new Scene(root);
assertTrue("isChild of scene", isRoot(root, scene));
assertNull("child.clipParent is null", NodeShim.getClipParent(root));
assertSame("child.getScene() is scene", scene, root.getScene());
}
@Test
public void testSceneInsertGroup1() {
StubNode child = new StubNode();
Group group = new Group();
Scene scene = new Scene(group);
ParentShim.getChildren(group).add(child);
assertSame("group.getScene() is scene", scene, group.getScene());
assertSame("child.getScene() is scene", scene, child.getScene());
}
@Test
public void testSceneInsertGroup2() {
StubNode child = new StubNode();
Group group = new Group();
Scene scene = new Scene(group);
ParentShim.getChildren(group).add(child);
assertSame("group.getScene() is scene", scene, group.getScene());
assertSame("child.getScene() is scene", scene, child.getScene());
}
@Test public void testUnparentCL() {
StubNode child = new StubNode();
StubNode parent = new StubNode();
parent.setClip(child);
parent.setClip(null);
assertNull("parent.clip is null", parent.getClip());
assertNull("child.clipParent is null", NodeShim.getClipParent(child));
}
@Test public void testUnparentG() {
StubNode child = new StubNode();
Group parent = new Group(child);
ParentShim.getChildren(parent).remove(child);
assertEquals("parent.content is zero size", 0, ParentShim.getChildren(parent).size());
assertNull("child.parent is null", child.getParent());
}
@Test public void testSwitchCLCL() {
StubNode child = new StubNode();
StubNode p1 = new StubNode();
p1.setClip(child);
StubNode p2 = new StubNode();
thrown.expect(IllegalArgumentException.class);
try {
p2.setClip(child);
} catch (final IllegalArgumentException e) {
assertSame("p1.clip is child", child, p1.getClip());
assertNull("p2.clip is null", p2.getClip());
assertSame("child.clipParent is p1",
p1, NodeShim.getClipParent(child));
assertNull("child.parent is null", child.getParent());
assertNull("child.getScene() is null", child.getScene());
throw e;
}
}
@Test public void testSwitchCLG() {
StubNode child = new StubNode();
StubNode p1 = new StubNode();
p1.setClip(child);
Group p2 = new Group();
ObservableList<Node> content = ParentShim.getChildren(p2);
try {
content.add(child);
fail("IllegalArgument should have been thrown.");
} catch (IllegalArgumentException iae) {
}
assertSame("p1.clip is child", child, p1.getClip());
assertNull("p2.clip is null", p2.getClip());
assertTrue("notChild of p2", notChild(child, p2));
assertSame("child.clipParent is p1", p1, NodeShim.getClipParent(child));
assertNull("child.parent is null", child.getParent());
assertNull("child.getScene() is null", child.getScene());
}
@Test public void testSwitchCLS() {
StubParent clipNode = new StubParent();
StubNode p1 = new StubNode();
p1.setClip(clipNode);
try {
Scene p2 = new Scene(clipNode);
fail("IllegalArgument should have been thrown.");
} catch (Throwable t) {
}
assertSame("p1.clip is child", clipNode, p1.getClip());
assertSame("child.clipParent is p1", p1, NodeShim.getClipParent(clipNode));
assertNull("child.parent is null", clipNode.getParent());
assertNull("child.getScene() is null", clipNode.getScene());
}
@Test public void testSwitchGCL() {
StubNode child = new StubNode();
Group p1 = new Group(child);
StubNode p2 = new StubNode();
thrown.expect(IllegalArgumentException.class);
try {
p2.setClip(child);
} catch (final IllegalArgumentException e) {
assertNull("p1.clip is null", p1.getClip());
assertTrue("isChild of p1", isChild(child, p1));
assertNull("p2.clip is null", p2.getClip());
assertNull("child.clipParent is null", NodeShim.getClipParent(child));
assertSame("child.parent is p1", p1, child.getParent());
assertNull("child.getScene() is null", child.getScene());
throw e;
}
}
@Test public void testSwitchGGStopgap() {
StubNode child = new StubNode();
Group p1 = new Group(child);
Group p2 = new Group(child);
assertTrue("notChild of p1", notChild(child, p1));
assertTrue("isChild of p2", isChild(child, p2));
assertSame("child.parent is p2", p2, child.getParent());
}
@Test public void testSwitchSCL() {
StubParent root = new StubParent();
Scene scene = new Scene(root);
StubNode p2 = new StubNode();
thrown.expect(IllegalArgumentException.class);
try {
p2.setClip(root);
} catch (final IllegalArgumentException e) {
assertTrue("isRoot of scene", isRoot(root, scene));
assertNull("p2.clip is null", p2.getClip());
assertNull("root.clipParent is null", NodeShim.getClipParent(root));
assertSame("root.getScene() is scene", scene, root.getScene());
throw e;
}
}
@Test public void testGroupInsert() {
StubNode n0 = new StubNode();
n0.setId("n0");
StubNode n1 = new StubNode();
n1.setId("n1");
StubNode n2 = new StubNode();
n2.setId("n2");
Group g = new Group(n0, n1, n2);
ObservableList<Node> content = ParentShim.getChildren(g);
try {
content.add(n1);
fail("IllegalArgument should have been thrown.");
} catch (IllegalArgumentException iae) {
}
assertEquals("g.content is size 3", 3, ParentShim.getChildren(g).size());
assertSame("g.content[0] is n0", n0, ParentShim.getChildren(g).get(0));
assertSame("g.content[1] is n1", n1, ParentShim.getChildren(g).get(1));
assertSame("g.content[2] is n2", n2, ParentShim.getChildren(g).get(2));
}
@Test public void testGroupReplace1() {
StubNode n0 = new StubNode();
n0.setId("n0");
StubNode n1 = new StubNode();
n1.setId("n1");
StubNode n2 = new StubNode();
n2.setId("n2");
Group g = new Group(n0, n1, n2);
ParentShim.getChildren(g).remove(1);
ObservableList<Node> n = javafx.collections.FXCollections.<Node>observableArrayList();
n.addAll(n1,n1);
ObservableList<Node> content = ParentShim.getChildren(g);
try {
content.addAll(1, n);
fail("IllegalArgument should have been thrown.");
} catch (IllegalArgumentException iae) {
}
assertEquals("g.content is size 2", 2, ParentShim.getChildren(g).size());
assertSame("g.content[0] is n0", n0, ParentShim.getChildren(g).get(0));
assertSame("g.content[1] is n2", n2, ParentShim.getChildren(g).get(1));
}
@Test public void testGroupReplace2() {
StubNode n0 = new StubNode();
n0.setId("n0");
StubNode n1 = new StubNode();
n1.setId("n1");
StubNode n2 = new StubNode();
n2.setId("n2");
Group g = new Group(n0, n1, n2);
try {
ParentShim.getChildren(g).set(1, n0);
fail("No exception thrown.");
} catch (IllegalArgumentException e) {
}
assertEquals("g.content is size 3", 3, ParentShim.getChildren(g).size());
assertSame("g.content[0] is n0", n0, ParentShim.getChildren(g).get(0));
assertSame("g.content[1] is n1", n1, ParentShim.getChildren(g).get(1));
assertSame("g.content[2] is n2", n2, ParentShim.getChildren(g).get(2));
}
@Test public void testGroupReplace3() {
StubNode n0 = new StubNode();
n0.setId("n0");
StubNode n1 = new StubNode();
n1.setId("n1");
StubNode n2 = new StubNode();
n2.setId("n2");
Group g = new Group(n0, n1, n2);
ParentShim.getChildren(g).set(1, n1);
ParentShim.getChildren(g).set(2, n2);
assertEquals("g.content is size 3", 3, ParentShim.getChildren(g).size());
assertSame("g.content[0] is n0", n0, ParentShim.getChildren(g).get(0));
assertSame("g.content[1] is n1", n1, ParentShim.getChildren(g).get(1));
assertSame("g.content[2] is n2", n2, ParentShim.getChildren(g).get(2));
}
@Test public void testCircularCLCL() {
StubNode node1 = new StubNode();
StubNode node2 = new StubNode();
node2.setClip(node1);
thrown.expect(IllegalArgumentException.class);
try {
node1.setClip(node2);
} catch (final IllegalArgumentException e) {
assertNull("node1.clip is null", node1.getClip());
assertSame("node1.clipParent is node2",
node2,
NodeShim.getClipParent(node1));
assertSame("node2.clip is node1", node1, node2.getClip());
assertNull("node2.clipParent is null", NodeShim.getClipParent(node2));
throw e;
}
}
@Test public void testCircularCLG() {
StubNode node1 = new StubNode();
Group node2 = new Group(node1);
thrown.expect(IllegalArgumentException.class);
try {
node1.setClip(node2);
} catch (final IllegalArgumentException e) {
assertNull("node1.clip is null", node1.getClip());
assertNull("node1.clipParent is null", NodeShim.getClipParent(node1));
assertSame("node1.parent is node2", node2, node1.getParent());
assertNull("node2.clip is null", node2.getClip());
assertNull("node2.clipParent is null", NodeShim.getClipParent(node2));
assertTrue("node1 is child of node2", isChild(node1, node2));
throw e;
}
}
@Test public void testCircularGCL() {
Group node1 = new Group();
StubNode node2 = new StubNode();
node2.setClip(node1);
ObservableList<Node> content = ParentShim.getChildren(node1);
try {
content.add(node2);
fail("IllegalArgument should have been thrown.");
} catch (IllegalArgumentException iae) {
}
assertNull("node1.clip is null", node1.getClip());
assertSame("node1.clipParent is node2", node2, NodeShim.getClipParent(node1));
assertTrue("node2 is not child of node1", notChild(node2, node1));
assertSame("node2.clip is node1", node1, node2.getClip());
assertNull("node2.clipParent is null", NodeShim.getClipParent(node2));
assertNull("node2.parent is null", node2.getParent());
}
@Test public void testCircularGG() {
Group node1 = new Group();
Group node2 = new Group(node1);
ObservableList<Node> content = ParentShim.getChildren(node1);
try {
content.add(node2);
fail("IllegalArgument should have been thrown.");
} catch (IllegalArgumentException iae) {
}
assertSame("node1.parent is node2", node2, node1.getParent());
assertTrue("node2 is not a child of node1", notChild(node2, node1));
assertNull("node2.parent is null", node2.getParent());
assertTrue("node1 is child of node2", isChild(node1, node2));
}
@Test public void testCircularSelfCL() {
StubNode node1 = new StubNode();
thrown.expect(IllegalArgumentException.class);
try {
node1.setClip(node1);
} catch (final IllegalArgumentException e) {
assertNull("node1.clip is null", node1.getClip());
assertNull("node1.clipParent is null", NodeShim.getClipParent(node1));
throw e;
}
}
@Test public void testCircularSelfG() {
Group node1 = new Group();
ObservableList<Node> content = ParentShim.getChildren(node1);
try {
content.add(node1);
fail("IllegalArgument should have been thrown.");
} catch (IllegalArgumentException iae) {
}
assertTrue("node1 is not a child of itself", notChild(node1, node1));
assertNull("node1.parent is null", node1.getParent());
}
public static final class StubNode extends Node {
static {
StubNodeHelper.setStubNodeAccessor(new StubNodeHelper.StubNodeAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((StubNode) node).doCreatePeer();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((StubNode) node).doComputeGeomBounds(bounds, tx);
}
@Override
public boolean doComputeContains(Node node, double localX, double localY) {
return ((StubNode) node).doComputeContains(localX, localY);
}
});
}
{
StubNodeHelper.initHelper(this);
}
public StubNode() {
super();
}
private NGNode doCreatePeer() {
return new NGGroup();
}
private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) {
return bounds;
}
private boolean doComputeContains(double localX, double localY) {
return false;
}
}
public static final class StubParent extends Parent {
static {
StubParentHelper.setStubParentAccessor(new StubParentHelper.StubParentAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((StubParent) node).doCreatePeer();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((StubParent) node).doComputeGeomBounds(bounds, tx);
}
@Override
public boolean doComputeContains(Node node, double localX, double localY) {
return ((StubParent) node).doComputeContains(localX, localY);
}
});
}
{
StubParentHelper.initHelper(this);
}
public StubParent() {
super();
}
private NGNode doCreatePeer() {
return new NGGroup();
}
private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) {
return bounds;
}
private boolean doComputeContains(double localX, double localY) {
return false;
}
}
}
