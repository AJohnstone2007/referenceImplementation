package com.sun.scenario.effect;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.DirtyRegionPool;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.scenario.effect.impl.state.GaussianShadowState;
import com.sun.scenario.effect.impl.state.LinearConvolveKernel;
public class GaussianShadow extends AbstractShadow {
private GaussianShadowState state = new GaussianShadowState();
public GaussianShadow() {
this(10f);
}
public GaussianShadow(float radius) {
this(radius, Color4f.BLACK);
}
public GaussianShadow(float radius, Color4f color) {
this(radius, color, DefaultInput);
}
public GaussianShadow(float radius, Color4f color, Effect input) {
super(input);
state.setRadius(radius);
state.setShadowColor(color);
}
@Override
LinearConvolveKernel getState() {
return state;
}
@Override
public AccelType getAccelType(FilterContext fctx) {
return Renderer.getRenderer(fctx).getAccelType();
}
public final Effect getInput() {
return getInputs().get(0);
}
public void setInput(Effect input) {
setInput(0, input);
}
public float getRadius() {
return state.getRadius();
}
public void setRadius(float radius) {
float old = state.getRadius();
state.setRadius(radius);
}
public float getHRadius() {
return state.getHRadius();
}
public void setHRadius(float hradius) {
float old = state.getHRadius();
state.setHRadius(hradius);
}
public float getVRadius() {
return state.getVRadius();
}
public void setVRadius(float vradius) {
float old = state.getVRadius();
state.setVRadius(vradius);
}
public float getSpread() {
return state.getSpread();
}
public void setSpread(float spread) {
float old = state.getSpread();
state.setSpread(spread);
}
public Color4f getColor() {
return state.getShadowColor();
}
public void setColor(Color4f color) {
Color4f old = state.getShadowColor();
state.setShadowColor(color);
}
public float getGaussianRadius() {
return getRadius();
}
public float getGaussianWidth() {
return getHRadius() * 2.0f + 1.0f;
}
public float getGaussianHeight() {
return getVRadius() * 2.0f + 1.0f;
}
public void setGaussianRadius(float r) {
setRadius(r);
}
public void setGaussianWidth(float w) {
setHRadius(w < 1.0f ? 0.0f : ((w - 1.0f) / 2.0f));
}
public void setGaussianHeight(float h) {
setVRadius(h < 1.0f ? 0.0f : ((h - 1.0f) / 2.0f));
}
public ShadowMode getMode() {
return ShadowMode.GAUSSIAN;
}
public AbstractShadow implFor(ShadowMode mode) {
int passes = 0;
switch (mode) {
case GAUSSIAN:
return this;
case ONE_PASS_BOX:
passes = 1;
break;
case TWO_PASS_BOX:
passes = 2;
break;
case THREE_PASS_BOX:
passes = 3;
break;
}
BoxShadow box = new BoxShadow();
box.setInput(getInput());
box.setGaussianWidth(getGaussianWidth());
box.setGaussianHeight(getGaussianHeight());
box.setColor(getColor());
box.setPasses(passes);
box.setSpread(getSpread());
return box;
}
@Override
public BaseBounds getBounds(BaseTransform transform, Effect defaultInput) {
BaseBounds r = super.getBounds(null, defaultInput);
int hpad = state.getPad(0);
int vpad = state.getPad(1);
RectBounds ret = new RectBounds(r.getMinX(), r.getMinY(), r.getMaxX(), r.getMaxY());
ret.grow(hpad, vpad);
return transformBounds(transform, ret);
}
@Override
public Rectangle getResultBounds(BaseTransform transform,
Rectangle outputClip,
ImageData... inputDatas)
{
Rectangle r = super.getResultBounds(transform, outputClip, inputDatas);
int hpad = state.getPad(0);
int vpad = state.getPad(1);
Rectangle ret = new Rectangle(r);
ret.grow(hpad, vpad);
return ret;
}
@Override
public boolean reducesOpaquePixels() {
return true;
}
@Override
public DirtyRegionContainer getDirtyRegions(Effect defaultInput, DirtyRegionPool regionPool) {
Effect di = getDefaultedInput(0, defaultInput);
DirtyRegionContainer drc = di.getDirtyRegions(defaultInput, regionPool);
drc.grow(state.getPad(0), state.getPad(1));
return drc;
}
}
