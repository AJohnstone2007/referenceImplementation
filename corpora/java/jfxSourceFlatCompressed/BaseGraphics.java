package com.sun.prism.impl;
import com.sun.glass.ui.Screen;
import com.sun.javafx.geom.Ellipse2D;
import com.sun.javafx.geom.Line2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.RoundRectangle2D;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.sg.prism.NGCamera;
import com.sun.javafx.sg.prism.NodePath;
import com.sun.prism.BasicStroke;
import com.sun.prism.CompositeMode;
import com.sun.prism.PixelFormat;
import com.sun.prism.RectShadowGraphics;
import com.sun.prism.RenderTarget;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture;
import com.sun.prism.paint.Color;
import com.sun.prism.paint.Paint;
public abstract class BaseGraphics implements RectShadowGraphics {
private static final BasicStroke DEFAULT_STROKE =
new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f);
private static final Paint DEFAULT_PAINT = Color.WHITE;
protected static final RoundRectangle2D scratchRRect = new RoundRectangle2D();
protected static final Ellipse2D scratchEllipse = new Ellipse2D();
protected static final Line2D scratchLine = new Line2D();
protected static final BaseTransform IDENT = BaseTransform.IDENTITY_TRANSFORM;
private final Affine3D transform3D = new Affine3D();
private NGCamera camera = NGCamera.INSTANCE;
private RectBounds devClipRect;
private RectBounds finalClipRect;
protected RectBounds nodeBounds = null;
private Rectangle clipRect;
private int clipRectIndex;
private boolean hasPreCullingBits = false;
private float extraAlpha = 1f;
private CompositeMode compMode;
private boolean antialiasedShape = true;
private boolean depthBuffer = false;
private boolean depthTest = false;
protected Paint paint = DEFAULT_PAINT;
protected BasicStroke stroke = DEFAULT_STROKE;
protected boolean isSimpleTranslate = true;
protected float transX;
protected float transY;
private final BaseContext context;
private final RenderTarget renderTarget;
private boolean state3D = false;
private float pixelScaleX = 1.0f;
private float pixelScaleY = 1.0f;
protected BaseGraphics(BaseContext context, RenderTarget target) {
this.context = context;
this.renderTarget = target;
devClipRect = new RectBounds(0, 0,
target.getContentWidth(),
target.getContentHeight());
finalClipRect = new RectBounds(devClipRect);
compMode = CompositeMode.SRC_OVER;
if (context != null) {
context.setRenderTarget(this);
}
}
protected NGCamera getCamera() {
return camera;
}
public RenderTarget getRenderTarget() {
return renderTarget;
}
@Override
public void setState3D(boolean flag) {
this.state3D = flag;
}
@Override
public boolean isState3D() {
return state3D;
}
public Screen getAssociatedScreen() {
return context.getAssociatedScreen();
}
public ResourceFactory getResourceFactory() {
return context.getResourceFactory();
}
public BaseTransform getTransformNoClone() {
return transform3D;
}
@Override
public void setPerspectiveTransform(GeneralTransform3D transform) {
context.setPerspectiveTransform(transform);
}
public void setTransform(BaseTransform transform) {
if (transform == null) {
transform3D.setToIdentity();
} else {
transform3D.setTransform(transform);
}
validateTransformAndPaint();
}
public void setTransform(double m00, double m10,
double m01, double m11,
double m02, double m12)
{
transform3D.setTransform(m00, m10, m01, m11, m02, m12);
validateTransformAndPaint();
}
public void setTransform3D(double mxx, double mxy, double mxz, double mxt,
double myx, double myy, double myz, double myt,
double mzx, double mzy, double mzz, double mzt)
{
transform3D.setTransform(mxx, mxy, mxz, mxt,
myx, myy, myz, myt,
mzx, mzy, mzz, mzt);
validateTransformAndPaint();
}
public void transform(BaseTransform transform) {
transform3D.concatenate(transform);
validateTransformAndPaint();
}
public void translate(float tx, float ty) {
if (tx != 0f || ty != 0f) {
transform3D.translate(tx, ty);
validateTransformAndPaint();
}
}
public void translate(float tx, float ty, float tz) {
if (tx != 0f || ty != 0f || tz != 0f) {
transform3D.translate(tx, ty, tz);
validateTransformAndPaint();
}
}
public void scale(float sx, float sy) {
if (sx != 1f || sy != 1f) {
transform3D.scale(sx, sy);
validateTransformAndPaint();
}
}
public void scale(float sx, float sy, float sz) {
if (sx != 1f || sy != 1f || sz != 1f) {
transform3D.scale(sx, sy, sz);
validateTransformAndPaint();
}
}
public void setClipRectIndex(int index) {
this.clipRectIndex = index;
}
public int getClipRectIndex() {
return this.clipRectIndex;
}
public void setHasPreCullingBits(boolean hasBits) {
this.hasPreCullingBits = hasBits;
}
public boolean hasPreCullingBits() {
return hasPreCullingBits;
}
private NodePath renderRoot;
@Override
public final void setRenderRoot(NodePath root) {
this.renderRoot = root;
}
@Override
public final NodePath getRenderRoot() {
return renderRoot;
}
private void validateTransformAndPaint() {
if (transform3D.isTranslateOrIdentity() &&
paint.getType() == Paint.Type.COLOR)
{
isSimpleTranslate = true;
transX = (float)transform3D.getMxt();
transY = (float)transform3D.getMyt();
} else {
isSimpleTranslate = false;
transX = 0f;
transY = 0f;
}
}
public NGCamera getCameraNoClone() {
return camera;
}
public void setDepthTest(boolean depthTest) {
this.depthTest = depthTest;
}
public boolean isDepthTest() {
return depthTest;
}
public void setDepthBuffer(boolean depthBuffer) {
this.depthBuffer = depthBuffer;
}
public boolean isDepthBuffer() {
return depthBuffer;
}
public boolean isAlphaTestShader() {
return (PrismSettings.forceAlphaTestShader || (isDepthTest() && isDepthBuffer()));
}
public void setAntialiasedShape(boolean aa) {
antialiasedShape = aa;
}
public boolean isAntialiasedShape() {
return antialiasedShape;
}
@Override
public void setPixelScaleFactors(float pixelScaleX, float pixelScaleY) {
this.pixelScaleX = pixelScaleX;
this.pixelScaleY = pixelScaleY;
}
@Override
public float getPixelScaleFactorX() {
return pixelScaleX;
}
@Override
public float getPixelScaleFactorY() {
return pixelScaleY;
}
public void setCamera(NGCamera camera) {
this.camera = camera;
}
public Rectangle getClipRect() {
return (clipRect != null) ? new Rectangle(clipRect) : null;
}
public Rectangle getClipRectNoClone() {
return clipRect;
}
public RectBounds getFinalClipNoClone() {
return finalClipRect;
}
public void setClipRect(Rectangle clipRect) {
this.finalClipRect.setBounds(devClipRect);
if (clipRect == null) {
this.clipRect = null;
} else {
this.clipRect = new Rectangle(clipRect);
this.finalClipRect.intersectWith(clipRect);
}
}
public float getExtraAlpha() {
return extraAlpha;
}
public void setExtraAlpha(float extraAlpha) {
this.extraAlpha = extraAlpha;
}
public CompositeMode getCompositeMode() {
return compMode;
}
public void setCompositeMode(CompositeMode compMode) {
this.compMode = compMode;
}
public Paint getPaint() {
return paint;
}
public void setPaint(Paint paint) {
this.paint = paint;
validateTransformAndPaint();
}
public BasicStroke getStroke() {
return stroke;
}
public void setStroke(BasicStroke stroke) {
this.stroke = stroke;
}
public void clear() {
clear(Color.TRANSPARENT);
}
protected abstract void renderShape(Shape shape, BasicStroke stroke,
float bx, float by, float bw, float bh);
public void fill(Shape shape) {
float bx = 0f, by = 0f, bw = 0f, bh = 0f;
if (paint.isProportional()) {
if (nodeBounds != null) {
bx = nodeBounds.getMinX();
by = nodeBounds.getMinY();
bw = nodeBounds.getWidth();
bh = nodeBounds.getHeight();
} else {
float[] bbox = {
Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY,
};
Shape.accumulate(bbox, shape, BaseTransform.IDENTITY_TRANSFORM);
bx = bbox[0];
by = bbox[1];
bw = bbox[2] - bx;
bh = bbox[3] - by;
}
}
renderShape(shape, null, bx, by, bw, bh);
}
public void draw(Shape shape) {
float bx = 0f, by = 0f, bw = 0f, bh = 0f;
if (paint.isProportional()) {
if (nodeBounds != null) {
bx = nodeBounds.getMinX();
by = nodeBounds.getMinY();
bw = nodeBounds.getWidth();
bh = nodeBounds.getHeight();
} else {
float[] bbox = {
Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY,
};
Shape.accumulate(bbox, shape, BaseTransform.IDENTITY_TRANSFORM);
bx = bbox[0];
by = bbox[1];
bw = bbox[2] - bx;
bh = bbox[3] - by;
}
}
renderShape(shape, stroke, bx, by, bw, bh);
}
@Override
public void drawTexture(Texture tex, float x, float y, float w, float h) {
drawTexture(tex,
x, y, x+w, y+h,
0, 0, w, h);
}
@Override
public void drawTexture(Texture tex,
float dx1, float dy1, float dx2, float dy2,
float sx1, float sy1, float sx2, float sy2)
{
BaseTransform xform = isSimpleTranslate ? IDENT : getTransformNoClone();
PixelFormat format = tex.getPixelFormat();
if (format == PixelFormat.BYTE_ALPHA) {
context.validatePaintOp(this, xform, tex, dx1, dy1, dx2-dx1, dy2-dy1);
} else {
context.validateTextureOp(this, xform, tex, format);
}
if (isSimpleTranslate) {
dx1 += transX;
dy1 += transY;
dx2 += transX;
dy2 += transY;
}
float pw = tex.getPhysicalWidth();
float ph = tex.getPhysicalHeight();
float cx1 = tex.getContentX();
float cy1 = tex.getContentY();
float tx1 = (cx1 + sx1) / pw;
float ty1 = (cy1 + sy1) / ph;
float tx2 = (cx1 + sx2) / pw;
float ty2 = (cy1 + sy2) / ph;
VertexBuffer vb = context.getVertexBuffer();
if (context.isSuperShaderEnabled()) {
vb.addSuperQuad(dx1, dy1, dx2, dy2, tx1, ty1, tx2, ty2, false);
} else {
vb.addQuad(dx1, dy1, dx2, dy2, tx1, ty1, tx2, ty2);
}
}
@Override
public void drawTexture3SliceH(Texture tex,
float dx1, float dy1, float dx2, float dy2,
float sx1, float sy1, float sx2, float sy2,
float dh1, float dh2, float sh1, float sh2)
{
BaseTransform xform = isSimpleTranslate ? IDENT : getTransformNoClone();
PixelFormat format = tex.getPixelFormat();
if (format == PixelFormat.BYTE_ALPHA) {
context.validatePaintOp(this, xform, tex, dx1, dy1, dx2-dx1, dy2-dy1);
} else {
context.validateTextureOp(this, xform, tex, format);
}
if (isSimpleTranslate) {
dx1 += transX;
dy1 += transY;
dx2 += transX;
dy2 += transY;
dh1 += transX;
dh2 += transX;
}
float pw = tex.getPhysicalWidth();
float ph = tex.getPhysicalHeight();
float cx1 = tex.getContentX();
float cy1 = tex.getContentY();
float tx1 = (cx1 + sx1) / pw;
float ty1 = (cy1 + sy1) / ph;
float tx2 = (cx1 + sx2) / pw;
float ty2 = (cy1 + sy2) / ph;
float th1 = (cx1 + sh1) / pw;
float th2 = (cx1 + sh2) / pw;
VertexBuffer vb = context.getVertexBuffer();
if (context.isSuperShaderEnabled()) {
vb.addSuperQuad(dx1, dy1, dh1, dy2, tx1, ty1, th1, ty2, false);
vb.addSuperQuad(dh1, dy1, dh2, dy2, th1, ty1, th2, ty2, false);
vb.addSuperQuad(dh2, dy1, dx2, dy2, th2, ty1, tx2, ty2, false);
} else {
vb.addQuad(dx1, dy1, dh1, dy2, tx1, ty1, th1, ty2);
vb.addQuad(dh1, dy1, dh2, dy2, th1, ty1, th2, ty2);
vb.addQuad(dh2, dy1, dx2, dy2, th2, ty1, tx2, ty2);
}
}
@Override
public void drawTexture3SliceV(Texture tex,
float dx1, float dy1, float dx2, float dy2,
float sx1, float sy1, float sx2, float sy2,
float dv1, float dv2, float sv1, float sv2)
{
BaseTransform xform = isSimpleTranslate ? IDENT : getTransformNoClone();
PixelFormat format = tex.getPixelFormat();
if (format == PixelFormat.BYTE_ALPHA) {
context.validatePaintOp(this, xform, tex, dx1, dy1, dx2-dx1, dy2-dy1);
} else {
context.validateTextureOp(this, xform, tex, format);
}
if (isSimpleTranslate) {
dx1 += transX;
dy1 += transY;
dx2 += transX;
dy2 += transY;
dv1 += transY;
dv2 += transY;
}
float pw = tex.getPhysicalWidth();
float ph = tex.getPhysicalHeight();
float cx1 = tex.getContentX();
float cy1 = tex.getContentY();
float tx1 = (cx1 + sx1) / pw;
float ty1 = (cy1 + sy1) / ph;
float tx2 = (cx1 + sx2) / pw;
float ty2 = (cy1 + sy2) / ph;
float tv1 = (cy1 + sv1) / ph;
float tv2 = (cy1 + sv2) / ph;
VertexBuffer vb = context.getVertexBuffer();
if (context.isSuperShaderEnabled()) {
vb.addSuperQuad(dx1, dy1, dx2, dv1, tx1, ty1, tx2, tv1, false);
vb.addSuperQuad(dx1, dv1, dx2, dv2, tx1, tv1, tx2, tv2, false);
vb.addSuperQuad(dx1, dv2, dx2, dy2, tx1, tv2, tx2, ty2, false);
} else {
vb.addQuad(dx1, dy1, dx2, dv1, tx1, ty1, tx2, tv1);
vb.addQuad(dx1, dv1, dx2, dv2, tx1, tv1, tx2, tv2);
vb.addQuad(dx1, dv2, dx2, dy2, tx1, tv2, tx2, ty2);
}
}
@Override
public void drawTexture9Slice(Texture tex,
float dx1, float dy1, float dx2, float dy2,
float sx1, float sy1, float sx2, float sy2,
float dh1, float dv1, float dh2, float dv2,
float sh1, float sv1, float sh2, float sv2)
{
BaseTransform xform = isSimpleTranslate ? IDENT : getTransformNoClone();
PixelFormat format = tex.getPixelFormat();
if (format == PixelFormat.BYTE_ALPHA) {
context.validatePaintOp(this, xform, tex, dx1, dy1, dx2-dx1, dy2-dy1);
} else {
context.validateTextureOp(this, xform, tex, format);
}
if (isSimpleTranslate) {
dx1 += transX;
dy1 += transY;
dx2 += transX;
dy2 += transY;
dh1 += transX;
dv1 += transY;
dh2 += transX;
dv2 += transY;
}
float pw = tex.getPhysicalWidth();
float ph = tex.getPhysicalHeight();
float cx1 = tex.getContentX();
float cy1 = tex.getContentY();
float tx1 = (cx1 + sx1) / pw;
float ty1 = (cy1 + sy1) / ph;
float tx2 = (cx1 + sx2) / pw;
float ty2 = (cy1 + sy2) / ph;
float th1 = (cx1 + sh1) / pw;
float tv1 = (cy1 + sv1) / ph;
float th2 = (cx1 + sh2) / pw;
float tv2 = (cy1 + sv2) / ph;
VertexBuffer vb = context.getVertexBuffer();
if (context.isSuperShaderEnabled()) {
vb.addSuperQuad(dx1, dy1, dh1, dv1, tx1, ty1, th1, tv1, false);
vb.addSuperQuad(dh1, dy1, dh2, dv1, th1, ty1, th2, tv1, false);
vb.addSuperQuad(dh2, dy1, dx2, dv1, th2, ty1, tx2, tv1, false);
vb.addSuperQuad(dx1, dv1, dh1, dv2, tx1, tv1, th1, tv2, false);
vb.addSuperQuad(dh1, dv1, dh2, dv2, th1, tv1, th2, tv2, false);
vb.addSuperQuad(dh2, dv1, dx2, dv2, th2, tv1, tx2, tv2, false);
vb.addSuperQuad(dx1, dv2, dh1, dy2, tx1, tv2, th1, ty2, false);
vb.addSuperQuad(dh1, dv2, dh2, dy2, th1, tv2, th2, ty2, false);
vb.addSuperQuad(dh2, dv2, dx2, dy2, th2, tv2, tx2, ty2, false);
} else {
vb.addQuad(dx1, dy1, dh1, dv1, tx1, ty1, th1, tv1);
vb.addQuad(dh1, dy1, dh2, dv1, th1, ty1, th2, tv1);
vb.addQuad(dh2, dy1, dx2, dv1, th2, ty1, tx2, tv1);
vb.addQuad(dx1, dv1, dh1, dv2, tx1, tv1, th1, tv2);
vb.addQuad(dh1, dv1, dh2, dv2, th1, tv1, th2, tv2);
vb.addQuad(dh2, dv1, dx2, dv2, th2, tv1, tx2, tv2);
vb.addQuad(dx1, dv2, dh1, dy2, tx1, tv2, th1, ty2);
vb.addQuad(dh1, dv2, dh2, dy2, th1, tv2, th2, ty2);
vb.addQuad(dh2, dv2, dx2, dy2, th2, tv2, tx2, ty2);
}
}
public void drawTextureVO(Texture tex,
float topopacity, float botopacity,
float dx1, float dy1, float dx2, float dy2,
float sx1, float sy1, float sx2, float sy2)
{
BaseTransform xform = isSimpleTranslate ? IDENT : getTransformNoClone();
PixelFormat format = tex.getPixelFormat();
if (format == PixelFormat.BYTE_ALPHA) {
context.validatePaintOp(this, xform, tex, dx1, dy1, dx2-dx1, dy2-dy1);
} else {
context.validateTextureOp(this, xform, tex, format);
}
if (isSimpleTranslate) {
dx1 += transX;
dy1 += transY;
dx2 += transX;
dy2 += transY;
}
float tw = tex.getPhysicalWidth();
float th = tex.getPhysicalHeight();
float cx1 = tex.getContentX();
float cy1 = tex.getContentY();
float tx1 = (cx1 + sx1) / tw;
float ty1 = (cy1 + sy1) / th;
float tx2 = (cx1 + sx2) / tw;
float ty2 = (cy1 + sy2) / th;
VertexBuffer vb = context.getVertexBuffer();
if (topopacity == 1f && botopacity == 1f) {
vb.addQuad(dx1, dy1, dx2, dy2,
tx1, ty1, tx2, ty2);
} else {
topopacity *= getExtraAlpha();
botopacity *= getExtraAlpha();
vb.addQuadVO(topopacity, botopacity,
dx1, dy1, dx2, dy2,
tx1, ty1, tx2, ty2);
}
}
public void drawTextureRaw(Texture tex,
float dx1, float dy1, float dx2, float dy2,
float tx1, float ty1, float tx2, float ty2)
{
float bx = dx1;
float by = dy1;
float bw = dx2 - dx1;
float bh = dy2 - dy1;
BaseTransform xform = getTransformNoClone();
if (isSimpleTranslate) {
xform = IDENT;
dx1 += transX;
dy1 += transY;
dx2 += transX;
dy2 += transY;
}
PixelFormat format = tex.getPixelFormat();
if (format == PixelFormat.BYTE_ALPHA) {
context.validatePaintOp(this, xform, tex, bx, by, bw, bh);
} else {
context.validateTextureOp(this, xform, tex, format);
}
VertexBuffer vb = context.getVertexBuffer();
vb.addQuad(dx1, dy1, dx2, dy2,
tx1, ty1, tx2, ty2);
}
public void drawMappedTextureRaw(Texture tex,
float dx1, float dy1, float dx2, float dy2,
float tx11, float ty11, float tx21, float ty21,
float tx12, float ty12, float tx22, float ty22)
{
float bx = dx1;
float by = dy1;
float bw = dx2 - dx1;
float bh = dy2 - dy1;
BaseTransform xform = getTransformNoClone();
if (isSimpleTranslate) {
xform = IDENT;
dx1 += transX;
dy1 += transY;
dx2 += transX;
dy2 += transY;
}
PixelFormat format = tex.getPixelFormat();
if (format == PixelFormat.BYTE_ALPHA) {
context.validatePaintOp(this, xform, tex, bx, by, bw, bh);
} else {
context.validateTextureOp(this, xform, tex, format);
}
VertexBuffer vb = context.getVertexBuffer();
vb.addMappedQuad(dx1, dy1, dx2, dy2,
tx11, ty11, tx21, ty21,
tx12, ty12, tx22, ty22);
}
}
