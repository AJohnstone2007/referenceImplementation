package com.sun.javafx.scene.traversal;
import javafx.scene.Node;
public interface Algorithm {
public Node select(Node owner, Direction dir, TraversalContext context);
public Node selectFirst(TraversalContext context);
public Node selectLast(TraversalContext context);
}
