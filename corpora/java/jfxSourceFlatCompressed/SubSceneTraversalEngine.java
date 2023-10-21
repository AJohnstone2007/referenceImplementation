package com.sun.javafx.scene.traversal;
import javafx.scene.Parent;
import javafx.scene.SubScene;
public final class SubSceneTraversalEngine extends TopMostTraversalEngine{
private final SubScene subScene;
public SubSceneTraversalEngine(SubScene scene) {
this.subScene = scene;
}
protected Parent getRoot() {
return subScene.getRoot();
}
}
