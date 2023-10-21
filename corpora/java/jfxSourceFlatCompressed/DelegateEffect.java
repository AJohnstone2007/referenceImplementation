package com.sun.scenario.effect;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.DirtyRegionPool;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
public abstract class DelegateEffect extends Effect {
protected DelegateEffect(Effect input) {
super(input);
}
protected DelegateEffect(Effect input1, Effect input2) {
super(input1, input2);
}
protected abstract Effect getDelegate();
@Override
public BaseBounds getBounds(BaseTransform transform,
Effect defaultInput)
{
return getDelegate().getBounds(transform, defaultInput);
}
@Override
public ImageData filter(FilterContext fctx,
BaseTransform transform,
Rectangle outputClip,
Object renderHelper,
Effect defaultInput)
{
return getDelegate().filter(fctx, transform, outputClip,
renderHelper, defaultInput);
}
@Override
public Point2D untransform(Point2D p, Effect defaultInput) {
return getDelegate().untransform(p, defaultInput);
}
@Override
public Point2D transform(Point2D p, Effect defaultInput) {
return getDelegate().transform(p, defaultInput);
}
@Override
public AccelType getAccelType(FilterContext fctx) {
return getDelegate().getAccelType(fctx);
}
@Override
public boolean reducesOpaquePixels() {
return getDelegate().reducesOpaquePixels();
}
@Override
public DirtyRegionContainer getDirtyRegions(Effect defaultInput, DirtyRegionPool regionPool) {
return getDelegate().getDirtyRegions(defaultInput, regionPool);
}
}
