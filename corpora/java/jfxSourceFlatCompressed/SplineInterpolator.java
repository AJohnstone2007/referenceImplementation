package com.sun.scenario.animation;
import javafx.animation.Interpolator;
public class SplineInterpolator extends Interpolator {
private final double x1, y1, x2, y2;
private final boolean isCurveLinear;
private static final int SAMPLE_SIZE = 16;
private static final double SAMPLE_INCREMENT = 1.0 / SAMPLE_SIZE;
private final double[] xSamples = new double[SAMPLE_SIZE + 1];
public SplineInterpolator(double px1, double py1, double px2, double py2) {
if (px1 < 0 || px1 > 1 || py1 < 0 || py1 > 1 || px2 < 0 || px2 > 1
|| py2 < 0 || py2 > 1) {
throw new IllegalArgumentException(
"Control point coordinates must " + "all be in range [0,1]");
}
this.x1 = px1;
this.y1 = py1;
this.x2 = px2;
this.y2 = py2;
isCurveLinear = ((x1 == y1) && (x2 == y2));
if (!isCurveLinear) {
for (int i = 0; i < SAMPLE_SIZE + 1; ++i) {
xSamples[i] = eval(i * SAMPLE_INCREMENT, x1, x2);
}
}
}
public double getX1() {
return x1;
}
public double getY1() {
return y1;
}
public double getX2() {
return x2;
}
public double getY2() {
return y2;
}
@Override
public int hashCode() {
int hash = 7;
hash = 19 * hash + (int) (Double.doubleToLongBits(this.x1) ^ (Double.doubleToLongBits(this.x1) >>> 32));
hash = 19 * hash + (int) (Double.doubleToLongBits(this.y1) ^ (Double.doubleToLongBits(this.y1) >>> 32));
hash = 19 * hash + (int) (Double.doubleToLongBits(this.x2) ^ (Double.doubleToLongBits(this.x2) >>> 32));
hash = 19 * hash + (int) (Double.doubleToLongBits(this.y2) ^ (Double.doubleToLongBits(this.y2) >>> 32));
return hash;
}
@Override
public boolean equals(Object obj) {
if (obj == null) {
return false;
}
if (getClass() != obj.getClass()) {
return false;
}
final SplineInterpolator other = (SplineInterpolator) obj;
if (Double.doubleToLongBits(this.x1) != Double.doubleToLongBits(other.x1)) {
return false;
}
if (Double.doubleToLongBits(this.y1) != Double.doubleToLongBits(other.y1)) {
return false;
}
if (Double.doubleToLongBits(this.x2) != Double.doubleToLongBits(other.x2)) {
return false;
}
if (Double.doubleToLongBits(this.y2) != Double.doubleToLongBits(other.y2)) {
return false;
}
return true;
}
@Override
public double curve(double x) {
if (x < 0 || x > 1) {
throw new IllegalArgumentException("x must be in range [0,1]");
}
if (isCurveLinear || x == 0 || x == 1) {
return x;
}
return eval(findTForX(x), y1, y2);
}
private double eval(double t, double p1, double p2) {
double compT = 1 - t;
return t * (3 * compT * (compT * p1 + t * p2) + (t * t));
}
private double evalDerivative(double t, double p1, double p2) {
double compT = 1 - t;
return 3 * (compT * (compT * p1 + 2 * t * (p2 - p1)) + t * t * (1 - p2));
}
private double getInitialGuessForT(double x) {
for (int i = 1; i < SAMPLE_SIZE + 1; ++i) {
if (xSamples[i] >= x) {
double xRange = xSamples[i] - xSamples[i - 1];
if (xRange == 0) {
return (i - 1) * SAMPLE_INCREMENT;
} else {
return ((i - 1) + ((x - xSamples[i - 1]) / xRange))
* SAMPLE_INCREMENT;
}
}
}
return 1;
}
private double findTForX(double x) {
double t = getInitialGuessForT(x);
final int numIterations = 4;
for (int i = 0; i < numIterations; ++i) {
double xT = (eval(t, x1, x2) - x);
if (xT == 0) {
break;
}
double dXdT = evalDerivative(t, x1, x2);
if (dXdT == 0) {
break;
}
t -= xT / dXdT;
}
return t;
}
@Override
public String toString() {
return "SplineInterpolator [x1=" + x1 + ", y1=" + y1 + ", x2=" + x2
+ ", y2=" + y2 + "]";
}
}
