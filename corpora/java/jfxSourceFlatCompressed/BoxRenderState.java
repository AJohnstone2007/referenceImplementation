package com.sun.scenario.effect.impl.state;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.NoninvertibleTransformException;
import com.sun.scenario.effect.Color4f;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.Filterable;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.impl.BufferUtil;
import com.sun.scenario.effect.impl.EffectPeer;
import com.sun.scenario.effect.impl.Renderer;
import java.nio.FloatBuffer;
public class BoxRenderState extends LinearConvolveRenderState {
private static final int MAX_BOX_SIZES[] = {
getMaxSizeForKernelSize(MAX_KERNEL_SIZE, 0),
getMaxSizeForKernelSize(MAX_KERNEL_SIZE, 1),
getMaxSizeForKernelSize(MAX_KERNEL_SIZE, 2),
getMaxSizeForKernelSize(MAX_KERNEL_SIZE, 3),
};
private final boolean isShadow;
private final int blurPasses;
private final float spread;
private Color4f shadowColor;
private EffectCoordinateSpace space;
private BaseTransform inputtx;
private BaseTransform resulttx;
private final float inputSizeH;
private final float inputSizeV;
private final int spreadPass;
private float samplevectors[];
private int validatedPass;
private float passSize;
private FloatBuffer weights;
private float weightsValidSize;
private float weightsValidSpread;
private boolean swCompatible;
public static int getMaxSizeForKernelSize(int kernelSize, int blurPasses) {
if (blurPasses == 0) {
return Integer.MAX_VALUE;
}
int passSize = (kernelSize - 1) | 1;
passSize = ((passSize - 1) / blurPasses) | 1;
assert getKernelSize(passSize, blurPasses) <= kernelSize;
return passSize;
}
public static int getKernelSize(int passSize, int blurPasses) {
int kernelSize = (passSize < 1) ? 1 : passSize;
kernelSize = (kernelSize-1) * blurPasses + 1;
kernelSize |= 1;
return kernelSize;
}
public BoxRenderState(float hsize, float vsize, int blurPasses, float spread,
boolean isShadow, Color4f shadowColor, BaseTransform filtertx)
{
this.isShadow = isShadow;
this.shadowColor = shadowColor;
this.spread = spread;
this.blurPasses = blurPasses;
if (filtertx == null) filtertx = BaseTransform.IDENTITY_TRANSFORM;
double txScaleX = Math.hypot(filtertx.getMxx(), filtertx.getMyx());
double txScaleY = Math.hypot(filtertx.getMxy(), filtertx.getMyy());
float fSizeH = (float) (hsize * txScaleX);
float fSizeV = (float) (vsize * txScaleY);
int maxPassSize = MAX_BOX_SIZES[blurPasses];
if (fSizeH > maxPassSize) {
txScaleX = maxPassSize / hsize;
fSizeH = maxPassSize;
}
if (fSizeV > maxPassSize) {
txScaleY = maxPassSize / vsize;
fSizeV = maxPassSize;
}
this.inputSizeH = fSizeH;
this.inputSizeV = fSizeV;
this.spreadPass = (fSizeV > 1) ? 1 : 0;
boolean custom = (txScaleX != filtertx.getMxx() ||
0.0 != filtertx.getMyx() ||
txScaleY != filtertx.getMyy() ||
0.0 != filtertx.getMxy());
if (custom) {
this.space = EffectCoordinateSpace.CustomSpace;
this.inputtx = BaseTransform.getScaleInstance(txScaleX, txScaleY);
this.resulttx = filtertx
.copy()
.deriveWithScale(1.0 / txScaleX, 1.0 / txScaleY, 1.0);
} else {
this.space = EffectCoordinateSpace.RenderSpace;
this.inputtx = filtertx;
this.resulttx = BaseTransform.IDENTITY_TRANSFORM;
}
}
public int getBoxPixelSize(int pass) {
float size = passSize;
if (size < 1.0f) size = 1.0f;
int boxsize = ((int) Math.ceil(size)) | 1;
return boxsize;
}
public int getBlurPasses() {
return blurPasses;
}
public float getSpread() {
return spread;
}
@Override
public boolean isShadow() {
return isShadow;
}
@Override
public Color4f getShadowColor() {
return shadowColor;
}
@Override
public float[] getPassShadowColorComponents() {
return (validatedPass == 0)
? BLACK_COMPONENTS
: shadowColor.getPremultipliedRGBComponents();
}
@Override
public EffectCoordinateSpace getEffectTransformSpace() {
return space;
}
@Override
public BaseTransform getInputTransform(BaseTransform filterTransform) {
return inputtx;
}
@Override
public BaseTransform getResultTransform(BaseTransform filterTransform) {
return resulttx;
}
@Override
public EffectPeer<BoxRenderState> getPassPeer(Renderer r, FilterContext fctx) {
if (isPassNop()) {
return null;
}
int ksize = getPassKernelSize();
int psize = getPeerSize(ksize);
Effect.AccelType actype = r.getAccelType();
String name;
switch (actype) {
case NONE:
case SIMD:
if (swCompatible && spread == 0.0f) {
name = isShadow() ? "BoxShadow" : "BoxBlur";
break;
}
default:
name = isShadow() ? "LinearConvolveShadow" : "LinearConvolve";
break;
}
EffectPeer peer = r.getPeerInstance(fctx, name, psize);
return peer;
}
@Override
public Rectangle getInputClip(int i, Rectangle filterClip) {
if (filterClip != null) {
int klenh = getInputKernelSize(0);
int klenv = getInputKernelSize(1);
if ((klenh | klenv) > 1) {
filterClip = new Rectangle(filterClip);
filterClip.grow(klenh/2, klenv/2);
}
}
return filterClip;
}
@Override
public ImageData validatePassInput(ImageData src, int pass) {
this.validatedPass = pass;
BaseTransform srcTx = src.getTransform();
samplevectors = new float[2];
samplevectors[pass] = 1.0f;
float iSize = (pass == 0) ? inputSizeH : inputSizeV;
if (srcTx.isTranslateOrIdentity()) {
this.swCompatible = true;
this.passSize = iSize;
} else {
try {
srcTx.inverseDeltaTransform(samplevectors, 0, samplevectors, 0, 1);
} catch (NoninvertibleTransformException ex) {
this.passSize = 0.0f;
samplevectors[0] = samplevectors[1] = 0.0f;
this.swCompatible = true;
return src;
}
double srcScale = Math.hypot(samplevectors[0], samplevectors[1]);
float pSize = (float) (iSize * srcScale);
pSize *= srcScale;
int maxPassSize = MAX_BOX_SIZES[blurPasses];
if (pSize > maxPassSize) {
pSize = maxPassSize;
srcScale = maxPassSize / iSize;
}
this.passSize = pSize;
samplevectors[0] /= srcScale;
samplevectors[1] /= srcScale;
Rectangle srcSize = src.getUntransformedBounds();
if (pass == 0) {
this.swCompatible = nearOne(samplevectors[0], srcSize.width)
&& nearZero(samplevectors[1], srcSize.width);
} else {
this.swCompatible = nearZero(samplevectors[0], srcSize.height)
&& nearOne(samplevectors[1], srcSize.height);
}
}
Filterable f = src.getUntransformedImage();
samplevectors[0] /= f.getPhysicalWidth();
samplevectors[1] /= f.getPhysicalHeight();
return src;
}
@Override
public Rectangle getPassResultBounds(Rectangle srcdimension, Rectangle outputClip) {
Rectangle ret = new Rectangle(srcdimension);
if (validatedPass == 0) {
ret.grow(getInputKernelSize(0) / 2, 0);
} else {
ret.grow(0, getInputKernelSize(1) / 2);
}
if (outputClip != null) {
if (validatedPass == 0) {
outputClip = new Rectangle(outputClip);
outputClip.grow(0, getInputKernelSize(1) / 2);
}
ret.intersectWith(outputClip);
}
return ret;
}
@Override
public float[] getPassVector() {
float xoff = samplevectors[0];
float yoff = samplevectors[1];
int ksize = getPassKernelSize();
int center = ksize / 2;
float ret[] = new float[4];
ret[0] = xoff;
ret[1] = yoff;
ret[2] = -center * xoff;
ret[3] = -center * yoff;
return ret;
}
@Override
public int getPassWeightsArrayLength() {
validateWeights();
return weights.limit() / 4;
}
@Override
public FloatBuffer getPassWeights() {
validateWeights();
weights.rewind();
return weights;
}
private void validateWeights() {
float pSize;
if (blurPasses == 0) {
pSize = 1.0f;
} else {
pSize = passSize;
if (pSize < 1.0f) pSize = 1.0f;
}
float passSpread = (validatedPass == spreadPass) ? spread : 0f;
if (weights != null &&
weightsValidSize == pSize &&
weightsValidSpread == passSpread)
{
return;
}
int klen = ((int) Math.ceil(pSize)) | 1;
int totalklen = klen;
for (int p = 1; p < blurPasses; p++) {
totalklen += klen - 1;
}
double ik[] = new double[totalklen];
for (int i = 0; i < klen; i++) {
ik[i] = 1.0;
}
double excess = klen - pSize;
if (excess > 0.0) {
ik[0] = ik[klen-1] = 1.0 - excess * 0.5;
}
int filledklen = klen;
for (int p = 1; p < blurPasses; p++) {
filledklen += klen - 1;
int i = filledklen - 1;
while (i > klen) {
double sum = ik[i];
for (int k = 1; k < klen; k++) {
sum += ik[i-k];
}
ik[i--] = sum;
}
while (i > 0) {
double sum = ik[i];
for (int k = 0; k < i; k++) {
sum += ik[k];
}
ik[i--] = sum;
}
}
double sum = 0.0;
for (int i = 0; i < ik.length; i++) {
sum += ik[i];
}
sum += (1.0 - sum) * passSpread;
if (weights == null) {
int maxbufsize = getPeerSize(MAX_KERNEL_SIZE);
maxbufsize = (maxbufsize + 3) & (~3);
weights = BufferUtil.newFloatBuffer(maxbufsize);
}
weights.clear();
for (int i = 0; i < ik.length; i++) {
weights.put((float) (ik[i] / sum));
}
int limit = getPeerSize(ik.length);
while (weights.position() < limit) {
weights.put(0f);
}
weights.limit(limit);
weights.rewind();
}
@Override
public int getInputKernelSize(int pass) {
float size = (pass == 0) ? inputSizeH : inputSizeV;
if (size < 1.0f) size = 1.0f;
int klen = ((int) Math.ceil(size)) | 1;
int totalklen = 1;
for (int p = 0; p < blurPasses; p++) {
totalklen += klen - 1;
}
return totalklen;
}
@Override
public int getPassKernelSize() {
float size = passSize;
if (size < 1.0f) size = 1.0f;
int klen = ((int) Math.ceil(size)) | 1;
int totalklen = 1;
for (int p = 0; p < blurPasses; p++) {
totalklen += klen - 1;
}
return totalklen;
}
@Override
public boolean isNop() {
if (isShadow) return false;
return (blurPasses == 0
|| (inputSizeH <= 1.0f && inputSizeV <= 1.0f));
}
@Override
public boolean isPassNop() {
if (isShadow && validatedPass == 1) return false;
return (blurPasses == 0 || (passSize) <= 1.0f);
}
}
