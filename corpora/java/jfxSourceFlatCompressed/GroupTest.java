package test.javafx.scene;
import com.sun.javafx.scene.GroupHelper;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;
import java.util.HashSet;
import java.util.Set;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.ParentShim;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
public class GroupTest {
void checkSGConsistency(Group g) {
}
@Test
public void testVarArgConstructor() {
Rectangle r1 = new Rectangle();
Rectangle r2 = new Rectangle();
Group g = new Group(r1, r2);
assertTrue(ParentShim.getChildren(g).contains(r1));
assertTrue(ParentShim.getChildren(g).contains(r2));
}
@Test
public void testCollectionConstructor() {
Rectangle r1 = new Rectangle();
Rectangle r2 = new Rectangle();
Set s = new HashSet();
s.add(r1);
s.add(r2);
Group g = new Group(s);
assertTrue(ParentShim.getChildren(g).contains(r1));
assertTrue(ParentShim.getChildren(g).contains(r2));
}
@Test
public void testCyclicGraph() {
Group group1 = new Group();
Group group2 = new Group();
assertNull(group1.getParent());
assertNull(group2.getParent());
assertEquals(0, ParentShim.getChildren(group1).size());
assertEquals(0, ParentShim.getChildren(group2).size());
checkSGConsistency(group1);
checkSGConsistency(group2);
ParentShim.getChildren(group1).add(group2);
assertNull(group1.getParent());
assertEquals(group1, group2.getParent());
assertEquals(1, ParentShim.getChildren(group1).size());
assertEquals(0, ParentShim.getChildren(group2).size());
checkSGConsistency(group1);
checkSGConsistency(group2);
ObservableList<Node> content = ParentShim.getChildren(group2);
try {
content.add(group1);
fail("IllegalArgument should have been thrown.");
} catch (IllegalArgumentException iae) {
}
assertNull(group1.getParent());
assertEquals(group1, group2.getParent());
assertEquals(1, ParentShim.getChildren(group1).size());
assertEquals(0, ParentShim.getChildren(group2).size());
checkSGConsistency(group1);
checkSGConsistency(group2);
}
@Test
public void testAddRemove() {
Group group = new Group();
Rectangle node = new Rectangle();
assertEquals(0, ParentShim.getChildren(group).size());
assertNull(node.getParent());
assertNull(group.getParent());
checkSGConsistency(group);
ParentShim.getChildren(group).add(node);
assertEquals(1, ParentShim.getChildren(group).size());
assertEquals(node, ParentShim.getChildren(group).get(0));
assertEquals(group, node.getParent());
assertNull(group.getParent());
checkSGConsistency(group);
ParentShim.getChildren(group).remove(node);
assertEquals(0, ParentShim.getChildren(group).size());
assertNull(node.getParent());
assertNull(group.getParent());
checkSGConsistency(group);
}
@Test
public void testAddRemove2() {
Group group = new Group();
Rectangle node1 = new Rectangle();
node1.setX(1);
Rectangle node2 = new Rectangle();
node2.setX(2);
assertEquals(0, ParentShim.getChildren(group).size());
assertNull(node1.getParent());
assertNull(node2.getParent());
assertNull(group.getParent());
checkSGConsistency(group);
ParentShim.getChildren(group).add(node1);
assertEquals(1, ParentShim.getChildren(group).size());
assertEquals(node1, ParentShim.getChildren(group).get(0));
assertNull(node2.getParent());
assertEquals(group, node1.getParent());
assertNull(group.getParent());
checkSGConsistency(group);
ParentShim.getChildren(group).add(node2);
assertEquals(2, ParentShim.getChildren(group).size());
assertEquals(node1, ParentShim.getChildren(group).get(0));
assertEquals(node2, ParentShim.getChildren(group).get(1));
assertEquals(group, node1.getParent());
assertEquals(group, node2.getParent());
assertNull(group.getParent());
checkSGConsistency(group);
ParentShim.getChildren(group).remove(node1);
assertEquals(1, ParentShim.getChildren(group).size());
assertNull(node1.getParent());
assertEquals(group, node2.getParent());
assertEquals(node2, ParentShim.getChildren(group).get(0));
assertNull(group.getParent());
checkSGConsistency(group);
ParentShim.getChildren(group).add(node1);
assertEquals(2, ParentShim.getChildren(group).size());
assertEquals(node1, ParentShim.getChildren(group).get(1));
assertEquals(node2, ParentShim.getChildren(group).get(0));
assertEquals(group, node1.getParent());
assertEquals(group, node2.getParent());
assertNull(group.getParent());
checkSGConsistency(group);
Rectangle node3 = new Rectangle();
node3.setX(3);
Rectangle node4 = new Rectangle();
node4.setX(4);
assertNull(node3.getParent());
assertNull(node4.getParent());
assertNull(group.getParent());
checkSGConsistency(group);
ParentShim.getChildren(group).add(0, node3);
ParentShim.getChildren(group).add(node4);
assertEquals(4, ParentShim.getChildren(group).size());
assertEquals(node1, ParentShim.getChildren(group).get(2));
assertEquals(node2, ParentShim.getChildren(group).get(1));
assertEquals(node3, ParentShim.getChildren(group).get(0));
assertEquals(node4, ParentShim.getChildren(group).get(3));
assertEquals(group, node1.getParent());
assertEquals(group, node2.getParent());
assertEquals(group, node3.getParent());
assertEquals(group, node4.getParent());
assertNull(group.getParent());
checkSGConsistency(group);
ParentShim.getChildren(group).clear();
assertEquals(0, ParentShim.getChildren(group).size());
assertNull(node1.getParent());
assertNull(node2.getParent());
assertNull(node3.getParent());
assertNull(node4.getParent());
assertNull(group.getParent());
checkSGConsistency(group);
}
@Test
public void testMultiInit() {
Rectangle node = new Rectangle();
assertNull(node.getParent());
Group group1 = new Group();
ParentShim.getChildren(group1).add(node);
assertEquals(1, ParentShim.getChildren(group1).size());
assertEquals(node, ParentShim.getChildren(group1).get(0));
assertEquals(group1, node.getParent());
assertNull(group1.getParent());
checkSGConsistency(group1);
Group group2 = new Group();
try {
ParentShim.getChildren(group2).add(node);
} catch (Throwable t) {
assertNull("unexpected exception", t);
}
assertEquals(0, ParentShim.getChildren(group1).size());
assertEquals(1, ParentShim.getChildren(group2).size());
assertEquals(node, ParentShim.getChildren(group2).get(0));
assertSame(group2, node.getParent());
assertNull(group1.getParent());
assertNull(group2.getParent());
checkSGConsistency(group1);
checkSGConsistency(group2);
}
@Test
public void testMultiInit2() {
Rectangle node = new Rectangle();
assertNull(node.getParent());
Group group1 = new Group();
ObservableList<Node> content = ParentShim.getChildren(group1);
try {
content.addAll(node, node);
fail("IllegalArgument should have been thrown.");
} catch (IllegalArgumentException iae) {
}
assertEquals(0, ParentShim.getChildren(group1).size());
assertNull(node.getParent());
assertNull(group1.getParent());
checkSGConsistency(group1);
Group group2 = new Group();
ParentShim.getChildren(group2).add(node);
assertEquals(1, ParentShim.getChildren(group2).size());
assertSame(node, ParentShim.getChildren(group2).get(0));
assertSame(group2, node.getParent());
assertEquals(0, ParentShim.getChildren(group1).size());
assertNull(group1.getParent());
assertNull(group2.getParent());
checkSGConsistency(group1);
checkSGConsistency(group2);
}
@Test
public void testMultiAdd() {
Rectangle node = new Rectangle();
Group group1 = new Group();
Group group2 = new Group();
assertNull(node.getParent());
assertEquals(0, ParentShim.getChildren(group1).size());
assertEquals(0, ParentShim.getChildren(group2).size());
assertNull(group1.getParent());
assertNull(group2.getParent());
checkSGConsistency(group1);
checkSGConsistency(group2);
ParentShim.getChildren(group1).add(node);
assertEquals(1, ParentShim.getChildren(group1).size());
assertSame(node, ParentShim.getChildren(group1).get(0));
assertSame(group1, node.getParent());
assertNull(group1.getParent());
checkSGConsistency(group1);
try {
ParentShim.getChildren(group2).add(node);
} catch(Throwable t) {
assertNull("unexpected exception", t);
}
assertEquals(0, ParentShim.getChildren(group1).size());
assertEquals(1, ParentShim.getChildren(group2).size());
assertSame(node, ParentShim.getChildren(group2).get(0));
assertSame(group2, node.getParent());
assertNull(group1.getParent());
assertNull(group2.getParent());
checkSGConsistency(group1);
checkSGConsistency(group2);
}
@Test
public void testMultiAdd2() {
Rectangle node = new Rectangle();
Group group1 = new Group();
Group group2 = new Group();
assertNull(node.getParent());
assertEquals(0, ParentShim.getChildren(group1).size());
assertEquals(0, ParentShim.getChildren(group2).size());
assertNull(group1.getParent());
assertNull(group2.getParent());
checkSGConsistency(group1);
checkSGConsistency(group2);
ParentShim.getChildren(group1).add(node);
assertEquals(1, ParentShim.getChildren(group1).size());
assertSame(node, ParentShim.getChildren(group1).get(0));
assertSame(group1, node.getParent());
assertNull(group1.getParent());
checkSGConsistency(group1);
ObservableList<Node> content = ParentShim.getChildren(group1);
try {
content.add(node);
fail("IllegalArgument should have been thrown.");
} catch (IllegalArgumentException iae) {
}
assertEquals(1, ParentShim.getChildren(group1).size());
assertSame(node, ParentShim.getChildren(group1).get(0));
assertSame(group1, node.getParent());
assertNull(group1.getParent());
checkSGConsistency(group1);
try {
ParentShim.getChildren(group2).add(node);
} catch (Throwable t) {
assertNull("unexpected exception", t);
}
assertEquals(0, ParentShim.getChildren(group1).size());
assertEquals(1, ParentShim.getChildren(group2).size());
assertSame(node, ParentShim.getChildren(group2).get(0));
assertSame(group2, node.getParent());
assertNull(group1.getParent());
assertNull(group2.getParent());
checkSGConsistency(group1);
checkSGConsistency(group2);
}
@Test
public void testMultiAdd3() {
Rectangle node = new Rectangle();
Group group1 = new Group();
Group group2 = new Group();
assertNull(node.getParent());
assertEquals(0, ParentShim.getChildren(group1).size());
assertEquals(0, ParentShim.getChildren(group2).size());
assertNull(group1.getParent());
assertNull(group2.getParent());
checkSGConsistency(group1);
checkSGConsistency(group2);
}
@Test public void Node_prefWidth_BasedOnLayoutBounds_CleansUpAfterBadBounds() {
SpecialGroup node = new SpecialGroup();
node.setTestBB(0, 0, Double.NaN, 50);
assertEquals(0, node.prefWidth(-1), 0);
assertEquals(0, node.prefWidth(5), 0);
}
@Test public void Node_prefWidth_BasedOnLayoutBounds_CleansUpAfterBadBounds2() {
SpecialGroup node = new SpecialGroup();
node.setTestBB(0, 0, -10, 50);
assertEquals(0, node.prefWidth(-1), 0);
assertEquals(0, node.prefWidth(5), 0);
}
@Test public void Node_prefHeight_BasedOnLayoutBounds_CleansUpAfterBadBounds() {
SpecialGroup node = new SpecialGroup();
node.setTestBB(0, 0, 50, Double.NaN);
assertEquals(0, node.prefHeight(-1), 0);
assertEquals(0, node.prefHeight(5), 0);
}
@Test public void Node_prefHeight_BasedOnLayoutBounds_CleansUpAfterBadBounds2() {
SpecialGroup node = new SpecialGroup();
node.setTestBB(0, 0, 50, -10);
assertEquals(0, node.prefHeight(-1), 0);
assertEquals(0, node.prefHeight(5), 0);
}
@Test
public void testPrefWidthDoesNotIncludeInvisibleChild() {
Group group = new Group();
test.javafx.scene.layout.MockRegion region = new test.javafx.scene.layout.MockRegion(100,150);
region.setVisible(false);
test.javafx.scene.layout.MockRegion region2 = new test.javafx.scene.layout.MockRegion(50,75);
ParentShim.getChildren(group).addAll(region,region2);
assertEquals(50, group.prefWidth(-1), 0);
}
@Test
public void testPrefHeightDoesNotIncludeInvisibleChild() {
Group group = new Group();
test.javafx.scene.layout.MockRegion region = new test.javafx.scene.layout.MockRegion(100,150);
region.setVisible(false);
test.javafx.scene.layout.MockRegion region2 = new test.javafx.scene.layout.MockRegion(50,75);
ParentShim.getChildren(group).addAll(region,region2);
assertEquals(75, group.prefHeight(-1), 0);
}
@Test
public void testPrefWidthWithResizableChild() {
Group group = new Group();
test.javafx.scene.layout.MockRegion region = new test.javafx.scene.layout.MockRegion(100,150);
ParentShim.getChildren(group).add(region);
assertEquals(100, group.prefWidth(-1), 0);
}
@Test
public void testPrefHeightWithResizableChild() {
Group group = new Group();
test.javafx.scene.layout.MockRegion region = new test.javafx.scene.layout.MockRegion(100,150);
ParentShim.getChildren(group).add(region);
assertEquals(150, group.prefHeight(-1), 0);
}
@Test
public void testPrefWidthIncludesResizableChildScaleX() {
Group group = new Group();
test.javafx.scene.layout.MockRegion region = new test.javafx.scene.layout.MockRegion(100,150);
region.setScaleX(2.0);
ParentShim.getChildren(group).add(region);
assertEquals(200, group.prefWidth(-1), 0);
}
@Test
public void testPrefHeightIncludesResizableChildScaleY() {
Group group = new Group();
test.javafx.scene.layout.MockRegion region = new test.javafx.scene.layout.MockRegion(100,150);
region.setScaleY(2.0);
ParentShim.getChildren(group).add(region);
assertEquals(300, group.prefHeight(-1), 0);
}
@Test
public void testPrefWidthIncludesResizableChildsClip() {
Group group = new Group();
test.javafx.scene.layout.MockRegion region = new test.javafx.scene.layout.MockRegion(100,150);
region.setClip(new Rectangle(50,75));
ParentShim.getChildren(group).add(region);
assertEquals(50, group.prefWidth(-1), 0);
}
@Test
public void testPrefHeightIncludesResizableChildsClip() {
Group group = new Group();
test.javafx.scene.layout.MockRegion region = new test.javafx.scene.layout.MockRegion(100,150);
region.setClip(new Rectangle(50,75));
ParentShim.getChildren(group).add(region);
assertEquals(75, group.prefHeight(-1), 0);
}
@Test
public void testPrefWidthIncludesResizableChildsRotation() {
Group group = new Group();
test.javafx.scene.layout.MockRegion region = new test.javafx.scene.layout.MockRegion(100,150);
region.setRotate(90);
ParentShim.getChildren(group).add(region);
assertEquals(150, group.prefWidth(-1), 0);
}
@Test
public void testPrefHeightIncludesResizableChildsRotation() {
Group group = new Group();
test.javafx.scene.layout.MockRegion region = new test.javafx.scene.layout.MockRegion(100,150);
region.setRotate(90);
ParentShim.getChildren(group).add(region);
assertEquals(100, group.prefHeight(-1), 0);
}
@Test
public void testPrefWidthIncludesResizableChildsTranslateX() {
Group group = new Group();
test.javafx.scene.layout.MockRegion region1 = new test.javafx.scene.layout.MockRegion(100,150);
test.javafx.scene.layout.MockRegion region2 = new test.javafx.scene.layout.MockRegion(100,150);
region2.setTranslateX(50);
ParentShim.getChildren(group).addAll(region1,region2);
assertEquals(150, group.prefWidth(-1), 0);
}
@Test
public void testPrefHeightIncludesResizableChildsTranslateY() {
Group group = new Group();
test.javafx.scene.layout.MockRegion region1 = new test.javafx.scene.layout.MockRegion(100,150);
test.javafx.scene.layout.MockRegion region2 = new test.javafx.scene.layout.MockRegion(100,150);
region2.setTranslateY(50);
ParentShim.getChildren(group).addAll(region1,region2);
assertEquals(200, group.prefHeight(-1), 0);
}
@Test
public void testPrefWidthDoesNotIncludeScaleX() {
Group group = new Group();
group.setScaleX(2.0);
test.javafx.scene.layout.MockRegion region = new test.javafx.scene.layout.MockRegion(100,150);
ParentShim.getChildren(group).add(region);
assertEquals(100, group.prefWidth(-1), 0);
}
@Test
public void testPrefHeightDoesNotIncludeScaleY() {
Group group = new Group();
group.setScaleY(2.0);
test.javafx.scene.layout.MockRegion region = new test.javafx.scene.layout.MockRegion(100,150);
ParentShim.getChildren(group).add(region);
assertEquals(150, group.prefHeight(-1), 0);
}
@Test
public void testPrefWidthDoesNotIncludeClip() {
Group group = new Group();
group.setClip(new Rectangle(50,75));
test.javafx.scene.layout.MockRegion region = new test.javafx.scene.layout.MockRegion(100,150);
ParentShim.getChildren(group).add(region);
assertEquals(100, group.prefWidth(-1), 0);
}
@Test
public void testPrefHeightDoesNotIncludeClip() {
Group group = new Group();
group.setClip(new Rectangle(50,75));
test.javafx.scene.layout.MockRegion region = new test.javafx.scene.layout.MockRegion(100,150);
ParentShim.getChildren(group).add(region);
assertEquals(150, group.prefHeight(-1), 0);
}
@Test
public void testPrefWidthDoesNotIncludeRotation() {
Group group = new Group();
group.setRotate(45);
test.javafx.scene.layout.MockRegion region = new test.javafx.scene.layout.MockRegion(100,150);
ParentShim.getChildren(group).add(region);
assertEquals(100, group.prefWidth(-1), 0);
}
@Test
public void testPrefHeightDoesNotIncludeRotation() {
Group group = new Group();
group.setRotate(45);
test.javafx.scene.layout.MockRegion region = new test.javafx.scene.layout.MockRegion(100,150);
ParentShim.getChildren(group).add(region);
assertEquals(150, group.prefHeight(-1), 0);
}
@Test
public void testPrefWidthDoesNotIncludeTranslateX() {
Group group = new Group();
group.setTranslateX(50);
test.javafx.scene.layout.MockRegion region1 = new test.javafx.scene.layout.MockRegion(100,150);
test.javafx.scene.layout.MockRegion region2 = new test.javafx.scene.layout.MockRegion(100,150);
ParentShim.getChildren(group).addAll(region1,region2);
assertEquals(100, group.prefWidth(-1), 0);
}
@Test
public void testPrefHeightDoesNotIncludeTranslateY() {
Group group = new Group();
group.setTranslateY(50);
test.javafx.scene.layout.MockRegion region1 = new test.javafx.scene.layout.MockRegion(100,150);
test.javafx.scene.layout.MockRegion region2 = new test.javafx.scene.layout.MockRegion(100,150);
ParentShim.getChildren(group).addAll(region1,region2);
assertEquals(150, group.prefHeight(-1), 0);
}
@Test
public void testNonResizableChildDoesNotTriggerLayout() {
Group group = new Group();
Rectangle rect = new Rectangle(50,50);
ParentShim.getChildren(group).add(rect);
group.layout();
rect.setWidth(100);
assertFalse(group.isNeedsLayout());
}
@Test
public void testMinMaxSizesCorrespondToPreferred() {
Group group = new Group();
Rectangle rect = new Rectangle(50,50);
ParentShim.getChildren(group).add(rect);
assertEquals(50, group.minWidth(-1), 1e-100);
assertEquals(50, group.minHeight(-1), 1e-100);
assertEquals(50, group.maxWidth(-1), 1e-100);
assertEquals(50, group.maxHeight(-1), 1e-100);
rect.setWidth(100);
assertEquals(100, group.minWidth(-1), 1e-100);
assertEquals(50, group.minHeight(-1), 1e-100);
assertEquals(100, group.maxWidth(-1), 1e-100);
assertEquals(50, group.maxHeight(-1), 1e-100);
rect.setHeight(200);
assertEquals(100, group.minWidth(-1), 1e-100);
assertEquals(200, group.minHeight(-1), 1e-100);
assertEquals(100, group.maxWidth(-1), 1e-100);
assertEquals(200, group.maxHeight(-1), 1e-100);
}
protected class SpecialGroup extends Group {
BoundingBox testBB;
{
SpecialGroupHelper.initHelper(this);
}
public SpecialGroup() {
}
void setTestBB(double x, double y, double width, double height) {
testBB = new BoundingBox(x, y, width, height);
}
private Bounds doComputeLayoutBounds() {
return testBB;
}
}
static final class SpecialGroupHelper extends GroupHelper {
private static final SpecialGroupHelper theInstance;
static {
theInstance = new SpecialGroupHelper();
}
private static SpecialGroupHelper getInstance() {
return theInstance;
}
public static void initHelper(SpecialGroup specialGroup) {
setHelper(specialGroup, getInstance());
}
@Override
protected Bounds computeLayoutBoundsImpl(Node node) {
return ((SpecialGroup) node).doComputeLayoutBounds();
}
}
}
