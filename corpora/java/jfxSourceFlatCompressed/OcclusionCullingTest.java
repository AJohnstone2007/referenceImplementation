package test.com.sun.javafx.sg.prism;
import test.com.sun.javafx.sg.prism.TestGraphics;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGNodeShim;
import com.sun.javafx.sg.prism.NodePath;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
public class OcclusionCullingTest extends NGTestBase {
@Test
public void testRectangleOcclusion() {
final TestNGRectangle root = createRectangle(0, 0, 50, 50);
TestNGGroup group = createGroup(
createRectangle(0, 0, 100, 100), root);
NodePath rootPath = new NodePath();
group.getRenderRoot(rootPath, new RectBounds(20, 20, 30, 30), -1, BaseTransform.IDENTITY_TRANSFORM, new GeneralTransform3D());
TestGraphics g = new TestGraphics();
g.setRenderRoot(rootPath);
group.render(g);
assertRoot(rootPath, root);
checkRootRendering(group, rootPath);
}
@Test
public void testGroupOcclusion() {
final TestNGRectangle root = createRectangle(0, 0, 50, 50);
TestNGGroup group = createGroup(createGroup(
createRectangle(0, 0, 100, 100)), createGroup(root));
NodePath rootPath = new NodePath();
group.getRenderRoot(rootPath, new RectBounds(20, 20, 30, 30), -1, BaseTransform.IDENTITY_TRANSFORM, new GeneralTransform3D());
TestGraphics g = new TestGraphics();
g.setRenderRoot(rootPath);
group.render(g);
assertRoot(rootPath, root);
checkRootRendering(group, rootPath);
}
@Test
public void testRegionOcclusion() {
final TestNGRegion root = createRegion(50, 50);
TestNGGroup group = createGroup(
createRegion(100, 100), root);
NodePath rootPath = new NodePath();
group.getRenderRoot(rootPath, new RectBounds(20, 20, 30, 30), -1, BaseTransform.IDENTITY_TRANSFORM, new GeneralTransform3D());
TestGraphics g = new TestGraphics();
g.setRenderRoot(rootPath);
group.render(g);
assertRoot(rootPath, root);
checkRootRendering(group, rootPath);
}
@Test
public void testPresetRegionOcclusion() {
final TestNGRegion root = createRegion(100, 100);
final TestNGRegion other = createRegion(50, 50);
TestNGGroup group = createGroup(
root, other);
other.setOpaqueInsets(30, 30, 0, 0);
NodePath rootPath = new NodePath();
group.getRenderRoot(rootPath, new RectBounds(20, 20, 30, 30), -1, BaseTransform.IDENTITY_TRANSFORM, new GeneralTransform3D());
TestGraphics g = new TestGraphics();
g.setRenderRoot(rootPath);
group.render(g);
assertRoot(rootPath, root);
checkRootRendering(group, rootPath);
}
@Test
public void test2SameRectanglesOcclusion() {
final TestNGRectangle root = createRectangle(10, 10, 100, 100);
TestNGGroup group = createGroup(
createGroup(createRectangle(10, 10, 100, 100), createRectangle(20, 20, 20, 20)),
createGroup(root));
NodePath rootPath = new NodePath();
group.getRenderRoot(rootPath, new RectBounds(10, 10, 100, 100), -1, BaseTransform.IDENTITY_TRANSFORM, new GeneralTransform3D());
TestGraphics g = new TestGraphics();
g.setRenderRoot(rootPath);
group.render(g);
assertRoot(rootPath, root);
checkRootRendering(group, rootPath);
}
@Test
public void test2SameRectanglesOcclusionWithRootNotDirty() {
final TestNGRectangle root = createRectangle(10, 10, 100, 100);
final TestNGGroup rootParent = createGroup(root);
TestNGGroup group = createGroup(
createGroup(createRectangle(10, 10, 100, 100), createRectangle(20, 20, 20, 20)), rootParent);
NGNodeShim.set_dirty(group, NGNode.DirtyFlag.CLEAN);
NGNodeShim.set_dirty(rootParent, NGNode.DirtyFlag.CLEAN);
NGNodeShim.set_childDirty(rootParent, false);
NGNodeShim.set_dirty(root, NGNode.DirtyFlag.CLEAN);
NGNodeShim.set_childDirty(root, false);
NodePath rootPath = new NodePath();
group.getRenderRoot(rootPath, new RectBounds(10, 10, 100, 100), -1, BaseTransform.IDENTITY_TRANSFORM, new GeneralTransform3D());
assertTrue(rootPath.isEmpty());
final TestNGRectangle dirtySibling = createRectangle(0,0,10,10);
rootParent.add(-1, dirtySibling);
rootPath = new NodePath();
group.getRenderRoot(rootPath, new RectBounds(10, 10, 100, 100), -1, BaseTransform.IDENTITY_TRANSFORM, new GeneralTransform3D());
assertRoot(rootPath, root);
}
@Test
public void testTransparentRegionWithChildren() {
final TestNGRectangle root = createRectangle(10, 10, 100, 100);
final TestNGGroup rootParent = createGroup(root);
TestNGRegion region = createTransparentRegion(0, 0, 100, 100,
createGroup(createRectangle(10, 10, 100, 100), createRectangle(20, 20, 20, 20)), rootParent);
NGNodeShim.set_dirty(region, NGNode.DirtyFlag.CLEAN);
NGNodeShim.set_dirty(rootParent, NGNode.DirtyFlag.CLEAN);
NGNodeShim.set_childDirty(rootParent, false);
NGNodeShim.set_dirty(root, NGNode.DirtyFlag.CLEAN);
NGNodeShim.set_childDirty(root, false);
NodePath rootPath = new NodePath();
region.getRenderRoot(rootPath, new RectBounds(10, 10, 100, 100), -1, BaseTransform.IDENTITY_TRANSFORM, new GeneralTransform3D());
assertTrue(rootPath.isEmpty());
final TestNGRectangle dirtySibling = createRectangle(0,0,10,10);
rootParent.add(-1,dirtySibling);
rootPath = new NodePath();
region.getRenderRoot(rootPath, new RectBounds(10, 10, 100, 100), -1, BaseTransform.IDENTITY_TRANSFORM, new GeneralTransform3D());
assertRoot(rootPath, root);
}
@Test
public void testOpaqueRegion() {
final TestNGRectangle rect = createRectangle(10, 10, 100, 100);
TestNGRegion region = createOpaqueRegion(0, 0, 200, 200, rect);
TestNGGroup root = createGroup(region);
NodePath rootPath = new NodePath();
root.getRenderRoot(rootPath, new RectBounds(10, 10, 100, 100), -1, BaseTransform.IDENTITY_TRANSFORM, new GeneralTransform3D());
assertRoot(rootPath, rect);
rootPath.clear();
root.getRenderRoot(rootPath, new RectBounds(5, 5, 150, 150), -1, BaseTransform.IDENTITY_TRANSFORM, new GeneralTransform3D());
assertRoot(rootPath, region);
TestGraphics g = new TestGraphics();
g.setRenderRoot(rootPath);
root.render(g);
checkRootRendering(root, rootPath);
rootPath.clear();
root.getRenderRoot(rootPath, new RectBounds(-5, -5, 150, 150), -1, BaseTransform.IDENTITY_TRANSFORM, new GeneralTransform3D());
assertRoot(rootPath, root);
}
private void checkRootRendering(TestNGNode node, NodePath root) {
assertTrue(node.rendered());
if (node instanceof TestNGGroup) {
if (root.hasNext()) {
boolean foundRoot = false;
root.next();
for (NGNode p : ((TestNGGroup)node).getChildren()) {
TestNGNode n = (TestNGNode) p;
if (n == root.getCurrentNode()) {
foundRoot = true;
checkRootRendering(n, root);
continue;
}
checkRendered(n, foundRoot);
}
} else {
for (NGNode p : ((TestNGGroup)node).getChildren()) {
checkRendered((TestNGNode)p, true);
}
}
}
}
private void checkRendered(TestNGNode node, boolean rendered) {
assertEquals(rendered, node.rendered());
if (node instanceof TestNGGroup) {
for (NGNode p : ((TestNGGroup)node).getChildren()) {
checkRendered((TestNGNode)p, rendered);
}
}
}
private void assertRoot(NodePath rootPath, final NGNode root) {
rootPath.reset();
while(rootPath.hasNext()) {
rootPath.next();
}
assertSame(root, rootPath.getCurrentNode());
rootPath.reset();
}
}
