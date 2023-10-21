package javafx.scene.paint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javafx.beans.NamedArg;
public final class Stop {
static final List<Stop> NO_STOPS = List.of(
new Stop(0.0, Color.TRANSPARENT),
new Stop(1.0, Color.TRANSPARENT));
static List<Stop> normalize(Stop stops[]) {
List<Stop> stoplist = (stops == null ? null : Arrays.asList(stops));
return normalize(stoplist);
}
static List<Stop> normalize(List<Stop> stops) {
if (stops == null) {
return NO_STOPS;
}
Stop zerostop = null;
Stop onestop = null;
List<Stop> newlist = new ArrayList<Stop>(stops.size());
for (Stop s : stops) {
if (s == null || s.getColor() == null) continue;
double off = s.getOffset();
if (off <= 0.0) {
if (zerostop == null || off >= zerostop.getOffset()) {
zerostop = s;
}
} else if (off >= 1.0) {
if (onestop == null || off < onestop.getOffset()) {
onestop = s;
}
} else if (off == off) {
for (int i = newlist.size() - 1; i >= 0; i--) {
Stop s2 = newlist.get(i);
if (s2.getOffset() <= off) {
if (s2.getOffset() == off) {
if (i > 0 && newlist.get(i-1).getOffset() == off) {
newlist.set(i, s);
} else {
newlist.add(i+1, s);
}
} else {
newlist.add(i+1, s);
}
s = null;
break;
}
}
if (s != null) {
newlist.add(0, s);
}
}
}
if (zerostop == null) {
Color zerocolor;
if (newlist.isEmpty()) {
if (onestop == null) {
return NO_STOPS;
}
zerocolor = onestop.getColor();
} else {
zerocolor = newlist.get(0).getColor();
if (onestop == null && newlist.size() == 1) {
newlist.clear();
}
}
zerostop = new Stop(0.0, zerocolor);
} else if (zerostop.getOffset() < 0.0) {
zerostop = new Stop(0.0, zerostop.getColor());
}
newlist.add(0, zerostop);
if (onestop == null) {
onestop = new Stop(1.0, newlist.get(newlist.size()-1).getColor());
} else if (onestop.getOffset() > 1.0) {
onestop = new Stop(1.0, onestop.getColor());
}
newlist.add(onestop);
return Collections.unmodifiableList(newlist);
}
private double offset;
public final double getOffset() {
return offset;
}
private Color color;
public final Color getColor() {
return color;
}
private int hash = 0;
public Stop(@NamedArg("offset") double offset, @NamedArg(value="color", defaultValue="BLACK") Color color) {
this.offset = offset;
this.color = color;
}
@Override public boolean equals(Object obj) {
if (obj == null) return false;
if (obj == this) return true;
if (obj instanceof Stop) {
Stop other = (Stop) obj;
return offset == other.offset &&
(color == null ? other.color == null : color.equals(other.color));
} else return false;
}
@Override public int hashCode() {
if (hash == 0) {
long bits = 17L;
bits = 37L * bits + Double.doubleToLongBits(offset);
bits = 37L * bits + color.hashCode();
hash = (int) (bits ^ (bits >> 32));
}
return hash;
}
@Override public String toString() {
return color + " " + offset*100 + "%";
}
}
