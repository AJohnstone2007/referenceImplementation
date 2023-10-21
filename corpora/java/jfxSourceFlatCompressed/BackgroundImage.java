package javafx.scene.layout;
import javafx.beans.NamedArg;
import javafx.scene.image.Image;
public final class BackgroundImage {
public final Image getImage() { return image; }
final Image image;
public final BackgroundRepeat getRepeatX() { return repeatX; }
final BackgroundRepeat repeatX;
public final BackgroundRepeat getRepeatY() { return repeatY; }
final BackgroundRepeat repeatY;
public final BackgroundPosition getPosition() { return position; }
final BackgroundPosition position;
public final BackgroundSize getSize() { return size; }
final BackgroundSize size;
Boolean opaque = null;
private final int hash;
public BackgroundImage(
@NamedArg("image") Image image, @NamedArg("repeatX") BackgroundRepeat repeatX, @NamedArg("repeatY") BackgroundRepeat repeatY,
@NamedArg("position") BackgroundPosition position, @NamedArg("size") BackgroundSize size)
{
if (image == null) throw new NullPointerException("Image cannot be null");
this.image = image;
this.repeatX = repeatX == null ? BackgroundRepeat.REPEAT : repeatX;
this.repeatY = repeatY == null ? BackgroundRepeat.REPEAT : repeatY;
this.position = position == null ? BackgroundPosition.DEFAULT : position;
this.size = size == null ? BackgroundSize.DEFAULT : size;
int result = this.image.hashCode();
result = 31 * result + this.repeatX.hashCode();
result = 31 * result + this.repeatY.hashCode();
result = 31 * result + this.position.hashCode();
result = 31 * result + this.size.hashCode();
hash = result;
}
@Override public boolean equals(Object o) {
if (this == o) return true;
if (o == null || getClass() != o.getClass()) return false;
BackgroundImage that = (BackgroundImage) o;
if (hash != that.hash) return false;
if (!image.equals(that.image)) return false;
if (!position.equals(that.position)) return false;
if (repeatX != that.repeatX) return false;
if (repeatY != that.repeatY) return false;
if (!size.equals(that.size)) return false;
return true;
}
@Override public int hashCode() {
return hash;
}
}
