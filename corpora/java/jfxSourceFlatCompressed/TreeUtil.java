package javafx.scene.control;
import java.util.List;
class TreeUtil {
static <T> int getExpandedDescendantCount(TreeItem<T> node, boolean treeItemCountDirty) {
if (node == null) return 0;
if (node.isLeaf()) return 1;
return node.getExpandedDescendentCount(treeItemCountDirty);
}
static int updateExpandedItemCount(TreeItem treeItem, boolean treeItemCountDirty, boolean isShowRoot) {
if (treeItem == null) {
return 0;
} else if (! treeItem.isExpanded()) {
return 1;
} else {
int count = getExpandedDescendantCount(treeItem, treeItemCountDirty);
if (! isShowRoot) count--;
return count;
}
}
static <T> TreeItem<T> getItem(TreeItem<T> parent, int itemIndex, boolean treeItemCountDirty) {
if (parent == null) return null;
if (itemIndex == 0) return parent;
if (itemIndex >= getExpandedDescendantCount(parent, treeItemCountDirty)) return null;
List<TreeItem<T>> children = parent.getChildren();
if (children == null) return null;
int idx = itemIndex - 1;
TreeItem<T> child;
for (int i = 0, max = children.size(); i < max; i++) {
child = children.get(i);
if (idx == 0) return child;
if (child.isLeaf() || ! child.isExpanded()) {
idx--;
continue;
}
int expandedChildCount = getExpandedDescendantCount(child, treeItemCountDirty);
if (idx >= expandedChildCount) {
idx -= expandedChildCount;
continue;
}
TreeItem<T> result = getItem(child, idx, treeItemCountDirty);
if (result != null) return result;
idx--;
}
return null;
}
static <T> int getRow(TreeItem<T> item, TreeItem<T> root, boolean treeItemCountDirty, boolean isShowRoot) {
if (item == null) {
return -1;
} else if (isShowRoot && item.equals(root)) {
return 0;
}
int row = 0;
TreeItem<T> i = item;
TreeItem<T> p = item.getParent();
TreeItem<T> sibling;
List<TreeItem<T>> siblings;
boolean parentIsCollapsed = false;
while (!i.equals(root) && p != null) {
if (!p.isExpanded()) {
parentIsCollapsed = true;
break;
}
siblings = p.children;
int itemIndex = siblings.indexOf(i);
for (int pos = itemIndex - 1; pos > -1; pos--) {
sibling = siblings.get(pos);
if (sibling == null) continue;
row += getExpandedDescendantCount(sibling, treeItemCountDirty);
if (sibling.equals(root)) {
if (! isShowRoot) {
return -1;
}
return row;
}
}
i = p;
p = p.getParent();
if (p == null && !i.equals(root)) {
return -1;
}
row++;
}
return (p == null && row == 0) || parentIsCollapsed ? -1 : isShowRoot ? row : row - 1;
}
}
