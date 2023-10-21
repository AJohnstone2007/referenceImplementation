package test.javafx.scene.control.cell;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.control.cell.TextFieldListCell;
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
public class ListCellStartEditTest {
private static final boolean[] EDITABLE_STATES = { true, false };
private final Supplier<ListCell<String>> listCellSupplier;
private ListView<String> listView;
private ListCell<String> listCell;
@Parameterized.Parameters
public static Collection<Object[]> data() {
return wrapAsObjectArray(List.of(ListCell::new, ComboBoxListCell::new, TextFieldListCell::new,
ChoiceBoxListCell::new, () -> new CheckBoxListCell<>(obj -> new SimpleBooleanProperty())));
}
private static Collection<Object[]> wrapAsObjectArray(List<Supplier<ListCell<?>>> listCells) {
return listCells.stream().map(cell -> new Object[] { cell }).collect(toList());
}
public ListCellStartEditTest(Supplier<ListCell<String>> listCellSupplier) {
this.listCellSupplier = listCellSupplier;
}
@Before
public void setup() {
ObservableList<String> items = FXCollections.observableArrayList("1", "2", "3");
listView = new ListView<>(items);
listCell = listCellSupplier.get();
}
@Test
public void testStartEditMustNotThrowNPE() {
listCell.startEdit();
}
@Test
public void testStartEditRespectsEditable() {
listCell.updateIndex(0);
listCell.updateListView(listView);
for (boolean isListViewEditable : EDITABLE_STATES) {
for (boolean isCellEditable : EDITABLE_STATES) {
testStartEditImpl(isListViewEditable, isCellEditable);
}
}
}
private void testStartEditImpl(boolean isListViewEditable, boolean isCellEditable) {
assertFalse(listCell.isEditing());
listView.setEditable(isListViewEditable);
listCell.setEditable(isCellEditable);
listCell.startEdit();
boolean expectedEditingState = isListViewEditable && isCellEditable;
assertEquals(expectedEditingState, listCell.isEditing());
if (listCell instanceof CheckBoxListCell) {
assertNotNull(listCell.getGraphic());
} else if (!listCell.getClass().equals(ListCell.class)) {
assertEquals(expectedEditingState, listCell.getGraphic() != null);
}
listCell.cancelEdit();
}
}
