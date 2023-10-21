package test.javafx.scene.control.cell;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TextFieldListCellTest {
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
@Test public void testStatic_forListView_noArgs_ensureCellFactoryIsNotNull() {
Callback<ListView<String>, ListCell<String>> cellFactory = TextFieldListCell.forListView();
assertNotNull(cellFactory);
}
@Test public void testStatic_forListView_noArgs_ensureCellFactoryCreatesCells() {
Callback<ListView<String>, ListCell<String>> cellFactory = TextFieldListCell.forListView();
ListView<String> listView = new ListView<>();
TextFieldListCell<String> cell = (TextFieldListCell<String>)cellFactory.call(listView);
assertNotNull(cell);
}
@Test public void testStatic_forListView_callback_ensureCellHasNonNullStringConverter() {
Callback<ListView<String>, ListCell<String>> cellFactory = TextFieldListCell.forListView();
ListView<String> listView = new ListView<>();
TextFieldListCell<String> cell = (TextFieldListCell<String>)cellFactory.call(listView);
assertNotNull(cell.getConverter());
}
@Test public void testStatic_forListView_converter_ensureCellFactoryIsNotNull() {
Callback<ListView<Object>, ListCell<Object>> cellFactory = TextFieldListCell.forListView(converter);
assertNotNull(cellFactory);
}
@Test public void testStatic_forListView_converter_ensureCellFactoryCreatesCells() {
Callback<ListView<Object>, ListCell<Object>> cellFactory = TextFieldListCell.forListView(converter);
ListView<Object> listView = new ListView<>();
TextFieldListCell<Object> cell = (TextFieldListCell<Object>)cellFactory.call(listView);
assertNotNull(cell);
}
@Test public void testStatic_forListView_converter_ensureCellHasSetStringConverter() {
Callback<ListView<Object>, ListCell<Object>> cellFactory = TextFieldListCell.forListView(converter);
ListView<Object> listView = new ListView<>();
TextFieldListCell<Object> cell = (TextFieldListCell<Object>)cellFactory.call(listView);
assertNotNull(cell.getConverter());
assertEquals(converter, cell.getConverter());
}
@Test public void testConstructor_noArgs_defaultStringConverterIsNull() {
TextFieldListCell<Object> cell = new TextFieldListCell<>();
assertNull(cell.getConverter());
}
@Test public void testConstructor_noArgs_defaultStyleClass() {
TextFieldListCell<Object> cell = new TextFieldListCell<>();
assertTrue(cell.getStyleClass().contains("text-field-list-cell"));
}
@Test public void testConstructor_noArgs_defaultGraphicIsNull() {
TextFieldListCell<Object> cell = new TextFieldListCell<>();
assertNull(cell.getGraphic());
}
@Test public void testConstructor_converter_defaultStringConverterIsNotNull() {
TextFieldListCell<Object> cell = new TextFieldListCell<>(converter);
assertNotNull(cell.getConverter());
}
@Test public void testConstructor_converter_defaultStyleClass() {
TextFieldListCell<Object> cell = new TextFieldListCell<>(converter);
assertTrue(cell.getStyleClass().contains("text-field-list-cell"));
}
@Test public void testConstructor_converter_defaultGraphicIsACheckBox() {
TextFieldListCell<Object> cell = new TextFieldListCell<>(converter);
assertNull(cell.getGraphic());
}
@Test public void test_updateItem_isEmpty_graphicIsNull() {
TextFieldListCell<Object> cell = new TextFieldListCell<>();
cell.updateItem("TEST", true);
assertNull(cell.getGraphic());
}
@Test public void test_updateItem_isEmpty_textIsNull() {
TextFieldListCell<Object> cell = new TextFieldListCell<>();
cell.updateItem("TEST", true);
assertNull(cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull() {
TextFieldListCell<Object> cell = new TextFieldListCell<>();
cell.updateItem("TEST", false);
assertNotNull(cell.getText());
assertEquals("TEST", cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull_nullConverter() {
TextFieldListCell<Object> cell = new TextFieldListCell<>();
cell.setConverter(null);
cell.updateItem("TEST", false);
assertNotNull(cell.getText());
assertEquals("TEST", cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull_nonNullConverter() {
TextFieldListCell<Object> cell = new TextFieldListCell<>();
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
TextFieldListCell<Object> cell = new TextFieldListCell<>();
cell.setEditable(false);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_listViewEditableIsFalse_isEmpty() {
ListView listView = new ListView();
listView.setEditable(false);
TextFieldListCell<Object> cell = new TextFieldListCell<>();
cell.updateListView(listView);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_listViewEditableIsTrue_isEmpty() {
ListView listView = new ListView();
listView.setEditable(true);
TextFieldListCell<Object> cell = new TextFieldListCell<>();
cell.updateListView(listView);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_listViewEditableIsTrue_cellEditableIsTrue_isEmpty() {
ListView listView = new ListView();
listView.setEditable(true);
TextFieldListCell<Object> cell = new TextFieldListCell<>();
cell.setEditable(true);
cell.updateListView(listView);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_cellEditableIsFalse_isNotEmpty() {
TextFieldListCell<Object> cell = new TextFieldListCell<>();
cell.updateItem("TEST", false);
cell.setEditable(false);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_listViewEditableIsFalse_isNotEmpty() {
ListView listView = new ListView();
listView.setEditable(false);
TextFieldListCell<Object> cell = new TextFieldListCell<>();
cell.updateListView(listView);
cell.updateItem("TEST", false);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_listViewEditableIsTrue_isNotEmpty() {
ListView listView = new ListView();
listView.setEditable(true);
TextFieldListCell<Object> cell = new TextFieldListCell<>();
cell.updateListView(listView);
cell.updateItem("TEST", false);
cell.startEdit();
assertTrue(cell.isEditing());
assertNotNull(cell.getGraphic());
}
@Test public void test_startEdit_listViewEditableIsTrue_cellEditableIsTrue_isNotEmpty() {
ListView listView = new ListView();
listView.setEditable(true);
TextFieldListCell<Object> cell = new TextFieldListCell<>();
cell.setEditable(true);
cell.updateListView(listView);
cell.updateItem("TEST", false);
cell.startEdit();
assertTrue(cell.isEditing());
assertNotNull(cell.getGraphic());
}
@Test public void test_cancelEdit() {
ListView listView = new ListView();
listView.setEditable(true);
TextFieldListCell<Object> cell = new TextFieldListCell<>();
cell.updateListView(listView);
cell.updateItem("TEST", false);
cell.startEdit();
assertTrue(cell.isEditing());
assertNotNull(cell.getGraphic());
cell.cancelEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
}
