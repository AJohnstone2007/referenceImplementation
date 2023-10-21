package test.javafx.scene.control;
import java.util.Arrays;
import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewFocusModel;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TableViewShim;
import javafx.scene.control.TreeViewShim;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class TableViewSelectionModelImplTest {
private TableViewSelectionModel<String> model;
private TableViewFocusModel focusModel;
private Class<? extends TableViewSelectionModel >modelClass;
private static ObservableList<String> defaultData = FXCollections.<String>observableArrayList();
private static ObservableList<String> data = FXCollections.<String>observableArrayList();
private static final String ROW_1_VALUE = "Row 1";
private static final String ROW_2_VALUE = "Row 2";
private static final String ROW_5_VALUE = "Row 5";
private static final String ROW_20_VALUE = "Row 20";
private static final TableView<String> tableView;
private static final TableColumn<String,String> col0;
private static final TableColumn<String,String> col1;
private static final TableColumn<String,String> col2;
static {
defaultData.addAll(ROW_1_VALUE, ROW_2_VALUE, "Long Row 3", "Row 4", ROW_5_VALUE, "Row 6",
"Row 7", "Row 8", "Row 9", "Row 10", "Row 11", "Row 12", "Row 13",
"Row 14", "Row 15", "Row 16", "Row 17", "Row 18", "Row 19", ROW_20_VALUE);
data.setAll(defaultData);
tableView = new TableView();
tableView.setItems(data);
tableView.getColumns().addAll(
col0 = new TableColumn<>(),
col1 = new TableColumn<>(),
col2 = new TableColumn<>()
);
}
@Parameters public static Collection implementations() {
return Arrays.asList(new Object[][] {
{ TableViewShim.get_TableViewArrayListSelectionModel_class() }
});
}
public TableViewSelectionModelImplTest(Class<? extends TableViewSelectionModel> modelClass) {
this.modelClass = modelClass;
}
@AfterClass public static void tearDownClass() throws Exception { }
@Before public void setUp() throws Exception {
data.setAll(defaultData);
if (TableViewSelectionModel.class.isAssignableFrom(modelClass)) {
model = TreeViewShim.newInstance_from_class(modelClass, tableView);
tableView.setSelectionModel((TableViewSelectionModel) model);
focusModel = new TableViewFocusModel(tableView);
tableView.setFocusModel(focusModel);
}
}
@After public void tearDown() {
model = null;
}
private Object getValue(Object item) {
return item;
}
private String indices(MultipleSelectionModel sm) {
return "Selected Indices: " + sm.getSelectedIndices();
}
private String items(MultipleSelectionModel sm) {
return "Selected Items: " + sm.getSelectedItems();
}
private String cells(TableViewSelectionModel<?> sm) {
StringBuilder sb = new StringBuilder("Selected Cells: ");
for (TablePosition tp : sm.getSelectedCells()) {
sb.append("(");
sb.append(tp.getRow());
sb.append(",");
sb.append(tp.getColumn());
sb.append(") ");
}
return sb.toString();
}
private String focusedCell() {
return "Focused Cell: " + focusModel.getFocusedCell();
}
private TablePosition pos(int row, TableColumn<String,?> col) {
return new TablePosition(tableView, row, col);
}
@Test public void selectRowWhenInSingleCellSelectionMode() {
model.setSelectionMode(SelectionMode.SINGLE);
model.setCellSelectionEnabled(true);
assertFalse(model.isSelected(3, col0));
model.select(1);
model.select(3);
assertFalse(model.isSelected(1, col0));
assertFalse(model.isSelected(3, col0));
assertFalse(cells(model), model.isSelected(3, null));
assertFalse(model.isSelected(3));
assertEquals(1, model.getSelectedCells().size());
}
@Test public void selectRowWhenInSingleCellSelectionMode2() {
model.setSelectionMode(SelectionMode.SINGLE);
model.setCellSelectionEnabled(true);
model.select(1, null);
model.select(3, null);
assertFalse(model.isSelected(1, col0));
assertFalse(model.isSelected(3, col0));
assertFalse(cells(model), model.isSelected(3, null));
assertFalse(model.isSelected(3));
assertEquals(1, model.getSelectedCells().size());
}
@Test public void selectRowWhenInMultipleCellSelectionMode() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(true);
model.clearSelection();
model.select(1);
model.select(3);
assertTrue(model.isSelected(1, col0));
assertTrue(model.isSelected(3, col0));
assertTrue(model.isSelected(3, null));
assertTrue(model.isSelected(3));
assertEquals(6, model.getSelectedCells().size());
}
@Test public void selectCellWhenInSingleCellSelectionMode() {
model.setSelectionMode(SelectionMode.SINGLE);
model.setCellSelectionEnabled(true);
assertFalse(model.isSelected(3, col0));
model.select(1, col0);
model.select(3, col0);
assertFalse(model.isSelected(1, col0));
assertTrue(model.isSelected(3, col0));
assertEquals(1, model.getSelectedCells().size());
assertEquals(pos(3, col0), model.getSelectedCells().get(0));
}
@Test public void selectCellWhenInMultipleCellSelectionMode() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(true);
assertFalse(model.isSelected(3, col0));
model.clearSelection();
model.select(1, col0);
model.select(3, col0);
assertTrue(model.isSelected(1, col0));
assertTrue(model.isSelected(3, col0));
assertEquals(2, model.getSelectedCells().size());
assertEquals(pos(1, col0), model.getSelectedCells().get(0));
assertEquals(pos(3, col0), model.getSelectedCells().get(1));
}
@Test public void selectLeftCell() {
model.setSelectionMode(SelectionMode.SINGLE);
model.setCellSelectionEnabled(true);
model.select(2, col2);
assertTrue(model.isSelected(2, col2));
model.selectLeftCell();
assertFalse(model.isSelected(2, col2));
assertTrue(model.isSelected(2, col1));
}
@Test public void selectRightCell() {
model.setSelectionMode(SelectionMode.SINGLE);
model.setCellSelectionEnabled(true);
model.select(2, col1);
assertTrue(model.isSelected(2, col1));
model.selectRightCell();
assertFalse(model.isSelected(2, col1));
assertTrue(model.isSelected(2, col2));
}
@Test public void selectPreviousCell() {
model.setSelectionMode(SelectionMode.SINGLE);
model.setCellSelectionEnabled(true);
model.select(2, col1);
assertTrue(model.isSelected(2, col1));
model.selectAboveCell();
assertFalse(model.isSelected(2, col1));
assertTrue(model.isSelected(1, col1));
}
@Test public void selectNextCell() {
model.setSelectionMode(SelectionMode.SINGLE);
model.setCellSelectionEnabled(true);
model.select(2, col1);
assertTrue(model.isSelected(2, col1));
model.selectBelowCell();
assertFalse(model.isSelected(2, col1));
assertTrue(model.isSelected(3, col1));
}
@Test public void selectLeftCellWhenAtEdge() {
model.setSelectionMode(SelectionMode.SINGLE);
model.setCellSelectionEnabled(true);
model.select(0, col0);
assertTrue(model.isSelected(0, col0));
model.selectLeftCell();
assertTrue(model.isSelected(0, col0));
assertEquals(1, model.getSelectedCells().size());
}
@Test public void selectRightCellWhenAtEdge() {
model.setSelectionMode(SelectionMode.SINGLE);
model.setCellSelectionEnabled(true);
TableColumn<String,?> rightEdge = tableView.getVisibleLeafColumn(tableView.getVisibleLeafColumns().size() - 1);
model.select(0, rightEdge);
assertTrue(model.isSelected(0, rightEdge));
model.selectRightCell();
assertTrue(model.isSelected(0, rightEdge));
assertEquals(1, model.getSelectedCells().size());
}
@Test public void selectPreviousCellWhenAtEdge() {
model.setSelectionMode(SelectionMode.SINGLE);
model.setCellSelectionEnabled(true);
model.select(0, col0);
assertTrue(model.isSelected(0, col0));
model.selectAboveCell();
assertTrue(model.isSelected(0, col0));
assertEquals(1, model.getSelectedCells().size());
}
@Test public void selectNextCellWhenAtEdge() {
model.setSelectionMode(SelectionMode.SINGLE);
model.setCellSelectionEnabled(true);
int count = data.size();
model.select(count - 1, col0);
assertTrue(model.isSelected(count - 1, col0));
model.selectBelowCell();
assertTrue(model.isSelected(count - 1, col0));
assertEquals(1, model.getSelectedCells().size());
}
@Test public void selectNextRowInCellSelectionMode() {
model.setSelectionMode(SelectionMode.SINGLE);
model.setCellSelectionEnabled(true);
model.select(2, col1);
assertTrue(model.isSelected(2, col1));
model.selectNext();
assertFalse(cells(model), model.isSelected(2, col1));
assertTrue(cells(model), model.isSelected(2, col2));
}
@Test public void selectPreviousRowInCellSelectionMode() {
model.setSelectionMode(SelectionMode.SINGLE);
model.setCellSelectionEnabled(true);
model.select(2, col1);
assertTrue(model.isSelected(2, col1));
model.selectPrevious();
assertFalse(model.isSelected(2, col1));
assertTrue(model.isSelected(2, col0));
}
@Test public void selectPreviousCellWithMultipleSelection() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(true);
model.clearSelection();
model.select(2, col1);
model.selectAboveCell();
assertTrue(model.isSelected(2, col1));
assertTrue(model.isSelected(1, col1));
assertEquals(2, model.getSelectedCells().size());
}
@Test public void selectNextCellWithMultipleSelection() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(true);
model.clearSelection();
model.select(2, col1);
model.selectBelowCell();
assertTrue(model.isSelected(2, col1));
assertTrue(model.isSelected(3, col1));
assertEquals(2, model.getSelectedCells().size());
}
@Test public void selectLeftCellWithMultipleSelection() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(true);
model.clearSelection();
model.select(2, col1);
model.selectLeftCell();
assertTrue(cells(model), model.isSelected(2, col1));
assertTrue(cells(model), model.isSelected(2, col0));
assertEquals(2, model.getSelectedCells().size());
}
@Test public void selectRightCellWithMultipleSelection() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(true);
model.clearSelection();
model.select(2, col1);
model.selectRightCell();
assertTrue(cells(model), model.isSelected(2, col1));
assertTrue(cells(model), model.isSelected(2, col2));
assertEquals(2, model.getSelectedCells().size());
}
@Test public void testIsSelectedWithNullColumnInput() {
model.setCellSelectionEnabled(true);
assertFalse(model.isSelected(0, null));
model.select(10);
assertFalse(cells(model), model.isSelected(10, null));
assertEquals(1, model.getSelectedCells().size());
}
@Test public void ensureCellSelectionIsNoOpWhenDisabled() {
model.setCellSelectionEnabled(false);
model.select(2, col2);
assertEquals(2, model.getSelectedCells().get(0).getRow());
assertEquals(col2, model.getSelectedCells().get(0).getTableColumn());
assertTrue(cells(model), model.isSelected(2, col2));
assertTrue(cells(model), model.isSelected(2));
}
@Test public void clearSelectionOfSelectedCell() {
model.setCellSelectionEnabled(true);
model.setSelectionMode(SelectionMode.MULTIPLE);
model.select(2, col2);
model.select(3, col2);
assertTrue(model.isSelected(2, col2));
assertTrue(model.isSelected(3, col2));
model.clearSelection(2, col2);
assertFalse(model.isSelected(2, col2));
assertTrue(model.isSelected(3, col2));
}
@Test public void clearSelectionOfCellWhenInRowSelectionMode() {
model.setCellSelectionEnabled(false);
model.setSelectionMode(SelectionMode.MULTIPLE);
model.select(2);
model.select(3);
assertTrue(model.isSelected(2));
assertTrue(model.isSelected(3));
model.clearSelection(2);
assertFalse(indices(model), model.isSelected(2));
assertTrue(indices(model), model.isSelected(3));
}
@Test public void selectNextCellWhenAtFirstCell() {
model.setCellSelectionEnabled(true);
model.select(0, col0);
model.selectBelowCell();
assertTrue(cells(model), model.isSelected(1, col0));
}
@Test public void selectFirstRowInSingleSelectionRowMode() {
model.setSelectionMode(SelectionMode.SINGLE);
model.setCellSelectionEnabled(false);
model.select(4);
model.selectFirst();
assertTrue(cells(model), model.isSelected(0, null));
assertTrue(cells(model), model.isSelected(0));
assertEquals(1, model.getSelectedCells().size());
}
@Test public void selectFirstRowInSingleSelectionCellMode() {
model.setSelectionMode(SelectionMode.SINGLE);
model.setCellSelectionEnabled(true);
model.select(4, col1);
model.selectFirst();
assertTrue(cells(model), model.isSelected(0, col1));
assertFalse(cells(model), model.isSelected(0, null));
assertFalse(cells(model), model.isSelected(0, col0));
assertEquals(1, model.getSelectedCells().size());
}
@Test public void selectFirstRowInMultipleSelectionRowMode() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(false);
model.select(4);
model.selectFirst();
assertTrue(cells(model), model.isSelected(0));
assertTrue(cells(model), model.isSelected(0, null));
assertTrue(cells(model), model.isSelected(4));
assertEquals(2, model.getSelectedCells().size());
}
@Test public void selectFirstRowInMultipleSelectionCellMode() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(true);
model.clearSelection();
model.select(4, col1);
model.selectFirst();
assertTrue(cells(model), model.isSelected(0, col1));
assertTrue(cells(model), model.isSelected(4, col1));
assertEquals(2, model.getSelectedCells().size());
}
@Test public void selectLastRowInSingleSelectionRowMode() {
model.setSelectionMode(SelectionMode.SINGLE);
model.setCellSelectionEnabled(false);
model.select(4);
model.selectLast();
assertTrue(cells(model), model.isSelected(tableView.getItems().size() - 1));
assertEquals(1, model.getSelectedCells().size());
}
@Test public void selectLastRowInSingleSelectionCellMode() {
model.setSelectionMode(SelectionMode.SINGLE);
model.setCellSelectionEnabled(true);
model.select(4, col1);
model.selectLast();
assertTrue(cells(model), model.isSelected(tableView.getItems().size() - 1, col1));
assertEquals(1, model.getSelectedCells().size());
}
@Test public void selectLastRowInMultipleSelectionRowMode() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(false);
model.clearSelection();
model.select(4);
model.selectLast();
assertTrue(cells(model), model.isSelected(tableView.getItems().size() - 1));
assertTrue(cells(model), model.isSelected(4));
assertEquals(2, model.getSelectedCells().size());
}
@Test public void selectLastRowInMultipleSelectionCellMode() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(true);
model.clearSelection();
model.select(4, col1);
model.selectLast();
assertTrue(cells(model), model.isSelected(tableView.getItems().size() - 1, col1));
assertTrue(cells(model), model.isSelected(4, col1));
assertEquals(2, model.getSelectedCells().size());
}
@Test public void selectCellInRowSelectionMode_expectCellInformationToRemain() {
model.setCellSelectionEnabled(false);
model.select(4, col0);
assertEquals(cells(model), col0, model.getSelectedCells().get(0).getTableColumn());
assertEquals(col0, focusModel.getFocusedCell().getTableColumn());
assertTrue(model.isSelected(4, col0));
assertTrue(model.isSelected(4));
}
@Test public void focusOnRow() {
focusModel.focus(3);
assertTrue(focusedCell(), focusModel.isFocused(3));
assertTrue(focusedCell(), focusModel.isFocused(3, null));
assertEquals(new TablePosition(tableView, 3, null), focusModel.getFocusedCell());
assertEquals(tableView.getItems().get(3), focusModel.getFocusedItem());
}
@Test public void focusOnNegativeRowIndex() {
focusModel.focus(-20);
assertEquals(new TablePosition(tableView, -1, null), focusModel.getFocusedCell());
assertFalse(focusedCell(), focusModel.isFocused(-20, null));
}
@Test public void focusOutOfColumnsBounds() {
focusModel.focus(3, null);
assertEquals(new TablePosition(tableView, 3, null), focusModel.getFocusedCell());
assertTrue(focusedCell(), focusModel.isFocused(3, null));
}
@Test public void focusPreviousRow() {
focusModel.focus(3);
assertEquals(new TablePosition(tableView, 3, null), focusModel.getFocusedCell());
assertTrue(focusedCell(), focusModel.isFocused(3));
assertTrue(focusedCell(), focusModel.isFocused(3, null));
focusModel.focusPrevious();
assertEquals(new TablePosition(tableView, 2, null), focusModel.getFocusedCell());
assertTrue(focusedCell(), focusModel.isFocused(2));
assertTrue(focusedCell(), focusModel.isFocused(2, null));
}
@Test public void focusPreviousRowImmediately() {
focusModel.focusPrevious();
assertEquals(new TablePosition(tableView, 0, col0), focusModel.getFocusedCell());
assertTrue(focusedCell(), focusModel.isFocused(0, null));
}
@Test public void focusPreviousRowFromFirstRow() {
focusModel.focus(0);
focusModel.focusPrevious();
assertEquals(new TablePosition(tableView, 0, null), focusModel.getFocusedCell());
assertTrue(focusedCell(), focusModel.isFocused(0));
assertTrue(focusedCell(), focusModel.isFocused(0, null));
}
@Test public void focusNextRow() {
focusModel.focus(3);
focusModel.focusNext();
assertEquals(new TablePosition(tableView, 4, null), focusModel.getFocusedCell());
assertTrue(focusedCell(), focusModel.isFocused(4));
assertTrue(focusedCell(), focusModel.isFocused(4, null));
}
@Test public void focusNextRowImmediately() {
assertEquals(new TablePosition(tableView, 0, col0), focusModel.getFocusedCell());
focusModel.focusNext();
assertEquals(new TablePosition(tableView, 1, col0), focusModel.getFocusedCell());
assertTrue(focusedCell(), focusModel.isFocused(1));
assertTrue(focusedCell(), focusModel.isFocused(1, null));
}
@Test public void focusNextRowFromLastRow() {
int rowCount = tableView.getItems().size() - 1;
focusModel.focus(rowCount);
focusModel.focusNext();
assertEquals(new TablePosition(tableView, rowCount, null), focusModel.getFocusedCell());
assertTrue(focusedCell(), focusModel.isFocused(rowCount));
assertTrue(focusedCell(), focusModel.isFocused(rowCount, null));
}
@Test public void focusAboveCell() {
focusModel.focus(3, col1);
assertEquals(new TablePosition(tableView, 3, col1), focusModel.getFocusedCell());
assertTrue(focusedCell(), focusModel.isFocused(3, col1));
focusModel.focusAboveCell();
assertEquals(new TablePosition(tableView, 2, col1), focusModel.getFocusedCell());
assertTrue(focusedCell(), focusModel.isFocused(2));
assertTrue(focusedCell(), focusModel.isFocused(2, col1));
}
@Test public void focusAboveCellFromFirstRow() {
focusModel.focus(0, col1);
focusModel.focusAboveCell();
assertEquals(new TablePosition(tableView, 0, col1), focusModel.getFocusedCell());
assertTrue(focusedCell(), focusModel.isFocused(0));
assertTrue(focusedCell(), focusModel.isFocused(0, col1));
}
@Test public void focusBelowCell() {
focusModel.focus(3, col1);
focusModel.focusBelowCell();
assertEquals(new TablePosition(tableView, 4, col1), focusModel.getFocusedCell());
assertTrue(focusedCell(), focusModel.isFocused(4));
assertTrue(focusedCell(), focusModel.isFocused(4, col1));
}
@Test public void focusBelowCellFromLastRow() {
int rowCount = tableView.getItems().size() - 1;
focusModel.focus(rowCount, col1);
focusModel.focusBelowCell();
assertEquals(new TablePosition(tableView, rowCount, col1), focusModel.getFocusedCell());
assertTrue(focusedCell(), focusModel.isFocused(rowCount));
assertTrue(focusedCell(), focusModel.isFocused(rowCount, col1));
}
@Test public void focusLeftCell() {
focusModel.focus(3, col1);
focusModel.focusLeftCell();
assertEquals(new TablePosition(tableView, 3, col0), focusModel.getFocusedCell());
assertTrue(focusedCell(), focusModel.isFocused(3));
assertTrue(focusedCell(), focusModel.isFocused(3, col0));
}
@Test public void focusLeftCellFromFirstColumn() {
focusModel.focus(3, col0);
focusModel.focusLeftCell();
assertEquals(new TablePosition(tableView, 3, col0), focusModel.getFocusedCell());
assertTrue(focusedCell(), focusModel.isFocused(3));
assertTrue(focusedCell(), focusModel.isFocused(3, col0));
}
@Test public void focusLeftCellFromNullColumn() {
focusModel.focus(3, null);
focusModel.focusLeftCell();
assertEquals(new TablePosition(tableView, 3, null), focusModel.getFocusedCell());
assertTrue(focusedCell(), focusModel.isFocused(3));
assertTrue(focusedCell(), focusModel.isFocused(3, null));
}
@Test public void focusRightCell() {
focusModel.focus(3, col0);
focusModel.focusRightCell();
assertEquals(new TablePosition(tableView, 3, col1), focusModel.getFocusedCell());
assertTrue(focusedCell(), focusModel.isFocused(3));
assertTrue(focusedCell(), focusModel.isFocused(3, col1));
}
@Test public void focusRightCellFromEndColumn() {
model.clearSelection();
TableColumn<String,?> rightEdge = tableView.getVisibleLeafColumn(tableView.getVisibleLeafColumns().size() - 1);
focusModel.focus(3, rightEdge);
focusModel.focusRightCell();
assertEquals(new TablePosition(tableView, 3, rightEdge), focusModel.getFocusedCell());
assertTrue(focusedCell(), focusModel.isFocused(3));
assertTrue(focusedCell(), focusModel.isFocused(3, rightEdge));
}
@Test public void test_rt33442() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(true);
model.clearSelection();
assertTrue(model.getSelectedCells().isEmpty());
model.selectRange(0, col0, 4, col2);
assertEquals(15, model.getSelectedCells().size());
for (int row = 0; row <= 4; row++) {
for (int column = 0; column <= 2; column++) {
assertTrue(model.isSelected(row, tableView.getVisibleLeafColumn(column)));
}
}
}
@Test public void test_rt33442_changeSelectionModeClearsSelection() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(true);
assertTrue(model.getSelectedCells().isEmpty());
model.selectRange(0, col0, 4, col2);
assertEquals(15, model.getSelectedCells().size());
model.setSelectionMode(SelectionMode.SINGLE);
assertEquals(1, model.getSelectedCells().size());
for (int row = 0; row <= 4; row++) {
for (int column = 0; column <= 2; column++) {
if (row == 4 && column == 2) {
assertTrue(model.isSelected(row, tableView.getVisibleLeafColumn(column)));
} else {
assertFalse(model.isSelected(row, tableView.getVisibleLeafColumn(column)));
}
}
}
}
@Test public void test_jdk_8143594() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(false);
tableView.getItems().set(3, null);
model.select(0);
model.clearAndSelect(3);
model.clearAndSelect(0);
model.clearAndSelect(3);
}
@Test public void test_cellSelection_nullColumn_isSelected_noCellsSelected() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(true);
assertFalse(model.isSelected(0, null));
}
@Test public void test_cellSelection_nullColumn_isSelected_oneCellSelected() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(true);
model.select(0, col0);
assertTrue(model.isSelected(0, col0));
assertFalse(model.isSelected(0, null));
}
@Test public void test_cellSelection_nullColumn_isSelected_allCellsSelected() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(true);
model.select(0, null);
assertTrue(model.isSelected(0, null));
}
@Test public void test_cellSelection_nullColumn_selectAllCellsInRow() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(true);
model.select(0, null);
assertTrue(model.isSelected(0, col0));
assertTrue(model.isSelected(0, col1));
assertTrue(model.isSelected(0, col2));
assertEquals(3, model.getSelectedCells().size());
}
@Test public void test_cellSelection_nullColumn_clearAndSelectAllCellsInRow() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(true);
model.select(1, col1);
model.clearAndSelect(0, null);
assertTrue(model.isSelected(0, col0));
assertTrue(model.isSelected(0, col1));
assertTrue(model.isSelected(0, col2));
assertEquals(3, model.getSelectedCells().size());
}
@Test public void test_cellSelection_nullColumn_clearSelection_noCellsSelected() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(true);
model.clearSelection(0, null);
assertFalse(model.isSelected(0, col0));
assertFalse(model.isSelected(0, col1));
assertFalse(model.isSelected(0, col2));
assertEquals(0, model.getSelectedCells().size());
}
@Test public void test_cellSelection_nullColumn_clearSelection_allCellsSelected() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(true);
model.select(0, col1);
assertTrue(model.isSelected(0, col1));
model.clearSelection(0, null);
assertFalse(model.isSelected(0, col0));
assertFalse(model.isSelected(0, col1));
assertFalse(model.isSelected(0, col2));
assertEquals(0, model.getSelectedCells().size());
}
@Test public void test_cellSelection_nullColumn_clearSelection_oneCellSelected() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.setCellSelectionEnabled(true);
model.select(0, null);
model.clearSelection(0, null);
assertFalse(model.isSelected(0, col0));
assertFalse(model.isSelected(0, col1));
assertFalse(model.isSelected(0, col2));
assertEquals(0, model.getSelectedCells().size());
}
@Test public void test_jdk_8144501() {
model.setSelectionMode(SelectionMode.MULTIPLE);
model.select(2);
model.select(3);
ListChangeListener<String> listener = change -> {
while (change.next()) {
assertNotNull(change.getList());
assertEquals(1, change.getList().size());
assertNotNull(change.getList().get(0));
}
};
model.getSelectedItems().addListener(listener);
model.clearSelection(2);
model.getSelectedItems().removeListener(listener);
}
}
