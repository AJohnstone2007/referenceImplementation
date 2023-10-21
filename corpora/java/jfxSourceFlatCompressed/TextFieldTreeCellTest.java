package test.javafx.scene.control.cell;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TextFieldTreeCellTest {
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
@Test public void testStatic_forTreeView_noArgs_ensureCellFactoryIsNotNull() {
Callback<TreeView<String>, TreeCell<String>> cellFactory = TextFieldTreeCell.forTreeView();
assertNotNull(cellFactory);
}
@Test public void testStatic_forTreeView_noArgs_ensureCellFactoryCreatesCells() {
Callback<TreeView<String>, TreeCell<String>> cellFactory = TextFieldTreeCell.forTreeView();
TreeView<String> treeView = new TreeView<>();
TextFieldTreeCell<String> cell = (TextFieldTreeCell<String>)cellFactory.call(treeView);
assertNotNull(cell);
}
@Test public void testStatic_forTreeView_callback_ensureCellHasNonNullStringConverter() {
Callback<TreeView<String>, TreeCell<String>> cellFactory = TextFieldTreeCell.forTreeView();
TreeView<String> treeView = new TreeView<>();
TextFieldTreeCell<String> cell = (TextFieldTreeCell<String>)cellFactory.call(treeView);
assertNotNull(cell.getConverter());
}
@Test public void testStatic_forTreeView_converter_ensureCellFactoryIsNotNull() {
Callback<TreeView<Object>, TreeCell<Object>> cellFactory = TextFieldTreeCell.forTreeView(converter);
assertNotNull(cellFactory);
}
@Test public void testStatic_forTreeView_converter_ensureCellFactoryCreatesCells() {
Callback<TreeView<Object>, TreeCell<Object>> cellFactory = TextFieldTreeCell.forTreeView(converter);
TreeView<Object> treeView = new TreeView<>();
TextFieldTreeCell<Object> cell = (TextFieldTreeCell<Object>)cellFactory.call(treeView);
assertNotNull(cell);
}
@Test public void testStatic_forTreeView_converter_ensureCellHasSetStringConverter() {
Callback<TreeView<Object>, TreeCell<Object>> cellFactory = TextFieldTreeCell.forTreeView(converter);
TreeView<Object> treeView = new TreeView<>();
TextFieldTreeCell<Object> cell = (TextFieldTreeCell<Object>)cellFactory.call(treeView);
assertNotNull(cell.getConverter());
assertEquals(converter, cell.getConverter());
}
@Test public void testConstructor_noArgs_defaultStringConverterIsNull() {
TextFieldTreeCell<Object> cell = new TextFieldTreeCell<>();
assertNull(cell.getConverter());
}
@Test public void testConstructor_noArgs_defaultStyleClass() {
TextFieldTreeCell<Object> cell = new TextFieldTreeCell<>();
assertTrue(cell.getStyleClass().contains("text-field-tree-cell"));
}
@Test public void testConstructor_noArgs_defaultGraphicIsNull() {
TextFieldTreeCell<Object> cell = new TextFieldTreeCell<>();
assertNull(cell.getGraphic());
}
@Test public void testConstructor_converter_defaultStringConverterIsNotNull() {
TextFieldTreeCell<Object> cell = new TextFieldTreeCell<>(converter);
assertNotNull(cell.getConverter());
}
@Test public void testConstructor_converter_defaultStyleClass() {
TextFieldTreeCell<Object> cell = new TextFieldTreeCell<>(converter);
assertTrue(cell.getStyleClass().contains("text-field-tree-cell"));
}
@Test public void testConstructor_converter_defaultGraphicIsACheckBox() {
TextFieldTreeCell<Object> cell = new TextFieldTreeCell<>(converter);
assertNull(cell.getGraphic());
}
@Test public void test_updateItem_isEmpty_graphicIsNull() {
TextFieldTreeCell<Object> cell = new TextFieldTreeCell<>();
cell.updateItem("TEST", true);
assertNull(cell.getGraphic());
}
@Test public void test_updateItem_isEmpty_textIsNull() {
TextFieldTreeCell<Object> cell = new TextFieldTreeCell<>();
cell.updateItem("TEST", true);
assertNull(cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull() {
TextFieldTreeCell<Object> cell = new TextFieldTreeCell<>();
cell.updateItem("TEST", false);
assertNotNull(cell.getText());
assertEquals("TEST", cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull_nullConverter() {
TextFieldTreeCell<Object> cell = new TextFieldTreeCell<>();
cell.setConverter(null);
cell.updateItem("TEST", false);
assertNotNull(cell.getText());
assertEquals("TEST", cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull_nonNullConverter() {
TextFieldTreeCell<Object> cell = new TextFieldTreeCell<>();
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
TextFieldTreeCell<Object> cell = new TextFieldTreeCell<>();
cell.setEditable(false);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_treeViewEditableIsFalse_isEmpty() {
TreeView treeView = new TreeView();
treeView.setEditable(false);
TextFieldTreeCell<Object> cell = new TextFieldTreeCell<>();
cell.updateTreeView(treeView);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_treeViewEditableIsTrue_isEmpty() {
TreeView treeView = new TreeView();
treeView.setEditable(true);
TextFieldTreeCell<Object> cell = new TextFieldTreeCell<>();
cell.updateTreeView(treeView);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_treeViewEditableIsTrue_cellEditableIsTrue_isEmpty() {
TreeView treeView = new TreeView();
treeView.setEditable(true);
TextFieldTreeCell<Object> cell = new TextFieldTreeCell<>();
cell.setEditable(true);
cell.updateTreeView(treeView);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_cellEditableIsFalse_isNotEmpty() {
TextFieldTreeCell<Object> cell = new TextFieldTreeCell<>();
cell.updateItem("TEST", false);
cell.setEditable(false);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_treeViewEditableIsFalse_isNotEmpty() {
TreeView treeView = new TreeView();
treeView.setEditable(false);
TextFieldTreeCell<Object> cell = new TextFieldTreeCell<>();
cell.updateTreeView(treeView);
cell.updateItem("TEST", false);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_treeViewEditableIsTrue_isNotEmpty() {
TreeItem root = new TreeItem("Root");
TreeView treeView = new TreeView(root);
treeView.setEditable(true);
TextFieldTreeCell<Object> cell = new TextFieldTreeCell<>();
cell.updateTreeView(treeView);
cell.updateIndex(0);
cell.startEdit();
assertTrue(cell.isEditing());
assertNotNull(cell.getGraphic());
}
@Test public void test_startEdit_treeViewEditableIsTrue_cellEditableIsTrue_isNotEmpty() {
TreeItem root = new TreeItem("Root");
TreeView treeView = new TreeView(root);
treeView.setEditable(true);
TextFieldTreeCell<Object> cell = new TextFieldTreeCell<>();
cell.setEditable(true);
cell.updateTreeView(treeView);
cell.updateIndex(0);
cell.startEdit();
assertTrue(cell.isEditing());
assertNotNull(cell.getGraphic());
}
@Test public void test_cancelEdit() {
TreeItem root = new TreeItem("Root");
TreeView treeView = new TreeView(root);
treeView.setEditable(true);
TextFieldTreeCell<Object> cell = new TextFieldTreeCell<>();
cell.updateTreeView(treeView);
cell.updateIndex(0);
cell.startEdit();
assertTrue(cell.isEditing());
assertNotNull(cell.getGraphic());
cell.cancelEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
}
