package com.sun.javafx.scene.traversal;
import javafx.geometry.NodeOrientation;
public enum Direction {
UP(false),
DOWN(true),
LEFT(false),
RIGHT(true),
NEXT(true),
NEXT_IN_LINE(true),
PREVIOUS(false);
private final boolean forward;
Direction(boolean forward) {
this.forward = forward;
}
public boolean isForward() {
return forward;
}
public Direction getDirectionForNodeOrientation(NodeOrientation orientation) {
if (orientation == NodeOrientation.RIGHT_TO_LEFT) {
switch (this) {
case LEFT:
return RIGHT;
case RIGHT:
return LEFT;
}
}
return this;
}
}
