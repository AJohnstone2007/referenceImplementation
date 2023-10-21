package com.sun.scenario.effect;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.DirtyRegionPool;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.impl.state.RenderState;
import com.sun.scenario.effect.light.Light;
public class PhongLighting extends CoreEffect<RenderState> {
private float surfaceScale;
private float diffuseConstant;
private float specularConstant;
private float specularExponent;
private Light light;
public PhongLighting(Light light) {
this(light, new GaussianShadow(10f), DefaultInput);
}
public PhongLighting(Light light, Effect bumpInput, Effect contentInput) {
super(bumpInput, contentInput);
this.surfaceScale = 1f;
this.diffuseConstant = 1f;
this.specularConstant = 1f;
this.specularExponent = 1f;
setLight(light);
}
public final Effect getBumpInput() {
return getInputs().get(0);
}
public void setBumpInput(Effect bumpInput) {
setInput(0, bumpInput);
}
public final Effect getContentInput() {
return getInputs().get(1);
}
private Effect getContentInput(Effect defaultInput) {
return getDefaultedInput(1, defaultInput);
}
public void setContentInput(Effect contentInput) {
setInput(1, contentInput);
}
public Light getLight() {
return light;
}
public void setLight(Light light) {
if (light == null) {
throw new IllegalArgumentException("Light must be non-null");
}
this.light = light;
updatePeerKey("PhongLighting_" + light.getType().name());
}
public float getDiffuseConstant() {
return diffuseConstant;
}
public void setDiffuseConstant(float diffuseConstant) {
if (diffuseConstant < 0f || diffuseConstant > 2f) {
throw new IllegalArgumentException("Diffuse constant must be in the range [0,2]");
}
float old = this.diffuseConstant;
this.diffuseConstant = diffuseConstant;
}
public float getSpecularConstant() {
return specularConstant;
}
public void setSpecularConstant(float specularConstant) {
if (specularConstant < 0f || specularConstant > 2f) {
throw new IllegalArgumentException("Specular constant must be in the range [0,2]");
}
float old = this.specularConstant;
this.specularConstant = specularConstant;
}
public float getSpecularExponent() {
return specularExponent;
}
public void setSpecularExponent(float specularExponent) {
if (specularExponent < 0f || specularExponent > 40f) {
throw new IllegalArgumentException("Specular exponent must be in the range [0,40]");
}
float old = this.specularExponent;
this.specularExponent = specularExponent;
}
public float getSurfaceScale() {
return surfaceScale;
}
public void setSurfaceScale(float surfaceScale) {
if (surfaceScale < 0f || surfaceScale > 10f) {
throw new IllegalArgumentException("Surface scale must be in the range [0,10]");
}
float old = this.surfaceScale;
this.surfaceScale = surfaceScale;
}
@Override
public BaseBounds getBounds(BaseTransform transform,
Effect defaultInput)
{
return getContentInput(defaultInput).getBounds(transform, defaultInput);
}
@Override
public Rectangle getResultBounds(BaseTransform transform,
Rectangle outputClip,
ImageData... inputDatas)
{
return super.getResultBounds(transform, outputClip, inputDatas[1]);
}
@Override
public Point2D transform(Point2D p, Effect defaultInput) {
return getContentInput(defaultInput).transform(p, defaultInput);
}
@Override
public Point2D untransform(Point2D p, Effect defaultInput) {
return getContentInput(defaultInput).untransform(p, defaultInput);
}
@Override
public RenderState getRenderState(FilterContext fctx,
BaseTransform transform,
Rectangle outputClip,
Object renderHelper,
Effect defaultInput)
{
return new RenderState() {
@Override
public EffectCoordinateSpace getEffectTransformSpace() {
return EffectCoordinateSpace.RenderSpace;
}
@Override
public BaseTransform getInputTransform(BaseTransform filterTransform) {
return filterTransform;
}
@Override
public BaseTransform getResultTransform(BaseTransform filterTransform) {
return BaseTransform.IDENTITY_TRANSFORM;
}
@Override
public Rectangle getInputClip(int i, Rectangle filterClip) {
if (i == 0 && filterClip != null) {
Rectangle r = new Rectangle(filterClip);
r.grow(1, 1);
return r;
}
return filterClip;
}
};
}
@Override
public boolean reducesOpaquePixels() {
final Effect contentInput = getContentInput();
return contentInput != null && contentInput.reducesOpaquePixels();
}
@Override
public DirtyRegionContainer getDirtyRegions(Effect defaultInput, DirtyRegionPool regionPool) {
Effect bump = getDefaultedInput(0, defaultInput);
DirtyRegionContainer drc1 = bump.getDirtyRegions(defaultInput, regionPool);
drc1.grow(1, 1);
Effect content = getDefaultedInput(1, defaultInput);
DirtyRegionContainer drc2 = content.getDirtyRegions(defaultInput, regionPool);
drc1.merge(drc2);
regionPool.checkIn(drc2);
return drc1;
}
}
