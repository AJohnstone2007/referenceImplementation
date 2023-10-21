package test.javafx.scene.control.cell;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.ChoiceBoxTreeCell;
import javafx.scene.control.cell.ComboBoxTreeCell;
import javafx.scene.control.cell.TextFieldTreeCell;
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
public class TreeCellStartEditTest {
private static final boolean[] EDITABLE_STATES = { true, false };
private final Supplier<TreeCell<String>> treeCellSupplier;
private TreeView<String> treeView;
private TreeCell<String> treeCell;
@Parameterized.Parameters
public static Collection<Object[]> data() {
return wrapAsObjectArray(List.of(TreeCell::new, ComboBoxTreeCell::new, TextFieldTreeCell::new,
ChoiceBoxTreeCell::new,() -> new CheckBoxTreeCell<>(obj -> new SimpleBooleanProperty())));
}
private static Collection<Object[]> wrapAsObjectArray(List<Supplier<TreeCell<String>>> treeCells) {
return treeCells.stream().map(cell -> new Object[] { cell }).collect(toList());
}
public TreeCellStartEditTest(Supplier<TreeCell<String>> treeCellSupplier) {
this.treeCellSupplier = treeCellSupplier;
}
@Before
public void setup() {
TreeItem<String> root = new TreeItem<>("1");
root.getChildren().addAll(List.of(new TreeItem<>("2"), new TreeItem<>("3")));
treeView = new TreeView<>(root);
treeCell = treeCellSupplier.get();
}
@Test
public void testStartEditMustNotThrowNPE() {
treeCell.startEdit();
}
@Test
public void testStartEditRespectsEditable() {
treeCell.updateIndex(0);
treeCell.updateTreeView(treeView);
for (boolean isTreeViewEditable : EDITABLE_STATES) {
for (boolean isCellEditable : EDITABLE_STATES) {
testStartEditImpl(isTreeViewEditable, isCellEditable);
}
}
}
private void testStartEditImpl(boolean isTreeViewEditable, boolean isCellEditable) {
assertFalse(treeCell.isEditing());
treeView.setEditable(isTreeViewEditable);
treeCell.setEditable(isCellEditable);
treeCell.startEdit();
boolean expectedEditingState = isTreeViewEditable && isCellEditable;
assertEquals(expectedEditingState, treeCell.isEditing());
if (treeCell instanceof CheckBoxTreeCell) {
assertNotNull(treeCell.getGraphic());
} else if (!treeCell.getClass().equals(TreeCell.class)) {
assertEquals(expectedEditingState, treeCell.getGraphic() != null);
}
treeCell.cancelEdit();
}
}
