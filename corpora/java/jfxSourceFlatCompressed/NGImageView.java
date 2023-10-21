package com.sun.javafx.sg.prism;
import com.sun.javafx.geom.RectBounds;
import com.sun.prism.Graphics;
import com.sun.prism.Image;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture;
import com.sun.prism.image.CachingCompoundImage;
import com.sun.prism.image.CompoundCoords;
import com.sun.prism.image.Coords;
import com.sun.prism.image.ViewPort;
public class NGImageView extends NGNode {
private Image image;
private CachingCompoundImage compoundImage;
private CompoundCoords compoundCoords;
private float x, y, w, h;
private Coords coords;
private ViewPort reqviewport;
private ViewPort imgviewport;
private boolean renderable = false;
private boolean coordsOK = false;
private void invalidate() {
coordsOK = false;
coords = null;
compoundCoords = null;
imgviewport = null;
geometryChanged();
}
public void setViewport(float vx, float vy, float vw, float vh, float cw, float ch)
{
if (vw > 0 && vh > 0) {
reqviewport = new ViewPort(vx, vy, vw, vh);
} else {
reqviewport = null;
}
this.w = cw;
this.h = ch;
invalidate();
}
private void calculatePositionAndClipping() {
renderable = false;
coordsOK = true;
if (reqviewport == null || image == null) {
renderable = image != null;
return;
}
float iw = image.getWidth();
float ih = image.getHeight();
if (iw == 0 || ih == 0) return;
imgviewport = reqviewport.getScaledVersion(image.getPixelScale());
coords = imgviewport.getClippedCoords(iw, ih, w, h);
renderable = coords != null;
}
@Override
protected void doRender(Graphics g) {
if (!coordsOK) {
calculatePositionAndClipping();
}
if (renderable) {
super.doRender(g);
}
}
final static int MAX_SIZE_OVERRIDE = 0;
private int maxSizeWrapper(ResourceFactory factory) {
return MAX_SIZE_OVERRIDE > 0 ? MAX_SIZE_OVERRIDE : factory.getMaximumTextureSize();
}
@Override
protected void renderContent(Graphics g) {
int imgW = image.getWidth();
int imgH = image.getHeight();
ResourceFactory factory = g.getResourceFactory();
int maxSize = maxSizeWrapper(factory);
if (imgW <= maxSize && imgH <= maxSize) {
Texture texture = factory.getCachedTexture(image, Texture.WrapMode.CLAMP_TO_EDGE);
if (coords == null) {
g.drawTexture(texture, x, y, x + w, y + h, 0, 0, imgW, imgH);
} else {
coords.draw(texture, g, x, y);
}
texture.unlock();
} else {
if (compoundImage == null) compoundImage = new CachingCompoundImage(image, maxSize);
if (coords == null) coords = new Coords(w, h, new ViewPort(0, 0, imgW, imgH));
if (compoundCoords == null) compoundCoords = new CompoundCoords(compoundImage, coords);
compoundCoords.draw(g, compoundImage, x, y);
}
}
@Override
protected boolean hasOverlappingContents() {
return false;
}
public void setImage(Object img) {
Image newImage = (Image)img;
if (image == newImage) return;
boolean needsInvalidate = newImage == null || image == null
|| image.getPixelScale() != newImage.getPixelScale()
|| image.getHeight() != newImage.getHeight()
|| image.getWidth() != newImage.getWidth();
image = newImage;
compoundImage = null;
if (needsInvalidate) invalidate();
}
public void setX(float x) {
if (this.x != x) {
this.x = x;
geometryChanged();
}
}
public void setY(float y) {
if (this.y != y) {
this.y = y;
geometryChanged();
}
}
public void setSmooth(boolean s) {}
@Override
protected boolean supportsOpaqueRegions() { return true; }
@Override
protected boolean hasOpaqueRegion() {
assert image == null || (image.getWidth() >= 1 && image.getHeight() >= 1);
return super.hasOpaqueRegion() && w >= 1 && h >= 1 && image != null && image.isOpaque();
}
@Override
protected RectBounds computeOpaqueRegion(RectBounds opaqueRegion) {
return (RectBounds) opaqueRegion.deriveWithNewBounds(x, y, 0, x+w, y+h, 0);
}
}
