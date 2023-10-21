package javafx.css;
import javafx.scene.text.Font;
final public class Size {
final private double value;
final private SizeUnits units;
public Size(double value, SizeUnits units) {
this.value = value;
this.units = (units != null) ? units : SizeUnits.PX;
}
public double getValue() {
return value;
}
public SizeUnits getUnits() {
return units;
}
public boolean isAbsolute() {
return units.isAbsolute();
}
double points(Font font) {
return points(1.0, font);
}
double points(double multiplier, Font font) {
return units.points(value, multiplier, font);
}
public double pixels(double multiplier, Font font) {
return units.pixels(value, multiplier, font);
}
public double pixels(Font font) {
return pixels(1.0f, font);
}
double pixels(double multiplier) {
return pixels(multiplier, null);
}
public double pixels() {
return pixels(1.0f, null);
}
@Override public String toString() {
return Double.toString(value) + units.toString();
}
@Override public int hashCode() {
long bits = 17L;
bits = 37L * bits + Double.doubleToLongBits(value);
bits = 37L * bits + units.hashCode();
return (int) (bits ^ (bits >> 32));
}
@Override public boolean equals(Object obj) {
if (this == obj) return true;
if (obj == null || obj.getClass() != this.getClass()) {
return false;
}
final Size other = (Size)obj;
if (units != other.units) {
return false;
}
if (value == other.value) {
return true;
}
if ((value > 0) ? other.value > 0 : other.value < 0) {
final double v0 = value > 0 ? value : -value;
final double v1 = other.value > 0 ? other.value : -other.value;
final double diff = value - other.value;
if (diff < -0.000001 || 0.000001 < diff) {
return false;
}
return true;
}
return false;
}
}
