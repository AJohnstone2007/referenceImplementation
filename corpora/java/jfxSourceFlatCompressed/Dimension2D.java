package javafx.geometry;
import javafx.beans.NamedArg;
public class Dimension2D {
public Dimension2D(@NamedArg("width") double width, @NamedArg("height") double height) {
this.width = width;
this.height = height;
}
private double width;
public final double getWidth() {
return width;
}
private double height;
public final double getHeight() {
return height;
}
private int hash = 0;
@Override public boolean equals(Object obj) {
if (obj == this) return true;
if (obj instanceof Dimension2D) {
Dimension2D other = (Dimension2D) obj;
return getWidth() == other.getWidth() && getHeight() == other.getHeight();
} else return false;
}
@Override public int hashCode() {
if (hash == 0) {
long bits = 7L;
bits = 31L * bits + Double.doubleToLongBits(getWidth());
bits = 31L * bits + Double.doubleToLongBits(getHeight());
hash = (int) (bits ^ (bits >> 32));
}
return hash;
}
@Override public String toString() {
return "Dimension2D [width = " + getWidth() + ", height = " + getHeight() + "]";
}
}
