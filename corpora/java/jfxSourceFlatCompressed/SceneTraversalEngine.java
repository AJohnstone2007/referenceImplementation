package com.sun.javafx.scene.traversal;
import javafx.scene.Parent;
import javafx.scene.Scene;
public final class SceneTraversalEngine extends TopMostTraversalEngine{
private final Scene scene;
public SceneTraversalEngine(Scene scene) {
this.scene = scene;
}
protected Parent getRoot() {
return scene.getRoot();
}
}
