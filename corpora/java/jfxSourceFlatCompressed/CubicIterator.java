package com.sun.javafx.geom;
import java.util.NoSuchElementException;
import com.sun.javafx.geom.transform.BaseTransform;
class CubicIterator implements PathIterator {
CubicCurve2D cubic;
BaseTransform transform;
int index;
CubicIterator(CubicCurve2D q, BaseTransform tx) {
this.cubic = q;
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
throw new NoSuchElementException("cubic iterator iterator out of bounds");
}
int type;
if (index == 0) {
coords[0] = cubic.x1;
coords[1] = cubic.y1;
type = SEG_MOVETO;
} else {
coords[0] = cubic.ctrlx1;
coords[1] = cubic.ctrly1;
coords[2] = cubic.ctrlx2;
coords[3] = cubic.ctrly2;
coords[4] = cubic.x2;
coords[5] = cubic.y2;
type = SEG_CUBICTO;
}
if (transform != null) {
transform.transform(coords, 0, coords, 0, index == 0 ? 1 : 3);
}
return type;
}
}
