package com.sun.scenario.effect;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Iterator;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.impl.Renderer;
public class ImageData {
private static HashSet<ImageData> alldatas;
static {
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged((PrivilegedAction) () -> {
if (System.getProperty("decora.showleaks") != null) {
alldatas = new HashSet<ImageData>();
Runtime.getRuntime().addShutdownHook(new Thread() {
@Override
public void run() {
Iterator<ImageData> datas = alldatas.iterator();
while (datas.hasNext()) {
ImageData id = datas.next();
Rectangle r = id.getUntransformedBounds();
System.out.println("id["+r.width+"x"+r.height+", refcount="+id.refcount+"] leaked from:");
id.fromwhere.printStackTrace(System.out);
}
}
});
}
return null;
});
}
private ImageData sharedOwner;
private FilterContext fctx;
private int refcount;
private Filterable image;
private final Rectangle bounds;
private BaseTransform transform;
private Throwable fromwhere;
private boolean reusable;
public ImageData(FilterContext fctx, Filterable image, Rectangle bounds) {
this(fctx, image, bounds, BaseTransform.IDENTITY_TRANSFORM);
}
public ImageData(FilterContext fctx, Filterable image, Rectangle bounds,
BaseTransform transform)
{
this.fctx = fctx;
this.refcount = 1;
this.image = image;
this.bounds = bounds;
this.transform = transform;
if (alldatas != null) {
alldatas.add(this);
this.fromwhere = new Throwable();
}
}
public ImageData transform(BaseTransform concattx) {
if (concattx.isIdentity()) {
return this;
}
BaseTransform newtx;
if (this.transform.isIdentity()) {
newtx = concattx;
} else {
newtx = concattx.copy().deriveWithConcatenation(this.transform);
}
return new ImageData(this, newtx, bounds);
}
private ImageData(ImageData original, BaseTransform transform,
Rectangle bounds)
{
this(original.fctx, original.image, bounds, transform);
this.sharedOwner = original;
}
public void setReusable(boolean reusable) {
if (sharedOwner != null) {
throw new InternalError("cannot make a shared ImageData reusable");
}
this.reusable = reusable;
}
public FilterContext getFilterContext() {
return fctx;
}
public Filterable getUntransformedImage() {
return image;
}
public Rectangle getUntransformedBounds() {
return bounds;
}
public BaseTransform getTransform() {
return transform;
}
public Filterable getTransformedImage(Rectangle clip) {
if (image == null || fctx == null) {
return null;
}
if (transform.isIdentity()) {
return image;
} else {
Rectangle txbounds = getTransformedBounds(clip);
return Renderer.getRenderer(fctx).
transform(fctx, image, transform, bounds, txbounds);
}
}
public void releaseTransformedImage(Filterable tximage) {
if (fctx != null && tximage != null && tximage != image) {
Effect.releaseCompatibleImage(fctx, tximage);
}
}
public Rectangle getTransformedBounds(Rectangle clip) {
if (transform.isIdentity()) {
return bounds;
}
Rectangle txbounds = new Rectangle();
transform.transform(bounds, txbounds);
if (clip != null) {
txbounds.intersectWith(clip);
}
return txbounds;
}
public int getReferenceCount() {
return refcount;
}
public boolean addref() {
if (reusable && refcount == 0) {
if (image != null) {
image.lock();
}
}
++refcount;
return image != null && !image.isLost();
}
public void unref() {
if (--refcount == 0) {
if (sharedOwner != null) {
sharedOwner.unref();
sharedOwner = null;
} else if (fctx != null && image != null) {
if (reusable) {
image.unlock();
return;
}
Effect.releaseCompatibleImage(fctx, image);
}
fctx = null;
image = null;
if (alldatas != null) {
alldatas.remove(this);
}
}
}
public boolean validate(FilterContext fctx) {
return image != null &&
Renderer.getRenderer(fctx).isImageDataCompatible(this);
}
@Override
public String toString() {
return "ImageData{" +
"sharedOwner=" + sharedOwner +
", fctx=" + fctx +
", refcount=" + refcount +
", image=" + image +
", bounds=" + bounds +
", transform=" + transform +
", reusable=" + reusable + '}';
}
}
