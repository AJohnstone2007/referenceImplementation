package com.sun.javafx.font;
import java.util.HashMap;
import java.util.Map;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.Affine2D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.text.GlyphList;
public abstract class PrismFontStrike<T extends PrismFontFile> implements FontStrike {
private DisposerRecord disposer;
private T fontResource;
private Map<Integer,Glyph> glyphMap = new HashMap<Integer,Glyph>();
private PrismMetrics metrics;
protected boolean drawShapes = false;
private float size;
private BaseTransform transform;
private int aaMode;
private FontStrikeDesc desc;
protected PrismFontStrike(T fontResource,
float size, BaseTransform tx, int aaMode,
FontStrikeDesc desc) {
this.fontResource = fontResource;
this.size = size;
this.desc = desc;
PrismFontFactory factory = PrismFontFactory.getFontFactory();
boolean lcdEnabled = factory.isLCDTextSupported();
this.aaMode = lcdEnabled ? aaMode : FontResource.AA_GREYSCALE;
if (tx.isTranslateOrIdentity()) {
transform = BaseTransform.IDENTITY_TRANSFORM;
} else {
transform = new Affine2D(tx.getMxx(), tx.getMyx(),
tx.getMxy(), tx.getMyy(),
0f, 0f);
}
}
DisposerRecord getDisposer() {
if (disposer == null) {
disposer = createDisposer(desc);
}
return disposer;
}
protected abstract DisposerRecord createDisposer(FontStrikeDesc desc);
public synchronized void clearDesc() {
fontResource.getStrikeMap().remove(desc);
}
public float getSize() {
return size;
}
public Metrics getMetrics() {
if (metrics == null) {
metrics = fontResource.getFontMetrics(size);
}
return metrics;
}
public T getFontResource() {
return fontResource;
}
public boolean drawAsShapes() {
return drawShapes;
}
public int getAAMode() {
return aaMode;
}
public BaseTransform getTransform() {
return transform;
}
@Override
public int getQuantizedPosition(Point2D point) {
if (aaMode == FontResource.AA_GREYSCALE) {
point.x = (float)Math.round(point.x);
} else {
point.x = (float)Math.round(3.0 * point.x)/ 3.0f;
}
point.y = (float)Math.round(point.y);
return 0;
}
public float getCharAdvance(char ch) {
int glyphCode = fontResource.getGlyphMapper().charToGlyph((int)ch);
return fontResource.getAdvance(glyphCode, size);
}
public Glyph getGlyph(char ch) {
int glyphCode = fontResource.getGlyphMapper().charToGlyph((int)ch);
return getGlyph(glyphCode);
}
protected abstract Glyph createGlyph(int glyphCode);
public Glyph getGlyph(int glyphCode) {
Glyph glyph = glyphMap.get(glyphCode);
if (glyph == null) {
glyph = createGlyph(glyphCode);
glyphMap.put(glyphCode, glyph);
}
return glyph;
}
protected abstract Path2D createGlyphOutline(int glyphCode);
public Shape getOutline(GlyphList gl, BaseTransform transform) {
Path2D result = new Path2D();
getOutline(gl, transform, result);
return result;
}
void getOutline(GlyphList gl, BaseTransform transform, Path2D p) {
p.reset();
if (gl == null) {
return;
}
if (transform == null) {
transform = BaseTransform.IDENTITY_TRANSFORM;
}
Affine2D t = new Affine2D();
for (int i = 0; i < gl.getGlyphCount(); i++) {
int glyphCode = gl.getGlyphCode(i);
if (glyphCode != CharToGlyphMapper.INVISIBLE_GLYPH_ID) {
Shape gp = createGlyphOutline(glyphCode);
if (gp != null) {
t.setTransform(transform);
t.translate(gl.getPosX(i), gl.getPosY(i));
p.append(gp.getPathIterator(t), false);
}
}
}
}
@Override
public boolean equals(Object obj) {
if (obj == null) {
return false;
}
if (!(obj instanceof PrismFontStrike)) {
return false;
}
final PrismFontStrike other = (PrismFontStrike) obj;
return this.size == other.size &&
this.transform.getMxx() == other.transform.getMxx() &&
this.transform.getMxy() == other.transform.getMxy() &&
this.transform.getMyx() == other.transform.getMyx() &&
this.transform.getMyy() == other.transform.getMyy() &&
this.fontResource.equals(other.fontResource);
}
private int hash;
@Override
public int hashCode() {
if (hash != 0) {
return hash;
}
hash = Float.floatToIntBits(size) +
Float.floatToIntBits((float)transform.getMxx()) +
Float.floatToIntBits((float)transform.getMyx()) +
Float.floatToIntBits((float)transform.getMxy()) +
Float.floatToIntBits((float)transform.getMyy());
hash = 71 * hash + fontResource.hashCode();
return hash;
}
public String toString() {
return "FontStrike: " + super.toString() +
" font resource = " + fontResource +
" size = " + size +
" matrix = " + transform;
}
}
