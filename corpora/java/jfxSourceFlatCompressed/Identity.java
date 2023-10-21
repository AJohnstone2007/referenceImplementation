package com.sun.scenario.effect;
import java.util.HashMap;
import java.util.Map;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.DirtyRegionPool;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
public class Identity extends Effect {
private Filterable src;
private Point2D loc = new Point2D();
private final Map<FilterContext, ImageData> datacache =
new HashMap<FilterContext, ImageData>();
public Identity(Filterable src) {
this.src = src;
}
public final Filterable getSource() {
return src;
}
public void setSource(Filterable src) {
Filterable old = this.src;
this.src = src;
clearCache();
}
public final Point2D getLocation() {
return loc;
}
public void setLocation(Point2D pt) {
if (pt == null) {
throw new IllegalArgumentException("Location must be non-null");
}
Point2D old = this.loc;
this.loc.setLocation(pt);
}
@Override
public BaseBounds getBounds(BaseTransform transform,
Effect defaultInput)
{
if (src == null) {
return new RectBounds();
}
float srcw = src.getPhysicalWidth() / src.getPixelScale();
float srch = src.getPhysicalHeight() / src.getPixelScale();
BaseBounds r = new RectBounds(loc.x, loc.y, loc.x + srcw, loc.y + srch);
if (transform != null && !transform.isIdentity()) {
r = transformBounds(transform, r);
}
return r;
}
@Override
public ImageData filter(FilterContext fctx,
BaseTransform transform,
Rectangle outputClip,
Object renderHelper,
Effect defaultInput)
{
ImageData id = datacache.get(fctx);
if (id != null && !id.addref()) {
id.setReusable(false);
datacache.remove(fctx);
id.unref();
id = null;
}
if (id == null) {
Renderer r = Renderer.getRenderer(fctx);
Filterable f = src;
if (f == null) {
f = getCompatibleImage(fctx, 1, 1);
id = new ImageData(fctx, f, new Rectangle(1, 1));
} else {
id = r.createImageData(fctx, f);
}
if (id == null) {
return new ImageData(fctx, null, null);
}
id.setReusable(true);
datacache.put(fctx, id);
}
transform = Offset.getOffsetTransform(transform, loc.x, loc.y);
id = id.transform(transform);
return id;
}
@Override
public AccelType getAccelType(FilterContext fctx) {
return AccelType.INTRINSIC;
}
private void clearCache() {
datacache.clear();
}
@Override
public boolean reducesOpaquePixels() {
return true;
}
@Override
public DirtyRegionContainer getDirtyRegions(Effect defaultInput, DirtyRegionPool regionPool) {
DirtyRegionContainer drc = regionPool.checkOut();
drc.reset();
return drc;
}
}
