package com.sun.javafx.scene.traversal;
import com.sun.javafx.scene.ParentHelper;
import javafx.scene.Node;
import javafx.scene.Parent;
public abstract class TopMostTraversalEngine extends TraversalEngine{
protected TopMostTraversalEngine() {
super(DEFAULT_ALGORITHM);
}
TopMostTraversalEngine(Algorithm algorithm) {
super(algorithm);
}
public final Node trav(Node node, Direction dir) {
Node newNode = null;
Parent p = node.getParent();
Node traverseNode = node;
while (p != null) {
ParentTraversalEngine engine = ParentHelper.getTraversalEngine(p);
if (engine != null && engine.canTraverse()) {
newNode = engine.select(node, dir);
if (newNode != null) {
break;
} else {
traverseNode = p;
if (dir == Direction.NEXT) {
dir = Direction.NEXT_IN_LINE;
}
}
}
p = p.getParent();
}
if (newNode == null) {
newNode = select(traverseNode, dir);
}
if (newNode == null) {
if (dir == Direction.NEXT || dir == Direction.NEXT_IN_LINE) {
newNode = selectFirst();
} else if (dir == Direction.PREVIOUS) {
newNode = selectLast();
}
}
if (newNode != null) {
focusAndNotify(newNode);
}
return newNode;
}
private void focusAndNotify(Node newNode) {
newNode.requestFocus();
notifyTreeTraversedTo(newNode);
}
private void notifyTreeTraversedTo(Node newNode) {
Parent p = newNode.getParent();
while (p != null) {
final ParentTraversalEngine traversalEngine = ParentHelper.getTraversalEngine(p);
if (traversalEngine != null) {
traversalEngine.notifyTraversedTo(newNode);
}
p = p.getParent();
}
notifyTraversedTo(newNode);
}
public final Node traverseToFirst() {
Node n = selectFirst();
if (n != null) focusAndNotify(n);
return n;
}
public final Node traverseToLast() {
Node n = selectLast();
if (n != null) focusAndNotify(n);
return n;
}
}
