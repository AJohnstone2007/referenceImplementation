package com.sun.javafx.font.directwrite;
import com.sun.javafx.font.DisposerRecord;
import com.sun.javafx.font.FontResource;
import com.sun.javafx.font.FontStrikeDesc;
import com.sun.javafx.font.Glyph;
import com.sun.javafx.font.PrismFontFactory;
import com.sun.javafx.font.PrismFontStrike;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
class DWFontStrike extends PrismFontStrike<DWFontFile> {
DWRITE_MATRIX matrix;
static final boolean SUBPIXEL_ON;
static final boolean SUBPIXEL_Y;
static final boolean SUBPIXEL_NATIVE;
static {
int mode = PrismFontFactory.getFontFactory().getSubPixelMode();
SUBPIXEL_ON = (mode & PrismFontFactory.SUB_PIXEL_ON) != 0;
SUBPIXEL_Y = (mode & PrismFontFactory.SUB_PIXEL_Y) != 0;
SUBPIXEL_NATIVE = (mode & PrismFontFactory.SUB_PIXEL_NATIVE) != 0;
}
DWFontStrike(DWFontFile fontResource, float size, BaseTransform tx,
int aaMode, FontStrikeDesc desc) {
super(fontResource, size, tx, aaMode, desc);
float maxDim = PrismFontFactory.getFontSizeLimit();
if (tx.isTranslateOrIdentity()) {
drawShapes = size > maxDim;
} else {
BaseTransform tx2d = getTransform();
matrix = new DWRITE_MATRIX();
matrix.m11 = (float)tx2d.getMxx();
matrix.m12 = (float)tx2d.getMyx();
matrix.m21 = (float)tx2d.getMxy();
matrix.m22 = (float)tx2d.getMyy();
if (Math.abs(matrix.m11 * size) > maxDim ||
Math.abs(matrix.m12 * size) > maxDim ||
Math.abs(matrix.m21 * size) > maxDim ||
Math.abs(matrix.m22 * size) > maxDim)
{
drawShapes = true;
}
}
}
@Override protected DisposerRecord createDisposer(FontStrikeDesc desc) {
return null;
}
@Override
public int getQuantizedPosition(Point2D point) {
if (SUBPIXEL_ON && (matrix == null || SUBPIXEL_NATIVE)) {
if (getAAMode() == FontResource.AA_GREYSCALE || SUBPIXEL_NATIVE) {
float subPixel = point.x;
point.x = (int)point.x;
subPixel -= point.x;
int index = 0;
if (subPixel >= 0.66f) {
index = 2;
} else if (subPixel >= 0.33f) {
index = 1;
}
if (SUBPIXEL_Y) {
subPixel = point.y;
point.y = (int)point.y;
subPixel -= point.y;
if (subPixel >= 0.66f) {
index += 6;
} else if (subPixel >= 0.33f) {
index += 3;
}
} else {
point.y = (float)Math.round(point.y);
}
return index;
}
}
return super.getQuantizedPosition(point);
}
IDWriteFontFace getFontFace() {
DWFontFile fontResource = getFontResource();
return fontResource.getFontFace();
}
RectBounds getBBox(int glyphCode) {
DWFontFile fontResource = getFontResource();
return fontResource.getBBox(glyphCode, getSize());
}
int getUpem() {
return getFontResource().getUnitsPerEm();
}
@Override protected Path2D createGlyphOutline(int glyphCode) {
DWFontFile fontResource = getFontResource();
return fontResource.getGlyphOutline(glyphCode, getSize());
}
@Override protected Glyph createGlyph(int glyphCode) {
return new DWGlyph(this, glyphCode, drawShapes);
}
}
