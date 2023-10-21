package test.javafx.scene.control;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceBoxShim;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxShim;
import javafx.scene.control.Control;
import javafx.scene.control.FocusModel;
import javafx.scene.control.ListView;
import javafx.scene.control.ListViewShim;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPaneShim;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewFocusModel;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TableViewShim;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableView.TreeTableViewFocusModel;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.control.TreeTableViewShim;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeViewShim;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
@RunWith(Parameterized.class)
public class SelectionFocusModelMemoryTest {
private Scene scene;
private Stage stage;
private Pane root;
private boolean showBeforeReplaceSM;
@Test
public void testTreeViewFocusModel() {
TreeItem<String> root = new TreeItem<>("root");
ObservableList<String> data = FXCollections.observableArrayList("Apple", "Orange", "Banana");
data.forEach(text -> root.getChildren().add(new TreeItem<>(text)));
TreeView<String> control = new TreeView<>(root);
WeakReference<FocusModel<?>> weakRef = new WeakReference<>(control.getFocusModel());
FocusModel<TreeItem<String>> replacingSm = TreeViewShim.get_TreeViewFocusModel(control);
maybeShowControl(control);
control.setFocusModel(replacingSm);
attemptGC(weakRef, 10);
assertNull("focusModel must be gc'ed", weakRef.get());
}
@Test
public void testTreeTableViewFocusModel() {
TreeItem<String> root = new TreeItem<>("root");
ObservableList<String> data = FXCollections.observableArrayList("Apple", "Orange", "Banana");
data.forEach(text -> root.getChildren().add(new TreeItem<>(text)));
TreeTableView<String> control = new TreeTableView<>(root);
WeakReference<FocusModel<?>> weakRef = new WeakReference<>(control.getFocusModel());
TreeTableViewFocusModel<String> replacingSm = new TreeTableViewFocusModel<>(control);
maybeShowControl(control);
control.setFocusModel(replacingSm);
attemptGC(weakRef, 10);
assertNull("focusModel must be gc'ed", weakRef.get());
}
@Test
public void testTableViewFocusModel() {
TableView<String> control = new TableView<>(FXCollections.observableArrayList("Apple", "Orange", "Banana"));
WeakReference<FocusModel<?>> weakRef = new WeakReference<>(control.getFocusModel());
TableViewFocusModel<String> replacingSm = new TableViewFocusModel<>(control);
maybeShowControl(control);
control.setFocusModel(replacingSm);
attemptGC(weakRef, 10);
assertNull("focusModel must be gc'ed", weakRef.get());
}
@Test
public void testListViewFocusModel() {
ListView<String> control = new ListView<>(FXCollections.observableArrayList("Apple", "Orange", "Banana"));
WeakReference<FocusModel<?>> weakRef = new WeakReference<>(control.getFocusModel());
FocusModel<String> replacingSm = ListViewShim.getListViewFocusModel(control);
maybeShowControl(control);
control.setFocusModel(replacingSm);
attemptGC(weakRef, 10);
assertNull("focusModel must be gc'ed", weakRef.get());
}
@Test
public void testTreeViewSelectionModel() {
TreeItem<String> root = new TreeItem<>("root");
ObservableList<String> data = FXCollections.observableArrayList("Apple", "Orange", "Banana");
data.forEach(text -> root.getChildren().add(new TreeItem<>(text)));
TreeView<String> control = new TreeView<>(root);
WeakReference<SelectionModel<?>> weakRef = new WeakReference<>(control.getSelectionModel());
MultipleSelectionModel<TreeItem<String>> replacingSm = TreeViewShim.get_TreeViewBitSetSelectionModel(control);
maybeShowControl(control);
control.setSelectionModel(replacingSm);
attemptGC(weakRef, 10);
assertNull("selectionModel must be gc'ed", weakRef.get());
}
@Test
public void testTreeTableViewSelectionModel() {
TreeItem<String> root = new TreeItem<>("root");
ObservableList<String> data = FXCollections.observableArrayList("Apple", "Orange", "Banana");
data.forEach(text -> root.getChildren().add(new TreeItem<>(text)));
TreeTableView<String> control = new TreeTableView<>(root);
WeakReference<SelectionModel<?>> weakRef = new WeakReference<>(control.getSelectionModel());
TreeTableViewSelectionModel<String> replacingSm = (TreeTableViewSelectionModel<String>) TreeTableViewShim.get_TreeTableViewArrayListSelectionModel(control);
maybeShowControl(control);
control.setSelectionModel(replacingSm);
attemptGC(weakRef, 10);
assertNull("selectionModel must be gc'ed", weakRef.get());
}
@Test
public void testTableViewSelectionModel() {
TableView<String> control = new TableView<>(FXCollections.observableArrayList("Apple", "Orange", "Banana"));
WeakReference<SelectionModel<?>> weakRef = new WeakReference<>(control.getSelectionModel());
TableViewSelectionModel<String> replacingSm = TableViewShim.get_TableViewArrayListSelectionModel(control);
maybeShowControl(control);
control.setSelectionModel(replacingSm);
attemptGC(weakRef, 10);
assertNull("selectionModel must be gc'ed", weakRef.get());
}
@Test
public void testListViewSelectionModel() {
ListView<String> control = new ListView<>(FXCollections.observableArrayList("Apple", "Orange", "Banana"));
WeakReference<SelectionModel<?>> weakRef = new WeakReference<>(control.getSelectionModel());
MultipleSelectionModel<String> replacingSm = ListViewShim.getListViewBitSetSelectionModel(control);
maybeShowControl(control);
control.setSelectionModel(replacingSm);
attemptGC(weakRef, 10);
assertNull("selectionModel must be gc'ed", weakRef.get());
}
@Test
public void testTabPaneSelectionModel() {
TabPane control = new TabPane();
ObservableList<String> data = FXCollections.observableArrayList("Apple", "Orange", "Banana");
data.forEach(text -> control.getTabs().add(new Tab(text)));
WeakReference<SelectionModel<?>> weakRef = new WeakReference<>(control.getSelectionModel());
SingleSelectionModel<Tab> replacingSm = TabPaneShim.getTabPaneSelectionModel(control);
maybeShowControl(control);
control.setSelectionModel(replacingSm);
attemptGC(weakRef, 10);
assertNull("selectionModel must be gc'ed", weakRef.get());
}
@Test
public void testComboBoxSelectionModel() {
ComboBox<String> control = new ComboBox<>(FXCollections.observableArrayList("Apple", "Orange", "Banana"));
WeakReference<SelectionModel<?>> weakRef = new WeakReference<>(control.getSelectionModel());
SingleSelectionModel<String> replacingSm = ComboBoxShim.get_ComboBoxSelectionModel(control);
maybeShowControl(control);
control.setSelectionModel(replacingSm);
attemptGC(weakRef, 10);
assertNull("selectionModel must be gc'ed", weakRef.get());
}
@Test
public void testChoiceBoxSelectionModel() {
ChoiceBox<String> control = new ChoiceBox<>(FXCollections.observableArrayList("Apple", "Orange", "Banana"));
WeakReference<SelectionModel<?>> weakRef = new WeakReference<>(control.getSelectionModel());
SingleSelectionModel<String> replacingSm = ChoiceBoxShim.get_ChoiceBoxSelectionModel(control);
maybeShowControl(control);
control.setSelectionModel(replacingSm);
attemptGC(weakRef, 10);
assertNull("selectionModel must be gc'ed", weakRef.get());
}
private void attemptGC(WeakReference<?> weakRef, int n) {
for (int i = 0; i < n; i++) {
System.gc();
if (weakRef.get() == null) {
break;
}
try {
Thread.sleep(500);
} catch (InterruptedException e) {
System.err.println("InterruptedException occurred during Thread.sleep()");
}
}
}
protected void maybeShowControl(Control control) {
if (!showBeforeReplaceSM) return;
show(control);
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
Object[][] data = new Object[][] {
{false},
{true },
};
return Arrays.asList(data);
}
public SelectionFocusModelMemoryTest(boolean showBeforeReplaceSM) {
this.showBeforeReplaceSM = showBeforeReplaceSM;
}
private void show(Control node) {
if (root == null) {
root = new VBox();
scene = new Scene(root);
stage = new Stage();
stage.setScene(scene);
}
root.getChildren().add(node);
if (!stage.isShowing()) {
stage.show();
}
}
@After
public void cleanup() {
if (stage != null) {
stage.hide();
}
}
}
