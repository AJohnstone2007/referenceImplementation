package com.sun.prism.impl.ps;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.transform.Affine2D;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.AffineBase;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.NoninvertibleTransformException;
import com.sun.javafx.sg.prism.NGCamera;
import com.sun.javafx.sg.prism.NGPerspectiveCamera;
import com.sun.prism.Image;
import com.sun.prism.PixelFormat;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture;
import com.sun.prism.Texture.Usage;
import com.sun.prism.Texture.WrapMode;
import com.sun.prism.impl.BufferUtil;
import com.sun.prism.paint.Color;
import com.sun.prism.paint.Gradient;
import com.sun.prism.paint.ImagePattern;
import com.sun.prism.paint.LinearGradient;
import com.sun.prism.paint.RadialGradient;
import com.sun.prism.paint.Stop;
import com.sun.prism.ps.Shader;
import com.sun.prism.ps.ShaderGraphics;
import java.util.WeakHashMap;
class PaintHelper {
static final int MULTI_MAX_FRACTIONS = 12;
private static final int MULTI_TEXTURE_SIZE = 16;
private static final int MULTI_CACHE_SIZE = 256;
private static final int GTEX_CLR_TABLE_SIZE = 101;
private static final int GTEX_CLR_TABLE_MIRRORED_SIZE =
GTEX_CLR_TABLE_SIZE * 2 - 1;
private static final float FULL_TEXEL_Y = 1.0f / MULTI_CACHE_SIZE;
private static final float HALF_TEXEL_Y = FULL_TEXEL_Y / 2.0f;
private static final FloatBuffer stopVals =
BufferUtil.newFloatBuffer(MULTI_MAX_FRACTIONS * 4);
private static final ByteBuffer bgraColors =
BufferUtil.newByteBuffer(MULTI_TEXTURE_SIZE*4);
private static final Image colorsImg =
Image.fromByteBgraPreData(bgraColors, MULTI_TEXTURE_SIZE, 1);
private static final int[] previousColors = new int[MULTI_TEXTURE_SIZE];
private static final byte gtexColors[] = new byte[GTEX_CLR_TABLE_MIRRORED_SIZE * 4];
private static final Image gtexImg =
Image.fromByteBgraPreData(ByteBuffer.wrap(gtexColors), GTEX_CLR_TABLE_MIRRORED_SIZE, 1);
private static long cacheOffset = -1;
private static Texture gradientCacheTexture = null;
private static Texture gtexCacheTexture = null;
private static final WeakHashMap<Gradient, Void> gradientMap = new WeakHashMap<>();
private static final Affine2D scratchXform2D = new Affine2D();
private static final Affine3D scratchXform3D = new Affine3D();
private static float len(float dx, float dy) {
return ((dx == 0f) ? Math.abs(dy)
: ((dy == 0f) ? Math.abs(dx)
: (float)Math.sqrt(dx * dx + dy * dy)));
}
static void initGradientTextures(ShaderGraphics g) {
cacheOffset = -1;
gradientMap.clear();
gradientCacheTexture = g.getResourceFactory().createTexture(
PixelFormat.BYTE_BGRA_PRE, Usage.DEFAULT, WrapMode.CLAMP_TO_EDGE,
MULTI_TEXTURE_SIZE, MULTI_CACHE_SIZE);
gradientCacheTexture.setLinearFiltering(true);
gradientCacheTexture.contentsUseful();
gradientCacheTexture.makePermanent();
gtexCacheTexture = g.getResourceFactory().createTexture(
PixelFormat.BYTE_BGRA_PRE, Usage.DEFAULT, WrapMode.CLAMP_NOT_NEEDED,
GTEX_CLR_TABLE_MIRRORED_SIZE, MULTI_CACHE_SIZE);
gtexCacheTexture.setLinearFiltering(true);
gtexCacheTexture.contentsUseful();
gtexCacheTexture.makePermanent();
}
static Texture getGradientTexture(ShaderGraphics g, Gradient paint) {
if (gradientCacheTexture == null || gradientCacheTexture.isSurfaceLost()) {
initGradientTextures(g);
}
gradientCacheTexture.lock();
return gradientCacheTexture;
}
static Texture getWrapGradientTexture(ShaderGraphics g) {
if (gtexCacheTexture == null || gtexCacheTexture.isSurfaceLost()) {
initGradientTextures(g);
}
gtexCacheTexture.lock();
return gtexCacheTexture;
}
private static void stopsToImage(List<Stop> stops, int numStops)
{
if (numStops > MULTI_MAX_FRACTIONS) {
throw new RuntimeException(
"Maximum number of gradient stops exceeded " +
"(paint uses " + numStops +
" stops, but max is " + MULTI_MAX_FRACTIONS + ")");
}
bgraColors.clear();
Color lastColor = null;
for (int i = 0; i < MULTI_TEXTURE_SIZE; i++) {
Color c;
if (i < numStops) {
c = stops.get(i).getColor();
lastColor = c;
} else {
c = lastColor;
}
c.putBgraPreBytes(bgraColors);
int argb = c.getIntArgbPre();
if (argb != previousColors[i]) {
previousColors[i] = argb;
}
}
bgraColors.rewind();
}
private static void insertInterpColor(byte colors[], int index,
Color c0, Color c1, float t)
{
t *= 255.0f;
float u = 255.0f - t;
index *= 4;
colors[index + 0] = (byte) (c0.getBluePremult() * u + c1.getBluePremult() * t + 0.5f);
colors[index + 1] = (byte) (c0.getGreenPremult() * u + c1.getGreenPremult() * t + 0.5f);
colors[index + 2] = (byte) (c0.getRedPremult() * u + c1.getRedPremult() * t + 0.5f);
colors[index + 3] = (byte) (c0.getAlpha() * u + c1.getAlpha() * t + 0.5f);
}
private static Color PINK = new Color(1.0f, 0.078431375f, 0.5764706f, 1.0f);
private static void stopsToGtexImage(List<Stop> stops, int numStops) {
Color lastColor = stops.get(0).getColor();
float offset = stops.get(0).getOffset();
int lastIndex = (int) (offset * (GTEX_CLR_TABLE_SIZE - 1) + 0.5f);
insertInterpColor(gtexColors, 0, lastColor, lastColor, 0.0f);
for (int i = 1; i < numStops; i++) {
Color color = stops.get(i).getColor();
offset = stops.get(i).getOffset();
int index = (int) (offset * (GTEX_CLR_TABLE_SIZE - 1) + 0.5f);
if (index == lastIndex) {
insertInterpColor(gtexColors, index, lastColor, color, 0.5f);
} else {
for (int j = lastIndex+1; j <= index; j++) {
float t = j - lastIndex;
t /= (index - lastIndex);
insertInterpColor(gtexColors, j, lastColor, color, t);
}
}
lastIndex = index;
lastColor = color;
}
for (int i = 1; i < GTEX_CLR_TABLE_SIZE; i++) {
int j = (GTEX_CLR_TABLE_SIZE - 1 + i) * 4;
int k = (GTEX_CLR_TABLE_SIZE - 1 - i) * 4;
gtexColors[j + 0] = gtexColors[k + 0];
gtexColors[j + 1] = gtexColors[k + 1];
gtexColors[j + 2] = gtexColors[k + 2];
gtexColors[j + 3] = gtexColors[k + 3];
}
}
public static int initGradient(Gradient paint) {
long offset = paint.getGradientOffset();
if (gradientMap.containsKey(paint) && offset >= 0 && (offset > cacheOffset - MULTI_CACHE_SIZE)) {
return (int) (offset % MULTI_CACHE_SIZE);
} else {
List<Stop> stops = paint.getStops();
int numStops = paint.getNumStops();
stopsToImage(stops,numStops);
stopsToGtexImage(stops, numStops);
long nextOffset = ++cacheOffset;
paint.setGradientOffset(nextOffset);
int cacheIdx = (int)(nextOffset % MULTI_CACHE_SIZE);
gradientCacheTexture.update(colorsImg, 0, cacheIdx);
gtexCacheTexture.update(gtexImg, 0, cacheIdx);
gradientMap.put(paint, null);
return cacheIdx;
}
}
private static void setMultiGradient(Shader shader,
Gradient paint)
{
List<Stop> stops = paint.getStops();
int numStops = paint.getNumStops();
stopVals.clear();
for (int i = 0; i < MULTI_MAX_FRACTIONS; i++) {
stopVals.put((i < numStops) ?
stops.get(i).getOffset() : 0f);
stopVals.put((i < numStops-1) ?
1f / (stops.get(i+1).getOffset() - stops.get(i).getOffset()) : 0f);
stopVals.put(0f);
stopVals.put(0f);
}
stopVals.rewind();
shader.setConstants("fractions", stopVals, 0, MULTI_MAX_FRACTIONS);
float index_y = initGradient(paint);
shader.setConstant("offset", index_y / (float)MULTI_CACHE_SIZE + HALF_TEXEL_Y);
}
private static void setTextureGradient(Shader shader,
Gradient paint)
{
float cy = initGradient(paint) + 0.5f;
float cx = 0.5f;
float fractmul = 0.0f, clampmul = 0.0f;
switch (paint.getSpreadMethod()) {
case Gradient.PAD:
clampmul = GTEX_CLR_TABLE_SIZE - 1.0f;
break;
case Gradient.REPEAT:
fractmul = GTEX_CLR_TABLE_SIZE - 1.0f;
break;
case Gradient.REFLECT:
fractmul = GTEX_CLR_TABLE_MIRRORED_SIZE - 1.0f;
break;
}
float xscale = 1.0f / gtexCacheTexture.getPhysicalWidth();
float yscale = 1.0f / gtexCacheTexture.getPhysicalHeight();
cx *= xscale;
cy *= yscale;
fractmul *= xscale;
clampmul *= xscale;
shader.setConstant("content", cx, cy, fractmul, clampmul);
}
static void setLinearGradient(ShaderGraphics g,
Shader shader,
LinearGradient paint,
float rx, float ry, float rw, float rh)
{
BaseTransform paintXform = paint.getGradientTransformNoClone();
Affine3D at = scratchXform3D;
g.getPaintShaderTransform(at);
if (paintXform != null) {
at.concatenate(paintXform);
}
float x1 = rx + (paint.getX1() * rw);
float y1 = ry + (paint.getY1() * rh);
float x2 = rx + (paint.getX2() * rw);
float y2 = ry + (paint.getY2() * rh);
float x = x1;
float y = y1;
at.translate(x, y);
x = x2 - x;
y = y2 - y;
double len = len(x, y);
at.rotate(Math.atan2(y, x));
at.scale(len, 1);
double p0, p1, p2;
if (!at.is2D()) {
BaseTransform inv;
try {
inv = at.createInverse();
} catch (NoninvertibleTransformException e) {
at.setToScale(0, 0, 0);
inv = at;
}
NGCamera cam = g.getCameraNoClone();
Vec3d tmpVec = new Vec3d();
PickRay tmpvec = new PickRay();
PickRay ray00 = project(0,0,cam,inv,tmpvec,tmpVec,null);
PickRay ray10 = project(1,0,cam,inv,tmpvec,tmpVec,null);
PickRay ray01 = project(0,1,cam,inv,tmpvec,tmpVec,null);
p0 = ray10.getDirectionNoClone().x - ray00.getDirectionNoClone().x;
p1 = ray01.getDirectionNoClone().x - ray00.getDirectionNoClone().x;
p2 = ray00.getDirectionNoClone().x;
p0 *= -ray00.getOriginNoClone().z;
p1 *= -ray00.getOriginNoClone().z;
p2 *= -ray00.getOriginNoClone().z;
double wv0 = ray10.getDirectionNoClone().z - ray00.getDirectionNoClone().z;
double wv1 = ray01.getDirectionNoClone().z - ray00.getDirectionNoClone().z;
double wv2 = ray00.getDirectionNoClone().z;
shader.setConstant("gradParams", (float)p0, (float)p1, (float)p2, (float)ray00.getOriginNoClone().x);
shader.setConstant("perspVec", (float)wv0, (float)wv1, (float)wv2);
} else {
try {
at.invert();
} catch (NoninvertibleTransformException ex) {
at.setToScale(0, 0, 0);
}
p0 = (float)at.getMxx();
p1 = (float)at.getMxy();
p2 = (float)at.getMxt();
shader.setConstant("gradParams", (float)p0, (float)p1, (float)p2, 0.0f);
shader.setConstant("perspVec", 0.0f, 0.0f, 1.0f);
}
setMultiGradient(shader, paint);
}
static AffineBase getLinearGradientTx(LinearGradient paint,
Shader shader,
BaseTransform renderTx,
float rx, float ry, float rw, float rh)
{
AffineBase ret;
float x1 = paint.getX1();
float y1 = paint.getY1();
float x2 = paint.getX2();
float y2 = paint.getY2();
if (paint.isProportional()) {
x1 = rx + x1 * rw;
y1 = ry + y1 * rh;
x2 = rx + x2 * rw;
y2 = ry + y2 * rh;
}
float dx = x2 - x1;
float dy = y2 - y1;
float len = len(dx, dy);
if (paint.getSpreadMethod() == Gradient.REFLECT) {
len *= 2.0f;
}
BaseTransform paintXform = paint.getGradientTransformNoClone();
if (paintXform.isIdentity() && renderTx.isIdentity()) {
Affine2D at = scratchXform2D;
at.setToTranslation(x1, y1);
at.rotate(dx, dy);
at.scale(len, 1);
ret = at;
} else {
Affine3D at = scratchXform3D;
at.setTransform(renderTx);
at.concatenate(paintXform);
at.translate(x1, y1);
at.rotate(Math.atan2(dy, dx));
at.scale(len, 1);
ret = at;
}
try {
ret.invert();
} catch (NoninvertibleTransformException e) {
scratchXform2D.setToScale(0, 0);
ret = scratchXform2D;
}
setTextureGradient(shader, paint);
return ret;
}
static void setRadialGradient(ShaderGraphics g,
Shader shader,
RadialGradient paint,
float rx, float ry, float rw, float rh)
{
Affine3D at = scratchXform3D;
g.getPaintShaderTransform(at);
float radius = paint.getRadius();
float cx = paint.getCenterX();
float cy = paint.getCenterY();
float fa = paint.getFocusAngle();
float fd = paint.getFocusDistance();
if (fd < 0) {
fd = -fd;
fa = fa+180;
}
fa = (float) Math.toRadians(fa);
if (paint.isProportional()) {
float bcx = rx + (rw / 2f);
float bcy = ry + (rh / 2f);
float scale = Math.min(rw, rh);
cx = (cx - 0.5f) * scale + bcx;
cy = (cy - 0.5f) * scale + bcy;
if (rw != rh && rw != 0f && rh != 0f) {
at.translate(bcx, bcy);
at.scale(rw / scale, rh / scale);
at.translate(-bcx, -bcy);
}
radius = radius * scale;
}
BaseTransform paintXform = paint.getGradientTransformNoClone();
if (paintXform != null) {
at.concatenate(paintXform);
}
at.translate(cx, cy);
at.rotate(fa);
at.scale(radius, radius);
try {
at.invert();
} catch (Exception e) {
at.setToScale(0.0, 0.0, 0.0);
}
if (!at.is2D()) {
NGCamera cam = g.getCameraNoClone();
Vec3d tmpVec = new Vec3d();
PickRay tmpvec = new PickRay();
PickRay ray00 = project(0, 0, cam, at, tmpvec, tmpVec, null);
PickRay ray10 = project(1, 0, cam, at, tmpvec, tmpVec, null);
PickRay ray01 = project(0, 1, cam, at, tmpvec, tmpVec, null);
double p0 = ray10.getDirectionNoClone().x - ray00.getDirectionNoClone().x;
double p1 = ray01.getDirectionNoClone().x - ray00.getDirectionNoClone().x;
double p2 = ray00.getDirectionNoClone().x;
double py0 = ray10.getDirectionNoClone().y - ray00.getDirectionNoClone().y;
double py1 = ray01.getDirectionNoClone().y - ray00.getDirectionNoClone().y;
double py2 = ray00.getDirectionNoClone().y;
p0 *= -ray00.getOriginNoClone().z;
p1 *= -ray00.getOriginNoClone().z;
p2 *= -ray00.getOriginNoClone().z;
py0 *= -ray00.getOriginNoClone().z;
py1 *= -ray00.getOriginNoClone().z;
py2 *= -ray00.getOriginNoClone().z;
double wv0 = ray10.getDirectionNoClone().z - ray00.getDirectionNoClone().z;
double wv1 = ray01.getDirectionNoClone().z - ray00.getDirectionNoClone().z;
double wv2 = ray00.getDirectionNoClone().z;
shader.setConstant("perspVec", (float) wv0, (float) wv1, (float) wv2);
shader.setConstant("m0", (float) p0, (float) p1, (float) p2, (float) ray00.getOriginNoClone().x);
shader.setConstant("m1", (float) py0, (float) py1, (float) py2, (float) ray00.getOriginNoClone().y);
} else {
float m00 = (float) at.getMxx();
float m01 = (float) at.getMxy();
float m02 = (float) at.getMxt();
shader.setConstant("m0", m00, m01, m02, 0.0f);
float m10 = (float) at.getMyx();
float m11 = (float) at.getMyy();
float m12 = (float) at.getMyt();
shader.setConstant("m1", m10, m11, m12, 0.0f);
shader.setConstant("perspVec", 0.0f, 0.0f, 1.0f);
}
fd = (float) Math.min(fd, 0.99f);
float denom = 1.0f - (fd * fd);
float inv_denom = 1.0f / denom;
shader.setConstant("precalc", fd, denom, inv_denom);
setMultiGradient(shader, paint);
}
static AffineBase getRadialGradientTx(RadialGradient paint,
Shader shader,
BaseTransform renderTx,
float rx, float ry, float rw, float rh)
{
Affine3D at = scratchXform3D;
at.setTransform(renderTx);
float radius = paint.getRadius();
float cx = paint.getCenterX();
float cy = paint.getCenterY();
float fa = paint.getFocusAngle();
float fd = paint.getFocusDistance();
if (fd < 0) {
fd = -fd;
fa = fa+180;
}
fa = (float) Math.toRadians(fa);
if (paint.isProportional()) {
float bcx = rx + (rw / 2f);
float bcy = ry + (rh / 2f);
float scale = Math.min(rw, rh);
cx = (cx - 0.5f) * scale + bcx;
cy = (cy - 0.5f) * scale + bcy;
if (rw != rh && rw != 0f && rh != 0f) {
at.translate(bcx, bcy);
at.scale(rw / scale, rh / scale);
at.translate(-bcx, -bcy);
}
radius = radius * scale;
}
if (paint.getSpreadMethod() == Gradient.REFLECT) {
radius *= 2.0f;
}
BaseTransform paintXform = paint.getGradientTransformNoClone();
if (paintXform != null) {
at.concatenate(paintXform);
}
at.translate(cx, cy);
at.rotate(fa);
at.scale(radius, radius);
try {
at.invert();
} catch (Exception e) {
at.setToScale(0.0, 0.0, 0.0);
}
fd = (float) Math.min(fd, 0.99f);
float denom = 1.0f - (fd * fd);
float inv_denom = 1.0f / denom;
shader.setConstant("precalc", fd, denom, inv_denom);
setTextureGradient(shader, paint);
return at;
}
static void setImagePattern(ShaderGraphics g,
Shader shader,
ImagePattern paint,
float rx, float ry, float rw, float rh)
{
float x1 = rx + (paint.getX() * rw);
float y1 = ry + (paint.getY() * rh);
float x2 = x1 + (paint.getWidth() * rw);
float y2 = y1 + (paint.getHeight() * rh);
ResourceFactory rf = g.getResourceFactory();
Image img = paint.getImage();
Texture paintTex = rf.getCachedTexture(img, Texture.WrapMode.REPEAT);
float cx = paintTex.getContentX();
float cy = paintTex.getContentY();
float cw = paintTex.getContentWidth();
float ch = paintTex.getContentHeight();
float texw = paintTex.getPhysicalWidth();
float texh = paintTex.getPhysicalHeight();
paintTex.unlock();
Affine3D at = scratchXform3D;
g.getPaintShaderTransform(at);
BaseTransform paintXform = paint.getPatternTransformNoClone();
if (paintXform != null) {
at.concatenate(paintXform);
}
at.translate(x1, y1);
at.scale(x2 - x1, y2 - y1);
if (cw < texw) {
at.translate(0.5/cw, 0.0);
cx += 0.5f;
}
if (ch < texh) {
at.translate(0.0, 0.5/ch);
cy += 0.5f;
}
try {
at.invert();
} catch (Exception e) {
at.setToScale(0.0, 0.0, 0.0);
}
if (!at.is2D()) {
NGCamera cam = g.getCameraNoClone();
Vec3d tmpVec = new Vec3d();
PickRay tmpvec = new PickRay();
PickRay ray00 = project(0,0,cam,at,tmpvec,tmpVec,null);
PickRay ray10 = project(1,0,cam,at,tmpvec,tmpVec,null);
PickRay ray01 = project(0,1,cam,at,tmpvec,tmpVec,null);
double p0 = ray10.getDirectionNoClone().x - ray00.getDirectionNoClone().x;
double p1 = ray01.getDirectionNoClone().x - ray00.getDirectionNoClone().x;
double p2 = ray00.getDirectionNoClone().x;
double py0 = ray10.getDirectionNoClone().y - ray00.getDirectionNoClone().y;
double py1 = ray01.getDirectionNoClone().y - ray00.getDirectionNoClone().y;
double py2 = ray00.getDirectionNoClone().y;
p0 *= -ray00.getOriginNoClone().z;
p1 *= -ray00.getOriginNoClone().z;
p2 *= -ray00.getOriginNoClone().z;
py0 *= -ray00.getOriginNoClone().z;
py1 *= -ray00.getOriginNoClone().z;
py2 *= -ray00.getOriginNoClone().z;
double wv0 = ray10.getDirectionNoClone().z - ray00.getDirectionNoClone().z;
double wv1 = ray01.getDirectionNoClone().z - ray00.getDirectionNoClone().z;
double wv2 = ray00.getDirectionNoClone().z;
shader.setConstant("perspVec", (float)wv0, (float)wv1, (float)wv2);
shader.setConstant("xParams", (float)p0, (float)p1, (float)p2, (float)ray00.getOriginNoClone().x);
shader.setConstant("yParams", (float)py0, (float)py1, (float)py2, (float)ray00.getOriginNoClone().y);
} else {
float m00 = (float)at.getMxx();
float m01 = (float)at.getMxy();
float m02 = (float)at.getMxt();
shader.setConstant("xParams", m00, m01, m02, 0.0f);
float m10 = (float)at.getMyx();
float m11 = (float)at.getMyy();
float m12 = (float)at.getMyt();
shader.setConstant("yParams", m10, m11, m12, 0.0f);
shader.setConstant("perspVec", 0.0f, 0.0f, 1.0f);
}
cx /= texw;
cy /= texh;
cw /= texw;
ch /= texh;
shader.setConstant("content", cx, cy, cw, ch);
}
static AffineBase getImagePatternTx(ShaderGraphics g,
ImagePattern paint,
Shader shader,
BaseTransform renderTx,
float rx, float ry, float rw, float rh)
{
float px = paint.getX();
float py = paint.getY();
float pw = paint.getWidth();
float ph = paint.getHeight();
if (paint.isProportional()) {
px = rx + px * rw;
py = ry + py * rh;
pw = pw * rw;
ph = ph * rh;
}
ResourceFactory rf = g.getResourceFactory();
Image img = paint.getImage();
Texture paintTex = rf.getCachedTexture(img, Texture.WrapMode.REPEAT);
float cx = paintTex.getContentX();
float cy = paintTex.getContentY();
float cw = paintTex.getContentWidth();
float ch = paintTex.getContentHeight();
float texw = paintTex.getPhysicalWidth();
float texh = paintTex.getPhysicalHeight();
paintTex.unlock();
AffineBase ret;
BaseTransform paintXform = paint.getPatternTransformNoClone();
if (paintXform.isIdentity() && renderTx.isIdentity()) {
Affine2D at = scratchXform2D;
at.setToTranslation(px, py);
at.scale(pw, ph);
ret = at;
} else {
Affine3D at = scratchXform3D;
at.setTransform(renderTx);
at.concatenate(paintXform);
at.translate(px, py);
at.scale(pw, ph);
ret = at;
}
if (cw < texw) {
ret.translate(0.5/cw, 0.0);
cx += 0.5f;
}
if (ch < texh) {
ret.translate(0.0, 0.5/ch);
cy += 0.5f;
}
try {
ret.invert();
} catch (Exception e) {
ret = scratchXform2D;
scratchXform2D.setToScale(0.0, 0.0);
}
cx /= texw;
cy /= texh;
cw /= texw;
ch /= texh;
shader.setConstant("content", cx, cy, cw, ch);
return ret;
}
static PickRay project(float x, float y,
NGCamera cam, BaseTransform inv,
PickRay tmpray, Vec3d tmpvec, Point2D ret)
{
tmpray = cam.computePickRay(x, y, tmpray);
return tmpray.project(inv, cam instanceof NGPerspectiveCamera,
tmpvec, ret);
}
}
