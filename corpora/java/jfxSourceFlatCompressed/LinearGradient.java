package javafx.scene.paint;
import java.util.List;
import com.sun.javafx.scene.paint.GradientUtils;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.NamedArg;
public final class LinearGradient extends Paint {
private double startX;
public final double getStartX() {
return startX;
}
private double startY;
public final double getStartY() {
return startY;
}
private double endX;
public final double getEndX() {
return endX;
}
private double endY;
public final double getEndY() {
return endY;
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
public LinearGradient(
@NamedArg("startX") double startX,
@NamedArg("startY") double startY,
@NamedArg(value="endX", defaultValue="1") double endX,
@NamedArg(value="endY", defaultValue="1") double endY,
@NamedArg(value="proportional", defaultValue="true") boolean proportional,
@NamedArg("cycleMethod") CycleMethod cycleMethod,
@NamedArg("stops") Stop... stops) {
this.startX = startX;
this.startY = startY;
this.endX = endX;
this.endY = endY;
this.proportional = proportional;
this.cycleMethod = (cycleMethod == null) ? CycleMethod.NO_CYCLE: cycleMethod;
this.stops = Stop.normalize(stops);
this.opaque = determineOpacity();
}
public LinearGradient(
@NamedArg("startX") double startX,
@NamedArg("startY") double startY,
@NamedArg(value="endX", defaultValue="1") double endX,
@NamedArg(value="endY", defaultValue="1") double endY,
@NamedArg(value="proportional", defaultValue="true") boolean proportional,
@NamedArg("cycleMethod") CycleMethod cycleMethod,
@NamedArg("stops") List<Stop> stops) {
this.startX = startX;
this.startY = startY;
this.endX = endX;
this.endY = endY;
this.proportional = proportional;
this.cycleMethod = (cycleMethod == null) ? CycleMethod.NO_CYCLE: cycleMethod;
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
if (obj == null) return false;
if (obj == this) return true;
if (obj instanceof LinearGradient) {
final LinearGradient other = (LinearGradient) obj;
if ((startX != other.startX) ||
(startY != other.startY) ||
(endX != other.endX) ||
(endY != other.endY) ||
(proportional != other.proportional) ||
(cycleMethod != other.cycleMethod)) return false;
if (!stops.equals(other.stops)) return false;
return true;
} else return false;
}
@Override public int hashCode() {
if (hash == 0) {
long bits = 17L;
bits = 37L * bits + Double.doubleToLongBits(startX);
bits = 37L * bits + Double.doubleToLongBits(startY);
bits = 37L * bits + Double.doubleToLongBits(endX);
bits = 37L * bits + Double.doubleToLongBits(endY);
bits = 37L * bits + ((proportional) ? 1231L : 1237L);
bits = 37L * bits + cycleMethod.hashCode();
for (Stop stop: stops) {
bits = 37L * bits + stop.hashCode();
}
hash = (int) (bits ^ (bits >> 32));
}
return hash;
}
@Override public String toString() {
final StringBuilder s = new StringBuilder("linear-gradient(from ")
.append(GradientUtils.lengthToString(startX, proportional))
.append(" ").append(GradientUtils.lengthToString(startY, proportional))
.append(" to ").append(GradientUtils.lengthToString(endX, proportional))
.append(" ").append(GradientUtils.lengthToString(endY, proportional))
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
public static LinearGradient valueOf(String value) {
if (value == null) {
throw new NullPointerException("gradient must be specified");
}
String start = "linear-gradient(";
String end = ")";
if (value.startsWith(start)) {
if (!value.endsWith(end)) {
throw new IllegalArgumentException("Invalid gradient specification, "
+ "must end with \"" + end + '"');
}
value = value.substring(start.length(), value.length() - end.length());
}
GradientUtils.Parser parser = new GradientUtils.Parser(value);
if (parser.getSize() < 2) {
throw new IllegalArgumentException("Invalid gradient specification");
}
GradientUtils.Point startX = GradientUtils.Point.MIN;
GradientUtils.Point startY = GradientUtils.Point.MIN;
GradientUtils.Point endX = GradientUtils.Point.MIN;
GradientUtils.Point endY = GradientUtils.Point.MIN;
String[] tokens = parser.splitCurrentToken();
if ("from".equals(tokens[0])) {
GradientUtils.Parser.checkNumberOfArguments(tokens, 5);
startX = parser.parsePoint(tokens[1]);
startY = parser.parsePoint(tokens[2]);
if (!"to".equals(tokens[3])) {
throw new IllegalArgumentException("Invalid gradient specification, \"to\" expected");
}
endX = parser.parsePoint(tokens[4]);
endY = parser.parsePoint(tokens[5]);
parser.shift();
} else if ("to".equals(tokens[0])) {
int horizontalSet = 0;
int verticalSet = 0;
for (int i = 1; i < 3 && i < tokens.length; i++) {
if ("left".equals(tokens[i])) {
startX = GradientUtils.Point.MAX;
endX = GradientUtils.Point.MIN;
horizontalSet++;
} else if ("right".equals(tokens[i])) {
startX = GradientUtils.Point.MIN;
endX = GradientUtils.Point.MAX;
horizontalSet++;
} else if ("top".equals(tokens[i])) {
startY = GradientUtils.Point.MAX;
endY = GradientUtils.Point.MIN;
verticalSet++;
} else if ("bottom".equals(tokens[i])) {
startY = GradientUtils.Point.MIN;
endY = GradientUtils.Point.MAX;
verticalSet++;
} else {
throw new IllegalArgumentException("Invalid gradient specification,"
+ " unknown value after 'to'");
}
}
if (verticalSet > 1) {
throw new IllegalArgumentException("Invalid gradient specification,"
+ " vertical direction set twice after 'to'");
}
if (horizontalSet > 1) {
throw new IllegalArgumentException("Invalid gradient specification,"
+ " horizontal direction set twice after 'to'");
}
parser.shift();
} else {
startY = GradientUtils.Point.MIN;
endY = GradientUtils.Point.MAX;
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
double dist = 0;
if (!startX.proportional) {
double dx = endX.value - startX.value;
double dy = endY.value - startY.value;
dist = Math.sqrt(dx*dx + dy*dy);
}
Stop[] stops = parser.parseStops(startX.proportional, dist);
return new LinearGradient(startX.value, startY.value, endX.value, endY.value,
startX.proportional, method, stops);
}
}
