package test.javafx.scene.control.cell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
@RunWith(Parameterized.class)
public class TableCellStartEditTest {
private static final boolean[] EDITABLE_STATES = { true, false };
private final Supplier<TableCell<String, ?>> tableCellSupplier;
private TableView<String> table;
private TableRow<String> tableRow;
private TableColumn<String, ?> tableColumn;
private TableCell<String, ?> tableCell;
@Parameterized.Parameters
public static Collection<Object[]> data() {
return wrapAsObjectArray(List.of(TableCell::new, ComboBoxTableCell::new, TextFieldTableCell::new,
ChoiceBoxTableCell::new, CheckBoxTableCell::new, ProgressBarTableCell::new));
}
private static Collection<Object[]> wrapAsObjectArray(List<Supplier<TableCell<Object, ?>>> tableCells) {
return tableCells.stream().map(cell -> new Object[] { cell }).collect(toList());
}
public TableCellStartEditTest(Supplier<TableCell<String, ?>> tableCellSupplier) {
this.tableCellSupplier = tableCellSupplier;
}
@Before
public void setup() {
ObservableList<String> items = FXCollections.observableArrayList("1", "2", "3");
table = new TableView<>(items);
tableColumn = new TableColumn<>();
table.getColumns().add(tableColumn);
tableRow = new TableRow<>();
tableCell = tableCellSupplier.get();
}
@Test
public void testStartEditMustNotThrowNPE() {
tableCell.startEdit();
}
@Test
public void testStartEditRespectsEditable() {
tableCell.updateIndex(0);
tableCell.updateTableColumn(tableColumn);
tableCell.updateTableRow(tableRow);
tableCell.updateTableView(table);
for (boolean isTableEditable : EDITABLE_STATES) {
for (boolean isColumnEditable : EDITABLE_STATES) {
for (boolean isRowEditable : EDITABLE_STATES) {
for (boolean isCellEditable : EDITABLE_STATES) {
testStartEditImpl(isTableEditable, isColumnEditable, isRowEditable, isCellEditable);
}
}
}
}
}
private void testStartEditImpl(boolean isTableEditable, boolean isColumnEditable, boolean isRowEditable,
boolean isCellEditable) {
assertFalse(tableCell.isEditing());
table.setEditable(isTableEditable);
tableColumn.setEditable(isColumnEditable);
tableRow.setEditable(isRowEditable);
tableCell.setEditable(isCellEditable);
tableCell.startEdit();
boolean expectedEditingState = isTableEditable && isColumnEditable && isRowEditable && isCellEditable;
assertEquals(expectedEditingState, tableCell.isEditing());
if (tableCell instanceof CheckBoxTableCell) {
assertNotNull(tableCell.getGraphic());
} else if (tableCell instanceof ProgressBarTableCell) {
assertNotNull(tableCell.getGraphic());
} else if (!tableCell.getClass().equals(TableCell.class)) {
assertEquals(expectedEditingState, tableCell.getGraphic() != null);
}
tableCell.cancelEdit();
}
}
