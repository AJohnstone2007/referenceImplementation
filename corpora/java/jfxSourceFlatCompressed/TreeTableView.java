package javafx.scene.control;
import com.sun.javafx.collections.MappingChange;
import com.sun.javafx.collections.NonIterableChange;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.SelectedCellsMap;
import com.sun.javafx.scene.control.behavior.TableCellBehavior;
import com.sun.javafx.scene.control.behavior.TableCellBehaviorBase;
import com.sun.javafx.scene.control.behavior.TreeTableCellBehavior;
import javafx.beans.property.DoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.converter.SizeConverter;
import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import com.sun.javafx.scene.control.TableColumnComparatorBase;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.event.WeakEventHandler;
import javafx.scene.control.skin.TreeTableViewSkin;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.IntPredicate;
import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
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
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.util.Callback;
@DefaultProperty("root")
public class TreeTableView<S> extends Control {
public TreeTableView() {
this(null);
}
public TreeTableView(TreeItem<S> root) {
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.TREE_TABLE_VIEW);
setRoot(root);
updateExpandedItemCount(root);
setSelectionModel(new TreeTableViewArrayListSelectionModel<S>(this));
setFocusModel(new TreeTableViewFocusModel<S>(this));
getColumns().addListener(weakColumnsObserver);
getSortOrder().addListener((ListChangeListener.Change<? extends TreeTableColumn<S, ?>> c) -> {
doSort(TableUtil.SortEventType.SORT_ORDER_CHANGE, c);
});
getProperties().addListener((MapChangeListener<Object, Object>) c -> {
if (c.wasAdded() && TableView.SET_CONTENT_WIDTH.equals(c.getKey())) {
if (c.getValueAdded() instanceof Number) {
setContentWidth((Double) c.getValueAdded());
}
getProperties().remove(TableView.SET_CONTENT_WIDTH);
}
});
pseudoClassStateChanged(PseudoClass.getPseudoClass(getColumnResizePolicy().toString()), true);
isInited = true;
}
@SuppressWarnings("unchecked")
public static <S> EventType<TreeTableView.EditEvent<S>> editAnyEvent() {
return (EventType<TreeTableView.EditEvent<S>>) EDIT_ANY_EVENT;
}
private static final EventType<?> EDIT_ANY_EVENT =
new EventType<>(Event.ANY, "TREE_TABLE_VIEW_EDIT");
@SuppressWarnings("unchecked")
public static <S> EventType<TreeTableView.EditEvent<S>> editStartEvent() {
return (EventType<TreeTableView.EditEvent<S>>) EDIT_START_EVENT;
}
private static final EventType<?> EDIT_START_EVENT =
new EventType<>(editAnyEvent(), "EDIT_START");
@SuppressWarnings("unchecked")
public static <S> EventType<TreeTableView.EditEvent<S>> editCancelEvent() {
return (EventType<TreeTableView.EditEvent<S>>) EDIT_CANCEL_EVENT;
}
private static final EventType<?> EDIT_CANCEL_EVENT =
new EventType<>(editAnyEvent(), "EDIT_CANCEL");
@SuppressWarnings("unchecked")
public static <S> EventType<TreeTableView.EditEvent<S>> editCommitEvent() {
return (EventType<TreeTableView.EditEvent<S>>) EDIT_COMMIT_EVENT;
}
private static final EventType<?> EDIT_COMMIT_EVENT =
new EventType<>(editAnyEvent(), "EDIT_COMMIT");
@Deprecated(since="8u20")
public static int getNodeLevel(TreeItem<?> node) {
return TreeView.getNodeLevel(node);
}
public static final Callback<TreeTableView.ResizeFeatures, Boolean> UNCONSTRAINED_RESIZE_POLICY =
new Callback<TreeTableView.ResizeFeatures, Boolean>() {
@Override public String toString() {
return "unconstrained-resize";
}
@Override public Boolean call(TreeTableView.ResizeFeatures prop) {
double result = TableUtil.resize(prop.getColumn(), prop.getDelta());
return Double.compare(result, 0.0) == 0;
}
};
public static final Callback<TreeTableView.ResizeFeatures, Boolean> CONSTRAINED_RESIZE_POLICY =
new Callback<TreeTableView.ResizeFeatures, Boolean>() {
private boolean isFirstRun = true;
@Override public String toString() {
return "constrained-resize";
}
@Override public Boolean call(TreeTableView.ResizeFeatures prop) {
TreeTableView<?> table = prop.getTable();
List<? extends TableColumnBase<?,?>> visibleLeafColumns = table.getVisibleLeafColumns();
Boolean result = TableUtil.constrainedResize(prop,
isFirstRun,
table.contentWidth,
visibleLeafColumns);
isFirstRun = ! isFirstRun ? false : ! result;
return result;
}
};
public static final Callback<TreeTableView, Boolean> DEFAULT_SORT_POLICY = new Callback<TreeTableView, Boolean>() {
@Override public Boolean call(TreeTableView table) {
try {
TreeItem rootItem = table.getRoot();
if (rootItem == null || rootItem.getChildren().isEmpty()) return false;
TreeSortMode sortMode = table.getSortMode();
if (sortMode == null) return false;
rootItem.lastSortMode = sortMode;
rootItem.lastComparator = table.getComparator();
rootItem.sort();
return true;
} catch (UnsupportedOperationException e) {
return false;
}
}
};
private boolean expandedItemCountDirty = true;
private Map<Integer, SoftReference<TreeItem<S>>> treeItemCacheMap = new HashMap<>();
private final ObservableList<TreeTableColumn<S,?>> columns = FXCollections.observableArrayList();
private final ObservableList<TreeTableColumn<S,?>> visibleLeafColumns = FXCollections.observableArrayList();
private final ObservableList<TreeTableColumn<S,?>> unmodifiableVisibleLeafColumns = FXCollections.unmodifiableObservableList(visibleLeafColumns);
private ObservableList<TreeTableColumn<S,?>> sortOrder = FXCollections.observableArrayList();
double contentWidth;
private boolean isInited = false;
private final EventHandler<TreeItem.TreeModificationEvent<S>> rootEvent = e -> {
EventType<?> eventType = e.getEventType();
boolean match = false;
while (eventType != null) {
if (eventType.equals(TreeItem.<S>expandedItemCountChangeEvent())) {
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
private final ListChangeListener<TreeTableColumn<S,?>> columnsObserver = new ListChangeListener<TreeTableColumn<S,?>>() {
@Override public void onChanged(ListChangeListener.Change<? extends TreeTableColumn<S,?>> c) {
final List<TreeTableColumn<S,?>> columns = getColumns();
while (c.next()) {
if (c.wasAdded()) {
List<TreeTableColumn<S,?>> duplicates = new ArrayList<>();
for (TreeTableColumn<S,?> addedColumn : c.getAddedSubList()) {
if (addedColumn == null) continue;
int count = 0;
for (TreeTableColumn<S,?> column : columns) {
if (addedColumn == column) {
count++;
}
}
if (count > 1) {
duplicates.add(addedColumn);
}
}
if (!duplicates.isEmpty()) {
String titleList = "";
for (TreeTableColumn<S,?> dupe : duplicates) {
titleList += "'" + dupe.getText() + "', ";
}
throw new IllegalStateException("Duplicate TreeTableColumns detected in TreeTableView columns list with titles " + titleList);
}
}
}
c.reset();
List<TreeTableColumn<S,?>> toRemove = new ArrayList<TreeTableColumn<S,?>>();
while (c.next()) {
final List<? extends TreeTableColumn<S, ?>> removed = c.getRemoved();
final List<? extends TreeTableColumn<S, ?>> added = c.getAddedSubList();
if (c.wasRemoved()) {
toRemove.addAll(removed);
for (TreeTableColumn<S,?> tc : removed) {
tc.setTreeTableView(null);
}
}
if (c.wasAdded()) {
toRemove.removeAll(added);
for (TreeTableColumn<S,?> tc : added) {
tc.setTreeTableView(TreeTableView.this);
}
}
TableUtil.removeColumnsListener(removed, weakColumnsObserver);
TableUtil.addColumnsListener(added, weakColumnsObserver);
TableUtil.removeTableColumnListener(c.getRemoved(),
weakColumnVisibleObserver,
weakColumnSortableObserver,
weakColumnSortTypeObserver,
weakColumnComparatorObserver);
TableUtil.addTableColumnListener(c.getAddedSubList(),
weakColumnVisibleObserver,
weakColumnSortableObserver,
weakColumnSortTypeObserver,
weakColumnComparatorObserver);
}
updateVisibleLeafColumns();
sortOrder.removeAll(toRemove);
final TreeTableViewFocusModel<S> fm = getFocusModel();
final TreeTableViewSelectionModel<S> sm = getSelectionModel();
c.reset();
List<TreeTableColumn<S,?>> removed = new ArrayList<>();
List<TreeTableColumn<S,?>> added = new ArrayList<>();
while (c.next()) {
if (c.wasRemoved()) {
removed.addAll(c.getRemoved());
}
if (c.wasAdded()) {
added.addAll(c.getAddedSubList());
}
}
removed.removeAll(added);
if (fm != null) {
TreeTablePosition<S, ?> focusedCell = fm.getFocusedCell();
boolean match = false;
for (TreeTableColumn<S, ?> tc : removed) {
match = focusedCell != null && focusedCell.getTableColumn() == tc;
if (match) {
break;
}
}
if (match) {
int matchingColumnIndex = lastKnownColumnIndex.getOrDefault(focusedCell.getTableColumn(), 0);
int newFocusColumnIndex =
matchingColumnIndex == 0 ? 0 :
Math.min(getVisibleLeafColumns().size() - 1, matchingColumnIndex - 1);
fm.focus(focusedCell.getRow(), getVisibleLeafColumn(newFocusColumnIndex));
}
}
if (sm != null) {
List<TreeTablePosition> selectedCells = new ArrayList<>(sm.getSelectedCells());
for (TreeTablePosition selectedCell : selectedCells) {
boolean match = false;
for (TreeTableColumn<S, ?> tc : removed) {
match = selectedCell != null && selectedCell.getTableColumn() == tc;
if (match) break;
}
if (match) {
int matchingColumnIndex = lastKnownColumnIndex.getOrDefault(selectedCell.getTableColumn(), -1);
if (matchingColumnIndex == -1) continue;
if (sm instanceof TreeTableViewArrayListSelectionModel) {
TreeTablePosition<S,?> fixedTablePosition =
new TreeTablePosition<S,Object>(TreeTableView.this,
selectedCell.getRow(),
selectedCell.getTableColumn());
fixedTablePosition.fixedColumnIndex = matchingColumnIndex;
((TreeTableViewArrayListSelectionModel)sm).clearSelection(fixedTablePosition);
} else {
sm.clearSelection(selectedCell.getRow(), selectedCell.getTableColumn());
}
}
}
}
lastKnownColumnIndex.clear();
for (TreeTableColumn<S,?> tc : getColumns()) {
int index = getVisibleLeafIndex(tc);
if (index > -1) {
lastKnownColumnIndex.put(tc, index);
}
}
}
};
private final WeakHashMap<TreeTableColumn<S,?>, Integer> lastKnownColumnIndex = new WeakHashMap<>();
private final InvalidationListener columnVisibleObserver = valueModel -> {
updateVisibleLeafColumns();
};
private final InvalidationListener columnSortableObserver = valueModel -> {
TreeTableColumn col = (TreeTableColumn) ((BooleanProperty)valueModel).getBean();
if (! getSortOrder().contains(col)) return;
doSort(TableUtil.SortEventType.COLUMN_SORTABLE_CHANGE, col);
};
private final InvalidationListener columnSortTypeObserver = valueModel -> {
TreeTableColumn col = (TreeTableColumn) ((ObjectProperty)valueModel).getBean();
if (! getSortOrder().contains(col)) return;
doSort(TableUtil.SortEventType.COLUMN_SORT_TYPE_CHANGE, col);
};
private final InvalidationListener columnComparatorObserver = valueModel -> {
TreeTableColumn col = (TreeTableColumn) ((SimpleObjectProperty)valueModel).getBean();
if (! getSortOrder().contains(col)) return;
doSort(TableUtil.SortEventType.COLUMN_COMPARATOR_CHANGE, col);
};
private final InvalidationListener cellSelectionModelInvalidationListener = o -> {
boolean isCellSelection = ((BooleanProperty)o).get();
pseudoClassStateChanged(PSEUDO_CLASS_CELL_SELECTION, isCellSelection);
pseudoClassStateChanged(PSEUDO_CLASS_ROW_SELECTION, !isCellSelection);
};
private WeakEventHandler<TreeItem.TreeModificationEvent<S>> weakRootEventListener;
private final WeakInvalidationListener weakColumnVisibleObserver =
new WeakInvalidationListener(columnVisibleObserver);
private final WeakInvalidationListener weakColumnSortableObserver =
new WeakInvalidationListener(columnSortableObserver);
private final WeakInvalidationListener weakColumnSortTypeObserver =
new WeakInvalidationListener(columnSortTypeObserver);
private final WeakInvalidationListener weakColumnComparatorObserver =
new WeakInvalidationListener(columnComparatorObserver);
private final WeakListChangeListener<TreeTableColumn<S,?>> weakColumnsObserver =
new WeakListChangeListener<TreeTableColumn<S,?>>(columnsObserver);
private final WeakInvalidationListener weakCellSelectionModelInvalidationListener =
new WeakInvalidationListener(cellSelectionModelInvalidationListener);
private ObjectProperty<TreeItem<S>> root = new SimpleObjectProperty<TreeItem<S>>(this, "root") {
private WeakReference<TreeItem<S>> weakOldItem;
@Override protected void invalidated() {
TreeItem<S> oldTreeItem = weakOldItem == null ? null : weakOldItem.get();
if (oldTreeItem != null && weakRootEventListener != null) {
oldTreeItem.removeEventHandler(TreeItem.<S>treeNotificationEvent(), weakRootEventListener);
}
TreeItem<S> root = getRoot();
if (root != null) {
weakRootEventListener = new WeakEventHandler<>(rootEvent);
getRoot().addEventHandler(TreeItem.<S>treeNotificationEvent(), weakRootEventListener);
weakOldItem = new WeakReference<>(root);
}
getSortOrder().clear();
expandedItemCountDirty = true;
updateRootExpanded();
}
};
public final void setRoot(TreeItem<S> value) {
rootProperty().set(value);
}
public final TreeItem<S> getRoot() {
return root == null ? null : root.get();
}
public final ObjectProperty<TreeItem<S>> rootProperty() {
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
private ObjectProperty<TreeTableColumn<S,?>> treeColumn;
public final ObjectProperty<TreeTableColumn<S,?>> treeColumnProperty() {
if (treeColumn == null) {
treeColumn = new SimpleObjectProperty<>(this, "treeColumn", null);
}
return treeColumn;
}
public final void setTreeColumn(TreeTableColumn<S,?> value) {
treeColumnProperty().set(value);
}
public final TreeTableColumn<S,?> getTreeColumn() {
return treeColumn == null ? null : treeColumn.get();
}
private ObjectProperty<TreeTableViewSelectionModel<S>> selectionModel;
public final void setSelectionModel(TreeTableViewSelectionModel<S> value) {
selectionModelProperty().set(value);
}
public final TreeTableViewSelectionModel<S> getSelectionModel() {
return selectionModel == null ? null : selectionModel.get();
}
public final ObjectProperty<TreeTableViewSelectionModel<S>> selectionModelProperty() {
if (selectionModel == null) {
selectionModel = new SimpleObjectProperty<TreeTableViewSelectionModel<S>>(this, "selectionModel") {
TreeTableViewSelectionModel<S> oldValue = null;
@Override protected void invalidated() {
if (oldValue != null) {
oldValue.cellSelectionEnabledProperty().removeListener(weakCellSelectionModelInvalidationListener);
if (oldValue instanceof TreeTableViewArrayListSelectionModel) {
((TreeTableViewArrayListSelectionModel)oldValue).dispose();
}
}
oldValue = get();
if (oldValue != null) {
oldValue.cellSelectionEnabledProperty().addListener(weakCellSelectionModelInvalidationListener);
weakCellSelectionModelInvalidationListener.invalidated(oldValue.cellSelectionEnabledProperty());
}
}
};
}
return selectionModel;
}
private ObjectProperty<TreeTableViewFocusModel<S>> focusModel;
public final void setFocusModel(TreeTableViewFocusModel<S> value) {
focusModelProperty().set(value);
}
public final TreeTableViewFocusModel<S> getFocusModel() {
return focusModel == null ? null : focusModel.get();
}
public final ObjectProperty<TreeTableViewFocusModel<S>> focusModelProperty() {
if (focusModel == null) {
focusModel = new SimpleObjectProperty<TreeTableViewFocusModel<S>>(this, "focusModel");
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
private ReadOnlyObjectWrapper<TreeTablePosition<S,?>> editingCell;
private void setEditingCell(TreeTablePosition<S,?> value) {
editingCellPropertyImpl().set(value);
}
public final TreeTablePosition<S,?> getEditingCell() {
return editingCell == null ? null : editingCell.get();
}
public final ReadOnlyObjectProperty<TreeTablePosition<S,?>> editingCellProperty() {
return editingCellPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyObjectWrapper<TreeTablePosition<S,?>> editingCellPropertyImpl() {
if (editingCell == null) {
editingCell = new ReadOnlyObjectWrapper<TreeTablePosition<S,?>>(this, "editingCell");
}
return editingCell;
}
private BooleanProperty tableMenuButtonVisible;
public final BooleanProperty tableMenuButtonVisibleProperty() {
if (tableMenuButtonVisible == null) {
tableMenuButtonVisible = new SimpleBooleanProperty(this, "tableMenuButtonVisible");
}
return tableMenuButtonVisible;
}
public final void setTableMenuButtonVisible (boolean value) {
tableMenuButtonVisibleProperty().set(value);
}
public final boolean isTableMenuButtonVisible() {
return tableMenuButtonVisible == null ? false : tableMenuButtonVisible.get();
}
private ObjectProperty<Callback<TreeTableView.ResizeFeatures, Boolean>> columnResizePolicy;
public final void setColumnResizePolicy(Callback<TreeTableView.ResizeFeatures, Boolean> callback) {
columnResizePolicyProperty().set(callback);
}
public final Callback<TreeTableView.ResizeFeatures, Boolean> getColumnResizePolicy() {
return columnResizePolicy == null ? UNCONSTRAINED_RESIZE_POLICY : columnResizePolicy.get();
}
public final ObjectProperty<Callback<TreeTableView.ResizeFeatures, Boolean>> columnResizePolicyProperty() {
if (columnResizePolicy == null) {
columnResizePolicy = new SimpleObjectProperty<Callback<TreeTableView.ResizeFeatures, Boolean>>(this, "columnResizePolicy", UNCONSTRAINED_RESIZE_POLICY) {
private Callback<TreeTableView.ResizeFeatures, Boolean> oldPolicy;
@Override protected void invalidated() {
if (isInited) {
get().call(new TreeTableView.ResizeFeatures(TreeTableView.this, null, 0.0));
if (oldPolicy != null) {
PseudoClass state = PseudoClass.getPseudoClass(oldPolicy.toString());
pseudoClassStateChanged(state, false);
}
if (get() != null) {
PseudoClass state = PseudoClass.getPseudoClass(get().toString());
pseudoClassStateChanged(state, true);
}
oldPolicy = get();
}
}
};
}
return columnResizePolicy;
}
private ObjectProperty<Callback<TreeTableView<S>, TreeTableRow<S>>> rowFactory;
public final ObjectProperty<Callback<TreeTableView<S>, TreeTableRow<S>>> rowFactoryProperty() {
if (rowFactory == null) {
rowFactory = new SimpleObjectProperty<Callback<TreeTableView<S>, TreeTableRow<S>>>(this, "rowFactory");
}
return rowFactory;
}
public final void setRowFactory(Callback<TreeTableView<S>, TreeTableRow<S>> value) {
rowFactoryProperty().set(value);
}
public final Callback<TreeTableView<S>, TreeTableRow<S>> getRowFactory() {
return rowFactory == null ? null : rowFactory.get();
}
private ObjectProperty<Node> placeholder;
public final ObjectProperty<Node> placeholderProperty() {
if (placeholder == null) {
placeholder = new SimpleObjectProperty<Node>(this, "placeholder");
}
return placeholder;
}
public final void setPlaceholder(Node value) {
placeholderProperty().set(value);
}
public final Node getPlaceholder() {
return placeholder == null ? null : placeholder.get();
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
@Override public CssMetaData<TreeTableView<?>,Number> getCssMetaData() {
return StyleableProperties.FIXED_CELL_SIZE;
}
@Override public Object getBean() {
return TreeTableView.this;
}
@Override public String getName() {
return "fixedCellSize";
}
};
}
return fixedCellSize;
}
private ObjectProperty<TreeSortMode> sortMode;
public final ObjectProperty<TreeSortMode> sortModeProperty() {
if (sortMode == null) {
sortMode = new SimpleObjectProperty<>(this, "sortMode", TreeSortMode.ALL_DESCENDANTS);
}
return sortMode;
}
public final void setSortMode(TreeSortMode value) {
sortModeProperty().set(value);
}
public final TreeSortMode getSortMode() {
return sortMode == null ? TreeSortMode.ALL_DESCENDANTS : sortMode.get();
}
private ReadOnlyObjectWrapper<Comparator<TreeItem<S>>> comparator;
private void setComparator(Comparator<TreeItem<S>> value) {
comparatorPropertyImpl().set(value);
}
public final Comparator<TreeItem<S>> getComparator() {
return comparator == null ? null : comparator.get();
}
public final ReadOnlyObjectProperty<Comparator<TreeItem<S>>> comparatorProperty() {
return comparatorPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyObjectWrapper<Comparator<TreeItem<S>>> comparatorPropertyImpl() {
if (comparator == null) {
comparator = new ReadOnlyObjectWrapper<>(this, "comparator");
}
return comparator;
}
private ObjectProperty<Callback<TreeTableView<S>, Boolean>> sortPolicy;
public final void setSortPolicy(Callback<TreeTableView<S>, Boolean> callback) {
sortPolicyProperty().set(callback);
}
@SuppressWarnings("unchecked")
public final Callback<TreeTableView<S>, Boolean> getSortPolicy() {
return sortPolicy == null ?
(Callback<TreeTableView<S>, Boolean>)(Object) DEFAULT_SORT_POLICY :
sortPolicy.get();
}
@SuppressWarnings("unchecked")
public final ObjectProperty<Callback<TreeTableView<S>, Boolean>> sortPolicyProperty() {
if (sortPolicy == null) {
sortPolicy = new SimpleObjectProperty<Callback<TreeTableView<S>, Boolean>>(
this, "sortPolicy", (Callback<TreeTableView<S>, Boolean>)(Object) DEFAULT_SORT_POLICY) {
@Override protected void invalidated() {
sort();
}
};
}
return sortPolicy;
}
private ObjectProperty<EventHandler<SortEvent<TreeTableView<S>>>> onSort;
public void setOnSort(EventHandler<SortEvent<TreeTableView<S>>> value) {
onSortProperty().set(value);
}
public EventHandler<SortEvent<TreeTableView<S>>> getOnSort() {
if( onSort != null ) {
return onSort.get();
}
return null;
}
public ObjectProperty<EventHandler<SortEvent<TreeTableView<S>>>> onSortProperty() {
if( onSort == null ) {
onSort = new ObjectPropertyBase<EventHandler<SortEvent<TreeTableView<S>>>>() {
@Override protected void invalidated() {
EventType<SortEvent<TreeTableView<S>>> eventType = SortEvent.sortEvent();
EventHandler<SortEvent<TreeTableView<S>>> eventHandler = get();
setEventHandler(eventType, eventHandler);
}
@Override public Object getBean() {
return TreeTableView.this;
}
@Override public String getName() {
return "onSort";
}
};
}
return onSort;
}
@Override protected void layoutChildren() {
if (expandedItemCountDirty) {
updateExpandedItemCount(getRoot());
}
super.layoutChildren();
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
@Override protected void invalidated() {
setEventHandler(ScrollToEvent.scrollToTopIndex(), get());
}
@Override public Object getBean() {
return TreeTableView.this;
}
@Override public String getName() {
return "onScrollTo";
}
};
}
return onScrollTo;
}
public void scrollToColumn(TreeTableColumn<S, ?> column) {
ControlUtils.scrollToColumn(this, column);
}
public void scrollToColumnIndex(int columnIndex) {
if( getColumns() != null ) {
ControlUtils.scrollToColumn(this, getColumns().get(columnIndex));
}
}
private ObjectProperty<EventHandler<ScrollToEvent<TreeTableColumn<S, ?>>>> onScrollToColumn;
public void setOnScrollToColumn(EventHandler<ScrollToEvent<TreeTableColumn<S, ?>>> value) {
onScrollToColumnProperty().set(value);
}
public EventHandler<ScrollToEvent<TreeTableColumn<S, ?>>> getOnScrollToColumn() {
if( onScrollToColumn != null ) {
return onScrollToColumn.get();
}
return null;
}
public ObjectProperty<EventHandler<ScrollToEvent<TreeTableColumn<S, ?>>>> onScrollToColumnProperty() {
if( onScrollToColumn == null ) {
onScrollToColumn = new ObjectPropertyBase<EventHandler<ScrollToEvent<TreeTableColumn<S, ?>>>>() {
@Override
protected void invalidated() {
EventType<ScrollToEvent<TreeTableColumn<S, ?>>> type = ScrollToEvent.scrollToColumn();
setEventHandler(type, get());
}
@Override
public Object getBean() {
return TreeTableView.this;
}
@Override
public String getName() {
return "onScrollToColumn";
}
};
}
return onScrollToColumn;
}
public int getRow(TreeItem<S> item) {
return TreeUtil.getRow(item, getRoot(), expandedItemCountDirty, isShowRoot());
}
public TreeItem<S> getTreeItem(int row) {
if (row < 0) return null;
final int _row = isShowRoot() ? row : (row + 1);
if (expandedItemCountDirty) {
updateExpandedItemCount(getRoot());
} else {
if (treeItemCacheMap.containsKey(_row)) {
SoftReference<TreeItem<S>> treeItemRef = treeItemCacheMap.get(_row);
TreeItem<S> treeItem = treeItemRef.get();
if (treeItem != null) {
return treeItem;
}
}
}
TreeItem<S> treeItem = TreeUtil.getItem(getRoot(), _row, expandedItemCountDirty);
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
public final ObservableList<TreeTableColumn<S,?>> getColumns() {
return columns;
}
public final ObservableList<TreeTableColumn<S,?>> getSortOrder() {
return sortOrder;
}
public boolean resizeColumn(TreeTableColumn<S,?> column, double delta) {
if (column == null || Double.compare(delta, 0.0) == 0) return false;
boolean allowed = getColumnResizePolicy().call(new TreeTableView.ResizeFeatures<S>(TreeTableView.this, column, delta));
if (!allowed) return false;
return true;
}
public void edit(int row, TreeTableColumn<S,?> column) {
if (!isEditable() || (column != null && ! column.isEditable())) {
return;
}
if (row < 0 && column == null) {
setEditingCell(null);
} else {
setEditingCell(new TreeTablePosition<>(this, row, column));
}
}
public ObservableList<TreeTableColumn<S,?>> getVisibleLeafColumns() {
return unmodifiableVisibleLeafColumns;
}
public int getVisibleLeafIndex(TreeTableColumn<S,?> column) {
return getVisibleLeafColumns().indexOf(column);
}
public TreeTableColumn<S,?> getVisibleLeafColumn(int column) {
if (column < 0 || column >= visibleLeafColumns.size()) return null;
return visibleLeafColumns.get(column);
}
private boolean sortingInProgress;
boolean isSortingInProgress() {
return sortingInProgress;
}
public void sort() {
sortingInProgress = true;
final ObservableList<TreeTableColumn<S,?>> sortOrder = getSortOrder();
final Comparator<TreeItem<S>> oldComparator = getComparator();
setComparator(sortOrder.isEmpty() ? null : new TableColumnComparatorBase.TreeTableColumnComparator(sortOrder));
SortEvent<TreeTableView<S>> sortEvent = new SortEvent<>(TreeTableView.this, TreeTableView.this);
fireEvent(sortEvent);
if (sortEvent.isConsumed()) {
sortingInProgress = false;
return;
}
final List<TreeTablePosition<S,?>> prevState = new ArrayList<>(getSelectionModel().getSelectedCells());
final int itemCount = prevState.size();
getSelectionModel().startAtomic();
Callback<TreeTableView<S>, Boolean> sortPolicy = getSortPolicy();
if (sortPolicy == null) {
sortingInProgress = false;
return;
}
Boolean success = sortPolicy.call(this);
if (getSortMode() == TreeSortMode.ALL_DESCENDANTS) {
Set<TreeItem<S>> sortedParents = new HashSet<>();
for (TreeTablePosition<S,?> selectedPosition : prevState) {
if (selectedPosition.getTreeItem() != null) {
TreeItem<S> parent = selectedPosition.getTreeItem().getParent();
while (parent != null && sortedParents.add(parent)) {
parent.getChildren();
parent = parent.getParent();
}
}
}
}
getSelectionModel().stopAtomic();
if (success == null || ! success) {
sortLock = true;
TableUtil.handleSortFailure(sortOrder, lastSortEventType, lastSortEventSupportInfo);
setComparator(oldComparator);
sortLock = false;
} else {
if (getSelectionModel() instanceof TreeTableViewArrayListSelectionModel) {
final TreeTableViewArrayListSelectionModel<S> sm = (TreeTableViewArrayListSelectionModel<S>) getSelectionModel();
final ObservableList<TreeTablePosition<S, ?>> newState = sm.getSelectedCells();
List<TreeTablePosition<S, ?>> removed = new ArrayList<>();
for (int i = 0; i < itemCount; i++) {
TreeTablePosition<S, ?> prevItem = prevState.get(i);
if (!newState.contains(prevItem)) {
removed.add(prevItem);
}
}
if (!removed.isEmpty()) {
ListChangeListener.Change<TreeTablePosition<S, ?>> c = new NonIterableChange.GenericAddRemoveChange<>(0, itemCount, removed, newState);
sm.fireCustomSelectedCellsListChangeEvent(c);
}
}
getSelectionModel().setSelectedIndex(getRow(getSelectionModel().getSelectedItem()));
getFocusModel().focus(getSelectionModel().getSelectedIndex());
}
sortingInProgress = false;
}
public void refresh() {
getProperties().put(Properties.RECREATE, Boolean.TRUE);
}
private boolean sortLock = false;
private TableUtil.SortEventType lastSortEventType = null;
private Object[] lastSortEventSupportInfo = null;
private void doSort(final TableUtil.SortEventType sortEventType, final Object... supportInfo) {
if (sortLock) {
return;
}
this.lastSortEventType = sortEventType;
this.lastSortEventSupportInfo = supportInfo;
sort();
this.lastSortEventType = null;
this.lastSortEventSupportInfo = null;
}
private void updateExpandedItemCount(TreeItem<S> treeItem) {
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
private void setContentWidth(double contentWidth) {
this.contentWidth = contentWidth;
if (isInited) {
getColumnResizePolicy().call(new TreeTableView.ResizeFeatures<S>(TreeTableView.this, null, 0.0));
}
}
private void updateVisibleLeafColumns() {
List<TreeTableColumn<S,?>> cols = new ArrayList<TreeTableColumn<S,?>>();
buildVisibleLeafColumns(getColumns(), cols);
visibleLeafColumns.setAll(cols);
getColumnResizePolicy().call(new TreeTableView.ResizeFeatures<S>(TreeTableView.this, null, 0.0));
}
private void buildVisibleLeafColumns(List<TreeTableColumn<S,?>> cols, List<TreeTableColumn<S,?>> vlc) {
for (TreeTableColumn<S,?> c : cols) {
if (c == null) continue;
boolean hasChildren = ! c.getColumns().isEmpty();
if (hasChildren) {
buildVisibleLeafColumns(c.getColumns(), vlc);
} else if (c.isVisible()) {
vlc.add(c);
}
}
}
private static final String DEFAULT_STYLE_CLASS = "tree-table-view";
private static final PseudoClass PSEUDO_CLASS_CELL_SELECTION =
PseudoClass.getPseudoClass("cell-selection");
private static final PseudoClass PSEUDO_CLASS_ROW_SELECTION =
PseudoClass.getPseudoClass("row-selection");
private static class StyleableProperties {
private static final CssMetaData<TreeTableView<?>,Number> FIXED_CELL_SIZE =
new CssMetaData<TreeTableView<?>,Number>("-fx-fixed-cell-size",
SizeConverter.getInstance(),
Region.USE_COMPUTED_SIZE) {
@Override public Double getInitialValue(TreeTableView<?> node) {
return node.getFixedCellSize();
}
@Override public boolean isSettable(TreeTableView<?> n) {
return n.fixedCellSize == null || !n.fixedCellSize.isBound();
}
@Override public StyleableProperty<Number> getStyleableProperty(TreeTableView<?> n) {
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
@Override protected Skin<?> createDefaultSkin() {
return new TreeTableViewSkin<S>(this);
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case ROW_COUNT: return getExpandedItemCount();
case COLUMN_COUNT: return getVisibleLeafColumns().size();
case SELECTED_ITEMS: {
@SuppressWarnings("unchecked")
ObservableList<TreeTableRow<S>> rows = (ObservableList<TreeTableRow<S>>)super.queryAccessibleAttribute(attribute, parameters);
List<Node> selection = new ArrayList<>();
for (TreeTableRow<S> row : rows) {
@SuppressWarnings("unchecked")
ObservableList<Node> cells = (ObservableList<Node>)row.queryAccessibleAttribute(attribute, parameters);
if (cells != null) selection.addAll(cells);
}
return FXCollections.observableArrayList(selection);
}
case FOCUS_ITEM: {
Node row = (Node)super.queryAccessibleAttribute(attribute, parameters);
if (row == null) return null;
Node cell = (Node)row.queryAccessibleAttribute(attribute, parameters);
return cell != null ? cell : row;
}
case CELL_AT_ROW_COLUMN: {
@SuppressWarnings("unchecked")
TreeTableRow<S> row = (TreeTableRow<S>)super.queryAccessibleAttribute(attribute, parameters);
return row != null ? row.queryAccessibleAttribute(attribute, parameters) : null;
}
case MULTIPLE_SELECTION: {
TreeTableViewSelectionModel<S> sm = getSelectionModel();
return sm != null && sm.getSelectionMode() == SelectionMode.MULTIPLE;
}
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
public static class ResizeFeatures<S> extends ResizeFeaturesBase<TreeItem<S>> {
private TreeTableView<S> treeTable;
public ResizeFeatures(TreeTableView<S> treeTable, TreeTableColumn<S,?> column, Double delta) {
super(column, delta);
this.treeTable = treeTable;
}
@Override public TreeTableColumn<S,?> getColumn() {
return (TreeTableColumn<S,?>) super.getColumn();
}
public TreeTableView<S> getTable() { return treeTable; }
}
public static class EditEvent<S> extends Event {
private static final long serialVersionUID = -4437033058917528976L;
public static final EventType<?> ANY = EDIT_ANY_EVENT;
private final TreeTableView<S> source;
private final S oldValue;
private final S newValue;
private transient final TreeItem<S> treeItem;
public EditEvent(TreeTableView<S> source,
EventType<? extends TreeTableView.EditEvent> eventType,
TreeItem<S> treeItem, S oldValue, S newValue) {
super(source, Event.NULL_SOURCE_TARGET, eventType);
this.source = source;
this.oldValue = oldValue;
this.newValue = newValue;
this.treeItem = treeItem;
}
@Override public TreeTableView<S> getSource() {
return source;
}
public TreeItem<S> getTreeItem() {
return treeItem;
}
public S getNewValue() {
return newValue;
}
public S getOldValue() {
return oldValue;
}
}
public static abstract class TreeTableViewSelectionModel<S> extends
TableSelectionModel<TreeItem<S>> {
private final TreeTableView<S> treeTableView;
public TreeTableViewSelectionModel(final TreeTableView<S> treeTableView) {
if (treeTableView == null) {
throw new NullPointerException("TreeTableView can not be null");
}
this.treeTableView = treeTableView;
}
public abstract ObservableList<TreeTablePosition<S,?>> getSelectedCells();
public TreeTableView<S> getTreeTableView() {
return treeTableView;
}
@Override public TreeItem<S> getModelItem(int index) {
return treeTableView.getTreeItem(index);
}
@Override protected int getItemCount() {
return treeTableView.getExpandedItemCount();
}
@Override public void focus(int row) {
focus(row, null);
}
@Override public int getFocusedIndex() {
return getFocusedCell().getRow();
}
@Override public void selectRange(int minRow, TableColumnBase<TreeItem<S>,?> minColumn,
int maxRow, TableColumnBase<TreeItem<S>,?> maxColumn) {
final int minColumnIndex = treeTableView.getVisibleLeafIndex((TreeTableColumn<S,?>)minColumn);
final int maxColumnIndex = treeTableView.getVisibleLeafIndex((TreeTableColumn<S,?>)maxColumn);
for (int _row = minRow; _row <= maxRow; _row++) {
for (int _col = minColumnIndex; _col <= maxColumnIndex; _col++) {
select(_row, treeTableView.getVisibleLeafColumn(_col));
}
}
}
private void focus(int row, TreeTableColumn<S,?> column) {
focus(new TreeTablePosition<>(getTreeTableView(), row, column));
}
private void focus(TreeTablePosition<S,?> pos) {
if (getTreeTableView().getFocusModel() == null) return;
getTreeTableView().getFocusModel().focus(pos.getRow(), pos.getTableColumn());
}
private TreeTablePosition<S,?> getFocusedCell() {
if (treeTableView.getFocusModel() == null) {
return new TreeTablePosition<>(treeTableView, -1, null);
}
return treeTableView.getFocusModel().getFocusedCell();
}
}
static class TreeTableViewArrayListSelectionModel<S> extends TreeTableViewSelectionModel<S> {
private final MappingChange.Map<TreeTablePosition<S,?>,Integer> cellToIndicesMap = f -> f.getRow();
private TreeTableView<S> treeTableView = null;
public TreeTableViewArrayListSelectionModel(final TreeTableView<S> treeTableView) {
super(treeTableView);
this.treeTableView = treeTableView;
this.treeTableView.rootProperty().addListener(weakRootPropertyListener);
this.treeTableView.showRootProperty().addListener(showRootPropertyListener);
updateTreeEventListener(null, treeTableView.getRoot());
selectedCellsMap = new SelectedCellsMap<TreeTablePosition<S,?>>(this::fireCustomSelectedCellsListChangeEvent) {
@Override public boolean isCellSelectionEnabled() {
return TreeTableViewArrayListSelectionModel.this.isCellSelectionEnabled();
}
};
selectedCellsSeq = new ReadOnlyUnbackedObservableList<TreeTablePosition<S,?>>() {
@Override public TreeTablePosition<S,?> get(int i) {
return selectedCellsMap.get(i);
}
@Override public int size() {
return selectedCellsMap.size();
}
};
updateDefaultSelection();
cellSelectionEnabledProperty().addListener(o -> {
updateDefaultSelection();
TableCellBehaviorBase.setAnchor(treeTableView, getFocusedCell(), true);
});
}
private void dispose() {
this.treeTableView.rootProperty().removeListener(weakRootPropertyListener);
this.treeTableView.showRootProperty().removeListener(showRootPropertyListener);
TreeItem<S> root = this.treeTableView.getRoot();
if (root != null) {
root.removeEventHandler(TreeItem.<S>expandedItemCountChangeEvent(), weakTreeItemListener);
}
}
private void updateTreeEventListener(TreeItem<S> oldRoot, TreeItem<S> newRoot) {
if (oldRoot != null && weakTreeItemListener != null) {
oldRoot.removeEventHandler(TreeItem.<S>expandedItemCountChangeEvent(), weakTreeItemListener);
}
if (newRoot != null) {
weakTreeItemListener = new WeakEventHandler<>(treeItemListener);
newRoot.addEventHandler(TreeItem.<S>expandedItemCountChangeEvent(), weakTreeItemListener);
}
}
private ChangeListener<TreeItem<S>> rootPropertyListener = (observable, oldValue, newValue) -> {
updateDefaultSelection();
updateTreeEventListener(oldValue, newValue);
};
private InvalidationListener showRootPropertyListener = o -> {
shiftSelection(0, treeTableView.isShowRoot() ? 1 : -1, null);
};
private EventHandler<TreeItem.TreeModificationEvent<S>> treeItemListener = new EventHandler<>() {
@Override public void handle(TreeItem.TreeModificationEvent<S> e) {
if (getSelectedIndex() == -1 && getSelectedItem() == null) return;
final TreeItem<S> treeItem = e.getTreeItem();
if (treeItem == null) return;
final int oldSelectedIndex = getSelectedIndex();
treeTableView.expandedItemCountDirty = true;
int startRow = treeTableView.getRow(treeItem);
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
final boolean isCellSelectionMode = isCellSelectionEnabled();
ObservableList<TreeTableColumn<S, ?>> columns = getTreeTableView().getVisibleLeafColumns();
selectedIndices._beginChange();
final int from = startRow + 1;
final int to = startRow + count;
final List<Integer> removed = new ArrayList<>();
TreeTableColumn<S, ?> selectedColumn = null;
for (int i = from; i < to; i++) {
if (isCellSelectionMode) {
for (int column = 0; column < columns.size(); column++) {
final TreeTableColumn<S, ?> col = columns.get(column);
if (isSelected(i, col)) {
wasAnyChildSelected = true;
clearSelection(i, col);
selectedColumn = col;
}
}
} else {
if (isSelected(i)) {
wasAnyChildSelected = true;
removed.add(i);
}
}
}
if (!removed.isEmpty()) {
selectedIndices._nextRemove(selectedIndices.indexOf(removed.get(0)), removed);
}
for (int index : removed) {
startAtomic();
clearSelection(new TreeTablePosition<>(treeTableView, index, null, false));
stopAtomic();
}
selectedIndices._endChange();
if (wasPrimarySelectionInChild && wasAnyChildSelected) {
select(startRow, selectedColumn);
}
shift += -count + 1;
startRow++;
} else if (e.wasPermutated()) {
List<TreeTablePosition<S, ?>> currentSelection = new ArrayList<>(selectedCellsMap.getSelectedCells());
List<TreeTablePosition<S, ?>> updatedSelection = new ArrayList<>();
boolean selectionIndicesChanged = false;
for (TreeTablePosition<S, ?> selectedCell : currentSelection) {
int newRow = treeTableView.getRow(selectedCell.getTreeItem());
if (selectedCell.getRow() != newRow) {
selectionIndicesChanged = true;
}
updatedSelection.add(new TreeTablePosition<>(selectedCell, newRow));
}
if (selectionIndicesChanged) {
if (treeTableView.isSortingInProgress()) {
startAtomic();
selectedCellsMap.setAll(updatedSelection);
stopAtomic();
} else {
startAtomic();
quietClearSelection();
stopAtomic();
selectedCellsMap.setAll(updatedSelection);
int selectedIndex = treeTableView.getRow(getSelectedItem());
setSelectedIndex(selectedIndex);
focus(selectedIndex);
}
}
} else if (e.wasAdded()) {
shift += ControlUtils.isTreeItemIncludingAncestorsExpanded(treeItem) ? addedSize : 0;
startRow = treeTableView.getRow(e.getChange().getAddedSubList().get(0));
TreeTablePosition<S, ?> anchor = TreeTableCellBehavior.getAnchor(treeTableView, null);
if (anchor != null && anchor.getRow() >= startRow) {
boolean isAnchorSelected = isSelected(anchor.getRow(), anchor.getTableColumn());
if (isAnchorSelected) {
TreeTablePosition<S, ?> newAnchor = new TreeTablePosition<>(treeTableView, anchor.getRow() + shift, anchor.getTableColumn());
TreeTableCellBehavior.setAnchor(treeTableView, newAnchor, false);
}
}
} else if (e.wasRemoved()) {
startRow += e.getFrom() + 1;
final List<Integer> selectedIndices = getSelectedIndices();
final List<TreeItem<S>> selectedItems = getSelectedItems();
final TreeItem<S> selectedItem = getSelectedItem();
final List<? extends TreeItem<S>> removedChildren = e.getChange().getRemoved();
if (ControlUtils.isTreeItemIncludingAncestorsExpanded(treeItem)) {
int lastSelectedSiblingIndex = selectedItems.stream()
.map(item -> ControlUtils.getIndexOfChildWithDescendant(treeItem, item))
.max(Comparator.naturalOrder())
.orElse(-1);
if (e.getFrom() <= lastSelectedSiblingIndex || lastSelectedSiblingIndex == -1) {
shift -= removedSize;
}
}
for (int i = 0; i < selectedIndices.size() && !selectedItems.isEmpty(); i++) {
int index = selectedIndices.get(i);
if (index > selectedItems.size()) break;
if (removedChildren.size() == 1 &&
selectedItems.size() == 1 &&
selectedItem != null &&
selectedItem.equals(removedChildren.get(0))) {
if (oldSelectedIndex < getItemCount()) {
final int previousRow = oldSelectedIndex == 0 ? 0 : oldSelectedIndex - 1;
TreeItem<S> newSelectedItem = getModelItem(previousRow);
if (!selectedItem.equals(newSelectedItem)) {
clearAndSelect(previousRow);
}
}
}
}
}
} while (e.getChange() != null && e.getChange().next());
if (shift != 0) {
shiftSelection(startRow, shift, new Callback<ShiftParams, Void>() {
@Override public Void call(ShiftParams param) {
startAtomic();
final int clearIndex = param.getClearIndex();
final int setIndex = param.getSetIndex();
TreeTablePosition<S,?> oldTP = null;
if (clearIndex > -1) {
for (int i = 0; i < selectedCellsMap.size(); i++) {
TreeTablePosition<S,?> tp = selectedCellsMap.get(i);
if (tp.getRow() == clearIndex) {
oldTP = tp;
selectedCellsMap.remove(tp);
} else if (tp.getRow() == setIndex && !param.isSelected()) {
selectedCellsMap.remove(tp);
}
}
}
if (oldTP != null && param.isSelected()) {
TreeTablePosition<S,?> newTP = new TreeTablePosition<>(
treeTableView, param.getSetIndex(), oldTP.getTableColumn());
selectedCellsMap.add(newTP);
}
stopAtomic();
return null;
}
});
}
}
};
private WeakChangeListener<TreeItem<S>> weakRootPropertyListener =
new WeakChangeListener<>(rootPropertyListener);
private WeakEventHandler<TreeItem.TreeModificationEvent<S>> weakTreeItemListener;
private final SelectedCellsMap<TreeTablePosition<S,?>> selectedCellsMap;
private final ReadOnlyUnbackedObservableList<TreeTablePosition<S,?>> selectedCellsSeq;
@Override public ObservableList<TreeTablePosition<S,?>> getSelectedCells() {
return selectedCellsSeq;
}
@Override public void clearAndSelect(int row) {
clearAndSelect(row, null);
}
@Override public void clearAndSelect(int row, TableColumnBase<TreeItem<S>,?> column) {
if (row < 0 || row >= getItemCount()) return;
final TreeTablePosition<S,?> newTablePosition = new TreeTablePosition<>(getTreeTableView(), row, (TreeTableColumn<S,?>)column);
final boolean isCellSelectionEnabled = isCellSelectionEnabled();
TreeTableCellBehavior.setAnchor(treeTableView, newTablePosition, false);
List<TreeTablePosition<S,?>> previousSelection = new ArrayList<>(selectedCellsMap.getSelectedCells());
final boolean wasSelected = isSelected(row, column);
if (wasSelected && previousSelection.size() == 1) {
TreeTablePosition<S,?> selectedCell = getSelectedCells().get(0);
if (getSelectedItem() == getModelItem(row)) {
if (selectedCell.getRow() == row && selectedCell.getTableColumn() == column) {
return;
}
}
}
startAtomic();
clearSelection();
select(row, column);
stopAtomic();
if (isCellSelectionEnabled) {
previousSelection.remove(newTablePosition);
} else {
for (TreeTablePosition<S,?> tp : previousSelection) {
if (tp.getRow() == row) {
previousSelection.remove(tp);
break;
}
}
}
ListChangeListener.Change<TreeTablePosition<S, ?>> change;
if (wasSelected) {
change = ControlUtils.buildClearAndSelectChange(
selectedCellsSeq, previousSelection, newTablePosition, Comparator.comparing(TreeTablePosition::getRow));
} else {
final int changeIndex = isCellSelectionEnabled ? 0 : Math.max(0, selectedCellsSeq.indexOf(newTablePosition));
final int changeSize = isCellSelectionEnabled ? getSelectedCells().size() : 1;
change = new NonIterableChange.GenericAddRemoveChange<>(
changeIndex, changeIndex + changeSize, previousSelection, selectedCellsSeq);
}
fireCustomSelectedCellsListChangeEvent(change);
}
@Override public void select(int row) {
select(row, null);
}
@Override public void select(int row, TableColumnBase<TreeItem<S>,?> column) {
if (row < 0 || row >= getRowCount()) return;
if (isCellSelectionEnabled() && column == null) {
List<TreeTableColumn<S,?>> columns = getTreeTableView().getVisibleLeafColumns();
for (int i = 0; i < columns.size(); i++) {
select(row, columns.get(i));
}
return;
}
if (TableCellBehavior.hasDefaultAnchor(treeTableView)) {
TableCellBehavior.removeAnchor(treeTableView);
}
if (getSelectionMode() == SelectionMode.SINGLE) {
quietClearSelection();
}
selectedCellsMap.add(new TreeTablePosition<>(getTreeTableView(), row, (TreeTableColumn<S,?>)column));
updateSelectedIndex(row);
focus(row, (TreeTableColumn<S, ?>) column);
}
@Override public void select(TreeItem<S> obj) {
if (obj == null && getSelectionMode() == SelectionMode.SINGLE) {
clearSelection();
return;
}
int firstIndex = treeTableView.getRow(obj);
if (firstIndex > -1) {
if (isSelected(firstIndex)) {
return;
}
if (getSelectionMode() == SelectionMode.SINGLE) {
quietClearSelection();
}
select(firstIndex);
} else {
setSelectedIndex(-1);
setSelectedItem(obj);
}
}
@Override public void selectIndices(int row, int... rows) {
if (rows == null) {
select(row);
return;
}
int rowCount = getRowCount();
if (getSelectionMode() == SelectionMode.SINGLE) {
quietClearSelection();
for (int i = rows.length - 1; i >= 0; i--) {
int index = rows[i];
if (index >= 0 && index < rowCount) {
select(index);
break;
}
}
if (selectedCellsMap.isEmpty()) {
if (row > 0 && row < rowCount) {
select(row);
}
}
} else {
int lastIndex = -1;
Set<TreeTablePosition<S,?>> positions = new LinkedHashSet<>();
if (row >= 0 && row < rowCount) {
if (isCellSelectionEnabled()) {
List<TreeTableColumn<S,?>> columns = getTreeTableView().getVisibleLeafColumns();
for (int column = 0; column < columns.size(); column++) {
if (! selectedCellsMap.isSelected(row, column)) {
positions.add(new TreeTablePosition<>(getTreeTableView(), row, columns.get(column)));
}
}
} else {
boolean match = selectedCellsMap.isSelected(row, -1);
if (!match) {
positions.add(new TreeTablePosition<>(getTreeTableView(), row, null));
}
}
lastIndex = row;
}
for (int i = 0; i < rows.length; i++) {
int index = rows[i];
if (index < 0 || index >= rowCount) continue;
lastIndex = index;
if (isCellSelectionEnabled()) {
List<TreeTableColumn<S,?>> columns = getTreeTableView().getVisibleLeafColumns();
for (int column = 0; column < columns.size(); column++) {
if (! selectedCellsMap.isSelected(index, column)) {
positions.add(new TreeTablePosition<>(getTreeTableView(), index, columns.get(column)));
lastIndex = index;
}
}
} else {
if (! selectedCellsMap.isSelected(index, -1)) {
positions.add(new TreeTablePosition<>(getTreeTableView(), index, null));
}
}
}
selectedCellsMap.addAll(positions);
if (lastIndex != -1) {
select(lastIndex);
}
}
}
@Override public void selectAll() {
if (getSelectionMode() == SelectionMode.SINGLE) return;
if (isCellSelectionEnabled()) {
List<TreeTablePosition<S,?>> indices = new ArrayList<>();
TreeTableColumn<S,?> column;
TreeTablePosition<S,?> tp = null;
for (int col = 0; col < getTreeTableView().getVisibleLeafColumns().size(); col++) {
column = getTreeTableView().getVisibleLeafColumns().get(col);
for (int row = 0; row < getRowCount(); row++) {
tp = new TreeTablePosition<>(getTreeTableView(), row, column);
indices.add(tp);
}
}
selectedCellsMap.setAll(indices);
if (tp != null) {
select(tp.getRow(), tp.getTableColumn());
focus(tp.getRow(), tp.getTableColumn());
}
} else {
List<TreeTablePosition<S,?>> indices = new ArrayList<>();
for (int i = 0; i < getRowCount(); i++) {
indices.add(new TreeTablePosition<>(getTreeTableView(), i, null));
}
selectedCellsMap.setAll(indices);
int focusedIndex = getFocusedIndex();
if (focusedIndex == -1) {
final int itemCount = getItemCount();
if (itemCount > 0) {
select(itemCount - 1);
focus(indices.get(indices.size() - 1));
}
} else {
select(focusedIndex);
focus(focusedIndex);
}
}
}
@Override public void selectRange(int minRow, TableColumnBase<TreeItem<S>,?> minColumn,
int maxRow, TableColumnBase<TreeItem<S>,?> maxColumn) {
if (getSelectionMode() == SelectionMode.SINGLE) {
quietClearSelection();
select(maxRow, maxColumn);
return;
}
startAtomic();
final int itemCount = getItemCount();
final boolean isCellSelectionEnabled = isCellSelectionEnabled();
final int minColumnIndex = treeTableView.getVisibleLeafIndex((TreeTableColumn<S,?>)minColumn);
final int maxColumnIndex = treeTableView.getVisibleLeafIndex((TreeTableColumn<S,?>)maxColumn);
final int _minColumnIndex = Math.min(minColumnIndex, maxColumnIndex);
final int _maxColumnIndex = Math.max(minColumnIndex, maxColumnIndex);
final int _minRow = Math.min(minRow, maxRow);
final int _maxRow = Math.max(minRow, maxRow);
List<TreeTablePosition<S,?>> cellsToSelect = new ArrayList<>();
for (int _row = _minRow; _row <= _maxRow; _row++) {
if (_row < 0 || _row >= itemCount) continue;
if (! isCellSelectionEnabled) {
cellsToSelect.add(new TreeTablePosition<>(treeTableView, _row, (TreeTableColumn<S,?>)minColumn));
} else {
for (int _col = _minColumnIndex; _col <= _maxColumnIndex; _col++) {
final TreeTableColumn<S, ?> column = treeTableView.getVisibleLeafColumn(_col);
if (column == null && isCellSelectionEnabled) continue;
cellsToSelect.add(new TreeTablePosition<>(treeTableView, _row, column));
}
}
}
cellsToSelect.removeAll(getSelectedCells());
selectedCellsMap.addAll(cellsToSelect);
stopAtomic();
updateSelectedIndex(maxRow);
focus(maxRow, (TreeTableColumn<S,?>)maxColumn);
final TreeTableColumn<S,?> startColumn = (TreeTableColumn<S,?>)minColumn;
final TreeTableColumn<S,?> endColumn = isCellSelectionEnabled ? (TreeTableColumn<S,?>)maxColumn : startColumn;
final int startChangeIndex = selectedCellsMap.indexOf(new TreeTablePosition<>(treeTableView, minRow, startColumn));
final int endChangeIndex = selectedCellsMap.indexOf(new TreeTablePosition<>(treeTableView, maxRow, endColumn));
if (startChangeIndex > -1 && endChangeIndex > -1) {
final int startIndex = Math.min(startChangeIndex, endChangeIndex);
final int endIndex = Math.max(startChangeIndex, endChangeIndex);
ListChangeListener.Change c = new NonIterableChange.SimpleAddChange<>(startIndex, endIndex + 1, selectedCellsSeq);
fireCustomSelectedCellsListChangeEvent(c);
}
}
@Override public void clearSelection(int index) {
clearSelection(index, null);
}
@Override
public void clearSelection(int row, TableColumnBase<TreeItem<S>,?> column) {
clearSelection(new TreeTablePosition<S,Object>(getTreeTableView(), row, (TreeTableColumn)column));
}
private void clearSelection(TreeTablePosition<S,?> tp) {
final boolean csMode = isCellSelectionEnabled();
final int row = tp.getRow();
final boolean columnIsNull = tp.getTableColumn() == null;
List<TreeTablePosition> toRemove = new ArrayList<>();
for (TreeTablePosition pos : getSelectedCells()) {
if (!csMode) {
if (pos.getRow() == row) {
toRemove.add(pos);
break;
}
} else {
if (columnIsNull && pos.getRow() == row) {
toRemove.add(pos);
} else if (pos.equals(tp)) {
toRemove.add(tp);
break;
}
}
}
toRemove.stream().forEach(selectedCellsMap::remove);
if (isEmpty() && ! isAtomic()) {
updateSelectedIndex(-1);
selectedCellsMap.clear();
}
}
@Override public void clearSelection() {
final List<TreeTablePosition<S,?>> removed = new ArrayList<>((Collection)getSelectedCells());
quietClearSelection();
if (! isAtomic()) {
updateSelectedIndex(-1);
focus(-1);
if (!removed.isEmpty()) {
ListChangeListener.Change<TreeTablePosition<S, ?>> c = new NonIterableChange<TreeTablePosition<S, ?>>(0, 0, selectedCellsSeq) {
@Override public List<TreeTablePosition<S, ?>> getRemoved() {
return removed;
}
};
fireCustomSelectedCellsListChangeEvent(c);
}
}
}
private void quietClearSelection() {
startAtomic();
selectedCellsMap.clear();
stopAtomic();
}
@Override public boolean isSelected(int index) {
return isSelected(index, null);
}
@Override public boolean isSelected(int row, TableColumnBase<TreeItem<S>,?> column) {
final boolean isCellSelectionEnabled = isCellSelectionEnabled();
if (isCellSelectionEnabled && column == null) {
int columnCount = treeTableView.getVisibleLeafColumns().size();
for (int col = 0; col < columnCount; col++) {
if (!selectedCellsMap.isSelected(row, col)) {
return false;
}
}
return true;
} else {
int columnIndex = !isCellSelectionEnabled || column == null ? -1 : treeTableView.getVisibleLeafIndex((TreeTableColumn<S, ?>) column);
return selectedCellsMap.isSelected(row, columnIndex);
}
}
@Override public boolean isEmpty() {
return selectedCellsMap.isEmpty();
}
@Override public void selectPrevious() {
if (isCellSelectionEnabled()) {
TreeTablePosition<S,?> pos = getFocusedCell();
if (pos.getColumn() - 1 >= 0) {
select(pos.getRow(), getTableColumn(pos.getTableColumn(), -1));
} else if (pos.getRow() < getRowCount() - 1) {
select(pos.getRow() - 1, getTableColumn(getTreeTableView().getVisibleLeafColumns().size() - 1));
}
} else {
int focusIndex = getFocusedIndex();
if (focusIndex == -1) {
select(getRowCount() - 1);
} else if (focusIndex > 0) {
select(focusIndex - 1);
}
}
}
@Override public void selectNext() {
if (isCellSelectionEnabled()) {
TreeTablePosition<S,?> pos = getFocusedCell();
if (pos.getColumn() + 1 < getTreeTableView().getVisibleLeafColumns().size()) {
select(pos.getRow(), getTableColumn(pos.getTableColumn(), 1));
} else if (pos.getRow() < getRowCount() - 1) {
select(pos.getRow() + 1, getTableColumn(0));
}
} else {
int focusIndex = getFocusedIndex();
if (focusIndex == -1) {
select(0);
} else if (focusIndex < getRowCount() -1) {
select(focusIndex + 1);
}
}
}
@Override public void selectAboveCell() {
TreeTablePosition<S,?> pos = getFocusedCell();
if (pos.getRow() == -1) {
select(getRowCount() - 1);
} else if (pos.getRow() > 0) {
select(pos.getRow() - 1, pos.getTableColumn());
}
}
@Override public void selectBelowCell() {
TreeTablePosition<S,?> pos = getFocusedCell();
if (pos.getRow() == -1) {
select(0);
} else if (pos.getRow() < getRowCount() -1) {
select(pos.getRow() + 1, pos.getTableColumn());
}
}
@Override public void selectFirst() {
TreeTablePosition<S,?> focusedCell = getFocusedCell();
if (getSelectionMode() == SelectionMode.SINGLE) {
quietClearSelection();
}
if (getRowCount() > 0) {
if (isCellSelectionEnabled()) {
select(0, focusedCell.getTableColumn());
} else {
select(0);
}
}
}
@Override public void selectLast() {
TreeTablePosition<S,?> focusedCell = getFocusedCell();
if (getSelectionMode() == SelectionMode.SINGLE) {
quietClearSelection();
}
int numItems = getRowCount();
if (numItems > 0 && getSelectedIndex() < numItems - 1) {
if (isCellSelectionEnabled()) {
select(numItems - 1, focusedCell.getTableColumn());
} else {
select(numItems - 1);
}
}
}
@Override public void selectLeftCell() {
if (! isCellSelectionEnabled()) return;
TreeTablePosition<S,?> pos = getFocusedCell();
if (pos.getColumn() - 1 >= 0) {
select(pos.getRow(), getTableColumn(pos.getTableColumn(), -1));
}
}
@Override public void selectRightCell() {
if (! isCellSelectionEnabled()) return;
TreeTablePosition<S,?> pos = getFocusedCell();
if (pos.getColumn() + 1 < getTreeTableView().getVisibleLeafColumns().size()) {
select(pos.getRow(), getTableColumn(pos.getTableColumn(), 1));
}
}
private void updateDefaultSelection() {
int newSelectionIndex = -1;
TreeItem<S> selectedItem = getSelectedItem();
if (selectedItem != null) {
newSelectionIndex = treeTableView.getRow(selectedItem);
}
int newFocusIndex = newSelectionIndex != -1 ? newSelectionIndex : treeTableView.getExpandedItemCount() > 0 ? 0 : -1;
clearSelection();
select(newSelectionIndex, isCellSelectionEnabled() ? getTableColumn(0) : null);
focus(newFocusIndex, isCellSelectionEnabled() ? getTableColumn(0) : null);
}
private TreeTableColumn<S,?> getTableColumn(int pos) {
return getTreeTableView().getVisibleLeafColumn(pos);
}
private TreeTableColumn<S,?> getTableColumn(TreeTableColumn<S,?> column, int offset) {
int columnIndex = getTreeTableView().getVisibleLeafIndex(column);
int newColumnIndex = columnIndex + offset;
return getTreeTableView().getVisibleLeafColumn(newColumnIndex);
}
private void updateSelectedIndex(int row) {
setSelectedIndex(row);
setSelectedItem(getModelItem(row));
}
@Override public void focus(int row) {
focus(row, null);
}
private void focus(int row, TreeTableColumn<S,?> column) {
focus(new TreeTablePosition<>(getTreeTableView(), row, column));
}
private void focus(TreeTablePosition<S,?> pos) {
if (getTreeTableView().getFocusModel() == null) return;
getTreeTableView().getFocusModel().focus(pos.getRow(), pos.getTableColumn());
getTreeTableView().notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM);
}
@Override public int getFocusedIndex() {
return getFocusedCell().getRow();
}
private TreeTablePosition<S,?> getFocusedCell() {
if (treeTableView.getFocusModel() == null) {
return new TreeTablePosition<>(treeTableView, -1, null);
}
return treeTableView.getFocusModel().getFocusedCell();
}
private int getRowCount() {
return treeTableView.getExpandedItemCount();
}
private void fireCustomSelectedCellsListChangeEvent(ListChangeListener.Change<? extends TreeTablePosition<S,?>> c) {
IntPredicate removeRowFilter = row -> !isCellSelectionEnabled() ||
getSelectedCells().stream().noneMatch(tp -> tp.getRow() == row);
ControlUtils.updateSelectedIndices(this, this.isCellSelectionEnabled(), c, removeRowFilter);
if (isAtomic()) {
return;
}
selectedCellsSeq.callObservers(new MappingChange<>(c, MappingChange.NOOP_MAP, selectedCellsSeq));
}
}
public static class TreeTableViewFocusModel<S> extends TableFocusModel<TreeItem<S>, TreeTableColumn<S,?>> {
private final TreeTableView<S> treeTableView;
private final TreeTablePosition EMPTY_CELL;
public TreeTableViewFocusModel(final TreeTableView<S> treeTableView) {
if (treeTableView == null) {
throw new NullPointerException("TableView can not be null");
}
this.treeTableView = treeTableView;
this.EMPTY_CELL = new TreeTablePosition<>(treeTableView, -1, null);
this.treeTableView.rootProperty().addListener(weakRootPropertyListener);
updateTreeEventListener(null, treeTableView.getRoot());
int focusRow = getItemCount() > 0 ? 0 : -1;
TreeTablePosition<S,?> pos = new TreeTablePosition<>(treeTableView, focusRow, null);
setFocusedCell(pos);
showRootListener = obs -> {
if (isFocused(0)) {
focus(-1);
focus(0);
}
};
treeTableView.showRootProperty().addListener(new WeakInvalidationListener(showRootListener));
focusedCellProperty().addListener(o -> {
treeTableView.notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM);
});
}
private final ChangeListener<TreeItem<S>> rootPropertyListener = (observable, oldValue, newValue) -> {
updateTreeEventListener(oldValue, newValue);
};
private final WeakChangeListener<TreeItem<S>> weakRootPropertyListener =
new WeakChangeListener<>(rootPropertyListener);
private final InvalidationListener showRootListener;
private void updateTreeEventListener(TreeItem<S> oldRoot, TreeItem<S> newRoot) {
if (oldRoot != null && weakTreeItemListener != null) {
oldRoot.removeEventHandler(TreeItem.<S>expandedItemCountChangeEvent(), weakTreeItemListener);
}
if (newRoot != null) {
weakTreeItemListener = new WeakEventHandler<>(treeItemListener);
newRoot.addEventHandler(TreeItem.<S>expandedItemCountChangeEvent(), weakTreeItemListener);
}
}
private EventHandler<TreeItem.TreeModificationEvent<S>> treeItemListener = new EventHandler<TreeItem.TreeModificationEvent<S>>() {
@Override public void handle(TreeItem.TreeModificationEvent<S> e) {
if (getFocusedIndex() == -1) return;
int shift = 0;
if (e.getChange() != null) {
e.getChange().next();
}
do {
int row = treeTableView.getRow(e.getTreeItem());
if (e.wasExpanded()) {
if (row < getFocusedIndex()) {
shift += e.getTreeItem().getExpandedDescendentCount(false) - 1;
}
} else if (e.wasCollapsed()) {
if (row < getFocusedIndex()) {
shift += -e.getTreeItem().previousExpandedDescendentCount + 1;
}
} else if (e.wasAdded()) {
TreeItem<S> eventTreeItem = e.getTreeItem();
if (ControlUtils.isTreeItemIncludingAncestorsExpanded(eventTreeItem)) {
for (int i = 0; i < e.getAddedChildren().size(); i++) {
TreeItem<S> item = e.getAddedChildren().get(i);
row = treeTableView.getRow(item);
if (item != null && row <= (shift+getFocusedIndex())) {
shift += item.getExpandedDescendentCount(false);
}
}
}
} else if (e.wasRemoved()) {
row += e.getFrom() + 1;
for (int i = 0; i < e.getRemovedChildren().size(); i++) {
TreeItem<S> item = e.getRemovedChildren().get(i);
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
if (shift != 0) {
TreeTablePosition<S, ?> focusedCell = getFocusedCell();
final int newFocus = focusedCell.getRow() + shift;
if (newFocus >= 0) {
Platform.runLater(() -> focus(newFocus, focusedCell.getTableColumn()));
}
}
}
};
private WeakEventHandler<TreeItem.TreeModificationEvent<S>> weakTreeItemListener;
@Override protected int getItemCount() {
return treeTableView.getExpandedItemCount();
}
@Override protected TreeItem<S> getModelItem(int index) {
if (index < 0 || index >= getItemCount()) return null;
return treeTableView.getTreeItem(index);
}
private ReadOnlyObjectWrapper<TreeTablePosition<S,?>> focusedCell;
public final ReadOnlyObjectProperty<TreeTablePosition<S,?>> focusedCellProperty() {
return focusedCellPropertyImpl().getReadOnlyProperty();
}
private void setFocusedCell(TreeTablePosition<S,?> value) { focusedCellPropertyImpl().set(value); }
public final TreeTablePosition<S,?> getFocusedCell() { return focusedCell == null ? EMPTY_CELL : focusedCell.get(); }
private ReadOnlyObjectWrapper<TreeTablePosition<S,?>> focusedCellPropertyImpl() {
if (focusedCell == null) {
focusedCell = new ReadOnlyObjectWrapper<TreeTablePosition<S,?>>(EMPTY_CELL) {
private TreeTablePosition<S,?> old;
@Override protected void invalidated() {
if (get() == null) return;
if (old == null || !old.equals(get())) {
setFocusedIndex(get().getRow());
setFocusedItem(getModelItem(getValue().getRow()));
old = get();
}
}
@Override
public Object getBean() {
return TreeTableView.TreeTableViewFocusModel.this;
}
@Override
public String getName() {
return "focusedCell";
}
};
}
return focusedCell;
}
@Override public void focus(int row, TreeTableColumn<S,?> column) {
if (row < 0 || row >= getItemCount()) {
setFocusedCell(EMPTY_CELL);
} else {
TreeTablePosition<S,?> oldFocusCell = getFocusedCell();
TreeTablePosition<S,?> newFocusCell = new TreeTablePosition<>(treeTableView, row, column);
setFocusedCell(newFocusCell);
if (newFocusCell.equals(oldFocusCell)) {
setFocusedIndex(row);
setFocusedItem(getModelItem(row));
}
}
}
public void focus(TreeTablePosition<S,?> pos) {
if (pos == null) return;
focus(pos.getRow(), pos.getTableColumn());
}
@Override public boolean isFocused(int row, TreeTableColumn<S,?> column) {
if (row < 0 || row >= getItemCount()) return false;
TreeTablePosition<S,?> cell = getFocusedCell();
boolean columnMatch = column == null || column.equals(cell.getTableColumn());
return cell.getRow() == row && columnMatch;
}
@Override public void focus(int index) {
if (treeTableView.expandedItemCountDirty) {
treeTableView.updateExpandedItemCount(treeTableView.getRoot());
}
if (index < 0 || index >= getItemCount()) {
setFocusedCell(EMPTY_CELL);
} else {
setFocusedCell(new TreeTablePosition<>(treeTableView, index, null));
}
}
@Override public void focusAboveCell() {
TreeTablePosition<S,?> cell = getFocusedCell();
if (getFocusedIndex() == -1) {
focus(getItemCount() - 1, cell.getTableColumn());
} else if (getFocusedIndex() > 0) {
focus(getFocusedIndex() - 1, cell.getTableColumn());
}
}
@Override public void focusBelowCell() {
TreeTablePosition<S,?> cell = getFocusedCell();
if (getFocusedIndex() == -1) {
focus(0, cell.getTableColumn());
} else if (getFocusedIndex() != getItemCount() -1) {
focus(getFocusedIndex() + 1, cell.getTableColumn());
}
}
@Override public void focusLeftCell() {
TreeTablePosition<S,?> cell = getFocusedCell();
if (cell.getColumn() <= 0) return;
focus(cell.getRow(), getTableColumn(cell.getTableColumn(), -1));
}
@Override public void focusRightCell() {
TreeTablePosition<S,?> cell = getFocusedCell();
if (cell.getColumn() == getColumnCount() - 1) return;
focus(cell.getRow(), getTableColumn(cell.getTableColumn(), 1));
}
@Override public void focusPrevious() {
if (getFocusedIndex() == -1) {
focus(0);
} else if (getFocusedIndex() > 0) {
focusAboveCell();
}
}
@Override public void focusNext() {
if (getFocusedIndex() == -1) {
focus(0);
} else if (getFocusedIndex() != getItemCount() -1) {
focusBelowCell();
}
}
private int getColumnCount() {
return treeTableView.getVisibleLeafColumns().size();
}
private TreeTableColumn<S,?> getTableColumn(TreeTableColumn<S,?> column, int offset) {
int columnIndex = treeTableView.getVisibleLeafIndex(column);
int newColumnIndex = columnIndex + offset;
return treeTableView.getVisibleLeafColumn(newColumnIndex);
}
}
}
