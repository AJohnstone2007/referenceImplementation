package com.sun.scenario.effect;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.DirtyRegionPool;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.Translate2D;
public class Offset extends Effect {
private int xoff;
private int yoff;
public Offset(int xoff, int yoff, Effect input) {
super(input);
this.xoff = xoff;
this.yoff = yoff;
}
public final Effect getInput() {
return getInputs().get(0);
}
public void setInput(Effect input) {
setInput(0, input);
}
public int getX() {
return xoff;
}
public void setX(int xoff) {
int old = this.xoff;
this.xoff = xoff;
}
public int getY() {
return yoff;
}
public void setY(int yoff) {
float old = this.yoff;
this.yoff = yoff;
}
static BaseTransform getOffsetTransform(BaseTransform transform,
double xoff, double yoff)
{
if (transform == null || transform.isIdentity()) {
return Translate2D.getInstance(xoff, yoff);
} else {
return transform.copy().deriveWithTranslation(xoff, yoff);
}
}
@Override
public BaseBounds getBounds(BaseTransform transform,
Effect defaultInput)
{
BaseTransform at = getOffsetTransform(transform, xoff, yoff);
Effect input = getDefaultedInput(0, defaultInput);
return input.getBounds(at, defaultInput);
}
@Override
public ImageData filter(FilterContext fctx,
BaseTransform transform,
Rectangle outputClip,
Object renderHelper,
Effect defaultInput)
{
BaseTransform at = getOffsetTransform(transform, xoff, yoff);
Effect input = getDefaultedInput(0, defaultInput);
return input.filter(fctx, at, outputClip, renderHelper, defaultInput);
}
@Override
public Point2D transform(Point2D p, Effect defaultInput) {
p = getDefaultedInput(0, defaultInput).transform(p, defaultInput);
float x = (float) (p.x + xoff);
float y = (float) (p.y + yoff);
p = new Point2D(x, y);
return p;
}
@Override
public Point2D untransform(Point2D p, Effect defaultInput) {
float x = (float) (p.x - xoff);
float y = (float) (p.y - yoff);
p = new Point2D(x, y);
p = getDefaultedInput(0, defaultInput).untransform(p, defaultInput);
return p;
}
@Override
public AccelType getAccelType(FilterContext fctx) {
return getInputs().get(0).getAccelType(fctx);
}
@Override
public boolean reducesOpaquePixels() {
return getX() != 0 || getY() != 0 || (getInput() != null && getInput().reducesOpaquePixels());
}
@Override
public DirtyRegionContainer getDirtyRegions(Effect defaultInput, DirtyRegionPool regionPool) {
Effect di = getDefaultedInput(0, defaultInput);
DirtyRegionContainer drc = di.getDirtyRegions(defaultInput, regionPool);
if (xoff != 0 || yoff != 0) {
for (int i = 0; i < drc.size(); i++) {
drc.getDirtyRegion(i).translate(xoff, yoff, 0);
}
}
return drc;
}
}
