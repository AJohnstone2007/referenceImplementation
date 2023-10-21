package test.com.sun.javafx.sg.prism;
import test.com.sun.javafx.sg.prism.TestGraphics;
import java.util.ArrayList;
import java.util.List;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.scenario.effect.DropShadow;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
@RunWith(Parameterized.class)
public class GridDirtyRegionTest extends DirtyRegionTestBase {
private static final float TRANSLATE_DELTA = 50;
public GridDirtyRegionTest(Creator creator, Polluter polluter) {
super(creator, polluter);
}
@Before public void setUp() {
NGNode[] content = new NGNode[9];
for (int row=0; row<3; row++) {
for (int col=0; col<3; col++) {
NGNode node = creator.create();
BaseTransform tx = BaseTransform.IDENTITY_TRANSFORM;
tx = tx.deriveWithTranslation((col * 110), (row * 110));
transform(node, tx);
content[(row * 3) + col] = node;
}
}
root = createGroup(content);
root.render(TestGraphics.TEST_GRAPHICS);
}
@Test public void sanityCheck() {
NGNode node = root.getChildren().get(0);
assertEquals(new RectBounds(0, 0, 100, 100), node.getContentBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM));
assertEquals(new RectBounds(0, 0, 100, 100), node.getCompleteBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM));
node = root.getChildren().get(1);
assertEquals(new RectBounds(0, 0, 100, 100), node.getContentBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM));
assertEquals(new RectBounds(110, 0, 210, 100), node.getCompleteBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM));
node = root.getChildren().get(3);
assertEquals(new RectBounds(0, 0, 100, 100), node.getContentBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM));
assertEquals(new RectBounds(0, 110, 100, 210), node.getCompleteBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM));
}
@Test public void cleanNodesShouldNotContributeToDirtyRegion() {
assertDirtyRegionEquals(root, new RectBounds());
}
@Test public void cleanChildNodesOnADirtyParentShouldNotContributeToDirtyRegion() {
translate(root, TRANSLATE_DELTA, TRANSLATE_DELTA);
for (NGNode child : root.getChildren()) {
assertDirtyRegionEquals(child, new RectBounds());
}
}
@Test public void whenOnlyTheRootIsDirtyOnlyTheRootShouldBeAskedToAccumulateDirtyRegions() {
translate(root, TRANSLATE_DELTA, TRANSLATE_DELTA);
assertOnlyTheseNodesAreAskedToAccumulateDirtyRegions(root);
}
@Test public void cleanChildNodesOnACleanParentShouldNotContributeToDirtyRegion() {
NGNode middleChild = root.getChildren().get(root.getChildren().size()/2);
polluter.pollute(middleChild);
for (NGNode child : root.getChildren()) {
if (child != middleChild) {
assertDirtyRegionEquals(child, new RectBounds());
}
}
}
@Test public void whenOnlyASingleChildIsDirtyThenParentAndAllChildrenAreAskedToAccumulateDirtyRegions() {
NGNode middleChild = root.getChildren().get(root.getChildren().size()/2);
polluter.pollute(middleChild);
List<NGNode> nodes = new ArrayList<>(root.getChildren());
nodes.add(root);
NGNode[] arr = new NGNode[nodes.size()];
for (int i=0; i<nodes.size(); i++) arr[i] = nodes.get(i);
assertOnlyTheseNodesAreAskedToAccumulateDirtyRegions(arr);
}
@Test public void whenOnlyASingleChildIsDirtyThenOnlyParentAndThatChildShouldComputeDirtyRegions() {
NGNode middleChild = root.getChildren().get(root.getChildren().size()/2);
polluter.pollute(middleChild);
assertOnlyTheseNodesAreAskedToComputeDirtyRegions(root, middleChild);
}
@Test public void aDirtyChildNodeShouldFormTheDirtyRegionWhenItIsTheOnlyDirtyNode() {
NGNode middleChild = root.getChildren().get(root.getChildren().size()/2);
assertDirtyRegionEquals(root, polluter.polluteAndGetExpectedBounds(middleChild));
}
@Test public void theUnionOfTwoDirtyChildNodesDirtyRegionsShouldFormTheDirtyRegionWhenTheyAreTheOnlyDirtyNodes() {
NGNode firstChild = root.getChildren().get(0);
NGNode middleChild = root.getChildren().get(root.getChildren().size()/2);
RectBounds firstChildArea = polluter.polluteAndGetExpectedBounds(firstChild);
RectBounds middleChildArea = polluter.polluteAndGetExpectedBounds(middleChild);
RectBounds expected = (RectBounds)firstChildArea.deriveWithUnion(middleChildArea);
assertDirtyRegionEquals(root, expected);
}
@Test public void whenTheParentIsDirtyAndSomeChildrenAreDirtyTheParentBoundsShouldFormTheDirtyRegion() {
BaseBounds original = root.getCompleteBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM);
translate(root, TRANSLATE_DELTA, TRANSLATE_DELTA);
BaseBounds transformed = root.getCompleteBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM);
polluter.pollute(root.getChildren().get(0));
polluter.pollute(root.getChildren().get(root.getChildren().size()/2));
polluter.pollute(root.getChildren().get(root.getChildren().size()-1));
RectBounds expected = (RectBounds)original.deriveWithUnion(transformed);
assertDirtyRegionEquals(root, expected);
}
@Test public void anEffectShouldChangeTheTransformedBoundsOfAChild() {
NGNode middleChild = root.getChildren().get(root.getChildren().size()/2);
BaseBounds oldTransformedBounds = middleChild.getCompleteBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM);
DropShadow shadow = new DropShadow();
shadow.setGaussianWidth(21);
shadow.setGaussianHeight(21);
shadow.setOffsetX(2);
shadow.setOffsetY(2);
setEffect(middleChild, shadow);
BaseBounds newTransformedBounds = middleChild.getCompleteBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM);
assertFalse(newTransformedBounds.equals(oldTransformedBounds));
}
@Test public void whenAnEffectIsSetTheChildBecomesDirtyAndTheDirtyRegionIncludesTheEffectBounds() {
NGNode middleChild = root.getChildren().get(root.getChildren().size()/2);
DropShadow shadow = new DropShadow();
shadow.setGaussianWidth(21);
shadow.setGaussianHeight(21);
shadow.setOffsetX(2);
shadow.setOffsetY(2);
BaseBounds oldTransformedBounds = middleChild.getCompleteBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM);
setEffect(middleChild, shadow);
BaseBounds newTransformedBounds = middleChild.getCompleteBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM);
RectBounds expected = (RectBounds)oldTransformedBounds.deriveWithUnion(newTransformedBounds);
assertDirtyRegionEquals(root, expected);
}
@Test public void whenAnEffectIsChangedOnTheChildTheDirtyRegionIncludesTheOldAndNewEffectBounds() {
NGNode middleChild = root.getChildren().get(root.getChildren().size()/2);
DropShadow shadow = new DropShadow();
shadow.setGaussianWidth(21);
shadow.setGaussianHeight(21);
shadow.setOffsetX(2);
shadow.setOffsetY(2);
BaseBounds oldTransformedBounds = middleChild.getCompleteBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM);
setEffect(middleChild, shadow);
BaseBounds newTransformedBounds = middleChild.getCompleteBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM);
shadow.setOffsetX(20);
shadow.setOffsetY(20);
BaseBounds evenNewerTransformedBounds = middleChild.getCompleteBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM);
RectBounds expected = (RectBounds)oldTransformedBounds.deriveWithUnion(newTransformedBounds).deriveWithUnion(evenNewerTransformedBounds);
assertDirtyRegionEquals(root, expected);
}
}
