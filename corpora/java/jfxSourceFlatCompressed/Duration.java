package javafx.util;
import java.io.Serializable;
import javafx.beans.NamedArg;
public class Duration implements Comparable<Duration>, Serializable {
public static final Duration ZERO = new Duration(0);
public static final Duration ONE = new Duration(1);
public static final Duration INDEFINITE = new Duration(Double.POSITIVE_INFINITY);
public static final Duration UNKNOWN = new Duration(Double.NaN);
public static Duration valueOf(String time) {
int index = -1;
for (int i=0; i<time.length(); i++) {
char c = time.charAt(i);
if (!Character.isDigit(c) && c != '.' && c != '-') {
index = i;
break;
}
}
if (index == -1) {
throw new IllegalArgumentException("The time parameter must have a suffix of [ms|s|m|h]");
}
double value = Double.parseDouble(time.substring(0, index));
String suffix = time.substring(index);
if ("ms".equals(suffix)) {
return millis(value);
} else if ("s".equals(suffix)) {
return seconds(value);
} else if ("m".equals(suffix)) {
return minutes(value);
} else if ("h".equals(suffix)) {
return hours(value);
} else {
throw new IllegalArgumentException("The time parameter must have a suffix of [ms|s|m|h]");
}
}
public static Duration millis(double ms) {
if (ms == 0) {
return ZERO;
} else if (ms == 1) {
return ONE;
} else if (ms == Double.POSITIVE_INFINITY) {
return INDEFINITE;
} else if (Double.isNaN(ms)) {
return UNKNOWN;
} else {
return new Duration(ms);
}
}
public static Duration seconds(double s) {
if (s == 0) {
return ZERO;
} else if (s == Double.POSITIVE_INFINITY) {
return INDEFINITE;
} else if (Double.isNaN(s)) {
return UNKNOWN;
} else {
return new Duration(s * 1000.0);
}
}
public static Duration minutes(double m) {
if (m == 0) {
return ZERO;
} else if (m == Double.POSITIVE_INFINITY) {
return INDEFINITE;
} else if (Double.isNaN(m)) {
return UNKNOWN;
} else {
return new Duration(m * (1000.0 * 60.0));
}
}
public static Duration hours(double h) {
if (h == 0) {
return ZERO;
} else if (h == Double.POSITIVE_INFINITY) {
return INDEFINITE;
} else if (Double.isNaN(h)) {
return UNKNOWN;
} else {
return new Duration(h * (1000.0 * 60.0 * 60.0));
}
}
private final double millis;
public Duration(@NamedArg("millis") double millis) {
this.millis = millis;
}
public double toMillis() {
return millis;
}
public double toSeconds() {
return millis / 1000.0;
}
public double toMinutes() {
return millis / (60 * 1000.0);
}
public double toHours() {
return millis / (60 * 60 * 1000.0);
}
public Duration add(Duration other) {
return millis(millis + other.millis);
}
public Duration subtract(Duration other) {
return millis(millis - other.millis);
}
@Deprecated
public Duration multiply(Duration other) {
return millis(millis * other.millis);
}
public Duration multiply(double n) {
return millis(millis * n);
}
public Duration divide(double n) {
return millis(millis / n);
}
@Deprecated
public Duration divide(Duration other) {
return millis(millis / other.millis);
}
public Duration negate() {
return millis(-millis);
}
public boolean isIndefinite() {
return millis == Double.POSITIVE_INFINITY;
}
public boolean isUnknown() {
return Double.isNaN(millis);
}
public boolean lessThan(Duration other) {
return millis < other.millis;
}
public boolean lessThanOrEqualTo(Duration other) {
return millis <= other.millis;
}
public boolean greaterThan(Duration other) {
return millis > other.millis;
}
public boolean greaterThanOrEqualTo(Duration other) {
return millis >= other.millis;
}
@Override public String toString() {
return isIndefinite() ? "INDEFINITE" : (isUnknown() ? "UNKNOWN" : millis + " ms");
}
@Override public int compareTo(Duration d) {
return Double.compare(millis, d.millis);
}
@Override public boolean equals(Object obj) {
return obj == this || obj instanceof Duration && millis == ((Duration) obj).millis;
}
@Override public int hashCode() {
long bits = Double.doubleToLongBits(millis);
return (int)(bits ^ (bits >>> 32));
}
}
