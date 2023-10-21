package com.sun.javafx.font.directwrite;
import com.sun.javafx.font.Disposer;
import com.sun.javafx.font.FontStrikeDesc;
import com.sun.javafx.font.PrismFontFactory;
import com.sun.javafx.font.PrismFontFile;
import com.sun.javafx.font.PrismFontStrike;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
class DWFontFile extends PrismFontFile {
private IDWriteFontFace fontFace;
private DWDisposer disposer;
DWFontFile(String name, String filename, int fIndex, boolean register,
boolean embedded, boolean copy, boolean tracked) throws Exception {
super(name, filename, fIndex, register, embedded, copy, tracked);
fontFace = createFontFace();
if (PrismFontFactory.debugFonts) {
if (fontFace == null) {
System.err.println("Failed to create IDWriteFontFace for " + this);
}
}
if (copy) {
disposer = new DWDisposer(fontFace);
Disposer.addRecord(this, disposer);
}
}
private IDWriteFontFace createEmbeddedFontFace() {
IDWriteFactory factory = DWFactory.getDWriteFactory();
IDWriteFontFile fontFile = factory.CreateFontFileReference(getFileName());
if (fontFile == null) return null;
boolean[] isSupportedFontType = new boolean[1];
int[] fontFileType = new int[1];
int[] fontFaceType = new int[1];
int[] numberOfFaces = new int[1];
int hr = fontFile.Analyze(isSupportedFontType, fontFileType, fontFaceType, numberOfFaces);
IDWriteFontFace face = null;
if (hr == OS.S_OK && isSupportedFontType[0]) {
int faceIndex = getFontIndex();
int simulation = OS.DWRITE_FONT_SIMULATIONS_NONE;
face = factory.CreateFontFace(fontFaceType[0], fontFile, faceIndex, simulation);
}
fontFile.Release();
return face;
}
private IDWriteFontFace createFontFace() {
if (isEmbeddedFont()) {
return createEmbeddedFontFace();
}
IDWriteFontCollection collection = DWFactory.getFontCollection();
int index = collection.FindFamilyName(getFamilyName());
if (index == -1) {
return createEmbeddedFontFace();
}
IDWriteFontFamily family = collection.GetFontFamily(index);
if (family == null) return null;
int weight = isBold() ? OS.DWRITE_FONT_WEIGHT_BOLD :
OS.DWRITE_FONT_WEIGHT_NORMAL;
int stretch = OS.DWRITE_FONT_STRETCH_NORMAL;
int style = isItalic() ? OS.DWRITE_FONT_STYLE_ITALIC :
OS.DWRITE_FONT_STYLE_NORMAL;
IDWriteFont font = family.GetFirstMatchingFont(weight, stretch, style);
family.Release();
if (font == null) return null;
IDWriteFontFace face = font.CreateFontFace();
font.Release();
return face;
}
IDWriteFontFace getFontFace() {
return fontFace;
}
Path2D getGlyphOutline(int gc, float size) {
if (fontFace == null) return null;
if (size == 0) return new Path2D();
return fontFace.GetGlyphRunOutline(size, (short)gc, false);
}
RectBounds getBBox(int glyphCode, float size) {
float[] bb = new float[4];
getGlyphBoundingBox(glyphCode, size, bb);
return new RectBounds(bb[0], bb[1], bb[2], bb[3]);
}
@Override protected int[] createGlyphBoundingBox(int gc) {
if (fontFace == null) return null;
DWRITE_GLYPH_METRICS metrics = fontFace.GetDesignGlyphMetrics((short)gc, false);
if (metrics == null) return null;
int[] bb = new int[4];
bb[0] = metrics.leftSideBearing;
bb[1] = metrics.verticalOriginY - metrics.advanceHeight + metrics.bottomSideBearing;
bb[2] = metrics.advanceWidth - metrics.rightSideBearing;
bb[3] = metrics.verticalOriginY - metrics.topSideBearing;
return bb;
}
@Override
protected PrismFontStrike<DWFontFile> createStrike(float size, BaseTransform transform,
int aaMode, FontStrikeDesc desc) {
return new DWFontStrike(this, size, transform, aaMode, desc);
}
@Override
protected synchronized void disposeOnShutdown() {
if (fontFace != null) {
if (disposer != null) {
disposer.dispose();
} else {
fontFace.Release();
if (PrismFontFactory.debugFonts) {
System.err.println("null disposer for " + fontFace);
}
}
if (PrismFontFactory.debugFonts) {
System.err.println("fontFace freed: " + fontFace);
}
fontFace = null;
}
super.disposeOnShutdown();
}
}
