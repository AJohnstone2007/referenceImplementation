package javafx.scene.layout;
import com.sun.javafx.scene.layout.region.BorderImageSlices;
import javafx.beans.NamedArg;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
public class BorderImage {
public final Image getImage() { return image; }
final Image image;
public final BorderRepeat getRepeatX() { return repeatX; }
final BorderRepeat repeatX;
public final BorderRepeat getRepeatY() { return repeatY; }
final BorderRepeat repeatY;
public final BorderWidths getWidths() { return widths; }
final BorderWidths widths;
public final BorderWidths getSlices() { return slices; }
final BorderWidths slices;
public final boolean isFilled() { return filled; }
final boolean filled;
public final Insets getInsets() { return insets; }
final Insets insets;
final Insets innerEdge;
final Insets outerEdge;
private final int hash;
public BorderImage(
@NamedArg("image") Image image, @NamedArg("widths") BorderWidths widths, @NamedArg("insets") Insets insets, @NamedArg("slices") BorderWidths slices, @NamedArg("filled") boolean filled,
@NamedArg("repeatX") BorderRepeat repeatX, @NamedArg("repeatY") BorderRepeat repeatY) {
if (image == null) throw new NullPointerException("Image cannot be null");
this.image = image;
this.widths = widths == null ? BorderWidths.DEFAULT : widths;
this.insets = insets == null ? Insets.EMPTY : insets;
this.slices = slices == null ? BorderImageSlices.DEFAULT.widths : slices;
this.filled = filled;
this.repeatX = repeatX == null ? BorderRepeat.STRETCH : repeatX;
this.repeatY = repeatY == null ? this.repeatX : repeatY;
outerEdge = new Insets(
Math.max(0, -this.insets.getTop()),
Math.max(0, -this.insets.getRight()),
Math.max(0, -this.insets.getBottom()),
Math.max(0, -this.insets.getLeft()));
innerEdge = new Insets(
this.insets.getTop() + this.widths.getTop(),
this.insets.getRight() + this.widths.getRight(),
this.insets.getBottom() + this.widths.getBottom(),
this.insets.getLeft() + this.widths.getLeft());
int result = this.image.hashCode();
result = 31 * result + this.widths.hashCode();
result = 31 * result + this.slices.hashCode();
result = 31 * result + this.repeatX.hashCode();
result = 31 * result + this.repeatY.hashCode();
result = 31 * result + (this.filled ? 1 : 0);
hash = result;
}
@Override public boolean equals(Object o) {
if (this == o) return true;
if (o == null || getClass() != o.getClass()) return false;
BorderImage that = (BorderImage) o;
if (this.hash != that.hash) return false;
if (filled != that.filled) return false;
if (!image.equals(that.image)) return false;
if (repeatX != that.repeatX) return false;
if (repeatY != that.repeatY) return false;
if (!slices.equals(that.slices)) return false;
if (!widths.equals(that.widths)) return false;
return true;
}
@Override public int hashCode() {
return hash;
}
}
