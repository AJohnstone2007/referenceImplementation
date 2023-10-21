package test.javafx.scene;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.image.Image;
import test.javafx.scene.image.TestImages;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.pgstub.CursorSizeConverter;
import test.com.sun.javafx.pgstub.StubToolkit;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.ImageCursor;
@RunWith(Parameterized.class)
public final class ImageCursor_findBestImage_Test {
private static final Image[] TEST_CURSOR_IMAGES = {
TestImages.TEST_IMAGE_32x32,
TestImages.TEST_IMAGE_64x64,
TestImages.TEST_IMAGE_32x64,
TestImages.TEST_IMAGE_64x32
};
private static StubToolkit toolkit;
private static CursorSizeConverter oldCursorSizeConverter;
private final int bestWidth;
private final int bestHeight;
private final float hotspotX;
private final float hotspotY;
private final int expectedIndex;
private final float expectedHotspotX;
private final float expectedHotspotY;
@Parameters
public static Collection data() {
return Arrays.asList(new Object[][] {
{ 32, 64, 0, 0, 2, 0, 0 },
{ 64, 32, 32, 32, 3, 63, 31 },
{ 48, 64, 16, 16, 2, 16, 32 },
{ 64, 48, 16, 16, 3, 32, 16 },
{ 92, 92, 16, 4, 1, 32, 8 },
{ 16, 16, 4, 16, 0, 4, 16 },
{ 16, 32, 0, 0, 0, 0, 0 },
{ 32, 16, 0, 0, 0, 0, 0 }
});
}
@BeforeClass
public static void setUpClass() {
toolkit = (StubToolkit) Toolkit.getToolkit();
oldCursorSizeConverter = toolkit.getCursorSizeConverter();
}
@AfterClass
public static void tearDownClass() {
toolkit.setCursorSizeConverter(oldCursorSizeConverter);
}
public ImageCursor_findBestImage_Test(final int bestWidth,
final int bestHeight,
final float hotspotX,
final float hotspotY,
final int expectedIndex,
final float expectedHotspotX,
final float expectedHotspotY) {
this.bestWidth = bestWidth;
this.bestHeight = bestHeight;
this.hotspotX = hotspotX;
this.hotspotY = hotspotY;
this.expectedIndex = expectedIndex;
this.expectedHotspotX = expectedHotspotX;
this.expectedHotspotY = expectedHotspotY;
}
@Test
public void findBestImageTest() {
toolkit.setCursorSizeConverter(
CursorSizeConverter.createConstantConverter(bestWidth,
bestHeight));
final ImageCursor selectedCursor = ImageCursor.chooseBestCursor(
TEST_CURSOR_IMAGES,
hotspotX, hotspotY);
ImageCursorTest.assertCursorEquals(selectedCursor,
TEST_CURSOR_IMAGES[expectedIndex],
expectedHotspotX, expectedHotspotY);
}
}
