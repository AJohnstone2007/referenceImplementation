package com.sun.javafx.sg.prism;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.Shape;
public class NGPolyline extends NGShape {
private Path2D path = new Path2D();
public void updatePolyline(float[] points) {
path.reset();
if (points == null || points.length == 0 || points.length % 2 != 0) {
return;
}
path.moveTo(points[0], points[1]);
for (int i = 1; i < points.length/2; i++) {
float px = points[i*2+0];
float py = points[i*2+1];
path.lineTo(px, py);
}
geometryChanged();
}
@Override
public Shape getShape() {
return path;
}
}
