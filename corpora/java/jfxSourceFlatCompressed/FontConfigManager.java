package com.sun.javafx.font;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
class FontConfigManager {
static boolean debugFonts = false;
static boolean useFontConfig = true;
static boolean fontConfigFailed = false;
static boolean useEmbeddedFontSupport = false;
static {
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged(
(PrivilegedAction<Void>) () -> {
String dbg = System.getProperty("prism.debugfonts", "");
debugFonts = "true".equals(dbg);
String ufc = System.getProperty("prism.useFontConfig", "true");
useFontConfig = "true".equals(ufc);
String emb = System.getProperty("prism.embeddedfonts", "");
useEmbeddedFontSupport = "true".equals(emb);
return null;
}
);
}
public static class FontConfigFont {
public String familyName;
public String styleStr;
public String fullName;
public String fontFile;
}
public static class FcCompFont {
public String fcName;
public String fcFamily;
public int style;
public FontConfigFont firstFont;
public FontConfigFont[] allFonts;
}
private static final String[] fontConfigNames = {
"sans:regular:roman",
"sans:bold:roman",
"sans:regular:italic",
"sans:bold:italic",
"serif:regular:roman",
"serif:bold:roman",
"serif:regular:italic",
"serif:bold:italic",
"monospace:regular:roman",
"monospace:bold:roman",
"monospace:regular:italic",
"monospace:bold:italic",
};
private static FcCompFont[] fontConfigFonts;
private FontConfigManager() {
}
private static String[] getFontConfigNames() {
return fontConfigNames;
}
private static String getFCLocaleStr() {
Locale l = Locale.getDefault();
String localeStr = l.getLanguage();
String country = l.getCountry();
if (!country.equals("")) {
localeStr = localeStr + "-" + country;
}
return localeStr;
}
private static native boolean getFontConfig(String locale,
FcCompFont[] fonts,
boolean includeFallbacks);
private static synchronized void initFontConfigLogFonts() {
if (fontConfigFonts != null || fontConfigFailed) {
return;
}
long t0 = 0;
if (debugFonts) {
t0 = System.nanoTime();
}
String[] fontConfigNames = FontConfigManager.getFontConfigNames();
FcCompFont[] fontArr = new FcCompFont[fontConfigNames.length];
for (int i = 0; i< fontArr.length; i++) {
fontArr[i] = new FcCompFont();
fontArr[i].fcName = fontConfigNames[i];
int colonPos = fontArr[i].fcName.indexOf(':');
fontArr[i].fcFamily = fontArr[i].fcName.substring(0, colonPos);
fontArr[i].style = i % 4;
}
boolean foundFontConfig = false;
if (useFontConfig) {
foundFontConfig = getFontConfig(getFCLocaleStr(), fontArr, true);
} else {
if (debugFonts) {
System.err.println("Not using FontConfig");
}
}
if (useEmbeddedFontSupport ||
!foundFontConfig)
{
EmbeddedFontSupport.initLogicalFonts(fontArr);
}
FontConfigFont anyFont = null;
for (int i = 0; i< fontArr.length; i++) {
FcCompFont fci = fontArr[i];
if (fci.firstFont == null) {
if (debugFonts) {
System.err.println("Fontconfig returned no font for " +
fontArr[i].fcName);
}
fontConfigFailed = true;
} else if (anyFont == null) {
anyFont = fci.firstFont;
defaultFontFile = anyFont.fontFile;
}
}
if (anyFont == null) {
fontConfigFailed = true;
System.err.println("Error: JavaFX detected no fonts! " +
"Please refer to release notes for proper font configuration");
return;
} else if (fontConfigFailed) {
for (int i = 0; i< fontArr.length; i++) {
if (fontArr[i].firstFont == null) {
fontArr[i].firstFont = anyFont;
}
}
}
fontConfigFonts = fontArr;
if (debugFonts) {
long t1 = System.nanoTime();
System.err.println("Time spent accessing fontconfig="
+ ((t1 - t0) / 1000000) + "ms.");
for (int i = 0; i<fontConfigFonts.length; i++) {
FcCompFont fci = fontConfigFonts[i];
System.err.println("FC font " + fci.fcName+" maps to " +
fci.firstFont.fullName +
" in file " + fci.firstFont.fontFile);
if (fci.allFonts != null) {
for (int f=0;f<fci.allFonts.length;f++) {
FontConfigFont fcf = fci.allFonts[f];
System.err.println(" "+f+ ") Family=" +
fcf.familyName +
", Style="+ fcf.styleStr +
", Fullname="+fcf.fullName +
", File="+fcf.fontFile);
}
}
}
}
}
private static native boolean populateMapsNative
(HashMap<String,String> fontToFileMap,
HashMap<String,String> fontToFamilyNameMap,
HashMap<String,ArrayList<String>> familyToFontListMap,
Locale locale);
public static void populateMaps
(HashMap<String,String> fontToFileMap,
HashMap<String,String> fontToFamilyNameMap,
HashMap<String,ArrayList<String>> familyToFontListMap,
Locale locale) {
boolean pnm = false;
if (useFontConfig && !fontConfigFailed) {
pnm = populateMapsNative(fontToFileMap, fontToFamilyNameMap,
familyToFontListMap, locale);
}
if (fontConfigFailed ||
useEmbeddedFontSupport ||
!pnm) {
EmbeddedFontSupport.populateMaps(fontToFileMap,
fontToFamilyNameMap,
familyToFontListMap, locale);
}
}
private static String mapFxToFcLogicalFamilyName(String fxName) {
if (fxName.equals("serif")) {
return "serif";
} else if (fxName.equals("monospaced")) {
return "monospace";
} else {
return "sans";
}
}
public static FcCompFont getFontConfigFont(String fxFamilyName,
boolean bold, boolean italic) {
initFontConfigLogFonts();
if (fontConfigFonts == null) {
return null;
}
String name = mapFxToFcLogicalFamilyName(fxFamilyName.toLowerCase());
int style = (bold) ? 1 : 0;
if (italic) {
style +=2;
}
FcCompFont fcInfo = null;
for (int i=0; i<fontConfigFonts.length; i++) {
if (name.equals(fontConfigFonts[i].fcFamily) &&
style == fontConfigFonts[i].style) {
fcInfo = fontConfigFonts[i];
break;
}
}
if (fcInfo == null) {
fcInfo = fontConfigFonts[0];
}
if (debugFonts) {
System.err.println("FC name=" + name + " style=" + style +
" uses " + fcInfo.firstFont.fullName +
" in file: " + fcInfo.firstFont.fontFile);
}
return fcInfo;
}
private static String defaultFontFile;
public static String getDefaultFontPath() {
if (fontConfigFonts == null && !fontConfigFailed) {
getFontConfigFont("System", false, false);
}
return defaultFontFile;
}
public static ArrayList<String>
getFileNames(FcCompFont font, boolean fallBacksOnly) {
ArrayList fileList = new ArrayList<String>();
if (font.allFonts != null) {
int start = (fallBacksOnly) ? 1 : 0;
for (int i=start; i<font.allFonts.length; i++) {
fileList.add(font.allFonts[i].fontFile);
}
}
return fileList;
}
public static ArrayList<String>
getFontNames(FcCompFont font, boolean fallBacksOnly) {
ArrayList fontList = new ArrayList<String>();
if (font.allFonts != null) {
int start = (fallBacksOnly) ? 1 : 0;
for (int i=start; i<font.allFonts.length; i++) {
fontList.add(font.allFonts[i].fullName);
}
}
return fontList;
}
private static class EmbeddedFontSupport {
private static String fontDirProp = null;
private static String fontDir;
private static boolean fontDirFromJRE = false;
static {
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged(
(PrivilegedAction<Void>) () -> {
initEmbeddedFonts();
return null;
}
);
}
private static void initEmbeddedFonts() {
fontDirProp = System.getProperty("prism.fontdir");
if (fontDirProp != null) {
fontDir = fontDirProp;
} else {
try {
final String javaHome = System.getProperty("java.home");
if (javaHome == null) {
return;
}
File fontDirectory = new File(javaHome, "lib/fonts");
if (fontDirectory.exists()) {
fontDirFromJRE = true;
fontDir = fontDirectory.getPath();
}
if (debugFonts) {
System.err.println("Fallback fontDir is " + fontDirectory +
" exists = " +
fontDirectory.exists());
}
} catch (Exception e) {
if (debugFonts) {
e.printStackTrace();
}
fontDir = "/";
}
}
}
private static String getStyleStr(int style) {
switch (style) {
case 0 : return "regular";
case 1 : return "bold";
case 2 : return "italic";
case 3 : return "bolditalic";
default : return "regular";
}
}
@SuppressWarnings("removal")
private static boolean exists(final File f) {
return AccessController.doPrivileged(
(PrivilegedAction<Boolean>) () -> f.exists()
);
}
static String[] jreFontsProperties = {
"sans.regular.0.font", "Lucida Sans Regular",
"sans.regular.0.file", "LucidaSansRegular.ttf",
"sans.bold.0.font", "Lucida Sans Bold",
"sans.bold.0.file", "LucidaSansDemiBold.ttf",
"monospace.regular.0.font", "Lucida Typewriter Regular",
"monospace.regular.0.file", "LucidaTypewriterRegular.ttf",
"monospace.bold.0.font", "Lucida Typewriter Bold",
"monospace.bold.0.file", "LucidaTypewriterBold.ttf",
"serif.regular.0.font", "Lucida Bright",
"serif.regular.0.file", "LucidaBrightRegular.ttf",
"serif.bold.0.font", "Lucida Bright Demibold",
"serif.bold.0.file", "LucidaBrightDemiBold.ttf",
"serif.italic.0.font", "Lucida Bright Italic",
"serif.italic.0.file", "LucidaBrightItalic.ttf",
"serif.bolditalic.0.font", "Lucida Bright Demibold Italic",
"serif.bolditalic.0.file", "LucidaBrightDemiItalic.ttf"
};
static void initLogicalFonts(FcCompFont[] fonts) {
Properties props = new Properties();
try {
File f = new File(fontDir,"logicalfonts.properties");
if (f.exists()) {
FileInputStream fis = new FileInputStream(f);
props.load(fis);
fis.close();
} else if (fontDirFromJRE) {
for(int i=0; i < jreFontsProperties.length; i += 2) {
props.setProperty(jreFontsProperties[i],jreFontsProperties[i+1]);
}
if (debugFonts) {
System.err.println("Using fallback implied logicalfonts.properties");
}
}
} catch (IOException ioe) {
if (debugFonts) {
System.err.println(ioe);
return;
}
}
for (int f=0; f<fonts.length; f++) {
String fcFamily = fonts[f].fcFamily;
String styleStr = getStyleStr(fonts[f].style);
String key = fcFamily+"."+styleStr+".";
ArrayList<FontConfigFont> allFonts =
new ArrayList<FontConfigFont>();
int i=0;
while (true) {
String file = props.getProperty(key+i+".file");
String font = props.getProperty(key+i+".font");
i++;
if (file == null) {
break;
}
File ff = new File(fontDir, file);
if (!exists(ff)) {
if (debugFonts) {
System.out.println("Failed to find logical font file "+ff);
}
continue;
}
FontConfigFont fcFont = new FontConfigFont();
fcFont.fontFile = ff.getPath();
fcFont.fullName = font;
fcFont.familyName = null;
fcFont.styleStr = null;
if (fonts[f].firstFont == null) {
fonts[f].firstFont = fcFont;
}
allFonts.add(fcFont);
}
if (allFonts.size() > 0) {
fonts[f].allFonts = new FontConfigFont[allFonts.size()];
allFonts.toArray(fonts[f].allFonts);
}
}
}
static void populateMaps
(HashMap<String,String> fontToFileMap,
HashMap<String,String> fontToFamilyNameMap,
HashMap<String,ArrayList<String>> familyToFontListMap,
Locale locale)
{
final Properties props = new Properties();
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged(
(PrivilegedAction<Void>) () -> {
try {
String lFile = fontDir+"/allfonts.properties";
FileInputStream fis = new FileInputStream(lFile);
props.load(fis);
fis.close();
} catch (IOException ioe) {
props.clear();
if (debugFonts) {
System.err.println(ioe);
System.err.println("Fall back to opening the files");
}
}
return null;
}
);
if (!props.isEmpty()) {
int maxFont = Integer.MAX_VALUE;
try {
maxFont = Integer.parseInt(props.getProperty("maxFont",""));
} catch (NumberFormatException e) {
}
if (maxFont <= 0) {
maxFont = Integer.MAX_VALUE;
}
for (int f=0; f<maxFont; f++) {
String family = props.getProperty("family."+f);
String font = props.getProperty("font."+f);
String file = props.getProperty("file."+f);
if (file == null) {
break;
}
File ff = new File(fontDir, file);
if (!exists(ff)) {
continue;
}
if (family == null || font == null) {
continue;
}
String fontLC = font.toLowerCase(Locale.ENGLISH);
String familyLC = family.toLowerCase(Locale.ENGLISH);
fontToFileMap.put(fontLC, ff.getPath());
fontToFamilyNameMap.put(fontLC, family);
ArrayList<String> familyArr =
familyToFontListMap.get(familyLC);
if (familyArr == null) {
familyArr = new ArrayList<String>(4);
familyToFontListMap.put(familyLC, familyArr);
}
familyArr.add(font);
}
}
}
}
}
