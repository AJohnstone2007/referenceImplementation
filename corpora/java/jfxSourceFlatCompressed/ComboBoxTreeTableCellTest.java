package test.javafx.scene.control.cell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class ComboBoxTreeTableCellTest {
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
Callback<TreeTableColumn<String,String>, TreeTableCell<String,String>> cellFactory = ComboBoxTreeTableCell.forTreeTableColumn();
assertNotNull(cellFactory);
}
@Test public void testStatic_forTreeTableColumn_noArgs_ensureCellFactoryCreatesCells() {
Callback<TreeTableColumn<String,String>, TreeTableCell<String,String>> cellFactory = ComboBoxTreeTableCell.forTreeTableColumn();
TreeTableColumn<String,String> tableColumn = new TreeTableColumn<>();
ComboBoxTreeTableCell<String,String> cell = (ComboBoxTreeTableCell<String,String>)cellFactory.call(tableColumn);
assertNotNull(cell);
}
@Test public void testStatic_forTreeTableColumn_noArgs_ensureCellHasNonNullStringConverter() {
Callback<TreeTableColumn<String,String>, TreeTableCell<String,String>> cellFactory = ComboBoxTreeTableCell.forTreeTableColumn();
TreeTableColumn<String,String> tableColumn = new TreeTableColumn<>();
ComboBoxTreeTableCell<String,String> cell = (ComboBoxTreeTableCell<String,String>)cellFactory.call(tableColumn);
assertNotNull(cell.getConverter());
}
@Test public void testStatic_forTreeTableColumn_items_ensureSuccessWhenItemsIsNull() {
ObservableList<String> items = null;
Callback<TreeTableColumn<String,String>, TreeTableCell<String,String>> cellFactory = ComboBoxTreeTableCell.forTreeTableColumn(items);
assertNotNull(cellFactory);
}
@Test public void testStatic_forTreeTableColumn_items_ensureCellFactoryIsNotNull() {
ObservableList<String> items = FXCollections.emptyObservableList();
Callback<TreeTableColumn<String,String>, TreeTableCell<String,String>> cellFactory = ComboBoxTreeTableCell.forTreeTableColumn(items);
assertNotNull(cellFactory);
}
@Test public void testStatic_forTreeTableColumn_items_ensureCellFactoryCreatesCells() {
ObservableList<String> items = FXCollections.emptyObservableList();
Callback<TreeTableColumn<String,String>, TreeTableCell<String,String>> cellFactory = ComboBoxTreeTableCell.forTreeTableColumn(items);
TreeTableColumn<String,String> tableColumn = new TreeTableColumn<>();
ComboBoxTreeTableCell<String,String> cell = (ComboBoxTreeTableCell<String,String>)cellFactory.call(tableColumn);
assertNotNull(cell);
}
@Test public void testStatic_forTreeTableColumn_items_ensureCellHasNonNullStringConverter() {
ObservableList<String> items = FXCollections.emptyObservableList();
Callback<TreeTableColumn<String,String>, TreeTableCell<String,String>> cellFactory = ComboBoxTreeTableCell.forTreeTableColumn(items);
TreeTableColumn<String,String> tableColumn = new TreeTableColumn<>();
ComboBoxTreeTableCell<String,String> cell = (ComboBoxTreeTableCell<String,String>)cellFactory.call(tableColumn);
assertNotNull(cell.getConverter());
}
@Test public void testConstructor_noArgs_defaultStringConverterIsNotNull() {
ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>();
assertNotNull(cell.getConverter());
}
@Test public void testConstructor_noArgs_defaultStyleClass() {
ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>();
assertTrue(cell.getStyleClass().contains("combo-box-tree-table-cell"));
}
@Test public void testConstructor_noArgs_defaultGraphicIsNull() {
ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>();
assertNull(cell.getGraphic());
}
@Test public void testConstructor_converter_defaultStringConverterIsNotNull() {
ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>(converter);
assertNotNull(cell.getConverter());
}
@Test public void testConstructor_converter_defaultStyleClass() {
ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>(converter);
assertTrue(cell.getStyleClass().contains("combo-box-tree-table-cell"));
}
@Test public void testConstructor_converter_defaultGraphicIsACheckBox() {
ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>(converter);
assertNull(cell.getGraphic());
}
@Test public void testConstructor_varargs_defaultStringConverterIsNotNull() {
Object[] items = new Object[] { "Item 1", "Item 2", "Item 3" };
ComboBoxTableCell<Object, Object> cell = new ComboBoxTableCell<>(items);
assertNotNull(cell.getConverter());
}
@Test public void testConstructor_varargs_defaultStyleClass() {
Object[] items = new Object[] { "Item 1", "Item 2", "Item 3" };
ComboBoxTableCell<Object, Object> cell = new ComboBoxTableCell<>(items);
assertTrue(cell.getStyleClass().contains("combo-box-table-cell"));
}
@Test public void testConstructor_varargs_defaultGraphicIsACheckBox() {
Object[] items = new Object[] { "Item 1", "Item 2", "Item 3" };
ComboBoxTableCell<Object, Object> cell = new ComboBoxTableCell<>(items);
assertNull(cell.getGraphic());
}
@Test public void testConstructor_varargs_itemsListIsNotNullOrEmpty() {
Object[] items = new Object[] { "Item 1", "Item 2", "Item 3" };
ComboBoxTableCell<Object, Object> cell = new ComboBoxTableCell<>(items);
assertNotNull(cell.getItems());
assertEquals(3, cell.getItems().size());
}
@Test public void testComboBoxEditable_falseByDefault() {
ComboBoxTableCell<Object, Object> cell = new ComboBoxTableCell<>();
assertFalse(cell.isComboBoxEditable());
}
@Test public void testComboBoxEditable_setter() {
ComboBoxTableCell<Object, Object> cell = new ComboBoxTableCell<>();
cell.setComboBoxEditable(true);
assertTrue(cell.isComboBoxEditable());
cell.setComboBoxEditable(false);
assertFalse(cell.isComboBoxEditable());
}
@Test public void test_updateItem_isEmpty_graphicIsNull() {
ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>();
cell.updateItem("TEST", true);
assertNull(cell.getGraphic());
}
@Test public void test_updateItem_isEmpty_textIsNull() {
ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>();
cell.updateItem("TEST", true);
assertNull(cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull() {
ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>();
cell.updateItem("TEST", false);
assertNotNull(cell.getText());
assertEquals("TEST", cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull_nullConverter() {
ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>();
cell.setConverter(null);
cell.updateItem("TEST", false);
assertNotNull(cell.getText());
assertEquals("TEST", cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull_nonNullConverter() {
ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>();
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
ComboBoxTreeTableCell<Object,Object> cell = new ComboBoxTreeTableCell<>();
cell.setEditable(false);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_tableViewEditableIsFalse_isEmpty() {
TreeTableView tableView = new TreeTableView();
tableView.setEditable(false);
ComboBoxTreeTableCell<Object,Object> cell = new ComboBoxTreeTableCell<>();
cell.updateTreeTableView(tableView);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_tableViewEditableIsTrue_isEmpty() {
TreeTableColumn tc = new TreeTableColumn();
TreeTableView tableView = new TreeTableView();
tableView.setEditable(true);
ComboBoxTreeTableCell<Object,Object> cell = new ComboBoxTreeTableCell<>();
cell.updateTreeTableView(tableView);
cell.updateTableColumn(tc);
tableView.edit(0, tc);
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_tableViewEditableIsTrue_cellEditableIsTrue_isEmpty() {
TreeTableColumn tc = new TreeTableColumn();
TreeTableView tableView = new TreeTableView();
tableView.setEditable(true);
ComboBoxTreeTableCell<Object,Object> cell = new ComboBoxTreeTableCell<>();
cell.setEditable(true);
cell.updateTreeTableView(tableView);
cell.updateTableColumn(tc);
tableView.edit(0, tc);
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_cellEditableIsFalse_isNotEmpty() {
ComboBoxTreeTableCell<Object, Object> cell = new ComboBoxTreeTableCell<>();
cell.updateItem("TEST", false);
cell.setEditable(false);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_tableViewEditableIsFalse_isNotEmpty() {
TreeTableView tableView = new TreeTableView();
tableView.setEditable(false);
ComboBoxTreeTableCell<Object,Object> cell = new ComboBoxTreeTableCell<>();
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
ComboBoxTreeTableCell<Object,Object> cell = new ComboBoxTreeTableCell<>();
cell.updateTreeTableView(tableView);
cell.updateIndex(0);
cell.updateTableColumn(tc);
tableView.edit(0, tc);
assertTrue(cell.isEditing());
assertNotNull(cell.getGraphic());
}
@Test public void test_startEdit_tableViewEditableIsTrue_cellEditableIsTrue_isNotEmpty() {
TreeTableColumn tc = new TreeTableColumn();
TreeTableView tableView = new TreeTableView(new TreeItem("TEST"));
tableView.getColumns().add(tc);
tableView.setEditable(true);
ComboBoxTreeTableCell<Object,Object> cell = new ComboBoxTreeTableCell<>();
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
ComboBoxTreeTableCell<Object,Object> cell = new ComboBoxTreeTableCell<>();
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
ComboBoxTreeTableCell<Object,Object> cell = new ComboBoxTreeTableCell<>();
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
@Test public void test_rt_29320() {
TreeTableColumn tc = new TreeTableColumn();
TreeTableView tableView = new TreeTableView(new TreeItem("TEST"));
tableView.getColumns().add(tc);
tableView.setEditable(true);
ComboBoxTreeTableCell<Object,Object> cell = new ComboBoxTreeTableCell<>();
cell.updateTreeTableView(tableView);
cell.updateIndex(0);
cell.updateTableColumn(tc);
cell.setEditable(true);
tableView.edit(0, tc);
ComboBox cb = (ComboBox) cell.getGraphic();
assertNotNull(cell.getConverter());
assertNotNull(cb.getConverter());
assertEquals(cell.getConverter(), cb.getConverter());
cell.setConverter(null);
assertNull(cb.getConverter());
StringConverter<Object> customConverter = new StringConverter<Object>() {
@Override public String toString(Object object) { return null; }
@Override public Object fromString(String string) { return null; }
};
cell.setConverter(customConverter);
assertEquals(customConverter, cb.getConverter());
}
}
