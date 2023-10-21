package com.sun.javafx.scene.traversal;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import java.util.List;
public interface TraversalContext {
List<Node> getAllTargetNodes();
Bounds getSceneLayoutBounds(Node node);
Parent getRoot();
Node selectFirstInParent(Parent parent);
Node selectLastInParent(Parent parent);
Node selectInSubtree(Parent subTreeRoot, Node from, Direction dir);
}
