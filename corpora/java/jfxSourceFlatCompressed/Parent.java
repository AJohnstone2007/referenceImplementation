package javafx.scene;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.sun.javafx.util.TempState;
import com.sun.javafx.util.Utils;
import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.collections.VetoableListDecorator;
import javafx.css.Selector;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.NoninvertibleTransformException;
import com.sun.javafx.scene.CssFlags;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.sg.prism.NGGroup;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.scene.LayoutFlags;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.stage.WindowHelper;
import java.util.Collections;
import javafx.stage.Window;
public abstract class Parent extends Node {
static final int DIRTY_CHILDREN_THRESHOLD = 10;
private static final boolean warnOnAutoMove = PropertyHelper.getBooleanProperty("javafx.sg.warn");
private static final int REMOVED_CHILDREN_THRESHOLD = 20;
private boolean removedChildrenOptimizationDisabled = false;
static {
ParentHelper.setParentAccessor(new ParentHelper.ParentAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((Parent) node).doCreatePeer();
}
@Override
public void doUpdatePeer(Node node) {
((Parent) node).doUpdatePeer();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((Parent) node).doComputeGeomBounds(bounds, tx);
}
@Override
public boolean doComputeContains(Node node, double localX, double localY) {
return ((Parent) node).doComputeContains(localX, localY);
}
@Override
public void doProcessCSS(Node node) {
((Parent) node).doProcessCSS();
}
@Override
public void doPickNodeLocal(Node node, PickRay localPickRay,
PickResultChooser result) {
((Parent) node).doPickNodeLocal(localPickRay, result);
}
@Override
public boolean pickChildrenNode(Parent parent, PickRay pickRay, PickResultChooser result) {
return parent.pickChildrenNode(pickRay, result);
}
@Override
public void setTraversalEngine(Parent parent, ParentTraversalEngine value) {
parent.setTraversalEngine(value);
}
@Override
public ParentTraversalEngine getTraversalEngine(Parent parent) {
return parent.getTraversalEngine();
}
@Override
public List<String> doGetAllParentStylesheets(Parent parent) {
return parent.doGetAllParentStylesheets();
}
});
}
private void doUpdatePeer() {
final NGGroup peer = getPeer();
if (Utils.assertionEnabled()) {
List<NGNode> pgnodes = peer.getChildren();
if (pgnodes.size() != pgChildrenSize) {
java.lang.System.err.println("*** pgnodes.size() [" + pgnodes.size() + "] != pgChildrenSize [" + pgChildrenSize + "]");
}
}
if (isDirty(DirtyBits.PARENT_CHILDREN)) {
peer.clearFrom(startIdx);
for (int idx = startIdx; idx < children.size(); idx++) {
peer.add(idx, children.get(idx).getPeer());
}
if (removedChildrenOptimizationDisabled) {
peer.markDirty();
removedChildrenOptimizationDisabled = false;
} else {
if (removed != null && !removed.isEmpty()) {
for(int i = 0; i < removed.size(); i++) {
peer.addToRemoved(removed.get(i).getPeer());
}
}
}
if (removed != null) {
removed.clear();
}
pgChildrenSize = children.size();
startIdx = pgChildrenSize;
}
if (isDirty(DirtyBits.PARENT_CHILDREN_VIEW_ORDER)) {
computeViewOrderChildren();
peer.setViewOrderChildren(viewOrderChildren);
}
if (Utils.assertionEnabled()) validatePG();
}
private final Set<Node> childSet = new HashSet<Node>();
private int startIdx = 0;
private int pgChildrenSize = 0;
void validatePG() {
boolean assertionFailed = false;
final NGGroup peer = getPeer();
List<NGNode> pgnodes = peer.getChildren();
if (pgnodes.size() != children.size()) {
java.lang.System.err.println("*** pgnodes.size validatePG() [" + pgnodes.size() + "] != children.size() [" + children.size() + "]");
assertionFailed = true;
} else {
for (int idx = 0; idx < children.size(); idx++) {
Node n = children.get(idx);
if (n.getParent() != this) {
java.lang.System.err.println("*** this=" + this + " validatePG children[" + idx + "].parent= " + n.getParent());
assertionFailed = true;
}
if (n.getPeer() != pgnodes.get(idx)) {
java.lang.System.err.println("*** pgnodes[" + idx + "] validatePG != children[" + idx + "]");
assertionFailed = true;
}
}
}
if (assertionFailed) {
throw new java.lang.AssertionError("validation of PGGroup children failed");
}
}
void printSeq(String prefix, List<Node> nodes) {
String str = prefix;
for (Node nn : nodes) {
str += nn + " ";
}
System.out.println(str);
}
private final List<Node> viewOrderChildren = new ArrayList(1);
void markViewOrderChildrenDirty() {
viewOrderChildren.clear();
NodeHelper.markDirty(this, DirtyBits.PARENT_CHILDREN_VIEW_ORDER);
}
private void computeViewOrderChildren() {
boolean viewOrderSet = false;
for (Node child : children) {
double vo = child.getViewOrder();
if (!viewOrderSet && vo != 0) {
viewOrderSet = true;
}
}
viewOrderChildren.clear();
if (viewOrderSet) {
viewOrderChildren.addAll(children);
Collections.sort(viewOrderChildren, (Node a, Node b)
-> a.getViewOrder() < b.getViewOrder() ? 1
: a.getViewOrder() == b.getViewOrder() ? 0 : -1);
}
}
private List<Node> getOrderedChildren() {
if (isDirty(DirtyBits.PARENT_CHILDREN_VIEW_ORDER)) {
computeViewOrderChildren();
}
if (!viewOrderChildren.isEmpty()) {
return viewOrderChildren;
}
return children;
}
private boolean childrenTriggerPermutation = false;
private List<Node> removed;
private boolean geomChanged;
private boolean childSetModified;
private final ObservableList<Node> children = new VetoableListDecorator<Node>(new TrackableObservableList<Node>() {
protected void onChanged(Change<Node> c) {
unmodifiableManagedChildren = null;
boolean relayout = false;
boolean viewOrderChildrenDirty = false;
if (childSetModified) {
while (c.next()) {
int from = c.getFrom();
int to = c.getTo();
for (int i = from; i < to; ++i) {
Node n = children.get(i);
if (n.getParent() != null && n.getParent() != Parent.this) {
if (warnOnAutoMove) {
java.lang.System.err.println("WARNING added to a new parent without first removing it from its current");
java.lang.System.err.println("    parent. It will be automatically removed from its current parent.");
java.lang.System.err.println("    node=" + n + " oldparent= " + n.getParent() + " newparent=" + this);
}
n.getParent().children.remove(n);
if (warnOnAutoMove) {
Thread.dumpStack();
}
}
}
List<Node> removed = c.getRemoved();
int removedSize = removed.size();
for (int i = 0; i < removedSize; ++i) {
final Node n = removed.get(i);
if (n.isManaged()) {
relayout = true;
}
}
if (((removedSize > 0) || (to - from) > 0) && !viewOrderChildren.isEmpty()) {
viewOrderChildrenDirty = true;
}
for (int i = from; i < to; ++i) {
Node node = children.get(i);
if (node.getViewOrder() != 0) {
viewOrderChildrenDirty = true;
}
if (node.isManaged() || (node instanceof Parent && ((Parent) node).layoutFlag != LayoutFlags.CLEAN)) {
relayout = true;
}
node.setParent(Parent.this);
node.setScenes(getScene(), getSubScene());
if (node.isVisible()) {
geomChanged = true;
childIncluded(node);
}
}
}
if (dirtyChildren == null && children.size() > DIRTY_CHILDREN_THRESHOLD) {
dirtyChildren
= new ArrayList<Node>(2 * DIRTY_CHILDREN_THRESHOLD);
if (dirtyChildrenCount > 0) {
int size = children.size();
for (int i = 0; i < size; ++i) {
Node ch = children.get(i);
if (ch.isVisible() && ch.boundsChanged) {
dirtyChildren.add(ch);
}
}
}
}
} else {
layout_loop:while (c.next()) {
List<Node> removed = c.getRemoved();
for (int i = 0, removedSize = removed.size(); i < removedSize; ++i) {
if (removed.get(i).isManaged()) {
relayout = true;
break layout_loop;
}
}
for (int i = c.getFrom(), to = c.getTo(); i < to; ++i) {
if (children.get(i).isManaged()) {
relayout = true;
break layout_loop;
}
}
}
}
if (relayout) {
requestLayout();
}
if (geomChanged) {
NodeHelper.geomChanged(Parent.this);
}
c.reset();
c.next();
if (startIdx > c.getFrom()) {
startIdx = c.getFrom();
}
NodeHelper.markDirty(Parent.this, DirtyBits.PARENT_CHILDREN);
NodeHelper.markDirty(Parent.this, DirtyBits.NODE_FORCE_SYNC);
if (viewOrderChildrenDirty) {
markViewOrderChildrenDirty();
}
}
}) {
@Override
protected void onProposedChange(final List<Node> newNodes, int[] toBeRemoved) {
final Scene scene = getScene();
if (scene != null) {
Window w = scene.getWindow();
if (w != null && WindowHelper.getPeer(w) != null) {
Toolkit.getToolkit().checkFxUserThread();
}
}
geomChanged = false;
long newLength = children.size() + newNodes.size();
int removedLength = 0;
for (int i = 0; i < toBeRemoved.length; i += 2) {
removedLength += toBeRemoved[i + 1] - toBeRemoved[i];
}
newLength -= removedLength;
if (childrenTriggerPermutation) {
childSetModified = false;
return;
}
childSetModified = true;
if (newLength == childSet.size()) {
childSetModified = false;
for (int i = newNodes.size() - 1; i >= 0; --i ) {
Node n = newNodes.get(i);
if (!childSet.contains(n)) {
childSetModified = true;
break;
}
}
}
for (int i = 0; i < toBeRemoved.length; i += 2) {
for (int j = toBeRemoved[i]; j < toBeRemoved[i + 1]; j++) {
childSet.remove(children.get(j));
}
}
try {
if (childSetModified) {
for (int i = newNodes.size() - 1; i >= 0; --i ) {
Node node = newNodes.get(i);
if (node == null) {
throw new NullPointerException(
constructExceptionMessage(
"child node is null", null));
}
if (node.getClipParent() != null) {
throw new IllegalArgumentException(
constructExceptionMessage(
"node already used as a clip", node));
}
if (wouldCreateCycle(Parent.this, node)) {
throw new IllegalArgumentException(
constructExceptionMessage(
"cycle detected", node));
}
}
}
childSet.addAll(newNodes);
if (childSet.size() != newLength) {
throw new IllegalArgumentException(
constructExceptionMessage(
"duplicate children added", null));
}
} catch (RuntimeException e) {
childSet.clear();
childSet.addAll(children);
throw e;
}
if (!childSetModified) {
return;
}
if (removed == null) {
removed = new ArrayList<Node>();
}
if (removed.size() + removedLength > REMOVED_CHILDREN_THRESHOLD || !isTreeVisible()) {
removedChildrenOptimizationDisabled = true;
}
for (int i = 0; i < toBeRemoved.length; i += 2) {
for (int j = toBeRemoved[i]; j < toBeRemoved[i + 1]; j++) {
Node old = children.get(j);
final Scene oldScene = old.getScene();
if (oldScene != null) {
oldScene.generateMouseExited(old);
}
if (dirtyChildren != null) {
dirtyChildren.remove(old);
}
if (old.isVisible()) {
geomChanged = true;
childExcluded(old);
}
if (old.getParent() == Parent.this) {
old.setParent(null);
old.setScenes(null, null);
}
if (scene != null && !removedChildrenOptimizationDisabled) {
removed.add(old);
}
}
}
}
private String constructExceptionMessage(
String cause, Node offendingNode) {
final StringBuilder sb = new StringBuilder("Children: ");
sb.append(cause);
sb.append(": parent = ").append(Parent.this);
if (offendingNode != null) {
sb.append(", node = ").append(offendingNode);
}
return sb.toString();
}
};
private final ObservableList<Node> unmodifiableChildren =
FXCollections.unmodifiableObservableList(children);
private List<Node> unmodifiableManagedChildren = null;
protected ObservableList<Node> getChildren() {
return children;
}
public ObservableList<Node> getChildrenUnmodifiable() {
return unmodifiableChildren;
}
protected <E extends Node> List<E> getManagedChildren() {
if (unmodifiableManagedChildren == null) {
unmodifiableManagedChildren = new ArrayList<Node>();
for (int i=0, max=children.size(); i<max; i++) {
Node e = children.get(i);
if (e.isManaged()) {
unmodifiableManagedChildren.add(e);
}
}
}
return (List<E>)unmodifiableManagedChildren;
}
final void managedChildChanged() {
requestLayout();
unmodifiableManagedChildren = null;
}
final void toFront(Node node) {
if (Utils.assertionEnabled()) {
if (!childSet.contains(node)) {
throw new java.lang.AssertionError(
"specified node is not in the list of children");
}
}
if (children.get(children.size() - 1) != node) {
childrenTriggerPermutation = true;
try {
children.remove(node);
children.add(node);
} finally {
childrenTriggerPermutation = false;
}
}
}
final void toBack(Node node) {
if (Utils.assertionEnabled()) {
if (!childSet.contains(node)) {
throw new java.lang.AssertionError(
"specified node is not in the list of children");
}
}
if (children.get(0) != node) {
childrenTriggerPermutation = true;
try {
children.remove(node);
children.add(0, node);
} finally {
childrenTriggerPermutation = false;
}
}
}
@Override
void scenesChanged(final Scene newScene, final SubScene newSubScene,
final Scene oldScene, final SubScene oldSubScene) {
if (oldScene != null && newScene == null) {
StyleManager.getInstance().forget(this);
if (removed != null) {
removed.clear();
}
}
for (int i=0; i<children.size(); i++) {
children.get(i).setScenes(newScene, newSubScene);
}
final boolean awaitingLayout = layoutFlag != LayoutFlags.CLEAN;
sceneRoot = (newSubScene != null && newSubScene.getRoot() == this) ||
(newScene != null && newScene.getRoot() == this);
layoutRoot = !isManaged() || sceneRoot;
if (awaitingLayout) {
if (newScene != null && layoutRoot) {
if (newSubScene != null) {
newSubScene.setDirtyLayout(this);
}
}
}
}
@Override
void setDerivedDepthTest(boolean value) {
super.setDerivedDepthTest(value);
for (int i=0, max=children.size(); i<max; i++) {
final Node node = children.get(i);
node.computeDerivedDepthTest();
}
}
boolean pickChildrenNode(PickRay pickRay, PickResultChooser result) {
List<Node> orderedChildren = getOrderedChildren();
for (int i = orderedChildren.size() - 1; i >= 0; i--) {
orderedChildren.get(i).pickNode(pickRay, result);
if (result.isClosed()) {
return false;
}
}
return true;
}
private void doPickNodeLocal(PickRay pickRay, PickResultChooser result) {
double boundsDistance = intersectsBounds(pickRay);
if (!Double.isNaN(boundsDistance) && pickChildrenNode(pickRay, result)) {
if (isPickOnBounds()) {
result.offer(this, boundsDistance, PickResultChooser.computePoint(pickRay, boundsDistance));
}
}
}
@Override boolean isConnected() {
return super.isConnected() || sceneRoot;
}
@Override public Node lookup(String selector) {
Node n = super.lookup(selector);
if (n == null) {
for (int i=0, max=children.size(); i<max; i++) {
final Node node = children.get(i);
n = node.lookup(selector);
if (n != null) return n;
}
}
return n;
}
@Override List<Node> lookupAll(Selector selector, List<Node> results) {
results = super.lookupAll(selector, results);
for (int i=0, max=children.size(); i<max; i++) {
final Node node = children.get(i);
results = node.lookupAll(selector, results);
}
return results;
}
private ParentTraversalEngine traversalEngine;
private final void setTraversalEngine(ParentTraversalEngine value) {
this.traversalEngine = value;
}
private final ParentTraversalEngine getTraversalEngine() {
return traversalEngine;
}
private ReadOnlyBooleanWrapper needsLayout;
LayoutFlags layoutFlag = LayoutFlags.CLEAN;
protected final void setNeedsLayout(boolean value) {
if (value) {
markDirtyLayout(true, false);
} else if (layoutFlag == LayoutFlags.NEEDS_LAYOUT) {
boolean hasBranch = false;
for (int i = 0, max = children.size(); i < max; i++) {
final Node child = children.get(i);
if (child instanceof Parent) {
if (((Parent)child).layoutFlag != LayoutFlags.CLEAN) {
hasBranch = true;
break;
}
}
}
setLayoutFlag(hasBranch ? LayoutFlags.DIRTY_BRANCH : LayoutFlags.CLEAN);
}
}
public final boolean isNeedsLayout() {
return layoutFlag == LayoutFlags.NEEDS_LAYOUT;
}
public final ReadOnlyBooleanProperty needsLayoutProperty() {
if (needsLayout == null) {
needsLayout = new ReadOnlyBooleanWrapper(this, "needsLayout", layoutFlag == LayoutFlags.NEEDS_LAYOUT);
}
return needsLayout;
}
private boolean performingLayout = false;
boolean isPerformingLayout() {
return performingLayout;
}
private boolean sizeCacheClear = true;
private double prefWidthCache = -1;
private double prefHeightCache = -1;
private double minWidthCache = -1;
private double minHeightCache = -1;
void setLayoutFlag(LayoutFlags flag) {
if (needsLayout != null) {
needsLayout.set(flag == LayoutFlags.NEEDS_LAYOUT);
}
layoutFlag = flag;
}
private void markDirtyLayout(boolean local, boolean forceParentLayout) {
setLayoutFlag(LayoutFlags.NEEDS_LAYOUT);
if (local || layoutRoot) {
if (sceneRoot) {
Toolkit.getToolkit().requestNextPulse();
if (getSubScene() != null) {
getSubScene().setDirtyLayout(this);
}
} else {
markDirtyLayoutBranch();
}
} else {
requestParentLayout(forceParentLayout);
}
}
public void requestLayout() {
clearSizeCache();
markDirtyLayout(false, forceParentLayout);
}
private boolean forceParentLayout = false;
void requestLayout(boolean forceParentLayout) {
boolean savedForceParentLayout = this.forceParentLayout;
this.forceParentLayout = forceParentLayout;
requestLayout();
this.forceParentLayout = savedForceParentLayout;
}
protected final void requestParentLayout() {
requestParentLayout(false);
}
void requestParentLayout(boolean forceParentLayout) {
if (!layoutRoot) {
final Parent p = getParent();
if (p != null && (!p.performingLayout || forceParentLayout)) {
p.requestLayout();
}
}
}
void clearSizeCache() {
if (sizeCacheClear) {
return;
}
sizeCacheClear = true;
prefWidthCache = -1;
prefHeightCache = -1;
minWidthCache = -1;
minHeightCache = -1;
}
@Override public double prefWidth(double height) {
if (height == -1) {
if (prefWidthCache == -1) {
prefWidthCache = computePrefWidth(-1);
if (Double.isNaN(prefWidthCache) || prefWidthCache < 0) prefWidthCache = 0;
sizeCacheClear = false;
}
return prefWidthCache;
} else {
double result = computePrefWidth(height);
return Double.isNaN(result) || result < 0 ? 0 : result;
}
}
@Override public double prefHeight(double width) {
if (width == -1) {
if (prefHeightCache == -1) {
prefHeightCache = computePrefHeight(-1);
if (Double.isNaN(prefHeightCache) || prefHeightCache < 0) prefHeightCache = 0;
sizeCacheClear = false;
}
return prefHeightCache;
} else {
double result = computePrefHeight(width);
return Double.isNaN(result) || result < 0 ? 0 : result;
}
}
@Override public double minWidth(double height) {
if (height == -1) {
if (minWidthCache == -1) {
minWidthCache = computeMinWidth(-1);
if (Double.isNaN(minWidthCache) || minWidthCache < 0) minWidthCache = 0;
sizeCacheClear = false;
}
return minWidthCache;
} else {
double result = computeMinWidth(height);
return Double.isNaN(result) || result < 0 ? 0 : result;
}
}
@Override public double minHeight(double width) {
if (width == -1) {
if (minHeightCache == -1) {
minHeightCache = computeMinHeight(-1);
if (Double.isNaN(minHeightCache) || minHeightCache < 0) minHeightCache = 0;
sizeCacheClear = false;
}
return minHeightCache;
} else {
double result = computeMinHeight(width);
return Double.isNaN(result) || result < 0 ? 0 : result;
}
}
protected double computePrefWidth(double height) {
double minX = 0;
double maxX = 0;
for (int i=0, max=children.size(); i<max; i++) {
Node node = children.get(i);
if (node.isManaged()) {
final double x = node.getLayoutBounds().getMinX() + node.getLayoutX();
minX = Math.min(minX, x);
maxX = Math.max(maxX, x + boundedSize(node.prefWidth(-1), node.minWidth(-1), node.maxWidth(-1)));
}
}
return maxX - minX;
}
protected double computePrefHeight(double width) {
double minY = 0;
double maxY = 0;
for (int i=0, max=children.size(); i<max; i++) {
Node node = children.get(i);
if (node.isManaged()) {
final double y = node.getLayoutBounds().getMinY() + node.getLayoutY();
minY = Math.min(minY, y);
maxY = Math.max(maxY, y + boundedSize(node.prefHeight(-1), node.minHeight(-1), node.maxHeight(-1)));
}
}
return maxY - minY;
}
protected double computeMinWidth(double height) {
return prefWidth(height);
}
protected double computeMinHeight(double width) {
return prefHeight(width);
}
@Override public double getBaselineOffset() {
for (int i=0, max=children.size(); i<max; i++) {
final Node child = children.get(i);
if (child.isManaged()) {
double offset = child.getBaselineOffset();
if (offset == BASELINE_OFFSET_SAME_AS_HEIGHT) {
continue;
}
return child.getLayoutBounds().getMinY() + child.getLayoutY() + offset;
}
}
return super.getBaselineOffset();
}
private Node currentLayoutChild = null;
boolean isCurrentLayoutChild(Node node) {
return node == currentLayoutChild;
}
public final void layout() {
LayoutFlags flag = layoutFlag;
setLayoutFlag(LayoutFlags.CLEAN);
switch(flag) {
case CLEAN:
break;
case NEEDS_LAYOUT:
if (performingLayout) {
break;
}
performingLayout = true;
layoutChildren();
case DIRTY_BRANCH:
for (int i = 0, max = children.size(); i < max; i++) {
final Node child = children.get(i);
currentLayoutChild = child;
if (child instanceof Parent) {
((Parent)child).layout();
} else if (child instanceof SubScene) {
((SubScene)child).layoutPass();
}
}
currentLayoutChild = null;
performingLayout = false;
break;
}
}
protected void layoutChildren() {
for (int i=0, max=children.size(); i<max; i++) {
final Node node = children.get(i);
currentLayoutChild = node;
if (node.isResizable() && node.isManaged()) {
node.autosize();
}
}
currentLayoutChild = null;
}
private boolean sceneRoot = false;
boolean layoutRoot = false;
@Override final void notifyManagedChanged() {
layoutRoot = !isManaged() || sceneRoot;
}
final boolean isSceneRoot() {
return sceneRoot;
}
private final ObservableList<String> stylesheets = new TrackableObservableList<String>() {
@Override
protected void onChanged(Change<String> c) {
final Scene scene = getScene();
if (scene != null) {
StyleManager.getInstance().stylesheetsChanged(Parent.this, c);
c.reset();
while(c.next()) {
if (c.wasRemoved() == false) {
continue;
}
break;
}
reapplyCSS();
}
}
};
public final ObservableList<String> getStylesheets() { return stylesheets; }
private List<String> doGetAllParentStylesheets() {
List<String> list = null;
final Parent myParent = getParent();
if (myParent != null) {
list = ParentHelper.getAllParentStylesheets(myParent);
}
if (stylesheets != null && stylesheets.isEmpty() == false) {
if (list == null) {
list = new ArrayList<String>(stylesheets.size());
}
for (int n=0,nMax=stylesheets.size(); n<nMax; n++) {
list.add(stylesheets.get(n));
}
}
return list;
}
private void doProcessCSS() {
if (cssFlag == CssFlags.CLEAN) return;
if (cssFlag == CssFlags.DIRTY_BRANCH) {
super.processCSS();
return;
}
ParentHelper.superProcessCSS(this);
if (children.isEmpty()) return;
final Node[] childArray = children.toArray(new Node[children.size()]);
for (int i=0; i<childArray.length; i++) {
final Node child = childArray[i];
final Parent childParent = child.getParent();
if (childParent == null || childParent != this) continue;
if(CssFlags.UPDATE.compareTo(child.cssFlag) > 0) {
child.cssFlag = CssFlags.UPDATE;
}
NodeHelper.processCSS(child);
}
}
{
ParentHelper.initHelper(this);
}
protected Parent() {
layoutFlag = LayoutFlags.NEEDS_LAYOUT;
setAccessibleRole(AccessibleRole.PARENT);
}
private NGNode doCreatePeer() {
return new NGGroup();
}
@Override
void nodeResolvedOrientationChanged() {
for (int i = 0, max = children.size(); i < max; ++i) {
children.get(i).parentResolvedOrientationInvalidated();
}
}
private BaseBounds tmp = new RectBounds();
private BaseBounds cachedBounds = new RectBounds();
private boolean cachedBoundsInvalid;
private int dirtyChildrenCount;
private ArrayList<Node> dirtyChildren;
private Node top;
private Node left;
private Node bottom;
private Node right;
private Node near;
private Node far;
private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) {
if (children.isEmpty()) {
return bounds.makeEmpty();
}
if (tx.isTranslateOrIdentity()) {
if (cachedBoundsInvalid) {
recomputeBounds();
if (dirtyChildren != null) {
dirtyChildren.clear();
}
cachedBoundsInvalid = false;
dirtyChildrenCount = 0;
}
if (!tx.isIdentity()) {
bounds = bounds.deriveWithNewBounds((float)(cachedBounds.getMinX() + tx.getMxt()),
(float)(cachedBounds.getMinY() + tx.getMyt()),
(float)(cachedBounds.getMinZ() + tx.getMzt()),
(float)(cachedBounds.getMaxX() + tx.getMxt()),
(float)(cachedBounds.getMaxY() + tx.getMyt()),
(float)(cachedBounds.getMaxZ() + tx.getMzt()));
} else {
bounds = bounds.deriveWithNewBounds(cachedBounds);
}
return bounds;
} else {
double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE, maxZ = Double.MIN_VALUE;
boolean first = true;
for (int i=0, max=children.size(); i<max; i++) {
final Node node = children.get(i);
if (node.isVisible()) {
bounds = getChildTransformedBounds(node, tx, bounds);
if (bounds.isEmpty()) continue;
if (first) {
minX = bounds.getMinX();
minY = bounds.getMinY();
minZ = bounds.getMinZ();
maxX = bounds.getMaxX();
maxY = bounds.getMaxY();
maxZ = bounds.getMaxZ();
first = false;
} else {
minX = Math.min(bounds.getMinX(), minX);
minY = Math.min(bounds.getMinY(), minY);
minZ = Math.min(bounds.getMinZ(), minZ);
maxX = Math.max(bounds.getMaxX(), maxX);
maxY = Math.max(bounds.getMaxY(), maxY);
maxZ = Math.max(bounds.getMaxZ(), maxZ);
}
}
}
if (first)
bounds.makeEmpty();
else
bounds = bounds.deriveWithNewBounds((float)minX, (float)minY, (float)minZ,
(float)maxX, (float)maxY, (float)maxZ);
return bounds;
}
}
private void setChildDirty(final Node node, final boolean dirty) {
if (node.boundsChanged == dirty) {
return;
}
node.boundsChanged = dirty;
if (dirty) {
if (dirtyChildren != null) {
dirtyChildren.add(node);
}
++dirtyChildrenCount;
} else {
if (dirtyChildren != null) {
dirtyChildren.remove(node);
}
--dirtyChildrenCount;
}
}
private void childIncluded(final Node node) {
cachedBoundsInvalid = true;
setChildDirty(node, true);
}
private void childExcluded(final Node node) {
if (node == left) {
left = null;
cachedBoundsInvalid = true;
}
if (node == top) {
top = null;
cachedBoundsInvalid = true;
}
if (node == near) {
near = null;
cachedBoundsInvalid = true;
}
if (node == right) {
right = null;
cachedBoundsInvalid = true;
}
if (node == bottom) {
bottom = null;
cachedBoundsInvalid = true;
}
if (node == far) {
far = null;
cachedBoundsInvalid = true;
}
setChildDirty(node, false);
}
private void recomputeBounds() {
if (children.isEmpty()) {
cachedBounds.makeEmpty();
return;
}
if (children.size() == 1) {
Node node = children.get(0);
node.boundsChanged = false;
if (node.isVisible()) {
cachedBounds = getChildTransformedBounds(node, BaseTransform.IDENTITY_TRANSFORM, cachedBounds);
top = left = bottom = right = near = far = node;
} else {
cachedBounds.makeEmpty();
}
return;
}
if ((dirtyChildrenCount == 0) ||
!updateCachedBounds(dirtyChildren != null
? dirtyChildren : children,
dirtyChildrenCount)) {
createCachedBounds(children);
}
}
private final int LEFT_INVALID = 1;
private final int TOP_INVALID = 1 << 1;
private final int NEAR_INVALID = 1 << 2;
private final int RIGHT_INVALID = 1 << 3;
private final int BOTTOM_INVALID = 1 << 4;
private final int FAR_INVALID = 1 << 5;
private boolean updateCachedBounds(final List<Node> dirtyNodes,
int remainingDirtyNodes) {
if (cachedBounds.isEmpty()) {
createCachedBounds(dirtyNodes);
return true;
}
int invalidEdges = 0;
if ((left == null) || left.boundsChanged) {
invalidEdges |= LEFT_INVALID;
}
if ((top == null) || top.boundsChanged) {
invalidEdges |= TOP_INVALID;
}
if ((near == null) || near.boundsChanged) {
invalidEdges |= NEAR_INVALID;
}
if ((right == null) || right.boundsChanged) {
invalidEdges |= RIGHT_INVALID;
}
if ((bottom == null) || bottom.boundsChanged) {
invalidEdges |= BOTTOM_INVALID;
}
if ((far == null) || far.boundsChanged) {
invalidEdges |= FAR_INVALID;
}
float minX = cachedBounds.getMinX();
float minY = cachedBounds.getMinY();
float minZ = cachedBounds.getMinZ();
float maxX = cachedBounds.getMaxX();
float maxY = cachedBounds.getMaxY();
float maxZ = cachedBounds.getMaxZ();
for (int i = dirtyNodes.size() - 1; remainingDirtyNodes > 0; --i) {
final Node node = dirtyNodes.get(i);
if (node.boundsChanged) {
node.boundsChanged = false;
--remainingDirtyNodes;
tmp = getChildTransformedBounds(node, BaseTransform.IDENTITY_TRANSFORM, tmp);
if (!tmp.isEmpty()) {
float tmpx = tmp.getMinX();
float tmpy = tmp.getMinY();
float tmpz = tmp.getMinZ();
float tmpx2 = tmp.getMaxX();
float tmpy2 = tmp.getMaxY();
float tmpz2 = tmp.getMaxZ();
if (tmpx <= minX) {
minX = tmpx;
left = node;
invalidEdges &= ~LEFT_INVALID;
}
if (tmpy <= minY) {
minY = tmpy;
top = node;
invalidEdges &= ~TOP_INVALID;
}
if (tmpz <= minZ) {
minZ = tmpz;
near = node;
invalidEdges &= ~NEAR_INVALID;
}
if (tmpx2 >= maxX) {
maxX = tmpx2;
right = node;
invalidEdges &= ~RIGHT_INVALID;
}
if (tmpy2 >= maxY) {
maxY = tmpy2;
bottom = node;
invalidEdges &= ~BOTTOM_INVALID;
}
if (tmpz2 >= maxZ) {
maxZ = tmpz2;
far = node;
invalidEdges &= ~FAR_INVALID;
}
}
}
}
if (invalidEdges != 0) {
return false;
}
cachedBounds = cachedBounds.deriveWithNewBounds(minX, minY, minZ,
maxX, maxY, maxZ);
return true;
}
private void createCachedBounds(final List<Node> fromNodes) {
float minX, minY, minZ;
float maxX, maxY, maxZ;
final int nodeCount = fromNodes.size();
int i;
for (i = 0; i < nodeCount; ++i) {
final Node node = fromNodes.get(i);
node.boundsChanged = false;
if (node.isVisible()) {
tmp = node.getTransformedBounds(
tmp, BaseTransform.IDENTITY_TRANSFORM);
if (!tmp.isEmpty()) {
left = top = near = right = bottom = far = node;
break;
}
}
}
if (i == nodeCount) {
left = top = near = right = bottom = far = null;
cachedBounds.makeEmpty();
return;
}
minX = tmp.getMinX();
minY = tmp.getMinY();
minZ = tmp.getMinZ();
maxX = tmp.getMaxX();
maxY = tmp.getMaxY();
maxZ = tmp.getMaxZ();
for (++i; i < nodeCount; ++i) {
final Node node = fromNodes.get(i);
node.boundsChanged = false;
if (node.isVisible()) {
tmp = node.getTransformedBounds(
tmp, BaseTransform.IDENTITY_TRANSFORM);
if (!tmp.isEmpty()) {
final float tmpx = tmp.getMinX();
final float tmpy = tmp.getMinY();
final float tmpz = tmp.getMinZ();
final float tmpx2 = tmp.getMaxX();
final float tmpy2 = tmp.getMaxY();
final float tmpz2 = tmp.getMaxZ();
if (tmpx < minX) { minX = tmpx; left = node; }
if (tmpy < minY) { minY = tmpy; top = node; }
if (tmpz < minZ) { minZ = tmpz; near = node; }
if (tmpx2 > maxX) { maxX = tmpx2; right = node; }
if (tmpy2 > maxY) { maxY = tmpy2; bottom = node; }
if (tmpz2 > maxZ) { maxZ = tmpz2; far = node; }
}
}
}
cachedBounds = cachedBounds.deriveWithNewBounds(minX, minY, minZ,
maxX, maxY, maxZ);
}
@Override protected void updateBounds() {
for (int i=0, max=children.size(); i<max; i++) {
children.get(i).updateBounds();
}
super.updateBounds();
}
private Node currentlyProcessedChild;
private BaseBounds getChildTransformedBounds(Node node, BaseTransform tx, BaseBounds bounds) {
currentlyProcessedChild = node;
bounds = node.getTransformedBounds(bounds, tx);
currentlyProcessedChild = null;
return bounds;
}
void childBoundsChanged(Node node) {
if (node == currentlyProcessedChild) {
return;
}
cachedBoundsInvalid = true;
setChildDirty(node, true);
NodeHelper.geomChanged(this);
}
void childVisibilityChanged(Node node) {
if (node.isVisible()) {
childIncluded(node);
} else {
childExcluded(node);
}
NodeHelper.geomChanged(this);
}
private boolean doComputeContains(double localX, double localY) {
final Point2D tempPt = TempState.getInstance().point;
for (int i=0, max=children.size(); i<max; i++) {
final Node node = children.get(i);
tempPt.x = (float)localX;
tempPt.y = (float)localY;
try {
node.parentToLocal(tempPt);
} catch (NoninvertibleTransformException e) {
continue;
}
if (node.contains(tempPt.x, tempPt.y)) {
return true;
}
}
return false;
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case CHILDREN: return getChildrenUnmodifiable();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
void releaseAccessible() {
for (int i=0, max=children.size(); i<max; i++) {
final Node node = children.get(i);
node.releaseAccessible();
}
super.releaseAccessible();
}
List<Node> test_getRemoved() {
return removed;
}
List<Node> test_getViewOrderChildren() {
return viewOrderChildren;
}
}
