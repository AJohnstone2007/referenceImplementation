package com.sun.javafx.font.directwrite;
import com.sun.javafx.geom.Path2D;
class IDWriteFontFace extends IUnknown {
IDWriteFontFace(long ptr) {
super(ptr);
}
DWRITE_GLYPH_METRICS GetDesignGlyphMetrics(short glyphIndex, boolean isSideways) {
return OS.GetDesignGlyphMetrics(ptr, glyphIndex, isSideways);
}
Path2D GetGlyphRunOutline(float emSize, short glyphIndex, boolean isSideways) {
return OS.GetGlyphRunOutline(ptr, emSize, glyphIndex, isSideways);
}
}
