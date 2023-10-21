package test.javafx.scene.control.cell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.ComboBoxTreeCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class ComboBoxTreeCellTest {
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
Callback<TreeView<String>, TreeCell<String>> cellFactory = ComboBoxTreeCell.forTreeView();
assertNotNull(cellFactory);
}
@Test public void testStatic_forTreeView_noArgs_ensureCellFactoryCreatesCells() {
Callback<TreeView<String>, TreeCell<String>> cellFactory = ComboBoxTreeCell.forTreeView();
TreeView<String> treeView = new TreeView<>();
ComboBoxTreeCell<String> cell = (ComboBoxTreeCell<String>)cellFactory.call(treeView);
assertNotNull(cell);
}
@Test public void testStatic_forTreeView_noArgs_ensureCellHasNonNullStringConverter() {
Callback<TreeView<String>, TreeCell<String>> cellFactory = ComboBoxTreeCell.forTreeView();
TreeView<String> treeView = new TreeView<>();
ComboBoxTreeCell<String> cell = (ComboBoxTreeCell<String>)cellFactory.call(treeView);
assertNotNull(cell.getConverter());
}
@Test public void testStatic_forTreeView_items_ensureSuccessWhenItemsIsNull() {
ObservableList<String> items = null;
Callback<TreeView<String>, TreeCell<String>> cellFactory = ComboBoxTreeCell.forTreeView(items);
assertNotNull(cellFactory);
}
@Test public void testStatic_forTreeView_items_ensureCellFactoryIsNotNull() {
ObservableList<String> items = FXCollections.emptyObservableList();
Callback<TreeView<String>, TreeCell<String>> cellFactory = ComboBoxTreeCell.forTreeView(items);
assertNotNull(cellFactory);
}
@Test public void testStatic_forTreeView_items_ensureCellFactoryCreatesCells() {
ObservableList<String> items = FXCollections.emptyObservableList();
Callback<TreeView<String>, TreeCell<String>> cellFactory = ComboBoxTreeCell.forTreeView(items);
TreeView<String> treeView = new TreeView<>();
ComboBoxTreeCell<String> cell = (ComboBoxTreeCell<String>)cellFactory.call(treeView);
assertNotNull(cell);
}
@Test public void testStatic_forTreeView_items_ensureCellHasNonNullStringConverter() {
ObservableList<String> items = FXCollections.emptyObservableList();
Callback<TreeView<String>, TreeCell<String>> cellFactory = ComboBoxTreeCell.forTreeView(items);
TreeView<String> treeView = new TreeView<>();
ComboBoxTreeCell<String> cell = (ComboBoxTreeCell<String>)cellFactory.call(treeView);
assertNotNull(cell.getConverter());
}
@Test public void testConstructor_noArgs_defaultStringConverterIsNotNull() {
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>();
assertNotNull(cell.getConverter());
}
@Test public void testConstructor_noArgs_defaultStyleClass() {
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>();
assertTrue(cell.getStyleClass().contains("combo-box-tree-cell"));
}
@Test public void testConstructor_noArgs_defaultGraphicIsNull() {
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>();
assertNull(cell.getGraphic());
}
@Test public void testConstructor_converter_defaultStringConverterIsNotNull() {
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>(converter);
assertNotNull(cell.getConverter());
}
@Test public void testConstructor_converter_defaultStyleClass() {
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>(converter);
assertTrue(cell.getStyleClass().contains("combo-box-tree-cell"));
}
@Test public void testConstructor_converter_defaultGraphicIsACheckBox() {
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>(converter);
assertNull(cell.getGraphic());
}
@Test public void test_updateItem_isEmpty_graphicIsNull() {
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>();
cell.updateItem("TEST", true);
assertNull(cell.getGraphic());
}
@Test public void test_updateItem_isEmpty_textIsNull() {
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>();
cell.updateItem("TEST", true);
assertNull(cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull() {
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>();
cell.updateItem("TEST", false);
assertNotNull(cell.getText());
assertEquals("TEST", cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull_nullConverter() {
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>();
cell.setConverter(null);
cell.updateItem("TEST", false);
assertNotNull(cell.getText());
assertEquals("TEST", cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull_nonNullConverter() {
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>();
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
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>();
cell.setEditable(false);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_treeViewEditableIsFalse_isEmpty() {
TreeView treeView = new TreeView();
treeView.setEditable(false);
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>();
cell.updateTreeView(treeView);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_treeViewEditableIsTrue_isEmpty() {
TreeView treeView = new TreeView();
treeView.setEditable(true);
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>();
cell.updateTreeView(treeView);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_treeViewEditableIsTrue_cellEditableIsTrue_isEmpty() {
TreeView treeView = new TreeView();
treeView.setEditable(true);
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>();
cell.setEditable(true);
cell.updateTreeView(treeView);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_cellEditableIsFalse_isNotEmpty() {
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>();
cell.updateItem("TEST", false);
cell.setEditable(false);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_treeViewEditableIsFalse_isNotEmpty() {
TreeView treeView = new TreeView();
treeView.setEditable(false);
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>();
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
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>();
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
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>();
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
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>();
cell.updateTreeView(treeView);
cell.updateIndex(0);
cell.startEdit();
assertTrue(cell.isEditing());
assertNotNull(cell.getGraphic());
cell.cancelEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_rt_29320() {
TreeItem root = new TreeItem("Root");
TreeView treeView = new TreeView(root);
treeView.setEditable(true);
ComboBoxTreeCell<Object> cell = new ComboBoxTreeCell<>();
cell.updateTreeView(treeView);
cell.updateIndex(0);
cell.setEditable(true);
cell.startEdit();
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
