package test.javafx.scene.control.skin;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.sun.javafx.tk.Toolkit;
import static javafx.collections.FXCollections.*;
import static javafx.scene.control.ControlShim.*;
import static javafx.scene.control.SkinBaseShim.*;
import static javafx.scene.control.skin.TableSkinShim.*;
import static javafx.scene.control.skin.TableSkinShim.getVirtualFlow;
import static javafx.scene.control.skin.TextInputSkinShim.*;
import static org.junit.Assert.*;
import static test.com.sun.javafx.scene.control.infrastructure.ControlSkinFactory.*;
import static test.com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.control.skin.TableRowSkin;
import javafx.scene.control.skin.TreeTableRowSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import test.com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils;
import test.com.sun.javafx.scene.control.test.Person;
public class SkinCleanupTest {
private Scene scene;
private Stage stage;
private Pane root;
@Ignore("JDK-8277000")
@Test
public void testTreeTableRowFixedCellSizeListener() {
TreeTableView<Person> tableView = createPersonTreeTable(false);
showControl(tableView, true);
TreeTableRow<?> tableRow = (TreeTableRow<?>) getCell(tableView, 1);
TreeTableRowSkin<?> rowSkin = (TreeTableRowSkin<?>) tableRow.getSkin();
assertNull("row skin must not have listener to fixedCellSize",
unregisterChangeListeners(rowSkin, tableView.fixedCellSizeProperty()));
}
@Test
public void testTreeTablePrefRowWidthFixedCellSize() {
TreeTableView<String[]> table = createManyColumnsTreeTableView(true);
showControl(table, false, 300, 800);
double totalColumnWidth = table.getVisibleLeafColumns().stream()
.mapToDouble(col -> col.getWidth())
.sum();
TreeTableRow<?> tableRow = (TreeTableRow<?>) VirtualFlowTestUtils.getCell(table, 2);
assertEquals("pref row width for fixed cell size", totalColumnWidth, tableRow.prefWidth(-1), .1);
}
@Test
public void testTreeTablePrefRowTreeTable() {
TreeTableView<String[]> table = createManyColumnsTreeTableView(false);
showControl(table, false, 300, 800);
double totalColumnWidth = table.getVisibleLeafColumns().stream()
.mapToDouble(col -> col.getWidth())
.sum();
TreeTableRow<?> tableRow = (TreeTableRow<?>) VirtualFlowTestUtils.getCell(table, 2);
assertEquals("sanity: pref row witdh for not fixed cell size", totalColumnWidth, tableRow.prefWidth(-1), .1);
}
@Test
public void testTreeTableRowTreeColumnListenerReplaceSkin() {
TreeTableView<Person> tableView = createPersonTreeTable(false);
showControl(tableView, true);
TreeTableRow<?> tableRow = (TreeTableRow<?>) getCell(tableView, 1);
replaceSkin(tableRow);
tableView.setTreeColumn(tableView.getColumns().get(1));
assertTrue("dirty marker must have been set", isDirty(tableRow));
}
@Test
public void testTreeTableRowTreeColumnListener() {
TreeTableView<Person> tableView = createPersonTreeTable(false);
showControl(tableView, true);
TreeTableRow<?> tableRow = (TreeTableRow<?>) getCell(tableView, 1);
tableView.setTreeColumn(tableView.getColumns().get(1));
assertTrue("dirty marker must have been set", isDirty(tableRow));
}
@Test
public void testTreeTableRowGraphicListenerReplaceSkin() {
TreeTableView<Person> tableView = createPersonTreeTable(false);
showControl(tableView, true);
int index = 1;
Label graphic = new Label("dummy");
TreeItem<Person> treeItem = tableView.getTreeItem(index);
treeItem.setGraphic(graphic);
TreeTableRow<?> tableRow = (TreeTableRow<?>) getCell(tableView, index);
replaceSkin(tableView);
tableRow.layout();
assertEquals(index, tableRow.getIndex());
assertTrue(tableRow.getChildrenUnmodifiable().contains(graphic));
}
@Test
public void testTreeTableRowGraphicListener() {
TreeTableView<Person> tableView = createPersonTreeTable(false);
showControl(tableView, true);
int index = 1;
Label graphic = new Label("dummy");
tableView.getTreeItem(index).setGraphic(graphic);
Toolkit.getToolkit().firePulse();
TreeTableRow<?> tableRow = (TreeTableRow<?>) getCell(tableView, index);
assertTrue(tableRow.getChildrenUnmodifiable().contains(graphic));
}
@Test
public void testTreeTableRowFixedCellSizeReplaceSkin() {
TreeTableView<Person> tableView = createPersonTreeTable(false);
showControl(tableView, true);
TreeTableRow<?> tableRow = (TreeTableRow<?>) getCell(tableView, 1);
replaceSkin(tableRow);
double fixed = 200;
tableView.setFixedCellSize(fixed);
assertEquals("fixed cell size: ", fixed, tableRow.prefHeight(-1), 1);
}
@Test
public void testTreeTableRowFixedCellSize() {
TreeTableView<Person> tableView = createPersonTreeTable(false);
showControl(tableView, true);
TreeTableRow<?> tableRow = (TreeTableRow<?>) getCell(tableView, 1);
double fixed = 200;
tableView.setFixedCellSize(fixed);
assertEquals("fixed cell size: ", fixed, tableRow.prefHeight(-1), 1);
}
@Test
public void testTreeTableRowFixedCellSizeEnabledReplaceSkin() {
TreeTableView<Person> tableView = createPersonTreeTable(false);
showControl(tableView, true);
TreeTableRow<?> tableRow = (TreeTableRow<?>) getCell(tableView, 1);
replaceSkin(tableRow);
assertFalse("fixed cell size disabled initially", isFixedCellSizeEnabled(tableRow));
double fixed = 200;
tableView.setFixedCellSize(fixed);
assertTrue("fixed cell size enabled", isFixedCellSizeEnabled(tableRow));
}
@Test
public void testTreeTableRowFixedCellSizeEnabled() {
TreeTableView<Person> tableView = createPersonTreeTable(false);
showControl(tableView, true);
TreeTableRow<?> tableRow = (TreeTableRow<?>) getCell(tableView, 1);
assertFalse("fixed cell size disabled initially", isFixedCellSizeEnabled(tableRow));
double fixed = 200;
tableView.setFixedCellSize(fixed);
assertTrue("fixed cell size enabled", isFixedCellSizeEnabled(tableRow));
}
@Test
public void testTreeTableRowVirtualFlowWidthListenerReplaceSkin() {
TreeTableView<Person> tableView = createPersonTreeTable(false);
showControl(tableView, true);
VirtualFlow<?> flow = getVirtualFlow(tableView);
TreeTableRow<?> tableRow = (TreeTableRow<?>) getCell(tableView, 1);
replaceSkin(tableRow);
Toolkit.getToolkit().firePulse();
TreeTableRowSkin<?> rowSkin = (TreeTableRowSkin<?>) tableRow.getSkin();
assertNotNull("row skin must have listener to virtualFlow width",
unregisterChangeListeners(rowSkin, flow.widthProperty()));
}
@Test
public void testTreeTableRowVirtualFlowWidthListener() {
TreeTableView<Person> tableView = createPersonTreeTable(false);
showControl(tableView, true);
VirtualFlow<?> flow = getVirtualFlow(tableView);
TreeTableRow<?> tableRow = (TreeTableRow<?>) getCell(tableView, 1);
TreeTableRowSkin<?> rowSkin = (TreeTableRowSkin<?>) tableRow.getSkin();
assertNotNull("row skin must have listener to virtualFlow width",
unregisterChangeListeners(rowSkin, flow.widthProperty()));
}
@Test
public void testTreeTableRowChildCountFixedCellSizeReplaceSkin() {
TreeTableView<Person> tableView = createPersonTreeTable(false);
tableView.setFixedCellSize(100);
showControl(tableView, true);
TreeTableRow<?> tableRow = (TreeTableRow<?>) getCell(tableView, 0);
int childCount = tableRow.getChildrenUnmodifiable().size();
replaceSkin(tableRow);
Toolkit.getToolkit().firePulse();
assertEquals(childCount, tableRow.getChildrenUnmodifiable().size());
}
@Test
public void testTreeTableRowChildCountReplaceSkin() {
TreeTableView<Person> tableView = createPersonTreeTable(false);
showControl(tableView, true);
TreeTableRow<?> tableRow = (TreeTableRow<?>) getCell(tableView, 0);
int childCount = tableRow.getChildrenUnmodifiable().size();
replaceSkin(tableRow);
Toolkit.getToolkit().firePulse();
assertEquals(childCount, tableRow.getChildrenUnmodifiable().size());
}
@Test
public void testTreeTableRowVirtualFlowReplaceSkin() {
TreeTableView<Person> tableView = createPersonTreeTable(false);
showControl(tableView, true);
TreeTableRow<?> tableRow = (TreeTableRow<?>) getCell(tableView, 1);
replaceSkin(tableRow);
assertEquals(tableView.getSkin(), getTableViewSkin(tableRow));
assertEquals(getVirtualFlow(tableView), getVirtualFlow(tableRow));
}
@Test
public void testTreeTableRowVirtualFlow() {
TreeTableView<Person> tableView = createPersonTreeTable(false);
showControl(tableView, true);
TreeTableRow<?> tableRow = (TreeTableRow<?>) getCell(tableView, 1);
assertEquals(tableView.getSkin(), getTableViewSkin(tableRow));
assertEquals(getVirtualFlow(tableView), getVirtualFlow(tableRow));
}
@Ignore("JDK-8274065")
@Test
public void testTreeTableRowVirtualFlowInstallSkin() {
TreeTableRow<?> tableRow = createTreeTableRow(1);
installDefaultSkin(tableRow);
TreeTableView<?> tableView = tableRow.getTreeTableView();
assertEquals(tableView.getSkin(), getTableViewSkin(tableRow));
assertEquals(getVirtualFlow(tableView), getVirtualFlow(tableRow));
}
@Test
public void testTreeTableRowWithGraphicMemoryLeak() {
TreeTableView<Person> tableView = createPersonTreeTable(false);
showControl(tableView, true);
tableView.getTreeItem(1).setGraphic(new Label("nothing"));
TreeTableRow<?> tableRow = (TreeTableRow<?>) getCell(tableView, 1);
WeakReference<?> weakRef = new WeakReference<>(replaceSkin(tableRow));
assertNotNull(weakRef.get());
attemptGC(weakRef);
assertEquals("Skin must be gc'ed", null, weakRef.get());
}
@Ignore("JDK-8274065")
@Test
public void testTreeTableRowWithGraphicMemoryLeakInstallSkin() {
TreeTableRow<?> tableRow = createTreeTableRow(1);
installDefaultSkin(tableRow);
tableRow.getTreeTableView().getTreeItem(1).setGraphic(new Label("nothing"));
WeakReference<?> weakRef = new WeakReference<>(replaceSkin(tableRow));
assertNotNull(weakRef.get());
attemptGC(weakRef);
assertEquals("Skin must be gc'ed", null, weakRef.get());
}
@Test
public void testTreeTableRowLeafColumnsListenerReplaceSkin() {
TreeTableView<Person> tableView = createPersonTreeTable(false);
showControl(tableView, true);
TreeTableRow<?> tableRow = (TreeTableRow<?>) getCell(tableView, 1);
replaceSkin(tableRow);
tableView.getColumns().get(0).setVisible(false);
assertTrue("dirty marker must have been set", isDirty(tableRow));
}
@Test
public void testTreeTableRowLeafColumnsListener() {
TreeTableView<Person> tableView = createPersonTreeTable(false);
showControl(tableView, true);
TreeTableRow<?> tableRow = (TreeTableRow<?>) getCell(tableView, 1);
tableView.getColumns().get(0).setVisible(false);
assertTrue("dirty marker must have been set", isDirty(tableRow));
}
@Test
public void testTreeTableRowItemListenerReplaceSkin() {
TreeTableView<Person> tableView = createPersonTreeTable(false);
showControl(tableView, true);
int initial = 0;
TreeTableRow<?> tableRow = (TreeTableRow<?>) getCell(tableView, initial);
replaceSkin(tableRow);
int index = 1;
tableRow.updateIndex(index);
List<IndexedCell<?>> cells = getCells(tableRow);
assertEquals(tableView.getVisibleLeafColumns().size(), cells.size());
assertEquals("cell index must be updated", index, cells.get(0).getIndex());
}
@Test
public void testTreeTableRowItemListener() {
TreeTableView<Person> tableView = createPersonTreeTable(false);
showControl(tableView, true);
int initial = 0;
TreeTableRow<?> tableRow = (TreeTableRow<?>) getCell(tableView, initial);
int index = 1;
tableRow.updateIndex(index);
List<IndexedCell<?>> cells = getCells(tableRow);
assertEquals(tableView.getVisibleLeafColumns().size(), cells.size());
assertEquals("cell index must be updated", index, cells.get(0).getIndex());
}
private TreeTableView<String[]> createManyColumnsTreeTableView(boolean useFixedCellSize) {
final TreeTableView<String[]> tableView = new TreeTableView<>();
final ObservableList<TreeTableColumn<String[], ?>> columns = tableView
.getColumns();
for (int i = 0; i < COL_COUNT; i++) {
TreeTableColumn<String[], String> column = new TreeTableColumn<>("Col" + i);
final int colIndex = i;
column.setCellValueFactory((cell) -> new SimpleStringProperty(
cell.getValue().getValue()[colIndex]));
columns.add(column);
sizeColumn(column, COL_WIDTH);
}
ObservableList<String[]> items = FXCollections.observableArrayList();
for (int i = 0; i < ROW_COUNT; i++) {
String[] rec = new String[COL_COUNT];
for (int j = 0; j < rec.length; j++) {
rec[j] = i + ":" + j;
}
items.add(rec);
}
TreeItem<String[]> root = new TreeItem<>(items.get(0));
root.setExpanded(true);
for (int i = 1; i < items.size(); i++) {
root.getChildren().add(new TreeItem<>(items.get(i)));
}
tableView.setRoot(root);
if (useFixedCellSize) {
tableView.setFixedCellSize(FIXED_CELL_SIZE);
}
return tableView;
}
private TreeTableRow<?> createTreeTableRow(int index) {
TreeTableView<Person> table = createPersonTreeTable(true);
TreeTableRow<Person> tableRow = new TreeTableRow<>();
tableRow.updateTreeTableView(table);
tableRow.updateIndex(index);
assertFalse("sanity: row must not be empty at index: " + index, tableRow.isEmpty());
return tableRow;
}
private TreeTableView<Person> createPersonTreeTable(boolean installSkin) {
TreeItem<Person> root = new TreeItem<>(new Person("rootFirst", "rootLast", "root@nowhere.com"));
root.setExpanded(true);
root.getChildren().addAll(
Person.persons().stream()
.map(TreeItem::new)
.collect(Collectors.toList()));
TreeTableView<Person> table = new TreeTableView<>(root);
assertEquals(Person.persons().size() + 1, table.getExpandedItemCount());
TreeTableColumn<Person, String> firstName = new TreeTableColumn<>("First Name");
firstName.setCellValueFactory(new TreeItemPropertyValueFactory<>("firstName"));
TreeTableColumn<Person, String> lastName = new TreeTableColumn<>("Last Name");
lastName.setCellValueFactory(new TreeItemPropertyValueFactory<>("lastName"));
table.getColumns().addAll(firstName, lastName);
if (installSkin) {
installDefaultSkin(table);
}
return table;
}
@Ignore("JDK-8277000")
@Test
public void testTableRowFixedCellSizeListener() {
TableView<Person> tableView = createPersonTable(false);
showControl(tableView, true);
TableRow<?> tableRow = (TableRow<?>) getCell(tableView, 1);
TableRowSkin<?> rowSkin = (TableRowSkin<?>) tableRow.getSkin();
assertNull("row skin must not have listener to fixedCellSize",
unregisterChangeListeners(rowSkin, tableView.fixedCellSizeProperty()));
}
@Test
public void testTablePrefRowWidthFixedCellSize() {
TableView<String[]> table = createManyColumnsTableView(true);
showControl(table, false, 300, 800);
double totalColumnWidth = table.getVisibleLeafColumns().stream()
.mapToDouble(col -> col.getWidth())
.sum();
TableRow<?> tableRow = (TableRow<?>) VirtualFlowTestUtils.getCell(table, 2);
assertEquals("pref row width for fixed cell size", totalColumnWidth, tableRow.prefWidth(-1), .1);
}
@Test
public void testTablePrefRowWidth() {
TableView<String[]> table = createManyColumnsTableView(false);
showControl(table, false, 300, 800);
double totalColumnWidth = table.getVisibleLeafColumns().stream()
.mapToDouble(col -> col.getWidth())
.sum();
TableRow<?> tableRow = (TableRow<?>) VirtualFlowTestUtils.getCell(table, 2);
assertEquals("sanity: pref row witdh for not fixed cell size", totalColumnWidth, tableRow.prefWidth(-1), .1);
}
@Test
public void testTableRowFixedCellSizeReplaceSkin() {
TableView<Person> tableView = createPersonTable(false);
showControl(tableView, true);
TableRow<?> tableRow = (TableRow<?>) getCell(tableView, 1);
replaceSkin(tableRow);
double fixed = 200;
tableView.setFixedCellSize(fixed);
assertEquals("fixed cell size: ", fixed, tableRow.prefHeight(-1), 1);
}
@Test
public void testTableRowFixedCellSize() {
TableView<Person> tableView = createPersonTable(false);
showControl(tableView, true);
TableRow<?> tableRow = (TableRow<?>) getCell(tableView, 1);
double fixed = 200;
tableView.setFixedCellSize(fixed);
assertEquals("fixed cell size: ", fixed, tableRow.prefHeight(-1), 1);
}
@Test
public void testTableRowFixedCellSizeEnabledReplaceSkin() {
TableView<Person> tableView = createPersonTable(false);
showControl(tableView, true);
TableRow<?> tableRow = (TableRow<?>) getCell(tableView, 1);
replaceSkin(tableRow);
assertFalse("fixed cell size disabled initially", isFixedCellSizeEnabled(tableRow));
double fixed = 200;
tableView.setFixedCellSize(fixed);
assertTrue("fixed cell size enabled", isFixedCellSizeEnabled(tableRow));
}
@Test
public void testTableRowFixedCellSizeEnabled() {
TableView<Person> tableView = createPersonTable(false);
showControl(tableView, true);
TableRow<?> tableRow = (TableRow<?>) getCell(tableView, 1);
assertFalse("fixed cell size disabled initially", isFixedCellSizeEnabled(tableRow));
double fixed = 200;
tableView.setFixedCellSize(fixed);
assertTrue("fixed cell size enabled", isFixedCellSizeEnabled(tableRow));
}
@Test
public void testTableRowVirtualFlowWidthListenerReplaceSkin() {
TableView<Person> tableView = createPersonTable(false);
showControl(tableView, true);
VirtualFlow<?> flow = getVirtualFlow(tableView);
TableRow<?> tableRow = (TableRow<?>) getCell(tableView, 1);
replaceSkin(tableRow);
Toolkit.getToolkit().firePulse();
TableRowSkin<?> rowSkin = (TableRowSkin<?>) tableRow.getSkin();
assertNotNull("row skin must have listener to virtualFlow width",
unregisterChangeListeners(rowSkin, flow.widthProperty()));
}
@Test
public void testTableRowVirtualFlowWidthListener() {
TableView<Person> tableView = createPersonTable(false);
showControl(tableView, true);
VirtualFlow<?> flow = getVirtualFlow(tableView);
TableRow<?> tableRow = (TableRow<?>) getCell(tableView, 1);
TableRowSkin<?> rowSkin = (TableRowSkin<?>) tableRow.getSkin();
assertNotNull("row skin must have listener to virtualFlow width",
unregisterChangeListeners(rowSkin, flow.widthProperty()));
}
@Test
public void testTableRowChildCountFixedCellSizeReplaceSkin() {
TableView<Person> tableView = createPersonTable(false);
tableView.setFixedCellSize(100);
showControl(tableView, true);
TableRow<?> tableRow = (TableRow<?>) getCell(tableView, 0);
int childCount = tableRow.getChildrenUnmodifiable().size();
assertEquals(2, childCount);
replaceSkin(tableRow);
Toolkit.getToolkit().firePulse();
assertEquals(childCount, tableRow.getChildrenUnmodifiable().size());
}
@Test
public void testTableRowChildCountReplaceSkin() {
TableView<Person> tableView = createPersonTable(false);
showControl(tableView, true);
TableRow<?> tableRow = (TableRow<?>) getCell(tableView, 0);
int childCount = tableRow.getChildrenUnmodifiable().size();
assertEquals(2, childCount);
replaceSkin(tableRow);
Toolkit.getToolkit().firePulse();
assertEquals(childCount, tableRow.getChildrenUnmodifiable().size());
}
@Test
public void testTableRowVirtualFlowReplaceSkin() {
TableView<Person> tableView = createPersonTable(false);
showControl(tableView, true);
TableRow<?> tableRow = (TableRow<?>) getCell(tableView, 1);
replaceSkin(tableRow);
assertEquals(tableView.getSkin(), getTableViewSkin(tableRow));
assertEquals(getVirtualFlow(tableView), getVirtualFlow(tableRow));
}
@Test
public void testTableRowVirtualFlow() {
TableView<Person> tableView = createPersonTable(false);
showControl(tableView, true);
TableRow<?> tableRow = (TableRow<?>) getCell(tableView, 1);
assertEquals(tableView.getSkin(), getTableViewSkin(tableRow));
assertEquals(getVirtualFlow(tableView), getVirtualFlow(tableRow));
}
@Ignore("JDK-8274065")
@Test
public void testTableRowVirtualFlowInstallSkin() {
TableRow<?> tableRow = createTableRow(0);
installDefaultSkin(tableRow);
TableView<?> tableView = tableRow.getTableView();
assertEquals(tableView.getSkin(), getTableViewSkin(tableRow));
assertEquals(getVirtualFlow(tableView), getVirtualFlow(tableRow));
}
@Test
public void testTableRowLeafColumnsListenerReplaceSkin() {
TableView<Person> tableView = createPersonTable(false);
showControl(tableView, true);
TableRow<?> tableRow = (TableRow<?>) getCell(tableView, 1);
replaceSkin(tableRow);
tableView.getColumns().get(0).setVisible(false);
assertTrue("dirty marker must have been set", isDirty(tableRow));
}
@Test
public void testTableRowLeafColumnsListener() {
TableView<Person> tableView = createPersonTable(false);
showControl(tableView, true);
TableRow<?> tableRow = (TableRow<?>) getCell(tableView, 1);
tableView.getColumns().get(0).setVisible(false);
assertTrue("dirty marker must have been set", isDirty(tableRow));
}
@Test
public void testTableRowItemListenerReplaceSkin() {
TableView<Person> tableView = createPersonTable(false);
showControl(tableView, true);
int initial = 0;
TableRow<?> tableRow = (TableRow<?>) getCell(tableView, initial);
replaceSkin(tableRow);
int index = 1;
tableRow.updateIndex(index);
List<IndexedCell<?>> cells = getCells(tableRow);
assertEquals(tableView.getVisibleLeafColumns().size(), cells.size());
assertEquals("cell index must be updated", index, cells.get(0).getIndex());
}
@Test
public void testTableRowItemListener() {
TableView<Person> tableView = createPersonTable(false);
showControl(tableView, true);
int initial = 0;
TableRow<?> tableRow = (TableRow<?>) getCell(tableView, initial);
int index = 1;
tableRow.updateIndex(index);
Toolkit.getToolkit().firePulse();
List<IndexedCell<?>> cells = getCells(tableRow);
assertEquals(tableView.getVisibleLeafColumns().size(), cells.size());
assertEquals("cell index must be updated", index, cells.get(0).getIndex());
}
private static final int COL_COUNT = 50;
private static final int ROW_COUNT = 10;
private static final double COL_WIDTH = 50;
private static final double FIXED_CELL_SIZE = 24;
private TableView<String[]> createManyColumnsTableView(boolean useFixedCellSize) {
final TableView<String[]> tableView = new TableView<>();
final ObservableList<TableColumn<String[], ?>> columns = tableView
.getColumns();
tableView.getSelectionModel().setCellSelectionEnabled(true);
for (int i = 0; i < COL_COUNT; i++) {
TableColumn<String[], String> column = new TableColumn<>("Col" + i);
final int colIndex = i;
column.setCellValueFactory((cell) -> new SimpleStringProperty(
cell.getValue()[colIndex]));
columns.add(column);
sizeColumn(column, COL_WIDTH);
}
ObservableList<String[]> items = tableView.getItems();
for (int i = 0; i < ROW_COUNT; i++) {
String[] rec = new String[COL_COUNT];
for (int j = 0; j < rec.length; j++) {
rec[j] = i + ":" + j;
}
items.add(rec);
}
if (useFixedCellSize) {
tableView.setFixedCellSize(FIXED_CELL_SIZE);
}
return tableView;
}
private void sizeColumn(TableColumnBase<?, ?> column, double width) {
column.setPrefWidth(width);
column.setMinWidth(width);
column.setMaxWidth(width);
}
private TableRow<?> createTableRow(int index) {
TableView<Person> table = createPersonTable(true);
TableRow<Person> tableRow = new TableRow<>();
tableRow.updateTableView(table);
tableRow.updateIndex(index);
assertFalse("sanity: row must not be empty at index: " + index, tableRow.isEmpty());
return tableRow;
}
private TableView<Person> createPersonTable(boolean installSkin) {
TableView<Person> table = new TableView<>(Person.persons());
TableColumn<Person, String> firstName = new TableColumn<>("First Name");
firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
TableColumn<Person, String> lastName = new TableColumn<>("Last Name");
lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
table.getColumns().addAll(firstName, lastName);
if (installSkin) {
installDefaultSkin(table);
}
return table;
}
@Test
public void testScrollEventFilter() {
TextArea area = new TextArea("some text");
showControl(area, true);
setHandlePressed(area, true);
ScrollEvent scrollEvent = new ScrollEvent(ScrollEvent.ANY, 0, 0, 0, 0, false, false, false, false,
true,
false, 0, 0, 0, USE_PREF_SIZE, null, USE_COMPUTED_SIZE, null, 0, 0, null);
assertTrue("sanity: created a fake direct event", scrollEvent.isDirect());
ScrollEvent copy = scrollEvent.copyFor(area, area);
Event.fireEvent(area, copy);
assertTrue("scrollEvent must be consumed", copy.isConsumed());
}
@Test
public void testTextAreaSelectUpdate() {
TextArea area = new TextArea("some text");
installDefaultSkin(area);
Text textNode = getTextNode(area);
area.selectAll();
textNode.getParent().getParent().layout();
int end = area.getLength();
assertEquals("sanity: area caret moved to end", end, area.getCaretPosition());
assertEquals("sanity: area selection updated", end, area.getSelection().getEnd());
assertEquals("textNode end", end, textNode.getSelectionEnd());
}
@Test
public void testTextAreaSetWrapUpdate() {
TextArea area = new TextArea("some text");
installDefaultSkin(area);
boolean isWrap = area.isWrapText();
ScrollPane scrollPane = getScrollPane(area);
assertEquals(isWrap, scrollPane.isFitToWidth());
area.setWrapText(!isWrap);
assertEquals(!isWrap, scrollPane.isFitToWidth());
}
@Test
public void testTextAreaSetColumnCount() {
TextArea area = new TextArea("some text");
int prefColumn = area.getPrefColumnCount();
assertEquals("sanity: initial count", TextArea.DEFAULT_PREF_COLUMN_COUNT, prefColumn);
installDefaultSkin(area);
replaceSkin(area);
area.setPrefColumnCount(prefColumn * 2);
}
@Test
public void testTextAreaSetColumnCountUpdate() {
TextArea area = new TextArea("some text");
int prefColumn = area.getPrefColumnCount();
assertEquals("sanity: initial count", TextArea.DEFAULT_PREF_COLUMN_COUNT, prefColumn);
installDefaultSkin(area);
ScrollPane scrollPane = getScrollPane(area);
double prefViewportWidth = scrollPane.getPrefViewportWidth();
area.setPrefColumnCount(prefColumn * 2);
assertEquals("prefViewportWidth must be updated", prefViewportWidth * 2, scrollPane.getPrefViewportWidth(), 1);
}
@Test
public void testTextAreaSetRowCount() {
TextArea area = new TextArea("some text");
int prefRows = area.getPrefRowCount();
installDefaultSkin(area);
replaceSkin(area);
area.setPrefRowCount(prefRows * 2);
}
@Test
public void testTextAreaSetRowCountUpdate() {
TextArea area = new TextArea("some text");
int prefRows = area.getPrefRowCount();
assertEquals("sanity: initial row count", TextArea.DEFAULT_PREF_ROW_COUNT, prefRows);
installDefaultSkin(area);
ScrollPane scrollPane = getScrollPane(area);
double prefViewportHeight = scrollPane.getPrefViewportHeight();
area.setPrefRowCount(prefRows * 2);
assertEquals("prefViewportHeight must be updated", prefViewportHeight * 2, scrollPane.getPrefViewportHeight(), 1);
}
@Test
public void testTextAreaSetTextUpdate() {
String initial = "some text";
TextArea area = new TextArea(initial);
installDefaultSkin(area);
Text textNode = getTextNode(area);
assertEquals("sanity initial text sync'ed to textNode", initial, textNode.getText());
String replaced = "replaced text";
area.setText(replaced);
assertEquals(replaced, textNode.getText());
}
@Test
public void testTextAreaPrompt() {
TextArea area = new TextArea();
installDefaultSkin(area);
replaceSkin(area);
area.setPromptText("prompt");
}
@Test
public void testTextAreaPromptUpdate() {
TextArea area = new TextArea();
installDefaultSkin(area);
assertNull("sanity: default prompt is null", getPromptNode(area));
area.setPromptText("prompt");
assertNotNull("prompt node must be created", getPromptNode(area));
}
@Test
public void testTextAreaChildren() {
TextArea area = new TextArea("some text");
installDefaultSkin(area);
int children = area.getChildrenUnmodifiable().size();
replaceSkin(area);
assertEquals("children size must be unchanged: ", children, area.getChildrenUnmodifiable().size());
}
@Test
public void testTextAreaSetScrollLeft() {
TextArea area = new TextArea(LOREM_IPSUM + LOREM_IPSUM);
installDefaultSkin(area);
replaceSkin(area);
area.setScrollLeft(500);
}
@Test
public void testTextAreaSetScrollLeftUpdate() {
TextArea area = new TextArea(LOREM_IPSUM + LOREM_IPSUM);
showControl(area, true);
ScrollPane scrollPane = getScrollPane(area);
double scrollLeft = 500;
area.setScrollLeft(scrollLeft);
Toolkit.getToolkit().firePulse();
assertEquals("sanity: scrollLeft updated", scrollLeft, area.getScrollLeft(), 0.1);
assertTrue("scrollPane hValue > 0", scrollPane.getHvalue() > 0.0);
}
@Test
public void testTextAreaSetScrollTop() {
TextArea area = new TextArea(LOREM_IPSUM + LOREM_IPSUM);
area.setWrapText(true);
installDefaultSkin(area);
replaceSkin(area);
area.setScrollTop(100);
}
@Ignore("8272082")
@Test
public void testTextAreaSetScrollTopUpdate() {
TextArea area = new TextArea(LOREM_IPSUM + LOREM_IPSUM);
area.setWrapText(true);
showControl(area, true, 300, 300);
ScrollPane scrollPane = getScrollPane(area);
double scrollTop = 100;
area.setScrollTop(scrollTop);
Toolkit.getToolkit().firePulse();
assertEquals("sanity: scrollTop updated", scrollTop, area.getScrollTop(), 0.1);
assertTrue("scrollPane vValue > 0", scrollPane.getVvalue() > 0.0);
}
public static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, "
+ "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim "
+ "ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip "
+ "ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate "
+ "velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat "
+ "cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
@Test
public void testTextFieldCaretPosition() {
TextField field = new TextField("some text");
showControl(field, true);
int index = 2;
field.positionCaret(index);
replaceSkin(field);
field.positionCaret(index + 1);
}
@Test
public void testTextFieldCaretPositionUpdate() {
TextField field = new TextField("some text");
showControl(field, true);
Text textNode = getTextNode(field);
field.positionCaret(2);
assertEquals("textNode caret", field.getCaretPosition(), textNode.getCaretPosition());
}
@Test
public void testTextFieldSelection() {
TextField field = new TextField("some text");
installDefaultSkin(field);
replaceSkin(field);
field.selectAll();
}
@Test
public void testTextFieldSelectionUpdate() {
TextField field = new TextField("some text");
installDefaultSkin(field);
Text textNode = getTextNode(field);
field.selectAll();
int end = field.getLength();
assertEquals("sanity: field caret moved to end", end, field.getCaretPosition());
assertEquals("sanity: field selection updated", end, field.getSelection().getEnd());
assertEquals("textNode end", end, textNode.getSelectionEnd());
}
@Test
public void testTextFieldText() {
TextField field = new TextField("some text");
installDefaultSkin(field);
replaceSkin(field);
field.setText("replaced");
}
@Test
public void testTextFieldFont() {
TextField field = new TextField("some text");
installDefaultSkin(field);
replaceSkin(field);
field.setFont(new Font(30));
}
@Test
public void testTextFieldAlignment() {
TextField field = new TextField("some text");
showControl(field, true);
assertTrue(field.getWidth() > 0);
replaceSkin(field);
field.setAlignment(Pos.TOP_RIGHT);
}
@Test
public void testTextFieldAlignmentUpdate() {
TextField rightAligned = new TextField("dummy");
rightAligned.setPrefColumnCount(50);
rightAligned.setAlignment(Pos.CENTER_RIGHT);
showControl(rightAligned, true);
double rightTranslate = getTextTranslateX(rightAligned);
TextField field = new TextField("dummy");
field.setPrefColumnCount(50);
assertEquals("sanity: ", Pos.CENTER_LEFT, field.getAlignment());
showControl(field, true);
Toolkit.getToolkit().firePulse();
double textTranslate = getTextTranslateX(field);
assertEquals("sanity:", 0, textTranslate, 1);
field.setAlignment(Pos.CENTER_RIGHT);
assertEquals("translateX must be updated", rightTranslate, getTextTranslateX(field), 1);
}
@Test
public void testTextFieldPrompt() {
TextField field = new TextField();
installDefaultSkin(field);
replaceSkin(field);
field.setPromptText("prompt");
}
@Test
public void testTextFieldPromptUpdate() {
TextField field = new TextField();
installDefaultSkin(field);
assertNull("sanity: default prompt is null", getPromptNode(field));
field.setPromptText("prompt");
assertNotNull("prompt node must be created", getPromptNode(field));
}
@Test
public void testTextFieldChildren() {
TextField field = new TextField("some text");
installDefaultSkin(field);
int children = field.getChildrenUnmodifiable().size();
replaceSkin(field);
assertEquals("children size must be unchanged: ", children, field.getChildrenUnmodifiable().size());
}
@Test
public void testTextInputMethodRequests() {
TextField field = new TextField("some text");
field.selectRange(2, 5);
String selected = field.getSelectedText();
installDefaultSkin(field);
assertEquals("sanity: skin has set requests", selected, field.getInputMethodRequests().getSelectedText());
field.getSkin().dispose();
if (field.getInputMethodRequests() != null) {
assertEquals(selected, field.getInputMethodRequests().getSelectedText());
}
}
@Test
public void testTextInputOnInputMethodTextChangedNoHandler() {
TextField field = new TextField("some text");
field.setOnInputMethodTextChanged(null);
installDefaultSkin(field);
field.getSkin().dispose();
assertNull("skin dispose must remove handler it has installed", field.getOnInputMethodTextChanged());
}
@Test
public void testTextInputOnInputMethodTextChangedWithHandler() {
TextField field = new TextField("some text");
EventHandler<? super InputMethodEvent> handler = e -> {};
field.setOnInputMethodTextChanged(handler);
installDefaultSkin(field);
assertSame("sanity: skin must not replace handler", handler, field.getOnInputMethodTextChanged());
field.getSkin().dispose();
assertSame("skin dispose must not remove handler that was installed by control",
handler, field.getOnInputMethodTextChanged());
}
@Test
public void testTextInputOnInputMethodTextChangedReplacedHandler() {
TextField field = new TextField("some text");
installDefaultSkin(field);
EventHandler<? super InputMethodEvent> handler = e -> {};
field.setOnInputMethodTextChanged(handler);
field.getSkin().dispose();
assertSame("skin dispose must not remove handler that was installed by control",
handler, field.getOnInputMethodTextChanged());
}
@Ignore("JDK-8268877")
@Test
public void testTextInputOnInputMethodTextChangedEvent() {
String initialText = "some text";
String prefix = "from input event";
TextField field = new TextField(initialText);
installDefaultSkin(field);
InputMethodEvent event = new InputMethodEvent(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED,
List.of(), prefix, 0);
Event.fireEvent(field, event);
assertEquals("sanity: prefix must be committed", prefix + initialText, field.getText());
replaceSkin(field);
Event.fireEvent(field, event);
assertEquals(" prefix must be committed again", prefix + prefix + initialText, field.getText());
}
@Ignore("JDK-8268877")
@Test
public void testTextInputOnInputMethodTextChangedHandler() {
TextField field = new TextField("some text");
installDefaultSkin(field);
EventHandler<? super InputMethodEvent> handler = field.getOnInputMethodTextChanged();
replaceSkin(field);
assertNotSame("replaced skin must replace skin handler", handler, field.getOnInputMethodTextChanged());
assertNotNull("handler must not be null  ", field.getOnInputMethodTextChanged());
}
@Test
public void testTreeViewSetRoot() {
TreeView<String> treeView = new TreeView<>(createRoot());
installDefaultSkin(treeView);
replaceSkin(treeView);
treeView.setRoot(createRoot());
}
@Test
public void testTreeViewAddRootChild() {
TreeView<String> treeView = new TreeView<>(createRoot());
installDefaultSkin(treeView);
replaceSkin(treeView);
treeView.getRoot().getChildren().add(createRoot());
}
@Test
public void testTreeViewReplaceRootChildren() {
TreeView<String> treeView = new TreeView<>(createRoot());
installDefaultSkin(treeView);
replaceSkin(treeView);
treeView.getRoot().getChildren().setAll(createRoot().getChildren());
}
@Test
public void testTreeViewRefresh() {
TreeView<String> treeView = new TreeView<>();
installDefaultSkin(treeView);
replaceSkin(treeView);
treeView.refresh();
}
@Test
public void testMemoryLeakAlternativeSkinWithRoot() {
TreeView<String> treeView = new TreeView<>(createRoot());
installDefaultSkin(treeView);
WeakReference<?> weakRef = new WeakReference<>(replaceSkin(treeView));
assertNotNull(weakRef.get());
attemptGC(weakRef);
assertEquals("Skin must be gc'ed", null, weakRef.get());
}
private TreeItem<String> createRoot() {
TreeItem<String> root = new TreeItem<>("root");
root.setExpanded(true);
root.getChildren().addAll(new TreeItem<>("child one"), new TreeItem<>("child two"));
return root;
}
@Test
public void testTreeCellReplaceTreeViewWithNull() {
TreeCell<Object> cell = new TreeCell<>();
TreeView<Object> treeView = new TreeView<>();
cell.updateTreeView(treeView);
installDefaultSkin(cell);
cell.updateTreeView(null);
treeView.setFixedCellSize(100);
}
@Test
public void testTreeCellPrefHeightOnReplaceTreeView() {
TreeCell<Object> cell = new TreeCell<>();
cell.updateTreeView(new TreeView<>());
installDefaultSkin(cell);
TreeView<Object> treeView = new TreeView<>();
treeView.setFixedCellSize(100);
cell.updateTreeView(treeView);
assertEquals("fixed cell set to value of new treeView",
cell.getTreeView().getFixedCellSize(),
cell.prefHeight(-1), 1);
}
@Test
public void testListCellReplaceListViewWithNull() {
ListCell<Object> cell = new ListCell<>();
ListView<Object> listView = new ListView<>();
cell.updateListView(listView);
installDefaultSkin(cell);
cell.updateListView(null);
listView.setFixedCellSize(100);
}
@Test
public void testListCellPrefHeightOnReplaceListView() {
ListCell<Object> cell = new ListCell<>();
cell.updateListView(new ListView<>());
installDefaultSkin(cell);
ListView<Object> listView = new ListView<>();
listView.setFixedCellSize(100);
cell.updateListView(listView);
assertEquals("fixed cell set to value of new listView",
cell.getListView().getFixedCellSize(),
cell.prefHeight(-1), 1);
}
@Test
public void testListViewAddItems() {
ListView<String> listView = new ListView<>();
installDefaultSkin(listView);
replaceSkin(listView);
listView.getItems().add("addded");
}
@Test
public void testListViewRefresh() {
ListView<String> listView = new ListView<>();
installDefaultSkin(listView);
replaceSkin(listView);
listView.refresh();
}
@Test
public void testListViewSetItems() {
ListView<String> listView = new ListView<>();
installDefaultSkin(listView);
replaceSkin(listView);
listView.setItems(observableArrayList());
}
@Test
public void testChoiceBoxSetItems() {
ChoiceBox<String> box = new ChoiceBox<>();
installDefaultSkin(box);
replaceSkin(box);
box.setItems(observableArrayList("one"));
box.getItems().add("added");
}
@Test
public void testChoiceBoxAddItems() {
ChoiceBox<String> box = new ChoiceBox<>();
installDefaultSkin(box);
replaceSkin(box);
box.getItems().add("added");
}
@Test
public void testToolBarAddItems() {
ToolBar bar = new ToolBar();
installDefaultSkin(bar);
replaceSkin(bar);
bar.getItems().add(new Rectangle());
}
@Test
public void testToolBarFocus() {
ToolBar bar = new ToolBar();
bar.getItems().addAll(new Button("dummy"), new Button("other"));
showControl(bar, false);
Button outside = new Button("outside");
showControl(outside, true);
bar.requestFocus();
assertEquals("first item in toolbar must be focused", bar.getItems().get(0), scene.getFocusOwner());
}
@Test
public void testChildrenCountAfterSkinIsReplaced() {
TabPane tabPane = new TabPane();
tabPane.getTabs().addAll(new Tab("0"), new Tab("1"));
installDefaultSkin(tabPane);
int childrenCount = tabPane.getChildrenUnmodifiable().size();
replaceSkin(tabPane);
assertEquals(childrenCount, tabPane.getChildrenUnmodifiable().size());
}
@Test
public void testChildrenCountAfterSkinIsRemoved() {
TabPane tabPane = new TabPane();
assertEquals(0, tabPane.getChildrenUnmodifiable().size());
tabPane.getTabs().addAll(new Tab("0"), new Tab("1"));
installDefaultSkin(tabPane);
assertEquals(3, tabPane.getChildrenUnmodifiable().size());
tabPane.setSkin(null);
assertNull(tabPane.getSkin());
assertEquals(0, tabPane.getChildrenUnmodifiable().size());
}
@Test
public void testNPEWhenTabsAddedAfterSkinIsReplaced() {
TabPane tabPane = new TabPane();
tabPane.getTabs().addAll(new Tab("0"), new Tab("1"));
installDefaultSkin(tabPane);
replaceSkin(tabPane);
tabPane.getTabs().addAll(new Tab("2"), new Tab("3"));
}
@Test
public void testNPEWhenTabRemovedAfterSkinIsReplaced() {
TabPane tabPane = new TabPane();
tabPane.getTabs().addAll(new Tab("0"), new Tab("1"));
installDefaultSkin(tabPane);
replaceSkin(tabPane);
tabPane.getTabs().remove(0);
}
@Test
public void testAddRemoveTabsAfterSkinIsReplaced() {
TabPane tabPane = new TabPane();
tabPane.getTabs().addAll(new Tab("0"), new Tab("1"));
installDefaultSkin(tabPane);
assertEquals(2, tabPane.getTabs().size());
assertEquals(3, tabPane.getChildrenUnmodifiable().size());
replaceSkin(tabPane);
tabPane.getTabs().addAll(new Tab("2"), new Tab("3"));
assertEquals(4, tabPane.getTabs().size());
assertEquals(5, tabPane.getChildrenUnmodifiable().size());
tabPane.getTabs().clear();
assertEquals(0, tabPane.getTabs().size());
assertEquals(1, tabPane.getChildrenUnmodifiable().size());
}
protected void showControl(Control control, boolean focused) {
showControl(control, focused, -1, -1);
}
protected void showControl(Control control, boolean focused, double sceneX, double sceneY) {
if (root == null) {
root = new VBox();
if (sceneX > 0) {
scene = new Scene(root, sceneX, sceneY);
} else {
scene = new Scene(root);
}
stage = new Stage();
stage.setScene(scene);
}
if (!root.getChildren().contains(control)) {
root.getChildren().add(control);
}
stage.show();
if (focused) {
stage.requestFocus();
control.requestFocus();
assertTrue(control.isFocused());
assertSame(control, scene.getFocusOwner());
}
}
@After
public void cleanup() {
if (stage != null) stage.hide();
Thread.currentThread().setUncaughtExceptionHandler(null);
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
}
}
