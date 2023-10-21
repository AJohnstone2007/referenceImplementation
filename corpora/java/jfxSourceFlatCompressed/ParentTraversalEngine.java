package com.sun.javafx.scene.traversal;
import javafx.scene.Parent;
public final class ParentTraversalEngine extends TraversalEngine{
private final Parent root;
private Boolean overridenTraversability;
public ParentTraversalEngine(Parent root, Algorithm algorithm) {
super(algorithm);
this.root = root;
}
public ParentTraversalEngine(Parent root) {
super();
this.root = root;
}
public void setOverriddenFocusTraversability(Boolean value) {
overridenTraversability = value;
}
@Override
protected Parent getRoot() {
return root;
}
public boolean isParentTraversable() {
return overridenTraversability != null ? root.isFocusTraversable() && overridenTraversability : root.isFocusTraversable();
}
}
