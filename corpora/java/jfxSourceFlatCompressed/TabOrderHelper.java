package com.sun.javafx.scene.traversal;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.ParentHelper;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import java.util.List;
final class TabOrderHelper {
private static Node findPreviousFocusableInList(List<Node> nodeList, int startIndex) {
for (int i = startIndex ; i >= 0 ; i--) {
Node prevNode = nodeList.get(i);
if (isDisabledOrInvisible(prevNode)) continue;
final ParentTraversalEngine traversalEngine = prevNode instanceof Parent
? ParentHelper.getTraversalEngine((Parent) prevNode) : null;
if (prevNode instanceof Parent) {
if (traversalEngine != null && traversalEngine.canTraverse()) {
Node selected = traversalEngine.selectLast();
if (selected != null) {
return selected;
}
} else {
List<Node> prevNodesList = ((Parent) prevNode).getChildrenUnmodifiable();
if (prevNodesList.size() > 0) {
Node newNode = findPreviousFocusableInList(prevNodesList, prevNodesList.size() - 1);
if (newNode != null) {
return newNode;
}
}
}
}
if (traversalEngine != null
? traversalEngine.isParentTraversable()
: prevNode.isFocusTraversable()) {
return prevNode;
}
}
return null;
}
private static boolean isDisabledOrInvisible(Node prevNode) {
return prevNode.isDisabled() || !NodeHelper.isTreeVisible(prevNode);
}
public static Node findPreviousFocusablePeer(Node node, Parent root) {
Node startNode = node;
Node newNode = null;
List<Node> parentNodes = findPeers(startNode);
if (parentNodes == null) {
ObservableList<Node> rootChildren = ((Parent) node).getChildrenUnmodifiable();
return findPreviousFocusableInList(rootChildren, rootChildren.size() - 1);
}
int ourIndex = parentNodes.indexOf(startNode);
newNode = findPreviousFocusableInList(parentNodes, ourIndex - 1);
while (newNode == null && startNode.getParent() != root) {
List<Node> peerNodes;
int parentIndex;
Parent parent = startNode.getParent();
if (parent != null) {
final ParentTraversalEngine parentEngine
= ParentHelper.getTraversalEngine(parent);
if (parentEngine != null ? parentEngine.isParentTraversable() : parent.isFocusTraversable()) {
newNode = parent;
} else {
peerNodes = findPeers(parent);
if (peerNodes != null) {
parentIndex = peerNodes.indexOf(parent);
newNode = findPreviousFocusableInList(peerNodes, parentIndex - 1);
}
}
}
startNode = parent;
}
return newNode;
}
private static List<Node> findPeers(Node node) {
List<Node> parentNodes = null;
Parent parent = node.getParent();
if (parent != null) {
parentNodes = parent.getChildrenUnmodifiable();
}
return parentNodes;
}
private static Node findNextFocusableInList(List<Node> nodeList, int startIndex) {
for (int i = startIndex ; i < nodeList.size() ; i++) {
Node nextNode = nodeList.get(i);
if (isDisabledOrInvisible(nextNode)) continue;
final ParentTraversalEngine traversalEngine = nextNode instanceof Parent
? ParentHelper.getTraversalEngine((Parent) nextNode) : null;
if (traversalEngine != null
? traversalEngine.isParentTraversable()
: nextNode.isFocusTraversable()) {
return nextNode;
}
else if (nextNode instanceof Parent) {
if (traversalEngine!= null && traversalEngine.canTraverse()) {
Node selected = traversalEngine.selectFirst();
if (selected != null) {
return selected;
} else {
continue;
}
}
List<Node> nextNodesList = ((Parent)nextNode).getChildrenUnmodifiable();
if (nextNodesList.size() > 0) {
Node newNode = findNextFocusableInList(nextNodesList, 0);
if (newNode != null) {
return newNode;
}
}
}
}
return null;
}
public static Node findNextFocusablePeer(Node node, Parent root, boolean traverseIntoCurrent) {
Node startNode = node;
Node newNode = null;
if (traverseIntoCurrent && node instanceof Parent) {
newNode = findNextFocusableInList(((Parent)node).getChildrenUnmodifiable(), 0);
}
if (newNode == null) {
List<Node> parentNodes = findPeers(startNode);
if (parentNodes == null) {
return null;
}
int ourIndex = parentNodes.indexOf(startNode);
newNode = findNextFocusableInList(parentNodes, ourIndex + 1);
}
while (newNode == null && startNode.getParent() != root) {
List<Node> peerNodes;
int parentIndex;
Parent parent = startNode.getParent();
if (parent != null) {
peerNodes = findPeers(parent);
if (peerNodes != null) {
parentIndex = peerNodes.indexOf(parent);
newNode = findNextFocusableInList(peerNodes, parentIndex + 1);
}
}
startNode = parent;
}
return newNode;
}
public static Node getFirstTargetNode(Parent p) {
if (p == null || isDisabledOrInvisible(p)) return null;
final ParentTraversalEngine traversalEngine
= ParentHelper.getTraversalEngine(p);
if (traversalEngine!= null && traversalEngine.canTraverse()) {
Node selected = traversalEngine.selectFirst();
if (selected != null) {
return selected;
}
}
List<Node> parentsNodes = p.getChildrenUnmodifiable();
for (Node n : parentsNodes) {
if (isDisabledOrInvisible(n)) continue;
final ParentTraversalEngine parentEngine = n instanceof Parent
? ParentHelper.getTraversalEngine((Parent)n) : null;
if (parentEngine != null ? parentEngine.isParentTraversable() : n.isFocusTraversable()) {
return n;
}
if (n instanceof Parent) {
Node result = getFirstTargetNode((Parent)n);
if (result != null) return result;
}
}
return null;
}
public static Node getLastTargetNode(Parent p) {
if (p == null || isDisabledOrInvisible(p)) return null;
final ParentTraversalEngine traversalEngine
= ParentHelper.getTraversalEngine(p);
if (traversalEngine!= null && traversalEngine.canTraverse()) {
Node selected = traversalEngine.selectLast();
if (selected != null) {
return selected;
}
}
List<Node> parentsNodes = p.getChildrenUnmodifiable();
for (int i = parentsNodes.size() - 1; i >= 0; --i) {
Node n = parentsNodes.get(i);
if (isDisabledOrInvisible(n)) continue;
if (n instanceof Parent) {
Node result = getLastTargetNode((Parent) n);
if (result != null) return result;
}
final ParentTraversalEngine parentEngine = n instanceof Parent
? ParentHelper.getTraversalEngine((Parent) n) : null;
if (parentEngine != null ? parentEngine.isParentTraversable() : n.isFocusTraversable()) {
return n;
}
}
return null;
}
}
