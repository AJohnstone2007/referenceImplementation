package com.sun.javafx.iio.gif;
import com.sun.javafx.iio.ImageFrame;
import com.sun.javafx.iio.ImageMetadata;
import com.sun.javafx.iio.ImageStorage;
import com.sun.javafx.iio.common.ImageLoaderImpl;
import com.sun.javafx.iio.common.ImageTools;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
public class GIFImageLoader2 extends ImageLoaderImpl {
static final byte FILE_SIG87[] = {'G', 'I', 'F', '8', '7', 'a'};
static final byte FILE_SIG89[] = {'G', 'I', 'F', '8', '9', 'a'};
static final byte NETSCAPE_SIG[] = {'N', 'E', 'T', 'S', 'C', 'A', 'P', 'E', '2', '.', '0'};
static final int DEFAULT_FPS = 25;
InputStream stream = null;
int screenW, screenH, bgColor;
byte globalPalette[][];
byte image[];
int loopCount = 1;
public GIFImageLoader2(InputStream input) throws IOException {
super(GIFDescriptor.getInstance());
this.stream = input;
readGlobalHeader();
}
private void readGlobalHeader() throws IOException {
byte signature[] = readBytes(new byte[6]);
if (!Arrays.equals(FILE_SIG87, signature) && !Arrays.equals(FILE_SIG89, signature)) {
throw new IOException("Bad GIF signature!");
}
screenW = readShort();
screenH = readShort();
int cInfo = readByte();
bgColor = readByte();
int aspectR = readByte();
if ((cInfo & 0x80) != 0) {
globalPalette = readPalete(2 << (cInfo & 7), -1);
}
image = new byte[screenW * screenH * 4];
}
private byte[][] readPalete(int size, int trnsIndex) throws IOException {
byte palette[][] = new byte[4][size];
byte paletteData[] = readBytes(new byte[size*3]);
for (int i = 0, idx = 0; i != size; ++i) {
for (int k = 0; k != 3; ++k) {
palette[k][i] = paletteData[idx++];
}
palette[3][i] = (i == trnsIndex) ? 0 : (byte)0xFF;
}
return palette;
}
private void consumeAnExtension() throws IOException {
for (int blSize = readByte(); blSize != 0; blSize = readByte()) {
skipBytes(blSize);
}
}
private void readAppExtension() throws IOException {
int size = readByte();
byte buf[] = readBytes(new byte[size]);
if (Arrays.equals(NETSCAPE_SIG, buf)) {
for (int subBlockSize = readByte(); subBlockSize != 0; subBlockSize = readByte()) {
byte subBlock[] = readBytes(new byte[subBlockSize]);
int subBlockId = subBlock[0];
if (subBlockSize == 3 && subBlockId == 1) {
loopCount = (subBlock[1] & 0xff) | ((subBlock[2] & 0xff) << 8);
}
}
} else {
consumeAnExtension();
}
}
private int readControlCode() throws IOException {
int size = readByte();
int pField = readByte();
int frameDelay = readShort();
int trnsIndex = readByte();
if (size != 4 || readByte() != 0) {
throw new IOException("Bad GIF GraphicControlExtension");
}
return ((pField & 0x1F) << 24) + (trnsIndex << 16) + frameDelay;
}
private int waitForImageFrame() throws IOException {
int controlData = 0;
while (true) {
int ch = stream.read();
switch (ch) {
case 0x2C:
return controlData;
case 0x21:
switch (readByte()) {
case 0xF9:
controlData = readControlCode();
break;
case 0xFF:
readAppExtension();
break;
default:
consumeAnExtension();
}
break;
case -1: case 0x3B:
return -1;
default:
throw new IOException("Unexpected GIF control characher 0x"
+ String.format("%02X", ch));
}
}
}
private void decodeImage(byte image[], int w, int h, int interlace[]) throws IOException {
LZWDecoder dec = new LZWDecoder();
byte data[] = dec.getString();
int y = 0, iPos = 0, xr = w;
while (true) {
int len = dec.readString();
if (len == -1) {
dec.waitForTerminator();
return;
}
for (int pos = 0; pos != len;) {
int ax = xr < (len - pos) ? xr : (len - pos);
System.arraycopy(data, pos, image, iPos, ax);
iPos += ax;
pos += ax;
if ((xr -= ax) == 0) {
if (++y == h) {
dec.waitForTerminator();
return;
}
int iY = interlace == null ? y : interlace[y];
iPos = iY * w;
xr = w;
}
}
}
}
private int[] computeInterlaceReIndex(int h) {
int data[] = new int[h], pos = 0;
for (int i = 0; i < h; i += 8) data[pos++] = i;
for (int i = 4; i < h; i += 8) data[pos++] = i;
for (int i = 2; i < h; i += 4) data[pos++] = i;
for (int i = 1; i < h; i += 2) data[pos++] = i;
return data;
}
public ImageFrame load(int imageIndex, int width, int height, boolean preserveAspectRatio, boolean smooth) throws IOException {
int imageControlCode = waitForImageFrame();
if (imageControlCode < 0) {
return null;
}
int left = readShort(), top = readShort(), w = readShort(), h = readShort();
if (left + w > screenW || top + h > screenH) {
throw new IOException("Wrong GIF image frame size");
}
int imgCtrl = readByte();
boolean isTRNS = ((imageControlCode >>> 24) & 1) == 1;
int trnsIndex = isTRNS ? (imageControlCode >>> 16) & 0xFF : -1;
boolean localPalette = (imgCtrl & 0x80) != 0;
boolean isInterlaced = (imgCtrl & 0x40) != 0;
byte palette[][] = localPalette ? readPalete(2 << (imgCtrl & 7), trnsIndex) : globalPalette;
int[] outWH = ImageTools.computeDimensions(screenW, screenH, width, height, preserveAspectRatio);
width = outWH[0];
height = outWH[1];
ImageMetadata metadata = updateMetadata(width, height, imageControlCode & 0xFFFF);
int disposalCode = (imageControlCode >>> 26) & 7;
byte pImage[] = new byte[w * h];
decodeImage(pImage, w, h, isInterlaced ? computeInterlaceReIndex(h) : null);
ByteBuffer img = decodePalette(pImage, palette, trnsIndex,
left, top, w, h, disposalCode);
if (screenW != width || screenH != height) {
img = ImageTools.scaleImage(img, screenW, screenH, 4,
width, height, smooth);
}
return new ImageFrame(ImageStorage.ImageType.RGBA, img,
width, height, width * 4, null, metadata);
}
private int readByte() throws IOException {
int ch = stream.read();
if (ch < 0) {
throw new EOFException();
}
return ch;
}
private int readShort() throws IOException {
int lsb = readByte(), msb = readByte();
return lsb + (msb << 8);
}
private byte[] readBytes(byte data[]) throws IOException {
return readBytes(data, 0, data.length);
}
private byte[] readBytes(byte data[], int offs, int size) throws IOException {
while (size > 0) {
int sz = stream.read(data, offs, size);
if (sz < 0) {
throw new EOFException();
}
offs += sz;
size -= sz;
}
return data;
}
private void skipBytes(int n) throws IOException {
ImageTools.skipFully(stream, n);
}
public void dispose() {}
private void restoreToBackground(byte img[], int left, int top, int w, int h) {
for (int y = 0; y != h; ++y) {
int iPos = ((top + y) * screenW + left) * 4;
for (int x = 0; x != w; iPos += 4, ++x) {
img[iPos + 3] = 0;
}
}
}
private ByteBuffer decodePalette(byte[] srcImage, byte[][] palette, int trnsIndex,
int left, int top, int w, int h, int disposalCode) {
byte img[] = (disposalCode == 3) ? image.clone() : image;
for (int y = 0; y != h; ++y) {
int iPos = ((top + y) * screenW + left) * 4;
int i = y * w;
if (trnsIndex < 0) {
for (int x = 0; x != w; iPos += 4, ++x) {
int index = 0xFF & srcImage[i + x];
img[iPos + 0] = palette[0][index];
img[iPos + 1] = palette[1][index];
img[iPos + 2] = palette[2][index];
img[iPos + 3] = palette[3][index];
}
} else {
for (int x = 0; x != w; iPos += 4, ++x) {
int index = 0xFF & srcImage[i + x];
if (index != trnsIndex) {
img[iPos + 0] = palette[0][index];
img[iPos + 1] = palette[1][index];
img[iPos + 2] = palette[2][index];
img[iPos + 3] = palette[3][index];
}
}
}
}
if (disposalCode != 3) img = img.clone();
if (disposalCode == 2) restoreToBackground(image, left, top, w, h);
return ByteBuffer.wrap(img);
}
private ImageMetadata updateMetadata(int w, int h, int delayTime) {
ImageMetadata metaData = new ImageMetadata(null, true, null, null, null,
delayTime != 0 ? delayTime*10 : 1000/DEFAULT_FPS, loopCount, w, h, null, null, null);
updateImageMetadata(metaData);
return metaData;
}
class LZWDecoder {
private final int initCodeSize, clearCode, eofCode;
private int codeSize, codeMask, tableIndex, oldCode;
private int blockLength = 0, blockPos = 0;
private byte block[] = new byte[255];
private int inData = 0, inBits = 0;
private int[] prefix = new int[4096];
private byte[] suffix = new byte[4096];
private byte[] initial = new byte[4096];
private int[] length = new int[4096];
private byte[] string = new byte[4096];
public LZWDecoder() throws IOException {
initCodeSize = readByte();
clearCode = 1 << initCodeSize;
eofCode = clearCode + 1;
initTable();
}
public final int readString() throws IOException {
int code = getCode();
if (code == eofCode) {
return -1;
} else if (code == clearCode) {
initTable();
code = getCode();
if (code == eofCode) {
return -1;
}
} else {
int newSuffixIndex;
if (code < tableIndex) {
newSuffixIndex = code;
} else {
newSuffixIndex = oldCode;
if (code != tableIndex) {
throw new IOException("Bad GIF LZW: Out-of-sequence code!");
}
}
if (tableIndex < 4096) {
int ti = tableIndex;
int oc = oldCode;
prefix[ti] = oc;
suffix[ti] = initial[newSuffixIndex];
initial[ti] = initial[oc];
length[ti] = length[oc] + 1;
++tableIndex;
if ((tableIndex == (1 << codeSize)) && (tableIndex < 4096)) {
++codeSize;
codeMask = (1 << codeSize) - 1;
}
}
}
int c = code;
int len = length[c];
for (int i = len - 1; i >= 0; i--) {
string[i] = suffix[c];
c = prefix[c];
}
oldCode = code;
return len;
}
public final byte[] getString() { return string; }
public final void waitForTerminator() throws IOException {
consumeAnExtension();
}
private void initTable() {
int numEntries = 1 << initCodeSize;
for (int i = 0; i < numEntries; i++) {
prefix[i] = -1;
suffix[i] = (byte) i;
initial[i] = (byte) i;
length[i] = 1;
}
for (int i = numEntries; i < 4096; i++) {
prefix[i] = -1;
length[i] = 1;
}
codeSize = initCodeSize + 1;
codeMask = (1 << codeSize) - 1;
tableIndex = numEntries + 2;
oldCode = 0;
}
private int getCode() throws IOException {
while (inBits < codeSize) {
inData |= nextByte() << inBits;
inBits += 8;
}
int code = inData & codeMask;
inBits -= codeSize;
inData >>>= codeSize;
return code;
}
private int nextByte() throws IOException {
if (blockPos == blockLength) {
readData();
}
return (int)block[blockPos++] & 0xFF;
}
private void readData() throws IOException {
blockPos = 0;
blockLength = readByte();
if (blockLength > 0) {
readBytes(block, 0, blockLength);
} else {
throw new EOFException();
}
}
}
}
