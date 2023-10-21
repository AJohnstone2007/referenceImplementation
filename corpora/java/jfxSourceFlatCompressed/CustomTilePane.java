package layout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.geometry.HPos;
import static javafx.geometry.Orientation.HORIZONTAL;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.TilePane;
import static javafx.scene.layout.TilePane.getAlignment;
import static javafx.scene.layout.TilePane.getMargin;
public class CustomTilePane extends TilePane {
final int pad = 20;
public CustomTilePane() {
super();
}
public CustomTilePane(Node... children) {
super();
}
private Pos getTileAlignmentInternal() {
Pos localPos = getTileAlignment();
return localPos == null ? Pos.CENTER : localPos;
}
private Pos getAlignmentInternal() {
Pos localPos = getAlignment();
return localPos == null ? Pos.TOP_LEFT : localPos;
}
private int computeOther(int numNodes, int numCells) {
double other = (double)numNodes/(double)Math.max(1, numCells);
return (int)Math.ceil(other);
}
private int computeColumns(double width, double tilewidth) {
return Math.max(1, (int) ((width + snapSpace(getHgap())) / (tilewidth + snapSpace(getHgap()))));
}
private int computeRows(double height, double tileheight) {
return Math.max(1, (int) ((height + snapSpace(getVgap())) / (tileheight + snapSpace(getVgap()))));
}
private double computeContentWidth(int columns, double tilewidth) {
if (columns == 0) {
return 0;
}
return columns * tilewidth + (columns - 1) * snapSpace(getHgap());
}
private double computeContentHeight(int rows, double tileheight) {
if (rows == 0) {
return 0;
}
return rows * tileheight + (rows - 1) * snapSpace(getVgap());
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
static double computeYOffset(double height, double contentHeight, VPos vpos) {
switch(vpos) {
case BASELINE:
case TOP:
return 0;
case CENTER:
return (height - contentHeight) / 2;
case BOTTOM:
return height - contentHeight;
default:
throw new AssertionError("Unhandled vPos");
}
}
@Override protected void layoutChildren() {
List<Node> sortedManagedChidlren = new ArrayList<>(getManagedChildren());
Collections.sort(sortedManagedChidlren, (c1, c2)
-> Double.valueOf(c2.prefHeight(-1)).compareTo(
Double.valueOf(c1.prefHeight(-1))));
List<Node> managed = sortedManagedChidlren;
HPos hpos = getAlignmentInternal().getHpos();
VPos vpos = getAlignmentInternal().getVpos();
double width = getWidth();
double height = getHeight();
double top = snapSpace(getInsets().getTop());
double left = snapSpace(getInsets().getLeft());
double bottom = snapSpace(getInsets().getBottom());
double right = snapSpace(getInsets().getRight());
double vgap = snapSpace(getVgap());
double hgap = snapSpace(getHgap());
double insideWidth = width - left - right;
double insideHeight = height - top - bottom;
double tileWidth = getTileWidth() > insideWidth ? insideWidth : getTileWidth();
double tileHeight = getTileHeight() > insideHeight ? insideHeight : getTileHeight();
int lastRowRemainder = 0;
int lastColumnRemainder = 0;
if (getOrientation() == HORIZONTAL) {
actualColumns = computeColumns(insideWidth, tileWidth);
actualRows = computeOther(managed.size(), actualColumns);
lastRowRemainder = hpos != HPos.LEFT?
actualColumns - (actualColumns*actualRows - managed.size()) : 0;
} else {
actualRows = computeRows(insideHeight, tileHeight);
actualColumns = computeOther(managed.size(), actualRows);
lastColumnRemainder = vpos != VPos.TOP?
actualRows - (actualColumns*actualRows - managed.size()) : 0;
}
double rowX = left + computeXOffset(insideWidth,
computeContentWidth(actualColumns, tileWidth),
hpos);
double columnY = top + computeYOffset(insideHeight,
computeContentHeight(actualRows, tileHeight),
vpos);
double lastRowX = lastRowRemainder > 0?
left + computeXOffset(insideWidth,
computeContentWidth(lastRowRemainder, tileWidth),
hpos) : rowX;
double lastColumnY = lastColumnRemainder > 0?
top + computeYOffset(insideHeight,
computeContentHeight(lastColumnRemainder, tileHeight),
vpos) : columnY;
double baselineOffset = 0;
int r = 0;
int c = 0;
for (int i = 0, size = managed.size(); i < size; i++) {
Node child = managed.get(i);
double xoffset = r == (actualRows - 1)? lastRowX : rowX;
double yoffset = c == (actualColumns - 1)? lastColumnY : columnY;
double tileX = xoffset + (c * (tileWidth + hgap));
double tileY = yoffset + (r * (tileHeight + vgap));
Pos childAlignment = getAlignment(child);
layoutInArea(child, tileX, tileY, tileWidth, tileHeight, baselineOffset,
getMargin(child),
childAlignment != null? childAlignment.getHpos() : getTileAlignmentInternal().getHpos(),
childAlignment != null? childAlignment.getVpos() : getTileAlignmentInternal().getVpos());
if (getOrientation() == HORIZONTAL) {
if (++c == actualColumns) {
c = 0;
r++;
}
} else {
if (++r == actualRows) {
r = 0;
c++;
}
}
}
}
private int actualRows = 0;
private int actualColumns = 0;
}
