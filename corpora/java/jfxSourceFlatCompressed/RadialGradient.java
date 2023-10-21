package javafx.scene.paint;
import java.util.List;
import com.sun.javafx.scene.paint.GradientUtils;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.NamedArg;
public final class RadialGradient extends Paint {
private double focusAngle;
public final double getFocusAngle() {
return focusAngle;
}
private double focusDistance;
public final double getFocusDistance() {
return focusDistance;
}
private double centerX;
public final double getCenterX() {
return centerX;
}
private double centerY;
public final double getCenterY() {
return centerY;
}
private double radius;
public final double getRadius() {
return radius;
}
private boolean proportional;
public final boolean isProportional() {
return proportional;
}
private CycleMethod cycleMethod;
public final CycleMethod getCycleMethod() {
return cycleMethod;
}
private List<Stop> stops;
public final List<Stop> getStops() {
return stops;
}
@Override public final boolean isOpaque() {
return opaque;
}
private final boolean opaque;
private Object platformPaint;
private int hash;
public RadialGradient(
@NamedArg("focusAngle") double focusAngle,
@NamedArg("focusDistance") double focusDistance,
@NamedArg("centerX") double centerX,
@NamedArg("centerY") double centerY,
@NamedArg(value="radius", defaultValue="1") double radius,
@NamedArg(value="proportional", defaultValue="true") boolean proportional,
@NamedArg("cycleMethod") CycleMethod cycleMethod,
@NamedArg("stops") Stop... stops) {
this.focusAngle = focusAngle;
this.focusDistance = focusDistance;
this.centerX = centerX;
this.centerY = centerY;
this.radius = radius;
this.proportional = proportional;
this.cycleMethod = (cycleMethod == null) ? CycleMethod.NO_CYCLE : cycleMethod;
this.stops = Stop.normalize(stops);
this.opaque = determineOpacity();
}
public RadialGradient(
@NamedArg("focusAngle") double focusAngle,
@NamedArg("focusDistance") double focusDistance,
@NamedArg("centerX") double centerX,
@NamedArg("centerY") double centerY,
@NamedArg(value="radius", defaultValue="1") double radius,
@NamedArg(value="proportional", defaultValue="true") boolean proportional,
@NamedArg("cycleMethod") CycleMethod cycleMethod,
@NamedArg("stops") List<Stop> stops) {
this.focusAngle = focusAngle;
this.focusDistance = focusDistance;
this.centerX = centerX;
this.centerY = centerY;
this.radius = radius;
this.proportional = proportional;
this.cycleMethod = (cycleMethod == null) ? CycleMethod.NO_CYCLE : cycleMethod;
this.stops = Stop.normalize(stops);
this.opaque = determineOpacity();
}
private boolean determineOpacity() {
final int numStops = this.stops.size();
for (int i = 0; i < numStops; i++) {
if (!stops.get(i).getColor().isOpaque()) {
return false;
}
}
return true;
}
@Override
Object acc_getPlatformPaint() {
if (platformPaint == null) {
platformPaint = Toolkit.getToolkit().getPaint(this);
}
return platformPaint;
}
@Override public boolean equals(Object obj) {
if (obj == this) return true;
if (obj instanceof RadialGradient) {
final RadialGradient other = (RadialGradient) obj;
if ((focusAngle != other.focusAngle) ||
(focusDistance != other.focusDistance) ||
(centerX != other.centerX) ||
(centerY != other.centerY) ||
(radius != other.radius) ||
(proportional != other.proportional) ||
(cycleMethod != other.cycleMethod)) return false;
if (!stops.equals(other.stops)) return false;
return true;
} else return false;
}
@Override public int hashCode() {
if (hash == 0) {
long bits = 17;
bits = 37 * bits + Double.doubleToLongBits(focusAngle);
bits = 37 * bits + Double.doubleToLongBits(focusDistance);
bits = 37 * bits + Double.doubleToLongBits(centerX);
bits = 37 * bits + Double.doubleToLongBits(centerY);
bits = 37 * bits + Double.doubleToLongBits(radius);
bits = 37 * bits + (proportional ? 1231 : 1237);
bits = 37 * bits + cycleMethod.hashCode();
for (Stop stop: stops) {
bits = 37 * bits + stop.hashCode();
}
hash = (int) (bits ^ (bits >> 32));
}
return hash;
}
@Override public String toString() {
final StringBuilder s = new StringBuilder("radial-gradient(focus-angle ").append(focusAngle)
.append("deg, focus-distance ").append(focusDistance * 100)
.append("% , center ").append(GradientUtils.lengthToString(centerX, proportional))
.append(" ").append(GradientUtils.lengthToString(centerY, proportional))
.append(", radius ").append(GradientUtils.lengthToString(radius, proportional))
.append(", ");
switch (cycleMethod) {
case REFLECT:
s.append("reflect").append(", ");
break;
case REPEAT:
s.append("repeat").append(", ");
break;
}
for (Stop stop : stops) {
s.append(stop).append(", ");
}
s.delete(s.length() - 2, s.length());
s.append(")");
return s.toString();
}
public static RadialGradient valueOf(String value) {
if (value == null) {
throw new NullPointerException("gradient must be specified");
}
String start = "radial-gradient(";
String end = ")";
if (value.startsWith(start)) {
if (!value.endsWith(end)) {
throw new IllegalArgumentException("Invalid gradient specification,"
+ " must end with \"" + end + '"');
}
value = value.substring(start.length(), value.length() - end.length());
}
GradientUtils.Parser parser = new GradientUtils.Parser(value);
if (parser.getSize() < 2) {
throw new IllegalArgumentException("Invalid gradient specification");
}
double angle = 0, distance = 0;
GradientUtils.Point centerX, centerY, radius;
String[] tokens = parser.splitCurrentToken();
if ("focus-angle".equals(tokens[0])) {
GradientUtils.Parser.checkNumberOfArguments(tokens, 1);
angle = GradientUtils.Parser.parseAngle(tokens[1]);
parser.shift();
}
tokens = parser.splitCurrentToken();
if ("focus-distance".equals(tokens[0])) {
GradientUtils.Parser.checkNumberOfArguments(tokens, 1);
distance = GradientUtils.Parser.parsePercentage(tokens[1]);
parser.shift();
}
tokens = parser.splitCurrentToken();
if ("center".equals(tokens[0])) {
GradientUtils.Parser.checkNumberOfArguments(tokens, 2);
centerX = parser.parsePoint(tokens[1]);
centerY = parser.parsePoint(tokens[2]);
parser.shift();
} else {
centerX = GradientUtils.Point.MIN;
centerY = GradientUtils.Point.MIN;
}
tokens = parser.splitCurrentToken();
if ("radius".equals(tokens[0])) {
GradientUtils.Parser.checkNumberOfArguments(tokens, 1);
radius = parser.parsePoint(tokens[1]);
parser.shift();
} else {
throw new IllegalArgumentException("Invalid gradient specification: "
+ "radius must be specified");
}
CycleMethod method = CycleMethod.NO_CYCLE;
String currentToken = parser.getCurrentToken();
if ("repeat".equals(currentToken)) {
method = CycleMethod.REPEAT;
parser.shift();
} else if ("reflect".equals(currentToken)) {
method = CycleMethod.REFLECT;
parser.shift();
}
Stop[] stops = parser.parseStops(radius.proportional, radius.value);
return new RadialGradient(angle, distance, centerX.value, centerY.value,
radius.value, radius.proportional, method, stops);
}
}
