package test.javafx.scene.control.cell;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.junit.Before;
import org.junit.Test;
public class CheckBoxTableCellTest {
private SimpleBooleanProperty booleanProperty;
private Callback<Integer, ObservableValue<Boolean>> callback;
private StringConverter<Object> converter;
private TableView<Object> tableView;
private TableColumn<Object, Object> tableColumn;
@Before public void setup() {
tableView = new TableView<>();
tableColumn = new TableColumn<>();
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
private void setTableViewAndTableColumn(TableCell cell) {
cell.updateTableView(tableView);
cell.updateTableColumn(tableColumn);
}
@Test public void testStatic_forTableColumn_callback_ensureCellFactoryIsNotNull() {
assertFalse(booleanProperty.get());
Callback<TableColumn<Object, Object>, TableCell<Object, Object>> cellFactory = CheckBoxTableCell.forTableColumn(callback);
assertNotNull(cellFactory);
}
@Test public void testStatic_forTableColumn_callback_ensureCellFactoryCreatesCells() {
assertFalse(booleanProperty.get());
Callback<TableColumn<Object, Object>, TableCell<Object, Object>> cellFactory = CheckBoxTableCell.forTableColumn(callback);
TableColumn tableColumn = new TableColumn<>();
CheckBoxTableCell<Object, Object> cell = (CheckBoxTableCell<Object, Object>)cellFactory.call(tableColumn);
assertNotNull(cell);
}
@Test public void testStatic_forTableColumn_callback_ensureCellHasNonNullSelectedStateCallback() {
assertFalse(booleanProperty.get());
Callback<TableColumn<Object, Object>, TableCell<Object, Object>> cellFactory = CheckBoxTableCell.forTableColumn(callback);
TableColumn tableColumn = new TableColumn<>();
CheckBoxTableCell<Object, Object> cell = (CheckBoxTableCell<Object, Object>)cellFactory.call(tableColumn);
assertNotNull(cell.getSelectedStateCallback());
}
@Test public void testStatic_forTableColumn_callback_ensureCellHasNullStringConverter() {
assertFalse(booleanProperty.get());
Callback<TableColumn<Object, Object>, TableCell<Object, Object>> cellFactory = CheckBoxTableCell.forTableColumn(callback);
TableColumn tableColumn = new TableColumn<>();
CheckBoxTableCell<Object, Object> cell = (CheckBoxTableCell<Object, Object>)cellFactory.call(tableColumn);
assertNull(cell.getConverter());
}
@Test public void testStatic_forTableColumn_callback_2_ensureCellFactoryIsNotNull() {
assertFalse(booleanProperty.get());
Callback<TableColumn<Object, Object>, TableCell<Object, Object>> cellFactory = CheckBoxTableCell.forTableColumn(callback, converter);
assertNotNull(cellFactory);
}
@Test public void testStatic_forTableColumn_callback_2_ensureCellFactoryCreatesCells() {
assertFalse(booleanProperty.get());
Callback<TableColumn<Object, Object>, TableCell<Object, Object>> cellFactory = CheckBoxTableCell.forTableColumn(callback, converter);
TableColumn tableColumn = new TableColumn<>();
CheckBoxTableCell<Object, Object> cell = (CheckBoxTableCell<Object, Object>)cellFactory.call(tableColumn);
assertNotNull(cell);
}
@Test public void testStatic_forTableColumn_callback_2_ensureCellHasNonNullSelectedStateCallback() {
assertFalse(booleanProperty.get());
Callback<TableColumn<Object, Object>, TableCell<Object, Object>> cellFactory = CheckBoxTableCell.forTableColumn(callback, converter);
TableColumn tableColumn = new TableColumn<>();
CheckBoxTableCell<Object, Object> cell = (CheckBoxTableCell<Object, Object>)cellFactory.call(tableColumn);
assertNotNull(cell.getSelectedStateCallback());
}
@Test public void testStatic_forTableColumn_callback_2_ensureCellHasSetStringConverter() {
assertFalse(booleanProperty.get());
Callback<TableColumn<Object, Object>, TableCell<Object, Object>> cellFactory = CheckBoxTableCell.forTableColumn(callback, converter);
TableColumn tableColumn = new TableColumn<>();
CheckBoxTableCell<Object, Object> cell = (CheckBoxTableCell<Object, Object>)cellFactory.call(tableColumn);
assertNotNull(cell.getConverter());
assertEquals(converter, cell.getConverter());
}
@Test public void testConstructor_noArgs_defaultCallbackIsNull() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>();
assertNull(cell.getSelectedStateCallback());
}
@Test public void testConstructor_noArgs_defaultStringConverterIsNull() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>();
assertNull(cell.getConverter());
}
@Test public void testConstructor_noArgs_defaultStyleClass() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>();
assertTrue(cell.getStyleClass().contains("check-box-table-cell"));
}
@Test public void testConstructor_noArgs_defaultGraphicIsNull() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>();
assertNull(cell.getGraphic());
}
@Test public void testConstructor_getSelectedProperty_selectedPropertyIsNotNull() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>(callback);
assertEquals(callback, cell.getSelectedStateCallback());
}
@Test public void testConstructor_getSelectedProperty_defaultStringConverterIsNull() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>(callback);
assertNull(cell.getConverter());
}
@Test public void testConstructor_getSelectedProperty_defaultStyleClass() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>(callback);
assertTrue(cell.getStyleClass().contains("check-box-table-cell"));
}
@Test public void testConstructor_getSelectedProperty_defaultGraphicIsNull() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>(callback);
assertNull(cell.getGraphic());
}
@Test public void testConstructor_getSelectedProperty_converter_selectedPropertyIsNotNull() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>(callback, converter);
assertEquals(callback, cell.getSelectedStateCallback());
}
@Test public void testConstructor_getSelectedProperty_converter_defaultStringConverterIsNotNull() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>(callback, converter);
assertNotNull(cell.getConverter());
assertEquals(converter, cell.getConverter());
}
@Test public void testConstructor_getSelectedProperty_converter_defaultStyleClass() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>(callback, converter);
assertTrue(cell.getStyleClass().contains("check-box-table-cell"));
}
@Test public void testConstructor_getSelectedProperty_converter_defaultGraphicIsNull() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>(callback, converter);
assertNull(cell.getGraphic());
}
@Test(expected=NullPointerException.class)
public void test_getSelectedPropertyIsNull() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>();
cell.updateItem("TEST", false);
}
@Test public void test_updateItem_isEmpty_graphicIsNull() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>();
cell.updateItem("TEST", true);
assertNull(cell.getGraphic());
}
@Test public void test_updateItem_isEmpty_textIsNull() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>();
cell.updateItem("TEST", true);
assertNull(cell.getText());
}
@Test public void test_updateItem_isNotEmpty_graphicIsNotNull() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>(callback);
setTableViewAndTableColumn(cell);
cell.updateItem("TEST", false);
assertNotNull(cell.getGraphic());
assertTrue(cell.getGraphic() instanceof CheckBox);
}
@Test public void test_updateItem_isNotEmpty_textIsNullBecauseOfNullConverter_1() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>(callback);
setTableViewAndTableColumn(cell);
cell.updateItem("TEST", false);
assertNull(cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNullBecauseOfNullConverter_2() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>(callback);
setTableViewAndTableColumn(cell);
cell.setConverter(null);
cell.updateItem("TEST", false);
assertNull(cell.getText());
}
@Test public void test_updateItem_isNotEmpty_textIsNotNull_nonNullConverter() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>(callback);
setTableViewAndTableColumn(cell);
cell.setConverter(new StringConverter<Object>() {
@Override public Object fromString(String string) {
return "ERROR";
}
@Override public String toString(Object object) {
return "CONVERTED";
}
});
cell.updateItem("TEST", false);
assertEquals("CONVERTED", cell.getText());
}
@Test public void test_booleanPropertyChangeUpdatesCheckBoxSelection() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>(callback);
setTableViewAndTableColumn(cell);
cell.updateItem("TEST", false);
CheckBox cb = (CheckBox)cell.getGraphic();
assertFalse(cb.isSelected());
booleanProperty.set(true);
assertTrue(cb.isScaleShape());
booleanProperty.set(false);
assertFalse(cb.isSelected());
}
@Test public void test_checkBoxSelectionUpdatesBooleanProperty() {
CheckBoxTableCell<Object, Object> cell = new CheckBoxTableCell<>(callback);
setTableViewAndTableColumn(cell);
cell.updateItem("TEST", false);
CheckBox cb = (CheckBox)cell.getGraphic();
assertFalse(booleanProperty.get());
cb.setSelected(true);
assertTrue(booleanProperty.get());
cb.setSelected(false);
assertFalse(booleanProperty.get());
}
}
