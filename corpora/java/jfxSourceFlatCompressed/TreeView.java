package javafx.scene.control;
import javafx.css.converter.SizeConverter;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.behavior.TreeCellBehavior;
import javafx.scene.control.skin.TreeViewSkin;
import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.beans.value.WritableValue;
import javafx.collections.ListChangeListener;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@DefaultProperty("root")
public class TreeView<T> extends Control {
@SuppressWarnings("unchecked")
public static <T> EventType<EditEvent<T>> editAnyEvent() {
return (EventType<EditEvent<T>>) EDIT_ANY_EVENT;
}
private static final EventType<?> EDIT_ANY_EVENT =
new EventType<>(Event.ANY, "TREE_VIEW_EDIT");
@SuppressWarnings("unchecked")
public static <T> EventType<EditEvent<T>> editStartEvent() {
return (EventType<EditEvent<T>>) EDIT_START_EVENT;
}
private static final EventType<?> EDIT_START_EVENT =
new EventType<>(editAnyEvent(), "EDIT_START");
@SuppressWarnings("unchecked")
public static <T> EventType<EditEvent<T>> editCancelEvent() {
return (EventType<EditEvent<T>>) EDIT_CANCEL_EVENT;
}
private static final EventType<?> EDIT_CANCEL_EVENT =
new EventType<>(editAnyEvent(), "EDIT_CANCEL");
@SuppressWarnings("unchecked")
public static <T> EventType<EditEvent<T>> editCommitEvent() {
return (EventType<EditEvent<T>>) EDIT_COMMIT_EVENT;
}
private static final EventType<?> EDIT_COMMIT_EVENT =
new EventType<>(editAnyEvent(), "EDIT_COMMIT");
@Deprecated(since="8u20")
public static int getNodeLevel(TreeItem<?> node) {
if (node == null) return -1;
int level = 0;
TreeItem<?> parent = node.getParent();
while (parent != null) {
level++;
parent = parent.getParent();
}
return level;
}
public TreeView() {
this(null);
}
public TreeView(TreeItem<T> root) {
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.TREE_VIEW);
setRoot(root);
updateExpandedItemCount(root);
MultipleSelectionModel<TreeItem<T>> sm = new TreeViewBitSetSelectionModel<T>(this);
setSelectionModel(sm);
setFocusModel(new TreeViewFocusModel<T>(this));
setOnEditCommit(DEFAULT_EDIT_COMMIT_HANDLER);
}
private boolean expandedItemCountDirty = true;
private Map<Integer, SoftReference<TreeItem<T>>> treeItemCacheMap = new HashMap<>();
private final EventHandler<TreeModificationEvent<T>> rootEvent = e -> {
EventType<?> eventType = e.getEventType();
boolean match = false;
while (eventType != null) {
if (eventType.equals(TreeItem.<T>expandedItemCountChangeEvent())) {
match = true;
break;
}
eventType = eventType.getSuperType();
}
if (match) {
expandedItemCountDirty = true;
requestLayout();
}
};
private WeakEventHandler<TreeModificationEvent<T>> weakRootEventListener;
private ObjectProperty<Callback<TreeView<T>, TreeCell<T>>> cellFactory;
public final void setCellFactory(Callback<TreeView<T>, TreeCell<T>> value) {
cellFactoryProperty().set(value);
}
public final Callback<TreeView<T>, TreeCell<T>> getCellFactory() {
return cellFactory == null ? null : cellFactory.get();
}
public final ObjectProperty<Callback<TreeView<T>, TreeCell<T>>> cellFactoryProperty() {
if (cellFactory == null) {
cellFactory = new SimpleObjectProperty<Callback<TreeView<T>, TreeCell<T>>>(this, "cellFactory");
}
return cellFactory;
}
private ObjectProperty<TreeItem<T>> root = new SimpleObjectProperty<TreeItem<T>>(this, "root") {
private WeakReference<TreeItem<T>> weakOldItem;
@Override protected void invalidated() {
TreeItem<T> oldTreeItem = weakOldItem == null ? null : weakOldItem.get();
if (oldTreeItem != null && weakRootEventListener != null) {
oldTreeItem.removeEventHandler(TreeItem.<T>treeNotificationEvent(), weakRootEventListener);
}
TreeItem<T> root = getRoot();
if (root != null) {
weakRootEventListener = new WeakEventHandler<>(rootEvent);
getRoot().addEventHandler(TreeItem.<T>treeNotificationEvent(), weakRootEventListener);
weakOldItem = new WeakReference<>(root);
}
edit(null);
expandedItemCountDirty = true;
updateRootExpanded();
}
};
public final void setRoot(TreeItem<T> value) {
rootProperty().set(value);
}
public final TreeItem<T> getRoot() {
return root == null ? null : root.get();
}
public final ObjectProperty<TreeItem<T>> rootProperty() {
return root;
}
private BooleanProperty showRoot;
public final void setShowRoot(boolean value) {
showRootProperty().set(value);
}
public final boolean isShowRoot() {
return showRoot == null ? true : showRoot.get();
}
public final BooleanProperty showRootProperty() {
if (showRoot == null) {
showRoot = new SimpleBooleanProperty(this, "showRoot", true) {
@Override protected void invalidated() {
updateRootExpanded();
updateExpandedItemCount(getRoot());
}
};
}
return showRoot;
}
private ObjectProperty<MultipleSelectionModel<TreeItem<T>>> selectionModel;
public final void setSelectionModel(MultipleSelectionModel<TreeItem<T>> value) {
selectionModelProperty().set(value);
}
public final MultipleSelectionModel<TreeItem<T>> getSelectionModel() {
return selectionModel == null ? null : selectionModel.get();
}
public final ObjectProperty<MultipleSelectionModel<TreeItem<T>>> selectionModelProperty() {
if (selectionModel == null) {
selectionModel = new SimpleObjectProperty<MultipleSelectionModel<TreeItem<T>>>(this, "selectionModel");
}
return selectionModel;
}
private ObjectProperty<FocusModel<TreeItem<T>>> focusModel;
public final void setFocusModel(FocusModel<TreeItem<T>> value) {
focusModelProperty().set(value);
}
public final FocusModel<TreeItem<T>> getFocusModel() {
return focusModel == null ? null : focusModel.get();
}
public final ObjectProperty<FocusModel<TreeItem<T>>> focusModelProperty() {
if (focusModel == null) {
focusModel = new SimpleObjectProperty<FocusModel<TreeItem<T>>>(this, "focusModel");
}
return focusModel;
}
private ReadOnlyIntegerWrapper expandedItemCount = new ReadOnlyIntegerWrapper(this, "expandedItemCount", 0);
public final ReadOnlyIntegerProperty expandedItemCountProperty() {
return expandedItemCount.getReadOnlyProperty();
}
private void setExpandedItemCount(int value) {
expandedItemCount.set(value);
}
public final int getExpandedItemCount() {
if (expandedItemCountDirty) {
updateExpandedItemCount(getRoot());
}
return expandedItemCount.get();
}
private DoubleProperty fixedCellSize;
public final void setFixedCellSize(double value) {
fixedCellSizeProperty().set(value);
}
public final double getFixedCellSize() {
return fixedCellSize == null ? Region.USE_COMPUTED_SIZE : fixedCellSize.get();
}
public final DoubleProperty fixedCellSizeProperty() {
if (fixedCellSize == null) {
fixedCellSize = new StyleableDoubleProperty(Region.USE_COMPUTED_SIZE) {
@Override public CssMetaData<TreeView<?>,Number> getCssMetaData() {
return StyleableProperties.FIXED_CELL_SIZE;
}
@Override public Object getBean() {
return TreeView.this;
}
@Override public String getName() {
return "fixedCellSize";
}
};
}
return fixedCellSize;
}
private BooleanProperty editable;
public final void setEditable(boolean value) {
editableProperty().set(value);
}
public final boolean isEditable() {
return editable == null ? false : editable.get();
}
public final BooleanProperty editableProperty() {
if (editable == null) {
editable = new SimpleBooleanProperty(this, "editable", false);
}
return editable;
}
private ReadOnlyObjectWrapper<TreeItem<T>> editingItem;
private void setEditingItem(TreeItem<T> value) {
editingItemPropertyImpl().set(value);
}
public final TreeItem<T> getEditingItem() {
return editingItem == null ? null : editingItem.get();
}
public final ReadOnlyObjectProperty<TreeItem<T>> editingItemProperty() {
return editingItemPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyObjectWrapper<TreeItem<T>> editingItemPropertyImpl() {
if (editingItem == null) {
editingItem = new ReadOnlyObjectWrapper<TreeItem<T>>(this, "editingItem");
}
return editingItem;
}
private ObjectProperty<EventHandler<EditEvent<T>>> onEditStart;
public final void setOnEditStart(EventHandler<EditEvent<T>> value) {
onEditStartProperty().set(value);
}
public final EventHandler<EditEvent<T>> getOnEditStart() {
return onEditStart == null ? null : onEditStart.get();
}
public final ObjectProperty<EventHandler<EditEvent<T>>> onEditStartProperty() {
if (onEditStart == null) {
onEditStart = new SimpleObjectProperty<EventHandler<EditEvent<T>>>(this, "onEditStart") {
@Override protected void invalidated() {
setEventHandler(TreeView.<T>editStartEvent(), get());
}
};
}
return onEditStart;
}
private ObjectProperty<EventHandler<EditEvent<T>>> onEditCommit;
public final void setOnEditCommit(EventHandler<EditEvent<T>> value) {
onEditCommitProperty().set(value);
}
public final EventHandler<EditEvent<T>> getOnEditCommit() {
return onEditCommit == null ? null : onEditCommit.get();
}
public final ObjectProperty<EventHandler<EditEvent<T>>> onEditCommitProperty() {
if (onEditCommit == null) {
onEditCommit = new SimpleObjectProperty<EventHandler<EditEvent<T>>>(this, "onEditCommit") {
@Override protected void invalidated() {
setEventHandler(TreeView.<T>editCommitEvent(), get());
}
};
}
return onEditCommit;
}
private EventHandler<TreeView.EditEvent<T>> DEFAULT_EDIT_COMMIT_HANDLER = t -> {
TreeItem<T> editedItem = t.getTreeItem();
if (editedItem == null) return;
editedItem.setValue(t.getNewValue());
};
private ObjectProperty<EventHandler<EditEvent<T>>> onEditCancel;
public final void setOnEditCancel(EventHandler<EditEvent<T>> value) {
onEditCancelProperty().set(value);
}
public final EventHandler<EditEvent<T>> getOnEditCancel() {
return onEditCancel == null ? null : onEditCancel.get();
}
public final ObjectProperty<EventHandler<EditEvent<T>>> onEditCancelProperty() {
if (onEditCancel == null) {
onEditCancel = new SimpleObjectProperty<EventHandler<EditEvent<T>>>(this, "onEditCancel") {
@Override protected void invalidated() {
setEventHandler(TreeView.<T>editCancelEvent(), get());
}
};
}
return onEditCancel;
}
@Override protected void layoutChildren() {
if (expandedItemCountDirty) {
updateExpandedItemCount(getRoot());
}
super.layoutChildren();
}
public void edit(TreeItem<T> item) {
if (!isEditable()) return;
setEditingItem(item);
}
public void scrollTo(int index) {
ControlUtils.scrollToIndex(this, index);
}
private ObjectProperty<EventHandler<ScrollToEvent<Integer>>> onScrollTo;
public void setOnScrollTo(EventHandler<ScrollToEvent<Integer>> value) {
onScrollToProperty().set(value);
}
public EventHandler<ScrollToEvent<Integer>> getOnScrollTo() {
if( onScrollTo != null ) {
return onScrollTo.get();
}
return null;
}
public ObjectProperty<EventHandler<ScrollToEvent<Integer>>> onScrollToProperty() {
if( onScrollTo == null ) {
onScrollTo = new ObjectPropertyBase<EventHandler<ScrollToEvent<Integer>>>() {
@Override
protected void invalidated() {
setEventHandler(ScrollToEvent.scrollToTopIndex(), get());
}
@Override
public Object getBean() {
return TreeView.this;
}
@Override
public String getName() {
return "onScrollTo";
}
};
}
return onScrollTo;
}
public int getRow(TreeItem<T> item) {
return TreeUtil.getRow(item, getRoot(), expandedItemCountDirty, isShowRoot());
}
public TreeItem<T> getTreeItem(int row) {
if (row < 0) return null;
final int _row = isShowRoot() ? row : (row + 1);
if (expandedItemCountDirty) {
updateExpandedItemCount(getRoot());
} else {
if (treeItemCacheMap.containsKey(_row)) {
SoftReference<TreeItem<T>> treeItemRef = treeItemCacheMap.get(_row);
TreeItem<T> treeItem = treeItemRef.get();
if (treeItem != null) {
return treeItem;
}
}
}
TreeItem<T> treeItem = TreeUtil.getItem(getRoot(), _row, expandedItemCountDirty);
treeItemCacheMap.put(_row, new SoftReference<>(treeItem));
return treeItem;
}
public int getTreeItemLevel(TreeItem<?> node) {
final TreeItem<?> root = getRoot();
if (node == null) return -1;
if (node == root) return 0;
int level = 0;
TreeItem<?> parent = node.getParent();
while (parent != null) {
level++;
if (parent == root) {
break;
}
parent = parent.getParent();
}
return level;
}
@Override protected Skin<?> createDefaultSkin() {
return new TreeViewSkin<T>(this);
}
public void refresh() {
getProperties().put(Properties.RECREATE, Boolean.TRUE);
}
private void updateExpandedItemCount(TreeItem<T> treeItem) {
setExpandedItemCount(TreeUtil.updateExpandedItemCount(treeItem, expandedItemCountDirty, isShowRoot()));
if (expandedItemCountDirty) {
treeItemCacheMap.clear();
}
expandedItemCountDirty = false;
}
private void updateRootExpanded() {
if (!isShowRoot() && getRoot() != null && ! getRoot().isExpanded()) {
getRoot().setExpanded(true);
}
}
private static final String DEFAULT_STYLE_CLASS = "tree-view";
private static class StyleableProperties {
private static final CssMetaData<TreeView<?>,Number> FIXED_CELL_SIZE =
new CssMetaData<TreeView<?>,Number>("-fx-fixed-cell-size",
SizeConverter.getInstance(),
Region.USE_COMPUTED_SIZE) {
@Override public Double getInitialValue(TreeView<?> node) {
return node.getFixedCellSize();
}
@Override public boolean isSettable(TreeView<?> n) {
return n.fixedCellSize == null || !n.fixedCellSize.isBound();
}
@Override public StyleableProperty<Number> getStyleableProperty(TreeView<?> n) {
return (StyleableProperty<Number>)(WritableValue<Number>) n.fixedCellSizeProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
styleables.add(FIXED_CELL_SIZE);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
return getClassCssMetaData();
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case MULTIPLE_SELECTION: {
MultipleSelectionModel<TreeItem<T>> sm = getSelectionModel();
return sm != null && sm.getSelectionMode() == SelectionMode.MULTIPLE;
}
case ROW_COUNT: return getExpandedItemCount();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
public static class EditEvent<T> extends Event {
private static final long serialVersionUID = -4437033058917528976L;
public static final EventType<?> ANY = EDIT_ANY_EVENT;
private final TreeView<T> source;
private final T oldValue;
private final T newValue;
private transient final TreeItem<T> treeItem;
public EditEvent(TreeView<T> source,
EventType<? extends EditEvent> eventType,
TreeItem<T> treeItem, T oldValue, T newValue) {
super(source, Event.NULL_SOURCE_TARGET, eventType);
this.source = source;
this.oldValue = oldValue;
this.newValue = newValue;
this.treeItem = treeItem;
}
@Override public TreeView<T> getSource() {
return source;
}
public TreeItem<T> getTreeItem() {
return treeItem;
}
public T getNewValue() {
return newValue;
}
public T getOldValue() {
return oldValue;
}
}
static class TreeViewBitSetSelectionModel<T> extends MultipleSelectionModelBase<TreeItem<T>> {
private TreeView<T> treeView = null;
public TreeViewBitSetSelectionModel(final TreeView<T> treeView) {
if (treeView == null) {
throw new IllegalArgumentException("TreeView can not be null");
}
this.treeView = treeView;
this.treeView.rootProperty().addListener(weakRootPropertyListener);
showRootListener = o -> {
shiftSelection(0, treeView.isShowRoot() ? 1 : -1, null);
};
this.treeView.showRootProperty().addListener(new WeakInvalidationListener(showRootListener));
updateTreeEventListener(null, treeView.getRoot());
updateDefaultSelection();
}
private void updateTreeEventListener(TreeItem<T> oldRoot, TreeItem<T> newRoot) {
if (oldRoot != null && weakTreeItemListener != null) {
oldRoot.removeEventHandler(TreeItem.<T>expandedItemCountChangeEvent(), weakTreeItemListener);
}
if (newRoot != null) {
weakTreeItemListener = new WeakEventHandler<>(treeItemListener);
newRoot.addEventHandler(TreeItem.<T>expandedItemCountChangeEvent(), weakTreeItemListener);
}
}
private ChangeListener<TreeItem<T>> rootPropertyListener = (observable, oldValue, newValue) -> {
updateDefaultSelection();
updateTreeEventListener(oldValue, newValue);
};
private EventHandler<TreeModificationEvent<T>> treeItemListener = e -> {
if (getSelectedIndex() == -1 && getSelectedItem() == null) return;
final TreeItem<T> treeItem = e.getTreeItem();
if (treeItem == null) return;
treeView.expandedItemCountDirty = true;
int startRow = treeView.getRow(treeItem);
int shift = 0;
ListChangeListener.Change<? extends TreeItem<?>> change = e.getChange();
if (change != null) {
change.next();
}
do {
final int addedSize = change == null ? 0 : change.getAddedSize();
final int removedSize = change == null ? 0 : change.getRemovedSize();
if (e.wasExpanded()) {
shift += treeItem.getExpandedDescendentCount(false) - 1;
startRow++;
} else if (e.wasCollapsed()) {
treeItem.getExpandedDescendentCount(false);
final int count = treeItem.previousExpandedDescendentCount;
final int selectedIndex = getSelectedIndex();
final boolean wasPrimarySelectionInChild =
selectedIndex >= (startRow + 1) &&
selectedIndex < (startRow + count);
boolean wasAnyChildSelected = false;
selectedIndices._beginChange();
final int from = startRow + 1;
final int to = startRow + count;
final List<Integer> removed = new ArrayList<>();
for (int i = from; i < to; i++) {
if (isSelected(i)) {
wasAnyChildSelected = true;
removed.add(i);
}
}
if (!removed.isEmpty()) {
selectedIndices._nextRemove(selectedIndices.indexOf(removed.get(0)), removed);
}
for (int index : removed) {
startAtomic();
clearSelection(index);
stopAtomic();
}
selectedIndices._endChange();
if (wasPrimarySelectionInChild && wasAnyChildSelected) {
select(startRow);
}
shift += -count + 1;
startRow++;
} else if (e.wasPermutated()) {
} else if (e.wasAdded()) {
shift += ControlUtils.isTreeItemIncludingAncestorsExpanded(treeItem) ? addedSize : 0;
startRow = treeView.getRow(e.getChange().getAddedSubList().get(0));
} else if (e.wasRemoved()) {
startRow += e.getFrom() + 1;
final List<Integer> selectedIndices1 = getSelectedIndices();
final int selectedIndex = getSelectedIndex();
final List<TreeItem<T>> selectedItems = getSelectedItems();
final TreeItem<T> selectedItem = getSelectedItem();
final List<? extends TreeItem<T>> removedChildren = e.getChange().getRemoved();
if (ControlUtils.isTreeItemIncludingAncestorsExpanded(treeItem)) {
int lastSelectedSiblingIndex = selectedItems.stream()
.map(item -> ControlUtils.getIndexOfChildWithDescendant(treeItem, item))
.max(Comparator.naturalOrder())
.orElse(-1);
if (e.getFrom() <= lastSelectedSiblingIndex || lastSelectedSiblingIndex == -1) {
shift -= removedSize;
}
}
for (int i = 0; i < selectedIndices1.size() && !selectedItems.isEmpty(); i++) {
int index = selectedIndices1.get(i);
if (index > selectedItems.size()) break;
if (removedChildren.size() == 1 &&
selectedItems.size() == 1 &&
selectedItem != null &&
selectedItem.equals(removedChildren.get(0))) {
if (selectedIndex < getItemCount()) {
final int previousRow = selectedIndex == 0 ? 0 : selectedIndex - 1;
TreeItem<T> newSelectedItem = getModelItem(previousRow);
if (!selectedItem.equals(newSelectedItem)) {
select(newSelectedItem);
}
}
}
}
}
} while (e.getChange() != null && e.getChange().next());
shiftSelection(startRow, shift, null);
if (e.wasAdded() || e.wasRemoved()) {
Integer anchor = TreeCellBehavior.getAnchor(treeView, null);
if (anchor != null && isSelected(anchor + shift)) {
TreeCellBehavior.setAnchor(treeView, anchor + shift, false);
}
}
};
private WeakChangeListener<TreeItem<T>> weakRootPropertyListener =
new WeakChangeListener<>(rootPropertyListener);
private WeakEventHandler<TreeModificationEvent<T>> weakTreeItemListener;
private InvalidationListener showRootListener;
@Override public void selectAll() {
final int anchor = TreeCellBehavior.getAnchor(treeView, -1);
super.selectAll();
TreeCellBehavior.setAnchor(treeView, anchor, false);
}
@Override public void select(TreeItem<T> obj) {
if (obj == null && getSelectionMode() == SelectionMode.SINGLE) {
clearSelection();
return;
}
if (obj != null) {
TreeItem<?> item = obj.getParent();
while (item != null) {
item.setExpanded(true);
item = item.getParent();
}
}
treeView.updateExpandedItemCount(treeView.getRoot());
int row = treeView.getRow(obj);
if (row == -1) {
setSelectedIndex(-1);
setSelectedItem(obj);
} else {
select(row);
}
}
@Override public void clearAndSelect(int row) {
TreeCellBehavior.setAnchor(treeView, row, false);
super.clearAndSelect(row);
}
@Override protected void focus(int itemIndex) {
if (treeView.getFocusModel() != null) {
treeView.getFocusModel().focus(itemIndex);
}
treeView.notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM);
}
@Override protected int getFocusedIndex() {
if (treeView.getFocusModel() == null) return -1;
return treeView.getFocusModel().getFocusedIndex();
}
@Override protected int getItemCount() {
return treeView == null ? 0 : treeView.getExpandedItemCount();
}
@Override public TreeItem<T> getModelItem(int index) {
if (treeView == null) return null;
if (index < 0 || index >= treeView.getExpandedItemCount()) return null;
return treeView.getTreeItem(index);
}
private void updateDefaultSelection() {
clearSelection();
focus(getItemCount() > 0 ? 0 : -1);
}
}
static class TreeViewFocusModel<T> extends FocusModel<TreeItem<T>> {
private final TreeView<T> treeView;
public TreeViewFocusModel(final TreeView<T> treeView) {
this.treeView = treeView;
this.treeView.rootProperty().addListener(weakRootPropertyListener);
updateTreeEventListener(null, treeView.getRoot());
if (treeView.getExpandedItemCount() > 0) {
focus(0);
}
showRootListener = obs -> {
if (isFocused(0)) {
focus(-1);
focus(0);
}
};
treeView.showRootProperty().addListener(new WeakInvalidationListener(showRootListener));
focusedIndexProperty().addListener(o -> {
treeView.notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM);
});
}
private final ChangeListener<TreeItem<T>> rootPropertyListener = (observable, oldValue, newValue) -> {
updateTreeEventListener(oldValue, newValue);
};
private final WeakChangeListener<TreeItem<T>> weakRootPropertyListener =
new WeakChangeListener<>(rootPropertyListener);
private final InvalidationListener showRootListener;
private void updateTreeEventListener(TreeItem<T> oldRoot, TreeItem<T> newRoot) {
if (oldRoot != null && weakTreeItemListener != null) {
oldRoot.removeEventHandler(TreeItem.<T>expandedItemCountChangeEvent(), weakTreeItemListener);
}
if (newRoot != null) {
weakTreeItemListener = new WeakEventHandler<>(treeItemListener);
newRoot.addEventHandler(TreeItem.<T>expandedItemCountChangeEvent(), weakTreeItemListener);
}
}
private EventHandler<TreeModificationEvent<T>> treeItemListener = new EventHandler<TreeModificationEvent<T>>() {
@Override public void handle(TreeModificationEvent<T> e) {
if (getFocusedIndex() == -1) return;
int row = treeView.getRow(e.getTreeItem());
int shift = 0;
if (e.getChange() != null) {
e.getChange().next();
}
do {
if (e.wasExpanded()) {
if (row < getFocusedIndex()) {
shift += e.getTreeItem().getExpandedDescendentCount(false) - 1;
}
} else if (e.wasCollapsed()) {
if (row < getFocusedIndex()) {
shift += -e.getTreeItem().previousExpandedDescendentCount + 1;
}
} else if (e.wasAdded()) {
TreeItem<T> eventTreeItem = e.getTreeItem();
if (ControlUtils.isTreeItemIncludingAncestorsExpanded(eventTreeItem)) {
for (int i = 0; i < e.getAddedChildren().size(); i++) {
TreeItem<T> item = e.getAddedChildren().get(i);
row = treeView.getRow(item);
if (item != null && row <= (shift+getFocusedIndex())) {
shift += item.getExpandedDescendentCount(false);
}
}
}
} else if (e.wasRemoved()) {
row += e.getFrom() + 1;
for (int i = 0; i < e.getRemovedChildren().size(); i++) {
TreeItem<T> item = e.getRemovedChildren().get(i);
if (item != null && item.equals(getFocusedItem())) {
focus(Math.max(0, getFocusedIndex() - 1));
return;
}
}
if (ControlUtils.isTreeItemIncludingAncestorsExpanded(e.getTreeItem())) {
int focusedSiblingRow = ControlUtils.getIndexOfChildWithDescendant(e.getTreeItem(), getFocusedItem());
if (e.getFrom() <= focusedSiblingRow) {
shift -= e.getRemovedSize();
}
}
}
} while (e.getChange() != null && e.getChange().next());
if(shift != 0) {
final int newFocus = getFocusedIndex() + shift;
if (newFocus >= 0) {
Platform.runLater(() -> focus(newFocus));
}
}
}
};
private WeakEventHandler<TreeModificationEvent<T>> weakTreeItemListener;
@Override protected int getItemCount() {
return treeView == null ? -1 : treeView.getExpandedItemCount();
}
@Override protected TreeItem<T> getModelItem(int index) {
if (treeView == null) return null;
if (index < 0 || index >= treeView.getExpandedItemCount()) return null;
return treeView.getTreeItem(index);
}
@Override public void focus(int index) {
if (treeView.expandedItemCountDirty) {
treeView.updateExpandedItemCount(treeView.getRoot());
}
super.focus(index);
}
}
}
