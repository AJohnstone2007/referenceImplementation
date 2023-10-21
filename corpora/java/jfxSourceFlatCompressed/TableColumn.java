package javafx.scene.control;
import com.sun.javafx.scene.control.Properties;
import javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.TableViewSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.collections.WeakListChangeListener;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
public class TableColumn<S,T> extends TableColumnBase<S,T> implements EventTarget {
@SuppressWarnings("unchecked")
public static <S,T> EventType<CellEditEvent<S,T>> editAnyEvent() {
return (EventType<CellEditEvent<S,T>>) EDIT_ANY_EVENT;
}
private static final EventType<?> EDIT_ANY_EVENT =
new EventType<>(Event.ANY, "TABLE_COLUMN_EDIT");
@SuppressWarnings("unchecked")
public static <S,T> EventType<CellEditEvent<S,T>> editStartEvent() {
return (EventType<CellEditEvent<S,T>>) EDIT_START_EVENT;
}
private static final EventType<?> EDIT_START_EVENT =
new EventType<>(editAnyEvent(), "EDIT_START");
@SuppressWarnings("unchecked")
public static <S,T> EventType<CellEditEvent<S,T>> editCancelEvent() {
return (EventType<CellEditEvent<S,T>>) EDIT_CANCEL_EVENT;
}
private static final EventType<?> EDIT_CANCEL_EVENT =
new EventType<>(editAnyEvent(), "EDIT_CANCEL");
@SuppressWarnings("unchecked")
public static <S,T> EventType<CellEditEvent<S,T>> editCommitEvent() {
return (EventType<CellEditEvent<S,T>>) EDIT_COMMIT_EVENT;
}
private static final EventType<?> EDIT_COMMIT_EVENT =
new EventType<>(editAnyEvent(), "EDIT_COMMIT");
public static final Callback<TableColumn<?,?>, TableCell<?,?>> DEFAULT_CELL_FACTORY =
new Callback<TableColumn<?,?>, TableCell<?,?>>() {
@Override public TableCell<?,?> call(TableColumn<?,?> param) {
return new TableCell<Object,Object>() {
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
public TableColumn() {
getStyleClass().add(DEFAULT_STYLE_CLASS);
setOnEditCommit(DEFAULT_EDIT_COMMIT_HANDLER);
getColumns().addListener(weakColumnsListener);
tableViewProperty().addListener(observable -> {
for (TableColumn<S, ?> tc : getColumns()) {
tc.setTableView(getTableView());
}
});
}
public TableColumn(String text) {
this();
setText(text);
}
private EventHandler<CellEditEvent<S,T>> DEFAULT_EDIT_COMMIT_HANDLER = t -> {
int index = t.getTablePosition() != null ? t.getTablePosition().getRow() : -1;
List<S> list = t.getTableView() != null ? t.getTableView().getItems() : null;
if (list == null || index < 0 || index >= list.size()) return;
S rowData = list.get(index);
ObservableValue<T> ov = getCellObservableValue(rowData);
if (ov instanceof WritableValue) {
((WritableValue)ov).setValue(t.getNewValue());
}
};
private ListChangeListener<TableColumn<S,?>> columnsListener = c -> {
while (c.next()) {
for (TableColumn<S,?> tc : c.getRemoved()) {
if (getColumns().contains(tc)) continue;
tc.setTableView(null);
tc.setParentColumn(null);
}
for (TableColumn<S,?> tc : c.getAddedSubList()) {
tc.setTableView(getTableView());
}
updateColumnWidths();
}
};
private WeakListChangeListener<TableColumn<S,?>> weakColumnsListener =
new WeakListChangeListener<TableColumn<S,?>>(columnsListener);
private final ObservableList<TableColumn<S,?>> columns = FXCollections.<TableColumn<S,?>>observableArrayList();
private ReadOnlyObjectWrapper<TableView<S>> tableView = new ReadOnlyObjectWrapper<TableView<S>>(this, "tableView");
public final ReadOnlyObjectProperty<TableView<S>> tableViewProperty() {
return tableView.getReadOnlyProperty();
}
final void setTableView(TableView<S> value) { tableView.set(value); }
public final TableView<S> getTableView() { return tableView.get(); }
private ObjectProperty<Callback<CellDataFeatures<S,T>, ObservableValue<T>>> cellValueFactory;
public final void setCellValueFactory(Callback<CellDataFeatures<S,T>, ObservableValue<T>> value) {
cellValueFactoryProperty().set(value);
}
public final Callback<CellDataFeatures<S,T>, ObservableValue<T>> getCellValueFactory() {
return cellValueFactory == null ? null : cellValueFactory.get();
}
public final ObjectProperty<Callback<CellDataFeatures<S,T>, ObservableValue<T>>> cellValueFactoryProperty() {
if (cellValueFactory == null) {
cellValueFactory = new SimpleObjectProperty<Callback<CellDataFeatures<S,T>, ObservableValue<T>>>(this, "cellValueFactory");
}
return cellValueFactory;
}
private final ObjectProperty<Callback<TableColumn<S,T>, TableCell<S,T>>> cellFactory =
new SimpleObjectProperty<Callback<TableColumn<S,T>, TableCell<S,T>>>(
this, "cellFactory", (Callback<TableColumn<S,T>, TableCell<S,T>>) ((Callback) DEFAULT_CELL_FACTORY)) {
@Override protected void invalidated() {
TableView<S> table = getTableView();
if (table == null) return;
Map<Object,Object> properties = table.getProperties();
if (properties.containsKey(Properties.RECREATE)) {
properties.remove(Properties.RECREATE);
}
properties.put(Properties.RECREATE, Boolean.TRUE);
}
};
public final void setCellFactory(Callback<TableColumn<S,T>, TableCell<S,T>> value) {
cellFactory.set(value);
}
public final Callback<TableColumn<S,T>, TableCell<S,T>> getCellFactory() {
return cellFactory.get();
}
public final ObjectProperty<Callback<TableColumn<S,T>, TableCell<S,T>>> cellFactoryProperty() {
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
private ObjectProperty<EventHandler<CellEditEvent<S,T>>> onEditStart;
public final void setOnEditStart(EventHandler<CellEditEvent<S,T>> value) {
onEditStartProperty().set(value);
}
public final EventHandler<CellEditEvent<S,T>> getOnEditStart() {
return onEditStart == null ? null : onEditStart.get();
}
public final ObjectProperty<EventHandler<CellEditEvent<S,T>>> onEditStartProperty() {
if (onEditStart == null) {
onEditStart = new SimpleObjectProperty<EventHandler<CellEditEvent<S,T>>>(this, "onEditStart") {
@Override protected void invalidated() {
eventHandlerManager.setEventHandler(TableColumn.<S,T>editStartEvent(), get());
}
};
}
return onEditStart;
}
private ObjectProperty<EventHandler<CellEditEvent<S,T>>> onEditCommit;
public final void setOnEditCommit(EventHandler<CellEditEvent<S,T>> value) {
onEditCommitProperty().set(value);
}
public final EventHandler<CellEditEvent<S,T>> getOnEditCommit() {
return onEditCommit == null ? null : onEditCommit.get();
}
public final ObjectProperty<EventHandler<CellEditEvent<S,T>>> onEditCommitProperty() {
if (onEditCommit == null) {
onEditCommit = new SimpleObjectProperty<EventHandler<CellEditEvent<S,T>>>(this, "onEditCommit") {
@Override protected void invalidated() {
eventHandlerManager.setEventHandler(TableColumn.<S,T>editCommitEvent(), get());
}
};
}
return onEditCommit;
}
private ObjectProperty<EventHandler<CellEditEvent<S,T>>> onEditCancel;
public final void setOnEditCancel(EventHandler<CellEditEvent<S,T>> value) {
onEditCancelProperty().set(value);
}
public final EventHandler<CellEditEvent<S,T>> getOnEditCancel() {
return onEditCancel == null ? null : onEditCancel.get();
}
public final ObjectProperty<EventHandler<CellEditEvent<S,T>>> onEditCancelProperty() {
if (onEditCancel == null) {
onEditCancel = new SimpleObjectProperty<EventHandler<CellEditEvent<S, T>>>(this, "onEditCancel") {
@Override protected void invalidated() {
eventHandlerManager.setEventHandler(TableColumn.<S,T>editCancelEvent(), get());
}
};
}
return onEditCancel;
}
@Override public final ObservableList<TableColumn<S,?>> getColumns() {
return columns;
}
@Override public final ObservableValue<T> getCellObservableValue(int index) {
if (index < 0) return null;
final TableView<S> table = getTableView();
if (table == null || table.getItems() == null) return null;
final List<S> items = table.getItems();
if (index >= items.size()) return null;
final S rowData = items.get(index);
return getCellObservableValue(rowData);
}
@Override public final ObservableValue<T> getCellObservableValue(S item) {
final Callback<CellDataFeatures<S,T>, ObservableValue<T>> factory = getCellValueFactory();
if (factory == null) return null;
final TableView<S> table = getTableView();
if (table == null) return null;
final CellDataFeatures<S,T> cdf = new CellDataFeatures<S,T>(table, this, item);
return factory.call(cdf);
}
private static final String DEFAULT_STYLE_CLASS = "table-column";
@Override
public String getTypeSelector() {
return "TableColumn";
}
@Override
public Styleable getStyleableParent() {
return getTableView(); }
@Override
public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return Collections.emptyList();
}
@Override public Node getStyleableNode() {
if (! (getTableView().getSkin() instanceof TableViewSkin)) return null;
TableViewSkin<?> skin = (TableViewSkin<?>) getTableView().getSkin();
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
if (TableColumn.this.equals(header.getTableColumn())) {
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
private final TableView<S> tableView;
private final TableColumn<S,T> tableColumn;
private final S value;
public CellDataFeatures(TableView<S> tableView,
TableColumn<S,T> tableColumn, S value) {
this.tableView = tableView;
this.tableColumn = tableColumn;
this.value = value;
}
public S getValue() {
return value;
}
public TableColumn<S,T> getTableColumn() {
return tableColumn;
}
public TableView<S> getTableView() {
return tableView;
}
}
public static class CellEditEvent<S,T> extends Event {
private static final long serialVersionUID = -609964441682677579L;
public static final EventType<?> ANY = EDIT_ANY_EVENT;
private final T newValue;
private transient final TablePosition<S,T> pos;
public CellEditEvent(TableView<S> table, TablePosition<S,T> pos,
EventType<CellEditEvent<S,T>> eventType, T newValue) {
super(table, Event.NULL_SOURCE_TARGET, eventType);
this.pos = pos;
this.newValue = newValue;
}
public TableView<S> getTableView() {
return pos != null ? pos.getTableView() : null;
}
public TableColumn<S,T> getTableColumn() {
return pos != null ? pos.getTableColumn() : null;
}
public TablePosition<S,T> getTablePosition() {
return pos;
}
public T getNewValue() {
return newValue;
}
public T getOldValue() {
S rowData = getRowValue();
if (rowData == null || pos.getTableColumn() == null) {
return null;
}
return (T) pos.getTableColumn().getCellData(rowData);
}
public S getRowValue() {
List<S> items = getTableView() != null ? getTableView().getItems() : null;
if (items == null) return null;
int row = pos != null ? pos.getRow() : -1;
if (row < 0 || row >= items.size()) return null;
return items.get(row);
}
}
public static enum SortType {
ASCENDING,
DESCENDING;
}
}
