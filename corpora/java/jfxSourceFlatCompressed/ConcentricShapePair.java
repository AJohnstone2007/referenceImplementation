package com.sun.javafx.geom;
import com.sun.javafx.geom.transform.BaseTransform;
public final class ConcentricShapePair extends ShapePair {
private final Shape outer;
private final Shape inner;
public ConcentricShapePair(Shape outer, Shape inner) {
this.outer = outer;
this.inner = inner;
}
@Override
public int getCombinationType() {
return TYPE_SUBTRACT;
}
@Override
public Shape getOuterShape() {
return outer;
}
@Override
public Shape getInnerShape() {
return inner;
}
@Override
public Shape copy() {
return new ConcentricShapePair(outer.copy(), inner.copy());
}
@Override
public boolean contains(float x, float y) {
return outer.contains(x, y) && !inner.contains(x, y);
}
@Override
public boolean intersects(float x, float y, float w, float h) {
return outer.intersects(x, y, w, h) && !inner.contains(x, y, w, h);
}
@Override
public boolean contains(float x, float y, float w, float h) {
return outer.contains(x, y, w, h) && !inner.intersects(x, y, w, h);
}
@Override
public RectBounds getBounds() {
return outer.getBounds();
}
@Override
public PathIterator getPathIterator(BaseTransform tx) {
return new PairIterator(outer.getPathIterator(tx),
inner.getPathIterator(tx));
}
@Override
public PathIterator getPathIterator(BaseTransform tx, float flatness) {
return new PairIterator(outer.getPathIterator(tx, flatness),
inner.getPathIterator(tx, flatness));
}
static class PairIterator implements PathIterator {
PathIterator outer;
PathIterator inner;
PairIterator(PathIterator outer, PathIterator inner) {
this.outer = outer;
this.inner = inner;
}
public int getWindingRule() {
return WIND_EVEN_ODD;
}
public int currentSegment(float[] coords) {
if (outer.isDone()) {
return inner.currentSegment(coords);
} else {
return outer.currentSegment(coords);
}
}
public boolean isDone() {
return outer.isDone() && inner.isDone();
}
public void next() {
if (outer.isDone()) {
inner.next();
} else {
outer.next();
}
}
}
}
