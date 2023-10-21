package test.javafx.scene.control.cell;
import test.com.sun.javafx.scene.control.infrastructure.MouseEventFirer;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TextFieldTableCellTest {
private StringConverter<Object> converter;
@Before public void setup() {
converter = new StringConverter<Object>() {
@Override public String toString(Object object) {
return null;
}
@Override public Object fromString(String string) {
return null;
}
};
}
@Test public void testStatic_forTableColumn_noArgs_ensureCellFactoryIsNotNull() {
Callback<TableColumn<Object,String>, TableCell<Object,String>> cellFactory = TextFieldTableCell.forTableColumn();
assertNotNull(cellFactory);
}
@Test public void testStatic_forTableColumn_noArgs_ensureCellFactoryCreatesCells() {
Callback<TableColumn<Object,String>, TableCell<Object,String>> cellFactory = TextFieldTableCell.forTableColumn();
TableColumn<Object,String> tableColumn = new TableColumn<>();
TextFieldTableCell<Object,String> cell = (TextFieldTableCell<Object,String>)cellFactory.call(tableColumn);
assertNotNull(cell);
}
@Test public void testStatic_forTableColumn_callback_ensureCellHasNonNullStringConverter() {
Callback<TableColumn<Object,String>, TableCell<Object,String>> cellFactory = TextFieldTableCell.forTableColumn();
TableColumn<Object,String> tableColumn = new TableColumn<>();
TextFieldTableCell<Object,String> cell = (TextFieldTableCell<Object,String>)cellFactory.call(tableColumn);
assertNotNull(cell.getConverter());
}
@Test public void testStatic_forTableColumn_converter_ensureCellFactoryIsNotNull() {
Callback<TableColumn<Object,Object>, TableCell<Object,Object>> cellFactory = TextFieldTableCell.forTableColumn(converter);
assertNotNull(cellFactory);
}
@Test public void testStatic_forTableColumn_converter_ensureCellFactoryCreatesCells() {
Callback<TableColumn<Object,Object>, TableCell<Object,Object>> cellFactory = TextFieldTableCell.forTableColumn(converter);
TableColumn<Object,Object> tableColumn = new TableColumn<>();
TextFieldTableCell<Object,Object> cell = (TextFieldTableCell<Object,Object>)cellFactory.call(tableColumn);
assertNotNull(cell);
}
@Test public void testStatic_forTableColumn_converter_ensureCellHasSetStringConverter() {
Callback<TableColumn<Object,Object>, TableCell<Object,Object>> cellFactory = TextFieldTableCell.forTableColumn(converter);
TableColumn<Object,Object> tableColumn = new TableColumn<>();
TextFieldTableCell<Object,Object> cell = (TextFieldTableCell<Object,Object>)cellFactory.call(tableColumn);
assertNotNull(cell.getConverter());
assertEquals(converter, cell.getConverter());
}
@Test public void testConstructor_noArgs_defaultStringConverterIsNull() {
TextFieldTableCell<?,Object> cell = new TextFieldTableCell<>();
assertNull(cell.getConverter());
}
@Test public void testConstructor_noArgs_defaultStyleClass() {
TextFieldTableCell<?,Object> cell = new TextFieldTableCell<>();
assertTrue(cell.getStyleClass().contains("text-field-table-cell"));
}
@Test public void testConstructor_noArgs_defaultGraphicIsNull() {
TextFieldTableCell<?,Object> cell = new TextFieldTableCell<>();
assertNull(cell.getGraphic());
}
@Test public void testConstructor_converter_defaultStringConverterIsNotNull() {
TextFieldTableCell<?,Object> cell = new TextFieldTableCell<>(converter);
assertNotNull(cell.getConverter());
}
@Test public void testConstructor_converter_defaultStyleClass() {
TextFieldTableCell<?,Object> cell = new TextFieldTableCell<>(converter);
assertTrue(cell.getStyleClass().contains("text-field-table-cell"));
}
@Test public void testConstructor_converter_defaultGraphicIsACheckBox() {
TextFieldTableCell<?,Object> cell = new TextFieldTableCell<>(converter);
assertNull(cell.getGraphic());
}
@Test public void test_updateItem_isEmpty_graphicIsNull() {
TextFieldTableCell<?,Object> cell = new TextFieldTableCell<>();
cell.updateItem("TEST", true);
assertNull(cell.getGraphic());
}
@Test public void test_updateItem_isEmpty_textIsNull() {
TextFieldTableCell<?,Object> cell = new TextFieldTableCell<>();
cell.updateItem("TEST", true);
assertNull(cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull() {
TextFieldTableCell<?,Object> cell = new TextFieldTableCell<>();
cell.updateItem("TEST", false);
assertNotNull(cell.getText());
assertEquals("TEST", cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull_nullConverter() {
TextFieldTableCell<?,Object> cell = new TextFieldTableCell<>();
cell.setConverter(null);
cell.updateItem("TEST", false);
assertNotNull(cell.getText());
assertEquals("TEST", cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull_nonNullConverter() {
TextFieldTableCell<?,Object> cell = new TextFieldTableCell<>();
cell.setConverter(
new StringConverter<Object>() {
@Override public Object fromString(String string) {
return null;
}
@Override public String toString(Object object) {
return "CONVERTED";
}
});
cell.updateItem("TEST", false);
assertNotNull(cell.getText());
assertEquals("CONVERTED", cell.getText());
}
@Test public void test_startEdit_cellEditableIsFalse_isEmpty() {
TextFieldTableCell<Object,Object> cell = new TextFieldTableCell<>();
cell.setEditable(false);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_tableViewEditableIsFalse_isEmpty() {
TableView tableView = new TableView();
tableView.setEditable(false);
TextFieldTableCell<Object,Object> cell = new TextFieldTableCell<>();
cell.updateTableView(tableView);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_tableViewEditableIsTrue_isEmpty() {
TableColumn tc = new TableColumn();
TableView tableView = new TableView();
tableView.setEditable(true);
TextFieldTableCell<Object,Object> cell = new TextFieldTableCell<>();
cell.updateTableView(tableView);
cell.updateTableColumn(tc);
tableView.edit(0, tc);
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_cellEditableIsFalse_isNotEmpty() {
TextFieldTableCell<Object,Object> cell = new TextFieldTableCell<>();
cell.updateItem("TEST", false);
cell.setEditable(false);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_tableColumnEditableIsFalse_isNotEmpty() {
TableColumn<Object,Object> tableColumn = new TableColumn<>();
tableColumn.setEditable(false);
TableView tableView = new TableView(FXCollections.observableArrayList("TEST"));
tableView.getColumns().add(tableColumn);
TextFieldTableCell<Object,Object> cell = new TextFieldTableCell<>();
cell.updateTableColumn(tableColumn);
cell.updateTableView(tableView);
cell.updateItem("TEST", false);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_tableViewEditableIsTrue_isNotEmpty() {
TableColumn tc = new TableColumn();
TableView tableView = new TableView(FXCollections.observableArrayList("TEST"));
tableView.getColumns().add(tc);
tableView.setEditable(true);
TextFieldTableCell<Object,Object> cell = new TextFieldTableCell<>();
cell.updateTableView(tableView);
cell.updateIndex(0);
cell.updateTableColumn(tc);
tableView.edit(0, tc);
assertTrue(cell.isEditing());
assertNotNull(cell.getGraphic());
}
@Test public void test_startEdit_tableColumnEditableIsTrue_cellEditableIsTrue_isNotEmpty() {
TableColumn tc = new TableColumn();
TableView tableView = new TableView(FXCollections.observableArrayList("TEST"));
tableView.getColumns().add(tc);
tableView.setEditable(true);
TextFieldTableCell<Object,Object> cell = new TextFieldTableCell<>();
cell.updateTableView(tableView);
cell.updateIndex(0);
cell.updateTableColumn(tc);
cell.setEditable(true);
tableView.edit(0, tc);
assertTrue(cell.isEditing());
assertNotNull(cell.getGraphic());
}
@Test public void test_cancelEdit_usingCellCancelEdit() {
TableColumn tc = new TableColumn();
TableView tableView = new TableView(FXCollections.observableArrayList("TEST"));
tableView.getColumns().add(tc);
tableView.setEditable(true);
TextFieldTableCell<Object,Object> cell = new TextFieldTableCell<>();
cell.updateTableView(tableView);
cell.updateIndex(0);
cell.updateTableColumn(tc);
cell.setEditable(true);
tableView.edit(0, tc);
assertTrue(cell.isEditing());
assertNotNull(cell.getGraphic());
cell.cancelEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_cancelEdit_usingTableCancelEdit() {
TableColumn tc = new TableColumn();
TableView tableView = new TableView(FXCollections.observableArrayList("TEST"));
tableView.getColumns().add(tc);
tableView.setEditable(true);
TextFieldTableCell<Object,Object> cell = new TextFieldTableCell<>();
cell.updateTableView(tableView);
cell.updateIndex(0);
cell.updateTableColumn(tc);
cell.setEditable(true);
tableView.edit(0, tc);
assertTrue(cell.isEditing());
assertNotNull(cell.getGraphic());
tableView.edit(-1, null);
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_rt_32869() {
TableColumn tc = new TableColumn();
tc.setCellValueFactory(param -> new ReadOnlyStringWrapper("Dummy Text"));
TableView tableView = new TableView(FXCollections.observableArrayList("TEST"));
tableView.getColumns().add(tc);
tableView.setEditable(true);
TextFieldTableCell<Object,Object> cell = new TextFieldTableCell<>();
cell.updateTableView(tableView);
cell.updateIndex(0);
cell.updateTableColumn(tc);
cell.setEditable(true);
tableView.edit(0, tc);
assertTrue(cell.isEditing());
assertNotNull(cell.getGraphic());
TextField textField = (TextField) cell.getGraphic();
MouseEventFirer mouse = new MouseEventFirer(textField);
textField.requestFocus();
textField.selectAll();
assertEquals("Dummy Text", textField.getSelectedText());
assertEquals("Dummy Text", textField.getSelectedText());
mouse.fireMousePressed(MouseButton.SECONDARY);
assertEquals("Dummy Text", textField.getSelectedText());
mouse.fireMouseReleased(MouseButton.SECONDARY);
assertEquals("Dummy Text", textField.getSelectedText());
mouse.dispose();
}
}
