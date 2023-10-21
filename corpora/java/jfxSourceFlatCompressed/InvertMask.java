package com.sun.scenario.effect;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.DirtyRegionPool;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.impl.state.RenderState;
public class InvertMask extends CoreEffect<RenderState> {
private int pad;
private int xoff;
private int yoff;
public InvertMask() {
this(10);
}
public InvertMask(Effect input) {
this(10, input);
}
public InvertMask(int pad) {
this(pad, DefaultInput);
}
public InvertMask(int pad, Effect input) {
super(input);
setPad(pad);
updatePeerKey("InvertMask");
}
public final Effect getInput() {
return getInputs().get(0);
}
public void setInput(Effect input) {
setInput(0, input);
}
public int getPad() {
return pad;
}
public void setPad(int pad) {
if (pad < 0) {
throw new IllegalArgumentException("Pad value must be non-negative");
}
int old = this.pad;
this.pad = pad;
}
public int getOffsetX() {
return xoff;
}
public void setOffsetX(int xoff) {
int old = this.xoff;
this.xoff = xoff;
}
public int getOffsetY() {
return yoff;
}
public void setOffsetY(int yoff) {
float old = this.yoff;
this.yoff = yoff;
}
@Override
public BaseBounds getBounds(BaseTransform transform, Effect defaultInput) {
BaseBounds bounds = super.getBounds(BaseTransform.IDENTITY_TRANSFORM, defaultInput);
BaseBounds ret = new RectBounds(bounds.getMinX(), bounds.getMinY(),
bounds.getMaxX(), bounds.getMaxY());
((RectBounds) ret).grow(pad, pad);
if (!transform.isIdentity()) {
ret = transformBounds(transform, ret);
}
return ret;
}
@Override
public Rectangle getResultBounds(BaseTransform transform,
Rectangle outputClip,
ImageData... inputDatas)
{
Rectangle r = super.getResultBounds(transform, outputClip, inputDatas);
Rectangle ret = new Rectangle(r);
ret.grow(pad, pad);
return ret;
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
return EffectCoordinateSpace.UserSpace;
}
@Override
public BaseTransform getInputTransform(BaseTransform filterTransform) {
return BaseTransform.IDENTITY_TRANSFORM;
}
@Override
public BaseTransform getResultTransform(BaseTransform filterTransform) {
return filterTransform;
}
@Override
public Rectangle getInputClip(int i, Rectangle filterClip) {
if (filterClip != null) {
if (pad != 0) {
filterClip = new Rectangle(filterClip);
filterClip.grow(pad, pad);
}
}
return filterClip;
}
};
}
@Override
public boolean reducesOpaquePixels() {
return true;
}
@Override
public DirtyRegionContainer getDirtyRegions(Effect defaultInput, DirtyRegionPool regionPool) {
Effect di = getDefaultedInput(0, defaultInput);
DirtyRegionContainer drc = di.getDirtyRegions(defaultInput, regionPool);
if (xoff != 0 || yoff != 0) {
for (int i = 0; i < drc.size(); i++) {
drc.getDirtyRegion(i).translate(xoff, yoff, 0);
}
}
return drc;
}
}
