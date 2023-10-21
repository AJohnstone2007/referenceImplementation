package test.com.sun.javafx.sg.prism;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.sg.prism.NGCircle;
import com.sun.javafx.sg.prism.NGGroup;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGNodeShim;
import com.sun.javafx.sg.prism.NGRectangle;
import com.sun.javafx.sg.prism.NodePath;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
public class RenderRootTest extends NGTestBase {
private NGRectangle rect;
private NGGroup root;
@Before
public void setup() {
rect = createRectangle(10, 10, 90, 90);
root = createGroup(rect);
}
private NodePath getRenderRoot(NGGroup root, int dirtyX, int dirtyY, int dirtyWidth, int dirtyHeight) {
final DirtyRegionContainer drc = new DirtyRegionContainer(1);
final RectBounds dirtyRegion = new RectBounds(dirtyX, dirtyY, dirtyX+dirtyWidth, dirtyY+dirtyHeight);
drc.addDirtyRegion(dirtyRegion);
final BaseTransform tx = BaseTransform.IDENTITY_TRANSFORM;
final GeneralTransform3D pvTx = new GeneralTransform3D();
NGNodeShim.markCullRegions(root, drc, -1, tx, pvTx);
NodePath path = new NodePath();
root.getRenderRoot(path, dirtyRegion, 0, tx, pvTx);
return path;
}
private void assertRenderRoot(NGNode expectedRoot, NodePath rootPath) {
if (expectedRoot == null) {
assertTrue(rootPath.isEmpty());
} else {
while (rootPath.hasNext()) rootPath.next();
assertSame(expectedRoot, rootPath.getCurrentNode());
}
}
@Test
public void dirtyRegionWithinOpaqueRegion() {
NodePath rootPath = getRenderRoot(root, 20, 20, 70, 70);
assertRenderRoot(rect, rootPath);
}
@Test
public void dirtyRegionWithinOpaqueRegion_Clean() {
root.clearDirtyTree();
NodePath rootPath = getRenderRoot(root, 20, 20, 70, 70);
assertRenderRoot(null, rootPath);
}
@Test
public void dirtyRegionMatchesOpaqueRegion() {
NodePath rootPath = getRenderRoot(root, 10, 10, 90, 90);
assertRenderRoot(rect, rootPath);
}
@Test
public void dirtyRegionMatchesOpaqueRegion_Clean() {
root.clearDirtyTree();
NodePath rootPath = getRenderRoot(root, 10, 10, 90, 90);
assertRenderRoot(null, rootPath);
}
@Test
public void dirtyRegionWithinOpaqueRegionTouchesTop() {
NodePath rootPath = getRenderRoot(root, 20, 10, 70, 70);
assertRenderRoot(rect, rootPath);
}
@Test
public void dirtyRegionWithinOpaqueRegionTouchesTop_Clean() {
root.clearDirtyTree();
NodePath rootPath = getRenderRoot(root, 20, 10, 70, 70);
assertRenderRoot(null, rootPath);
}
@Test
public void dirtyRegionWithinOpaqueRegionTouchesRight() {
NodePath rootPath = getRenderRoot(root, 20, 20, 80, 70);
assertRenderRoot(rect, rootPath);
}
@Test
public void dirtyRegionWithinOpaqueRegionTouchesRight_Clean() {
root.clearDirtyTree();
NodePath rootPath = getRenderRoot(root, 20, 20, 80, 70);
assertRenderRoot(null, rootPath);
}
@Test
public void dirtyRegionWithinOpaqueRegionTouchesBottom() {
NodePath rootPath = getRenderRoot(root, 20, 20, 70, 80);
assertRenderRoot(rect, rootPath);
}
@Test
public void dirtyRegionWithinOpaqueRegionTouchesBottom_Clean() {
root.clearDirtyTree();
NodePath rootPath = getRenderRoot(root, 20, 20, 70, 80);
assertRenderRoot(null, rootPath);
}
@Test
public void dirtyRegionWithinOpaqueRegionTouchesLeft() {
NodePath rootPath = getRenderRoot(root, 10, 20, 70, 70);
assertRenderRoot(rect, rootPath);
}
@Test
public void dirtyRegionWithinOpaqueRegionTouchesLeft_Clean() {
root.clearDirtyTree();
NodePath rootPath = getRenderRoot(root, 10, 20, 70, 70);
assertRenderRoot(null, rootPath);
}
@Test
public void opaqueRegionWithinDirtyRegion() {
NodePath rootPath = getRenderRoot(root, 0, 0, 110, 110);
assertRenderRoot(root, rootPath);
}
@Test
public void opaqueRegionWithinDirtyRegion_Clean() {
root.clearDirtyTree();
NodePath rootPath = getRenderRoot(root, 0, 0, 110, 110);
assertRenderRoot(root, rootPath);
}
@Test
public void dirtyRegionIntersectsOpaqueRegionTop() {
NodePath rootPath = getRenderRoot(root, 20, 0, 70, 30);
assertRenderRoot(root, rootPath);
}
@Test
public void dirtyRegionIntersectsOpaqueRegionTop_Clean() {
root.clearDirtyTree();
NodePath rootPath = getRenderRoot(root, 20, 0, 70, 30);
assertRenderRoot(root, rootPath);
}
@Test
public void dirtyRegionIntersectsOpaqueRegionRight() {
NodePath rootPath = getRenderRoot(root, 90, 20, 30, 70);
assertRenderRoot(root, rootPath);
}
@Test
public void dirtyRegionIntersectsOpaqueRegionRight_Clean() {
root.clearDirtyTree();
NodePath rootPath = getRenderRoot(root, 90, 20, 30, 70);
assertRenderRoot(root, rootPath);
}
@Test
public void dirtyRegionIntersectsOpaqueRegionBottom() {
NodePath rootPath = getRenderRoot(root, 20, 90, 70, 30);
assertRenderRoot(root, rootPath);
}
@Test
public void dirtyRegionIntersectsOpaqueRegionBottom_Clean() {
root.clearDirtyTree();
NodePath rootPath = getRenderRoot(root, 20, 90, 70, 30);
assertRenderRoot(root, rootPath);
}
@Test
public void dirtyRegionIntersectsOpaqueRegionLeft() {
NodePath rootPath = getRenderRoot(root, 0, 20, 30, 70);
assertRenderRoot(root, rootPath);
}
@Test
public void dirtyRegionIntersectsOpaqueRegionLeft_Clean() {
root.clearDirtyTree();
NodePath rootPath = getRenderRoot(root, 0, 20, 30, 70);
assertRenderRoot(root, rootPath);
}
@Test
public void dirtyRegionCompletelyOutsideOfOpaqueRegion() {
NodePath rootPath = getRenderRoot(root, 0, 0, 5, 5);
assertRenderRoot(root, rootPath);
}
@Test
public void dirtyRegionCompletelyOutsideOfOpaqueRegion_Clean() {
root.clearDirtyTree();
NodePath rootPath = getRenderRoot(root, 0, 0, 5, 5);
assertRenderRoot(root, rootPath);
}
@Ignore("JDK-8265510")
@Test
public void emptyDirtyRegion1() {
NodePath rootPath = getRenderRoot(root, 0, 0, -1, -1);
assertRenderRoot(root, rootPath);
}
@Ignore("JDK-8265510")
@Test
public void emptyDirtyRegion2() {
NodePath rootPath = getRenderRoot(root, -1, -1, -2, -2);
assertRenderRoot(root, rootPath);
}
@Ignore("JDK-8265510")
@Test
public void invalidDirtyRegionOutsideOpaqueRegion() {
NodePath rootPath = getRenderRoot(root, -10, -10, 5, 5);
assertRenderRoot(root, rootPath);
}
@Test
public void zeroSizeDirtyRegionWithinOpaqueRegion() {
NodePath rootPath = getRenderRoot(root, 20, 20, 0, 0);
assertRenderRoot(rect, rootPath);
}
@Test
public void zeroSizeDirtyRegionOutsideOpaqueRegion1() {
NodePath rootPath = getRenderRoot(root, 0, 0, 0, 0);
assertRenderRoot(root, rootPath);
}
@Test
public void zeroSizeDirtyRegionOutsideOpaqueRegion2() {
NodePath rootPath = getRenderRoot(root, 5, 5, 0, 0);
assertRenderRoot(root, rootPath);
}
@Test
public void withRectangularClip() {
NGRectangle clip = createRectangle(20, 20, 70, 70);
rect.setClipNode(clip);
NodePath rootPath = getRenderRoot(root, 20, 20, 70, 70);
assertRenderRoot(rect, rootPath);
}
@Test
public void withRectangularClip_negative() {
NGRectangle clip = createRectangle(20, 20, 70, 70);
rect.setClipNode(clip);
NodePath rootPath = getRenderRoot(root, 19, 20, 70, 70);
assertRenderRoot(root, rootPath);
}
@Test
public void withRectangularClipTranslated() {
NGRectangle clip = createRectangle(20, 20, 70, 70);
clip.setTransformMatrix(BaseTransform.getTranslateInstance(10, 10));
rect.setClipNode(clip);
NodePath rootPath = getRenderRoot(root, 30, 30, 70, 70);
assertRenderRoot(rect, rootPath);
}
@Test
public void withRectangularClipTranslated_negative() {
NGRectangle clip = createRectangle(20, 20, 70, 70);
clip.setTransformMatrix(BaseTransform.getTranslateInstance(10, 10));
rect.setClipNode(clip);
NodePath rootPath = getRenderRoot(root, 29, 30, 70, 70);
assertRenderRoot(root, rootPath);
}
@Test
public void withRectangularClipScaled() {
NGRectangle clip = createRectangle(20, 20, 70, 70);
clip.setTransformMatrix(BaseTransform.getScaleInstance(.5, .5));
rect.setClipNode(clip);
NodePath rootPath = getRenderRoot(root, 10, 10, 35, 35);
assertRenderRoot(rect, rootPath);
}
@Test
public void withRectangularClipScaled_negative() {
NGRectangle clip = createRectangle(20, 20, 70, 70);
clip.setTransformMatrix(BaseTransform.getScaleInstance(.5, .5));
rect.setClipNode(clip);
NodePath rootPath = getRenderRoot(root, 9, 10, 35, 35);
assertRenderRoot(root, rootPath);
}
@Test
public void withCircleClip() {
NGCircle clip = createCircle(50, 50, 45);
rect.setClipNode(clip);
NodePath rootPath = getRenderRoot(root, 40, 40, 20, 20);
assertRenderRoot(rect, rootPath);
}
@Test
public void withCircleClip_negative() {
NGCircle clip = createCircle(50, 50, 45);
rect.setClipNode(clip);
NodePath rootPath = getRenderRoot(root, 10, 10, 90, 90);
assertRenderRoot(root, rootPath);
}
}
