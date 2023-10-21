package test.com.sun.javafx.sg.prism;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.sg.prism.NGImageView;
import com.sun.javafx.sg.prism.NGNodeShim;
import com.sun.prism.Image;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
public class NGImageViewTest extends NGTestBase {
static final byte[] ICON_PIXELS = new byte[16*16];
static final Image ICON = Image.fromByteRgbData(ICON_PIXELS, 16, 16);
NGImageView imageView;
@Before
public void setup() {
imageView = new NGImageView();
imageView.setImage(ICON);
imageView.setX(10);
imageView.setY(10);
imageView.setViewport(0, 0, 0, 0, 16, 16);
}
@Test
public void testSupportsOpaqueRegion() {
assertTrue(NGNodeShim.supportsOpaqueRegions(imageView));
}
@Test
public void hasOpaqueRegionWithNonEmptyImage() {
assertTrue(NGNodeShim.hasOpaqueRegion(imageView));
}
@Test
public void hasOpaqueRegionIfViewPortGreaterThanOne() {
assertTrue(NGNodeShim.hasOpaqueRegion(imageView));
imageView.setViewport(0, 0, 2, 2, 16, 16);
assertTrue(NGNodeShim.hasOpaqueRegion(imageView));
imageView.setViewport(0, 0, 1, 1, 16, 16);
assertTrue(NGNodeShim.hasOpaqueRegion(imageView));
imageView.setViewport(0, 0, 0, 0, .1f, .1f);
assertFalse(NGNodeShim.hasOpaqueRegion(imageView));
}
@Test
public void doesNotHaveOpaqueRegionForNullImage() {
imageView.setImage(null);
assertFalse(NGNodeShim.hasOpaqueRegion(imageView));
}
@Test
public void computeOpaqueRegionForWholeNumbers() {
assertEquals(new RectBounds(10, 10, 26, 26),
NGNodeShim.computeOpaqueRegion(imageView, new RectBounds()));
}
}
