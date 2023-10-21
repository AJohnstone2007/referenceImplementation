package layout;
import java.util.List;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import static javafx.scene.layout.HBox.getMargin;
public class CustomHBox extends HBox {
private boolean biasDirty = true;
private boolean performingLayout = false;
private double minBaselineComplement = Double.NaN;
private double prefBaselineComplement = Double.NaN;
private Orientation bias;
private double[][] tempArray;
final int pad = 20;
public CustomHBox() {
super();
}
public CustomHBox(Node... children) {
super();
}
private Pos getAlignmentInternal() {
Pos localPos = getAlignment();
return localPos == null ? Pos.TOP_LEFT : localPos;
}
static double computeXOffset(double width, double contentWidth, HPos hpos) {
switch(hpos) {
case LEFT:
return 0;
case CENTER:
return (width - contentWidth) / 2;
case RIGHT:
return width - contentWidth;
default:
throw new AssertionError("Unhandled hPos");
}
}
private double[][] getAreaWidths(List<Node>managed, double height, boolean minimum) {
double[][] temp = getTempArray(managed.size());
final double insideHeight = height == -1? -1 : height -
snapSpace(getInsets().getTop()) - snapSpace(getInsets().getBottom());
final boolean shouldFillHeight = true;
for (int i = 0, size = managed.size(); i < size; i++) {
Node child = managed.get(i);
Insets margin = getMargin(child);
}
return temp;
}
private static double sum(double[] array, int size) {
int i = 0;
double res = 0;
while (i != size) {
res += array[i++];
}
return res;
}
private double adjustAreaWidths(List<Node>managed, double areaWidths[][], double width, double height) {
Insets insets = getInsets();
double top = snapSpace(insets.getTop());
double bottom = snapSpace(insets.getBottom());
double contentWidth = sum(areaWidths[0], managed.size()) + (managed.size()-1)*snapSpace(getSpacing());
double extraWidth = width -
snapSpace(insets.getLeft()) - snapSpace(insets.getRight()) - contentWidth;
return contentWidth;
}
@Override protected void layoutChildren() {
performingLayout = true;
List<Node> managed = getManagedChildren();
Insets insets = getInsets();
Pos align = getAlignmentInternal();
HPos alignHpos = align.getHpos();
VPos alignVpos = align.getVpos();
double width = getWidth();
double height = getHeight();
double top = snapSpace(insets.getTop());
double left = snapSpace(insets.getLeft());
double bottom = snapSpace(insets.getBottom());
double right = snapSpace(insets.getRight());
double space = snapSpace(getSpacing());
boolean shouldFillHeight = true;
final double[][] actualAreaWidths = getAreaWidths(managed, height, false);
double contentWidth = adjustAreaWidths(managed, actualAreaWidths, width, height);
double contentHeight = height - top - bottom;
double x = left + computeXOffset(width - left - right, contentWidth, align.getHpos());
double y = top;
double baselineOffset = -1;
if (alignVpos == VPos.BASELINE) {
double baselineComplement = 0;
baselineOffset = 0;
}
for (int i = 0, size = managed.size(); i < size; i++) {
Node child = managed.get(i);
Insets margin = getMargin(child);
layoutInArea(child, x, y, actualAreaWidths[0][i], contentHeight,
baselineOffset, margin, true, shouldFillHeight,
alignHpos, alignVpos);
x += actualAreaWidths[0][i] + space;
}
performingLayout = false;
}
private double[][] getTempArray(int size) {
if (tempArray == null) {
tempArray = new double[2][size];
} else if (tempArray[0].length < size) {
tempArray = new double[2][Math.max(tempArray.length * 3, size)];
}
return tempArray;
}
}
