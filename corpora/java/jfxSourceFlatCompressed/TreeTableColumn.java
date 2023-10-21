package javafx.scene.control;
import com.sun.javafx.scene.control.Properties;
import javafx.css.CssMetaData;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.skin.*;
import javafx.util.Callback;
import javafx.css.Styleable;
public class TreeTableColumn<S,T> extends TableColumnBase<TreeItem<S>,T> implements EventTarget {
@SuppressWarnings("unchecked")
public static <S,T> EventType<TreeTableColumn.CellEditEvent<S,T>> editAnyEvent() {
return (EventType<TreeTableColumn.CellEditEvent<S,T>>) EDIT_ANY_EVENT;
}
private static final EventType<?> EDIT_ANY_EVENT =
new EventType<>(Event.ANY, "TREE_TABLE_COLUMN_EDIT");
@SuppressWarnings("unchecked")
public static <S,T> EventType<TreeTableColumn.CellEditEvent<S,T>> editStartEvent() {
return (EventType<TreeTableColumn.CellEditEvent<S,T>>) EDIT_START_EVENT;
}
private static final EventType<?> EDIT_START_EVENT =
new EventType<>(editAnyEvent(), "EDIT_START");
@SuppressWarnings("unchecked")
public static <S,T> EventType<TreeTableColumn.CellEditEvent<S,T>> editCancelEvent() {
return (EventType<TreeTableColumn.CellEditEvent<S,T>>) EDIT_CANCEL_EVENT;
}
private static final EventType<?> EDIT_CANCEL_EVENT =
new EventType<>(editAnyEvent(), "EDIT_CANCEL");
@SuppressWarnings("unchecked")
public static <S,T> EventType<TreeTableColumn.CellEditEvent<S,T>> editCommitEvent() {
return (EventType<TreeTableColumn.CellEditEvent<S,T>>) EDIT_COMMIT_EVENT;
}
private static final EventType<?> EDIT_COMMIT_EVENT =
new EventType<>(editAnyEvent(), "EDIT_COMMIT");
public static final Callback<TreeTableColumn<?,?>, TreeTableCell<?,?>> DEFAULT_CELL_FACTORY =
new Callback<TreeTableColumn<?,?>, TreeTableCell<?,?>>() {
@Override public TreeTableCell<?,?> call(TreeTableColumn<?,?> param) {
return new TreeTableCell() {
@Override protected void updateItem(Object item, boolean empty) {
if (item == getItem()) return;
super.updateItem(item, empty);
if (item == null) {
super.setText(null);
super.setGraphic(null);
} else if (item instanceof Node) {
super.setText(null);
super.setGraphic((Node)item);
} else {
super.setText(item.toString());
super.setGraphic(null);
}
}
};
}
};
public TreeTableColumn() {
getStyleClass().add(DEFAULT_STYLE_CLASS);
setOnEditCommit(DEFAULT_EDIT_COMMIT_HANDLER);
getColumns().addListener(weakColumnsListener);
treeTableViewProperty().addListener(observable -> {
for (TreeTableColumn<S, ?> tc : getColumns()) {
tc.setTreeTableView(getTreeTableView());
}
});
}
public TreeTableColumn(String text) {
this();
setText(text);
}
private EventHandler<TreeTableColumn.CellEditEvent<S,T>> DEFAULT_EDIT_COMMIT_HANDLER =
t -> {
TreeItem<S> rowValue = t.getRowValue();
if (rowValue == null) return;
ObservableValue<T> ov = getCellObservableValue(rowValue);
if (ov instanceof WritableValue) {
((WritableValue)ov).setValue(t.getNewValue());
}
};
private ListChangeListener<TreeTableColumn<S, ?>> columnsListener = new ListChangeListener<TreeTableColumn<S,?>>() {
@Override public void onChanged(ListChangeListener.Change<? extends TreeTableColumn<S,?>> c) {
while (c.next()) {
for (TreeTableColumn<S,?> tc : c.getRemoved()) {
if (getColumns().contains(tc)) continue;
tc.setTreeTableView(null);
tc.setParentColumn(null);
}
for (TreeTableColumn<S,?> tc : c.getAddedSubList()) {
tc.setTreeTableView(getTreeTableView());
}
updateColumnWidths();
}
}
};
private WeakListChangeListener<TreeTableColumn<S, ?>> weakColumnsListener =
new WeakListChangeListener<>(columnsListener);
private final ObservableList<TreeTableColumn<S,?>> columns = FXCollections.<TreeTableColumn<S,?>>observableArrayList();
private ReadOnlyObjectWrapper<TreeTableView<S>> treeTableView =
new ReadOnlyObjectWrapper<TreeTableView<S>>(this, "treeTableView");
public final ReadOnlyObjectProperty<TreeTableView<S>> treeTableViewProperty() {
return treeTableView.getReadOnlyProperty();
}
final void setTreeTableView(TreeTableView<S> value) { treeTableView.set(value); }
public final TreeTableView<S> getTreeTableView() { return treeTableView.get(); }
private ObjectProperty<Callback<TreeTableColumn.CellDataFeatures<S,T>, ObservableValue<T>>> cellValueFactory;
public final void setCellValueFactory(Callback<TreeTableColumn.CellDataFeatures<S,T>, ObservableValue<T>> value) {
cellValueFactoryProperty().set(value);
}
public final Callback<TreeTableColumn.CellDataFeatures<S,T>, ObservableValue<T>> getCellValueFactory() {
return cellValueFactory == null ? null : cellValueFactory.get();
}
public final ObjectProperty<Callback<TreeTableColumn.CellDataFeatures<S,T>, ObservableValue<T>>> cellValueFactoryProperty() {
if (cellValueFactory == null) {
cellValueFactory = new SimpleObjectProperty<Callback<TreeTableColumn.CellDataFeatures<S,T>, ObservableValue<T>>>(this, "cellValueFactory");
}
return cellValueFactory;
}
private final ObjectProperty<Callback<TreeTableColumn<S,T>, TreeTableCell<S,T>>> cellFactory =
new SimpleObjectProperty<Callback<TreeTableColumn<S,T>, TreeTableCell<S,T>>>(
this, "cellFactory", (Callback<TreeTableColumn<S,T>, TreeTableCell<S,T>>) ((Callback) DEFAULT_CELL_FACTORY)) {
@Override protected void invalidated() {
TreeTableView<S> table = getTreeTableView();
if (table == null) return;
Map<Object,Object> properties = table.getProperties();
if (properties.containsKey(Properties.RECREATE)) {
properties.remove(Properties.RECREATE);
}
properties.put(Properties.RECREATE, Boolean.TRUE);
}
};
public final void setCellFactory(Callback<TreeTableColumn<S,T>, TreeTableCell<S,T>> value) {
cellFactory.set(value);
}
public final Callback<TreeTableColumn<S,T>, TreeTableCell<S,T>> getCellFactory() {
return cellFactory.get();
}
public final ObjectProperty<Callback<TreeTableColumn<S,T>, TreeTableCell<S,T>>> cellFactoryProperty() {
return cellFactory;
}
private ObjectProperty<SortType> sortType;
public final ObjectProperty<SortType> sortTypeProperty() {
if (sortType == null) {
sortType = new SimpleObjectProperty<SortType>(this, "sortType", SortType.ASCENDING);
}
return sortType;
}
public final void setSortType(SortType value) {
sortTypeProperty().set(value);
}
public final SortType getSortType() {
return sortType == null ? SortType.ASCENDING : sortType.get();
}
private ObjectProperty<EventHandler<TreeTableColumn.CellEditEvent<S,T>>> onEditStart;
public final void setOnEditStart(EventHandler<TreeTableColumn.CellEditEvent<S,T>> value) {
onEditStartProperty().set(value);
}
public final EventHandler<TreeTableColumn.CellEditEvent<S,T>> getOnEditStart() {
return onEditStart == null ? null : onEditStart.get();
}
public final ObjectProperty<EventHandler<TreeTableColumn.CellEditEvent<S,T>>> onEditStartProperty() {
if (onEditStart == null) {
onEditStart = new SimpleObjectProperty<EventHandler<TreeTableColumn.CellEditEvent<S,T>>>(this, "onEditStart") {
@Override protected void invalidated() {
eventHandlerManager.setEventHandler(TreeTableColumn.<S,T>editStartEvent(), get());
}
};
}
return onEditStart;
}
private ObjectProperty<EventHandler<TreeTableColumn.CellEditEvent<S,T>>> onEditCommit;
public final void setOnEditCommit(EventHandler<TreeTableColumn.CellEditEvent<S,T>> value) {
onEditCommitProperty().set(value);
}
public final EventHandler<TreeTableColumn.CellEditEvent<S,T>> getOnEditCommit() {
return onEditCommit == null ? null : onEditCommit.get();
}
public final ObjectProperty<EventHandler<TreeTableColumn.CellEditEvent<S,T>>> onEditCommitProperty() {
if (onEditCommit == null) {
onEditCommit = new SimpleObjectProperty<EventHandler<TreeTableColumn.CellEditEvent<S,T>>>(this, "onEditCommit") {
@Override protected void invalidated() {
eventHandlerManager.setEventHandler(TreeTableColumn.<S,T>editCommitEvent(), get());
}
};
}
return onEditCommit;
}
private ObjectProperty<EventHandler<TreeTableColumn.CellEditEvent<S,T>>> onEditCancel;
public final void setOnEditCancel(EventHandler<TreeTableColumn.CellEditEvent<S,T>> value) {
onEditCancelProperty().set(value);
}
public final EventHandler<TreeTableColumn.CellEditEvent<S,T>> getOnEditCancel() {
return onEditCancel == null ? null : onEditCancel.get();
}
public final ObjectProperty<EventHandler<TreeTableColumn.CellEditEvent<S,T>>> onEditCancelProperty() {
if (onEditCancel == null) {
onEditCancel = new SimpleObjectProperty<EventHandler<TreeTableColumn.CellEditEvent<S,T>>>(this, "onEditCancel") {
@Override protected void invalidated() {
eventHandlerManager.setEventHandler(TreeTableColumn.<S,T>editCancelEvent(), get());
}
};
}
return onEditCancel;
}
@Override public final ObservableList<TreeTableColumn<S,?>> getColumns() {
return columns;
}
@Override public final ObservableValue<T> getCellObservableValue(int index) {
if (index < 0) return null;
final TreeTableView<S> table = getTreeTableView();
if (table == null || index >= table.getExpandedItemCount()) return null;
TreeItem<S> item = table.getTreeItem(index);
return getCellObservableValue(item);
}
@Override public final ObservableValue<T> getCellObservableValue(TreeItem<S> item) {
final Callback<TreeTableColumn.CellDataFeatures<S,T>, ObservableValue<T>> factory = getCellValueFactory();
if (factory == null) return null;
final TreeTableView<S> table = getTreeTableView();
if (table == null) return null;
final TreeTableColumn.CellDataFeatures<S,T> cdf = new TreeTableColumn.CellDataFeatures<S,T>(table, this, item);
return factory.call(cdf);
}
private static final String DEFAULT_STYLE_CLASS = "table-column";
@Override public String getTypeSelector() {
return "TreeTableColumn";
}
@Override public Styleable getStyleableParent() {
return getTreeTableView();
}
@Override public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return Collections.emptyList();
}
@Override public Node getStyleableNode() {
if (! (getTreeTableView().getSkin() instanceof TreeTableViewSkin)) return null;
TreeTableViewSkin<?> skin = (TreeTableViewSkin<?>) getTreeTableView().getSkin();
TableHeaderRow tableHeader = null;
for (Node n : skin.getChildren()) {
if (n instanceof TableHeaderRow) {
tableHeader = (TableHeaderRow)n;
}
}
NestedTableColumnHeader rootHeader = null;
for (Node n : tableHeader.getChildren()) {
if (n instanceof NestedTableColumnHeader) {
rootHeader = (NestedTableColumnHeader) n;
}
}
return scan(rootHeader);
}
private TableColumnHeader scan(TableColumnHeader header) {
if (TreeTableColumn.this.equals(header.getTableColumn())) {
return header;
}
if (header instanceof NestedTableColumnHeader) {
NestedTableColumnHeader parent = (NestedTableColumnHeader) header;
for (int i = 0; i < parent.getColumnHeaders().size(); i++) {
TableColumnHeader result = scan(parent.getColumnHeaders().get(i));
if (result != null) {
return result;
}
}
}
return null;
}
public static class CellDataFeatures<S,T> {
private final TreeTableView<S> treeTableView;
private final TreeTableColumn<S,T> tableColumn;
private final TreeItem<S> value;
public CellDataFeatures(TreeTableView<S> treeTableView,
TreeTableColumn<S,T> tableColumn, TreeItem<S> value) {
this.treeTableView = treeTableView;
this.tableColumn = tableColumn;
this.value = value;
}
public TreeItem<S> getValue() {
return value;
}
public TreeTableColumn<S,T> getTreeTableColumn() {
return tableColumn;
}
public TreeTableView<S> getTreeTableView() {
return treeTableView;
}
}
public static class CellEditEvent<S,T> extends Event {
private static final long serialVersionUID = -609964441682677579L;
public static final EventType<?> ANY = EDIT_ANY_EVENT;
private final T newValue;
private transient final TreeTablePosition<S,T> pos;
public CellEditEvent(TreeTableView<S> table, TreeTablePosition<S,T> pos,
EventType<TreeTableColumn.CellEditEvent<S,T>> eventType, T newValue) {
super(table, Event.NULL_SOURCE_TARGET, eventType);
this.pos = pos;
this.newValue = newValue;
}
public TreeTableView<S> getTreeTableView() {
return pos != null ? pos.getTreeTableView() : null;
}
public TreeTableColumn<S,T> getTableColumn() {
return pos != null ? pos.getTableColumn() : null;
}
public TreeTablePosition<S,T> getTreeTablePosition() {
return pos;
}
public T getNewValue() {
return newValue;
}
public T getOldValue() {
TreeItem<S> rowData = getRowValue();
if (rowData == null || pos.getTableColumn() == null) {
return null;
}
return (T) pos.getTableColumn().getCellData(rowData);
}
public TreeItem<S> getRowValue() {
TreeTableView<S> treeTable = getTreeTableView();
int row = pos != null ? pos.getRow() : -1;
int expandedItemCount = treeTable != null ? treeTable.getExpandedItemCount() : 0;
if (row < 0 || row >= expandedItemCount) return null;
return treeTable.getTreeItem(row);
}
}
public static enum SortType {
ASCENDING,
DESCENDING;
}
}
