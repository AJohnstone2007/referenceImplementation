package com.sun.glass.ui.monocle;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
class NativeCursors {
static void colorKeyCursor(byte[] source,
Buffer dest,
int targetDepth,
int transparentPixel) {
switch (targetDepth) {
case 32:
colorKeyCursor32(source,
(IntBuffer) dest, transparentPixel);
break;
case 16:
colorKeyCursor16(source,
(ShortBuffer) dest, transparentPixel);
break;
default:
throw new UnsupportedOperationException();
}
}
static void offsetCursor(Buffer sourceBuffer,
Buffer destBuffer,
int offsetX, int offsetY,
int width, int height,
int depth, int transparentPixel) {
switch (depth) {
case 32:
offsetCursor32((IntBuffer) sourceBuffer,
(IntBuffer) destBuffer,
offsetX, offsetY,
width, height,
transparentPixel);
break;
case 16:
offsetCursor16((ShortBuffer) sourceBuffer,
(ShortBuffer) destBuffer,
offsetX, offsetY,
width, height,
transparentPixel);
break;
default:
throw new UnsupportedOperationException();
}
}
private static void colorKeyCursor32(byte[] source, IntBuffer destBuffer,
int transparentPixel) {
IntBuffer sourceBuffer = ByteBuffer.wrap(source).asIntBuffer();
while (sourceBuffer.position() < sourceBuffer.limit()) {
int i = sourceBuffer.get();
if ((i & 0xff) == 0) {
destBuffer.put(transparentPixel);
} else {
destBuffer.put(i);
}
}
destBuffer.rewind();
}
private static void colorKeyCursor16(byte[] source, ShortBuffer destBuffer,
int transparentPixel) {
IntBuffer sourceBuffer = ByteBuffer.wrap(source).asIntBuffer();
while (sourceBuffer.position() < sourceBuffer.limit()) {
int i = sourceBuffer.get();
if ((i & 0xff) == 0) {
destBuffer.put((short) transparentPixel);
} else {
int pixel = ((i >> 8) & 0xf800)
| ((i >> 5) & 0x7e0)
| ( (i >> 3) & 0x1f);
destBuffer.put((short) pixel);
}
}
destBuffer.rewind();
}
private static void offsetCursor32(IntBuffer sourceBuffer,
IntBuffer destBuffer,
int offsetX, int offsetY,
int width, int height,
int transparentPixel) {
if (offsetX == 0 && offsetY == 0) {
destBuffer.put(sourceBuffer);
} else {
int i;
for (i = 0; i < offsetY; i++) {
for (int j = 0; j < width; j++) {
destBuffer.put(transparentPixel);
}
}
for (; i < height; i++) {
int j;
for (j = 0; j < offsetX; j++) {
destBuffer.put(transparentPixel);
}
int srcPos = (i - offsetY) * width;
sourceBuffer.limit(srcPos + width - j);
sourceBuffer.position(srcPos);
destBuffer.put(sourceBuffer);
}
}
sourceBuffer.rewind();
sourceBuffer.limit(sourceBuffer.capacity());
destBuffer.rewind();
destBuffer.limit(destBuffer.capacity());
}
private static void offsetCursor16(ShortBuffer sourceBuffer,
ShortBuffer destBuffer,
int offsetX, int offsetY,
int width, int height,
int transparentPixel) {
if (offsetX == 0 && offsetY == 0) {
destBuffer.put(sourceBuffer);
} else {
int i;
for (i = 0; i < offsetY; i++) {
for (int j = 0; j < width; j++) {
destBuffer.put((short) transparentPixel);
}
}
for (; i < height; i++) {
int j;
for (j = 0; j < offsetX; j++) {
destBuffer.put((short) transparentPixel);
}
int srcPos = (i - offsetY) * width;
sourceBuffer.limit(srcPos + width - j);
sourceBuffer.position(srcPos);
destBuffer.put(sourceBuffer);
}
}
sourceBuffer.rewind();
sourceBuffer.limit(sourceBuffer.capacity());
destBuffer.rewind();
destBuffer.limit(destBuffer.capacity());
}
}
