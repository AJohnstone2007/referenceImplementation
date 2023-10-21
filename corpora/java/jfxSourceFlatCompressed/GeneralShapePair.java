package com.sun.javafx.geom;
import com.sun.javafx.geom.transform.BaseTransform;
public class GeneralShapePair extends ShapePair {
private final Shape outer;
private final Shape inner;
private final int combinationType;
public GeneralShapePair(Shape outer, Shape inner, int combinationType) {
this.outer = outer;
this.inner = inner;
this.combinationType = combinationType;
}
@Override
public final int getCombinationType() {
return combinationType;
}
@Override
public final Shape getOuterShape() {
return outer;
}
@Override
public final Shape getInnerShape() {
return inner;
}
@Override
public Shape copy() {
return new GeneralShapePair(outer.copy(), inner.copy(), combinationType);
}
@Override
public boolean contains(float x, float y) {
if (combinationType == TYPE_INTERSECT) {
return outer.contains(x, y) && inner.contains(x, y);
} else {
return outer.contains(x, y) && !inner.contains(x, y);
}
}
@Override
public boolean intersects(float x, float y, float w, float h) {
if (combinationType == TYPE_INTERSECT) {
return outer.intersects(x, y, w, h) && inner.intersects(x, y, w, h);
} else {
return outer.intersects(x, y, w, h) && !inner.contains(x, y, w, h);
}
}
@Override
public boolean contains(float x, float y, float w, float h) {
if (combinationType == TYPE_INTERSECT) {
return outer.contains(x, y, w, h) && inner.contains(x, y, w, h);
} else {
return outer.contains(x, y, w, h) && !inner.intersects(x, y, w, h);
}
}
@Override
public RectBounds getBounds() {
RectBounds b = outer.getBounds();
if (combinationType == TYPE_INTERSECT) {
b.intersectWith(inner.getBounds());
}
return b;
}
@Override
public PathIterator getPathIterator(BaseTransform tx) {
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
public PathIterator getPathIterator(BaseTransform tx, float flatness) {
return new FlatteningPathIterator(getPathIterator(tx), flatness);
}
}
