package com.sun.javafx.scene.traversal;
import javafx.geometry.Bounds;
import javafx.scene.Node;
public interface TraverseListener {
public void onTraverse(Node node, Bounds bounds);
}
