package com.sun.javafx.font;
import com.sun.javafx.font.FontFileReader.Buffer;
abstract class CMap {
static final char noSuchChar = (char)0xfffd;
static final int SHORTMASK = 0x0000ffff;
static final int INTMASK = 0xffffffff;
private static final int MAX_CODE_POINTS = 0x10ffff;
static CMap initialize(PrismFontFile font) {
CMap cmap = null;
int offset, platformID, encodingID=-1;
int three0=0, three1=0, three10=0, zeroStarOffset=0;
boolean zeroStar = false, threeStar = false;
Buffer cmapBuffer = font.readTable(FontConstants.cmapTag);
short numberSubTables = cmapBuffer.getShort(2);
for (int i=0; i<numberSubTables; i++) {
cmapBuffer.position(i * 8 + 4);
platformID = cmapBuffer.getShort();
if (platformID == 0) {
zeroStar = true;
encodingID = cmapBuffer.getShort();
zeroStarOffset = cmapBuffer.getInt();
}
else if (platformID == 3) {
threeStar = true;
encodingID = cmapBuffer.getShort();
offset = cmapBuffer.getInt();
switch (encodingID) {
case 0: three0 = offset; break;
case 1: three1 = offset; break;
case 10: three10 = offset; break;
}
}
}
if (threeStar) {
if (three10 != 0) {
cmap = createCMap(cmapBuffer, three10);
}
else if (three0 != 0) {
cmap = createCMap(cmapBuffer, three0);
}
else if (three1 != 0) {
cmap = createCMap(cmapBuffer, three1);
}
} else if (zeroStar && zeroStarOffset != 0) {
cmap = createCMap(cmapBuffer, zeroStarOffset);
} else {
cmap = createCMap(cmapBuffer, cmapBuffer.getInt(8));
}
return cmap;
}
static CMap createCMap(Buffer buffer, int offset) {
int subtableFormat = buffer.getChar(offset);
switch (subtableFormat) {
case 0: return new CMapFormat0(buffer, offset);
case 2: return new CMapFormat2(buffer, offset);
case 4: return new CMapFormat4(buffer, offset);
case 6: return new CMapFormat6(buffer, offset);
case 8: return new CMapFormat8(buffer, offset);
case 10: return new CMapFormat10(buffer, offset);
case 12: return new CMapFormat12(buffer, offset);
default: throw new RuntimeException("Cmap format unimplemented: " +
(int)buffer.getChar(offset));
}
}
abstract char getGlyph(int charCode);
static class CMapFormat4 extends CMap {
int segCount;
int entrySelector;
int rangeShift;
char[] endCount;
char[] startCount;
short[] idDelta;
char[] idRangeOffset;
char[] glyphIds;
CMapFormat4(Buffer buffer, int offset) {
buffer.position(offset);
buffer.getChar();
int subtableLength = buffer.getChar();
if (offset+subtableLength > buffer.capacity()) {
subtableLength = buffer.capacity() - offset;
}
buffer.getChar();
segCount = buffer.getChar()/2;
buffer.getChar();
entrySelector = buffer.getChar();
rangeShift = buffer.getChar()/2;
startCount = new char[segCount];
endCount = new char[segCount];
idDelta = new short[segCount];
idRangeOffset = new char[segCount];
for (int i=0; i<segCount; i++) {
endCount[i] = buffer.getChar();
}
buffer.getChar();
for (int i=0; i<segCount; i++) {
startCount[i] = buffer.getChar();
}
for (int i=0; i<segCount; i++) {
idDelta[i] = (short)buffer.getChar();
}
for (int i=0; i<segCount; i++) {
char ctmp = buffer.getChar();
idRangeOffset[i] = (char)((ctmp>>1)&0xffff);
}
int pos = (segCount*8+16)/2;
buffer.position(pos*2+offset);
int numGlyphIds = (subtableLength/2 - pos);
glyphIds = new char[numGlyphIds];
for (int i=0;i<numGlyphIds;i++) {
glyphIds[i] = buffer.getChar();
}
}
char getGlyph(int charCode) {
int index = 0;
char glyphCode = 0;
int controlGlyph = getControlCodeGlyph(charCode, true);
if (controlGlyph >= 0) {
return (char)controlGlyph;
}
int left = 0, right = startCount.length;
index = startCount.length >> 1;
while (left < right) {
if (endCount[index] < charCode) {
left = index + 1;
} else {
right = index;
}
index = (left + right) >> 1;
}
if (charCode >= startCount[index] && charCode <= endCount[index]) {
int rangeOffset = idRangeOffset[index];
if (rangeOffset == 0) {
glyphCode = (char)(charCode + idDelta[index]);
} else {
int glyphIDIndex = rangeOffset - segCount + index
+ (charCode - startCount[index]);
glyphCode = glyphIds[glyphIDIndex];
if (glyphCode != 0) {
glyphCode = (char)(glyphCode + idDelta[index]);
}
}
}
return glyphCode;
}
}
static class CMapFormat0 extends CMap {
byte [] cmap;
CMapFormat0(Buffer buffer, int offset) {
int len = buffer.getChar(offset+2);
cmap = new byte[len-6];
buffer.get(offset+6, cmap, 0, len-6);
}
char getGlyph(int charCode) {
if (charCode < 256) {
if (charCode < 0x0010) {
switch (charCode) {
case 0x0009:
case 0x000a:
case 0x000d: return CharToGlyphMapper.INVISIBLE_GLYPH_ID;
}
}
return (char)(0xff & cmap[charCode]);
} else {
return 0;
}
}
}
static class CMapFormat2 extends CMap {
char[] subHeaderKey = new char[256];
char[] firstCodeArray;
char[] entryCountArray;
short[] idDeltaArray;
char[] idRangeOffSetArray;
char[] glyphIndexArray;
CMapFormat2(Buffer buffer, int offset) {
int tableLen = buffer.getChar(offset+2);
buffer.position(offset+6);
char maxSubHeader = 0;
for (int i=0;i<256;i++) {
subHeaderKey[i] = buffer.getChar();
if (subHeaderKey[i] > maxSubHeader) {
maxSubHeader = subHeaderKey[i];
}
}
int numSubHeaders = (maxSubHeader >> 3) +1;
firstCodeArray = new char[numSubHeaders];
entryCountArray = new char[numSubHeaders];
idDeltaArray = new short[numSubHeaders];
idRangeOffSetArray = new char[numSubHeaders];
for (int i=0; i<numSubHeaders; i++) {
firstCodeArray[i] = buffer.getChar();
entryCountArray[i] = buffer.getChar();
idDeltaArray[i] = (short)buffer.getChar();
idRangeOffSetArray[i] = buffer.getChar();
}
int glyphIndexArrSize = (tableLen-518-numSubHeaders*8)/2;
glyphIndexArray = new char[glyphIndexArrSize];
for (int i=0; i<glyphIndexArrSize;i++) {
glyphIndexArray[i] = buffer.getChar();
}
}
char getGlyph(int charCode) {
int controlGlyph = getControlCodeGlyph(charCode, true);
if (controlGlyph >= 0) {
return (char)controlGlyph;
}
char highByte = (char)(charCode >> 8);
char lowByte = (char)(charCode & 0xff);
int key = subHeaderKey[highByte]>>3;
char mapMe;
if (key != 0) {
mapMe = lowByte;
} else {
mapMe = highByte;
if (mapMe == 0) {
mapMe = lowByte;
}
}
char firstCode = firstCodeArray[key];
if (mapMe < firstCode) {
return 0;
} else {
mapMe -= firstCode;
}
if (mapMe < entryCountArray[key]) {
int glyphArrayOffset = ((idRangeOffSetArray.length-key)*8)-6;
int glyphSubArrayStart =
(idRangeOffSetArray[key] - glyphArrayOffset)/2;
char glyphCode = glyphIndexArray[glyphSubArrayStart+mapMe];
if (glyphCode != 0) {
glyphCode += idDeltaArray[key];
return glyphCode;
}
}
return 0;
}
}
static class CMapFormat6 extends CMap {
char firstCode;
char entryCount;
char[] glyphIdArray;
CMapFormat6(Buffer buffer, int offset) {
buffer.position(offset+6);
firstCode = buffer.getChar();
entryCount = buffer.getChar();
glyphIdArray = new char[entryCount];
for (int i=0; i< entryCount; i++) {
glyphIdArray[i] = buffer.getChar();
}
}
char getGlyph(int charCode) {
int controlGlyph = getControlCodeGlyph(charCode, true);
if (controlGlyph >= 0) {
return (char)controlGlyph;
}
charCode -= firstCode;
if (charCode < 0 || charCode >= entryCount) {
return 0;
} else {
return glyphIdArray[charCode];
}
}
}
static class CMapFormat8 extends CMap {
CMapFormat8(Buffer buffer, int offset) {
}
char getGlyph(int charCode) {
return 0;
}
}
static class CMapFormat10 extends CMap {
long startCharCode;
int numChars;
char[] glyphIdArray;
CMapFormat10(Buffer buffer, int offset) {
buffer.position(offset+12);
startCharCode = buffer.getInt() & INTMASK;
numChars = buffer.getInt() & INTMASK;
if (numChars <= 0 || numChars > MAX_CODE_POINTS ||
offset > buffer.capacity() - numChars*2 - 12 - 8)
{
throw new RuntimeException("Invalid cmap subtable");
}
glyphIdArray = new char[numChars];
for (int i=0; i< numChars; i++) {
glyphIdArray[i] = buffer.getChar();
}
}
char getGlyph(int charCode) {
int code = (int)(charCode - startCharCode);
if (code < 0 || code >= numChars) {
return 0;
} else {
return glyphIdArray[code];
}
}
}
static class CMapFormat12 extends CMap {
int numGroups;
int highBit =0;
int power;
int extra;
long[] startCharCode;
long[] endCharCode;
int[] startGlyphID;
CMapFormat12(Buffer buffer, int offset) {
numGroups = buffer.getInt(offset+12);
if (numGroups <= 0 || numGroups > MAX_CODE_POINTS ||
offset > buffer.capacity() - numGroups*12 - 12 - 4)
{
throw new RuntimeException("Invalid cmap subtable");
}
startCharCode = new long[numGroups];
endCharCode = new long[numGroups];
startGlyphID = new int[numGroups];
buffer.position(offset+16);
for (int i=0; i<numGroups; i++) {
startCharCode[i] = buffer.getInt() & INTMASK;
endCharCode[i] = buffer.getInt() & INTMASK;
startGlyphID[i] = buffer.getInt() & INTMASK;
}
int value = numGroups;
if (value >= 1 << 16) {
value >>= 16;
highBit += 16;
}
if (value >= 1 << 8) {
value >>= 8;
highBit += 8;
}
if (value >= 1 << 4) {
value >>= 4;
highBit += 4;
}
if (value >= 1 << 2) {
value >>= 2;
highBit += 2;
}
if (value >= 1 << 1) {
value >>= 1;
highBit += 1;
}
power = 1 << highBit;
extra = numGroups - power;
}
char getGlyph(int charCode) {
int controlGlyph = getControlCodeGlyph(charCode, false);
if (controlGlyph >= 0) {
return (char)controlGlyph;
}
int probe = power;
int range = 0;
if (startCharCode[extra] <= charCode) {
range = extra;
}
while (probe > 1) {
probe >>= 1;
if (startCharCode[range+probe] <= charCode) {
range += probe;
}
}
if (startCharCode[range] <= charCode &&
endCharCode[range] >= charCode) {
return (char)
(startGlyphID[range] + (charCode - startCharCode[range]));
}
return 0;
}
}
static class NullCMapClass extends CMap {
char getGlyph(int charCode) {
return 0;
}
}
public static final NullCMapClass theNullCmap = new NullCMapClass();
final int getControlCodeGlyph(int charCode, boolean noSurrogates) {
if (charCode < 0x0010) {
switch (charCode) {
case 0x0009:
case 0x000a:
case 0x000d: return CharToGlyphMapper.INVISIBLE_GLYPH_ID;
}
} else if (charCode >= 0x200c) {
if ((charCode <= 0x200f) ||
(charCode >= 0x2028 && charCode <= 0x202e) ||
(charCode >= 0x206a && charCode <= 0x206f)) {
return CharToGlyphMapper.INVISIBLE_GLYPH_ID;
} else if (noSurrogates && charCode >= 0xFFFF) {
return 0;
}
}
return -1;
}
}
