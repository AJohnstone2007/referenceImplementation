package test.com.sun.javafx.sg.prism;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGRectangle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.assertEquals;
@RunWith(Parameterized.class)
public class DirtyRegionClipTest extends DirtyRegionTestBase {
@Parameterized.Parameters
public static Collection createParameters() {
final Polluter pollutePositiveTranslation = new Polluter() {
{ tx = BaseTransform.getTranslateInstance(50, 50); }
@Override public void pollute(NGNode node) { DirtyRegionTestBase.transform(node, tx); }
@Override public String toString() { return "Pollute Positive Translation"; }
};
List<Object[]> params = new ArrayList<Object[]>();
List<Polluter> polluters = Arrays.asList(new Polluter[]{
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
return params;
}
public DirtyRegionClipTest(Creator creator, Polluter polluter) {
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
node = root.getChildren().get(root.getChildren().size()/2);
assertEquals(new RectBounds(0, 0, 100, 100), node.getContentBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM));
assertEquals(new RectBounds(110, 110, 210, 210), node.getCompleteBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM));
}
@Test public void dirtyRegionContainsClip() {
windowClip = new RectBounds(115, 115, 120, 120);
NGNode middleChild = root.getChildren().get(root.getChildren().size()/2);
assertContainsClip(root, polluter.polluteAndGetExpectedBounds(middleChild), DirtyRegionContainer.DTR_CONTAINS_CLIP);
}
@Test public void dirtyRegionPartiallyOverlapsClip() {
windowClip = new RectBounds(90, 90, 120, 120);
NGNode middleChild = root.getChildren().get(root.getChildren().size()/2);
assertContainsClip(root, polluter.polluteAndGetExpectedBounds(middleChild), DirtyRegionContainer.DTR_OK);
}
@Test public void dirtyRegionDoesNotContainClip() {
NGNode middleChild = root.getChildren().get(root.getChildren().size()/2);
assertContainsClip(root, polluter.polluteAndGetExpectedBounds(middleChild), DirtyRegionContainer.DTR_OK);
}
}