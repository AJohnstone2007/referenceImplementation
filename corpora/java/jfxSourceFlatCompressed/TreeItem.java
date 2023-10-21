package javafx.scene.control;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;
import com.sun.javafx.event.EventHandlerManager;
import java.util.Comparator;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import static javafx.scene.control.TreeSortMode.*;
public class TreeItem<T> implements EventTarget {
@SuppressWarnings("unchecked")
public static <T> EventType<TreeModificationEvent<T>> treeNotificationEvent() {
return (EventType<TreeModificationEvent<T>>) TREE_NOTIFICATION_EVENT;
}
private static final EventType<?> TREE_NOTIFICATION_EVENT
= new EventType<>(Event.ANY, "TreeNotificationEvent");
@SuppressWarnings("unchecked")
public static <T> EventType<TreeModificationEvent<T>> expandedItemCountChangeEvent() {
return (EventType<TreeModificationEvent<T>>) EXPANDED_ITEM_COUNT_CHANGE_EVENT;
}
private static final EventType<?> EXPANDED_ITEM_COUNT_CHANGE_EVENT
= new EventType<>(treeNotificationEvent(), "ExpandedItemCountChangeEvent");
@SuppressWarnings("unchecked")
public static <T> EventType<TreeModificationEvent<T>> branchExpandedEvent() {
return (EventType<TreeModificationEvent<T>>) BRANCH_EXPANDED_EVENT;
}
private static final EventType<?> BRANCH_EXPANDED_EVENT
= new EventType<>(expandedItemCountChangeEvent(), "BranchExpandedEvent");
@SuppressWarnings("unchecked")
public static <T> EventType<TreeModificationEvent<T>> branchCollapsedEvent() {
return (EventType<TreeModificationEvent<T>>) BRANCH_COLLAPSED_EVENT;
}
private static final EventType<?> BRANCH_COLLAPSED_EVENT
= new EventType<>(expandedItemCountChangeEvent(), "BranchCollapsedEvent");
@SuppressWarnings("unchecked")
public static <T> EventType<TreeModificationEvent<T>> childrenModificationEvent() {
return (EventType<TreeModificationEvent<T>>) CHILDREN_MODIFICATION_EVENT;
}
private static final EventType<?> CHILDREN_MODIFICATION_EVENT
= new EventType<>(expandedItemCountChangeEvent(), "ChildrenModificationEvent");
@SuppressWarnings("unchecked")
public static <T> EventType<TreeModificationEvent<T>> valueChangedEvent() {
return (EventType<TreeModificationEvent<T>>) VALUE_CHANGED_EVENT;
}
private static final EventType<?> VALUE_CHANGED_EVENT
= new EventType<>(treeNotificationEvent(), "ValueChangedEvent");
@SuppressWarnings("unchecked")
public static <T> EventType<TreeModificationEvent<T>> graphicChangedEvent() {
return (EventType<TreeModificationEvent<T>>) GRAPHIC_CHANGED_EVENT;
}
private static final EventType<?> GRAPHIC_CHANGED_EVENT
= new EventType<>(treeNotificationEvent(), "GraphicChangedEvent");
public TreeItem() {
this(null);
}
public TreeItem(final T value) {
this(value, (Node)null);
}
public TreeItem(final T value, final Node graphic) {
setValue(value);
setGraphic(graphic);
addEventHandler(TreeItem.<Object>expandedItemCountChangeEvent(), itemListener);
}
private final EventHandler<TreeModificationEvent<Object>> itemListener =
new EventHandler<TreeModificationEvent<Object>>() {
@Override public void handle(TreeModificationEvent<Object> event) {
expandedDescendentCountDirty = true;
}
};
private boolean ignoreSortUpdate = false;
private boolean expandedDescendentCountDirty = true;
ObservableList<TreeItem<T>> children;
private final EventHandlerManager eventHandlerManager =
new EventHandlerManager(this);
private int expandedDescendentCount = 1;
int previousExpandedDescendentCount = 1;
Comparator<TreeItem<T>> lastComparator = null;
TreeSortMode lastSortMode = null;
private int parentLinkCount = 0;
private ListChangeListener<TreeItem<T>> childrenListener = c -> {
expandedDescendentCountDirty = true;
updateChildren(c);
};
private ObjectProperty<T> value;
public final void setValue(T value) { valueProperty().setValue(value); }
public final T getValue() { return value == null ? null : value.getValue(); }
public final ObjectProperty<T> valueProperty() {
if (value == null) {
value = new ObjectPropertyBase<T>() {
@Override protected void invalidated() {
fireEvent(new TreeModificationEvent<T>(VALUE_CHANGED_EVENT, TreeItem.this, get()));
}
@Override public Object getBean() {
return TreeItem.this;
}
@Override public String getName() {
return "value";
}
};
}
return value;
}
private ObjectProperty<Node> graphic;
public final void setGraphic(Node value) { graphicProperty().setValue(value); }
public final Node getGraphic() { return graphic == null ? null : graphic.getValue(); }
public final ObjectProperty<Node> graphicProperty() {
if (graphic == null) {
graphic = new ObjectPropertyBase<Node>() {
@Override protected void invalidated() {
fireEvent(new TreeModificationEvent<T>(GRAPHIC_CHANGED_EVENT, TreeItem.this));
}
@Override
public Object getBean() {
return TreeItem.this;
}
@Override
public String getName() {
return "graphic";
}
};
}
return graphic;
}
private BooleanProperty expanded;
public final void setExpanded(boolean value) {
if (! value && expanded == null) return;
expandedProperty().setValue(value);
}
public final boolean isExpanded() { return expanded == null ? false : expanded.getValue(); }
public final BooleanProperty expandedProperty() {
if (expanded == null) {
expanded = new BooleanPropertyBase() {
@Override protected void invalidated() {
if (isLeaf()) return;
EventType<?> evtType = isExpanded() ?
BRANCH_EXPANDED_EVENT : BRANCH_COLLAPSED_EVENT;
fireEvent(new TreeModificationEvent<T>(evtType, TreeItem.this, isExpanded()));
}
@Override
public Object getBean() {
return TreeItem.this;
}
@Override
public String getName() {
return "expanded";
}
};
}
return expanded;
}
private ReadOnlyBooleanWrapper leaf;
private void setLeaf(boolean value) {
if (value && leaf == null) {
return;
} else if (leaf == null) {
leaf = new ReadOnlyBooleanWrapper(this, "leaf", true);
}
leaf.setValue(value);
}
public boolean isLeaf() { return leaf == null ? true : leaf.getValue(); }
public final ReadOnlyBooleanProperty leafProperty() {
if (leaf == null) {
leaf = new ReadOnlyBooleanWrapper(this, "leaf", true);
}
return leaf.getReadOnlyProperty();
}
private ReadOnlyObjectWrapper<TreeItem<T>> parent = new ReadOnlyObjectWrapper<TreeItem<T>>(this, "parent");
private void setParent(TreeItem<T> value) { parent.setValue(value); }
public final TreeItem<T> getParent() { return parent == null ? null : parent.getValue(); }
public final ReadOnlyObjectProperty<TreeItem<T>> parentProperty() { return parent.getReadOnlyProperty(); }
public ObservableList<TreeItem<T>> getChildren() {
if (children == null) {
children = FXCollections.observableArrayList();
children.addListener(childrenListener);
}
if (children.isEmpty()) return children;
if (!ignoreSortUpdate) {
checkSortState();
}
return children;
}
public TreeItem<T> previousSibling() {
return previousSibling(this);
}
public TreeItem<T> previousSibling(final TreeItem<T> beforeNode) {
if (getParent() == null || beforeNode == null) {
return null;
}
List<TreeItem<T>> parentChildren = getParent().getChildren();
final int childCount = parentChildren.size();
int pos = -1;
for (int i = 0; i < childCount; i++) {
if (beforeNode.equals(parentChildren.get(i))) {
pos = i - 1;
return pos < 0 ? null : parentChildren.get(pos);
}
}
return null;
}
public TreeItem<T> nextSibling() {
return nextSibling(this);
}
public TreeItem<T> nextSibling(final TreeItem<T> afterNode) {
if (getParent() == null || afterNode == null) {
return null;
}
List<TreeItem<T>> parentChildren = getParent().getChildren();
final int childCount = parentChildren.size();
int pos = -1;
for (int i = 0; i < childCount; i++) {
if (afterNode.equals(parentChildren.get(i))) {
pos = i + 1;
return pos >= childCount ? null : parentChildren.get(pos);
}
}
return null;
}
@Override public String toString() {
return "TreeItem [ value: " + getValue() + " ]";
}
private void fireEvent(TreeModificationEvent<T> evt) {
Event.fireEvent(this, evt);
}
@Override public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
if (getParent() != null) {
getParent().buildEventDispatchChain(tail);
}
return tail.append(eventHandlerManager);
}
public <E extends Event> void addEventHandler(EventType<E> eventType, EventHandler<E> eventHandler) {
eventHandlerManager.addEventHandler(eventType, eventHandler);
}
public <E extends Event> void removeEventHandler(EventType<E> eventType, EventHandler<E> eventHandler) {
eventHandlerManager.removeEventHandler(eventType, eventHandler);
}
void sort() {
sort(children, lastComparator, lastSortMode);
}
private void sort(final ObservableList<TreeItem<T>> children,
final Comparator<TreeItem<T>> comparator,
final TreeSortMode sortMode) {
if (comparator == null) return;
runSort(children, comparator, sortMode);
if (getParent() == null) {
TreeModificationEvent<T> e = new TreeModificationEvent<T>(TreeItem.childrenModificationEvent(), this);
e.wasPermutated = true;
fireEvent(e);
}
}
private void checkSortState() {
TreeItem<T> rootNode = getRoot();
TreeSortMode sortMode = rootNode.lastSortMode;
Comparator<TreeItem<T>> comparator = rootNode.lastComparator;
if (comparator != null && comparator != lastComparator) {
lastComparator = comparator;
runSort(children, comparator, sortMode);
}
}
private void runSort(ObservableList<TreeItem<T>> children, Comparator<TreeItem<T>> comparator, TreeSortMode sortMode) {
if (sortMode == ALL_DESCENDANTS) {
doSort(children, comparator);
} else if (sortMode == ONLY_FIRST_LEVEL) {
if (getParent() == null) {
doSort(children, comparator);
}
} else {
}
}
private TreeItem<T> getRoot() {
TreeItem<T> parent = getParent();
if (parent == null) return this;
while (true) {
TreeItem<T> newParent = parent.getParent();
if (newParent == null) return parent;
parent = newParent;
}
}
private void doSort(ObservableList<TreeItem<T>> children, final Comparator<TreeItem<T>> comparator) {
if (!isLeaf() && isExpanded()) {
FXCollections.sort(children, comparator);
}
}
int getExpandedDescendentCount(boolean reset) {
if (reset || expandedDescendentCountDirty) {
updateExpandedDescendentCount(reset);
expandedDescendentCountDirty = false;
}
return expandedDescendentCount;
}
private void updateExpandedDescendentCount(boolean reset) {
previousExpandedDescendentCount = expandedDescendentCount;
expandedDescendentCount = 1;
ignoreSortUpdate = true;
if (!isLeaf() && isExpanded()) {
for (TreeItem<T> child : getChildren()) {
if (child == null) continue;
expandedDescendentCount += child.isExpanded() ? child.getExpandedDescendentCount(reset) : 1;
}
}
ignoreSortUpdate = false;
}
private void updateChildren(ListChangeListener.Change<? extends TreeItem<T>> c) {
setLeaf(children.isEmpty());
final List<TreeItem<T>> added = new ArrayList<>();
final List<TreeItem<T>> removed = new ArrayList<>();
while (c.next()) {
added.addAll(c.getAddedSubList());
removed.addAll(c.getRemoved());
}
updateChildrenParent(removed, null);
updateChildrenParent(added, this);
c.reset();
fireEvent(new TreeModificationEvent<T>(
CHILDREN_MODIFICATION_EVENT, this, added, removed, c));
}
private static <T> void updateChildrenParent(List<? extends TreeItem<T>> treeItems, final TreeItem<T> newParent) {
if (treeItems == null) return;
for (final TreeItem<T> treeItem : treeItems) {
if (treeItem == null) continue;
TreeItem<T> currentParent = treeItem.getParent();
if (treeItem.parentLinkCount == 0) {
treeItem.setParent(newParent);
}
boolean parentMatch = currentParent != null && currentParent.equals(newParent);
if (parentMatch) {
if (newParent == null) {
treeItem.parentLinkCount--;
} else {
treeItem.parentLinkCount++;
}
}
}
}
public static class TreeModificationEvent<T> extends Event {
private static final long serialVersionUID = 4741889985221719579L;
public static final EventType<?> ANY = TREE_NOTIFICATION_EVENT;
private transient final TreeItem<T> treeItem;
private final T newValue;
private final List<? extends TreeItem<T>> added;
private final List<? extends TreeItem<T>> removed;
private final ListChangeListener.Change<? extends TreeItem<T>> change;
private final boolean wasExpanded;
private final boolean wasCollapsed;
private boolean wasPermutated;
public TreeModificationEvent(EventType<? extends Event> eventType, TreeItem<T> treeItem) {
this (eventType, treeItem, null);
}
public TreeModificationEvent(EventType<? extends Event> eventType,
TreeItem<T> treeItem, T newValue) {
super(eventType);
this.treeItem = treeItem;
this.newValue = newValue;
this.added = null;
this.removed = null;
this.change = null;
this.wasExpanded = false;
this.wasCollapsed = false;
}
public TreeModificationEvent(EventType<? extends Event> eventType,
TreeItem<T> treeItem, boolean expanded) {
super(eventType);
this.treeItem = treeItem;
this.newValue = null;
this.added = null;
this.removed = null;
this.change = null;
this.wasExpanded = expanded;
this.wasCollapsed = ! expanded;
}
public TreeModificationEvent(EventType<? extends Event> eventType,
TreeItem<T> treeItem,
List<? extends TreeItem<T>> added,
List<? extends TreeItem<T>> removed) {
this(eventType, treeItem, added, removed, null);
}
private TreeModificationEvent(EventType<? extends Event> eventType,
TreeItem<T> treeItem,
List<? extends TreeItem<T>> added,
List<? extends TreeItem<T>> removed,
ListChangeListener.Change<? extends TreeItem<T>> change) {
super(eventType);
this.treeItem = treeItem;
this.newValue = null;
this.added = added;
this.removed = removed;
this.change = change;
this.wasExpanded = false;
this.wasCollapsed = false;
this.wasPermutated = added != null && removed != null &&
added.size() == removed.size() &&
added.containsAll(removed);
}
@Override public TreeItem<T> getSource() {
return this.treeItem;
}
public TreeItem<T> getTreeItem() {
return treeItem;
}
public T getNewValue() {
return newValue;
}
public List<? extends TreeItem<T>> getAddedChildren() {
return added == null ? Collections.<TreeItem<T>>emptyList() : added;
}
public List<? extends TreeItem<T>> getRemovedChildren() {
return removed == null ? Collections.<TreeItem<T>>emptyList() : removed;
}
public int getRemovedSize() {
return getRemovedChildren().size();
}
public int getAddedSize() {
return getAddedChildren().size();
}
public boolean wasExpanded() { return wasExpanded; }
public boolean wasCollapsed() { return wasCollapsed; }
public boolean wasAdded() { return getAddedSize() > 0; }
public boolean wasRemoved() { return getRemovedSize() > 0; }
public boolean wasPermutated() { return wasPermutated; }
int getFrom() { return change == null ? -1 : change.getFrom(); }
int getTo() { return change == null ? -1 : change.getTo(); }
ListChangeListener.Change<? extends TreeItem<T>> getChange() { return change; }
}
}
