package test.javafx.scene.control;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.skin.TreeTableRowSkin;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils.*;
import static org.junit.Assert.*;
public class TreeTableRowTest {
private TreeTableRow<String> cell;
private TreeTableView<String> tree;
private static final String ROOT = "Root";
private static final String APPLES = "Apples";
private static final String ORANGES = "Oranges";
private static final String PEARS = "Pears";
private TreeItem<String> root;
private TreeItem<String> apples;
private TreeItem<String> oranges;
private TreeItem<String> pears;
@Before public void setup() {
cell = new TreeTableRow<String>();
root = new TreeItem<>(ROOT);
apples = new TreeItem<>(APPLES);
oranges = new TreeItem<>(ORANGES);
pears = new TreeItem<>(PEARS);
root.getChildren().addAll(apples, oranges, pears);
tree = new TreeTableView<String>(root);
root.setExpanded(true);
}
@Test public void styleClassIs_tree_table_row_cell_byDefault() {
assertStyleClassContains(cell, "tree-table-row-cell");
}
@Test public void itemIsNullByDefault() {
assertNull(cell.getItem());
}
@Test public void treeViewIsNullByDefault() {
assertNull(cell.getTreeTableView());
assertNull(cell.treeTableViewProperty().get());
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
@Test public void treeViewPropertyReturnsCorrectBean() {
assertSame(cell, cell.treeTableViewProperty().getBean());
}
@Test public void treeViewPropertyNameIs_treeView() {
assertEquals("treeTableView", cell.treeTableViewProperty().getName());
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
@Test public void itemMatchesIndexWithinTreeItems() {
cell.updateIndex(0);
cell.updateTreeTableView(tree);
assertSame(ROOT, cell.getItem());
assertSame(root, cell.getTreeItem());
cell.updateIndex(1);
assertSame(APPLES, cell.getItem());
assertSame(apples, cell.getTreeItem());
}
@Test public void itemMatchesIndexWithinTreeItems2() {
cell.updateTreeTableView(tree);
cell.updateIndex(0);
assertSame(ROOT, cell.getItem());
assertSame(root, cell.getTreeItem());
cell.updateIndex(1);
assertSame(APPLES, cell.getItem());
assertSame(apples, cell.getTreeItem());
}
@Test public void itemIsNullWhenIndexIsOutOfRange() {
cell.updateIndex(50);
cell.updateTreeTableView(tree);
assertNull(cell.getItem());
}
@Test public void treeItemIsNullWhenIndexIsOutOfRange() {
cell.updateIndex(50);
cell.updateTreeTableView(tree);
assertNull(cell.getTreeItem());
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
assertNull(cell.getTreeItem());
assertNull(cell.getItem());
}
@Ignore
@Test public void itemIsUpdatedWhenTreeTableViewItemsIsUpdated() {
cell.updateIndex(1);
cell.updateTreeTableView(tree);
assertSame(APPLES, cell.getItem());
assertSame(apples, cell.getTreeItem());
root.getChildren().set(0, new TreeItem<>("Lime"));
assertEquals("Lime", cell.getItem());
}
@Ignore
@Test public void itemIsUpdatedWhenTreeTableViewItemsHasNewItemInsertedBeforeIndex() {
cell.updateIndex(2);
cell.updateTreeTableView(tree);
assertSame(ORANGES, cell.getItem());
assertSame(oranges, cell.getTreeItem());
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
@Ignore
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
@Test public void selectionOnSelectionModelIsReflectedInCells() {
cell.updateTreeTableView(tree);
cell.updateIndex(0);
TreeTableCell<String,String> other = new TreeTableCell<String,String>();
other.updateTreeTableView(tree);
other.updateIndex(1);
tree.getSelectionModel().selectFirst();
assertTrue(cell.isSelected());
assertFalse(other.isSelected());
}
@Ignore
@Test public void changesToSelectionOnSelectionModelAreReflectedInCells() {
cell.updateTreeTableView(tree);
cell.updateIndex(0);
TreeTableCell<String,String> other = new TreeTableCell<String,String>();
other.updateTreeTableView(tree);
other.updateIndex(1);
tree.getSelectionModel().selectFirst();
tree.getSelectionModel().selectNext();
assertFalse(cell.isSelected());
assertTrue(other.isSelected());
}
@Ignore
@Test public void changesToSelectionOnSelectionModelAreReflectedInCells_MultipleSelection() {
tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
cell.updateTreeTableView(tree);
cell.updateIndex(0);
TreeTableCell<String,String> other = new TreeTableCell<String,String>();
other.updateTreeTableView(tree);
other.updateIndex(1);
tree.getSelectionModel().selectFirst();
tree.getSelectionModel().selectNext();
assertTrue(cell.isSelected());
assertTrue(other.isSelected());
}
@Test public void setANullSelectionModel() {
cell.updateIndex(0);
cell.updateTreeTableView(tree);
TreeTableCell<String,String> other = new TreeTableCell<String,String>();
other.updateTreeTableView(tree);
other.updateIndex(1);
tree.setSelectionModel(null);
assertFalse(cell.isSelected());
assertFalse(other.isSelected());
}
@Test public void focusOnFocusModelIsReflectedInCells() {
cell.updateTreeTableView(tree);
cell.updateIndex(0);
TreeTableCell<String,String> other = new TreeTableCell<String,String>();
other.updateTreeTableView(tree);
other.updateIndex(1);
tree.getFocusModel().focus(0);
assertTrue(cell.isFocused());
assertFalse(other.isFocused());
}
@Ignore
@Test public void changesToFocusOnFocusModelAreReflectedInCells() {
cell.updateTreeTableView(tree);
cell.updateIndex(0);
TreeTableCell<String,String> other = new TreeTableCell<String,String>();
other.updateTreeTableView(tree);
other.updateIndex(1);
tree.getFocusModel().focus(0);
tree.getFocusModel().focus(1);
assertFalse(cell.isFocused());
assertTrue(other.isFocused());
}
@Test public void setANullFocusModel() {
cell.updateIndex(0);
cell.updateTreeTableView(tree);
TreeTableCell<String,String> other = new TreeTableCell<String,String>();
other.updateTreeTableView(tree);
other.updateIndex(1);
tree.setFocusModel(null);
assertFalse(cell.isSelected());
assertFalse(other.isSelected());
}
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
@Ignore
@Test public void editCellWithTreeResultsInUpdatedEditingIndexProperty() {
tree.setEditable(true);
cell.updateTreeTableView(tree);
cell.updateIndex(1);
cell.startEdit();
assertEquals(apples, tree.getEditingCell().getTreeItem());
}
@Test public void commitWhenTreeIsNullIsOK() {
cell.updateIndex(1);
cell.startEdit();
cell.commitEdit("Watermelon");
}
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
cell.updateIndex(1);
cell.startEdit();
cell.cancelEdit();
}
@Test public void cancelSetsTreeTableViewEditingCellIsNull() {
tree.setEditable(true);
cell.updateTreeTableView(tree);
cell.updateIndex(1);
cell.startEdit();
cell.cancelEdit();
assertNull(tree.getEditingCell());
assertFalse(cell.isEditing());
}
@Ignore
@Test public void movingTreeCellEditingIndexCausesCurrentlyInEditCellToCancel() {
tree.setEditable(true);
cell.updateTreeTableView(tree);
cell.updateIndex(0);
cell.startEdit();
TreeTableCell<String,String> other = new TreeTableCell<String,String>();
other.updateTreeTableView(tree);
other.updateIndex(1);
tree.edit(1, null);
assertTrue(other.isEditing());
assertFalse(cell.isEditing());
}
@Test public void tableViewIsNullByDefault() {
assertNull(cell.getTreeTableView());
assertNull(cell.treeTableViewProperty().get());
}
@Test public void updateTreeTableViewUpdatesTableView() {
cell.updateTreeTableView(tree);
assertSame(tree, cell.getTreeTableView());
assertSame(tree, cell.treeTableViewProperty().get());
}
@Test public void canSetTableViewBackToNull() {
cell.updateTreeTableView(tree);
cell.updateTreeTableView(null);
assertNull(cell.getTreeTableView());
assertNull(cell.treeTableViewProperty().get());
}
@Test public void tableViewPropertyReturnsCorrectBean() {
assertSame(cell, cell.treeTableViewProperty().getBean());
}
@Test public void tableViewPropertyNameIs_treeTableView() {
assertEquals("treeTableView", cell.treeTableViewProperty().getName());
}
@Test public void test_rt_33106() {
cell.updateTreeTableView(tree);
tree.setRoot(null);
cell.updateIndex(1);
}
@Test public void test_jdk_8151524() {
TreeTableRow cell = new TreeTableRow();
cell.setSkin(new TreeTableRowSkin(cell));
}
}
