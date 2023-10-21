package com.sun.javafx.scene.traversal;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.scene.NodeHelper;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import java.util.ArrayList;
import java.util.List;
public abstract class TraversalEngine{
static final Algorithm DEFAULT_ALGORITHM = PlatformImpl.isContextual2DNavigation() ? new Hueristic2D() : new ContainerTabOrder();
private final TraversalContext context = new EngineContext();
private final TempEngineContext tempEngineContext = new TempEngineContext();
protected final Algorithm algorithm;
private final Bounds initialBounds = new BoundingBox(0, 0, 1, 1);
private final ArrayList<TraverseListener> listeners = new ArrayList<>();
protected TraversalEngine(Algorithm algorithm) {
this.algorithm = algorithm;
}
protected TraversalEngine() {
this.algorithm = null;
}
public final void addTraverseListener(TraverseListener listener) {
listeners.add(listener);
}
final void notifyTraversedTo(Node newNode) {
for (TraverseListener l : listeners) {
l.onTraverse(newNode, getLayoutBounds(newNode, getRoot()));
}
}
public final Node select(Node from, Direction dir) {
return algorithm.select(from, dir, context);
}
public final Node selectFirst() {
return algorithm.selectFirst(context);
}
public final Node selectLast() {
return algorithm.selectLast(context);
}
protected abstract Parent getRoot();
public final boolean canTraverse() {
return algorithm != null;
}
private Bounds getLayoutBounds(Node n, Parent forParent) {
final Bounds bounds;
if (n != null) {
if (forParent == null) {
bounds = n.localToScene(n.getLayoutBounds());
} else {
bounds = forParent.sceneToLocal(n.localToScene(n.getLayoutBounds()));
}
} else {
bounds = initialBounds;
}
return bounds;
}
private final class EngineContext extends BaseEngineContext {
@Override
public Parent getRoot() {
return TraversalEngine.this.getRoot();
}
}
private final class TempEngineContext extends BaseEngineContext {
private Parent root;
@Override
public Parent getRoot() {
return root;
}
public void setRoot(Parent root) {
this.root = root;
}
}
private abstract class BaseEngineContext implements TraversalContext {
@Override
public List<Node> getAllTargetNodes() {
final List<Node> targetNodes = new ArrayList<>();
addFocusableChildrenToList(targetNodes, getRoot());
return targetNodes;
}
@Override
public Bounds getSceneLayoutBounds(Node n) {
return getLayoutBounds(n, null);
}
private void addFocusableChildrenToList(List<Node> list, Parent parent) {
List<Node> parentsNodes = parent.getChildrenUnmodifiable();
for (Node n : parentsNodes) {
if (n.isFocusTraversable() && !n.isFocused() && NodeHelper.isTreeVisible(n) && !n.isDisabled()) {
list.add(n);
}
if (n instanceof Parent) {
addFocusableChildrenToList(list, (Parent)n);
}
}
}
@Override
public Node selectFirstInParent(Parent parent) {
tempEngineContext.setRoot(parent);
return DEFAULT_ALGORITHM.selectFirst(tempEngineContext);
}
@Override
public Node selectLastInParent(Parent parent) {
tempEngineContext.setRoot(parent);
return DEFAULT_ALGORITHM.selectLast(tempEngineContext);
}
@Override
public Node selectInSubtree(Parent subTreeRoot, Node from, Direction dir) {
tempEngineContext.setRoot(subTreeRoot);
return DEFAULT_ALGORITHM.select(from, dir, tempEngineContext);
}
}
}
