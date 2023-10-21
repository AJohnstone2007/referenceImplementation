package javafx.scene.layout;
import com.sun.javafx.geom.Vec2d;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ListChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import static javafx.scene.layout.Region.positionInArea;
public class BorderPane extends Pane {
private static final String MARGIN = "borderpane-margin";
private static final String ALIGNMENT = "borderpane-alignment";
public static void setAlignment(Node child, Pos value) {
setConstraint(child, ALIGNMENT, value);
}
public static Pos getAlignment(Node child) {
return (Pos)getConstraint(child, ALIGNMENT);
}
public static void setMargin(Node child, Insets value) {
setConstraint(child, MARGIN, value);
}
public static Insets getMargin(Node child) {
return (Insets)getConstraint(child, MARGIN);
}
private static Insets getNodeMargin(Node child) {
Insets margin = getMargin(child);
return margin != null ? margin : Insets.EMPTY;
}
public static void clearConstraints(Node child) {
setAlignment(child, null);
setMargin(child, null);
}
public BorderPane() {
super();
}
public BorderPane(Node center) {
super();
setCenter(center);
}
public BorderPane(Node center, Node top, Node right, Node bottom, Node left) {
super();
setCenter(center);
setTop(top);
setRight(right);
setBottom(bottom);
setLeft(left);
}
public final ObjectProperty<Node> centerProperty() {
if (center == null) {
center = new BorderPositionProperty("center");
}
return center;
}
private ObjectProperty<Node> center;
public final void setCenter(Node value) { centerProperty().set(value); }
public final Node getCenter() { return center == null ? null : center.get(); }
public final ObjectProperty<Node> topProperty() {
if (top == null) {
top = new BorderPositionProperty("top");
}
return top;
}
private ObjectProperty<Node> top;
public final void setTop(Node value) { topProperty().set(value); }
public final Node getTop() { return top == null ? null : top.get(); }
public final ObjectProperty<Node> bottomProperty() {
if (bottom == null) {
bottom = new BorderPositionProperty("bottom");
}
return bottom;
}
private ObjectProperty<Node> bottom;
public final void setBottom(Node value) { bottomProperty().set(value); }
public final Node getBottom() { return bottom == null ? null : bottom.get(); }
public final ObjectProperty<Node> leftProperty() {
if (left == null) {
left = new BorderPositionProperty("left");
}
return left;
}
private ObjectProperty<Node> left;
public final void setLeft(Node value) { leftProperty().set(value); }
public final Node getLeft() { return left == null ? null : left.get(); }
public final ObjectProperty<Node> rightProperty() {
if (right == null) {
right = new BorderPositionProperty("right");
}
return right;
}
private ObjectProperty<Node> right;
public final void setRight(Node value) { rightProperty().set(value); }
public final Node getRight() { return right == null ? null : right.get(); }
@Override public Orientation getContentBias() {
final Node c = getCenter();
if (c != null && c.isManaged() && c.getContentBias() != null) {
return c.getContentBias();
}
final Node r = getRight();
if (r != null && r.isManaged() && r.getContentBias() == Orientation.VERTICAL) {
return r.getContentBias();
}
final Node l = getLeft();
if (l != null && l.isManaged() && l.getContentBias() == Orientation.VERTICAL) {
return l.getContentBias();
}
final Node b = getBottom();
if (b != null && b.isManaged() && b.getContentBias() == Orientation.HORIZONTAL) {
return b.getContentBias();
}
final Node t = getTop();
if (t != null && t.isManaged() && t.getContentBias() == Orientation.HORIZONTAL) {
return t.getContentBias();
}
return null;
}
@Override protected double computeMinWidth(double height) {
double topMinWidth = getAreaWidth(getTop(), -1, true);
double bottomMinWidth = getAreaWidth(getBottom(), -1, true);
double leftPrefWidth;
double rightPrefWidth;
double centerMinWidth;
if (height != -1 && (childHasContentBias(getLeft(), Orientation.VERTICAL) ||
childHasContentBias(getRight(), Orientation.VERTICAL) ||
childHasContentBias(getCenter(), Orientation.VERTICAL))) {
double topPrefHeight = getAreaHeight(getTop(), -1, false);
double bottomPrefHeight = getAreaHeight(getBottom(), -1, false);
double middleAreaHeight = Math.max(0, height - topPrefHeight - bottomPrefHeight);
leftPrefWidth = getAreaWidth(getLeft(), middleAreaHeight, false);
rightPrefWidth = getAreaWidth(getRight(), middleAreaHeight, false);
centerMinWidth = getAreaWidth(getCenter(), middleAreaHeight, true);
} else {
leftPrefWidth = getAreaWidth(getLeft(), -1, false);
rightPrefWidth = getAreaWidth(getRight(), -1, false);
centerMinWidth = getAreaWidth(getCenter(), -1, true);
}
final Insets insets = getInsets();
return insets.getLeft() +
Math.max(leftPrefWidth + centerMinWidth + rightPrefWidth, Math.max(topMinWidth,bottomMinWidth)) +
insets.getRight();
}
@Override protected double computeMinHeight(double width) {
final Insets insets = getInsets();
double topPrefHeight = getAreaHeight(getTop(), width, false);
double bottomPrefHeight = getAreaHeight(getBottom(), width, false);
double leftMinHeight = getAreaHeight(getLeft(), -1, true);
double rightMinHeight = getAreaHeight(getRight(), -1, true);
double centerMinHeight;
if (width != -1 && childHasContentBias(getCenter(), Orientation.HORIZONTAL)) {
double leftPrefWidth = getAreaWidth(getLeft(), -1, false);
double rightPrefWidth = getAreaWidth(getRight(), -1, false);
centerMinHeight = getAreaHeight(getCenter(),
Math.max(0, width - leftPrefWidth - rightPrefWidth) , true);
} else {
centerMinHeight = getAreaHeight(getCenter(), -1, true);
}
double middleAreaMinHeigh = Math.max(centerMinHeight, Math.max(rightMinHeight, leftMinHeight));
return insets.getTop() + topPrefHeight + middleAreaMinHeigh + bottomPrefHeight + insets.getBottom();
}
@Override protected double computePrefWidth(double height) {
double topPrefWidth = getAreaWidth(getTop(), -1, false);
double bottomPrefWidth = getAreaWidth(getBottom(), -1, false);
double leftPrefWidth;
double rightPrefWidth;
double centerPrefWidth;
if ( height != -1 && (childHasContentBias(getLeft(), Orientation.VERTICAL) ||
childHasContentBias(getRight(), Orientation.VERTICAL) ||
childHasContentBias(getCenter(), Orientation.VERTICAL))) {
double topPrefHeight = getAreaHeight(getTop(), -1, false);
double bottomPrefHeight = getAreaHeight(getBottom(), -1, false);
double middleAreaHeight = Math.max(0, height - topPrefHeight - bottomPrefHeight);
leftPrefWidth = getAreaWidth(getLeft(), middleAreaHeight, false);
rightPrefWidth = getAreaWidth(getRight(), middleAreaHeight, false);
centerPrefWidth = getAreaWidth(getCenter(), middleAreaHeight, false);
} else {
leftPrefWidth = getAreaWidth(getLeft(), -1, false);
rightPrefWidth = getAreaWidth(getRight(), -1, false);
centerPrefWidth = getAreaWidth(getCenter(), -1, false);
}
final Insets insets = getInsets();
return insets.getLeft() +
Math.max(leftPrefWidth + centerPrefWidth + rightPrefWidth, Math.max(topPrefWidth,bottomPrefWidth)) +
insets.getRight();
}
@Override protected double computePrefHeight(double width) {
final Insets insets = getInsets();
double topPrefHeight = getAreaHeight(getTop(), width, false);
double bottomPrefHeight = getAreaHeight(getBottom(), width, false);
double leftPrefHeight = getAreaHeight(getLeft(), -1, false);
double rightPrefHeight = getAreaHeight(getRight(), -1, false);
double centerPrefHeight;
if (width != -1 && childHasContentBias(getCenter(), Orientation.HORIZONTAL)) {
double leftPrefWidth = getAreaWidth(getLeft(), -1, false);
double rightPrefWidth = getAreaWidth(getRight(), -1, false);
centerPrefHeight = getAreaHeight(getCenter(),
Math.max(0, width - leftPrefWidth - rightPrefWidth) , false);
} else {
centerPrefHeight = getAreaHeight(getCenter(), -1, false);
}
double middleAreaPrefHeigh = Math.max(centerPrefHeight, Math.max(rightPrefHeight, leftPrefHeight));
return insets.getTop() + topPrefHeight + middleAreaPrefHeigh + bottomPrefHeight + insets.getBottom();
}
@Override protected void layoutChildren() {
final Insets insets = getInsets();
double width = getWidth();
double height = getHeight();
final Orientation bias = getContentBias();
if (bias == null) {
final double minWidth = minWidth(-1);
final double minHeight = minHeight(-1);
width = width < minWidth ? minWidth : width;
height = height < minHeight ? minHeight : height;
} else if (bias == Orientation.HORIZONTAL) {
final double minWidth = minWidth(-1);
width = width < minWidth ? minWidth : width;
final double minHeight = minHeight(width);
height = height < minHeight ? minHeight : height;
} else {
final double minHeight = minHeight(-1);
height = height < minHeight ? minHeight : height;
final double minWidth = minWidth(height);
width = width < minWidth ? minWidth : width;
}
final double insideX = insets.getLeft();
final double insideY = insets.getTop();
final double insideWidth = width - insideX - insets.getRight();
final double insideHeight = height - insideY - insets.getBottom();
final Node c = getCenter();
final Node r = getRight();
final Node b = getBottom();
final Node l = getLeft();
final Node t = getTop();
double topHeight = 0;
if (t != null && t.isManaged()) {
Insets topMargin = getNodeMargin(t);
double adjustedWidth = adjustWidthByMargin(insideWidth, topMargin);
double adjustedHeight = adjustHeightByMargin(insideHeight, topMargin);
topHeight = snapSizeY(t.prefHeight(adjustedWidth));
topHeight = Math.min(topHeight, adjustedHeight);
Vec2d result = boundedNodeSizeWithBias(t, adjustedWidth,
topHeight, true, true, TEMP_VEC2D);
topHeight = snapSizeY(result.y);
t.resize(snapSizeX(result.x), topHeight);
topHeight = snapSpaceY(topMargin.getBottom()) + topHeight + snapSpaceY(topMargin.getTop());
Pos alignment = getAlignment(t);
positionInArea(t, insideX, insideY, insideWidth, topHeight, 0 ,
topMargin,
alignment != null? alignment.getHpos() : HPos.LEFT,
alignment != null? alignment.getVpos() : VPos.TOP, isSnapToPixel());
}
double bottomHeight = 0;
if (b != null && b.isManaged()) {
Insets bottomMargin = getNodeMargin(b);
double adjustedWidth = adjustWidthByMargin(insideWidth, bottomMargin);
double adjustedHeight = adjustHeightByMargin(insideHeight - topHeight, bottomMargin);
bottomHeight = snapSizeY(b.prefHeight(adjustedWidth));
bottomHeight = Math.min(bottomHeight, adjustedHeight);
Vec2d result = boundedNodeSizeWithBias(b, adjustedWidth,
bottomHeight, true, true, TEMP_VEC2D);
bottomHeight = snapSizeY(result.y);
b.resize(snapSizeX(result.x), bottomHeight);
bottomHeight = snapSpaceY(bottomMargin.getBottom()) + bottomHeight + snapSpaceY(bottomMargin.getTop());
Pos alignment = getAlignment(b);
positionInArea(b, insideX, insideY + insideHeight - bottomHeight,
insideWidth, bottomHeight, 0 ,
bottomMargin,
alignment != null? alignment.getHpos() : HPos.LEFT,
alignment != null? alignment.getVpos() : VPos.BOTTOM, isSnapToPixel());
}
double leftWidth = 0;
if (l != null && l.isManaged()) {
Insets leftMargin = getNodeMargin(l);
double adjustedWidth = adjustWidthByMargin(insideWidth, leftMargin);
double adjustedHeight = adjustHeightByMargin(insideHeight - topHeight - bottomHeight, leftMargin);
leftWidth = snapSizeX(l.prefWidth(adjustedHeight));
leftWidth = Math.min(leftWidth, adjustedWidth);
Vec2d result = boundedNodeSizeWithBias(l, leftWidth, adjustedHeight,
true, true, TEMP_VEC2D);
leftWidth = snapSizeX(result.x);
l.resize(leftWidth, snapSizeY(result.y));
leftWidth = snapSpaceX(leftMargin.getLeft()) + leftWidth + snapSpaceX(leftMargin.getRight());
Pos alignment = getAlignment(l);
positionInArea(l, insideX, insideY + topHeight,
leftWidth, insideHeight - topHeight - bottomHeight, 0 ,
leftMargin,
alignment != null? alignment.getHpos() : HPos.LEFT,
alignment != null? alignment.getVpos() : VPos.TOP, isSnapToPixel());
}
double rightWidth = 0;
if (r != null && r.isManaged()) {
Insets rightMargin = getNodeMargin(r);
double adjustedWidth = adjustWidthByMargin(insideWidth - leftWidth, rightMargin);
double adjustedHeight = adjustHeightByMargin(insideHeight - topHeight - bottomHeight, rightMargin);
rightWidth = snapSizeX(r.prefWidth(adjustedHeight));
rightWidth = Math.min(rightWidth, adjustedWidth);
Vec2d result = boundedNodeSizeWithBias(r, rightWidth, adjustedHeight,
true, true, TEMP_VEC2D);
rightWidth = snapSizeX(result.x);
r.resize(rightWidth, snapSizeY(result.y));
rightWidth = snapSpaceX(rightMargin.getLeft()) + rightWidth + snapSpaceX(rightMargin.getRight());
Pos alignment = getAlignment(r);
positionInArea(r, insideX + insideWidth - rightWidth, insideY + topHeight,
rightWidth, insideHeight - topHeight - bottomHeight, 0 ,
rightMargin,
alignment != null? alignment.getHpos() : HPos.RIGHT,
alignment != null? alignment.getVpos() : VPos.TOP, isSnapToPixel());
}
if (c != null && c.isManaged()) {
Pos alignment = getAlignment(c);
layoutInArea(c, insideX + leftWidth, insideY + topHeight,
insideWidth - leftWidth - rightWidth,
insideHeight - topHeight - bottomHeight, 0 ,
getNodeMargin(c),
alignment != null? alignment.getHpos() : HPos.CENTER,
alignment != null? alignment.getVpos() : VPos.CENTER);
}
}
private double getAreaWidth(Node child, double height, boolean minimum) {
if (child != null && child.isManaged()) {
Insets margin = getNodeMargin(child);
return minimum ? computeChildMinAreaWidth(child, -1, margin, height, false):
computeChildPrefAreaWidth(child, -1, margin, height, false);
}
return 0;
}
private double getAreaHeight(Node child, double width, boolean minimum) {
if (child != null && child.isManaged()) {
Insets margin = getNodeMargin(child);
return minimum ? computeChildMinAreaHeight(child, -1, margin, width):
computeChildPrefAreaHeight(child, -1, margin, width);
}
return 0;
}
private boolean childHasContentBias(Node child, Orientation orientation) {
if (child != null && child.isManaged()) {
return child.getContentBias() == orientation;
}
return false;
}
private final class BorderPositionProperty extends ObjectPropertyBase<Node> {
private Node oldValue = null;
private final String propertyName;
private boolean isBeingInvalidated;
BorderPositionProperty(String propertyName) {
this.propertyName = propertyName;
getChildren().addListener(new ListChangeListener<Node>() {
@Override
public void onChanged(ListChangeListener.Change<? extends Node> c) {
if (oldValue == null || isBeingInvalidated) {
return;
}
while (c.next()) {
if (c.wasRemoved()) {
List<? extends Node> removed = c.getRemoved();
for (int i = 0, sz = removed.size(); i < sz; ++i) {
if (removed.get(i) == oldValue) {
oldValue = null;
set(null);
}
}
}
}
}
});
}
@Override
protected void invalidated() {
final List<Node> children = getChildren();
isBeingInvalidated = true;
try {
if (oldValue != null) {
children.remove(oldValue);
}
final Node _value = get();
this.oldValue = _value;
if (_value != null) {
children.add(_value);
}
} finally {
isBeingInvalidated = false;
}
}
@Override
public Object getBean() {
return BorderPane.this;
}
@Override
public String getName() {
return propertyName;
}
}
}
