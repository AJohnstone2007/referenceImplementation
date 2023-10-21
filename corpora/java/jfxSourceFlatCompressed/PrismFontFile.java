package com.sun.javafx.font;
import java.lang.ref.WeakReference;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.font.FontFileReader.Buffer;
import static com.sun.javafx.font.PrismMetrics.*;
public abstract class PrismFontFile implements FontResource, FontConstants {
String familyName;
String fullName;
String psName;
String localeFamilyName;
String localeFullName;
String styleName;
String localeStyleName;
String filename;
int filesize;
FontFileReader filereader;
int numGlyphs = -1;
short indexToLocFormat;
int fontIndex;
boolean isCFF;
boolean isEmbedded = false;
boolean isCopy = false;
boolean isTracked = false;
boolean isDecoded = false;
boolean isRegistered = true;
Map<FontStrikeDesc, WeakReference<PrismFontStrike>> strikeMap =
new ConcurrentHashMap<FontStrikeDesc, WeakReference<PrismFontStrike>>();
protected PrismFontFile(String name, String filename, int fIndex,
boolean register, boolean embedded,
boolean copy, boolean tracked) throws Exception {
this.filename = filename;
this.isRegistered = register;
this.isEmbedded = embedded;
this.isCopy = copy;
this.isTracked = tracked;
init(name, fIndex);
}
WeakReference<PrismFontFile> createFileDisposer(PrismFontFactory factory,
FileRefCounter rc) {
FileDisposer disposer = new FileDisposer(filename, isTracked, rc);
WeakReference<PrismFontFile> ref = Disposer.addRecord(this, disposer);
disposer.setFactory(factory, ref);
return ref;
}
void setIsDecoded(boolean decoded) {
isDecoded = decoded;
}
@SuppressWarnings("removal")
protected synchronized void disposeOnShutdown() {
if (isCopy || isDecoded) {
AccessController.doPrivileged(
(PrivilegedAction<Void>) () -> {
try {
if (decFileRefCount() > 0) {
return null;
}
boolean delOK = (new File(filename)).delete();
if (!delOK && PrismFontFactory.debugFonts) {
System.err.println("Temp file not deleted : "
+ filename);
}
isCopy = isDecoded = false;
} catch (Exception e) {
}
return null;
}
);
if (PrismFontFactory.debugFonts) {
System.err.println("Temp file deleted: " + filename);
}
}
}
public int getDefaultAAMode() {
return AA_GREYSCALE;
}
static class FileRefCounter {
private int refCnt = 1;
synchronized int getRefCount() {
return refCnt;
}
synchronized int increment() {
return ++refCnt;
}
synchronized int decrement() {
return (refCnt == 0) ? 0 : --refCnt;
}
}
private FileRefCounter refCounter = null;
FileRefCounter getFileRefCounter() {
return refCounter;
}
FileRefCounter createFileRefCounter() {
refCounter = new FileRefCounter();
return refCounter;
}
void setAndIncFileRefCounter(FileRefCounter rc) {
this.refCounter = rc;
this.refCounter.increment();
}
int decFileRefCount() {
if (refCounter == null) {
return 0;
} else {
return refCounter.decrement();
}
}
static class FileDisposer implements DisposerRecord {
String fileName;
boolean isTracked;
FileRefCounter refCounter;
PrismFontFactory factory;
WeakReference<PrismFontFile> refKey;
public FileDisposer(String fileName, boolean isTracked,
FileRefCounter rc) {
this.fileName = fileName;
this.isTracked = isTracked;
this.refCounter = rc;
}
public void setFactory(PrismFontFactory factory,
WeakReference<PrismFontFile> refKey) {
this.factory = factory;
this.refKey = refKey;
}
@SuppressWarnings("removal")
public synchronized void dispose() {
if (fileName != null) {
AccessController.doPrivileged(
(PrivilegedAction<Void>) () -> {
try {
if (refCounter != null &&
refCounter.decrement() > 0)
{
return null;
}
File file = new File(fileName);
int size = (int)file.length();
file.delete();
if (isTracked) {
FontFileWriter.FontTracker.
getTracker().subBytes(size);
}
if (factory != null && refKey != null) {
Object o = refKey.get();
if (o == null) {
factory.removeTmpFont(refKey);
factory = null;
refKey = null;
}
}
if (PrismFontFactory.debugFonts) {
System.err.println("FileDisposer=" + fileName);
}
} catch (Exception e) {
if (PrismFontFactory.debugFonts) {
e.printStackTrace();
}
}
return null;
}
);
fileName = null;
}
}
}
public String getFileName() {
return filename;
}
protected int getFileSize() {
return filesize;
}
protected int getFontIndex() {
return fontIndex;
}
public String getFullName() {
return fullName;
}
public String getPSName() {
if (psName == null) {
psName = fullName;
}
return psName;
}
public String getFamilyName() {
return familyName;
}
public String getStyleName() {
return styleName;
}
public String getLocaleFullName() {
return localeFullName;
}
public String getLocaleFamilyName() {
return localeFamilyName;
}
public String getLocaleStyleName() {
return localeStyleName;
}
public int getFeatures() {
return -1;
}
public Map getStrikeMap() {
return strikeMap;
}
protected abstract PrismFontStrike createStrike(float size,
BaseTransform transform,
int aaMode,
FontStrikeDesc desc);
public FontStrike getStrike(float size, BaseTransform transform,
int aaMode) {
FontStrikeDesc desc = new FontStrikeDesc(size, transform, aaMode);
WeakReference<PrismFontStrike> ref = strikeMap.get(desc);
PrismFontStrike strike = null;
if (ref != null) {
strike = ref.get();
}
if (strike == null) {
strike = createStrike(size, transform, aaMode, desc);
DisposerRecord disposer = strike.getDisposer();
if (disposer != null) {
ref = Disposer.addRecord(strike, disposer);
} else {
ref = new WeakReference<PrismFontStrike>(strike);
}
strikeMap.put(desc, ref);
}
return strike;
}
HashMap<Integer, int[]> bbCache = null;
static final int[] EMPTY_BOUNDS = new int[4];
protected abstract int[] createGlyphBoundingBox(int gc);
@Override
public float[] getGlyphBoundingBox(int gc, float size, float[] retArr) {
if (retArr == null || retArr.length < 4) {
retArr = new float[4];
}
if (gc >= getNumGlyphs()) {
retArr[0] = retArr[1] = retArr[2] = retArr[3] = 0;
return retArr;
}
if (bbCache == null) {
bbCache = new HashMap<Integer, int[]>();
}
int[] bb = bbCache.get(gc);
if (bb == null) {
bb = createGlyphBoundingBox(gc);
if (bb == null) bb = EMPTY_BOUNDS;
bbCache.put(gc, bb);
}
float scale = size / getUnitsPerEm();
retArr[0] = bb[0] * scale;
retArr[1] = bb[1] * scale;
retArr[2] = bb[2] * scale;
retArr[3] = bb[3] * scale;
return retArr;
}
int getNumGlyphs() {
if (numGlyphs == -1) {
Buffer buffer = readTable(maxpTag);
numGlyphs = buffer.getChar(4);
}
return numGlyphs;
}
protected boolean isCFF() {
return isCFF;
}
private Object peer;
public Object getPeer() {
return peer;
}
public void setPeer(Object peer) {
this.peer = peer;
}
int getTableLength(int tag) {
int len = 0;
DirectoryEntry tagDE = getDirectoryEntry(tag);
if (tagDE != null) {
len = tagDE.length;
}
return len;
}
synchronized Buffer readTable(int tag) {
Buffer buffer = null;
boolean openedFile = false;
try {
openedFile = filereader.openFile();
DirectoryEntry tagDE = getDirectoryEntry(tag);
if (tagDE != null) {
buffer = filereader.readBlock(tagDE.offset, tagDE.length);
}
} catch (Exception e) {
if (PrismFontFactory.debugFonts) {
e.printStackTrace();
}
} finally {
if (openedFile) {
try {
filereader.closeFile();
} catch (Exception e2) {
}
}
}
return buffer;
}
int directoryCount = 1;
public int getFontCount() {
return directoryCount;
}
int numTables;
DirectoryEntry[] tableDirectory;
static class DirectoryEntry {
int tag;
int offset;
int length;
}
DirectoryEntry getDirectoryEntry(int tag) {
for (int i=0;i<numTables;i++) {
if (tableDirectory[i].tag == tag) {
return tableDirectory[i];
}
}
return null;
}
private void init(String name, int fIndex) throws Exception {
filereader = new FontFileReader(filename);
WoffDecoder decoder = null;
try {
if (!filereader.openFile()) {
throw new FileNotFoundException("Unable to create FontResource"
+ " for file " + filename);
}
Buffer buffer = filereader.readBlock(0, TTCHEADERSIZE);
int sfntTag = buffer.getInt();
if (sfntTag == woffTag) {
decoder = new WoffDecoder();
File file = decoder.openFile();
decoder.decode(filereader);
decoder.closeFile();
filereader.closeFile();
filereader = new FontFileReader(file.getPath());
if (!filereader.openFile()) {
throw new FileNotFoundException("Unable to create "
+ "FontResource for file " + filename);
}
buffer = filereader.readBlock(0, TTCHEADERSIZE);
sfntTag = buffer.getInt();
}
filesize = (int)filereader.getLength();
int headerOffset = 0;
if (sfntTag == ttcfTag) {
buffer.getInt();
directoryCount = buffer.getInt();
if (fIndex >= directoryCount) {
throw new Exception("Bad collection index");
}
fontIndex = fIndex;
buffer = filereader.readBlock(TTCHEADERSIZE+4*fIndex, 4);
headerOffset = buffer.getInt();
buffer = filereader.readBlock(headerOffset, 4);
sfntTag = buffer.getInt();
}
switch (sfntTag) {
case v1ttTag:
case trueTag:
break;
case ottoTag:
isCFF = true;
break;
default:
throw new Exception("Unsupported sfnt " + filename);
}
buffer = filereader.readBlock(headerOffset+4, 2);
numTables = buffer.getShort();
int directoryOffset = headerOffset+DIRECTORYHEADERSIZE;
Buffer ibuffer = filereader.
readBlock(directoryOffset, numTables*DIRECTORYENTRYSIZE);
DirectoryEntry table;
tableDirectory = new DirectoryEntry[numTables];
for (int i=0; i<numTables;i++) {
tableDirectory[i] = table = new DirectoryEntry();
table.tag = ibuffer.getInt();
ibuffer.skip(4);
table.offset = ibuffer.getInt();
table.length = ibuffer.getInt();
if ((table.offset < 0) || (table.length < 0) ||
(table.offset + table.length < table.length) ||
(table.offset + table.length > filesize))
{
throw new Exception("bad table, tag="+table.tag);
}
}
DirectoryEntry headDE = getDirectoryEntry(headTag);
Buffer headTable = filereader.readBlock(headDE.offset,
headDE.length);
upem = (float)(headTable.getShort(18) & 0xffff);
if (!(16 <= upem && upem <= 16384)) {
upem = 2048;
}
indexToLocFormat = headTable.getShort(50);
if (indexToLocFormat < 0 || indexToLocFormat > 1) {
throw new Exception("Bad indexToLocFormat");
}
Buffer hhea = readTable(hheaTag);
if (hhea == null) {
numHMetrics = -1;
} else {
ascent = -(float)hhea.getShort(4);
descent = -(float)hhea.getShort(6);
linegap = (float)hhea.getShort(8);
numHMetrics = hhea.getChar(34) & 0xffff;
int hmtxEntries = getTableLength(hmtxTag) >> 2;
if (numHMetrics > hmtxEntries) {
numHMetrics = hmtxEntries;
}
}
getNumGlyphs();
setStyle();
checkCMAP();
initNames();
if (familyName == null || fullName == null) {
String fontName = name != null ? name : "";
if (fullName == null) {
fullName = familyName != null ? familyName : fontName;
}
if (familyName == null) {
familyName = fullName != null ? fullName : fontName;
}
throw new Exception("Font name not found.");
}
if (decoder != null) {
isDecoded = true;
filename = filereader.getFilename();
PrismFontFactory.getFontFactory().addDecodedFont(this);
}
} catch (Exception e) {
if (decoder != null) {
decoder.deleteFile();
}
throw e;
} finally {
filereader.closeFile();
}
}
private static final int fsSelectionItalicBit = 0x00001;
private static final int fsSelectionBoldBit = 0x00020;
private static final int MACSTYLE_BOLD_BIT = 0x1;
private static final int MACSTYLE_ITALIC_BIT = 0x2;
private boolean isBold;
private boolean isItalic;
private float upem;
private float ascent, descent, linegap;
private int numHMetrics;
private void setStyle() {
DirectoryEntry os2_DE = getDirectoryEntry(os_2Tag);
if (os2_DE != null) {
Buffer os_2Table = filereader.readBlock(os2_DE.offset,
os2_DE.length);
int fsSelection = os_2Table.getChar(62) & 0xffff;
isItalic = (fsSelection & fsSelectionItalicBit) != 0;
isBold = (fsSelection & fsSelectionBoldBit) != 0;
} else {
DirectoryEntry headDE = getDirectoryEntry(headTag);
Buffer headTable = filereader.readBlock(headDE.offset,
headDE.length);
short macStyleBits = headTable.getShort(44);
isItalic = (macStyleBits & MACSTYLE_ITALIC_BIT) != 0;
isBold = (macStyleBits & MACSTYLE_BOLD_BIT) != 0;
}
}
public boolean isBold() {
return isBold;
}
public boolean isItalic() {
return isItalic;
}
public boolean isDecoded() {
return isDecoded;
}
public boolean isRegistered() {
return isRegistered;
}
public boolean isEmbeddedFont() {
return isEmbedded;
}
public int getUnitsPerEm() {
return (int)upem;
}
public short getIndexToLocFormat() {
return indexToLocFormat;
}
public int getNumHMetrics() {
return numHMetrics;
}
public static final int MAC_PLATFORM_ID = 1;
public static final int MACROMAN_SPECIFIC_ID = 0;
public static final int MACROMAN_ENGLISH_LANG = 0;
public static final int MS_PLATFORM_ID = 3;
public static final short MS_ENGLISH_LOCALE_ID = 0x0409;
public static final int FAMILY_NAME_ID = 1;
public static final int STYLE_NAME_ID = 2;
public static final int FULL_NAME_ID = 4;
public static final int PS_NAME_ID = 6;
void initNames() throws Exception {
byte[] name = new byte[256];
DirectoryEntry nameDE = getDirectoryEntry(nameTag);
Buffer buffer = filereader.readBlock(nameDE.offset, nameDE.length);
buffer.skip(2);
short numRecords = buffer.getShort();
int stringPtr = buffer.getShort() & 0xffff;
for (int i=0; i<numRecords; i++) {
short platformID = buffer.getShort();
if (platformID != MS_PLATFORM_ID &&
platformID != MAC_PLATFORM_ID) {
buffer.skip(10);
continue;
}
short encodingID = buffer.getShort();
if ((platformID == MS_PLATFORM_ID && encodingID > 1) ||
(platformID == MAC_PLATFORM_ID &&
encodingID != MACROMAN_SPECIFIC_ID)) {
buffer.skip(8);
continue;
}
short langID = buffer.getShort();
if (platformID == MAC_PLATFORM_ID &&
langID != MACROMAN_ENGLISH_LANG) {
buffer.skip(6);
continue;
}
short nameID = buffer.getShort();
int nameLen = ((int)buffer.getShort()) & 0xffff;
int namePtr = (((int)buffer.getShort()) & 0xffff) + stringPtr;
String tmpName = null;
String enc;
switch (nameID) {
case FAMILY_NAME_ID:
if (familyName == null || langID == MS_ENGLISH_LOCALE_ID ||
langID == nameLocaleID)
{
buffer.get(namePtr, name, 0, nameLen);
if (platformID == MAC_PLATFORM_ID) {
enc = "US-ASCII";
} else {
enc = "UTF-16BE";
}
tmpName = new String(name, 0, nameLen, enc);
if (familyName == null ||
langID == MS_ENGLISH_LOCALE_ID){
familyName = tmpName;
}
if (langID == nameLocaleID) {
localeFamilyName = tmpName;
}
}
break;
case FULL_NAME_ID:
if (fullName == null ||
langID == MS_ENGLISH_LOCALE_ID ||
langID == nameLocaleID)
{
buffer.get(namePtr, name, 0, nameLen);
if (platformID == MAC_PLATFORM_ID) {
enc = "US-ASCII";
} else {
enc = "UTF-16BE";
}
tmpName = new String(name, 0, nameLen, enc);
if (fullName == null ||
langID == MS_ENGLISH_LOCALE_ID) {
fullName = tmpName;
}
if (langID == nameLocaleID) {
localeFullName = tmpName;
}
}
break;
case PS_NAME_ID:
if (psName == null) {
buffer.get(namePtr, name, 0, nameLen);
if (platformID == MAC_PLATFORM_ID) {
enc = "US-ASCII";
} else {
enc = "UTF-16BE";
}
psName = new String(name, 0, nameLen, enc);
}
break;
case STYLE_NAME_ID:
if (styleName == null ||
langID == MS_ENGLISH_LOCALE_ID ||
langID == nameLocaleID)
{
buffer.get(namePtr, name, 0, nameLen);
if (platformID == MAC_PLATFORM_ID) {
enc = "US-ASCII";
} else {
enc = "UTF-16BE";
}
tmpName = new String(name, 0, nameLen, enc);
if (styleName == null ||
langID == MS_ENGLISH_LOCALE_ID) {
styleName = tmpName;
}
if (langID == nameLocaleID) {
localeStyleName = tmpName;
}
}
break;
default:
break;
}
if (localeFamilyName == null) {
localeFamilyName = familyName;
}
if (localeFullName == null) {
localeFullName = fullName;
}
if (localeStyleName == null) {
localeStyleName = styleName;
}
}
}
private void checkCMAP() throws Exception {
DirectoryEntry cmapDE = getDirectoryEntry(FontConstants.cmapTag);
if (cmapDE != null) {
if (cmapDE.length < 4) {
throw new Exception("Invalid cmap table length");
}
Buffer cmapTableHeader = filereader.readBlock(cmapDE.offset, 4);
short version = cmapTableHeader.getShort();
short numberSubTables = cmapTableHeader.getShort();
int indexLength = numberSubTables * 8;
if (numberSubTables <= 0 || cmapDE.length < indexLength + 4) {
throw new Exception("Invalid cmap subtables count");
}
Buffer cmapTableIndex = filereader.readBlock(cmapDE.offset + 4, indexLength);
for (int i = 0; i < numberSubTables; i++) {
short platformID = cmapTableIndex.getShort();
short encodingID = cmapTableIndex.getShort();
int offset = cmapTableIndex.getInt();
if (offset < 0 || offset >= cmapDE.length) {
throw new Exception("Invalid cmap subtable offset");
}
}
}
}
private static Map<String, Short> lcidMap;
private static void addLCIDMapEntry(Map<String, Short> map,
String key, short value) {
map.put(key, Short.valueOf(value));
}
private static synchronized void createLCIDMap() {
if (lcidMap != null) {
return;
}
Map<String, Short> map = new HashMap<String, Short>(200);
addLCIDMapEntry(map, "ar", (short) 0x0401);
addLCIDMapEntry(map, "bg", (short) 0x0402);
addLCIDMapEntry(map, "ca", (short) 0x0403);
addLCIDMapEntry(map, "zh", (short) 0x0404);
addLCIDMapEntry(map, "cs", (short) 0x0405);
addLCIDMapEntry(map, "da", (short) 0x0406);
addLCIDMapEntry(map, "de", (short) 0x0407);
addLCIDMapEntry(map, "el", (short) 0x0408);
addLCIDMapEntry(map, "es", (short) 0x040a);
addLCIDMapEntry(map, "fi", (short) 0x040b);
addLCIDMapEntry(map, "fr", (short) 0x040c);
addLCIDMapEntry(map, "iw", (short) 0x040d);
addLCIDMapEntry(map, "hu", (short) 0x040e);
addLCIDMapEntry(map, "is", (short) 0x040f);
addLCIDMapEntry(map, "it", (short) 0x0410);
addLCIDMapEntry(map, "ja", (short) 0x0411);
addLCIDMapEntry(map, "ko", (short) 0x0412);
addLCIDMapEntry(map, "nl", (short) 0x0413);
addLCIDMapEntry(map, "no", (short) 0x0414);
addLCIDMapEntry(map, "pl", (short) 0x0415);
addLCIDMapEntry(map, "pt", (short) 0x0416);
addLCIDMapEntry(map, "rm", (short) 0x0417);
addLCIDMapEntry(map, "ro", (short) 0x0418);
addLCIDMapEntry(map, "ru", (short) 0x0419);
addLCIDMapEntry(map, "hr", (short) 0x041a);
addLCIDMapEntry(map, "sk", (short) 0x041b);
addLCIDMapEntry(map, "sq", (short) 0x041c);
addLCIDMapEntry(map, "sv", (short) 0x041d);
addLCIDMapEntry(map, "th", (short) 0x041e);
addLCIDMapEntry(map, "tr", (short) 0x041f);
addLCIDMapEntry(map, "ur", (short) 0x0420);
addLCIDMapEntry(map, "in", (short) 0x0421);
addLCIDMapEntry(map, "uk", (short) 0x0422);
addLCIDMapEntry(map, "be", (short) 0x0423);
addLCIDMapEntry(map, "sl", (short) 0x0424);
addLCIDMapEntry(map, "et", (short) 0x0425);
addLCIDMapEntry(map, "lv", (short) 0x0426);
addLCIDMapEntry(map, "lt", (short) 0x0427);
addLCIDMapEntry(map, "fa", (short) 0x0429);
addLCIDMapEntry(map, "vi", (short) 0x042a);
addLCIDMapEntry(map, "hy", (short) 0x042b);
addLCIDMapEntry(map, "eu", (short) 0x042d);
addLCIDMapEntry(map, "mk", (short) 0x042f);
addLCIDMapEntry(map, "tn", (short) 0x0432);
addLCIDMapEntry(map, "xh", (short) 0x0434);
addLCIDMapEntry(map, "zu", (short) 0x0435);
addLCIDMapEntry(map, "af", (short) 0x0436);
addLCIDMapEntry(map, "ka", (short) 0x0437);
addLCIDMapEntry(map, "fo", (short) 0x0438);
addLCIDMapEntry(map, "hi", (short) 0x0439);
addLCIDMapEntry(map, "mt", (short) 0x043a);
addLCIDMapEntry(map, "se", (short) 0x043b);
addLCIDMapEntry(map, "gd", (short) 0x043c);
addLCIDMapEntry(map, "ms", (short) 0x043e);
addLCIDMapEntry(map, "kk", (short) 0x043f);
addLCIDMapEntry(map, "ky", (short) 0x0440);
addLCIDMapEntry(map, "sw", (short) 0x0441);
addLCIDMapEntry(map, "tt", (short) 0x0444);
addLCIDMapEntry(map, "bn", (short) 0x0445);
addLCIDMapEntry(map, "pa", (short) 0x0446);
addLCIDMapEntry(map, "gu", (short) 0x0447);
addLCIDMapEntry(map, "ta", (short) 0x0449);
addLCIDMapEntry(map, "te", (short) 0x044a);
addLCIDMapEntry(map, "kn", (short) 0x044b);
addLCIDMapEntry(map, "ml", (short) 0x044c);
addLCIDMapEntry(map, "mr", (short) 0x044e);
addLCIDMapEntry(map, "sa", (short) 0x044f);
addLCIDMapEntry(map, "mn", (short) 0x0450);
addLCIDMapEntry(map, "cy", (short) 0x0452);
addLCIDMapEntry(map, "gl", (short) 0x0456);
addLCIDMapEntry(map, "dv", (short) 0x0465);
addLCIDMapEntry(map, "qu", (short) 0x046b);
addLCIDMapEntry(map, "mi", (short) 0x0481);
addLCIDMapEntry(map, "ar_IQ", (short) 0x0801);
addLCIDMapEntry(map, "zh_CN", (short) 0x0804);
addLCIDMapEntry(map, "de_CH", (short) 0x0807);
addLCIDMapEntry(map, "en_GB", (short) 0x0809);
addLCIDMapEntry(map, "es_MX", (short) 0x080a);
addLCIDMapEntry(map, "fr_BE", (short) 0x080c);
addLCIDMapEntry(map, "it_CH", (short) 0x0810);
addLCIDMapEntry(map, "nl_BE", (short) 0x0813);
addLCIDMapEntry(map, "no_NO_NY", (short) 0x0814);
addLCIDMapEntry(map, "pt_PT", (short) 0x0816);
addLCIDMapEntry(map, "ro_MD", (short) 0x0818);
addLCIDMapEntry(map, "ru_MD", (short) 0x0819);
addLCIDMapEntry(map, "sr_CS", (short) 0x081a);
addLCIDMapEntry(map, "sv_FI", (short) 0x081d);
addLCIDMapEntry(map, "az_AZ", (short) 0x082c);
addLCIDMapEntry(map, "se_SE", (short) 0x083b);
addLCIDMapEntry(map, "ga_IE", (short) 0x083c);
addLCIDMapEntry(map, "ms_BN", (short) 0x083e);
addLCIDMapEntry(map, "uz_UZ", (short) 0x0843);
addLCIDMapEntry(map, "qu_EC", (short) 0x086b);
addLCIDMapEntry(map, "ar_EG", (short) 0x0c01);
addLCIDMapEntry(map, "zh_HK", (short) 0x0c04);
addLCIDMapEntry(map, "de_AT", (short) 0x0c07);
addLCIDMapEntry(map, "en_AU", (short) 0x0c09);
addLCIDMapEntry(map, "fr_CA", (short) 0x0c0c);
addLCIDMapEntry(map, "sr_CS", (short) 0x0c1a);
addLCIDMapEntry(map, "se_FI", (short) 0x0c3b);
addLCIDMapEntry(map, "qu_PE", (short) 0x0c6b);
addLCIDMapEntry(map, "ar_LY", (short) 0x1001);
addLCIDMapEntry(map, "zh_SG", (short) 0x1004);
addLCIDMapEntry(map, "de_LU", (short) 0x1007);
addLCIDMapEntry(map, "en_CA", (short) 0x1009);
addLCIDMapEntry(map, "es_GT", (short) 0x100a);
addLCIDMapEntry(map, "fr_CH", (short) 0x100c);
addLCIDMapEntry(map, "hr_BA", (short) 0x101a);
addLCIDMapEntry(map, "ar_DZ", (short) 0x1401);
addLCIDMapEntry(map, "zh_MO", (short) 0x1404);
addLCIDMapEntry(map, "de_LI", (short) 0x1407);
addLCIDMapEntry(map, "en_NZ", (short) 0x1409);
addLCIDMapEntry(map, "es_CR", (short) 0x140a);
addLCIDMapEntry(map, "fr_LU", (short) 0x140c);
addLCIDMapEntry(map, "bs_BA", (short) 0x141a);
addLCIDMapEntry(map, "ar_MA", (short) 0x1801);
addLCIDMapEntry(map, "en_IE", (short) 0x1809);
addLCIDMapEntry(map, "es_PA", (short) 0x180a);
addLCIDMapEntry(map, "fr_MC", (short) 0x180c);
addLCIDMapEntry(map, "sr_BA", (short) 0x181a);
addLCIDMapEntry(map, "ar_TN", (short) 0x1c01);
addLCIDMapEntry(map, "en_ZA", (short) 0x1c09);
addLCIDMapEntry(map, "es_DO", (short) 0x1c0a);
addLCIDMapEntry(map, "sr_BA", (short) 0x1c1a);
addLCIDMapEntry(map, "ar_OM", (short) 0x2001);
addLCIDMapEntry(map, "en_JM", (short) 0x2009);
addLCIDMapEntry(map, "es_VE", (short) 0x200a);
addLCIDMapEntry(map, "ar_YE", (short) 0x2401);
addLCIDMapEntry(map, "es_CO", (short) 0x240a);
addLCIDMapEntry(map, "ar_SY", (short) 0x2801);
addLCIDMapEntry(map, "en_BZ", (short) 0x2809);
addLCIDMapEntry(map, "es_PE", (short) 0x280a);
addLCIDMapEntry(map, "ar_JO", (short) 0x2c01);
addLCIDMapEntry(map, "en_TT", (short) 0x2c09);
addLCIDMapEntry(map, "es_AR", (short) 0x2c0a);
addLCIDMapEntry(map, "ar_LB", (short) 0x3001);
addLCIDMapEntry(map, "en_ZW", (short) 0x3009);
addLCIDMapEntry(map, "es_EC", (short) 0x300a);
addLCIDMapEntry(map, "ar_KW", (short) 0x3401);
addLCIDMapEntry(map, "en_PH", (short) 0x3409);
addLCIDMapEntry(map, "es_CL", (short) 0x340a);
addLCIDMapEntry(map, "ar_AE", (short) 0x3801);
addLCIDMapEntry(map, "es_UY", (short) 0x380a);
addLCIDMapEntry(map, "ar_BH", (short) 0x3c01);
addLCIDMapEntry(map, "es_PY", (short) 0x3c0a);
addLCIDMapEntry(map, "ar_QA", (short) 0x4001);
addLCIDMapEntry(map, "es_BO", (short) 0x400a);
addLCIDMapEntry(map, "es_SV", (short) 0x440a);
addLCIDMapEntry(map, "es_HN", (short) 0x480a);
addLCIDMapEntry(map, "es_NI", (short) 0x4c0a);
addLCIDMapEntry(map, "es_PR", (short) 0x500a);
lcidMap = map;
}
private static short getLCIDFromLocale(Locale locale) {
if (locale.equals(Locale.US) || locale.getLanguage().equals("en")) {
return MS_ENGLISH_LOCALE_ID;
}
if (lcidMap == null) {
createLCIDMap();
}
String key = locale.toString();
while (!key.isEmpty()) {
Short lcidObject = (Short) lcidMap.get(key);
if (lcidObject != null) {
return lcidObject.shortValue();
}
int pos = key.lastIndexOf('_');
if (pos < 1) {
return MS_ENGLISH_LOCALE_ID;
}
key = key.substring(0, pos);
}
return MS_ENGLISH_LOCALE_ID;
}
static short nameLocaleID = getSystemLCID();
private static short getSystemLCID() {
if (PrismFontFactory.isWindows) {
return PrismFontFactory.getSystemLCID();
} else {
return getLCIDFromLocale(Locale.getDefault());
}
}
private OpenTypeGlyphMapper mapper = null;
public CharToGlyphMapper getGlyphMapper() {
if (mapper == null) {
mapper = new OpenTypeGlyphMapper(this);
}
return mapper;
}
public FontStrike getStrike(float size, BaseTransform transform) {
return getStrike(size, transform, getDefaultAAMode());
}
char[] advanceWidths = null;
public float getAdvance(int glyphCode, float ptSize) {
if (glyphCode == CharToGlyphMapper.INVISIBLE_GLYPH_ID)
return 0f;
if (advanceWidths == null && numHMetrics > 0) {
synchronized (this) {
Buffer hmtx = readTable(hmtxTag);
if (hmtx == null) {
numHMetrics = -1;
return 0;
}
char[] aw = new char[numHMetrics];
for (int i=0; i<numHMetrics; i++) {
aw[i] = hmtx.getChar(i*4);
}
advanceWidths = aw;
}
}
if (numHMetrics > 0) {
char cadv;
if (glyphCode < numHMetrics) {
cadv = advanceWidths[glyphCode];
} else {
cadv = advanceWidths[numHMetrics-1];
}
return ((float)(cadv & 0xffff)*ptSize)/upem;
} else {
return 0f;
}
}
public PrismMetrics getFontMetrics(float ptSize) {
return new PrismMetrics((ascent*ptSize)/upem,
(descent*ptSize)/upem,
(linegap*ptSize)/upem,
this, ptSize);
}
private float[] styleMetrics;
float[] getStyleMetrics(float ptSize) {
if (styleMetrics == null) {
float [] smetrics = new float[METRICS_TOTAL];
Buffer os_2 = readTable(os_2Tag);
int length = os_2 != null ? os_2.capacity() : 0;
if (length >= 30) {
smetrics[STRIKETHROUGH_THICKNESS] = os_2.getShort(26) / upem;
smetrics[STRIKETHROUGH_OFFSET] = -os_2.getShort(28) / upem;
if (smetrics[STRIKETHROUGH_THICKNESS] < 0f) {
smetrics[STRIKETHROUGH_THICKNESS] = 0.05f;
}
if (Math.abs(smetrics[STRIKETHROUGH_OFFSET]) > 2.0f) {
smetrics[STRIKETHROUGH_OFFSET] = -0.4f;
}
} else {
smetrics[STRIKETHROUGH_THICKNESS] = 0.05f;
smetrics[STRIKETHROUGH_OFFSET] = -0.4f;
}
if (length >= 74) {
smetrics[TYPO_ASCENT] = -os_2.getShort(68) / upem;
smetrics[TYPO_DESCENT] = -os_2.getShort(70) / upem;
smetrics[TYPO_LINEGAP] = os_2.getShort(72) / upem;
} else {
smetrics[TYPO_ASCENT] = ascent / upem;
smetrics[TYPO_DESCENT] = descent / upem;
smetrics[TYPO_LINEGAP] = linegap / upem;
}
if (length >= 90) {
smetrics[XHEIGHT] = os_2.getShort(86) / upem;
smetrics[CAPHEIGHT] = os_2.getShort(88);
if ((smetrics[CAPHEIGHT] / ascent) < 0.5) {
smetrics[CAPHEIGHT] = 0;
} else {
smetrics[CAPHEIGHT] /= upem;
}
}
if (smetrics[XHEIGHT] == 0 || smetrics[CAPHEIGHT] == 0) {
FontStrike strike = getStrike(ptSize, BaseTransform.IDENTITY_TRANSFORM);
CharToGlyphMapper mapper = getGlyphMapper();
int missingGlyph = mapper.getMissingGlyphCode();
if (smetrics[XHEIGHT] == 0) {
int gc = mapper.charToGlyph('x');
if (gc != missingGlyph) {
RectBounds fbds = strike.getGlyph(gc).getBBox();
smetrics[XHEIGHT] = fbds.getHeight() / ptSize;
} else {
smetrics[XHEIGHT] = -ascent * 0.6f / upem;
}
}
if (smetrics[CAPHEIGHT] == 0) {
int gc = mapper.charToGlyph('H');
if (gc != missingGlyph) {
RectBounds fbds = strike.getGlyph(gc).getBBox();
smetrics[CAPHEIGHT] = fbds.getHeight() / ptSize;
} else {
smetrics[CAPHEIGHT] = -ascent * 0.9f / upem;
}
}
}
Buffer postTable = readTable(postTag);
if (postTable == null || postTable.capacity() < 12) {
smetrics[UNDERLINE_OFFSET] = 0.1f;
smetrics[UNDERLINE_THICKESS] = 0.05f;
} else {
smetrics[UNDERLINE_OFFSET] = -postTable.getShort(8) / upem;
smetrics[UNDERLINE_THICKESS] = postTable.getShort(10) / upem;
if (smetrics[UNDERLINE_THICKESS] < 0f) {
smetrics[UNDERLINE_THICKESS] = 0.05f;
}
if (Math.abs(smetrics[UNDERLINE_OFFSET]) > 2.0f) {
smetrics[UNDERLINE_OFFSET] = 0.1f;
}
}
styleMetrics = smetrics;
}
float[] metrics = new float[METRICS_TOTAL];
for (int i = 0; i < METRICS_TOTAL; i++) {
metrics[i] = styleMetrics[i] * ptSize;
}
return metrics;
}
byte[] getTableBytes(int tag) {
Buffer buffer = readTable(tag);
byte[] table = null;
if(buffer != null){
table = new byte[buffer.capacity()];
buffer.get(0, table, 0, buffer.capacity());
}
return table;
}
@Override
public boolean equals(Object obj) {
if (obj == null) {
return false;
}
if (!(obj instanceof PrismFontFile)) {
return false;
}
final PrismFontFile other = (PrismFontFile)obj;
return filename.equals(other.filename) && fullName.equals(other.fullName);
}
@Override
public int hashCode() {
return filename.hashCode() + (71 * fullName.hashCode());
}
}
