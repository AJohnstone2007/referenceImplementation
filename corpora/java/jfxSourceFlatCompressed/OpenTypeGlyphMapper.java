package com.sun.javafx.font;
public class OpenTypeGlyphMapper extends CharToGlyphMapper {
PrismFontFile font;
CMap cmap;
public OpenTypeGlyphMapper(PrismFontFile font) {
this.font = font;
try {
cmap = CMap.initialize(font);
} catch (Exception e) {
cmap = null;
}
if (cmap == null) {
handleBadCMAP();
}
missingGlyph = 0;
}
public int getGlyphCode(int charCode) {
try {
return cmap.getGlyph(charCode);
} catch(Exception e) {
handleBadCMAP();
return missingGlyph;
}
}
private void handleBadCMAP() {
cmap = CMap.theNullCmap;
}
boolean hasSupplementaryChars() {
return
cmap instanceof CMap.CMapFormat8 ||
cmap instanceof CMap.CMapFormat10 ||
cmap instanceof CMap.CMapFormat12;
}
}
