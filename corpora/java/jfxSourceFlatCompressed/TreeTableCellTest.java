package test.javafx.scene.control;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.skin.TreeTableCellSkin;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;
import test.com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.CellShim;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableCellShim;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.sun.javafx.tk.Toolkit;
import static test.com.sun.javafx.scene.control.infrastructure.ControlSkinFactory.*;
import static test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils.*;
import static org.junit.Assert.*;
public class TreeTableCellTest {
private TreeTableCell<String, String> cell;
private TreeTableView<String> tree;
private TreeTableRow<String> row;
private static final String ROOT = "Root";
private static final String APPLES = "Apples";
private static final String ORANGES = "Oranges";
private static final String PEARS = "Pears";
private TreeItem<String> root;
private TreeItem<String> apples;
private TreeItem<String> oranges;
private TreeItem<String> pears;
private StageLoader stageLoader;
private TreeTableColumn<String, String> editingColumn;
@Before public void setup() {
Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
if (throwable instanceof RuntimeException) {
throw (RuntimeException)throwable;
} else {
Thread.currentThread().getThreadGroup().uncaughtException(thread, throwable);
}
});
cell = new TreeTableCell<String, String>();
root = new TreeItem<>(ROOT);
apples = new TreeItem<>(APPLES);
oranges = new TreeItem<>(ORANGES);
pears = new TreeItem<>(PEARS);
root.getChildren().addAll(apples, oranges, pears);
tree = new TreeTableView<String>(root);
root.setExpanded(true);
editingColumn = new TreeTableColumn<>("TEST");
row = new TreeTableRow<>();
}
@After
public void cleanup() {
if (stageLoader != null) stageLoader.dispose();
Thread.currentThread().setUncaughtExceptionHandler(null);
}
@Test public void styleClassIs_tree_cell_byDefault() {
assertStyleClassContains(cell, "tree-table-cell");
}
@Test public void itemIsNullByDefault() {
assertNull(cell.getItem());
}
@Ignore
@Test public void itemMatchesIndexWithinTreeItems() {
cell.updateIndex(0);
cell.updateTreeTableView(tree);
assertSame(ROOT, cell.getItem());
assertSame(root, cell.getTableRow().getTreeItem());
cell.updateIndex(1);
assertSame(APPLES, cell.getItem());
assertSame(apples, cell.getTableRow().getTreeItem());
}
@Ignore
@Test public void itemMatchesIndexWithinTreeItems2() {
cell.updateTreeTableView(tree);
cell.updateIndex(0);
assertSame(ROOT, cell.getItem());
assertSame(root, cell.getTableRow().getTreeItem());
cell.updateIndex(1);
assertSame(APPLES, cell.getItem());
assertSame(apples, cell.getTableRow().getTreeItem());
}
@Test public void itemIsNullWhenIndexIsOutOfRange() {
cell.updateIndex(50);
cell.updateTreeTableView(tree);
assertNull(cell.getItem());
}
@Test public void treeItemIsNullWhenIndexIsOutOfRange() {
cell.updateIndex(50);
cell.updateTableRow(row);
cell.updateTreeTableView(tree);
assertNull(cell.getTableRow().getTreeItem());
}
@Test public void itemIsNullWhenIndexIsOutOfRange2() {
cell.updateTreeTableView(tree);
cell.updateIndex(50);
assertNull(cell.getItem());
}
@Ignore
@Test public void itemIsUpdatedWhenItWasOutOfRangeButUpdatesToTreeTableViewItemsMakesItInRange() {
cell.updateIndex(4);
cell.updateTreeTableView(tree);
root.getChildren().addAll(new TreeItem<String>("Pumpkin"), new TreeItem<>("Lemon"));
assertSame("Pumpkin", cell.getItem());
}
@Ignore
@Test public void itemIsUpdatedWhenItWasInRangeButUpdatesToTreeTableViewItemsMakesItOutOfRange() {
cell.updateIndex(2);
cell.updateTreeTableView(tree);
assertSame(ORANGES, cell.getItem());
root.getChildren().remove(oranges);
assertNull(cell.getTableRow().getTreeItem());
assertNull(cell.getItem());
}
@Ignore
@Test public void itemIsUpdatedWhenTreeTableViewItemsIsUpdated() {
cell.updateIndex(1);
cell.updateTreeTableView(tree);
assertSame(APPLES, cell.getItem());
assertSame(apples, cell.getTableRow().getTreeItem());
root.getChildren().set(0, new TreeItem<>("Lime"));
assertEquals("Lime", cell.getItem());
}
@Ignore
@Test public void itemIsUpdatedWhenTreeTableViewItemsHasNewItemInsertedBeforeIndex() {
cell.updateIndex(2);
cell.updateTreeTableView(tree);
assertSame(ORANGES, cell.getItem());
assertSame(oranges, cell.getTableRow().getTreeItem());
String previous = APPLES;
root.getChildren().add(0, new TreeItem<>("Lime"));
assertEquals(previous, cell.getItem());
}
@Ignore
@Test public void itemIsUpdatedWhenTreeTableViewItemsIsReplaced() {
cell.updateIndex(1);
cell.updateTreeTableView(tree);
root.getChildren().setAll(new TreeItem<>("Water"), new TreeItem<>("Juice"), new TreeItem<>("Soda"));
assertEquals("Water", cell.getItem());
}
@Ignore
@Test public void itemIsUpdatedWhenTreeTableViewIsReplaced() {
cell.updateIndex(2);
cell.updateTreeTableView(tree);
TreeItem<String> newRoot = new TreeItem<>();
newRoot.setExpanded(true);
newRoot.getChildren().setAll(new TreeItem<>("Water"), new TreeItem<>("Juice"), new TreeItem<>("Soda"));
TreeTableView<String> treeView2 = new TreeTableView<String>(newRoot);
cell.updateTreeTableView(treeView2);
assertEquals("Juice", cell.getItem());
}
@Test public void replaceItemsWithANull() {
cell.updateIndex(0);
cell.updateTreeTableView(tree);
tree.setRoot(null);
assertNull(cell.getItem());
}
@Ignore
@Test public void replaceANullItemsWithNotNull() {
cell.updateIndex(1);
cell.updateTreeTableView(tree);
tree.setRoot(null);
TreeItem<String> newRoot = new TreeItem<>();
newRoot.setExpanded(true);
newRoot.getChildren().setAll(new TreeItem<>("Water"), new TreeItem<>("Juice"), new TreeItem<>("Soda"));
tree.setRoot(newRoot);
assertEquals("Water", cell.getItem());
}
@Ignore
@Test public void editOnTreeTableViewResultsInEditingInCell() {
tree.setEditable(true);
cell.updateTreeTableView(tree);
cell.updateIndex(1);
tree.edit(1, null);
assertTrue(cell.isEditing());
}
@Test public void editOnTreeTableViewResultsInNotEditingInCellWhenDifferentIndex() {
tree.setEditable(true);
cell.updateTreeTableView(tree);
cell.updateIndex(1);
tree.edit(0, null);
assertFalse(cell.isEditing());
}
@Test public void editCellWithNullTreeTableViewResultsInNoExceptions() {
cell.updateTreeTableView(tree);
cell.updateIndex(1);
cell.startEdit();
}
@Test public void editCellOnNonEditableTreeDoesNothing() {
cell.updateIndex(1);
cell.updateTreeTableView(tree);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(tree.getEditingCell());
}
@Test public void editCellWithTreeResultsInUpdatedEditingIndexProperty() {
setupForEditing();
cell.updateIndex(1);
cell.startEdit();
assertEquals(apples, tree.getEditingCell().getTreeItem());
}
@Test public void editCellWithTreeNoColumnResultsInUpdatedEditingIndexProperty() {
cell.updateIndex(1);
setupForcedEditing(tree, null);
cell.startEdit();
assertTrue(cell.isEditing());
assertNotNull(tree.getEditingCell());
assertEquals(apples, tree.getEditingCell().getTreeItem());
}
@Test public void commitWhenTreeIsNullIsOK() {
cell.updateTreeTableView(tree);
cell.updateIndex(1);
cell.startEdit();
cell.commitEdit("Watermelon");
}
@Ignore
@Test public void commitWhenTreeIsNotNullWillUpdateTheItemsTree() {
tree.setEditable(true);
cell.updateTreeTableView(tree);
cell.updateIndex(1);
cell.startEdit();
cell.commitEdit("Watermelon");
assertEquals("Watermelon", tree.getRoot().getChildren().get(0).getValue());
}
@Test public void afterCommitTreeTableViewEditingCellIsNull() {
tree.setEditable(true);
cell.updateTreeTableView(tree);
cell.updateIndex(1);
cell.startEdit();
cell.commitEdit("Watermelon");
assertNull(tree.getEditingCell());
assertFalse(cell.isEditing());
}
@Test public void cancelEditCanBeCalledWhileTreeTableViewIsNull() {
cell.updateTreeTableView(tree);
cell.updateIndex(1);
cell.startEdit();
cell.cancelEdit();
}
@Test public void cancelSetsTreeTableViewEditingIndexToNegativeOne() {
tree.setEditable(true);
cell.updateTreeTableView(tree);
cell.updateIndex(1);
cell.startEdit();
cell.cancelEdit();
assertNull(tree.getEditingCell());
assertFalse(cell.isEditing());
}
@Test public void updateTreeTableViewUpdatesTreeTableView() {
cell.updateTreeTableView(tree);
assertSame(tree, cell.getTreeTableView());
assertSame(tree, cell.treeTableViewProperty().get());
}
@Test public void canSetTreeTableViewBackToNull() {
cell.updateTreeTableView(tree);
cell.updateTreeTableView(null);
assertNull(cell.getTreeTableView());
assertNull(cell.treeTableViewProperty().get());
}
@Test public void treeTableViewPropertyReturnsCorrectBean() {
assertSame(cell, cell.treeTableViewProperty().getBean());
}
@Test public void updateTreeTableViewWithNullFocusModelResultsInNoException() {
cell.updateTreeTableView(tree);
tree.setFocusModel(null);
cell.updateTreeTableView(new TreeTableView());
}
@Test public void updateTreeTableViewWithNullFocusModelResultsInNoException2() {
tree.setFocusModel(null);
cell.updateTreeTableView(tree);
cell.updateTreeTableView(new TreeTableView());
}
@Test public void updateTreeTableViewWithNullFocusModelResultsInNoException3() {
cell.updateTreeTableView(tree);
TreeTableView tree2 = new TreeTableView();
tree2.setFocusModel(null);
cell.updateTreeTableView(tree2);
}
@Test public void updateTreeTableViewWithNullSelectionModelResultsInNoException() {
cell.updateTreeTableView(tree);
tree.setSelectionModel(null);
cell.updateTreeTableView(new TreeTableView());
}
@Test public void updateTreeTableViewWithNullSelectionModelResultsInNoException2() {
tree.setSelectionModel(null);
cell.updateTreeTableView(tree);
cell.updateTreeTableView(new TreeTableView());
}
@Test public void updateTreeTableViewWithNullSelectionModelResultsInNoException3() {
cell.updateTreeTableView(tree);
TreeTableView tree2 = new TreeTableView();
tree2.setSelectionModel(null);
cell.updateTreeTableView(tree2);
}
@Test public void updateTreeTableViewWithNullItemsResultsInNoException() {
cell.updateTreeTableView(tree);
tree.setRoot(null);
cell.updateTreeTableView(new TreeTableView());
}
@Test public void updateTreeTableViewWithNullItemsResultsInNoException2() {
tree.setRoot(null);
cell.updateTreeTableView(tree);
cell.updateTreeTableView(new TreeTableView());
}
@Test public void updateTreeTableViewWithNullItemsResultsInNoException3() {
cell.updateTreeTableView(tree);
TreeTableView tree2 = new TreeTableView();
tree2.setRoot(null);
cell.updateTreeTableView(tree2);
}
@Test public void treeTableViewIsNullByDefault() {
assertNull(cell.getTreeTableView());
assertNull(cell.treeTableViewProperty().get());
}
@Test public void treeTableViewPropertyNameIs_treeTableView() {
assertEquals("treeTableView", cell.treeTableViewProperty().getName());
}
@Test public void checkTableRowPropertyName() {
assertEquals("tableRow", cell.tableRowProperty().getName());
}
@Test public void checkTableColumnPropertyName() {
assertEquals("tableColumn", cell.tableColumnProperty().getName());
}
@Test public void checkTableRowProperty() {
cell.updateTreeTableView(tree);
cell.updateTableRow(row);
assertSame(row, cell.getTableRow());
assertSame(row, cell.tableRowProperty().get());
assertFalse(cell.tableRowProperty() instanceof ObjectProperty);
}
@Test public void checkTableColumnProperty() {
TreeTableColumn<String, String> column = new TreeTableColumn<>();
cell.updateTreeTableView(tree);
cell.updateTableColumn(column);
assertSame(column, cell.getTableColumn());
assertSame(column, cell.tableColumnProperty().get());
assertFalse(cell.tableColumnProperty() instanceof ObjectProperty);
}
private int rt_29923_count = 0;
@Test public void test_rt_29923() {
cell = new TreeTableCellShim<String,String>() {
@Override public void updateItem(String item, boolean empty) {
super.updateItem(item, empty);
rt_29923_count++;
}
};
TreeTableColumn col = new TreeTableColumn("TEST");
col.setCellValueFactory(param -> null);
tree.getColumns().add(col);
cell.updateTableColumn(col);
cell.updateTreeTableView(tree);
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
cell.updateTreeTableView(tree);
tree.setRoot(null);
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
TreeTableColumn column = new TreeTableColumn("Column");
tree.getColumns().add(column);
if (setId && setIdBeforeHeaderInstantiation) {
column.setId("test-id");
}
if (setStyle && setStyleBeforeHeaderInstantiation) {
column.setStyle("-fx-border-color: red");
}
StageLoader sl = new StageLoader(tree);
TreeTableCell cell = (TreeTableCell) VirtualFlowTestUtils.getCell(tree, 0, 0);
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
TreeTableCell cell = new TreeTableCell();
cell.setSkin(new TreeTableCellSkin(cell));
}
@Test
public void testRowIsNotNullWhenAutoSizing() {
TreeTableColumn<String, String> treeTableColumn = new TreeTableColumn<>();
treeTableColumn.setCellFactory(col -> new TreeTableCell<>() {
@Override
protected void updateItem(String item, boolean empty) {
super.updateItem(item, empty);
assertNotNull(getTableRow());
}
});
tree.getColumns().add(treeTableColumn);
stageLoader = new StageLoader(tree);
}
@Test
public void testRowItemIsNotNullForNonEmptyCell() {
TreeTableColumn<String, String> treeTableColumn = new TreeTableColumn<>();
treeTableColumn.setCellValueFactory(cc -> new SimpleStringProperty(cc.getValue().getValue()));
treeTableColumn.setCellFactory(col -> new TreeTableCell<>() {
@Override
protected void updateItem(String item, boolean empty) {
super.updateItem(item, empty);
if (!empty) {
assertNotNull(getTableRow().getItem());
}
}
});
tree.getColumns().add(treeTableColumn);
stageLoader = new StageLoader(tree);
tree.getRoot().getChildren().add(new TreeItem<>("newItem"));
Toolkit.getToolkit().firePulse();
}
@Test
public void testCellInUneditableRowIsNotEditable() {
tree.setEditable(true);
row.setEditable(false);
TreeTableColumn<String, String> treeTableColumn = new TreeTableColumn<>();
treeTableColumn.setEditable(true);
tree.getColumns().add(treeTableColumn);
cell.updateTableColumn(treeTableColumn);
cell.updateTableRow(row);
cell.updateTreeTableView(tree);
cell.updateIndex(0);
cell.startEdit();
assertFalse(cell.isEditing());
}
@Test
public void testCellInUneditableTableIsNotEditable() {
tree.setEditable(false);
row.setEditable(true);
TreeTableColumn<String, String> treeTableColumn = new TreeTableColumn<>();
treeTableColumn.setEditable(true);
tree.getColumns().add(treeTableColumn);
cell.updateTableColumn(treeTableColumn);
cell.updateTableRow(row);
cell.updateTreeTableView(tree);
cell.updateIndex(0);
cell.startEdit();
assertFalse(cell.isEditing());
}
@Test
public void testCellInUneditableColumnIsNotEditable() {
tree.setEditable(true);
row.setEditable(true);
TreeTableColumn<String, String> treeTableColumn = new TreeTableColumn<>();
treeTableColumn.setEditable(false);
tree.getColumns().add(treeTableColumn);
cell.updateTableColumn(treeTableColumn);
cell.updateTableRow(row);
cell.updateTreeTableView(tree);
cell.updateIndex(0);
cell.startEdit();
assertFalse(cell.isEditing());
}
private void setupForEditing() {
tree.setEditable(true);
tree.getColumns().add(editingColumn);
editingColumn.setCellValueFactory(cc -> new SimpleObjectProperty<>(""));
cell.updateTreeTableView(tree);
cell.updateTableColumn(editingColumn);
}
@Test
public void testEditCancelEventAfterCancelOnCell() {
setupForEditing();
int editingIndex = 1;
cell.updateIndex(editingIndex);
tree.edit(editingIndex, editingColumn);
TreeTablePosition<?,?> editingPosition = tree.getEditingCell();
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditCancel(events::add);
cell.cancelEdit();
assertEquals("column must have received editCancel", 1, events.size());
assertEquals("editing location of cancel event", editingPosition, events.get(0).getTreeTablePosition());
}
@Test
public void testEditCancelEventAfterCancelOnTreeTable() {
setupForEditing();
int editingIndex = 1;
cell.updateIndex(editingIndex);
tree.edit(editingIndex, editingColumn);
TreeTablePosition<?, ?> editingPosition = tree.getEditingCell();
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditCancel(events::add);
tree.edit(-1, null);
assertEquals("column must have received editCancel", 1, events.size());
assertEquals("editing location of cancel event", editingPosition, events.get(0).getTreeTablePosition());
}
@Test
public void testEditCancelEventAfterCellReuse() {
setupForEditing();
int editingIndex = 1;
cell.updateIndex(editingIndex);
tree.edit(editingIndex, editingColumn);
TreeTablePosition<?, ?> editingPosition = tree.getEditingCell();
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditCancel(events::add);
cell.updateIndex(0);
assertEquals("column must have received editCancel", 1, events.size());
assertEquals("editing location of cancel event", editingPosition, events.get(0).getTreeTablePosition());
}
@Test
public void testEditCancelEventAfterCollapse() {
setupForEditing();
stageLoader = new StageLoader(tree);
int editingIndex = 1;
tree.edit(editingIndex, editingColumn);
TreeTablePosition<?, ?> editingPosition = tree.getEditingCell();
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditCancel(events::add);
root.setExpanded(false);
Toolkit.getToolkit().firePulse();
assertEquals("column must have received editCancel", 1, events.size());
assertEquals("editing location of cancel event", editingPosition, events.get(0).getTreeTablePosition());
}
@Test
public void testEditCancelEventAfterModifyItems() {
setupForEditing();
stageLoader = new StageLoader(tree);
int editingIndex = 2;
tree.edit(editingIndex, editingColumn);
TreeTablePosition<?, ?> editingPosition = tree.getEditingCell();
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditCancel(events::add);
root.getChildren().add(0, new TreeItem<>("added"));
Toolkit.getToolkit().firePulse();
assertNull("sanity: editing terminated on items modification", tree.getEditingCell());
assertEquals("column must have received editCancel", 1, events.size());
assertEquals("editing location of cancel event", editingPosition, events.get(0).getTreeTablePosition());
}
@Test
public void testEditCancelEventAfterRemoveEditingItem() {
setupForEditing();
stageLoader = new StageLoader(tree);
int editingIndex = 1;
tree.edit(editingIndex, editingColumn);
TreeTablePosition<?, ?> editingPosition = tree.getEditingCell();
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditCancel(events::add);
root.getChildren().remove(editingIndex - 1);
Toolkit.getToolkit().firePulse();
assertNull("sanity: editing terminated on items modification", tree.getEditingCell());
assertEquals("column must have received editCancel", 1, events.size());
assertEquals("editing location of cancel event", editingPosition, events.get(0).getTreeTablePosition());
}
@Test
public void testEditCancelMemoryLeakAfterRemoveEditingItem() {
setupForEditing();
stageLoader = new StageLoader(tree);
TreeItem<String> editingItem = new TreeItem<>("added");
WeakReference<TreeItem<?>> itemRef = new WeakReference<>(editingItem);
root.getChildren().add(0, editingItem);
Toolkit.getToolkit().firePulse();
int editingIndex = tree.getRow(editingItem);
tree.edit(editingIndex, editingColumn);
root.getChildren().remove(editingItem);
Toolkit.getToolkit().firePulse();
editingItem = null;
attemptGC(itemRef);
assertEquals("treeItem must be gc'ed", null, itemRef.get());
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
TreeTablePosition<?, ?> editingCell = events.get(0).getTreeTablePosition();
assertEquals(editingIndex, editingCell.getRow());
}
@Test
public void testEditStartEventAfterStartOnTable() {
setupForEditing();
int editingIndex = 1;
cell.updateIndex(editingIndex);
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditStart(events::add);
tree.edit(editingIndex, editingColumn);
assertEquals(editingColumn, events.get(0).getTableColumn());
TreeTablePosition<?, ?> editingCell = events.get(0).getTreeTablePosition();
assertEquals(editingIndex, editingCell.getRow());
}
@Test
public void testCommitEditMustNotFireCancel() {
setupForEditing();
editingColumn.setOnEditCommit(e -> {
TreeItem<String> treeItem = tree.getTreeItem(e.getTreeTablePosition().getRow());
treeItem.setValue(e.getNewValue());
tree.edit(-1, null);
});
int editingRow = 1;
cell.updateIndex(editingRow);
tree.edit(editingRow, editingColumn);
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditCancel(events::add);
String value = "edited";
cell.commitEdit(value);
assertEquals("sanity: value committed", value, tree.getTreeItem(editingRow).getValue());
assertEquals("commit must not have fired editCancel", 0, events.size());
}
@Test
public void testEditCommitEvent() {
setupForEditing();
int editingIndex = 1;
cell.updateIndex(editingIndex);
cell.startEdit();
TreeTablePosition<?, ?> editingPosition = tree.getEditingCell();
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditCommit(events::add);
cell.commitEdit("edited");
assertEquals("column must have received editCommit", 1, events.size());
assertEquals("editing location of commit event must be same as table's editingCell",
editingPosition, events.get(0).getTreeTablePosition());
}
@Test
public void testEditCommitEditingCellAtStartEdit() {
setupForEditing();
int editingIndex = 1;
cell.updateIndex(editingIndex);
cell.startEdit();
TreeTablePosition<?, ?> editingCellAtStartEdit = TreeTableCellShim.getEditingCellAtStartEdit(cell);
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.setOnEditCommit(events::add);
cell.commitEdit("edited");
assertEquals("column must have received editCommit", 1, events.size());
assertEquals("editing location of commit event must be same as editingCellAtStartEdit",
editingCellAtStartEdit, events.get(0).getTreeTablePosition());
}
@Test
public void testEditCommitEventNullTable() {
setupForcedEditing(null, editingColumn);
cell.startEdit();
TreeTablePosition<?, ?> editingCellAtStartEdit = TreeTableCellShim.getEditingCellAtStartEdit(cell);
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.addEventHandler(TreeTableColumn.editAnyEvent(), events::add);
cell.commitEdit("edited");
assertEquals("column must have received editCommit", 1, events.size());
assertEquals("editing location of commit event must be same as editingCellAtStartEdit",
editingCellAtStartEdit, events.get(0).getTreeTablePosition());
}
@Test
public void testEditStartNullTable() {
setupForcedEditing(null, editingColumn);
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.addEventHandler(TreeTableColumn.editAnyEvent(), events::add);
cell.startEdit();
assertEquals(1, events.size());
}
@Test
public void testEditCancelNullTable() {
setupForcedEditing(null, editingColumn);
cell.startEdit();
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.addEventHandler(TreeTableColumn.editAnyEvent(), events::add);
cell.cancelEdit();
assertEquals(1, events.size());
}
@Test
public void testEditCommitNullTable() {
setupForcedEditing(null, editingColumn);
cell.startEdit();
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.addEventHandler(TreeTableColumn.editAnyEvent(), events::add);
cell.commitEdit("edited");
assertEquals(1, events.size());
}
@Test
public void testEditStartNullColumn() {
setupForcedEditing(tree, null);
cell.startEdit();
}
@Test
public void testEditCancelNullColumn() {
setupForcedEditing(tree, null);
cell.startEdit();
cell.cancelEdit();
}
@Test
public void testEditCommitNullColumn() {
setupForcedEditing(tree, null);
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
cell.updateIndex(tree.getExpandedItemCount());
List<CellEditEvent<?, ?>> events = new ArrayList<>();
editingColumn.addEventHandler(TreeTableColumn.editStartEvent(), events::add);
cell.startEdit();
assertFalse("sanity: off-range cell must not be editing", cell.isEditing());
assertEquals("cell must not fire editStart if not editing", 0, events.size());
}
@Test
public void testStartEditOffRangeMustNotUpdateEditingLocation() {
setupForEditing();
cell.updateIndex(tree.getExpandedItemCount());
cell.startEdit();
assertFalse("sanity: off-range cell must not be editing", cell.isEditing());
assertNull("treetable editing location must not be updated", tree.getEditingCell());
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
setupForcedEditing(tree, null);
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
private void setupForcedEditing(TreeTableView table, TreeTableColumn editingColumn) {
if (table != null) {
table.setEditable(true);
cell.updateTreeTableView(table);
}
if (editingColumn != null ) cell.updateTableColumn(editingColumn);
TreeTableCellShim.set_lockItemOnEdit(cell, true);
CellShim.updateItem(cell, "something", false);
}
@Test
public void testMisbehavingCancelEditTerminatesEdit() {
TreeTableCell<String, String> cell = new MisbehavingOnCancelTreeTableCell<>();
tree.setEditable(true);
TreeTableColumn<String, String> editingColumn = new TreeTableColumn<>("TEST");
editingColumn.setCellValueFactory(param -> null);
tree.getColumns().add(editingColumn);
cell.updateTreeTableView(tree);
cell.updateTableColumn(editingColumn);
int editingIndex = 1;
int intermediate = 0;
cell.updateIndex(editingIndex);
tree.edit(editingIndex, editingColumn);
assertTrue("sanity: ", cell.isEditing());
try {
tree.edit(intermediate, editingColumn);
} catch (Exception ex) {
} finally {
assertFalse("cell must not be editing", cell.isEditing());
assertEquals("table must be editing at intermediate index", intermediate, tree.getEditingCell().getRow());
}
tree.edit(editingIndex, editingColumn);
assertTrue("sanity: ", cell.isEditing());
try {
cell.cancelEdit();
} catch (Exception ex) {
} finally {
assertFalse("cell must not be editing", cell.isEditing());
assertNull("table editing must be cancelled by cell", tree.getEditingCell());
}
}
public static class MisbehavingOnCancelTreeTableCell<S, T> extends TreeTableCell<S, T> {
@Override
public void cancelEdit() {
super.cancelEdit();
throw new RuntimeException("violating contract");
}
}
}
