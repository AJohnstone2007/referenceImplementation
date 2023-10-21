package test.com.sun.javafx.iio;
import com.sun.javafx.iio.ImageFrame;
import com.sun.javafx.iio.ImageStorage;
import com.sun.javafx.iio.ImageStorageException;
import com.sun.prism.Image;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class ImageHiDPITest {
private static final String IMAGE_NAME = "checker.png";
private String imagePath;
@Before
public void setup() {
imagePath = this.getClass().getResource(IMAGE_NAME).toExternalForm();
assertNotNull(imagePath);
}
private ImageFrame loadImage(String path, float pixelScale) {
try {
ImageFrame[] imageFrames =
new ImageStorage().loadAll(path, null, 0, 0, true, pixelScale, true);
assertNotNull(imageFrames);
assertEquals(1, imageFrames.length);
ImageFrame imageFrame = imageFrames[0];
assertNotNull(imageFrame);
assertEquals("Unexpected pixel scale",
pixelScale, imageFrame.getPixelScale(), 0.0001f);
int width = imageFrame.getWidth();
int height = imageFrame.getHeight();
assertTrue("Image size must be at least 8x8", width >= 8 && height >= 8);
return imageFrame;
} catch (ImageStorageException ex) {
throw new RuntimeException(ex);
}
}
private void testPixelGet(float pixelScale) {
ImageFrame imageFrame = loadImage(imagePath, pixelScale);
int width = imageFrame.getWidth();
int height = imageFrame.getHeight();
Image image = Image.convertImageFrame(imageFrame);
assertNotNull(image);
assertEquals(width, image.getWidth());
assertEquals(height, image.getHeight());
int w = (int) (width / pixelScale);
int h = (int) (height / pixelScale);
final int[] xvals = {
w / 2,
2,
w - 2,
2,
w - 2
};
final int[] yvals = {
h / 2,
2,
2,
h - 2,
h - 2
};
final int[] exColors = {
0xffff00ff,
0xffff0000,
0xff0000ff,
0xffff8080,
0xff8ff080
};
for (int i = 0; i < xvals.length; i++) {
int pix1 = image.getArgb(xvals[i], yvals[i]);
assertEquals("getArgb returns incorrect color", exColors[i], pix1);
int pix2 = image.getArgb(xvals[i], yvals[i]);
assertEquals("second call to getArgb returns different result", pix1, pix2);
}
}
@Test
public void testNormalPixelGet() {
testPixelGet(1.0f);
}
@Test
public void testScaledPixelGet() {
testPixelGet(2.0f);
}
@Test
public void testScaledImageSize() {
ImageFrame imageFrame1 = loadImage(imagePath, 1.0f);
ImageFrame imageFrame2 = loadImage(imagePath, 2.0f);
int exWidth2 = imageFrame1.getWidth() * 2;
int exHeight2 = imageFrame1.getHeight()* 2;
assertEquals("width of @2x image is wrong", exWidth2, imageFrame2.getWidth());
assertEquals("height of @2x image is wrong", exHeight2, imageFrame2.getHeight());
}
}
