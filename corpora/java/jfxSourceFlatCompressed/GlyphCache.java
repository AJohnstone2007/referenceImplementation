package com.sun.prism.impl;
import com.sun.javafx.font.CharToGlyphMapper;
import com.sun.javafx.font.CompositeGlyphMapper;
import com.sun.javafx.font.FontResource;
import com.sun.javafx.font.FontStrike;
import com.sun.javafx.font.Glyph;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.text.GlyphList;
import com.sun.prism.impl.packrect.RectanglePacker;
import com.sun.prism.Texture;
import com.sun.prism.impl.shape.MaskData;
import com.sun.prism.paint.Color;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.WeakHashMap;
import static com.sun.javafx.logging.PulseLogger.PULSE_LOGGING_ENABLED;
import com.sun.javafx.logging.PulseLogger;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture.WrapMode;
public class GlyphCache {
private static final int WIDTH = PrismSettings.glyphCacheWidth;
private static final int HEIGHT = PrismSettings.glyphCacheHeight;
private static ByteBuffer emptyMask;
private final BaseContext context;
private final FontStrike strike;
private static final int SEGSHIFT = 5;
private static final int SEGSIZE = 1 << SEGSHIFT;
private static final int SEGMASK = SEGSIZE - 1;
HashMap<Integer, GlyphData[]>
glyphDataMap = new HashMap<Integer, GlyphData[]>();
private static final int SUBPIXEL_SHIFT = 27;
private RectanglePacker packer;
private boolean isLCDCache;
static WeakHashMap<BaseContext, RectanglePacker> greyPackerMap =
new WeakHashMap<BaseContext, RectanglePacker>();
static WeakHashMap<BaseContext, RectanglePacker> lcdPackerMap =
new WeakHashMap<BaseContext, RectanglePacker>();
public GlyphCache(BaseContext context, FontStrike strike) {
this.context = context;
this.strike = strike;
isLCDCache = strike.getAAMode() == FontResource.AA_LCD;
WeakHashMap<BaseContext, RectanglePacker>
packerMap = isLCDCache ? lcdPackerMap : greyPackerMap;
packer = packerMap.get(context);
if (packer == null) {
ResourceFactory factory = context.getResourceFactory();
Texture tex = factory.createMaskTexture(WIDTH, HEIGHT,
WrapMode.CLAMP_NOT_NEEDED);
tex.contentsUseful();
tex.makePermanent();
if (!isLCDCache) {
factory.setGlyphTexture(tex);
}
tex.setLinearFiltering(false);
packer = new RectanglePacker(tex, WIDTH, HEIGHT);
packerMap.put(context, packer);
}
}
public void render(BaseContext ctx, GlyphList gl, float x, float y,
int start, int end, Color rangeColor, Color textColor,
BaseTransform xform, BaseBounds clip) {
int dstw, dsth;
if (isLCDCache) {
dstw = ctx.getLCDBuffer().getPhysicalWidth();
dsth = ctx.getLCDBuffer().getPhysicalHeight();
} else {
dstw = 1;
dsth = 1;
}
Texture tex = getBackingStore();
VertexBuffer vb = ctx.getVertexBuffer();
int len = gl.getGlyphCount();
Color currentColor = null;
Point2D pt = new Point2D();
for (int gi = 0; gi < len; gi++) {
int gc = gl.getGlyphCode(gi);
if ((gc & CompositeGlyphMapper.GLYPHMASK) == CharToGlyphMapper.INVISIBLE_GLYPH_ID) {
continue;
}
pt.setLocation(x + gl.getPosX(gi), y + gl.getPosY(gi));
xform.transform(pt, pt);
int subPixel = strike.getQuantizedPosition(pt);
GlyphData data = getCachedGlyph(gc, subPixel);
if (data != null) {
if (clip != null) {
if (x + gl.getPosX(gi) > clip.getMaxX()) break;
if (x + gl.getPosX(gi + 1) < clip.getMinX()) continue;
}
if (rangeColor != null && textColor != null) {
int offset = gl.getCharOffset(gi);
if (start <= offset && offset < end) {
if (rangeColor != currentColor) {
vb.setPerVertexColor(rangeColor, 1.0f);
currentColor = rangeColor;
}
} else {
if (textColor != currentColor) {
vb.setPerVertexColor(textColor, 1.0f);
currentColor = textColor;
}
}
}
addDataToQuad(data, vb, tex, pt.x, pt.y, dstw, dsth);
}
}
}
private void addDataToQuad(GlyphData data, VertexBuffer vb,
Texture tex, float x, float y,
float dstw, float dsth) {
y = Math.round(y);
Rectangle rect = data.getRect();
if (rect == null) {
return;
}
int border = data.getBlankBoundary();
float gw = rect.width - (border * 2);
float gh = rect.height - (border * 2);
float dx1 = data.getOriginX() + x;
float dy1 = data.getOriginY() + y;
float dx2;
float dy2 = dy1 + gh;
float tw = tex.getPhysicalWidth();
float th = tex.getPhysicalHeight();
float tx1 = (rect.x + border) / tw;
float ty1 = (rect.y + border) / th;
float tx2 = tx1 + (gw / tw);
float ty2 = ty1 + (gh / th);
if (isLCDCache) {
dx1 = Math.round(dx1 * 3.0f) / 3.0f;
dx2 = dx1 + gw / 3.0f;
float t2x1 = dx1 / dstw;
float t2x2 = dx2 / dstw;
float t2y1 = dy1 / dsth;
float t2y2 = dy2 / dsth;
vb.addQuad(dx1, dy1, dx2, dy2, tx1, ty1, tx2, ty2, t2x1, t2y1, t2x2, t2y2);
} else {
dx1 = Math.round(dx1);
dx2 = dx1 + gw;
if (context.isSuperShaderEnabled()) {
vb.addSuperQuad(dx1, dy1, dx2, dy2, tx1, ty1, tx2, ty2, true);
} else {
vb.addQuad(dx1, dy1, dx2, dy2, tx1, ty1, tx2, ty2);
}
}
}
public Texture getBackingStore() {
return packer.getBackingStore();
}
public void clear() {
glyphDataMap.clear();
}
private void clearAll() {
context.flushVertexBuffer();
context.clearGlyphCaches();
packer.clear();
}
private GlyphData getCachedGlyph(int glyphCode, int subPixel) {
int segIndex = glyphCode >>> SEGSHIFT;
int subIndex = glyphCode & SEGMASK;
segIndex |= (subPixel << SUBPIXEL_SHIFT);
GlyphData[] segment = glyphDataMap.get(segIndex);
if (segment != null) {
if (segment[subIndex] != null) {
return segment[subIndex];
}
} else {
segment = new GlyphData[SEGSIZE];
glyphDataMap.put(segIndex, segment);
}
GlyphData data = null;
Glyph glyph = strike.getGlyph(glyphCode);
if (glyph != null) {
byte[] glyphImage = glyph.getPixelData(subPixel);
if (glyphImage == null || glyphImage.length == 0) {
data = new GlyphData(0, 0, 0,
glyph.getPixelXAdvance(),
glyph.getPixelYAdvance(),
null);
} else {
MaskData maskData = MaskData.create(glyphImage,
glyph.getOriginX(),
glyph.getOriginY(),
glyph.getWidth(),
glyph.getHeight());
int border = 1;
int rectW = maskData.getWidth() + (2 * border);
int rectH = maskData.getHeight() + (2 * border);
int originX = maskData.getOriginX();
int originY = maskData.getOriginY();
Rectangle rect = new Rectangle(0, 0, rectW, rectH);
data = new GlyphData(originX, originY, border,
glyph.getPixelXAdvance(),
glyph.getPixelYAdvance(),
rect);
if (!packer.add(rect)) {
if (PULSE_LOGGING_ENABLED) {
PulseLogger.incrementCounter("Font Glyph Cache Cleared");
}
clearAll();
if (!packer.add(rect)) {
if (PrismSettings.verbose) {
System.out.println(rect + " won't fit in GlyphCache");
}
return null;
}
}
boolean skipFlush = true;
Texture backingStore = getBackingStore();
int emw = rect.width;
int emh = rect.height;
int bpp = backingStore.getPixelFormat().getBytesPerPixelUnit();
int stride = emw * bpp;
int size = stride * emh;
if (emptyMask == null || size > emptyMask.capacity()) {
emptyMask = BufferUtil.newByteBuffer(size);
}
try {
backingStore.update(emptyMask,
backingStore.getPixelFormat(),
rect.x, rect.y,
0, 0, emw, emh, stride,
skipFlush);
} catch (Exception e) {
if (PrismSettings.verbose) {
e.printStackTrace();
}
return null;
}
maskData.uploadToTexture(backingStore,
border + rect.x,
border + rect.y,
skipFlush);
}
segment[subIndex] = data;
}
return data;
}
static class GlyphData {
private final int originX;
private final int originY;
private final int blankBoundary;
private final float xAdvance, yAdvance;
private final Rectangle rect;
GlyphData(int originX, int originY, int blankBoundary,
float xAdvance, float yAdvance, Rectangle rect)
{
this.originX = originX;
this.originY = originY;
this.blankBoundary = blankBoundary;
this.xAdvance = xAdvance;
this.yAdvance = yAdvance;
this.rect = rect;
}
int getOriginX() {
return originX;
}
int getOriginY() {
return originY;
}
int getBlankBoundary() {
return blankBoundary;
}
float getXAdvance() {
return xAdvance;
}
float getYAdvance() {
return yAdvance;
}
Rectangle getRect() {
return rect;
}
}
private static void disposePackerForContext(BaseContext ctx,
WeakHashMap<BaseContext, RectanglePacker> packerMap) {
RectanglePacker packer = packerMap.remove(ctx);
if (packer != null) {
packer.dispose();
}
}
public static void disposeForContext(BaseContext ctx) {
disposePackerForContext(ctx, greyPackerMap);
disposePackerForContext(ctx, lcdPackerMap);
}
}
