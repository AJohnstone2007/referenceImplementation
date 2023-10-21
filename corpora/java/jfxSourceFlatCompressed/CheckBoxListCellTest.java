package test.javafx.scene.control.cell;
import static org.junit.Assert.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.junit.Before;
import org.junit.Test;
public class CheckBoxListCellTest {
private SimpleBooleanProperty booleanProperty;
private Callback<Object, ObservableValue<Boolean>> callback;
private StringConverter<Object> converter;
@Before public void setup() {
booleanProperty = new SimpleBooleanProperty(false);
callback = param -> booleanProperty;
converter = new StringConverter<Object>() {
@Override public String toString(Object object) {
return null;
}
@Override public Object fromString(String string) {
return null;
}
};
}
@Test public void testStatic_forListView_callback_ensureCellFactoryIsNotNull() {
assertFalse(booleanProperty.get());
Callback<ListView<Object>, ListCell<Object>> cellFactory = CheckBoxListCell.forListView(callback);
assertNotNull(cellFactory);
}
@Test public void testStatic_forListView_callback_ensureCellFactoryCreatesCells() {
assertFalse(booleanProperty.get());
Callback<ListView<Object>, ListCell<Object>> cellFactory = CheckBoxListCell.forListView(callback);
ListView<Object> listView = new ListView<>();
CheckBoxListCell<Object> cell = (CheckBoxListCell<Object>)cellFactory.call(listView);
assertNotNull(cell);
}
@Test public void testStatic_forListView_callback_ensureCellHasNonNullSelectedStateCallback() {
assertFalse(booleanProperty.get());
Callback<ListView<Object>, ListCell<Object>> cellFactory = CheckBoxListCell.forListView(callback);
ListView<Object> listView = new ListView<>();
CheckBoxListCell<Object> cell = (CheckBoxListCell<Object>)cellFactory.call(listView);
assertNotNull(cell.getSelectedStateCallback());
}
@Test public void testStatic_forListView_callback_ensureCellHasNonNullStringConverter() {
assertFalse(booleanProperty.get());
Callback<ListView<Object>, ListCell<Object>> cellFactory = CheckBoxListCell.forListView(callback);
ListView<Object> listView = new ListView<>();
CheckBoxListCell<Object> cell = (CheckBoxListCell<Object>)cellFactory.call(listView);
assertNotNull(cell.getConverter());
}
@Test public void testStatic_forListView_callback_2_ensureCellFactoryIsNotNull() {
assertFalse(booleanProperty.get());
Callback<ListView<Object>, ListCell<Object>> cellFactory = CheckBoxListCell.forListView(callback, converter);
assertNotNull(cellFactory);
}
@Test public void testStatic_forListView_callback_2_ensureCellFactoryCreatesCells() {
assertFalse(booleanProperty.get());
Callback<ListView<Object>, ListCell<Object>> cellFactory = CheckBoxListCell.forListView(callback, converter);
ListView<Object> listView = new ListView<>();
CheckBoxListCell<Object> cell = (CheckBoxListCell<Object>)cellFactory.call(listView);
assertNotNull(cell);
}
@Test public void testStatic_forListView_callback_2_ensureCellHasNonNullSelectedStateCallback() {
assertFalse(booleanProperty.get());
Callback<ListView<Object>, ListCell<Object>> cellFactory = CheckBoxListCell.forListView(callback, converter);
ListView<Object> listView = new ListView<>();
CheckBoxListCell<Object> cell = (CheckBoxListCell<Object>)cellFactory.call(listView);
assertNotNull(cell.getSelectedStateCallback());
}
@Test public void testStatic_forListView_callback_2_ensureCellHasSetStringConverter() {
assertFalse(booleanProperty.get());
Callback<ListView<Object>, ListCell<Object>> cellFactory = CheckBoxListCell.forListView(callback, converter);
ListView<Object> listView = new ListView<>();
CheckBoxListCell<Object> cell = (CheckBoxListCell<Object>)cellFactory.call(listView);
assertNotNull(cell.getConverter());
assertEquals(converter, cell.getConverter());
}
@Test public void testConstructor_noArgs_defaultCallbackIsNull() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>();
assertNull(cell.getSelectedStateCallback());
}
@Test public void testConstructor_noArgs_defaultStringConverterIsNotNull() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>();
assertNotNull(cell.getConverter());
}
@Test public void testConstructor_noArgs_defaultStyleClass() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>();
assertTrue(cell.getStyleClass().contains("check-box-list-cell"));
}
@Test public void testConstructor_noArgs_defaultGraphicIsNull() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>();
assertNull(cell.getGraphic());
}
@Test public void testConstructor_getSelectedProperty_selectedPropertyIsNotNull() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>(callback);
assertEquals(callback, cell.getSelectedStateCallback());
}
@Test public void testConstructor_getSelectedProperty_defaultStringConverterIsNotNull() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>(callback);
assertNotNull(cell.getConverter());
}
@Test public void testConstructor_getSelectedProperty_defaultStyleClass() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>(callback);
assertTrue(cell.getStyleClass().contains("check-box-list-cell"));
}
@Test public void testConstructor_getSelectedProperty_defaultGraphicIsNull() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>(callback);
assertNull(cell.getGraphic());
}
@Test public void testConstructor_getSelectedProperty_converter_selectedPropertyIsNotNull() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>(callback, converter);
assertEquals(callback, cell.getSelectedStateCallback());
}
@Test public void testConstructor_getSelectedProperty_converter_defaultStringConverterIsNotNull() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>(callback, converter);
assertNotNull(cell.getConverter());
assertEquals(converter, cell.getConverter());
}
@Test public void testConstructor_getSelectedProperty_converter_defaultStyleClass() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>(callback, converter);
assertTrue(cell.getStyleClass().contains("check-box-list-cell"));
}
@Test public void testConstructor_getSelectedProperty_converter_defaultGraphicIsNull() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>(callback, converter);
assertNull(cell.getGraphic());
}
@Test(expected=NullPointerException.class)
public void test_getSelectedPropertyIsNull() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>();
cell.updateItem("TEST", false);
}
@Test public void test_updateItem_isEmpty_graphicIsNull() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>();
cell.updateItem("TEST", true);
assertNull(cell.getGraphic());
}
@Test public void test_updateItem_isEmpty_textIsNull() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>();
cell.updateItem("TEST", true);
assertNull(cell.getText());
}
@Test public void test_updateItem_isNotEmpty_graphicIsNotNull() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>(callback);
cell.updateItem("TEST", false);
assertNotNull(cell.getGraphic());
assertTrue(cell.getGraphic() instanceof CheckBox);
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>(callback);
cell.updateItem("TEST", false);
assertNotNull(cell.getText());
assertEquals("TEST", cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull_nullConverter() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>(callback);
cell.setConverter(null);
cell.updateItem("TEST", false);
assertNotNull(cell.getText());
assertEquals("TEST", cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull_nonNullConverter() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>(callback);
cell.setConverter(new StringConverter<Object>() {
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
@Test public void test_booleanPropertyChangeUpdatesCheckBoxSelection() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>(callback);
cell.updateItem("TEST", false);
CheckBox cb = (CheckBox)cell.getGraphic();
assertFalse(cb.isSelected());
booleanProperty.set(true);
assertTrue(cb.isScaleShape());
booleanProperty.set(false);
assertFalse(cb.isSelected());
}
@Test public void test_checkBoxSelectionUpdatesBooleanProperty() {
CheckBoxListCell<Object> cell = new CheckBoxListCell<>(callback);
cell.updateItem("TEST", false);
CheckBox cb = (CheckBox)cell.getGraphic();
assertFalse(booleanProperty.get());
cb.setSelected(true);
assertTrue(booleanProperty.get());
cb.setSelected(false);
assertFalse(booleanProperty.get());
}
}
