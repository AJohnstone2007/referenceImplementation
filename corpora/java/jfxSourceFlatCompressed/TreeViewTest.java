package test.javafx.scene.control;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.scene.control.VirtualScrollBar;
import com.sun.javafx.scene.control.behavior.TreeCellBehavior;
import com.sun.javafx.tk.Toolkit;
import static org.junit.Assert.*;
import static test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils.*;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.FocusModel;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeCellShim;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeViewShim;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Callback;
import test.com.sun.javafx.scene.control.infrastructure.KeyEventFirer;
import test.com.sun.javafx.scene.control.infrastructure.KeyModifier;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;
import test.com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils;
import test.com.sun.javafx.scene.control.test.Employee;
import test.com.sun.javafx.scene.control.test.Person;
import test.com.sun.javafx.scene.control.test.RT_22463_Person;
import test.javafx.collections.MockListObserver;
public class TreeViewTest {
private TreeView<String> treeView;
private MultipleSelectionModel<TreeItem<String>> sm;
private FocusModel<TreeItem<String>> fm;
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
private String debug() {
StringBuilder sb = new StringBuilder("Selected Indices: [");
List<Integer> indices = sm.getSelectedIndices();
for (Integer index : indices) {
sb.append(index);
sb.append(", ");
}
sb.append("] \nFocus: " + fm.getFocusedIndex());
return sb.toString();
}
@Before public void setup() {
Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
if (throwable instanceof RuntimeException) {
throw (RuntimeException)throwable;
} else {
Thread.currentThread().getThreadGroup().uncaughtException(thread, throwable);
}
});
treeView = new TreeView<String>();
sm = treeView.getSelectionModel();
fm = treeView.getFocusModel();
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
@After
public void cleanup() {
Thread.currentThread().setUncaughtExceptionHandler(null);
}
private void installChildren() {
root = new TreeItem<String>("Root");
child1 = new TreeItem<String>("Child 1");
child2 = new TreeItem<String>("Child 2");
child3 = new TreeItem<String>("Child 3");
root.setExpanded(true);
root.getChildren().setAll(child1, child2, child3);
treeView.setRoot(root);
}
@Test public void ensureCorrectInitialState() {
installChildren();
assertEquals(0, treeView.getRow(root));
assertEquals(1, treeView.getRow(child1));
assertEquals(2, treeView.getRow(child2));
assertEquals(3, treeView.getRow(child3));
}
@Test public void noArgConstructorSetsTheStyleClass() {
assertStyleClassContains(treeView, "tree-view");
}
@Test public void noArgConstructorSetsNonNullSelectionModel() {
assertNotNull(treeView.getSelectionModel());
}
@Test public void noArgConstructorSetsNullItems() {
assertNull(treeView.getRoot());
}
@Test public void noArgConstructor_selectedItemIsNull() {
assertNull(treeView.getSelectionModel().getSelectedItem());
}
@Test public void noArgConstructor_selectedIndexIsNegativeOne() {
assertEquals(-1, treeView.getSelectionModel().getSelectedIndex());
}
@Test public void singleArgConstructorSetsTheStyleClass() {
final TreeView<String> b2 = new TreeView<>(new TreeItem<>("Hi"));
assertStyleClassContains(b2, "tree-view");
}
@Test public void singleArgConstructorSetsNonNullSelectionModel() {
final TreeView<String> b2 = new TreeView<>(new TreeItem<>("Hi"));
assertNotNull(b2.getSelectionModel());
}
@Test public void singleArgConstructorAllowsNullItems() {
final TreeView<String> b2 = new TreeView<>(null);
assertNull(b2.getRoot());
}
@Test public void singleArgConstructor_selectedItemIsNotNull() {
TreeItem<String> hiItem = new TreeItem<>("Hi");
final TreeView<String> b2 = new TreeView<>(hiItem);
assertNull(b2.getSelectionModel().getSelectedItem());
}
@Test public void singleArgConstructor_selectedIndexIsZero() {
final TreeView<String> b2 = new TreeView<>(new TreeItem<>("Hi"));
assertEquals(-1, b2.getSelectionModel().getSelectedIndex());
}
@Test public void selectionModelCanBeNull() {
treeView.setSelectionModel(null);
assertNull(treeView.getSelectionModel());
}
@Test public void selectionModelCanBeBound() {
MultipleSelectionModel<TreeItem<String>> sm =
TreeViewShim.<String>get_TreeViewBitSetSelectionModel(treeView);
ObjectProperty<MultipleSelectionModel<TreeItem<String>>> other = new SimpleObjectProperty<MultipleSelectionModel<TreeItem<String>>>(sm);
treeView.selectionModelProperty().bind(other);
assertSame(sm, treeView.getSelectionModel());
}
@Test public void selectionModelCanBeChanged() {
MultipleSelectionModel<TreeItem<String>> sm =
TreeViewShim.<String>get_TreeViewBitSetSelectionModel(treeView);
treeView.setSelectionModel(sm);
assertSame(sm, treeView.getSelectionModel());
}
@Test public void canSetSelectedItemToAnItemEvenWhenThereAreNoItems() {
TreeItem<String> element = new TreeItem<String>("I AM A CRAZY RANDOM STRING");
treeView.getSelectionModel().select(element);
assertEquals(-1, treeView.getSelectionModel().getSelectedIndex());
assertSame(element, treeView.getSelectionModel().getSelectedItem());
}
@Test public void canSetSelectedItemToAnItemNotInTheDataModel() {
installChildren();
TreeItem<String> element = new TreeItem<String>("I AM A CRAZY RANDOM STRING");
treeView.getSelectionModel().select(element);
assertEquals(-1, treeView.getSelectionModel().getSelectedIndex());
assertSame(element, treeView.getSelectionModel().getSelectedItem());
}
@Test public void settingTheSelectedItemToAnItemInItemsResultsInTheCorrectSelectedIndex() {
installChildren();
treeView.getSelectionModel().select(child1);
assertEquals(1, treeView.getSelectionModel().getSelectedIndex());
assertSame(child1, treeView.getSelectionModel().getSelectedItem());
}
@Ignore("Not yet supported")
@Test public void settingTheSelectedItemToANonexistantItemAndThenSettingItemsWhichContainsItResultsInCorrectSelectedIndex() {
treeView.getSelectionModel().select(child1);
installChildren();
assertEquals(1, treeView.getSelectionModel().getSelectedIndex());
assertSame(child1, treeView.getSelectionModel().getSelectedItem());
}
@Ignore("Not yet supported")
@Test public void ensureSelectionClearsWhenAllItemsAreRemoved_selectIndex0() {
installChildren();
treeView.getSelectionModel().select(0);
treeView.setRoot(null);
assertEquals(-1, treeView.getSelectionModel().getSelectedIndex());
assertEquals(null, treeView.getSelectionModel().getSelectedItem());
}
@Ignore("Not yet supported")
@Test public void ensureSelectionClearsWhenAllItemsAreRemoved_selectIndex2() {
installChildren();
treeView.getSelectionModel().select(2);
treeView.setRoot(null);
assertEquals(-1, treeView.getSelectionModel().getSelectedIndex());
assertEquals(null, treeView.getSelectionModel().getSelectedItem());
}
@Ignore("Not yet supported")
@Test public void ensureSelectedItemRemainsAccurateWhenItemsAreCleared() {
installChildren();
treeView.getSelectionModel().select(2);
treeView.setRoot(null);
assertNull(treeView.getSelectionModel().getSelectedItem());
assertEquals(-1, treeView.getSelectionModel().getSelectedIndex());
TreeItem<String> newRoot = new TreeItem<String>("New Root");
TreeItem<String> newChild1 = new TreeItem<String>("New Child 1");
TreeItem<String> newChild2 = new TreeItem<String>("New Child 2");
TreeItem<String> newChild3 = new TreeItem<String>("New Child 3");
newRoot.setExpanded(true);
newRoot.getChildren().setAll(newChild1, newChild2, newChild3);
treeView.setRoot(root);
treeView.getSelectionModel().select(2);
assertEquals(newChild2, treeView.getSelectionModel().getSelectedItem());
}
@Test public void ensureSelectionIsCorrectWhenItemsChange() {
installChildren();
treeView.getSelectionModel().select(0);
assertEquals(root, treeView.getSelectionModel().getSelectedItem());
TreeItem newRoot = new TreeItem<>("New Root");
treeView.setRoot(newRoot);
assertEquals(-1, treeView.getSelectionModel().getSelectedIndex());
assertNull(treeView.getSelectionModel().getSelectedItem());
}
@Test public void ensureSelectionRemainsOnBranchWhenExpanded() {
installChildren();
root.setExpanded(false);
treeView.getSelectionModel().select(0);
assertTrue(treeView.getSelectionModel().isSelected(0));
root.setExpanded(true);
assertTrue(treeView.getSelectionModel().isSelected(0));
assertTrue(treeView.getSelectionModel().getSelectedItems().contains(root));
}
@Test public void ensureRootIndexIsZeroWhenRootIsShowing() {
installChildren();
assertEquals(0, treeView.getRow(root));
}
@Test public void ensureRootIndexIsNegativeOneWhenRootIsNotShowing() {
installChildren();
treeView.setShowRoot(false);
assertEquals(-1, treeView.getRow(root));
}
@Test public void ensureCorrectIndexWhenRootTreeItemHasParent() {
installChildren();
treeView.setRoot(child1);
assertEquals(-1, treeView.getRow(root));
assertEquals(0, treeView.getRow(child1));
assertEquals(1, treeView.getRow(child2));
assertEquals(2, treeView.getRow(child3));
}
@Test public void ensureCorrectIndexWhenRootTreeItemHasParentAndRootIsNotShowing() {
installChildren();
treeView.setRoot(child1);
treeView.setShowRoot(false);
assertEquals(0, treeView.getExpandedItemCount());
assertEquals(-1, treeView.getRow(root));
assertEquals(-1, treeView.getRow(child1));
assertEquals(-1, treeView.getRow(child2));
assertEquals(-1, treeView.getRow(child3));
}
@Test public void ensureCorrectIndexWhenRootTreeItemIsCollapsed() {
installChildren();
root.setExpanded(false);
assertEquals(0, treeView.getRow(root));
assertEquals(-1, treeView.getRow(child1));
assertEquals(-1, treeView.getRow(child2));
assertEquals(-1, treeView.getRow(child3));
}
@Test public void removingLastTest() {
TreeView tree_view = new TreeView();
MultipleSelectionModel sm = tree_view.getSelectionModel();
TreeItem<String> tree_model = new TreeItem<String>("Root");
TreeItem node = new TreeItem("Data item");
tree_model.getChildren().add(node);
tree_view.setRoot(tree_model);
tree_model.setExpanded(true);
sm.select(tree_model.getChildren().get(0));
tree_model.getChildren().remove(sm.getSelectedItem());
assertEquals(tree_model, sm.getSelectedItem());
}
@Ignore @Test public void test_rt17112() {
TreeItem<String> root1 = new TreeItem<String>("Root");
root1.setExpanded(true);
addChildren(root1, "child");
for (TreeItem child : root1.getChildren()) {
addChildren(child, (String)child.getValue());
child.setExpanded(true);
}
final TreeView treeView1 = new TreeView();
final MultipleSelectionModel sm = treeView1.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
treeView1.setRoot(root1);
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
assertTrue(treeView1.getFocusModel().isFocused(6));
} else if (count == 1) {
assertEquals(rt17112_child1, sm.getSelectedItem());
assertFalse(sm.getSelectedItems().contains(rt17112_child2));
assertEquals(1, sm.getSelectedIndices().size());
assertTrue(treeView1.getFocusModel().isFocused(5));
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
@Test public void test_rt17522_focusShouldMoveWhenItemAddedAtFocusIndex() {
installChildren();
FocusModel fm = treeView.getFocusModel();
fm.focus(1);
assertTrue(fm.isFocused(1));
assertEquals(child1, fm.getFocusedItem());
TreeItem child0 = new TreeItem("child0");
root.getChildren().add(0, child0);
assertEquals(child1, fm.getFocusedItem());
assertTrue(fm.isFocused(2));
}
@Test public void test_rt17522_focusShouldMoveWhenItemAddedBeforeFocusIndex() {
installChildren();
FocusModel fm = treeView.getFocusModel();
fm.focus(1);
assertTrue(fm.isFocused(1));
TreeItem child0 = new TreeItem("child0");
root.getChildren().add(0, child0);
assertTrue("Focused index: " + fm.getFocusedIndex(), fm.isFocused(2));
}
@Test public void test_rt17522_focusShouldNotMoveWhenItemAddedAfterFocusIndex() {
installChildren();
FocusModel fm = treeView.getFocusModel();
fm.focus(1);
assertTrue(fm.isFocused(1));
TreeItem child4 = new TreeItem("child4");
root.getChildren().add(3, child4);
assertTrue("Focused index: " + fm.getFocusedIndex(), fm.isFocused(1));
}
@Test public void test_rt17522_focusShouldBeMovedWhenFocusedItemIsRemoved_1() {
installChildren();
FocusModel fm = treeView.getFocusModel();
fm.focus(1);
assertTrue(fm.isFocused(1));
root.getChildren().remove(child1);
assertEquals(0, fm.getFocusedIndex());
assertEquals(treeView.getTreeItem(0), fm.getFocusedItem());
}
@Test public void test_rt17522_focusShouldMoveWhenItemRemovedBeforeFocusIndex() {
installChildren();
FocusModel fm = treeView.getFocusModel();
fm.focus(2);
assertTrue(fm.isFocused(2));
root.getChildren().remove(child1);
assertTrue(fm.isFocused(1));
assertEquals(child2, fm.getFocusedItem());
}
@Test public void test_rt18385() {
installChildren();
treeView.getSelectionModel().select(1);
treeView.getRoot().getChildren().add(new TreeItem("Another Row"));
assertEquals(1, treeView.getSelectionModel().getSelectedIndices().size());
assertEquals(1, treeView.getSelectionModel().getSelectedItems().size());
}
@Test public void test_rt18339_onlyEditWhenTreeViewIsEditable_editableIsFalse() {
treeView.setEditable(false);
treeView.edit(root);
assertEquals(null, treeView.getEditingItem());
}
@Test public void test_rt18339_onlyEditWhenTreeViewIsEditable_editableIsTrue() {
treeView.setEditable(true);
treeView.edit(root);
assertEquals(root, treeView.getEditingItem());
}
@Test public void test_rt14451() {
installChildren();
treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
treeView.getSelectionModel().selectRange(0, 2);
assertEquals(2, treeView.getSelectionModel().getSelectedIndices().size());
}
@Test public void test_rt21586() {
installChildren();
treeView.getSelectionModel().select(1);
assertEquals(1, treeView.getSelectionModel().getSelectedIndex());
assertEquals(child1, treeView.getSelectionModel().getSelectedItem());
TreeItem root = new TreeItem<>("New Root");
TreeItem child1 = new TreeItem<>("New Child 1");
TreeItem child2 = new TreeItem<>("New Child 2");
TreeItem child3 = new TreeItem<>("New Child 3");
root.setExpanded(true);
root.getChildren().setAll(child1, child2, child3);
treeView.setRoot(root);
assertEquals(-1, treeView.getSelectionModel().getSelectedIndex());
assertNull(treeView.getSelectionModel().getSelectedItem());
}
@Test public void test_rt27181() {
myCompanyRootNode.setExpanded(true);
treeView.setRoot(myCompanyRootNode);
salesDepartment.setExpanded(true);
treeView.getSelectionModel().select(salesDepartment);
assertEquals(1, treeView.getFocusModel().getFocusedIndex());
itSupport.setExpanded(true);
assertEquals(1, treeView.getFocusModel().getFocusedIndex());
}
@Test public void test_rt27185() {
myCompanyRootNode.setExpanded(true);
treeView.setRoot(myCompanyRootNode);
itSupport.setExpanded(true);
treeView.getSelectionModel().select(mikeGraham);
assertEquals(mikeGraham, treeView.getFocusModel().getFocusedItem());
salesDepartment.setExpanded(true);
assertEquals(mikeGraham, treeView.getFocusModel().getFocusedItem());
}
@Test public void test_rt28114() {
myCompanyRootNode.setExpanded(true);
treeView.setRoot(myCompanyRootNode);
itSupport.setExpanded(true);
treeView.getSelectionModel().select(itSupport);
assertEquals(itSupport, treeView.getFocusModel().getFocusedItem());
assertEquals(itSupport, treeView.getSelectionModel().getSelectedItem());
assertTrue(! itSupport.isLeaf());
assertTrue(itSupport.isExpanded());
itSupport.getChildren().remove(mikeGraham);
assertEquals(itSupport, treeView.getFocusModel().getFocusedItem());
assertEquals(itSupport, treeView.getSelectionModel().getSelectedItem());
}
@Test public void test_rt27820_1() {
TreeItem root = new TreeItem("root");
root.setExpanded(true);
TreeItem child = new TreeItem("child");
root.getChildren().add(child);
treeView.setRoot(root);
treeView.getSelectionModel().select(0);
assertEquals(1, treeView.getSelectionModel().getSelectedItems().size());
assertEquals(root, treeView.getSelectionModel().getSelectedItem());
treeView.setRoot(null);
assertEquals(0, treeView.getSelectionModel().getSelectedItems().size());
assertNull(treeView.getSelectionModel().getSelectedItem());
}
@Test public void test_rt27820_2() {
TreeItem root = new TreeItem("root");
root.setExpanded(true);
TreeItem child = new TreeItem("child");
root.getChildren().add(child);
treeView.setRoot(root);
treeView.getSelectionModel().select(1);
assertEquals(1, treeView.getSelectionModel().getSelectedItems().size());
assertEquals(child, treeView.getSelectionModel().getSelectedItem());
treeView.setRoot(null);
assertEquals(0, treeView.getSelectionModel().getSelectedItems().size());
assertNull(treeView.getSelectionModel().getSelectedItem());
}
@Test public void test_rt28390() {
TreeItem root = new TreeItem("root");
treeView.setRoot(root);
treeView.setCellFactory(new Callback() {
@Override public Object call(Object p) {
TreeCell treeCell = new TreeCellShim() {
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
group.getChildren().setAll(treeView);
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
@Test public void test_rt28534() {
TreeItem root = new TreeItem("root");
root.getChildren().setAll(
new TreeItem(new Person("Jacob", "Smith", "jacob.smith@example.com")),
new TreeItem(new Person("Isabella", "Johnson", "isabella.johnson@example.com")),
new TreeItem(new Person("Ethan", "Williams", "ethan.williams@example.com")),
new TreeItem(new Person("Emma", "Jones", "emma.jones@example.com")),
new TreeItem(new Person("Michael", "Brown", "michael.brown@example.com")));
root.setExpanded(true);
TreeView<Person> tree = new TreeView<Person>(root);
VirtualFlowTestUtils.assertRowsNotEmpty(tree, 0, 6);
VirtualFlowTestUtils.assertRowsEmpty(tree, 6, -1);
root.getChildren().setAll(
new TreeItem(new Person("*_*Emma", "Jones", "emma.jones@example.com")),
new TreeItem(new Person("_Michael", "Brown", "michael.brown@example.com")));
VirtualFlowTestUtils.assertRowsNotEmpty(tree, 0, 3);
VirtualFlowTestUtils.assertRowsEmpty(tree, 3, -1);
}
@Test public void test_rt28556() {
List<Employee> employees = Arrays.<Employee>asList(
new Employee("Ethan Williams", "Sales Department"),
new Employee("Emma Jones", "Sales Department"),
new Employee("Michael Brown", "Sales Department"),
new Employee("Anna Black", "Sales Department"),
new Employee("Rodger York", "Sales Department"),
new Employee("Susan Collins", "Sales Department"),
new Employee("Mike Graham", "IT Support"),
new Employee("Judy Mayer", "IT Support"),
new Employee("Gregory Smith", "IT Support"),
new Employee("Jacob Smith", "Accounts Department"),
new Employee("Isabella Johnson", "Accounts Department"));
TreeItem<String> rootNode = new TreeItem<String>("MyCompany Human Resources");
rootNode.setExpanded(true);
List<TreeItem<String>> nodeList = FXCollections.observableArrayList();
for (Employee employee : employees) {
nodeList.add(new TreeItem<String>(employee.getName()));
}
rootNode.getChildren().setAll(nodeList);
TreeView<String> treeView = new TreeView<String>(rootNode);
final double indent = PlatformImpl.isCaspian() ? 31 :
PlatformImpl.isModena() ? 35 :
0;
VirtualFlowTestUtils.assertLayoutX(treeView, 1, 11, indent);
for (TreeItem<String> children : rootNode.getChildren()) {
assertEquals(rootNode, children.getParent());
}
Collections.sort(rootNode.getChildren(), (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
VirtualFlowTestUtils.assertLayoutX(treeView, 1, 11, indent);
for (TreeItem<String> children : rootNode.getChildren()) {
assertEquals(rootNode, children.getParent());
}
}
@Test public void test_rt22463() {
RT_22463_Person rootPerson = new RT_22463_Person();
rootPerson.setName("Root");
TreeItem<RT_22463_Person> root = new TreeItem<RT_22463_Person>(rootPerson);
root.setExpanded(true);
final TreeView<RT_22463_Person> tree = new TreeView<RT_22463_Person>();
tree.setRoot(root);
RT_22463_Person p1 = new RT_22463_Person();
p1.setId(1l);
p1.setName("name1");
RT_22463_Person p2 = new RT_22463_Person();
p2.setId(2l);
p2.setName("name2");
root.getChildren().addAll(
new TreeItem<RT_22463_Person>(p1),
new TreeItem<RT_22463_Person>(p2));
VirtualFlowTestUtils.assertCellTextEquals(tree, 1, "name1");
VirtualFlowTestUtils.assertCellTextEquals(tree, 2, "name2");
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
VirtualFlowTestUtils.assertCellTextEquals(tree, 1, "updated name1");
VirtualFlowTestUtils.assertCellTextEquals(tree, 2, "updated name2");
}
@Test public void test_rt28637() {
TreeItem<String> s1, s2, s3, s4;
ObservableList<TreeItem<String>> items = FXCollections.observableArrayList(
s1 = new TreeItem<String>("String1"),
s2 = new TreeItem<String>("String2"),
s3 = new TreeItem<String>("String3"),
s4 = new TreeItem<String>("String4"));
final TreeView<String> treeView = new TreeView<String>();
TreeItem<String> root = new TreeItem<String>("Root");
root.setExpanded(true);
treeView.setRoot(root);
treeView.setShowRoot(false);
root.getChildren().addAll(items);
treeView.getSelectionModel().select(0);
assertEquals((Object)s1, treeView.getSelectionModel().getSelectedItem());
assertEquals((Object)s1, treeView.getSelectionModel().getSelectedItems().get(0));
assertEquals(0, treeView.getSelectionModel().getSelectedIndex());
root.getChildren().remove(treeView.getSelectionModel().getSelectedItem());
assertEquals((Object)s2, treeView.getSelectionModel().getSelectedItem());
assertEquals((Object)s2, treeView.getSelectionModel().getSelectedItems().get(0));
assertEquals(0, treeView.getSelectionModel().getSelectedIndex());
}
@Ignore("Test passes from within IDE but not when run from command line. Needs more investigation.")
@Test public void test_rt28678() {
TreeItem<String> s1, s2, s3, s4;
ObservableList<TreeItem<String>> items = FXCollections.observableArrayList(
s1 = new TreeItem<String>("String1"),
s2 = new TreeItem<String>("String2"),
s3 = new TreeItem<String>("String3"),
s4 = new TreeItem<String>("String4"));
final TreeView<String> treeView = new TreeView<String>();
TreeItem<String> root = new TreeItem<String>("Root");
root.setExpanded(true);
treeView.setRoot(root);
treeView.setShowRoot(false);
root.getChildren().addAll(items);
Node graphic = new Circle(6, Color.RED);
assertNull(s2.getGraphic());
TreeCell s2Cell = (TreeCell) VirtualFlowTestUtils.getCell(treeView, 1);
assertNull(s2Cell.getGraphic());
s2.setGraphic(graphic);
Toolkit.getToolkit().firePulse();
assertEquals(graphic, s2.getGraphic());
assertEquals(graphic, s2Cell.getGraphic());
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
TreeView<Person> treeView = new TreeView<>();
treeView.setMaxHeight(50);
treeView.setPrefHeight(50);
TreeItem<Person> root = new TreeItem<Person>(new Person("Root", null, null));
root.setExpanded(true);
treeView.setRoot(root);
treeView.setShowRoot(false);
root.getChildren().setAll(persons);
Toolkit.getToolkit().firePulse();
VirtualScrollBar scrollBar = VirtualFlowTestUtils.getVirtualFlowVerticalScrollbar(treeView);
assertNotNull(scrollBar);
assertTrue(scrollBar.isVisible());
assertTrue(scrollBar.getVisibleAmount() > 0.0);
assertTrue(scrollBar.getVisibleAmount() < 1.0);
assertTrue(scrollBar.getVisibleAmount() > 0.15);
assertTrue(scrollBar.getVisibleAmount() < 0.17);
}
@Test public void test_rt27180_collapseBranch_childSelected_singleSelection() {
sm.setSelectionMode(SelectionMode.SINGLE);
treeView.setRoot(myCompanyRootNode);
myCompanyRootNode.setExpanded(true);
salesDepartment.setExpanded(true);
itSupport.setExpanded(true);
sm.select(2);
assertFalse(sm.isSelected(1));
assertTrue(sm.isSelected(2));
assertTrue(treeView.getFocusModel().isFocused(2));
assertEquals(1, sm.getSelectedIndices().size());
salesDepartment.setExpanded(false);
assertTrue(sm.isSelected(1));
assertTrue(treeView.getFocusModel().isFocused(1));
assertEquals(1, sm.getSelectedIndices().size());
}
@Test public void test_rt27180_collapseBranch_laterSiblingSelected_singleSelection() {
sm.setSelectionMode(SelectionMode.SINGLE);
treeView.setRoot(myCompanyRootNode);
myCompanyRootNode.setExpanded(true);
salesDepartment.setExpanded(true);
itSupport.setExpanded(true);
sm.select(8);
assertFalse(sm.isSelected(1));
assertTrue(sm.isSelected(8));
assertTrue(treeView.getFocusModel().isFocused(8));
assertEquals(1, sm.getSelectedIndices().size());
salesDepartment.setExpanded(false);
assertTrue(sm.isSelected(2));
assertTrue(treeView.getFocusModel().isFocused(2));
assertEquals(1, sm.getSelectedIndices().size());
}
@Test public void test_rt27180_collapseBranch_laterSiblingAndChildrenSelected() {
sm.setSelectionMode(SelectionMode.MULTIPLE);
treeView.setRoot(myCompanyRootNode);
treeView.getSelectionModel().clearSelection();
myCompanyRootNode.setExpanded(true);
salesDepartment.setExpanded(true);
itSupport.setExpanded(true);
sm.selectIndices(8, 9, 10);
assertFalse(sm.isSelected(1));
assertTrue(sm.isSelected(8));
assertTrue(sm.isSelected(9));
assertTrue(sm.isSelected(10));
assertTrue(treeView.getFocusModel().isFocused(10));
assertEquals(3, sm.getSelectedIndices().size());
salesDepartment.setExpanded(false);
assertTrue(sm.isSelected(2));
assertTrue(sm.isSelected(3));
assertTrue(sm.isSelected(4));
assertTrue(treeView.getFocusModel().isFocused(4));
assertEquals(3, sm.getSelectedIndices().size());
}
@Test public void test_rt27180_expandBranch_laterSiblingSelected_singleSelection() {
sm.setSelectionMode(SelectionMode.SINGLE);
treeView.setRoot(myCompanyRootNode);
myCompanyRootNode.setExpanded(true);
salesDepartment.setExpanded(false);
itSupport.setExpanded(true);
sm.select(2);
assertFalse(sm.isSelected(1));
assertTrue(sm.isSelected(2));
assertTrue(treeView.getFocusModel().isFocused(2));
assertEquals(1, sm.getSelectedIndices().size());
salesDepartment.setExpanded(true);
assertTrue(sm.isSelected(8));
assertTrue(treeView.getFocusModel().isFocused(8));
assertEquals(1, sm.getSelectedIndices().size());
}
@Test public void test_rt27180_expandBranch_laterSiblingAndChildrenSelected() {
sm.setSelectionMode(SelectionMode.MULTIPLE);
treeView.setRoot(myCompanyRootNode);
treeView.getSelectionModel().clearSelection();
myCompanyRootNode.setExpanded(true);
salesDepartment.setExpanded(false);
itSupport.setExpanded(true);
sm.selectIndices(2,3,4);
assertFalse(sm.isSelected(1));
assertTrue(sm.isSelected(2));
assertTrue(sm.isSelected(3));
assertTrue(sm.isSelected(4));
assertTrue(treeView.getFocusModel().isFocused(4));
assertEquals(3, sm.getSelectedIndices().size());
salesDepartment.setExpanded(true);
assertTrue(sm.isSelected(8));
assertTrue(sm.isSelected(9));
assertTrue(sm.isSelected(10));
assertTrue(treeView.getFocusModel().isFocused(10));
assertEquals(3, sm.getSelectedIndices().size());
}
@Test public void test_rt30400() {
TreeItem<String> rootItem = new TreeItem<>("root");
treeView.setRoot(rootItem);
treeView.setMinHeight(100);
treeView.setPrefHeight(100);
treeView.setCellFactory(
CheckBoxTreeCell.forTreeView(
param -> new ReadOnlyBooleanWrapper(true)));
VirtualFlowTestUtils.assertRowsNotEmpty(treeView, 0, 1);
VirtualFlowTestUtils.assertCellNotEmpty(VirtualFlowTestUtils.getCell(treeView, 0));
VirtualFlowTestUtils.assertCellEmpty(VirtualFlowTestUtils.getCell(treeView, 1));
VirtualFlowTestUtils.assertCellEmpty(VirtualFlowTestUtils.getCell(treeView, 2));
VirtualFlowTestUtils.assertCellEmpty(VirtualFlowTestUtils.getCell(treeView, 3));
}
@Test public void test_rt31165() {
installChildren();
treeView.setEditable(true);
treeView.setCellFactory(TextFieldTreeCell.forTreeView());
IndexedCell cell = VirtualFlowTestUtils.getCell(treeView, 1);
assertEquals(child1.getValue(), cell.getText());
assertFalse(cell.isEditing());
treeView.edit(child1);
assertEquals(child1, treeView.getEditingItem());
assertTrue(cell.isEditing());
VirtualFlowTestUtils.getVirtualFlow(treeView).requestLayout();
Toolkit.getToolkit().firePulse();
assertEquals(child1, treeView.getEditingItem());
assertTrue(cell.isEditing());
}
@Test public void test_rt31404() {
installChildren();
IndexedCell cell = VirtualFlowTestUtils.getCell(treeView, 0);
assertEquals("Root", cell.getText());
treeView.setShowRoot(false);
cell = VirtualFlowTestUtils.getCell(treeView, 0);
assertEquals("Child 1", cell.getText());
}
@Test public void test_rt31471() {
installChildren();
IndexedCell cell = VirtualFlowTestUtils.getCell(treeView, 0);
assertEquals("Root", cell.getItem());
treeView.setFixedCellSize(50);
VirtualFlowTestUtils.getVirtualFlow(treeView).requestLayout();
Toolkit.getToolkit().firePulse();
assertEquals("Root", cell.getItem());
assertEquals(50, cell.getHeight(), 0.00);
}
private int rt_31200_count = 0;
@Test public void test_rt_31200_tableRow() {
installChildren();
treeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
@Override
public TreeCell<String> call(TreeView<String> param) {
return new TreeCellShim<String>() {
ImageView view = new ImageView();
{ setGraphic(view); };
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
StageLoader sl = new StageLoader(treeView);
assertEquals(24, rt_31200_count);
sl.getStage().setHeight(250);
Toolkit.getToolkit().firePulse();
sl.getStage().setHeight(50);
Toolkit.getToolkit().firePulse();
assertEquals(24, rt_31200_count);
sl.dispose();
}
@Test public void test_rt_30484() {
installChildren();
treeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
@Override public TreeCell<String> call(TreeView<String> param) {
return new TreeCellShim<String>() {
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
VirtualFlowTestUtils.assertGraphicIsVisible(treeView, 0);
VirtualFlowTestUtils.assertGraphicIsVisible(treeView, 1);
VirtualFlowTestUtils.assertGraphicIsVisible(treeView, 2);
VirtualFlowTestUtils.assertGraphicIsVisible(treeView, 3);
VirtualFlowTestUtils.assertGraphicIsNotVisible(treeView, 4);
VirtualFlowTestUtils.assertGraphicIsNotVisible(treeView, 5);
}
private int rt_29650_start_count = 0;
private int rt_29650_commit_count = 0;
private int rt_29650_cancel_count = 0;
@Test public void test_rt_29650() {
installChildren();
treeView.setOnEditStart(t -> {
rt_29650_start_count++;
});
treeView.addEventHandler(TreeView.editCommitEvent(), t -> {
rt_29650_commit_count++;
});
treeView.setOnEditCancel(t -> {
rt_29650_cancel_count++;
});
treeView.setEditable(true);
treeView.setCellFactory(TextFieldTreeCell.forTreeView());
StageLoader sl = new StageLoader(treeView);
treeView.edit(root);
TreeCell rootCell = (TreeCell) VirtualFlowTestUtils.getCell(treeView, 0);
TextField textField = (TextField) rootCell.getGraphic();
textField.setSkin(new TextFieldSkin(textField));
textField.setText("Testing!");
KeyEventFirer keyboard = new KeyEventFirer(textField);
keyboard.doKeyPress(KeyCode.ENTER);
assertEquals("Testing!", root.getValue());
assertEquals(1, rt_29650_start_count);
assertEquals(1, rt_29650_commit_count);
assertEquals(0, rt_29650_cancel_count);
sl.dispose();
}
private int rt_33559_count = 0;
@Test public void test_rt_33559() {
installChildren();
treeView.setShowRoot(true);
final MultipleSelectionModel sm = treeView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
sm.clearAndSelect(0);
treeView.getSelectionModel().getSelectedItems().addListener((ListChangeListener) c -> {
while (c.next()) {
rt_33559_count++;
}
});
assertEquals(0, rt_33559_count);
root.setExpanded(true);
assertEquals(0, rt_33559_count);
}
@Test public void test_rt34103() {
treeView.setRoot(new TreeItem("Root"));
treeView.getRoot().setExpanded(true);
for (int i = 0; i < 4; i++) {
TreeItem parent = new TreeItem("item - " + i);
treeView.getRoot().getChildren().add(parent);
for (int j = 0; j < 4; j++) {
TreeItem child = new TreeItem("item - " + i + " " + j);
parent.getChildren().add(child);
}
}
treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
TreeItem item0 = treeView.getTreeItem(1);
assertEquals("item - 0", item0.getValue());
item0.setExpanded(true);
treeView.getSelectionModel().clearSelection();
treeView.getSelectionModel().selectIndices(1,2,3);
assertEquals(3, treeView.getSelectionModel().getSelectedIndices().size());
item0.setExpanded(false);
Toolkit.getToolkit().firePulse();
assertEquals(1, treeView.getSelectionModel().getSelectedIndices().size());
}
@Test public void test_rt26718() {
treeView.setRoot(new TreeItem("Root"));
treeView.getRoot().setExpanded(true);
for (int i = 0; i < 4; i++) {
TreeItem parent = new TreeItem("item - " + i);
treeView.getRoot().getChildren().add(parent);
for (int j = 0; j < 4; j++) {
TreeItem child = new TreeItem("item - " + i + " " + j);
parent.getChildren().add(child);
}
}
treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
final TreeItem item0 = treeView.getTreeItem(1);
final TreeItem item1 = treeView.getTreeItem(2);
assertEquals("item - 0", item0.getValue());
assertEquals("item - 1", item1.getValue());
item0.setExpanded(true);
item1.setExpanded(true);
Toolkit.getToolkit().firePulse();
treeView.getSelectionModel().selectRange(0, 8);
assertEquals(8, treeView.getSelectionModel().getSelectedIndices().size());
assertEquals(7, treeView.getSelectionModel().getSelectedIndex());
assertEquals(7, treeView.getFocusModel().getFocusedIndex());
item0.setExpanded(false);
Toolkit.getToolkit().firePulse();
assertEquals(3, treeView.getSelectionModel().getSelectedIndex());
assertEquals(3, treeView.getFocusModel().getFocusedIndex());
}
@Test public void test_rt26721_collapseParent_firstRootChild() {
treeView.setRoot(new TreeItem("Root"));
treeView.getRoot().setExpanded(true);
for (int i = 0; i < 4; i++) {
TreeItem parent = new TreeItem("item - " + i);
treeView.getRoot().getChildren().add(parent);
for (int j = 0; j < 4; j++) {
TreeItem child = new TreeItem("item - " + i + " " + j);
parent.getChildren().add(child);
}
}
treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
final TreeItem<String> item0 = treeView.getTreeItem(1);
final TreeItem<String> item0child0 = item0.getChildren().get(0);
final TreeItem<String> item1 = treeView.getTreeItem(2);
assertEquals("item - 0", item0.getValue());
assertEquals("item - 1", item1.getValue());
item0.setExpanded(true);
item1.setExpanded(true);
Toolkit.getToolkit().firePulse();
treeView.getSelectionModel().select(item0child0);
assertEquals(item0child0, treeView.getSelectionModel().getSelectedItem());
assertEquals(item0child0, treeView.getFocusModel().getFocusedItem());
item0.setExpanded(false);
Toolkit.getToolkit().firePulse();
assertEquals(item0, treeView.getSelectionModel().getSelectedItem());
assertEquals(item0, treeView.getFocusModel().getFocusedItem());
}
@Test public void test_rt26721_collapseParent_lastRootChild() {
treeView.setRoot(new TreeItem("Root"));
treeView.getRoot().setExpanded(true);
for (int i = 0; i < 4; i++) {
TreeItem parent = new TreeItem("item - " + i);
treeView.getRoot().getChildren().add(parent);
for (int j = 0; j < 4; j++) {
TreeItem child = new TreeItem("item - " + i + " " + j);
parent.getChildren().add(child);
}
}
treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
final TreeItem<String> item3 = treeView.getTreeItem(4);
final TreeItem<String> item3child0 = item3.getChildren().get(0);
assertEquals("item - 3", item3.getValue());
assertEquals("item - 3 0", item3child0.getValue());
item3.setExpanded(true);
Toolkit.getToolkit().firePulse();
treeView.getSelectionModel().select(item3child0);
assertEquals(item3child0, treeView.getSelectionModel().getSelectedItem());
assertEquals(item3child0, treeView.getFocusModel().getFocusedItem());
item3.setExpanded(false);
Toolkit.getToolkit().firePulse();
assertEquals(item3, treeView.getSelectionModel().getSelectedItem());
assertEquals(item3, treeView.getFocusModel().getFocusedItem());
}
@Test public void test_rt26721_collapseGrandParent() {
treeView.setRoot(new TreeItem("Root"));
treeView.getRoot().setExpanded(true);
for (int i = 0; i < 4; i++) {
TreeItem parent = new TreeItem("item - " + i);
treeView.getRoot().getChildren().add(parent);
for (int j = 0; j < 4; j++) {
TreeItem child = new TreeItem("item - " + i + " " + j);
parent.getChildren().add(child);
}
}
treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
final TreeItem<String> item0 = treeView.getTreeItem(1);
final TreeItem<String> item0child0 = item0.getChildren().get(0);
final TreeItem<String> item1 = treeView.getTreeItem(2);
assertEquals("item - 0", item0.getValue());
assertEquals("item - 1", item1.getValue());
item0.setExpanded(true);
item1.setExpanded(true);
Toolkit.getToolkit().firePulse();
treeView.getSelectionModel().select(item0child0);
assertEquals(item0child0, treeView.getSelectionModel().getSelectedItem());
assertEquals(item0child0, treeView.getFocusModel().getFocusedItem());
treeView.getRoot().setExpanded(false);
Toolkit.getToolkit().firePulse();
assertEquals(treeView.getRoot(), treeView.getSelectionModel().getSelectedItem());
assertEquals(treeView.getRoot(), treeView.getFocusModel().getFocusedItem());
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
final TreeView treeView = new TreeView();
treeView.setRoot(treeNode);
treeNode.setExpanded(true);
treeView.getSelectionModel().select(0);
assertTrue(treeView.getSelectionModel().isSelected(0));
assertTrue(treeView.getFocusModel().isFocused(0));
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
assertTrue(treeView.getSelectionModel().isSelected(0));
assertTrue(treeView.getFocusModel().isFocused(0));
}
private int test_rt_35213_eventCount = 0;
@Test public void test_rt35213() {
final TreeView<String> view = new TreeView<>();
TreeItem<String> root = new TreeItem<>("Boss");
view.setRoot(root);
TreeItem<String> group1 = new TreeItem<>("Group 1");
TreeItem<String> group2 = new TreeItem<>("Group 2");
TreeItem<String> group3 = new TreeItem<>("Group 3");
root.getChildren().addAll(group1, group2, group3);
TreeItem<String> employee1 = new TreeItem<>("Employee 1");
TreeItem<String> employee2 = new TreeItem<>("Employee 2");
group2.getChildren().addAll(employee1, employee2);
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
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(
new TreeItem("aabbaa"),
new TreeItem("bbc"));
final TreeView<String> treeView = new TreeView<>();
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
final TreeView<String> treeView = new TreeView<>();
treeView.setRoot(root);
StageLoader sl = new StageLoader(treeView);
assertNull(treeView.getSelectionModel().getSelectedItem());
treeView.getSelectionModel().select(2);
assertEquals("bbc", treeView.getSelectionModel().getSelectedItem().getValue());
root.getChildren().setAll(aabbaa, bbc);
assertEquals("bbc", treeView.getSelectionModel().getSelectedItem().getValue());
sl.dispose();
}
@Test
public void test_rt35857_selectLast_retainAllSelected() {
TreeView<String> treeView = new TreeView<String>(createTreeItem());
treeView.getSelectionModel().select(treeView.getRoot().getChildren().size());
assert_rt35857(treeView.getRoot().getChildren(), treeView.getSelectionModel(), true);
}
@Test
public void test_rt35857_selectLast_removeAllSelected() {
TreeView<String> treeView = new TreeView<String>(createTreeItem());
treeView.getSelectionModel().select(treeView.getRoot().getChildren().size());
assert_rt35857(treeView.getRoot().getChildren(), treeView.getSelectionModel(), false);
}
@Test
public void test_rt35857_selectFirst_retainAllSelected() {
TreeView<String> treeView = new TreeView<String>(createTreeItem());
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
final TreeView<String> treeTableView = new TreeView<String>(root);
treeTableView.getSelectionModel().select(1);
ObservableList<TreeItem<String>> selectedItems = treeTableView.getSelectionModel().getSelectedItems();
assertEquals(1, selectedItems.size());
assertEquals("A", selectedItems.get(0).getValue());
root.getChildren().removeAll(selectedItems);
assertEquals(2, root.getChildren().size());
assertEquals("B", root.getChildren().get(0).getValue());
assertEquals("C", root.getChildren().get(1).getValue());
}
private int rt_35889_cancel_count = 0;
@Test public void test_rt35889() {
TreeItem a = new TreeItem("a");
TreeItem b = new TreeItem("b");
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().setAll(a, b);
final TreeView<String> textFieldTreeView = new TreeView<String>(root);
textFieldTreeView.setEditable(true);
textFieldTreeView.setCellFactory(TextFieldTreeCell.forTreeView());
textFieldTreeView.setOnEditCancel(t -> {
rt_35889_cancel_count++;
});
TreeCell cell0 = (TreeCell) VirtualFlowTestUtils.getCell(textFieldTreeView, 0);
assertNull(cell0.getGraphic());
assertEquals("Root", cell0.getText());
textFieldTreeView.edit(root);
TextField textField = (TextField) cell0.getGraphic();
assertNotNull(textField);
assertEquals(0, rt_35889_cancel_count);
textField.setText("Z");
textField.getOnAction().handle(new ActionEvent());
assertEquals(0, rt_35889_cancel_count);
}
@Test public void test_rt36255_selection_does_not_expand_item() {
TreeItem a = new TreeItem("a");
TreeItem b = new TreeItem("b");
b.getChildren().add(new TreeItem("bb"));
final TreeItem<String> root = new TreeItem<>();
root.getChildren().addAll(a, b);
root.setExpanded(true);
TreeView<String> view = new TreeView<>(root);
view.setCellFactory(TextFieldTreeCell.forTreeView());
view.getSelectionModel().select(a);
assertEquals(Arrays.asList(a), view.getSelectionModel().getSelectedItems());
assertFalse(b.isExpanded());
view.getSelectionModel().select(b);
assertEquals(Arrays.asList(b), view.getSelectionModel().getSelectedItems());
assertFalse(b.isExpanded());
}
@Test public void test_rt25679() {
Button focusBtn = new Button("Focus here");
TreeItem<String> root = new TreeItem<>("Root");
root.getChildren().setAll(new TreeItem("a"), new TreeItem("b"));
root.setExpanded(true);
final TreeView<String> treeView = new TreeView<>(root);
SelectionModel sm = treeView.getSelectionModel();
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
assertEquals(-1, sm.getSelectedIndex());
assertNull(sm.getSelectedItem());
sl.dispose();
}
@Test public void test_rt36885_addChildBeforeSelection() {
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
final TreeView<String> treeView = new TreeView<String>(root);
MultipleSelectionModel<TreeItem<String>> sm = treeView.getSelectionModel();
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
TreeView<Integer> tv = new TreeView<>();
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
TreeView<String> tree = new TreeView<>();
tree.setShowRoot(false);
tree.setRoot(root);
MultipleSelectionModel sm = tree.getSelectionModel();
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
StageLoader sl = new StageLoader(tree);
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
@Test public void test_rt_37502() {
final TreeView<Long> tree = new TreeView<>(new NumberTreeItem(1));
tree.setCellFactory(new Callback<TreeView<Long>, TreeCell<Long>>() {
@Override
public TreeCell<Long> call(TreeView<Long> param) {
return new TreeCellShim<Long>() {
@Override
public void updateItem(Long item, boolean empty) {
super.updateItem(item, empty);
if (!empty) {
setText(item != null ? String.valueOf(item) : "");
} else{
setText(null);
}
}
};
}
});
StageLoader sl = new StageLoader(tree);
tree.getSelectionModel().select(0);
tree.getRoot().setExpanded(true);
Toolkit.getToolkit().firePulse();
sl.dispose();
}
private static class NumberTreeItem extends TreeItem<Long>{
private boolean loaded = false;
private NumberTreeItem(long value) {
super(value);
}
@Override public boolean isLeaf() {
return false;
}
@Override public ObservableList<TreeItem<Long>> getChildren() {
if(!loaded){
final ObservableList<TreeItem<Long>> children = super.getChildren();
for (int i = 0; i < 10; i++) {
children.add(new NumberTreeItem(10 * getValue() + i));
}
loaded = true;
}
return super.getChildren();
}
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
final TreeView<Integer> tree = new TreeView<>(root);
tree.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends TreeItem<Integer>> c) -> {
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
StageLoader sl = new StageLoader(tree);
assertEquals(0, rt_37538_count);
tree.getSelectionModel().select(0);
assertEquals(1, rt_37538_count);
sl.dispose();
}
@Ignore("Fix not yet developed for TreeView")
@Test public void test_rt_35395_fixedCellSize() {
test_rt_35395(true);
}
@Ignore("Fix not yet developed for TreeView")
@Test public void test_rt_35395_notFixedCellSize() {
test_rt_35395(false);
}
private int rt_35395_counter;
private void test_rt_35395(boolean useFixedCellSize) {
rt_35395_counter = 0;
TreeItem<String> root = new TreeItem<>("green");
root.setExpanded(true);
for (int i = 0; i < 20; i++) {
root.getChildren().addAll(new TreeItem<>("red"), new TreeItem<>("green"), new TreeItem<>("blue"), new TreeItem<>("purple"));
}
TreeView<String> treeView = new TreeView<>(root);
if (useFixedCellSize) {
treeView.setFixedCellSize(24);
}
treeView.setCellFactory(tv -> new TreeCellShim<String>() {
@Override public void updateItem(String color, boolean empty) {
rt_35395_counter += 1;
super.updateItem(color, empty);
setText(null);
if(empty) {
setGraphic(null);
} else {
Rectangle rect = new Rectangle(16, 16);
rect.setStyle("-fx-fill: " + color);
setGraphic(rect);
}
}
});
StageLoader sl = new StageLoader(treeView);
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
assertEquals(0, rt_35395_counter);
rt_35395_counter = 0;
treeView.scrollTo(5);
Platform.runLater(() -> {
Toolkit.getToolkit().firePulse();
assertEquals(5, rt_35395_counter);
rt_35395_counter = 0;
treeView.scrollTo(55);
Platform.runLater(() -> {
Toolkit.getToolkit().firePulse();
int expected = useFixedCellSize ? 17 : 53;
assertEquals(expected, rt_35395_counter);
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
final TreeView<String> treeView = new TreeView<>();
MultipleSelectionModel<TreeItem<String>> sm = treeView.getSelectionModel();
treeView.setRoot(rootOne);
treeView.getSelectionModel().selectFirst();
assertEquals(0, sm.getSelectedIndex());
assertEquals(rootOne, sm.getSelectedItem());
assertEquals(1, sm.getSelectedIndices().size());
assertEquals(0, (int) sm.getSelectedIndices().get(0));
assertEquals(1, sm.getSelectedItems().size());
assertEquals(rootOne, sm.getSelectedItems().get(0));
treeView.setRoot(rootTwo);
assertEquals(-1, sm.getSelectedIndex());
assertNull(sm.getSelectedItem());
assertEquals(0, sm.getSelectedIndices().size());
assertEquals(0, sm.getSelectedItems().size());
}
@Test public void test_rt_37853_replaceRoot() {
test_rt_37853(true);
}
@Test public void test_rt_37853_replaceRootChildren() {
test_rt_37853(false);
}
private int rt_37853_cancelCount;
private int rt_37853_commitCount;
private void test_rt_37853(boolean replaceRoot) {
treeView.setCellFactory(TextFieldTreeCell.forTreeView());
treeView.setEditable(true);
treeView.setRoot(new TreeItem<>("Root"));
treeView.getRoot().setExpanded(true);
for (int i = 0; i < 10; i++) {
treeView.getRoot().getChildren().add(new TreeItem<>("" + i));
}
StageLoader sl = new StageLoader(treeView);
treeView.setOnEditCancel(editEvent -> rt_37853_cancelCount++);
treeView.setOnEditCommit(editEvent -> rt_37853_commitCount++);
assertEquals(0, rt_37853_cancelCount);
assertEquals(0, rt_37853_commitCount);
treeView.edit(treeView.getRoot().getChildren().get(0));
assertNotNull(treeView.getEditingItem());
if (replaceRoot) {
treeView.setRoot(new TreeItem<>("New Root"));
} else {
treeView.getRoot().getChildren().clear();
for (int i = 0; i < 10; i++) {
treeView.getRoot().getChildren().add(new TreeItem<>("new item " + i));
}
}
assertEquals(1, rt_37853_cancelCount);
assertEquals(0, rt_37853_commitCount);
sl.dispose();
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
TreeView<String> stringTreeView = new TreeView<>(root);
stringTreeView.setShowRoot(false);
MultipleSelectionModel<TreeItem<String>> sm = stringTreeView.getSelectionModel();
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
final TreeView<String> treeView = new TreeView<>(root);
treeView.setShowRoot(false);
MultipleSelectionModel<TreeItem<String>> sm = treeView.getSelectionModel();
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
final TreeView<String> treeView = new TreeView<>(root);
treeView.setShowRoot(false);
MultipleSelectionModel<TreeItem<String>> sm = treeView.getSelectionModel();
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
TreeView<String> treeView = new TreeView<>(root);
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
TreeView<String> stringTreeView = new TreeView<>(root);
stringTreeView.setShowRoot(false);
MultipleSelectionModel<TreeItem<String>> sm = stringTreeView.getSelectionModel();
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
final TreeView<String> treeView = new TreeView<>(hiddenRoot);
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
List<TreeItem<String>> removed = new ArrayList<>();
while (c.next()) {
if (c.wasAdded()) {
assertEquals(1, c.getAddedSize());
assertTrue(c.getAddedSubList().contains(root1));
removed.clear();
}
if (c.wasRemoved()) {
removed.addAll(c.getRemoved());
}
}
if (!removed.isEmpty()) {
assertTrue(removed.containsAll(FXCollections.observableArrayList(treeItem1, treeItem2)));
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
TreeView<String> stringTreeView = new TreeView<>(root);
stringTreeView.setShowRoot(false);
MultipleSelectionModel<TreeItem<String>> sm = stringTreeView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
FocusModel<TreeItem<String>> fm = stringTreeView.getFocusModel();
StageLoader sl = new StageLoader(stringTreeView);
assertTrue(sm.isEmpty());
assertEquals(a, fm.getFocusedItem());
assertEquals(0, fm.getFocusedIndex());
VirtualFlowTestUtils.clickOnRow(stringTreeView, 0);
assertTrue(sm.isSelected(0));
assertEquals(a, sm.getSelectedItem());
assertTrue(fm.isFocused(0));
assertEquals(a, fm.getFocusedItem());
assertEquals(0, fm.getFocusedIndex());
Integer anchor = TreeCellBehavior.getAnchor(stringTreeView, null);
assertNotNull(anchor);
assertTrue(TreeCellBehavior.hasNonDefaultAnchor(stringTreeView));
assertEquals(0, (int)anchor);
root.getChildren().add(0, new TreeItem("z"));
assertFalse(sm.isSelected(0));
assertFalse(fm.isFocused(0));
assertTrue(sm.isSelected(1));
assertEquals(a, sm.getSelectedItem());
assertTrue(fm.isFocused(1));
assertEquals(a, fm.getFocusedItem());
assertEquals(1, fm.getFocusedIndex());
anchor = TreeCellBehavior.getAnchor(stringTreeView, null);
assertNotNull(anchor);
assertTrue(TreeCellBehavior.hasNonDefaultAnchor(stringTreeView));
assertEquals(1, (int)anchor);
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
TreeView<String> stringTreeView = new TreeView<>(root);
stringTreeView.setShowRoot(false);
MultipleSelectionModel<TreeItem<String>> sm = stringTreeView.getSelectionModel();
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
TreeView<String> stringTreeView = new TreeView<>(root);
stringTreeView.setShowRoot(false);
MultipleSelectionModel<TreeItem<String>> sm = stringTreeView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
Bindings.bindContent(rt_39482_list, sm.getSelectedItems());
assertEquals(0, sm.getSelectedItems().size());
assertEquals(0, rt_39482_list.size());
test_rt_39482_selectRow("a", sm, 0);
test_rt_39482_selectRow("b", sm, 1);
test_rt_39482_selectRow("c", sm, 2);
test_rt_39482_selectRow("d", sm, 3);
}
private void test_rt_39482_selectRow(String expectedString,
MultipleSelectionModel<TreeItem<String>> sm,
int rowToSelect) {
sm.selectAll();
assertEquals(4, sm.getSelectedIndices().size());
assertEquals(4, sm.getSelectedItems().size());
assertEquals(4, rt_39482_list.size());
sm.clearAndSelect(rowToSelect);
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
TreeView<String> stringTreeView = new TreeView<>(root);
stringTreeView.setShowRoot(false);
MultipleSelectionModel<TreeItem<String>> sm = stringTreeView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
StageLoader sl = new StageLoader(stringTreeView);
KeyEventFirer keyboard = new KeyEventFirer(stringTreeView);
assertEquals(0, sm.getSelectedItems().size());
sm.clearAndSelect(0);
if (useSMSelectAll) {
sm.selectAll();
} else {
keyboard.doKeyPress(KeyCode.A, KeyModifier.getShortcutKey());
}
assertEquals(4, sm.getSelectedItems().size());
assertEquals(0, (int) TreeCellBehavior.getAnchor(stringTreeView, -1));
keyboard.doKeyPress(KeyCode.DOWN, KeyModifier.SHIFT);
assertEquals(0, (int) TreeCellBehavior.getAnchor(stringTreeView, -1));
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
TreeView<String> stringTreeView = new TreeView<>(root);
stringTreeView.setShowRoot(false);
MultipleSelectionModel<TreeItem<String>> sm = stringTreeView.getSelectionModel();
FocusModel<TreeItem<String>> fm = stringTreeView.getFocusModel();
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
private ObservableList<String> test_rt_39661_setup() {
ObservableList<String> rawItems = FXCollections.observableArrayList(
"9-item", "8-item", "7-item", "6-item",
"5-item", "4-item", "3-item", "2-item", "1-item");
root = createSubTree("root", rawItems);
root.setExpanded(true);
treeView = new TreeView(root);
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
assertTrue("row of item must be less than expandedItemCount, but was: " + treeView.getRow(grandChild),
treeView.getRow(grandChild) < treeView.getExpandedItemCount());
}
@Test public void test_rt_39661_rowOfGrandChildParentCollapsedUpdatedOnInsertAbove() {
ObservableList<String> rawItems = test_rt_39661_setup();
int grandIndex = 2;
int childIndex = 3;
TreeItem child = createSubTree("addedChild2", rawItems);
TreeItem grandChild = (TreeItem) child.getChildren().get(grandIndex);
root.getChildren().add(childIndex, child);
int rowOfGrand = treeView.getRow(grandChild);
root.getChildren().add(childIndex - 1, createSubTree("other", rawItems));
assertEquals(-1, treeView.getRow(grandChild));
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
assertEquals(-1, treeView.getRow(grandChild));
}
@Test public void test_rt_39661_rowOfGrandChildParentExpandedUpdatedOnInsertAbove() {
ObservableList<String> rawItems = test_rt_39661_setup();
int grandIndex = 2;
int childIndex = 3;
TreeItem child = createSubTree("addedChild2", rawItems);
TreeItem grandChild = (TreeItem) child.getChildren().get(grandIndex);
child.setExpanded(true);
root.getChildren().add(childIndex, child);
int rowOfGrand = treeView.getRow(grandChild);
root.getChildren().add(childIndex -1, createSubTree("other", rawItems));
assertEquals(rowOfGrand + 1, treeView.getRow(grandChild));
}
@Test public void test_rt_39661_rowOfGrandChildDependsOnParentExpansion() {
ObservableList<String> rawItems = test_rt_39661_setup();
int grandIndex = 2;
int childIndex = 3;
TreeItem collapsedChild = createSubTree("addedChild", rawItems);
TreeItem collapsedGrandChild = (TreeItem) collapsedChild.getChildren().get(grandIndex);
root.getChildren().add(childIndex, collapsedChild);
int collapedGrandIndex = treeView.getRow(collapsedGrandChild);
int collapsedRowCount = treeView.getExpandedItemCount();
test_rt_39661_setup();
assertEquals(collapsedRowCount - 1, treeView.getExpandedItemCount());
TreeItem expandedChild = createSubTree("addedChild2", rawItems);
TreeItem expandedGrandChild = (TreeItem) expandedChild.getChildren().get(grandIndex);
expandedChild.setExpanded(true);
root.getChildren().add(childIndex, expandedChild);
assertNotSame("getRow must depend on expansionState " + collapedGrandIndex,
collapedGrandIndex, treeView.getRow(expandedGrandChild));
}
@Test public void test_rt_39661_rowOfGrandChildInCollapsedChild() {
ObservableList<String> rawItems = test_rt_39661_setup();
TreeItem newChild = createSubTree("added-child", rawItems);
TreeItem grandChild = (TreeItem) newChild.getChildren().get(2);
root.getChildren().add(6, newChild);
int row = treeView.getRow(grandChild);
assertEquals("grandChild not visible", -1, row);
if (row > -1) {
assertEquals(grandChild, treeView.getTreeItem(row));
}
}
@Test public void test_rt_39661_rowOfRootChild() {
ObservableList<String> rawItems = test_rt_39661_setup();
int index = 2;
TreeItem child = (TreeItem) root.getChildren().get(index);
assertEquals(index + 1, treeView.getRow(child));
}
@Test public void test_rt_39661_expandedItemCount() {
ObservableList<String> rawItems = test_rt_39661_setup();
int initialRowCount = treeView.getExpandedItemCount();
assertEquals(root.getChildren().size() + 1, initialRowCount);
TreeItem collapsedChild = createSubTree("collapsed-child", rawItems);
root.getChildren().add(collapsedChild);
assertEquals(initialRowCount + 1, treeView.getExpandedItemCount());
TreeItem expandedChild = createSubTree("expanded-child", rawItems);
expandedChild.setExpanded(true);
root.getChildren().add(0, expandedChild);
assertEquals(2 * initialRowCount + 1, treeView.getExpandedItemCount());
}
@Test public void test_rt_22599() {
TreeItem<RT22599_DataType> root = new TreeItem<>();
root.getChildren().setAll(
new TreeItem<>(new RT22599_DataType(1, "row1")),
new TreeItem<>(new RT22599_DataType(2, "row2")),
new TreeItem<>(new RT22599_DataType(3, "row3")));
root.setExpanded(true);
TreeView<RT22599_DataType> tree = new TreeView<>(root);
tree.setShowRoot(false);
StageLoader sl = new StageLoader(tree);
assertNotNull(tree.getSkin());
assertEquals("row1", VirtualFlowTestUtils.getCell(tree, 0).getText());
assertEquals("row2", VirtualFlowTestUtils.getCell(tree, 1).getText());
assertEquals("row3", VirtualFlowTestUtils.getCell(tree, 2).getText());
TreeItem<RT22599_DataType> data;
root.getChildren().set(0, data = new TreeItem<>(new RT22599_DataType(0, "row1a")));
Toolkit.getToolkit().firePulse();
assertEquals("row1a", VirtualFlowTestUtils.getCell(tree, 0).getText());
data.getValue().text = "row1b";
Toolkit.getToolkit().firePulse();
assertEquals("row1a", VirtualFlowTestUtils.getCell(tree, 0).getText());
tree.refresh();
Toolkit.getToolkit().firePulse();
assertEquals("row1b", VirtualFlowTestUtils.getCell(tree, 0).getText());
sl.dispose();
}
private static class RT22599_DataType {
public int id = 0;
public String text = "";
public RT22599_DataType(int id, String text) {
this.id = id;
this.text = text;
}
@Override public String toString() {
return text;
}
@Override public boolean equals(Object obj) {
if (obj == null) return false;
return id == ((RT22599_DataType)obj).id;
}
}
private int rt_39966_count = 0;
@Test public void test_rt_39966() {
TreeItem<String> root = new TreeItem<>("Root");
TreeView<String> table = new TreeView<>(root);
table.setShowRoot(true);
StageLoader sl = new StageLoader(table);
assertTrue(table.getSelectionModel().isEmpty());
table.getSelectionModel().selectedItemProperty().addListener((value, s1, s2) -> {
if (rt_39966_count == 0) {
rt_39966_count++;
assertFalse(table.getSelectionModel().isEmpty());
} else {
assertTrue(debug(), table.getSelectionModel().isEmpty());
}
});
table.getSelectionModel().select(0);
assertFalse(table.getSelectionModel().isEmpty());
table.setRoot(null);
assertTrue(debug(),table.getSelectionModel().isEmpty());
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
TreeView<String> treeView = new TreeView<>(root);
treeView.setShowRoot(false);
sm = treeView.getSelectionModel();
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
TreeView<String> treeView = new TreeView<>(root);
treeView.setShowRoot(false);
sm = treeView.getSelectionModel();
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
TreeView<String> treeView = new TreeView<>(root);
treeView.setShowRoot(false);
sm = treeView.getSelectionModel();
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
TreeView<String> treeView = new TreeView<>(root);
treeView.setShowRoot(false);
sm = treeView.getSelectionModel();
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
TreeView<String> treeView = new TreeView<>(root);
sm = treeView.getSelectionModel();
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
@Test public void test_rt_39674_staticChildren() {
TreeItem<String> item2;
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(
new TreeItem<>("0"),
new TreeItem<>("1"),
item2 = new TreeItem<>("2"),
new TreeItem<>("3"),
new TreeItem<>("4"),
new TreeItem<>("5")
);
item2.getChildren().addAll(
new TreeItem<>("0"),
new TreeItem<>("1"),
new TreeItem<>("2"),
new TreeItem<>("3"),
new TreeItem<>("4"),
new TreeItem<>("5")
);
TreeView<String> treeView = new TreeView<>(root);
sm = treeView.getSelectionModel();
StageLoader sl = new StageLoader(treeView);
sm.select(4);
assertEquals(4, sm.getSelectedIndex());
assertEquals("3", sm.getSelectedItem().getValue());
item2.setExpanded(true);
assertEquals(10, sm.getSelectedIndex());
assertEquals("3", sm.getSelectedItem().getValue());
sl.dispose();
}
@Ignore("RT-39674 not yet fixed")
@Test public void test_rt_39674_dynamicChildren() {
TreeItem<Integer> root = createTreeItem(0);
root.setExpanded(true);
TreeView<Integer> treeView = new TreeView<>(root);
SelectionModel<TreeItem<Integer>> sm = treeView.getSelectionModel();
StageLoader sl = new StageLoader(treeView);
sm.select(5);
assertEquals(5, sm.getSelectedIndex());
assertEquals(4, (int)sm.getSelectedItem().getValue());
root.getChildren().get(2).setExpanded(true);
assertEquals(12, sm.getSelectedIndex());
assertEquals(4, (int)sm.getSelectedItem().getValue());
sl.dispose();
}
private TreeItem<Integer> createTreeItem(final int index) {
final TreeItem<Integer> node = new TreeItem<Integer>(index) {
private boolean isLeaf;
private boolean isFirstTimeChildren = true;
private boolean isFirstTimeLeaf = true;
@Override
public ObservableList<TreeItem<Integer>> getChildren() {
if (isFirstTimeChildren) {
isFirstTimeChildren = false;
super.getChildren().setAll(buildChildren(this));
}
return super.getChildren();
}
@Override
public boolean isLeaf() {
if (isFirstTimeLeaf) {
isFirstTimeLeaf = false;
int index = getValue();
isLeaf = index % 2 != 0;
}
return isLeaf;
}
};
return node;
}
private ObservableList<TreeItem<Integer>> buildChildren(TreeItem<Integer> TreeItem) {
Integer index = TreeItem.getValue();
if (index % 2 == 0) {
ObservableList<TreeItem<Integer>> children = FXCollections.observableArrayList();
for (int i = 0; i < 5; i++) {
children.add(createTreeItem(i));
}
return children;
}
return FXCollections.emptyObservableList();
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
TreeView<String> stringTreeView = new TreeView<>(root);
stringTreeView.setShowRoot(false);
MultipleSelectionModel<TreeItem<String>> sm = stringTreeView.getSelectionModel();
sm.setSelectionMode(SelectionMode.MULTIPLE);
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
final TreeView<String> view = new TreeView<>();
StageLoader sl = new StageLoader(view);
view.getFocusModel().getFocusedIndex();
sl.dispose();
}
@Test public void test_rt_40278_showRoot() {
TreeItem<String> root = new TreeItem<>("Root");
root.setExpanded(true);
root.getChildren().addAll(new TreeItem<>("0"),new TreeItem<>("1"));
TreeView<String> view = new TreeView<>(root);
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
TreeView<String> view = new TreeView<>(root);
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
TreeView<String> view = new TreeView<>(root);
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
final TreeView<Integer> view = new TreeView<>(root);
MultipleSelectionModel<TreeItem<Integer>> sm = view.getSelectionModel();
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
@Test public void test_jdk8131924_showRoot() {
test_jdk8131924(true);
}
@Test public void test_jdk8131924_hideRoot() {
test_jdk8131924(false);
}
private void test_jdk8131924(boolean showRoot) {
final TreeView<String> treeView = new TreeView<>(new TreeItem("Root"));
MultipleSelectionModel<TreeItem<String>> model = treeView.getSelectionModel();
model.setSelectionMode(SelectionMode.MULTIPLE);
treeView.getRoot().setExpanded(true);
treeView.setShowRoot(showRoot);
for (int i = 0; i < 4; i++) {
treeView.getRoot().getChildren().add(new TreeItem("" + i));
}
int startIndex = showRoot ? 2 : 1;
model.select(startIndex);
assertEquals(startIndex, model.getSelectedIndex());
assertEquals(1, model.getSelectedIndices().size());
assertEquals("1", model.getSelectedItem().getValue());
treeView.getRoot().getChildren().add(startIndex + (showRoot ? -1 : 0), new TreeItem<>("NEW"));
assertEquals("1", model.getSelectedItem().getValue());
assertEquals(startIndex + 1, model.getSelectedIndex());
assertEquals(1, model.getSelectedIndices().size());
assertEquals(1, model.getSelectedItems().size());
treeView.getRoot().getChildren().remove(startIndex + (showRoot ? 0 : 1));
assertEquals(1, model.getSelectedIndices().size());
assertEquals(startIndex, model.getSelectedIndex());
assertEquals("NEW", model.getSelectedItem().getValue());
assertEquals(1, model.getSelectedItems().size());
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
final TreeView<String> view = new TreeView<>(root);
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
final TreeView<String> view = new TreeView<>(root);
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
@Test public void testRemovedSelectedItemsWhenBranchIsCollapsed() {
TreeItem<String> c1, c2, c3;
TreeItem<String> root = new TreeItem<>("foo");
root.getChildren().add(c1 = new TreeItem<>("bar"));
root.getChildren().add(c2 = new TreeItem<>("baz"));
root.getChildren().add(c3 = new TreeItem<>("qux"));
root.setExpanded(true);
TreeView<String> treeView = new TreeView<>(root);
treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
treeView.getSelectionModel().selectAll();
MockListObserver<TreeItem<String>> observer = new MockListObserver<>();
treeView.getSelectionModel().getSelectedItems().addListener(observer);
root.setExpanded(false);
observer.check1();
observer.checkAddRemove(0, treeView.getSelectionModel().getSelectedItems(), List.of(c1, c2, c3), 1, 1);
}
@Test
public void testMisbehavingCancelEditTerminatesEdit() {
TreeCell<String> cell = new MisbehavingOnCancelTreeCell<>();
treeView.setEditable(true);
installChildren();
cell.updateTreeView(treeView);
int editingIndex = 1;
int intermediate = 0;
cell.updateIndex(editingIndex);
TreeItem editingItem = treeView.getTreeItem(editingIndex);
TreeItem intermediateTreeItem = treeView.getTreeItem(intermediate);
treeView.edit(editingItem);
assertTrue("sanity: ", cell.isEditing());
try {
treeView.edit(intermediateTreeItem);
} catch (Exception ex) {
} finally {
assertFalse("cell must not be editing", cell.isEditing());
assertEquals("table must be editing at intermediate index",
intermediateTreeItem, treeView.getEditingItem());
}
treeView.edit(editingItem);
assertTrue("sanity: ", cell.isEditing());
try {
cell.cancelEdit();
} catch (Exception ex) {
} finally {
assertFalse("cell must not be editing", cell.isEditing());
assertNull("table editing must be cancelled by cell", treeView.getEditingItem());
}
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
TreeView<String> stringTreeView = new TreeView<>(root);
stringTreeView.setShowRoot(false);
SelectionModel sm = stringTreeView.getSelectionModel();
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
TreeView<String> table = new TreeView<>(rootNode);
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
TreeView<String> table = new TreeView<>(rootNode);
table.setShowRoot(false);
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
TreeView<String> table = new TreeView<>(rootNode);
table.setShowRoot(false);
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
public static class MisbehavingOnCancelTreeCell<S> extends TreeCell<S> {
@Override
public void cancelEdit() {
super.cancelEdit();
throw new RuntimeException("violating contract");
}
}
}
