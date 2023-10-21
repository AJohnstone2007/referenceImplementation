package com.sun.javafx.font;
import com.sun.javafx.scene.text.GlyphList;
import com.sun.javafx.geom.transform.Affine2D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.Shape;
public class CompositeStrike implements FontStrike {
private CompositeFontResource fontResource;
private float size;
private int aaMode;
BaseTransform transform;
private FontStrike slot0Strike;
private FontStrike[] strikeSlots;
private FontStrikeDesc desc;
DisposerRecord disposer;
public void clearDesc() {
fontResource.getStrikeMap().remove(desc);
if (slot0Strike != null) {
slot0Strike.clearDesc();
}
if (strikeSlots != null) {
for (int i=1; i<strikeSlots.length; i++) {
if (strikeSlots[i] != null) {
strikeSlots[i].clearDesc();
}
}
}
}
CompositeStrike(CompositeFontResource fontResource,
float size, BaseTransform graphicsTransform, int aaMode,
FontStrikeDesc desc) {
this.fontResource = fontResource;
this.size = size;
if (graphicsTransform.isTranslateOrIdentity()) {
this.transform = BaseTransform.IDENTITY_TRANSFORM;
} else {
this.transform = graphicsTransform.copy();
}
this.desc = desc;
this.aaMode = aaMode;
disposer = new CompositeStrikeDisposer(fontResource, desc);
}
public int getAAMode() {
PrismFontFactory factory = PrismFontFactory.getFontFactory();
if (factory.isLCDTextSupported()) {
return this.aaMode;
} else {
return FontResource.AA_GREYSCALE;
}
}
public BaseTransform getTransform() {
return transform;
}
public FontStrike getStrikeSlot(int slot) {
if (slot == 0) {
if (slot0Strike == null) {
FontResource slot0Resource = fontResource.getSlotResource(0);
slot0Strike = slot0Resource.getStrike(size, transform,
getAAMode());
}
return slot0Strike;
} else {
if (strikeSlots == null) {
strikeSlots = new FontStrike[fontResource.getNumSlots()];
}
if (slot >= strikeSlots.length) {
FontStrike[] tmp = new FontStrike[fontResource.getNumSlots()];
System.arraycopy(strikeSlots, 0, tmp, 0, strikeSlots.length);
strikeSlots = tmp;
}
if (strikeSlots[slot] == null) {
FontResource slotResource = fontResource.getSlotResource(slot);
strikeSlots[slot] = slotResource.getStrike(size, transform,
getAAMode());
}
return strikeSlots[slot];
}
}
public FontResource getFontResource() {
return fontResource;
}
public int getStrikeSlotForGlyph(int glyphCode) {
return (glyphCode >>> 24);
}
public float getSize() {
return size;
}
public boolean drawAsShapes() {
return getStrikeSlot(0).drawAsShapes();
}
private PrismMetrics metrics;
public Metrics getMetrics() {
if (metrics == null) {
PrismFontFile fr = (PrismFontFile)fontResource.getSlotResource(0);
metrics = fr.getFontMetrics(size);
}
return metrics;
}
public Glyph getGlyph(char symbol) {
int glyphCode = fontResource.getGlyphMapper().charToGlyph(symbol);
return getGlyph(glyphCode);
}
public Glyph getGlyph(int glyphCode) {
int slot = (glyphCode >>> 24);
int slotglyphCode = glyphCode & CompositeGlyphMapper.GLYPHMASK;
return getStrikeSlot(slot).getGlyph(slotglyphCode);
}
public float getCharAdvance(char ch) {
int glyphCode = fontResource.getGlyphMapper().charToGlyph((int)ch);
return fontResource.getAdvance(glyphCode, size);
}
@Override
public int getQuantizedPosition(Point2D point) {
return getStrikeSlot(0).getQuantizedPosition(point);
}
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
Glyph glyph = getGlyph(glyphCode);
Shape gp = glyph.getShape();
if (gp != null) {
t.setTransform(transform);
t.translate(gl.getPosX(i), gl.getPosY(i));
p.append(gp.getPathIterator(t), false);
}
}
}
}
}
