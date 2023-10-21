package test.javafx.scene.control;
import java.util.Arrays;
import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceBoxShim;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxShim;
import javafx.scene.control.TableView.TableViewFocusModel;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.Control;
import javafx.scene.control.ControlShim;
import javafx.scene.control.FocusModel;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ListViewShim;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.MultipleSelectionModelShim;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SelectionModelShim;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.control.TableViewShim;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableViewShim;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeViewShim;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
@RunWith(Parameterized.class)
public class SelectionModelImplTest {
private SelectionModel model;
private FocusModel focusModel;
private Class<? extends SelectionModel> modelClass;
private Control currentControl;
private ListView<String> listView;
private static ObservableList<String> data = FXCollections.<String>observableArrayList();
private static final String ROW_1_VALUE = "Row 1";
private static final String ROW_2_VALUE = "Row 2";
private static final String ROW_3_VALUE = "Row 3";
private static final String ROW_5_VALUE = "Row 5";
private static final String ROW_20_VALUE = "Row 20";
private TreeView treeView;
private TreeItem<String> root;
private TreeItem<String> ROW_2_TREE_VALUE;
private TreeItem<String> ROW_3_TREE_VALUE;
private TreeItem<String> ROW_5_TREE_VALUE;
private TableView tableView;
private TreeTableView treeTableView;
private ChoiceBox choiceBox;
private ComboBox comboBox;
@Parameters public static Collection implementations() {
return Arrays.asList(new Object[][] {
{ ListViewShim.get_ListViewBitSetSelectionModel_class() },
{ TreeViewShim.get_TreeViewBitSetSelectionModel_class() },
{ TableViewShim.get_TableViewArrayListSelectionModel_class() },
{ TreeTableViewShim.get_TreeTableViewArrayListSelectionModel_class() }
});
}
public SelectionModelImplTest(Class<? extends SelectionModel> modelClass) {
this.modelClass = modelClass;
}
@AfterClass public static void tearDownClass() throws Exception { }
@Before public void setUp() throws Exception {
data.setAll(ROW_1_VALUE, ROW_2_VALUE, ROW_3_VALUE, "Row 4", ROW_5_VALUE, "Row 6",
"Row 7", "Row 8", "Row 9", "Row 10", "Row 11", "Row 12", "Row 13",
"Row 14", "Row 15", "Row 16", "Row 17", "Row 18", "Row 19", ROW_20_VALUE);
listView = new ListView<>(data);
root = new TreeItem<>(ROW_1_VALUE);
root.setExpanded(true);
for (int i = 1; i < data.size(); i++) {
root.getChildren().add(new TreeItem<>(data.get(i)));
}
ROW_2_TREE_VALUE = root.getChildren().get(0);
ROW_3_TREE_VALUE = root.getChildren().get(1);
ROW_5_TREE_VALUE = root.getChildren().get(3);
treeView = new TreeView(root);
tableView = new TableView();
tableView.setItems(data);
treeTableView = new TreeTableView(root);
choiceBox = new ChoiceBox();
choiceBox.setItems(data);
comboBox = new ComboBox();
comboBox.setItems(data);
try {
if (modelClass.equals(ListViewShim.get_ListViewBitSetSelectionModel_class())) {
model = SelectionModelShim.newInstance_from_class(modelClass, ListView.class, listView);
listView.setSelectionModel((MultipleSelectionModel<String>)model);
focusModel = ListViewShim.getListViewFocusModel(listView);
listView.setFocusModel(focusModel);
currentControl = listView;
} else if (modelClass.equals(TreeViewShim.get_TreeViewBitSetSelectionModel_class())) {
model = SelectionModelShim.newInstance_from_class(modelClass, TreeView.class, treeView);
treeView.setSelectionModel((MultipleSelectionModel<String>)model);
focusModel = treeView.getFocusModel();
focusModel = TreeViewShim.get_TreeViewFocusModel(treeView);
treeView.setFocusModel(focusModel);
currentControl = treeView;
} else if (TableViewSelectionModel.class.isAssignableFrom(modelClass)) {
model = SelectionModelShim.newInstance_from_class(modelClass, TableView.class, tableView);
tableView.setSelectionModel((TableViewSelectionModel) model);
focusModel = new TableViewFocusModel(tableView);
tableView.setFocusModel((TableViewFocusModel) focusModel);
currentControl = tableView;
} else if (TreeTableView.TreeTableViewSelectionModel.class.isAssignableFrom(modelClass)) {
model = SelectionModelShim.newInstance_from_class(modelClass, TreeTableView.class, treeTableView);
treeTableView.setSelectionModel((TreeTableView.TreeTableViewSelectionModel) model);
focusModel = new TreeTableView.TreeTableViewFocusModel(treeTableView);
treeTableView.setFocusModel((TreeTableView.TreeTableViewFocusModel) focusModel);
currentControl = treeTableView;
} else if (ChoiceBoxShim.ChoiceBoxSelectionModel_isAssignableFrom(modelClass)) {
model = SelectionModelShim.newInstance_from_class(modelClass, ChoiceBox.class, choiceBox);
choiceBox.setSelectionModel((SingleSelectionModel) model);
focusModel = null;
currentControl = choiceBox;
} else if (ComboBoxShim.ComboBoxSelectionModel_isAssignableFrom(modelClass)) {
model = SelectionModelShim.newInstance_from_class(modelClass, ComboBox.class, comboBox);
comboBox.setSelectionModel((SingleSelectionModel) model);
focusModel = null;
currentControl = comboBox;
}
if (model instanceof MultipleSelectionModel) {
((MultipleSelectionModel)model).setSelectionMode(SelectionMode.SINGLE);
}
} catch (Exception ex) {
ex.printStackTrace();
throw ex;
}
}
@After public void tearDown() {
model = null;
}
private boolean isTree() {
return (TreeViewShim.is_TreeViewBitSetSelectionModel(model)) ||
(TreeTableViewShim.instanceof_TreeTableViewArrayListSelectionModel(model));
}
private Object getValue(Object item) {
if (item instanceof TreeItem) {
return ((TreeItem)item).getValue();
}
return item;
}
@Test public void testDefaultState() {
assertEquals(-1, model.getSelectedIndex());
assertNull(getValue(model.getSelectedItem()));
if (focusModel != null) {
assertEquals(0, focusModel.getFocusedIndex());
assertEquals(ROW_1_VALUE, getValue(focusModel.getFocusedItem()));
}
}
@Test public void selectInvalidIndex() {
model.select(100);
testDefaultState();
}
@Test public void selectRowAfterInvalidIndex() {
model.select(100);
model.selectNext();
assertEquals(1, model.getSelectedIndex());
if (focusModel != null) assertEquals(1, focusModel.getFocusedIndex());
model.selectNext();
assertEquals(2, model.getSelectedIndex());
if (focusModel != null) assertEquals(2, focusModel.getFocusedIndex());
}
@Test public void selectInvalidItem() {
assertEquals(-1, model.getSelectedIndex());
Object obj = new TreeItem("DUMMY");
model.select(obj);
assertSame(obj, model.getSelectedItem());
assertEquals(-1, model.getSelectedIndex());
}
@Test public void selectValidIndex() {
int index = 4;
model.select(index);
assertEquals(index, model.getSelectedIndex());
assertNotNull(model.getSelectedItem());
assertEquals(ROW_5_VALUE, getValue(model.getSelectedItem()));
if (focusModel != null) {
assertEquals(index, focusModel.getFocusedIndex());
assertNotNull(focusModel.getFocusedItem());
assertEquals(ROW_5_VALUE, getValue(focusModel.getFocusedItem()));
}
}
@Test public void clearPartialSelectionWithSingleSelection() {
assertFalse(model.isSelected(5));
model.select(5);
assertTrue(model.isSelected(5));
model.clearSelection(5);
assertFalse(model.isSelected(5));
}
@Test public void ensureIsEmptyIsAccurate() {
assertTrue(model.isEmpty());
model.select(5);
assertFalse(model.isEmpty());
model.clearSelection();
assertTrue(model.isEmpty());
}
@Test public void testSingleSelectionMode() {
model.clearSelection();
assertTrue(model.isEmpty());
model.select(5);
assertTrue("Selected: " + model.getSelectedIndex() + ", expected: 5", model.isSelected(5));
model.select(10);
assertTrue(model.isSelected(10));
assertFalse(model.isSelected(5));
}
@Test public void testSelectNullObject() {
model.select(null);
}
@Test public void testFocusOnNegativeIndex() {
if (focusModel == null) return;
assertEquals(0, focusModel.getFocusedIndex());
focusModel.focus(-1);
assertEquals(-1, focusModel.getFocusedIndex());
assertFalse(focusModel.isFocused(-1));
}
@Test public void testFocusOnOutOfBoundsIndex() {
if (focusModel == null) return;
assertEquals(0, focusModel.getFocusedIndex());
focusModel.focus(Integer.MAX_VALUE);
assertEquals(-1, focusModel.getFocusedIndex());
assertNull(focusModel.getFocusedItem());
assertFalse(focusModel.isFocused(Integer.MAX_VALUE));
}
@Test public void testFocusOnValidIndex() {
if (focusModel == null) return;
assertEquals(0, focusModel.getFocusedIndex());
focusModel.focus(1);
assertEquals(1, focusModel.getFocusedIndex());
assertTrue(focusModel.isFocused(1));
if (isTree()) {
assertEquals(root.getChildren().get(0), focusModel.getFocusedItem());
} else {
assertEquals(data.get(1), focusModel.getFocusedItem());
}
}
@Ignore("Not yet implemented in TreeView")
@Test public void testSelectionChangesWhenItemIsInsertedAtStartOfModel() {
model.select(3);
assertTrue(model.isSelected(3));
data.add(0, "Inserted String");
assertFalse(model.isSelected(3));
assertTrue(model.isSelected(4));
}
@Test public void test_rt_29821() {
if (currentControl instanceof ChoiceBox) {
model.clearSelection();
model.select(3);
assertNotNull(choiceBox.getValue());
model.select(null);
assertFalse(model.isSelected(3));
assertNull(choiceBox.getValue());
} else {
IndexedCell cell_3 = VirtualFlowTestUtils.getCell(currentControl, 3);
assertNotNull(cell_3);
assertFalse(cell_3.isSelected());
model.clearSelection();
model.select(3);
assertTrue(cell_3.isSelected());
model.select(null);
assertFalse(model.isSelected(3));
if (currentControl instanceof ComboBox) {
ControlShim.layoutChildren(currentControl);
}
assertFalse(cell_3.isSelected());
}
}
@Test public void test_rt_30356_selectRowAtIndex0() throws Exception {
setUp();
if (isTree()) {
if (currentControl instanceof TreeView) {
((TreeView)currentControl).setShowRoot(false);
} else if (currentControl instanceof TreeTableView) {
((TreeTableView)currentControl).setShowRoot(false);
}
model.select(0);
assertEquals(ROW_2_TREE_VALUE, model.getSelectedItem());
root.getChildren().remove(0);
assertEquals(ROW_3_TREE_VALUE, model.getSelectedItem());
} else if (currentControl instanceof ChoiceBox || currentControl instanceof ComboBox) {
} else {
model.select(0);
assertEquals("model is " + model, ROW_1_VALUE, model.getSelectedItem());
data.remove(0);
assertEquals(ROW_2_VALUE, model.getSelectedItem());
}
if (! (currentControl instanceof ChoiceBox || currentControl instanceof ComboBox)) {
IndexedCell cell = VirtualFlowTestUtils.getCell(currentControl, 0);
assertTrue(cell.isSelected());
}
}
@Test public void test_rt_30356_selectRowAtIndex1() throws Exception {
setUp();
if (isTree()) {
if (currentControl instanceof TreeView) {
((TreeView)currentControl).setShowRoot(false);
} else if (currentControl instanceof TreeTableView) {
((TreeTableView)currentControl).setShowRoot(false);
}
model.select(1);
assertEquals(ROW_3_TREE_VALUE, model.getSelectedItem());
assertTrue(root.isExpanded());
assertEquals(19, root.getChildren().size());
TreeItem<String> removed = root.getChildren().remove(1);
assertEquals("Row 3", getValue(removed));
assertEquals(ROW_2_TREE_VALUE, model.getSelectedItem());
} else if (currentControl instanceof ChoiceBox || currentControl instanceof ComboBox) {
} else {
model.select(1);
assertEquals(ROW_2_VALUE, model.getSelectedItem());
assertEquals(1, model.getSelectedIndex());
data.remove(1);
assertEquals(ROW_1_VALUE, model.getSelectedItem());
assertEquals(0, model.getSelectedIndex());
}
}
private int rt32618_count = 0;
@Test public void test_rt32618_singleSelection() {
model.selectedItemProperty().addListener((ov, t, t1) -> {
rt32618_count++;
});
assertEquals(0, rt32618_count);
model.select(1);
assertEquals(1, rt32618_count);
assertEquals(ROW_2_VALUE, getValue(model.getSelectedItem()));
model.clearAndSelect(2);
assertEquals(2, rt32618_count);
assertEquals(ROW_3_VALUE, getValue(model.getSelectedItem()));
}
}
