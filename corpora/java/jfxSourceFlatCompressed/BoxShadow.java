package com.sun.scenario.effect;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.DirtyRegionPool;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.scenario.effect.impl.state.BoxShadowState;
import com.sun.scenario.effect.impl.state.LinearConvolveKernel;
public class BoxShadow extends AbstractShadow {
private final BoxShadowState state = new BoxShadowState();
public BoxShadow() {
this(1, 1);
}
public BoxShadow(int hsize, int vsize) {
this(hsize, vsize, 1, DefaultInput);
}
public BoxShadow(int hsize, int vsize, int passes) {
this(hsize, vsize, passes, DefaultInput);
}
public BoxShadow(int hsize, int vsize, int passes, Effect input) {
super(input);
setHorizontalSize(hsize);
setVerticalSize(vsize);
setPasses(passes);
setColor(Color4f.BLACK);
setSpread(0f);
}
@Override
LinearConvolveKernel getState() {
return state;
}
public final Effect getInput() {
return getInputs().get(0);
}
public void setInput(Effect input) {
setInput(0, input);
}
public int getHorizontalSize() {
return state.getHsize();
}
public final void setHorizontalSize(int hsize) {
state.setHsize(hsize);
}
public int getVerticalSize() {
return state.getVsize();
}
public final void setVerticalSize(int vsize) {
state.setVsize(vsize);
}
public int getPasses() {
return state.getBlurPasses();
}
public final void setPasses(int passes) {
state.setBlurPasses(passes);
}
public Color4f getColor() {
return state.getShadowColor();
}
public final void setColor(Color4f color) {
state.setShadowColor(color);
}
public float getSpread() {
return state.getSpread();
}
public final void setSpread(float spread) {
state.setSpread(spread);
}
public float getGaussianRadius() {
float d = (getHorizontalSize() + getVerticalSize()) / 2.0f;
d *= 3.0f;
return (d < 1.0f ? 0.0f : ((d - 1.0f) / 2.0f));
}
public float getGaussianWidth() {
return getHorizontalSize() * 3.0f;
}
public float getGaussianHeight() {
return getVerticalSize() * 3.0f;
}
public void setGaussianRadius(float r) {
float d = r * 2.0f + 1.0f;
setGaussianWidth(d);
setGaussianHeight(d);
}
public void setGaussianWidth(float w) {
w /= 3.0f;
setHorizontalSize(Math.round(w));
}
public void setGaussianHeight(float h) {
h /= 3.0f;
setVerticalSize(Math.round(h));
}
public ShadowMode getMode() {
switch (getPasses()) {
case 1:
return ShadowMode.ONE_PASS_BOX;
case 2:
return ShadowMode.TWO_PASS_BOX;
default:
return ShadowMode.THREE_PASS_BOX;
}
}
public AbstractShadow implFor(ShadowMode mode) {
switch (mode) {
case GAUSSIAN:
GaussianShadow gs = new GaussianShadow();
gs.setInput(getInput());
gs.setGaussianWidth(getGaussianWidth());
gs.setGaussianHeight(getGaussianHeight());
gs.setColor(getColor());
gs.setSpread(getSpread());
return gs;
case ONE_PASS_BOX:
setPasses(1);
break;
case TWO_PASS_BOX:
setPasses(2);
break;
case THREE_PASS_BOX:
setPasses(3);
break;
}
return this;
}
@Override
public AccelType getAccelType(FilterContext fctx) {
return Renderer.getRenderer(fctx).getAccelType();
}
@Override
public BaseBounds getBounds(BaseTransform transform, Effect defaultInput) {
BaseBounds r = super.getBounds(null, defaultInput);
int hgrow = state.getKernelSize(0) / 2;
int vgrow = state.getKernelSize(1) / 2;
RectBounds ret = new RectBounds(r.getMinX(), r.getMinY(), r.getMaxX(), r.getMaxY());
ret.grow(hgrow, vgrow);
return transformBounds(transform, ret);
}
@Override
public Rectangle getResultBounds(BaseTransform transform,
Rectangle outputClip,
ImageData... inputDatas)
{
Rectangle r = inputDatas[0].getUntransformedBounds();
r = state.getResultBounds(r, 0);
r = state.getResultBounds(r, 1);
r.intersectWith(outputClip);
return r;
}
@Override
public boolean reducesOpaquePixels() {
return true;
}
@Override
public DirtyRegionContainer getDirtyRegions(Effect defaultInput, DirtyRegionPool regionPool) {
Effect di = getDefaultedInput(0, defaultInput);
DirtyRegionContainer drc = di.getDirtyRegions(defaultInput, regionPool);
drc.grow(state.getKernelSize(0) / 2, state.getKernelSize(1) / 2);
return drc;
}
}
