package com.sun.javafx.font.coretext;
import com.sun.javafx.font.DisposerRecord;
import com.sun.javafx.font.FontResource;
import com.sun.javafx.font.FontStrikeDesc;
import com.sun.javafx.font.Glyph;
import com.sun.javafx.font.PrismFontFactory;
import com.sun.javafx.font.PrismFontStrike;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.transform.BaseTransform;
class CTFontStrike extends PrismFontStrike<CTFontFile> {
private long fontRef;
CGAffineTransform matrix;
static final float SUBPIXEL4_SIZE = 12;
static final float SUBPIXEL3_SIZE = 18;
static final float SUBPIXEL2_SIZE = 34;
private static final boolean SUBPIXEL;
static {
int mode = PrismFontFactory.getFontFactory().getSubPixelMode();
SUBPIXEL = (mode & PrismFontFactory.SUB_PIXEL_ON) != 0;
}
CTFontStrike(CTFontFile fontResource, float size,
BaseTransform graphicsTransform, int aaMode,
FontStrikeDesc desc) {
super(fontResource, size, graphicsTransform, aaMode, desc);
float maxDim = PrismFontFactory.getFontSizeLimit();
if (graphicsTransform.isTranslateOrIdentity()) {
drawShapes = size > maxDim;
} else {
BaseTransform tx2d = getTransform();
matrix = new CGAffineTransform();
matrix.a = tx2d.getMxx();
matrix.b = -tx2d.getMyx();
matrix.c = -tx2d.getMxy();
matrix.d = tx2d.getMyy();
if (Math.abs(matrix.a * size) > maxDim ||
Math.abs(matrix.b * size) > maxDim ||
Math.abs(matrix.c * size) > maxDim ||
Math.abs(matrix.d * size) > maxDim)
{
drawShapes = true;
}
}
if (fontResource.isEmbeddedFont()) {
final long cgFontRef = fontResource.getCGFontRef();
if (cgFontRef != 0) {
fontRef = OS.CTFontCreateWithGraphicsFont(
cgFontRef, size, matrix, 0);
}
} else {
final long psNameRef = OS.CFStringCreate(fontResource.getPSName());
if (psNameRef != 0) {
fontRef = OS.CTFontCreateWithName(psNameRef, size, matrix);
OS.CFRelease(psNameRef);
}
}
if (fontRef == 0) {
if (PrismFontFactory.debugFonts) {
System.err.println("Failed to create CTFont for " + this);
}
}
}
long getFontRef() {
return fontRef;
}
@Override protected DisposerRecord createDisposer(FontStrikeDesc desc) {
CTFontFile fontResource = getFontResource();
return new CTStrikeDisposer(fontResource, desc, fontRef);
}
@Override protected Glyph createGlyph(int glyphCode) {
return new CTGlyph(this, glyphCode, drawShapes);
}
@Override
public int getQuantizedPosition(Point2D point) {
if (SUBPIXEL && matrix == null) {
if (getSize() < SUBPIXEL4_SIZE) {
float subPixelX = point.x;
point.x = (int) point.x;
subPixelX -= point.x;
point.y = (float) Math.round(point.y);
if (subPixelX >= 0.75f) return 3;
if (subPixelX >= 0.50f) return 2;
if (subPixelX >= 0.25f) return 1;
return 0;
}
if (getAAMode() == FontResource.AA_GREYSCALE) {
if (getSize() < SUBPIXEL3_SIZE) {
float subPixelX = point.x;
point.x = (int) point.x;
subPixelX -= point.x;
point.y = (float) Math.round(point.y);
if (subPixelX >= 0.66f) return 2;
if (subPixelX >= 0.33f) return 1;
return 0;
}
if (getSize() < SUBPIXEL2_SIZE) {
float subPixelX = point.x;
point.x = (int) point.x;
subPixelX -= point.x;
point.y = (float) Math.round(point.y);
if (subPixelX >= 0.5f) return 1;
}
return 0;
}
}
return super.getQuantizedPosition(point);
}
float getSubPixelPosition(int index) {
if (index == 0) return 0;
float size = getSize();
if (size < SUBPIXEL4_SIZE) {
if (index == 3) return 0.75f;
if (index == 2) return 0.50f;
if (index == 1) return 0.25f;
return 0;
}
if (getAAMode() == FontResource.AA_LCD) return 0;
if (size < SUBPIXEL3_SIZE) {
if (index == 2) return 0.66f;
if (index == 1) return 0.33f;
return 0;
}
if (size < SUBPIXEL2_SIZE) {
if (index == 1) return 0.50f;
}
return 0;
}
boolean isSubPixelGlyph() {
return SUBPIXEL && matrix == null;
}
@Override protected Path2D createGlyphOutline(int glyphCode) {
CTFontFile fontResource = getFontResource();
return fontResource.getGlyphOutline(glyphCode, getSize());
}
CGRect getBBox(int glyphCode) {
CTFontFile fontResource = getFontResource();
return fontResource.getBBox(glyphCode, getSize());
}
}
