package test.javafx.scene.control.cell;
import javafx.collections.FXCollections;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TextFieldTreeTableCellTest {
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
@Test public void testStatic_forTreeTableColumn_noArgs_ensureCellFactoryIsNotNull() {
Callback<TreeTableColumn<Object,String>, TreeTableCell<Object,String>> cellFactory = TextFieldTreeTableCell.forTreeTableColumn();
assertNotNull(cellFactory);
}
@Test public void testStatic_forTreeTableColumn_noArgs_ensureCellFactoryCreatesCells() {
Callback<TreeTableColumn<Object,String>, TreeTableCell<Object,String>> cellFactory = TextFieldTreeTableCell.forTreeTableColumn();
TreeTableColumn<Object,String> tableColumn = new TreeTableColumn<>();
TextFieldTreeTableCell<Object,String> cell = (TextFieldTreeTableCell<Object,String>)cellFactory.call(tableColumn);
assertNotNull(cell);
}
@Test public void testStatic_forTreeTableColumn_callback_ensureCellHasNonNullStringConverter() {
Callback<TreeTableColumn<Object,String>, TreeTableCell<Object,String>> cellFactory = TextFieldTreeTableCell.forTreeTableColumn();
TreeTableColumn<Object,String> tableColumn = new TreeTableColumn<>();
TextFieldTreeTableCell<Object,String> cell = (TextFieldTreeTableCell<Object,String>)cellFactory.call(tableColumn);
assertNotNull(cell.getConverter());
}
@Test public void testStatic_forTreeTableColumn_converter_ensureCellFactoryIsNotNull() {
Callback<TreeTableColumn<Object,Object>, TreeTableCell<Object,Object>> cellFactory = TextFieldTreeTableCell.forTreeTableColumn(converter);
assertNotNull(cellFactory);
}
@Test public void testStatic_forTreeTableColumn_converter_ensureCellFactoryCreatesCells() {
Callback<TreeTableColumn<Object,Object>, TreeTableCell<Object,Object>> cellFactory = TextFieldTreeTableCell.forTreeTableColumn(converter);
TreeTableColumn<Object,Object> tableColumn = new TreeTableColumn<>();
TextFieldTreeTableCell<Object,Object> cell = (TextFieldTreeTableCell<Object,Object>)cellFactory.call(tableColumn);
assertNotNull(cell);
}
@Test public void testStatic_forTreeTableColumn_converter_ensureCellHasSetStringConverter() {
Callback<TreeTableColumn<Object,Object>, TreeTableCell<Object,Object>> cellFactory = TextFieldTreeTableCell.forTreeTableColumn(converter);
TreeTableColumn<Object,Object> tableColumn = new TreeTableColumn<>();
TextFieldTreeTableCell<Object,Object> cell = (TextFieldTreeTableCell<Object,Object>)cellFactory.call(tableColumn);
assertNotNull(cell.getConverter());
assertEquals(converter, cell.getConverter());
}
@Test public void testConstructor_noArgs_defaultStringConverterIsNull() {
TextFieldTreeTableCell<?,Object> cell = new TextFieldTreeTableCell<>();
assertNull(cell.getConverter());
}
@Test public void testConstructor_noArgs_defaultStyleClass() {
TextFieldTreeTableCell<?,Object> cell = new TextFieldTreeTableCell<>();
assertTrue(cell.getStyleClass().contains("text-field-tree-table-cell"));
}
@Test public void testConstructor_noArgs_defaultGraphicIsNull() {
TextFieldTreeTableCell<?,Object> cell = new TextFieldTreeTableCell<>();
assertNull(cell.getGraphic());
}
@Test public void testConstructor_converter_defaultStringConverterIsNotNull() {
TextFieldTreeTableCell<?,Object> cell = new TextFieldTreeTableCell<>(converter);
assertNotNull(cell.getConverter());
}
@Test public void testConstructor_converter_defaultStyleClass() {
TextFieldTreeTableCell<?,Object> cell = new TextFieldTreeTableCell<>(converter);
assertTrue(cell.getStyleClass().contains("text-field-tree-table-cell"));
}
@Test public void testConstructor_converter_defaultGraphicIsACheckBox() {
TextFieldTreeTableCell<?,Object> cell = new TextFieldTreeTableCell<>(converter);
assertNull(cell.getGraphic());
}
@Test public void test_updateItem_isEmpty_graphicIsNull() {
TextFieldTreeTableCell<?,Object> cell = new TextFieldTreeTableCell<>();
cell.updateItem("TEST", true);
assertNull(cell.getGraphic());
}
@Test public void test_updateItem_isEmpty_textIsNull() {
TextFieldTreeTableCell<?,Object> cell = new TextFieldTreeTableCell<>();
cell.updateItem("TEST", true);
assertNull(cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull() {
TextFieldTreeTableCell<?,Object> cell = new TextFieldTreeTableCell<>();
cell.updateItem("TEST", false);
assertNotNull(cell.getText());
assertEquals("TEST", cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull_nullConverter() {
TextFieldTreeTableCell<?,Object> cell = new TextFieldTreeTableCell<>();
cell.setConverter(null);
cell.updateItem("TEST", false);
assertNotNull(cell.getText());
assertEquals("TEST", cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull_nonNullConverter() {
TextFieldTreeTableCell<?,Object> cell = new TextFieldTreeTableCell<>();
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
TextFieldTreeTableCell<Object,Object> cell = new TextFieldTreeTableCell<>();
cell.setEditable(false);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_tableViewEditableIsFalse_isEmpty() {
TreeTableView tableView = new TreeTableView();
tableView.setEditable(false);
TextFieldTreeTableCell<Object,Object> cell = new TextFieldTreeTableCell<>();
cell.updateTreeTableView(tableView);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_tableViewEditableIsTrue_isEmpty() {
TreeTableColumn tc = new TreeTableColumn();
TreeTableView tableView = new TreeTableView();
tableView.setEditable(true);
TextFieldTreeTableCell<Object,Object> cell = new TextFieldTreeTableCell<>();
cell.updateTreeTableView(tableView);
cell.updateTableColumn(tc);
tableView.edit(0, tc);
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_cellEditableIsFalse_isNotEmpty() {
TextFieldTreeTableCell<Object,Object> cell = new TextFieldTreeTableCell<>();
cell.updateItem("TEST", false);
cell.setEditable(false);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_tableColumnEditableIsFalse_isNotEmpty() {
TreeTableColumn<Object,Object> tableColumn = new TreeTableColumn<>();
tableColumn.setEditable(false);
TreeTableView tableView = new TreeTableView(new TreeItem("TEST"));
tableView.getColumns().add(tableColumn);
TextFieldTreeTableCell<Object,Object> cell = new TextFieldTreeTableCell<>();
cell.updateTableColumn(tableColumn);
cell.updateTreeTableView(tableView);
cell.updateItem("TEST", false);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_tableViewEditableIsTrue_isNotEmpty() {
TreeTableColumn tc = new TreeTableColumn();
TreeTableView tableView = new TreeTableView(new TreeItem("TEST"));
tableView.getColumns().add(tc);
tableView.setEditable(true);
TextFieldTreeTableCell<Object,Object> cell = new TextFieldTreeTableCell<>();
cell.updateTreeTableView(tableView);
cell.updateIndex(0);
cell.updateTableColumn(tc);
tableView.edit(0, tc);
assertTrue(cell.isEditing());
assertNotNull(cell.getGraphic());
}
@Test public void test_startEdit_tableColumnEditableIsTrue_cellEditableIsTrue_isNotEmpty() {
TreeTableColumn tc = new TreeTableColumn();
TreeTableView tableView = new TreeTableView(new TreeItem("TEST"));
tableView.getColumns().add(tc);
tableView.setEditable(true);
TextFieldTreeTableCell<Object,Object> cell = new TextFieldTreeTableCell<>();
cell.updateTreeTableView(tableView);
cell.updateIndex(0);
cell.updateTableColumn(tc);
cell.setEditable(true);
tableView.edit(0, tc);
assertTrue(cell.isEditing());
assertNotNull(cell.getGraphic());
}
@Test public void test_cancelEdit_usingCellCancelEdit() {
TreeTableColumn tc = new TreeTableColumn();
TreeTableView tableView = new TreeTableView(new TreeItem("TEST"));
tableView.getColumns().add(tc);
tableView.setEditable(true);
TextFieldTreeTableCell<Object,Object> cell = new TextFieldTreeTableCell<>();
cell.updateTreeTableView(tableView);
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
TreeTableColumn tc = new TreeTableColumn();
TreeTableView tableView = new TreeTableView(new TreeItem("TEST"));
tableView.getColumns().add(tc);
tableView.setEditable(true);
TextFieldTreeTableCell<Object,Object> cell = new TextFieldTreeTableCell<>();
cell.updateTreeTableView(tableView);
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
}
