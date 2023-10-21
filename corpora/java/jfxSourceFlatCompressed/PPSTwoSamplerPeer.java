package com.sun.scenario.effect.impl.prism.ps;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.prism.Texture;
import com.sun.prism.ps.Shader;
import com.sun.prism.ps.ShaderGraphics;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.Filterable;
import com.sun.scenario.effect.FloatMap;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.scenario.effect.impl.prism.PrTexture;
public abstract class PPSTwoSamplerPeer extends PPSEffectPeer {
private Shader shader;
protected PPSTwoSamplerPeer(FilterContext fctx, Renderer r, String shaderName) {
super(fctx, r, shaderName);
}
@Override
public void dispose() {
if (shader != null) {
shader.dispose();
}
}
@Override
ImageData filterImpl(ImageData... inputs) {
final Rectangle dstBounds = getDestBounds();
final int dstw = dstBounds.width;
final int dsth = dstBounds.height;
PPSRenderer renderer = getRenderer();
PPSDrawable dst = renderer.getCompatibleImage(dstw, dsth);
if (dst == null) {
renderer.markLost();
return new ImageData(getFilterContext(), dst, dstBounds);
}
setDestNativeBounds(dst.getPhysicalWidth(), dst.getPhysicalHeight());
Filterable src0F = inputs[0].getUntransformedImage();
final PrTexture src0Tex = (PrTexture) src0F;
Rectangle src0Bounds = inputs[0].getUntransformedBounds();
BaseTransform src0Transform = inputs[0].getTransform();
setInputBounds(0, src0Bounds);
setInputTransform(0, src0Transform);
setInputNativeBounds(0, src0Tex.getNativeBounds());
final PrTexture src1Tex;
final float[] src1Rect = new float[8];
int src1Coords;
if (inputs.length > 1) {
Filterable src1F = inputs[1].getUntransformedImage();
src1Tex = (PrTexture) src1F;
if (src1Tex == null) {
renderer.markLost();
return new ImageData(getFilterContext(), dst, dstBounds);
}
Rectangle src1Bounds = inputs[1].getUntransformedBounds();
BaseTransform src1Transform = inputs[1].getTransform();
setInputBounds(1, src1Bounds);
setInputTransform(1, src1Transform);
setInputNativeBounds(1, src1Tex.getNativeBounds());
src1Coords = getTextureCoordinates(1, src1Rect,
src1Bounds.x, src1Bounds.y,
src1F.getPhysicalWidth(),
src1F.getPhysicalHeight(),
dstBounds,
src1Transform);
} else {
FloatMap map = (FloatMap)getSamplerData(1);
src1Tex = (PrTexture)map.getAccelData(getFilterContext());
if (src1Tex == null) {
renderer.markLost();
return new ImageData(getFilterContext(), dst, dstBounds);
}
Rectangle b = new Rectangle(map.getWidth(), map.getHeight());
Rectangle nb = src1Tex.getNativeBounds();
setInputBounds(1, b);
setInputNativeBounds(1, nb);
src1Rect[0] = src1Rect[1] = 0f;
src1Rect[2] = ((float) b.width) / nb.width;
src1Rect[3] = ((float) b.height) / nb.height;
src1Coords = 4;
}
float[] src0Rect = new float[8];
int src0Coords = getTextureCoordinates(0, src0Rect,
src0Bounds.x, src0Bounds.y,
src0F.getPhysicalWidth(),
src0F.getPhysicalHeight(),
dstBounds,
src0Transform);
ShaderGraphics g = dst.createGraphics();
if (g == null) {
renderer.markLost();
return new ImageData(getFilterContext(), dst, dstBounds);
}
if (shader == null) {
shader = createShader();
}
if (shader == null || !shader.isValid()) {
renderer.markLost();
return new ImageData(getFilterContext(), dst, dstBounds);
}
g.setExternalShader(shader);
updateShader(shader);
float dx1 = 0;
float dy1 = 0;
float dx2 = dstw;
float dy2 = dsth;
Texture ptex0 = src0Tex.getTextureObject();
if (ptex0 == null) {
renderer.markLost();
return new ImageData(getFilterContext(), dst, dstBounds);
}
Texture ptex1 = src1Tex.getTextureObject();
if (ptex1 == null) {
renderer.markLost();
return new ImageData(getFilterContext(), dst, dstBounds);
}
float t0xoff = ((float)ptex0.getContentX()) / ptex0.getPhysicalWidth();
float t0yoff = ((float)ptex0.getContentY()) / ptex0.getPhysicalHeight();
float t0x11 = t0xoff + src0Rect[0];
float t0y11 = t0yoff + src0Rect[1];
float t0x22 = t0xoff + src0Rect[2];
float t0y22 = t0yoff + src0Rect[3];
float t1xoff = ((float)ptex1.getContentX()) / ptex1.getPhysicalWidth();
float t1yoff = ((float)ptex1.getContentY()) / ptex1.getPhysicalHeight();
float t1x11 = t1xoff + src1Rect[0];
float t1y11 = t1yoff + src1Rect[1];
float t1x22 = t1xoff + src1Rect[2];
float t1y22 = t1yoff + src1Rect[3];
if (src0Coords < 8 && src1Coords < 8) {
g.drawTextureRaw2(ptex0, ptex1,
dx1, dy1, dx2, dy2,
t0x11, t0y11, t0x22, t0y22,
t1x11, t1y11, t1x22, t1y22);
} else {
float t0x21, t0y21, t0x12, t0y12;
float t1x21, t1y21, t1x12, t1y12;
if (src0Coords < 8) {
t0x21 = t0x22;
t0y21 = t0y11;
t0x12 = t0x11;
t0y12 = t0y22;
} else {
t0x21 = t0xoff + src0Rect[4];
t0y21 = t0yoff + src0Rect[5];
t0x12 = t0xoff + src0Rect[6];
t0y12 = t0yoff + src0Rect[7];
}
if (src1Coords < 8) {
t1x21 = t1x22;
t1y21 = t1y11;
t1x12 = t1x11;
t1y12 = t1y22;
} else {
t1x21 = t1xoff + src1Rect[4];
t1y21 = t1yoff + src1Rect[5];
t1x12 = t1xoff + src1Rect[6];
t1y12 = t1yoff + src1Rect[7];
}
g.drawMappedTextureRaw2(ptex0, ptex1,
dx1, dy1, dx2, dy2,
t0x11, t0y11, t0x21, t0y21,
t0x12, t0y12, t0x22, t0y22,
t1x11, t1y11, t1x21, t1y21,
t1x12, t1y12, t1x22, t1y22);
}
g.setExternalShader(null);
if (inputs.length <= 1) {
src1Tex.unlock();
}
return new ImageData(getFilterContext(), dst, dstBounds);
}
}
