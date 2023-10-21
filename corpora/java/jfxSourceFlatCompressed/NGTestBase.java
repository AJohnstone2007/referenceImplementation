package test.com.sun.javafx.sg.prism;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import java.lang.reflect.Field;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.DirtyRegionPool;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.sg.prism.NGCircleShim;
import com.sun.javafx.sg.prism.NGGroupShim;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGNodeShim;
import com.sun.javafx.sg.prism.NGRectangleShim;
import com.sun.javafx.sg.prism.NGRegionShim;
import com.sun.javafx.sg.prism.NodeEffectInput;
import com.sun.prism.Graphics;
import com.sun.prism.paint.Color;
import com.sun.scenario.effect.Effect;
public class NGTestBase {
protected static <N extends NGNode> void transform(N node, BaseTransform tx) {
tx = node.getTransform().copy().deriveWithConcatenation(tx);
node.setTransformedBounds(node.getEffectBounds(new RectBounds(), tx), false);
node.setTransformMatrix(tx);
}
protected static <N extends NGNode> void translate(N node, double tx, double ty) {
transform(node, BaseTransform.getTranslateInstance(tx, ty));
}
protected static <N extends NGNode> void setEffect(N node, Effect effect) {
node.setEffect(null);
BaseBounds effectBounds = new RectBounds();
effectBounds = effectBounds.deriveWithNewBounds(effect.getBounds(BaseTransform.IDENTITY_TRANSFORM, new NodeEffectInput(node)));
BaseBounds clippedBounds = node.getEffectBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM);
node.setEffect(effect);
effectBounds = effectBounds.deriveWithUnion(clippedBounds);
node.setTransformedBounds(node.getTransform().transform(effectBounds, effectBounds), false);
}
public static TestNGRectangle createRectangle(int x, int y, int width, int height) {
TestNGRectangle rect = new TestNGRectangle();
rect.updateRectangle(x, y, width, height, 0, 0);
final RectBounds bounds = new RectBounds(x, y, x + width, y + height);
rect.setContentBounds(bounds);
rect.setFillPaint(new Color(0, 0, 0, 1.0f));
rect.setTransformMatrix(BaseTransform.IDENTITY_TRANSFORM);
rect.setTransformedBounds(bounds, false);
return rect;
}
public static TestNGCircle createCircle(int cx, int cy, int radius) {
TestNGCircle c = new TestNGCircle();
c.updateCircle(cx, cy, radius);
final RectBounds bounds = new RectBounds(cx - radius, cy - radius, cx + radius, cy + radius);
c.setContentBounds(bounds);
c.setFillPaint(new Color(0, 0, 0, 1.0f));
c.setTransformMatrix(BaseTransform.IDENTITY_TRANSFORM);
c.setTransformedBounds(bounds, false);
return c;
}
public static TestNGRegion createOpaqueRegion(int x, int y, int width, int height, NGNode... children) {
TestNGRegion r = createTransparentRegion(x, y, width, height, children);
r.updateBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.BLACK, null, null)));
r.setOpaqueInsets(0, 0, 0, 0);
return r;
}
public static TestNGRegion createTransparentRegion(int x, int y, int width, int height, NGNode... children) {
TestNGRegion r = new TestNGRegion();
for (NGNode child : children) {
r.add(-1, child);
}
r.setSize(width, height);
final RectBounds bounds = new RectBounds(0, 0, width, height);
r.setContentBounds(bounds);
if (x != 0 || y != 0) {
r.setTransformMatrix(BaseTransform.getTranslateInstance(x, y));
r.setTransformedBounds(r.getTransform().transform(bounds, new RectBounds()), true);
} else {
r.setTransformMatrix(BaseTransform.IDENTITY_TRANSFORM);
r.setTransformedBounds(bounds, false);
}
return r;
}
public static TestNGGroup createGroup(NGNode... children) {
TestNGGroup group = new TestNGGroup();
BaseBounds contentBounds = new RectBounds();
for (NGNode child : children) {
contentBounds = contentBounds.deriveWithUnion(
child.getCompleteBounds(
new RectBounds(), BaseTransform.IDENTITY_TRANSFORM));
group.add(-1, child);
}
group.setContentBounds(contentBounds);
group.setTransformMatrix(BaseTransform.IDENTITY_TRANSFORM);
group.setTransformedBounds(contentBounds, false);
return group;
}
public static TestNGRegion createRegion(int w, int h, NGNode... children) {
TestNGRegion region = new TestNGRegion();
BaseBounds contentBounds = new RectBounds();
for (NGNode child : children) {
contentBounds = contentBounds.deriveWithUnion(
child.getCompleteBounds(
new RectBounds(), BaseTransform.IDENTITY_TRANSFORM));
region.add(-1, child);
}
region.setContentBounds(contentBounds);
region.setTransformMatrix(BaseTransform.IDENTITY_TRANSFORM);
region.setTransformedBounds(contentBounds, false);
region.setSize(w, h);
javafx.scene.paint.Color color = new javafx.scene.paint.Color(0, 0, 0, 1);
try {
Field platformPaint = color.getClass().getDeclaredField("platformPaint");
platformPaint.setAccessible(true);
platformPaint.set(color, new Color(0f, 0f, 0f, 1f));
} catch (Exception e) {
e.printStackTrace();
}
Background background = new Background(new BackgroundFill[] {
new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)}, null);
region.updateBackground(background);
region.setOpaqueInsets(0, 0, 0, 0);
return region;
}
public static final class TestNGGroup extends NGGroupShim implements TestNGNode {
private boolean askedToAccumulateDirtyRegion;
private boolean computedDirtyRegion;
private boolean rendered;
@Override
protected void renderContent(Graphics g) {
super.renderContent(g);
rendered = true;
}
@Override public int accumulateDirtyRegions(final RectBounds clip,
RectBounds dirtyRegion, DirtyRegionPool pool, DirtyRegionContainer drc, BaseTransform tx, GeneralTransform3D pvTx) {
askedToAccumulateDirtyRegion = true;
return super.accumulateDirtyRegions(clip, dirtyRegion, pool, drc, tx, pvTx);
}
@Override public int accumulateGroupDirtyRegion(
final RectBounds clip, RectBounds dirtyRegion, DirtyRegionPool pool, DirtyRegionContainer drc, BaseTransform tx, GeneralTransform3D pvTx)
{
computedDirtyRegion = true;
return super.accumulateGroupDirtyRegion(clip, dirtyRegion, pool, drc, tx, pvTx);
}
@Override public int accumulateNodeDirtyRegion(
final RectBounds clip, RectBounds dirtyRegion, DirtyRegionContainer drc, BaseTransform tx, GeneralTransform3D pvTx)
{
computedDirtyRegion = true;
return super.accumulateNodeDirtyRegion(clip, dirtyRegion, drc, tx, pvTx);
}
@Override public boolean askedToAccumulateDirtyRegion() { return askedToAccumulateDirtyRegion; }
@Override public boolean computedDirtyRegion() { return computedDirtyRegion; }
@Override public boolean rendered() { return rendered; }
}
public static final class TestNGRegion extends NGRegionShim implements TestNGNode {
private boolean askedToAccumulateDirtyRegion;
private boolean computedDirtyRegion;
private boolean rendered;
@Override
protected void renderContent(Graphics g) {
super.renderContent(g);
rendered = true;
}
@Override public int accumulateDirtyRegions(final RectBounds clip,
RectBounds dirtyRegion, DirtyRegionPool pool, DirtyRegionContainer drc, BaseTransform tx, GeneralTransform3D pvTx) {
askedToAccumulateDirtyRegion = true;
return super.accumulateDirtyRegions(clip, dirtyRegion, pool, drc, tx, pvTx);
}
@Override public int accumulateGroupDirtyRegion(
final RectBounds clip, RectBounds dirtyRegion, DirtyRegionPool pool, DirtyRegionContainer drc, BaseTransform tx, GeneralTransform3D pvTx)
{
computedDirtyRegion = true;
return super.accumulateGroupDirtyRegion(clip, dirtyRegion, pool, drc, tx, pvTx);
}
@Override public int accumulateNodeDirtyRegion(
final RectBounds clip, RectBounds dirtyRegion, DirtyRegionContainer drc, BaseTransform tx, GeneralTransform3D pvTx)
{
computedDirtyRegion = true;
return super.accumulateNodeDirtyRegion(clip, dirtyRegion, drc, tx, pvTx);
}
@Override public boolean askedToAccumulateDirtyRegion() { return askedToAccumulateDirtyRegion; }
@Override public boolean computedDirtyRegion() { return computedDirtyRegion; }
@Override public boolean rendered() { return rendered; }
}
public static final class TestNGRectangle extends NGRectangleShim implements TestNGNode {
private boolean askedToAccumulateDirtyRegion;
private boolean computedDirtyRegion;
private boolean rendered;
@Override
protected void renderContent(Graphics g) {
rendered = true;
}
@Override public int accumulateDirtyRegions(final RectBounds clip,
RectBounds dirtyRegion, DirtyRegionPool pool, DirtyRegionContainer drc, BaseTransform tx, GeneralTransform3D pvTx) {
askedToAccumulateDirtyRegion = true;
return super.accumulateDirtyRegions(clip, dirtyRegion, pool, drc, tx, pvTx);
}
@Override public int accumulateNodeDirtyRegion(
final RectBounds clip, RectBounds dirtyRegion, DirtyRegionContainer drc, BaseTransform tx, GeneralTransform3D pvTx)
{
computedDirtyRegion = true;
return super.accumulateNodeDirtyRegion(clip, dirtyRegion, drc, tx, pvTx);
}
@Override public boolean askedToAccumulateDirtyRegion() { return askedToAccumulateDirtyRegion; }
@Override public boolean computedDirtyRegion() { return computedDirtyRegion; }
@Override public boolean rendered() { return rendered; }
}
public static final class TestNGCircle extends NGCircleShim implements TestNGNode {
private boolean askedToAccumulateDirtyRegion;
private boolean computedDirtyRegion;
private boolean rendered;
@Override
protected void renderContent(Graphics g) {
rendered = true;
}
@Override public int accumulateDirtyRegions(final RectBounds clip,
RectBounds dirtyRegion, DirtyRegionPool pool, DirtyRegionContainer drc, BaseTransform tx, GeneralTransform3D pvTx) {
askedToAccumulateDirtyRegion = true;
return super.accumulateDirtyRegions(clip, dirtyRegion, pool, drc, tx, pvTx);
}
@Override public int accumulateNodeDirtyRegion(
final RectBounds clip, RectBounds dirtyRegion, DirtyRegionContainer drc, BaseTransform tx, GeneralTransform3D pvTx)
{
computedDirtyRegion = true;
return super.accumulateNodeDirtyRegion(clip, dirtyRegion, drc,tx, pvTx);
}
@Override public boolean askedToAccumulateDirtyRegion() { return askedToAccumulateDirtyRegion; }
@Override public boolean computedDirtyRegion() { return computedDirtyRegion; }
@Override public boolean rendered() { return rendered; }
}
public interface TestNGNode {
public boolean askedToAccumulateDirtyRegion();
public boolean computedDirtyRegion();
public boolean rendered();
}
public static abstract class Creator<N extends NGNode> {
public abstract N create();
}
public static abstract class Polluter {
public BaseTransform tx = BaseTransform.IDENTITY_TRANSFORM;
public abstract void pollute(NGNode node);
public BaseBounds modifiedBounds(NGNode node) {
return DirtyRegionTestBase.getWhatTransformedBoundsWouldBe(node, tx);
}
public RectBounds polluteAndGetExpectedBounds(NGNode node) {
BaseBounds originalBounds = node.getCompleteBounds(new RectBounds(), BaseTransform.IDENTITY_TRANSFORM);
BaseBounds modifiedBounds = modifiedBounds(node);
BaseBounds expected = originalBounds.deriveWithUnion(modifiedBounds);
pollute(node);
return (RectBounds)expected;
}
}
}
