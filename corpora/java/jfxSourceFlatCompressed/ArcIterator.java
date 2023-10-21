package com.sun.javafx.geom;
import java.util.NoSuchElementException;
import com.sun.javafx.geom.transform.BaseTransform;
class ArcIterator implements PathIterator {
double x, y, w, h, angStRad, increment, cv;
BaseTransform transform;
int index;
int arcSegs;
int lineSegs;
ArcIterator(Arc2D a, BaseTransform at) {
this.w = a.width / 2;
this.h = a.height / 2;
this.x = a.x + w;
this.y = a.y + h;
this.angStRad = -Math.toRadians(a.start);
this.transform = at;
double ext = -a.extent;
if (ext >= 360.0 || ext <= -360) {
arcSegs = 4;
this.increment = Math.PI / 2;
this.cv = 0.5522847498307933;
if (ext < 0) {
increment = -increment;
cv = -cv;
}
} else {
arcSegs = (int) Math.ceil(Math.abs(ext) / 90.0);
this.increment = Math.toRadians(ext / arcSegs);
this.cv = btan(increment);
if (cv == 0) {
arcSegs = 0;
}
}
switch (a.getArcType()) {
case Arc2D.OPEN:
lineSegs = 0;
break;
case Arc2D.CHORD:
lineSegs = 1;
break;
case Arc2D.PIE:
lineSegs = 2;
break;
}
if (w < 0 || h < 0) {
arcSegs = lineSegs = -1;
}
}
public int getWindingRule() {
return WIND_NON_ZERO;
}
public boolean isDone() {
return index > arcSegs + lineSegs;
}
public void next() {
++index;
}
private static double btan(double increment) {
increment /= 2.0;
return 4.0 / 3.0 * Math.sin(increment) / (1.0 + Math.cos(increment));
}
public int currentSegment(float[] coords) {
if (isDone()) {
throw new NoSuchElementException("arc iterator out of bounds");
}
double angle = angStRad;
if (index == 0) {
coords[0] = (float) (x + Math.cos(angle) * w);
coords[1] = (float) (y + Math.sin(angle) * h);
if (transform != null) {
transform.transform(coords, 0, coords, 0, 1);
}
return SEG_MOVETO;
}
if (index > arcSegs) {
if (index == arcSegs + lineSegs) {
return SEG_CLOSE;
}
coords[0] = (float) x;
coords[1] = (float) y;
if (transform != null) {
transform.transform(coords, 0, coords, 0, 1);
}
return SEG_LINETO;
}
angle += increment * (index - 1);
double relx = Math.cos(angle);
double rely = Math.sin(angle);
coords[0] = (float) (x + (relx - cv * rely) * w);
coords[1] = (float) (y + (rely + cv * relx) * h);
angle += increment;
relx = Math.cos(angle);
rely = Math.sin(angle);
coords[2] = (float) (x + (relx + cv * rely) * w);
coords[3] = (float) (y + (rely - cv * relx) * h);
coords[4] = (float) (x + relx * w);
coords[5] = (float) (y + rely * h);
if (transform != null) {
transform.transform(coords, 0, coords, 0, 3);
}
return SEG_CUBICTO;
}
}
