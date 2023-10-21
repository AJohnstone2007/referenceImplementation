package com.sun.scenario.effect;
import com.sun.scenario.effect.impl.state.ZoomRadialBlurState;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.DirtyRegionPool;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.impl.state.RenderState;
public class ZoomRadialBlur extends CoreEffect<RenderState> {
private int r;
private float centerX;
private float centerY;
private final ZoomRadialBlurState state = new ZoomRadialBlurState(this);
public ZoomRadialBlur() {
this(1);
}
public ZoomRadialBlur(int radius) {
this(radius, DefaultInput);
}
public ZoomRadialBlur(int radius, Effect input) {
super(input);
setRadius(radius);
}
@Override
Object getState() {
return state;
}
public final Effect getInput() {
return getInputs().get(0);
}
public void setInput(Effect input) {
setInput(0, input);
}
public int getRadius() {
return r;
}
public void setRadius(int radius) {
if (radius < 1 || radius > 64) {
throw new IllegalArgumentException("Radius must be in the range [1,64]");
}
int old = this.r;
this.r = radius;
state.invalidateDeltas();
updatePeer();
}
private void updatePeer() {
int psize = 4 + r - (r%4);
updatePeerKey("ZoomRadialBlur", psize);
}
public float getCenterX() {
return centerX;
}
public void setCenterX(float centerX) {
float old = this.centerX;
this.centerX = centerX;
}
public float getCenterY() {
return centerY;
}
public void setCenterY(float centerY) {
float old = this.centerY;
this.centerY = centerY;
}
@Override
public ImageData filterImageDatas(FilterContext fctx,
BaseTransform transform,
Rectangle outputClip,
RenderState rstate,
ImageData... inputs)
{
Rectangle bnd = inputs[0].getUntransformedBounds();
state.updateDeltas(1f/bnd.width, 1f/bnd.height);
return super.filterImageDatas(fctx, transform, outputClip, rstate, inputs);
}
@Override
public RenderState getRenderState(FilterContext fctx,
BaseTransform transform,
Rectangle outputClip,
Object renderHelper,
Effect defaultInput)
{
return RenderState.UserSpaceRenderState;
}
@Override
public boolean reducesOpaquePixels() {
return true;
}
@Override
public DirtyRegionContainer getDirtyRegions(Effect defaultInput, DirtyRegionPool regionPool) {
Effect di = getDefaultedInput(0, defaultInput);
DirtyRegionContainer drc = di.getDirtyRegions(defaultInput, regionPool);
int radius = getRadius();
drc.grow(radius, radius);
return drc;
}
}
