package com.sun.javafx.geom;
import java.util.NoSuchElementException;
import com.sun.javafx.geom.transform.BaseTransform;
class QuadIterator implements PathIterator {
QuadCurve2D quad;
BaseTransform transform;
int index;
QuadIterator(QuadCurve2D q, BaseTransform tx) {
this.quad = q;
this.transform = tx;
}
public int getWindingRule() {
return WIND_NON_ZERO;
}
public boolean isDone() {
return (index > 1);
}
public void next() {
++index;
}
public int currentSegment(float[] coords) {
if (isDone()) {
throw new NoSuchElementException("quad iterator iterator out of bounds");
}
int type;
if (index == 0) {
coords[0] = quad.x1;
coords[1] = quad.y1;
type = SEG_MOVETO;
} else {
coords[0] = quad.ctrlx;
coords[1] = quad.ctrly;
coords[2] = quad.x2;
coords[3] = quad.y2;
type = SEG_QUADTO;
}
if (transform != null) {
transform.transform(coords, 0, coords, 0, index == 0 ? 1 : 2);
}
return type;
}
}
