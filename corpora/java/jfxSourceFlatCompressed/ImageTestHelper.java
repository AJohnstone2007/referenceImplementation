package test.com.sun.javafx.iio;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Random;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
public class ImageTestHelper {
public static void writeImage(BufferedImage bImg, String fileName, String format, String compression)
throws IOException
{
if (fileName != null) {
File file = new File(fileName);
file.delete();
writeImage(bImg, file, format, compression);
}
}
public static void writeImage(BufferedImage bImg, Object out, String format, String compression)
throws IOException
{
try (ImageOutputStream ios = ImageIO.createImageOutputStream(out)) {
Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName(format);
ImageWriter writer = iter.next();
ImageWriteParam iwp = writer.getDefaultWriteParam();
if (compression != null) {
iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
iwp.setCompressionType(compression);
}
writer.setOutput(ios);
try {
writer.write(null, new IIOImage(bImg, null, null), iwp);
} finally {
writer.dispose();
ios.flush();
}
}
}
public static ByteArrayInputStream writeImageToStream(BufferedImage bImg,
String format, String compression) throws IOException
{
ByteArrayOutputStream out = new ByteArrayOutputStream();
writeImage(bImg, out, format, compression);
return new ByteArrayInputStream(out.toByteArray());
}
public static void drawImageGradient(BufferedImage bImg) {
int w = bImg.getWidth();
int h = bImg.getHeight();
Graphics2D graphics = bImg.createGraphics();
GradientPaint g = new GradientPaint(0, 0, Color.RED, w, h, Color.GREEN);
graphics.setPaint(g);
graphics.fillRect(0, 0, w, h);
}
public static void drawImageRandom(BufferedImage bImg) {
int w = bImg.getWidth();
int h = bImg.getHeight();
Random r = new Random(1);
for (int y = 0; y < h; y++) {
for (int x = 0; x < w; x++) {
bImg.setRGB(x, y, r.nextInt(1 << 24));
}
}
}
public static void drawImageHue(BufferedImage bImg) {
int w = bImg.getWidth();
int h = bImg.getHeight();
for (int y = 0; y < h; y++) {
float s = 2.0f * y / h;
if (s > 1) {
s = 1;
}
float b = 2.0f * (h - y) / h;
if (b > 1) {
b = 1;
}
for (int x = 0; x < w; x++) {
float hue = (float) x / w;
bImg.setRGB(x, y, Color.HSBtoRGB(hue, s, b));
}
}
}
public static void drawImageAll(BufferedImage bImg) {
int w = bImg.getWidth();
int h = bImg.getHeight();
for (int y = 0; y < h; y++) {
for (int x = 0; x < w; x++) {
bImg.setRGB(x, y, y * h + x);
}
}
}
public static InputStream createTestImageStream(String format)
throws IOException
{
BufferedImage bImg = new BufferedImage(509, 157, BufferedImage.TYPE_INT_RGB);
ImageTestHelper.drawImageRandom(bImg);
return ImageTestHelper.writeImageToStream(bImg, format, null);
}
public static InputStream createStutteringInputStream(InputStream in) {
return new FilterInputStream(in) {
private final Random rnd = new Random(0);
private int numReadStutters = 10;
private int numSkipStutters = 10;
@Override
public int read(byte[] b, int off, int len) throws IOException {
if (numReadStutters > 0 && rnd.nextBoolean()) {
numReadStutters--;
return 0;
}
return in.read(b, off, 1);
}
@Override
public long skip(long n) throws IOException {
if (numSkipStutters > 0 && rnd.nextBoolean()) {
numSkipStutters--;
return 0;
}
return in.skip(1);
}
};
}
public static ByteArrayInputStream constructStreamFromInts(int[] ints) {
byte[] bytes = new byte[ints.length];
for (int i = 0; i < ints.length; i++) {
bytes[i] = (byte)ints[i];
}
return new ByteArrayInputStream(bytes);
}
}
