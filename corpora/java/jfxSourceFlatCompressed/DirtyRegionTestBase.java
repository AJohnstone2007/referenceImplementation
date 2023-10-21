package test.com.sun.javafx.sg.prism;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.DirtyRegionPool;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.sg.prism.NGCircle;
import com.sun.javafx.sg.prism.NGGroup;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGRectangle;
import com.sun.javafx.sg.prism.NGRegion;
import com.sun.javafx.sg.prism.NGShape;
import com.sun.prism.paint.Color;
import org.junit.runners.Parameterized;
import static org.junit.Assert.assertEquals;
public class DirtyRegionTestBase extends NGTestBase {
@Parameterized.Parameters
public static Collection createParameters() {
final Polluter polluteOpacity = new Polluter() {
@Override public void pollute(NGNode node) { node.setOpacity(.5f); }
@Override public String toString() { return "Pollute Opacity"; }
};
final Polluter restoreOpacity = new Polluter() {
@Override public void pollute(NGNode node) {
node.setOpacity(0f);
NGNode parent = node;
while(parent.getParent() != null) parent = parent.getParent();
parent.render(TestGraphics.TEST_GRAPHICS);
node.setOpacity(1f);
}
@Override public String toString() { return "Restore Opacity"; }
};
final Polluter polluteFill = new Polluter() {
@Override public void pollute(NGNode node) {
if (node instanceof NGShape) {
com.sun.javafx.sg.prism.NGShape shape = (NGShape)node;
shape.setFillPaint(new Color(.43f, .23f, .66f, 1f));
} else if (node instanceof NGRegion) {
NGRegion region = (NGRegion)node;
javafx.scene.paint.Color color = new javafx.scene.paint.Color(.43f, .23f, .66f, 1f);
try {
Field platformPaint = color.getClass().getDeclaredField("platformPaint");
platformPaint.setAccessible(true);
platformPaint.set(color, new Color(.43f, .23f, .66f, 1f));
} catch (Exception e) {
e.printStackTrace();
}
region.updateBackground(new Background(new BackgroundFill[] {
new BackgroundFill(
color,
CornerRadii.EMPTY, Insets.EMPTY)
}));
} else {
throw new IllegalArgumentException("I don't know how to make the fill dirty on " + node);
}
}
@Override public String toString() { return "Pollute Fill"; }
};
final Polluter pollutePositiveTranslation = new Polluter() {
{ tx = BaseTransform.getTranslateInstance(50, 50); }
@Override public void pollute(NGNode node) { DirtyRegionTestBase.transform(node, tx); }
@Override public String toString() { return "Pollute Positive Translation"; }
};
final Polluter polluteNegativeTranslation = new Polluter() {
{ tx = BaseTransform.getTranslateInstance(-50, -50); }
@Override public void pollute(NGNode node) { DirtyRegionTestBase.transform(node, tx); }
@Override public String toString() { return "Pollute Negative Translation"; }
};
final Polluter polluteBiggerScale = new Polluter() {
{ tx = BaseTransform.getScaleInstance(2, 2); }
@Override public void pollute(NGNode node) { DirtyRegionTestBase.transform(node, tx); }
@Override public String toString() { return "Pollute Bigger Scale"; }
};
final Polluter polluteSmallerScale = new Polluter() {
{ tx = BaseTransform.getScaleInstance(.5, .5); }
@Override public void pollute(NGNode node) { DirtyRegionTestBase.transform(node, tx); }
@Override public String toString() { return "Pollute Smaller Scale"; }
};
final Polluter polluteRotate = new Polluter() {
@Override public void pollute(NGNode node) {
BaseBounds effectBounds = node.getEffectBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM);
BaseTransform tx = BaseTransform.getRotateInstance(45, effectBounds.getWidth()/2f, effectBounds.getHeight()/2f);
DirtyRegionTestBase.transform(node, tx);
}
@Override public BaseBounds modifiedBounds(NGNode node) {
BaseBounds effectBounds = node.getEffectBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM);
BaseTransform tx = BaseTransform.getRotateInstance(45, effectBounds.getWidth() / 2f, effectBounds.getHeight() / 2f);
return DirtyRegionTestBase.getWhatTransformedBoundsWouldBe(node, tx);
}
@Override public String toString() { return "Pollute Rotate"; }
};
final Polluter polluteVisibility = new Polluter() {
@Override public void pollute(NGNode node) {
node.setVisible(false);
}
@Override public String toString() { return "Pollute Visibility"; }
};
final Polluter restoreVisibility = new Polluter() {
@Override public void pollute(NGNode node) {
node.setVisible(false);
NGNode parent = node;
while(parent.getParent() != null) parent = parent.getParent();
parent.render(TestGraphics.TEST_GRAPHICS);
node.setVisible(true);
}
@Override public String toString() { return "Restore Visibility"; }
};
List<Object[]> params = new ArrayList<Object[]>();
List<Polluter> polluters = Arrays.asList(new Polluter[]{
polluteRotate,
polluteOpacity,
restoreOpacity,
polluteVisibility,
restoreVisibility,
polluteSmallerScale,
polluteNegativeTranslation,
polluteBiggerScale,
pollutePositiveTranslation
});
for (final Polluter polluter : polluters) {
params.add(new Object[] {new Creator() {
@Override public NGNode create() { return createGroup(createRectangle(0, 0, 100, 100)); }
@Override public String toString() { return "Group with one Rectangle"; }
}, polluter});
}
List<Polluter> rectanglePolluters = new ArrayList<Polluter>(polluters);
rectanglePolluters.add(new Polluter() {
@Override public void pollute(NGNode node) {
NGRectangle rect = (NGRectangle)node;
BaseBounds bounds = rect.getContentBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM);
rect.updateRectangle(bounds.getMinX(), bounds.getMinY(), 25, 25, 0, 0);
}
@Override public String toString() { return "Pollute Rectangle Geometry"; }
});
for (final Polluter polluter : rectanglePolluters) {
params.add(new Object[] {new Creator() {
@Override public NGNode create() { return createRectangle(0, 0, 100, 100); }
@Override public String toString() { return "Rectangle"; }
}, polluter});
}
List<Polluter> circlePolluters = new ArrayList<Polluter>(polluters);
circlePolluters.add(new Polluter() {
@Override public void pollute(NGNode node) {
NGCircle c = (NGCircle)node;
BaseBounds bounds = c.getContentBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM);
c.updateCircle(
bounds.getMinX() + (bounds.getWidth()/2f),
bounds.getMinY() + (bounds.getHeight()/2f),
10);
}
@Override public String toString() { return "Pollute Circle Geometry"; }
});
for (final Polluter polluter : circlePolluters) {
params.add(new Object[] {new Creator() {
@Override public NGNode create() { return createCircle(50, 50, 50); }
@Override public String toString() { return "Circle"; }
}, polluter});
}
return params;
}
protected Creator creator;
protected Polluter polluter;
protected TestNGGroup root;
protected RectBounds windowClip = new RectBounds(-100000, -100000, 100000, 10000);
protected DirtyRegionTestBase(Creator creator, Polluter polluter) {
this.creator = creator;
this.polluter = polluter;
}
protected void assertDirtyRegionEquals(NGNode start, RectBounds expected) {
DirtyRegionPool pool = new DirtyRegionPool(1);
DirtyRegionContainer drc = pool.checkOut();
int status = start.accumulateDirtyRegions(
windowClip,
new RectBounds(), pool,
drc,
BaseTransform.IDENTITY_TRANSFORM, new GeneralTransform3D());
RectBounds dirtyRegion = drc.getDirtyRegion(0) ;
expected = new RectBounds(
Math.max(expected.getMinX() - 1, dirtyRegion.getMinX()),
Math.max(expected.getMinY() - 1, dirtyRegion.getMinY()),
Math.min(expected.getMaxX() + 1, dirtyRegion.getMaxX()),
Math.min(expected.getMaxY() + 1, dirtyRegion.getMaxY()));
assertEquals("creator=" + creator + ", polluter=" + polluter, expected, dirtyRegion);
}
protected void assertContainsClip(NGNode start, RectBounds expectedDirtyRegion, int expectedStatus) {
DirtyRegionPool pool = new DirtyRegionPool(1);
DirtyRegionContainer drc = pool.checkOut();
int status = start.accumulateDirtyRegions(
windowClip,
new RectBounds(), pool,
drc,
BaseTransform.IDENTITY_TRANSFORM, new GeneralTransform3D());
assertEquals(expectedStatus, status);
if (status == DirtyRegionContainer.DTR_OK) {
assertDirtyRegionEquals(start, expectedDirtyRegion);
}
}
protected void assertOnlyTheseNodesAreAskedToAccumulateDirtyRegions(NGNode... nodes) {
accumulateDirtyRegions();
Set<NGNode> set = new HashSet<NGNode>(Arrays.asList(nodes));
assertOnlyTheseNodesWereAskedToAccumulateDirtyRegions(root, set);
}
protected void assertOnlyTheseNodesAreAskedToComputeDirtyRegions(NGNode... nodes) {
accumulateDirtyRegions();
Set<NGNode> set = new HashSet<NGNode>(Arrays.asList(nodes));
assertOnlyTheseNodesWereAskedToComputeDirtyRegions(root, set);
}
private void accumulateDirtyRegions() {
DirtyRegionPool pool = new DirtyRegionPool(1);
DirtyRegionTestBase.resetGroupBounds(root);
root.accumulateDirtyRegions(
new RectBounds(0, 0, 800, 600),
new RectBounds(), pool,
pool.checkOut(),
BaseTransform.IDENTITY_TRANSFORM,
new GeneralTransform3D());
}
private void assertOnlyTheseNodesWereAskedToAccumulateDirtyRegions(NGNode start, Set<NGNode> nodes) {
assertEquals(
"creator=" + creator + ", polluter=" + polluter,
nodes.contains(start), ((TestNGNode)start).askedToAccumulateDirtyRegion());
if (start instanceof NGGroup) {
for (NGNode child : ((NGGroup)start).getChildren()) {
assertOnlyTheseNodesWereAskedToAccumulateDirtyRegions(child, nodes);
}
}
}
private void assertOnlyTheseNodesWereAskedToComputeDirtyRegions(NGNode start, Set<NGNode> nodes) {
assertEquals(
"creator=" + creator + ", polluter=" + polluter,
nodes.contains(start), ((TestNGNode)start).computedDirtyRegion());
if (start instanceof NGGroup) {
for (NGNode child : ((NGGroup)start).getChildren()) {
assertOnlyTheseNodesWereAskedToComputeDirtyRegions(child, nodes);
}
}
}
static protected void resetGroupBounds(NGGroup group) {
BaseBounds contentBounds = new RectBounds();
for (NGNode child : group.getChildren()) {
contentBounds = contentBounds.deriveWithUnion(
child.getCompleteBounds(
new RectBounds(), BaseTransform.IDENTITY_TRANSFORM));
}
BaseBounds currentContentBounds = group.getContentBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM);
if (!contentBounds.equals(currentContentBounds)) {
System.out.println("CurrentContentBounds=" + currentContentBounds + ", bounds=" + contentBounds);
group.setContentBounds(contentBounds);
group.setTransformedBounds(group.getEffectBounds(new RectBounds(), group.getTransform()), false);
}
}
static protected BaseBounds getWhatTransformedBoundsWouldBe(NGNode node, BaseTransform tx) {
BaseTransform existing = BaseTransform.IDENTITY_TRANSFORM.deriveWithNewTransform(node.getTransform());
tx = existing.deriveWithConcatenation(tx);
return node.getEffectBounds(new RectBounds(), tx);
}
}
