package com.sun.javafx.font;
public abstract class CharToGlyphMapper {
public static final int HI_SURROGATE_SHIFT = 10;
public static final int HI_SURROGATE_START = 0xD800;
public static final int HI_SURROGATE_END = 0xDBFF;
public static final int LO_SURROGATE_START = 0xDC00;
public static final int LO_SURROGATE_END = 0xDFFF;
public static final int SURROGATES_START = 0x10000;
public static final int MISSING_GLYPH = 0;
public static final int INVISIBLE_GLYPH_ID = 0xffff;
protected int missingGlyph = MISSING_GLYPH;
public boolean canDisplay(char cp) {
int glyph = charToGlyph(cp);
return glyph != missingGlyph;
}
public int getMissingGlyphCode() {
return missingGlyph;
}
public abstract int getGlyphCode(int charCode);
public int charToGlyph(char unicode) {
return getGlyphCode(unicode);
}
public int charToGlyph(int unicode) {
return getGlyphCode(unicode);
}
public void charsToGlyphs(int start, int count, char[] unicodes,
int[] glyphs, int glyphStart) {
for (int i=0; i<count; i++) {
int code = unicodes[start + i];
if (code >= HI_SURROGATE_START &&
code <= HI_SURROGATE_END && i + 1 < count) {
char low = unicodes[start + i + 1];
if (low >= LO_SURROGATE_START &&
low <= LO_SURROGATE_END) {
code = ((code - HI_SURROGATE_START) << HI_SURROGATE_SHIFT) +
low - LO_SURROGATE_START + SURROGATES_START;
glyphs[glyphStart + i] = getGlyphCode(code);
i += 1;
glyphs[glyphStart + i] = INVISIBLE_GLYPH_ID;
continue;
}
}
glyphs[glyphStart + i] = getGlyphCode(code);
}
}
public void charsToGlyphs(int start, int count, char[] unicodes, int[] glyphs) {
charsToGlyphs(start, count, unicodes, glyphs, 0);
}
public void charsToGlyphs(int count, char[] unicodes, int[] glyphs) {
charsToGlyphs(0, count, unicodes, glyphs, 0);
}
}
