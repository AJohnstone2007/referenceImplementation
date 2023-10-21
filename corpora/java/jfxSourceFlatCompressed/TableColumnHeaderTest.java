package test.javafx.scene.control.skin;
import com.sun.javafx.tk.Toolkit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.Skinnable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import test.com.sun.javafx.scene.control.infrastructure.MouseEventFirer;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;
import test.com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils;
import test.com.sun.javafx.scene.control.test.Person;
import javafx.scene.control.skin.TableColumnHeaderShim;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
public class TableColumnHeaderTest {
private TableColumnHeader firstColumnHeader;
private TableView<Person> tableView;
private StageLoader sl;
private static String NAME0 = "Humphrey McPhee";
private static String NAME1 = "Justice Caldwell";
private static String NAME2 = "Orrin Davies";
private static String NAME3 = "Emma Wilson";
@Before
public void before() {
ObservableList<Person> model = FXCollections.observableArrayList(
new Person(NAME0, 76),
new Person(NAME1, 30),
new Person(NAME2, 30),
new Person(NAME3, 8)
);
TableColumn<Person, String> column = new TableColumn<>("Col ");
column.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));
tableView = new TableView<>(model);
tableView.getColumns().add(column);
sl = new StageLoader(tableView);
Toolkit tk = Toolkit.getToolkit();
tk.firePulse();
column.setStyle("-fx-font: System;");
firstColumnHeader = VirtualFlowTestUtils.getTableColumnHeader(tableView, column);
}
@After
public void after() {
sl.dispose();
}
@Test
public void testColumnRightClickDoesAllowResizing() {
MouseEventFirer firer = new MouseEventFirer(firstColumnHeader);
assertFalse(TableColumnHeaderShim.getTableHeaderRowColumnDragLock(firstColumnHeader));
firer.fireMousePressed(MouseButton.SECONDARY);
assertTrue(TableColumnHeaderShim.getTableHeaderRowColumnDragLock(firstColumnHeader));
firer.fireMouseReleased(MouseButton.SECONDARY);
assertFalse(TableColumnHeaderShim.getTableHeaderRowColumnDragLock(firstColumnHeader));
}
@Test
public void testColumnRightClickDoesAllowResizingWhenConsumed() {
firstColumnHeader.addEventHandler(MouseEvent.MOUSE_RELEASED, Event::consume);
MouseEventFirer firer = new MouseEventFirer(firstColumnHeader);
assertFalse(TableColumnHeaderShim.getTableHeaderRowColumnDragLock(firstColumnHeader));
firer.fireMousePressed(MouseButton.SECONDARY);
assertTrue(TableColumnHeaderShim.getTableHeaderRowColumnDragLock(firstColumnHeader));
firer.fireMouseReleased(MouseButton.SECONDARY);
assertFalse(TableColumnHeaderShim.getTableHeaderRowColumnDragLock(firstColumnHeader));
}
@Test
public void test_resizeColumnToFitContent() {
TableColumn column = tableView.getColumns().get(0);
double width = column.getWidth();
TableColumnHeaderShim.resizeColumnToFitContent(firstColumnHeader, -1);
assertEquals("Width must be the same",
width, column.getWidth(), 0.001);
}
@Test
public void test_resizeColumnToFitContentIncrease() {
TableColumn column = tableView.getColumns().get(0);
double width = column.getWidth();
tableView.getItems().get(0).setFirstName("This is a big text inside that column");
assertEquals("Width must be the same",
width, column.getWidth(), 0.001);
TableColumnHeaderShim.resizeColumnToFitContent(firstColumnHeader, -1);
assertTrue("Column width must be greater",
width < column.getWidth());
tableView.getItems().get(0).setFirstName(NAME0);
TableColumnHeaderShim.resizeColumnToFitContent(firstColumnHeader, -1);
assertEquals("Width must be equal to initial value",
width, column.getWidth(), 0.001);
}
@Test
public void test_resizeColumnToFitContentDecrease() {
TableColumn column = tableView.getColumns().get(0);
double width = column.getWidth();
tableView.getItems().get(0).setFirstName("small");
tableView.getItems().get(1).setFirstName("small");
tableView.getItems().get(2).setFirstName("small");
tableView.getItems().get(3).setFirstName("small");
assertEquals("Width must be the same",
width, column.getWidth(), 0.001);
TableColumnHeaderShim.resizeColumnToFitContent(firstColumnHeader, -1);
assertTrue("Column width must be smaller",
width > column.getWidth());
tableView.getItems().get(0).setFirstName(NAME0);
tableView.getItems().get(1).setFirstName(NAME1);
tableView.getItems().get(2).setFirstName(NAME2);
tableView.getItems().get(3).setFirstName(NAME3);
TableColumnHeaderShim.resizeColumnToFitContent(firstColumnHeader, -1);
assertEquals("Width must be equal to initial value",
width, column.getWidth(), 0.001);
}
@Test
public void test_resizeColumnToFitContentHeader() {
TableColumn column = tableView.getColumns().get(0);
double width = column.getWidth();
column.setText("This is a big text inside that column");
assertEquals("Width must be the same",
width, column.getWidth(), 0.001);
TableColumnHeaderShim.resizeColumnToFitContent(firstColumnHeader, -1);
assertTrue("Column width must be greater",
width < column.getWidth());
column.setText("Col");
TableColumnHeaderShim.resizeColumnToFitContent(firstColumnHeader, 3);
assertEquals("Width must be equal to initial value",
width, column.getWidth(), 0.001);
}
@Test
public void test_resizeColumnToFitContentMaxRow() {
TableColumn column = tableView.getColumns().get(0);
double width = column.getWidth();
tableView.getItems().get(3).setFirstName("This is a big text inside that column");
TableColumnHeaderShim.resizeColumnToFitContent(firstColumnHeader, 3);
assertEquals("Width must be the same",
width, column.getWidth(), 0.001);
tableView.getItems().get(3).setFirstName(NAME3);
TableColumnHeaderShim.resizeColumnToFitContent(firstColumnHeader, 3);
assertEquals("Width must be equal to initial value",
width, column.getWidth(), 0.001);
}
@Test
public void test_resizeColumnToFitContentRowStyle() {
TableColumn column = tableView.getColumns().get(0);
tableView.setRowFactory(this::createSmallRow);
TableColumnHeaderShim.resizeColumnToFitContent(firstColumnHeader, -1);
double width = column.getWidth();
tableView.setRowFactory(this::createLargeRow);
TableColumnHeaderShim.resizeColumnToFitContent(firstColumnHeader, -1);
assertTrue("Column width must be greater", width < column.getWidth());
}
@Test
public void test_resizeColumnToFitContentCustomRowSkin() {
TableColumn column = tableView.getColumns().get(0);
tableView.setRowFactory(this::createCustomRow);
TableColumnHeaderShim.resizeColumnToFitContent(firstColumnHeader, -1);
double width = column.getWidth();
assertTrue(width > 0);
}
private TableRow<Person> createCustomRow(TableView<Person> tableView) {
TableRow<Person> row = new TableRow<>() {
protected Skin<?> createDefaultSkin() {
return new CustomSkin(this);
};
};
return row;
}
private static class CustomSkin implements Skin<TableRow<?>> {
private TableRow<?> row;
private Node node = new HBox();
CustomSkin(TableRow<?> row) {
this.row = row;
}
@Override
public TableRow<?> getSkinnable() {
return row;
}
@Override
public Node getNode() {
return node;
}
@Override
public void dispose() {
node = null;
}
}
private TableRow<Person> createSmallRow(TableView<Person> tableView) {
TableRow<Person> row = new TableRow<>();
row.setStyle("-fx-font: 24 Amble");
return row;
}
private TableRow<Person> createLargeRow(TableView<Person> param) {
TableRow<Person> row = new TableRow<>();
row.setStyle("-fx-font: 48 Amble");
return row;
}
}
