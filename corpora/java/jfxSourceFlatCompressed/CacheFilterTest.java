package test.com.sun.javafx.sg.prism;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.geom.transform.Translate2D;
import com.sun.javafx.sg.prism.CacheFilter;
import com.sun.javafx.sg.prism.CacheFilterShim;
import com.sun.javafx.sg.prism.NGNodeShim;
import com.sun.javafx.sg.prism.NGRectangle;
import javafx.scene.CacheHint;
import org.junit.Test;
import static org.junit.Assert.*;
public class CacheFilterTest {
@Test public void settingCacheHintToDefaultInConstructor() {
NGRectangle r = new NGRectangle();
CacheFilter cf = CacheFilterShim.getCacheFilter(r, CacheHint.DEFAULT);
assertFalse(CacheFilterShim.isRotateHint(cf));
assertFalse(CacheFilterShim.isScaleHint(cf));
}
@Test public void settingCacheHintToDefault() {
NGRectangle r = new NGRectangle();
CacheFilter cf = CacheFilterShim.getCacheFilter(r, CacheHint.SPEED);
cf.setHint(CacheHint.DEFAULT);
assertFalse(CacheFilterShim.isRotateHint(cf));
assertFalse(CacheFilterShim.isScaleHint(cf));
}
@Test public void settingCacheHintToSpeedInConstructor() {
NGRectangle r = new NGRectangle();
CacheFilter cf = CacheFilterShim.getCacheFilter(r, CacheHint.SPEED);
assertTrue(CacheFilterShim.isRotateHint(cf));
assertTrue(CacheFilterShim.isScaleHint(cf));
}
@Test public void settingCacheHintToSpeed() {
NGRectangle r = new NGRectangle();
CacheFilter cf = CacheFilterShim.getCacheFilter(r, CacheHint.DEFAULT);
cf.setHint(CacheHint.SPEED);
assertTrue(CacheFilterShim.isRotateHint(cf));
assertTrue(CacheFilterShim.isScaleHint(cf));
}
@Test public void settingCacheHintToQualityInConstructor() {
NGRectangle r = new NGRectangle();
CacheFilter cf = CacheFilterShim.getCacheFilter(r, CacheHint.QUALITY);
assertFalse(CacheFilterShim.isRotateHint(cf));
assertFalse(CacheFilterShim.isScaleHint(cf));
}
@Test public void settingCacheHintToQuality() {
NGRectangle r = new NGRectangle();
CacheFilter cf = CacheFilterShim.getCacheFilter(r, CacheHint.SPEED);
cf.setHint(CacheHint.QUALITY);
assertFalse(CacheFilterShim.isRotateHint(cf));
assertFalse(CacheFilterShim.isScaleHint(cf));
}
@Test public void settingCacheHintToRotateInConstructor() {
NGRectangle r = new NGRectangle();
CacheFilter cf = CacheFilterShim.getCacheFilter(r, CacheHint.ROTATE);
assertTrue(CacheFilterShim.isRotateHint(cf));
assertFalse(CacheFilterShim.isScaleHint(cf));
}
@Test public void settingCacheHintToRotate() {
NGRectangle r = new NGRectangle();
CacheFilter cf = CacheFilterShim.getCacheFilter(r, CacheHint.DEFAULT);
cf.setHint(CacheHint.ROTATE);
assertTrue(CacheFilterShim.isRotateHint(cf));
assertFalse(CacheFilterShim.isScaleHint(cf));
}
@Test public void settingCacheHintToScaleInConstructor() {
NGRectangle r = new NGRectangle();
CacheFilter cf = CacheFilterShim.getCacheFilter(r, CacheHint.SCALE);
assertFalse(CacheFilterShim.isRotateHint(cf));
assertTrue(CacheFilterShim.isScaleHint(cf));
}
@Test public void settingCacheHintToScale() {
NGRectangle r = new NGRectangle();
CacheFilter cf = CacheFilterShim.getCacheFilter(r, CacheHint.DEFAULT);
cf.setHint(CacheHint.SCALE);
assertFalse(CacheFilterShim.isRotateHint(cf));
assertTrue(CacheFilterShim.isScaleHint(cf));
}
@Test public void settingCacheHintToScaleAndRotateInConstructor() {
NGRectangle r = new NGRectangle();
CacheFilter cf = CacheFilterShim.getCacheFilter(r, CacheHint.SCALE_AND_ROTATE);
assertTrue(CacheFilterShim.isRotateHint(cf));
assertTrue(CacheFilterShim.isScaleHint(cf));
}
@Test public void settingCacheHintToScaleAndRotate() {
NGRectangle r = new NGRectangle();
CacheFilter cf = CacheFilterShim.getCacheFilter(r, CacheHint.DEFAULT);
cf.setHint(CacheHint.SCALE_AND_ROTATE);
assertTrue(CacheFilterShim.isRotateHint(cf));
assertTrue(CacheFilterShim.isScaleHint(cf));
}
@Test public void cacheFilterReturnsCorrectDirtyBounds() {
NGRectangle r = new NGRectangle();
r.updateRectangle(0.3f, 0.9f, 100.3f, 119.9f, 0, 0);
r.setTransformMatrix(BaseTransform.IDENTITY_TRANSFORM);
r.setTransformedBounds(new RectBounds(0.3f, 0.9f, 100.6f, 120.8f), false);
CacheFilter cf = CacheFilterShim.getCacheFilter(r, CacheHint.DEFAULT);
RectBounds result = new RectBounds();
CacheFilterShim.computeDirtyBounds(cf, result, BaseTransform.IDENTITY_TRANSFORM, new GeneralTransform3D());
assertEquals(new RectBounds(0, 0, 101, 121), result);
NGNodeShim.clearDirty(r);
final Translate2D translation = new Translate2D(10, 10);
r.setTransformMatrix(translation);
r.setTransformedBounds(new RectBounds(10.3f, 10, 110.6f, 130.8f), false);
CacheFilterShim.computeDirtyBounds(cf, result, BaseTransform.IDENTITY_TRANSFORM, new GeneralTransform3D());
assertEquals(new RectBounds(0, 0, 111, 131), result);
}
}
