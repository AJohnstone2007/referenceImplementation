package com.sun.javafx.font;
import java.util.Map;
class WindowsFontMap {
private static class FamilyDescription {
private String familyName;
private String plainFullName;
private String boldFullName;
private String italicFullName;
private String boldItalicFullName;
private String plainFileName;
private String boldFileName;
private String italicFileName;
private String boldItalicFileName;
}
private static Map<String, FamilyDescription> PLATFORM_FONT_MAP;
private static void populateHardcodedFileNameMap() {
var segoeFD = new FamilyDescription();
segoeFD.familyName = "Segoe UI";
segoeFD.plainFullName = "Segoe UI";
segoeFD.plainFileName = "segoeui.ttf";
segoeFD.boldFullName = "Segoe UI Bold";
segoeFD.boldFileName = "segoeuib.ttf";
segoeFD.italicFullName = "Segoe UI Italic";
segoeFD.italicFileName = "segoeuii.ttf";
segoeFD.boldItalicFullName = "Segoe UI Bold Italic";
segoeFD.boldItalicFileName = "segoeuiz.ttf";
var tahomaFD = new FamilyDescription();
tahomaFD.familyName = "Tahoma";
tahomaFD.plainFullName = "Tahoma";
tahomaFD.plainFileName = "tahoma.ttf";
tahomaFD.boldFullName = "Tahoma Bold";
tahomaFD.boldFileName = "tahomabd.ttf";
var verdanaFD = new FamilyDescription();
verdanaFD.familyName = "Verdana";
verdanaFD.plainFullName = "Verdana";
verdanaFD.plainFileName = "verdana.TTF";
verdanaFD.boldFullName = "Verdana Bold";
verdanaFD.boldFileName = "verdanab.TTF";
verdanaFD.italicFullName = "Verdana Italic";
verdanaFD.italicFileName = "verdanai.TTF";
verdanaFD.boldItalicFullName = "Verdana Bold Italic";
verdanaFD.boldItalicFileName = "verdanaz.TTF";
var arialFD = new FamilyDescription();
arialFD.familyName = "Arial";
arialFD.plainFullName = "Arial";
arialFD.plainFileName = "ARIAL.TTF";
arialFD.boldFullName = "Arial Bold";
arialFD.boldFileName = "ARIALBD.TTF";
arialFD.italicFullName = "Arial Italic";
arialFD.italicFileName = "ARIALI.TTF";
arialFD.boldItalicFullName = "Arial Bold Italic";
arialFD.boldItalicFileName = "ARIALBI.TTF";
var timesFD = new FamilyDescription();
timesFD.familyName = "Times New Roman";
timesFD.plainFullName = "Times New Roman";
timesFD.plainFileName = "times.ttf";
timesFD.boldFullName = "Times New Roman Bold";
timesFD.boldFileName = "timesbd.ttf";
timesFD.italicFullName = "Times New Roman Italic";
timesFD.italicFileName = "timesi.ttf";
timesFD.boldItalicFullName = "Times New Roman Bold Italic";
timesFD.boldItalicFileName = "timesbi.ttf";
var courierFD = new FamilyDescription();
courierFD.familyName = "Courier New";
courierFD.plainFullName = "Courier New";
courierFD.plainFileName = "cour.ttf";
courierFD.boldFullName = "Courier New Bold";
courierFD.boldFileName = "courbd.ttf";
courierFD.italicFullName = "Courier New Italic";
courierFD.italicFileName = "couri.ttf";
courierFD.boldItalicFullName = "Courier New Bold Italic";
courierFD.boldItalicFileName = "courbi.ttf";
PLATFORM_FONT_MAP = Map.of(
"segoe", segoeFD,
"tahoma", tahomaFD,
"verdana", verdanaFD,
"arial", arialFD,
"times", timesFD,
"courier", courierFD);
}
static String getPathName(String filename) {
return PrismFontFactory.getPathNameWindows(filename);
}
static String findFontFile(String lcName, int style) {
if (PLATFORM_FONT_MAP == null) {
populateHardcodedFileNameMap();
}
if (PLATFORM_FONT_MAP == null || PLATFORM_FONT_MAP.size() == 0) {
return null;
}
int spaceIndex = lcName.indexOf(' ');
String firstWord = lcName;
if (spaceIndex > 0) {
firstWord = lcName.substring(0, spaceIndex);
}
FamilyDescription fd = PLATFORM_FONT_MAP.get(firstWord);
if (fd == null) {
return null;
}
String file = null;
if (style < 0) {
if (lcName.equalsIgnoreCase(fd.plainFullName)) {
file = fd.plainFileName;
} else if (lcName.equalsIgnoreCase(fd.boldFullName)) {
file = fd.boldFileName;
} else if (lcName.equalsIgnoreCase(fd.italicFullName)) {
file = fd.italicFileName;
} else if (lcName.equalsIgnoreCase(fd.boldItalicFullName)) {
file = fd.boldItalicFileName;
}
if (file != null) {
return getPathName(file);
} else {
return null;
}
} else if (!lcName.equalsIgnoreCase(fd.familyName)) {
return null;
}
switch (style) {
case 0 :
file = fd.plainFileName;
break;
case 1 :
file = fd.boldFileName;
if (file == null) {
file = fd.plainFileName;
}
break;
case 2 :
file = fd.italicFileName;
if (file == null) {
file = fd.plainFileName;
}
break;
case 3:
file = fd.boldItalicFileName;
if (file == null) {
file = fd.italicFileName;
}
if (file == null) {
file = fd.boldFileName;
}
if (file == null) {
file = fd.plainFileName;
}
break;
}
if (file != null) {
return getPathName(file);
} else {
return null;
}
}
}
