package com.sun.javafx.geom;
import java.util.NoSuchElementException;
import com.sun.javafx.geom.transform.BaseTransform;
class LineIterator implements PathIterator {
Line2D line;
BaseTransform transform;
int index;
LineIterator(Line2D l, BaseTransform tx) {
this.line = l;
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
throw new NoSuchElementException("line iterator out of bounds");
}
int type;
if (index == 0) {
coords[0] = line.x1;
coords[1] = line.y1;
type = SEG_MOVETO;
} else {
coords[0] = line.x2;
coords[1] = line.y2;
type = SEG_LINETO;
}
if (transform != null) {
transform.transform(coords, 0, coords, 0, 1);
}
return type;
}
}
