package com.sun.scenario.effect.impl.state;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.Affine2D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.NoninvertibleTransformException;
import com.sun.scenario.effect.Color4f;
import com.sun.scenario.effect.Filterable;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.impl.BufferUtil;
import java.nio.FloatBuffer;
public class GaussianRenderState extends LinearConvolveRenderState {
public static final float MAX_RADIUS = (MAX_KERNEL_SIZE - 1) / 2;
private boolean isShadow;
private Color4f shadowColor;
private float spread;
private EffectCoordinateSpace space;
private BaseTransform inputtx;
private BaseTransform resulttx;
private float inputRadiusX;
private float inputRadiusY;
private float spreadPass;
private int validatedPass;
private PassType passType;
private float passRadius;
private FloatBuffer weights;
private float samplevectors[];
private float weightsValidRadius;
private float weightsValidSpread;
static FloatBuffer getGaussianWeights(FloatBuffer weights,
int pad,
float radius,
float spread)
{
int r = pad;
int klen = (r * 2) + 1;
if (weights == null) {
weights = BufferUtil.newFloatBuffer(128);
}
weights.clear();
float sigma = radius / 3;
float sigma22 = 2 * sigma * sigma;
if (sigma22 < Float.MIN_VALUE) {
sigma22 = Float.MIN_VALUE;
}
float total = 0.0F;
for (int row = -r; row <= r; row++) {
float kval = (float) Math.exp(-(row * row) / sigma22);
weights.put(kval);
total += kval;
}
total += (weights.get(0) - total) * spread;
for (int i = 0; i < klen; i++) {
weights.put(i, weights.get(i) / total);
}
int limit = getPeerSize(klen);
while (weights.position() < limit) {
weights.put(0.0F);
}
weights.limit(limit);
weights.rewind();
return weights;
}
public GaussianRenderState(float xradius, float yradius, float spread,
boolean isShadow, Color4f shadowColor, BaseTransform filtertx)
{
this.isShadow = isShadow;
this.shadowColor = shadowColor;
this.spread = spread;
if (filtertx == null) filtertx = BaseTransform.IDENTITY_TRANSFORM;
double mxx = filtertx.getMxx();
double mxy = filtertx.getMxy();
double myx = filtertx.getMyx();
double myy = filtertx.getMyy();
double txScaleX = Math.hypot(mxx, myx);
double txScaleY = Math.hypot(mxy, myy);
boolean scaled = false;
float scaledRadiusX = (float) (xradius * txScaleX);
float scaledRadiusY = (float) (yradius * txScaleY);
if (scaledRadiusX < MIN_EFFECT_RADIUS && scaledRadiusY < MIN_EFFECT_RADIUS) {
this.inputRadiusX = 0.0f;
this.inputRadiusY = 0.0f;
this.spreadPass = 0;
this.space = EffectCoordinateSpace.RenderSpace;
this.inputtx = filtertx;
this.resulttx = BaseTransform.IDENTITY_TRANSFORM;
this.samplevectors = new float[] { 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f };
} else {
if (scaledRadiusX > MAX_RADIUS) {
scaledRadiusX = MAX_RADIUS;
txScaleX = MAX_RADIUS / xradius;
scaled = true;
}
if (scaledRadiusY > MAX_RADIUS) {
scaledRadiusY = MAX_RADIUS;
txScaleY = MAX_RADIUS / yradius;
scaled = true;
}
this.inputRadiusX = scaledRadiusX;
this.inputRadiusY = scaledRadiusY;
this.spreadPass = (inputRadiusY > 1f || inputRadiusY >= inputRadiusX) ? 1 : 0;
if (scaled) {
this.space = EffectCoordinateSpace.CustomSpace;
this.inputtx = BaseTransform.getScaleInstance(txScaleX, txScaleY);
this.resulttx = filtertx
.copy()
.deriveWithScale(1.0 / txScaleX, 1.0 / txScaleY, 1.0);
this.samplevectors = new float[] { 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f };
} else {
this.space = EffectCoordinateSpace.RenderSpace;
this.inputtx = filtertx;
this.resulttx = BaseTransform.IDENTITY_TRANSFORM;
this.samplevectors = new float[] { (float) (mxx / txScaleX),
(float) (myx / txScaleX),
(float) (mxy / txScaleY),
(float) (myy / txScaleY),
0.0f, 0.0f };
}
}
}
public GaussianRenderState(float radius, float dx, float dy, BaseTransform filtertx) {
this.isShadow = false;
this.spread = 0.0f;
if (filtertx == null) filtertx = BaseTransform.IDENTITY_TRANSFORM;
double mxx = filtertx.getMxx();
double mxy = filtertx.getMxy();
double myx = filtertx.getMyx();
double myy = filtertx.getMyy();
double tdx = mxx * dx + mxy * dy;
double tdy = myx * dx + myy * dy;
double txScale = Math.hypot(tdx, tdy);
boolean scaled = false;
float scaledRadius = (float) (radius * txScale);
if (scaledRadius < MIN_EFFECT_RADIUS) {
this.inputRadiusX = 0.0f;
this.inputRadiusY = 0.0f;
this.spreadPass = 0;
this.space = EffectCoordinateSpace.RenderSpace;
this.inputtx = filtertx;
this.resulttx = BaseTransform.IDENTITY_TRANSFORM;
this.samplevectors = new float[] { 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
} else {
if (scaledRadius > MAX_RADIUS) {
scaledRadius = MAX_RADIUS;
txScale = MAX_RADIUS / radius;
scaled = true;
}
this.inputRadiusX = scaledRadius;
this.inputRadiusY = 0.0f;
this.spreadPass = 0;
if (scaled) {
double odx = mxy * dx - mxx * dy;
double ody = myy * dx - myx * dy;
double txOScale = Math.hypot(odx, ody);
this.space = EffectCoordinateSpace.CustomSpace;
Affine2D a2d = new Affine2D();
a2d.scale(txScale, txOScale);
a2d.rotate(dx, -dy);
BaseTransform a2di;
try {
a2di = a2d.createInverse();
} catch (NoninvertibleTransformException ex) {
a2di = BaseTransform.IDENTITY_TRANSFORM;
}
this.inputtx = a2d;
this.resulttx = filtertx
.copy()
.deriveWithConcatenation(a2di);
this.samplevectors = new float[] { 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
} else {
this.space = EffectCoordinateSpace.RenderSpace;
this.inputtx = filtertx;
this.resulttx = BaseTransform.IDENTITY_TRANSFORM;
this.samplevectors = new float[] { (float) (tdx / txScale),
(float) (tdy / txScale),
0.0f, 0.0f, 0.0f, 0.0f };
}
}
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
public Rectangle getInputClip(int i, Rectangle filterClip) {
if (filterClip != null) {
double dx0 = samplevectors[0] * inputRadiusX;
double dy0 = samplevectors[1] * inputRadiusX;
double dx1 = samplevectors[2] * inputRadiusY;
double dy1 = samplevectors[3] * inputRadiusY;
int padx = (int) Math.ceil(dx0+dx1);
int pady = (int) Math.ceil(dy0+dy1);
if ((padx | pady) != 0) {
filterClip = new Rectangle(filterClip);
filterClip.grow(padx, pady);
}
}
return filterClip;
}
@Override
public ImageData validatePassInput(ImageData src, int pass) {
this.validatedPass = pass;
Filterable f = src.getUntransformedImage();
BaseTransform srcTx = src.getTransform();
float iRadius = (pass == 0) ? inputRadiusX : inputRadiusY;
int vecindex = pass * 2;
if (srcTx.isTranslateOrIdentity()) {
this.passRadius = iRadius;
samplevectors[4] = samplevectors[vecindex];
samplevectors[5] = samplevectors[vecindex+1];
if (validatedPass == 0) {
if ( nearOne(samplevectors[4], f.getPhysicalWidth()) &&
nearZero(samplevectors[5], f.getPhysicalWidth()))
{
passType = PassType.HORIZONTAL_CENTERED;
} else {
passType = PassType.GENERAL_VECTOR;
}
} else {
if (nearZero(samplevectors[4], f.getPhysicalHeight()) &&
nearOne(samplevectors[5], f.getPhysicalHeight()))
{
passType = PassType.VERTICAL_CENTERED;
} else {
passType = PassType.GENERAL_VECTOR;
}
}
} else {
passType = PassType.GENERAL_VECTOR;
try {
srcTx.inverseDeltaTransform(samplevectors, vecindex, samplevectors, 4, 1);
} catch (NoninvertibleTransformException ex) {
this.passRadius = 0.0f;
samplevectors[4] = samplevectors[5] = 0.0f;
return src;
}
double srcScale = Math.hypot(samplevectors[4], samplevectors[5]);
float pRad = (float) (iRadius * srcScale);
if (pRad > MAX_RADIUS) {
pRad = MAX_RADIUS;
srcScale = MAX_RADIUS / iRadius;
}
this.passRadius = pRad;
samplevectors[4] /= srcScale;
samplevectors[5] /= srcScale;
}
samplevectors[4] /= f.getPhysicalWidth();
samplevectors[5] /= f.getPhysicalHeight();
return src;
}
@Override
public Rectangle getPassResultBounds(Rectangle srcdimension, Rectangle outputClip) {
double r = (validatedPass == 0) ? inputRadiusX : inputRadiusY;
int i = validatedPass * 2;
double dx = samplevectors[i+0] * r;
double dy = samplevectors[i+1] * r;
int padx = (int) Math.ceil(Math.abs(dx));
int pady = (int) Math.ceil(Math.abs(dy));
Rectangle ret = new Rectangle(srcdimension);
ret.grow(padx, pady);
if (outputClip != null) {
if (validatedPass == 0) {
dx = samplevectors[2] * r;
dy = samplevectors[3] * r;
padx = (int) Math.ceil(Math.abs(dx));
pady = (int) Math.ceil(Math.abs(dy));
if ((padx | pady) != 0) {
outputClip = new Rectangle(outputClip);
outputClip.grow(padx, pady);
}
}
ret.intersectWith(outputClip);
}
return ret;
}
@Override
public PassType getPassType() {
return passType;
}
@Override
public float[] getPassVector() {
float xoff = samplevectors[4];
float yoff = samplevectors[5];
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
@Override
public int getInputKernelSize(int pass) {
return 1 + 2 * (int) Math.ceil((pass == 0) ? inputRadiusX : inputRadiusY);
}
@Override
public int getPassKernelSize() {
return 1 + 2 * (int) Math.ceil(passRadius);
}
@Override
public boolean isNop() {
if (isShadow) return false;
return inputRadiusX < MIN_EFFECT_RADIUS
&& inputRadiusY < MIN_EFFECT_RADIUS;
}
@Override
public boolean isPassNop() {
if (isShadow && validatedPass == 1) return false;
return (passRadius) < MIN_EFFECT_RADIUS;
}
private void validateWeights() {
float r = passRadius;
float s = (validatedPass == spreadPass) ? spread : 0f;
if (weights == null ||
weightsValidRadius != r ||
weightsValidSpread != s)
{
weights = getGaussianWeights(weights, (int) Math.ceil(r), r, s);
weightsValidRadius = r;
weightsValidSpread = s;
}
}
}
