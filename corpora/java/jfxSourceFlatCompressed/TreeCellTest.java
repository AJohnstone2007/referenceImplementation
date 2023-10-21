package test.javafx.scene.control;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.sun.javafx.tk.Toolkit;
import static javafx.scene.control.ControlShim.*;
import static org.junit.Assert.*;
import static test.com.sun.javafx.scene.control.infrastructure.ControlSkinFactory.*;
import static test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils.*;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.FocusModel;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.MultipleSelectionModelBaseShim;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeView.EditEvent;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.control.skin.TreeCellSkin;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;
import test.com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils;
public class TreeCellTest {
private TreeCell<String> cell;
private TreeView<String> tree;
private static final String ROOT = "Root";
private static final String APPLES = "Apples";
private static final String ORANGES = "Oranges";
private static final String PEARS = "Pears";
private TreeItem<String> root;
private TreeItem<String> apples;
private TreeItem<String> oranges;
private TreeItem<String> pears;
private StageLoader stageLoader;
@Before public void setup() {
cell = new TreeCell<String>();
root = new TreeItem<>(ROOT);
apples = new TreeItem<>(APPLES);
oranges = new TreeItem<>(ORANGES);
pears = new TreeItem<>(PEARS);
root.getChildren().addAll(apples, oranges, pears);
tree = new TreeView<String>(root);
root.setExpanded(true);
}
@After
public void cleanup() {
if (stageLoader != null) stageLoader.dispose();
}
@Test public void styleClassIs_tree_cell_byDefault() {
assertStyleClassContains(cell, "tree-cell");
}
@Test public void itemIsNullByDefault() {
assertNull(cell.getItem());
}
@Test public void treeViewIsNullByDefault() {
assertNull(cell.getTreeView());
assertNull(cell.treeViewProperty().get());
}
@Test public void updateTreeViewUpdatesTreeView() {
cell.updateTreeView(tree);
assertSame(tree, cell.getTreeView());
assertSame(tree, cell.treeViewProperty().get());
}
@Test public void canSetTreeViewBackToNull() {
cell.updateTreeView(tree);
cell.updateTreeView(null);
assertNull(cell.getTreeView());
assertNull(cell.treeViewProperty().get());
}
@Test public void treeViewPropertyReturnsCorrectBean() {
assertSame(cell, cell.treeViewProperty().getBean());
}
@Test public void treeViewPropertyNameIs_treeView() {
assertEquals("treeView", cell.treeViewProperty().getName());
}
@Test public void updateTreeViewWithNullFocusModelResultsInNoException() {
cell.updateTreeView(tree);
tree.setFocusModel(null);
cell.updateTreeView(new TreeView());
}
@Test public void updateTreeViewWithNullFocusModelResultsInNoException2() {
tree.setFocusModel(null);
cell.updateTreeView(tree);
cell.updateTreeView(new TreeView());
}
@Test public void updateTreeViewWithNullFocusModelResultsInNoException3() {
cell.updateTreeView(tree);
TreeView tree2 = new TreeView();
tree2.setFocusModel(null);
cell.updateTreeView(tree2);
}
@Test public void updateTreeViewWithNullSelectionModelResultsInNoException() {
cell.updateTreeView(tree);
tree.setSelectionModel(null);
cell.updateTreeView(new TreeView());
}
@Test public void updateTreeViewWithNullSelectionModelResultsInNoException2() {
tree.setSelectionModel(null);
cell.updateTreeView(tree);
cell.updateTreeView(new TreeView());
}
@Test public void updateTreeViewWithNullSelectionModelResultsInNoException3() {
cell.updateTreeView(tree);
TreeView tree2 = new TreeView();
tree2.setSelectionModel(null);
cell.updateTreeView(tree2);
}
@Test public void updateTreeViewWithNullItemsResultsInNoException() {
cell.updateTreeView(tree);
tree.setRoot(null);
cell.updateTreeView(new TreeView());
}
@Test public void updateTreeViewWithNullItemsResultsInNoException2() {
tree.setRoot(null);
cell.updateTreeView(tree);
cell.updateTreeView(new TreeView());
}
@Test public void updateTreeViewWithNullItemsResultsInNoException3() {
cell.updateTreeView(tree);
TreeView tree2 = new TreeView();
tree2.setRoot(null);
cell.updateTreeView(tree2);
}
@Test public void itemMatchesIndexWithinTreeItems() {
cell.updateIndex(0);
cell.updateTreeView(tree);
assertSame(ROOT, cell.getItem());
assertSame(root, cell.getTreeItem());
cell.updateIndex(1);
assertSame(APPLES, cell.getItem());
assertSame(apples, cell.getTreeItem());
}
@Test public void itemMatchesIndexWithinTreeItems2() {
cell.updateTreeView(tree);
cell.updateIndex(0);
assertSame(ROOT, cell.getItem());
assertSame(root, cell.getTreeItem());
cell.updateIndex(1);
assertSame(APPLES, cell.getItem());
assertSame(apples, cell.getTreeItem());
}
@Test public void itemIsNullWhenIndexIsOutOfRange() {
cell.updateIndex(50);
cell.updateTreeView(tree);
assertNull(cell.getItem());
}
@Test public void treeItemIsNullWhenIndexIsOutOfRange() {
cell.updateIndex(50);
cell.updateTreeView(tree);
assertNull(cell.getTreeItem());
}
@Test public void itemIsNullWhenIndexIsOutOfRange2() {
cell.updateTreeView(tree);
cell.updateIndex(50);
assertNull(cell.getItem());
}
@Ignore
@Test public void itemIsUpdatedWhenItWasOutOfRangeButUpdatesToTreeViewItemsMakesItInRange() {
cell.updateIndex(4);
cell.updateTreeView(tree);
root.getChildren().addAll(new TreeItem<String>("Pumpkin"), new TreeItem<>("Lemon"));
assertSame("Pumpkin", cell.getItem());
}
@Ignore
@Test public void itemIsUpdatedWhenItWasInRangeButUpdatesToTreeViewItemsMakesItOutOfRange() {
cell.updateIndex(2);
cell.updateTreeView(tree);
assertSame(ORANGES, cell.getItem());
root.getChildren().remove(oranges);
assertNull(cell.getTreeItem());
assertNull(cell.getItem());
}
@Ignore
@Test public void itemIsUpdatedWhenTreeViewItemsIsUpdated() {
cell.updateIndex(1);
cell.updateTreeView(tree);
assertSame(APPLES, cell.getItem());
assertSame(apples, cell.getTreeItem());
root.getChildren().set(0, new TreeItem<>("Lime"));
assertEquals("Lime", cell.getItem());
}
@Ignore
@Test public void itemIsUpdatedWhenTreeViewItemsHasNewItemInsertedBeforeIndex() {
cell.updateIndex(2);
cell.updateTreeView(tree);
assertSame(ORANGES, cell.getItem());
assertSame(oranges, cell.getTreeItem());
String previous = APPLES;
root.getChildren().add(0, new TreeItem<>("Lime"));
assertEquals(previous, cell.getItem());
}
@Ignore
@Test public void itemIsUpdatedWhenTreeViewItemsIsReplaced() {
cell.updateIndex(1);
cell.updateTreeView(tree);
root.getChildren().setAll(new TreeItem<>("Water"), new TreeItem<>("Juice"), new TreeItem<>("Soda"));
assertEquals("Water", cell.getItem());
}
@Test public void itemIsUpdatedWhenTreeViewIsReplaced() {
cell.updateIndex(2);
cell.updateTreeView(tree);
TreeItem<String> newRoot = new TreeItem<>();
newRoot.setExpanded(true);
newRoot.getChildren().setAll(new TreeItem<>("Water"), new TreeItem<>("Juice"), new TreeItem<>("Soda"));
TreeView<String> treeView2 = new TreeView<String>(newRoot);
cell.updateTreeView(treeView2);
assertEquals("Juice", cell.getItem());
}
@Test public void replaceItemsWithANull() {
cell.updateIndex(0);
cell.updateTreeView(tree);
tree.setRoot(null);
assertNull(cell.getItem());
}
@Test public void replaceANullItemsWithNotNull() {
cell.updateIndex(1);
cell.updateTreeView(tree);
tree.setRoot(null);
TreeItem<String> newRoot = new TreeItem<>();
newRoot.setExpanded(true);
newRoot.getChildren().setAll(new TreeItem<>("Water"), new TreeItem<>("Juice"), new TreeItem<>("Soda"));
tree.setRoot(newRoot);
assertEquals("Water", cell.getItem());
}
@Test public void selectionOnSelectionModelIsReflectedInCells() {
cell.updateTreeView(tree);
cell.updateIndex(0);
TreeCell<String> other = new TreeCell<String>();
other.updateTreeView(tree);
other.updateIndex(1);
tree.getSelectionModel().selectFirst();
assertTrue(cell.isSelected());
assertFalse(other.isSelected());
}
@Test public void changesToSelectionOnSelectionModelAreReflectedInCells() {
cell.updateTreeView(tree);
cell.updateIndex(0);
TreeCell<String> other = new TreeCell<String>();
other.updateTreeView(tree);
other.updateIndex(1);
tree.getSelectionModel().selectFirst();
tree.getSelectionModel().selectNext();
assertFalse(cell.isSelected());
assertTrue(other.isSelected());
}
@Test public void replacingTheSelectionModelCausesSelectionOnCellsToBeUpdated() {
cell.updateTreeView(tree);
cell.updateIndex(0);
tree.getSelectionModel().select(0);
TreeCell<String> other = new TreeCell<String>();
other.updateTreeView(tree);
other.updateIndex(1);
MultipleSelectionModel<TreeItem<String>> selectionModel = new SelectionModelMock();
selectionModel.select(1);
tree.setSelectionModel(selectionModel);
assertFalse(cell.isSelected());
assertTrue(other.isSelected());
}
@Test public void changesToSelectionOnSelectionModelAreReflectedInCells_MultipleSelection() {
tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
cell.updateTreeView(tree);
cell.updateIndex(0);
TreeCell<String> other = new TreeCell<String>();
other.updateTreeView(tree);
other.updateIndex(1);
tree.getSelectionModel().selectFirst();
tree.getSelectionModel().selectNext();
assertTrue(cell.isSelected());
assertTrue(other.isSelected());
}
@Test public void replacingTheSelectionModelCausesSelectionOnCellsToBeUpdated_MultipleSelection() {
cell.updateTreeView(tree);
cell.updateIndex(0);
tree.getSelectionModel().select(0);
TreeCell<String> other = new TreeCell<String>();
other.updateTreeView(tree);
other.updateIndex(1);
MultipleSelectionModel<TreeItem<String>> selectionModel = new SelectionModelMock();
selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
selectionModel.selectIndices(0, 1);
tree.setSelectionModel(selectionModel);
assertTrue(cell.isSelected());
assertTrue(other.isSelected());
}
@Test public void replaceANullSelectionModel() {
tree.setSelectionModel(null);
cell.updateIndex(0);
cell.updateTreeView(tree);
TreeCell<String> other = new TreeCell<String>();
other.updateTreeView(tree);
other.updateIndex(1);
MultipleSelectionModel<TreeItem<String>> selectionModel = new SelectionModelMock();
selectionModel.select(1);
tree.setSelectionModel(selectionModel);
assertFalse(cell.isSelected());
assertTrue(other.isSelected());
}
@Test public void setANullSelectionModel() {
cell.updateIndex(0);
cell.updateTreeView(tree);
TreeCell<String> other = new TreeCell<String>();
other.updateTreeView(tree);
other.updateIndex(1);
tree.setSelectionModel(null);
assertFalse(cell.isSelected());
assertFalse(other.isSelected());
}
@Ignore @Test public void replacingTheSelectionModelRemovesTheListenerFromTheOldModel() {
cell.updateIndex(0);
cell.updateTreeView(tree);
MultipleSelectionModel<TreeItem<String>> sm = tree.getSelectionModel();
ListChangeListener listener = getListChangeListener(cell, "weakSelectedListener");
assertListenerListContains(sm.getSelectedIndices(), listener);
tree.setSelectionModel(new SelectionModelMock());
assertListenerListDoesNotContain(sm.getSelectedIndices(), listener);
}
@Test public void focusOnFocusModelIsReflectedInCells() {
cell.updateTreeView(tree);
cell.updateIndex(0);
TreeCell<String> other = new TreeCell<String>();
other.updateTreeView(tree);
other.updateIndex(1);
tree.getFocusModel().focus(0);
assertTrue(cell.isFocused());
assertFalse(other.isFocused());
}
@Test public void changesToFocusOnFocusModelAreReflectedInCells() {
cell.updateTreeView(tree);
cell.updateIndex(0);
TreeCell<String> other = new TreeCell<String>();
other.updateTreeView(tree);
other.updateIndex(1);
tree.getFocusModel().focus(0);
tree.getFocusModel().focus(1);
assertFalse(cell.isFocused());
assertTrue(other.isFocused());
}
@Test public void replacingTheFocusModelCausesFocusOnCellsToBeUpdated() {
cell.updateTreeView(tree);
cell.updateIndex(0);
tree.getFocusModel().focus(0);
TreeCell<String> other = new TreeCell<String>();
other.updateTreeView(tree);
other.updateIndex(1);
FocusModel<TreeItem<String>> focusModel = new FocusModelMock();
focusModel.focus(1);
tree.setFocusModel(focusModel);
assertFalse(cell.isFocused());
assertTrue(other.isFocused());
}
@Test public void replaceANullFocusModel() {
tree.setFocusModel(null);
cell.updateIndex(0);
cell.updateTreeView(tree);
TreeCell<String> other = new TreeCell<String>();
other.updateTreeView(tree);
other.updateIndex(1);
FocusModel<TreeItem<String>> focusModel = new FocusModelMock();
focusModel.focus(1);
tree.setFocusModel(focusModel);
assertFalse(cell.isFocused());
assertTrue(other.isFocused());
}
@Test public void setANullFocusModel() {
cell.updateIndex(0);
cell.updateTreeView(tree);
TreeCell<String> other = new TreeCell<String>();
other.updateTreeView(tree);
other.updateIndex(1);
tree.setFocusModel(null);
assertFalse(cell.isSelected());
assertFalse(other.isSelected());
}
@Test public void replacingTheFocusModelRemovesTheListenerFromTheOldModel() {
cell.updateIndex(0);
cell.updateTreeView(tree);
FocusModel<TreeItem<String>> fm = tree.getFocusModel();
InvalidationListener listener = getInvalidationListener(cell, "weakFocusedListener");
assertValueListenersContains(fm.focusedIndexProperty(), listener);
tree.setFocusModel(new FocusModelMock());
assertValueListenersDoesNotContain(fm.focusedIndexProperty(), listener);
}
@Test public void editOnTreeViewResultsInEditingInCell() {
tree.setEditable(true);
cell.updateTreeView(tree);
cell.updateIndex(1);
tree.edit(apples);
assertTrue(cell.isEditing());
}
@Test public void editOnTreeViewResultsInNotEditingInCellWhenDifferentIndex() {
tree.setEditable(true);
cell.updateTreeView(tree);
cell.updateIndex(1);
tree.edit(root);
assertFalse(cell.isEditing());
}
@Test public void editCellWithNullTreeViewResultsInNoExceptions() {
cell.updateIndex(1);
cell.startEdit();
}
@Test public void editCellOnNonEditableTreeDoesNothing() {
cell.updateIndex(1);
cell.updateTreeView(tree);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(tree.getEditingItem());
}
@Test public void editCellWithTreeResultsInUpdatedEditingIndexProperty() {
tree.setEditable(true);
cell.updateTreeView(tree);
cell.updateIndex(1);
cell.startEdit();
assertEquals(apples, tree.getEditingItem());
}
@Test public void editCellFiresEventOnTree() {
tree.setEditable(true);
cell.updateTreeView(tree);
cell.updateIndex(2);
final boolean[] called = new boolean[] { false };
tree.setOnEditStart(event -> {
called[0] = true;
});
cell.startEdit();
assertTrue(called[0]);
}
@Test public void commitWhenTreeIsNullIsOK() {
cell.updateIndex(1);
cell.startEdit();
cell.commitEdit("Watermelon");
}
@Test public void commitWhenTreeIsNotNullWillUpdateTheItemsTree() {
tree.setEditable(true);
cell.updateTreeView(tree);
cell.updateIndex(1);
cell.startEdit();
cell.commitEdit("Watermelon");
assertEquals("Watermelon", tree.getRoot().getChildren().get(0).getValue());
}
@Test public void commitSendsEventToTree() {
tree.setEditable(true);
cell.updateTreeView(tree);
cell.updateIndex(1);
cell.startEdit();
final boolean[] called = new boolean[] { false };
tree.setOnEditCommit(event -> {
called[0] = true;
});
cell.commitEdit("Watermelon");
assertTrue(called[0]);
}
@Test public void afterCommitTreeViewEditingIndexIsNegativeOne() {
tree.setEditable(true);
cell.updateTreeView(tree);
cell.updateIndex(1);
cell.startEdit();
cell.commitEdit("Watermelon");
assertNull(tree.getEditingItem());
assertFalse(cell.isEditing());
}
@Test public void cancelEditCanBeCalledWhileTreeViewIsNull() {
cell.updateIndex(1);
cell.startEdit();
cell.cancelEdit();
}
@Test public void cancelEditFiresChangeEvent() {
tree.setEditable(true);
cell.updateTreeView(tree);
cell.updateIndex(1);
cell.startEdit();
final boolean[] called = new boolean[] { false };
tree.setOnEditCancel(event -> {
called[0] = true;
});
cell.cancelEdit();
assertTrue(called[0]);
}
@Test public void cancelSetsTreeViewEditingIndexToNegativeOne() {
tree.setEditable(true);
cell.updateTreeView(tree);
cell.updateIndex(1);
cell.startEdit();
cell.cancelEdit();
assertNull(tree.getEditingItem());
assertFalse(cell.isEditing());
}
@Test public void movingTreeCellEditingIndexCausesCurrentlyInEditCellToCancel() {
tree.setEditable(true);
cell.updateTreeView(tree);
cell.updateIndex(0);
cell.startEdit();
TreeCell other = new TreeCell();
other.updateTreeView(tree);
other.updateIndex(1);
tree.edit(apples);
assertTrue(other.isEditing());
assertFalse(cell.isEditing());
}
@Test
public void testEditCancelEventAfterCancelOnCell() {
tree.setEditable(true);
cell.updateTreeView(tree);
int editingIndex = 1;
TreeItem<String> editingItem = tree.getTreeItem(editingIndex);
cell.updateIndex(editingIndex);
tree.edit(editingItem);
List<EditEvent<String>> events = new ArrayList<>();
tree.setOnEditCancel(events::add);
cell.cancelEdit();
assertEquals(1, events.size());
assertEquals("editing location of cancel event", editingItem, events.get(0).getTreeItem());
}
@Test
public void testEditCancelEventAfterCancelOnTree() {
tree.setEditable(true);
cell.updateTreeView(tree);
int editingIndex = 1;
TreeItem<String> editingItem = tree.getTreeItem(editingIndex);
cell.updateIndex(editingIndex);
tree.edit(editingItem);
List<EditEvent<String>> events = new ArrayList<>();
tree.setOnEditCancel(events::add);
tree.edit(null);
assertEquals(1, events.size());
assertEquals("editing location of cancel event", editingItem, events.get(0).getTreeItem());
}
@Test
public void testEditCancelEventAfterCellReuse() {
tree.setEditable(true);
cell.updateTreeView(tree);
int editingIndex = 1;
TreeItem<String> editingItem = tree.getTreeItem(editingIndex);
cell.updateIndex(editingIndex);
tree.edit(editingItem);
List<EditEvent<String>> events = new ArrayList<>();
tree.setOnEditCancel(events::add);
cell.updateIndex(0);
assertEquals(1, events.size());
assertEquals("editing location of cancel event", editingItem, events.get(0).getTreeItem());
}
@Test
public void testEditCancelEventAfterCollapse() {
stageLoader = new StageLoader(tree);
tree.setEditable(true);
int editingIndex = 1;
TreeItem<String> editingItem = tree.getTreeItem(editingIndex);
tree.edit(editingItem);
List<EditEvent<String>> events = new ArrayList<>();
tree.setOnEditCancel(events::add);
root.setExpanded(false);
Toolkit.getToolkit().firePulse();
assertEquals(1, events.size());
assertEquals("editing location of cancel event", editingItem, events.get(0).getTreeItem());
}
@Test
public void testEditCancelEventAfterModifyItems() {
stageLoader = new StageLoader(tree);
tree.setEditable(true);
int editingIndex = 2;
TreeItem<String> editingItem = tree.getTreeItem(editingIndex);
tree.edit(editingItem);
List<EditEvent<String>> events = new ArrayList<>();
tree.setOnEditCancel(events::add);
root.getChildren().add(0, new TreeItem<>("added"));
Toolkit.getToolkit().firePulse();
assertEquals(1, events.size());
assertEquals("editing location of cancel event", editingItem, events.get(0).getTreeItem());
}
@Test
public void testEditCancelEventAfterRemoveEditingItem() {
stageLoader = new StageLoader(tree);
tree.setEditable(true);
int editingIndex = 2;
TreeItem<String> editingItem = tree.getTreeItem(editingIndex);
tree.edit(editingItem);
List<EditEvent<String>> events = new ArrayList<>();
tree.setOnEditCancel(events::add);
root.getChildren().remove(editingItem);
Toolkit.getToolkit().firePulse();
assertNull("removing item must cancel edit on tree", tree.getEditingItem());
assertEquals(1, events.size());
assertEquals("editing location of cancel event", editingItem, events.get(0).getTreeItem());
}
@Test
public void testEditCancelMemoryLeakAfterRemoveEditingItem() {
stageLoader = new StageLoader(tree);
tree.setEditable(true);
TreeItem<String> editingItem = new TreeItem<>("added");
WeakReference<TreeItem<?>> itemRef = new WeakReference<>(editingItem);
root.getChildren().add(0, editingItem);
Toolkit.getToolkit().firePulse();
tree.edit(editingItem);
root.getChildren().remove(editingItem);
Toolkit.getToolkit().firePulse();
assertNull("removing item must cancel edit on tree", tree.getEditingItem());
editingItem = null;
attemptGC(itemRef);
assertEquals("treeItem must be gc'ed", null, itemRef.get());
}
@Test
public void testEditCommitMemoryLeakAfterRemoveEditingItem() {
stageLoader = new StageLoader(tree);
tree.setEditable(true);
TreeItem<String> editingItem = new TreeItem<>("added");
WeakReference<TreeItem<?>> itemRef = new WeakReference<>(editingItem);
root.getChildren().add(0, editingItem);
int editingIndex = tree.getRow(editingItem);
Toolkit.getToolkit().firePulse();
tree.edit(editingItem);
TreeCell<String> editingCell = (TreeCell<String>) VirtualFlowTestUtils.getCell(tree, editingIndex);
editingCell.commitEdit("added changed");
root.getChildren().remove(editingItem);
Toolkit.getToolkit().firePulse();
assertNull("removing item must cancel edit on tree", tree.getEditingItem());
editingItem = null;
attemptGC(itemRef);
assertEquals("treeItem must be gc'ed", null, itemRef.get());
}
@Test
public void testStartEditOffRangeMustNotFireStartEdit() {
tree.setEditable(true);
cell.updateTreeView(tree);
cell.updateTreeItem(new TreeItem<>("not-contained"));
List<EditEvent<?>> events = new ArrayList<>();
tree.addEventHandler(TreeView.editStartEvent(), events::add);
cell.startEdit();
assertFalse("sanity: off-range cell must not be editing", cell.isEditing());
assertEquals("cell must not fire editStart if not editing", 0, events.size());
}
@Test
public void testStartEditOffRangeMustNotUpdateEditingLocation() {
tree.setEditable(true);
cell.updateTreeView(tree);
cell.updateTreeItem(new TreeItem<>("not-contained"));
cell.startEdit();
assertFalse("sanity: off-range cell must not be editing", cell.isEditing());
assertNull("tree editing location must not be updated", tree.getEditingItem());
}
@Test
public void testCommitEditMustNotFireCancel() {
tree.setEditable(true);
int editingIndex = 1;
TreeItem<String> editingItem = tree.getTreeItem(editingIndex);
tree.setOnEditCommit(e -> {
editingItem.setValue(e.getNewValue());
tree.edit(null);
});
cell.updateTreeView(tree);
cell.updateIndex(editingIndex);
List<EditEvent<String>> events = new ArrayList<>();
tree.setOnEditCancel(events::add);
tree.edit(editingItem);
String value = "edited";
cell.commitEdit(value);
assertEquals("sanity: value committed", value, tree.getTreeItem(editingIndex).getValue());
assertEquals("commit must not have fired editCancel", 0, events.size());
}
@Test
public void testTreeHasDefaultCommitHandler() {
assertNotNull("treeView must have default commit handler", tree.getOnEditCommit());
}
@Test
public void testDefaultCommitUpdatesData() {
TreeItem<String> editingItem = setupForEditing(cell);
tree.edit(editingItem);
String value = "edited";
cell.commitEdit(value);
assertEquals("value committed", value, editingItem.getValue());
}
@Test
public void testDefaultCommitUpdatesCell() {
TreeCell<String> cell = TextFieldTreeCell.forTreeView().call(tree);
TreeItem<String> editingItem = setupForEditing(cell);
tree.edit(editingItem);
String value = "edited";
cell.commitEdit(value);
assertEquals("cell text updated to committed value", value, cell.getText());
}
@Test
public void testDoNothingCommitHandlerDoesNotUpdateData() {
TreeItem<String> editingItem = setupForEditing(cell);
String oldValue = editingItem.getValue();
tree.setOnEditCommit(e -> {});
tree.edit(editingItem);
String value = "edited";
cell.commitEdit(value);
assertEquals("edited value must not be committed", oldValue, editingItem.getValue());
}
@Ignore("JDK-8187314")
@Test
public void testDoNothingCommitHandlerDoesNotUpdateCell() {
TreeCell<String> cell = TextFieldTreeCell.forTreeView().call(tree);
TreeItem<String> editingItem = setupForEditing(cell);
String oldValue = editingItem.getValue();
tree.setOnEditCommit(e -> {});
tree.edit(editingItem);
String value = "edited";
cell.commitEdit(value);
assertEquals("cell text must not have changed", oldValue, cell.getText());
}
private TreeItem<String> setupForEditing(TreeCell<String> editingCell) {
tree.setEditable(true);
editingCell.updateTreeView(tree);
editingCell.updateIndex(1);
return editingCell.getTreeItem();
}
@Test
public void testSetupForEditing() {
TreeCell<String> cell = new TreeCell<>();
TreeItem<String> cellTreeItem = setupForEditing(cell);
assertTrue("sanity: tree must be editable", tree.isEditable());
assertEquals("sanity: returned treeItem", cellTreeItem, cell.getTreeItem());
assertEquals(1, cell.getIndex());
assertEquals("sanity: cell configured with tree's treeItem at index",
tree.getTreeItem(cell.getIndex()), cell.getTreeItem());
assertNull("sanity: config doesn't change tree state", tree.getEditingItem());
}
@Test public void test_rt_33106() {
cell.updateTreeView(tree);
tree.setRoot(null);
cell.updateIndex(1);
}
private final class SelectionModelMock extends MultipleSelectionModelBaseShim<TreeItem<String>> {
@Override protected int getItemCount() {
return root.getChildren().size() + 1;
}
@Override protected TreeItem<String> getModelItem(int index) {
return index == 0 ? root : root.getChildren().get(index - 1);
}
@Override protected void focus(int index) {
}
@Override protected int getFocusedIndex() {
return tree.getFocusModel().getFocusedIndex();
}
};
private final class FocusModelMock extends FocusModel {
@Override protected int getItemCount() {
return root.getChildren().size() + 1;
}
@Override protected TreeItem<String> getModelItem(int index) {
return index == 0 ? root : root.getChildren().get(index - 1);
}
}
@Test public void test_jdk_8151524() {
TreeCell cell = new TreeCell();
cell.setSkin(new TreeCellSkin(cell));
}
@Test
public void testTreeCellHeights() {
TreeCell<Object> cell = new TreeCell<>();
TreeView<Object> treeView = new TreeView<>();
cell.updateTreeView(treeView);
installDefaultSkin(cell);
treeView.setFixedCellSize(100);
assertEquals("pref height must be fixedCellSize",
treeView.getFixedCellSize(),
cell.prefHeight(-1), 1);
assertEquals("min height must be fixedCellSize",
treeView.getFixedCellSize(),
cell.minHeight(-1), 1);
assertEquals("max height must be fixedCellSize",
treeView.getFixedCellSize(),
cell.maxHeight(-1), 1);
}
}
