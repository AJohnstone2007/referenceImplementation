package test.com.sun.javafx.test;
import javafx.geometry.Bounds;
public class BBoxComparator extends ValueComparator {
private double delta;
public BBoxComparator(double delta) {
this.delta = delta;
}
@Override
public boolean equals(Object expected, Object actual) {
if (expected == actual) {
return true;
}
if (!(expected instanceof Bounds)) {
return false;
}
if (!(actual instanceof Bounds)) {
return false;
}
Bounds bExpectend = (Bounds) expected;
Bounds bActual = (Bounds) actual;
if (Math.abs(bExpectend.getMinX() - bActual.getMinX()) > delta) {
return false;
}
if (Math.abs(bExpectend.getMinY() - bActual.getMinY()) > delta) {
return false;
}
if (Math.abs(bExpectend.getMaxX() - bActual.getMaxX()) > delta) {
return false;
}
if (Math.abs(bExpectend.getMaxY() - bActual.getMaxY()) > delta) {
return false;
}
return true;
}
}
