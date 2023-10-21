package com.sun.prism.impl.ps;
import com.sun.glass.ui.Screen;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.sg.prism.NGCamera;
import com.sun.prism.CompositeMode;
import com.sun.prism.PixelFormat;
import com.sun.prism.RTTexture;
import com.sun.prism.RenderTarget;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture;
import com.sun.prism.impl.BaseContext;
import com.sun.prism.impl.BaseGraphics;
import com.sun.prism.paint.Color;
import com.sun.prism.paint.Gradient;
import com.sun.prism.paint.ImagePattern;
import com.sun.prism.paint.LinearGradient;
import com.sun.prism.paint.Paint;
import com.sun.prism.paint.RadialGradient;
import com.sun.prism.ps.Shader;
import com.sun.prism.ps.ShaderFactory;
public abstract class BaseShaderContext extends BaseContext {
private static final int CHECK_SHADER = (0x01 );
private static final int CHECK_TRANSFORM = (0x01 << 1);
private static final int CHECK_CLIP = (0x01 << 2);
private static final int CHECK_COMPOSITE = (0x01 << 3);
private static final int CHECK_PAINT_OP_MASK =
(CHECK_SHADER | CHECK_TRANSFORM | CHECK_CLIP | CHECK_COMPOSITE);
private static final int CHECK_TEXTURE_OP_MASK =
(CHECK_SHADER | CHECK_TRANSFORM | CHECK_CLIP | CHECK_COMPOSITE);
private static final int CHECK_CLEAR_OP_MASK =
(CHECK_CLIP);
public enum MaskType {
SOLID ("Solid"),
TEXTURE ("Texture"),
ALPHA_ONE ("AlphaOne", true),
ALPHA_TEXTURE ("AlphaTexture", true),
ALPHA_TEXTURE_DIFF ("AlphaTextureDifference", true),
FILL_PGRAM ("FillPgram"),
DRAW_PGRAM ("DrawPgram", FILL_PGRAM),
FILL_CIRCLE ("FillCircle"),
DRAW_CIRCLE ("DrawCircle", FILL_CIRCLE),
FILL_ELLIPSE ("FillEllipse"),
DRAW_ELLIPSE ("DrawEllipse", FILL_ELLIPSE),
FILL_ROUNDRECT ("FillRoundRect"),
DRAW_ROUNDRECT ("DrawRoundRect", FILL_ROUNDRECT),
DRAW_SEMIROUNDRECT("DrawSemiRoundRect");
private String name;
private MaskType filltype;
private boolean newPaintStyle;
private MaskType(String name) {
this.name = name;
}
private MaskType(String name, boolean newstyle) {
this.name = name;
this.newPaintStyle = newstyle;
}
private MaskType(String name, MaskType filltype) {
this.name = name;
this.filltype = filltype;
}
public String getName() {
return name;
}
public MaskType getFillType() {
return filltype;
}
public boolean isNewPaintStyle() {
return newPaintStyle;
}
}
private static final int NUM_STOCK_SHADER_SLOTS =
MaskType.values().length << 4;
private final Shader[] stockShaders = new Shader[NUM_STOCK_SHADER_SLOTS];
private final Shader[] stockATShaders = new Shader[NUM_STOCK_SHADER_SLOTS];
public enum SpecialShaderType {
TEXTURE_RGB ("Solid_TextureRGB"),
TEXTURE_MASK_RGB ("Mask_TextureRGB"),
TEXTURE_YV12 ("Solid_TextureYV12"),
TEXTURE_First_LCD ("Solid_TextureFirstPassLCD"),
TEXTURE_SECOND_LCD ("Solid_TextureSecondPassLCD"),
SUPER ("Mask_TextureSuper");
private String name;
private SpecialShaderType(String name) {
this.name = name;
}
public String getName() {
return name;
}
}
private final Shader[] specialShaders = new Shader[SpecialShaderType.values().length];
private final Shader[] specialATShaders = new Shader[SpecialShaderType.values().length];
private Shader externalShader;
private RTTexture lcdBuffer;
private final ShaderFactory factory;
private State state;
protected BaseShaderContext(Screen screen, ShaderFactory factory, int vbQuads) {
super(screen, factory, vbQuads);
this.factory = factory;
init();
}
protected void init() {
state = null;
if (externalShader != null && !externalShader.isValid()) {
externalShader.dispose();
externalShader = null;
}
}
public static class State {
private Shader lastShader;
private RenderTarget lastRenderTarget;
private NGCamera lastCamera;
private boolean lastDepthTest;
private BaseTransform lastTransform = new Affine3D();
private Rectangle lastClip;
private CompositeMode lastComp;
private Texture[] lastTextures = new Texture[4];
private boolean isXformValid;
private float lastConst1 = Float.NaN;
private float lastConst2 = Float.NaN;
private float lastConst3 = Float.NaN;
private float lastConst4 = Float.NaN;
private float lastConst5 = Float.NaN;
private float lastConst6 = Float.NaN;
private boolean lastState3D = false;
}
@Override
protected void setPerspectiveTransform(GeneralTransform3D transform) {
if (checkDisposed()) return;
state.isXformValid = false;
super.setPerspectiveTransform(transform);
}
protected void resetLastClip(State state) {
if (checkDisposed()) return;
state.lastClip = null;
}
protected abstract State updateRenderTarget(RenderTarget target, NGCamera camera,
boolean depthTest);
protected abstract void updateTexture(int texUnit, Texture tex);
protected abstract void updateShaderTransform(Shader shader,
BaseTransform xform);
protected abstract void updateWorldTransform(BaseTransform xform);
protected abstract void updateClipRect(Rectangle clipRect);
protected abstract void updateCompositeMode(CompositeMode mode);
private static int getStockShaderIndex(MaskType maskType, Paint paint) {
int paintType;
int paintOption;
if (paint == null) {
paintType = 0;
paintOption = 0;
} else {
paintType = paint.getType().ordinal();
if (paint.getType().isGradient()) {
paintOption = ((Gradient)paint).getSpreadMethod();
} else {
paintOption = 0;
}
}
return (maskType.ordinal() << 4) | (paintType << 2) | (paintOption << 0);
}
private Shader getPaintShader(boolean alphaTest, MaskType maskType, Paint paint) {
if (checkDisposed()) return null;
int index = getStockShaderIndex(maskType, paint);
Shader shaders[] = alphaTest ? stockATShaders : stockShaders;
Shader shader = shaders[index];
if (shader != null && !shader.isValid()) {
shader.dispose();
shader = null;
}
if (shader == null) {
String shaderName =
maskType.getName() + "_" + paint.getType().getName();
if (paint.getType().isGradient() && !maskType.isNewPaintStyle()) {
Gradient grad = (Gradient) paint;
int spreadMethod = grad.getSpreadMethod();
if (spreadMethod == Gradient.PAD) {
shaderName += "_PAD";
} else if (spreadMethod == Gradient.REFLECT) {
shaderName += "_REFLECT";
} else if (spreadMethod == Gradient.REPEAT) {
shaderName += "_REPEAT";
}
}
if (alphaTest) {
shaderName += "_AlphaTest";
}
shader = shaders[index] = factory.createStockShader(shaderName);
}
return shader;
}
private void updatePaintShader(BaseShaderGraphics g, Shader shader,
MaskType maskType, Paint paint,
float bx, float by, float bw, float bh)
{
if (checkDisposed()) return;
Paint.Type paintType = paint.getType();
if (paintType == Paint.Type.COLOR || maskType.isNewPaintStyle()) {
return;
}
float rx, ry, rw, rh;
if (paint.isProportional()) {
rx = bx; ry = by; rw = bw; rh = bh;
} else {
rx = 0f; ry = 0f; rw = 1f; rh = 1f;
}
switch (paintType) {
case LINEAR_GRADIENT:
PaintHelper.setLinearGradient(g, shader,
(LinearGradient)paint,
rx, ry, rw, rh);
break;
case RADIAL_GRADIENT:
PaintHelper.setRadialGradient(g, shader,
(RadialGradient)paint,
rx, ry, rw, rh);
break;
case IMAGE_PATTERN:
PaintHelper.setImagePattern(g, shader,
(ImagePattern)paint,
rx, ry, rw, rh);
default:
break;
}
}
private Shader getSpecialShader(BaseGraphics g, SpecialShaderType sst) {
if (checkDisposed()) return null;
boolean alphaTest = g.isAlphaTestShader();
Shader shaders[] = alphaTest ? specialATShaders : specialShaders;
Shader shader = shaders[sst.ordinal()];
if (shader != null && !shader.isValid()) {
shader.dispose();
shader = null;
}
if (shader == null) {
String shaderName = sst.getName();
if (alphaTest) {
shaderName += "_AlphaTest";
}
shaders[sst.ordinal()] = shader = factory.createStockShader(shaderName);
}
return shader;
}
@Override
public boolean isSuperShaderEnabled() {
if (checkDisposed()) return false;
return state.lastShader == specialATShaders[SpecialShaderType.SUPER.ordinal()]
|| state.lastShader == specialShaders[SpecialShaderType.SUPER.ordinal()];
}
private void updatePerVertexColor(Paint paint, float extraAlpha) {
if (checkDisposed()) return;
if (paint != null && paint.getType() == Paint.Type.COLOR) {
getVertexBuffer().setPerVertexColor((Color)paint, extraAlpha);
} else {
getVertexBuffer().setPerVertexColor(extraAlpha);
}
}
@Override
public void validateClearOp(BaseGraphics g) {
checkState((BaseShaderGraphics) g, CHECK_CLEAR_OP_MASK, null, null);
}
@Override
public void validatePaintOp(BaseGraphics g, BaseTransform xform,
Texture maskTex,
float bx, float by, float bw, float bh)
{
validatePaintOp((BaseShaderGraphics)g, xform,
maskTex, bx, by, bw, bh);
}
Shader validatePaintOp(BaseShaderGraphics g, BaseTransform xform,
MaskType maskType,
float bx, float by, float bw, float bh)
{
return validatePaintOp(g, xform, maskType, null, bx, by, bw, bh);
}
Shader validatePaintOp(BaseShaderGraphics g, BaseTransform xform,
MaskType maskType,
float bx, float by, float bw, float bh,
float k1, float k2, float k3, float k4, float k5, float k6)
{
if (checkDisposed()) return null;
if (state.lastConst1 != k1 || state.lastConst2 != k2 ||
state.lastConst3 != k3 || state.lastConst4 != k4 ||
state.lastConst5 != k5 || state.lastConst6 != k6)
{
flushVertexBuffer();
state.lastConst1 = k1;
state.lastConst2 = k2;
state.lastConst3 = k3;
state.lastConst4 = k4;
state.lastConst5 = k5;
state.lastConst6 = k6;
}
return validatePaintOp(g, xform, maskType, null, bx, by, bw, bh);
}
Shader validatePaintOp(BaseShaderGraphics g, BaseTransform xform,
MaskType maskType, Texture maskTex,
float bx, float by, float bw, float bh,
float k1, float k2, float k3, float k4, float k5, float k6)
{
if (state.lastConst1 != k1 || state.lastConst2 != k2 ||
state.lastConst3 != k3 || state.lastConst4 != k4 ||
state.lastConst5 != k5 || state.lastConst6 != k6)
{
flushVertexBuffer();
state.lastConst1 = k1;
state.lastConst2 = k2;
state.lastConst3 = k3;
state.lastConst4 = k4;
state.lastConst5 = k5;
state.lastConst6 = k6;
}
return validatePaintOp(g, xform, maskType, maskTex, bx, by, bw, bh);
}
Shader validatePaintOp(BaseShaderGraphics g, BaseTransform xform,
Texture maskTex,
float bx, float by, float bw, float bh)
{
return validatePaintOp(g, xform, MaskType.TEXTURE,
maskTex, bx, by, bw, bh);
}
Shader validatePaintOp(BaseShaderGraphics g, BaseTransform xform,
MaskType maskType, Texture maskTex,
float bx, float by, float bw, float bh)
{
if (maskType == null) {
throw new InternalError("maskType must be non-null");
}
if (externalShader == null) {
Paint paint = g.getPaint();
Texture paintTex = null;
Texture tex0;
Texture tex1;
if (paint.getType().isGradient()) {
flushVertexBuffer();
if (maskType.isNewPaintStyle()) {
paintTex = PaintHelper.getWrapGradientTexture(g);
} else {
paintTex = PaintHelper.getGradientTexture(g, (Gradient)paint);
}
} else if (paint.getType() == Paint.Type.IMAGE_PATTERN) {
flushVertexBuffer();
ImagePattern texPaint = (ImagePattern)paint;
ResourceFactory rf = g.getResourceFactory();
paintTex = rf.getCachedTexture(texPaint.getImage(), Texture.WrapMode.REPEAT);
}
Shader shader;
if (factory.isSuperShaderAllowed() &&
paintTex == null &&
maskTex == factory.getGlyphTexture())
{
shader = getSpecialShader(g, SpecialShaderType.SUPER);
tex0 = factory.getRegionTexture();
tex1 = maskTex;
} else {
if (maskTex != null) {
tex0 = maskTex;
tex1 = paintTex;
} else {
tex0 = paintTex;
tex1 = null;
}
shader = getPaintShader(g.isAlphaTestShader(), maskType, paint);
}
checkState(g, CHECK_PAINT_OP_MASK, xform, shader);
setTexture(0, tex0);
setTexture(1, tex1);
updatePaintShader(g, shader, maskType, paint, bx, by, bw, bh);
updatePerVertexColor(paint, g.getExtraAlpha());
if (paintTex != null) paintTex.unlock();
return shader;
} else {
checkState(g, CHECK_PAINT_OP_MASK, xform, externalShader);
setTexture(0, maskTex);
setTexture(1, null);
updatePerVertexColor(null, g.getExtraAlpha());
return externalShader;
}
}
@Override
public void validateTextureOp(BaseGraphics g, BaseTransform xform,
Texture tex0, PixelFormat format)
{
validateTextureOp((BaseShaderGraphics)g, xform, tex0, null, format);
}
public Shader validateLCDOp(BaseShaderGraphics g, BaseTransform xform,
Texture tex0, Texture tex1, boolean firstPass,
Paint fillColor)
{
if (checkDisposed()) return null;
Shader shader = firstPass ? getSpecialShader(g, SpecialShaderType.TEXTURE_First_LCD) :
getSpecialShader(g, SpecialShaderType.TEXTURE_SECOND_LCD);
checkState(g, CHECK_TEXTURE_OP_MASK, xform, shader);
setTexture(0, tex0);
setTexture(1, tex1);
updatePerVertexColor(fillColor, g.getExtraAlpha());
return shader;
}
Shader validateTextureOp(BaseShaderGraphics g, BaseTransform xform,
Texture[] textures, PixelFormat format)
{
if (checkDisposed()) return null;
Shader shader;
if (format == PixelFormat.MULTI_YCbCr_420) {
if (textures.length < 3) {
return null;
}
if (externalShader == null) {
shader = getSpecialShader(g, SpecialShaderType.TEXTURE_YV12);
} else {
shader = externalShader;
}
} else {
return null;
}
if (null != shader) {
checkState(g, CHECK_TEXTURE_OP_MASK, xform, shader);
int texCount = Math.max(0, Math.min(textures.length, 4));
for (int index = 0; index < texCount; index++) {
setTexture(index, textures[index]);
}
updatePerVertexColor(null, g.getExtraAlpha());
}
return shader;
}
Shader validateTextureOp(BaseShaderGraphics g, BaseTransform xform,
Texture tex0, Texture tex1, PixelFormat format)
{
if (checkDisposed()) return null;
Shader shader;
if (externalShader == null) {
switch (format) {
case INT_ARGB_PRE:
case BYTE_BGRA_PRE:
case BYTE_RGB:
case BYTE_GRAY:
case BYTE_APPLE_422:
if (factory.isSuperShaderAllowed() &&
tex0 == factory.getRegionTexture() &&
tex1 == null)
{
shader = getSpecialShader(g, SpecialShaderType.SUPER);
tex1 = factory.getGlyphTexture();
} else {
shader = getSpecialShader(g, SpecialShaderType.TEXTURE_RGB);
}
break;
case MULTI_YCbCr_420:
case BYTE_ALPHA:
default:
throw new InternalError("Pixel format not supported: " + format);
}
} else {
shader = externalShader;
}
checkState(g, CHECK_TEXTURE_OP_MASK, xform, shader);
setTexture(0, tex0);
setTexture(1, tex1);
updatePerVertexColor(null, g.getExtraAlpha());
return shader;
}
Shader validateMaskTextureOp(BaseShaderGraphics g, BaseTransform xform,
Texture tex0, Texture tex1, PixelFormat format)
{
if (checkDisposed()) return null;
Shader shader;
if (externalShader == null) {
switch (format) {
case INT_ARGB_PRE:
case BYTE_BGRA_PRE:
case BYTE_RGB:
case BYTE_GRAY:
case BYTE_APPLE_422:
shader = getSpecialShader(g, SpecialShaderType.TEXTURE_MASK_RGB);
break;
case MULTI_YCbCr_420:
case BYTE_ALPHA:
default:
throw new InternalError("Pixel format not supported: " + format);
}
} else {
shader = externalShader;
}
checkState(g, CHECK_TEXTURE_OP_MASK, xform, shader);
setTexture(0, tex0);
setTexture(1, tex1);
updatePerVertexColor(null, g.getExtraAlpha());
return shader;
}
void setExternalShader(BaseShaderGraphics g, Shader shader) {
if (checkDisposed()) return;
flushVertexBuffer();
if (shader != null) {
shader.enable();
}
externalShader = shader;
}
private void checkState(BaseShaderGraphics g,
int checkFlags,
BaseTransform xform,
Shader shader)
{
if (checkDisposed()) return;
setRenderTarget(g);
if ((checkFlags & CHECK_SHADER) != 0) {
if (shader != state.lastShader) {
flushVertexBuffer();
shader.enable();
state.lastShader = shader;
state.isXformValid = false;
checkFlags |= CHECK_TRANSFORM;
}
}
if ((checkFlags & CHECK_TRANSFORM) != 0) {
if (!state.isXformValid || !xform.equals(state.lastTransform)) {
flushVertexBuffer();
updateShaderTransform(shader, xform);
state.lastTransform.setTransform(xform);
state.isXformValid = true;
}
}
if ((checkFlags & CHECK_CLIP) != 0) {
Rectangle clip = g.getClipRectNoClone();
if (clip != state.lastClip) {
flushVertexBuffer();
updateClipRect(clip);
state.lastClip = clip;
}
}
if ((checkFlags & CHECK_COMPOSITE) != 0) {
CompositeMode mode = g.getCompositeMode();
if (mode != state.lastComp) {
flushVertexBuffer();
updateCompositeMode(mode);
state.lastComp = mode;
}
}
}
private void setTexture(int texUnit, Texture tex) {
if (checkDisposed()) return;
if (tex != null) tex.assertLocked();
if (tex != state.lastTextures[texUnit]) {
flushVertexBuffer();
updateTexture(texUnit, tex);
state.lastTextures[texUnit] = tex;
}
}
public void initLCDBuffer(int width, int height) {
if (checkDisposed()) return;
lcdBuffer = factory.createRTTexture(width, height, Texture.WrapMode.CLAMP_NOT_NEEDED);
lcdBuffer.makePermanent();
}
public void disposeLCDBuffer() {
if (lcdBuffer != null) {
lcdBuffer.dispose();
lcdBuffer = null;
}
}
@Override
public RTTexture getLCDBuffer() {
return lcdBuffer;
}
public void validateLCDBuffer(RenderTarget renderTarget) {
if (checkDisposed()) return;
if (lcdBuffer == null ||
lcdBuffer.getPhysicalWidth() < renderTarget.getPhysicalWidth() ||
lcdBuffer.getPhysicalHeight() < renderTarget.getPhysicalHeight())
{
disposeLCDBuffer();
initLCDBuffer(renderTarget.getPhysicalWidth(), renderTarget.getPhysicalHeight());
}
}
abstract public void blit(RTTexture srcRTT, RTTexture dstRTT,
int srcX0, int srcY0, int srcX1, int srcY1,
int dstX0, int dstY0, int dstX1, int dstY1);
@Override
protected void setRenderTarget(RenderTarget target, NGCamera camera,
boolean depthTest, boolean state3D)
{
if (checkDisposed()) return;
if (target instanceof Texture) {
((Texture) target).assertLocked();
}
if (state == null ||
state3D != state.lastState3D ||
target != state.lastRenderTarget ||
camera != state.lastCamera ||
depthTest != state.lastDepthTest)
{
flushVertexBuffer();
state = updateRenderTarget(target, camera, depthTest);
state.lastRenderTarget = target;
state.lastCamera = camera;
state.lastDepthTest = depthTest;
state.isXformValid = false;
if (state3D != state.lastState3D) {
state.lastState3D = state3D;
state.lastShader = null;
state.lastConst1 = Float.NaN;
state.lastConst2 = Float.NaN;
state.lastConst3 = Float.NaN;
state.lastConst4 = Float.NaN;
state.lastConst5 = Float.NaN;
state.lastConst6 = Float.NaN;
state.lastComp = null;
state.lastClip = null;
for (int i = 0; i != state.lastTextures.length; i++) {
state.lastTextures[i] = null;
}
if (state3D) {
setDeviceParametersFor3D();
} else {
setDeviceParametersFor2D();
}
}
}
}
@Override
protected void releaseRenderTarget() {
if (state != null) {
state.lastRenderTarget = null;
for (int i=0; i<state.lastTextures.length; i++) {
state.lastTextures[i] = null;
}
}
}
private void disposeShaders(Shader[] shaders) {
for (int i = 0; i < shaders.length; i++) {
if (shaders[i] != null) {
shaders[i].dispose();
shaders[i] = null;
}
}
}
@Override
public void dispose() {
disposeShaders(stockShaders);
disposeShaders(stockATShaders);
disposeShaders(specialShaders);
disposeShaders(specialATShaders);
if (externalShader != null) {
externalShader.dispose();
externalShader = null;
}
disposeLCDBuffer();
releaseRenderTarget();
state = null;
super.dispose();
}
}
