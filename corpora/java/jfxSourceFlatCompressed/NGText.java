package com.sun.javafx.sg.prism;
import com.sun.javafx.font.FontResource;
import com.sun.javafx.font.FontStrike;
import com.sun.javafx.font.Metrics;
import com.sun.javafx.font.PGFont;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.RoundRectangle2D;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.text.GlyphList;
import com.sun.javafx.text.TextRun;
import com.sun.prism.Graphics;
import com.sun.prism.paint.Color;
public class NGText extends NGShape {
static final BaseTransform IDENT = BaseTransform.IDENTITY_TRANSFORM;
public NGText() {
}
private GlyphList[] runs;
public void setGlyphs(Object[] glyphs) {
this.runs = (GlyphList[])glyphs;
geometryChanged();
}
private float layoutX, layoutY;
public void setLayoutLocation(float x, float y) {
layoutX = x;
layoutY = y;
geometryChanged();
}
private PGFont font;
public void setFont(Object font) {
if (font != null && font.equals(this.font)) {
return;
}
this.font = (PGFont)font;
this.fontStrike = null;
this.identityStrike = null;
geometryChanged();
}
private int fontSmoothingType;
public void setFontSmoothingType(int fontSmoothingType) {
this.fontSmoothingType = fontSmoothingType;
geometryChanged();
}
private boolean underline;
public void setUnderline(boolean underline) {
this.underline = underline;
geometryChanged();
}
private boolean strikethrough;
public void setStrikethrough(boolean strikethrough) {
this.strikethrough = strikethrough;
geometryChanged();
}
private Object selectionPaint;
private int selectionStart;
private int selectionEnd;
public void setSelection(int start, int end, Object color) {
selectionPaint = color;
selectionStart = start;
selectionEnd = end;
geometryChanged();
}
@Override protected BaseBounds computePadding(BaseBounds region) {
float pad = fontSmoothingType == FontResource.AA_LCD ? 2f : 1f;
return region.deriveWithNewBounds(region.getMinX() - pad,
region.getMinY() - pad,
region.getMinZ(),
region.getMaxX() + pad,
region.getMaxY() + pad,
region.getMaxZ());
}
private static double EPSILON = 0.01;
private FontStrike fontStrike = null;
private FontStrike identityStrike = null;
private double[] strikeMat = new double[4];
private FontStrike getStrike(BaseTransform xform) {
int smoothingType = fontSmoothingType;
if (getMode() == Mode.STROKE_FILL) {
smoothingType = FontResource.AA_GREYSCALE;
}
if (xform.isIdentity()) {
if (identityStrike == null ||
smoothingType != identityStrike.getAAMode()) {
identityStrike = font.getStrike(IDENT, smoothingType);
}
return identityStrike;
}
if (fontStrike == null ||
fontStrike.getSize() != font.getSize() ||
(xform.getMxy() == 0 && strikeMat[1] != 0) ||
(xform.getMyx() == 0 && strikeMat[2] != 0) ||
(Math.abs(strikeMat[0] - xform.getMxx()) > EPSILON) ||
(Math.abs(strikeMat[1] - xform.getMxy()) > EPSILON) ||
(Math.abs(strikeMat[2] - xform.getMyx()) > EPSILON) ||
(Math.abs(strikeMat[3] - xform.getMyy()) > EPSILON) ||
smoothingType != fontStrike.getAAMode())
{
fontStrike = font.getStrike(xform, smoothingType);
strikeMat[0] = xform.getMxx();
strikeMat[1] = xform.getMxy();
strikeMat[2] = xform.getMyx();
strikeMat[3] = xform.getMyy();
}
return fontStrike;
}
@Override public Shape getShape() {
if (runs == null) {
return new Path2D();
}
FontStrike strike = getStrike(IDENT);
Path2D outline = new Path2D();
for (int i = 0; i < runs.length; i++) {
GlyphList run = runs[i];
Point2D pt = run.getLocation();
float x = pt.x - layoutX;
float y = pt.y - layoutY;
BaseTransform t = BaseTransform.getTranslateInstance(x, y);
outline.append(strike.getOutline(run, t), false);
Metrics metrics = null;
if (underline) {
metrics = strike.getMetrics();
RoundRectangle2D rect = new RoundRectangle2D();
rect.x = x;
rect.y = y + metrics.getUnderLineOffset();
rect.width = run.getWidth();
rect.height = metrics.getUnderLineThickness();
outline.append(rect, false);
}
if (strikethrough) {
if (metrics == null) {
metrics = strike.getMetrics();
}
RoundRectangle2D rect = new RoundRectangle2D();
rect.x = x;
rect.y = y + metrics.getStrikethroughOffset();
rect.width = run.getWidth();
rect.height = metrics.getStrikethroughThickness();
outline.append(rect, false);
}
}
return outline;
}
private boolean drawingEffect = false;
@Override protected void renderEffect(Graphics g) {
if (!g.getTransformNoClone().isTranslateOrIdentity()) {
drawingEffect = true;
}
try {
super.renderEffect(g);
} finally {
drawingEffect = false;
}
}
private static int FILL = 1 << 1;
private static int SHAPE_FILL = 1 << 2;
private static int TEXT = 1 << 3;
private static int DECORATION = 1 << 4;
@Override protected void renderContent2D(Graphics g, boolean printing) {
if (mode == Mode.EMPTY) return;
if (runs == null || runs.length == 0) return;
BaseTransform tx = g.getTransformNoClone();
FontStrike strike = getStrike(tx);
if (strike.getAAMode() == FontResource.AA_LCD ||
(fillPaint != null && fillPaint.isProportional()) ||
(drawPaint != null && drawPaint.isProportional()))
{
BaseBounds bds = getContentBounds(new RectBounds(), IDENT);
g.setNodeBounds((RectBounds)bds);
}
Color selectionColor = null;
if (selectionStart != selectionEnd && selectionPaint instanceof Color) {
selectionColor = (Color)selectionPaint;
}
BaseBounds clipBds = null;
if (getClipNode() != null) {
clipBds = getClippedBounds(new RectBounds(), IDENT);
}
if (mode != Mode.STROKE) {
g.setPaint(fillPaint);
int op = TEXT;
op |= strike.drawAsShapes() || drawingEffect ? SHAPE_FILL : FILL;
renderText(g, strike, clipBds, selectionColor, op);
if (underline || strikethrough) {
op = DECORATION | SHAPE_FILL;
renderText(g, strike, clipBds, selectionColor, op);
}
}
if (mode != Mode.FILL) {
g.setPaint(drawPaint);
g.setStroke(drawStroke);
int op = TEXT;
if (underline || strikethrough) {
op |= DECORATION;
}
renderText(g, strike, clipBds, selectionColor, op);
}
g.setNodeBounds(null);
}
private void renderText(Graphics g, FontStrike strike, BaseBounds clipBds,
Color selectionColor, int op) {
for (int i = 0; i < runs.length; i++) {
TextRun run = (TextRun)runs[i];
RectBounds lineBounds = run.getLineBounds();
Point2D pt = run.getLocation();
float x = pt.x - layoutX;
float y = pt.y - layoutY;
if (clipBds != null) {
if (y > clipBds.getMaxY()) break;
if (y + lineBounds.getHeight() < clipBds.getMinY()) continue;
if (x > clipBds.getMaxX()) continue;
if (x + run.getWidth() < clipBds.getMinX()) continue;
}
y -= lineBounds.getMinY();
if ((op & TEXT) != 0 && run.getGlyphCount() > 0) {
if ((op & FILL) != 0) {
int start = run.getStart();
g.drawString(run, strike, x, y,
selectionColor,
selectionStart - start,
selectionEnd - start);
} else {
BaseTransform t = BaseTransform.getTranslateInstance(x, y);
if ((op & SHAPE_FILL) != 0) {
g.fill(strike.getOutline(run, t));
} else {
g.draw(strike.getOutline(run, t));
}
}
}
if ((op & DECORATION) != 0) {
Metrics metrics = strike.getMetrics();
if (underline) {
float offset = y + metrics.getUnderLineOffset();
float thickness = metrics.getUnderLineThickness();
if ((op & SHAPE_FILL) != 0) {
if (thickness <= 1f && g.getTransformNoClone().isTranslateOrIdentity()) {
float myt = (float)g.getTransformNoClone().getMyt();
offset = Math.round(offset + myt) - myt;
}
g.fillRect(x, offset, run.getWidth(), thickness);
} else {
g.drawRect(x, offset, run.getWidth(), thickness);
}
}
if (strikethrough) {
float offset = y + metrics.getStrikethroughOffset();
float thickness = metrics.getStrikethroughThickness();
if ((op & SHAPE_FILL) != 0) {
if (thickness <= 1f && g.getTransformNoClone().isTranslateOrIdentity()) {
float myt = (float)g.getTransformNoClone().getMyt();
offset = Math.round(offset + myt) - myt;
}
g.fillRect(x, offset, run.getWidth(), thickness);
} else {
g.drawRect(x, offset, run.getWidth(), thickness);
}
}
}
}
}
}
