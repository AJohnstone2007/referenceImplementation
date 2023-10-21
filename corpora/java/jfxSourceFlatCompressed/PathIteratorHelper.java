package com.sun.javafx.tk.quantum;
import com.sun.javafx.geom.PathIterator;
class PathIteratorHelper {
public static final class Struct {
float f0, f1, f2, f3, f4, f5;
}
private PathIterator itr;
private float[] f = new float[6];
public PathIteratorHelper(PathIterator itr) {
this.itr = itr;
}
public int getWindingRule() {
return itr.getWindingRule();
}
public boolean isDone() {
return itr.isDone();
}
public void next() {
itr.next();
}
public int currentSegment(Struct struct) {
int ret = itr.currentSegment(f);
struct.f0 = f[0];
struct.f1 = f[1];
struct.f2 = f[2];
struct.f3 = f[3];
struct.f4 = f[4];
struct.f5 = f[5];
return ret;
}
}
