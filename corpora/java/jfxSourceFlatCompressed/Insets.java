package javafx.geometry;
import javafx.beans.NamedArg;
public class Insets {
public static final Insets EMPTY = new Insets(0, 0, 0, 0);
public final double getTop() { return top; }
private double top;
public final double getRight() { return right; }
private double right;
public final double getBottom() { return bottom; }
private double bottom;
public final double getLeft() { return left; }
private double left;
private int hash = 0;
public Insets(@NamedArg("top") double top, @NamedArg("right") double right, @NamedArg("bottom") double bottom, @NamedArg("left") double left) {
this.top = top;
this.right = right;
this.bottom = bottom;
this.left = left;
}
public Insets(@NamedArg("topRightBottomLeft") double topRightBottomLeft) {
this.top = topRightBottomLeft;
this.right = topRightBottomLeft;
this.bottom = topRightBottomLeft;
this.left = topRightBottomLeft;
}
@Override public boolean equals(Object obj) {
if (obj == this) return true;
if (obj instanceof Insets) {
Insets other = (Insets) obj;
return top == other.top
&& right == other.right
&& bottom == other.bottom
&& left == other.left;
} else return false;
}
@Override public int hashCode() {
if (hash == 0) {
long bits = 17L;
bits = 37L * bits + Double.doubleToLongBits(top);
bits = 37L * bits + Double.doubleToLongBits(right);
bits = 37L * bits + Double.doubleToLongBits(bottom);
bits = 37L * bits + Double.doubleToLongBits(left);
hash = (int) (bits ^ (bits >> 32));
}
return hash;
}
@Override public String toString() {
return "Insets [top=" + top + ", right=" + right + ", bottom="
+ bottom + ", left=" + left + "]";
}
}
