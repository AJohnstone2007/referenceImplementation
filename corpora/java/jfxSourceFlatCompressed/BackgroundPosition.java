package javafx.scene.layout;
import javafx.beans.NamedArg;
import javafx.geometry.Side;
public class BackgroundPosition {
public static final BackgroundPosition DEFAULT = new BackgroundPosition(
Side.LEFT, 0, true, Side.TOP, 0, true);
public static final BackgroundPosition CENTER = new BackgroundPosition(
Side.LEFT, .5, true, Side.TOP, .5, true);
public final Side getHorizontalSide() { return horizontalSide; }
final Side horizontalSide;
public final Side getVerticalSide() { return verticalSide; }
final Side verticalSide;
public final double getHorizontalPosition() { return horizontalPosition; }
final double horizontalPosition;
public final double getVerticalPosition() { return verticalPosition; }
final double verticalPosition;
public final boolean isHorizontalAsPercentage() { return horizontalAsPercentage; }
final boolean horizontalAsPercentage;
public final boolean isVerticalAsPercentage() { return verticalAsPercentage; }
final boolean verticalAsPercentage;
private final int hash;
public BackgroundPosition(@NamedArg("horizontalSide") Side horizontalSide, @NamedArg("horizontalPosition") double horizontalPosition, @NamedArg("horizontalAsPercentage") boolean horizontalAsPercentage,
@NamedArg("verticalSide") Side verticalSide, @NamedArg("verticalPosition") double verticalPosition, @NamedArg("verticalAsPercentage") boolean verticalAsPercentage) {
if (horizontalSide == Side.TOP || horizontalSide == Side.BOTTOM) {
throw new IllegalArgumentException("The horizontalSide must be LEFT or RIGHT");
}
if (verticalSide == Side.LEFT || verticalSide == Side.RIGHT) {
throw new IllegalArgumentException("The verticalSide must be TOP or BOTTOM");
}
this.horizontalSide = horizontalSide == null ? Side.LEFT : horizontalSide;
this.verticalSide = verticalSide == null ? Side.TOP : verticalSide;
this.horizontalPosition = horizontalPosition;
this.verticalPosition = verticalPosition;
this.horizontalAsPercentage = horizontalAsPercentage;
this.verticalAsPercentage = verticalAsPercentage;
int result;
long temp;
result = this.horizontalSide.hashCode();
result = 31 * result + this.verticalSide.hashCode();
temp = this.horizontalPosition != +0.0d ? Double.doubleToLongBits(this.horizontalPosition) : 0L;
result = 31 * result + (int) (temp ^ (temp >>> 32));
temp = this.verticalPosition != +0.0d ? Double.doubleToLongBits(this.verticalPosition) : 0L;
result = 31 * result + (int) (temp ^ (temp >>> 32));
result = 31 * result + (this.horizontalAsPercentage ? 1 : 0);
result = 31 * result + (this.verticalAsPercentage ? 1 : 0);
hash = result;
}
@Override public boolean equals(Object o) {
if (this == o) return true;
if (o == null || getClass() != o.getClass()) return false;
BackgroundPosition that = (BackgroundPosition) o;
if (hash != that.hash) return false;
if (horizontalAsPercentage != that.horizontalAsPercentage) return false;
if (Double.compare(that.horizontalPosition, horizontalPosition) != 0) return false;
if (verticalAsPercentage != that.verticalAsPercentage) return false;
if (Double.compare(that.verticalPosition, verticalPosition) != 0) return false;
if (horizontalSide != that.horizontalSide) return false;
if (verticalSide != that.verticalSide) return false;
return true;
}
@Override public int hashCode() {
return hash;
}
}
