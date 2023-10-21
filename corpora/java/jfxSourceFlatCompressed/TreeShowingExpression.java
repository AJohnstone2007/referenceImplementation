package com.sun.javafx.scene;
import com.sun.javafx.binding.ExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;
public class TreeShowingExpression extends BooleanExpression {
private final ChangeListener<Boolean> windowShowingChangedListener = (obs, old, current) -> updateTreeShowing();
private final ChangeListener<Window> sceneWindowChangedListener = (obs, old, current) -> windowChanged(old, current);
private final ChangeListener<Scene> nodeSceneChangedListener = (obs, old, current) -> sceneChanged(old, current);
private final Node node;
private ExpressionHelper<Boolean> helper;
private boolean valid;
private boolean treeShowing;
public TreeShowingExpression(Node node) {
this.node = node;
this.node.sceneProperty().addListener(nodeSceneChangedListener);
NodeHelper.treeVisibleProperty(node).addListener(windowShowingChangedListener);
sceneChanged(null, node.getScene());
}
public void dispose() {
node.sceneProperty().removeListener(nodeSceneChangedListener);
NodeHelper.treeVisibleProperty(node).removeListener(windowShowingChangedListener);
valid = false;
sceneChanged(node.getScene(), null);
}
@Override
public void addListener(InvalidationListener listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(InvalidationListener listener) {
helper = ExpressionHelper.removeListener(helper, listener);
}
@Override
public void addListener(ChangeListener<? super Boolean> listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override
public void removeListener(ChangeListener<? super Boolean> listener) {
helper = ExpressionHelper.removeListener(helper, listener);
}
protected void invalidate() {
if (valid) {
valid = false;
ExpressionHelper.fireValueChangedEvent(helper);
}
}
@Override
public boolean get() {
if (!valid) {
updateTreeShowing();
valid = true;
}
return treeShowing;
}
private void sceneChanged(Scene oldScene, Scene newScene) {
if (oldScene != null) {
oldScene.windowProperty().removeListener(sceneWindowChangedListener);
}
if (newScene != null) {
newScene.windowProperty().addListener(sceneWindowChangedListener);
}
windowChanged(
oldScene == null ? null : oldScene.getWindow(),
newScene == null ? null : newScene.getWindow()
);
}
private void windowChanged(Window oldWindow, Window newWindow) {
if (oldWindow != null) {
oldWindow.showingProperty().removeListener(windowShowingChangedListener);
}
if (newWindow != null) {
newWindow.showingProperty().addListener(windowShowingChangedListener);
}
updateTreeShowing();
}
private void updateTreeShowing() {
boolean newValue = NodeHelper.isTreeShowing(node);
if (newValue != treeShowing) {
treeShowing = newValue;
invalidate();
}
}
}
