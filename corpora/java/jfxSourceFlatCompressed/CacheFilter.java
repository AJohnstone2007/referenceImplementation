package com.sun.javafx.sg.prism;
import com.sun.javafx.logging.PulseLogger;
import javafx.scene.CacheHint;
import java.util.List;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.Affine2D;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.prism.Graphics;
import com.sun.prism.RTTexture;
import com.sun.prism.Texture;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.Filterable;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.impl.prism.PrDrawable;
import com.sun.scenario.effect.impl.prism.PrFilterContext;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
public class CacheFilter {
private static enum ScrollCacheState {
CHECKING_PRECONDITIONS,
ENABLED,
DISABLED
}
private static final Rectangle TEMP_RECT = new Rectangle();
private static final DirtyRegionContainer TEMP_CONTAINER = new DirtyRegionContainer(1);
private static final Affine3D TEMP_CACHEFILTER_TRANSFORM = new Affine3D();
private static final RectBounds TEMP_BOUNDS = new RectBounds();
private static final double EPSILON = 0.0000001;
private RTTexture tempTexture;
private double lastXDelta;
private double lastYDelta;
private ScrollCacheState scrollCacheState = ScrollCacheState.CHECKING_PRECONDITIONS;
private ImageData cachedImageData;
private Rectangle cacheBounds = new Rectangle();
private final Affine2D cachedXform = new Affine2D();
private double cachedScaleX;
private double cachedScaleY;
private double cachedRotate;
private double cachedX;
private double cachedY;
private NGNode node;
private final Affine2D screenXform = new Affine2D();
private boolean scaleHint;
private boolean rotateHint;
private CacheHint cacheHint;
private boolean wasUnsupported = false;
private Rectangle computeDirtyRegionForTranslate() {
if (lastXDelta != 0) {
if (lastXDelta > 0) {
TEMP_RECT.setBounds(0, 0, (int)lastXDelta, cacheBounds.height);
} else {
TEMP_RECT.setBounds(cacheBounds.width + (int)lastXDelta, 0, -(int)lastXDelta, cacheBounds.height);
}
} else {
if (lastYDelta > 0) {
TEMP_RECT.setBounds(0, 0, cacheBounds.width, (int)lastYDelta);
} else {
TEMP_RECT.setBounds(0, cacheBounds.height + (int)lastYDelta, cacheBounds.width, -(int)lastYDelta);
}
}
return TEMP_RECT;
}
protected CacheFilter(NGNode node, CacheHint cacheHint) {
this.node = node;
this.scrollCacheState = ScrollCacheState.CHECKING_PRECONDITIONS;
setHint(cacheHint);
}
public void setHint(CacheHint cacheHint) {
this.cacheHint = cacheHint;
this.scaleHint = (cacheHint == CacheHint.SPEED ||
cacheHint == CacheHint.SCALE ||
cacheHint == CacheHint.SCALE_AND_ROTATE);
this.rotateHint = (cacheHint == CacheHint.SPEED ||
cacheHint == CacheHint.ROTATE ||
cacheHint == CacheHint.SCALE_AND_ROTATE);
}
final boolean isScaleHint() { return scaleHint; }
final boolean isRotateHint() { return rotateHint; }
boolean matchesHint(CacheHint cacheHint) {
return this.cacheHint == cacheHint;
}
boolean unsupported(double[] xformInfo) {
double scaleX = xformInfo[0];
double scaleY = xformInfo[1];
double rotate = xformInfo[2];
if (rotate > EPSILON || rotate < -EPSILON) {
if (scaleX > scaleY + EPSILON || scaleY > scaleX + EPSILON ||
scaleX < scaleY - EPSILON || scaleY < scaleX - EPSILON ||
cachedScaleX > cachedScaleY + EPSILON ||
cachedScaleY > cachedScaleX + EPSILON ||
cachedScaleX < cachedScaleY - EPSILON ||
cachedScaleY < cachedScaleX - EPSILON ) {
return true;
}
}
return false;
}
private boolean isXformScrollCacheCapable(double[] xformInfo) {
if (unsupported(xformInfo)) {
return false;
}
double rotate = xformInfo[2];
return rotateHint || rotate == 0;
}
private boolean needToRenderCache(BaseTransform renderXform, double[] xformInfo,
float pixelScaleX, float pixelScaleY)
{
if (cachedImageData == null) {
return true;
}
if (lastXDelta != 0 || lastYDelta != 0) {
if (Math.abs(lastXDelta) >= cacheBounds.width || Math.abs(lastYDelta) >= cacheBounds.height ||
Math.rint(lastXDelta) != lastXDelta || Math.rint(lastYDelta) != lastYDelta) {
node.clearDirtyTree();
lastXDelta = lastYDelta = 0;
return true;
}
if (scrollCacheState == ScrollCacheState.CHECKING_PRECONDITIONS) {
if (scrollCacheCapable() && isXformScrollCacheCapable(xformInfo)) {
scrollCacheState = ScrollCacheState.ENABLED;
} else {
scrollCacheState = ScrollCacheState.DISABLED;
return true;
}
}
}
if (cachedXform.getMxx() == renderXform.getMxx() &&
cachedXform.getMyy() == renderXform.getMyy() &&
cachedXform.getMxy() == renderXform.getMxy() &&
cachedXform.getMyx() == renderXform.getMyx()) {
return false;
}
if (wasUnsupported || unsupported(xformInfo)) {
return true;
}
double scaleX = xformInfo[0];
double scaleY = xformInfo[1];
double rotate = xformInfo[2];
if (scaleHint) {
if (cachedScaleX < pixelScaleX || cachedScaleY < pixelScaleY) {
return true;
}
if (rotateHint) {
return false;
} else {
if (cachedRotate - EPSILON < rotate && rotate < cachedRotate + EPSILON) {
return false;
} else {
return true;
}
}
} else {
if (rotateHint) {
if (cachedScaleX - EPSILON < scaleX && scaleX < cachedScaleX + EPSILON &&
cachedScaleY - EPSILON < scaleY && scaleY < cachedScaleY + EPSILON) {
return false;
} else {
return true;
}
}
else {
return true;
}
}
}
void updateScreenXform(double[] xformInfo) {
if (scaleHint) {
if (rotateHint) {
double screenScaleX = xformInfo[0] / cachedScaleX;
double screenScaleY = xformInfo[1] / cachedScaleY;
double screenRotate = xformInfo[2] - cachedRotate;
screenXform.setToScale(screenScaleX, screenScaleY);
screenXform.rotate(screenRotate);
} else {
double screenScaleX = xformInfo[0] / cachedScaleX;
double screenScaleY = xformInfo[1] / cachedScaleY;
screenXform.setToScale(screenScaleX, screenScaleY);
}
} else {
if (rotateHint) {
double screenRotate = xformInfo[2] - cachedRotate;
screenXform.setToRotation(screenRotate, 0.0, 0.0);
} else {
screenXform.setTransform(BaseTransform.IDENTITY_TRANSFORM);
}
}
}
public void invalidate() {
if (scrollCacheState == ScrollCacheState.ENABLED) {
scrollCacheState = ScrollCacheState.CHECKING_PRECONDITIONS;
}
imageDataUnref();
lastXDelta = lastYDelta = 0;
}
void imageDataUnref() {
if (tempTexture != null) {
tempTexture.dispose();
tempTexture = null;
}
if (cachedImageData != null) {
Filterable implImage = cachedImageData.getUntransformedImage();
if (implImage != null) {
implImage.lock();
}
cachedImageData.unref();
cachedImageData = null;
}
}
void invalidateByTranslation(double translateXDelta, double translateYDelta) {
if (cachedImageData == null) {
return;
}
if (scrollCacheState == ScrollCacheState.DISABLED) {
imageDataUnref();
} else {
if (translateXDelta != 0 && translateYDelta != 0) {
imageDataUnref();
} else {
lastYDelta = translateYDelta;
lastXDelta = translateXDelta;
}
}
}
public void dispose() {
invalidate();
node = null;
}
double[] unmatrix(BaseTransform xform) {
double[] retVal = new double[3];
double[][] row = {{xform.getMxx(), xform.getMxy()},
{xform.getMyx(), xform.getMyy()}};
final double xSignum = unitDir(row[0][0]);
final double ySignum = unitDir(row[1][1]);
double scaleX = xSignum * v2length(row[0]);
v2scale(row[0], xSignum);
double shearXY = v2dot(row[0], row[1]);
v2combine(row[1], row[0], row[1], 1.0, -shearXY);
double scaleY = ySignum * v2length(row[1]);
v2scale(row[1], ySignum);
double sin = row[1][0];
double cos = row[0][0];
double angleRad = 0.0;
if (sin >= 0) {
angleRad = Math.acos(cos);
} else {
if (cos > 0) {
angleRad = 2.0 * Math.PI + Math.asin(sin);
} else {
angleRad = Math.PI + Math.acos(-cos);
}
}
retVal[0] = scaleX;
retVal[1] = scaleY;
retVal[2] = angleRad;
return retVal;
}
double unitDir(double v) {
return v < 0.0 ? -1.0 : 1.0;
}
void v2combine(double v0[], double v1[], double result[], double scalarA, double scalarB) {
result[0] = scalarA*v0[0] + scalarB*v1[0];
result[1] = scalarA*v0[1] + scalarB*v1[1];
}
double v2dot(double v0[], double v1[]) {
return v0[0]*v1[0] + v0[1]*v1[1];
}
void v2scale(double v[], double newLen) {
double len = v2length(v);
if (len != 0) {
v[0] *= newLen / len;
v[1] *= newLen / len;
}
}
double v2length(double v[]) {
return Math.sqrt(v[0]*v[0] + v[1]*v[1]);
}
void render(Graphics g) {
BaseTransform xform = g.getTransformNoClone();
FilterContext fctx = PrFilterContext.getInstance(g.getAssociatedScreen());
double[] xformInfo = unmatrix(xform);
boolean isUnsupported = unsupported(xformInfo);
lastXDelta = lastXDelta * xformInfo[0];
lastYDelta = lastYDelta * xformInfo[1];
if (cachedImageData != null) {
Filterable implImage = cachedImageData.getUntransformedImage();
if (implImage != null) {
implImage.lock();
if (!cachedImageData.validate(fctx)) {
implImage.unlock();
invalidate();
}
}
}
float pixelScaleX = g.getPixelScaleFactorX();
float pixelScaleY = g.getPixelScaleFactorY();
if (needToRenderCache(xform, xformInfo, pixelScaleX, pixelScaleY)) {
if (PulseLogger.PULSE_LOGGING_ENABLED) {
PulseLogger.incrementCounter("CacheFilter rebuilding");
}
if (cachedImageData != null) {
Filterable implImage = cachedImageData.getUntransformedImage();
if (implImage != null) {
implImage.unlock();
}
invalidate();
}
if (scaleHint) {
cachedScaleX = Math.max(pixelScaleX, xformInfo[0]);
cachedScaleY = Math.max(pixelScaleY, xformInfo[1]);
cachedRotate = 0;
cachedXform.setTransform(cachedScaleX, 0.0,
0.0, cachedScaleX,
0.0, 0.0);
updateScreenXform(xformInfo);
} else {
cachedScaleX = xformInfo[0];
cachedScaleY = xformInfo[1];
cachedRotate = xformInfo[2];
cachedXform.setTransform(xform.getMxx(), xform.getMyx(),
xform.getMxy(), xform.getMyy(),
0.0, 0.0);
screenXform.setTransform(BaseTransform.IDENTITY_TRANSFORM);
}
cacheBounds = getCacheBounds(cacheBounds, cachedXform);
cachedImageData = createImageData(fctx, cacheBounds);
renderNodeToCache(cachedImageData, cacheBounds, cachedXform, null);
Rectangle cachedBounds = cachedImageData.getUntransformedBounds();
cachedX = cachedBounds.x;
cachedY = cachedBounds.y;
} else {
if (scrollCacheState == ScrollCacheState.ENABLED &&
(lastXDelta != 0 || lastYDelta != 0) ) {
moveCacheBy(cachedImageData, lastXDelta, lastYDelta);
renderNodeToCache(cachedImageData, cacheBounds, cachedXform, computeDirtyRegionForTranslate());
lastXDelta = lastYDelta = 0;
}
if (isUnsupported) {
screenXform.setTransform(BaseTransform.IDENTITY_TRANSFORM);
} else {
updateScreenXform(xformInfo);
}
}
wasUnsupported = isUnsupported;
Filterable implImage = cachedImageData.getUntransformedImage();
if (implImage == null) {
if (PulseLogger.PULSE_LOGGING_ENABLED) {
PulseLogger.incrementCounter("CacheFilter not used");
}
renderNodeToScreen(g);
} else {
double mxt = xform.getMxt();
double myt = xform.getMyt();
renderCacheToScreen(g, implImage, mxt, myt);
implImage.unlock();
}
}
ImageData createImageData(FilterContext fctx, Rectangle bounds) {
Filterable ret;
try {
ret = Effect.getCompatibleImage(fctx,
bounds.width, bounds.height);
Texture cachedTex = ((PrDrawable) ret).getTextureObject();
cachedTex.contentsUseful();
} catch (Throwable e) {
ret = null;
}
return new ImageData(fctx, ret, bounds);
}
void renderNodeToCache(ImageData cacheData,
Rectangle cacheBounds,
BaseTransform xform,
Rectangle dirtyBounds) {
final PrDrawable image = (PrDrawable) cacheData.getUntransformedImage();
if (image != null) {
Graphics g = image.createGraphics();
TEMP_CACHEFILTER_TRANSFORM.setToIdentity();
TEMP_CACHEFILTER_TRANSFORM.translate(-cacheBounds.x, -cacheBounds.y);
if (xform != null) {
TEMP_CACHEFILTER_TRANSFORM.concatenate(xform);
}
if (dirtyBounds != null) {
TEMP_CONTAINER.deriveWithNewRegion((RectBounds)TEMP_BOUNDS.deriveWithNewBounds(dirtyBounds));
node.doPreCulling(TEMP_CONTAINER, TEMP_CACHEFILTER_TRANSFORM, new GeneralTransform3D());
g.setHasPreCullingBits(true);
g.setClipRectIndex(0);
g.setClipRect(dirtyBounds);
}
g.transform(TEMP_CACHEFILTER_TRANSFORM);
if (node.getClipNode() != null) {
node.renderClip(g);
} else if (node.getEffectFilter() != null) {
node.renderEffect(g);
} else {
node.renderContent(g);
}
}
}
void renderNodeToScreen(Object implGraphics) {
Graphics g = (Graphics)implGraphics;
if (node.getEffectFilter() != null) {
node.renderEffect(g);
} else {
node.renderContent(g);
}
}
void renderCacheToScreen(Object implGraphics, Filterable implImage,
double mxt, double myt)
{
Graphics g = (Graphics)implGraphics;
g.setTransform(screenXform.getMxx(),
screenXform.getMyx(),
screenXform.getMxy(),
screenXform.getMyy(),
mxt, myt);
g.translate((float)cachedX, (float)cachedY);
Texture cachedTex = ((PrDrawable)implImage).getTextureObject();
Rectangle cachedBounds = cachedImageData.getUntransformedBounds();
g.drawTexture(cachedTex, 0, 0,
cachedBounds.width, cachedBounds.height);
}
boolean scrollCacheCapable() {
if (!(node instanceof NGGroup)) {
return false;
}
List<NGNode> children = ((NGGroup)node).getChildren();
if (children.size() != 1) {
return false;
}
NGNode child = children.get(0);
if (!child.getTransform().is2D()) {
return false;
}
NGNode clip = node.getClipNode();
if (clip == null || !clip.isRectClip(BaseTransform.IDENTITY_TRANSFORM, false)) {
return false;
}
if (node instanceof NGRegion) {
NGRegion region = (NGRegion) node;
if (!region.getBorder().isEmpty()) {
return false;
}
final Background background = region.getBackground();
if (!background.isEmpty()) {
if (!background.getImages().isEmpty()
|| background.getFills().size() != 1) {
return false;
}
BackgroundFill fill = background.getFills().get(0);
javafx.scene.paint.Paint fillPaint = fill.getFill();
BaseBounds clipBounds = clip.getCompleteBounds(TEMP_BOUNDS, BaseTransform.IDENTITY_TRANSFORM);
return fillPaint.isOpaque() && fillPaint instanceof Color && fill.getInsets().equals(Insets.EMPTY)
&& clipBounds.getMinX() == 0 && clipBounds.getMinY() == 0
&& clipBounds.getMaxX() == region.getWidth() && clipBounds.getMaxY() == region.getHeight();
}
}
return true;
}
void moveCacheBy(ImageData cachedImageData, double xDelta, double yDelta) {
PrDrawable drawable = (PrDrawable) cachedImageData.getUntransformedImage();
final Rectangle r = cachedImageData.getUntransformedBounds();
int x = (int)Math.max(0, (-xDelta));
int y = (int)Math.max(0, (-yDelta));
int destX = (int)Math.max(0, (xDelta));
int destY = (int) Math.max(0, yDelta);
int w = r.width - (int) Math.abs(xDelta);
int h = r.height - (int) Math.abs(yDelta);
final Graphics g = drawable.createGraphics();
if (tempTexture != null) {
tempTexture.lock();
if (tempTexture.isSurfaceLost()) {
tempTexture = null;
}
}
if (tempTexture == null) {
tempTexture = g.getResourceFactory().
createRTTexture(drawable.getPhysicalWidth(), drawable.getPhysicalHeight(),
Texture.WrapMode.CLAMP_NOT_NEEDED);
}
final Graphics tempG = tempTexture.createGraphics();
tempG.clear();
tempG.drawTexture(drawable.getTextureObject(), 0, 0, w, h, x, y, x + w, y + h);
tempG.sync();
g.clear();
g.drawTexture(tempTexture, destX, destY, destX + w, destY + h, 0, 0, w, h);
tempTexture.unlock();
}
Rectangle getCacheBounds(Rectangle bounds, BaseTransform xform) {
final BaseBounds b = node.getClippedBounds(TEMP_BOUNDS, xform);
bounds.setBounds(b);
return bounds;
}
BaseBounds computeDirtyBounds(BaseBounds region, BaseTransform tx, GeneralTransform3D pvTx) {
if (!node.dirtyBounds.isEmpty()) {
region = region.deriveWithNewBounds(node.dirtyBounds);
} else {
region = region.deriveWithNewBounds(node.transformedBounds);
}
if (!region.isEmpty()) {
region.roundOut();
region = node.computePadding(region);
region = tx.transform(region, region);
region = pvTx.transform(region, region);
}
return region;
}
}
