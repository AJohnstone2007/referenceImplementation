package com.sun.glass.ui.monocle;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.util.Logging;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.WritableByteChannel;
import java.text.MessageFormat;
class FramebufferY8 extends Framebuffer {
private static final int BITS_TO_BYTES = 3;
private final PlatformLogger logger = Logging.getJavaFXLogger();
private final ByteBuffer bb;
private final int width;
private final int height;
private final int bitDepth;
private final int byteDepth;
private ByteBuffer lineByteBuffer;
private Buffer linePixelBuffer;
FramebufferY8(ByteBuffer bb, int width, int height, int depth, boolean clear) {
super(bb, width, height, depth, clear);
this.bb = bb;
this.width = width;
this.height = height;
this.bitDepth = depth;
this.byteDepth = depth >>> BITS_TO_BYTES;
if (byteDepth != Integer.BYTES && byteDepth != Short.BYTES && byteDepth != Byte.BYTES) {
String msg = MessageFormat.format("Unsupported color depth: {0} bpp", bitDepth);
logger.severe(msg);
throw new IllegalArgumentException(msg);
}
}
private void copyNextPixel(IntBuffer source, ByteBuffer target) {
int pixel32 = source.get();
int r = (pixel32 >> 16) & 0xFF;
int g = (pixel32 >> 8) & 0xFF;
int b = pixel32 & 0xFF;
int y = (int) (0.2126f * r + 0.7152f * g + 0.0722f * b);
target.put((byte) y);
}
private void copyNextPixel(IntBuffer source, ShortBuffer target) {
int pixel32 = source.get();
int r = (pixel32 >> 8) & 0xF800;
int g = (pixel32 >> 5) & 0x07E0;
int b = (pixel32 >> 3) & 0x001F;
int pixel16 = r | g | b;
target.put((short) pixel16);
}
@Override
void write(WritableByteChannel out) throws IOException {
bb.clear();
switch (byteDepth) {
case Byte.BYTES: {
if (lineByteBuffer == null) {
lineByteBuffer = ByteBuffer.allocate(width * Byte.BYTES);
lineByteBuffer.order(ByteOrder.nativeOrder());
linePixelBuffer = lineByteBuffer.duplicate();
}
IntBuffer srcPixels = bb.asIntBuffer();
ByteBuffer byteBuffer = (ByteBuffer) linePixelBuffer;
for (int y = 0; y < height; y++) {
byteBuffer.clear();
for (int x = 0; x < width; x++) {
copyNextPixel(srcPixels, byteBuffer);
}
lineByteBuffer.clear();
out.write(lineByteBuffer);
}
break;
}
case Short.BYTES: {
if (lineByteBuffer == null) {
lineByteBuffer = ByteBuffer.allocate(width * Short.BYTES);
lineByteBuffer.order(ByteOrder.nativeOrder());
linePixelBuffer = lineByteBuffer.asShortBuffer();
}
IntBuffer srcPixels = bb.asIntBuffer();
ShortBuffer shortBuffer = (ShortBuffer) linePixelBuffer;
for (int y = 0; y < height; y++) {
shortBuffer.clear();
for (int x = 0; x < width; x++) {
copyNextPixel(srcPixels, shortBuffer);
}
lineByteBuffer.clear();
out.write(lineByteBuffer);
}
break;
}
case Integer.BYTES: {
out.write(bb);
break;
}
default:
String msg = MessageFormat.format("byteDepth={0}", byteDepth);
logger.severe(msg);
throw new IllegalStateException(msg);
}
}
@Override
void copyToBuffer(ByteBuffer out) {
bb.clear();
switch (byteDepth) {
case Byte.BYTES: {
if (lineByteBuffer == null) {
lineByteBuffer = ByteBuffer.allocate(width * Byte.BYTES);
lineByteBuffer.order(ByteOrder.nativeOrder());
linePixelBuffer = lineByteBuffer.duplicate();
}
IntBuffer srcPixels = bb.asIntBuffer();
ByteBuffer byteBuffer = (ByteBuffer) linePixelBuffer;
for (int y = 0; y < height; y++) {
byteBuffer.clear();
for (int x = 0; x < width; x++) {
copyNextPixel(srcPixels, byteBuffer);
}
lineByteBuffer.clear();
out.put(lineByteBuffer);
}
break;
}
case Short.BYTES: {
if (lineByteBuffer == null) {
lineByteBuffer = ByteBuffer.allocate(width * Short.BYTES);
lineByteBuffer.order(ByteOrder.nativeOrder());
linePixelBuffer = lineByteBuffer.asShortBuffer();
}
IntBuffer srcPixels = bb.asIntBuffer();
ShortBuffer shortBuffer = (ShortBuffer) linePixelBuffer;
for (int y = 0; y < height; y++) {
shortBuffer.clear();
for (int x = 0; x < width; x++) {
copyNextPixel(srcPixels, shortBuffer);
}
lineByteBuffer.clear();
out.put(lineByteBuffer);
}
break;
}
case Integer.BYTES: {
out.put(bb);
break;
}
default:
String msg = MessageFormat.format("byteDepth={0}", byteDepth);
logger.severe(msg);
throw new IllegalStateException(msg);
}
}
@Override
public String toString() {
return MessageFormat.format("{0}[width={1} height={2} depth={3} bb={4}]",
getClass().getName(), width, height, bitDepth, bb);
}
}
