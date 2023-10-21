package javafx.scene.layout;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
public class AnchorPane extends Pane {
private static final String TOP_ANCHOR = "pane-top-anchor";
private static final String LEFT_ANCHOR = "pane-left-anchor";
private static final String BOTTOM_ANCHOR = "pane-bottom-anchor";
private static final String RIGHT_ANCHOR = "pane-right-anchor";
public static void setTopAnchor(Node child, Double value) {
setConstraint(child, TOP_ANCHOR, value);
}
public static Double getTopAnchor(Node child) {
return (Double)getConstraint(child, TOP_ANCHOR);
}
public static void setLeftAnchor(Node child, Double value) {
setConstraint(child, LEFT_ANCHOR, value);
}
public static Double getLeftAnchor(Node child) {
return (Double)getConstraint(child, LEFT_ANCHOR);
}
public static void setBottomAnchor(Node child, Double value) {
setConstraint(child, BOTTOM_ANCHOR, value);
}
public static Double getBottomAnchor(Node child) {
return (Double)getConstraint(child, BOTTOM_ANCHOR);
}
public static void setRightAnchor(Node child, Double value) {
setConstraint(child, RIGHT_ANCHOR, value);
}
public static Double getRightAnchor(Node child) {
return (Double)getConstraint(child, RIGHT_ANCHOR);
}
public static void clearConstraints(Node child) {
setTopAnchor(child, null);
setRightAnchor(child, null);
setBottomAnchor(child, null);
setLeftAnchor(child, null);
}
public AnchorPane() {
super();
}
public AnchorPane(Node... children) {
super();
getChildren().addAll(children);
}
@Override protected double computeMinWidth(double height) {
return computeWidth(true, height);
}
@Override protected double computeMinHeight(double width) {
return computeHeight(true, width);
}
@Override protected double computePrefWidth(double height) {
return computeWidth(false, height);
}
@Override protected double computePrefHeight(double width) {
return computeHeight(false, width);
}
private double computeWidth(final boolean minimum, final double height) {
double max = 0;
double contentHeight = height != -1 ? height - getInsets().getTop() - getInsets().getBottom() : -1;
final List<Node> children = getManagedChildren();
for (Node child : children) {
Double leftAnchor = getLeftAnchor(child);
Double rightAnchor = getRightAnchor(child);
double left = leftAnchor != null? leftAnchor :
(rightAnchor != null? 0 : child.getLayoutBounds().getMinX() + child.getLayoutX());
double right = rightAnchor != null? rightAnchor : 0;
double childHeight = -1;
if (child.getContentBias() == Orientation.VERTICAL && contentHeight != -1) {
childHeight = computeChildHeight(child, getTopAnchor(child), getBottomAnchor(child), contentHeight, -1);
}
max = Math.max(max, left + (minimum && leftAnchor != null && rightAnchor != null?
child.minWidth(childHeight) : computeChildPrefAreaWidth(child, -1, null, childHeight, false)) + right);
}
final Insets insets = getInsets();
return insets.getLeft() + max + insets.getRight();
}
private double computeHeight(final boolean minimum, final double width) {
double max = 0;
double contentWidth = width != -1 ? width - getInsets().getLeft()- getInsets().getRight() : -1;
final List<Node> children = getManagedChildren();
for (Node child : children) {
Double topAnchor = getTopAnchor(child);
Double bottomAnchor = getBottomAnchor(child);
double top = topAnchor != null? topAnchor :
(bottomAnchor != null? 0 : child.getLayoutBounds().getMinY() + child.getLayoutY());
double bottom = bottomAnchor != null? bottomAnchor : 0;
double childWidth = -1;
if (child.getContentBias() == Orientation.HORIZONTAL && contentWidth != -1) {
childWidth = computeChildWidth(child, getLeftAnchor(child), getRightAnchor(child), contentWidth, -1);
}
max = Math.max(max, top + (minimum && topAnchor != null && bottomAnchor != null?
child.minHeight(childWidth) : computeChildPrefAreaHeight(child, -1, null, childWidth)) + bottom);
}
final Insets insets = getInsets();
return insets.getTop() + max + insets.getBottom();
}
private double computeChildWidth(Node child, Double leftAnchor, Double rightAnchor, double areaWidth, double height) {
if (leftAnchor != null && rightAnchor != null && child.isResizable()) {
final Insets insets = getInsets();
return areaWidth - insets.getLeft() - insets.getRight() - leftAnchor - rightAnchor;
}
return computeChildPrefAreaWidth(child, -1, Insets.EMPTY, height, true);
}
private double computeChildHeight(Node child, Double topAnchor, Double bottomAnchor, double areaHeight, double width) {
if (topAnchor != null && bottomAnchor != null && child.isResizable()) {
final Insets insets = getInsets();
return areaHeight - insets.getTop() - insets.getBottom() - topAnchor - bottomAnchor;
}
return computeChildPrefAreaHeight(child, -1, Insets.EMPTY, width);
}
@Override protected void layoutChildren() {
final Insets insets = getInsets();
final List<Node> children = getManagedChildren();
for (Node child : children) {
final Double topAnchor = getTopAnchor(child);
final Double bottomAnchor = getBottomAnchor(child);
final Double leftAnchor = getLeftAnchor(child);
final Double rightAnchor = getRightAnchor(child);
final Bounds childLayoutBounds = child.getLayoutBounds();
final Orientation bias = child.getContentBias();
double x = child.getLayoutX() + childLayoutBounds.getMinX();
double y = child.getLayoutY() + childLayoutBounds.getMinY();
double w;
double h;
if (bias == Orientation.VERTICAL) {
h = computeChildHeight(child, topAnchor, bottomAnchor, getHeight(), -1);
w = computeChildWidth(child, leftAnchor, rightAnchor, getWidth(), h);
} else if (bias == Orientation.HORIZONTAL) {
w = computeChildWidth(child, leftAnchor, rightAnchor, getWidth(), -1);
h = computeChildHeight(child, topAnchor, bottomAnchor, getHeight(), w);
} else {
w = computeChildWidth(child, leftAnchor, rightAnchor, getWidth(), -1);
h = computeChildHeight(child, topAnchor, bottomAnchor, getHeight(), -1);
}
if (leftAnchor != null) {
x = insets.getLeft() + leftAnchor;
} else if (rightAnchor != null) {
x = getWidth() - insets.getRight() - rightAnchor - w;
}
if (topAnchor != null) {
y = insets.getTop() + topAnchor;
} else if (bottomAnchor != null) {
y = getHeight() - insets.getBottom() - bottomAnchor - h;
}
child.resizeRelocate(x, y, w, h);
}
}
}
