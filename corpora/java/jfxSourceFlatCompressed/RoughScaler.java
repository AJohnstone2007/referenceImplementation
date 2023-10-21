package com.sun.javafx.iio.common;
import java.nio.ByteBuffer;
public class RoughScaler implements PushbroomScaler {
protected int numBands;
protected int destWidth;
protected int destHeight;
protected double scaleY;
protected ByteBuffer destBuf;
protected int[] colPositions;
protected int sourceLine;
protected int nextSourceLine;
protected int destLine;
public RoughScaler(int sourceWidth, int sourceHeight, int numBands,
int destWidth, int destHeight) {
if (sourceWidth <= 0 || sourceHeight <= 0 || numBands <= 0 ||
destWidth <= 0 || destHeight <= 0) {
throw new IllegalArgumentException();
}
this.numBands = numBands;
this.destWidth = destWidth;
this.destHeight = destHeight;
this.destBuf = ByteBuffer.wrap(new byte[destHeight * destWidth * numBands]);
double scaleX = (double) sourceWidth / (double) destWidth;
this.scaleY = (double) sourceHeight / (double) destHeight;
this.colPositions = new int[destWidth];
for (int i = 0; i < destWidth; i++) {
int pos = (int) ((i + 0.5) * scaleX);
colPositions[i] = pos * numBands;
}
this.sourceLine = 0;
this.destLine = 0;
this.nextSourceLine = (int) (0.5 * scaleY);
}
public ByteBuffer getDestination() {
return this.destBuf;
}
public boolean putSourceScanline(byte[] scanline, int off) {
if (off < 0) {
throw new IllegalArgumentException("off < 0!");
}
if (destLine < destHeight) {
if (sourceLine == nextSourceLine) {
assert destBuf.hasArray() : "destBuf.hasArray() == false => destBuf is direct";
byte[] dest = destBuf.array();
int destOffset = destLine * destWidth * numBands;
int doff = destOffset;
for (int i = 0; i < destWidth; i++) {
int sourceOffset = off + this.colPositions[i];
for (int j = 0; j < numBands; j++) {
dest[doff++] = scanline[sourceOffset + j];
}
}
while ((int) ((++destLine + 0.5) * scaleY) == sourceLine)
{
System.arraycopy(dest, destOffset, dest, doff, destWidth * numBands);
doff += destWidth * numBands;
}
nextSourceLine = (int) ((destLine + 0.5) * scaleY);
}
++sourceLine;
}
return destLine == destHeight;
}
}
