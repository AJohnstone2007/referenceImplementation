package test.javafx.scene.control;
import com.sun.javafx.scene.control.TableColumnBaseHelper;
import static test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils.assertStyleClassContains;
import static javafx.scene.control.TreeTableColumn.SortType.ASCENDING;
import static javafx.scene.control.TreeTableColumn.SortType.DESCENDING;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import com.sun.javafx.scene.control.behavior.TreeTableCellBehavior;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import test.javafx.collections.MockListObserver;
import test.com.sun.javafx.scene.control.infrastructure.KeyEventFirer;
import test.com.sun.javafx.scene.control.infrastructure.KeyModifier;
import test.com.sun.javafx.scene.control.infrastructure.MouseEventFirer;
import javafx.scene.control.skin.TreeTableCellSkin;
import test.com.sun.javafx.scene.control.test.Data;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TreeTableView.TreeTableViewFocusModel;
import javafx.scene.control.cell.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.sun.javafx.scene.control.TableColumnComparatorBase.TreeTableColumnComparator;
import test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;
import test.com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils;
import com.sun.javafx.scene.control.VirtualScrollBar;
import test.com.sun.javafx.scene.control.test.Person;
import test.com.sun.javafx.scene.control.test.RT_22463_Person;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.control.Button;
import javafx.scene.control.Cell;
import javafx.scene.control.FocusModel;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.MultipleSelectionModelBaseShim;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumnBaseShim;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableCellShim;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableRowShim;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableViewShim;
import javafx.scene.control.TreeView;
public class TreeTableViewTest {
private TreeTableView<String> treeTableView;
private TreeTableView.TreeTableViewSelectionModel sm;
private TreeTableViewFocusModel<String> fm;
private TreeItem<String> root;
private TreeItem<String> child1;
private TreeItem<String> child2;
private TreeItem<String> child3;
private TreeItem<String> myCompanyRootNode;
private TreeItem<String> salesDepartment;
private TreeItem<String> ethanWilliams;
private TreeItem<String> emmaJones;
private TreeItem<String> michaelBrown;
private TreeItem<String> annaBlack;
private TreeItem<String> rodgerYork;
private TreeItem<String> susanCollins;
private TreeItem<String> itSupport;
private TreeItem<String> mikeGraham;
private TreeItem<String> judyMayer;
private TreeItem<String> gregorySmith;
@Before public void setup() {
treeTableView = new TreeTableView<String>();
sm = treeTableView.getSelectionModel();
fm = treeTableView.getFocusModel();
myCompanyRootNode = new TreeItem<String>("MyCompany Human Resources");
salesDepartment = new TreeItem<String>("Sales Department");
ethanWilliams = new TreeItem<String>("Ethan Williams");
emmaJones = new TreeItem<String>("Emma Jones");
michaelBrown = new TreeItem<String>("Michael Brown");
annaBlack = new TreeItem<String>("Anna Black");
rodgerYork = new TreeItem<String>("Rodger York");
susanCollins = new TreeItem<String>("Susan Collins");
itSupport = new TreeItem<String>("IT Support");
mikeGraham = new TreeItem<String>("Mike Graham");
judyMayer = new TreeItem<String>("Judy Mayer");
gregorySmith = new TreeItem<String>("Gregory Smith");
myCompanyRootNode.getChildren().setAll(
salesDepartment,
itSupport
);
salesDepartment.getChildren().setAll(
ethanWilliams,
emmaJones,
michaelBrown,
annaBlack,
rodgerYork,
susanCollins
);
itSupport.getChildren().setAll(
mikeGraham,
judyMayer,
gregorySmith
);
}
private void installChildren() {
root = new TreeItem<String>("Root");
child1 = new TreeItem<String>("Child 1");
child2 = new TreeItem<String>("Child 2");
child3 = new TreeItem<String>("Child 3");
root.setExpanded(true);
root.getChildren().setAll(child1, child2, child3);
treeTableView.setRoot(root);
}
private String debug() {
StringBuilder sb = new StringBuilder("Selected Cells: [");
List<TreeTablePosition<?,?>> cells = sm.getSelectedCells();
for (TreeTablePosition cell : cells) {
sb.append("(");
sb.append(cell.getRow());
sb.append(",");
sb.append(cell.getColumn());
sb.append("), ");
}
sb.append("] \nFocus: " + fm.getFocusedIndex());
return sb.toString();
}
@Test public void ensureCorrectInitialState() {
installChildren();
assertEquals(0, treeTableView.getRow(root));
assertEquals(1, treeTableView.getRow(child1));
assertEquals(2, treeTableView.getRow(child2));
assertEquals(3, treeTableView.getRow(child3));
}
@Test public void noArgConstructorSetsNonNullSelectionModel() {
assertNotNull(sm);
}
@Test public void noArgConstructor_selectedItemIsNull() {
assertNull(sm.getSelectedItem());
}
@Test public void noArgConstructor_selectedIndexIsNegativeOne() {
assertEquals(-1, sm.getSelectedIndex());
}
@Test public void noArgConstructorSetsNonNullSortPolicy() {
assertNotNull(treeTableView.getSortPolicy());
}
@Test public void noArgConstructorSetsNullComparator() {
assertNull(treeTableView.getComparator());
}
@Test public void noArgConstructorSetsNullOnSort() {
assertNull(treeTableView.getOnSort());
}
@Test public void noArgConstructorSetsDefaultColumnResizePolicyPseudoclass() {
TreeTableView<?> view = new TreeTableView<>();
assertTrue(view.getPseudoClassStates().stream().anyMatch(
c -> c.getPseudoClassName().equals(TreeTableView.UNCONSTRAINED_RESIZE_POLICY.toString())));
}
@Test public void singleArgConstructorSetsDefaultColumnResizePolicyPseudoclass() {
TreeTableView<?> view = new TreeTableView<>(null);
assertTrue(view.getPseudoClassStates().stream().anyMatch(
c -> c.getPseudoClassName().equals(TreeTableView.UNCONSTRAINED_RESIZE_POLICY.toString())));
}
@Test public void testColumns() {
TreeTableColumn col1 = new TreeTableColumn();
assertNotNull(treeTableView.getColumns());
assertEquals(0, treeTableView.getColumns().size());
treeTableView.getColumns().add(col1);
assertEquals(1, treeTableView.getColumns().size());
treeTableView.getColumns().remove(col1);
assertEquals(0, treeTableView.getColumns().size());
}
@Test public void testVisibleLeafColumns() {
TreeTableColumn col1 = new TreeTableColumn();
assertNotNull(treeTableView.getColumns());
assertEquals(0, treeTableView.getColumns().size());
treeTableView.getColumns().add(col1);
assertEquals(1, treeTableView.getVisibleLeafColumns().size());
treeTableView.getColumns().remove(col1);
assertEquals(0, treeTableView.getVisibleLeafColumns().size());
}
@Test public void testSortOrderCleanup() {
TreeTableView treeTableView = new TreeTableView();
TreeTableColumn<String,String> first = new TreeTableColumn<String,String>("first");
first.setCellValueFactory(new PropertyValueFactory("firstName"));
TreeTableColumn<String,String> second = new TreeTableColumn<String,String>("second");
second.setCellValueFactory(new PropertyValueFactory("lastName"));
treeTableView.getColumns().addAll(first, second);
treeTableView.getSortOrder().setAll(first, second);
treeTableView.getColumns().remove(first);
assertFalse(treeTableView.getSortOrder().contains(first));
}
private TreeItem<String> apple, orange, banana;
private static final Callback<TreeTableView<String>, Boolean> NO_SORT_FAILED_SORT_POLICY =
treeTableView1 -> false;
private static final Callback<TreeTableView<String>, Boolean> SORT_SUCCESS_ASCENDING_SORT_POLICY =
treeTableView1 -> {
if (treeTableView1.getSortOrder().isEmpty()) return true;
FXCollections.sort(treeTableView1.getRoot().getChildren(), new Comparator<TreeItem<String>>() {
@Override public int compare(TreeItem<String> o1, TreeItem<String> o2) {
return o1.getValue().compareTo(o2.getValue());
}
});
return true;
};
private TreeTableColumn<String, String> initSortTestStructure() {
TreeTableColumn<String, String> col = new TreeTableColumn<String, String>("column");
col.setSortType(ASCENDING);
col.setCellValueFactory(param -> new ReadOnlyObjectWrapper<String>(param.getValue().getValue()));
treeTableView.getColumns().add(col);
TreeItem<String> newRoot = new TreeItem<String>("root");
newRoot.setExpanded(true);
newRoot.getChildren().addAll(
apple = new TreeItem("Apple"),
orange = new TreeItem("Orange"),
banana = new TreeItem("Banana"));
treeTableView.setRoot(newRoot);
return col;
}
private int countSelectedIndexChangeEvent;
private int countSelectedItemChangeEvent;
private int countSelectedIndicesChangeEvent;
private int countSelectedItemsChangeEvent;
private int expectedCountSelectedIndexChangeEvent;
private int expectedCountSelectedItemChangeEvent;
private int expectedCountSelectedIndicesChangeEvent;
private int expectedCountSelectedItemsChangeEvent;
private TreeItem<String> selectedItemBefore;
private List<TreeItem<String>> selectedItemsBefore;
private List<Integer> selectedIndicesBefore;
private List<TreeTablePosition<String,?>> selectedCellsBefore;
@Test public void testSelectionUpdatesCorrectlyAfterSort() {
TreeTableColumn<String, String> col = setupForPermutationTest();
treeTableView.getSortOrder().add(col);
verifySelectionAfterPermutation();
}
@Test public void testSelectionUpdatesCorrectlyAfterRootReverseAndSetAll() {
setupForPermutationTest();
TreeItem<String> parentTreeItem = treeTableView.getRoot();
List<TreeItem<String>> childrenReversed = getReverseChildrenOrder(parentTreeItem);
parentTreeItem.getChildren().setAll(childrenReversed);
verifySelectionAfterPermutation();
}
@Ignore("JDK-8193442")
@Test public void testSelectionUpdatesCorrectlyAfterRemovingSelectedItem() {
setupForPermutationTest();
TreeItem<String> parentOfSelectedTreeItem = ((TreeItem<String>)sm.getSelectedItem()).getParent();
expectedCountSelectedItemChangeEvent = 1;
selectedItemBefore = treeTableView.getTreeItem(
(int)sm.getSelectedIndices().get(sm.getSelectedIndices().size() - 1));
parentOfSelectedTreeItem.getChildren().remove(sm.getSelectedItem());
verifySelectionAfterPermutation();
}
@Ignore("JDK-8248389")
@Test public void testSelectionUpdatesCorrectlyAfterAddingAnItemBeforeSelectedItem() {
setupForPermutationTest();
TreeItem<String> parentOfSelectedTreeItem = ((TreeItem<String>)sm.getSelectedItem()).getParent();
int indexOfSelectedItem = parentOfSelectedTreeItem.getChildren().indexOf(sm.getSelectedItem());
if (indexOfSelectedItem > 0) {
indexOfSelectedItem--;
}
parentOfSelectedTreeItem.getChildren().add(indexOfSelectedItem, new TreeItem("AddingOne"));
verifySelectionAfterPermutation();
}
@Test public void testSelectionUpdatesCorrectlyAfterChildReverseAndSetAll() {
setupForPermutationTest();
TreeItem<String> parentTreeItem = ((TreeItem<String>)sm.getSelectedItem()).getParent();
List<TreeItem<String>> childrenReversed = getReverseChildrenOrder(parentTreeItem);
parentTreeItem.getChildren().setAll(childrenReversed);
verifySelectionAfterPermutation();
}
@Ignore("JDK-8193442")
@Test public void testSelectionUpdatesCorrectlyAfterChildReverseRemoveOneAndSetAll() {
setupForPermutationTest();
TreeItem<String> parentTreeItem = ((TreeItem<String>)sm.getSelectedItem()).getParent();
List<TreeItem<String>> childrenReversed = getReverseChildrenOrder(parentTreeItem);
childrenReversed.remove(0);
parentTreeItem.getChildren().setAll(childrenReversed);
verifySelectionAfterPermutation();
}
@Ignore("JDK-8193442")
@Test public void testSelectionUpdatesCorrectlyAfterChildRemoveOneAndSetAll() {
TreeItem<String> parentTreeItem = ((TreeItem<String>)sm.getSelectedItem()).getParent();
List<TreeItem<String>> children = new ArrayList<>(parentTreeItem.getChildren());
children.remove(0);
parentTreeItem.getChildren().setAll(children);
verifySelectionAfterPermutation();
}
@Ignore("JDK-8193442")
@Test public void testSelectionUpdatesCorrectlyAfterChildRemoveOneAndSetAllAndSort() {
TreeTableColumn<String, String> col = setupForPermutationTest();
TreeItem<String> parentTreeItem = ((TreeItem<String>)sm.getSelectedItem()).getParent();
List<TreeItem<String>> children = new ArrayList<>(parentTreeItem.getChildren());
children.remove(0);
parentTreeItem.getChildren().setAll(children);
treeTableView.getSortOrder().add(col);
verifySelectionAfterPermutation();
}
private List<TreeItem<String>> getReverseChildrenOrder(TreeItem<String> treeItem) {
List<TreeItem<String>> childrenReversed = new ArrayList<>();
int childrenSize = treeItem.getChildren().size();
for (int i = 0; i < childrenSize; i++) {
childrenReversed.add(treeItem.getChildren().get(childrenSize - 1 - i));
}
return childrenReversed;
}
private TreeTableColumn<String, String> setupForPermutationTest() {
countSelectedIndexChangeEvent = 0;
countSelectedItemChangeEvent = 0;
countSelectedIndicesChangeEvent = 0;
countSelectedItemsChangeEvent = 0;
expectedCountSelectedIndexChangeEvent = 1;
expectedCountSelectedItemChangeEvent = 0;
expectedCountSelectedIndicesChangeEvent = 1;
expectedCountSelectedItemsChangeEvent = 1;
TreeTableColumn<String, String> col = new TreeTableColumn<String, String>("column");
col.setSortType(DESCENDING);
col.setCellValueFactory(param -> new ReadOnlyObjectWrapper<String>(param.getValue().getValue()));
treeTableView.getColumns().add(col);
TreeItem<String> treeRoot = new TreeItem<String>("root");
treeRoot.setExpanded(true);
treeTableView.setRoot(treeRoot);
final int FIRST_LEVEL_COUNT = 8;
for (int i = 0; i < FIRST_LEVEL_COUNT; i++) {
TreeItem<String> ti = new TreeItem<>( "" + i);
ti.setExpanded(true);
treeRoot.getChildren().add(ti);
for (int j = 0; j < FIRST_LEVEL_COUNT - 1; j++) {
TreeItem<String> tj = new TreeItem<>("" + i + j);
tj.setExpanded(true);
ti.getChildren().add(tj);
for (int k = 0; k < FIRST_LEVEL_COUNT - 2; k++) {
TreeItem<String> tk = new TreeItem<>("" + i + j + k);
tk.setExpanded(true);
tj.getChildren().add(tk);
for (int l = 0; l < FIRST_LEVEL_COUNT - 3; l++) {
TreeItem<String> tl = new TreeItem<>("" + i + j + k + l);
tl.setExpanded(true);
tk.getChildren().add(tl);
for (int m = 0; m < FIRST_LEVEL_COUNT - 4; m++) {
TreeItem<String> tm = new TreeItem<>("" + i + j + k + l + m);
tl.getChildren().add(tm);
}
}
}
}
}
sm.setSelectionMode(SelectionMode.MULTIPLE);
int indices[] = new int[] {1, 400, 800, 1200, 1600, 2000, 2400, 2800, 3200, 3600, 4000, 4400, 4800, 5200, 5600, 6000, 6400};
sm.selectIndices(1, 400, 800, 1200, 1600, 2000, 2400, 2800, 3200, 3600, 4000, 4400, 4800, 5200, 5600, 6000, 6400);
assertEquals(indices.length, sm.getSelectedIndices().size());
assertEquals(indices.length, sm.getSelectedItems().size());
assertEquals(indices.length, sm.getSelectedCells().size());
assertEquals(indices[indices.length - 1], sm.getSelectedIndex());
assertEquals(treeTableView.getTreeItem(indices[indices.length - 1]), sm.getSelectedItem());
selectedItemBefore = (TreeItem<String>) sm.getSelectedItem();
selectedItemsBefore = new ArrayList<>(sm.getSelectedItems());
selectedIndicesBefore = new ArrayList<>(sm.getSelectedIndices());
selectedCellsBefore = new ArrayList<>(sm.getSelectedCells());
sm.selectedIndexProperty().addListener(ov -> {
countSelectedIndexChangeEvent++;
assertEquals(selectedItemBefore, treeTableView.getTreeItem(sm.getSelectedIndex()));
});
sm.selectedItemProperty().addListener(l -> {
countSelectedItemChangeEvent++;
});
sm.getSelectedIndices().addListener((ListChangeListener) c -> {
countSelectedIndicesChangeEvent++;
c.next();
if (c.wasRemoved()) {
assertTrue(selectedIndicesBefore.equals(c.getRemoved()));
}
verifySelectedIndices(c.getAddedSubList());
verifySelectedIndices(c.getList());
});
sm.getSelectedItems().addListener((ListChangeListener) c -> {
countSelectedItemsChangeEvent++;
c.next();
if (c.wasRemoved()) {
verifySelectedItems(c.getRemoved());
}
verifySelectedItems(c.getAddedSubList());
verifySelectedItems(c.getList());
});
return col;
}
private void verifySelectedCells(List<TreeTablePosition<String, ?>> selectedCells) {
assertEquals(selectedCellsBefore.size(), selectedCells.size());
for (TreeTablePosition beforePos : selectedCellsBefore) {
boolean isCellStillSelected = false;
for (TreeTablePosition afterPos : selectedCells) {
if ((beforePos.getTreeItem() == afterPos.getTreeItem()) &&
(beforePos.getTableColumn() == afterPos.getTableColumn()) &&
(beforePos.getColumn() == afterPos.getColumn())) {
isCellStillSelected = true;
}
}
assertTrue("The item (" + beforePos.getRow() + ", " + beforePos.getColumn() +
") lost selection during permutation", isCellStillSelected);
}
}
private void verifySelectedItems(List<TreeItem<String>> selectedItems) {
assertEquals(selectedItemsBefore.size(), selectedItems.size());
for (TreeItem<String> item : selectedItemsBefore) {
assertTrue("The item (" + item + ") lost selection during permutation",
selectedItems.contains(item));
}
}
private void verifySelectedIndices(List<Integer> currentIndices) {
assertEquals(selectedIndicesBefore.size(), currentIndices.size());
for (Integer row : currentIndices) {
assertTrue(selectedItemsBefore.contains(treeTableView.getTreeItem(row)));
}
}
private void verifySelectionAfterPermutation() {
assertEquals(expectedCountSelectedIndexChangeEvent, countSelectedIndexChangeEvent);
assertEquals(expectedCountSelectedItemChangeEvent, countSelectedItemChangeEvent);
assertEquals(expectedCountSelectedIndicesChangeEvent, countSelectedIndicesChangeEvent);
assertEquals(expectedCountSelectedItemsChangeEvent, countSelectedItemsChangeEvent);
assertEquals("Selected Item should remain same", selectedItemBefore, sm.getSelectedItem());
assertEquals("Selected index should be updated", treeTableView.getRow(selectedItemBefore), sm.getSelectedIndex());
verifySelectedCells(sm.getSelectedCells());
verifySelectedItems(sm.getSelectedItems());
verifySelectedIndices(sm.getSelectedIndices());
}
@Ignore("This test is only valid if sort event consumption should revert changes")
@Test public void testSortEventCanBeConsumedToStopSortOccurring_changeSortOrderList() {
TreeTableColumn<String, String> col = initSortTestStructure();
treeTableView.setOnSort(event -> {
event.consume();
});
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, orange, banana);
treeTableView.getSortOrder().add(col);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, orange, banana);
assertTrue(treeTableView.getSortOrder().isEmpty());
}
@Test public void testSortEventCanBeNotConsumedToAllowSortToOccur_changeSortOrderList() {
TreeTableColumn<String, String> col = initSortTestStructure();
treeTableView.setOnSort(event -> {
});
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, orange, banana);
treeTableView.getSortOrder().add(col);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, banana, orange);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getSortOrder(), col);
}
@Ignore("This test is only valid if sort event consumption should revert changes")
@Test public void testSortEventCanBeConsumedToStopSortOccurring_changeColumnSortType_AscendingToDescending() {
TreeTableColumn<String, String> col = initSortTestStructure();
assertEquals(ASCENDING, col.getSortType());
treeTableView.getSortOrder().add(col);
treeTableView.setOnSort(event -> {
event.consume();
});
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, banana, orange);
col.setSortType(DESCENDING);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, banana, orange);
assertEquals(ASCENDING, col.getSortType());
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getSortOrder(), col);
}
@Test public void testSortEventCanBeNotConsumedToAllowSortToOccur_changeColumnSortType_AscendingToDescending() {
TreeTableColumn<String, String> col = initSortTestStructure();
assertEquals(ASCENDING, col.getSortType());
treeTableView.getSortOrder().add(col);
treeTableView.setOnSort(event -> {
});
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, banana, orange);
col.setSortType(DESCENDING);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), orange, banana, apple);
assertEquals(DESCENDING, col.getSortType());
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getSortOrder(), col);
}
@Ignore("This test is only valid if sort event consumption should revert changes")
@Test public void testSortEventCanBeConsumedToStopSortOccurring_changeColumnSortType_DescendingToNull() {
TreeTableColumn<String, String> col = initSortTestStructure();
col.setSortType(DESCENDING);
assertEquals(DESCENDING, col.getSortType());
treeTableView.getSortOrder().add(col);
treeTableView.setOnSort(event -> {
event.consume();
});
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), orange, banana, apple);
col.setSortType(null);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), orange, banana, apple);
assertEquals(DESCENDING, col.getSortType());
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getSortOrder(), col);
}
@Test public void testSortEventCanBeNotConsumedToAllowSortToOccur_changeColumnSortType_DescendingToNull() {
TreeTableColumn<String, String> col = initSortTestStructure();
col.setSortType(DESCENDING);
assertEquals(DESCENDING, col.getSortType());
treeTableView.getSortOrder().add(col);
treeTableView.setOnSort(event -> {
});
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), orange, banana, apple);
col.setSortType(null);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), orange, banana, apple);
assertNull(col.getSortType());
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getSortOrder(), col);
}
@Ignore("This test is only valid if sort event consumption should revert changes")
@Test public void testSortEventCanBeConsumedToStopSortOccurring_changeColumnSortType_NullToAscending() {
TreeTableColumn<String, String> col = initSortTestStructure();
col.setSortType(null);
assertNull(col.getSortType());
treeTableView.getSortOrder().add(col);
treeTableView.setOnSort(event -> {
event.consume();
});
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, orange, banana);
col.setSortType(ASCENDING);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, orange, banana);
assertNull(col.getSortType());
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getSortOrder(), col);
}
@Test public void testSortEventCanBeNotConsumedToAllowSortToOccur_changeColumnSortType_NullToAscending() {
TreeTableColumn<String, String> col = initSortTestStructure();
col.setSortType(null);
assertNull(col.getSortType());
treeTableView.getSortOrder().add(col);
treeTableView.setOnSort(event -> {
});
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, orange, banana);
col.setSortType(ASCENDING);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, banana, orange);
assertEquals(ASCENDING, col.getSortType());
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getSortOrder(), col);
}
@Test public void testSortMethodWithNullSortPolicy() {
TreeTableColumn<String, String> col = initSortTestStructure();
treeTableView.setSortPolicy(null);
assertNull(treeTableView.getSortPolicy());
treeTableView.sort();
}
@Test public void testNoIOOBEWhenSortingAfterSelectAndClearRootChildren() {
TreeTableView<String> ttv = new TreeTableView<>();
TreeItem<String> root = new TreeItem<>("root");
TreeItem<String> child = new TreeItem<>("child");
root.getChildren().add(child);
root.setExpanded(true);
ttv.setRoot(root);
ttv.setShowRoot(false);
TreeTableColumn<String, String> ttc = new TreeTableColumn<>("Column");
ttv.getSortOrder().add(ttc);
ttv.getSelectionModel().select(0);
root.getChildren().remove(0);
ControlTestUtils.runWithExceptionHandler(() -> {
ttv.sort();
});
}
@Test public void testNPEWhenRootItemIsNull() {
TreeTableView<String> ttv = new TreeTableView<>();
ControlTestUtils.runWithExceptionHandler(() -> {
ttv.sort();
});
}
@Test public void testChangingSortPolicyUpdatesItemsList() {
TreeTableColumn<String, String> col = initSortTestStructure();
col.setSortType(DESCENDING);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, orange, banana);
treeTableView.getSortOrder().add(col);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), orange, banana, apple);
treeTableView.setSortPolicy(SORT_SUCCESS_ASCENDING_SORT_POLICY);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, banana, orange);
}
@Test public void testChangingSortPolicyDoesNotUpdateItemsListWhenTheSortOrderListIsEmpty() {
TreeTableColumn<String, String> col = initSortTestStructure();
col.setSortType(DESCENDING);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, orange, banana);
treeTableView.setSortPolicy(SORT_SUCCESS_ASCENDING_SORT_POLICY);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, orange, banana);
}
@Test public void testFailedSortPolicyBacksOutLastChange_sortOrderAddition() {
TreeTableColumn<String, String> col = initSortTestStructure();
col.setSortType(DESCENDING);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, orange, banana);
treeTableView.setSortPolicy(NO_SORT_FAILED_SORT_POLICY);
treeTableView.getSortOrder().add(col);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, orange, banana);
assertTrue(treeTableView.getSortOrder().isEmpty());
}
@Test public void testFailedSortPolicyBacksOutLastChange_sortOrderRemoval() {
TreeTableColumn<String, String> col = initSortTestStructure();
col.setSortType(DESCENDING);
treeTableView.getSortOrder().add(col);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), orange, banana, apple);
treeTableView.setSortPolicy(NO_SORT_FAILED_SORT_POLICY);
treeTableView.getSortOrder().remove(col);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), orange, banana, apple);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getSortOrder(), col);
}
@Test public void testFailedSortPolicyBacksOutLastChange_sortTypeChange_ascendingToDescending() {
TreeTableColumn<String, String> col = initSortTestStructure();
col.setSortType(ASCENDING);
treeTableView.getSortOrder().add(col);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, banana, orange);
treeTableView.setSortPolicy(NO_SORT_FAILED_SORT_POLICY);
col.setSortType(DESCENDING);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, banana, orange);
assertEquals(ASCENDING, col.getSortType());
}
@Test public void testFailedSortPolicyBacksOutLastChange_sortTypeChange_descendingToNull() {
TreeTableColumn<String, String> col = initSortTestStructure();
col.setSortType(DESCENDING);
treeTableView.getSortOrder().add(col);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), orange, banana, apple);
treeTableView.setSortPolicy(NO_SORT_FAILED_SORT_POLICY);
col.setSortType(null);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), orange, banana, apple);
assertEquals(DESCENDING, col.getSortType());
}
@Test public void testFailedSortPolicyBacksOutLastChange_sortTypeChange_nullToAscending() {
TreeTableColumn<String, String> col = initSortTestStructure();
col.setSortType(null);
treeTableView.getSortOrder().add(col);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, orange, banana);
treeTableView.setSortPolicy(NO_SORT_FAILED_SORT_POLICY);
col.setSortType(ASCENDING);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, orange, banana);
assertNull(col.getSortType());
}
@Test public void testComparatorChangesInSyncWithSortOrder_1() {
TreeTableColumn<String, String> col = initSortTestStructure();
assertNull(treeTableView.getComparator());
assertTrue(treeTableView.getSortOrder().isEmpty());
treeTableView.getSortOrder().add(col);
TreeTableColumnComparator c = (TreeTableColumnComparator)treeTableView.getComparator();
assertNotNull(c);
VirtualFlowTestUtils.assertListContainsItemsInOrder(c.getColumns(), col);
}
@Test public void testComparatorChangesInSyncWithSortOrder_2() {
TreeTableColumn<String, String> col = initSortTestStructure();
assertNull(treeTableView.getComparator());
assertTrue(treeTableView.getSortOrder().isEmpty());
treeTableView.getSortOrder().add(col);
TreeTableColumnComparator c = (TreeTableColumnComparator)treeTableView.getComparator();
assertNotNull(c);
VirtualFlowTestUtils.assertListContainsItemsInOrder(c.getColumns(), col);
treeTableView.getSortOrder().remove(col);
assertNull(treeTableView.getComparator());
}
@Test public void testFailedSortPolicyBacksOutComparatorChange_sortOrderAddition() {
TreeTableColumn<String, String> col = initSortTestStructure();
final TreeTableColumnComparator oldComparator = (TreeTableColumnComparator)treeTableView.getComparator();
col.setSortType(DESCENDING);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), apple, orange, banana);
treeTableView.setSortPolicy(NO_SORT_FAILED_SORT_POLICY);
treeTableView.getSortOrder().add(col);
assertEquals(oldComparator, treeTableView.getComparator());
}
@Test public void testFailedSortPolicyBacksOutComparatorChange_sortOrderRemoval() {
TreeTableColumn<String, String> col = initSortTestStructure();
TreeTableColumnComparator oldComparator = (TreeTableColumnComparator)treeTableView.getComparator();
assertNull(oldComparator);
col.setSortType(DESCENDING);
treeTableView.getSortOrder().add(col);
VirtualFlowTestUtils.assertListContainsItemsInOrder(treeTableView.getRoot().getChildren(), orange, banana, apple);
oldComparator = (TreeTableColumnComparator)treeTableView.getComparator();
VirtualFlowTestUtils.assertListContainsItemsInOrder(oldComparator.getColumns(), col);
treeTableView.setSortPolicy(NO_SORT_FAILED_SORT_POLICY);
treeTableView.getSortOrder().remove(col);
assertTrue(treeTableView.getSortOrder().contains(col));
VirtualFlowTestUtils.assertListContainsItemsInOrder(oldComparator.getColumns(), col);
}
@Test public void testFailedSortPolicyBacksOutComparatorChange_sortTypeChange() {
TreeTableColumn<String, String> col = initSortTestStructure();
final TreeTableColumnComparator oldComparator = (TreeTableColumnComparator)treeTableView.getComparator();
assertNull(oldComparator);
treeTableView.setSortPolicy(NO_SORT_FAILED_SORT_POLICY);
treeTableView.getSortOrder().add(col);
col.setSortType(ASCENDING);
assertTrue(treeTableView.getSortOrder().isEmpty());
assertNull(oldComparator);
}
@Test public void test_rt18339_onlyEditWhenTableViewIsEditable_tableEditableIsFalse_columnEditableIsFalse() {
TreeTableColumn<String,String> first = new TreeTableColumn<String,String>("first");
first.setEditable(false);
treeTableView.getColumns().add(first);
treeTableView.setEditable(false);
treeTableView.edit(1, first);
assertEquals(null, treeTableView.getEditingCell());
}
@Test public void test_rt18339_onlyEditWhenTableViewIsEditable_tableEditableIsFalse_columnEditableIsTrue() {
TreeTableColumn<String,String> first = new TreeTableColumn<String,String>("first");
first.setEditable(true);
treeTableView.getColumns().add(first);
treeTableView.setEditable(false);
treeTableView.edit(1, first);
assertEquals(null, treeTableView.getEditingCell());
}
@Test public void test_rt18339_onlyEditWhenTableViewIsEditable_tableEditableIsTrue_columnEditableIsFalse() {
TreeTableColumn<String,String> first = new TreeTableColumn<String,String>("first");
first.setEditable(false);
treeTableView.getColumns().add(first);
treeTableView.setEditable(true);
treeTableView.edit(1, first);
assertEquals(null, treeTableView.getEditingCell());
}
@Test public void test_rt18339_onlyEditWhenTableViewIsEditable_tableEditableIsTrue_columnEditableIsTrue() {
TreeTableColumn<String,String> first = new TreeTableColumn<String,String>("first");
first.setEditable(true);
treeTableView.getColumns().add(first);
treeTableView.setEditable(true);
treeTableView.edit(1, first);
assertEquals(new TreeTablePosition(treeTableView, 1, first), treeTableView.getEditingCell());
}
@Test public void noArgConstructorSetsTheStyleClass() {
assertStyleClassContains(treeTableView, "tree-table-view");
}
@Test public void noArgConstructorSetsNullItems() {
assertNull(treeTableView.getRoot());
}
@Test public void singleArgConstructorSetsTheStyleClass() {
final TreeTableView<String> b2 = new TreeTableView<String>(new TreeItem<String>("Hi"));
assertStyleClassContains(b2, "tree-table-view");
}
@Test public void selectionModelCanBeNull() {
treeTableView.setSelectionModel(null);
assertNull(treeTableView.getSelectionModel());
}
@Test public void selectionModelCanBeBound() {
TableSelectionModel<TreeItem<String>> sm =
TreeTableViewShim.<String>get_TreeTableViewArrayListSelectionModel(treeTableView);
ObjectProperty<TreeTableView.TreeTableViewSelectionModel<String>> other =
new SimpleObjectProperty(sm);
treeTableView.selectionModelProperty().bind(other);
assertSame(sm, treeTableView.getSelectionModel());
}
@Test public void selectionModelCanBeChanged() {
TableSelectionModel<TreeItem<String>> sm =
TreeTableViewShim.<String>get_TreeTableViewArrayListSelectionModel(treeTableView);
TreeTableViewShim.<String>setSelectionModel(treeTableView, sm);
assertSame(sm, treeTableView.getSelectionModel());
}
@Test public void canSetSelectedItemToAnItemEvenWhenThereAreNoItems() {
TreeItem<String> element = new TreeItem<String>("I AM A CRAZY RANDOM STRING");
treeTableView.getSelectionModel().select(element);
assertEquals(-1, treeTableView.getSelectionModel().getSelectedIndex());
assertSame(element, treeTableView.getSelectionModel().getSelectedItem());
}
@Test public void canSetSelectedItemToAnItemNotInTheDataModel() {
installChildren();
TreeItem<String> element = new TreeItem<String>("I AM A CRAZY RANDOM STRING");
treeTableView.getSelectionModel().select(element);
assertEquals(-1, treeTableView.getSelectionModel().getSelectedIndex());
assertSame(element, treeTableView.getSelectionModel().getSelectedItem());
}
@Test public void settingTheSelectedItemToAnItemInItemsResultsInTheCorrectSelectedIndex() {
installChildren();
treeTableView.getSelectionModel().select(child1);
assertEquals(1, treeTableView.getSelectionModel().getSelectedIndex());
assertSame(child1, treeTableView.getSelectionModel().getSelectedItem());
}
@Ignore("Not yet supported")
@Test public void settingTheSelectedItemToANonexistantItemAndThenSettingItemsWhichContainsItResultsInCorrectSelectedIndex() {
treeTableView.getSelectionModel().select(child1);
installChildren();
assertEquals(1, treeTableView.getSelectionModel().getSelectedIndex());
assertSame(child1, treeTableView.getSelectionModel().getSelectedItem());
}
@Ignore("Not yet supported")
@Test public void ensureSelectionClearsWhenAllItemsAreRemoved_selectIndex0() {
installChildren();
treeTableView.getSelectionModel().select(0);
treeTableView.setRoot(null);
assertEquals(-1, treeTableView.getSelectionModel().getSelectedIndex());
assertEquals(null, treeTableView.getSelectionModel().getSelectedItem());
}
@Ignore("Not yet supported")
@Test public void ensureSelectionClearsWhenAllItemsAreRemoved_selectIndex2() {
installChildren();
treeTableView.getSelectionModel().select(2);
treeTableView.setRoot(null);
assertEquals(-1, treeTableView.getSelectionModel().getSelectedIndex());
assertEquals(null, treeTableView.getSelectionModel().getSelectedItem());
}
@Ignore("Not yet supported")
@Test public void ensureSelectedItemRemainsAccurateWhenItemsAreCleared() {
installChildren();
treeTableView.getSelectionModel().select(2);
treeTableView.setRoot(null);
assertNull(treeTableView.getSelectionModel().getSelectedItem());
assertEquals(-1, treeTableView.getSelectionModel().getSelectedIndex());
TreeItem<String> newRoot = new TreeItem<String>("New Root");
TreeItem<String> newChild1 = new TreeItem<String>("New Child 1");
TreeItem<String> newChild2 = new TreeItem<String>("New Child 2");
TreeItem<String> newChild3 = new TreeItem<String>("New Child 3");
newRoot.setExpanded(true);
newRoot.getChildren().setAll(newChild1, newChild2, newChild3);
treeTableView.setRoot(root);
treeTableView.getSelectionModel().select(2);
assertEquals(newChild2, treeTableView.getSelectionModel().getSelectedItem());
}
@Test public void ensureSelectionIsCorrectWhenItemsChange() {
installChildren();
treeTableView.getSelectionModel().select(0);
assertEquals(root, treeTableView.getSelectionModel().getSelectedItem());
TreeItem newRoot = new TreeItem<String>("New Root");
treeTableView.setRoot(newRoot);
assertEquals(-1, treeTableView.getSelectionModel().getSelectedIndex());
assertNull(treeTableView.getSelectionModel().getSelectedItem());
assertEquals(0, treeTableView.getFocusModel().getFocusedIndex());
assertEquals(newRoot, treeTableView.getFocusModel().getFocusedItem());
}
@Test public void ensureSelectionRemainsOnBranchWhenExpanded() {
installChildren();
root.setExpanded(false);
treeTableView.getSelectionModel().select(0);
assertTrue(treeTableView.getSelectionModel().isSelected(0));
root.setExpanded(true);
assertTrue(treeTableView.getSelectionModel().isSelected(0));
assertTrue(treeTableView.getSelectionModel().getSelectedItems().contains(root));
}
@Test public void ensureRootIndexIsZeroWhenRootIsShowing() {
installChildren();
assertEquals(0, treeTableView.getRow(root));
}
@Test public void ensureRootIndexIsNegativeOneWhenRootIsNotShowing() {
installChildren();
treeTableView.setShowRoot(false);
assertEquals(-1, treeTableView.getRow(root));
}
@Test public void ensureCorrectIndexWhenRootTreeItemHasParent() {
installChildren();
treeTableView.setRoot(child1);
assertEquals(-1, treeTableView.getRow(root));
assertEquals(0, treeTableView.getRow(child1));
assertEquals(1, treeTableView.getRow(child2));
assertEquals(2, treeTableView.getRow(child3));
}
@Test public void ensureCorrectIndexWhenRootTreeItemHasParentAndRootIsNotShowing() {
installChildren();
treeTableView.setRoot(child1);
treeTableView.setShowRoot(false);
assertEquals(0, treeTableView.getExpandedItemCount());
assertEquals(-1, treeTableView.getRow(root));
assertEquals(-1, treeTableView.getRow(child1));
assertEquals(-1, treeTableView.getRow(child2));
assertEquals(-1, treeTableView.getRow(child3));
}
@Test public void ensureCorrectIndexWhenRootTreeItemIsCollapsed() {
installChildren();
root.setExpanded(false);
assertEquals(0, treeTableView.getRow(root));
assertEquals(-1, treeTableView.getRow(child1));
assertEquals(-1, treeTableView.getRow(child2));
assertEquals(-1, treeTableView.getRow(child3));
}
@Ignore @Test public void test_rt17112() {
TreeItem<String> root1 = new TreeItem<String>("Root");
root1.setExpanded(true);
addChildren(root1, "child");
for (TreeItem child : root1.getChildren()) {
addChildren(child, (String)child.getValue());
child.setExpanded(true);
}
final TreeTableView treeTableView1 = new TreeTableView();
final MultipleSelectionModel sm = treeTableView1.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
treeTableView1.setRoot(root1);
final TreeItem<String> rt17112_child1 = root1.getChildren().get(1);
final TreeItem<String> rt17112_child1_0 = rt17112_child1.getChildren().get(0);
final TreeItem<String> rt17112_child2 = root1.getChildren().get(2);
sm.getSelectedItems().addListener(new InvalidationListener() {
int count = 0;
@Override public void invalidated(Observable observable) {
if (count == 0) {
assertEquals(rt17112_child1_0, sm.getSelectedItem());
assertEquals(1, sm.getSelectedIndices().size());
assertEquals(6, sm.getSelectedIndex());
assertTrue(treeTableView1.getFocusModel().isFocused(6));
} else if (count == 1) {
assertEquals(rt17112_child1, sm.getSelectedItem());
assertFalse(sm.getSelectedItems().contains(rt17112_child2));
assertEquals(1, sm.getSelectedIndices().size());
assertTrue(treeTableView1.getFocusModel().isFocused(5));
}
count++;
}
});
sm.select(rt17112_child1_0);
rt17112_child1.setExpanded(false);
}
private void addChildren(TreeItem parent, String name) {
for (int i=0; i<3; i++) {
TreeItem<String> ti = new TreeItem<String>(name+"-"+i);
parent.getChildren().add(ti);
}
}
@Test public void test_rt17522_focusShouldMoveWhenItemAddedAtFocusIndex_1() {
installChildren();
FocusModel fm = treeTableView.getFocusModel();
fm.focus(1);
assertTrue(fm.isFocused(1));
assertEquals(child1, fm.getFocusedItem());
TreeItem child0 = new TreeItem("child0");
root.getChildren().add(0, child0);
assertEquals(child1, fm.getFocusedItem());
assertTrue(fm.isFocused(2));
}
@Test public void test_rt17522_focusShouldMoveWhenItemAddedBeforeFocusIndex_1() {
installChildren();
FocusModel fm = treeTableView.getFocusModel();
fm.focus(1);
assertTrue(fm.isFocused(1));
TreeItem child0 = new TreeItem("child0");
root.getChildren().add(0, child0);
assertTrue("Focused index: " + fm.getFocusedIndex(), fm.isFocused(2));
}
@Test public void test_rt17522_focusShouldNotMoveWhenItemAddedAfterFocusIndex_1() {
installChildren();
FocusModel fm = treeTableView.getFocusModel();
fm.focus(1);
assertTrue(fm.isFocused(1));
TreeItem child4 = new TreeItem("child4");
root.getChildren().add(3, child4);
assertTrue("Focused index: " + fm.getFocusedIndex(), fm.isFocused(1));
}
@Test public void test_rt17522_focusShouldBeMovedWhenFocusedItemIsRemoved_1() {
installChildren();
FocusModel fm = treeTableView.getFocusModel();
fm.focus(1);
assertTrue(fm.isFocused(1));
root.getChildren().remove(child1);
assertEquals(0, fm.getFocusedIndex());
assertEquals(treeTableView.getTreeItem(0), fm.getFocusedItem());
}
@Test public void test_rt17522_focusShouldMoveWhenItemRemovedBeforeFocusIndex_1() {
installChildren();
FocusModel fm = treeTableView.getFocusModel();
fm.focus(2);
assertTrue(fm.isFocused(2));
root.getChildren().remove(child1);
assertTrue(fm.isFocused(1));
assertEquals(child2, fm.getFocusedItem());
}
@Test public void test_rt18385() {
installChildren();
treeTableView.getSelectionModel().select(1);
treeTableView.getRoot().getChildren().add(new TreeItem("Another Row"));
assertEquals(1, treeTableView.getSelectionModel().getSelectedIndices().size());
assertEquals(1, treeTableView.getSelectionModel().getSelectedItems().size());
}
@Test public void test_rt18339_onlyEditWhenTreeTableViewIsEditable_editableIsFalse() {
TreeItem root = new TreeItem("root");
root.getChildren().setAll(
new TreeItem(new Person("Jacob", "Smith", "jacob.smith@example.com")),
new TreeItem(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
new TreeItem(new Person("Ethan", "Williams", "ethan.williams@example.com")),
new TreeItem(new Person("Emma", "Jones", "emma.jones@example.com")),
new TreeItem(new Person("Michael", "Brown", "michael.brown@example.com")));
root.setExpanded(true);
TreeTableView<Person> table = new TreeTableView<Person>(root);
TreeTableColumn firstNameCol = new TreeTableColumn("First Name");
firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("firstName"));
table.setEditable(false);
table.edit(0,firstNameCol);
assertNull(table.getEditingCell());
}
@Test public void test_rt18339_onlyEditWhenTreeTableViewIsEditable_editableIsTrue() {
TreeItem root = new TreeItem("root");
root.getChildren().setAll(
new TreeItem(new Person("Jacob", "Smith", "jacob.smith@example.com")),
new TreeItem(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
new TreeItem(new Person("Ethan", "Williams", "ethan.williams@example.com")),
new TreeItem(new Person("Emma", "Jones", "emma.jones@example.com")),
new TreeItem(new Person("Michael", "Brown", "michael.brown@example.com")));
root.setExpanded(true);
TreeTableView<Person> table = new TreeTableView<Person>(root);
TreeTableColumn firstNameCol = new TreeTableColumn("First Name");
firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("firstName"));
table.setEditable(true);
table.edit(0,firstNameCol);
assertEquals(root, table.getEditingCell().getTreeItem());
}
@Test public void test_rt14451() {
installChildren();
treeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
treeTableView.getSelectionModel().selectRange(0, 2);
assertEquals(2, treeTableView.getSelectionModel().getSelectedIndices().size());
}
@Test public void test_rt21586() {
installChildren();
treeTableView.getSelectionModel().select(1);
assertEquals(1, treeTableView.getSelectionModel().getSelectedIndex());
assertEquals(child1, treeTableView.getSelectionModel().getSelectedItem());
TreeItem root = new TreeItem<String>("New Root");
TreeItem child1 = new TreeItem<String>("New Child 1");
TreeItem child2 = new TreeItem<String>("New Child 2");
TreeItem child3 = new TreeItem<String>("New Child 3");
root.setExpanded(true);
root.getChildren().setAll(child1, child2, child3);
treeTableView.setRoot(root);
assertEquals(-1, treeTableView.getSelectionModel().getSelectedIndex());
assertNull(treeTableView.getSelectionModel().getSelectedItem());
assertEquals(0, treeTableView.getFocusModel().getFocusedIndex());
assertEquals(root, treeTableView.getFocusModel().getFocusedItem());
}
@Test public void test_rt27181() {
myCompanyRootNode.setExpanded(true);
treeTableView.setRoot(myCompanyRootNode);
salesDepartment.setExpanded(true);
treeTableView.getSelectionModel().select(salesDepartment);
assertEquals(1, treeTableView.getFocusModel().getFocusedIndex());
itSupport.setExpanded(true);
assertEquals(1, treeTableView.getFocusModel().getFocusedIndex());
}
@Test public void test_rt27185() {
myCompanyRootNode.setExpanded(true);
treeTableView.setRoot(myCompanyRootNode);
itSupport.setExpanded(true);
treeTableView.getSelectionModel().select(mikeGraham);
assertEquals(mikeGraham, treeTableView.getFocusModel().getFocusedItem());
salesDepartment.setExpanded(true);
assertEquals(mikeGraham, treeTableView.getFocusModel().getFocusedItem());
}
@Test public void test_rt28114() {
myCompanyRootNode.setExpanded(true);
treeTableView.setRoot(myCompanyRootNode);
itSupport.setExpanded(true);
treeTableView.getSelectionModel().select(itSupport);
assertEquals(itSupport, treeTableView.getFocusModel().getFocusedItem());
assertEquals(itSupport, treeTableView.getSelectionModel().getSelectedItem());
assertTrue(! itSupport.isLeaf());
assertTrue(itSupport.isExpanded());
itSupport.getChildren().remove(mikeGraham);
assertEquals(itSupport, treeTableView.getFocusModel().getFocusedItem());
assertEquals(itSupport, treeTableView.getSelectionModel().getSelectedItem());
}
@Test public void test_rt27820_1() {
TreeItem root = new TreeItem("root");
root.setExpanded(true);
TreeItem child = new TreeItem("child");
root.getChildren().add(child);
treeTableView.setRoot(root);
treeTableView.getSelectionModel().select(0);
assertEquals(1, treeTableView.getSelectionModel().getSelectedItems().size());
assertEquals(root, treeTableView.getSelectionModel().getSelectedItem());
treeTableView.setRoot(null);
assertEquals(0, treeTableView.getSelectionModel().getSelectedItems().size());
assertNull(treeTableView.getSelectionModel().getSelectedItem());
}
@Test public void test_rt27820_2() {
TreeItem root = new TreeItem("root");
root.setExpanded(true);
TreeItem child = new TreeItem("child");
root.getChildren().add(child);
treeTableView.setRoot(root);
treeTableView.getSelectionModel().select(1);
assertEquals(1, treeTableView.getSelectionModel().getSelectedItems().size());
assertEquals(child, treeTableView.getSelectionModel().getSelectedItem());
treeTableView.setRoot(null);
assertEquals(0, treeTableView.getSelectionModel().getSelectedItems().size());
assertNull(treeTableView.getSelectionModel().getSelectedItem());
}
@Test public void test_rt28390() {
TreeItem root = new TreeItem("root");
treeTableView.setRoot(root);
treeTableView.setRowFactory(new Callback() {
@Override public Object call(Object p) {
TreeTableRow treeCell = new TreeTableRowShim() {
{
disclosureNodeProperty().addListener((ov, t, t1) -> {
setDisclosureNode(null);
});
}
@Override public void updateItem(Object item, boolean empty) {
super.updateItem(item, empty);
setText(item == null ? "" : item.toString());
}
};
treeCell.setDisclosureNode(null);
return treeCell;
}
});
try {
Group group = new Group();
group.getChildren().setAll(treeTableView);
Scene scene = new Scene(group);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
} catch (NullPointerException e) {
System.out.println("A null disclosure node is valid, so we shouldn't have an NPE here.");
e.printStackTrace();
assertTrue(false);
}
}
@Ignore("This test begun failing when createDefaultCellImpl was removed from TreeTableViewSkin on 28/3/2013")
@Test public void test_rt28534() {
TreeItem root = new TreeItem("root");
root.getChildren().setAll(
new TreeItem(new Person("Jacob", "Smith", "jacob.smith@example.com")),
new TreeItem(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
new TreeItem(new Person("Ethan", "Williams", "ethan.williams@example.com")),
new TreeItem(new Person("Emma", "Jones", "emma.jones@example.com")),
new TreeItem(new Person("Michael", "Brown", "michael.brown@example.com")));
root.setExpanded(true);
TreeTableView<Person> table = new TreeTableView<Person>(root);
TreeTableColumn firstNameCol = new TreeTableColumn("First Name");
firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("firstName"));
TreeTableColumn lastNameCol = new TreeTableColumn("Last Name");
lastNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("lastName"));
TreeTableColumn emailCol = new TreeTableColumn("Email");
emailCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("email"));
table.getColumns().addAll(firstNameCol, lastNameCol, emailCol);
VirtualFlowTestUtils.assertRowsNotEmpty(table, 0, 6);
VirtualFlowTestUtils.assertRowsEmpty(table, 6, -1);
root.getChildren().setAll(
new TreeItem(new Person("*_*Emma", "Jones", "emma.jones@example.com")),
new TreeItem(new Person("_Michael", "Brown", "michael.brown@example.com")));
VirtualFlowTestUtils.assertRowsNotEmpty(table, 0, 3);
VirtualFlowTestUtils.assertRowsEmpty(table, 3, -1);
}
@Test public void test_rt22463() {
final TreeTableView<RT_22463_Person> table = new TreeTableView<RT_22463_Person>();
table.setTableMenuButtonVisible(true);
TreeTableColumn c1 = new TreeTableColumn("Id");
TreeTableColumn c2 = new TreeTableColumn("Name");
c1.setCellValueFactory(new TreeItemPropertyValueFactory<Person, Long>("id"));
c2.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("name"));
table.getColumns().addAll(c1, c2);
RT_22463_Person rootPerson = new RT_22463_Person();
rootPerson.setName("Root");
TreeItem<RT_22463_Person> root = new TreeItem<RT_22463_Person>(rootPerson);
root.setExpanded(true);
table.setRoot(root);
RT_22463_Person p1 = new RT_22463_Person();
p1.setId(1l);
p1.setName("name1");
RT_22463_Person p2 = new RT_22463_Person();
p2.setId(2l);
p2.setName("name2");
root.getChildren().addAll(
new TreeItem<RT_22463_Person>(p1),
new TreeItem<RT_22463_Person>(p2));
VirtualFlowTestUtils.assertCellTextEquals(table, 1, "1", "name1");
VirtualFlowTestUtils.assertCellTextEquals(table, 2, "2", "name2");
RT_22463_Person new_p1 = new RT_22463_Person();
new_p1.setId(1l);
new_p1.setName("updated name1");
RT_22463_Person new_p2 = new RT_22463_Person();
new_p2.setId(2l);
new_p2.setName("updated name2");
root.getChildren().clear();
root.getChildren().setAll(
new TreeItem<RT_22463_Person>(new_p1),
new TreeItem<RT_22463_Person>(new_p2));
VirtualFlowTestUtils.assertCellTextEquals(table, 1, "1", "updated name1");
VirtualFlowTestUtils.assertCellTextEquals(table, 2, "2", "updated name2");
}
@Test public void test_rt28637() {
TreeItem<String> s1, s2, s3, s4;
ObservableList<TreeItem<String>> items = FXCollections.observableArrayList(
s1 = new TreeItem<String>("String1"),
s2 = new TreeItem<String>("String2"),
s3 = new TreeItem<String>("String3"),
s4 = new TreeItem<String>("String4"));
final TreeTableView<String> treeTableView = new TreeTableView<String>();
TreeItem<String> root = new TreeItem<String>("Root");
root.setExpanded(true);
treeTableView.setRoot(root);
treeTableView.setShowRoot(false);
root.getChildren().addAll(items);
treeTableView.getSelectionModel().select(0);
assertEquals((Object)s1, treeTableView.getSelectionModel().getSelectedItem());
assertEquals((Object)s1, treeTableView.getSelectionModel().getSelectedItems().get(0));
assertEquals(0, treeTableView.getSelectionModel().getSelectedIndex());
root.getChildren().remove(treeTableView.getSelectionModel().getSelectedItem());
assertEquals((Object)s2, treeTableView.getSelectionModel().getSelectedItem());
assertEquals((Object)s2, treeTableView.getSelectionModel().getSelectedItems().get(0));
assertEquals(0, treeTableView.getSelectionModel().getSelectedIndex());
}
@Test public void test_rt24844() {
TreeItem<Person> p0, p1, p2, p3, p4;
ObservableList<TreeItem<Person>> persons = FXCollections.observableArrayList(
p3 = new TreeItem<Person>(new Person("Jacob", "Smith", "jacob.smith@example.com")),
p2 = new TreeItem<Person>(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
p1 = new TreeItem<Person>(new Person("Ethan", "Williams", "ethan.williams@example.com")),
p0 = new TreeItem<Person>(new Person("Emma", "Jones", "emma.jones@example.com")),
p4 = new TreeItem<Person>(new Person("Michael", "Brown", "michael.brown@example.com")));
TreeTableView<Person> table = new TreeTableView<>();
TreeItem<Person> root = new TreeItem<Person>(new Person("Root", null, null));
root.setExpanded(true);
table.setRoot(root);
table.setShowRoot(false);
root.getChildren().setAll(persons);
TreeTableColumn firstNameCol = new TreeTableColumn("First Name");
firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("firstName"));
firstNameCol.setComparator((t, t1) -> 0);
table.getColumns().addAll(firstNameCol);
table.getSortOrder().add(firstNameCol);
assertEquals(p3, root.getChildren().get(0));
assertEquals(p2, root.getChildren().get(1));
assertEquals(p1, root.getChildren().get(2));
assertEquals(p0, root.getChildren().get(3));
assertEquals(p4, root.getChildren().get(4));
firstNameCol.setComparator((t, t1) -> t.toString().compareTo(t1.toString()));
assertEquals(p0, root.getChildren().get(0));
assertEquals(p1, root.getChildren().get(1));
assertEquals(p2, root.getChildren().get(2));
assertEquals(p3, root.getChildren().get(3));
assertEquals(p4, root.getChildren().get(4));
}
@Test public void test_rt29331() {
TreeTableView<Person> table = new TreeTableView<Person>();
TreeItem<Person> p0, p1, p2, p3, p4;
TreeTableColumn firstNameCol = new TreeTableColumn("First Name");
firstNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));
TreeTableColumn lastNameCol = new TreeTableColumn("Last Name");
lastNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("lastName"));
TreeTableColumn emailCol = new TreeTableColumn("Email");
emailCol.setCellValueFactory(new PropertyValueFactory<Person, String>("email"));
TreeTableColumn parentColumn = new TreeTableColumn<>("Parent");
parentColumn.getColumns().addAll(firstNameCol, lastNameCol, emailCol);
table.getColumns().addAll(parentColumn);
emailCol.setVisible(false);
assertFalse(emailCol.isVisible());
parentColumn.getColumns().setAll(emailCol, firstNameCol, lastNameCol);
assertFalse(emailCol.isVisible());
}
private int rt29330_count = 0;
@Test public void test_rt29330_1() {
ObservableList<TreeItem<Person>> persons = FXCollections.observableArrayList(
new TreeItem<Person>(new Person("Jacob", "Smith", "jacob.smith@example.com")),
new TreeItem<Person>(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
new TreeItem<Person>(new Person("Ethan", "Williams", "ethan.williams@example.com")),
new TreeItem<Person>(new Person("Emma", "Jones", "emma.jones@example.com")),
new TreeItem<Person>(new Person("Michael", "Brown", "michael.brown@example.com")));
TreeTableView<Person> table = new TreeTableView<>();
TreeItem<Person> root = new TreeItem<Person>(new Person("Root", null, null));
root.setExpanded(true);
table.setRoot(root);
table.setShowRoot(false);
root.getChildren().setAll(persons);
TreeTableColumn parentColumn = new TreeTableColumn<>("Parent");
table.getColumns().addAll(parentColumn);
TreeTableColumn firstNameCol = new TreeTableColumn("First Name");
firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("firstName"));
TreeTableColumn lastNameCol = new TreeTableColumn("Last Name");
lastNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("lastName"));
parentColumn.getColumns().addAll(firstNameCol, lastNameCol);
table.setOnSort(event -> {
rt29330_count++;
});
assertEquals(ASCENDING, lastNameCol.getSortType());
assertEquals(0, rt29330_count);
table.getSortOrder().add(lastNameCol);
assertEquals(1, rt29330_count);
lastNameCol.setSortType(DESCENDING);
assertEquals(2, rt29330_count);
lastNameCol.setSortType(null);
assertEquals(3, rt29330_count);
lastNameCol.setSortType(ASCENDING);
assertEquals(4, rt29330_count);
}
@Test public void test_rt29330_2() {
ObservableList<TreeItem<Person>> persons = FXCollections.observableArrayList(
new TreeItem<Person>(new Person("Jacob", "Smith", "jacob.smith@example.com")),
new TreeItem<Person>(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
new TreeItem<Person>(new Person("Ethan", "Williams", "ethan.williams@example.com")),
new TreeItem<Person>(new Person("Emma", "Jones", "emma.jones@example.com")),
new TreeItem<Person>(new Person("Michael", "Brown", "michael.brown@example.com")));
TreeTableView<Person> table = new TreeTableView<>();
TreeItem<Person> root = new TreeItem<Person>(new Person("Root", null, null));
root.setExpanded(true);
table.setRoot(root);
table.setShowRoot(false);
root.getChildren().setAll(persons);
TreeTableColumn firstNameCol = new TreeTableColumn("First Name");
firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("firstName"));
TreeTableColumn lastNameCol = new TreeTableColumn("Last Name");
lastNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("lastName"));
TreeTableColumn parentColumn = new TreeTableColumn<>("Parent");
parentColumn.getColumns().addAll(firstNameCol, lastNameCol);
table.getColumns().addAll(parentColumn);
table.setOnSort(event -> {
rt29330_count++;
});
assertEquals(ASCENDING, lastNameCol.getSortType());
assertEquals(0, rt29330_count);
table.getSortOrder().add(lastNameCol);
assertEquals(1, rt29330_count);
lastNameCol.setSortType(DESCENDING);
assertEquals(2, rt29330_count);
lastNameCol.setSortType(null);
assertEquals(3, rt29330_count);
lastNameCol.setSortType(ASCENDING);
assertEquals(4, rt29330_count);
}
@Test public void test_rt29313_selectedIndices() {
ObservableList<TreeItem<Person>> persons = FXCollections.observableArrayList(
new TreeItem<Person>(new Person("Jacob", "Smith", "jacob.smith@example.com")),
new TreeItem<Person>(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
new TreeItem<Person>(new Person("Ethan", "Williams", "ethan.williams@example.com")),
new TreeItem<Person>(new Person("Emma", "Jones", "emma.jones@example.com")),
new TreeItem<Person>(new Person("Michael", "Brown", "michael.brown@example.com")));
TreeTableView<Person> table = new TreeTableView<>();
TreeItem<Person> root = new TreeItem<Person>(new Person("Root", null, null));
root.setExpanded(true);
table.setRoot(root);
table.setShowRoot(false);
root.getChildren().setAll(persons);
TableSelectionModel sm = table.getSelectionModel();
TreeTableColumn firstNameCol = new TreeTableColumn("First Name");
firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("firstName"));
TreeTableColumn lastNameCol = new TreeTableColumn("Last Name");
lastNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("lastName"));
TreeTableColumn emailCol = new TreeTableColumn("Email");
emailCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("email"));
table.getColumns().addAll(firstNameCol, lastNameCol, emailCol);
sm.setCellSelectionEnabled(true);
sm.setSelectionMode(SelectionMode.MULTIPLE);
assertTrue(sm.getSelectedIndices().isEmpty());
sm.select(0, firstNameCol);
assertEquals(1, sm.getSelectedIndices().size());
sm.select(1, firstNameCol);
assertEquals(2, sm.getSelectedIndices().size());
sm.select(1, lastNameCol);
assertEquals(2, sm.getSelectedIndices().size());
assertEquals(0, sm.getSelectedIndices().get(0));
assertEquals(1, sm.getSelectedIndices().get(1));
}
@Test public void test_rt29313_selectedItems() {
TreeItem<Person> p0, p1;
ObservableList<TreeItem<Person>> persons = FXCollections.observableArrayList(
p0 = new TreeItem<Person>(new Person("Jacob", "Smith", "jacob.smith@example.com")),
p1 = new TreeItem<Person>(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
new TreeItem<Person>(new Person("Ethan", "Williams", "ethan.williams@example.com")),
new TreeItem<Person>(new Person("Emma", "Jones", "emma.jones@example.com")),
new TreeItem<Person>(new Person("Michael", "Brown", "michael.brown@example.com")));
TreeTableView<Person> table = new TreeTableView<>();
TreeItem<Person> root = new TreeItem<Person>(new Person("Root", null, null));
root.setExpanded(true);
table.setRoot(root);
table.setShowRoot(false);
root.getChildren().setAll(persons);
TableSelectionModel sm = table.getSelectionModel();
TreeTableColumn firstNameCol = new TreeTableColumn("First Name");
firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("firstName"));
TreeTableColumn lastNameCol = new TreeTableColumn("Last Name");
lastNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("lastName"));
TreeTableColumn emailCol = new TreeTableColumn("Email");
emailCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("email"));
table.getColumns().addAll(firstNameCol, lastNameCol, emailCol);
sm.setCellSelectionEnabled(true);
sm.setSelectionMode(SelectionMode.MULTIPLE);
assertTrue(sm.getSelectedItems().isEmpty());
sm.select(0, firstNameCol);
assertEquals(1, sm.getSelectedItems().size());
sm.select(1, firstNameCol);
assertEquals(2, sm.getSelectedItems().size());
sm.select(1, lastNameCol);
assertEquals(2, sm.getSelectedItems().size());
assertEquals(p0, sm.getSelectedItems().get(0));
assertEquals(p1, sm.getSelectedItems().get(1));
}
@Test public void test_rt29566() {
ObservableList<TreeItem<Person>> persons = FXCollections.observableArrayList(
new TreeItem<Person>(new Person("Jacob", "Smith", "jacob.smith@example.com")),
new TreeItem<Person>(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
new TreeItem<Person>(new Person("Ethan", "Williams", "ethan.williams@example.com")),
new TreeItem<Person>(new Person("Emma", "Jones", "emma.jones@example.com")),
new TreeItem<Person>(new Person("Michael", "Brown", "michael.brown@example.com")));
TreeTableView<Person> table = new TreeTableView<>();
TreeItem<Person> root = new TreeItem<Person>(new Person("Root", null, null));
root.setExpanded(true);
table.setRoot(root);
table.setShowRoot(false);
root.getChildren().setAll(persons);
TableSelectionModel sm = table.getSelectionModel();
TreeTableColumn firstNameCol = new TreeTableColumn("First Name");
firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("firstName"));
TreeTableColumn lastNameCol = new TreeTableColumn("Last Name");
lastNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("lastName"));
TreeTableColumn emailCol = new TreeTableColumn("Email");
emailCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("email"));
table.getColumns().addAll(firstNameCol, lastNameCol, emailCol);
VirtualFlowTestUtils.assertCellTextEquals(table, 0, "Jacob", "Smith", "jacob.smith@example.com");
VirtualFlowTestUtils.assertCellTextEquals(table, 1, "Isabella", "Johnson", "isabella.johnson@example.com");
VirtualFlowTestUtils.assertCellTextEquals(table, 2, "Ethan", "Williams", "ethan.williams@example.com");
VirtualFlowTestUtils.assertCellTextEquals(table, 3, "Emma", "Jones", "emma.jones@example.com");
VirtualFlowTestUtils.assertCellTextEquals(table, 4, "Michael", "Brown", "michael.brown@example.com");
table.getColumns().remove(lastNameCol);
VirtualFlowTestUtils.assertCellTextEquals(table, 0, "Jacob", "jacob.smith@example.com");
VirtualFlowTestUtils.assertCellTextEquals(table, 1, "Isabella", "isabella.johnson@example.com");
VirtualFlowTestUtils.assertCellTextEquals(table, 2, "Ethan", "ethan.williams@example.com");
VirtualFlowTestUtils.assertCellTextEquals(table, 3, "Emma", "emma.jones@example.com");
VirtualFlowTestUtils.assertCellTextEquals(table, 4, "Michael", "michael.brown@example.com");
table.getColumns().add(1, lastNameCol);
VirtualFlowTestUtils.assertCellTextEquals(table, 0, "Jacob", "Smith", "jacob.smith@example.com");
VirtualFlowTestUtils.assertCellTextEquals(table, 1, "Isabella", "Johnson", "isabella.johnson@example.com");
VirtualFlowTestUtils.assertCellTextEquals(table, 2, "Ethan", "Williams", "ethan.williams@example.com");
VirtualFlowTestUtils.assertCellTextEquals(table, 3, "Emma", "Jones", "emma.jones@example.com");
VirtualFlowTestUtils.assertCellTextEquals(table, 4, "Michael", "Brown", "michael.brown@example.com");
}
@Test public void test_rt29390() {
ObservableList<TreeItem<Person>> persons = FXCollections.observableArrayList(
new TreeItem<Person>(new Person("Jacob", "Smith", "jacob.smith@example.com")),
new TreeItem<Person>(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
new TreeItem<Person>(new Person("Ethan", "Williams", "ethan.williams@example.com")),
new TreeItem<Person>(new Person("Emma", "Jones", "emma.jones@example.com")),
new TreeItem<Person>(new Person("Jacob", "Smith", "jacob.smith@example.com")),
new TreeItem<Person>(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
new TreeItem<Person>(new Person("Ethan", "Williams", "ethan.williams@example.com")),
new TreeItem<Person>(new Person("Emma", "Jones", "emma.jones@example.com")),
new TreeItem<Person>(new Person("Jacob", "Smith", "jacob.smith@example.com")),
new TreeItem<Person>(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
new TreeItem<Person>(new Person("Ethan", "Williams", "ethan.williams@example.com")),
new TreeItem<Person>(new Person("Emma", "Jones", "emma.jones@example.com")),
new TreeItem<Person>(new Person("Jacob", "Smith", "jacob.smith@example.com")),
new TreeItem<Person>(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
new TreeItem<Person>(new Person("Ethan", "Williams", "ethan.williams@example.com")),
new TreeItem<Person>(new Person("Emma", "Jones", "emma.jones@example.com")
));
TreeTableView<Person> table = new TreeTableView<>();
table.setMaxHeight(50);
table.setPrefHeight(50);
TreeItem<Person> root = new TreeItem<Person>(new Person("Root", null, null));
root.setExpanded(true);
table.setRoot(root);
table.setShowRoot(false);
root.getChildren().setAll(persons);
TreeTableColumn firstNameCol = new TreeTableColumn("First Name");
firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("firstName"));
table.getColumns().add(firstNameCol);
Toolkit.getToolkit().firePulse();
VirtualScrollBar scrollBar = VirtualFlowTestUtils.getVirtualFlowVerticalScrollbar(table);
assertNotNull(scrollBar);
assertTrue(scrollBar.isVisible());
assertTrue(scrollBar.getVisibleAmount() > 0.0);
assertTrue(scrollBar.getVisibleAmount() < 1.0);
assertEquals(0.0625, scrollBar.getVisibleAmount(), 0.0);
}
@Test public void test_rt29676_withText() {
TreeTableView<Data> treeTableView = new TreeTableView<Data>();
treeTableView.setMaxWidth(100);
TreeItem<Data> root = new TreeItem<Data>(new Data("Root"));
treeTableView.setRoot(root);
addLevel(root, 0, 30);
treeTableView.getRoot().setExpanded(true);
TreeTableColumn<Data, String> column = new TreeTableColumn<Data, String>("Items' name");
column.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getValue().getData()));
treeTableView.getColumns().add(column);
StageLoader sl = new StageLoader(treeTableView);
root.setExpanded(true);
for (int i = 0; i < root.getChildren().size(); i++) {
TreeItem<Data> child = root.getChildren().get(i);
child.setExpanded(true);
}
int cellCount = VirtualFlowTestUtils.getCellCount(treeTableView);
for (int i = 0; i < cellCount; i++) {
final TreeTableRow rowCell = (TreeTableRow) VirtualFlowTestUtils.getCell(treeTableView, i);
final TreeItem treeItem = rowCell.getTreeItem();
if (treeItem == null) continue;
final boolean isBranch = ! treeItem.isLeaf();
List<Node> children = rowCell.getChildrenUnmodifiable();
for (int j = 0; j < children.size(); j++) {
final Node child = children.get(j);
assertTrue(child.isVisible());
assertNotNull(child.getParent());
assertNotNull(child.getScene());
if (child.getStyleClass().contains("tree-disclosure-node")) {
}
if (child.getStyleClass().contains("tree-table-cell")) {
TreeTableCell cell = (TreeTableCell) child;
assertNotNull(cell.getText());
assertFalse(cell.getText().isEmpty());
}
}
}
sl.dispose();
}
private void addLevel(TreeItem<Data> item, int level, int length) {
for (int i = 0; i < 3; i++) {
StringBuilder builder = new StringBuilder();
builder.append("Level " + level + " Item " + item);
if (length > 0) {
builder.append(" l");
for (int j = 0; j < length; j++) {
builder.append("o");
}
builder.append("ng");
}
String itemString = builder.toString();
TreeItem<Data> child = new TreeItem<Data>(new Data(itemString));
if (level < 3 - 1) {
addLevel(child, level + 1, length);
}
item.getChildren().add(child);
}
}
@Test public void test_rt27180_collapseBranch_childSelected_singleSelection() {
sm.setCellSelectionEnabled(false);
sm.setSelectionMode(SelectionMode.SINGLE);
treeTableView.setRoot(myCompanyRootNode);
myCompanyRootNode.setExpanded(true);
salesDepartment.setExpanded(true);
itSupport.setExpanded(true);
sm.select(2);
assertFalse(sm.isSelected(1));
assertTrue(sm.isSelected(2));
assertTrue(treeTableView.getFocusModel().isFocused(2));
assertEquals(1, sm.getSelectedCells().size());
salesDepartment.setExpanded(false);
assertTrue(sm.getSelectedIndices().toString(), sm.isSelected(1));
assertTrue(treeTableView.getFocusModel().isFocused(1));
assertEquals(1, sm.getSelectedCells().size());
}
@Test public void test_rt27180_collapseBranch_laterSiblingSelected_singleSelection() {
sm.setCellSelectionEnabled(false);
sm.setSelectionMode(SelectionMode.SINGLE);
treeTableView.setRoot(myCompanyRootNode);
myCompanyRootNode.setExpanded(true);
salesDepartment.setExpanded(true);
itSupport.setExpanded(true);
sm.select(8);
assertFalse(sm.isSelected(1));
assertTrue(sm.isSelected(8));
assertTrue(treeTableView.getFocusModel().isFocused(8));
assertEquals(1, sm.getSelectedIndices().size());
salesDepartment.setExpanded(false);
assertTrue(debug(), sm.isSelected(2));
assertTrue(treeTableView.getFocusModel().isFocused(2));
assertEquals(1, sm.getSelectedIndices().size());
}
@Test public void test_rt27180_collapseBranch_laterSiblingAndChildrenSelected() {
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.setCellSelectionEnabled(false);
treeTableView.setRoot(myCompanyRootNode);
myCompanyRootNode.setExpanded(true);
salesDepartment.setExpanded(true);
itSupport.setExpanded(true);
sm.clearSelection();
sm.selectIndices(8, 9, 10);
assertFalse(sm.isSelected(1));
assertTrue(sm.isSelected(8));
assertTrue(sm.isSelected(9));
assertTrue(sm.isSelected(10));
assertTrue(treeTableView.getFocusModel().isFocused(10));
assertEquals(debug(), 3, sm.getSelectedIndices().size());
salesDepartment.setExpanded(false);
assertTrue(debug(), sm.isSelected(2));
assertTrue(sm.isSelected(3));
assertTrue(sm.isSelected(4));
assertTrue(treeTableView.getFocusModel().isFocused(4));
assertEquals(3, sm.getSelectedIndices().size());
}
@Test public void test_rt27180_expandBranch_laterSiblingSelected_singleSelection() {
sm.setCellSelectionEnabled(false);
sm.setSelectionMode(SelectionMode.SINGLE);
treeTableView.setRoot(myCompanyRootNode);
myCompanyRootNode.setExpanded(true);
salesDepartment.setExpanded(false);
itSupport.setExpanded(true);
sm.select(2);
assertFalse(sm.isSelected(1));
assertTrue(sm.isSelected(2));
assertTrue(treeTableView.getFocusModel().isFocused(2));
assertEquals(1, sm.getSelectedIndices().size());
salesDepartment.setExpanded(true);
assertTrue(debug(), sm.isSelected(8));
assertTrue(treeTableView.getFocusModel().isFocused(8));
assertEquals(1, sm.getSelectedIndices().size());
}
@Test public void test_rt27180_expandBranch_laterSiblingAndChildrenSelected() {
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.setCellSelectionEnabled(false);
treeTableView.setRoot(myCompanyRootNode);
myCompanyRootNode.setExpanded(true);
salesDepartment.setExpanded(false);
itSupport.setExpanded(true);
sm.clearSelection();
sm.selectIndices(2,3,4);
assertFalse(sm.isSelected(1));
assertTrue(sm.isSelected(2));
assertTrue(sm.isSelected(3));
assertTrue(sm.isSelected(4));
assertTrue(treeTableView.getFocusModel().isFocused(4));
assertEquals(3, sm.getSelectedIndices().size());
salesDepartment.setExpanded(true);
assertTrue(debug(), sm.isSelected(8));
assertTrue(sm.isSelected(9));
assertTrue(sm.isSelected(10));
assertTrue(treeTableView.getFocusModel().isFocused(10));
assertEquals(3, sm.getSelectedIndices().size());
}
@Test public void test_rt30400() {
TreeItem<String> rootItem = new TreeItem<>("root");
final TreeTableView<String> tableView = new TreeTableView<String>(rootItem);
tableView.setMinHeight(100);
tableView.setPrefHeight(100);
TreeTableColumn<String, String> firstNameCol = new TreeTableColumn<>("First Name");
firstNameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue()));
firstNameCol.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(param -> new ReadOnlyBooleanWrapper(true)));
tableView.getColumns().add(firstNameCol);
VirtualFlowTestUtils.assertRowsNotEmpty(tableView, 0, 1);
VirtualFlowTestUtils.assertCellNotEmpty(VirtualFlowTestUtils.getCell(tableView, 0));
VirtualFlowTestUtils.assertCellEmpty(VirtualFlowTestUtils.getCell(tableView, 1));
VirtualFlowTestUtils.assertCellEmpty(VirtualFlowTestUtils.getCell(tableView, 2));
VirtualFlowTestUtils.assertCellEmpty(VirtualFlowTestUtils.getCell(tableView, 3));
}
@Ignore("This bug is not yet fixed")
@Test public void test_rt31165() {
installChildren();
treeTableView.setEditable(true);
TreeTableColumn firstNameCol = new TreeTableColumn("First Name");
firstNameCol.setCellValueFactory(param -> new ReadOnlyStringWrapper("TEST"));
firstNameCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
firstNameCol.setEditable(true);
treeTableView.getColumns().add(firstNameCol);
IndexedCell cell = VirtualFlowTestUtils.getCell(treeTableView, 1, 0);
assertEquals("TEST", cell.getText());
assertFalse(cell.isEditing());
treeTableView.edit(1, firstNameCol);
assertEquals(child1, treeTableView.getEditingCell().getTreeItem());
assertTrue(cell.isEditing());
VirtualFlowTestUtils.getVirtualFlow(treeTableView).requestLayout();
Toolkit.getToolkit().firePulse();
assertEquals(child1, treeTableView.getEditingCell().getTreeItem());
assertTrue(cell.isEditing());
}
@Test public void test_rt31404() {
installChildren();
TreeTableColumn<String,String> firstNameCol = new TreeTableColumn<>("First Name");
firstNameCol.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue()));
treeTableView.getColumns().add(firstNameCol);
IndexedCell cell = VirtualFlowTestUtils.getCell(treeTableView, 0, 0);
assertEquals("Root", cell.getText());
treeTableView.setShowRoot(false);
assertEquals("Child 1", cell.getText());
}
@Test public void test_rt31471() {
installChildren();
TreeTableColumn<String,String> firstNameCol = new TreeTableColumn<>("First Name");
firstNameCol.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue()));
treeTableView.getColumns().add(firstNameCol);
IndexedCell cell = VirtualFlowTestUtils.getCell(treeTableView, 0);
assertEquals("Root", cell.getItem());
treeTableView.setFixedCellSize(50);
VirtualFlowTestUtils.getVirtualFlow(treeTableView).requestLayout();
Toolkit.getToolkit().firePulse();
assertEquals("Root", cell.getItem());
assertEquals(50, cell.getHeight(), 0.00);
}
@Test public void test_rt30466() {
final Node graphic1 = new Circle(6.75, Color.RED);
final Node graphic2 = new Circle(6.75, Color.GREEN);
installChildren();
TreeTableColumn<String,String> firstNameCol = new TreeTableColumn<>("First Name");
firstNameCol.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue()));
treeTableView.getColumns().add(firstNameCol);
TreeTableRow cell = (TreeTableRow) VirtualFlowTestUtils.getCell(treeTableView, 0);
assertEquals("Root", cell.getItem());
root.setGraphic(graphic1);
cell = (TreeTableRow) VirtualFlowTestUtils.getCell(treeTableView, 0);
boolean matchGraphic1 = false;
boolean matchGraphic2 = false;
for (Node n : cell.getChildrenUnmodifiable()) {
if (n == graphic1) {
matchGraphic1 = true;
}
if (n == graphic2) {
matchGraphic2 = true;
}
}
assertTrue(matchGraphic1);
assertFalse(matchGraphic2);
root.setGraphic(graphic2);
cell = (TreeTableRow) VirtualFlowTestUtils.getCell(treeTableView, 0);
matchGraphic1 = false;
matchGraphic2 = false;
for (Node n : cell.getChildrenUnmodifiable()) {
if (n == graphic1) {
matchGraphic1 = true;
}
if (n == graphic2) {
matchGraphic2 = true;
}
}
assertFalse(matchGraphic1);
assertTrue(matchGraphic2);
}
private int rt_31200_count = 0;
@Test public void test_rt_31200_tableCell() {
rt_31200_count = 0;
installChildren();
TreeTableColumn<String,String> firstNameCol = new TreeTableColumn<>("First Name");
firstNameCol.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue()));
treeTableView.getColumns().add(firstNameCol);
firstNameCol.setCellFactory(new Callback<TreeTableColumn<String, String>, TreeTableCell<String, String>>() {
@Override
public TreeTableCell<String, String> call(TreeTableColumn<String, String> param) {
return new TreeTableCellShim<String, String>() {
ImageView view = new ImageView();
{
setGraphic(view);
}
;
@Override
public void updateItem(String item, boolean empty) {
if (getItem() == null ? item == null : getItem().equals(item)) {
rt_31200_count++;
}
super.updateItem(item, empty);
if (item == null || empty) {
view.setImage(null);
setText(null);
} else {
setText(item);
}
}
};
}
});
StageLoader sl = new StageLoader(treeTableView);
assertTrue(rt_31200_count > 0);
assertTrue(rt_31200_count < 20);
sl.getStage().setHeight(250);
Toolkit.getToolkit().firePulse();
sl.getStage().setHeight(50);
Toolkit.getToolkit().firePulse();
assertTrue(rt_31200_count > 0);
assertTrue(rt_31200_count < 20);
sl.dispose();
}
@Test public void test_rt_31200_tableRow() {
rt_31200_count = 0;
installChildren();
TreeTableColumn<String,String> firstNameCol = new TreeTableColumn<>("First Name");
firstNameCol.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue()));
treeTableView.getColumns().add(firstNameCol);
treeTableView.setRowFactory(new Callback<TreeTableView<String>, TreeTableRow<String>>() {
@Override
public TreeTableRow<String> call(TreeTableView<String> param) {
return new TreeTableRowShim<String>() {
ImageView view = new ImageView();
{
setGraphic(view);
}
;
@Override
public void updateItem(String item, boolean empty) {
if (getItem() == null ? item == null : getItem().equals(item)) {
rt_31200_count++;
}
super.updateItem(item, empty);
if (item == null || empty) {
view.setImage(null);
setText(null);
} else {
setText(item.toString());
}
}
};
}
});
StageLoader sl = new StageLoader(treeTableView);
assertEquals(21, rt_31200_count);
sl.getStage().setHeight(250);
Toolkit.getToolkit().firePulse();
sl.getStage().setHeight(50);
Toolkit.getToolkit().firePulse();
assertEquals(21, rt_31200_count);
sl.dispose();
}
@Test public void test_rt_31727() {
installChildren();
treeTableView.setEditable(true);
TreeTableColumn firstNameCol = new TreeTableColumn("First Name");
firstNameCol.setCellValueFactory(param -> new ReadOnlyStringWrapper("TEST"));
firstNameCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
firstNameCol.setEditable(true);
treeTableView.getColumns().add(firstNameCol);
treeTableView.setEditable(true);
firstNameCol.setEditable(true);
treeTableView.edit(0, firstNameCol);
TreeTablePosition editingCell = treeTableView.getEditingCell();
assertNotNull(editingCell);
assertEquals(0, editingCell.getRow());
assertEquals(0, editingCell.getColumn());
assertEquals(firstNameCol, editingCell.getTableColumn());
assertEquals(treeTableView, editingCell.getTreeTableView());
treeTableView.edit(-1, null);
editingCell = treeTableView.getEditingCell();
assertNull(editingCell);
}
@Test public void test_rt_21517() {
installChildren();
TreeTableColumn<String, String> col = new TreeTableColumn<String, String>("column");
col.setSortType(ASCENDING);
col.setCellValueFactory(param -> new ReadOnlyObjectWrapper<String>(param.getValue().getValue()));
treeTableView.getColumns().add(col);
assertEquals(0, sm.getSelectedCells().size());
assertEquals(0, sm.getSelectedItems().size());
assertEquals(0, sm.getSelectedIndices().size());
sm.select(3);
assertTrue(sm.isSelected(3));
assertEquals(3, sm.getSelectedIndex());
assertEquals(1, sm.getSelectedIndices().size());
assertTrue(sm.getSelectedIndices().contains(3));
assertEquals(child3, sm.getSelectedItem());
assertEquals(1, sm.getSelectedItems().size());
assertTrue(sm.getSelectedItems().contains(child3));
TreeTableRow rootRow = (TreeTableRow) VirtualFlowTestUtils.getCell(treeTableView, 0);
assertFalse(rootRow.isSelected());
TreeTableRow child3Row = (TreeTableRow) VirtualFlowTestUtils.getCell(treeTableView, 3);
assertTrue(child3Row.isSelected());
treeTableView.getSortOrder().add(col);
assertTrue(sm.isSelected(3));
assertEquals(3, sm.getSelectedIndex());
assertEquals(1, sm.getSelectedIndices().size());
assertTrue(sm.getSelectedIndices().contains(3));
assertEquals(child3, sm.getSelectedItem());
assertEquals(1, sm.getSelectedItems().size());
assertTrue(sm.getSelectedItems().contains(child3));
rootRow = (TreeTableRow) VirtualFlowTestUtils.getCell(treeTableView, 0);
assertFalse(rootRow.isSelected());
child3Row = (TreeTableRow) VirtualFlowTestUtils.getCell(treeTableView, 3);
assertTrue(child3Row.isSelected());
col.setSortType(TreeTableColumn.SortType.DESCENDING);
assertTrue(debug(), sm.isSelected(1));
assertEquals(1, sm.getSelectedIndex());
assertEquals(1, sm.getSelectedIndices().size());
assertTrue(sm.getSelectedIndices().contains(1));
assertEquals(child3, sm.getSelectedItem());
assertEquals(1, sm.getSelectedItems().size());
assertTrue(sm.getSelectedItems().contains(child3));
rootRow = (TreeTableRow) VirtualFlowTestUtils.getCell(treeTableView, 0);
assertFalse(rootRow.isSelected());
child3Row = (TreeTableRow) VirtualFlowTestUtils.getCell(treeTableView, 1);
assertTrue(child3Row.isSelected());
}
@Test public void test_rt_30484_treeTableCell() {
installChildren();
TreeTableColumn<String, String> col = new TreeTableColumn<String, String>("column");
col.setSortType(ASCENDING);
col.setCellValueFactory(param -> new ReadOnlyObjectWrapper<String>(param.getValue().getValue()));
treeTableView.getColumns().add(col);
col.setCellFactory(new Callback<TreeTableColumn<String, String>, TreeTableCell<String, String>>() {
@Override
public TreeTableCell<String, String> call(TreeTableColumn<String, String> param) {
return new TreeTableCellShim<String, String>() {
Rectangle graphic = new Rectangle(10, 10, Color.RED);
{ setGraphic(graphic); };
@Override public void updateItem(String item, boolean empty) {
super.updateItem(item, empty);
if (item == null || empty) {
graphic.setVisible(false);
setText(null);
} else {
graphic.setVisible(true);
setText(item);
}
}
};
}
});
VirtualFlowTestUtils.assertGraphicIsVisible(treeTableView, 0, 0);
VirtualFlowTestUtils.assertGraphicIsVisible(treeTableView, 1, 0);
VirtualFlowTestUtils.assertGraphicIsVisible(treeTableView, 2, 0);
VirtualFlowTestUtils.assertGraphicIsVisible(treeTableView, 3, 0);
VirtualFlowTestUtils.assertGraphicIsNotVisible(treeTableView, 4, 0);
VirtualFlowTestUtils.assertGraphicIsNotVisible(treeTableView, 5, 0);
}
@Test public void test_rt_30484_treeTableRow() {
installChildren();
TreeTableColumn<String, String> col = new TreeTableColumn<String, String>("column");
col.setSortType(ASCENDING);
col.setCellValueFactory(param -> new ReadOnlyObjectWrapper<String>(param.getValue().getValue()));
treeTableView.getColumns().add(col);
treeTableView.setRowFactory(new Callback<TreeTableView<String>, TreeTableRow<String>>() {
@Override public TreeTableRow<String> call(TreeTableView<String> param) {
return new TreeTableRowShim<String>() {
Rectangle graphic = new Rectangle(10, 10, Color.RED);
{ setGraphic(graphic); };
@Override public void updateItem(String item, boolean empty) {
super.updateItem(item, empty);
if (item == null || empty) {
graphic.setVisible(false);
setText(null);
} else {
graphic.setVisible(true);
setText(item.toString());
}
}
};
}
});
VirtualFlowTestUtils.assertGraphicIsVisible(treeTableView, 0);
VirtualFlowTestUtils.assertGraphicIsVisible(treeTableView, 1);
VirtualFlowTestUtils.assertGraphicIsVisible(treeTableView, 2);
VirtualFlowTestUtils.assertGraphicIsVisible(treeTableView, 3);
VirtualFlowTestUtils.assertGraphicIsNotVisible(treeTableView, 4);
VirtualFlowTestUtils.assertGraphicIsNotVisible(treeTableView, 5);
}
private int rt_31015_count = 0;
@Test public void test_rt_31015() {
installChildren();
root.getChildren().clear();
treeTableView.setEditable(true);
TreeTableColumn<String, String> col = new TreeTableColumn<String, String>("column");
col.setCellValueFactory(param -> new ReadOnlyObjectWrapper<String>(param.getValue().getValue()));
treeTableView.getColumns().add(col);
Callback<TreeTableColumn<String,String>, TreeTableCell<String, String>> cellFactory = new Callback<TreeTableColumn<String,String>, TreeTableCell<String, String>>() {
public TreeTableCell<String, String> call(TreeTableColumn<String, String> p) {
return new TreeTableCell<String, String>() {
@Override public void cancelEdit() {
super.cancelEdit();
rt_31015_count++;
}
};
}
};
col.setCellFactory(cellFactory);
StageLoader sl = new StageLoader(treeTableView);
assertEquals(0, rt_31015_count);
treeTableView.edit(0, col);
assertEquals(0, rt_31015_count);
treeTableView.edit(-1, null);
assertEquals(1, rt_31015_count);
sl.dispose();
}
@Test public void test_rt_30688() {
installChildren();
root.getChildren().clear();
treeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
TreeTableColumn<String, String> col = new TreeTableColumn<>("column");
col.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue()));
treeTableView.getColumns().add(col);
StageLoader sl = new StageLoader(treeTableView);
assertEquals(TreeTableViewShim.get_contentWidth(treeTableView),
TableColumnBaseShim.getWidth(col), 0.0);
sl.dispose();
}
private int rt_29650_start_count = 0;
private int rt_29650_commit_count = 0;
private int rt_29650_cancel_count = 0;
@Test public void test_rt_29650() {
installChildren();
treeTableView.setEditable(true);
TreeTableColumn<String, String> col = new TreeTableColumn<>("column");
Callback<TreeTableColumn<String, String>, TreeTableCell<String, String>> factory = TextFieldTreeTableCell.forTreeTableColumn();
col.setCellFactory(factory);
col.setCellValueFactory(param -> param.getValue().valueProperty());
treeTableView.getColumns().add(col);
col.setOnEditStart(t -> {
rt_29650_start_count++;
});
col.addEventHandler(TreeTableColumn.editCommitEvent(), t -> {
rt_29650_commit_count++;
});
col.setOnEditCancel(t -> {
rt_29650_cancel_count++;
});
StageLoader sl = new StageLoader(treeTableView);
treeTableView.edit(0, col);
Toolkit.getToolkit().firePulse();
TreeTableCell rootCell = (TreeTableCell) VirtualFlowTestUtils.getCell(treeTableView, 0, 0);
TextField textField = (TextField) rootCell.getGraphic();
textField.setText("Testing!");
KeyEventFirer keyboard = new KeyEventFirer(textField);
keyboard.doKeyPress(KeyCode.ENTER);
assertEquals("Testing!", treeTableView.getTreeItem(0).getValue());
assertEquals(1, rt_29650_start_count);
assertEquals(1, rt_29650_commit_count);
assertEquals(0, rt_29650_cancel_count);
sl.dispose();
}
private int rt_29849_start_count = 0;
@Test public void test_rt_29849() {
installChildren();
treeTableView.setEditable(true);
TreeTableColumn<String, String> col = new TreeTableColumn<>("column");
col.setEditable(true);
col.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue()));
treeTableView.getColumns().add(col);
col.setOnEditStart(t -> {
rt_29849_start_count++;
});
StageLoader sl = new StageLoader(treeTableView);
Callback<TreeTableColumn<String, String>, TreeTableCell<String, String>> factory = TextFieldTreeTableCell.forTreeTableColumn();
col.setCellFactory(factory);
Toolkit.getToolkit().firePulse();
treeTableView.edit(0, col);
assertEquals(1, rt_29849_start_count);
sl.dispose();
}
@Test public void test_rt_34327() {
Comparator nonGenericComparator = treeTableView.getComparator();
Comparator<TreeItem<String>> genericComparator = treeTableView.getComparator();
assertNull(nonGenericComparator);
assertNull(genericComparator);
TreeTableColumn<String, String> col = new TreeTableColumn<>("column");
col.setEditable(true);
col.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue()));
treeTableView.getColumns().add(col);
installChildren();
treeTableView.getSortOrder().add(col);
nonGenericComparator = treeTableView.getComparator();
genericComparator = treeTableView.getComparator();
assertNotNull(nonGenericComparator);
assertNotNull(genericComparator);
try {
nonGenericComparator.compare("abc", "def");
fail("This should not work!");
} catch (ClassCastException e) {
}
try {
Object string1 = "abc";
Object string2 = "def";
genericComparator.compare((TreeItem<String>)string1, (TreeItem<String>)string2);
fail("This should not work!");
} catch (ClassCastException e) {
}
}
@Test public void test_rt26718() {
treeTableView.setRoot(new TreeItem("Root"));
treeTableView.getRoot().setExpanded(true);
for (int i = 0; i < 4; i++) {
TreeItem parent = new TreeItem("item - " + i);
treeTableView.getRoot().getChildren().add(parent);
for (int j = 0; j < 4; j++) {
TreeItem child = new TreeItem("item - " + i + " " + j);
parent.getChildren().add(child);
}
}
treeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
final TreeItem item0 = treeTableView.getTreeItem(1);
final TreeItem item1 = treeTableView.getTreeItem(2);
assertEquals("item - 0", item0.getValue());
assertEquals("item - 1", item1.getValue());
item0.setExpanded(true);
item1.setExpanded(true);
Toolkit.getToolkit().firePulse();
treeTableView.getSelectionModel().selectRange(0, 8);
assertEquals(8, treeTableView.getSelectionModel().getSelectedIndices().size());
assertEquals(7, treeTableView.getSelectionModel().getSelectedIndex());
assertEquals(7, treeTableView.getFocusModel().getFocusedIndex());
item0.setExpanded(false);
Toolkit.getToolkit().firePulse();
assertEquals(3, treeTableView.getSelectionModel().getSelectedIndex());
assertEquals(3, treeTableView.getFocusModel().getFocusedIndex());
}
@Test public void test_rt_34493() {
ObservableList<TreeItem<Person>> persons = FXCollections.observableArrayList(
new TreeItem<Person>(new Person("Jacob", "Smith", "jacob.smith@example.com"))
);
TreeTableView<Person> table = new TreeTableView<>();
TreeItem<Person> root = new TreeItem<Person>(new Person("Root", null, null));
root.setExpanded(true);
table.setRoot(root);
table.setShowRoot(false);
root.getChildren().setAll(persons);
TreeTableColumn first = new TreeTableColumn("First Name");
first.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("firstName"));
TreeTableColumn last = new TreeTableColumn("Last Name");
last.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("lastName"));
TreeTableColumn email = new TreeTableColumn("Email");
email.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("email"));
table.getColumns().addAll(first, last, email);
StageLoader sl = new StageLoader(table);
TableColumnBaseHelper.setWidth(last, 400);
assertEquals(400, last.getWidth(), 0.0);
table.getColumns().remove(first);
Toolkit.getToolkit().firePulse();
assertEquals(400, last.getWidth(), 0.0);
sl.dispose();
}
@Test public void test_rt26721_collapseParent_firstRootChild() {
TreeTableView<String> table = new TreeTableView<>();
table.setRoot(new TreeItem("Root"));
table.getRoot().setExpanded(true);
for (int i = 0; i < 4; i++) {
TreeItem parent = new TreeItem("item - " + i);
table.getRoot().getChildren().add(parent);
for (int j = 0; j < 4; j++) {
TreeItem child = new TreeItem("item - " + i + " " + j);
parent.getChildren().add(child);
}
}
table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
final TreeItem<String> item0 = table.getTreeItem(1);
final TreeItem<String> item0child0 = item0.getChildren().get(0);
final TreeItem<String> item1 = table.getTreeItem(2);
assertEquals("item - 0", item0.getValue());
assertEquals("item - 1", item1.getValue());
item0.setExpanded(true);
item1.setExpanded(true);
Toolkit.getToolkit().firePulse();
table.getSelectionModel().select(item0child0);
assertEquals(item0child0, table.getSelectionModel().getSelectedItem());
assertEquals(item0child0, table.getFocusModel().getFocusedItem());
item0.setExpanded(false);
Toolkit.getToolkit().firePulse();
assertEquals(item0, table.getSelectionModel().getSelectedItem());
assertEquals(item0, table.getFocusModel().getFocusedItem());
}
@Test public void test_rt26721_collapseParent_lastRootChild() {
TreeTableView<String> table = new TreeTableView<>();
table.setRoot(new TreeItem("Root"));
table.getRoot().setExpanded(true);
for (int i = 0; i < 4; i++) {
TreeItem parent = new TreeItem("item - " + i);
table.getRoot().getChildren().add(parent);
for (int j = 0; j < 4; j++) {
TreeItem child = new TreeItem("item - " + i + " " + j);
parent.getChildren().add(child);
}
}
table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
final TreeItem<String> item3 = table.getTreeItem(4);
final TreeItem<String> item3child0 = item3.getChildren().get(0);
assertEquals("item - 3", item3.getValue());
assertEquals("item - 3 0", item3child0.getValue());
item3.setExpanded(true);
Toolkit.getToolkit().firePulse();
table.getSelectionModel().select(item3child0);
assertEquals(item3child0, table.getSelectionModel().getSelectedItem());
assertEquals(item3child0, table.getFocusModel().getFocusedItem());
item3.setExpanded(false);
Toolkit.getToolkit().firePulse();
assertEquals(item3, table.getSelectionModel().getSelectedItem());
assertEquals(item3, table.getFocusModel().getFocusedItem());
}
@Test public void test_rt26721_collapseGrandParent() {
TreeTableView<String> table = new TreeTableView<>();
table.setRoot(new TreeItem("Root"));
table.getRoot().setExpanded(true);
for (int i = 0; i < 4; i++) {
TreeItem parent = new TreeItem("item - " + i);
table.getRoot().getChildren().add(parent);
for (int j = 0; j < 4; j++) {
TreeItem child = new TreeItem("item - " + i + " " + j);
parent.getChildren().add(child);
}
}
table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
final TreeItem<String> item0 = table.getTreeItem(1);
final TreeItem<String> item0child0 = item0.getChildren().get(0);
final TreeItem<String> item1 = table.getTreeItem(2);
assertEquals("item - 0", item0.getValue());
assertEquals("item - 1", item1.getValue());
item0.setExpanded(true);
item1.setExpanded(true);
Toolkit.getToolkit().firePulse();
table.getSelectionModel().select(item0child0);
assertEquals(item0child0, table.getSelectionModel().getSelectedItem());
assertEquals(item0child0, table.getFocusModel().getFocusedItem());
table.getRoot().setExpanded(false);
Toolkit.getToolkit().firePulse();
assertEquals(table.getRoot(), table.getSelectionModel().getSelectedItem());
assertEquals(table.getRoot(), table.getFocusModel().getFocusedItem());
}
@Test public void test_rt_34685_directEditCall_cellSelectionMode() {
test_rt_34685_commitCount = 0;
test_rt_34685(false, true);
}
@Test public void test_rt_34685_directEditCall_rowSelectionMode() {
test_rt_34685_commitCount = 0;
test_rt_34685(false, false);
}
@Test public void test_rt_34685_mouseDoubleClick_cellSelectionMode() {
test_rt_34685_commitCount = 0;
test_rt_34685(true, true);
}
@Test public void test_rt_34685_mouseDoubleClick_rowSelectionMode() {
test_rt_34685_commitCount = 0;
test_rt_34685(true, false);
}
private int test_rt_34685_commitCount = 0;
private void test_rt_34685(boolean useMouseToInitiateEdit, boolean cellSelectionModeEnabled) {
assertEquals(0, test_rt_34685_commitCount);
Person person1;
ObservableList<TreeItem<Person>> persons = FXCollections.observableArrayList(
new TreeItem<>(person1 = new Person("John", "Smith", "john.smith@example.com"))
);
TreeTableView<Person> table = new TreeTableView<>();
table.getSelectionModel().setCellSelectionEnabled(cellSelectionModeEnabled);
table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
table.setEditable(true);
TreeItem<Person> root = new TreeItem<Person>(new Person("Root", null, null));
root.setExpanded(true);
table.setRoot(root);
table.setShowRoot(false);
root.getChildren().setAll(persons);
TreeTableColumn first = new TreeTableColumn("First Name");
first.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("firstName"));
first.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
EventHandler<TreeTableColumn.CellEditEvent<Person, String>> onEditCommit = first.getOnEditCommit();
first.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<Person, String>>() {
@Override public void handle(TreeTableColumn.CellEditEvent<Person, String> event) {
test_rt_34685_commitCount++;
onEditCommit.handle(event);
}
});
table.getColumns().addAll(first);
VirtualFlowTestUtils.BLOCK_STAGE_LOADER_DISPOSE = true;
TreeTableCell cell = (TreeTableCell) VirtualFlowTestUtils.getCell(table, 0, 0);
VirtualFlowTestUtils.BLOCK_STAGE_LOADER_DISPOSE = false;
assertTrue(cell.getSkin() instanceof TreeTableCellSkin);
assertNull(cell.getGraphic());
assertEquals("John", cell.getText());
assertEquals("John", person1.getFirstName());
if (useMouseToInitiateEdit) {
MouseEventFirer mouse = new MouseEventFirer(cell);
mouse.fireMousePressAndRelease(2, 10, 10);
mouse.dispose();
} else {
table.edit(0,first);
}
Toolkit.getToolkit().firePulse();
assertNotNull(cell.getGraphic());
assertTrue(cell.getGraphic() instanceof TextField);
TextField textField = (TextField) cell.getGraphic();
assertEquals("John", textField.getText());
textField.setText("Andrew");
textField.requestFocus();
Toolkit.getToolkit().firePulse();
KeyEventFirer keyboard = new KeyEventFirer(textField);
keyboard.doKeyPress(KeyCode.ENTER);
VirtualFlowTestUtils.getVirtualFlow(table).requestLayout();
Toolkit.getToolkit().firePulse();
VirtualFlowTestUtils.assertTableCellTextEquals(table, 0, 0, "Andrew");
assertEquals("Andrew", cell.getText());
assertEquals("Andrew", person1.getFirstName());
assertEquals(1, test_rt_34685_commitCount);
}
@Test public void test_rt34694() {
TreeItem treeNode = new TreeItem("Controls");
treeNode.getChildren().addAll(
new TreeItem("Button"),
new TreeItem("ButtonBar"),
new TreeItem("LinkBar"),
new TreeItem("LinkButton"),
new TreeItem("PopUpButton"),
new TreeItem("ToggleButtonBar")
);
final TreeTableView<String> table = new TreeTableView<>();
table.setRoot(treeNode);
treeNode.setExpanded(true);
table.getSelectionModel().select(0);
assertTrue(table.getSelectionModel().isSelected(0));
assertTrue(table.getFocusModel().isFocused(0));
treeNode.getChildren().clear();
treeNode.getChildren().addAll(
new TreeItem("Button1"),
new TreeItem("ButtonBar1"),
new TreeItem("LinkBar1"),
new TreeItem("LinkButton1"),
new TreeItem("PopUpButton1"),
new TreeItem("ToggleButtonBar1")
);
Toolkit.getToolkit().firePulse();
assertTrue(table.getSelectionModel().isSelected(0));
assertTrue(table.getFocusModel().isFocused(0));
}
private int test_rt_35213_eventCount = 0;
@Test public void test_rt35213() {
final TreeTableView<String> view = new TreeTableView<>();
TreeItem<String> root = new TreeItem<>("Boss");
view.setRoot(root);
TreeItem<String> group1 = new TreeItem<>("Group 1");
TreeItem<String> group2 = new TreeItem<>("Group 2");
TreeItem<String> group3 = new TreeItem<>("Group 3");
root.getChildren().addAll(group1, group2, group3);
TreeItem<String> employee1 = new TreeItem<>("Employee 1");
TreeItem<String> employee2 = new TreeItem<>("Employee 2");
group2.getChildren().addAll(employee1, employee2);
TreeTableColumn<String, String> nameColumn = new TreeTableColumn<>("Name");
nameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue()));
view.getColumns().add(nameColumn);
view.expandedItemCountProperty().addListener((observableValue, oldCount, newCount) -> {
if (test_rt_35213_eventCount == 0) {
assertEquals(4, newCount);
assertEquals("Boss", view.getTreeItem(0).getValue());
assertEquals("Group 1", view.getTreeItem(1).getValue());
assertEquals("Group 2", view.getTreeItem(2).getValue());
assertEquals("Group 3", view.getTreeItem(3).getValue());
} else if (test_rt_35213_eventCount == 1) {
assertEquals(6, newCount);
assertEquals("Boss", view.getTreeItem(0).getValue());
assertEquals("Group 1", view.getTreeItem(1).getValue());
assertEquals("Group 2", view.getTreeItem(2).getValue());
assertEquals("Employee 1", view.getTreeItem(3).getValue());
assertEquals("Employee 2", view.getTreeItem(4).getValue());
assertEquals("Group 3", view.getTreeItem(5).getValue());
} else if (test_rt_35213_eventCount == 2) {
assertEquals(4, newCount);
assertEquals("Boss", view.getTreeItem(0).getValue());
assertEquals("Group 1", view.getTreeItem(1).getValue());
assertEquals("Group 2", view.getTreeItem(2).getValue());
assertEquals("Group 3", view.getTreeItem(3).getValue());
}
test_rt_35213_eventCount++;
});
StageLoader sl = new StageLoader(view);
root.setExpanded(true);
Toolkit.getToolkit().firePulse();
group2.setExpanded(true);
Toolkit.getToolkit().firePulse();
group2.setExpanded(false);
Toolkit.getToolkit().firePulse();
sl.dispose();
}
@Test public void test_rt23245_itemIsInTree() {
final TreeTableView<String> view = new TreeTableView<String>();
final List<TreeItem<String>> items = new ArrayList<>();
for (int i = 0; i < 10; i++) {
final TreeItem<String> item = new TreeItem<String>("Item" + i);
item.setExpanded(true);
items.add(item);
}
for (int i = 0; i < 9; i++) {
items.get(i).getChildren().add(items.get(i + 1));
}
view.setRoot(items.get(0));
for (int i = 0; i < 10; i++) {
assertEquals(0, view.getTreeItemLevel(items.get(i)));
assertEquals(items.get(i), view.getRoot());
assertEquals(items.get(i), view.getTreeItem(0));
if (i < 9) {
view.setRoot(items.get(i + 1));
}
}
}
@Test public void test_rt23245_itemIsNotInTree_noRootNode() {
final TreeView<String> view = new TreeView<String>();
final List<TreeItem<String>> items = new ArrayList<>();
for (int i = 0; i < 10; i++) {
final TreeItem<String> item = new TreeItem<String>("Item" + i);
item.setExpanded(true);
items.add(item);
}
for (int i = 0; i < 9; i++) {
items.get(i).getChildren().add(items.get(i + 1));
}
for (int i = 0; i < 10; i++) {
assertEquals(i, view.getTreeItemLevel(items.get(i)));
assertNull(view.getTreeItem(i));
}
}
@Test public void test_rt23245_itemIsNotInTree_withUnrelatedRootNode() {
final TreeView<String> view = new TreeView<String>();
final List<TreeItem<String>> items = new ArrayList<>();
for (int i = 0; i < 10; i++) {
final TreeItem<String> item = new TreeItem<String>("Item" + i);
item.setExpanded(true);
items.add(item);
}
for (int i = 0; i < 9; i++) {
items.get(i).getChildren().add(items.get(i + 1));
}
view.setRoot(new TreeItem("Unrelated root node"));
for (int i = 0; i < 10; i++) {
assertEquals(i, view.getTreeItemLevel(items.get(i)));
assertNull(view.getTreeItem(i + 1));
}
}
@Test public void test_rt35039_setRoot() {
TreeItem aabbaa = new TreeItem("aabbaa");
TreeItem bbc = new TreeItem("bbc");
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().setAll(aabbaa, bbc);
final TreeTableView<String> treeView = new TreeTableView<>();
treeView.setRoot(root);
StageLoader sl = new StageLoader(treeView);
assertNull(treeView.getSelectionModel().getSelectedItem());
treeView.getSelectionModel().select(2);
assertEquals("bbc", treeView.getSelectionModel().getSelectedItem().getValue());
treeView.setRoot(root);
assertEquals("bbc", treeView.getSelectionModel().getSelectedItem().getValue());
sl.dispose();
}
@Test public void test_rt35039_resetRootChildren() {
TreeItem aabbaa = new TreeItem("aabbaa");
TreeItem bbc = new TreeItem("bbc");
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().setAll(aabbaa, bbc);
final TreeTableView<String> treeView = new TreeTableView<>();
treeView.setRoot(root);
StageLoader sl = new StageLoader(treeView);
assertNull(treeView.getSelectionModel().getSelectedItem());
treeView.getSelectionModel().select(2);
assertEquals("bbc", treeView.getSelectionModel().getSelectedItem().getValue());
root.getChildren().setAll(aabbaa, bbc);
assertEquals("bbc", treeView.getSelectionModel().getSelectedItem().getValue());
sl.dispose();
}
@Test public void test_rt35763() {
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
TreeItem aaa = new TreeItem("aaa");
TreeItem bbb = new TreeItem("bbb");
root.getChildren().setAll(bbb, aaa);
final TreeTableView<String> treeView = new TreeTableView<>();
TreeTableColumn<String, String> col = new TreeTableColumn<>("Column");
col.setCellValueFactory(param -> param.getValue().valueProperty());
treeView.getColumns().add(col);
treeView.setRoot(root);
assertEquals(root, treeView.getTreeItem(0));
assertEquals(bbb, treeView.getTreeItem(1));
assertEquals(aaa,treeView.getTreeItem(2));
treeView.getSortOrder().setAll(col);
assertEquals(1, treeView.getSortOrder().size());
assertEquals(col, treeView.getSortOrder().get(0));
Toolkit.getToolkit().firePulse();
assertEquals(root, treeView.getTreeItem(0));
assertEquals(bbb, treeView.getTreeItem(2));
assertEquals(aaa,treeView.getTreeItem(1));
TreeItem<String> root2 = new TreeItem<>("Root");
root2.setExpanded(true);
TreeItem ccc = new TreeItem("ccc");
TreeItem ddd = new TreeItem("ddd");
root2.getChildren().setAll(ddd, ccc);
treeView.setRoot(root2);
assertEquals(root2, treeView.getTreeItem(0));
assertEquals(ddd, treeView.getTreeItem(1));
assertEquals(ccc,treeView.getTreeItem(2));
assertTrue(treeView.getSortOrder().isEmpty());
}
@Test
public void test_rt35857_selectLast_retainAllSelected() {
TreeTableView<String> treeView = new TreeTableView<String>(createTreeItem());
treeView.getSelectionModel().select(treeView.getRoot().getChildren().size());
assert_rt35857(treeView.getRoot().getChildren(), treeView.getSelectionModel(), true);
}
@Test
public void test_rt35857_selectLast_removeAllSelected() {
TreeTableView<String> treeView = new TreeTableView<String>(createTreeItem());
treeView.getSelectionModel().select(treeView.getRoot().getChildren().size());
assert_rt35857(treeView.getRoot().getChildren(), treeView.getSelectionModel(), false);
}
@Test
public void test_rt35857_selectFirst_retainAllSelected() {
TreeTableView<String> treeView = new TreeTableView<String>(createTreeItem());
treeView.getSelectionModel().select(1);
assert_rt35857(treeView.getRoot().getChildren(), treeView.getSelectionModel(), true);
}
protected TreeItem<String> createTreeItem() {
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().setAll(new TreeItem("A"), new TreeItem("B"), new TreeItem("C"));
return root;
}
protected <T> void assert_rt35857(ObservableList<T> items, MultipleSelectionModel<T> sm, boolean retain) {
T selectedItem = sm.getSelectedItem();
ObservableList<T> expected;
if (retain) {
expected = FXCollections.observableArrayList(selectedItem);
items.retainAll(sm.getSelectedItems());
} else {
expected = FXCollections.observableArrayList(items);
expected.remove(selectedItem);
items.removeAll(sm.getSelectedItems());
}
String modified = (retain ? " retainAll " : " removeAll ") + " selectedItems ";
assertEquals("expected list after" + modified, expected, items);
}
@Test public void test_rt35857() {
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
TreeItem a = new TreeItem("A");
TreeItem b = new TreeItem("B");
TreeItem c = new TreeItem("C");
root.getChildren().setAll(a, b, c);
final TreeTableView<String> treeTableView = new TreeTableView<String>(root);
treeTableView.getSelectionModel().select(1);
ObservableList<TreeItem<String>> selectedItems = treeTableView.getSelectionModel().getSelectedItems();
assertEquals(1, selectedItems.size());
assertEquals("A", selectedItems.get(0).getValue());
root.getChildren().removeAll(selectedItems);
assertEquals(2, root.getChildren().size());
assertEquals("B", root.getChildren().get(0).getValue());
assertEquals("C", root.getChildren().get(1).getValue());
}
private int rt36452_instanceCount = 0;
@Test public void test_rt36452() {
TreeTableColumn<String, String> myColumn = new TreeTableColumn<String,String>();
myColumn.setCellValueFactory((item)->(new ReadOnlyObjectWrapper<>(item.getValue().getValue())));
myColumn.setCellFactory(column -> new TreeTableCell<String, String>() {
{
rt36452_instanceCount++;
}
});
TreeTableView<String> ttv = new TreeTableView<>();
ttv.setShowRoot(false);
ttv.getColumns().add(myColumn);
TreeItem<String> treeRootItem = new TreeItem<>("root");
treeRootItem.setExpanded(true);
for (int i = 0; i < 100; i++) {
treeRootItem.getChildren().add(new TreeItem<>("Child: " + i));
}
ttv.setRoot(treeRootItem);
ttv.setFixedCellSize(25);
StackPane root = new StackPane();
root.getChildren().add(ttv);
StageLoader sl = new StageLoader(root);
final int cellCountAtStart = rt36452_instanceCount;
for (int i = 0; i < 100; i++) {
ttv.scrollTo(i);
Toolkit.getToolkit().firePulse();
}
assertEquals(cellCountAtStart + 14, rt36452_instanceCount);
sl.dispose();
}
@Test public void test_rt25679_rowSelection() {
test_rt25679(true);
}
@Test public void test_rt25679_cellSelection() {
test_rt25679(false);
}
private void test_rt25679(boolean rowSelection) {
Button focusBtn = new Button("Focus here");
TreeItem<String> root = new TreeItem<>("Root");
root.getChildren().setAll(new TreeItem("a"), new TreeItem("b"));
root.setExpanded(true);
final TreeTableView<String> treeView = new TreeTableView<>(root);
TreeTableColumn<String, String> tableColumn = new TreeTableColumn<>();
tableColumn.setCellValueFactory(rowValue -> new SimpleStringProperty(rowValue.getValue().getValue()));
treeView.getColumns().add(tableColumn);
TreeTableView.TreeTableViewSelectionModel<String> sm = treeView.getSelectionModel();
sm.setCellSelectionEnabled(! rowSelection);
VBox vbox = new VBox(focusBtn, treeView);
StageLoader sl = new StageLoader(vbox);
sl.getStage().requestFocus();
focusBtn.requestFocus();
Toolkit.getToolkit().firePulse();
assertEquals(sl.getStage().getScene().getFocusOwner(), focusBtn);
assertTrue(focusBtn.isFocused());
assertEquals(-1, sm.getSelectedIndex());
assertNull(sm.getSelectedItem());
treeView.requestFocus();
assertEquals(sl.getStage().getScene().getFocusOwner(), treeView);
assertTrue(treeView.isFocused());
if (rowSelection) {
assertEquals(0, sm.getSelectedIndices().size());
assertNull(sm.getSelectedItem());
assertFalse(sm.isSelected(0));
assertEquals(0, sm.getSelectedCells().size());
} else {
assertFalse(sm.isSelected(0, tableColumn));
assertEquals(0, sm.getSelectedCells().size());
}
sl.dispose();
}
@Test public void test_rt36885() {
test_rt36885(false);
}
@Test public void test_rt36885_addChildAfterSelection() {
test_rt36885(true);
}
private void test_rt36885(boolean addChildToAAfterSelection) {
TreeItem<String> root = new TreeItem<>("Root");
TreeItem<String> a = new TreeItem<>("a");
TreeItem<String> a1 = new TreeItem<>("a1");
TreeItem<String> b = new TreeItem<>("b");
TreeItem<String> b1 = new TreeItem<>("b1");
TreeItem<String> b2 = new TreeItem<>("b2");
root.setExpanded(true);
root.getChildren().setAll(a, b);
a.setExpanded(false);
if (!addChildToAAfterSelection) {
a.getChildren().add(a1);
}
b.setExpanded(true);
b.getChildren().addAll(b1, b2);
final TreeTableView<String> treeView = new TreeTableView<>(root);
TreeTableColumn<String, String> tableColumn = new TreeTableColumn<>();
tableColumn.setCellValueFactory(rowValue -> new SimpleStringProperty(rowValue.getValue().getValue()));
treeView.getColumns().add(tableColumn);
TreeTableView.TreeTableViewSelectionModel<String> sm = treeView.getSelectionModel();
FocusModel<TreeItem<String>> fm = treeView.getFocusModel();
sm.select(b1);
assertEquals(3, sm.getSelectedIndex());
assertEquals(b1, sm.getSelectedItem());
assertEquals(3, fm.getFocusedIndex());
assertEquals(b1, fm.getFocusedItem());
if (addChildToAAfterSelection) {
a.getChildren().add(a1);
}
a.setExpanded(true);
assertEquals(4, sm.getSelectedIndex());
assertEquals(b1, sm.getSelectedItem());
assertEquals(4, fm.getFocusedIndex());
assertEquals(b1, fm.getFocusedItem());
}
private int rt_37061_index_counter = 0;
private int rt_37061_item_counter = 0;
@Test public void test_rt_37061() {
TreeItem<Integer> root = new TreeItem<>(0);
root.setExpanded(true);
TreeTableView<Integer> tv = new TreeTableView<>();
tv.setRoot(root);
tv.getSelectionModel().select(0);
tv.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
rt_37061_index_counter++;
});
tv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
rt_37061_item_counter++;
});
tv.getRoot().getChildren().add(new TreeItem("1"));
assertEquals(0, rt_37061_index_counter);
assertEquals(0, rt_37061_item_counter);
}
@Test public void test_rt_37054_noScroll() {
test_rt_37054(false);
}
@Test public void test_rt_37054_scroll() {
test_rt_37054(true);
}
private void test_rt_37054(boolean scroll) {
ObjectProperty<Integer> offset = new SimpleObjectProperty<Integer>(0);
TreeItem<Integer> root = new TreeItem<>(0);
root.setExpanded(true);
for (int i = 1; i <= 50; i++) {
root.getChildren().add(new TreeItem<>(i));
}
final TreeTableColumn<Integer, Integer> column = new TreeTableColumn<>("Column");
final TreeTableView<Integer> table = new TreeTableView<>(root);
table.getColumns().add( column );
column.setPrefWidth( 150 );
column.setCellValueFactory( cdf -> new ObjectBinding<Integer>() {
{ super.bind( offset ); }
@Override protected Integer computeValue() {
return cdf.getValue().getValue() + offset.get();
}
});
StackPane stack = new StackPane();
stack.getChildren().add(table);
StageLoader sl = new StageLoader(stack);
int index = scroll ? 0 : 25;
if (scroll) {
table.scrollTo(index);
Toolkit.getToolkit().firePulse();
}
TreeTableCell cell = (TreeTableCell) VirtualFlowTestUtils.getCell(table, index + 3, 0);
final int initialValue = (Integer) cell.getItem();
offset.setValue(offset.get() + 1);
Toolkit.getToolkit().firePulse();
final int incrementedValue = (Integer) cell.getItem();
assertEquals(initialValue + 1, incrementedValue);
sl.dispose();
}
private int rt_37395_index_addCount = 0;
private int rt_37395_index_removeCount = 0;
private int rt_37395_index_permutationCount = 0;
private int rt_37395_item_addCount = 0;
private int rt_37395_item_removeCount = 0;
private int rt_37395_item_permutationCount = 0;
@Test public void test_rt_37395() {
TreeItem<String> root = new TreeItem<>();
TreeItem<String> two = new TreeItem<>("two");
two.getChildren().add(new TreeItem<>("childOne"));
two.getChildren().add(new TreeItem<>("childTwo"));
root.getChildren().add(new TreeItem<>("one"));
root.getChildren().add(two);
root.getChildren().add(new TreeItem<>("three"));
TreeTableColumn<String, String> nameColumn = new TreeTableColumn<>("name");
nameColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper(param.getValue().getValue()));
nameColumn.setPrefWidth(200);
TreeTableView<String> table = new TreeTableView<>();
table.setShowRoot(false);
table.setRoot(root);
table.getColumns().addAll(nameColumn);
TreeTableView.TreeTableViewSelectionModel sm = table.getSelectionModel();
sm.getSelectedIndices().addListener(new ListChangeListener<Integer>() {
@Override public void onChanged(Change<? extends Integer> c) {
while (c.next()) {
if (c.wasRemoved()) {
c.getRemoved().forEach(item -> {
if (item == null) {
fail("Removed index should never be null");
} else {
rt_37395_index_removeCount++;
}
});
}
if (c.wasAdded()) {
c.getAddedSubList().forEach(item -> {
rt_37395_index_addCount++;
});
}
if (c.wasPermutated()) {
rt_37395_index_permutationCount++;
}
}
}
});
sm.getSelectedItems().addListener(new ListChangeListener<TreeItem<String>>() {
@Override public void onChanged(Change<? extends TreeItem<String>> c) {
while (c.next()) {
if (c.wasRemoved()) {
c.getRemoved().forEach(item -> {
if (item == null) {
fail("Removed item should never be null");
} else {
rt_37395_item_removeCount++;
}
});
}
if (c.wasAdded()) {
c.getAddedSubList().forEach(item -> {
rt_37395_item_addCount++;
});
}
if (c.wasPermutated()) {
rt_37395_item_permutationCount++;
}
}
}
});
assertEquals(0, rt_37395_index_removeCount);
assertEquals(0, rt_37395_index_addCount);
assertEquals(0, rt_37395_index_permutationCount);
assertEquals(0, rt_37395_item_removeCount);
assertEquals(0, rt_37395_item_addCount);
assertEquals(0, rt_37395_item_permutationCount);
StageLoader sl = new StageLoader(table);
sm.select(2);
assertEquals(0, rt_37395_index_removeCount);
assertEquals(1, rt_37395_index_addCount);
assertEquals(0, rt_37395_index_permutationCount);
assertEquals(0, rt_37395_item_removeCount);
assertEquals(1, rt_37395_item_addCount);
assertEquals(0, rt_37395_item_permutationCount);
two.setExpanded(true);
assertEquals(1, rt_37395_index_removeCount);
assertEquals(2, rt_37395_index_addCount);
assertEquals(0, rt_37395_index_permutationCount);
assertEquals(0, rt_37395_item_removeCount);
assertEquals(1, rt_37395_item_addCount);
assertEquals(0, rt_37395_item_permutationCount);
two.setExpanded(false);
assertEquals(2, rt_37395_index_removeCount);
assertEquals(3, rt_37395_index_addCount);
assertEquals(0, rt_37395_index_permutationCount);
assertEquals(0, rt_37395_item_removeCount);
assertEquals(1, rt_37395_item_addCount);
assertEquals(0, rt_37395_item_permutationCount);
sl.dispose();
}
@Test public void test_rt_37429() {
TreeItem<String> root = new TreeItem<>();
TreeItem<String> two = new TreeItem<>("two");
two.getChildren().add(new TreeItem<>("childOne"));
two.getChildren().add(new TreeItem<>("childTwo"));
two.setExpanded(true);
root.getChildren().add(new TreeItem<>("one"));
root.getChildren().add(two);
root.getChildren().add(new TreeItem<>("three"));
TreeTableColumn<String, String> nameColumn = new TreeTableColumn<>("name");
nameColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper(param.getValue().getValue()));
nameColumn.setPrefWidth(200);
TreeTableView<String> table = new TreeTableView<>();
table.setShowRoot(false);
table.setRoot(root);
table.getColumns().addAll(nameColumn);
table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<TreeItem<String>>) c -> {
while (c.next()) {
if(c.wasRemoved()) {
c.getRemoved().forEach(item -> {});
}
if (c.wasAdded()) {
c.getAddedSubList();
}
}
});
StageLoader sl = new StageLoader(table);
ControlTestUtils.runWithExceptionHandler(() -> {
table.getSelectionModel().select(0);
table.getSortOrder().add(nameColumn);
});
sl.dispose();
}
private int rt_37429_items_change_count = 0;
private int rt_37429_cells_change_count = 0;
@Test public void test_rt_37429_sortEventsShouldNotFireExtraChangeEvents() {
TreeItem<String> root = new TreeItem<>();
root.getChildren().add(new TreeItem<>("a"));
root.getChildren().add(new TreeItem<>("c"));
root.getChildren().add(new TreeItem<>("b"));
TreeTableColumn<String, String> nameColumn = new TreeTableColumn<>("name");
nameColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper(param.getValue().getValue()));
nameColumn.setPrefWidth(200);
TreeTableView<String> table = new TreeTableView<>();
table.setShowRoot(false);
table.setRoot(root);
table.getColumns().addAll(nameColumn);
table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<TreeItem<String>>) c -> {
while (c.next()) {
rt_37429_items_change_count++;
}
});
table.getSelectionModel().getSelectedCells().addListener((ListChangeListener<TreeTablePosition<String, ?>>) c -> {
while (c.next()) {
rt_37429_cells_change_count++;
}
});
StageLoader sl = new StageLoader(table);
assertEquals(0, rt_37429_items_change_count);
assertEquals(0, rt_37429_cells_change_count);
table.getSelectionModel().select(0);
assertEquals(1, rt_37429_items_change_count);
assertEquals(1, rt_37429_cells_change_count);
table.getSortOrder().add(nameColumn);
assertEquals(1, rt_37429_items_change_count);
assertEquals(1, rt_37429_cells_change_count);
nameColumn.setSortType(TreeTableColumn.SortType.DESCENDING);
assertEquals(1, rt_37429_items_change_count);
assertEquals(2, rt_37429_cells_change_count);
nameColumn.setSortType(TreeTableColumn.SortType.ASCENDING);
assertEquals(1, rt_37429_items_change_count);
assertEquals(3, rt_37429_cells_change_count);
sl.dispose();
}
private int rt_37538_count = 0;
@Test public void test_rt_37538_noCNextCall() {
test_rt_37538(false, false);
}
@Test public void test_rt_37538_callCNextOnce() {
test_rt_37538(true, false);
}
@Test public void test_rt_37538_callCNextInLoop() {
test_rt_37538(false, true);
}
private void test_rt_37538(boolean callCNextOnce, boolean callCNextInLoop) {
TreeItem<Integer> root = new TreeItem<>(0);
root.setExpanded(true);
for (int i = 1; i <= 50; i++) {
root.getChildren().add(new TreeItem<>(i));
}
final TreeTableColumn<Integer, Integer> column = new TreeTableColumn<>("Column");
column.setCellValueFactory( cdf -> new ReadOnlyObjectWrapper<Integer>(cdf.getValue().getValue()));
final TreeTableView<Integer> table = new TreeTableView<>(root);
table.getColumns().add( column );
table.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends TreeItem<Integer>> c) -> {
if (callCNextOnce) {
c.next();
} else if (callCNextInLoop) {
while (c.next()) {
}
}
if (rt_37538_count >= 1) {
Thread.dumpStack();
fail("This method should only be called once");
}
rt_37538_count++;
});
StageLoader sl = new StageLoader(table);
assertEquals(0, rt_37538_count);
table.getSelectionModel().select(0);
assertEquals(1, rt_37538_count);
sl.dispose();
}
@Test public void test_rt_37593() {
TreeItem<String> root = new TreeItem<>();
TreeItem<String> one = new TreeItem<>("one");
root.getChildren().add(one);
TreeItem<String> two = new TreeItem<>("two");
two.getChildren().add(new TreeItem<>("childOne"));
two.getChildren().add(new TreeItem<>("childTwo"));
root.getChildren().add(two);
root.getChildren().add(new TreeItem<>("three"));
TreeTableColumn<String, String> nameColumn = new TreeTableColumn<>("name");
nameColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper(param.getValue().getValue()));
treeTableView.setShowRoot(false);
treeTableView.setRoot(root);
treeTableView.getColumns().addAll(nameColumn);
treeTableView.getSortOrder().add(nameColumn);
nameColumn.setSortType(TreeTableColumn.SortType.DESCENDING);
sm.select(one);
assertTrue(sm.isSelected(2));
assertEquals(one, sm.getSelectedItem());
two.setExpanded(true);
assertEquals(one, sm.getSelectedItem());
assertTrue(debug(), sm.isSelected(4));
VirtualFlowTestUtils.clickOnRow(treeTableView, 4, true);
assertEquals(one, sm.getSelectedItem());
assertTrue(debug(), sm.isSelected(4));
}
@Test public void test_rt_35395_testCell_fixedCellSize() {
test_rt_35395(true, true);
}
@Test public void test_rt_35395_testCell_notFixedCellSize() {
test_rt_35395(true, false);
}
@Ignore("Fix not yet developed for TreeTableView")
@Test public void test_rt_35395_testRow_fixedCellSize() {
test_rt_35395(false, true);
}
@Ignore("Fix not yet developed for TreeTableView")
@Test public void test_rt_35395_testRow_notFixedCellSize() {
test_rt_35395(false, false);
}
private int rt_35395_counter;
private void test_rt_35395(boolean testCell, boolean useFixedCellSize) {
rt_35395_counter = 0;
TreeItem<String> root = new TreeItem<>("green");
root.setExpanded(true);
for (int i = 0; i < 20; i++) {
root.getChildren().addAll(new TreeItem<>("red"), new TreeItem<>("green"), new TreeItem<>("blue"), new TreeItem<>("purple"));
}
TreeTableView<String> treeTableView = new TreeTableView<>(root);
if (useFixedCellSize) {
treeTableView.setFixedCellSize(24);
}
treeTableView.setRowFactory(tv -> new TreeTableRowShim<String>() {
@Override public void updateItem(String color, boolean empty) {
rt_35395_counter += testCell ? 0 : 1;
super.updateItem(color, empty);
}
});
TreeTableColumn<String,String> column = new TreeTableColumn<>("Column");
column.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue()));
column.setCellFactory(tv -> new TreeTableCellShim<String,String>() {
@Override public void updateItem(String color, boolean empty) {
rt_35395_counter += testCell ? 1 : 0;
super.updateItem(color, empty);
setText(null);
if (empty) {
setGraphic(null);
} else {
Rectangle rect = new Rectangle(16, 16);
rect.setStyle("-fx-fill: " + color);
setGraphic(rect);
}
}
});
treeTableView.getColumns().addAll(column);
StageLoader sl = new StageLoader(treeTableView);
Platform.runLater(() -> {
rt_35395_counter = 0;
root.getChildren().set(10, new TreeItem<>("yellow"));
Platform.runLater(() -> {
Toolkit.getToolkit().firePulse();
assertEquals(1, rt_35395_counter);
rt_35395_counter = 0;
root.getChildren().set(30, new TreeItem<>("yellow"));
Platform.runLater(() -> {
Toolkit.getToolkit().firePulse();
assertTrue(rt_35395_counter < 15);
rt_35395_counter = 0;
treeTableView.scrollTo(5);
Platform.runLater(() -> {
Toolkit.getToolkit().firePulse();
assertTrue(rt_35395_counter > 0);
assertTrue(rt_35395_counter < 18);
rt_35395_counter = 0;
treeTableView.scrollTo(55);
Platform.runLater(() -> {
Toolkit.getToolkit().firePulse();
assertTrue(rt_35395_counter > 0);
assertTrue(rt_35395_counter < 30);
sl.dispose();
});
});
});
});
});
}
@Test public void test_rt_37632() {
final TreeItem<String> rootOne = new TreeItem<>("Root 1");
final TreeItem<String> rootTwo = new TreeItem<>("Root 2");
TreeTableColumn<String,String> tableColumn = new TreeTableColumn("column");
tableColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getValue()));
final TreeTableView<String> treeTableView = new TreeTableView<>();
treeTableView.getColumns().addAll(tableColumn);
MultipleSelectionModel<TreeItem<String>> sm = treeTableView.getSelectionModel();
treeTableView.setRoot(rootOne);
treeTableView.getSelectionModel().selectFirst();
assertEquals(0, sm.getSelectedIndex());
assertEquals(rootOne, sm.getSelectedItem());
assertEquals(1, sm.getSelectedIndices().size());
assertEquals(0, (int) sm.getSelectedIndices().get(0));
assertEquals(1, sm.getSelectedItems().size());
assertEquals(rootOne, sm.getSelectedItems().get(0));
treeTableView.setRoot(rootTwo);
assertEquals(-1, sm.getSelectedIndex());
assertNull(sm.getSelectedItem());
assertEquals(0, sm.getSelectedIndices().size());
assertEquals(0, sm.getSelectedItems().size());
}
private TreeTableView<Person> test_rt_38464_createControl() {
ObservableList<TreeItem<Person>> persons = FXCollections.observableArrayList(
new TreeItem<>(new Person("Jacob", "Smith", "jacob.smith@example.com")),
new TreeItem<>(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
new TreeItem<>(new Person("Ethan", "Williams", "ethan.williams@example.com")),
new TreeItem<>(new Person("Emma", "Jones", "emma.jones@example.com")),
new TreeItem<>(new Person("Michael", "Brown", "michael.brown@example.com")));
TreeTableView<Person> table = new TreeTableView<>();
table.setShowRoot(false);
TreeItem<Person> root = new TreeItem<>(new Person("Root", null, null));
root.setExpanded(true);
root.getChildren().setAll(persons);
table.setRoot(root);
TreeTableColumn firstNameCol = new TreeTableColumn("First Name");
firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("firstName"));
TreeTableColumn lastNameCol = new TreeTableColumn("Last Name");
lastNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("lastName"));
table.getColumns().addAll(firstNameCol, lastNameCol);
return table;
}
@Test public void test_rt_38464_rowSelection_selectFirstRowOnly() {
TreeTableView<Person> table = test_rt_38464_createControl();
TreeTableView.TreeTableViewSelectionModel<Person> sm = table.getSelectionModel();
sm.setCellSelectionEnabled(false);
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.select(0);
assertTrue(sm.isSelected(0));
assertTrue(sm.isSelected(0, table.getColumns().get(0)));
assertTrue(sm.isSelected(0, table.getColumns().get(1)));
assertEquals(1, sm.getSelectedIndices().size());
assertEquals(1, sm.getSelectedItems().size());
assertEquals(1, sm.getSelectedCells().size());
}
@Test public void test_rt_38464_rowSelection_selectFirstRowAndThenCallNoOpMethods() {
TreeTableView<Person> table = test_rt_38464_createControl();
TreeTableView.TreeTableViewSelectionModel<Person> sm = table.getSelectionModel();
sm.setCellSelectionEnabled(false);
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.select(0);
sm.select(0);
sm.select(0, table.getColumns().get(0));
sm.select(0, table.getColumns().get(1));
assertTrue(sm.isSelected(0));
assertTrue(sm.isSelected(0, table.getColumns().get(0)));
assertTrue(sm.isSelected(0, table.getColumns().get(1)));
assertEquals(1, sm.getSelectedIndices().size());
assertEquals(1, sm.getSelectedItems().size());
assertEquals(1, sm.getSelectedCells().size());
}
@Test public void test_rt_38464_cellSelection_selectFirstRowOnly() {
TreeTableView<Person> table = test_rt_38464_createControl();
TreeTableView.TreeTableViewSelectionModel<Person> sm = table.getSelectionModel();
sm.setCellSelectionEnabled(true);
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.select(0);
assertTrue(sm.isSelected(0));
assertTrue(sm.isSelected(0, table.getColumns().get(0)));
assertTrue(sm.isSelected(0, table.getColumns().get(1)));
assertEquals(1, sm.getSelectedIndices().size());
assertEquals(1, sm.getSelectedItems().size());
assertEquals(2, sm.getSelectedCells().size());
}
@Test public void test_rt_38464_cellSelection_selectFirstRowAndThenCallNoOpMethods() {
TreeTableView<Person> table = test_rt_38464_createControl();
TreeTableView.TreeTableViewSelectionModel<Person> sm = table.getSelectionModel();
sm.setCellSelectionEnabled(true);
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.select(0);
sm.select(0, table.getColumns().get(0));
sm.select(0, table.getColumns().get(1));
assertTrue(sm.isSelected(0));
assertTrue(sm.isSelected(0, table.getColumns().get(0)));
assertTrue(sm.isSelected(0, table.getColumns().get(1)));
assertEquals(1, sm.getSelectedIndices().size());
assertEquals(1, sm.getSelectedItems().size());
assertEquals(2, sm.getSelectedCells().size());
}
@Test public void test_rt38464_selectCellMultipleTimes() {
TreeTableView<Person> table = test_rt_38464_createControl();
TreeTableView.TreeTableViewSelectionModel<Person> sm = table.getSelectionModel();
sm.setCellSelectionEnabled(true);
sm.setSelectionMode(SelectionMode.MULTIPLE);
assertEquals(0, sm.getSelectedCells().size());
assertEquals(0, sm.getSelectedItems().size());
assertEquals(0, sm.getSelectedIndices().size());
sm.select(0, table.getColumns().get(0));
assertEquals(1, sm.getSelectedCells().size());
assertEquals(1, sm.getSelectedItems().size());
assertEquals(1, sm.getSelectedIndices().size());
sm.select(0, table.getColumns().get(0));
assertEquals(1, sm.getSelectedCells().size());
assertEquals(1, sm.getSelectedItems().size());
assertEquals(1, sm.getSelectedIndices().size());
}
@Test public void test_rt38464_selectCellThenRow() {
TreeTableView<Person> table = test_rt_38464_createControl();
TreeTableView.TreeTableViewSelectionModel<Person> sm = table.getSelectionModel();
sm.setCellSelectionEnabled(true);
sm.setSelectionMode(SelectionMode.MULTIPLE);
assertEquals(0, sm.getSelectedCells().size());
assertEquals(0, sm.getSelectedItems().size());
assertEquals(0, sm.getSelectedIndices().size());
sm.select(0, table.getColumns().get(0));
assertEquals(1, sm.getSelectedCells().size());
assertEquals(1, sm.getSelectedItems().size());
assertEquals(1, sm.getSelectedIndices().size());
sm.select(0);
assertEquals(2, sm.getSelectedCells().size());
assertEquals(1, sm.getSelectedItems().size());
assertEquals(1, sm.getSelectedIndices().size());
}
@Test public void test_rt38464_selectRowThenCell() {
TreeTableView<Person> table = test_rt_38464_createControl();
TreeTableView.TreeTableViewSelectionModel<Person> sm = table.getSelectionModel();
sm.setCellSelectionEnabled(true);
sm.setSelectionMode(SelectionMode.MULTIPLE);
assertEquals(0, sm.getSelectedCells().size());
assertEquals(0, sm.getSelectedItems().size());
assertEquals(0, sm.getSelectedIndices().size());
sm.select(0);
assertEquals(2, sm.getSelectedCells().size());
assertEquals(1, sm.getSelectedItems().size());
assertEquals(1, sm.getSelectedIndices().size());
sm.select(0, table.getColumns().get(0));
assertEquals(2, sm.getSelectedCells().size());
assertEquals(1, sm.getSelectedItems().size());
assertEquals(1, sm.getSelectedIndices().size());
}
@Test public void test_rt38464_selectTests_cellSelection_singleSelection_selectsOneRow() {
test_rt38464_selectTests(true, true, true);
}
@Test public void test_rt38464_selectTests_cellSelection_singleSelection_selectsTwoRows() {
test_rt38464_selectTests(true, true, false);
}
@Test public void test_rt38464_selectTests_cellSelection_multipleSelection_selectsOneRow() {
test_rt38464_selectTests(true, false, true);
}
@Test public void test_rt38464_selectTests_cellSelection_multipleSelection_selectsTwoRows() {
test_rt38464_selectTests(true, false, false);
}
@Test public void test_rt38464_selectTests_rowSelection_singleSelection_selectsOneRow() {
test_rt38464_selectTests(false, true, true);
}
@Test public void test_rt38464_selectTests_rowSelection_singleSelection_selectsTwoRows() {
test_rt38464_selectTests(false, true, false);
}
@Test public void test_rt38464_selectTests_rowSelection_multipleSelection_selectsOneRow() {
test_rt38464_selectTests(false, false, true);
}
@Test public void test_rt38464_selectTests_rowSelection_multipleSelection_selectsTwoRows() {
test_rt38464_selectTests(false, false, false);
}
private void test_rt38464_selectTests(boolean cellSelection, boolean singleSelection, boolean selectsOneRow) {
TreeTableView<Person> table = test_rt_38464_createControl();
TreeTableView.TreeTableViewSelectionModel<Person> sm = table.getSelectionModel();
sm.setCellSelectionEnabled(cellSelection);
sm.setSelectionMode(singleSelection ? SelectionMode.SINGLE : SelectionMode.MULTIPLE);
assertEquals(0, sm.getSelectedCells().size());
assertEquals(0, sm.getSelectedItems().size());
assertEquals(0, sm.getSelectedIndices().size());
if (selectsOneRow) {
sm.select(0);
} else {
sm.selectIndices(0, 1);
}
final int expectedCells = singleSelection ? 1 :
selectsOneRow && cellSelection ? 2 :
selectsOneRow && !cellSelection ? 1 :
!selectsOneRow && cellSelection ? 4 :
2;
final int expectedItems = singleSelection ? 1 :
selectsOneRow ? 1 : 2;
assertEquals(expectedCells, sm.getSelectedCells().size());
assertEquals(expectedItems, sm.getSelectedItems().size());
assertEquals(expectedItems, sm.getSelectedIndices().size());
for (TreeTablePosition<?,?> tp : sm.getSelectedCells()) {
if (cellSelection) {
assertNotNull(tp.getTableColumn());
} else {
assertNull(tp.getTableColumn());
}
}
}
@Test public void test_rt_37853_replaceRoot() {
test_rt_37853(true);
}
@Test public void test_rt_37853_replaceRootChildren() {
test_rt_37853(false);
}
private int rt_37853_cancelCount;
private int rt_37853_commitCount;
public void test_rt_37853(boolean replaceRoot) {
TreeTableColumn<String,String> first = new TreeTableColumn<>("first");
first.setEditable(true);
first.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
treeTableView.getColumns().add(first);
treeTableView.setEditable(true);
treeTableView.setRoot(new TreeItem<>("Root"));
treeTableView.getRoot().setExpanded(true);
for (int i = 0; i < 10; i++) {
treeTableView.getRoot().getChildren().add(new TreeItem<>("" + i));
}
StageLoader sl = new StageLoader(treeTableView);
first.setOnEditCancel(editEvent -> rt_37853_cancelCount++);
first.setOnEditCommit(editEvent -> rt_37853_commitCount++);
assertEquals(0, rt_37853_cancelCount);
assertEquals(0, rt_37853_commitCount);
treeTableView.edit(1, first);
assertNotNull(treeTableView.getEditingCell());
if (replaceRoot) {
treeTableView.setRoot(new TreeItem<>("New Root"));
} else {
treeTableView.getRoot().getChildren().clear();
for (int i = 0; i < 10; i++) {
treeTableView.getRoot().getChildren().add(new TreeItem<>("new item " + i));
}
}
assertEquals(1, rt_37853_cancelCount);
assertEquals(0, rt_37853_commitCount);
sl.dispose();
}
private final Supplier<TreeTableColumn<Person,String>> columnCallable = () -> {
TreeTableColumn<Person,String> column = new TreeTableColumn<>("Last Name");
column.setCellValueFactory(new TreeItemPropertyValueFactory<Person,String>("lastName"));
return column;
};
private TreeTableColumn<Person, String> test_rt_38892_firstNameCol;
private TreeTableColumn<Person, String> test_rt_38892_lastNameCol;
private TreeTableView<Person> init_test_rt_38892() {
ObservableList<TreeItem<Person>> persons = FXCollections.observableArrayList(
new TreeItem<>(new Person("Jacob", "Smith", "jacob.smith@example.com")),
new TreeItem<>(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
new TreeItem<>(new Person("Ethan", "Williams", "ethan.williams@example.com")),
new TreeItem<>(new Person("Emma", "Jones", "emma.jones@example.com")),
new TreeItem<>(new Person("Michael", "Brown", "michael.brown@example.com")));
TreeTableView<Person> table = new TreeTableView<>();
table.setShowRoot(false);
table.getSelectionModel().setCellSelectionEnabled(true);
table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
TreeItem<Person> root = new TreeItem<>(new Person("Root", null, null));
root.setExpanded(true);
root.getChildren().setAll(persons);
table.setRoot(root);
test_rt_38892_firstNameCol = new TreeTableColumn<>("First Name");
test_rt_38892_firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("firstName"));
test_rt_38892_lastNameCol = columnCallable.get();
table.getColumns().addAll(test_rt_38892_firstNameCol, test_rt_38892_lastNameCol);
return table;
}
@Test public void test_rt_38892_focusMovesToLeftWhenPossible() {
TreeTableView<Person> table = init_test_rt_38892();
TreeTableView.TreeTableViewFocusModel<Person> fm = table.getFocusModel();
fm.focus(0, test_rt_38892_lastNameCol);
assertEquals(0, fm.getFocusedIndex());
assertEquals(0, fm.getFocusedCell().getRow());
assertEquals(test_rt_38892_lastNameCol, fm.getFocusedCell().getTableColumn());
assertEquals(1, fm.getFocusedCell().getColumn());
table.getColumns().remove(1);
table.getColumns().add(columnCallable.get());
assertEquals(0, fm.getFocusedIndex());
assertEquals(0, fm.getFocusedCell().getRow());
assertEquals(test_rt_38892_firstNameCol, fm.getFocusedCell().getTableColumn());
assertEquals(0, fm.getFocusedCell().getColumn());
}
@Test public void test_rt_38892_removeLeftMostColumn() {
TreeTableView<Person> table = init_test_rt_38892();
TreeTableView.TreeTableViewFocusModel<Person> fm = table.getFocusModel();
fm.focus(0, test_rt_38892_firstNameCol);
assertEquals(0, fm.getFocusedIndex());
assertEquals(0, fm.getFocusedCell().getRow());
assertEquals(test_rt_38892_firstNameCol, fm.getFocusedCell().getTableColumn());
assertEquals(0, fm.getFocusedCell().getColumn());
table.getColumns().remove(0);
TreeTableColumn<Person,String> newColumn = columnCallable.get();
table.getColumns().add(0, newColumn);
assertEquals(0, fm.getFocusedIndex());
assertEquals(0, fm.getFocusedCell().getRow());
assertEquals(test_rt_38892_lastNameCol, fm.getFocusedCell().getTableColumn());
assertEquals(0, fm.getFocusedCell().getColumn());
}
@Test public void test_rt_38892_removeSelectionFromCellsInRemovedColumn() {
TreeTableView<Person> table = init_test_rt_38892();
TreeTableView.TreeTableViewSelectionModel sm = table.getSelectionModel();
sm.select(0, test_rt_38892_firstNameCol);
sm.select(1, test_rt_38892_lastNameCol);
sm.select(2, test_rt_38892_firstNameCol);
sm.select(3, test_rt_38892_lastNameCol);
sm.select(4, test_rt_38892_firstNameCol);
assertEquals(5, sm.getSelectedCells().size());
table.getColumns().remove(1);
assertEquals(3, sm.getSelectedCells().size());
assertTrue(sm.isSelected(0, test_rt_38892_firstNameCol));
assertFalse(sm.isSelected(1, test_rt_38892_lastNameCol));
assertTrue(sm.isSelected(2, test_rt_38892_firstNameCol));
assertFalse(sm.isSelected(3, test_rt_38892_lastNameCol));
assertTrue(sm.isSelected(4, test_rt_38892_firstNameCol));
}
@Test public void test_rt_38787_remove_b() {
test_rt_38787("a", 0, 1);
}
@Test public void test_rt_38787_remove_b_c() {
test_rt_38787("a", 0, 1, 2);
}
@Test public void test_rt_38787_remove_c_d() {
test_rt_38787("b", 1, 2, 3);
}
@Test public void test_rt_38787_remove_a() {
test_rt_38787("b", 0, 0);
}
private void test_rt_38787(String expectedItem, int expectedIndex, int... indicesToRemove) {
TreeItem<String> a, b, c, d;
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(
a = new TreeItem<String>("a"),
b = new TreeItem<String>("b"),
c = new TreeItem<String>("c"),
d = new TreeItem<String>("d")
);
TreeTableView<String> stringTreeTableView = new TreeTableView<>(root);
stringTreeTableView.setShowRoot(false);
TreeTableColumn<String,String> column = new TreeTableColumn<>("Column");
column.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
stringTreeTableView.getColumns().add(column);
MultipleSelectionModel<TreeItem<String>> sm = stringTreeTableView.getSelectionModel();
sm.select(b);
assertEquals(1, sm.getSelectedIndex());
assertEquals(1, (int)sm.getSelectedIndices().get(0));
assertEquals(b, sm.getSelectedItem());
assertEquals(b, sm.getSelectedItems().get(0));
assertFalse(sm.isSelected(0));
assertTrue(sm.isSelected(1));
assertFalse(sm.isSelected(2));
List<TreeItem<String>> itemsToRemove = new ArrayList<>(indicesToRemove.length);
for (int index : indicesToRemove) {
itemsToRemove.add(root.getChildren().get(index));
}
root.getChildren().removeAll(itemsToRemove);
assertEquals(expectedIndex, sm.getSelectedIndex());
assertEquals(expectedIndex, (int)sm.getSelectedIndices().get(0));
assertEquals(expectedItem, sm.getSelectedItem().getValue());
assertEquals(expectedItem, sm.getSelectedItems().get(0).getValue());
}
private int rt_38341_indices_count = 0;
private int rt_38341_items_count = 0;
@Test public void test_rt_38341() {
Callback<Integer, TreeItem<String>> callback = number -> {
final TreeItem<String> root = new TreeItem<>("Root " + number);
final TreeItem<String> child = new TreeItem<>("Child " + number);
root.getChildren().add(child);
return root;
};
final TreeItem<String> root = new TreeItem<String>();
root.setExpanded(true);
root.getChildren().addAll(callback.call(1), callback.call(2));
final TreeTableView<String> treeTableView = new TreeTableView<>(root);
treeTableView.setShowRoot(false);
TreeTableColumn<String,String> column = new TreeTableColumn<>("Column");
column.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
treeTableView.getColumns().add(column);
MultipleSelectionModel<TreeItem<String>> sm = treeTableView.getSelectionModel();
sm.getSelectedIndices().addListener((ListChangeListener<Integer>) c -> rt_38341_indices_count++);
sm.getSelectedItems().addListener((ListChangeListener<TreeItem<String>>) c -> rt_38341_items_count++);
assertEquals(0, rt_38341_indices_count);
assertEquals(0, rt_38341_items_count);
root.getChildren().get(0).setExpanded(true);
sm.select(1);
assertEquals(1, sm.getSelectedIndex());
assertEquals(1, sm.getSelectedIndices().size());
assertEquals(1, (int)sm.getSelectedIndices().get(0));
assertEquals(1, sm.getSelectedItems().size());
assertEquals("Child 1", sm.getSelectedItem().getValue());
assertEquals("Child 1", sm.getSelectedItems().get(0).getValue());
assertEquals(1, rt_38341_indices_count);
assertEquals(1, rt_38341_items_count);
root.getChildren().get(0).getChildren().remove(0);
assertEquals(0, sm.getSelectedIndex());
assertEquals(1, sm.getSelectedIndices().size());
assertEquals(0, (int)sm.getSelectedIndices().get(0));
assertEquals(1, sm.getSelectedItems().size());
assertEquals("Root 1", sm.getSelectedItem().getValue());
assertEquals("Root 1", sm.getSelectedItems().get(0).getValue());
assertEquals(2, rt_38341_indices_count);
assertEquals(2, rt_38341_items_count);
}
private int rt_38943_index_count = 0;
private int rt_38943_item_count = 0;
@Test public void test_rt_38943() {
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(
new TreeItem<>("a"),
new TreeItem<>("b"),
new TreeItem<>("c"),
new TreeItem<>("d")
);
TreeTableView<String> stringTreeTableView = new TreeTableView<>(root);
stringTreeTableView.setShowRoot(false);
TreeTableColumn<String,String> column = new TreeTableColumn<>("Column");
column.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
stringTreeTableView.getColumns().add(column);
MultipleSelectionModel<TreeItem<String>> sm = stringTreeTableView.getSelectionModel();
sm.selectedIndexProperty().addListener((observable, oldValue, newValue) -> rt_38943_index_count++);
sm.selectedItemProperty().addListener((observable, oldValue, newValue) -> rt_38943_item_count++);
assertEquals(-1, sm.getSelectedIndex());
assertNull(sm.getSelectedItem());
assertEquals(0, rt_38943_index_count);
assertEquals(0, rt_38943_item_count);
sm.select(0);
assertEquals(0, sm.getSelectedIndex());
assertEquals("a", sm.getSelectedItem().getValue());
assertEquals(1, rt_38943_index_count);
assertEquals(1, rt_38943_item_count);
sm.clearSelection(0);
assertEquals(-1, sm.getSelectedIndex());
assertNull(sm.getSelectedItem());
assertEquals(2, rt_38943_index_count);
assertEquals(2, rt_38943_item_count);
}
@Test public void test_rt_38884() {
final TreeItem<String> root = new TreeItem<>("Root");
final TreeItem<String> foo = new TreeItem<>("foo");
TreeTableView<String> treeView = new TreeTableView<>(root);
treeView.setShowRoot(false);
root.setExpanded(true);
treeView.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends TreeItem<String>> c) -> {
while (c.next()) {
if (c.wasRemoved()) {
assertTrue(c.getRemovedSize() > 0);
List<? extends TreeItem<String>> removed = c.getRemoved();
TreeItem<String> removedItem = null;
try {
removedItem = removed.get(0);
} catch (Exception e) {
fail();
}
assertEquals(foo, removedItem);
}
}
});
root.getChildren().add(foo);
treeView.getSelectionModel().select(0);
root.getChildren().clear();
}
private int rt_37360_add_count = 0;
private int rt_37360_remove_count = 0;
@Test public void test_rt_37360() {
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(
new TreeItem<>("a"),
new TreeItem<>("b")
);
TreeTableView<String> stringTreeTableView = new TreeTableView<>(root);
stringTreeTableView.setShowRoot(false);
TreeTableColumn<String,String> column = new TreeTableColumn<>("Column");
column.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
stringTreeTableView.getColumns().add(column);
MultipleSelectionModel<TreeItem<String>> sm = stringTreeTableView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.getSelectedItems().addListener((ListChangeListener<TreeItem<String>>) c -> {
while (c.next()) {
if (c.wasAdded()) {
rt_37360_add_count += c.getAddedSize();
}
if (c.wasRemoved()) {
rt_37360_remove_count += c.getRemovedSize();
}
}
});
assertEquals(0, sm.getSelectedItems().size());
assertEquals(0, rt_37360_add_count);
assertEquals(0, rt_37360_remove_count);
sm.select(0);
assertEquals(1, sm.getSelectedItems().size());
assertEquals(1, rt_37360_add_count);
assertEquals(0, rt_37360_remove_count);
sm.select(1);
assertEquals(2, sm.getSelectedItems().size());
assertEquals(2, rt_37360_add_count);
assertEquals(0, rt_37360_remove_count);
sm.clearAndSelect(1);
assertEquals(1, sm.getSelectedItems().size());
assertEquals(2, rt_37360_add_count);
assertEquals(1, rt_37360_remove_count);
}
private int rt_37366_count = 0;
@Test public void test_rt_37366() {
final TreeItem<String> treeItem2 = new TreeItem<>("Item 2");
treeItem2.getChildren().addAll(new TreeItem<>("Item 21"), new TreeItem<>("Item 22"));
final TreeItem<String> root1 = new TreeItem<>("Root Node 1");
TreeItem<String> treeItem1 = new TreeItem<>("Item 1");
root1.getChildren().addAll(treeItem1, treeItem2, new TreeItem<>("Item 3"));
root1.setExpanded(true);
final TreeItem<String> root2 = new TreeItem<>("Root Node 2");
final TreeItem<String> hiddenRoot = new TreeItem<>("Hidden Root Node");
hiddenRoot.getChildren().add(root1);
hiddenRoot.getChildren().add(root2);
final TreeTableView<String> treeView = new TreeTableView<>(hiddenRoot);
treeView.setShowRoot(false);
AtomicInteger step = new AtomicInteger();
MultipleSelectionModel<TreeItem<String>> sm = treeView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.getSelectedItems().addListener((ListChangeListener.Change<? extends TreeItem<String>> c) -> {
switch (step.get()) {
case 0: {
while (c.next()) {
assertFalse(c.wasRemoved());
assertTrue(c.wasAdded());
assertEquals(1, c.getAddedSize());
assertTrue(c.getAddedSubList().contains(treeItem1));
}
break;
}
case 1: {
while (c.next()) {
assertFalse(c.wasRemoved());
assertTrue(c.wasAdded());
assertEquals(1, c.getAddedSize());
assertTrue(c.getAddedSubList().contains(treeItem2));
}
break;
}
case 2: {
boolean wasRemoved = false;
while (c.next()) {
if (c.wasAdded()) {
fail("no addition expected yet");
}
if (c.wasRemoved()) {
assertTrue(c.getRemoved().containsAll(FXCollections.observableArrayList(treeItem1, treeItem2)));
wasRemoved = true;
}
}
if (!wasRemoved) {
fail("Expected a remove operation");
}
step.incrementAndGet();
break;
}
case 3: {
boolean wasAdded = false;
while (c.next()) {
if (c.wasAdded()) {
assertEquals(1, c.getAddedSize());
assertTrue(c.getAddedSubList().contains(root1));
wasAdded = true;
}
if (c.wasRemoved()) {
fail("no removal expected now");
}
}
if (!wasAdded) {
fail("Expected an add operation");
}
break;
}
}
rt_37366_count++;
});
assertEquals(0, rt_37366_count);
step.set(0);
sm.select(1);
assertEquals(1, rt_37366_count);
assertFalse(sm.isSelected(0));
assertTrue(sm.isSelected(1));
assertFalse(sm.isSelected(2));
step.set(1);
sm.select(2);
assertEquals(2, rt_37366_count);
assertFalse(sm.isSelected(0));
assertTrue(sm.isSelected(1));
assertTrue(sm.isSelected(2));
step.set(2);
root1.setExpanded(false);
assertEquals(4, rt_37366_count);
assertTrue(sm.isSelected(0));
assertFalse(sm.isSelected(1));
assertFalse(sm.isSelected(2));
}
@Test public void test_rt_38491() {
TreeItem<String> a;
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(
a = new TreeItem<>("a"),
new TreeItem<>("b")
);
TreeTableView<String> stringTreeView = new TreeTableView<>(root);
stringTreeView.setShowRoot(false);
TreeTableColumn<String,String> column = new TreeTableColumn<>("Column");
column.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
stringTreeView.getColumns().add(column);
TreeTableView.TreeTableViewSelectionModel<String> sm = stringTreeView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
TreeTableViewFocusModel<String> fm = stringTreeView.getFocusModel();
StageLoader sl = new StageLoader(stringTreeView);
assertTrue(sm.isEmpty());
assertEquals(a, fm.getFocusedItem());
assertEquals(0, fm.getFocusedIndex());
sm.select(0, column);
assertTrue(sm.isSelected(0));
assertEquals(a, sm.getSelectedItem());
assertTrue(fm.isFocused(0));
assertEquals(a, fm.getFocusedItem());
assertEquals(0, fm.getFocusedIndex());
assertEquals(0, fm.getFocusedCell().getRow());
assertEquals(column, fm.getFocusedCell().getTableColumn());
TreeTablePosition<String, ?> anchor = TreeTableCellBehavior.getAnchor(stringTreeView, null);
assertNotNull(anchor);
assertTrue(TreeTableCellBehavior.hasNonDefaultAnchor(stringTreeView));
assertEquals(0, anchor.getRow());
root.getChildren().add(0, new TreeItem("z"));
assertFalse(sm.isSelected(0));
assertFalse(fm.isFocused(0));
assertTrue(sm.isSelected(1));
assertEquals(a, sm.getSelectedItem());
assertTrue(fm.isFocused(1));
assertEquals(a, fm.getFocusedItem());
assertEquals(1, fm.getFocusedIndex());
assertEquals(1, fm.getFocusedCell().getRow());
assertEquals(column, fm.getFocusedCell().getTableColumn());
anchor = TreeTableCellBehavior.getAnchor(stringTreeView, null);
assertNotNull(anchor);
assertTrue(TreeTableCellBehavior.hasNonDefaultAnchor(stringTreeView));
assertEquals(1, anchor.getRow());
assertEquals(column, anchor.getTableColumn());
sl.dispose();
}
private final ObservableList<TreeItem<String>> rt_39256_list = FXCollections.observableArrayList();
@Test public void test_rt_39256() {
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(
new TreeItem<>("a"),
new TreeItem<>("b"),
new TreeItem<>("c"),
new TreeItem<>("d")
);
TreeTableView<String> stringTreeTableView = new TreeTableView<>(root);
stringTreeTableView.setShowRoot(false);
TreeTableColumn<String,String> column = new TreeTableColumn<>("Column");
column.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
stringTreeTableView.getColumns().add(column);
MultipleSelectionModel<TreeItem<String>> sm = stringTreeTableView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
Bindings.bindContent(rt_39256_list, sm.getSelectedItems());
assertEquals(0, sm.getSelectedItems().size());
assertEquals(0, rt_39256_list.size());
sm.selectAll();
assertEquals(4, sm.getSelectedItems().size());
assertEquals(4, rt_39256_list.size());
sm.selectAll();
assertEquals(4, sm.getSelectedItems().size());
assertEquals(4, rt_39256_list.size());
sm.selectAll();
assertEquals(4, sm.getSelectedItems().size());
assertEquals(4, rt_39256_list.size());
}
private final ObservableList<TreeItem<String>> rt_39482_list = FXCollections.observableArrayList();
@Test public void test_rt_39482() {
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(
new TreeItem<>("a"),
new TreeItem<>("b"),
new TreeItem<>("c"),
new TreeItem<>("d")
);
TreeTableView<String> stringTreeTableView = new TreeTableView<>(root);
stringTreeTableView.setShowRoot(false);
TreeTableColumn<String,String> column = new TreeTableColumn<>("Column");
column.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
stringTreeTableView.getColumns().add(column);
TreeTableView.TreeTableViewSelectionModel<String> sm = stringTreeTableView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
Bindings.bindContent(rt_39482_list, sm.getSelectedItems());
assertEquals(0, sm.getSelectedItems().size());
assertEquals(0, rt_39482_list.size());
test_rt_39482_selectRow("a", sm, 0, column);
test_rt_39482_selectRow("b", sm, 1, column);
test_rt_39482_selectRow("c", sm, 2, column);
test_rt_39482_selectRow("d", sm, 3, column);
}
private void test_rt_39482_selectRow(String expectedString,
TreeTableView.TreeTableViewSelectionModel<String> sm,
int rowToSelect,
TreeTableColumn<String,String> columnToSelect) {
sm.selectAll();
assertEquals(4, sm.getSelectedCells().size());
assertEquals(4, sm.getSelectedIndices().size());
assertEquals(4, sm.getSelectedItems().size());
assertEquals(4, rt_39482_list.size());
sm.clearAndSelect(rowToSelect, columnToSelect);
assertEquals(1, sm.getSelectedCells().size());
assertEquals(1, sm.getSelectedIndices().size());
assertEquals(1, sm.getSelectedItems().size());
assertEquals(expectedString, sm.getSelectedItem().getValue());
assertEquals(expectedString, rt_39482_list.get(0).getValue());
assertEquals(1, rt_39482_list.size());
}
@Test public void test_rt_39559_useSM_selectAll() {
test_rt_39559(true);
}
@Test public void test_rt_39559_useKeyboard_selectAll() {
test_rt_39559(false);
}
private void test_rt_39559(boolean useSMSelectAll) {
TreeItem<String> a, b;
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(
a = new TreeItem<>("a"),
b = new TreeItem<>("b"),
new TreeItem<>("c"),
new TreeItem<>("d")
);
TreeTableView<String> stringTreeTableView = new TreeTableView<>(root);
stringTreeTableView.setShowRoot(false);
TreeTableColumn<String,String> column = new TreeTableColumn<>("Column");
column.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
stringTreeTableView.getColumns().add(column);
TreeTableView.TreeTableViewSelectionModel<String> sm = stringTreeTableView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
StageLoader sl = new StageLoader(stringTreeTableView);
KeyEventFirer keyboard = new KeyEventFirer(stringTreeTableView);
assertEquals(0, sm.getSelectedItems().size());
sm.clearAndSelect(0);
if (useSMSelectAll) {
sm.selectAll();
} else {
keyboard.doKeyPress(KeyCode.A, KeyModifier.getShortcutKey());
}
assertEquals(4, sm.getSelectedItems().size());
assertEquals(0, ((TreeTablePosition) TreeTableCellBehavior.getAnchor(stringTreeTableView, null)).getRow());
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.SHIFT);
assertEquals(0, ((TreeTablePosition) TreeTableCellBehavior.getAnchor(stringTreeTableView, null)).getRow());
assertEquals(2, sm.getSelectedItems().size());
assertEquals(a, sm.getSelectedItems().get(0));
assertEquals(b, sm.getSelectedItems().get(1));
sl.dispose();
}
@Test public void test_rt_16068_firstElement_selectAndRemoveSameRow() {
test_rt_16068(0, 0, 0);
}
@Test public void test_rt_16068_firstElement_selectRowAndRemoveLaterSibling() {
test_rt_16068(0, 2, 0);
}
@Test public void test_rt_16068_middleElement_selectAndRemoveSameRow() {
test_rt_16068(1, 1, 0);
}
@Test public void test_rt_16068_middleElement_selectRowAndRemoveLaterSibling() {
test_rt_16068(1, 2, 1);
}
@Test public void test_rt_16068_middleElement_selectRowAndRemoveEarlierSibling() {
test_rt_16068(1, 0, 0);
}
@Test public void test_rt_16068_lastElement_selectAndRemoveSameRow() {
test_rt_16068(3, 3, 2);
}
@Test public void test_rt_16068_lastElement_selectRowAndRemoveEarlierSibling() {
test_rt_16068(3, 0, 2);
}
private void test_rt_16068(int indexToSelect, int indexToRemove, int expectedIndex) {
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(
new TreeItem<>("a"),
new TreeItem<>("b"),
new TreeItem<>("c"),
new TreeItem<>("d")
);
TreeTableView<String> stringTreeTableView = new TreeTableView<>(root);
stringTreeTableView.setShowRoot(false);
TreeTableColumn<String,String> column = new TreeTableColumn<>("Column");
column.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
stringTreeTableView.getColumns().add(column);
TreeTableView.TreeTableViewSelectionModel<String> sm = stringTreeTableView.getSelectionModel();
FocusModel<TreeItem<String>> fm = stringTreeTableView.getFocusModel();
sm.select(indexToSelect);
assertEquals(indexToSelect, sm.getSelectedIndex());
assertEquals(root.getChildren().get(indexToSelect).getValue(), sm.getSelectedItem().getValue());
assertEquals(indexToSelect, fm.getFocusedIndex());
assertEquals(root.getChildren().get(indexToSelect).getValue(), fm.getFocusedItem().getValue());
root.getChildren().remove(indexToRemove);
assertEquals(expectedIndex, sm.getSelectedIndex());
assertEquals(root.getChildren().get(expectedIndex).getValue(), sm.getSelectedItem().getValue());
assertEquals(debug(), expectedIndex, fm.getFocusedIndex());
assertEquals(root.getChildren().get(expectedIndex).getValue(), fm.getFocusedItem().getValue());
}
@Test public void test_rt_39675() {
TreeItem<String> b;
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(
new TreeItem<>("a"),
b = new TreeItem<>("b"),
new TreeItem<>("c"),
new TreeItem<>("d")
);
b.setExpanded(true);
b.getChildren().addAll(
new TreeItem<>("b1"),
new TreeItem<>("b2"),
new TreeItem<>("b3"),
new TreeItem<>("b4")
);
TreeTableView<String> stringTreeTableView = new TreeTableView<>(root);
TreeTableColumn<String,String> column0 = new TreeTableColumn<>("Column1");
column0.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
TreeTableColumn<String,String> column1 = new TreeTableColumn<>("Column2");
column1.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
TreeTableColumn<String,String> column2 = new TreeTableColumn<>("Column3");
column2.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
stringTreeTableView.getColumns().addAll(column0, column1, column2);
sm = stringTreeTableView.getSelectionModel();
sm.setSelectionMode(SelectionMode.SINGLE);
sm.setCellSelectionEnabled(true);
StageLoader sl = new StageLoader(stringTreeTableView);
assertEquals(0, sm.getSelectedItems().size());
sm.clearAndSelect(4, column0);
assertTrue(sm.isSelected(4, column0));
assertEquals(1, sm.getSelectedCells().size());
assertEquals("b2", ((TreeItem)sm.getSelectedItem()).getValue());
b.setExpanded(false);
assertTrue(sm.isSelected(2, column0));
assertEquals(1, sm.getSelectedCells().size());
assertEquals("b", ((TreeItem)sm.getSelectedItem()).getValue());
sl.dispose();
}
private ObservableList<String> test_rt_39661_setup() {
ObservableList<String> rawItems = FXCollections.observableArrayList(
"9-item", "8-item", "7-item", "6-item",
"5-item", "4-item", "3-item", "2-item", "1-item");
root = createSubTree("root", rawItems);
root.setExpanded(true);
treeTableView = new TreeTableView(root);
return rawItems;
}
private TreeItem createSubTree(Object item, ObservableList<String> rawItems) {
TreeItem child = new TreeItem(item);
child.getChildren().setAll(rawItems.stream()
.map(rawItem -> new TreeItem(rawItem))
.collect(Collectors.toList()));
return child;
}
@Test public void test_rt_39661_rowLessThanExpandedItemCount() {
ObservableList<String> rawItems = test_rt_39661_setup();
TreeItem child = createSubTree("child", rawItems);
TreeItem grandChild = (TreeItem) child.getChildren().get(rawItems.size() - 1);
root.getChildren().add(child);
assertTrue("row of item must be less than expandedItemCount, but was: " + treeTableView.getRow(grandChild),
treeTableView.getRow(grandChild) < treeTableView.getExpandedItemCount());
}
@Test public void test_rt_39661_rowOfGrandChildParentCollapsedUpdatedOnInsertAbove() {
ObservableList<String> rawItems = test_rt_39661_setup();
int grandIndex = 2;
int childIndex = 3;
TreeItem child = createSubTree("addedChild2", rawItems);
TreeItem grandChild = (TreeItem) child.getChildren().get(grandIndex);
root.getChildren().add(childIndex, child);
int rowOfGrand = treeTableView.getRow(grandChild);
root.getChildren().add(childIndex - 1, createSubTree("other", rawItems));
assertEquals(-1, treeTableView.getRow(grandChild));
}
@Test public void test_rt_39661_rowOfGrandChildParentCollapsedUpdatedOnInsertAboveWithoutAccess() {
ObservableList<String> rawItems = test_rt_39661_setup();
int grandIndex = 2;
int childIndex = 3;
TreeItem child = createSubTree("addedChild2", rawItems);
TreeItem grandChild = (TreeItem) child.getChildren().get(grandIndex);
root.getChildren().add(childIndex, child);
int rowOfGrand = 7;
root.getChildren().add(childIndex, createSubTree("other", rawItems));
assertEquals(-1, treeTableView.getRow(grandChild));
}
@Test public void test_rt_39661_rowOfGrandChildParentExpandedUpdatedOnInsertAbove() {
ObservableList<String> rawItems = test_rt_39661_setup();
int grandIndex = 2;
int childIndex = 3;
TreeItem child = createSubTree("addedChild2", rawItems);
TreeItem grandChild = (TreeItem) child.getChildren().get(grandIndex);
child.setExpanded(true);
root.getChildren().add(childIndex, child);
int rowOfGrand = treeTableView.getRow(grandChild);
root.getChildren().add(childIndex -1, createSubTree("other", rawItems));
assertEquals(rowOfGrand + 1, treeTableView.getRow(grandChild));
}
@Test public void test_rt_39661_rowOfGrandChildDependsOnParentExpansion() {
ObservableList<String> rawItems = test_rt_39661_setup();
int grandIndex = 2;
int childIndex = 3;
TreeItem collapsedChild = createSubTree("addedChild", rawItems);
TreeItem collapsedGrandChild = (TreeItem) collapsedChild.getChildren().get(grandIndex);
root.getChildren().add(childIndex, collapsedChild);
int collapedGrandIndex = treeTableView.getRow(collapsedGrandChild);
int collapsedRowCount = treeTableView.getExpandedItemCount();
test_rt_39661_setup();
assertEquals(collapsedRowCount - 1, treeTableView.getExpandedItemCount());
TreeItem expandedChild = createSubTree("addedChild2", rawItems);
TreeItem expandedGrandChild = (TreeItem) expandedChild.getChildren().get(grandIndex);
expandedChild.setExpanded(true);
root.getChildren().add(childIndex, expandedChild);
assertNotSame("getRow must depend on expansionState " + collapedGrandIndex,
collapedGrandIndex, treeTableView.getRow(expandedGrandChild));
}
@Test public void test_rt_39661_rowOfGrandChildInCollapsedChild() {
ObservableList<String> rawItems = test_rt_39661_setup();
TreeItem newChild = createSubTree("added-child", rawItems);
TreeItem grandChild = (TreeItem) newChild.getChildren().get(2);
root.getChildren().add(6, newChild);
int row = treeTableView.getRow(grandChild);
assertEquals("grandChild not visible", -1, row);
if (row > -1) {
assertEquals(grandChild, treeTableView.getTreeItem(row));
}
}
@Test public void test_rt_39661_rowOfRootChild() {
ObservableList<String> rawItems = test_rt_39661_setup();
int index = 2;
TreeItem child = (TreeItem) root.getChildren().get(index);
assertEquals(index + 1, treeTableView.getRow(child));
}
@Test public void test_rt_39661_expandedItemCount() {
ObservableList<String> rawItems = test_rt_39661_setup();
int initialRowCount = treeTableView.getExpandedItemCount();
assertEquals(root.getChildren().size() + 1, initialRowCount);
TreeItem collapsedChild = createSubTree("collapsed-child", rawItems);
root.getChildren().add(collapsedChild);
assertEquals(initialRowCount + 1, treeTableView.getExpandedItemCount());
TreeItem expandedChild = createSubTree("expanded-child", rawItems);
expandedChild.setExpanded(true);
root.getChildren().add(0, expandedChild);
assertEquals(2 * initialRowCount + 1, treeTableView.getExpandedItemCount());
}
private int test_rt_39822_count = 0;
@Test public void test_rt_39822() {
final Thread.UncaughtExceptionHandler exceptionHandler = Thread.currentThread().getUncaughtExceptionHandler();
Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
if (test_rt_39822_count == 0) {
test_rt_39822_count++;
if (! (e instanceof IllegalStateException)) {
e.printStackTrace();
fail("Expected IllegalStateException, instead got " + e);
}
} else {
test_rt_39822_count++;
}
});
TreeTableView<String> table = new TreeTableView<>();
TreeTableColumn<String, String> col1 = new TreeTableColumn<>("Foo");
table.getColumns().addAll(col1, col1);
StageLoader sl = null;
try {
sl = new StageLoader(table);
} finally {
if (sl != null) {
sl.dispose();
}
Thread.currentThread().setUncaughtExceptionHandler(exceptionHandler);
}
}
private int test_rt_39842_count = 0;
@Test public void test_rt_39842_selectLeftDown() {
test_rt_39842(true, false);
}
@Test public void test_rt_39842_selectLeftUp() {
test_rt_39842(true, true);
}
@Test public void test_rt_39842_selectRightDown() {
test_rt_39842(false, false);
}
@Test public void test_rt_39842_selectRightUp() {
test_rt_39842(false, true);
}
private void test_rt_39842(boolean selectToLeft, boolean selectUpwards) {
test_rt_39842_count = 0;
TreeTableColumn firstNameCol = new TreeTableColumn("First Name");
firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("firstName"));
TreeTableColumn lastNameCol = new TreeTableColumn("Last Name");
lastNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Person, String>("lastName"));
TreeItem root = new TreeItem("root");
root.getChildren().setAll(
new TreeItem(new Person("Jacob", "Smith", "jacob.smith@example.com")),
new TreeItem(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
new TreeItem(new Person("Ethan", "Williams", "ethan.williams@example.com")),
new TreeItem(new Person("Emma", "Jones", "emma.jones@example.com")),
new TreeItem(new Person("Michael", "Brown", "michael.brown@example.com")));
root.setExpanded(true);
TreeTableView<Person> table = new TreeTableView<>(root);
table.setShowRoot(false);
table.getColumns().addAll(firstNameCol, lastNameCol);
sm = table.getSelectionModel();
sm.setCellSelectionEnabled(true);
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.getSelectedCells().addListener((ListChangeListener) c -> test_rt_39842_count++);
StageLoader sl = new StageLoader(table);
assertEquals(0, test_rt_39842_count);
if (selectToLeft) {
if (selectUpwards) {
sm.selectRange(3, lastNameCol, 0, firstNameCol);
} else {
sm.selectRange(0, lastNameCol, 3, firstNameCol);
}
} else {
if (selectUpwards) {
sm.selectRange(3, firstNameCol, 0, lastNameCol);
} else {
sm.selectRange(0, firstNameCol, 3, lastNameCol);
}
}
assertEquals(8, sm.getSelectedCells().size());
assertEquals(1, test_rt_39842_count);
for (int row = 0; row <= 3; row++) {
for (int column = 0; column <= 1; column++) {
IndexedCell cell = VirtualFlowTestUtils.getCell(table, row, column);
assertTrue(cell.isSelected());
}
}
sl.dispose();
}
@Test public void test_rt_22599() {
TreeItem<RT22599_DataType> root = new TreeItem<>();
root.getChildren().setAll(
new TreeItem<>(new RT22599_DataType(1, "row1")),
new TreeItem<>(new RT22599_DataType(2, "row2")),
new TreeItem<>(new RT22599_DataType(3, "row3")));
root.setExpanded(true);
TreeTableColumn<RT22599_DataType, String> col = new TreeTableColumn<>("Header");
col.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue().text));
TreeTableView<RT22599_DataType> table = new TreeTableView<>(root);
table.setShowRoot(false);
table.getColumns().addAll(col);
StageLoader sl = new StageLoader(table);
assertNotNull(table.getSkin());
assertEquals("row1", VirtualFlowTestUtils.getCell(table, 0, 0).getText());
assertEquals("row2", VirtualFlowTestUtils.getCell(table, 1, 0).getText());
assertEquals("row3", VirtualFlowTestUtils.getCell(table, 2, 0).getText());
TreeItem<RT22599_DataType> data;
root.getChildren().set(0, data = new TreeItem<>(new RT22599_DataType(0, "row1a")));
Toolkit.getToolkit().firePulse();
assertEquals("row1a", VirtualFlowTestUtils.getCell(table, 0, 0).getText());
data.getValue().text = "row1b";
Toolkit.getToolkit().firePulse();
assertEquals("row1a", VirtualFlowTestUtils.getCell(table, 0, 0).getText());
table.refresh();
Toolkit.getToolkit().firePulse();
assertEquals("row1b", VirtualFlowTestUtils.getCell(table, 0, 0).getText());
sl.dispose();
}
private static class RT22599_DataType {
public int id = 0;
public String text = "";
public RT22599_DataType(int id, String text) {
this.id = id;
this.text = text;
}
@Override public boolean equals(Object obj) {
if (obj == null) return false;
return id == ((RT22599_DataType)obj).id;
}
}
private int rt_39966_count = 0;
@Test public void test_rt_39966() {
TreeItem<String> root = new TreeItem<>("Root");
TreeTableView<String> table = new TreeTableView<>(root);
table.setShowRoot(true);
TreeTableColumn<String,String> column = new TreeTableColumn<>("Column");
column.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
table.getColumns().add(column);
StageLoader sl = new StageLoader(table);
assertTrue(table.getSelectionModel().isEmpty());
table.getSelectionModel().selectedItemProperty().addListener((value, s1, s2) -> {
if (rt_39966_count == 0) {
rt_39966_count++;
assertFalse(table.getSelectionModel().isEmpty());
} else {
assertTrue(table.getSelectionModel().isEmpty());
}
});
table.getSelectionModel().select(0);
assertFalse(table.getSelectionModel().isEmpty());
table.setRoot(null);
assertTrue(table.getSelectionModel().isEmpty());
sl.dispose();
}
@Test public void test_rt_40012_selectedAtLastOnDisjointRemoveItemsAbove() {
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(
new TreeItem<>("0"),
new TreeItem<>("1"),
new TreeItem<>("2"),
new TreeItem<>("3"),
new TreeItem<>("4"),
new TreeItem<>("5")
);
TreeTableView<String> stringTreeTableView = new TreeTableView<>(root);
stringTreeTableView.setShowRoot(false);
TreeTableView.TreeTableViewSelectionModel<String> sm = stringTreeTableView.getSelectionModel();
TreeTableColumn<String,String> column = new TreeTableColumn<>("Column");
column.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
stringTreeTableView.getColumns().add(column);
int last = root.getChildren().size() - 1;
sm.select(last);
root.getChildren().removeAll(root.getChildren().get(1), root.getChildren().get(3));
int expected = last - 2;
assertEquals("5", sm.getSelectedItem().getValue());
assertEquals("selected index after disjoint removes above", expected, sm.getSelectedIndex());
}
@Test public void test_rt_40012_accessSelectedAtLastOnDisjointRemoveItemsAbove() {
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(
new TreeItem<>("0"),
new TreeItem<>("1"),
new TreeItem<>("2"),
new TreeItem<>("3"),
new TreeItem<>("4"),
new TreeItem<>("5")
);
TreeTableView<String> stringTreeTableView = new TreeTableView<>(root);
stringTreeTableView.setShowRoot(false);
TreeTableView.TreeTableViewSelectionModel<String> sm = stringTreeTableView.getSelectionModel();
TreeTableColumn<String,String> column = new TreeTableColumn<>("Column");
column.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
stringTreeTableView.getColumns().add(column);
int last = root.getChildren().size() - 1;
sm.select(last);
root.getChildren().removeAll(root.getChildren().get(1), root.getChildren().get(3));
int selected = sm.getSelectedIndex();
if (selected > -1) {
root.getChildren().get(selected);
}
}
private int rt_40012_count = 0;
@Test public void test_rt_40012_selectedIndexNotificationOnDisjointRemovesAbove() {
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(
new TreeItem<>("0"),
new TreeItem<>("1"),
new TreeItem<>("2"),
new TreeItem<>("3"),
new TreeItem<>("4"),
new TreeItem<>("5")
);
TreeTableView<String> stringTreeTableView = new TreeTableView<>(root);
stringTreeTableView.setShowRoot(false);
TreeTableView.TreeTableViewSelectionModel<String> sm = stringTreeTableView.getSelectionModel();
TreeTableColumn<String,String> column = new TreeTableColumn<>("Column");
column.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
stringTreeTableView.getColumns().add(column);
int last = root.getChildren().size() - 2;
sm.select(last);
assertEquals(last, sm.getSelectedIndex());
rt_40012_count = 0;
sm.selectedIndexProperty().addListener(o -> rt_40012_count++);
root.getChildren().removeAll(root.getChildren().get(1), root.getChildren().get(3));
assertEquals("sanity: selectedIndex must be shifted by -2", last - 2, sm.getSelectedIndex());
assertEquals("must fire single event on removes above", 1, rt_40012_count);
}
@Test
public void test_rt_40012_selectedItemNotificationOnDisjointRemovesAbove() {
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(
new TreeItem<>("0"),
new TreeItem<>("1"),
new TreeItem<>("2"),
new TreeItem<>("3"),
new TreeItem<>("4"),
new TreeItem<>("5")
);
TreeTableView<String> stringTreeTableView = new TreeTableView<>(root);
stringTreeTableView.setShowRoot(false);
TreeTableView.TreeTableViewSelectionModel<String> sm = stringTreeTableView.getSelectionModel();
TreeTableColumn<String,String> column = new TreeTableColumn<>("Column");
column.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
stringTreeTableView.getColumns().add(column);
int last = root.getChildren().size() - 2;
Object lastItem = root.getChildren().get(last);
sm.select(last);
assertEquals(lastItem, sm.getSelectedItem());
rt_40012_count = 0;
sm.selectedItemProperty().addListener(o -> rt_40012_count++);
root.getChildren().removeAll(root.getChildren().get(1), root.getChildren().get(3));
assertEquals("sanity: selectedItem unchanged", lastItem, sm.getSelectedItem());
assertEquals("must not fire on unchanged selected item", 0, rt_40012_count);
}
private int rt_40010_count = 0;
@Test public void test_rt_40010() {
TreeItem<String> root = new TreeItem<>("Root");
TreeItem<String> child = new TreeItem<>("child");
root.setExpanded(true);
root.getChildren().addAll(child);
TreeTableView<String> stringTreeTableView = new TreeTableView<>(root);
TreeTableView.TreeTableViewSelectionModel<String> sm = stringTreeTableView.getSelectionModel();
TreeTableColumn<String,String> column = new TreeTableColumn<>("Column");
column.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
stringTreeTableView.getColumns().add(column);
sm.getSelectedIndices().addListener((ListChangeListener<? super Integer>) l -> rt_40010_count++);
sm.getSelectedItems().addListener((ListChangeListener<? super TreeItem<String>>) l -> rt_40010_count++);
assertEquals(0, rt_40010_count);
sm.select(1);
assertEquals(1, sm.getSelectedIndex());
assertEquals(child, sm.getSelectedItem());
assertEquals(2, rt_40010_count);
root.getChildren().remove(child);
assertEquals(0, sm.getSelectedIndex());
assertEquals(root, sm.getSelectedItem());
assertEquals(4, rt_40010_count);
}
private int rt_40212_count = 0;
@Test public void test_rt_40212() {
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(
new TreeItem<>("0"),
new TreeItem<>("1"),
new TreeItem<>("2"),
new TreeItem<>("3"),
new TreeItem<>("4"),
new TreeItem<>("5")
);
TreeTableView<String> stringTreeTableView = new TreeTableView<>(root);
stringTreeTableView.setShowRoot(false);
TreeTableView.TreeTableViewSelectionModel<String> sm = stringTreeTableView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
TreeTableColumn<String,String> column = new TreeTableColumn<>("Column");
column.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
stringTreeTableView.getColumns().add(column);
sm.selectRange(3, 5);
int selected = sm.getSelectedIndex();
sm.getSelectedIndices().addListener((ListChangeListener<Integer>) change -> {
assertEquals("sanity: selectedIndex unchanged", selected, sm.getSelectedIndex());
while(change.next()) {
assertEquals("single event on clearAndSelect already selected", 1, ++rt_40212_count);
boolean type = change.wasAdded() || change.wasRemoved() || change.wasPermutated() || change.wasUpdated();
assertTrue("at least one of the change types must be true", type);
}
});
sm.clearAndSelect(selected);
}
@Test public void test_rt_40280() {
final TreeTableView<String> view = new TreeTableView<>();
StageLoader sl = new StageLoader(view);
MultipleSelectionModelBaseShim.getFocusedIndex(view.getSelectionModel());
view.getFocusModel().getFocusedIndex();
sl.dispose();
}
@Test public void test_rt_40278_showRoot() {
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(new TreeItem<>("0"),new TreeItem<>("1"));
TreeTableView<String> view = new TreeTableView<>(root);
view.setShowRoot(false);
MultipleSelectionModel<TreeItem<String>> sm = view.getSelectionModel();
assertFalse("sanity: test setup such that root is not showing", view.isShowRoot());
sm.select(0);
assertEquals(0, sm.getSelectedIndex());
assertEquals(view.getTreeItem(sm.getSelectedIndex()), sm.getSelectedItem());
view.setShowRoot(true);
assertEquals(1, sm.getSelectedIndex());
assertEquals(view.getTreeItem(sm.getSelectedIndex()), sm.getSelectedItem());
}
@Test public void test_rt_40278_hideRoot_selectionOnChild() {
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(new TreeItem<>("0"),new TreeItem<>("1"));
TreeTableView<String> view = new TreeTableView<>(root);
view.setShowRoot(true);
MultipleSelectionModel<TreeItem<String>> sm = view.getSelectionModel();
assertTrue("sanity: test setup such that root is showing", view.isShowRoot());
sm.select(1);
assertEquals(1, sm.getSelectedIndex());
assertEquals(view.getTreeItem(sm.getSelectedIndex()), sm.getSelectedItem());
view.setShowRoot(false);
assertEquals(0, sm.getSelectedIndex());
assertEquals(view.getTreeItem(sm.getSelectedIndex()), sm.getSelectedItem());
}
@Test public void test_rt_40278_hideRoot_selectionOnRoot() {
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(new TreeItem<>("0"),new TreeItem<>("1"));
TreeTableView<String> view = new TreeTableView<>(root);
view.setShowRoot(true);
MultipleSelectionModel<TreeItem<String>> sm = view.getSelectionModel();
assertTrue("sanity: test setup such that root is showing", view.isShowRoot());
sm.select(0);
assertEquals(0, sm.getSelectedIndex());
assertEquals(view.getTreeItem(sm.getSelectedIndex()), sm.getSelectedItem());
view.setShowRoot(false);
assertEquals(0, sm.getSelectedIndex());
assertEquals(view.getTreeItem(sm.getSelectedIndex()), sm.getSelectedItem());
}
@Test public void test_rt_40263() {
TreeItem<Integer> root = new TreeItem<>(-1);
root.setExpanded(true);
for (int i = 0; i < 10; i++) {
root.getChildren().add(new TreeItem<Integer>(i));
}
final TreeTableView<Integer> view = new TreeTableView<>(root);
TreeTableView.TreeTableViewSelectionModel<Integer> sm = view.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
int[] indices = new int[]{2, 5, 7};
ListChangeListener<Integer> l = c -> {
int subChanges = 0;
while(c.next()) {
subChanges++;
}
assertEquals(1, subChanges);
c.reset();
c.next();
assertEquals(indices.length, c.getAddedSize());
assertArrayEquals(indices, c.getAddedSubList().stream().mapToInt(i -> i).toArray());
};
sm.getSelectedIndices().addListener(l);
sm.selectIndices(indices[0], indices);
}
@Test public void test_rt_40319_toRight_toBottom() { test_rt_40319(true, true, false); }
@Test public void test_rt_40319_toRight_toTop() { test_rt_40319(true, false, false); }
@Test public void test_rt_40319_toLeft_toBottom() { test_rt_40319(false, true, false); }
@Test public void test_rt_40319_toLeft_toTop() { test_rt_40319(false, false, false); }
@Test public void test_rt_40319_toRight_toBottom_useMouse() { test_rt_40319(true, true, true); }
@Test public void test_rt_40319_toRight_toTop_useMouse() { test_rt_40319(true, false, true); }
@Test public void test_rt_40319_toLeft_toBottom_useMouse() { test_rt_40319(false, true, true); }
@Test public void test_rt_40319_toLeft_toTop_useMouse() { test_rt_40319(false, false, true); }
private void test_rt_40319(boolean toRight, boolean toBottom, boolean useMouse) {
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(
new TreeItem<>("0"),
new TreeItem<>("1"),
new TreeItem<>("2"),
new TreeItem<>("3"),
new TreeItem<>("4"),
new TreeItem<>("5")
);
TreeTableView<String> t = new TreeTableView<>(root);
t.setShowRoot(false);
sm = t.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
TreeTableColumn<String,String> c1 = new TreeTableColumn<>("Column");
c1.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
TreeTableColumn<String,String> c2 = new TreeTableColumn<>("Column");
c2.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
t.getColumns().addAll(c1, c2);
final int startIndex = toRight ? 0 : 2;
final int endIndex = toRight ? 2 : 0;
final TreeTableColumn<String,String> startColumn = toBottom ? c1 : c2;
final TreeTableColumn<String,String> endColumn = toBottom ? c2 : c1;
sm.select(startIndex, startColumn);
if (useMouse) {
Cell endCell = VirtualFlowTestUtils.getCell(t, endIndex, toRight ? 1 : 0);
MouseEventFirer mouse = new MouseEventFirer(endCell);
mouse.fireMousePressAndRelease(KeyModifier.SHIFT);
} else {
t.getSelectionModel().selectRange(startIndex, startColumn, endIndex, endColumn);
}
assertEquals(3, sm.getSelectedItems().size());
assertEquals(3, sm.getSelectedIndices().size());
assertEquals(3, sm.getSelectedCells().size());
}
@Test public void test_jdk_8147483() {
TreeItem<Number> root = new TreeItem<>(0);
root.setExpanded(true);
final TreeTableView<Number> view = new TreeTableView<>(root);
view.setShowRoot(false);
AtomicInteger cellUpdateCount = new AtomicInteger();
AtomicInteger rowCreateCount = new AtomicInteger();
TreeTableColumn<Number, Number> column = new TreeTableColumn<>("Column");
column.setCellValueFactory(cdf -> new ReadOnlyIntegerWrapper(0));
column.setCellFactory( ttc -> new TreeTableCell<Number,Number>() {
@Override protected void updateItem(Number item, boolean empty) {
cellUpdateCount.incrementAndGet();
super.updateItem(item, empty);
}
});
view.getColumns().add(column);
view.setRowFactory(t -> {
rowCreateCount.incrementAndGet();
return new TreeTableRow<>();
});
assertEquals(0, cellUpdateCount.get());
assertEquals(0, rowCreateCount.get());
StageLoader sl = new StageLoader(view);
root.getChildren().add(new TreeItem(1));
Toolkit.getToolkit().firePulse();
final int firstCellUpdateCount = cellUpdateCount.get();
final int firstRowCreateCount = rowCreateCount.get();
root.getChildren().add(new TreeItem(2));
Toolkit.getToolkit().firePulse();
assertEquals(firstCellUpdateCount+1, cellUpdateCount.get());
assertEquals(firstRowCreateCount, rowCreateCount.get());
root.getChildren().add(new TreeItem(3));
Toolkit.getToolkit().firePulse();
assertEquals(firstCellUpdateCount+2, cellUpdateCount.get());
assertEquals(firstRowCreateCount, rowCreateCount.get());
sl.dispose();
}
@Test public void test_jdk_8144681_removeColumn() {
TreeTableView<Book> table = new TreeTableView<>();
TreeItem<Book> root = new TreeItem<>();
root.getChildren().addAll(
new TreeItem<>(new Book("Book 1", "Author 1", "Remark 1"))
, new TreeItem<>(new Book("Book 2", "Author 2", "Remark 2"))
, new TreeItem<>(new Book("Book 3", "Author 3", "Remark 3"))
, new TreeItem<>(new Book("Book 4", "Author 4", "Remark 4")));
table.setRoot(root);
String[] columns = { "title", "author", "remark" };
for (String prop : columns) {
TreeTableColumn<Book, String> col = new TreeTableColumn<>(prop);
col.setCellValueFactory(new TreeItemPropertyValueFactory<>(prop));
table.getColumns().add(col);
}
table.setColumnResizePolicy(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);
table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
table.getSelectionModel().setCellSelectionEnabled(true);
table.getSelectionModel().selectAll();
ControlTestUtils.runWithExceptionHandler(() -> table.getColumns().remove(2));
}
@Test public void test_jdk_8144681_moveColumn() {
TreeTableView<Book> table = new TreeTableView<>();
TreeItem<Book> root = new TreeItem<>();
root.getChildren().addAll(
new TreeItem<>(new Book("Book 1", "Author 1", "Remark 1"))
, new TreeItem<>(new Book("Book 2", "Author 2", "Remark 2"))
, new TreeItem<>(new Book("Book 3", "Author 3", "Remark 3"))
, new TreeItem<>(new Book("Book 4", "Author 4", "Remark 4")));
table.setRoot(root);
String[] columns = { "title", "author", "remark" };
for (String prop : columns) {
TreeTableColumn<Book, String> col = new TreeTableColumn<>(prop);
col.setCellValueFactory(new TreeItemPropertyValueFactory<>(prop));
table.getColumns().add(col);
}
table.setColumnResizePolicy(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);
table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
table.getSelectionModel().setCellSelectionEnabled(true);
table.getSelectionModel().selectAll();
ControlTestUtils.runWithExceptionHandler(() -> {
table.getColumns().setAll(table.getColumns().get(0), table.getColumns().get(2), table.getColumns().get(1));
});
}
private static class Book {
private SimpleStringProperty title = new SimpleStringProperty();
private SimpleStringProperty author = new SimpleStringProperty();
private SimpleStringProperty remark = new SimpleStringProperty();
public Book(String title, String author, String remark) {
super();
setTitle(title);
setAuthor(author);
setRemark(remark);
}
public SimpleStringProperty titleProperty() {
return this.title;
}
public java.lang.String getTitle() {
return this.titleProperty().get();
}
public void setTitle(final java.lang.String title) {
this.titleProperty().set(title);
}
public SimpleStringProperty authorProperty() {
return this.author;
}
public java.lang.String getAuthor() {
return this.authorProperty().get();
}
public void setAuthor(final java.lang.String author) {
this.authorProperty().set(author);
}
public SimpleStringProperty remarkProperty() {
return this.remark;
}
public java.lang.String getRemark() {
return this.remarkProperty().get();
}
public void setRemark(final java.lang.String remark) {
this.remarkProperty().set(remark);
}
@Override
public String toString() {
return String.format("%s(%s) - %s", getTitle(), getAuthor(), getRemark());
}
}
@Test public void test_jdk_8157205() {
final TreeItem<String> childNode1 = new TreeItem<>("Child Node 1");
childNode1.setExpanded(true);
TreeItem<String> item1 = new TreeItem<>("Node 1-1");
TreeItem<String> item2 = new TreeItem<>("Node 1-2");
childNode1.getChildren().addAll(item1, item2);
final TreeItem<String> root = new TreeItem<>("Root node");
root.setExpanded(true);
root.getChildren().add(childNode1);
final TreeTableView<String> view = new TreeTableView<>(root);
MultipleSelectionModel<TreeItem<String>> sm = view.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
AtomicInteger step = new AtomicInteger();
AtomicInteger indicesEventCount = new AtomicInteger();
sm.getSelectedIndices().addListener((ListChangeListener<Integer>)c -> {
switch (step.get()) {
case 0: {
c.next();
assertEquals(3, c.getAddedSize());
assertTrue("added: " + c.getAddedSubList(),
c.getAddedSubList().containsAll(FXCollections.observableArrayList(1,2,3)));
assertEquals(0, c.getFrom());
break;
}
case 1: {
List<Integer> removed = new ArrayList<>();
while (c.next()) {
if (c.wasRemoved()) {
removed.addAll(c.getRemoved());
} else {
fail("Unexpected state");
}
}
if (!removed.isEmpty()) {
assertTrue(removed.containsAll(FXCollections.observableArrayList(2,3)));
}
break;
}
}
indicesEventCount.incrementAndGet();
});
AtomicInteger itemsEventCount = new AtomicInteger();
sm.getSelectedItems().addListener((ListChangeListener<TreeItem<String>>)c -> {
switch (step.get()) {
case 0: {
c.next();
assertEquals(3, c.getAddedSize());
assertTrue("added: " + c.getAddedSubList(),
c.getAddedSubList().containsAll(FXCollections.observableArrayList(childNode1, item1, item2)));
assertEquals(0, c.getFrom());
break;
}
case 1: {
List<TreeItem<String>> removed = new ArrayList<>();
while (c.next()) {
if (c.wasRemoved()) {
removed.addAll(c.getRemoved());
} else {
fail("Unexpected state");
}
}
if (!removed.isEmpty()) {
assertTrue(removed.containsAll(FXCollections.observableArrayList(item1, item2)));
}
break;
}
}
itemsEventCount.incrementAndGet();
});
assertEquals(0, indicesEventCount.get());
assertEquals(0, itemsEventCount.get());
step.set(0);
sm.selectIndices(1,2,3);
assertTrue(sm.isSelected(1));
assertTrue(sm.isSelected(2));
assertTrue(sm.isSelected(3));
assertEquals(3, sm.getSelectedIndices().size());
assertEquals(3, sm.getSelectedItems().size());
assertEquals(1, indicesEventCount.get());
assertEquals(1, itemsEventCount.get());
step.set(1);
childNode1.setExpanded(false);
assertTrue(sm.isSelected(1));
assertFalse(sm.isSelected(2));
assertFalse(sm.isSelected(3));
assertEquals(1, sm.getSelectedIndices().size());
assertEquals(1, sm.getSelectedItems().size());
assertEquals(2, indicesEventCount.get());
assertEquals(2, itemsEventCount.get());
step.set(2);
childNode1.setExpanded(true);
assertTrue(sm.isSelected(1));
assertFalse(sm.isSelected(2));
assertFalse(sm.isSelected(3));
assertEquals(1, sm.getSelectedIndices().size());
assertEquals(1, sm.getSelectedItems().size());
assertEquals(2, indicesEventCount.get());
assertEquals(2, itemsEventCount.get());
}
@Test public void test_jdk_8157285() {
final TreeItem<String> childNode1 = new TreeItem<>("Child Node 1");
childNode1.setExpanded(true);
TreeItem<String> item1 = new TreeItem<>("Node 1-1");
TreeItem<String> item2 = new TreeItem<>("Node 1-2");
childNode1.getChildren().addAll(item1, item2);
final TreeItem<String> root = new TreeItem<>("Root node");
root.setExpanded(true);
root.getChildren().add(childNode1);
final TreeTableView<String> view = new TreeTableView<>(root);
MultipleSelectionModel<TreeItem<String>> sm = view.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
view.expandedItemCountProperty().addListener((observable, oldCount, newCount) -> {
if (childNode1.isExpanded()) return;
assertTrue(sm.isSelected(1));
assertFalse(sm.isSelected(2));
assertFalse(sm.isSelected(3));
assertEquals(1, sm.getSelectedIndices().size());
assertEquals(1, sm.getSelectedItems().size());
});
sm.selectIndices(1,2,3);
assertTrue(sm.isSelected(1));
assertTrue(sm.isSelected(2));
assertTrue(sm.isSelected(3));
assertEquals(3, sm.getSelectedIndices().size());
assertEquals(3, sm.getSelectedItems().size());
childNode1.setExpanded(false);
}
@Test public void test_jdk_8152396() {
final TreeItem<String> childNode1 = new TreeItem<>("Child Node 1");
TreeItem<String> item1 = new TreeItem<>("Node 1-1");
TreeItem<String> item2 = new TreeItem<>("Node 1-2");
childNode1.getChildren().addAll(item1, item2);
final TreeItem<String> root = new TreeItem<>("Root node");
root.setExpanded(true);
root.getChildren().add(childNode1);
final TreeTableView<String> view = new TreeTableView<>(root);
MultipleSelectionModel<TreeItem<String>> sm = view.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
view.expandedItemCountProperty().addListener((observable, oldCount, newCount) -> {
if (newCount.intValue() > oldCount.intValue()) {
for (int index: sm.getSelectedIndices()) {
TreeItem<String> item = view.getTreeItem(index);
if (item != null && item.isExpanded() && !item.getChildren().isEmpty()) {
int startIndex = index + 1;
int maxCount = startIndex + item.getChildren().size();
sm.selectRange(startIndex, maxCount);
}
}
}
});
FilteredList filteredList = sm.getSelectedItems().filtered(Objects::nonNull);
StageLoader sl = new StageLoader(view);
sm.select(1);
childNode1.setExpanded(true);
Toolkit.getToolkit().firePulse();
assertEquals(3, filteredList.size());
ControlTestUtils.runWithExceptionHandler(() -> childNode1.setExpanded(false));
Toolkit.getToolkit().firePulse();
assertEquals(1, filteredList.size());
sl.dispose();
}
@Test public void test_jdk_8160771() {
TreeTableView table = new TreeTableView();
TreeTableColumn first = new TreeTableColumn("First Name");
table.getColumns().add(first);
table.getVisibleLeafColumns().addListener((ListChangeListener) c -> {
c.next();
assertTrue(c.wasAdded());
assertSame(table, ((TreeTableColumn) c.getAddedSubList().get(0)).getTreeTableView());
});
TreeTableColumn last = new TreeTableColumn("Last Name");
table.getColumns().add(0, last);
}
private void test_jdk_8169642(Consumer<TreeTableView.TreeTableViewSelectionModel> before,
Consumer<TreeTableView.TreeTableViewSelectionModel> afterDescending,
Consumer<TreeTableView.TreeTableViewSelectionModel> afterAscending) {
final TreeItem<String> rootItem = new TreeItem<>("root");
rootItem.setExpanded(true);
rootItem.getChildren().addAll(new TreeItem<>("first child"), new TreeItem<>("second child"), new TreeItem<>("third child"));
final TreeTableView<String> tree = new TreeTableView<>(rootItem);
final TreeTableColumn<String, String> column = new TreeTableColumn<>("first column");
column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue()));
tree.getColumns().add(column);
TreeTableView.TreeTableViewSelectionModel sm = tree.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
assertTrue(sm.isEmpty());
before.accept(sm);
tree.getSortOrder().add(column);
column.setSortType(TreeTableColumn.SortType.DESCENDING);
afterDescending.accept(sm);
column.setSortType(TreeTableColumn.SortType.ASCENDING);
afterAscending.accept(sm);
}
@Test public void test_jdk_8169642_1_only() {
test_jdk_8169642(
sm -> {
sm.select(1);
assertTrue(sm.isSelected(1));
assertEquals(1, sm.getSelectedCells().size());
},
sm -> {
assertTrue(sm.isSelected(3));
assertEquals(1, sm.getSelectedCells().size());
},
sm -> {
assertTrue(sm.isSelected(1));
assertEquals(1, sm.getSelectedCells().size());
}
);
}
@Test public void test_jdk_8169642_2_only() {
test_jdk_8169642(
sm -> {
sm.select(2);
assertTrue(sm.isSelected(2));
assertEquals(1, sm.getSelectedCells().size());
},
sm -> {
assertTrue(sm.isSelected(2));
assertEquals(1, sm.getSelectedCells().size());
},
sm -> {
assertTrue(sm.isSelected(2));
assertEquals(1, sm.getSelectedCells().size());
}
);
}
@Test public void test_jdk_8169642_1_and_3() {
test_jdk_8169642(
sm -> {
sm.select(1);
sm.select(3);
assertTrue(sm.isSelected(1));
assertTrue(sm.isSelected(3));
assertEquals(2, sm.getSelectedCells().size());
},
sm -> {
assertTrue(sm.isSelected(1));
assertTrue(sm.isSelected(3));
assertEquals(2, sm.getSelectedCells().size());
},
sm -> {
assertTrue(sm.isSelected(1));
assertTrue(sm.isSelected(3));
assertEquals(2, sm.getSelectedCells().size());
}
);
}
@Test public void test_jdk_8169642_0_and_3() {
test_jdk_8169642(
sm -> {
sm.select(0);
sm.select(3);
assertTrue(sm.isSelected(0));
assertTrue(sm.isSelected(3));
assertEquals(2, sm.getSelectedCells().size());
},
sm -> {
assertTrue(sm.isSelected(0));
assertTrue(sm.isSelected(1));
assertEquals(2, sm.getSelectedCells().size());
},
sm -> {
assertTrue(sm.isSelected(0));
assertTrue(sm.isSelected(3));
assertEquals(2, sm.getSelectedCells().size());
}
);
}
@Test public void testRemovedSelectedItemsWhenBranchIsCollapsed() {
TreeItem<String> c1, c2, c3;
TreeItem<String> root = new TreeItem<>("foo");
root.getChildren().add(c1 = new TreeItem<>("bar"));
root.getChildren().add(c2 = new TreeItem<>("baz"));
root.getChildren().add(c3 = new TreeItem<>("qux"));
root.setExpanded(true);
TreeTableView<String> treeTableView = new TreeTableView<>(root);
treeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
treeTableView.getSelectionModel().selectAll();
MockListObserver<TreeItem<String>> observer = new MockListObserver<>();
treeTableView.getSelectionModel().getSelectedItems().addListener(observer);
root.setExpanded(false);
observer.check1();
observer.checkAddRemove(0, treeTableView.getSelectionModel().getSelectedItems(), List.of(c1, c2, c3), 1, 1);
}
@Test
public void test_clearAndSelectChangeMultipleSelectionCellMode() {
TreeItem<Person> root = new TreeItem<>(new Person("root", "",""));
root.getChildren().setAll(
new TreeItem<>(new Person("Jacob", "Smith", "jacob.smith@example.com")),
new TreeItem<>(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
new TreeItem<>(new Person("Ethan", "Williams", "ethan.williams@example.com")),
new TreeItem<>(new Person("Emma", "Jones", "emma.jones@example.com")),
new TreeItem<>(new Person("Michael", "Brown", "michael.brown@example.com")));
root.setExpanded(true);
TreeTableColumn<Person, String> firstNameCol = new TreeTableColumn<>("First Name");
firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("firstName"));
TreeTableColumn<Person, String> lastNameCol = new TreeTableColumn<>("Last Name");
lastNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("lastName"));
TreeTableColumn<Person, String> emailCol = new TreeTableColumn<>("Email");
emailCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("email"));
TreeTableView<Person> table = new TreeTableView<>(root);
table.getColumns().addAll(firstNameCol, lastNameCol, emailCol);
sm = table.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.setCellSelectionEnabled(true);
assertEquals(0, sm.getSelectedItems().size());
sm.select(1, firstNameCol);
assertTrue(sm.isSelected(1, firstNameCol));
assertEquals(1, sm.getSelectedCells().size());
assertEquals(1, sm.getSelectedItems().size());
TreeTableCell<Person, String> cell_1_1 = (TreeTableCell<Person, String>) VirtualFlowTestUtils.getCell(table, 1, 1);
new MouseEventFirer(cell_1_1).fireMousePressAndRelease(KeyModifier.getShortcutKey());
assertTrue(sm.isSelected(1, firstNameCol));
assertTrue(sm.isSelected(1, lastNameCol));
assertEquals(2, sm.getSelectedCells().size());
assertEquals(1, sm.getSelectedItems().size());
TreeTableCell<Person, String> cell_1_0 = (TreeTableCell<Person, String>) VirtualFlowTestUtils.getCell(table, 1, 0);
new MouseEventFirer(cell_1_0).fireMousePressAndRelease();
assertTrue(sm.isSelected(1, firstNameCol));
assertFalse(sm.isSelected(1, lastNameCol));
assertEquals(1, sm.getSelectedCells().size());
assertEquals(1, sm.getSelectedItems().size());
}
@Test
public void testRemoveTreeItemShiftSelection() {
TreeItem<String> a, b, a1, a2, a3;
TreeItem<String> root = new TreeItem<>("root");
root.getChildren().addAll(
a = new TreeItem<>("a"),
b = new TreeItem<>("b")
);
root.setExpanded(true);
a.getChildren().addAll(
a1 = new TreeItem<>("a1"),
a2 = new TreeItem<>("a2"),
a3 = new TreeItem<>("a3")
);
a.setExpanded(true);
TreeTableView<String> stringTreeTableView = new TreeTableView<>(root);
TreeTableColumn<String, String> column = new TreeTableColumn<>("Nodes");
column.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getValue()));
column.setPrefWidth(200);
stringTreeTableView.getColumns().add(column);
stringTreeTableView.setShowRoot(false);
SelectionModel sm = stringTreeTableView.getSelectionModel();
sm.clearAndSelect(3);
assertEquals(a3, sm.getSelectedItem());
root.getChildren().remove(b);
assertEquals(3, sm.getSelectedIndex());
assertEquals(a3, sm.getSelectedItem());
}
@Test
public void testRemoveTreeItemChangesSelectedItem() {
TreeItem<String> rootNode = new TreeItem<>("Root");
rootNode.setExpanded(true);
for (int i = 0; i < 3; i++) {
rootNode.getChildren().add(new TreeItem<>("Node " + i));
}
for (int i = 0; i < 2; i++) {
TreeItem<String> node = rootNode.getChildren().get(i);
node.setExpanded(true);
for (int j = 0; j < 2; j++) {
node.getChildren().add(new TreeItem<>("Sub Node " + i + "-" + j));
}
}
TreeTableColumn<String, String> column = new TreeTableColumn<>("Nodes");
column.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getValue()));
column.setPrefWidth(200);
TreeTableView<String> table = new TreeTableView<>(rootNode);
table.getColumns().add(column);
int selectIndex = 4;
int removeIndex = 2;
table.getSelectionModel().select(selectIndex);
assertEquals(4, table.getSelectionModel().getSelectedIndex());
assertEquals("Node 1", table.getSelectionModel().getSelectedItem().getValue());
table.getRoot().getChildren().remove(removeIndex);
assertEquals(4, table.getSelectionModel().getSelectedIndex());
assertEquals("Node 1", table.getSelectionModel().getSelectedItem().getValue());
}
@Test
public void test_ChangeToStringMouseMultipleSelectionCellMode() {
final Thread.UncaughtExceptionHandler exceptionHandler = Thread.currentThread().getUncaughtExceptionHandler();
Thread.currentThread().setUncaughtExceptionHandler((t, e) -> fail("We don't expect any exceptions in this test!"));
TreeItem<Person> root = new TreeItem<>(new Person("root", "",""));
root.getChildren().setAll(
new TreeItem<>(new Person("Jacob", "Smith", "jacob.smith@example.com")),
new TreeItem<>(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
new TreeItem<>(new Person("Ethan", "Williams", "ethan.williams@example.com")),
new TreeItem<>(new Person("Emma", "Jones", "emma.jones@example.com")),
new TreeItem<>(new Person("Michael", "Brown", "michael.brown@example.com")));
root.setExpanded(true);
TreeTableColumn<Person, String> firstNameCol = new TreeTableColumn<>("First Name");
firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("firstName"));
TreeTableColumn<Person, String> lastNameCol = new TreeTableColumn<>("Last Name");
lastNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("lastName"));
TreeTableColumn<Person, String> emailCol = new TreeTableColumn<>("Email");
emailCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("email"));
TreeTableView<Person> table = new TreeTableView<>(root);
table.getColumns().addAll(firstNameCol, lastNameCol, emailCol);
sm = table.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.setCellSelectionEnabled(true);
table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<TreeItem<Person>>) Object::toString);
assertEquals(0, sm.getSelectedItems().size());
sm.select(1, firstNameCol);
assertTrue(sm.isSelected(1, firstNameCol));
assertEquals(1, sm.getSelectedCells().size());
assertEquals(1, sm.getSelectedItems().size());
TreeTableCell<Person, String> cell = (TreeTableCell<Person, String>) VirtualFlowTestUtils.getCell(table, 1, 1);
MouseEventFirer mouse = new MouseEventFirer(cell);
mouse.fireMousePressAndRelease(KeyModifier.getShortcutKey());
assertTrue(sm.isSelected(1, firstNameCol));
assertTrue(sm.isSelected(1, lastNameCol));
assertEquals(2, sm.getSelectedCells().size());
assertEquals(1, sm.getSelectedItems().size());
Thread.currentThread().setUncaughtExceptionHandler(exceptionHandler);
}
@Test
public void testAnchorRemainsWhenAddingMoreItemsBelow() {
TreeItem<String> b;
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(
new TreeItem<>("a"),
b = new TreeItem<>("b"),
new TreeItem<>("c"),
new TreeItem<>("d")
);
TreeTableView<String> stringTreeView = new TreeTableView<>(root);
stringTreeView.setShowRoot(false);
TreeTableColumn<String,String> column = new TreeTableColumn<>("Column");
column.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue()));
stringTreeView.getColumns().add(column);
TreeTableView.TreeTableViewSelectionModel<String> sm = stringTreeView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
assertTrue(sm.isEmpty());
Cell startCell = VirtualFlowTestUtils.getCell(stringTreeView, 1, 0);
new MouseEventFirer(startCell).fireMousePressAndRelease();
assertTrue(sm.isSelected(1));
assertEquals(b, sm.getSelectedItem());
TreeTablePosition<String, ?> anchor = TreeTableCellBehavior.getAnchor(stringTreeView, null);
assertNotNull(anchor);
assertTrue(TreeTableCellBehavior.hasNonDefaultAnchor(stringTreeView));
assertEquals(1, anchor.getRow());
root.getChildren().add(new TreeItem<>("e"));
Cell endCell = VirtualFlowTestUtils.getCell(stringTreeView, 2, 0);
new MouseEventFirer(endCell).fireMousePressAndRelease(KeyModifier.SHIFT);
assertTrue(sm.isSelected(1));
assertTrue(sm.isSelected(2));
anchor = TreeTableCellBehavior.getAnchor(stringTreeView, null);
assertNotNull(anchor);
assertTrue(TreeTableCellBehavior.hasNonDefaultAnchor(stringTreeView));
assertEquals(1, anchor.getRow());
assertEquals(column, anchor.getTableColumn());
}
@Test
public void testAddTreeItemToCollapsedAncestorKeepsSelectedItem() {
TreeItem<String> rootNode = new TreeItem<>("Root");
rootNode.setExpanded(true);
TreeItem<String> level1 = new TreeItem<>("Node 0");
level1.setExpanded(false);
TreeItem<String> level2 = new TreeItem<>("Node 1");
level2.getChildren().add(new TreeItem<>("Node 2"));
level2.setExpanded(true);
rootNode.getChildren().add(level1);
rootNode.getChildren().add(new TreeItem<>("Node 3"));
level1.getChildren().add(level2);
TreeTableColumn<String, String> column = new TreeTableColumn<>("Nodes");
column.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getValue()));
column.setPrefWidth(200);
TreeTableView<String> table = new TreeTableView<>(rootNode);
table.setShowRoot(false);
table.getColumns().add(column);
table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
table.getSelectionModel().select(level1);
assertEquals(0, table.getSelectionModel().getSelectedIndex());
assertEquals("Node 0", table.getSelectionModel().getSelectedItem().getValue());
assertEquals(0, table.getFocusModel().getFocusedIndex());
assertEquals("Node 0", table.getFocusModel().getFocusedItem().getValue());
level2.getChildren().add(new TreeItem<>("Node 4"));
assertEquals(0, table.getSelectionModel().getSelectedIndex());
assertEquals("Node 0", table.getSelectionModel().getSelectedItem().getValue());
assertEquals(0, table.getFocusModel().getFocusedIndex());
assertEquals("Node 0", table.getFocusModel().getFocusedItem().getValue());
}
@Test
public void testRemoveTreeItemFromCollapsedAncestorKeepsSelectedItem() {
TreeItem<String> rootNode = new TreeItem<>("Root");
rootNode.setExpanded(true);
TreeItem<String> level1 = new TreeItem<>("Node 0");
level1.setExpanded(false);
TreeItem<String> level2 = new TreeItem<>("Node 1");
level2.getChildren().add(new TreeItem<>("Node 2"));
level2.getChildren().add(new TreeItem<>("Node 3"));
level2.setExpanded(true);
rootNode.getChildren().add(level1);
rootNode.getChildren().add(new TreeItem<>("Node 4"));
level1.getChildren().add(level2);
TreeTableColumn<String, String> column = new TreeTableColumn<>("Nodes");
column.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getValue()));
column.setPrefWidth(200);
TreeTableView<String> table = new TreeTableView<>(rootNode);
table.setShowRoot(false);
table.getColumns().add(column);
table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
table.getSelectionModel().select(level1);
assertEquals(0, table.getSelectionModel().getSelectedIndex());
assertEquals("Node 0", table.getSelectionModel().getSelectedItem().getValue());
assertEquals(0, table.getFocusModel().getFocusedIndex());
assertEquals("Node 0", table.getFocusModel().getFocusedItem().getValue());
level2.getChildren().remove(0);
assertEquals(0, table.getSelectionModel().getSelectedIndex());
assertEquals("Node 0", table.getSelectionModel().getSelectedItem().getValue());
assertEquals(0, table.getFocusModel().getFocusedIndex());
assertEquals("Node 0", table.getFocusModel().getFocusedItem().getValue());
}
}
