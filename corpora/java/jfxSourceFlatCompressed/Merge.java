package com.sun.scenario.effect;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.impl.state.RenderState;
public class Merge extends CoreEffect<RenderState> {
public Merge(Effect bottomInput, Effect topInput) {
super(bottomInput, topInput);
updatePeerKey("Merge");
}
public final Effect getBottomInput() {
return getInputs().get(0);
}
public void setBottomInput(Effect bottomInput) {
setInput(0, bottomInput);
}
public final Effect getTopInput() {
return getInputs().get(1);
}
public void setTopInput(Effect topInput) {
setInput(1, topInput);
}
@Override
public Point2D transform(Point2D p, Effect defaultInput) {
return getDefaultedInput(1, defaultInput).transform(p, defaultInput);
}
@Override
public Point2D untransform(Point2D p, Effect defaultInput) {
return getDefaultedInput(1, defaultInput).untransform(p, defaultInput);
}
@Override
public ImageData filter(FilterContext fctx,
BaseTransform transform,
Rectangle outputClip,
Object renderHelper,
Effect defaultInput)
{
Effect botinput = getDefaultedInput(0, defaultInput);
Effect topinput = getDefaultedInput(1, defaultInput);
ImageData botimg = botinput.filter(fctx, transform, outputClip,
renderHelper, defaultInput);
if (botimg != null) {
if (!botimg.validate(fctx)) {
return new ImageData(fctx, null, null);
}
if (renderHelper instanceof ImageDataRenderer) {
ImageDataRenderer imgr = (ImageDataRenderer) renderHelper;
imgr.renderImage(botimg, BaseTransform.IDENTITY_TRANSFORM, fctx);
botimg.unref();
botimg = null;
}
}
if (botimg == null) {
return topinput.filter(fctx, transform, outputClip,
renderHelper, defaultInput);
}
ImageData topimg = topinput.filter(fctx, transform, outputClip,
null, defaultInput);
if (!topimg.validate(fctx)) {
return new ImageData(fctx, null, null);
}
RenderState rstate = getRenderState(fctx, transform, outputClip,
renderHelper, defaultInput);
ImageData ret = filterImageDatas(fctx, transform, outputClip, rstate,
botimg, topimg);
botimg.unref();
topimg.unref();
return ret;
}
@Override
public RenderState getRenderState(FilterContext fctx,
BaseTransform transform,
Rectangle outputClip,
Object renderHelper,
Effect defaultInput)
{
return RenderState.RenderSpaceRenderState;
}
@Override
public boolean reducesOpaquePixels() {
final Effect topInput = getTopInput();
final Effect bottomInput = getBottomInput();
return topInput != null && topInput.reducesOpaquePixels() && bottomInput != null && bottomInput.reducesOpaquePixels();
}
}
