package javafx.scene.control;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.IntPredicate;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.javafx.scene.control.Logging;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.SelectedCellsMap;
import com.sun.javafx.scene.control.behavior.TableCellBehavior;
import com.sun.javafx.scene.control.behavior.TableCellBehaviorBase;
import javafx.beans.*;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import com.sun.javafx.collections.MappingChange;
import com.sun.javafx.collections.NonIterableChange;
import javafx.css.converter.SizeConverter;
import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import com.sun.javafx.scene.control.TableColumnComparatorBase.TableColumnComparator;
import javafx.scene.control.skin.TableViewSkin;
@DefaultProperty("items")
public class TableView<S> extends Control {
static final String SET_CONTENT_WIDTH = "TableView.contentWidth";
public static final Callback<ResizeFeatures, Boolean> UNCONSTRAINED_RESIZE_POLICY = new Callback<ResizeFeatures, Boolean>() {
@Override public String toString() {
return "unconstrained-resize";
}
@Override public Boolean call(ResizeFeatures prop) {
double result = TableUtil.resize(prop.getColumn(), prop.getDelta());
return Double.compare(result, 0.0) == 0;
}
};
public static final Callback<ResizeFeatures, Boolean> CONSTRAINED_RESIZE_POLICY = new Callback<ResizeFeatures, Boolean>() {
private boolean isFirstRun = true;
@Override public String toString() {
return "constrained-resize";
}
@Override public Boolean call(ResizeFeatures prop) {
TableView<?> table = prop.getTable();
List<? extends TableColumnBase<?,?>> visibleLeafColumns = table.getVisibleLeafColumns();
Boolean result = TableUtil.constrainedResize(prop,
isFirstRun,
table.contentWidth,
visibleLeafColumns);
isFirstRun = ! isFirstRun ? false : ! result;
return result;
}
};
public static final Callback<TableView, Boolean> DEFAULT_SORT_POLICY = new Callback<TableView, Boolean>() {
@Override public Boolean call(TableView table) {
try {
ObservableList<?> itemsList = table.getItems();
if (itemsList instanceof SortedList) {
SortedList sortedList = (SortedList) itemsList;
boolean comparatorsBound = sortedList.comparatorProperty().
isEqualTo(table.comparatorProperty()).get();
if (! comparatorsBound) {
if (Logging.getControlsLogger().isLoggable(Level.INFO)) {
String s = "TableView items list is a SortedList, but the SortedList " +
"comparator should be bound to the TableView comparator for " +
"sorting to be enabled (e.g. " +
"sortedList.comparatorProperty().bind(tableView.comparatorProperty());).";
Logging.getControlsLogger().info(s);
}
}
return comparatorsBound;
} else {
if (itemsList == null || itemsList.isEmpty()) {
return true;
}
Comparator comparator = table.getComparator();
if (comparator == null) {
return true;
}
FXCollections.sort(itemsList, comparator);
return true;
}
} catch (UnsupportedOperationException e) {
return false;
}
}
};
public TableView() {
this(FXCollections.<S>observableArrayList());
}
public TableView(ObservableList<S> items) {
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.TABLE_VIEW);
setItems(items);
setSelectionModel(new TableViewArrayListSelectionModel<S>(this));
setFocusModel(new TableViewFocusModel<S>(this));
getColumns().addListener(weakColumnsObserver);
getSortOrder().addListener((ListChangeListener<TableColumn<S, ?>>) c -> {
doSort(TableUtil.SortEventType.SORT_ORDER_CHANGE, c);
});
getProperties().addListener(new MapChangeListener<Object, Object>() {
@Override
public void onChanged(Change<? extends Object, ? extends Object> c) {
if (c.wasAdded() && SET_CONTENT_WIDTH.equals(c.getKey())) {
if (c.getValueAdded() instanceof Number) {
setContentWidth((Double) c.getValueAdded());
}
getProperties().remove(SET_CONTENT_WIDTH);
}
}
});
pseudoClassStateChanged(PseudoClass.getPseudoClass(getColumnResizePolicy().toString()), true);
isInited = true;
}
private final ObservableList<TableColumn<S,?>> columns = FXCollections.observableArrayList();
private final ObservableList<TableColumn<S,?>> visibleLeafColumns = FXCollections.observableArrayList();
private final ObservableList<TableColumn<S,?>> unmodifiableVisibleLeafColumns = FXCollections.unmodifiableObservableList(visibleLeafColumns);
private ObservableList<TableColumn<S,?>> sortOrder = FXCollections.observableArrayList();
private double contentWidth;
private boolean isInited = false;
private final ListChangeListener<TableColumn<S,?>> columnsObserver = new ListChangeListener<TableColumn<S,?>>() {
@Override public void onChanged(Change<? extends TableColumn<S,?>> c) {
final List<TableColumn<S,?>> columns = getColumns();
while (c.next()) {
if (c.wasAdded()) {
List<TableColumn<S,?>> duplicates = new ArrayList<>();
for (TableColumn<S,?> addedColumn : c.getAddedSubList()) {
if (addedColumn == null) continue;
int count = 0;
for (TableColumn<S,?> column : columns) {
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
for (TableColumn<S,?> dupe : duplicates) {
titleList += "'" + dupe.getText() + "', ";
}
throw new IllegalStateException("Duplicate TableColumns detected in TableView columns list with titles " + titleList);
}
}
}
c.reset();
List<TableColumn<S,?>> toRemove = new ArrayList<>();
while (c.next()) {
final List<? extends TableColumn<S, ?>> removed = c.getRemoved();
final List<? extends TableColumn<S, ?>> added = c.getAddedSubList();
if (c.wasRemoved()) {
toRemove.addAll(removed);
for (TableColumn<S,?> tc : removed) {
tc.setTableView(null);
}
}
if (c.wasAdded()) {
toRemove.removeAll(added);
for (TableColumn<S,?> tc : added) {
tc.setTableView(TableView.this);
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
final TableViewFocusModel<S> fm = getFocusModel();
final TableViewSelectionModel<S> sm = getSelectionModel();
c.reset();
List<TableColumn<S,?>> removed = new ArrayList<>();
List<TableColumn<S,?>> added = new ArrayList<>();
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
TablePosition<S, ?> focusedCell = fm.getFocusedCell();
boolean match = false;
for (TableColumn<S, ?> tc : removed) {
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
List<TablePosition> selectedCells = new ArrayList<>(sm.getSelectedCells());
for (TablePosition selectedCell : selectedCells) {
boolean match = false;
for (TableColumn<S, ?> tc : removed) {
match = selectedCell != null && selectedCell.getTableColumn() == tc;
if (match) break;
}
if (match) {
int matchingColumnIndex = lastKnownColumnIndex.getOrDefault(selectedCell.getTableColumn(), -1);
if (matchingColumnIndex == -1) continue;
if (sm instanceof TableViewArrayListSelectionModel) {
TablePosition<S,?> fixedTablePosition =
new TablePosition<>(TableView.this,
selectedCell.getRow(),
selectedCell.getTableColumn());
fixedTablePosition.fixedColumnIndex = matchingColumnIndex;
((TableViewArrayListSelectionModel)sm).clearSelection(fixedTablePosition);
} else {
sm.clearSelection(selectedCell.getRow(), selectedCell.getTableColumn());
}
}
}
}
lastKnownColumnIndex.clear();
for (TableColumn<S,?> tc : getColumns()) {
int index = getVisibleLeafIndex(tc);
if (index > -1) {
lastKnownColumnIndex.put(tc, index);
}
}
}
};
private final WeakHashMap<TableColumn<S,?>, Integer> lastKnownColumnIndex = new WeakHashMap<>();
private final InvalidationListener columnVisibleObserver = valueModel -> {
updateVisibleLeafColumns();
};
private final InvalidationListener columnSortableObserver = valueModel -> {
Object col = ((Property<?>)valueModel).getBean();
if (! getSortOrder().contains(col)) return;
doSort(TableUtil.SortEventType.COLUMN_SORTABLE_CHANGE, col);
};
private final InvalidationListener columnSortTypeObserver = valueModel -> {
Object col = ((Property<?>)valueModel).getBean();
if (! getSortOrder().contains(col)) return;
doSort(TableUtil.SortEventType.COLUMN_SORT_TYPE_CHANGE, col);
};
private final InvalidationListener columnComparatorObserver = valueModel -> {
Object col = ((Property<?>)valueModel).getBean();
if (! getSortOrder().contains(col)) return;
doSort(TableUtil.SortEventType.COLUMN_COMPARATOR_CHANGE, col);
};
private final InvalidationListener cellSelectionModelInvalidationListener = o -> {
final boolean isCellSelection = ((BooleanProperty)o).get();
pseudoClassStateChanged(PSEUDO_CLASS_CELL_SELECTION, isCellSelection);
pseudoClassStateChanged(PSEUDO_CLASS_ROW_SELECTION, !isCellSelection);
};
private final WeakInvalidationListener weakColumnVisibleObserver =
new WeakInvalidationListener(columnVisibleObserver);
private final WeakInvalidationListener weakColumnSortableObserver =
new WeakInvalidationListener(columnSortableObserver);
private final WeakInvalidationListener weakColumnSortTypeObserver =
new WeakInvalidationListener(columnSortTypeObserver);
private final WeakInvalidationListener weakColumnComparatorObserver =
new WeakInvalidationListener(columnComparatorObserver);
private final WeakListChangeListener<TableColumn<S,?>> weakColumnsObserver =
new WeakListChangeListener<TableColumn<S,?>>(columnsObserver);
private final WeakInvalidationListener weakCellSelectionModelInvalidationListener =
new WeakInvalidationListener(cellSelectionModelInvalidationListener);
public final ObjectProperty<ObservableList<S>> itemsProperty() { return items; }
private ObjectProperty<ObservableList<S>> items =
new SimpleObjectProperty<ObservableList<S>>(this, "items") {
WeakReference<ObservableList<S>> oldItemsRef;
@Override protected void invalidated() {
final ObservableList<S> oldItems = oldItemsRef == null ? null : oldItemsRef.get();
final ObservableList<S> newItems = getItems();
if (newItems != null && newItems == oldItems) {
return;
}
if (! (newItems instanceof SortedList)) {
getSortOrder().clear();
}
oldItemsRef = new WeakReference<>(newItems);
}
};
public final void setItems(ObservableList<S> value) { itemsProperty().set(value); }
public final ObservableList<S> getItems() {return items.get(); }
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
private ObjectProperty<Callback<ResizeFeatures, Boolean>> columnResizePolicy;
public final void setColumnResizePolicy(Callback<ResizeFeatures, Boolean> callback) {
columnResizePolicyProperty().set(callback);
}
public final Callback<ResizeFeatures, Boolean> getColumnResizePolicy() {
return columnResizePolicy == null ? UNCONSTRAINED_RESIZE_POLICY : columnResizePolicy.get();
}
public final ObjectProperty<Callback<ResizeFeatures, Boolean>> columnResizePolicyProperty() {
if (columnResizePolicy == null) {
columnResizePolicy = new SimpleObjectProperty<Callback<ResizeFeatures, Boolean>>(this, "columnResizePolicy", UNCONSTRAINED_RESIZE_POLICY) {
private Callback<ResizeFeatures, Boolean> oldPolicy;
@Override protected void invalidated() {
if (isInited) {
get().call(new ResizeFeatures(TableView.this, null, 0.0));
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
private ObjectProperty<Callback<TableView<S>, TableRow<S>>> rowFactory;
public final ObjectProperty<Callback<TableView<S>, TableRow<S>>> rowFactoryProperty() {
if (rowFactory == null) {
rowFactory = new SimpleObjectProperty<Callback<TableView<S>, TableRow<S>>>(this, "rowFactory");
}
return rowFactory;
}
public final void setRowFactory(Callback<TableView<S>, TableRow<S>> value) {
rowFactoryProperty().set(value);
}
public final Callback<TableView<S>, TableRow<S>> getRowFactory() {
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
private ObjectProperty<TableViewSelectionModel<S>> selectionModel
= new SimpleObjectProperty<TableViewSelectionModel<S>>(this, "selectionModel") {
TableViewSelectionModel<S> oldValue = null;
@Override protected void invalidated() {
if (oldValue != null) {
oldValue.cellSelectionEnabledProperty().removeListener(weakCellSelectionModelInvalidationListener);
if (oldValue instanceof TableViewArrayListSelectionModel) {
((TableViewArrayListSelectionModel)oldValue).dispose();
}
}
oldValue = get();
if (oldValue != null) {
oldValue.cellSelectionEnabledProperty().addListener(weakCellSelectionModelInvalidationListener);
weakCellSelectionModelInvalidationListener.invalidated(oldValue.cellSelectionEnabledProperty());
}
}
};
public final ObjectProperty<TableViewSelectionModel<S>> selectionModelProperty() {
return selectionModel;
}
public final void setSelectionModel(TableViewSelectionModel<S> value) {
selectionModelProperty().set(value);
}
public final TableViewSelectionModel<S> getSelectionModel() {
return selectionModel.get();
}
private ObjectProperty<TableViewFocusModel<S>> focusModel;
public final void setFocusModel(TableViewFocusModel<S> value) {
focusModelProperty().set(value);
}
public final TableViewFocusModel<S> getFocusModel() {
return focusModel == null ? null : focusModel.get();
}
public final ObjectProperty<TableViewFocusModel<S>> focusModelProperty() {
if (focusModel == null) {
focusModel = new SimpleObjectProperty<TableViewFocusModel<S>>(this, "focusModel");
}
return focusModel;
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
@Override public CssMetaData<TableView<?>,Number> getCssMetaData() {
return StyleableProperties.FIXED_CELL_SIZE;
}
@Override public Object getBean() {
return TableView.this;
}
@Override public String getName() {
return "fixedCellSize";
}
};
}
return fixedCellSize;
}
private ReadOnlyObjectWrapper<TablePosition<S,?>> editingCell;
private void setEditingCell(TablePosition<S,?> value) {
editingCellPropertyImpl().set(value);
}
public final TablePosition<S,?> getEditingCell() {
return editingCell == null ? null : editingCell.get();
}
public final ReadOnlyObjectProperty<TablePosition<S,?>> editingCellProperty() {
return editingCellPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyObjectWrapper<TablePosition<S,?>> editingCellPropertyImpl() {
if (editingCell == null) {
editingCell = new ReadOnlyObjectWrapper<TablePosition<S,?>>(this, "editingCell");
}
return editingCell;
}
private ReadOnlyObjectWrapper<Comparator<S>> comparator;
private void setComparator(Comparator<S> value) {
comparatorPropertyImpl().set(value);
}
public final Comparator<S> getComparator() {
return comparator == null ? null : comparator.get();
}
public final ReadOnlyObjectProperty<Comparator<S>> comparatorProperty() {
return comparatorPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyObjectWrapper<Comparator<S>> comparatorPropertyImpl() {
if (comparator == null) {
comparator = new ReadOnlyObjectWrapper<Comparator<S>>(this, "comparator");
}
return comparator;
}
private ObjectProperty<Callback<TableView<S>, Boolean>> sortPolicy;
public final void setSortPolicy(Callback<TableView<S>, Boolean> callback) {
sortPolicyProperty().set(callback);
}
@SuppressWarnings("unchecked")
public final Callback<TableView<S>, Boolean> getSortPolicy() {
return sortPolicy == null ?
(Callback<TableView<S>, Boolean>)(Object) DEFAULT_SORT_POLICY :
sortPolicy.get();
}
@SuppressWarnings("unchecked")
public final ObjectProperty<Callback<TableView<S>, Boolean>> sortPolicyProperty() {
if (sortPolicy == null) {
sortPolicy = new SimpleObjectProperty<Callback<TableView<S>, Boolean>>(
this, "sortPolicy", (Callback<TableView<S>, Boolean>)(Object) DEFAULT_SORT_POLICY) {
@Override protected void invalidated() {
sort();
}
};
}
return sortPolicy;
}
private ObjectProperty<EventHandler<SortEvent<TableView<S>>>> onSort;
public void setOnSort(EventHandler<SortEvent<TableView<S>>> value) {
onSortProperty().set(value);
}
public EventHandler<SortEvent<TableView<S>>> getOnSort() {
if( onSort != null ) {
return onSort.get();
}
return null;
}
public ObjectProperty<EventHandler<SortEvent<TableView<S>>>> onSortProperty() {
if( onSort == null ) {
onSort = new ObjectPropertyBase<EventHandler<SortEvent<TableView<S>>>>() {
@Override protected void invalidated() {
EventType<SortEvent<TableView<S>>> eventType = SortEvent.sortEvent();
EventHandler<SortEvent<TableView<S>>> eventHandler = get();
setEventHandler(eventType, eventHandler);
}
@Override public Object getBean() {
return TableView.this;
}
@Override public String getName() {
return "onSort";
}
};
}
return onSort;
}
public final ObservableList<TableColumn<S,?>> getColumns() {
return columns;
}
public final ObservableList<TableColumn<S,?>> getSortOrder() {
return sortOrder;
}
public void scrollTo(int index) {
ControlUtils.scrollToIndex(this, index);
}
public void scrollTo(S object) {
if( getItems() != null ) {
int idx = getItems().indexOf(object);
if( idx >= 0 ) {
ControlUtils.scrollToIndex(this, idx);
}
}
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
return TableView.this;
}
@Override
public String getName() {
return "onScrollTo";
}
};
}
return onScrollTo;
}
public void scrollToColumn(TableColumn<S, ?> column) {
ControlUtils.scrollToColumn(this, column);
}
public void scrollToColumnIndex(int columnIndex) {
if( getColumns() != null ) {
ControlUtils.scrollToColumn(this, getColumns().get(columnIndex));
}
}
private ObjectProperty<EventHandler<ScrollToEvent<TableColumn<S, ?>>>> onScrollToColumn;
public void setOnScrollToColumn(EventHandler<ScrollToEvent<TableColumn<S, ?>>> value) {
onScrollToColumnProperty().set(value);
}
public EventHandler<ScrollToEvent<TableColumn<S, ?>>> getOnScrollToColumn() {
if( onScrollToColumn != null ) {
return onScrollToColumn.get();
}
return null;
}
public ObjectProperty<EventHandler<ScrollToEvent<TableColumn<S, ?>>>> onScrollToColumnProperty() {
if( onScrollToColumn == null ) {
onScrollToColumn = new ObjectPropertyBase<EventHandler<ScrollToEvent<TableColumn<S, ?>>>>() {
@Override protected void invalidated() {
EventType<ScrollToEvent<TableColumn<S, ?>>> type = ScrollToEvent.scrollToColumn();
setEventHandler(type, get());
}
@Override public Object getBean() {
return TableView.this;
}
@Override public String getName() {
return "onScrollToColumn";
}
};
}
return onScrollToColumn;
}
public boolean resizeColumn(TableColumn<S,?> column, double delta) {
if (column == null || Double.compare(delta, 0.0) == 0) return false;
boolean allowed = getColumnResizePolicy().call(new ResizeFeatures<S>(TableView.this, column, delta));
if (!allowed) return false;
return true;
}
public void edit(int row, TableColumn<S,?> column) {
if (!isEditable() || (column != null && ! column.isEditable())) {
return;
}
if (row < 0 && column == null) {
setEditingCell(null);
} else {
setEditingCell(new TablePosition<>(this, row, column));
}
}
public ObservableList<TableColumn<S,?>> getVisibleLeafColumns() {
return unmodifiableVisibleLeafColumns;
}
public int getVisibleLeafIndex(TableColumn<S,?> column) {
return visibleLeafColumns.indexOf(column);
}
public TableColumn<S,?> getVisibleLeafColumn(int column) {
if (column < 0 || column >= visibleLeafColumns.size()) return null;
return visibleLeafColumns.get(column);
}
@Override protected Skin<?> createDefaultSkin() {
return new TableViewSkin<S>(this);
}
public void sort() {
final ObservableList<? extends TableColumnBase<S,?>> sortOrder = getSortOrder();
final Comparator<S> oldComparator = getComparator();
setComparator(sortOrder.isEmpty() ? null : new TableColumnComparator(sortOrder));
SortEvent<TableView<S>> sortEvent = new SortEvent<>(TableView.this, TableView.this);
fireEvent(sortEvent);
if (sortEvent.isConsumed()) {
return;
}
final List<TablePosition> prevState = new ArrayList<>(getSelectionModel().getSelectedCells());
final int itemCount = prevState.size();
getSelectionModel().startAtomic();
Callback<TableView<S>, Boolean> sortPolicy = getSortPolicy();
if (sortPolicy == null) return;
Boolean success = sortPolicy.call(this);
getSelectionModel().stopAtomic();
if (success == null || ! success) {
sortLock = true;
TableUtil.handleSortFailure(sortOrder, lastSortEventType, lastSortEventSupportInfo);
setComparator(oldComparator);
sortLock = false;
} else {
if (getSelectionModel() instanceof TableViewArrayListSelectionModel) {
final TableViewArrayListSelectionModel<S> sm = (TableViewArrayListSelectionModel<S>) getSelectionModel();
final ObservableList<TablePosition<S,?>> newState = (ObservableList<TablePosition<S,?>>)(Object)sm.getSelectedCells();
List<TablePosition<S, ?>> removed = new ArrayList<>();
for (int i = 0; i < itemCount; i++) {
TablePosition<S, ?> prevItem = prevState.get(i);
if (!newState.contains(prevItem)) {
removed.add(prevItem);
}
}
if (!removed.isEmpty()) {
ListChangeListener.Change<TablePosition<S, ?>> c = new NonIterableChange.GenericAddRemoveChange<>(0, itemCount, removed, newState);
sm.fireCustomSelectedCellsListChangeEvent(c);
}
}
}
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
private void setContentWidth(double contentWidth) {
this.contentWidth = contentWidth;
if (isInited) {
getColumnResizePolicy().call(new ResizeFeatures<S>(TableView.this, null, 0.0));
}
}
private void updateVisibleLeafColumns() {
List<TableColumn<S,?>> cols = new ArrayList<TableColumn<S,?>>();
buildVisibleLeafColumns(getColumns(), cols);
visibleLeafColumns.setAll(cols);
getColumnResizePolicy().call(new ResizeFeatures<S>(TableView.this, null, 0.0));
}
private void buildVisibleLeafColumns(List<TableColumn<S,?>> cols, List<TableColumn<S,?>> vlc) {
for (TableColumn<S,?> c : cols) {
if (c == null) continue;
boolean hasChildren = ! c.getColumns().isEmpty();
if (hasChildren) {
buildVisibleLeafColumns(c.getColumns(), vlc);
} else if (c.isVisible()) {
vlc.add(c);
}
}
}
private static final String DEFAULT_STYLE_CLASS = "table-view";
private static final PseudoClass PSEUDO_CLASS_CELL_SELECTION =
PseudoClass.getPseudoClass("cell-selection");
private static final PseudoClass PSEUDO_CLASS_ROW_SELECTION =
PseudoClass.getPseudoClass("row-selection");
private static class StyleableProperties {
private static final CssMetaData<TableView<?>,Number> FIXED_CELL_SIZE =
new CssMetaData<TableView<?>,Number>("-fx-fixed-cell-size",
SizeConverter.getInstance(),
Region.USE_COMPUTED_SIZE) {
@Override public Double getInitialValue(TableView<?> node) {
return node.getFixedCellSize();
}
@Override public boolean isSettable(TableView<?> n) {
return n.fixedCellSize == null || !n.fixedCellSize.isBound();
}
@Override public StyleableProperty<Number> getStyleableProperty(TableView<?> n) {
return (StyleableProperty<Number>) n.fixedCellSizeProperty();
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
case COLUMN_COUNT: return getVisibleLeafColumns().size();
case ROW_COUNT: return getItems().size();
case SELECTED_ITEMS: {
@SuppressWarnings("unchecked")
ObservableList<TableRow<S>> rows = (ObservableList<TableRow<S>>)super.queryAccessibleAttribute(attribute, parameters);
List<Node> selection = new ArrayList<>();
for (TableRow<S> row : rows) {
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
TableRow<S> row = (TableRow<S>)super.queryAccessibleAttribute(attribute, parameters);
return row != null ? row.queryAccessibleAttribute(attribute, parameters) : null;
}
case MULTIPLE_SELECTION: {
MultipleSelectionModel<S> sm = getSelectionModel();
return sm != null && sm.getSelectionMode() == SelectionMode.MULTIPLE;
}
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
public static class ResizeFeatures<S> extends ResizeFeaturesBase<S> {
private TableView<S> table;
public ResizeFeatures(TableView<S> table, TableColumn<S,?> column, Double delta) {
super(column, delta);
this.table = table;
}
@Override public TableColumn<S,?> getColumn() {
return (TableColumn<S,?>) super.getColumn();
}
public TableView<S> getTable() {
return table;
}
}
public static abstract class TableViewSelectionModel<S> extends TableSelectionModel<S> {
private final TableView<S> tableView;
boolean blockFocusCall = false;
public TableViewSelectionModel(final TableView<S> tableView) {
if (tableView == null) {
throw new NullPointerException("TableView can not be null");
}
this.tableView = tableView;
}
public abstract ObservableList<TablePosition> getSelectedCells();
@Override public boolean isSelected(int row, TableColumnBase<S, ?> column) {
return isSelected(row, (TableColumn<S,?>)column);
}
public abstract boolean isSelected(int row, TableColumn<S, ?> column);
@Override public void select(int row, TableColumnBase<S, ?> column) {
select(row, (TableColumn<S,?>)column);
}
public abstract void select(int row, TableColumn<S, ?> column);
@Override public void clearAndSelect(int row, TableColumnBase<S,?> column) {
clearAndSelect(row, (TableColumn<S,?>) column);
}
public abstract void clearAndSelect(int row, TableColumn<S,?> column);
@Override public void clearSelection(int row, TableColumnBase<S,?> column) {
clearSelection(row, (TableColumn<S,?>) column);
}
public abstract void clearSelection(int row, TableColumn<S, ?> column);
@Override public void selectRange(int minRow, TableColumnBase<S,?> minColumn,
int maxRow, TableColumnBase<S,?> maxColumn) {
final int minColumnIndex = tableView.getVisibleLeafIndex((TableColumn<S,?>)minColumn);
final int maxColumnIndex = tableView.getVisibleLeafIndex((TableColumn<S,?>)maxColumn);
for (int _row = minRow; _row <= maxRow; _row++) {
for (int _col = minColumnIndex; _col <= maxColumnIndex; _col++) {
select(_row, tableView.getVisibleLeafColumn(_col));
}
}
}
public TableView<S> getTableView() {
return tableView;
}
protected List<S> getTableModel() {
return tableView.getItems();
}
@Override protected S getModelItem(int index) {
if (index < 0 || index >= getItemCount()) return null;
return tableView.getItems().get(index);
}
@Override protected int getItemCount() {
return getTableModel().size();
}
@Override public void focus(int row) {
focus(row, null);
}
@Override public int getFocusedIndex() {
return getFocusedCell().getRow();
}
void focus(int row, TableColumn<S,?> column) {
focus(new TablePosition<>(getTableView(), row, column));
getTableView().notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM);
}
void focus(TablePosition<S,?> pos) {
if (blockFocusCall) return;
if (getTableView().getFocusModel() == null) return;
getTableView().getFocusModel().focus(pos.getRow(), pos.getTableColumn());
}
TablePosition<S,?> getFocusedCell() {
if (getTableView().getFocusModel() == null) {
return new TablePosition<>(getTableView(), -1, null);
}
return getTableView().getFocusModel().getFocusedCell();
}
}
static class TableViewArrayListSelectionModel<S> extends TableViewSelectionModel<S> {
private int itemCount = 0;
private final MappingChange.Map<TablePosition<S,?>,Integer> cellToIndicesMap = f -> f.getRow();
public TableViewArrayListSelectionModel(final TableView<S> tableView) {
super(tableView);
this.tableView = tableView;
this.itemsPropertyListener = new InvalidationListener() {
private WeakReference<ObservableList<S>> weakItemsRef = new WeakReference<>(tableView.getItems());
@Override public void invalidated(Observable observable) {
ObservableList<S> oldItems = weakItemsRef.get();
weakItemsRef = new WeakReference<>(tableView.getItems());
updateItemsObserver(oldItems, tableView.getItems());
}
};
this.tableView.itemsProperty().addListener(itemsPropertyListener);
selectedCellsMap = new SelectedCellsMap<TablePosition<S,?>>(this::fireCustomSelectedCellsListChangeEvent) {
@Override public boolean isCellSelectionEnabled() {
return TableViewArrayListSelectionModel.this.isCellSelectionEnabled();
}
};
selectedCellsSeq = new ReadOnlyUnbackedObservableList<TablePosition<S,?>>() {
@Override public TablePosition<S,?> get(int i) {
return selectedCellsMap.get(i);
}
@Override public int size() {
return selectedCellsMap.size();
}
};
ObservableList<S> items = getTableView().getItems();
if (items != null) {
items.addListener(weakItemsContentListener);
}
updateItemCount();
updateDefaultSelection();
cellSelectionEnabledProperty().addListener(o -> {
updateDefaultSelection();
TableCellBehaviorBase.setAnchor(tableView, getFocusedCell(), true);
});
}
private void dispose() {
this.tableView.itemsProperty().removeListener(itemsPropertyListener);
ObservableList<S> items = getTableView().getItems();
if (items != null) {
items.removeListener(weakItemsContentListener);
}
}
private final TableView<S> tableView;
final InvalidationListener itemsPropertyListener;
final ListChangeListener<S> itemsContentListener = c -> {
updateItemCount();
List<S> items1 = getTableModel();
boolean doSelectionUpdate = true;
while (c.next()) {
if (c.wasReplaced() || c.getAddedSize() == getItemCount()) {
this.selectedItemChange = c;
updateDefaultSelection();
this.selectedItemChange = null;
return;
}
final S selectedItem = getSelectedItem();
final int selectedIndex = getSelectedIndex();
if (items1 == null || items1.isEmpty()) {
clearSelection();
} else if (getSelectedIndex() == -1 && getSelectedItem() != null) {
int newIndex = items1.indexOf(getSelectedItem());
if (newIndex != -1) {
setSelectedIndex(newIndex);
doSelectionUpdate = false;
}
} else if (c.wasRemoved() &&
c.getRemovedSize() == 1 &&
! c.wasAdded() &&
selectedItem != null &&
selectedItem.equals(c.getRemoved().get(0))) {
if (getSelectedIndex() < getItemCount()) {
final int previousRow = selectedIndex == 0 ? 0 : selectedIndex - 1;
S newSelectedItem = getModelItem(previousRow);
if (! selectedItem.equals(newSelectedItem)) {
clearAndSelect(previousRow);
}
}
}
}
if (doSelectionUpdate) {
updateSelection(c);
}
};
final WeakListChangeListener<S> weakItemsContentListener
= new WeakListChangeListener<>(itemsContentListener);
private final SelectedCellsMap<TablePosition<S,?>> selectedCellsMap;
private final ReadOnlyUnbackedObservableList<TablePosition<S,?>> selectedCellsSeq;
@Override public ObservableList<TablePosition> getSelectedCells() {
return (ObservableList<TablePosition>)(Object)selectedCellsSeq;
}
private int previousModelSize = 0;
private void updateSelection(ListChangeListener.Change<? extends S> c) {
c.reset();
int shift = 0;
int startRow = -1;
while (c.next()) {
if (c.wasReplaced()) {
if (c.getList().isEmpty()) {
clearSelection();
} else {
int index = getSelectedIndex();
if (previousModelSize == c.getRemovedSize()) {
clearSelection();
} else if (index < getItemCount() && index >= 0) {
startAtomic();
clearSelection(index);
stopAtomic();
select(index);
} else {
clearSelection();
}
}
} else if (c.wasAdded() || c.wasRemoved()) {
startRow = c.getFrom();
shift += c.wasAdded() ? c.getAddedSize() : -c.getRemovedSize();
} else if (c.wasPermutated()) {
startAtomic();
final int oldSelectedIndex = getSelectedIndex();
int length = c.getTo() - c.getFrom();
HashMap<Integer, Integer> pMap = new HashMap<> (length);
for (int i = c.getFrom(); i < c.getTo(); i++) {
pMap.put(i, c.getPermutation(i));
}
List<TablePosition<S,?>> selectedIndices = new ArrayList<>((ObservableList<TablePosition<S,?>>)(Object)getSelectedCells());
List<TablePosition<S,?>> newIndices = new ArrayList<>(selectedIndices.size());
boolean selectionIndicesChanged = false;
for (int i = 0; i < selectedIndices.size(); i++) {
final TablePosition<S,?> oldIndex = selectedIndices.get(i);
final int oldRow = oldIndex.getRow();
if (pMap.containsKey(oldRow)) {
int newIndex = pMap.get(oldRow);
selectionIndicesChanged = selectionIndicesChanged || newIndex != oldRow;
newIndices.add(new TablePosition<>(oldIndex.getTableView(), newIndex, oldIndex.getTableColumn()));
}
}
if (selectionIndicesChanged) {
quietClearSelection();
stopAtomic();
selectedCellsMap.setAll(newIndices);
if (oldSelectedIndex >= 0 && oldSelectedIndex < itemCount) {
int newIndex = c.getPermutation(oldSelectedIndex);
setSelectedIndex(newIndex);
focus(newIndex);
}
} else {
stopAtomic();
}
}
}
TablePosition<S,?> anchor = TableCellBehavior.getAnchor(tableView, null);
if (shift != 0 && startRow >= 0 && anchor != null && anchor.getRow() >= startRow && (c.wasRemoved() || c.wasAdded())) {
if (isSelected(anchor.getRow(), anchor.getTableColumn())) {
TablePosition<S,?> newAnchor = new TablePosition<>(tableView, anchor.getRow() + shift, anchor.getTableColumn());
TableCellBehavior.setAnchor(tableView, newAnchor, false);
}
}
shiftSelection(startRow, shift, new Callback<ShiftParams, Void>() {
@Override public Void call(ShiftParams param) {
startAtomic();
final int clearIndex = param.getClearIndex();
final int setIndex = param.getSetIndex();
TablePosition<S,?> oldTP = null;
if (clearIndex > -1) {
for (int i = 0; i < selectedCellsMap.size(); i++) {
TablePosition<S,?> tp = selectedCellsMap.get(i);
if (tp.getRow() == clearIndex) {
oldTP = tp;
selectedCellsMap.remove(tp);
} else if (tp.getRow() == setIndex && !param.isSelected()) {
selectedCellsMap.remove(tp);
}
}
}
if (oldTP != null && param.isSelected()) {
TablePosition<S,?> newTP = new TablePosition<>(
tableView, param.getSetIndex(), oldTP.getTableColumn());
selectedCellsMap.add(newTP);
}
stopAtomic();
return null;
}
});
previousModelSize = getItemCount();
}
@Override public void clearAndSelect(int row) {
clearAndSelect(row, null);
}
@Override public void clearAndSelect(int row, TableColumn<S,?> column) {
if (row < 0 || row >= getItemCount()) return;
final TablePosition<S,?> newTablePosition = new TablePosition<>(getTableView(), row, column);
final boolean isCellSelectionEnabled = isCellSelectionEnabled();
TableCellBehavior.setAnchor(tableView, newTablePosition, false);
List<TablePosition<S,?>> previousSelection = new ArrayList<>(selectedCellsMap.getSelectedCells());
final boolean wasSelected = isSelected(row, column);
if (wasSelected && previousSelection.size() == 1) {
TablePosition<S,?> selectedCell = getSelectedCells().get(0);
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
for (TablePosition<S,?> tp : previousSelection) {
if (tp.getRow() == row) {
previousSelection.remove(tp);
break;
}
}
}
ListChangeListener.Change<TablePosition<S, ?>> change;
if (wasSelected) {
change = ControlUtils.buildClearAndSelectChange(
selectedCellsSeq, previousSelection, newTablePosition, Comparator.comparing(TablePosition::getRow));
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
@Override
public void select(int row, TableColumn<S,?> column) {
if (row < 0 || row >= getItemCount()) return;
if (isCellSelectionEnabled() && column == null) {
List<TableColumn<S,?>> columns = getTableView().getVisibleLeafColumns();
for (int i = 0; i < columns.size(); i++) {
select(row, columns.get(i));
}
return;
}
if (TableCellBehavior.hasDefaultAnchor(tableView)) {
TableCellBehavior.removeAnchor(tableView);
}
if (getSelectionMode() == SelectionMode.SINGLE) {
quietClearSelection();
}
selectedCellsMap.add(new TablePosition<>(getTableView(), row, column));
updateSelectedIndex(row);
focus(row, column);
}
@Override public void select(S obj) {
if (obj == null && getSelectionMode() == SelectionMode.SINGLE) {
clearSelection();
return;
}
S rowObj = null;
for (int i = 0; i < getItemCount(); i++) {
rowObj = getModelItem(i);
if (rowObj == null) continue;
if (rowObj.equals(obj)) {
if (isSelected(i)) {
return;
}
if (getSelectionMode() == SelectionMode.SINGLE) {
quietClearSelection();
}
select(i);
return;
}
}
setSelectedIndex(-1);
setSelectedItem(obj);
}
@Override public void selectIndices(int row, int... rows) {
if (rows == null) {
select(row);
return;
}
int rowCount = getItemCount();
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
Set<TablePosition<S,?>> positions = new LinkedHashSet<>();
if (row >= 0 && row < rowCount) {
if (isCellSelectionEnabled()) {
List<TableColumn<S,?>> columns = getTableView().getVisibleLeafColumns();
for (int column = 0; column < columns.size(); column++) {
if (! selectedCellsMap.isSelected(row, column)) {
positions.add(new TablePosition<>(getTableView(), row, columns.get(column)));
lastIndex = row;
}
}
} else {
boolean match = selectedCellsMap.isSelected(row, -1);
if (!match) {
positions.add(new TablePosition<>(getTableView(), row, null));
}
}
lastIndex = row;
}
for (int i = 0; i < rows.length; i++) {
int index = rows[i];
if (index < 0 || index >= rowCount) continue;
lastIndex = index;
if (isCellSelectionEnabled()) {
List<TableColumn<S,?>> columns = getTableView().getVisibleLeafColumns();
for (int column = 0; column < columns.size(); column++) {
if (! selectedCellsMap.isSelected(index, column)) {
positions.add(new TablePosition<>(getTableView(), index, columns.get(column)));
lastIndex = index;
}
}
} else {
if (! selectedCellsMap.isSelected(index, -1)) {
positions.add(new TablePosition<>(getTableView(), index, null));
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
List<TablePosition<S,?>> indices = new ArrayList<>();
TableColumn<S,?> column;
TablePosition<S,?> tp = null;
for (int col = 0; col < getTableView().getVisibleLeafColumns().size(); col++) {
column = getTableView().getVisibleLeafColumns().get(col);
for (int row = 0; row < getItemCount(); row++) {
tp = new TablePosition<>(getTableView(), row, column);
indices.add(tp);
}
}
selectedCellsMap.setAll(indices);
if (tp != null) {
select(tp.getRow(), tp.getTableColumn());
focus(tp.getRow(), tp.getTableColumn());
}
} else {
List<TablePosition<S,?>> indices = new ArrayList<>();
for (int i = 0; i < getItemCount(); i++) {
indices.add(new TablePosition<>(getTableView(), i, null));
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
@Override public void selectRange(int minRow, TableColumnBase<S,?> minColumn,
int maxRow, TableColumnBase<S,?> maxColumn) {
if (getSelectionMode() == SelectionMode.SINGLE) {
quietClearSelection();
select(maxRow, maxColumn);
return;
}
startAtomic();
final int itemCount = getItemCount();
final boolean isCellSelectionEnabled = isCellSelectionEnabled();
final int minColumnIndex = tableView.getVisibleLeafIndex((TableColumn<S,?>)minColumn);
final int maxColumnIndex = tableView.getVisibleLeafIndex((TableColumn<S,?>)maxColumn);
final int _minColumnIndex = Math.min(minColumnIndex, maxColumnIndex);
final int _maxColumnIndex = Math.max(minColumnIndex, maxColumnIndex);
final int _minRow = Math.min(minRow, maxRow);
final int _maxRow = Math.max(minRow, maxRow);
List<TablePosition<S,?>> cellsToSelect = new ArrayList<>();
for (int _row = _minRow; _row <= _maxRow; _row++) {
if (_row < 0 || _row >= itemCount) continue;
if (! isCellSelectionEnabled) {
cellsToSelect.add(new TablePosition<>(tableView, _row, (TableColumn<S,?>)minColumn));
} else {
for (int _col = _minColumnIndex; _col <= _maxColumnIndex; _col++) {
final TableColumn<S, ?> column = tableView.getVisibleLeafColumn(_col);
if (column == null && isCellSelectionEnabled) continue;
cellsToSelect.add(new TablePosition<>(tableView, _row, column));
}
}
}
cellsToSelect.removeAll(getSelectedCells());
selectedCellsMap.addAll(cellsToSelect);
stopAtomic();
updateSelectedIndex(maxRow);
focus(maxRow, (TableColumn<S,?>)maxColumn);
final TableColumn<S,?> startColumn = (TableColumn<S,?>)minColumn;
final TableColumn<S,?> endColumn = isCellSelectionEnabled ? (TableColumn<S,?>)maxColumn : startColumn;
final int startChangeIndex = selectedCellsMap.indexOf(new TablePosition<>(tableView, minRow, startColumn));
final int endChangeIndex = selectedCellsMap.indexOf(new TablePosition<>(tableView, maxRow, endColumn));
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
public void clearSelection(int row, TableColumn<S,?> column) {
clearSelection(new TablePosition<>(getTableView(), row, column));
}
private void clearSelection(TablePosition<S,?> tp) {
final boolean csMode = isCellSelectionEnabled();
final int row = tp.getRow();
final boolean columnIsNull = tp.getTableColumn() == null;
List<TablePosition> toRemove = new ArrayList<>();
for (TablePosition pos : getSelectedCells()) {
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
final List<TablePosition<S,?>> removed = new ArrayList<>((Collection)getSelectedCells());
quietClearSelection();
if (! isAtomic()) {
updateSelectedIndex(-1);
focus(-1);
if (!removed.isEmpty()) {
ListChangeListener.Change<TablePosition<S, ?>> c = new NonIterableChange<TablePosition<S, ?>>(0, 0, selectedCellsSeq) {
@Override public List<TablePosition<S, ?>> getRemoved() {
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
@Override
public boolean isSelected(int row, TableColumn<S,?> column) {
final boolean isCellSelectionEnabled = isCellSelectionEnabled();
if (isCellSelectionEnabled && column == null) {
int columnCount = tableView.getVisibleLeafColumns().size();
for (int col = 0; col < columnCount; col++) {
if (!selectedCellsMap.isSelected(row, col)) {
return false;
}
}
return true;
} else {
int columnIndex = !isCellSelectionEnabled || column == null ? -1 : tableView.getVisibleLeafIndex(column);
return selectedCellsMap.isSelected(row, columnIndex);
}
}
@Override public boolean isEmpty() {
return selectedCellsMap.isEmpty();
}
@Override public void selectPrevious() {
if (isCellSelectionEnabled()) {
TablePosition<S,?> pos = getFocusedCell();
if (pos.getColumn() - 1 >= 0) {
select(pos.getRow(), getTableColumn(pos.getTableColumn(), -1));
} else if (pos.getRow() < getItemCount() - 1) {
select(pos.getRow() - 1, getTableColumn(getTableView().getVisibleLeafColumns().size() - 1));
}
} else {
int focusIndex = getFocusedIndex();
if (focusIndex == -1) {
select(getItemCount() - 1);
} else if (focusIndex > 0) {
select(focusIndex - 1);
}
}
}
@Override public void selectNext() {
if (isCellSelectionEnabled()) {
TablePosition<S,?> pos = getFocusedCell();
if (pos.getColumn() + 1 < getTableView().getVisibleLeafColumns().size()) {
select(pos.getRow(), getTableColumn(pos.getTableColumn(), 1));
} else if (pos.getRow() < getItemCount() - 1) {
select(pos.getRow() + 1, getTableColumn(0));
}
} else {
int focusIndex = getFocusedIndex();
if (focusIndex == -1) {
select(0);
} else if (focusIndex < getItemCount() -1) {
select(focusIndex + 1);
}
}
}
@Override public void selectAboveCell() {
TablePosition<S,?> pos = getFocusedCell();
if (pos.getRow() == -1) {
select(getItemCount() - 1);
} else if (pos.getRow() > 0) {
select(pos.getRow() - 1, pos.getTableColumn());
}
}
@Override public void selectBelowCell() {
TablePosition<S,?> pos = getFocusedCell();
if (pos.getRow() == -1) {
select(0);
} else if (pos.getRow() < getItemCount() -1) {
select(pos.getRow() + 1, pos.getTableColumn());
}
}
@Override public void selectFirst() {
TablePosition<S,?> focusedCell = getFocusedCell();
if (getSelectionMode() == SelectionMode.SINGLE) {
quietClearSelection();
}
if (getItemCount() > 0) {
if (isCellSelectionEnabled()) {
select(0, focusedCell.getTableColumn());
} else {
select(0);
}
}
}
@Override public void selectLast() {
TablePosition<S,?> focusedCell = getFocusedCell();
if (getSelectionMode() == SelectionMode.SINGLE) {
quietClearSelection();
}
int numItems = getItemCount();
if (numItems > 0 && getSelectedIndex() < numItems - 1) {
if (isCellSelectionEnabled()) {
select(numItems - 1, focusedCell.getTableColumn());
} else {
select(numItems - 1);
}
}
}
@Override
public void selectLeftCell() {
if (! isCellSelectionEnabled()) return;
TablePosition<S,?> pos = getFocusedCell();
if (pos.getColumn() - 1 >= 0) {
select(pos.getRow(), getTableColumn(pos.getTableColumn(), -1));
}
}
@Override
public void selectRightCell() {
if (! isCellSelectionEnabled()) return;
TablePosition<S,?> pos = getFocusedCell();
if (pos.getColumn() + 1 < getTableView().getVisibleLeafColumns().size()) {
select(pos.getRow(), getTableColumn(pos.getTableColumn(), 1));
}
}
private void updateItemsObserver(ObservableList<S> oldList, ObservableList<S> newList) {
if (oldList != null) {
oldList.removeListener(weakItemsContentListener);
}
if (newList != null) {
newList.addListener(weakItemsContentListener);
}
updateItemCount();
updateDefaultSelection();
}
private void updateDefaultSelection() {
int newSelectionIndex = -1;
if (tableView.getItems() != null) {
S selectedItem = getSelectedItem();
if (selectedItem != null) {
newSelectionIndex = tableView.getItems().indexOf(selectedItem);
}
}
clearSelection();
select(newSelectionIndex, isCellSelectionEnabled() ? getTableColumn(0) : null);
}
private TableColumn<S,?> getTableColumn(int pos) {
return getTableView().getVisibleLeafColumn(pos);
}
private TableColumn<S,?> getTableColumn(TableColumn<S,?> column, int offset) {
int columnIndex = getTableView().getVisibleLeafIndex(column);
int newColumnIndex = columnIndex + offset;
return getTableView().getVisibleLeafColumn(newColumnIndex);
}
private void updateSelectedIndex(int row) {
setSelectedIndex(row);
setSelectedItem(getModelItem(row));
}
@Override protected int getItemCount() {
return itemCount;
}
private void updateItemCount() {
if (tableView == null) {
itemCount = -1;
} else {
List<S> items = getTableModel();
itemCount = items == null ? -1 : items.size();
}
}
private void fireCustomSelectedCellsListChangeEvent(ListChangeListener.Change<? extends TablePosition<S,?>> c) {
IntPredicate removeRowFilter = row -> !isCellSelectionEnabled() ||
getSelectedCells().stream().noneMatch(tp -> tp.getRow() == row);
ControlUtils.updateSelectedIndices(this, this.isCellSelectionEnabled(), c, removeRowFilter);
if (isAtomic()) {
return;
}
selectedCellsSeq.callObservers(new MappingChange<>(c, MappingChange.NOOP_MAP, selectedCellsSeq));
}
}
public static class TableViewFocusModel<S> extends TableFocusModel<S, TableColumn<S, ?>> {
private final TableView<S> tableView;
private final TablePosition<S,?> EMPTY_CELL;
public TableViewFocusModel(final TableView<S> tableView) {
if (tableView == null) {
throw new NullPointerException("TableView can not be null");
}
this.tableView = tableView;
this.EMPTY_CELL = new TablePosition<>(tableView, -1, null);
itemsObserver = new InvalidationListener() {
private WeakReference<ObservableList<S>> weakItemsRef = new WeakReference<>(tableView.getItems());
@Override public void invalidated(Observable observable) {
ObservableList<S> oldItems = weakItemsRef.get();
weakItemsRef = new WeakReference<>(tableView.getItems());
updateItemsObserver(oldItems, tableView.getItems());
}
};
this.tableView.itemsProperty().addListener(new WeakInvalidationListener(itemsObserver));
if (tableView.getItems() != null) {
this.tableView.getItems().addListener(weakItemsContentListener);
}
updateDefaultFocus();
focusedCellProperty().addListener(o -> {
tableView.notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM);
});
}
private final InvalidationListener itemsObserver;
private final ListChangeListener<S> itemsContentListener = c -> {
c.next();
if (c.wasReplaced() || c.getAddedSize() == getItemCount()) {
updateDefaultFocus();
return;
}
TablePosition<S,?> focusedCell = getFocusedCell();
final int focusedIndex = focusedCell.getRow();
if (focusedIndex == -1 || c.getFrom() > focusedIndex) {
return;
}
c.reset();
boolean added = false;
boolean removed = false;
int addedSize = 0;
int removedSize = 0;
while (c.next()) {
added |= c.wasAdded();
removed |= c.wasRemoved();
addedSize += c.getAddedSize();
removedSize += c.getRemovedSize();
}
if (added && ! removed) {
if (addedSize < c.getList().size()) {
final int newFocusIndex = Math.min(getItemCount() - 1, getFocusedIndex() + addedSize);
focus(newFocusIndex, focusedCell.getTableColumn());
}
} else if (!added && removed) {
final int newFocusIndex = Math.max(0, getFocusedIndex() - removedSize);
if (newFocusIndex < 0) {
focus(0, focusedCell.getTableColumn());
} else {
focus(newFocusIndex, focusedCell.getTableColumn());
}
}
};
private WeakListChangeListener<S> weakItemsContentListener
= new WeakListChangeListener<>(itemsContentListener);
private void updateItemsObserver(ObservableList<S> oldList, ObservableList<S> newList) {
if (oldList != null) oldList.removeListener(weakItemsContentListener);
if (newList != null) newList.addListener(weakItemsContentListener);
updateDefaultFocus();
}
@Override protected int getItemCount() {
if (tableView.getItems() == null) return -1;
return tableView.getItems().size();
}
@Override protected S getModelItem(int index) {
if (tableView.getItems() == null) return null;
if (index < 0 || index >= getItemCount()) return null;
return tableView.getItems().get(index);
}
private ReadOnlyObjectWrapper<TablePosition> focusedCell;
public final ReadOnlyObjectProperty<TablePosition> focusedCellProperty() {
return focusedCellPropertyImpl().getReadOnlyProperty();
}
private void setFocusedCell(TablePosition value) { focusedCellPropertyImpl().set(value); }
public final TablePosition getFocusedCell() { return focusedCell == null ? EMPTY_CELL : focusedCell.get(); }
private ReadOnlyObjectWrapper<TablePosition> focusedCellPropertyImpl() {
if (focusedCell == null) {
focusedCell = new ReadOnlyObjectWrapper<TablePosition>(EMPTY_CELL) {
private TablePosition old;
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
return TableViewFocusModel.this;
}
@Override
public String getName() {
return "focusedCell";
}
};
}
return focusedCell;
}
@Override public void focus(int row, TableColumn<S,?> column) {
if (row < 0 || row >= getItemCount()) {
setFocusedCell(EMPTY_CELL);
} else {
TablePosition<S,?> oldFocusCell = getFocusedCell();
TablePosition<S,?> newFocusCell = new TablePosition<>(tableView, row, column);
setFocusedCell(newFocusCell);
if (newFocusCell.equals(oldFocusCell)) {
setFocusedIndex(row);
setFocusedItem(getModelItem(row));
}
}
}
public void focus(TablePosition pos) {
if (pos == null) return;
focus(pos.getRow(), pos.getTableColumn());
}
@Override public boolean isFocused(int row, TableColumn<S,?> column) {
if (row < 0 || row >= getItemCount()) return false;
TablePosition cell = getFocusedCell();
boolean columnMatch = column == null || column.equals(cell.getTableColumn());
return cell.getRow() == row && columnMatch;
}
@Override public void focus(int index) {
if (index < 0 || index >= getItemCount()) {
setFocusedCell(EMPTY_CELL);
} else {
setFocusedCell(new TablePosition<>(tableView, index, null));
}
}
@Override public void focusAboveCell() {
TablePosition cell = getFocusedCell();
if (getFocusedIndex() == -1) {
focus(getItemCount() - 1, cell.getTableColumn());
} else if (getFocusedIndex() > 0) {
focus(getFocusedIndex() - 1, cell.getTableColumn());
}
}
@Override public void focusBelowCell() {
TablePosition cell = getFocusedCell();
if (getFocusedIndex() == -1) {
focus(0, cell.getTableColumn());
} else if (getFocusedIndex() != getItemCount() -1) {
focus(getFocusedIndex() + 1, cell.getTableColumn());
}
}
@Override public void focusLeftCell() {
TablePosition cell = getFocusedCell();
if (cell.getColumn() <= 0) return;
focus(cell.getRow(), getTableColumn(cell.getTableColumn(), -1));
}
@Override public void focusRightCell() {
TablePosition cell = getFocusedCell();
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
private void updateDefaultFocus() {
int newValueIndex = -1;
if (tableView.getItems() != null) {
S focusedItem = getFocusedItem();
if (focusedItem != null) {
newValueIndex = tableView.getItems().indexOf(focusedItem);
}
if (newValueIndex == -1) {
newValueIndex = tableView.getItems().size() > 0 ? 0 : -1;
}
}
TablePosition<S,?> focusedCell = getFocusedCell();
TableColumn<S,?> focusColumn = focusedCell != null && !EMPTY_CELL.equals(focusedCell) ?
focusedCell.getTableColumn() : tableView.getVisibleLeafColumn(0);
focus(newValueIndex, focusColumn);
}
private int getColumnCount() {
return tableView.getVisibleLeafColumns().size();
}
private TableColumn<S,?> getTableColumn(TableColumn<S,?> column, int offset) {
int columnIndex = tableView.getVisibleLeafIndex(column);
int newColumnIndex = columnIndex + offset;
return tableView.getVisibleLeafColumn(newColumnIndex);
}
}
}
