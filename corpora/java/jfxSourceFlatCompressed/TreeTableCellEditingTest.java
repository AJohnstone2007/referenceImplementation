package test.javafx.scene.control;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.TreeTableView;
@RunWith(Parameterized.class)
public class TreeTableCellEditingTest {
private TreeTableCell<String,String> cell;
private TreeTableView<String> table;
private TreeTableColumn<String, String> editingColumn;
private ObservableList<TreeItem<String>> model;
private int cellIndex;
private int editingIndex;
@Test
public void testOffEditingIndex() {
cell.updateIndex(editingIndex);
table.edit(editingIndex, editingColumn);
cell.updateIndex(cellIndex);
assertEquals("sanity: cell index changed", cellIndex, cell.getIndex());
assertEquals("sanity: treeTable editingIndex must be unchanged", editingIndex, table.getEditingCell().getRow());
assertEquals("sanity: treeTable editingColumn must be unchanged", editingColumn, table.getEditingCell().getTableColumn());
assertFalse("cell must not be editing on update from editingIndex" + editingIndex
+ " to cellIndex " + cellIndex, cell.isEditing());
}
@Test
public void testCancelOffEditingIndex() {
cell.updateIndex(editingIndex);
table.edit(editingIndex, editingColumn);
List<CellEditEvent<String, String>> events = new ArrayList<>();
editingColumn.setOnEditCancel(e -> {
events.add(e);
});
cell.updateIndex(cellIndex);
assertEquals("cell must have fired edit cancel", 1, events.size());
assertEquals("cancel event index must be same as editingIndex", editingIndex,
events.get(0).getTreeTablePosition().getRow());
assertEquals("cancel event index must be same as editingIndex",
editingIndex, table.getEditingCell().getRow());
}
@Test
public void testToEditingIndex() {
cell.updateIndex(cellIndex);
table.edit(editingIndex, editingColumn);
cell.updateIndex(editingIndex);
assertEquals("sanity: cell at editing index", editingIndex, cell.getIndex());
assertEquals("sanity: treeTable editingIndex must be unchanged", editingIndex, table.getEditingCell().getRow());
assertEquals("sanity: treeTable editingColumn must be unchanged", editingColumn, table.getEditingCell().getTableColumn());
assertTrue("cell must be editing on update from " + cellIndex
+ " to editingIndex " + editingIndex, cell.isEditing());
}
@Test
public void testStartEvent() {
cell.updateIndex(cellIndex);
table.edit(editingIndex, editingColumn);
List<CellEditEvent<String, String>> events = new ArrayList<>();
editingColumn.setOnEditStart(e -> {
events.add(e);
});
cell.updateIndex(editingIndex);
assertEquals("cell must have fired edit start on update from " + cellIndex + " to " + editingIndex,
1, events.size());
assertEquals("start event index must be same as editingIndex", editingIndex,
events.get(0).getTreeTablePosition().getRow());
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
Object[][] data = new Object[][] {
{1, 2},
{0, 1},
{1, 0},
{-1, 1},
};
return Arrays.asList(data);
}
public TreeTableCellEditingTest(int cellIndex, int editingIndex) {
this.cellIndex = cellIndex;
this.editingIndex = editingIndex;
}
@Test
public void testEditOnCellIndex() {
cell.updateIndex(editingIndex);
table.edit(editingIndex, editingColumn);
assertTrue("sanity: cell must be editing", cell.isEditing());
}
@Test
public void testEditOffCellIndex() {
cell.updateIndex(cellIndex);
table.edit(editingIndex, editingColumn);
assertFalse("sanity: cell editing must be unchanged", cell.isEditing());
}
@Before
public void setup() {
cell = new TreeTableCell<String,String>();
model = FXCollections.observableArrayList(new TreeItem<>("Four"),
new TreeItem<>("Five"), new TreeItem<>("Fear"));
TreeItem<String> root = new TreeItem<>("root");
root.getChildren().addAll(model);
root.setExpanded(true);
table = new TreeTableView<String>(root);
table.setEditable(true);
editingColumn = new TreeTableColumn<>("TEST");
editingColumn.setCellValueFactory(param -> null);
table.getColumns().add(editingColumn);
cell.updateTreeTableView(table);
cell.updateTableColumn(editingColumn);
table.getFocusModel().focus(-1);
assertFalse("sanity: cellIndex not same as editingIndex", cellIndex == editingIndex);
assertTrue("sanity: valid editingIndex", editingIndex < model.size());
}
}
