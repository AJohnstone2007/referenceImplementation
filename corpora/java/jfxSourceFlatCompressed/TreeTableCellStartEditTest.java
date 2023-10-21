package test.javafx.scene.control.cell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.ChoiceBoxTreeTableCell;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import javafx.scene.control.cell.ProgressBarTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
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
public class TreeTableCellStartEditTest {
private static final boolean[] EDITABLE_STATES = { true, false };
private final Supplier<TreeTableCell<String, ?>> treeTableCellSupplier;
private TreeTableView<String> treeTable;
private TreeTableRow<String> treeTableRow;
private TreeTableColumn<String, ?> treeTableColumn;
private TreeTableCell<String, ?> treeTableCell;
@Parameterized.Parameters
public static Collection<Object[]> data() {
return wrapAsObjectArray(
List.of(TreeTableCell::new , ComboBoxTreeTableCell::new, TextFieldTreeTableCell::new,
ChoiceBoxTreeTableCell::new, CheckBoxTreeTableCell::new, ProgressBarTreeTableCell::new));
}
private static Collection<Object[]> wrapAsObjectArray(List<Supplier<TreeTableCell<Object, ?>>> treeTableCells) {
return treeTableCells.stream().map(cell -> new Object[] { cell }).collect(toList());
}
public TreeTableCellStartEditTest(Supplier<TreeTableCell<String, ?>> treeTableCellSupplier) {
this.treeTableCellSupplier = treeTableCellSupplier;
}
@Before
public void setup() {
TreeItem<String> root = new TreeItem<>("1");
root.getChildren().addAll(List.of(new TreeItem<>("2"), new TreeItem<>("3")));
treeTable = new TreeTableView<>(root);
treeTableColumn = new TreeTableColumn<>();
treeTable.getColumns().add(treeTableColumn);
treeTableRow = new TreeTableRow<>();
treeTableCell = treeTableCellSupplier.get();
}
@Test
public void testStartEditMustNotThrowNPE() {
treeTableCell.startEdit();
}
@Test
public void testStartEditRespectsEditable() {
treeTableCell.updateIndex(0);
treeTableCell.updateTableColumn((TreeTableColumn) treeTableColumn);
treeTableCell.updateTableRow(treeTableRow);
treeTableCell.updateTreeTableView(treeTable);
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
private void testStartEditImpl(boolean isTreeTableEditable, boolean isColumnEditable, boolean isRowEditable,
boolean isCellEditable) {
assertFalse(treeTableCell.isEditing());
treeTable.setEditable(isTreeTableEditable);
treeTableColumn.setEditable(isColumnEditable);
treeTableRow.setEditable(isRowEditable);
treeTableCell.setEditable(isCellEditable);
treeTableCell.startEdit();
boolean expectedEditingState = isTreeTableEditable && isColumnEditable && isRowEditable && isCellEditable;
assertEquals(expectedEditingState, treeTableCell.isEditing());
if (treeTableCell instanceof CheckBoxTreeTableCell) {
assertNotNull(treeTableCell.getGraphic());
} else if (treeTableCell instanceof ProgressBarTreeTableCell) {
assertNotNull(treeTableCell.getGraphic());
} else if (!treeTableCell.getClass().equals(TreeTableCell.class)) {
assertEquals(expectedEditingState, treeTableCell.getGraphic() != null);
}
treeTableCell.cancelEdit();
}
}
