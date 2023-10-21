package com.sun.javafx.font.freetype;
import com.sun.javafx.font.Disposer;
import com.sun.javafx.font.FontResource;
import com.sun.javafx.font.FontStrikeDesc;
import com.sun.javafx.font.PrismFontFactory;
import com.sun.javafx.font.PrismFontFile;
import com.sun.javafx.font.PrismFontStrike;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.transform.BaseTransform;
class FTFontFile extends PrismFontFile {
private long library;
private long face;
private FTDisposer disposer;
FTFontFile(String name, String filename, int fIndex, boolean register,
boolean embedded, boolean copy, boolean tracked) throws Exception {
super(name, filename, fIndex, register, embedded, copy, tracked);
init();
}
private synchronized void init() throws Exception {
long[] ptr = new long[1];
int error = OSFreetype.FT_Init_FreeType(ptr);
if (error != 0) {
throw new Exception("FT_Init_FreeType Failed error " + error);
}
library = ptr[0];
if (FTFactory.LCD_SUPPORT) {
OSFreetype.FT_Library_SetLcdFilter(library, OSFreetype.FT_LCD_FILTER_DEFAULT);
}
String file = getFileName();
int fontIndex = getFontIndex();
byte[] buffer = (file+"\0").getBytes();
error = OSFreetype.FT_New_Face(library, buffer, fontIndex, ptr);
if (error != 0) {
throw new Exception("FT_New_Face Failed error " + error +
" Font File " + file +
" Font Index " + fontIndex);
}
face = ptr[0];
if (!isRegistered()) {
disposer = new FTDisposer(library, face);
Disposer.addRecord(this, disposer);
}
}
@Override
protected PrismFontStrike<?> createStrike(float size, BaseTransform transform,
int aaMode, FontStrikeDesc desc) {
return new FTFontStrike(this, size, transform, aaMode, desc);
}
@Override
protected synchronized int[] createGlyphBoundingBox(int gc) {
int flags = OSFreetype.FT_LOAD_NO_SCALE;
OSFreetype.FT_Load_Glyph(face, gc, flags);
int[] bbox = new int[4];
FT_GlyphSlotRec glyphRec = OSFreetype.getGlyphSlot(face);
if (glyphRec != null && glyphRec.metrics != null) {
FT_Glyph_Metrics gm = glyphRec.metrics;
bbox[0] = (int)gm.horiBearingX;
bbox[1] = (int)(gm.horiBearingY - gm.height);
bbox[2] = (int)(gm.horiBearingX + gm.width);
bbox[3] = (int)gm.horiBearingY;
}
return bbox;
}
synchronized Path2D createGlyphOutline(int gc, float size) {
int size26dot6 = (int)(size * 64);
OSFreetype.FT_Set_Char_Size(face, 0, size26dot6, 72, 72);
int flags = OSFreetype.FT_LOAD_NO_HINTING | OSFreetype.FT_LOAD_NO_BITMAP | OSFreetype.FT_LOAD_IGNORE_TRANSFORM;
OSFreetype.FT_Load_Glyph(face, gc, flags);
return OSFreetype.FT_Outline_Decompose(face);
}
synchronized void initGlyph(FTGlyph glyph, FTFontStrike strike) {
float size = strike.getSize();
if (size == 0) {
glyph.buffer = new byte[0];
glyph.bitmap = new FT_Bitmap();
return;
}
int size26dot6 = (int)(size * 64);
OSFreetype.FT_Set_Char_Size(face, 0, size26dot6, 72, 72);
boolean lcd = strike.getAAMode() == FontResource.AA_LCD &&
FTFactory.LCD_SUPPORT;
int flags = OSFreetype.FT_LOAD_RENDER | OSFreetype.FT_LOAD_NO_HINTING | OSFreetype.FT_LOAD_NO_BITMAP;
FT_Matrix matrix = strike.matrix;
if (matrix != null) {
OSFreetype.FT_Set_Transform(face, matrix, 0, 0);
} else {
flags |= OSFreetype.FT_LOAD_IGNORE_TRANSFORM;
}
if (lcd) {
flags |= OSFreetype.FT_LOAD_TARGET_LCD;
} else {
flags |= OSFreetype.FT_LOAD_TARGET_NORMAL;
}
int glyphCode = glyph.getGlyphCode();
int error = OSFreetype.FT_Load_Glyph(face, glyphCode, flags);
if (error != 0) {
if (PrismFontFactory.debugFonts) {
System.err.println("FT_Load_Glyph failed " + error +
" glyph code " + glyphCode +
" load falgs " + flags);
}
return;
}
FT_GlyphSlotRec glyphRec = OSFreetype.getGlyphSlot(face);
if (glyphRec == null) return;
FT_Bitmap bitmap = glyphRec.bitmap;
if (bitmap == null) return;
int pixelMode = bitmap.pixel_mode;
int width = bitmap.width;
int height = bitmap.rows;
int pitch = bitmap.pitch;
if (pixelMode != OSFreetype.FT_PIXEL_MODE_GRAY && pixelMode != OSFreetype.FT_PIXEL_MODE_LCD) {
if (PrismFontFactory.debugFonts) {
System.err.println("Unexpected pixel mode: " + pixelMode +
" glyph code " + glyphCode +
" load falgs " + flags);
}
return;
}
byte[] buffer;
if (width != 0 && height != 0) {
buffer = OSFreetype.getBitmapData(face);
if (buffer != null && pitch != width) {
byte[] newBuffer = new byte[width * height];
int src = 0, dst = 0;
for (int y = 0; y < height; y++) {
for (int x = 0; x < width; x++) {
newBuffer[dst + x] = buffer[src + x];
}
dst += width;
src += pitch;
}
buffer = newBuffer;
}
} else {
buffer = new byte[0];
}
glyph.buffer = buffer;
glyph.bitmap = bitmap;
glyph.bitmap_left = glyphRec.bitmap_left;
glyph.bitmap_top = glyphRec.bitmap_top;
glyph.advanceX = glyphRec.advance_x / 64f;
glyph.advanceY = glyphRec.advance_y / 64f;
glyph.userAdvance = glyphRec.linearHoriAdvance / 65536.0f;
glyph.lcd = lcd;
}
}
