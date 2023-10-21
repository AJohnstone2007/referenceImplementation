package com.sun.scenario.effect;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.DirtyRegionPool;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.impl.state.RenderState;
public class Flood extends CoreEffect<RenderState> {
private Object paint;
private RectBounds bounds = new RectBounds();
public Flood(Object paint) {
if (paint == null) {
throw new IllegalArgumentException("Paint must be non-null");
}
this.paint = paint;
updatePeerKey("Flood");
}
public Flood(Object paint, RectBounds bounds) {
this(paint);
if (bounds == null) {
throw new IllegalArgumentException("Bounds must be non-null");
}
this.bounds.setBounds(bounds);
}
public Object getPaint() {
return paint;
}
public void setPaint(Object paint) {
if (paint == null) {
throw new IllegalArgumentException("Paint must be non-null");
}
Object old = this.paint;
this.paint = paint;
}
public RectBounds getFloodBounds() {
return new RectBounds(bounds);
}
public void setFloodBounds(RectBounds bounds) {
if (bounds == null) {
throw new IllegalArgumentException("Bounds must be non-null");
}
RectBounds old = new RectBounds(this.bounds);
this.bounds.setBounds(bounds);
}
@Override
public BaseBounds getBounds(BaseTransform transform,
Effect defaultInput)
{
return transformBounds(transform, bounds);
}
@Override
public Point2D transform(Point2D p, Effect defaultInput) {
return new Point2D(Float.NaN, Float.NaN);
}
@Override
public Point2D untransform(Point2D p, Effect defaultInput) {
return new Point2D(Float.NaN, Float.NaN);
}
@Override
public RenderState getRenderState(FilterContext fctx,
BaseTransform transform,
Rectangle outputClip,
Object renderHelper,
Effect defaultInput)
{
return RenderState.RenderSpaceRenderState;
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
