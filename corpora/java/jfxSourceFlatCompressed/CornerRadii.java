package javafx.scene.layout;
import javafx.beans.NamedArg;
public class CornerRadii {
public static final CornerRadii EMPTY = new CornerRadii(
0, 0, 0, 0, 0, 0, 0, 0,
false, false, false, false, false, false, false, false
);
public final double getTopLeftHorizontalRadius() { return topLeftHorizontalRadius; }
private double topLeftHorizontalRadius;
public final double getTopLeftVerticalRadius() { return topLeftVerticalRadius; }
private double topLeftVerticalRadius;
public final double getTopRightVerticalRadius() { return topRightVerticalRadius; }
private double topRightVerticalRadius;
public final double getTopRightHorizontalRadius() { return topRightHorizontalRadius; }
private double topRightHorizontalRadius;
public final double getBottomRightHorizontalRadius() { return bottomRightHorizontalRadius; }
private double bottomRightHorizontalRadius;
public final double getBottomRightVerticalRadius() { return bottomRightVerticalRadius; }
private double bottomRightVerticalRadius;
public final double getBottomLeftVerticalRadius() { return bottomLeftVerticalRadius; }
private double bottomLeftVerticalRadius;
public final double getBottomLeftHorizontalRadius() { return bottomLeftHorizontalRadius; }
private double bottomLeftHorizontalRadius;
public final boolean isTopLeftHorizontalRadiusAsPercentage() { return topLeftHorizontalRadiusAsPercentage; }
private final boolean topLeftHorizontalRadiusAsPercentage;
public final boolean isTopLeftVerticalRadiusAsPercentage() { return topLeftVerticalRadiusAsPercentage; }
private final boolean topLeftVerticalRadiusAsPercentage;
public final boolean isTopRightVerticalRadiusAsPercentage() { return topRightVerticalRadiusAsPercentage; }
private final boolean topRightVerticalRadiusAsPercentage;
public final boolean isTopRightHorizontalRadiusAsPercentage() { return topRightHorizontalRadiusAsPercentage; }
private final boolean topRightHorizontalRadiusAsPercentage;
public final boolean isBottomRightHorizontalRadiusAsPercentage() { return bottomRightHorizontalRadiusAsPercentage; }
private final boolean bottomRightHorizontalRadiusAsPercentage;
public final boolean isBottomRightVerticalRadiusAsPercentage() { return bottomRightVerticalRadiusAsPercentage; }
private final boolean bottomRightVerticalRadiusAsPercentage;
public final boolean isBottomLeftVerticalRadiusAsPercentage() { return bottomLeftVerticalRadiusAsPercentage; }
private final boolean bottomLeftVerticalRadiusAsPercentage;
public final boolean isBottomLeftHorizontalRadiusAsPercentage() { return bottomLeftHorizontalRadiusAsPercentage; }
private final boolean bottomLeftHorizontalRadiusAsPercentage;
final boolean hasPercentBasedRadii;
public final boolean isUniform() { return uniform; }
final boolean uniform;
private final int hash;
public CornerRadii(@NamedArg("radius") double radius) {
if (radius < 0) {
throw new IllegalArgumentException("The radii value may not be < 0");
}
this.topLeftHorizontalRadius = this.topLeftVerticalRadius =
this.topRightVerticalRadius = this.topRightHorizontalRadius =
this.bottomRightHorizontalRadius = this.bottomRightVerticalRadius =
this.bottomLeftVerticalRadius = this.bottomLeftHorizontalRadius = radius;
this.topLeftHorizontalRadiusAsPercentage = this.topLeftVerticalRadiusAsPercentage =
this.topRightVerticalRadiusAsPercentage = this.topRightHorizontalRadiusAsPercentage =
this.bottomRightHorizontalRadiusAsPercentage = this.bottomRightVerticalRadiusAsPercentage =
this.bottomLeftVerticalRadiusAsPercentage = this.bottomLeftHorizontalRadiusAsPercentage = false;
hasPercentBasedRadii = false;
uniform = true;
this.hash = preComputeHash();
}
public CornerRadii(@NamedArg("radius") double radius, @NamedArg("asPercent") boolean asPercent) {
if (radius < 0) {
throw new IllegalArgumentException("The radii value may not be < 0");
}
this.topLeftHorizontalRadius = this.topLeftVerticalRadius =
this.topRightVerticalRadius = this.topRightHorizontalRadius =
this.bottomRightHorizontalRadius = this.bottomRightVerticalRadius =
this.bottomLeftVerticalRadius = this.bottomLeftHorizontalRadius = radius;
this.topLeftHorizontalRadiusAsPercentage = this.topLeftVerticalRadiusAsPercentage =
this.topRightVerticalRadiusAsPercentage = this.topRightHorizontalRadiusAsPercentage =
this.bottomRightHorizontalRadiusAsPercentage = this.bottomRightVerticalRadiusAsPercentage =
this.bottomLeftVerticalRadiusAsPercentage = this.bottomLeftHorizontalRadiusAsPercentage = asPercent;
uniform = true;
hasPercentBasedRadii = asPercent;
this.hash = preComputeHash();
}
public CornerRadii(@NamedArg("topLeft") double topLeft, @NamedArg("topRight") double topRight, @NamedArg("bottomRight") double bottomRight, @NamedArg("bottomLeft") double bottomLeft, @NamedArg("asPercent") boolean asPercent) {
if (topLeft < 0 || topRight < 0 || bottomRight < 0 || bottomLeft < 0) {
throw new IllegalArgumentException("No radii value may be < 0");
}
this.topLeftHorizontalRadius = this.topLeftVerticalRadius = topLeft;
this.topRightVerticalRadius = this.topRightHorizontalRadius = topRight;
this.bottomRightHorizontalRadius = this.bottomRightVerticalRadius = bottomRight;
this.bottomLeftVerticalRadius = this.bottomLeftHorizontalRadius = bottomLeft;
this.topLeftHorizontalRadiusAsPercentage = this.topLeftVerticalRadiusAsPercentage =
this.topRightVerticalRadiusAsPercentage = this.topRightHorizontalRadiusAsPercentage =
this.bottomRightHorizontalRadiusAsPercentage = this.bottomRightVerticalRadiusAsPercentage =
this.bottomLeftVerticalRadiusAsPercentage = this.bottomLeftHorizontalRadiusAsPercentage = asPercent;
uniform = topLeft == topRight && topLeft == bottomLeft && topLeft == bottomRight;
hasPercentBasedRadii = asPercent;
this.hash = preComputeHash();
}
public CornerRadii(
@NamedArg("topLeftHorizontalRadius") double topLeftHorizontalRadius, @NamedArg("topLeftVerticalRadius") double topLeftVerticalRadius, @NamedArg("topRightVerticalRadius") double topRightVerticalRadius, @NamedArg("topRightHorizontalRadius") double topRightHorizontalRadius,
@NamedArg("bottomRightHorizontalRadius") double bottomRightHorizontalRadius, @NamedArg("bottomRightVerticalRadius") double bottomRightVerticalRadius, @NamedArg("bottomLeftVerticalRadius") double bottomLeftVerticalRadius, @NamedArg("bottomLeftHorizontalRadius") double bottomLeftHorizontalRadius,
@NamedArg("topLeftHorizontalRadiusAsPercent") boolean topLeftHorizontalRadiusAsPercent, @NamedArg("topLeftVerticalRadiusAsPercent") boolean topLeftVerticalRadiusAsPercent, @NamedArg("topRightVerticalRadiusAsPercent") boolean topRightVerticalRadiusAsPercent,
@NamedArg("topRightHorizontalRadiusAsPercent") boolean topRightHorizontalRadiusAsPercent, @NamedArg("bottomRightHorizontalRadiusAsPercent") boolean bottomRightHorizontalRadiusAsPercent, @NamedArg("bottomRightVerticalRadiusAsPercent") boolean bottomRightVerticalRadiusAsPercent,
@NamedArg("bottomLeftVerticalRadiusAsPercent") boolean bottomLeftVerticalRadiusAsPercent, @NamedArg("bottomLeftHorizontalRadiusAsPercent") boolean bottomLeftHorizontalRadiusAsPercent)
{
if (topLeftHorizontalRadius < 0 || topLeftVerticalRadius < 0 ||
topRightVerticalRadius < 0 || topRightHorizontalRadius < 0 ||
bottomRightHorizontalRadius < 0 || bottomRightVerticalRadius < 0 ||
bottomLeftVerticalRadius < 0 || bottomLeftHorizontalRadius < 0) {
throw new IllegalArgumentException("No radii value may be < 0");
}
this.topLeftHorizontalRadius = topLeftHorizontalRadius;
this.topLeftVerticalRadius = topLeftVerticalRadius;
this.topRightVerticalRadius = topRightVerticalRadius;
this.topRightHorizontalRadius = topRightHorizontalRadius;
this.bottomRightHorizontalRadius = bottomRightHorizontalRadius;
this.bottomRightVerticalRadius = bottomRightVerticalRadius;
this.bottomLeftVerticalRadius = bottomLeftVerticalRadius;
this.bottomLeftHorizontalRadius = bottomLeftHorizontalRadius;
this.topLeftHorizontalRadiusAsPercentage = topLeftHorizontalRadiusAsPercent;
this.topLeftVerticalRadiusAsPercentage = topLeftVerticalRadiusAsPercent;
this.topRightVerticalRadiusAsPercentage = topRightVerticalRadiusAsPercent;
this.topRightHorizontalRadiusAsPercentage = topRightHorizontalRadiusAsPercent;
this.bottomRightHorizontalRadiusAsPercentage = bottomRightHorizontalRadiusAsPercent;
this.bottomRightVerticalRadiusAsPercentage = bottomRightVerticalRadiusAsPercent;
this.bottomLeftVerticalRadiusAsPercentage = bottomLeftVerticalRadiusAsPercent;
this.bottomLeftHorizontalRadiusAsPercentage = bottomLeftHorizontalRadiusAsPercent;
this.hash = preComputeHash();
hasPercentBasedRadii = topLeftHorizontalRadiusAsPercent || topLeftVerticalRadiusAsPercent ||
topRightVerticalRadiusAsPercent || topRightHorizontalRadiusAsPercent ||
bottomRightHorizontalRadiusAsPercent || bottomRightVerticalRadiusAsPercent ||
bottomLeftVerticalRadiusAsPercent || bottomLeftHorizontalRadiusAsPercent;
uniform = topLeftHorizontalRadius == topRightHorizontalRadius &&
topLeftVerticalRadius == topRightVerticalRadius &&
topLeftHorizontalRadius == bottomRightHorizontalRadius &&
topLeftVerticalRadius == bottomRightVerticalRadius &&
topLeftHorizontalRadius == bottomLeftHorizontalRadius &&
topLeftVerticalRadius == bottomLeftVerticalRadius &&
topLeftHorizontalRadiusAsPercent == topRightHorizontalRadiusAsPercent &&
topLeftVerticalRadiusAsPercent == topRightVerticalRadiusAsPercent &&
topLeftHorizontalRadiusAsPercent == bottomRightHorizontalRadiusAsPercent &&
topLeftVerticalRadiusAsPercent == bottomRightVerticalRadiusAsPercent &&
topLeftHorizontalRadiusAsPercent == bottomLeftHorizontalRadiusAsPercent &&
topLeftVerticalRadiusAsPercent == bottomLeftVerticalRadiusAsPercent;
}
private int preComputeHash() {
int result;
long temp;
temp = topLeftHorizontalRadius != +0.0d ? Double.doubleToLongBits(topLeftHorizontalRadius) : 0L;
result = (int) (temp ^ (temp >>> 32));
temp = topLeftVerticalRadius != +0.0d ? Double.doubleToLongBits(topLeftVerticalRadius) : 0L;
result = 31 * result + (int) (temp ^ (temp >>> 32));
temp = topRightVerticalRadius != +0.0d ? Double.doubleToLongBits(topRightVerticalRadius) : 0L;
result = 31 * result + (int) (temp ^ (temp >>> 32));
temp = topRightHorizontalRadius != +0.0d ? Double.doubleToLongBits(topRightHorizontalRadius) : 0L;
result = 31 * result + (int) (temp ^ (temp >>> 32));
temp = bottomRightHorizontalRadius != +0.0d ? Double.doubleToLongBits(bottomRightHorizontalRadius) : 0L;
result = 31 * result + (int) (temp ^ (temp >>> 32));
temp = bottomRightVerticalRadius != +0.0d ? Double.doubleToLongBits(bottomRightVerticalRadius) : 0L;
result = 31 * result + (int) (temp ^ (temp >>> 32));
temp = bottomLeftVerticalRadius != +0.0d ? Double.doubleToLongBits(bottomLeftVerticalRadius) : 0L;
result = 31 * result + (int) (temp ^ (temp >>> 32));
temp = bottomLeftHorizontalRadius != +0.0d ? Double.doubleToLongBits(bottomLeftHorizontalRadius) : 0L;
result = 31 * result + (int) (temp ^ (temp >>> 32));
result = 31 * result + (topLeftHorizontalRadiusAsPercentage ? 1 : 0);
result = 31 * result + (topLeftVerticalRadiusAsPercentage ? 1 : 0);
result = 31 * result + (topRightVerticalRadiusAsPercentage ? 1 : 0);
result = 31 * result + (topRightHorizontalRadiusAsPercentage ? 1 : 0);
result = 31 * result + (bottomRightHorizontalRadiusAsPercentage ? 1 : 0);
result = 31 * result + (bottomRightVerticalRadiusAsPercentage ? 1 : 0);
result = 31 * result + (bottomLeftVerticalRadiusAsPercentage ? 1 : 0);
result = 31 * result + (bottomLeftHorizontalRadiusAsPercentage ? 1 : 0);
result = 31 * result + result;
return result;
}
@Override public boolean equals(Object o) {
if (this == o) return true;
if (o == null || getClass() != o.getClass()) return false;
CornerRadii that = (CornerRadii) o;
if (this.hash != that.hash) return false;
if (Double.compare(that.bottomLeftHorizontalRadius, bottomLeftHorizontalRadius) != 0) return false;
if (bottomLeftHorizontalRadiusAsPercentage != that.bottomLeftHorizontalRadiusAsPercentage) return false;
if (Double.compare(that.bottomLeftVerticalRadius, bottomLeftVerticalRadius) != 0) return false;
if (bottomLeftVerticalRadiusAsPercentage != that.bottomLeftVerticalRadiusAsPercentage) return false;
if (Double.compare(that.bottomRightVerticalRadius, bottomRightVerticalRadius) != 0) return false;
if (bottomRightVerticalRadiusAsPercentage != that.bottomRightVerticalRadiusAsPercentage) return false;
if (Double.compare(that.bottomRightHorizontalRadius, bottomRightHorizontalRadius) != 0) return false;
if (bottomRightHorizontalRadiusAsPercentage != that.bottomRightHorizontalRadiusAsPercentage) return false;
if (Double.compare(that.topLeftVerticalRadius, topLeftVerticalRadius) != 0) return false;
if (topLeftVerticalRadiusAsPercentage != that.topLeftVerticalRadiusAsPercentage) return false;
if (Double.compare(that.topLeftHorizontalRadius, topLeftHorizontalRadius) != 0) return false;
if (topLeftHorizontalRadiusAsPercentage != that.topLeftHorizontalRadiusAsPercentage) return false;
if (Double.compare(that.topRightHorizontalRadius, topRightHorizontalRadius) != 0) return false;
if (topRightHorizontalRadiusAsPercentage != that.topRightHorizontalRadiusAsPercentage) return false;
if (Double.compare(that.topRightVerticalRadius, topRightVerticalRadius) != 0) return false;
if (topRightVerticalRadiusAsPercentage != that.topRightVerticalRadiusAsPercentage) return false;
return true;
}
@Override public int hashCode() {
return hash;
}
@Override public String toString() {
if (isUniform()) {
return "CornerRadii [uniform radius = " + topLeftHorizontalRadius + "]";
}
return "CornerRadii [" +
(topLeftHorizontalRadius == topLeftVerticalRadius ?
"topLeft=" + topLeftHorizontalRadius :
"topLeftHorizontalRadius=" + topLeftHorizontalRadius +
", topLeftVerticalRadius=" + topLeftVerticalRadius) +
(topRightHorizontalRadius == topRightVerticalRadius ?
", topRight=" + topRightHorizontalRadius :
", topRightVerticalRadius=" + topRightVerticalRadius +
", topRightHorizontalRadius=" + topRightHorizontalRadius) +
(bottomRightHorizontalRadius == bottomRightVerticalRadius ?
", bottomRight=" + bottomRightHorizontalRadius :
", bottomRightHorizontalRadius=" + bottomRightHorizontalRadius +
", bottomRightVerticalRadius=" + bottomRightVerticalRadius) +
(bottomLeftHorizontalRadius == bottomLeftVerticalRadius ?
", bottomLeft=" + bottomLeftHorizontalRadius :
", bottomLeftVerticalRadius=" + bottomLeftVerticalRadius +
", bottomLeftHorizontalRadius=" + bottomLeftHorizontalRadius) +
']';
}
}
