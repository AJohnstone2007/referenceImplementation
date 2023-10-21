package test.javafx.scene.control;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.sun.javafx.tk.Toolkit;
import static org.junit.Assert.*;
import static test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils.*;
import static test.com.sun.javafx.scene.control.infrastructure.ControlSkinFactory.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CellShim;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableCellShim;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableCellSkin;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;
import test.com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils;
public class TableCellTest {
private TableCell<String,String> cell;
private TableView<String> table;
private TableColumn<String, String> editingColumn;
private TableRow<String> row;
private ObservableList<String> model;
private StageLoader stageLoader;
@Before public void setup() {
Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
if (throwable instanceof RuntimeException) {
throw (RuntimeException)throwable;
} else {
Thread.currentThread().getThreadGroup().uncaughtException(thread, throwable);
}
});
cell = new TableCell<String,String>();
model = FXCollections.observableArrayList("Four", "Five", "Fear");
table = new TableView<String>(model);
editingColumn = new TableColumn<>("TEST");
row = new TableRow<>();
}
@After
public void cleanup() {
if (stageLoader != null) stageLoader.dispose();
Thread.currentThread().setUncaughtExceptionHandler(null);
}
@Test public void styleClassIs_table_cell_byDefault() {
assertStyleClassContains(cell, "table-cell");
}
@Test public void itemIsNullByDefault() {
assertNull(cell.getItem());
}
@Test public void tableViewIsNullByDefault() {
assertNull(cell.getTableView());
assertNull(cell.tableViewProperty().get());
}
@Test public void updateTableViewUpdatesTableView() {
cell.updateTableView(table);
assertSame(table, cell.getTableView());
assertSame(table, cell.tableViewProperty().get());
}
@Test public void canSetTableViewBackToNull() {
cell.updateTableView(table);
cell.updateTableView(null);
assertNull(cell.getTableView());
assertNull(cell.tableViewProperty().get());
}
@Test public void tableViewPropertyReturnsCorrectBean() {
assertSame(cell, cell.tableViewProperty().getBean());
}
@Test public void tableViewPropertyNameIs_tableView() {
assertEquals("tableView", cell.tableViewProperty().getName());
}
@Test public void updateTableViewWithNullFocusModelResultsInNoException() {
cell.updateTableView(table);
table.setFocusModel(null);
cell.updateTableView(new TableView());
}
@Test public void updateTableViewWithNullFocusModelResultsInNoException2() {
table.setFocusModel(null);
cell.updateTableView(table);
cell.updateTableView(new TableView());
}
@Test public void updateTableViewWithNullFocusModelResultsInNoException3() {
cell.updateTableView(table);
TableView table2 = new TableView();
table2.setFocusModel(null);
cell.updateTableView(table2);
}
@Test public void updateTableViewWithNullSelectionModelResultsInNoException() {
cell.updateTableView(table);
table.setSelectionModel(null);
cell.updateTableView(new TableView());
}
@Test public void updateTableViewWithNullSelectionModelResultsInNoException2() {
table.setSelectionModel(null);
cell.updateTableView(table);
cell.updateTableView(new TableView());
}
@Test public void updateTableViewWithNullSelectionModelResultsInNoException3() {
cell.updateTableView(table);
TableView table2 = new TableView();
table2.setSelectionModel(null);
cell.updateTableView(table2);
}
@Test public void updateTableViewWithNullItemsResultsInNoException() {
cell.updateTableView(table);
table.setItems(null);
cell.updateTableView(new TableView());
}
@Test public void updateTableViewWithNullItemsResultsInNoException2() {
table.setItems(null);
cell.updateTableView(table);
cell.updateTableView(new TableView());
}
@Test public void updateTableViewWithNullItemsResultsInNoException3() {
cell.updateTableView(table);
TableView table2 = new TableView();
table2.setItems(null);
cell.updateTableView(table2);
}
private int rt_29923_count = 0;
@Test public void test_rt_29923() {
cell = new TableCellShim<String,String>() {
@Override public void updateItem(String item, boolean empty) {
super.updateItem(item, empty);
rt_29923_count++;
}
};
TableColumn col = new TableColumn("TEST");
col.setCellValueFactory(param -> null);
table.getColumns().add(col);
cell.updateTableColumn(col);
cell.updateTableView(table);
cell.updateIndex(0);
assertNull(cell.getItem());
assertFalse(cell.isEmpty());
assertEquals(1, rt_29923_count);
cell.updateIndex(1);
assertNull(cell.getItem());
assertFalse(cell.isEmpty());
assertEquals(2, rt_29923_count);
}
@Test public void test_rt_33106() {
cell.updateTableView(table);
table.setItems(null);
cell.updateIndex(1);
}
@Test public void test_rt36715_idIsNullAtStartup() {
assertNull(cell.getId());
}
@Test public void test_rt36715_idIsSettable() {
cell.setId("test-id");
assertEquals("test-id", cell.getId());
}
@Test public void test_rt36715_columnHeaderIdMirrorsTableColumnId_setIdBeforeHeaderInstantiation() {
test_rt36715_cellPropertiesMirrorTableColumnProperties(true, true, false, false, false);
}
@Test public void test_rt36715_columnHeaderIdMirrorsTableColumnId_setIdAfterHeaderInstantiation() {
test_rt36715_cellPropertiesMirrorTableColumnProperties(true, false, false, false, false);
}
@Test public void test_rt36715_columnHeaderIdMirrorsTableColumnId_setIdBeforeHeaderInstantiation_setValueOnCell() {
test_rt36715_cellPropertiesMirrorTableColumnProperties(true, true, false, false, true);
}
@Test public void test_rt36715_columnHeaderIdMirrorsTableColumnId_setIdAfterHeaderInstantiation_setValueOnCell() {
test_rt36715_cellPropertiesMirrorTableColumnProperties(true, false, false, false, true);
}
@Test public void test_rt36715_styleIsEmptyStringAtStartup() {
assertEquals("", cell.getStyle());
}
@Test public void test_rt36715_styleIsSettable() {
cell.setStyle("-fx-border-color: red");
assertEquals("-fx-border-color: red", cell.getStyle());
}
@Test public void test_rt36715_columnHeaderStyleMirrorsTableColumnStyle_setStyleBeforeHeaderInstantiation() {
test_rt36715_cellPropertiesMirrorTableColumnProperties(false, false, true, true, false);
}
@Test public void test_rt36715_columnHeaderStyleMirrorsTableColumnStyle_setStyleAfterHeaderInstantiation() {
test_rt36715_cellPropertiesMirrorTableColumnProperties(false, false, true, false, false);
}
@Test public void test_rt36715_columnHeaderStyleMirrorsTableColumnStyle_setStyleBeforeHeaderInstantiation_setValueOnCell() {
test_rt36715_cellPropertiesMirrorTableColumnProperties(false, false, true, true, true);
}
@Test public void test_rt36715_columnHeaderStyleMirrorsTableColumnStyle_setStyleAfterHeaderInstantiation_setValueOnCell() {
test_rt36715_cellPropertiesMirrorTableColumnProperties(false, false, true, false, true);
}
private void test_rt36715_cellPropertiesMirrorTableColumnProperties(
boolean setId, boolean setIdBeforeHeaderInstantiation,
boolean setStyle, boolean setStyleBeforeHeaderInstantiation,
boolean setValueOnCell) {
TableColumn column = new TableColumn("Column");
table.getColumns().add(column);
if (setId && setIdBeforeHeaderInstantiation) {
column.setId("test-id");
}
if (setStyle && setStyleBeforeHeaderInstantiation) {
column.setStyle("-fx-border-color: red");
}
StageLoader sl = new StageLoader(table);
TableCell cell = (TableCell) VirtualFlowTestUtils.getCell(table, 0, 0);
if (setValueOnCell) {
if (setId) {
cell.setId("cell-id");
}
if (setStyle) {
cell.setStyle("-fx-border-color: green");
}
}
if (setId && ! setIdBeforeHeaderInstantiation) {
column.setId("test-id");
}
if (setStyle && ! setStyleBeforeHeaderInstantiation) {
column.setStyle("-fx-border-color: red");
}
if (setId) {
if (setValueOnCell) {
assertEquals("cell-id", cell.getId());
} else {
assertEquals("test-id", cell.getId());
}
}
if (setStyle) {
if (setValueOnCell) {
assertEquals("-fx-border-color: green", cell.getStyle());
} else {
assertEquals("-fx-border-color: red", cell.getStyle());
}
}
sl.dispose();
}
@Test public void test_jdk_8151524() {
TableCell cell = new TableCell();
cell.setSkin(new TableCellSkin(cell));
}
@Test
public void testRowIsNotNullWhenAutoSizing() {
TableColumn<String, String> tableColumn = new TableColumn<>();
tableColumn.setCellFactory(col -> new TableCell<>() {
@Override
protected void updateItem(String item, boolean empty) {
super.updateItem(item, empty);
assertNotNull(getTableRow());
}
});
table.getColumns().add(tableColumn);
stageLoader = new StageLoader(table);
}
@Test
public void testRowItemIsNotNullForNonEmptyCell() {
TableColumn<String, String> tableColumn = new TableColumn<>();
tableColumn.setCellValueFactory(cc -> new SimpleStringProperty(cc.getValue()));
tableColumn.setCellFactory(col -> new TableCell<>() {
@Override
protected void updateItem(String item, boolean empty) {
super.updateItem(item, empty);
if (!empty) {
assertNotNull(getTableRow().getItem());
}
}
});
table.getColumns().add(tableColumn);
stageLoader = new StageLoader(table);
table.getItems().add("newItem");
Toolkit.getToolkit().firePulse();
}
@Test
public void testCellInUneditableRowIsNotEditable() {
table.setEditable(true);
row.setEditable(false);
TableColumn<String, String> tableColumn = new TableColumn<>();
tableColumn.setEditable(true);
table.getColumns().add(tableColumn);
cell.updateTableColumn(tableColumn);
cell.updateTableRow(row);
cell.updateTableView(table);
cell.updateIndex(0);
cell.startEdit();
assertFalse(cell.isEditing());
}
@Test
public void testCellInUneditableTableIsNotEditable() {
table.setEditable(false);
row.setEditable(true);
TableColumn<String, String> tableColumn = new TableColumn<>();
tableColumn.setEditable(true);
table.getColumns().add(tableColumn);
cell.updateTableColumn(tableColumn);
cell.updateTableRow(row);
cell.updateTableView(table);
cell.updateIndex(0);
cell.startEdit();
assertFalse(cell.isEditing());
}
@Test
public void testCellInUneditableColumnIsNotEditable() {
table.setEditable(true);
row.setEditable(true);
TableColumn<String, String> tableColumn = new TableColumn<>();
tableColumn.setEditable(false);
table.getColumns().add(tableColumn);
cell.updateTableColumn(tableColumn);
cell.updateTableRow(row);
cell.updateTableView(table);
cell.updateIndex(0);
cell.startEdit();
assertFalse(cell.isEditing());
}
private void setupForEditing() {
table.setEditable(true);
table.getColumns().add(editingColumn);
editingColumn.setCellValueFactory(cc -> new SimpleObjectProperty<>(""));
cell.updateTableView(table);
cell.updateTableColumn(editingColumn);
}
@Test
public void testEditCancelEventAfterCancelOnCell() {
setupForEditing();
int editingIndex = 1;
cell.updateIndex(editingIndex);
table.edit(editingIndex, editingColumn);
TablePosition<?, ?> editingPosition = table.getEditingCell();
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditCancel(events::add);
cell.cancelEdit();
assertEquals("column must have received editCancel", 1, events.size());
assertEquals("editing location of cancel event", editingPosition, events.get(0).getTablePosition());
}
@Test
public void testEditCancelEventAfterCancelOnTable() {
setupForEditing();
int editingIndex = 1;
cell.updateIndex(editingIndex);
table.edit(editingIndex, editingColumn);
TablePosition<?, ?> editingPosition = table.getEditingCell();
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditCancel(events::add);
table.edit(-1, null);
assertEquals("column must have received editCancel", 1, events.size());
assertEquals("editing location of cancel event", editingPosition, events.get(0).getTablePosition());
}
@Test
public void testEditCancelEventAfterCellReuse() {
setupForEditing();
int editingIndex = 1;
cell.updateIndex(editingIndex);
table.edit(editingIndex, editingColumn);
TablePosition<?, ?> editingPosition = table.getEditingCell();
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditCancel(events::add);
cell.updateIndex(0);
assertEquals("column must have received editCancel", 1, events.size());
assertEquals("editing location of cancel event", editingPosition, events.get(0).getTablePosition());
}
@Test
public void testEditCancelEventAfterModifyItems() {
setupForEditing();
stageLoader = new StageLoader(table);
int editingIndex = 1;
table.edit(editingIndex, editingColumn);
TablePosition<?, ?> editingPosition = table.getEditingCell();
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditCancel(events::add);
table.getItems().add(0, "added");
Toolkit.getToolkit().firePulse();
assertEquals("column must have received editCancel", 1, events.size());
assertEquals("editing location of cancel event", editingPosition, events.get(0).getTablePosition());
}
@Test
public void testEditCancelEventAfterRemoveEditingItem() {
setupForEditing();
stageLoader = new StageLoader(table);
int editingIndex = 1;
table.edit(editingIndex, editingColumn);
TablePosition<?, ?> editingPosition = table.getEditingCell();
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditCancel(events::add);
table.getItems().remove(editingIndex);
Toolkit.getToolkit().firePulse();
assertNull("sanity: editing terminated on items modification", table.getEditingCell());
assertEquals("column must have received editCancel", 1, events.size());
assertEquals("editing location of cancel event", editingPosition, events.get(0).getTablePosition());
}
@Test
public void testEditCancelMemoryLeakAfterRemoveEditingItem() {
TableView<MenuItem> table = new TableView<>(FXCollections.observableArrayList(
new MenuItem("some"), new MenuItem("other")));
TableColumn<MenuItem, String> editingColumn = new TableColumn<>("Text");
editingColumn.setCellValueFactory(cc -> new SimpleObjectProperty<>(""));
table.setEditable(true);
table.getColumns().add(editingColumn);
stageLoader = new StageLoader(table);
int editingIndex = 1;
MenuItem editingItem = table.getItems().get(editingIndex);
WeakReference<MenuItem> itemRef = new WeakReference<>(editingItem);
table.edit(editingIndex, editingColumn);
table.getItems().remove(editingIndex);
editingItem = null;
Toolkit.getToolkit().firePulse();
attemptGC(itemRef);
assertEquals("item must be gc'ed", null, itemRef.get());
}
@Test
public void testEditStartFiresEvent() {
setupForEditing();
cell.updateIndex(1);
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditStart(events::add);
cell.startEdit();
assertEquals("startEdit must fire", 1, events.size());
}
@Test
public void testEditStartOnCellUpdatesControl() {
setupForEditing();
int editingRow = 1;
cell.updateIndex(editingRow);
TablePosition<?, ?> editingCell = new TablePosition<>(table, editingRow, editingColumn);
cell.startEdit();
assertEquals("table must be editing at", editingCell, table.getEditingCell());
}
@Test
public void testEditStartOnCellNoColumnUpdatesControl() {
int editingRow = 1;
cell.updateIndex(editingRow);
setupForcedEditing(table, null);
TablePosition<?, ?> editingCell = new TablePosition<>(table, editingRow, null);
cell.startEdit();
assertTrue(cell.isEditing());
assertEquals("table must be editing at", editingCell, table.getEditingCell());
}
@Test
public void testEditStartDoesNotFireEventWhileEditing() {
setupForEditing();
cell.updateIndex(1);
cell.startEdit();
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditStart(events::add);
cell.startEdit();
assertEquals("startEdit must not fire while editing", 0, events.size());
}
@Test
public void testEditStartEventAfterStartOnCell() {
setupForEditing();
int editingIndex = 1;
cell.updateIndex(editingIndex);
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditStart(events::add);
cell.startEdit();
assertEquals(editingColumn, events.get(0).getTableColumn());
TablePosition<?, ?> editingCell = events.get(0).getTablePosition();
assertEquals(editingIndex, editingCell.getRow());
}
@Test
public void testEditStartEventAfterStartOnTable() {
setupForEditing();
int editingIndex = 1;
cell.updateIndex(editingIndex);
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditStart(events::add);
table.edit(editingIndex, editingColumn);
assertEquals(editingColumn, events.get(0).getTableColumn());
TablePosition<?, ?> editingCell = events.get(0).getTablePosition();
assertEquals(editingIndex, editingCell.getRow());
}
@Test
public void testCommitEditMustNotFireCancel() {
setupForEditing();
editingColumn.setOnEditCommit(e -> {
table.getItems().set(e.getTablePosition().getRow(), e.getNewValue());
table.edit(-1, null);
});
int editingRow = 1;
cell.updateIndex(editingRow);
table.edit(editingRow, editingColumn);
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditCancel(events::add);
String value = "edited";
cell.commitEdit(value);
assertEquals("sanity: value committed", value, table.getItems().get(editingRow));
assertEquals("commit must not have fired editCancel", 0, events.size());
}
@Test
public void testEditCommitEvent() {
setupForEditing();
int editingIndex = 1;
cell.updateIndex(editingIndex);
cell.startEdit();
TablePosition<?, ?> editingPosition = table.getEditingCell();
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditCommit(events::add);
cell.commitEdit("edited");
assertEquals("column must have received editCommit", 1, events.size());
assertEquals("editing location of commit event must be same as table's editingCell",
editingPosition, events.get(0).getTablePosition());
}
@Test
public void testEditCommitEditingCellAtStartEdit() {
setupForEditing();
int editingIndex = 1;
cell.updateIndex(editingIndex);
cell.startEdit();
TablePosition<?, ?> editingCellAtStartEdit = TableCellShim.getEditingCellAtStartEdit(cell);
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditCommit(events::add);
cell.commitEdit("edited");
assertEquals("column must have received editCommit", 1, events.size());
assertEquals("editing location of commit event  must be same as editingCellAtStartEdit",
editingCellAtStartEdit, events.get(0).getTablePosition());
}
@Test
public void testEditCommitEventNullTable() {
setupForcedEditing(null, editingColumn);
cell.startEdit();
TablePosition<?, ?> editingCellAtStartEdit = TableCellShim.getEditingCellAtStartEdit(cell);
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.addEventHandler(TableColumn.editAnyEvent(), events::add);
cell.commitEdit("edited");
assertEquals("column must have received editCommit", 1, events.size());
assertEquals("editing location of commit event must be same as editingCellAtStartEdit",
editingCellAtStartEdit, events.get(0).getTablePosition());
}
@Test
public void testEditStartNullTable() {
setupForcedEditing(null, editingColumn);
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.addEventHandler(TableColumn.editAnyEvent(), events::add);
cell.startEdit();
assertEquals(1, events.size());
}
@Test
public void testEditCancelNullTable() {
setupForcedEditing(null, editingColumn);
cell.startEdit();
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.addEventHandler(TableColumn.editAnyEvent(), events::add);
cell.cancelEdit();
assertEquals(1, events.size());
}
@Test
public void testEditCommitNullTable() {
setupForcedEditing(null, editingColumn);
cell.startEdit();
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.addEventHandler(TableColumn.editAnyEvent(), events::add);
cell.commitEdit("edited");
assertEquals(1, events.size());
}
@Test
public void testEditStartNullColumn() {
setupForcedEditing(table, null);
cell.startEdit();
}
@Test
public void testEditCancelNullColumn() {
setupForcedEditing(table, null);
cell.startEdit();
cell.cancelEdit();
}
@Test
public void testEditCommitNullColumn() {
setupForcedEditing(table, null);
cell.startEdit();
cell.commitEdit("edited");
}
@Test
public void testEditStartNullTableNullColumn() {
setupForcedEditing(null, null);
cell.startEdit();
}
@Test
public void testEditCancelNullTableNullColumn() {
setupForcedEditing(null, null);
cell.startEdit();
cell.cancelEdit();
}
@Test
public void testEditCommitNullTableNullColumn() {
setupForcedEditing(null, null);
cell.startEdit();
cell.commitEdit("edited");
}
@Test
public void testStartEditOffRangeMustNotFireStartEdit() {
setupForEditing();
int editingRow = table.getItems().size();
cell.updateIndex(editingRow);
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.addEventHandler(TableColumn.editStartEvent(), events::add);
cell.startEdit();
assertFalse("sanity: off-range cell must not be editing", cell.isEditing());
assertEquals("must not fire editStart", 0, events.size());
}
@Test
public void testStartEditOffRangeMustNotUpdateEditingLocation() {
setupForEditing();
int editingRow = table.getItems().size();
cell.updateIndex(editingRow);
cell.startEdit();
assertFalse("sanity: off-range cell must not be editing", cell.isEditing());
assertNull("table editing location must not be updated", table.getEditingCell());
}
@Test
public void testCellStartEditNullTable() {
setupForcedEditing(null, editingColumn);
assertFalse(cell.isEmpty());
cell.startEdit();
assertTrue(cell.isEditing());
}
@Test
public void testCellStartEditNullColumn() {
setupForcedEditing(table, null);
assertFalse(cell.isEmpty());
cell.startEdit();
assertTrue(cell.isEditing());
}
@Test
public void testCellStartEditNullTableNullColumn() {
setupForcedEditing(null, null);
assertFalse(cell.isEmpty());
cell.startEdit();
assertTrue(cell.isEditing());
}
private void setupForcedEditing(TableView table, TableColumn editingColumn) {
if (table != null) {
table.setEditable(true);
cell.updateTableView(table);
}
if (editingColumn != null ) cell.updateTableColumn(editingColumn);
TableCellShim.set_lockItemOnEdit(cell, true);
CellShim.updateItem(cell, "something", false);
}
@Test
public void testMisbehavingCancelEditTerminatesEdit() {
TableCell<String, String> cell = new MisbehavingOnCancelTableCell<>();
table.setEditable(true);
TableColumn<String, String> editingColumn = new TableColumn<>("TEST");
editingColumn.setCellValueFactory(param -> null);
table.getColumns().add(editingColumn);
cell.updateTableView(table);
cell.updateTableColumn(editingColumn);
int editingIndex = 1;
int intermediate = 0;
cell.updateIndex(editingIndex);
table.edit(editingIndex, editingColumn);
assertTrue("sanity: ", cell.isEditing());
try {
table.edit(intermediate, editingColumn);
} catch (Exception ex) {
} finally {
assertFalse("cell must not be editing", cell.isEditing());
assertEquals("table must be editing at intermediate index", intermediate, table.getEditingCell().getRow());
}
table.edit(editingIndex, editingColumn);
assertTrue("sanity: ", cell.isEditing());
try {
cell.cancelEdit();
} catch (Exception ex) {
} finally {
assertFalse("cell must not be editing", cell.isEditing());
assertNull("table editing must be cancelled by cell", table.getEditingCell());
}
}
public static class MisbehavingOnCancelTableCell<S, T> extends TableCell<S, T> {
@Override
public void cancelEdit() {
super.cancelEdit();
throw new RuntimeException("violating contract");
}
}
}
