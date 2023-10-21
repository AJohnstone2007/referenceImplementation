package test.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.FramebufferY8Shim;
import com.sun.glass.ui.monocle.FramebufferY8SuperShim;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
public class FramebufferY8Test {
private static final String IMAGE_FORMAT = "png";
private static final String IMAGE_NAME = "allrgb";
private static final String IMAGE_PATH = IMAGE_NAME + "." + IMAGE_FORMAT;
private static final String IMAGE_PATH_Y8 = IMAGE_NAME + "Y8." + IMAGE_FORMAT;
private static final int ITERATIONS = 10;
private static final int VALUES_4_BIT = 16;
private static final int VALUES_12_BIT = VALUES_4_BIT * VALUES_4_BIT * VALUES_4_BIT;
private static final int BITS_TO_BYTES = 3;
private static final int WIDTH = VALUES_12_BIT;
private static final int HEIGHT = VALUES_12_BIT;
private static ByteBuffer bb;
private static IntBuffer pixels;
@BeforeClass
public static void onlyOnce() {
bb = ByteBuffer.allocate(WIDTH * HEIGHT * Integer.BYTES);
bb.order(ByteOrder.nativeOrder());
pixels = bb.asIntBuffer();
IntStream.range(0, WIDTH * HEIGHT).forEachOrdered(pixels::put);
pixels.flip();
}
private ByteBuffer copyOld(int bitsPerPixel) {
int bytesPerPixel = bitsPerPixel >>> BITS_TO_BYTES;
var source = new FramebufferY8SuperShim(bb, WIDTH, HEIGHT, bitsPerPixel, true);
var target = ByteBuffer.allocate(WIDTH * HEIGHT * bytesPerPixel);
source.copyToBuffer(target);
target.flip();
return target;
}
private ByteBuffer copyNew(int bitsPerPixel) {
int bytesPerPixel = bitsPerPixel >>> BITS_TO_BYTES;
var source = new FramebufferY8Shim(bb, WIDTH, HEIGHT, bitsPerPixel, true);
var target = ByteBuffer.allocate(WIDTH * HEIGHT * bytesPerPixel);
source.copyToBuffer(target);
target.flip();
return target;
}
private void copyTest(int bitsPerPixel) {
ByteBuffer oldBuffer = copyOld(bitsPerPixel);
ByteBuffer newBuffer = copyNew(bitsPerPixel);
if (oldBuffer.hasArray() && newBuffer.hasArray()) {
Assert.assertArrayEquals(oldBuffer.array(), newBuffer.array());
} else {
Assert.assertEquals(oldBuffer, newBuffer);
}
}
private ByteArrayOutputStream writeOld(int bitsPerPixel) throws IOException {
int bytesPerPixel = bitsPerPixel >>> BITS_TO_BYTES;
var source = new FramebufferY8SuperShim(bb, WIDTH, HEIGHT, bitsPerPixel, true);
try (var target = new ByteArrayOutputStream(WIDTH * HEIGHT * bytesPerPixel);
var channel = Channels.newChannel(target)) {
source.write(channel);
return target;
}
}
private ByteArrayOutputStream writeNew(int bitsPerPixel) throws IOException {
int bytesPerPixel = bitsPerPixel >>> BITS_TO_BYTES;
var source = new FramebufferY8Shim(bb, WIDTH, HEIGHT, bitsPerPixel, true);
try (var target = new ByteArrayOutputStream(WIDTH * HEIGHT * bytesPerPixel);
var channel = Channels.newChannel(target)) {
source.write(channel);
return target;
}
}
private void writeTest(int bitsPerPixel) throws IOException {
ByteArrayOutputStream oldStream = writeOld(bitsPerPixel);
ByteArrayOutputStream newStream = writeNew(bitsPerPixel);
Assert.assertArrayEquals(oldStream.toByteArray(), newStream.toByteArray());
}
private void printTime(Object source, String method, long duration) {
float msPerFrame = (float) duration / ITERATIONS;
System.out.println(String.format(
"Converted %,d frames of %,d x %,d px to RGB565 in %,d ms (%,.0f ms/frame): %s.%s",
ITERATIONS, WIDTH, HEIGHT, duration, msPerFrame,
source.getClass().getSuperclass().getSimpleName(), method));
}
private long timeOldCopyTo16() {
int bitsPerPixel = Short.SIZE;
int bytesPerPixel = bitsPerPixel >>> BITS_TO_BYTES;
var source = new FramebufferY8SuperShim(bb, WIDTH, HEIGHT, bitsPerPixel, true);
var target = ByteBuffer.allocate(WIDTH * HEIGHT * bytesPerPixel);
long begin = System.currentTimeMillis();
for (int i = 0; i < ITERATIONS; i++) {
source.copyToBuffer(target);
target.flip();
}
long end = System.currentTimeMillis();
long duration = end - begin;
printTime(source, "copyToBuffer", duration);
return duration;
}
private long timeNewCopyTo16() {
int bitsPerPixel = Short.SIZE;
int bytesPerPixel = bitsPerPixel >>> BITS_TO_BYTES;
var source = new FramebufferY8Shim(bb, WIDTH, HEIGHT, bitsPerPixel, true);
var target = ByteBuffer.allocate(WIDTH * HEIGHT * bytesPerPixel);
long begin = System.currentTimeMillis();
for (int i = 0; i < ITERATIONS; i++) {
source.copyToBuffer(target);
target.flip();
}
long end = System.currentTimeMillis();
long duration = end - begin;
printTime(source, "copyToBuffer", duration);
return duration;
}
private long timeOldWriteTo16() throws IOException {
int bitsPerPixel = Short.SIZE;
int bytesPerPixel = bitsPerPixel >>> BITS_TO_BYTES;
var source = new FramebufferY8SuperShim(bb, WIDTH, HEIGHT, bitsPerPixel, true);
try (var target = new ByteArrayOutputStream(WIDTH * HEIGHT * bytesPerPixel);
var channel = Channels.newChannel(target)) {
long begin = System.currentTimeMillis();
for (int i = 0; i < ITERATIONS; i++) {
source.write(channel);
target.reset();
}
long end = System.currentTimeMillis();
long duration = end - begin;
printTime(source, "write", duration);
return duration;
}
}
private long timeNewWriteTo16() throws IOException {
int bitsPerPixel = Short.SIZE;
int bytesPerPixel = bitsPerPixel >>> BITS_TO_BYTES;
var source = new FramebufferY8Shim(bb, WIDTH, HEIGHT, bitsPerPixel, true);
try (var target = new ByteArrayOutputStream(WIDTH * HEIGHT * bytesPerPixel);
var channel = Channels.newChannel(target)) {
long begin = System.currentTimeMillis();
for (int i = 0; i < ITERATIONS; i++) {
source.write(channel);
target.reset();
}
long end = System.currentTimeMillis();
long duration = end - begin;
printTime(source, "write", duration);
return duration;
}
}
@Test
public void copyTo16() {
copyTest(Short.SIZE);
}
@Test
public void copyTo32() {
copyTest(Integer.SIZE);
}
@Test
public void writeTo16() throws IOException {
writeTest(Short.SIZE);
}
@Test
public void writeTo32() throws IOException {
writeTest(Integer.SIZE);
}
@Test
public void timeCopyTo16() {
long oldTime = timeOldCopyTo16();
long newTime = timeNewCopyTo16();
if (newTime > oldTime) {
System.err.println("Warning: FramebufferY8.copyToBuffer with 16-bit target is slower");
}
}
@Test
public void timeWriteTo16() throws IOException {
long oldTime = timeOldWriteTo16();
long newTime = timeNewWriteTo16();
if (newTime > oldTime) {
System.err.println("Warning: FramebufferY8.write with 16-bit target is slower");
}
}
@Ignore("Saves the source ARGB32 buffer as a PNG image")
@Test
public void saveImage() {
var image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
for (int y = 0; y < HEIGHT; y++) {
for (int x = 0; x < WIDTH; x++) {
image.setRGB(x, y, pixels.get());
}
}
try {
ImageIO.write(image, IMAGE_FORMAT, new File(IMAGE_PATH));
} catch (IOException e) {
System.err.println(String.format("Error saving %s (%s)", IMAGE_PATH, e));
}
}
@Ignore("Saves the target Y8 buffer as a PNG image")
@Test
public void saveImageY8() {
int bitsPerPixel = Byte.SIZE;
int bytesPerPixel = bitsPerPixel >>> BITS_TO_BYTES;
var source = new FramebufferY8Shim(bb, WIDTH, HEIGHT, bitsPerPixel, true);
var target = ByteBuffer.allocate(WIDTH * HEIGHT * bytesPerPixel);
source.copyToBuffer(target);
target.flip();
var image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
System.arraycopy(target.array(), 0, data, 0, WIDTH * HEIGHT);
try {
ImageIO.write(image, IMAGE_FORMAT, new File(IMAGE_PATH_Y8));
} catch (IOException e) {
System.err.println(String.format("Error saving %s (%s)", IMAGE_PATH_Y8, e));
}
}
}
