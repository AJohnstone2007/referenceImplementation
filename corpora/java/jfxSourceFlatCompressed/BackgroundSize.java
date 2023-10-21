package javafx.scene.layout;
import javafx.beans.NamedArg;
public final class BackgroundSize {
public static final double AUTO = -1;
public static final BackgroundSize DEFAULT = new BackgroundSize(AUTO, AUTO, true, true, false, false);
public final double getWidth() { return width; }
final double width;
public final double getHeight() { return height; }
final double height;
public final boolean isWidthAsPercentage() { return widthAsPercentage; }
final boolean widthAsPercentage;
public final boolean isHeightAsPercentage() { return heightAsPercentage; }
final boolean heightAsPercentage;
public final boolean isContain() { return contain; }
final boolean contain;
public final boolean isCover() { return cover; }
final boolean cover;
private final int hash;
public BackgroundSize(@NamedArg("width") double width, @NamedArg("height") double height,
@NamedArg("widthAsPercentage") boolean widthAsPercentage, @NamedArg("heightAsPercentage") boolean heightAsPercentage,
@NamedArg("contain") boolean contain, @NamedArg("cover") boolean cover) {
if (width < 0 && width != AUTO)
throw new IllegalArgumentException("Width cannot be < 0, except when AUTO");
if (height < 0 && height != AUTO)
throw new IllegalArgumentException("Height cannot be < 0, except when AUTO");
this.width = width;
this.height = height;
this.widthAsPercentage = widthAsPercentage;
this.heightAsPercentage = heightAsPercentage;
this.contain = contain;
this.cover = cover;
int result;
long temp;
result = (this.widthAsPercentage ? 1 : 0);
result = 31 * result + (this.heightAsPercentage ? 1 : 0);
temp = this.width != +0.0d ? Double.doubleToLongBits(this.width) : 0L;
result = 31 * result + (int) (temp ^ (temp >>> 32));
temp = this.height != +0.0d ? Double.doubleToLongBits(this.height) : 0L;
result = 31 * result + (int) (temp ^ (temp >>> 32));
result = 31 * result + (this.cover ? 1 : 0);
result = 31 * result + (this.contain ? 1 : 0);
hash = result;
}
@Override public boolean equals(Object o) {
if (this == o) return true;
if (o == null || getClass() != o.getClass()) return false;
BackgroundSize that = (BackgroundSize) o;
if (this.hash != that.hash) return false;
if (contain != that.contain) return false;
if (cover != that.cover) return false;
if (Double.compare(that.height, height) != 0) return false;
if (heightAsPercentage != that.heightAsPercentage) return false;
if (widthAsPercentage != that.widthAsPercentage) return false;
if (Double.compare(that.width, width) != 0) return false;
return true;
}
@Override public int hashCode() {
return hash;
}
}
