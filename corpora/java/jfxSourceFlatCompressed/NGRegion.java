package com.sun.javafx.sg.prism;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderImage;
import javafx.scene.layout.BorderRepeat;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;
import com.sun.glass.ui.Screen;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.Affine2D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.logging.PulseLogger;
import static com.sun.javafx.logging.PulseLogger.PULSE_LOGGING_ENABLED;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.tk.Toolkit;
import com.sun.prism.BasicStroke;
import com.sun.prism.Graphics;
import com.sun.prism.Image;
import com.sun.prism.RTTexture;
import com.sun.prism.Texture;
import com.sun.prism.impl.PrismSettings;
import com.sun.prism.paint.ImagePattern;
import com.sun.prism.paint.Paint;
import com.sun.prism.PrinterGraphics;
import com.sun.scenario.effect.Offset;
public class NGRegion extends NGGroup {
private static final Affine2D SCRATCH_AFFINE = new Affine2D();
private static final Rectangle TEMP_RECT = new Rectangle();
private static WeakHashMap<Screen, RegionImageCache> imageCacheMap = new WeakHashMap<>();
private static final int CACHE_SLICE_V = 0x1;
private static final int CACHE_SLICE_H = 0x2;
private Background background = Background.EMPTY;
private Insets backgroundInsets = Insets.EMPTY;
private Border border = Border.EMPTY;
private List<CornerRadii> normalizedFillCorners;
private List<CornerRadii> normalizedStrokeCorners;
private Shape shape;
private NGShape ngShape;
private boolean scaleShape = true;
private boolean centerShape = true;
private boolean cacheShape = false;
private float opaqueTop = Float.NaN,
opaqueRight = Float.NaN,
opaqueBottom = Float.NaN,
opaqueLeft = Float.NaN;
private float width, height;
private int cacheMode;
private Integer cacheKey;
static Paint getPlatformPaint(javafx.scene.paint.Paint paint) {
return (Paint)Toolkit.getPaintAccessor().getPlatformPaint(paint);
}
private static final Offset nopEffect = new Offset(0, 0, null);
private EffectFilter nopEffectFilter;
public void updateShape(Object shape, boolean scaleShape, boolean positionShape, boolean cacheShape) {
this.ngShape = shape == null ? null : NodeHelper.getPeer(((javafx.scene.shape.Shape)shape));
this.shape = shape == null ? null : ngShape.getShape();
this.scaleShape = scaleShape;
this.centerShape = positionShape;
this.cacheShape = cacheShape;
invalidateOpaqueRegion();
cacheKey = null;
visualsChanged();
}
public void setSize(float width, float height) {
this.width = width;
this.height = height;
invalidateOpaqueRegion();
cacheKey = null;
visualsChanged();
if (background != null && background.isFillPercentageBased()) {
backgroundInsets = null;
}
}
public void imagesUpdated() {
visualsChanged();
}
public void updateBorder(Border b) {
final Border old = border;
border = b == null ? Border.EMPTY : b;
if (!border.getOutsets().equals(old.getOutsets())) {
geometryChanged();
} else {
visualsChanged();
}
}
public void updateStrokeCorners(List<CornerRadii> normalizedStrokeCorners) {
this.normalizedStrokeCorners = normalizedStrokeCorners;
}
private CornerRadii getNormalizedStrokeRadii(int index) {
return (normalizedStrokeCorners == null
? border.getStrokes().get(index).getRadii()
: normalizedStrokeCorners.get(index));
}
public void updateBackground(Background b) {
final Background old = background;
background = b == null ? Background.EMPTY : b;
final List<BackgroundFill> fills = background.getFills();
cacheMode = 0;
if (!PrismSettings.disableRegionCaching && !fills.isEmpty() && (shape == null || cacheShape)) {
cacheMode = CACHE_SLICE_H | CACHE_SLICE_V;
for (int i=0, max=fills.size(); i<max && cacheMode != 0; i++) {
final BackgroundFill fill = fills.get(i);
javafx.scene.paint.Paint paint = fill.getFill();
if (shape == null) {
if (paint instanceof LinearGradient) {
LinearGradient linear = (LinearGradient) paint;
if (linear.getStartX() != linear.getEndX()) {
cacheMode &= ~CACHE_SLICE_H;
}
if (linear.getStartY() != linear.getEndY()) {
cacheMode &= ~CACHE_SLICE_V;
}
} else if (!(paint instanceof Color)) {
cacheMode = 0;
}
} else if (paint instanceof javafx.scene.paint.ImagePattern) {
cacheMode = 0;
}
}
}
backgroundInsets = null;
cacheKey = null;
if (!background.getOutsets().equals(old.getOutsets())) {
geometryChanged();
} else {
visualsChanged();
}
}
public void updateFillCorners(List<CornerRadii> normalizedFillCorners) {
this.normalizedFillCorners = normalizedFillCorners;
}
private CornerRadii getNormalizedFillRadii(int index) {
return (normalizedFillCorners == null
? background.getFills().get(index).getRadii()
: normalizedFillCorners.get(index));
}
public void setOpaqueInsets(float top, float right, float bottom, float left) {
opaqueTop = top;
opaqueRight = right;
opaqueBottom = bottom;
opaqueLeft = left;
invalidateOpaqueRegion();
}
@Override public void clearDirtyTree() {
super.clearDirtyTree();
if (ngShape != null) {
ngShape.clearDirtyTree();
}
}
private RegionImageCache getImageCache(final Graphics g) {
final Screen screen = g.getAssociatedScreen();
RegionImageCache cache = imageCacheMap.get(screen);
if (cache != null) {
RTTexture tex = cache.getBackingStore();
if (tex.isSurfaceLost()) {
imageCacheMap.remove(screen);
cache = null;
}
}
if (cache == null) {
cache = new RegionImageCache(g.getResourceFactory());
imageCacheMap.put(screen, cache);
}
return cache;
}
private Integer getCacheKey(int w, int h) {
if (cacheKey == null) {
int key = 31 * w;
key = key * 37 + h;
key = key * 47 + background.hashCode();
if (shape != null) {
key = key * 73 + shape.hashCode();
}
cacheKey = key;
}
return cacheKey;
}
@Override protected boolean supportsOpaqueRegions() { return true; }
@Override
protected boolean hasOpaqueRegion() {
return super.hasOpaqueRegion() &&
!Float.isNaN(opaqueTop) && !Float.isNaN(opaqueRight) &&
!Float.isNaN(opaqueBottom) && !Float.isNaN(opaqueLeft);
}
@Override protected RectBounds computeOpaqueRegion(RectBounds opaqueRegion) {
return (RectBounds) opaqueRegion.deriveWithNewBounds(opaqueLeft, opaqueTop, 0, width - opaqueRight, height - opaqueBottom, 0);
}
@Override protected RenderRootResult computeRenderRoot(NodePath path, RectBounds dirtyRegion,
int cullingIndex, BaseTransform tx,
GeneralTransform3D pvTx) {
RenderRootResult result = super.computeRenderRoot(path, dirtyRegion, cullingIndex, tx, pvTx);
if (result == RenderRootResult.NO_RENDER_ROOT){
result = computeNodeRenderRoot(path, dirtyRegion, cullingIndex, tx, pvTx);
}
return result;
}
@Override protected boolean hasVisuals() {
return !border.isEmpty() || !background.isEmpty();
}
@Override protected boolean hasOverlappingContents() {
return true;
}
@Override protected void renderContent(Graphics g) {
if (!g.getTransformNoClone().is2D() && this.isContentBounds2D()) {
assert (getEffectFilter() == null);
if (nopEffectFilter == null) {
nopEffectFilter = new EffectFilter(nopEffect, this);
}
nopEffectFilter.render(g);
return;
}
if (shape != null) {
renderAsShape(g);
} else if (width > 0 && height > 0) {
renderAsRectangle(g);
}
super.renderContent(g);
}
private void renderAsShape(Graphics g) {
if (!background.isEmpty()) {
final Insets outsets = background.getOutsets();
final Shape outsetShape = resizeShape((float) -outsets.getTop(), (float) -outsets.getRight(),
(float) -outsets.getBottom(), (float) -outsets.getLeft());
final RectBounds outsetShapeBounds = outsetShape.getBounds();
final int textureWidth = Math.round(outsetShapeBounds.getWidth()),
textureHeight = Math.round(outsetShapeBounds.getHeight());
final int border = 1;
RTTexture cached = null;
Rectangle rect = null;
if (cacheMode != 0 && g.getTransformNoClone().isTranslateOrIdentity() && !(g instanceof PrinterGraphics)) {
final RegionImageCache imageCache = getImageCache(g);
if (imageCache.isImageCachable(textureWidth, textureHeight)) {
final Integer key = getCacheKey(textureWidth, textureHeight);
rect = TEMP_RECT;
rect.setBounds(0, 0, textureWidth + border, textureHeight + border);
boolean render = imageCache.getImageLocation(key, rect, background, shape, g);
if (!rect.isEmpty()) {
cached = imageCache.getBackingStore();
}
if (cached != null && render) {
Graphics cachedGraphics = cached.createGraphics();
cachedGraphics.translate(rect.x - outsetShapeBounds.getMinX(),
rect.y - outsetShapeBounds.getMinY());
renderBackgroundShape(cachedGraphics);
if (PULSE_LOGGING_ENABLED) {
PulseLogger.incrementCounter("Rendering region shape image to cache");
}
}
}
}
if (cached != null) {
final float dstX1 = outsetShapeBounds.getMinX();
final float dstY1 = outsetShapeBounds.getMinY();
final float dstX2 = outsetShapeBounds.getMaxX();
final float dstY2 = outsetShapeBounds.getMaxY();
final float srcX1 = rect.x;
final float srcY1 = rect.y;
final float srcX2 = srcX1 + textureWidth;
final float srcY2 = srcY1 + textureHeight;
g.drawTexture(cached, dstX1, dstY1, dstX2, dstY2, srcX1, srcY1, srcX2, srcY2);
if (PULSE_LOGGING_ENABLED) {
PulseLogger.incrementCounter("Cached region shape image used");
}
} else {
renderBackgroundShape(g);
}
}
if (!border.isEmpty()) {
final List<BorderStroke> strokes = border.getStrokes();
for (int i = 0, max = strokes.size(); i < max; i++) {
final BorderStroke stroke = strokes.get(i);
setBorderStyle(g, stroke, -1, false);
final Insets insets = stroke.getInsets();
g.draw(resizeShape((float) insets.getTop(), (float) insets.getRight(),
(float) insets.getBottom(), (float) insets.getLeft()));
}
}
}
private void renderBackgroundShape(Graphics g) {
if (PULSE_LOGGING_ENABLED) {
PulseLogger.incrementCounter("NGRegion renderBackgroundShape slow path");
PulseLogger.addMessage("Slow shape path for " + getName());
}
final List<BackgroundFill> fills = background.getFills();
for (int i = 0, max = fills.size(); i < max; i++) {
final BackgroundFill fill = fills.get(i);
final Paint paint = getPlatformPaint(fill.getFill());
assert paint != null;
g.setPaint(paint);
final Insets insets = fill.getInsets();
g.fill(resizeShape((float) insets.getTop(), (float) insets.getRight(),
(float) insets.getBottom(), (float) insets.getLeft()));
}
final List<BackgroundImage> images = background.getImages();
for (int i = 0, max = images.size(); i < max; i++) {
final BackgroundImage image = images.get(i);
final Image prismImage = (Image) Toolkit.getImageAccessor().getPlatformImage(image.getImage());
if (prismImage == null) {
continue;
}
final Shape translatedShape = resizeShape(0, 0, 0, 0);
final RectBounds bounds = translatedShape.getBounds();
ImagePattern pattern = image.getSize().isCover() ?
new ImagePattern(prismImage, bounds.getMinX(), bounds.getMinY(),
bounds.getWidth(), bounds.getHeight(), false, false) :
new ImagePattern(prismImage, bounds.getMinX(), bounds.getMinY(),
prismImage.getWidth(), prismImage.getHeight(), false, false);
g.setPaint(pattern);
g.fill(translatedShape);
}
}
private void renderAsRectangle(Graphics g) {
if (!background.isEmpty()) {
renderBackgroundRectangle(g);
}
if (!border.isEmpty()) {
renderBorderRectangle(g);
}
}
private void renderBackgroundRectangle(Graphics g) {
if (backgroundInsets == null) updateBackgroundInsets();
final double leftInset = backgroundInsets.getLeft() + 1;
final double rightInset = backgroundInsets.getRight() + 1;
final double topInset = backgroundInsets.getTop() + 1;
final double bottomInset = backgroundInsets.getBottom() + 1;
int cacheWidth = roundUp(width);
if ((cacheMode & CACHE_SLICE_H) != 0) {
cacheWidth = Math.min(cacheWidth, (int) (leftInset + rightInset));
}
int cacheHeight = roundUp(height);
if ((cacheMode & CACHE_SLICE_V) != 0) {
cacheHeight = Math.min(cacheHeight, (int) (topInset + bottomInset));
}
final Insets outsets = background.getOutsets();
final int outsetsTop = roundUp(outsets.getTop());
final int outsetsRight = roundUp(outsets.getRight());
final int outsetsBottom = roundUp(outsets.getBottom());
final int outsetsLeft = roundUp(outsets.getLeft());
final int textureWidth = outsetsLeft + cacheWidth + outsetsRight;
final int textureHeight = outsetsTop + cacheHeight + outsetsBottom;
final boolean cache =
background.getFills().size() > 1 &&
cacheMode != 0 &&
g.getTransformNoClone().isTranslateOrIdentity() &&
!(g instanceof PrinterGraphics);
final int border = 1;
RTTexture cached = null;
Rectangle rect = null;
if (cache) {
RegionImageCache imageCache = getImageCache(g);
if (imageCache.isImageCachable(textureWidth, textureHeight)) {
final Integer key = getCacheKey(textureWidth, textureHeight);
rect = TEMP_RECT;
rect.setBounds(0, 0, textureWidth + border, textureHeight + border);
boolean render = imageCache.getImageLocation(key, rect, background, shape, g);
if (!rect.isEmpty()) {
cached = imageCache.getBackingStore();
}
if (cached != null && render) {
Graphics cacheGraphics = cached.createGraphics();
cacheGraphics.translate(rect.x + outsetsLeft, rect.y + outsetsTop);
renderBackgroundRectanglesDirectly(cacheGraphics, cacheWidth, cacheHeight);
if (PULSE_LOGGING_ENABLED) {
PulseLogger.incrementCounter("Rendering region background image to cache");
}
}
}
}
if (cached != null) {
renderBackgroundRectangleFromCache(
g, cached, rect, textureWidth, textureHeight,
topInset, rightInset, bottomInset, leftInset,
outsetsTop, outsetsRight, outsetsBottom, outsetsLeft);
} else {
renderBackgroundRectanglesDirectly(g, width, height);
}
final List<BackgroundImage> images = background.getImages();
for (int i = 0, max = images.size(); i < max; i++) {
final BackgroundImage image = images.get(i);
Image prismImage = (Image) Toolkit.getImageAccessor().getPlatformImage(image.getImage());
if (prismImage == null) {
continue;
}
final int imgUnscaledWidth = (int)image.getImage().getWidth();
final int imgUnscaledHeight = (int)image.getImage().getHeight();
final int imgWidth = prismImage.getWidth();
final int imgHeight = prismImage.getHeight();
if (imgWidth != 0 && imgHeight != 0) {
final BackgroundSize size = image.getSize();
if (size.isCover()) {
final float scale = Math.max(width / imgWidth,height / imgHeight);
final Texture texture =
g.getResourceFactory().getCachedTexture(prismImage, Texture.WrapMode.CLAMP_TO_EDGE);
g.drawTexture(texture,
0, 0, width, height,
0, 0, width/scale, height/scale
);
texture.unlock();
} else {
final double w = size.isWidthAsPercentage() ? size.getWidth() * width : size.getWidth();
final double h = size.isHeightAsPercentage() ? size.getHeight() * height : size.getHeight();
final double tileWidth, tileHeight;
if (size.isContain()) {
final float scaleX = width / imgUnscaledWidth;
final float scaleY = height / imgUnscaledHeight;
final float scale = Math.min(scaleX, scaleY);
tileWidth = Math.ceil(scale * imgUnscaledWidth);
tileHeight = Math.ceil(scale * imgUnscaledHeight);
} else if (size.getWidth() >= 0 && size.getHeight() >= 0) {
tileWidth = w;
tileHeight = h;
} else if (w >= 0) {
tileWidth = w;
final double scale = tileWidth / imgUnscaledWidth;
tileHeight = imgUnscaledHeight * scale;
} else if (h >= 0) {
tileHeight = h;
final double scale = tileHeight / imgUnscaledHeight;
tileWidth = imgUnscaledWidth * scale;
} else {
tileWidth = imgUnscaledWidth;
tileHeight = imgUnscaledHeight;
}
final BackgroundPosition pos = image.getPosition();
final double tileX, tileY;
if (pos.getHorizontalSide() == Side.LEFT) {
final double position = pos.getHorizontalPosition();
if (pos.isHorizontalAsPercentage()) {
tileX = (position * width) - (position * tileWidth);
} else {
tileX = position;
}
} else {
if (pos.isHorizontalAsPercentage()) {
final double position = 1 - pos.getHorizontalPosition();
tileX = (position * width) - (position * tileWidth);
} else {
tileX = width - tileWidth- pos.getHorizontalPosition();
}
}
if (pos.getVerticalSide() == Side.TOP) {
final double position = pos.getVerticalPosition();
if (pos.isVerticalAsPercentage()) {
tileY = (position * height) - (position * tileHeight);
} else {
tileY = position;
}
} else {
if (pos.isVerticalAsPercentage()) {
final double position = 1 - pos.getVerticalPosition();
tileY = (position * height) - (position * tileHeight);
} else {
tileY = height - tileHeight - pos.getVerticalPosition();
}
}
paintTiles(g, prismImage, image.getRepeatX(), image.getRepeatY(),
pos.getHorizontalSide(), pos.getVerticalSide(),
0, 0, width, height,
0, 0, imgWidth, imgHeight,
(float) tileX, (float) tileY, (float) tileWidth, (float) tileHeight);
}
}
}
}
private void renderBackgroundRectangleFromCache(
Graphics g, RTTexture cached, Rectangle rect, int textureWidth, int textureHeight,
double topInset, double rightInset, double bottomInset, double leftInset,
int outsetsTop, int outsetsRight, int outsetsBottom, int outsetsLeft) {
final float pad = 0.5f - 1f/256f;
final float dstWidth = outsetsLeft + width + outsetsRight;
final float dstHeight = outsetsTop + height + outsetsBottom;
final boolean sameWidth = textureWidth == dstWidth;
final boolean sameHeight = textureHeight == dstHeight;
final float dstX1 = -outsetsLeft - pad;
final float dstY1 = -outsetsTop - pad;
final float dstX2 = width + outsetsRight + pad;
final float dstY2 = height + outsetsBottom + pad;
final float srcX1 = rect.x - pad;
final float srcY1 = rect.y - pad;
final float srcX2 = rect.x + textureWidth + pad;
final float srcY2 = rect.y + textureHeight + pad;
double adjustedLeftInset = leftInset;
double adjustedRightInset = rightInset;
double adjustedTopInset = topInset;
double adjustedBottomInset = bottomInset;
if (leftInset + rightInset > width) {
double fraction = width / (leftInset + rightInset);
adjustedLeftInset *= fraction;
adjustedRightInset *= fraction;
}
if (topInset + bottomInset > height) {
double fraction = height / (topInset + bottomInset);
adjustedTopInset *= fraction;
adjustedBottomInset *= fraction;
}
if (sameWidth && sameHeight) {
g.drawTexture(cached, dstX1, dstY1, dstX2, dstY2, srcX1, srcY1, srcX2, srcY2);
} else if (sameHeight) {
final float left = pad + (float) (adjustedLeftInset + outsetsLeft);
final float right = pad + (float) (adjustedRightInset + outsetsRight);
final float dstLeftX = dstX1 + left;
final float dstRightX = dstX2 - right;
final float srcLeftX = srcX1 + left;
final float srcRightX = srcX2 - right;
g.drawTexture3SliceH(cached,
dstX1, dstY1, dstX2, dstY2,
srcX1, srcY1, srcX2, srcY2,
dstLeftX, dstRightX, srcLeftX, srcRightX);
} else if (sameWidth) {
final float top = pad + (float) (adjustedTopInset + outsetsTop);
final float bottom = pad + (float) (adjustedBottomInset + outsetsBottom);
final float dstTopY = dstY1 + top;
final float dstBottomY = dstY2 - bottom;
final float srcTopY = srcY1 + top;
final float srcBottomY = srcY2 - bottom;
g.drawTexture3SliceV(cached,
dstX1, dstY1, dstX2, dstY2,
srcX1, srcY1, srcX2, srcY2,
dstTopY, dstBottomY, srcTopY, srcBottomY);
} else {
final float left = pad + (float) (adjustedLeftInset + outsetsLeft);
final float top = pad + (float) (adjustedTopInset + outsetsTop);
final float right = pad + (float) (adjustedRightInset + outsetsRight);
final float bottom = pad + (float) (adjustedBottomInset + outsetsBottom);
final float dstLeftX = dstX1 + left;
final float dstRightX = dstX2 - right;
final float srcLeftX = srcX1 + left;
final float srcRightX = srcX2 - right;
final float dstTopY = dstY1 + top;
final float dstBottomY = dstY2 - bottom;
final float srcTopY = srcY1 + top;
final float srcBottomY = srcY2 - bottom;
g.drawTexture9Slice(cached,
dstX1, dstY1, dstX2, dstY2,
srcX1, srcY1, srcX2, srcY2,
dstLeftX, dstTopY, dstRightX, dstBottomY,
srcLeftX, srcTopY, srcRightX, srcBottomY);
}
if (PULSE_LOGGING_ENABLED) {
PulseLogger.incrementCounter("Cached region background image used");
}
}
private void renderBackgroundRectanglesDirectly(Graphics g, float width, float height) {
final List<BackgroundFill> fills = background.getFills();
for (int i = 0, max = fills.size(); i < max; i++) {
final BackgroundFill fill = fills.get(i);
final Insets insets = fill.getInsets();
final float t = (float) insets.getTop(),
l = (float) insets.getLeft(),
b = (float) insets.getBottom(),
r = (float) insets.getRight();
float w = width - l - r;
float h = height - t - b;
if (w > 0 && h > 0) {
final Paint paint = getPlatformPaint(fill.getFill());
g.setPaint(paint);
final CornerRadii radii = getNormalizedFillRadii(i);
if (radii.isUniform() &&
!(!PlatformImpl.isCaspian() && !(PlatformUtil.isEmbedded() || PlatformUtil.isIOS()) && radii.getTopLeftHorizontalRadius() > 0 && radii.getTopLeftHorizontalRadius() <= 4)) {
float tlhr = (float) radii.getTopLeftHorizontalRadius();
float tlvr = (float) radii.getTopLeftVerticalRadius();
if (tlhr == 0 && tlvr == 0) {
g.fillRect(l, t, w, h);
} else {
float arcWidth = tlhr + tlhr;
float arcHeight = tlvr + tlvr;
if (arcWidth > w) arcWidth = w;
if (arcHeight > h) arcHeight = h;
g.fillRoundRect(l, t, w, h, arcWidth, arcHeight);
}
} else {
if (PULSE_LOGGING_ENABLED) {
PulseLogger.incrementCounter("NGRegion renderBackgrounds slow path");
PulseLogger.addMessage("Slow background path for " + getName());
}
g.fill(createPath(width, height, t, l, b, r, radii));
}
}
}
}
private void renderBorderRectangle(Graphics g) {
final List<BorderImage> images = border.getImages();
final List<BorderStroke> strokes = images.isEmpty() ? border.getStrokes() : Collections.emptyList();
for (int i = 0, max = strokes.size(); i < max; i++) {
final BorderStroke stroke = strokes.get(i);
final BorderWidths widths = stroke.getWidths();
final CornerRadii radii = getNormalizedStrokeRadii(i);
final Insets insets = stroke.getInsets();
final javafx.scene.paint.Paint topStroke = stroke.getTopStroke();
final javafx.scene.paint.Paint rightStroke = stroke.getRightStroke();
final javafx.scene.paint.Paint bottomStroke = stroke.getBottomStroke();
final javafx.scene.paint.Paint leftStroke = stroke.getLeftStroke();
final float topInset = (float) insets.getTop();
final float rightInset = (float) insets.getRight();
final float bottomInset = (float) insets.getBottom();
final float leftInset = (float) insets.getLeft();
final float topWidth = (float) (widths.isTopAsPercentage() ? height * widths.getTop() : widths.getTop());
final float rightWidth = (float) (widths.isRightAsPercentage() ? width * widths.getRight() : widths.getRight());
final float bottomWidth = (float) (widths.isBottomAsPercentage() ? height * widths.getBottom() : widths.getBottom());
final float leftWidth = (float) (widths.isLeftAsPercentage() ? width * widths.getLeft() : widths.getLeft());
final BorderStrokeStyle topStyle = stroke.getTopStyle();
final BorderStrokeStyle rightStyle = stroke.getRightStyle();
final BorderStrokeStyle bottomStyle = stroke.getBottomStyle();
final BorderStrokeStyle leftStyle = stroke.getLeftStyle();
final StrokeType topType = topStyle.getType();
final StrokeType rightType = rightStyle.getType();
final StrokeType bottomType = bottomStyle.getType();
final StrokeType leftType = leftStyle.getType();
final float t = topInset +
(topType == StrokeType.OUTSIDE ? -topWidth / 2 :
topType == StrokeType.INSIDE ? topWidth / 2 : 0);
final float l = leftInset +
(leftType == StrokeType.OUTSIDE ? -leftWidth / 2 :
leftType == StrokeType.INSIDE ? leftWidth / 2 : 0);
final float b = bottomInset +
(bottomType == StrokeType.OUTSIDE ? -bottomWidth / 2 :
bottomType == StrokeType.INSIDE ? bottomWidth / 2 : 0);
final float r = rightInset +
(rightType == StrokeType.OUTSIDE ? -rightWidth / 2 :
rightType == StrokeType.INSIDE ? rightWidth / 2 : 0);
final float radius = (float) radii.getTopLeftHorizontalRadius();
if (stroke.isStrokeUniform()) {
if (!(topStroke instanceof Color && ((Color)topStroke).getOpacity() == 0f) && topStyle != BorderStrokeStyle.NONE) {
float w = width - l - r;
float h = height - t - b;
final double di = 2 * radii.getTopLeftHorizontalRadius();
final double circle = di*Math.PI;
final double totalLineLength =
circle +
2 * (w - di) +
2 * (h - di);
if (w >= 0 && h >= 0) {
setBorderStyle(g, stroke, totalLineLength, true);
if (radii.isUniform() && radius == 0) {
g.drawRect(l, t, w, h);
} else if (radii.isUniform()) {
float ar = radius + radius;
if (ar > w) ar = w;
if (ar > h) ar = h;
g.drawRoundRect(l, t, w, h, ar, ar);
} else {
g.draw(createPath(width, height, t, l, b, r, radii));
}
}
}
} else if (radii.isUniform() && radius == 0) {
if (!(topStroke instanceof Color && ((Color)topStroke).getOpacity() == 0f) && topStyle != BorderStrokeStyle.NONE) {
g.setPaint(getPlatformPaint(topStroke));
if (BorderStrokeStyle.SOLID == topStyle) {
g.fillRect(leftInset, topInset, width - leftInset - rightInset, topWidth);
} else {
g.setStroke(createStroke(topStyle, topWidth, width, true));
g.drawLine(l, t, width - r, t);
}
}
if (!(rightStroke instanceof Color && ((Color)rightStroke).getOpacity() == 0f) && rightStyle != BorderStrokeStyle.NONE) {
g.setPaint(getPlatformPaint(rightStroke));
if (BorderStrokeStyle.SOLID == rightStyle) {
g.fillRect(width - rightInset - rightWidth, topInset,
rightWidth, height - topInset - bottomInset);
} else {
g.setStroke(createStroke(rightStyle, rightWidth, height, true));
g.drawLine(width - r, t, width - r, height - b);
}
}
if (!(bottomStroke instanceof Color && ((Color)bottomStroke).getOpacity() == 0f) && bottomStyle != BorderStrokeStyle.NONE) {
g.setPaint(getPlatformPaint(bottomStroke));
if (BorderStrokeStyle.SOLID == bottomStyle) {
g.fillRect(leftInset, height - bottomInset - bottomWidth,
width - leftInset - rightInset, bottomWidth);
} else {
g.setStroke(createStroke(bottomStyle, bottomWidth, width, true));
g.drawLine(l, height - b, width - r, height - b);
}
}
if (!(leftStroke instanceof Color && ((Color)leftStroke).getOpacity() == 0f) && leftStyle != BorderStrokeStyle.NONE) {
g.setPaint(getPlatformPaint(leftStroke));
if (BorderStrokeStyle.SOLID == leftStyle) {
g.fillRect(leftInset, topInset, leftWidth, height - topInset - bottomInset);
} else {
g.setStroke(createStroke(leftStyle, leftWidth, height, true));
g.drawLine(l, t, l, height - b);
}
}
} else {
Shape[] paths = createPaths(t, l, b, r, radii);
if (topStyle != BorderStrokeStyle.NONE) {
double rsum = radii.getTopLeftHorizontalRadius() + radii.getTopRightHorizontalRadius();
double topLineLength = width + rsum * (Math.PI / 4 - 1);
g.setStroke(createStroke(topStyle, topWidth, topLineLength, true));
g.setPaint(getPlatformPaint(topStroke));
g.draw(paths[0]);
}
if (rightStyle != BorderStrokeStyle.NONE) {
double rsum = radii.getTopRightVerticalRadius() + radii.getBottomRightVerticalRadius();
double rightLineLength = height + rsum * (Math.PI / 4 - 1);
g.setStroke(createStroke(rightStyle, rightWidth, rightLineLength, true));
g.setPaint(getPlatformPaint(rightStroke));
g.draw(paths[1]);
}
if (bottomStyle != BorderStrokeStyle.NONE) {
double rsum = radii.getBottomLeftHorizontalRadius() + radii.getBottomRightHorizontalRadius();
double bottomLineLength = width + rsum * (Math.PI / 4 - 1);
g.setStroke(createStroke(bottomStyle, bottomWidth, bottomLineLength, true));
g.setPaint(getPlatformPaint(bottomStroke));
g.draw(paths[2]);
}
if (leftStyle != BorderStrokeStyle.NONE) {
double rsum = radii.getTopLeftVerticalRadius() + radii.getBottomLeftVerticalRadius();
double leftLineLength = height + rsum * (Math.PI / 4 - 1);
g.setStroke(createStroke(leftStyle, leftWidth, leftLineLength, true));
g.setPaint(getPlatformPaint(leftStroke));
g.draw(paths[3]);
}
}
}
for (int i = 0, max = images.size(); i < max; i++) {
final BorderImage ib = images.get(i);
final Image prismImage = (Image) Toolkit.getImageAccessor().getPlatformImage(ib.getImage());
if (prismImage == null) {
continue;
}
final int imgWidth = prismImage.getWidth();
final int imgHeight = prismImage.getHeight();
final float imgScale = prismImage.getPixelScale();
final BorderWidths widths = ib.getWidths();
final Insets insets = ib.getInsets();
final BorderWidths slices = ib.getSlices();
final int topInset = (int) Math.round(insets.getTop());
final int rightInset = (int) Math.round(insets.getRight());
final int bottomInset = (int) Math.round(insets.getBottom());
final int leftInset = (int) Math.round(insets.getLeft());
final int topWidth = widthSize(widths.isTopAsPercentage(), widths.getTop(), height);
final int rightWidth = widthSize(widths.isRightAsPercentage(), widths.getRight(), width);
final int bottomWidth = widthSize(widths.isBottomAsPercentage(), widths.getBottom(), height);
final int leftWidth = widthSize(widths.isLeftAsPercentage(), widths.getLeft(), width);
final int topSlice = sliceSize(slices.isTopAsPercentage(), slices.getTop(), imgHeight, imgScale);
final int rightSlice = sliceSize(slices.isRightAsPercentage(), slices.getRight(), imgWidth, imgScale);
final int bottomSlice = sliceSize(slices.isBottomAsPercentage(), slices.getBottom(), imgHeight, imgScale);
final int leftSlice = sliceSize(slices.isLeftAsPercentage(), slices.getLeft(), imgWidth, imgScale);
if ((leftInset + leftWidth + rightInset + rightWidth) > width
|| (topInset + topWidth + bottomInset + bottomWidth) > height) {
continue;
}
final int centerMinX = leftInset + leftWidth;
final int centerMinY = topInset + topWidth;
final int centerW = Math.round(width) - rightInset - rightWidth - centerMinX;
final int centerH = Math.round(height) - bottomInset - bottomWidth - centerMinY;
final int centerMaxX = centerW + centerMinX;
final int centerMaxY = centerH + centerMinY;
final int centerSliceWidth = imgWidth - leftSlice - rightSlice;
final int centerSliceHeight = imgHeight - topSlice - bottomSlice;
paintTiles(g, prismImage, BorderRepeat.STRETCH, BorderRepeat.STRETCH, Side.LEFT, Side.TOP,
leftInset, topInset, leftWidth, topWidth,
0, 0, leftSlice, topSlice,
0, 0, leftWidth, topWidth);
float tileWidth = (ib.getRepeatX() == BorderRepeat.STRETCH) ?
centerW : (topSlice > 0 ? (centerSliceWidth * topWidth) / topSlice : 0);
float tileHeight = topWidth;
paintTiles(
g, prismImage, ib.getRepeatX(), BorderRepeat.STRETCH, Side.LEFT, Side.TOP,
centerMinX, topInset, centerW, topWidth,
leftSlice, 0, centerSliceWidth, topSlice,
(centerW - tileWidth) / 2, 0, tileWidth, tileHeight);
paintTiles(g, prismImage, BorderRepeat.STRETCH, BorderRepeat.STRETCH, Side.LEFT, Side.TOP,
centerMaxX, topInset, rightWidth, topWidth,
(imgWidth - rightSlice), 0, rightSlice, topSlice,
0, 0, rightWidth, topWidth);
tileWidth = leftWidth;
tileHeight = (ib.getRepeatY() == BorderRepeat.STRETCH) ?
centerH : (leftSlice > 0 ? (leftWidth * centerSliceHeight) / leftSlice : 0);
paintTiles(g, prismImage, BorderRepeat.STRETCH, ib.getRepeatY(), Side.LEFT, Side.TOP,
leftInset, centerMinY, leftWidth, centerH,
0, topSlice, leftSlice, centerSliceHeight,
0, (centerH - tileHeight) / 2, tileWidth, tileHeight);
tileWidth = rightWidth;
tileHeight = (ib.getRepeatY() == BorderRepeat.STRETCH) ?
centerH : (rightSlice > 0 ? (rightWidth * centerSliceHeight) / rightSlice : 0);
paintTiles(g, prismImage, BorderRepeat.STRETCH, ib.getRepeatY(), Side.LEFT, Side.TOP,
centerMaxX, centerMinY, rightWidth, centerH,
imgWidth - rightSlice, topSlice, rightSlice, centerSliceHeight,
0, (centerH - tileHeight) / 2, tileWidth, tileHeight);
paintTiles(g, prismImage, BorderRepeat.STRETCH, BorderRepeat.STRETCH, Side.LEFT, Side.TOP,
leftInset, centerMaxY, leftWidth, bottomWidth,
0, imgHeight - bottomSlice, leftSlice, bottomSlice,
0, 0, leftWidth, bottomWidth);
tileWidth = (ib.getRepeatX() == BorderRepeat.STRETCH) ?
centerW : (bottomSlice > 0 ? (centerSliceWidth * bottomWidth) / bottomSlice : 0);
tileHeight = bottomWidth;
paintTiles(g, prismImage, ib.getRepeatX(), BorderRepeat.STRETCH, Side.LEFT, Side.TOP,
centerMinX, centerMaxY, centerW, bottomWidth,
leftSlice, imgHeight - bottomSlice, centerSliceWidth, bottomSlice,
(centerW - tileWidth) / 2, 0, tileWidth, tileHeight);
paintTiles(g, prismImage, BorderRepeat.STRETCH, BorderRepeat.STRETCH, Side.LEFT, Side.TOP,
centerMaxX, centerMaxY, rightWidth, bottomWidth,
imgWidth - rightSlice, imgHeight - bottomSlice, rightSlice, bottomSlice,
0, 0, rightWidth, bottomWidth);
if (ib.isFilled()) {
final float imgW = (ib.getRepeatX() == BorderRepeat.STRETCH) ? centerW : centerSliceWidth;
final float imgH = (ib.getRepeatY() == BorderRepeat.STRETCH) ? centerH : centerSliceHeight;
paintTiles(g, prismImage, ib.getRepeatX(), ib.getRepeatY(), Side.LEFT, Side.TOP,
centerMinX, centerMinY, centerW, centerH,
leftSlice, topSlice, centerSliceWidth, centerSliceHeight,
0, 0, imgW, imgH);
}
}
}
private void updateBackgroundInsets() {
float top=0, right=0, bottom=0, left=0;
final List<BackgroundFill> fills = background.getFills();
for (int i=0, max=fills.size(); i<max; i++) {
final BackgroundFill fill = fills.get(i);
final Insets insets = fill.getInsets();
final CornerRadii radii = getNormalizedFillRadii(i);
top = (float) Math.max(top, insets.getTop() + Math.max(radii.getTopLeftVerticalRadius(), radii.getTopRightVerticalRadius()));
right = (float) Math.max(right, insets.getRight() + Math.max(radii.getTopRightHorizontalRadius(), radii.getBottomRightHorizontalRadius()));
bottom = (float) Math.max(bottom, insets.getBottom() + Math.max(radii.getBottomRightVerticalRadius(), radii.getBottomLeftVerticalRadius()));
left = (float) Math.max(left, insets.getLeft() + Math.max(radii.getTopLeftHorizontalRadius(), radii.getBottomLeftHorizontalRadius()));
}
backgroundInsets = new Insets(roundUp(top), roundUp(right), roundUp(bottom), roundUp(left));
}
private int widthSize(boolean isPercent, double sliceSize, float objSize) {
return (int) Math.round(isPercent ? sliceSize * objSize : sliceSize);
}
private int sliceSize(boolean isPercent, double sliceSize, float objSize, float scale) {
if (isPercent) sliceSize *= objSize;
if (sliceSize > objSize) sliceSize = objSize;
return (int) Math.round(sliceSize * scale);
}
private int roundUp(double d) {
return (d - (int)d) == 0 ? (int) d : (int) (d + 1);
}
private BasicStroke createStroke(BorderStrokeStyle sb,
double strokeWidth,
double lineLength,
boolean forceCentered) {
int cap;
if (sb.getLineCap() == StrokeLineCap.BUTT) {
cap = BasicStroke.CAP_BUTT;
} else if (sb.getLineCap() == StrokeLineCap.SQUARE) {
cap = BasicStroke.CAP_SQUARE;
} else {
cap = BasicStroke.CAP_ROUND;
}
int join;
if (sb.getLineJoin() == StrokeLineJoin.BEVEL) {
join = BasicStroke.JOIN_BEVEL;
} else if (sb.getLineJoin() == StrokeLineJoin.MITER) {
join = BasicStroke.JOIN_MITER;
} else {
join = BasicStroke.JOIN_ROUND;
}
int type;
if (forceCentered) {
type = BasicStroke.TYPE_CENTERED;
} else if (scaleShape) {
type = BasicStroke.TYPE_INNER;
} else {
switch (sb.getType()) {
case INSIDE:
type = BasicStroke.TYPE_INNER;
break;
case OUTSIDE:
type = BasicStroke.TYPE_OUTER;
break;
case CENTERED:
default:
type = BasicStroke.TYPE_CENTERED;
break;
}
}
BasicStroke bs;
if (sb == BorderStrokeStyle.NONE) {
throw new AssertionError("Should never have been asked to draw a border with NONE");
} else if (strokeWidth <= 0) {
bs = new BasicStroke((float) strokeWidth, cap, join,
(float) sb.getMiterLimit());
} else if (sb.getDashArray().size() > 0) {
List<Double> dashArray = sb.getDashArray();
double[] array;
float dashOffset;
if (dashArray == BorderStrokeStyle.DOTTED.getDashArray()) {
if (lineLength > 0) {
double remainder = lineLength % (strokeWidth * 2);
double numSpaces = lineLength / (strokeWidth * 2);
double spaceWidth = (strokeWidth * 2) + (remainder / numSpaces);
array = new double[] {0, spaceWidth};
dashOffset = 0;
} else {
array = new double[] {0, strokeWidth * 2};
dashOffset = 0;
}
} else if (dashArray == BorderStrokeStyle.DASHED.getDashArray()) {
if (lineLength > 0) {
final double dashLength = strokeWidth * 2;
double gapLength = strokeWidth * 1.4;
final double segmentLength = dashLength + gapLength;
final double divided = lineLength / segmentLength;
final double numSegments = (int) divided;
if (numSegments > 0) {
final double dashCumulative = numSegments * dashLength;
gapLength = (lineLength - dashCumulative) / numSegments;
}
array = new double[] {dashLength, gapLength};
dashOffset = (float) (dashLength*.6);
} else {
array = new double[] {2 * strokeWidth, 1.4 * strokeWidth};
dashOffset = 0;
}
} else {
array = new double[dashArray.size()];
for (int i=0; i<array.length; i++) {
array[i] = dashArray.get(i);
}
dashOffset = (float) sb.getDashOffset();
}
bs = new BasicStroke(type, (float) strokeWidth, cap, join,
(float) sb.getMiterLimit(),
array, dashOffset);
} else {
bs = new BasicStroke(type, (float) strokeWidth, cap, join,
(float) sb.getMiterLimit());
}
return bs;
}
private void setBorderStyle(Graphics g, BorderStroke sb, double length, boolean forceCentered) {
final BorderWidths widths = sb.getWidths();
BorderStrokeStyle bs = sb.getTopStyle();
double sbWidth = widths.isTopAsPercentage() ? height * widths.getTop() : widths.getTop();
Paint sbFill = getPlatformPaint(sb.getTopStroke());
if (bs == null) {
bs = sb.getLeftStyle();
sbWidth = widths.isLeftAsPercentage() ? width * widths.getLeft() : widths.getLeft();
sbFill = getPlatformPaint(sb.getLeftStroke());
if (bs == null) {
bs = sb.getBottomStyle();
sbWidth = widths.isBottomAsPercentage() ? height * widths.getBottom() : widths.getBottom();
sbFill = getPlatformPaint(sb.getBottomStroke());
if (bs == null) {
bs = sb.getRightStyle();
sbWidth = widths.isRightAsPercentage() ? width * widths.getRight() : widths.getRight();
sbFill = getPlatformPaint(sb.getRightStroke());
}
}
}
if (bs == null || bs == BorderStrokeStyle.NONE) {
return;
}
g.setStroke(createStroke(bs, sbWidth, length, forceCentered));
g.setPaint(sbFill);
}
private void doCorner(Path2D path, CornerRadii radii,
float x, float y, int quadrant,
float tstart, float tend, boolean newPath)
{
float dx0, dy0, dx1, dy1;
float hr, vr;
switch (quadrant & 0x3) {
case 0:
hr = (float) radii.getTopLeftHorizontalRadius();
vr = (float) radii.getTopLeftVerticalRadius();
dx0 = 0f; dy0 = vr; dx1 = hr; dy1 = 0f;
break;
case 1:
hr = (float) radii.getTopRightHorizontalRadius();
vr = (float) radii.getTopRightVerticalRadius();
dx0 = -hr; dy0 = 0f; dx1 = 0f; dy1 = vr;
break;
case 2:
hr = (float) radii.getBottomRightHorizontalRadius();
vr = (float) radii.getBottomRightVerticalRadius();
dx0 = 0f; dy0 = -vr; dx1 = -hr; dy1 = 0f;
break;
case 3:
hr = (float) radii.getBottomLeftHorizontalRadius();
vr = (float) radii.getBottomLeftVerticalRadius();
dx0 = hr; dy0 = 0f; dx1 = 0f; dy1 = -vr;
break;
default: return;
}
if (hr > 0 && vr > 0) {
path.appendOvalQuadrant(x + dx0, y + dy0, x, y, x + dx1, y + dy1, tstart, tend,
(newPath)
? Path2D.CornerPrefix.MOVE_THEN_CORNER
: Path2D.CornerPrefix.LINE_THEN_CORNER);
} else if (newPath) {
path.moveTo(x, y);
} else {
path.lineTo(x, y);
}
}
private Path2D createPath(float width, float height, float t, float l, float bo, float ro, CornerRadii radii) {
float r = width - ro;
float b = height - bo;
Path2D path = new Path2D();
doCorner(path, radii, l, t, 0, 0f, 1f, true);
doCorner(path, radii, r, t, 1, 0f, 1f, false);
doCorner(path, radii, r, b, 2, 0f, 1f, false);
doCorner(path, radii, l, b, 3, 0f, 1f, false);
path.closePath();
return path;
}
private Path2D makeRoundedEdge(CornerRadii radii,
float x0, float y0, float x1, float y1,
int quadrant)
{
Path2D path = new Path2D();
doCorner(path, radii, x0, y0, quadrant, 0.5f, 1.0f, true);
doCorner(path, radii, x1, y1, quadrant+1, 0.0f, 0.5f, false);
return path;
}
private Path2D[] createPaths(float t, float l, float bo, float ro, CornerRadii radii)
{
float r = width - ro;
float b = height - bo;
return new Path2D[] {
makeRoundedEdge(radii, l, t, r, t, 0),
makeRoundedEdge(radii, r, t, r, b, 1),
makeRoundedEdge(radii, r, b, l, b, 2),
makeRoundedEdge(radii, l, b, l, t, 3),
};
}
private Shape resizeShape(float topOffset, float rightOffset, float bottomOffset, float leftOffset) {
final RectBounds bounds = shape.getBounds();
if (scaleShape) {
SCRATCH_AFFINE.setToIdentity();
SCRATCH_AFFINE.translate(leftOffset, topOffset);
final float w = width - leftOffset - rightOffset;
final float h = height - topOffset - bottomOffset;
SCRATCH_AFFINE.scale(w / bounds.getWidth(), h / bounds.getHeight());
if (centerShape) {
SCRATCH_AFFINE.translate(-bounds.getMinX(), -bounds.getMinY());
}
return SCRATCH_AFFINE.createTransformedShape(shape);
} else if (centerShape) {
final float boundsWidth = bounds.getWidth();
final float boundsHeight = bounds.getHeight();
float newW = boundsWidth - leftOffset - rightOffset;
float newH = boundsHeight - topOffset - bottomOffset;
SCRATCH_AFFINE.setToIdentity();
SCRATCH_AFFINE.translate(leftOffset + (width - boundsWidth)/2 - bounds.getMinX(),
topOffset + (height - boundsHeight)/2 - bounds.getMinY());
if (newH != boundsHeight || newW != boundsWidth) {
SCRATCH_AFFINE.translate(bounds.getMinX(), bounds.getMinY());
SCRATCH_AFFINE.scale(newW / boundsWidth, newH / boundsHeight);
SCRATCH_AFFINE.translate(-bounds.getMinX(), -bounds.getMinY());
}
return SCRATCH_AFFINE.createTransformedShape(shape);
} else if (topOffset != 0 || rightOffset != 0 || bottomOffset != 0 || leftOffset != 0) {
float newW = bounds.getWidth() - leftOffset - rightOffset;
float newH = bounds.getHeight() - topOffset - bottomOffset;
SCRATCH_AFFINE.setToIdentity();
SCRATCH_AFFINE.translate(leftOffset, topOffset);
SCRATCH_AFFINE.translate(bounds.getMinX(), bounds.getMinY());
SCRATCH_AFFINE.scale(newW / bounds.getWidth(), newH / bounds.getHeight());
SCRATCH_AFFINE.translate(-bounds.getMinX(), -bounds.getMinY());
return SCRATCH_AFFINE.createTransformedShape(shape);
} else {
return shape;
}
}
private void paintTiles(Graphics g, Image img, BorderRepeat repeatX, BorderRepeat repeatY, Side horizontalSide, Side verticalSide,
final float regionX, final float regionY, final float regionWidth, final float regionHeight,
final int srcX, final int srcY, final int srcW, final int srcH,
float tileX, float tileY, float tileWidth, float tileHeight)
{
BackgroundRepeat rx = null;
BackgroundRepeat ry = null;
switch (repeatX) {
case REPEAT: rx = BackgroundRepeat.REPEAT; break;
case STRETCH: rx = BackgroundRepeat.NO_REPEAT; break;
case ROUND: rx = BackgroundRepeat.ROUND; break;
case SPACE: rx = BackgroundRepeat.SPACE; break;
}
switch (repeatY) {
case REPEAT: ry = BackgroundRepeat.REPEAT; break;
case STRETCH: ry = BackgroundRepeat.NO_REPEAT; break;
case ROUND: ry = BackgroundRepeat.ROUND; break;
case SPACE: ry = BackgroundRepeat.SPACE; break;
}
paintTiles(g, img, rx, ry, horizontalSide, verticalSide, regionX, regionY, regionWidth, regionHeight,
srcX, srcY, srcW, srcH, tileX, tileY, tileWidth, tileHeight);
}
private void paintTiles(Graphics g, Image img, BackgroundRepeat repeatX, BackgroundRepeat repeatY, Side horizontalSide, Side verticalSide,
final float regionX, final float regionY, final float regionWidth, final float regionHeight,
final int srcX, final int srcY, final int srcW, final int srcH,
float tileX, float tileY, float tileWidth, float tileHeight)
{
if (regionWidth <= 0 || regionHeight <= 0 || srcW <= 0 || srcH <= 0) return;
assert srcX >= 0 && srcY >= 0 && srcW > 0 && srcH > 0;
if (tileX == 0 && tileY == 0 && repeatX == BackgroundRepeat.REPEAT && repeatY == BackgroundRepeat.REPEAT) {
if (srcX != 0 || srcY != 0 || srcW != img.getWidth() || srcH != img.getHeight()) {
img = img.createSubImage(srcX, srcY, srcW, srcH);
}
g.setPaint(new ImagePattern(img, 0, 0, tileWidth, tileHeight, false, false));
g.fillRect(regionX, regionY, regionWidth, regionHeight);
} else {
if (repeatX == BackgroundRepeat.SPACE && (regionWidth < (tileWidth * 2))) {
repeatX = BackgroundRepeat.NO_REPEAT;
}
if (repeatY == BackgroundRepeat.SPACE && (regionHeight < (tileHeight * 2))) {
repeatY = BackgroundRepeat.NO_REPEAT;
}
final int countX, countY;
final float xIncrement, yIncrement;
if (repeatX == BackgroundRepeat.REPEAT) {
float offsetX = 0;
if (tileX != 0) {
float mod = tileX % tileWidth;
tileX = mod == 0 ? 0 : tileX < 0 ? mod : mod - tileWidth;
offsetX = tileX;
}
countX = (int) Math.max(1, Math.ceil((regionWidth - offsetX) / tileWidth));
xIncrement = horizontalSide == Side.RIGHT ? -tileWidth : tileWidth;
} else if (repeatX == BackgroundRepeat.SPACE) {
tileX = 0;
countX = (int) (regionWidth / tileWidth);
float remainder = (regionWidth % tileWidth);
xIncrement = tileWidth + (remainder / (countX - 1));
} else if (repeatX == BackgroundRepeat.ROUND) {
tileX = 0;
countX = (int) (regionWidth / tileWidth);
tileWidth = regionWidth / (int)(regionWidth / tileWidth);
xIncrement = tileWidth;
} else {
countX = 1;
xIncrement = horizontalSide == Side.RIGHT ? -tileWidth : tileWidth;
}
if (repeatY == BackgroundRepeat.REPEAT) {
float offsetY = 0;
if (tileY != 0) {
float mod = tileY % tileHeight;
tileY = mod == 0 ? 0 : tileY < 0 ? mod : mod - tileHeight;
offsetY = tileY;
}
countY = (int) Math.max(1, Math.ceil((regionHeight - offsetY) / tileHeight));
yIncrement = verticalSide == Side.BOTTOM ? -tileHeight : tileHeight;
} else if (repeatY == BackgroundRepeat.SPACE) {
tileY = 0;
countY = (int) (regionHeight / tileHeight);
float remainder = (regionHeight % tileHeight);
yIncrement = tileHeight + (remainder / (countY - 1));
} else if (repeatY == BackgroundRepeat.ROUND) {
tileY = 0;
countY = (int) (regionHeight / tileHeight);
tileHeight = regionHeight / (int)(regionHeight / tileHeight);
yIncrement = tileHeight;
} else {
countY = 1;
yIncrement = verticalSide == Side.BOTTOM ? -tileHeight : tileHeight;
}
final Texture texture =
g.getResourceFactory().getCachedTexture(img, Texture.WrapMode.CLAMP_TO_EDGE);
final int srcX2 = srcX + srcW;
final int srcY2 = srcY + srcH;
final float regionX2 = regionX + regionWidth;
final float regionY2 = regionY + regionHeight;
float dstY = regionY + tileY;
for (int y = 0; y < countY; y++) {
float dstY2 = dstY + tileHeight;
float dstX = regionX + tileX;
for (int x = 0; x < countX; x++) {
float dstX2 = dstX + tileWidth;
boolean skipRender = false;
float dx1 = dstX < regionX ? regionX : dstX;
float dy1 = dstY < regionY ? regionY : dstY;
if (dx1 > regionX2 || dy1 > regionY2) skipRender = true;
float dx2 = dstX2 > regionX2 ? regionX2 : dstX2;
float dy2 = dstY2 > regionY2 ? regionY2 : dstY2;
if (dx2 < regionX || dy2 < regionY) skipRender = true;
if (!skipRender) {
float sx1 = dstX < regionX ? srcX + srcW * (-tileX / tileWidth) : srcX;
float sy1 = dstY < regionY ? srcY + srcH * (-tileY / tileHeight) : srcY;
float sx2 = dstX2 > regionX2 ? srcX2 - srcW * ((dstX2 - regionX2) / tileWidth) : srcX2;
float sy2 = dstY2 > regionY2 ? srcY2 - srcH * ((dstY2 - regionY2) / tileHeight) : srcY2;
g.drawTexture(texture, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
}
dstX += xIncrement;
}
dstY += yIncrement;
}
texture.unlock();
}
}
final Border getBorder() {
return border;
}
final Background getBackground() {
return background;
}
final float getWidth() {
return width;
}
final float getHeight() {
return height;
}
}
