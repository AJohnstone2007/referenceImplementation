package test.javafx.scene.control.cell;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class ProgressBarTableCellTest {
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
@Test public void testStatic_forTableColumn_noArgs_ensureCellFactoryIsNotNull() {
assertFalse(booleanProperty.get());
Callback<TableColumn<Object, Double>, TableCell<Object, Double>> cellFactory = ProgressBarTableCell.forTableColumn();
assertNotNull(cellFactory);
}
@Test public void testStatic_forTableColumn_noArgs_ensureCellFactoryCreatesCells() {
assertFalse(booleanProperty.get());
Callback<TableColumn<Object, Double>, TableCell<Object, Double>> cellFactory = ProgressBarTableCell.forTableColumn();
TableColumn tableColumn = new TableColumn<>();
ProgressBarTableCell<Object> cell = (ProgressBarTableCell<Object>)cellFactory.call(tableColumn);
assertNotNull(cell);
}
@Test public void testConstructor_noArgs_defaultStyleClass() {
ProgressBarTableCell<Object> cell = new ProgressBarTableCell<>();
assertTrue(cell.getStyleClass().contains("progress-bar-table-cell"));
}
@Test public void testConstructor_noArgs_defaultGraphicIsNull() {
ProgressBarTableCell<Object> cell = new ProgressBarTableCell<>();
assertNull(cell.getGraphic());
}
@Test public void test_updateItem_isEmpty_graphicIsNull() {
ProgressBarTableCell<Object> cell = new ProgressBarTableCell<>();
cell.updateItem(0.5, true);
assertNull(cell.getGraphic());
}
@Test public void test_updateItem_isEmpty_textIsNull() {
ProgressBarTableCell<Object> cell = new ProgressBarTableCell<>();
cell.updateItem(0.5, true);
assertNull(cell.getText());
}
@Test public void test_updateItem_isNotEmpty_graphicIsNotNull() {
ProgressBarTableCell<Object> cell = new ProgressBarTableCell<>();
setTableViewAndTableColumn(cell);
cell.updateItem(0.5, false);
assertNotNull(cell.getGraphic());
assertTrue(cell.getGraphic() instanceof ProgressBar);
}
@Test public void test_graphicMaxWidthIsDoubleMaxValue() {
ProgressBarTableCell<Object> cell = new ProgressBarTableCell<>();
cell.updateItem(0.5, false);
assertNotNull(cell.getGraphic());
assertEquals(Double.MAX_VALUE, ((ProgressBar)cell.getGraphic()).getMaxWidth(), 0.0);
}
}
