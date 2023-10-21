package javafx.scene.layout;
import javafx.beans.NamedArg;
public final class BorderWidths {
public static final double AUTO = -1;
public static final BorderWidths DEFAULT = new BorderWidths(1, 1, 1, 1, false, false, false, false);
public static final BorderWidths EMPTY = new BorderWidths(0, 0, 0, 0, false, false, false, false);
public static final BorderWidths FULL = new BorderWidths(1d, 1d, 1d, 1d, true, true, true, true);
public final double getTop() { return top; }
final double top;
public final double getRight() { return right; }
final double right;
public final double getBottom() { return bottom; }
final double bottom;
public final double getLeft() { return left; }
final double left;
public final boolean isTopAsPercentage() { return topAsPercentage; }
final boolean topAsPercentage;
public final boolean isRightAsPercentage() { return rightAsPercentage; }
final boolean rightAsPercentage;
public final boolean isBottomAsPercentage() { return bottomAsPercentage; }
final boolean bottomAsPercentage;
public final boolean isLeftAsPercentage() { return leftAsPercentage; }
final boolean leftAsPercentage;
private final int hash;
public BorderWidths(@NamedArg("width") double width) {
this(width, width, width, width, false, false, false, false);
}
public BorderWidths(@NamedArg("top") double top, @NamedArg("right") double right, @NamedArg("bottom") double bottom, @NamedArg("left") double left) {
this(top, right, bottom, left, false, false, false, false);
}
public BorderWidths(
@NamedArg("top") double top, @NamedArg("right") double right, @NamedArg("bottom") double bottom, @NamedArg("left") double left, @NamedArg("topAsPercentage") boolean topAsPercentage,
@NamedArg("rightAsPercentage") boolean rightAsPercentage, @NamedArg("bottomAsPercentage") boolean bottomAsPercentage, @NamedArg("leftAsPercentage") boolean leftAsPercentage) {
if ((top != AUTO && top < 0) ||
(right != AUTO && right < 0) ||
(bottom != AUTO && bottom < 0) ||
(left != AUTO && left < 0)) {
throw new IllegalArgumentException("None of the widths can be < 0");
}
this.top = top;
this.right = right;
this.bottom = bottom;
this.left = left;
this.topAsPercentage = topAsPercentage;
this.rightAsPercentage = rightAsPercentage;
this.bottomAsPercentage = bottomAsPercentage;
this.leftAsPercentage = leftAsPercentage;
int result;
long temp;
temp = this.top != +0.0d ? Double.doubleToLongBits(this.top) : 0L;
result = (int) (temp ^ (temp >>> 32));
temp = this.right != +0.0d ? Double.doubleToLongBits(this.right) : 0L;
result = 31 * result + (int) (temp ^ (temp >>> 32));
temp = this.bottom != +0.0d ? Double.doubleToLongBits(this.bottom) : 0L;
result = 31 * result + (int) (temp ^ (temp >>> 32));
temp = this.left != +0.0d ? Double.doubleToLongBits(this.left) : 0L;
result = 31 * result + (int) (temp ^ (temp >>> 32));
result = 31 * result + (this.topAsPercentage ? 1 : 0);
result = 31 * result + (this.rightAsPercentage ? 1 : 0);
result = 31 * result + (this.bottomAsPercentage ? 1 : 0);
result = 31 * result + (this.leftAsPercentage ? 1 : 0);
hash = result;
}
@Override public boolean equals(Object o) {
if (this == o) return true;
if (o == null || getClass() != o.getClass()) return false;
BorderWidths that = (BorderWidths) o;
if (this.hash != that.hash) return false;
if (Double.compare(that.bottom, bottom) != 0) return false;
if (bottomAsPercentage != that.bottomAsPercentage) return false;
if (Double.compare(that.left, left) != 0) return false;
if (leftAsPercentage != that.leftAsPercentage) return false;
if (Double.compare(that.right, right) != 0) return false;
if (rightAsPercentage != that.rightAsPercentage) return false;
if (Double.compare(that.top, top) != 0) return false;
if (topAsPercentage != that.topAsPercentage) return false;
return true;
}
@Override public int hashCode() {
return hash;
}
}
