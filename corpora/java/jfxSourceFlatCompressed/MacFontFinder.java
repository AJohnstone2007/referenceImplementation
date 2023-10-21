package com.sun.javafx.font;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import com.sun.glass.utils.NativeLibLoader;
class MacFontFinder {
static {
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged(
(PrivilegedAction<Void>) () -> {
NativeLibLoader.loadLibrary("javafx_font");
return null;
}
);
}
private static final int SystemFontType = 2;
private static final int MonospacedFontType = 1;
private native static String getFont(int type);
public static String getSystemFont() {
return getFont(SystemFontType);
}
public static String getMonospacedFont() {
return getFont(MonospacedFontType);
}
native static float getSystemFontSize();
public static boolean populateFontFileNameMap(
HashMap<String,String> fontToFileMap,
HashMap<String,String> fontToFamilyNameMap,
HashMap<String,ArrayList<String>> familyToFontListMap,
Locale locale) {
if (fontToFileMap == null ||
fontToFamilyNameMap == null ||
familyToFontListMap == null) {
return false;
}
if (locale == null) {
locale = Locale.ENGLISH;
}
String[] fontData = getFontData();
if (fontData == null) return false;
int i = 0;
while (i < fontData.length) {
String name = fontData[i++];
String family = fontData[i++];
String file = fontData[i++];
if (PrismFontFactory.debugFonts) {
System.err.println("[MacFontFinder] Name=" + name);
System.err.println("\tFamily=" + family);
System.err.println("\tFile=" + file);
}
if (name == null || family == null || file == null) {
continue;
}
String lcName = name.toLowerCase(locale);
String lcFamily = family.toLowerCase(locale);
fontToFileMap.put(lcName, file);
fontToFamilyNameMap.put(lcName, family);
ArrayList<String> list = familyToFontListMap.get(lcFamily);
if (list == null) {
list = new ArrayList<String>();
familyToFontListMap.put(lcFamily, list);
}
list.add(name);
}
return true;
}
private native static String[] getFontData();
}
