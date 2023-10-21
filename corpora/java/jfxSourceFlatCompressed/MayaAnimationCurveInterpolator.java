package com.javafx.experiments.importers.maya;
import javafx.animation.Interpolator;
class MayaAnimationCurveInterpolator extends Interpolator {
float p1Delta;
float p2Delta;
boolean zeroDuration;
public String debug;
public MayaAnimationCurveInterpolator(
float p1Delta, float p2Delta,
boolean zeroDuration) {
this.p1Delta = p1Delta;
this.p2Delta = p2Delta;
this.zeroDuration = zeroDuration;
}
public double curve(double t) { return t; }
public double interpolate2(
double startValue, double endValue,
double fraction) {
if (Double.isNaN(fraction)) {
return startValue;
}
if (zeroDuration) {
return endValue;
}
float t = (float) fraction;
float oneMinusT = 1.0f - t;
float tSquared = t * t;
float oneMinusTSquared = oneMinusT * oneMinusT;
float p0 = (float) startValue;
float p3 = (float) endValue;
float p1 = p0 + p1Delta;
float p2 = p3 + p2Delta;
float ret = ((oneMinusTSquared * oneMinusT * p0) +
(3 * oneMinusTSquared * t * p1) +
(3 * oneMinusT * tSquared * p2) +
(tSquared * t * p3));
if (debug != null) {
}
return ret;
}
public int interpolate2(int startValue, int endValue, double fraction) {
return (int) interpolate(
(double) startValue,
(double) endValue,
fraction);
}
public Object interpolate2(Object startValue, Object endValue, double fraction) {
return interpolate(
((Number) startValue).doubleValue(),
((Number) endValue).doubleValue(),
fraction);
}
}
