package test.com.sun.javafx.iio.bmp;
import com.sun.javafx.iio.ImageFrame;
import com.sun.javafx.iio.ImageLoader;
import com.sun.javafx.iio.ImageLoaderFactory;
import com.sun.javafx.iio.bmp.BMPImageLoaderFactory;
import com.sun.javafx.iio.bmp.BMPImageLoaderShim;
import test.com.sun.javafx.iio.ImageTestHelper;
import com.sun.prism.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import static org.junit.Assert.*;
import org.junit.Test;
public class BMPImageLoaderTest {
static final boolean writeFiles = false;
static final int testWidth = 509, testHeight = 157;
int getByte(int dword, int shift) {
return (dword >> shift) & 0xff;
}
boolean compareByte(int p1, int p2, int shift, int tolerance) {
return Math.abs(getByte(p1, shift) - getByte(p2, shift)) <= tolerance;
}
boolean compareRGB(int p1, int p2, int tolerance) {
return compareByte(p1, p2, 24, tolerance) &&
compareByte(p1, p2, 16, tolerance) &&
compareByte(p1, p2, 8, tolerance);
}
void compare(Image img, BufferedImage bImg) {
assertNotNull(img);
assertNotNull(bImg);
int w = bImg.getWidth(), h = bImg.getHeight();
assertEquals("Unmatched width", w, img.getWidth());
assertEquals("Unmatched height", h, img.getHeight());
for (int y = 0; y < h; y++) {
for (int x = 0; x < w; x++) {
int p1 = bImg.getRGB(x, y);
int p2 = img.getArgb(x, y);
if (!compareRGB(p1, p2, 1)) {
throw new org.junit.ComparisonFailure(
"pixel " + x + ", " + y + " does not match",
String.format("0x%08X", p1), String.format("0x%08X", p2)
);
}
}
}
}
Image loadImage(InputStream stream) throws IOException {
ImageLoaderFactory loaderFactory = BMPImageLoaderFactory.getInstance();
ImageLoader loader = loaderFactory.createImageLoader(stream);
assertNotNull(loader);
ImageFrame frame = loader.load(0, 0, 0, true, true);
return Image.convertImageFrame(frame);
}
BufferedImage create4BitImage() {
int[] cmap = new int[16];
int i = 0;
for (int r = 0; r < 2; r++) {
for (int g = 0; g < 2; g++) {
for (int b = 0; b < 2; b++) {
cmap[i++] = 0xff << 24 | r * 255 << 16 | g * 255 << 8 | b * 255;
if ((r | g | b) == 0) {
cmap[i++] = 0xffc0c0c0;
} else {
cmap[i++] = 0xff << 24 | r * 128 << 16 | g * 128 << 8 | b * 128;
}
}
}
}
IndexColorModel cm = new IndexColorModel(4, 16, cmap, 0, false, -1, DataBuffer.TYPE_BYTE);
return new BufferedImage(testWidth, testHeight, BufferedImage.TYPE_BYTE_BINARY, cm);
}
BufferedImage createImage(int type) {
return new BufferedImage(testWidth, testHeight, type);
}
void writeBMPFile(BufferedImage bImg, String fileName, String compression) {
try {
ImageTestHelper.writeImage(bImg, fileName, "bmp", compression);
} catch (IOException e) {
System.out.println("writeBMPFile " + fileName + " failed: " + e);
}
}
Image getImage(BufferedImage bImg, String compression) throws IOException {
ByteArrayInputStream stream =
ImageTestHelper.writeImageToStream(bImg, "bmp", compression);
return loadImage(stream);
}
void testImageType(int type, String fileName, String compression) throws IOException {
BufferedImage bImg = createImage(type);
testImage(bImg, fileName, compression);
}
void testImageType(int type, String fileName) throws IOException {
BufferedImage bImg = createImage(type);
testImage(bImg, fileName, null);
}
void testImage(BufferedImage bImg, String fileName, String compression) throws IOException {
ImageTestHelper.drawImageRandom(bImg);
if (writeFiles) {
writeBMPFile(bImg, fileName, compression);
}
Image image = getImage(bImg, compression);
compare(image, bImg);
}
@Test
public void testRT32213() throws IOException {
final int[] bytes = {
0x42, 0x4d, 0x42, 0x00, 0x00, 0x00, 0x00, 0x00,
0x00, 0x00, 0x3e, 0x00, 0x00, 0x00, 0x28, 0x00,
0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, 0x00,
0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00,
0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00,
0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
0x00, 0x00, 0xff, 0xff, 0xff, 0x00, 0x80, 0x00,
0x00, 0x00
};
ByteArrayInputStream stream = ImageTestHelper.constructStreamFromInts(bytes);
Image image = loadImage(stream);
stream.reset();
BufferedImage bImg = ImageIO.read(new MemoryCacheImageInputStream(stream));
compare(image, bImg);
}
@Test
public void testRT15619() throws IOException {
InputStream stream = ImageTestHelper.createTestImageStream("bmp");
InputStream testStream = ImageTestHelper.createStutteringInputStream(stream);
loadImage(testStream);
}
@Test
public void test1Bit() throws IOException {
testImageType(BufferedImage.TYPE_BYTE_BINARY, "out1bit.bmp");
}
@Test
public void test4Bit() throws IOException {
testImage(create4BitImage(), "out4bit.bmp", null);
}
public void test4BitRLE() throws IOException {
testImage(create4BitImage(), "out4bitRLE.bmp", "BI_RLE4");
}
@Test
public void test8Bit() throws IOException {
testImageType(BufferedImage.TYPE_BYTE_INDEXED, "out8bit.bmp");
}
@Test
public void test8BitRLE() throws IOException {
testImageType(BufferedImage.TYPE_BYTE_INDEXED, "out8bitRLE.bmp", "BI_RLE8");
}
@Test
public void test16Bit() throws IOException {
testImageType(BufferedImage.TYPE_USHORT_555_RGB, "out16bit.bmp");
}
@Test
public void test24Bit() throws IOException {
testImageType(BufferedImage.TYPE_INT_RGB, "out24bit.bmp");
}
@Test
public void testBitfields() throws IOException {
testImageType(BufferedImage.TYPE_USHORT_555_RGB, "out16bit555.bmp", "BI_BITFIELDS");
testImageType(BufferedImage.TYPE_USHORT_565_RGB, "out16bit565.bmp", "BI_BITFIELDS");
}
@Test
public void testMasks() {
assertTrue(BMPImageLoaderShim.checkDisjointMasks(1, 2, 4));
assertTrue(BMPImageLoaderShim.checkDisjointMasks(0x00F, 0x0F0, 0xF00));
assertFalse(BMPImageLoaderShim.checkDisjointMasks(1, 2, 5));
assertFalse(BMPImageLoaderShim.checkDisjointMasks(2, 1, 6));
assertTrue(BMPImageLoaderShim.isPow2Minus1(1));
assertTrue(BMPImageLoaderShim.isPow2Minus1(3));
assertTrue(BMPImageLoaderShim.isPow2Minus1(7));
assertFalse(BMPImageLoaderShim.isPow2Minus1(2));
assertFalse(BMPImageLoaderShim.isPow2Minus1(11));
}
}
