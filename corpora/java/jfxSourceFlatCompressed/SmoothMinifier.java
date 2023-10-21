package com.sun.javafx.iio.common;
import java.nio.ByteBuffer;
import java.util.Arrays;
public class SmoothMinifier implements PushbroomScaler {
protected int sourceWidth;
protected int sourceHeight;
protected int numBands;
protected int destWidth;
protected int destHeight;
protected double scaleY;
protected ByteBuffer destBuf;
protected int boxHeight;
protected byte[][] sourceData;
protected int[] leftPoints;
protected int[] rightPoints;
protected int[] topPoints;
protected int[] bottomPoints;
protected int sourceLine;
protected int sourceDataLine;
protected int destLine;
protected int[] tmpBuf;
SmoothMinifier(int sourceWidth, int sourceHeight, int numBands,
int destWidth, int destHeight) {
if (sourceWidth <= 0 || sourceHeight <= 0 || numBands <= 0 ||
destWidth <= 0 || destHeight <= 0 ||
destWidth > sourceWidth || destHeight > sourceHeight) {
throw new IllegalArgumentException();
}
this.sourceWidth = sourceWidth;
this.sourceHeight = sourceHeight;
this.numBands = numBands;
this.destWidth = destWidth;
this.destHeight = destHeight;
this.destBuf = ByteBuffer.wrap(new byte[destHeight * destWidth * numBands]);
double scaleX = (double) sourceWidth / (double) destWidth;
this.scaleY = (double) sourceHeight / (double) destHeight;
int boxWidth = (sourceWidth + destWidth - 1) / destWidth;
this.boxHeight = (sourceHeight + destHeight - 1) / destHeight;
int boxLeft = boxWidth / 2;
int boxRight = boxWidth - boxLeft - 1;
int boxTop = boxHeight / 2;
int boxBottom = boxHeight - boxTop - 1;
this.sourceData = new byte[boxHeight][destWidth * numBands];
this.leftPoints = new int[destWidth];
this.rightPoints = new int[destWidth];
for (int dx = 0; dx < destWidth; dx++) {
int sx = (int) (dx * scaleX);
leftPoints[dx] = sx - boxLeft;
rightPoints[dx] = sx + boxRight;
}
this.topPoints = new int[destHeight];
this.bottomPoints = new int[destHeight];
for (int dy = 0; dy < destHeight; dy++) {
int sy = (int) (dy * scaleY);
topPoints[dy] = sy - boxTop;
bottomPoints[dy] = sy + boxBottom;
}
this.sourceLine = 0;
this.sourceDataLine = 0;
this.destLine = 0;
this.tmpBuf = new int[destWidth * numBands];
}
public ByteBuffer getDestination() {
return this.destBuf;
}
public boolean putSourceScanline(byte[] scanline, int off) {
if (off < 0) {
throw new IllegalArgumentException("off < 0!");
}
if (numBands == 1) {
int leftSample = scanline[off] & 0xff;
int rightSample = scanline[off + sourceWidth - 1] & 0xff;
for (int i = 0; i < destWidth; i++) {
int val = 0;
int rightBound = rightPoints[i];
for (int j = leftPoints[i]; j <= rightBound; j++) {
if (j < 0) {
val += leftSample;
} else if (j >= sourceWidth) {
val += rightSample;
} else {
val += scanline[off + j] & 0xff;
}
}
val /= (rightBound - leftPoints[i] + 1);
sourceData[sourceDataLine][i] = (byte) val;
}
} else {
int rightOff = off + (sourceWidth - 1) * numBands;
for (int i = 0; i < destWidth; i++) {
int leftBound = leftPoints[i];
int rightBound = rightPoints[i];
int numPoints = rightBound - leftBound + 1;
int iBands = i * numBands;
for (int k = 0; k < numBands; k++) {
int leftSample = scanline[off + k] & 0xff;
int rightSample = scanline[rightOff + k] & 0xff;
int val = 0;
for (int j = leftBound; j <= rightBound; j++) {
if (j < 0) {
val += leftSample;
} else if (j >= sourceWidth) {
val += rightSample;
} else {
val += scanline[off + j * numBands + k] & 0xff;
}
}
val /= numPoints;
sourceData[sourceDataLine][iBands + k] = (byte) val;
}
}
}
if (sourceLine == bottomPoints[destLine] ||
(destLine == destHeight - 1 && sourceLine == sourceHeight - 1)) {
assert destBuf.hasArray() : "destBuf.hasArray() == false => destBuf is direct";
byte[] dest = destBuf.array();
int destOffset = destLine * destWidth * numBands;
Arrays.fill(tmpBuf, 0);
for (int y = topPoints[destLine]; y <= bottomPoints[destLine]; y++) {
int index = 0;
if (y < 0) {
index = 0 - sourceLine + sourceDataLine;
} else if (y >= sourceHeight) {
index = (sourceHeight - 1 - sourceLine + sourceDataLine) % boxHeight;
} else {
index = (y - sourceLine + sourceDataLine) % boxHeight;
}
if (index < 0) {
index += boxHeight;
}
byte[] b = sourceData[index];
int destLen = b.length;
for (int x = 0; x < destLen; x++) {
tmpBuf[x] += b[x] & 0xff;
}
}
int sourceLen = tmpBuf.length;
for (int x = 0; x < sourceLen; x++) {
dest[destOffset + x] = (byte) (tmpBuf[x] / boxHeight);
}
if (destLine < destHeight - 1) {
destLine++;
}
}
if (++sourceLine != sourceHeight) {
sourceDataLine = (sourceDataLine + 1) % boxHeight;
}
return destLine == destHeight;
}
}
