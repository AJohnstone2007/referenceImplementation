package com.sun.scenario.effect;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.NoninvertibleTransformException;
import com.sun.scenario.effect.impl.state.RenderState;
public abstract class FilterEffect<T extends RenderState> extends Effect {
protected FilterEffect() {
super();
}
protected FilterEffect(Effect input) {
super(input);
}
protected FilterEffect(Effect input1, Effect input2) {
super(input1, input2);
}
@Override
public BaseBounds getBounds(BaseTransform transform,
Effect defaultInput)
{
int numinputs = getNumInputs();
RenderState rstate = getRenderState(null, transform, null,
null, defaultInput);
BaseTransform inputtx = rstate.getInputTransform(transform);
BaseBounds ret;
if (numinputs == 1) {
Effect input = getDefaultedInput(0, defaultInput);
ret = input.getBounds(inputtx, defaultInput);
} else {
BaseBounds inputBounds[] = new BaseBounds[numinputs];
for (int i = 0; i < numinputs; i++) {
Effect input = getDefaultedInput(i, defaultInput);
inputBounds[i] = input.getBounds(inputtx, defaultInput);
}
ret = combineBounds(inputBounds);
}
return transformBounds(rstate.getResultTransform(transform), ret);
}
protected static Rectangle untransformClip(BaseTransform transform,
Rectangle clip)
{
if (transform.isIdentity() || clip == null || clip.isEmpty()) {
return clip;
}
Rectangle transformedBounds = new Rectangle();
if (transform.isTranslateOrIdentity()) {
transformedBounds.setBounds(clip);
double tx = -transform.getMxt();
double ty = -transform.getMyt();
int itx = (int) Math.floor(tx);
int ity = (int) Math.floor(ty);
transformedBounds.translate(itx, ity);
if (itx != tx) {
transformedBounds.width++;
}
if (ity != ty) {
transformedBounds.height++;
}
return transformedBounds;
}
RectBounds b = new RectBounds(clip);
try {
b.grow(-0.5f, -0.5f);
b = (RectBounds) transform.inverseTransform(b, b);
b.grow(0.5f, 0.5f);
transformedBounds.setBounds(b);
} catch (NoninvertibleTransformException e) {
}
return transformedBounds;
}
public abstract T getRenderState(FilterContext fctx,
BaseTransform transform,
Rectangle outputClip,
Object renderHelper,
Effect defaultInput);
@Override
public ImageData filter(FilterContext fctx,
BaseTransform transform,
Rectangle outputClip,
Object renderHelper,
Effect defaultInput)
{
T rstate = getRenderState(fctx, transform, outputClip,
renderHelper, defaultInput);
int numinputs = getNumInputs();
ImageData inputDatas[] = new ImageData[numinputs];
Rectangle filterClip;
BaseTransform inputtx = rstate.getInputTransform(transform);
BaseTransform resulttx = rstate.getResultTransform(transform);
if (resulttx.isIdentity()) {
filterClip = outputClip;
} else {
filterClip = untransformClip(resulttx, outputClip);
}
for (int i = 0; i < numinputs; i++) {
Effect input = getDefaultedInput(i, defaultInput);
inputDatas[i] =
input.filter(fctx, inputtx,
rstate.getInputClip(i, filterClip),
null, defaultInput);
if (!inputDatas[i].validate(fctx)) {
for (int j = 0; j <= i; j++) {
inputDatas[j].unref();
}
return new ImageData(fctx, null, null);
}
}
ImageData ret = filterImageDatas(fctx, inputtx, filterClip, rstate, inputDatas);
for (int i = 0; i < numinputs; i++) {
inputDatas[i].unref();
}
if (!resulttx.isIdentity()) {
if (renderHelper instanceof ImageDataRenderer) {
ImageDataRenderer renderer = (ImageDataRenderer) renderHelper;
renderer.renderImage(ret, resulttx, fctx);
ret.unref();
ret = null;
} else {
ret = ret.transform(resulttx);
}
}
return ret;
}
@Override
public Point2D transform(Point2D p, Effect defaultInput) {
return getDefaultedInput(0, defaultInput).transform(p, defaultInput);
}
@Override
public Point2D untransform(Point2D p, Effect defaultInput) {
return getDefaultedInput(0, defaultInput).untransform(p, defaultInput);
}
protected abstract ImageData filterImageDatas(FilterContext fctx,
BaseTransform transform,
Rectangle outputClip,
T rstate,
ImageData... inputDatas);
}
