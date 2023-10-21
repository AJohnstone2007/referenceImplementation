package com.sun.javafx.sg.prism;
import com.sun.prism.Graphics;
import com.sun.prism.Image;
import com.sun.prism.Texture;
import com.sun.prism.paint.Color;
import com.sun.scenario.effect.Color4f;
import com.sun.scenario.effect.DropShadow;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.InnerShadow;
class EffectUtil {
private static final int TEX_SIZE = 256;
private static Texture itex;
private static Texture dtex;
static boolean renderEffectForRectangularNode(NGNode node,
Graphics g,
Effect effect,
float alpha, boolean aa,
float rx, float ry,
float rw, float rh)
{
if (!g.getTransformNoClone().is2D() && g.isDepthBuffer() && g.isDepthTest()) {
return false;
}
if (effect instanceof InnerShadow && !aa) {
InnerShadow shadow = (InnerShadow)effect;
float radius = shadow.getRadius();
if (radius > 0f &&
radius < rw/2 &&
radius < rh/2 &&
shadow.getChoke() == 0f &&
shadow.getShadowSourceInput() == null &&
shadow.getContentInput() == null)
{
node.renderContent(g);
EffectUtil.renderRectInnerShadow(g, shadow, alpha, rx, ry, rw, rh);
return true;
}
} else if (effect instanceof DropShadow) {
DropShadow shadow = (DropShadow)effect;
float radius = shadow.getRadius();
if (radius > 0f &&
radius < rw/2 &&
radius < rh/2 &&
shadow.getSpread() == 0f &&
shadow.getShadowSourceInput() == null &&
shadow.getContentInput() == null)
{
EffectUtil.renderRectDropShadow(g, shadow, alpha, rx, ry, rw, rh);
node.renderContent(g);
return true;
}
}
return false;
}
static void renderRectInnerShadow(Graphics g, InnerShadow shadow, float alpha,
float rx, float ry, float rw, float rh)
{
if (itex == null || itex.isSurfaceLost()) {
byte[] sdata = new byte[TEX_SIZE * TEX_SIZE];
fillGaussian(sdata, TEX_SIZE, TEX_SIZE/2, shadow.getChoke(), true);
Image img = Image.fromByteAlphaData(sdata, TEX_SIZE, TEX_SIZE);
itex = g.getResourceFactory().createTexture(img,
Texture.Usage.STATIC,
Texture.WrapMode.CLAMP_TO_EDGE);
assert itex.getWrapMode() == Texture.WrapMode.CLAMP_TO_EDGE;
itex.contentsUseful();
itex.makePermanent();
}
float r = shadow.getRadius();
int texsize = itex.getPhysicalWidth();
int tcx1 = itex.getContentX();
int tcx2 = tcx1 + itex.getContentWidth();
float t1 = (tcx1 + 0.5f) / texsize;
float t2 = (tcx2 - 0.5f) / texsize;
float cx1 = rx;
float cy1 = ry;
float cx2 = rx + rw;
float cy2 = ry + rh;
float ox1 = cx1 + shadow.getOffsetX();
float oy1 = cy1 + shadow.getOffsetY();
float ox2 = ox1 + rw;
float oy2 = oy1 + rh;
g.setPaint(toPrismColor(shadow.getColor(), alpha));
drawClippedTexture(g, itex,
cx1, cy1, cx2, cy2,
cx1, cy1, cx2, oy1-r,
t1, t1, t1, t1);
drawClippedTexture(g, itex,
cx1, cy1, cx2, cy2,
ox1-r, oy1-r, ox1+r, oy1+r,
t1, t1, t2, t2);
drawClippedTexture(g, itex,
cx1, cy1, cx2, cy2,
ox1+r, oy1-r, ox2-r, oy1+r,
t2, t1, t2, t2);
drawClippedTexture(g, itex,
cx1, cy1, cx2, cy2,
ox2-r, oy1-r, ox2+r, oy1+r,
t2, t1, t1, t2);
drawClippedTexture(g, itex,
cx1, cy1, cx2, cy2,
cx1, oy1-r, ox1-r, oy2+r,
t1, t1, t1, t1);
drawClippedTexture(g, itex,
cx1, cy1, cx2, cy2,
ox1-r, oy1+r, ox1+r, oy2-r,
t1, t2, t2, t2);
drawClippedTexture(g, itex,
cx1, cy1, cx2, cy2,
ox2-r, oy1+r, ox2+r, oy2-r,
t2, t2, t1, t2);
drawClippedTexture(g, itex,
cx1, cy1, cx2, cy2,
ox2+r, oy1-r, cx2, oy2+r,
t1, t1, t1, t1);
drawClippedTexture(g, itex,
cx1, cy1, cx2, cy2,
ox1-r, oy2-r, ox1+r, oy2+r,
t1, t2, t2, t1);
drawClippedTexture(g, itex,
cx1, cy1, cx2, cy2,
ox1+r, oy2-r, ox2-r, oy2+r,
t2, t2, t2, t1);
drawClippedTexture(g, itex,
cx1, cy1, cx2, cy2,
ox2-r, oy2-r, ox2+r, oy2+r,
t2, t2, t1, t1);
drawClippedTexture(g, itex,
cx1, cy1, cx2, cy2,
cx1, oy2+r, cx2, cy2,
t1, t1, t1, t1);
}
static void drawClippedTexture(Graphics g, Texture tex,
float cx1, float cy1, float cx2, float cy2,
float ox1, float oy1, float ox2, float oy2,
float tx1, float ty1, float tx2, float ty2)
{
if (ox1 >= ox2 || oy1 >= oy2 || cx1 >= cx2 || cy1 >= cy2) return;
if (ox2 > cx1 && ox1 < cx2) {
if (ox1 < cx1) {
tx1 += (tx2 - tx1) * (cx1 - ox1) / (ox2 - ox1);
ox1 = cx1;
}
if (ox2 > cx2) {
tx2 -= (tx2 - tx1) * (ox2 - cx2) / (ox2 - ox1);
ox2 = cx2;
}
} else {
return;
}
if (oy2 > cy1 && oy1 < cy2) {
if (oy1 < cy1) {
ty1 += (ty2 - ty1) * (cy1 - oy1) / (oy2 - oy1);
oy1 = cy1;
}
if (oy2 > cy2) {
ty2 -= (ty2 - ty1) * (oy2 - cy2) / (oy2 - oy1);
oy2 = cy2;
}
} else {
return;
}
g.drawTextureRaw(tex, ox1, oy1, ox2, oy2, tx1, ty1, tx2, ty2);
}
static void renderRectDropShadow(Graphics g, DropShadow shadow, float alpha,
float rx, float ry, float rw, float rh)
{
if (dtex == null || dtex.isSurfaceLost()) {
byte[] sdata = new byte[TEX_SIZE * TEX_SIZE];
fillGaussian(sdata, TEX_SIZE, TEX_SIZE / 2, shadow.getSpread(), false);
Image img = Image.fromByteAlphaData(sdata, TEX_SIZE, TEX_SIZE);
dtex = g.getResourceFactory().createTexture(img,
Texture.Usage.STATIC,
Texture.WrapMode.CLAMP_TO_EDGE);
assert dtex.getWrapMode() == Texture.WrapMode.CLAMP_TO_EDGE;
dtex.contentsUseful();
dtex.makePermanent();
}
float r = shadow.getRadius();
int texsize = dtex.getPhysicalWidth();
int cx1 = dtex.getContentX();
int cx2 = cx1 + dtex.getContentWidth();
float t1 = (cx1 + 0.5f) / texsize;
float t2 = (cx2 - 0.5f) / texsize;
float x1 = rx + shadow.getOffsetX();
float y1 = ry + shadow.getOffsetY();
float x2 = x1 + rw;
float y2 = y1 + rh;
g.setPaint(toPrismColor(shadow.getColor(), alpha));
g.drawTextureRaw(dtex,
x1-r, y1-r, x1+r, y1+r,
t1, t1, t2, t2);
g.drawTextureRaw(dtex,
x2-r, y1-r, x2+r, y1+r,
t2, t1, t1, t2);
g.drawTextureRaw(dtex,
x2-r, y2-r, x2+r, y2+r,
t2, t2, t1, t1);
g.drawTextureRaw(dtex,
x1-r, y2-r, x1+r, y2+r,
t1, t2, t2, t1);
g.drawTextureRaw(dtex,
x1+r, y1+r, x2-r, y2-r,
t2, t2, t2, t2);
g.drawTextureRaw(dtex,
x1-r, y1+r, x1+r, y2-r,
t1, t2, t2, t2);
g.drawTextureRaw(dtex,
x2-r, y1+r, x2+r, y2-r,
t2, t2, t1, t2);
g.drawTextureRaw(dtex,
x1+r, y1-r, x2-r, y1+r,
t2, t1, t2, t2);
g.drawTextureRaw(dtex,
x1+r, y2-r, x2-r, y2+r,
t2, t2, t2, t1);
}
private static void fillGaussian(byte[] pixels, int dim,
float r, float spread,
boolean inner)
{
float sigma = r / 3;
float sigma22 = 2 * sigma * sigma;
if (sigma22 < Float.MIN_VALUE) {
sigma22 = Float.MIN_VALUE;
}
float kvals[] = new float[dim];
int center = (dim+1)/2;
float total = 0.0f;
for (int i = 0; i < kvals.length; i++) {
int d = center - i;
total += (float) Math.exp(-(d * d) / sigma22);
kvals[i] = total;
}
for (int i = 0; i < kvals.length; i++) {
kvals[i] /= total;
}
for (int y = 0; y < dim; y++) {
for (int x = 0; x < dim; x++) {
float v = kvals[y] * kvals[x];
if (inner) {
v = 1.0f - v;
}
int a = (int) (v * 255);
if (a < 0) a = 0; else if (a > 255) a = 255;
pixels[y*dim+x] = (byte)a;
}
}
}
private static Color toPrismColor(Color4f decoraColor, float alpha) {
float r = decoraColor.getRed();
float g = decoraColor.getGreen();
float b = decoraColor.getBlue();
float a = decoraColor.getAlpha() * alpha;
return new Color(r, g, b, a);
}
private EffectUtil() {
}
}
