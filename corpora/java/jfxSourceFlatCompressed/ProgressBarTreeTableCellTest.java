package test.javafx.scene.control.cell;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ProgressBarTreeTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class ProgressBarTreeTableCellTest {
private SimpleBooleanProperty booleanProperty;
private Callback<Integer, ObservableValue<Boolean>> callback;
private StringConverter<Object> converter;
private TreeTableView<Object> tableView;
private TreeTableColumn<Object, Object> tableColumn;
@Before public void setup() {
tableView = new TreeTableView<>();
tableColumn = new TreeTableColumn<>();
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
private void setTableViewAndTreeTableColumn(TreeTableCell cell) {
cell.updateTreeTableView(tableView);
cell.updateTableColumn(tableColumn);
}
@Test public void testStatic_forTreeTableColumn_noArgs_ensureCellFactoryIsNotNull() {
assertFalse(booleanProperty.get());
Callback<TreeTableColumn<Object, Double>, TreeTableCell<Object, Double>> cellFactory = ProgressBarTreeTableCell.forTreeTableColumn();
assertNotNull(cellFactory);
}
@Test public void testStatic_forTreeTableColumn_noArgs_ensureCellFactoryCreatesCells() {
assertFalse(booleanProperty.get());
Callback<TreeTableColumn<Object, Double>, TreeTableCell<Object, Double>> cellFactory = ProgressBarTreeTableCell.forTreeTableColumn();
TreeTableColumn tableColumn = new TreeTableColumn<>();
ProgressBarTreeTableCell<Object> cell = (ProgressBarTreeTableCell<Object>)cellFactory.call(tableColumn);
assertNotNull(cell);
}
@Test public void testConstructor_noArgs_defaultStyleClass() {
ProgressBarTreeTableCell<Object> cell = new ProgressBarTreeTableCell<>();
assertTrue(cell.getStyleClass().contains("progress-bar-tree-table-cell"));
}
@Test public void testConstructor_noArgs_defaultGraphicIsNull() {
ProgressBarTreeTableCell<Object> cell = new ProgressBarTreeTableCell<>();
assertNull(cell.getGraphic());
}
@Test public void test_updateItem_isEmpty_graphicIsNull() {
ProgressBarTreeTableCell<Object> cell = new ProgressBarTreeTableCell<>();
cell.updateItem(0.5, true);
assertNull(cell.getGraphic());
}
@Test public void test_updateItem_isEmpty_textIsNull() {
ProgressBarTreeTableCell<Object> cell = new ProgressBarTreeTableCell<>();
cell.updateItem(0.5, true);
assertNull(cell.getText());
}
@Test public void test_updateItem_isNotEmpty_graphicIsNotNull() {
ProgressBarTreeTableCell<Object> cell = new ProgressBarTreeTableCell<>();
setTableViewAndTreeTableColumn(cell);
cell.updateItem(0.5, false);
assertNotNull(cell.getGraphic());
assertTrue(cell.getGraphic() instanceof ProgressBar);
}
@Test public void test_graphicMaxWidthIsDoubleMaxValue() {
ProgressBarTreeTableCell<Object> cell = new ProgressBarTreeTableCell<>();
cell.updateItem(0.5, false);
assertNotNull(cell.getGraphic());
assertEquals(Double.MAX_VALUE, ((ProgressBar)cell.getGraphic()).getMaxWidth(), 0.0);
}
}
