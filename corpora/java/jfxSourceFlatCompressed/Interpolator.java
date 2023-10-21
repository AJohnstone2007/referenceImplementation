package javafx.animation;
import javafx.util.Duration;
import com.sun.scenario.animation.NumberTangentInterpolator;
import com.sun.scenario.animation.SplineInterpolator;
public abstract class Interpolator {
private static final double EPSILON = 1e-12;
protected Interpolator() {
}
public static final Interpolator DISCRETE = new Interpolator() {
@Override
protected double curve(double t) {
return (Math.abs(t - 1.0) < EPSILON) ? 1.0 : 0.0;
}
@Override
public String toString() {
return "Interpolator.DISCRETE";
}
};
public static final Interpolator LINEAR = new Interpolator() {
@Override
protected double curve(double t) {
return t;
}
@Override
public String toString() {
return "Interpolator.LINEAR";
}
};
public static final Interpolator EASE_BOTH = new Interpolator() {
@Override
protected double curve(double t) {
return Interpolator.clamp((t < 0.2) ? 3.125 * t * t
: (t > 0.8) ? -3.125 * t * t + 6.25 * t - 2.125
: 1.25 * t - 0.125);
}
@Override
public String toString() {
return "Interpolator.EASE_BOTH";
}
};
public static final Interpolator EASE_IN = new Interpolator() {
private static final double S1 = 25.0 / 9.0;
private static final double S3 = 10.0 / 9.0;
private static final double S4 = 1.0 / 9.0;
@Override
protected double curve(double t) {
return Interpolator.clamp((t < 0.2) ? S1 * t * t : S3 * t - S4);
}
@Override
public String toString() {
return "Interpolator.EASE_IN";
}
};
public static final Interpolator EASE_OUT = new Interpolator() {
private static final double S1 = -25.0 / 9.0;
private static final double S2 = 50.0 / 9.0;
private static final double S3 = -16.0 / 9.0;
private static final double S4 = 10.0 / 9.0;
@Override
protected double curve(double t) {
return Interpolator.clamp((t > 0.8) ? S1 * t * t + S2 * t + S3 : S4
* t);
}
@Override
public String toString() {
return "Interpolator.EASE_OUT";
}
};
public static Interpolator SPLINE(double x1, double y1, double x2, double y2) {
return new SplineInterpolator(x1, y1, x2, y2);
}
public static Interpolator TANGENT(Duration t1, double v1, Duration t2,
double v2) {
return new NumberTangentInterpolator(t1, v1, t2, v2);
}
public static Interpolator TANGENT(Duration t, double v) {
return new NumberTangentInterpolator(t, v);
}
@SuppressWarnings({ "unchecked", "rawtypes" })
public Object interpolate(Object startValue, Object endValue,
double fraction) {
if ((startValue instanceof Number) && (endValue instanceof Number)) {
final double start = ((Number) startValue).doubleValue();
final double end = ((Number) endValue).doubleValue();
final double val = start + (end - start) * curve(fraction);
if ((startValue instanceof Double) || (endValue instanceof Double)) {
return Double.valueOf(val);
}
if ((startValue instanceof Float) || (endValue instanceof Float)) {
return Float.valueOf((float) val);
}
if ((startValue instanceof Long) || (endValue instanceof Long)) {
return Long.valueOf(Math.round(val));
}
return Integer.valueOf((int) Math.round(val));
} else if ((startValue instanceof Interpolatable) && (endValue instanceof Interpolatable)) {
return ((Interpolatable) startValue).interpolate(endValue,
curve(fraction));
} else {
return (curve(fraction) == 1.0) ? endValue : startValue;
}
}
public boolean interpolate(boolean startValue, boolean endValue,
double fraction) {
return (Math.abs(curve(fraction) - 1.0) < EPSILON) ? endValue
: startValue;
}
public double interpolate(double startValue, double endValue,
double fraction) {
return startValue + (endValue - startValue) * curve(fraction);
}
public int interpolate(int startValue, int endValue, double fraction) {
return startValue
+ (int) Math.round((endValue - startValue) * curve(fraction));
}
public long interpolate(long startValue, long endValue,
double fraction) {
return startValue
+ Math.round((endValue - startValue) * curve(fraction));
}
private static double clamp(double t) {
return (t < 0.0) ? 0.0 : (t > 1.0) ? 1.0 : t;
}
protected abstract double curve(double t);
}
