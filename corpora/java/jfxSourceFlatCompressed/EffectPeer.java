package com.sun.scenario.effect.impl;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.Effect.AccelType;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.NoninvertibleTransformException;
import com.sun.scenario.effect.impl.state.RenderState;
public abstract class EffectPeer<T extends RenderState> {
private final FilterContext fctx;
private final Renderer renderer;
private final String uniqueName;
private Effect effect;
private T renderState;
private int pass;
protected EffectPeer(FilterContext fctx, Renderer renderer, String uniqueName) {
if (fctx == null) {
throw new IllegalArgumentException("FilterContext must be non-null");
}
this.fctx = fctx;
this.renderer = renderer;
this.uniqueName = uniqueName;
}
public boolean isImageDataCompatible(ImageData id) {
return getRenderer().isImageDataCompatible(id);
}
public abstract ImageData filter(Effect effect,
T renderState,
BaseTransform transform,
Rectangle outputClip,
ImageData... inputs);
public void dispose() {
}
public AccelType getAccelType() {
return renderer.getAccelType();
}
protected final FilterContext getFilterContext() {
return fctx;
}
protected Renderer getRenderer() {
return renderer;
}
public String getUniqueName() {
return uniqueName;
}
protected Effect getEffect() {
return effect;
}
protected void setEffect(Effect effect) {
this.effect = effect;
}
protected T getRenderState() {
return renderState;
}
protected void setRenderState(T renderState) {
this.renderState = renderState;
}
public final int getPass() {
return pass;
}
public void setPass(int pass) {
this.pass = pass;
}
private final Rectangle[] inputBounds = new Rectangle[2];
protected final Rectangle getInputBounds(int inputIndex) {
return inputBounds[inputIndex];
}
protected final void setInputBounds(int inputIndex, Rectangle r) {
inputBounds[inputIndex] = r;
}
private final BaseTransform[] inputTransforms = new BaseTransform[2];
protected final BaseTransform getInputTransform(int inputIndex) {
return inputTransforms[inputIndex];
}
protected final void setInputTransform(int inputIndex, BaseTransform tx) {
inputTransforms[inputIndex] = tx;
}
private final Rectangle[] inputNativeBounds = new Rectangle[2];
protected final Rectangle getInputNativeBounds(int inputIndex) {
return inputNativeBounds[inputIndex];
}
protected final void setInputNativeBounds(int inputIndex, Rectangle r) {
inputNativeBounds[inputIndex] = r;
}
public Rectangle getResultBounds(BaseTransform transform,
Rectangle outputClip,
ImageData... inputDatas)
{
return getEffect().getResultBounds(transform, outputClip, inputDatas);
}
protected float[] getSourceRegion(int inputIndex)
{
return getSourceRegion(getInputBounds(inputIndex),
getInputNativeBounds(inputIndex),
getDestBounds());
}
static float[] getSourceRegion(Rectangle srcBounds,
Rectangle srcNativeBounds,
Rectangle dstBounds)
{
float x1 = dstBounds.x - srcBounds.x;
float y1 = dstBounds.y - srcBounds.y;
float x2 = x1 + dstBounds.width;
float y2 = y1 + dstBounds.height;
float sw = srcNativeBounds.width;
float sh = srcNativeBounds.height;
return new float[] {x1 / sw, y1 / sh, x2 / sw, y2 / sh};
}
public int getTextureCoordinates(int inputIndex, float coords[],
float srcX, float srcY,
float srcNativeWidth,
float srcNativeHeight,
Rectangle dstBounds,
BaseTransform transform)
{
return getTextureCoordinates(coords,
srcX, srcY,
srcNativeWidth, srcNativeHeight,
dstBounds, transform);
}
public static int getTextureCoordinates(float coords[],
float srcX, float srcY,
float srcNativeWidth,
float srcNativeHeight,
Rectangle dstBounds,
BaseTransform transform)
{
coords[0] = dstBounds.x;
coords[1] = dstBounds.y;
coords[2] = coords[0] + dstBounds.width;
coords[3] = coords[1] + dstBounds.height;
int numCoords;
if (transform.isTranslateOrIdentity()) {
srcX += (float) transform.getMxt();
srcY += (float) transform.getMyt();
numCoords = 4;
} else {
coords[4] = coords[2];
coords[5] = coords[1];
coords[6] = coords[0];
coords[7] = coords[3];
numCoords = 8;
try {
transform.inverseTransform(coords, 0, coords, 0, 4);
} catch (NoninvertibleTransformException e) {
coords[0] = coords[1] = coords[2] = coords[4] = 0f;
return 4;
}
}
for (int i = 0; i < numCoords; i += 2) {
coords[i ] = (coords[i ] - srcX) / srcNativeWidth;
coords[i+1] = (coords[i+1] - srcY) / srcNativeHeight;
}
return numCoords;
}
private Rectangle destBounds;
protected final void setDestBounds(Rectangle r) {
destBounds = r;
}
protected final Rectangle getDestBounds() {
return destBounds;
}
private final Rectangle destNativeBounds = new Rectangle();
protected final Rectangle getDestNativeBounds() {
return destNativeBounds;
}
protected final void setDestNativeBounds(int w, int h) {
destNativeBounds.width = w;
destNativeBounds.height = h;
}
protected Object getSamplerData(int i) {
return null;
}
protected boolean isOriginUpperLeft() {
return (getAccelType() != Effect.AccelType.OPENGL);
}
}
