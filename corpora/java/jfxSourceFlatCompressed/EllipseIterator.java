package com.sun.javafx.geom;
import java.util.NoSuchElementException;
import com.sun.javafx.geom.transform.BaseTransform;
class EllipseIterator implements PathIterator {
double x, y, w, h;
BaseTransform transform;
int index;
EllipseIterator(Ellipse2D e, BaseTransform tx) {
this.x = e.x;
this.y = e.y;
this.w = e.width;
this.h = e.height;
this.transform = tx;
if (w < 0f || h < 0f) {
index = 6;
}
}
public int getWindingRule() {
return WIND_NON_ZERO;
}
public boolean isDone() {
return index > 5;
}
public void next() {
++index;
}
public static final double CtrlVal = 0.5522847498307933;
private static final double pcv = 0.5 + CtrlVal * 0.5;
private static final double ncv = 0.5 - CtrlVal * 0.5;
private static final double ctrlpts[][] = {
{ 1.0, pcv, pcv, 1.0, 0.5, 1.0 },
{ ncv, 1.0, 0.0, pcv, 0.0, 0.5 },
{ 0.0, ncv, ncv, 0.0, 0.5, 0.0 },
{ pcv, 0.0, 1.0, ncv, 1.0, 0.5 }
};
public int currentSegment(float[] coords) {
if (isDone()) {
throw new NoSuchElementException("ellipse iterator out of bounds");
}
if (index == 5) {
return SEG_CLOSE;
}
if (index == 0) {
double ctrls[] = ctrlpts[3];
coords[0] = (float) (x + ctrls[4] * w);
coords[1] = (float) (y + ctrls[5] * h);
if (transform != null) {
transform.transform(coords, 0, coords, 0, 1);
}
return SEG_MOVETO;
}
double ctrls[] = ctrlpts[index - 1];
coords[0] = (float) (x + ctrls[0] * w);
coords[1] = (float) (y + ctrls[1] * h);
coords[2] = (float) (x + ctrls[2] * w);
coords[3] = (float) (y + ctrls[3] * h);
coords[4] = (float) (x + ctrls[4] * w);
coords[5] = (float) (y + ctrls[5] * h);
if (transform != null) {
transform.transform(coords, 0, coords, 0, 3);
}
return SEG_CUBICTO;
}
}
