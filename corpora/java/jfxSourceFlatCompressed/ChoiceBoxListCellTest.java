package test.javafx.scene.control.cell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class ChoiceBoxListCellTest {
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
Callback<ListView<String>, ListCell<String>> cellFactory = ChoiceBoxListCell.forListView();
assertNotNull(cellFactory);
}
@Test public void testStatic_forListView_noArgs_ensureCellFactoryCreatesCells() {
Callback<ListView<String>, ListCell<String>> cellFactory = ChoiceBoxListCell.forListView();
ListView<String> listView = new ListView<>();
ChoiceBoxListCell<String> cell = (ChoiceBoxListCell<String>)cellFactory.call(listView);
assertNotNull(cell);
}
@Test public void testStatic_forListView_noArgs_ensureCellHasNonNullStringConverter() {
Callback<ListView<String>, ListCell<String>> cellFactory = ChoiceBoxListCell.forListView();
ListView<String> listView = new ListView<>();
ChoiceBoxListCell<String> cell = (ChoiceBoxListCell<String>)cellFactory.call(listView);
assertNotNull(cell.getConverter());
}
@Test public void testStatic_forListView_items_ensureSuccessWhenItemsIsNull() {
ObservableList<String> items = null;
Callback<ListView<String>, ListCell<String>> cellFactory = ChoiceBoxListCell.forListView(items);
assertNotNull(cellFactory);
}
@Test public void testStatic_forListView_items_ensureCellFactoryIsNotNull() {
ObservableList<String> items = FXCollections.emptyObservableList();
Callback<ListView<String>, ListCell<String>> cellFactory = ChoiceBoxListCell.forListView(items);
assertNotNull(cellFactory);
}
@Test public void testStatic_forListView_items_ensureCellFactoryCreatesCells() {
ObservableList<String> items = FXCollections.emptyObservableList();
Callback<ListView<String>, ListCell<String>> cellFactory = ChoiceBoxListCell.forListView(items);
ListView<String> listView = new ListView<>();
ChoiceBoxListCell<String> cell = (ChoiceBoxListCell<String>)cellFactory.call(listView);
assertNotNull(cell);
}
@Test public void testStatic_forListView_items_ensureCellHasNonNullStringConverter() {
ObservableList<String> items = FXCollections.emptyObservableList();
Callback<ListView<String>, ListCell<String>> cellFactory = ChoiceBoxListCell.forListView(items);
ListView<String> listView = new ListView<>();
ChoiceBoxListCell<String> cell = (ChoiceBoxListCell<String>)cellFactory.call(listView);
assertNotNull(cell.getConverter());
}
@Test public void testConstructor_noArgs_defaultStringConverterIsNotNull() {
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>();
assertNotNull(cell.getConverter());
}
@Test public void testConstructor_noArgs_defaultStyleClass() {
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>();
assertTrue(cell.getStyleClass().contains("choice-box-list-cell"));
}
@Test public void testConstructor_noArgs_defaultGraphicIsNull() {
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>();
assertNull(cell.getGraphic());
}
@Test public void testConstructor_converter_defaultStringConverterIsNotNull() {
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>(converter);
assertNotNull(cell.getConverter());
}
@Test public void testConstructor_converter_defaultStyleClass() {
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>(converter);
assertTrue(cell.getStyleClass().contains("choice-box-list-cell"));
}
@Test public void testConstructor_converter_defaultGraphicIsACheckBox() {
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>(converter);
assertNull(cell.getGraphic());
}
@Test public void test_updateItem_isEmpty_graphicIsNull() {
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>();
cell.updateItem("TEST", true);
assertNull(cell.getGraphic());
}
@Test public void test_updateItem_isEmpty_textIsNull() {
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>();
cell.updateItem("TEST", true);
assertNull(cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull() {
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>();
cell.updateItem("TEST", false);
assertNotNull(cell.getText());
assertEquals("TEST", cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull_nullConverter() {
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>();
cell.setConverter(null);
cell.updateItem("TEST", false);
assertNotNull(cell.getText());
assertEquals("TEST", cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull_nonNullConverter() {
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>();
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
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>();
cell.setEditable(false);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_listViewEditableIsFalse_isEmpty() {
ListView listView = new ListView();
listView.setEditable(false);
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>();
cell.updateListView(listView);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
public void test_startEdit_cellEditableIsTrue_listViewIsNull_isEmpty() {
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>();
cell.setEditable(true);
cell.startEdit();
}
@Test public void test_startEdit_listViewEditableIsTrue_isEmpty() {
ListView listView = new ListView();
listView.setEditable(true);
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>();
cell.updateListView(listView);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_listViewEditableIsTrue_cellEditableIsTrue_isEmpty() {
ListView listView = new ListView();
listView.setEditable(true);
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>();
cell.setEditable(true);
cell.updateListView(listView);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_cellEditableIsFalse_isNotEmpty() {
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>();
cell.updateItem("TEST", false);
cell.setEditable(false);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_listViewEditableIsFalse_isNotEmpty() {
ListView listView = new ListView();
listView.setEditable(false);
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>();
cell.updateListView(listView);
cell.updateItem("TEST", false);
cell.startEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_startEdit_listViewEditableIsTrue_isNotEmpty() {
ListView listView = new ListView();
listView.setEditable(true);
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>();
cell.updateListView(listView);
cell.updateItem("TEST", false);
cell.startEdit();
assertTrue(cell.isEditing());
assertNotNull(cell.getGraphic());
}
@Test public void test_startEdit_listViewEditableIsTrue_cellEditableIsTrue_isNotEmpty() {
ListView listView = new ListView();
listView.setEditable(true);
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>();
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
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>();
cell.updateListView(listView);
cell.updateItem("TEST", false);
cell.startEdit();
assertTrue(cell.isEditing());
assertNotNull(cell.getGraphic());
cell.cancelEdit();
assertFalse(cell.isEditing());
assertNull(cell.getGraphic());
}
@Test public void test_rt_29320() {
ListView listView = new ListView();
listView.setEditable(true);
ChoiceBoxListCell<Object> cell = new ChoiceBoxListCell<>();
cell.updateListView(listView);
cell.updateItem("TEST", false);
cell.setEditable(true);
cell.startEdit();
ChoiceBox cb = (ChoiceBox) cell.getGraphic();
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
