package com.sun.javafx.geom;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.NoninvertibleTransformException;
public abstract class TransformedShape extends Shape {
public static TransformedShape transformedShape(Shape original, BaseTransform tx) {
if (tx.isTranslateOrIdentity()) {
return translatedShape(original, tx.getMxt(), tx.getMyt());
}
return new General(original, tx.copy());
}
public static TransformedShape translatedShape(Shape original, double tx, double ty) {
return new Translate(original, (float) tx, (float) ty);
}
final protected Shape delegate;
protected TransformedShape(Shape delegate) {
this.delegate = delegate;
}
public Shape getDelegateNoClone() {
return delegate;
}
public abstract BaseTransform getTransformNoClone();
public abstract BaseTransform adjust(BaseTransform tx);
protected Point2D untransform(float x, float y) {
Point2D p = new Point2D(x, y);
try {
p = getTransformNoClone().inverseTransform(p, p);
} catch (NoninvertibleTransformException e) {
}
return p;
}
protected BaseBounds untransformedBounds(float x, float y, float w, float h) {
RectBounds b = new RectBounds(x, y, x+w, y+h);
try {
return getTransformNoClone().inverseTransform(b, b);
} catch (NoninvertibleTransformException e) {
return b.makeEmpty();
}
}
@Override
public RectBounds getBounds() {
float box[] = new float[4];
Shape.accumulate(box, delegate, getTransformNoClone());
return new RectBounds(box[0], box[1], box[2], box[3]);
}
@Override
public boolean contains(float x, float y) {
return delegate.contains(untransform(x, y));
}
private Shape cachedTransformedShape;
private Shape getCachedTransformedShape() {
if (cachedTransformedShape == null) {
cachedTransformedShape = copy();
}
return cachedTransformedShape;
}
@Override
public boolean intersects(float x, float y, float w, float h) {
return getCachedTransformedShape().intersects(x, y, w, h);
}
@Override
public boolean contains(float x, float y, float w, float h) {
return getCachedTransformedShape().contains(x, y, w, h);
}
@Override
public PathIterator getPathIterator(BaseTransform transform) {
return delegate.getPathIterator(adjust(transform));
}
@Override
public PathIterator getPathIterator(BaseTransform transform,
float flatness)
{
return delegate.getPathIterator(adjust(transform), flatness);
}
@Override
public Shape copy() {
return getTransformNoClone().createTransformedShape(delegate);
}
static final class General extends TransformedShape {
BaseTransform transform;
General(Shape delegate, BaseTransform transform) {
super(delegate);
this.transform = transform;
}
public BaseTransform getTransformNoClone() {
return transform;
}
public BaseTransform adjust(BaseTransform transform) {
if (transform == null || transform.isIdentity()) {
return this.transform.copy();
} else {
return transform.copy().deriveWithConcatenation(this.transform);
}
}
}
static final class Translate extends TransformedShape {
private final float tx, ty;
private BaseTransform cachedTx;
public Translate(Shape delegate, float tx, float ty) {
super(delegate);
this.tx = tx;
this.ty = ty;
}
@Override
public BaseTransform getTransformNoClone() {
if (cachedTx == null) {
cachedTx = BaseTransform.getTranslateInstance(tx, ty);
}
return cachedTx;
}
public BaseTransform adjust(BaseTransform transform) {
if (transform == null || transform.isIdentity()) {
return BaseTransform.getTranslateInstance(tx, ty);
} else {
return transform.copy().deriveWithTranslation(tx, ty);
}
}
@Override
public RectBounds getBounds() {
RectBounds rb = delegate.getBounds();
rb.setBounds(rb.getMinX() + tx, rb.getMinY() + ty,
rb.getMaxX() + tx, rb.getMaxY() + ty);
return rb;
}
@Override
public boolean contains(float x, float y) {
return delegate.contains(x - tx, y - ty);
}
@Override
public boolean intersects(float x, float y, float w, float h) {
return delegate.intersects(x - tx, y - ty, w, h);
}
@Override
public boolean contains(float x, float y, float w, float h) {
return delegate.contains(x - tx, y - ty, w, h);
}
}
}
