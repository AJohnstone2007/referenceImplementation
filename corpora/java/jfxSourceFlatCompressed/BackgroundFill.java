package javafx.scene.layout;
import javafx.beans.NamedArg;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
public final class BackgroundFill {
public final Paint getFill() { return fill; }
final Paint fill;
public final CornerRadii getRadii() { return radii; }
final CornerRadii radii;
public final Insets getInsets() { return insets; }
final Insets insets;
private final int hash;
public BackgroundFill(@NamedArg("fill") Paint fill, @NamedArg("radii") CornerRadii radii, @NamedArg("insets") Insets insets) {
this.fill = fill == null ? Color.TRANSPARENT : fill;
this.radii = radii == null ? CornerRadii.EMPTY : radii;
this.insets = insets == null ? Insets.EMPTY : insets;
int result = this.fill.hashCode();
result = 31 * result + this.radii.hashCode();
result = 31 * result + this.insets.hashCode();
hash = result;
}
@Override public boolean equals(Object o) {
if (this == o) return true;
if (o == null || getClass() != o.getClass()) return false;
BackgroundFill that = (BackgroundFill) o;
if (hash != that.hash) return false;
if (!fill.equals(that.fill)) return false;
if (!insets.equals(that.insets)) return false;
if (!radii.equals(that.radii)) return false;
return true;
}
@Override public int hashCode() {
return hash;
}
}
