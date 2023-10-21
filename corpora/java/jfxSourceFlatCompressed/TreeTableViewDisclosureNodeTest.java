package test.javafx.scene.control.skin;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.sun.javafx.tk.Toolkit;
import static org.junit.Assert.*;
import static test.com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import test.com.sun.javafx.scene.control.infrastructure.MouseEventFirer;
public class TreeTableViewDisclosureNodeTest {
private Scene scene;
private Stage stage;
private Pane content;
private TreeTableView<String> treeTable;
private TreeItem<String> root;
private TreeTableView.TreeTableViewSelectionModel<?> sm;
private int rootLeafChildIndex;
private int rootExpandedChildIndex;
private int rootCollapsedChildIndex;
@Test
public void testSelectChildLeafAfterExpand() {
showTreeTable();
TreeItem<String> child = root.getChildren().get(rootCollapsedChildIndex);
child.setExpanded(true);
Toolkit.getToolkit().firePulse();
TreeItem<String> grandChild = child.getChildren().get(0);
int grandChildRowIndex = treeTable.getRow(grandChild);
assertTrue("sanity: grandChild is leaf", grandChild.isLeaf());
assertFalse("sanity: grandChild not selected", sm.isSelected(grandChildRowIndex));
fireMouseIntoIndentationRegion(grandChildRowIndex);
assertTrue("grandChild must be selected " + grandChildRowIndex, sm.isSelected(grandChildRowIndex));
}
@Test @Ignore("real-cleanup")
public void testRowReuse() {
showTreeTable();
TreeItem<String> expandedChild = root.getChildren().get(rootExpandedChildIndex);
TreeItem<String> grandChild = expandedChild.getChildren().get(0);
int grandChildRowIndex = treeTable.getRow(grandChild);
assertNull("leaf must not have disclosureNode", getDisclosureNode(grandChildRowIndex));
expandedChild.setExpanded(false);
Toolkit.getToolkit().firePulse();
expandedChild.setExpanded(true);
Toolkit.getToolkit().firePulse();
assertNull("leaf must not have disclosureNode", getDisclosureNode(grandChildRowIndex));
}
@Test
public void testSelectChildLeaf() {
showTreeTable();
TreeItem<String> expandedChild = root.getChildren().get(rootExpandedChildIndex);
TreeItem<String> grandChild = expandedChild.getChildren().get(0);
int grandChildRowIndex = treeTable.getRow(grandChild);
fireMouseIntoIndentationRegion(grandChildRowIndex);
assertTrue("row must be selected" + grandChildRowIndex, sm.isSelected(grandChildRowIndex));
}
@Test
public void testSelectRootLeaf() {
showTreeTable();
TreeItem<String> leafChild = root.getChildren().get(rootLeafChildIndex);
int leafChildRowIndex = treeTable.getRow(leafChild);
fireMouseIntoIndentationRegion(leafChildRowIndex);
assertTrue("row must be selected" + leafChildRowIndex, sm.isSelected(leafChildRowIndex));
}
@Test
public void testExpandCollapsedChild() {
showTreeTable();
TreeItem<String> child = root.getChildren().get(rootCollapsedChildIndex);
boolean expanded = child.isExpanded();
fireMouseIntoIndentationRegion(treeTable.getRow(child));
assertEquals("expansion state changed" + child.getValue(), !expanded, child.isExpanded());
}
@Test
public void testInitialRowState() {
showTreeTable();
assertHasVisibleDisclosureNode(0);
assertNull(getDisclosureNode(rootLeafChildIndex + 1));
assertHasVisibleDisclosureNode(rootExpandedChildIndex + 1);
assertNull(getDisclosureNode(rootExpandedChildIndex + 2));
}
protected void fireMouseIntoIndentationRegion(int rowIndex) {
TreeTableRow<?> grandChildTableRow = getTableRow(rowIndex);
TreeTableCell<?, ?> cell = (TreeTableCell<?, ?>) grandChildTableRow.lookup(".tree-table-cell");
MouseEventFirer mouse = new MouseEventFirer(cell, true);
double targetX = - cell.getWidth() / 2;
mouse.fireMousePressAndRelease(1, targetX, 0);
Toolkit.getToolkit().firePulse();
}
protected void assertHasVisibleDisclosureNode(int rowIndex) {
Node disclosure = getDisclosureNode(rowIndex);
assertNotNull("disclosureNode must added", disclosure);
assertTrue("disclosureNode must be visible", disclosure.isVisible());
}
protected Node getDisclosureNode(int rowIndex) {
TreeTableRow<?> tableRow = getTableRow(rowIndex);
Node disclosure = tableRow.lookup(".tree-disclosure-node");
return disclosure;
}
protected TreeTableRow<?> getTableRow(TreeItem<String> treeItem) {
return getTableRow(treeTable.getRow(treeItem));
}
protected TreeTableRow<?> getTableRow(int rowIndex) {
IndexedCell<?> tableRow = getCell(treeTable, rowIndex);
assertTrue("sanity: expect TreeTableRow but was: " + tableRow, tableRow instanceof TreeTableRow);
assertEquals("sanity: row index", rowIndex, tableRow.getIndex());
return (TreeTableRow<?>) tableRow;
}
@Test
public void testInitialTreeTableState() {
assertTrue(treeTable.isShowRoot());
assertSame(root, treeTable.getRoot());
assertTrue(root.getChildren().get(rootLeafChildIndex).isLeaf());
assertTrue(root.getChildren().get(rootExpandedChildIndex).isExpanded());
assertFalse(root.getChildren().get(rootCollapsedChildIndex).isExpanded());
int rowCount = root.getChildren().size() + 1
+ root.getChildren().get(rootExpandedChildIndex).getChildren().size();
assertEquals(rowCount, treeTable.getExpandedItemCount());
showTreeTable();
List<Node> children = List.of(treeTable);
assertEquals(children, content.getChildren());
assertTrue(sm.isEmpty());
}
protected void showTreeTable() {
showControl(treeTable);
}
protected void showControl(Control control) {
if (content == null) {
content = new VBox();
scene = new Scene(content);
stage = new Stage();
stage.setScene(scene);
}
if (!content.getChildren().contains(control)) {
content.getChildren().add(control);
}
stage.show();
stage.requestFocus();
control.requestFocus();
assertTrue(control.isFocused());
assertSame(control, scene.getFocusOwner());
}
protected void fillTree(TreeItem<String> rootItem) {
rootItem.setExpanded(true);
rootItem.getChildren().add(0, new TreeItem<>("leafChild"));
for (int i = 0; i < 10; i++) {
TreeItem<String> newChild = new TreeItem<>("child " + i);
if (i == 0) newChild.setExpanded(true);
rootItem.getChildren().add(newChild);
for (int j = 0; j < 3; j++) {
TreeItem<String> newChild2 = new TreeItem<>(i + " grandChild " + j);
newChild.getChildren().add(newChild2);
}
}
}
@Before
public void setup() {
Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
if (throwable instanceof RuntimeException) {
throw (RuntimeException)throwable;
} else {
Thread.currentThread().getThreadGroup().uncaughtException(thread, throwable);
}
});
rootLeafChildIndex = 0;
rootExpandedChildIndex = 1;
rootCollapsedChildIndex = 2;
root = new TreeItem<>("Root");
treeTable = new TreeTableView<String>(root);
fillTree(root);
sm = treeTable.getSelectionModel();
TreeTableColumn<String, String> treeColumn = new TreeTableColumn<>("Col1");
treeColumn.setPrefWidth(200);
treeColumn.setCellValueFactory(call -> new ReadOnlyStringWrapper(call.getValue().getValue()));
treeTable.getColumns().add(treeColumn);
}
@After
public void tearDown() {
if (stage != null) stage.hide();
Thread.currentThread().setUncaughtExceptionHandler(null);
}
}
