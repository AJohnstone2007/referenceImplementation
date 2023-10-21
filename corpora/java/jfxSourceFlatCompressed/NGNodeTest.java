package test.com.sun.javafx.sg.prism;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGNodeShim;
import com.sun.javafx.sg.prism.NGPath;
import com.sun.javafx.sg.prism.NGRectangle;
import com.sun.prism.Graphics;
import com.sun.prism.paint.Color;
import com.sun.scenario.effect.Blend;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
public class NGNodeTest extends NGTestBase {
NGNodeMock n;
@Before
public void setup() {
n = new NGNodeMock();
}
@Test
public void hasOpaqueRegionIsFalseIfOpacityIsLessThanOne() {
n.setOpacity(1);
assertTrue(n.hasOpaqueRegion());
n.setOpacity(.5f);
assertFalse(n.hasOpaqueRegion());
n.setOpacity(0);
assertFalse(n.hasOpaqueRegion());
}
@Test
public void hasOpaqueRegionIsFalseIfEffectIsNotNullAndEffect_reducesOpaquePixels_returnsFalse() {
n.setEffect(new TransparentEffect());
assertFalse(n.hasOpaqueRegion());
n.setEffect(null);
assertTrue(n.hasOpaqueRegion());
}
@Test
public void hasOpaqueRegionIsTrueIfEffectIsNotNullAndEffect_reducesOpaquePixels_returnsTrue() {
n.setEffect(new OpaqueEffect());
assertTrue(n.hasOpaqueRegion());
}
@Test
public void hasOpaqueRegionIsTrueIfClipIsNullOrHappy() {
n.setClipNode(null);
assertTrue(n.hasOpaqueRegion());
NGRectangle r = new NGRectangle();
r.updateRectangle(0, 0, 10, 10, 0, 0);
r.setFillPaint(Color.BLACK);
n.setClipNode(r);
assertTrue(n.hasOpaqueRegion());
}
@Test
public void hasOpaqueRegionIsFalseIfClipDoesNotSupportOpaqueRegions() {
n.setClipNode(new NGPath());
assertFalse(n.hasOpaqueRegion());
}
@Test
public void hasOpaqueRegionIsFalseIfClipDoesNotHaveAnOpaqueRegion() {
NGRectangle r = new NGRectangle();
n.setClipNode(r);
assertFalse(n.hasOpaqueRegion());
}
@Test
public void hasOpaqueRegionIsFalseIfBlendModeIsNotNullOrSRC_OVER() {
for (Blend.Mode mode : Blend.Mode.values()) {
n.setNodeBlendMode(mode);
if (mode == Blend.Mode.SRC_OVER) {
assertTrue(n.hasOpaqueRegion());
} else {
assertFalse(n.hasOpaqueRegion());
}
}
n.setNodeBlendMode(null);
assertTrue(n.hasOpaqueRegion());
}
@Test
public void opaqueRegionCached() {
RectBounds or = n.getOpaqueRegion();
assertNotNull(or);
n.changeOpaqueRegion(10, 10, 100, 100);
assertSame(or, n.getOpaqueRegion());
n.setEffect(new TransparentEffect());
assertNull(n.getOpaqueRegion());
n.setEffect(null);
assertNotSame(or, n.getOpaqueRegion());
}
@Test
public void opaqueRegionCached2() {
n.getOpaqueRegion();
n.opaqueRegionRecomputed = false;
n.getOpaqueRegion();
assertFalse(n.opaqueRegionRecomputed);
}
@Test
public void opaqueRegionRecomputedWhenTransformChanges() {
n.getOpaqueRegion();
n.opaqueRegionRecomputed = false;
n.setTransformMatrix(BaseTransform.getTranslateInstance(1, 1));
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setTransformMatrix(BaseTransform.getTranslateInstance(1, 1));
n.getOpaqueRegion();
assertFalse(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setTransformMatrix(BaseTransform.IDENTITY_TRANSFORM);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setTransformMatrix(BaseTransform.IDENTITY_TRANSFORM);
n.getOpaqueRegion();
assertFalse(n.opaqueRegionRecomputed);
}
@Test
public void opaqueRegionRecomputedWhenClipNodeReferenceChanges() {
n.getOpaqueRegion();
n.opaqueRegionRecomputed = false;
NGRectangle clip = new NGRectangle();
n.setClipNode(clip);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setClipNode(clip);
n.getOpaqueRegion();
assertFalse(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
clip = new NGRectangle();
clip.updateRectangle(0, 0, 10, 10, 0, 0);
clip.setFillPaint(Color.BLACK);
n.setClipNode(clip);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
clip = new NGRectangle();
clip.updateRectangle(0, 0, 10, 10, 0, 0);
clip.setFillPaint(Color.BLACK);
n.setClipNode(clip);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setClipNode(null);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setClipNode(null);
n.getOpaqueRegion();
assertFalse(n.opaqueRegionRecomputed);
}
@Test
public void opaqueRegionRecomputedWhenClipNodeHasOpaqueRegionChanges() {
n.getOpaqueRegion();
n.opaqueRegionRecomputed = false;
NGRectangle clip = new NGRectangle();
clip.updateRectangle(0, 0, 10, 10, 0, 0);
clip.setFillPaint(Color.BLACK);
n.setClipNode(clip);
n.getOpaqueRegion();
n.opaqueRegionRecomputed = false;
clip.setFillPaint(null);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
clip.setFillPaint(Color.WHITE);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
}
@Test
public void opaqueRegionRecomputedWhenOpacityGoesFromOneToLessThanOne() {
n.getOpaqueRegion();
n.opaqueRegionRecomputed = false;
n.setOpacity(.9f);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setOpacity(1f);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setOpacity(1f);
n.getOpaqueRegion();
assertFalse(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setOpacity(0f);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
}
@Test
public void opaqueRegionRecomputedWhenOpacityGoesFromZeroToMoreThanZero() {
n.setOpacity(0f);
n.getOpaqueRegion();
n.opaqueRegionRecomputed = false;
n.setOpacity(.9f);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setOpacity(0f);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setOpacity(0f);
n.getOpaqueRegion();
assertFalse(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setOpacity(1f);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
}
@Test
public void opaqueRegionNotRecomputedWhenOpacityNeverGoesToOneOrZero() {
n.setOpacity(.1f);
n.getOpaqueRegion();
for (float f=.1f; f<.9f; f+=.1) {
n.opaqueRegionRecomputed = false;
n.setOpacity(f);
n.getOpaqueRegion();
assertFalse(n.opaqueRegionRecomputed);
}
}
@Test
public void opaqueRegionRecomputedWhenBlendModeChanges() {
n.getOpaqueRegion();
for (Blend.Mode mode : Blend.Mode.values()) {
n.opaqueRegionRecomputed = false;
n.setNodeBlendMode(mode);
n.getOpaqueRegion();
if (mode == Blend.Mode.SRC_OVER) continue;
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setNodeBlendMode(null);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setNodeBlendMode(Blend.Mode.SRC_OVER);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setNodeBlendMode(mode);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setNodeBlendMode(Blend.Mode.SRC_OVER);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setNodeBlendMode(null);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
}
}
@Test
public void opaqueRegionNotRecomputedWhenEffectReferenceChanges() {
n.getOpaqueRegion();
n.opaqueRegionRecomputed = false;
n.setEffect(new TransparentEffect());
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setEffect(new TransparentEffect());
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setEffect(null);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
Effect effect = new TransparentEffect();
n.setEffect(effect);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setEffect(effect);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
effect = new OpaqueEffect();
n.setEffect(effect);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setEffect(effect);
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
n.opaqueRegionRecomputed = false;
n.setEffect(new OpaqueEffect());
n.getOpaqueRegion();
assertTrue(n.opaqueRegionRecomputed);
}
@Test
public void testGetOpaqueRegionReturnsNullIf_supportsOpaqueRegion_returnsFalse() {
NGPath path = new NGPath();
path.setFillPaint(Color.BLACK);
assertNull(path.getOpaqueRegion());
}
@Test public void testGetOpaqueRegionReturnsNullIf_hasOpaqueRegion_returnsFalse() {
n.setEffect(new TransparentEffect());
assertNull(n.getOpaqueRegion());
}
@Test public void testGetOpaqueRegionWithNoClip() {
assertEquals(new RectBounds(0, 0, 10, 10), n.getOpaqueRegion());
}
@Test public void testGetOpaqueRegionWithSimpleRectangleClip() {
NGRectangle clip = new NGRectangle();
clip.setFillPaint(Color.BLACK);
clip.updateRectangle(3, 3, 4, 4, 0, 0);
n.setClipNode(clip);
assertEquals(new RectBounds(3, 3, 7, 7), n.getOpaqueRegion());
}
@Test public void testGetOpaqueRegionWithSimpleRectangleClipWithNoFill() {
NGRectangle clip = new NGRectangle();
clip.updateRectangle(3, 3, 4, 4, 0, 0);
n.setClipNode(clip);
assertNull(n.getOpaqueRegion());
}
@Test public void testGetOpaqueRegionWithTranslatedRectangleClip() {
NGRectangle clip = new NGRectangle();
clip.setFillPaint(Color.BLACK);
clip.updateRectangle(0, 0, 4, 4, 0, 0);
clip.setTransformMatrix(BaseTransform.getTranslateInstance(2, 2));
n.setClipNode(clip);
assertEquals(new RectBounds(2, 2, 6, 6), n.getOpaqueRegion());
}
@Test public void testGetOpaqueRegionWithScaledRectangleClip() {
NGRectangle clip = new NGRectangle();
clip.setFillPaint(Color.BLACK);
clip.updateRectangle(0, 0, 4, 4, 0, 0);
clip.setTransformMatrix(BaseTransform.getScaleInstance(.5, .5));
n.setClipNode(clip);
assertEquals(new RectBounds(0, 0, 2, 2), n.getOpaqueRegion());
}
@Test public void testGetOpaqueRegionWithTranslatedAndScaledRectangleClip() {
NGRectangle clip = new NGRectangle();
clip.setFillPaint(Color.BLACK);
clip.updateRectangle(0, 0, 4, 4, 0, 0);
clip.setTransformMatrix(
BaseTransform.getTranslateInstance(2, 2).deriveWithConcatenation(
BaseTransform.getScaleInstance(.5, .5)));
n.setClipNode(clip);
assertEquals(new RectBounds(2, 2, 4, 4), n.getOpaqueRegion());
}
@Test public void testGetOpaqueRegionWithRotatedRectangleClip() {
NGRectangle clip = new NGRectangle();
clip.setFillPaint(Color.BLACK);
clip.updateRectangle(0, 0, 4, 4, 0, 0);
clip.setTransformMatrix(BaseTransform.getRotateInstance(45, 5, 5));
n.setClipNode(clip);
assertNull(n.getOpaqueRegion());
}
class NGNodeMock extends NGNodeShim {
boolean opaqueRegionRecomputed = false;
RectBounds computedOpaqueRegion = new RectBounds(0, 0, 10, 10);
void changeOpaqueRegion(float x, float y, float x2, float y2) {
computedOpaqueRegion = new RectBounds(x, y, x2, y2);
geometryChanged();
}
@Override
public boolean hasOpaqueRegion() {
opaqueRegionRecomputed = true;
return super.hasOpaqueRegion()
&& computedOpaqueRegion != null;
}
@Override
protected RectBounds computeOpaqueRegion(RectBounds opaqueRegion) {
opaqueRegionRecomputed = true;
assert computedOpaqueRegion != null;
return (RectBounds) opaqueRegion.deriveWithNewBounds(computedOpaqueRegion);
}
@Override
protected void renderContent(Graphics g) { }
@Override
protected boolean hasOverlappingContents() {
return false;
}
@Override
protected boolean supportsOpaqueRegions() {
return true;
}
}
static abstract class MockEffect extends Effect {
@Override
public ImageData filter(FilterContext fctx, BaseTransform transform, Rectangle outputClip, Object renderHelper, Effect defaultInput) {
return null;
}
@Override
public BaseBounds getBounds(BaseTransform transform, Effect defaultInput) {
return null;
}
@Override
public AccelType getAccelType(FilterContext fctx) {
return AccelType.OPENGL;
}
}
class TransparentEffect extends MockEffect {
@Override
public boolean reducesOpaquePixels() {
return true;
}
}
class OpaqueEffect extends MockEffect {
@Override
public boolean reducesOpaquePixels() {
return false;
}
}
}
