package javafx.scene.layout;
import javafx.beans.NamedArg;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeType;
public class BorderStroke {
public static final BorderWidths THIN = new BorderWidths(1);
public static final BorderWidths MEDIUM = new BorderWidths(3);
public static final BorderWidths THICK = new BorderWidths(5);
public static final BorderWidths DEFAULT_WIDTHS = THIN;
public final Paint getTopStroke() { return topStroke; }
final Paint topStroke;
public final Paint getRightStroke() { return rightStroke; }
final Paint rightStroke;
public final Paint getBottomStroke() { return bottomStroke; }
final Paint bottomStroke;
public final Paint getLeftStroke() { return leftStroke; }
final Paint leftStroke;
public final BorderStrokeStyle getTopStyle() { return topStyle; }
final BorderStrokeStyle topStyle;
public final BorderStrokeStyle getRightStyle() { return rightStyle; }
final BorderStrokeStyle rightStyle;
public final BorderStrokeStyle getBottomStyle() { return bottomStyle; }
final BorderStrokeStyle bottomStyle;
public final BorderStrokeStyle getLeftStyle() { return leftStyle; }
final BorderStrokeStyle leftStyle;
public final BorderWidths getWidths() { return widths; }
final BorderWidths widths;
public final Insets getInsets() { return insets; }
final Insets insets;
final Insets innerEdge;
final Insets outerEdge;
public final CornerRadii getRadii() { return radii; }
private final CornerRadii radii;
public final boolean isStrokeUniform() { return strokeUniform; }
private final boolean strokeUniform;
private final int hash;
public BorderStroke(@NamedArg("stroke") Paint stroke, @NamedArg("style") BorderStrokeStyle style, @NamedArg("radii") CornerRadii radii, @NamedArg("widths") BorderWidths widths) {
this.leftStroke = this.topStroke = this.rightStroke = this.bottomStroke = stroke == null ? Color.BLACK : stroke;
this.topStyle = this.rightStyle = this.bottomStyle = this.leftStyle = style == null ? BorderStrokeStyle.NONE : style;
this.radii = radii == null ? CornerRadii.EMPTY : radii;
this.widths = widths == null ? DEFAULT_WIDTHS : widths;
this.insets = Insets.EMPTY;
strokeUniform = this.widths.left == this.widths.top &&
this.widths.left == this.widths.right &&
this.widths.left == this.widths.bottom;
innerEdge = new Insets(
computeInside(this.topStyle.getType(), this.widths.getTop()),
computeInside(this.rightStyle.getType(), this.widths.getRight()),
computeInside(this.bottomStyle.getType(), this.widths.getBottom()),
computeInside(this.leftStyle.getType(), this.widths.getLeft())
);
outerEdge = new Insets(
Math.max(0, computeOutside(this.topStyle.getType(), this.widths.getTop())),
Math.max(0, computeOutside(this.rightStyle.getType(), this.widths.getRight())),
Math.max(0, computeOutside(this.bottomStyle.getType(), this.widths.getBottom())),
Math.max(0, computeOutside(this.leftStyle.getType(), this.widths.getLeft()))
);
this.hash = preComputeHash();
}
public BorderStroke(@NamedArg("stroke") Paint stroke, @NamedArg("style") BorderStrokeStyle style, @NamedArg("radii") CornerRadii radii, @NamedArg("widths") BorderWidths widths, @NamedArg("insets") Insets insets) {
this(stroke, stroke, stroke, stroke, style, style, style, style, radii, widths, insets);
}
public BorderStroke(
@NamedArg("topStroke") Paint topStroke, @NamedArg("rightStroke") Paint rightStroke, @NamedArg("bottomStroke") Paint bottomStroke, @NamedArg("leftStroke") Paint leftStroke,
@NamedArg("topStyle") BorderStrokeStyle topStyle, @NamedArg("rightStyle") BorderStrokeStyle rightStyle,
@NamedArg("bottomStyle") BorderStrokeStyle bottomStyle, @NamedArg("leftStyle") BorderStrokeStyle leftStyle,
@NamedArg("radii") CornerRadii radii, @NamedArg("widths") BorderWidths widths, @NamedArg("insets") Insets insets)
{
this.topStroke = topStroke == null ? Color.BLACK : topStroke;
this.rightStroke = rightStroke == null ? this.topStroke : rightStroke;
this.bottomStroke = bottomStroke == null ? this.topStroke : bottomStroke;
this.leftStroke = leftStroke == null ? this.rightStroke : leftStroke;
this.topStyle = topStyle == null ? BorderStrokeStyle.NONE : topStyle;
this.rightStyle = rightStyle == null ? this.topStyle : rightStyle;
this.bottomStyle = bottomStyle == null ? this.topStyle : bottomStyle;
this.leftStyle = leftStyle == null ? this.rightStyle : leftStyle;
this.radii = radii == null ? CornerRadii.EMPTY : radii;
this.widths = widths == null ? DEFAULT_WIDTHS : widths;
this.insets = insets == null ? Insets.EMPTY : insets;
final boolean colorsSame =
this.leftStroke.equals(this.topStroke) &&
this.leftStroke.equals(this.rightStroke) &&
this.leftStroke.equals(this.bottomStroke);
final boolean widthsSame =
this.widths.left == this.widths.top &&
this.widths.left == this.widths.right &&
this.widths.left == this.widths.bottom;
final boolean stylesSame =
this.leftStyle.equals(this.topStyle) &&
this.leftStyle.equals(this.rightStyle) &&
this.leftStyle.equals(this.bottomStyle);
strokeUniform = colorsSame && widthsSame && stylesSame;
innerEdge = new Insets(
this.insets.getTop() + computeInside(this.topStyle.getType(), this.widths.getTop()),
this.insets.getRight() + computeInside(this.rightStyle.getType(), this.widths.getRight()),
this.insets.getBottom() + computeInside(this.bottomStyle.getType(), this.widths.getBottom()),
this.insets.getLeft() + computeInside(this.leftStyle.getType(), this.widths.getLeft())
);
outerEdge = new Insets(
Math.max(0, computeOutside(this.topStyle.getType(), this.widths.getTop()) - this.insets.getTop()),
Math.max(0, computeOutside(this.rightStyle.getType(), this.widths.getRight()) - this.insets.getRight()),
Math.max(0, computeOutside(this.bottomStyle.getType(), this.widths.getBottom())- this.insets.getBottom()),
Math.max(0, computeOutside(this.leftStyle.getType(), this.widths.getLeft()) - this.insets.getLeft())
);
this.hash = preComputeHash();
}
private int preComputeHash() {
int result;
result = topStroke.hashCode();
result = 31 * result + rightStroke.hashCode();
result = 31 * result + bottomStroke.hashCode();
result = 31 * result + leftStroke.hashCode();
result = 31 * result + topStyle.hashCode();
result = 31 * result + rightStyle.hashCode();
result = 31 * result + bottomStyle.hashCode();
result = 31 * result + leftStyle.hashCode();
result = 31 * result + widths.hashCode();
result = 31 * result + radii.hashCode();
result = 31 * result + insets.hashCode();
return result;
}
private double computeInside(StrokeType type, double width) {
if (type == StrokeType.OUTSIDE) {
return 0;
} else if (type == StrokeType.CENTERED) {
return width / 2.0;
} else if (type == StrokeType.INSIDE) {
return width;
} else {
throw new AssertionError("Unexpected Stroke Type");
}
}
private double computeOutside(StrokeType type, double width) {
if (type == StrokeType.OUTSIDE) {
return width;
} else if (type == StrokeType.CENTERED) {
return width / 2.0;
} else if (type == StrokeType.INSIDE) {
return 0;
} else {
throw new AssertionError("Unexpected Stroke Type");
}
}
@Override public boolean equals(Object o) {
if (this == o) return true;
if (o == null || getClass() != o.getClass()) return false;
BorderStroke that = (BorderStroke) o;
if (this.hash != that.hash) return false;
if (!bottomStroke.equals(that.bottomStroke)) return false;
if (!bottomStyle.equals(that.bottomStyle)) return false;
if (!leftStroke.equals(that.leftStroke)) return false;
if (!leftStyle.equals(that.leftStyle)) return false;
if (!radii.equals(that.radii)) return false;
if (!rightStroke.equals(that.rightStroke)) return false;
if (!rightStyle.equals(that.rightStyle)) return false;
if (!topStroke.equals(that.topStroke)) return false;
if (!topStyle.equals(that.topStyle)) return false;
if (!widths.equals(that.widths)) return false;
if (!insets.equals(that.insets)) return false;
return true;
}
@Override public int hashCode() {
return hash;
}
}
