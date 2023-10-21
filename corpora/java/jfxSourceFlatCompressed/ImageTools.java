package com.sun.javafx.iio.common;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.iio.ImageFrame;
import com.sun.javafx.iio.ImageMetadata;
import com.sun.javafx.iio.ImageStorage;
import com.sun.javafx.iio.ImageStorage.ImageType;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
public class ImageTools {
public static final int PROGRESS_INTERVAL = 5;
public static int readFully(InputStream stream,
byte[] b, int off, int len) throws IOException {
if (len < 0) {
throw new IndexOutOfBoundsException();
}
int requestedLength = len;
if (off < 0 || len < 0 || off + len > b.length || off + len < 0) {
throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > b.length!");
}
while (len > 0) {
int nbytes = stream.read(b, off, len);
if (nbytes == -1) {
throw new EOFException();
}
off += nbytes;
len -= nbytes;
}
return requestedLength;
}
public static int readFully(InputStream stream, byte[] b) throws IOException {
return readFully(stream, b, 0, b.length);
}
public static void skipFully(InputStream stream, long n) throws IOException {
while (n > 0) {
long skipped = stream.skip(n);
if (skipped <= 0) {
if (stream.read() == -1) {
throw new EOFException();
}
n--;
} else {
n -= skipped;
}
}
}
public static ImageType getConvertedType(ImageType type) {
ImageType retType = type;
switch (type) {
case GRAY:
retType = ImageType.GRAY;
break;
case GRAY_ALPHA:
case GRAY_ALPHA_PRE:
case PALETTE_ALPHA:
case PALETTE_ALPHA_PRE:
case PALETTE_TRANS:
case RGBA:
retType = ImageType.RGBA_PRE;
break;
case PALETTE:
case RGB:
retType = ImageType.RGB;
break;
case RGBA_PRE:
retType = ImageType.RGBA_PRE;
break;
default:
throw new IllegalArgumentException("Unsupported ImageType " + type);
}
return retType;
}
public static byte[] createImageArray(ImageType type, int width, int height) {
int numBands = 0;
switch (type) {
case GRAY:
case PALETTE:
case PALETTE_ALPHA:
case PALETTE_ALPHA_PRE:
numBands = 1;
break;
case GRAY_ALPHA:
case GRAY_ALPHA_PRE:
numBands = 2;
break;
case RGB:
numBands = 3;
break;
case RGBA:
case RGBA_PRE:
numBands = 4;
break;
default:
throw new IllegalArgumentException("Unsupported ImageType " + type);
}
return new byte[width * height * numBands];
}
public static ImageFrame convertImageFrame(ImageFrame frame) {
ImageFrame retFrame;
ImageType type = frame.getImageType();
ImageType convertedType = getConvertedType(type);
if (convertedType == type) {
retFrame = frame;
} else {
byte[] inArray = null;
Buffer buf = frame.getImageData();
if (!(buf instanceof ByteBuffer)) {
throw new IllegalArgumentException("!(frame.getImageData() instanceof ByteBuffer)");
}
ByteBuffer bbuf = (ByteBuffer) buf;
if (bbuf.hasArray()) {
inArray = bbuf.array();
} else {
inArray = new byte[bbuf.capacity()];
bbuf.get(inArray);
}
int width = frame.getWidth();
int height = frame.getHeight();
int inStride = frame.getStride();
byte[] outArray = createImageArray(convertedType, width, height);
ByteBuffer newBuf = ByteBuffer.wrap(outArray);
int outStride = outArray.length / height;
byte[][] palette = frame.getPalette();
ImageMetadata metadata = frame.getMetadata();
int transparentIndex = metadata.transparentIndex != null ? metadata.transparentIndex : 0;
convert(width, height, type,
inArray, 0, inStride, outArray, 0, outStride,
palette, transparentIndex, false);
ImageMetadata imd = new ImageMetadata(metadata.gamma,
metadata.blackIsZero, null,
metadata.backgroundColor, null,
metadata.delayTime, metadata.loopCount,
metadata.imageWidth, metadata.imageHeight,
metadata.imageLeftPosition, metadata.imageTopPosition,
metadata.disposalMethod);
retFrame = new ImageFrame(convertedType, newBuf, width, height,
outStride, null, imd);
}
return retFrame;
}
public static byte[] convert(int width, int height, ImageType inputType,
byte[] input, int inputOffset, int inRowStride,
byte[] output, int outputOffset, int outRowStride,
byte[][] palette, int transparentIndex, boolean skipTransparent) {
if (inputType == ImageType.GRAY ||
inputType == ImageType.RGB ||
inputType == ImageType.RGBA_PRE) {
if (input != output) {
int bytesPerRow = width;
if (inputType == ImageType.RGB) {
bytesPerRow *= 3;
} else if (inputType == ImageType.RGBA_PRE) {
bytesPerRow *= 4;
}
if (height == 1) {
System.arraycopy(input, inputOffset, output, outputOffset, bytesPerRow);
} else {
int inRowOffset = inputOffset;
int outRowOffset = outputOffset;
for (int row = 0; row < height; row++) {
System.arraycopy(input, inRowOffset, output, outRowOffset, bytesPerRow);
inRowOffset += inRowStride;
outRowOffset += outRowStride;
}
}
}
} else if (inputType == ImageType.GRAY_ALPHA || inputType == ImageType.GRAY_ALPHA_PRE) {
int inOffset = inputOffset;
int outOffset = outputOffset;
if (inputType == ImageType.GRAY_ALPHA) {
for (int y = 0; y < height; y++) {
int inOff = inOffset;
int outOff = outOffset;
for (int x = 0; x < width; x++) {
byte gray = input[inOff++];
int alpha = input[inOff++] & 0xff;
float f = alpha / 255.0F;
gray = (byte) (f * (gray & 0xff));
output[outOff++] = gray;
output[outOff++] = gray;
output[outOff++] = gray;
output[outOff++] = (byte) alpha;
}
inOffset += inRowStride;
outOffset += outRowStride;
}
} else {
for (int y = 0; y < height; y++) {
int inOff = inOffset;
int outOff = outOffset;
for (int x = 0; x < width; x++) {
byte gray = input[inOff++];
output[outOff++] = gray;
output[outOff++] = gray;
output[outOff++] = gray;
output[outOff++] = input[inOff++];
}
inOffset += inRowStride;
outOffset += outRowStride;
}
}
} else if (inputType == ImageType.PALETTE) {
int inOffset = inputOffset;
int outOffset = outputOffset;
byte[] red = palette[0];
byte[] green = palette[1];
byte[] blue = palette[2];
int inOff = inOffset;
int outOff = outOffset;
for (int x = 0; x < width; x++) {
int index = (input[inOff++] & 0xff);
output[outOff++] = red[index];
output[outOff++] = green[index];
output[outOff++] = blue[index];
outOffset += outRowStride;
}
} else if (inputType == ImageType.PALETTE_ALPHA) {
int inOffset = inputOffset;
int outOffset = outputOffset;
byte[] red = palette[0];
byte[] green = palette[1];
byte[] blue = palette[2];
byte[] alpha = palette[3];
int inOff = inOffset;
int outOff = outOffset;
for (int x = 0; x < width; x++) {
int index = input[inOff++] & 0xff;
byte r = red[index];
byte g = green[index];
byte b = blue[index];
int a = alpha[index] & 0xff;
float f = a / 255.0F;
output[outOff++] = (byte) (f * (r & 0xff));
output[outOff++] = (byte) (f * (g & 0xff));
output[outOff++] = (byte) (f * (b & 0xff));
output[outOff++] = (byte) a;
}
inOffset += inRowStride;
outOffset += outRowStride;
} else if (inputType == ImageType.PALETTE_ALPHA_PRE) {
int inOffset = inputOffset;
int outOffset = outputOffset;
byte[] red = palette[0];
byte[] green = palette[1];
byte[] blue = palette[2];
byte[] alpha = palette[3];
for (int y = 0; y < height; y++) {
int inOff = inOffset;
int outOff = outOffset;
for (int x = 0; x < width; x++) {
int index = input[inOff++] & 0xff;
output[outOff++] = red[index];
output[outOff++] = green[index];
output[outOff++] = blue[index];
output[outOff++] = alpha[index];
}
inOffset += inRowStride;
outOffset += outRowStride;
}
} else if (inputType == ImageType.PALETTE_TRANS) {
int inOffset = inputOffset;
int outOffset = outputOffset;
for (int y = 0; y < height; y++) {
int inOff = inOffset;
int outOff = outOffset;
byte[] red = palette[0];
byte[] green = palette[1];
byte[] blue = palette[2];
for (int x = 0; x < width; x++) {
int index = input[inOff++] & 0xff;
if (index == transparentIndex) {
if (skipTransparent) {
outOff+=4;
} else {
output[outOff++] = (byte) 0;
output[outOff++] = (byte) 0;
output[outOff++] = (byte) 0;
output[outOff++] = (byte) 0;
}
} else {
output[outOff++] = red[index];
output[outOff++] = green[index];
output[outOff++] = blue[index];
output[outOff++] = (byte) 255;
}
}
inOffset += inRowStride;
outOffset += outRowStride;
}
} else if (inputType == ImageType.RGBA) {
int inOffset = inputOffset;
int outOffset = outputOffset;
for (int y = 0; y < height; y++) {
int inOff = inOffset;
int outOff = outOffset;
for (int x = 0; x < width; x++) {
byte red = input[inOff++];
byte green = input[inOff++];
byte blue = input[inOff++];
int alpha = input[inOff++] & 0xff;
float f = alpha / 255.0F;
output[outOff++] = (byte) (f * (red & 0xff));
output[outOff++] = (byte) (f * (green & 0xff));
output[outOff++] = (byte) (f * (blue & 0xff));
output[outOff++] = (byte) alpha;
}
inOffset += inRowStride;
outOffset += outRowStride;
}
} else {
throw new UnsupportedOperationException("Unsupported ImageType " +
inputType);
}
return output;
}
public static String getScaledImageName(String path) {
StringBuilder result = new StringBuilder();
int slash = path.lastIndexOf('/');
String name = (slash < 0) ? path : path.substring(slash + 1);
int dot = name.lastIndexOf(".");
if (dot < 0) {
dot = name.length();
}
if (slash >= 0) {
result.append(path.substring(0, slash + 1));
}
result.append(name.substring(0, dot));
result.append("@2x");
result.append(name.substring(dot));
return result.toString();
}
public static InputStream createInputStream(String input) throws IOException {
InputStream stream = null;
try {
File file = new File(input);
if (file.exists()) {
stream = new FileInputStream(file);
}
} catch (Exception e) {
}
if (stream == null) {
URL url = new URL(input);
stream = url.openStream();
}
return stream;
}
private static void computeUpdatedPixels(int sourceOffset,
int sourceExtent,
int destinationOffset,
int dstMin,
int dstMax,
int sourceSubsampling,
int passStart,
int passExtent,
int passPeriod,
int[] vals,
int offset) {
boolean gotPixel = false;
int firstDst = -1;
int secondDst = -1;
int lastDst = -1;
for (int i = 0; i < passExtent; i++) {
int src = passStart + i * passPeriod;
if (src < sourceOffset) {
continue;
}
if ((src - sourceOffset) % sourceSubsampling != 0) {
continue;
}
if (src >= sourceOffset + sourceExtent) {
break;
}
int dst = destinationOffset +
(src - sourceOffset) / sourceSubsampling;
if (dst < dstMin) {
continue;
}
if (dst > dstMax) {
break;
}
if (!gotPixel) {
firstDst = dst;
gotPixel = true;
} else if (secondDst == -1) {
secondDst = dst;
}
lastDst = dst;
}
vals[offset] = firstDst;
if (!gotPixel) {
vals[offset + 2] = 0;
} else {
vals[offset + 2] = lastDst - firstDst + 1;
}
vals[offset + 4] = Math.max(secondDst - firstDst, 1);
}
public static int[] computeUpdatedPixels(Rectangle sourceRegion,
Point2D destinationOffset,
int dstMinX,
int dstMinY,
int dstMaxX,
int dstMaxY,
int sourceXSubsampling,
int sourceYSubsampling,
int passXStart,
int passYStart,
int passWidth,
int passHeight,
int passPeriodX,
int passPeriodY) {
int[] vals = new int[6];
computeUpdatedPixels(sourceRegion.x, sourceRegion.width,
(int) (destinationOffset.x + 0.5F),
dstMinX, dstMaxX, sourceXSubsampling,
passXStart, passWidth, passPeriodX,
vals, 0);
computeUpdatedPixels(sourceRegion.y, sourceRegion.height,
(int) (destinationOffset.y + 0.5F),
dstMinY, dstMaxY, sourceYSubsampling,
passYStart, passHeight, passPeriodY,
vals, 1);
return vals;
}
public static int[] computeDimensions(int sourceWidth, int sourceHeight,
int maxWidth, int maxHeight, boolean preserveAspectRatio) {
int finalWidth = maxWidth < 0 ? 0 : maxWidth;
int finalHeight = maxHeight < 0 ? 0 : maxHeight;
if(finalWidth == 0 && finalHeight == 0) {
finalWidth = sourceWidth;
finalHeight = sourceHeight;
} else if (finalWidth != sourceWidth || finalHeight != sourceHeight) {
if (preserveAspectRatio) {
if (finalWidth == 0) {
finalWidth = Math.round((float) sourceWidth * finalHeight / sourceHeight);
} else if (finalHeight == 0) {
finalHeight = Math.round((float) sourceHeight * finalWidth / sourceWidth);
} else {
float scale = Math.min((float) finalWidth / sourceWidth, (float) finalHeight / sourceHeight);
finalWidth = Math.round(sourceWidth * scale);
finalHeight = Math.round(sourceHeight * scale);
}
} else {
if (finalHeight == 0) {
finalHeight = sourceHeight;
}
if (finalWidth == 0) {
finalWidth = sourceWidth;
}
}
if (finalWidth == 0) {
finalWidth = 1;
}
if (finalHeight == 0) {
finalHeight = 1;
}
}
return new int[]{finalWidth, finalHeight};
}
public static ImageFrame scaleImageFrame(ImageFrame src,
int destWidth, int destHeight, boolean isSmooth)
{
int numBands = ImageStorage.getInstance().getNumBands(src.getImageType());
ByteBuffer dst = scaleImage((ByteBuffer) src.getImageData(),
src.getWidth(), src.getHeight(), numBands,
destWidth, destHeight, isSmooth);
return new ImageFrame(src.getImageType(), dst,
destWidth, destHeight, destWidth * numBands, null, src.getMetadata());
}
public static ByteBuffer scaleImage(ByteBuffer src,
int sourceWidth, int sourceHeight, int numBands,
int destWidth, int destHeight, boolean isSmooth)
{
PushbroomScaler scaler = ScalerFactory.createScaler(
sourceWidth, sourceHeight, numBands,
destWidth, destHeight, isSmooth);
int stride = sourceWidth * numBands;
if (src.hasArray()) {
byte image[] = src.array();
for (int y = 0; y != sourceHeight; ++y) {
scaler.putSourceScanline(image, y * stride);
}
} else {
byte scanline[] = new byte[stride];
for (int y = 0; y != sourceHeight; ++y) {
src.get(scanline);
scaler.putSourceScanline(scanline, 0);
}
}
return scaler.getDestination();
}
}
