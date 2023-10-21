package com.sun.javafx.font;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import com.sun.glass.ui.Screen;
import com.sun.glass.utils.NativeLibLoader;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.text.GlyphLayout;
import static com.sun.javafx.FXPermissions.LOAD_FONT_PERMISSION;
public abstract class PrismFontFactory implements FontFactory {
public static final boolean debugFonts;
public static final boolean isWindows;
public static final boolean isLinux;
public static final boolean isMacOSX;
public static final boolean isIOS;
public static final boolean isAndroid;
public static final boolean isEmbedded;
public static final int cacheLayoutSize;
private static int subPixelMode;
public static final int SUB_PIXEL_ON = 1;
public static final int SUB_PIXEL_Y = 2;
public static final int SUB_PIXEL_NATIVE = 4;
private static float fontSizeLimit = 80f;
private static boolean lcdEnabled;
private static float lcdContrast = -1;
private static String jreFontDir;
private static final String jreDefaultFont = "Lucida Sans Regular";
private static final String jreDefaultFontLC = "lucida sans regular";
private static final String jreDefaultFontFile = "LucidaSansRegular.ttf";
private static final String CT_FACTORY = "com.sun.javafx.font.coretext.CTFactory";
private static final String DW_FACTORY = "com.sun.javafx.font.directwrite.DWFactory";
private static final String FT_FACTORY = "com.sun.javafx.font.freetype.FTFactory";
HashMap<String, FontResource> fontResourceMap =
new HashMap<String, FontResource>();
HashMap<String, CompositeFontResource> compResourceMap =
new HashMap<String, CompositeFontResource>();
static {
isWindows = PlatformUtil.isWindows();
isMacOSX = PlatformUtil.isMac();
isLinux = PlatformUtil.isLinux();
isIOS = PlatformUtil.isIOS();
isAndroid = PlatformUtil.isAndroid();
isEmbedded = PlatformUtil.isEmbedded();
int[] tempCacheLayoutSize = {0x10000};
@SuppressWarnings("removal")
boolean tmp = AccessController.doPrivileged(
(PrivilegedAction<Boolean>) () -> {
NativeLibLoader.loadLibrary("javafx_font");
String dbg = System.getProperty("prism.debugfonts", "");
boolean debug = "true".equals(dbg);
jreFontDir = getJDKFontDir();
String s = System.getProperty("com.sun.javafx.fontSize");
systemFontSize = -1f;
if (s != null) {
try {
systemFontSize = Float.parseFloat(s);
} catch (NumberFormatException nfe) {
System.err.println("Cannot parse font size '"
+ s + "'");
}
}
s = System.getProperty("prism.subpixeltext", "on");
if (s.indexOf("on") != -1 || s.indexOf("true") != -1) {
subPixelMode = SUB_PIXEL_ON;
}
if (s.indexOf("native") != -1) {
subPixelMode |= SUB_PIXEL_NATIVE | SUB_PIXEL_ON;
}
if (s.indexOf("vertical") != -1) {
subPixelMode |= SUB_PIXEL_Y | SUB_PIXEL_NATIVE | SUB_PIXEL_ON;
}
s = System.getProperty("prism.fontSizeLimit");
if (s != null) {
try {
fontSizeLimit = Float.parseFloat(s);
if (fontSizeLimit <= 0) {
fontSizeLimit = Float.POSITIVE_INFINITY;
}
} catch (NumberFormatException nfe) {
System.err.println("Cannot parse fontSizeLimit '" + s + "'");
}
}
boolean lcdTextOff = isMacOSX || isIOS || isAndroid || isEmbedded;
String defLCDProp = lcdTextOff ? "false" : "true";
String lcdProp = System.getProperty("prism.lcdtext", defLCDProp);
lcdEnabled = lcdProp.equals("true");
s = System.getProperty("prism.cacheLayoutSize");
if (s != null) {
try {
tempCacheLayoutSize[0] = Integer.parseInt(s);
if (tempCacheLayoutSize[0] < 0) {
tempCacheLayoutSize[0] = 0;
}
} catch (NumberFormatException nfe) {
System.err.println("Cannot parse cache layout size '"
+ s + "'");
}
}
return debug;
}
);
debugFonts = tmp;
cacheLayoutSize = tempCacheLayoutSize[0];
}
private static String getJDKFontDir() {
return System.getProperty("java.home","") + File.separator +
"lib" + File.separator + "fonts";
}
private static String getNativeFactoryName() {
if (isWindows) return DW_FACTORY;
if (isMacOSX || isIOS) return CT_FACTORY;
if (isLinux || isAndroid) return FT_FACTORY;
return null;
}
public static float getFontSizeLimit() {
return fontSizeLimit;
}
private static PrismFontFactory theFontFactory = null;
public static synchronized PrismFontFactory getFontFactory() {
if (theFontFactory != null) {
return theFontFactory;
}
String factoryClass = getNativeFactoryName();
if (factoryClass == null) {
throw new InternalError("cannot find a native font factory");
}
if (debugFonts) {
System.err.println("Loading FontFactory " + factoryClass);
if (subPixelMode != 0) {
String s = "Subpixel: enabled";
if ((subPixelMode & SUB_PIXEL_Y) != 0) {
s += ", vertical";
}
if ((subPixelMode & SUB_PIXEL_NATIVE) != 0) {
s += ", native";
}
System.err.println(s);
}
}
theFontFactory = getFontFactory(factoryClass);
if (theFontFactory == null) {
throw new InternalError("cannot load font factory: "+ factoryClass);
}
return theFontFactory;
}
private static synchronized PrismFontFactory getFontFactory(String factoryClass) {
try {
Class<?> clazz = Class.forName(factoryClass);
Method mid = clazz.getMethod("getFactory", (Class[])null);
return (PrismFontFactory)mid.invoke(null);
} catch (Throwable t) {
if (debugFonts) {
System.err.println("Loading font factory failed "+ factoryClass);
}
}
return null;
}
private HashMap<String, PrismFontFile>
fileNameToFontResourceMap = new HashMap<String, PrismFontFile>();
protected abstract PrismFontFile
createFontFile(String name, String filename,
int fIndex, boolean register,
boolean embedded,
boolean copy, boolean tracked)
throws Exception;
public abstract GlyphLayout createGlyphLayout();
private PrismFontFile createFontResource(String filename, int index) {
return createFontResource(null, filename, index,
true, false, false, false);
}
private PrismFontFile createFontResource(String name,
String filename, int index,
boolean register, boolean embedded,
boolean copy, boolean tracked) {
String key = (filename+index).toLowerCase();
PrismFontFile fr = fileNameToFontResourceMap.get(key);
if (fr != null) {
return fr;
}
try {
fr = createFontFile(name, filename, index, register,
embedded, copy, tracked);
if (register) {
storeInMap(fr.getFullName(), fr);
fileNameToFontResourceMap.put(key, fr);
}
return fr;
} catch (Exception e) {
if (PrismFontFactory.debugFonts) {
e.printStackTrace();
}
return null;
}
}
private PrismFontFile createFontResource(String name, String filename) {
PrismFontFile[] pffArr =
createFontResources(name, filename,
true, false, false, false, false);
if (pffArr == null || pffArr.length == 0) {
return null;
} else {
return pffArr[0];
}
}
private PrismFontFile[] createFontResources(String name, String filename,
boolean register,
boolean embedded,
boolean copy,
boolean tracked,
boolean loadAll) {
PrismFontFile[] fArr = null;
if (filename == null) {
return null;
}
PrismFontFile fr = createFontResource(name, filename, 0, register,
embedded, copy, tracked);
if (fr == null) {
return null;
}
int cnt = (!loadAll) ? 1 : fr.getFontCount();
fArr = new PrismFontFile[cnt];
fArr[0] = fr;
if (cnt == 1) {
return fArr;
}
PrismFontFile.FileRefCounter rc = null;
if (copy) {
rc = fr.createFileRefCounter();
}
int index = 1;
do {
String key = (filename+index).toLowerCase();
try {
fr = fileNameToFontResourceMap.get(key);
if (fr != null) {
fArr[index] = fr;
continue;
} else {
fr = createFontFile(null, filename, index,
register, embedded,
copy, tracked);
if (fr == null) {
return null;
}
if (rc != null) {
fr.setAndIncFileRefCounter(rc);
}
fArr[index] = fr;
String fontname = fr.getFullName();
if (register) {
storeInMap(fontname, fr);
fileNameToFontResourceMap.put(key, fr);
}
}
} catch (Exception e) {
if (PrismFontFactory.debugFonts) {
e.printStackTrace();
}
return null;
}
} while (++index < cnt);
return fArr;
}
private String dotStyleStr(boolean bold, boolean italic) {
if (!bold) {
if (!italic) {
return "";
}
else {
return ".italic";
}
} else {
if (!italic) {
return ".bold";
}
else {
return ".bolditalic";
}
}
}
private void storeInMap(String name, FontResource resource) {
if (name == null || resource == null) {
return;
}
if (resource instanceof PrismCompositeFontResource) {
System.err.println(name + " is a composite " +
resource);
Thread.dumpStack();
return;
}
fontResourceMap.put(name.toLowerCase(), resource);
}
private ArrayList<WeakReference<PrismFontFile>> tmpFonts;
synchronized void addDecodedFont(PrismFontFile fr) {
fr.setIsDecoded(true);
addTmpFont(fr);
}
private synchronized void addTmpFont(PrismFontFile fr) {
if (tmpFonts == null) {
tmpFonts = new ArrayList<WeakReference<PrismFontFile>>();
}
WeakReference<PrismFontFile> ref;
if (fr.isRegistered()) {
ref = new WeakReference<PrismFontFile>(fr);
} else {
ref = fr.createFileDisposer(this, fr.getFileRefCounter());
}
tmpFonts.add(ref);
addFileCloserHook();
}
synchronized void removeTmpFont(WeakReference<PrismFontFile> ref) {
if (tmpFonts != null) {
tmpFonts.remove(ref);
}
}
public synchronized FontResource getFontResource(String familyName,
boolean bold,
boolean italic,
boolean wantComp) {
if (familyName == null || familyName.isEmpty()) {
return null;
}
String lcFamilyName = familyName.toLowerCase();
String styleStr = dotStyleStr(bold, italic);
FontResource fr;
fr = lookupResource(lcFamilyName+styleStr, wantComp);
if (fr != null) {
return fr;
}
if (embeddedFonts != null && wantComp) {
fr = lookupResource(lcFamilyName+styleStr, false);
if (fr != null) {
return new PrismCompositeFontResource(fr, lcFamilyName+styleStr);
}
for (PrismFontFile embeddedFont : embeddedFonts.values()) {
String lcEmFamily = embeddedFont.getFamilyName().toLowerCase();
if (lcEmFamily.equals(lcFamilyName)) {
return new PrismCompositeFontResource(embeddedFont,
lcFamilyName+styleStr);
}
}
}
if (isWindows) {
int style = ((bold ? 1 : 0)) + ((italic) ? 2 : 0);
String fontFile = WindowsFontMap.findFontFile(lcFamilyName, style);
if (fontFile != null) {
fr = createFontResource(null, fontFile);
if (fr != null) {
if (bold == fr.isBold() && italic == fr.isItalic() &&
!styleStr.isEmpty())
{
storeInMap(lcFamilyName+styleStr, fr);
}
if (wantComp) {
fr = new PrismCompositeFontResource(fr,
lcFamilyName+styleStr);
}
return fr;
}
}
}
getFullNameToFileMap();
ArrayList<String> family = familyToFontListMap.get(lcFamilyName);
if (family == null) {
return null;
}
FontResource plainFR = null, boldFR = null,
italicFR = null, boldItalicFR = null;
for (String fontName : family) {
String lcFontName = fontName.toLowerCase();
fr = fontResourceMap.get(lcFontName);
if (fr == null) {
String file = findFile(lcFontName);
if (file != null) {
fr = getFontResource(fontName, file);
}
if (fr == null) {
continue;
}
storeInMap(lcFontName, fr);
}
if (bold == fr.isBold() && italic == fr.isItalic()) {
storeInMap(lcFamilyName+styleStr, fr);
if (wantComp) {
fr = new PrismCompositeFontResource(fr,
lcFamilyName+styleStr);
}
return fr;
}
if (!fr.isBold()) {
if (!fr.isItalic()) {
plainFR = fr;
} else {
italicFR = fr;
}
} else {
if (!fr.isItalic()) {
boldFR = fr;
} else {
boldItalicFR = fr;
}
}
}
if (!bold && !italic) {
if (boldFR != null) {
fr = boldFR;
} else if (italicFR != null) {
fr = italicFR;
} else {
fr = boldItalicFR;
}
} else if (bold && !italic) {
if (plainFR != null) {
fr = plainFR;
} else if (boldItalicFR != null) {
fr = boldItalicFR;
} else {
fr = italicFR;
}
} else if (!bold && italic) {
if (boldItalicFR != null) {
fr = boldItalicFR;
} else if (plainFR != null) {
fr = plainFR;
} else {
fr = boldFR;
}
} else {
if (italicFR != null) {
fr = italicFR;
} else if (boldFR != null) {
fr = boldFR;
} else {
fr = plainFR;
}
}
if (fr != null) {
storeInMap(lcFamilyName+styleStr, fr);
if (wantComp) {
fr = new PrismCompositeFontResource(fr, lcFamilyName+styleStr);
}
}
return fr;
}
public synchronized PGFont createFont(String familyName, boolean bold,
boolean italic, float size) {
FontResource fr = null;
if (familyName != null && !familyName.isEmpty()) {
PGFont logFont =
LogicalFont.getLogicalFont(familyName, bold, italic, size);
if (logFont != null) {
return logFont;
}
fr = getFontResource(familyName, bold, italic, true);
}
if (fr == null) {
return LogicalFont.getLogicalFont("System", bold, italic, size);
}
return new PrismFont(fr, fr.getFullName(), size);
}
public synchronized PGFont createFont(String name, float size) {
FontResource fr = null;
if (name != null && !name.isEmpty()) {
PGFont logFont =
LogicalFont.getLogicalFont(name, size);
if (logFont != null) {
return logFont;
}
fr = getFontResource(name, null, true);
}
if (fr == null) {
return LogicalFont.getLogicalFont(DEFAULT_FULLNAME, size);
}
return new PrismFont(fr, fr.getFullName(), size);
}
private PrismFontFile getFontResource(String name, String file) {
PrismFontFile fr = null;
if (isMacOSX) {
DFontDecoder decoder = null;
if (name != null) {
if (file.endsWith(".dfont")) {
decoder = new DFontDecoder();
try {
decoder.openFile();
decoder.decode(name);
decoder.closeFile();
file = decoder.getFile().getPath();
} catch (Exception e) {
file = null;
decoder.deleteFile();
decoder = null;
if (PrismFontFactory.debugFonts) {
e.printStackTrace();
}
}
}
}
if (file != null) {
fr = createFontResource(name, file);
}
if (decoder != null) {
if (fr != null) {
addDecodedFont(fr);
} else {
decoder.deleteFile();
}
}
} else {
fr = createFontResource(name, file);
}
return fr;
}
public synchronized PGFont deriveFont(PGFont font, boolean bold,
boolean italic, float size) {
FontResource fr = font.getFontResource();
return new PrismFont(fr, fr.getFullName(), size);
}
private FontResource lookupResource(String lcName, boolean wantComp) {
if (wantComp) {
return compResourceMap.get(lcName);
} else {
return fontResourceMap.get(lcName);
}
}
public synchronized FontResource getFontResource(String name, String file,
boolean wantComp) {
FontResource fr = null;
if (name != null) {
String lcName = name.toLowerCase();
FontResource fontResource = lookupResource(lcName, wantComp);
if (fontResource != null) {
return fontResource;
}
if (embeddedFonts != null && wantComp) {
fr = lookupResource(lcName, false);
if (fr != null) {
fr = new PrismCompositeFontResource(fr, lcName);
}
if (fr != null) {
return fr;
}
}
}
if (isWindows && name != null) {
String lcName = name.toLowerCase();
String fontFile = WindowsFontMap.findFontFile(lcName, -1);
if (fontFile != null) {
fr = createFontResource(null, fontFile);
if (fr != null) {
if (wantComp) {
fr = new PrismCompositeFontResource(fr, lcName);
}
return fr;
}
}
}
getFullNameToFileMap();
if (name != null && file != null) {
fr = getFontResource(name, file);
if (fr != null) {
if (wantComp) {
fr = new PrismCompositeFontResource(fr, name.toLowerCase());
}
return fr;
}
}
if (name != null) {
fr = getFontResourceByFullName(name, wantComp);
if (fr != null) {
return fr;
}
}
if (file != null) {
fr = getFontResourceByFileName(file, wantComp);
if (fr != null) {
return fr;
}
}
return null;
}
synchronized private FontResource
getFontResourceByFileName(String file, boolean wantComp) {
if (fontToFileMap.size() <= 1) {
return null;
}
String name = fileToFontMap.get(file.toLowerCase());
FontResource fontResource = null;
if (name == null) {
fontResource = createFontResource(file, 0);
if (fontResource != null) {
String lcName = fontResource.getFullName().toLowerCase();
storeInMap(lcName, fontResource);
if (wantComp) {
fontResource =
new PrismCompositeFontResource(fontResource, lcName);
}
}
} else {
String lcName = name.toLowerCase();
fontResource = lookupResource(lcName, wantComp);
if (fontResource == null) {
String fullPath = findFile(lcName);
if (fullPath != null) {
fontResource = getFontResource(name, fullPath);
if (fontResource != null) {
storeInMap(lcName, fontResource);
}
if (wantComp) {
fontResource =
new PrismCompositeFontResource(fontResource, lcName);
}
}
}
}
return fontResource;
}
synchronized private FontResource
getFontResourceByFullName(String name, boolean wantComp) {
String lcName = name.toLowerCase();
if (fontToFileMap.size() <= 1) {
name = jreDefaultFont;
}
FontResource fontResource = null;
String file = findFile(lcName);
if (file != null) {
fontResource = getFontResource(name, file);
if (fontResource != null) {
storeInMap(lcName, fontResource);
if (wantComp) {
fontResource =
new PrismCompositeFontResource(fontResource, lcName);
}
}
}
return fontResource;
}
FontResource getDefaultFontResource(boolean wantComp) {
FontResource fontResource = lookupResource(jreDefaultFontLC, wantComp);
if (fontResource == null) {
fontResource = createFontResource(jreDefaultFont,
jreFontDir+jreDefaultFontFile);
if (fontResource == null) {
for (String font : fontToFileMap.keySet()) {
String file = findFile(font);
fontResource = createFontResource(jreDefaultFontLC, file);
if (fontResource != null) {
break;
}
}
if (fontResource == null && isLinux) {
String path = FontConfigManager.getDefaultFontPath();
if (path != null) {
fontResource = createFontResource(jreDefaultFontLC,
path);
}
}
if (fontResource == null) {
return null;
}
}
storeInMap(jreDefaultFontLC, fontResource);
if (wantComp) {
fontResource =
new PrismCompositeFontResource(fontResource,
jreDefaultFontLC);
}
}
return fontResource;
}
private String findFile(String name) {
if (name.equals(jreDefaultFontLC)) {
return jreFontDir+jreDefaultFontFile;
}
getFullNameToFileMap();
String filename = fontToFileMap.get(name);
if (isWindows) {
filename = getPathNameWindows(filename);
}
return filename;
}
private static final String[] STR_ARRAY = new String[0];
private volatile HashMap<String,String> fontToFileMap = null;
private HashMap<String,String> fileToFontMap = null;
private HashMap<String,String> fontToFamilyNameMap = null;
private HashMap<String,ArrayList<String>> familyToFontListMap= null;
private static String sysFontDir = null;
private static String userFontDir = null;
private static native byte[] getFontPath();
private static native String regReadFontLink(String searchfont);
private static native String getEUDCFontFile();
private static void getPlatformFontDirs() {
if (userFontDir != null || sysFontDir != null) {
return;
}
byte [] pathBytes = getFontPath();
String path = new String(pathBytes);
int scIdx = path.indexOf(';');
if (scIdx < 0) {
sysFontDir = path;
} else {
sysFontDir = path.substring(0, scIdx);
userFontDir = path.substring(scIdx+1, path.length());
}
}
static ArrayList<String> [] getLinkedFonts(String searchFont,
boolean addSearchFont) {
ArrayList<String> [] fontRegInfo = new ArrayList[2];
fontRegInfo[0] = new ArrayList<String>();
fontRegInfo[1] = new ArrayList<String>();
if (isMacOSX) {
fontRegInfo[0].add("/Library/Fonts/Arial Unicode.ttf");
fontRegInfo[1].add("Arial Unicode MS");
fontRegInfo[0].add(jreFontDir + jreDefaultFontFile);
fontRegInfo[1].add(jreDefaultFont);
fontRegInfo[0].add("/System/Library/Fonts/Apple Symbols.ttf");
fontRegInfo[1].add("Apple Symbols");
fontRegInfo[0].add("/System/Library/Fonts/Apple Color Emoji.ttc");
fontRegInfo[1].add("Apple Color Emoji");
fontRegInfo[0].add("/System/Library/Fonts/STHeiti Light.ttf");
fontRegInfo[1].add("Heiti SC Light");
return fontRegInfo;
}
if (!isWindows) {
return fontRegInfo;
}
if (addSearchFont) {
fontRegInfo[0].add(null);
fontRegInfo[1].add(searchFont);
}
String fontRegBuf = regReadFontLink(searchFont);
if (fontRegBuf != null && fontRegBuf.length() > 0) {
String[] fontRegList = fontRegBuf.split("\u0000");
int linkListLen = fontRegList.length;
for (int i=0; i < linkListLen; i++) {
String[] splitFontData = fontRegList[i].split(",");
int len = splitFontData.length;
String file = getPathNameWindows(splitFontData[0]);
String name = (len > 1) ? splitFontData[1] : null;
if (name != null && fontRegInfo[1].contains(name)) {
continue;
} else if (name == null && fontRegInfo[0].contains(file)) {
continue;
}
fontRegInfo[0].add(file);
fontRegInfo[1].add(name);
}
}
String eudcFontFile = getEUDCFontFile();
if (eudcFontFile != null) {
fontRegInfo[0].add(eudcFontFile);
fontRegInfo[1].add(null);
}
fontRegInfo[0].add(jreFontDir + jreDefaultFontFile);
fontRegInfo[1].add(jreDefaultFont);
if (PlatformUtil.isWinVistaOrLater()) {
fontRegInfo[0].add(getPathNameWindows("mingliub.ttc"));
fontRegInfo[1].add("MingLiU-ExtB");
if (PlatformUtil.isWin7OrLater()) {
fontRegInfo[0].add(getPathNameWindows("seguisym.ttf"));
fontRegInfo[1].add("Segoe UI Symbol");
} else {
fontRegInfo[0].add(getPathNameWindows("cambria.ttc"));
fontRegInfo[1].add("Cambria Math");
}
}
return fontRegInfo;
}
private void resolveWindowsFonts
(HashMap<String,String> fontToFileMap,
HashMap<String,String> fontToFamilyNameMap,
HashMap<String,ArrayList<String>> familyToFontListMap) {
ArrayList<String> unmappedFontNames = null;
for (String font : fontToFamilyNameMap.keySet()) {
String file = fontToFileMap.get(font);
if (file == null) {
int dsi = font.indexOf("  ");
if (dsi > 0) {
String newName = font.substring(0, dsi);
newName = newName.concat(font.substring(dsi+1));
file = fontToFileMap.get(newName);
if (file != null &&
!fontToFamilyNameMap.containsKey(newName)) {
fontToFileMap.remove(newName);
fontToFileMap.put(font, file);
}
} else if (font.equals("marlett")) {
fontToFileMap.put(font, "marlett.ttf");
} else if (font.equals("david")) {
file = fontToFileMap.get("david regular");
if (file != null) {
fontToFileMap.remove("david regular");
fontToFileMap.put("david", file);
}
} else {
if (unmappedFontNames == null) {
unmappedFontNames = new ArrayList<String>();
}
unmappedFontNames.add(font);
}
}
}
if (unmappedFontNames != null) {
HashSet<String> unmappedFontFiles = new HashSet<String>();
HashMap<String,String> ffmapCopy = new HashMap<String,String>();
ffmapCopy.putAll(fontToFileMap);
for (String key : fontToFamilyNameMap.keySet()) {
ffmapCopy.remove(key);
}
for (String key : ffmapCopy.keySet()) {
unmappedFontFiles.add(ffmapCopy.get(key));
fontToFileMap.remove(key);
}
resolveFontFiles(unmappedFontFiles,
unmappedFontNames,
fontToFileMap,
fontToFamilyNameMap,
familyToFontListMap);
if (unmappedFontNames.size() > 0) {
int sz = unmappedFontNames.size();
for (int i=0; i<sz; i++) {
String name = unmappedFontNames.get(i);
String familyName = fontToFamilyNameMap.get(name);
if (familyName != null) {
ArrayList<String> family = familyToFontListMap.get(familyName);
if (family != null) {
if (family.size() <= 1) {
familyToFontListMap.remove(familyName);
}
}
}
fontToFamilyNameMap.remove(name);
}
}
}
}
private void resolveFontFiles(HashSet<String> unmappedFiles,
ArrayList<String> unmappedFonts,
HashMap<String,String> fontToFileMap,
HashMap<String,String> fontToFamilyNameMap,
HashMap<String,ArrayList<String>> familyToFontListMap) {
for (String file : unmappedFiles) {
try {
int fn = 0;
PrismFontFile ttf;
String fullPath = getPathNameWindows(file);
do {
ttf = createFontResource(fullPath, fn++);
if (ttf == null) {
break;
}
String fontNameLC = ttf.getFullName().toLowerCase();
String localeNameLC =ttf.getLocaleFullName().toLowerCase();
if (unmappedFonts.contains(fontNameLC) ||
unmappedFonts.contains(localeNameLC)) {
fontToFileMap.put(fontNameLC, file);
unmappedFonts.remove(fontNameLC);
if (unmappedFonts.contains(localeNameLC)) {
unmappedFonts.remove(localeNameLC);
String family = ttf.getFamilyName();
String familyLC = family.toLowerCase();
fontToFamilyNameMap.remove(localeNameLC);
fontToFamilyNameMap.put(fontNameLC, family);
ArrayList<String> familylist =
familyToFontListMap.get(familyLC);
if (familylist != null) {
familylist.remove(ttf.getLocaleFullName());
} else {
String localeFamilyLC =
ttf.getLocaleFamilyName().toLowerCase();
familylist =
familyToFontListMap.get(localeFamilyLC);
if (familylist != null) {
familyToFontListMap.remove(localeFamilyLC);
}
familylist = new ArrayList<String>();
familyToFontListMap.put(familyLC, familylist);
}
familylist.add(ttf.getFullName());
}
}
}
while (fn < ttf.getFontCount());
} catch (Exception e) {
if (debugFonts) {
e.printStackTrace();
}
}
}
}
static native void
populateFontFileNameMap(HashMap<String,String> fontToFileMap,
HashMap<String,String> fontToFamilyNameMap,
HashMap<String,ArrayList<String>>
familyToFontListMap,
Locale locale);
static String getPathNameWindows(final String filename) {
if (filename == null) {
return null;
}
getPlatformFontDirs();
File f = new File(filename);
if (f.isAbsolute()) {
return filename;
}
if (userFontDir == null) {
return sysFontDir+"\\"+filename;
}
@SuppressWarnings("removal")
String path = AccessController.doPrivileged(
new PrivilegedAction<String>() {
public String run() {
File f = new File(sysFontDir+"\\"+filename);
if (f.exists()) {
return f.getAbsolutePath();
}
else {
return userFontDir+"\\"+filename;
}
}
});
if (path != null) {
return path;
}
return null;
}
private static ArrayList<String> allFamilyNames;
public String[] getFontFamilyNames() {
if (allFamilyNames == null) {
ArrayList<String> familyNames = new ArrayList<String>();
LogicalFont.addFamilies(familyNames);
if (embeddedFonts != null) {
for (PrismFontFile embeddedFont : embeddedFonts.values()) {
if (!familyNames.contains(embeddedFont.getFamilyName()))
familyNames.add(embeddedFont.getFamilyName());
}
}
getFullNameToFileMap();
for (String f : fontToFamilyNameMap.values()) {
if (!familyNames.contains(f)) {
familyNames.add(f);
}
}
Collections.sort(familyNames);
allFamilyNames = new ArrayList<String>(familyNames);
}
return allFamilyNames.toArray(STR_ARRAY);
}
private static ArrayList<String> allFontNames;
public String[] getFontFullNames() {
if (allFontNames == null) {
ArrayList<String> fontNames = new ArrayList<String>();
LogicalFont.addFullNames(fontNames);
if (embeddedFonts != null) {
for (PrismFontFile embeddedFont : embeddedFonts.values()) {
if (!fontNames.contains(embeddedFont.getFullName())) {
fontNames.add(embeddedFont.getFullName());
}
}
}
getFullNameToFileMap();
for (ArrayList<String> a : familyToFontListMap.values()) {
for (String s : a) {
fontNames.add(s);
}
}
Collections.sort(fontNames);
allFontNames = fontNames;
}
return allFontNames.toArray(STR_ARRAY);
}
public String[] getFontFullNames(String family) {
String[] logFonts = LogicalFont.getFontsInFamily(family);
if (logFonts != null) {
return logFonts;
}
if (embeddedFonts != null) {
ArrayList<String> embeddedFamily = null;
for (PrismFontFile embeddedFont : embeddedFonts.values()) {
if (embeddedFont.getFamilyName().equalsIgnoreCase(family)) {
if (embeddedFamily == null) {
embeddedFamily = new ArrayList<String>();
}
embeddedFamily.add(embeddedFont.getFullName());
}
}
if (embeddedFamily != null) {
return embeddedFamily.toArray(STR_ARRAY);
}
}
getFullNameToFileMap();
family = family.toLowerCase();
ArrayList<String> familyFonts = familyToFontListMap.get(family);
if (familyFonts != null) {
return familyFonts.toArray(STR_ARRAY);
} else {
return STR_ARRAY;
}
}
public final int getSubPixelMode() {
return subPixelMode;
}
public boolean isLCDTextSupported() {
return lcdEnabled;
}
@Override
public boolean isPlatformFont(String name) {
if (name == null) return false;
String lcName = name.toLowerCase();
if (LogicalFont.isLogicalFont(lcName)) return true;
if (lcName.startsWith("lucida sans")) return true;
String systemFamily = getSystemFont(LogicalFont.SYSTEM).toLowerCase();
if (lcName.startsWith(systemFamily)) return true;
return false;
}
public static boolean isJreFont(FontResource fr) {
String file = fr.getFileName();
return file.startsWith(jreFontDir);
}
public static float getLCDContrast() {
if (lcdContrast == -1) {
if (isWindows) {
lcdContrast = getLCDContrastWin32() / 1000f;
} else {
lcdContrast = 1.3f;
}
}
return lcdContrast;
}
private static Thread fileCloser = null;
private synchronized void addFileCloserHook() {
if (fileCloser == null) {
final Runnable fileCloserRunnable = () -> {
if (embeddedFonts != null) {
for (PrismFontFile font : embeddedFonts.values()) {
font.disposeOnShutdown();
}
}
if (tmpFonts != null) {
for (WeakReference<PrismFontFile> ref : tmpFonts) {
PrismFontFile font = ref.get();
if (font != null) {
font.disposeOnShutdown();
}
}
}
};
@SuppressWarnings("removal")
var dummy = java.security.AccessController.doPrivileged(
(PrivilegedAction<Object>) () -> {
ThreadGroup tg = Thread.currentThread().getThreadGroup();
for (ThreadGroup tgn = tg;
tgn != null; tg = tgn, tgn = tg.getParent());
fileCloser = new Thread(tg, fileCloserRunnable);
fileCloser.setContextClassLoader(null);
Runtime.getRuntime().addShutdownHook(fileCloser);
return null;
}
);
}
}
private HashMap<String, PrismFontFile> embeddedFonts;
public PGFont[] loadEmbeddedFont(String name, InputStream fontStream,
float size,
boolean register,
boolean loadAll) {
if (!hasPermission()) {
return new PGFont[] { createFont(DEFAULT_FULLNAME, size) } ;
}
if (FontFileWriter.hasTempPermission()) {
return loadEmbeddedFont0(name, fontStream, size, register, loadAll);
}
FontFileWriter.FontTracker tracker =
FontFileWriter.FontTracker.getTracker();
boolean acquired = false;
try {
acquired = tracker.acquirePermit();
if (!acquired) {
return null;
}
return loadEmbeddedFont0(name, fontStream, size, register, loadAll);
} catch (InterruptedException e) {
return null;
} finally {
if (acquired) {
tracker.releasePermit();
}
}
}
private PGFont[] loadEmbeddedFont0(String name, InputStream fontStream,
float size,
boolean register,
boolean loadAll) {
PrismFontFile[] fr = null;
FontFileWriter fontWriter = new FontFileWriter();
try {
final File tFile = fontWriter.openFile();
byte[] buf = new byte[8192];
for (;;) {
int bytesRead = fontStream.read(buf);
if (bytesRead < 0) {
break;
}
fontWriter.writeBytes(buf, 0, bytesRead);
}
fontWriter.closeFile();
fr = loadEmbeddedFont1(name, tFile.getPath(), register, true,
fontWriter.isTracking(), loadAll);
if (fr != null && fr.length > 0) {
if (fr[0].isDecoded()) {
fontWriter.deleteFile();
}
}
addFileCloserHook();
} catch (Exception e) {
fontWriter.deleteFile();
} finally {
if (fr == null) {
fontWriter.deleteFile();
}
}
if (fr != null && fr.length > 0) {
if (size <= 0) size = getSystemFontSize();
int num = fr.length;
PrismFont[] pFonts = new PrismFont[num];
for (int i=0; i<num; i++) {
pFonts[i] = new PrismFont(fr[i], fr[i].getFullName(), size);
}
return pFonts;
}
return null;
}
public PGFont[] loadEmbeddedFont(String name, String path,
float size,
boolean register,
boolean loadAll) {
if (!hasPermission()) {
return new PGFont[] { createFont(DEFAULT_FULLNAME, size) };
}
addFileCloserHook();
FontResource[] frArr =
loadEmbeddedFont1(name, path, register, false, false, loadAll);
if (frArr != null && frArr.length > 0) {
if (size <= 0) size = getSystemFontSize();
int num = frArr.length;
PGFont[] pgFonts = new PGFont[num];
for (int i=0; i<num; i++) {
pgFonts[i] =
new PrismFont(frArr[i], frArr[i].getFullName(), size);
}
return pgFonts;
}
return null;
}
private void removeEmbeddedFont(String name) {
PrismFontFile font = embeddedFonts.get(name);
if (font == null) {
return;
}
embeddedFonts.remove(name);
String lcName = name.toLowerCase();
fontResourceMap.remove(lcName);
compResourceMap.remove(lcName);
Iterator<CompositeFontResource> fi = compResourceMap.values().iterator();
while (fi.hasNext()) {
CompositeFontResource compFont = fi.next();
if (compFont.getSlotResource(0) == font) {
fi.remove();
}
}
}
protected boolean registerEmbeddedFont(String path) {
return true;
}
private int numEmbeddedFonts = 0;
public int test_getNumEmbeddedFonts() {
return numEmbeddedFonts;
}
private synchronized
PrismFontFile[] loadEmbeddedFont1(String name, String path,
boolean register, boolean copy,
boolean tracked, boolean loadAll) {
++numEmbeddedFonts;
PrismFontFile[] frArr = createFontResources(name, path, register,
true, copy, tracked,
loadAll);
if (frArr == null || frArr.length == 0) {
return null;
}
if (embeddedFonts == null) {
embeddedFonts = new HashMap<String, PrismFontFile>();
}
boolean registerEmbedded = true;
for (int i=0; i<frArr.length; i++) {
PrismFontFile fr = frArr[i];
String family = fr.getFamilyName();
if (family == null || family.length() == 0) return null;
String fullname = fr.getFullName();
if (fullname == null || fullname.length() == 0) return null;
String psname = fr.getPSName();
if (psname == null || psname.length() == 0) return null;
FontResource resource = embeddedFonts.get(fullname);
if (resource != null && fr.equals(resource)) {
registerEmbedded = false;
}
}
if (registerEmbedded) {
if (!registerEmbeddedFont(frArr[0].getFileName())) {
return null;
}
}
if (copy && !frArr[0].isDecoded()) {
addTmpFont(frArr[0]);
}
if (!register) {
return frArr;
}
if (name != null && !name.isEmpty()) {
embeddedFonts.put(name, frArr[0]);
storeInMap(name, frArr[0]);
}
for (int i=0; i<frArr.length; i++) {
PrismFontFile fr = frArr[i];
String family = fr.getFamilyName();
String fullname = fr.getFullName();
removeEmbeddedFont(fullname);
embeddedFonts.put(fullname, fr);
storeInMap(fullname, fr);
family = family + dotStyleStr(fr.isBold(), fr.isItalic());
storeInMap(family, fr);
compResourceMap.remove(family.toLowerCase());
}
return frArr;
}
private void
logFontInfo(String message,
HashMap<String,String> fontToFileMap,
HashMap<String,String> fontToFamilyNameMap,
HashMap<String,ArrayList<String>> familyToFontListMap) {
System.err.println(message);
for (String keyName : fontToFileMap.keySet()) {
System.err.println("font="+keyName+" file="+
fontToFileMap.get(keyName));
}
for (String keyName : fontToFamilyNameMap.keySet()) {
System.err.println("font="+keyName+" family="+
fontToFamilyNameMap.get(keyName));
}
for (String keyName : familyToFontListMap.keySet()) {
System.err.println("family="+keyName+ " fonts="+
familyToFontListMap.get(keyName));
}
}
private synchronized HashMap<String,String> getFullNameToFileMap() {
if (fontToFileMap == null) {
HashMap<String, String>
tmpFontToFileMap = new HashMap<String,String>(100);
fontToFamilyNameMap = new HashMap<String,String>(100);
familyToFontListMap = new HashMap<String,ArrayList<String>>(50);
fileToFontMap = new HashMap<String,String>(100);
if (isWindows) {
getPlatformFontDirs();
populateFontFileNameMap(tmpFontToFileMap,
fontToFamilyNameMap,
familyToFontListMap,
Locale.ENGLISH);
if (debugFonts) {
System.err.println("Windows Locale ID=" + getSystemLCID());
logFontInfo(" *** WINDOWS FONTS BEFORE RESOLVING",
tmpFontToFileMap,
fontToFamilyNameMap,
familyToFontListMap);
}
resolveWindowsFonts(tmpFontToFileMap,
fontToFamilyNameMap,
familyToFontListMap);
if (debugFonts) {
logFontInfo(" *** WINDOWS FONTS AFTER RESOLVING",
tmpFontToFileMap,
fontToFamilyNameMap,
familyToFontListMap);
}
} else if (isMacOSX || isIOS) {
MacFontFinder.populateFontFileNameMap(tmpFontToFileMap,
fontToFamilyNameMap,
familyToFontListMap,
Locale.ENGLISH);
} else if (isLinux) {
FontConfigManager.populateMaps(tmpFontToFileMap,
fontToFamilyNameMap,
familyToFontListMap,
Locale.getDefault());
if (debugFonts) {
logFontInfo(" *** FONTCONFIG LOCATED FONTS:",
tmpFontToFileMap,
fontToFamilyNameMap,
familyToFontListMap);
}
} else if (isAndroid) {
AndroidFontFinder.populateFontFileNameMap(tmpFontToFileMap,
fontToFamilyNameMap,
familyToFontListMap,
Locale.ENGLISH);
} else {
fontToFileMap = tmpFontToFileMap;
return fontToFileMap;
}
for (String font : tmpFontToFileMap.keySet()) {
String file = tmpFontToFileMap.get(font);
fileToFontMap.put(file.toLowerCase(), font);
}
fontToFileMap = tmpFontToFileMap;
if (isAndroid) {
populateFontFileNameMapGeneric(
AndroidFontFinder.getSystemFontsDir());
}
populateFontFileNameMapGeneric(jreFontDir);
}
return fontToFileMap;
}
@SuppressWarnings("removal")
public final boolean hasPermission() {
try {
SecurityManager sm = System.getSecurityManager();
if (sm != null) {
sm.checkPermission(LOAD_FONT_PERMISSION);
}
return true;
} catch (SecurityException ex) {
return false;
}
}
private static class TTFilter implements FilenameFilter {
public boolean accept(File dir,String name) {
int offset = name.length()-4;
if (offset <= 0) {
return false;
} else {
return(name.startsWith(".ttf", offset) ||
name.startsWith(".TTF", offset) ||
name.startsWith(".ttc", offset) ||
name.startsWith(".TTC", offset) ||
name.startsWith(".otf", offset) ||
name.startsWith(".OTF", offset));
}
}
private TTFilter() {
}
static TTFilter ttFilter;
static TTFilter getInstance() {
if (ttFilter == null) {
ttFilter = new TTFilter();
}
return ttFilter;
}
}
void addToMaps(PrismFontFile fr) {
if (fr == null) {
return;
}
String fullName = fr.getFullName();
String familyName = fr.getFamilyName();
if (fullName == null || familyName == null) {
return;
}
String lcFullName = fullName.toLowerCase();
String lcFamilyName = familyName.toLowerCase();
fontToFileMap.put(lcFullName, fr.getFileName());
fontToFamilyNameMap.put(lcFullName, familyName);
ArrayList<String> familyList = familyToFontListMap.get(lcFamilyName);
if (familyList == null) {
familyList = new ArrayList<String>();
familyToFontListMap.put(lcFamilyName, familyList);
}
familyList.add(fullName);
}
void populateFontFileNameMapGeneric(String fontDir) {
final File dir = new File(fontDir);
String[] files = null;
try {
@SuppressWarnings("removal")
String[] tmp = AccessController.doPrivileged(
(PrivilegedExceptionAction<String[]>) () -> dir.list(TTFilter.getInstance())
);
files = tmp;
} catch (Exception e) {
}
if (files == null) {
return;
}
for (int i=0;i<files.length;i++) {
try {
String path = fontDir+File.separator+files[i];
if (!registerEmbeddedFont(path)) {
continue;
}
int index = 0;
PrismFontFile fr = createFontResource(path, index++);
if (fr == null) {
continue;
}
addToMaps(fr);
while (index < fr.getFontCount()) {
fr = createFontResource(path, index++);
if (fr == null) {
break;
}
addToMaps(fr);
}
} catch (Exception e) {
}
}
}
static native int getLCDContrastWin32();
private static native float getSystemFontSizeNative();
private static native String getSystemFontNative();
private static float systemFontSize;
private static String systemFontFamily = null;
private static String monospaceFontFamily = null;
public static float getSystemFontSize() {
if (systemFontSize == -1) {
if (isWindows) {
systemFontSize = getSystemFontSizeNative();
} else if (isMacOSX || isIOS) {
systemFontSize = MacFontFinder.getSystemFontSize();
} else if (isAndroid) {
systemFontSize = AndroidFontFinder.getSystemFontSize();
} else if (isEmbedded) {
try {
int screenDPI = Screen.getMainScreen().getResolutionY();
systemFontSize = ((float) screenDPI) / 6f;
} catch (NullPointerException npe) {
systemFontSize = 13f;
}
} else {
systemFontSize = 13f;
}
}
return systemFontSize;
}
public static String getSystemFont(String name) {
if (name.equals(LogicalFont.SYSTEM)) {
if (systemFontFamily == null) {
if (isWindows) {
systemFontFamily = getSystemFontNative();
if (systemFontFamily == null) {
systemFontFamily = "Arial";
}
} else if (isMacOSX || isIOS) {
systemFontFamily = MacFontFinder.getSystemFont();
if (systemFontFamily == null) {
systemFontFamily = "Lucida Grande";
}
} else if (isAndroid) {
systemFontFamily = AndroidFontFinder.getSystemFont();
} else {
systemFontFamily = "Lucida Sans";
}
}
return systemFontFamily;
} else if (name.equals(LogicalFont.SANS_SERIF)) {
return "Arial";
} else if (name.equals(LogicalFont.SERIF)) {
return "Times New Roman";
} else {
if (monospaceFontFamily == null) {
if (isMacOSX) {
}
}
if (monospaceFontFamily == null) {
monospaceFontFamily = "Courier New";
}
return monospaceFontFamily;
}
}
static native short getSystemLCID();
}
