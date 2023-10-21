package javafx.scene.layout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.NamedArg;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
public final class BorderStrokeStyle {
private static final List<Double> DOTTED_LIST = Collections.unmodifiableList(asList(0, 2));
private static final List<Double> DASHED_LIST = Collections.unmodifiableList(asList(2, 1.4));
public static final BorderStrokeStyle NONE = new BorderStrokeStyle(
StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 0, 0, null);
public static final BorderStrokeStyle DOTTED = new BorderStrokeStyle(
StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.ROUND, 10, 0, DOTTED_LIST);
public static final BorderStrokeStyle DASHED = new BorderStrokeStyle(
StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10, 0, DASHED_LIST);
public static final BorderStrokeStyle SOLID = new BorderStrokeStyle(
StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10, 0, null);
public final StrokeType getType() { return type; }
private final StrokeType type;
public final StrokeLineJoin getLineJoin() { return lineJoin; }
private final StrokeLineJoin lineJoin;
public final StrokeLineCap getLineCap() { return lineCap; }
private final StrokeLineCap lineCap;
public final double getMiterLimit() { return miterLimit; }
private final double miterLimit;
public final double getDashOffset() { return dashOffset; }
private final double dashOffset;
public final List<Double> getDashArray() { return dashArray; }
private final List<Double> dashArray;
private final int hash;
public BorderStrokeStyle(@NamedArg("type") StrokeType type, @NamedArg("lineJoin") StrokeLineJoin lineJoin,
@NamedArg("lineCap") StrokeLineCap lineCap, @NamedArg("miterLimit") double miterLimit,
@NamedArg("dashOffset") double dashOffset, @NamedArg("dashArray") List<Double> dashArray) {
this.type = (type != null) ?
type : StrokeType.CENTERED;
this.lineJoin = (lineJoin != null) ?
lineJoin : StrokeLineJoin.MITER;
this.lineCap = (lineCap != null) ?
lineCap : StrokeLineCap.BUTT;
this.miterLimit = miterLimit;
this.dashOffset = dashOffset;
if (dashArray == null) {
this.dashArray = Collections.emptyList();
} else {
if (dashArray == DASHED_LIST || dashArray == DOTTED_LIST) {
this.dashArray = dashArray;
} else {
List<Double> list = new ArrayList<Double>(dashArray);
this.dashArray = Collections.unmodifiableList(list);
}
}
int result;
long temp;
result = this.type.hashCode();
result = 31 * result + this.lineJoin.hashCode();
result = 31 * result + this.lineCap.hashCode();
temp = this.miterLimit != +0.0d ? Double.doubleToLongBits(this.miterLimit) : 0L;
result = 31 * result + (int) (temp ^ (temp >>> 32));
temp = this.dashOffset != +0.0d ? Double.doubleToLongBits(this.dashOffset) : 0L;
result = 31 * result + (int) (temp ^ (temp >>> 32));
result = 31 * result + this.dashArray.hashCode();
hash = result;
}
@Override public String toString() {
if (this == NONE) {
return "BorderStyle.NONE";
} else if (this == DASHED) {
return "BorderStyle.DASHED";
} else if (this == DOTTED) {
return "BorderStyle.DOTTED";
} else if (this == SOLID) {
return "BorderStyle.SOLID";
} else {
StringBuilder buffer = new StringBuilder();
buffer.append("BorderStyle: ");
buffer.append(type);
buffer.append(", ");
buffer.append(lineJoin);
buffer.append(", ");
buffer.append(lineCap);
buffer.append(", ");
buffer.append(miterLimit);
buffer.append(", ");
buffer.append(dashOffset);
buffer.append(", [");
if (dashArray != null) {
buffer.append(dashArray);
}
buffer.append("]");
return buffer.toString();
}
}
@Override public boolean equals(Object o) {
if (this == o) return true;
if ((this == NONE && o != NONE) || (o == NONE && this != NONE)) return false;
if (o == null || getClass() != o.getClass()) return false;
BorderStrokeStyle that = (BorderStrokeStyle) o;
if (this.hash != that.hash) return false;
if (Double.compare(that.dashOffset, dashOffset) != 0) return false;
if (Double.compare(that.miterLimit, miterLimit) != 0) return false;
if (!dashArray.equals(that.dashArray)) return false;
if (lineCap != that.lineCap) return false;
if (lineJoin != that.lineJoin) return false;
if (type != that.type) return false;
return true;
}
@Override public int hashCode() {
return hash;
}
private static List<Double> asList(double... items) {
List<Double> list = new ArrayList<Double>(items.length);
for (int i=0; i<items.length; i++) {
list.add(items[i]);
}
return list;
}
}
