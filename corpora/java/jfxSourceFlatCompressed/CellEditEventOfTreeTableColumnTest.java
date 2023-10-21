package test.javafx.scene.control;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static javafx.scene.control.TreeTableColumn.editCommitEvent;
import static javafx.scene.control.TreeTableColumn.*;
import static org.junit.Assert.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
public class CellEditEventOfTreeTableColumnTest {
private TreeTableView<String> table;
private TreeTableColumn<String, String> editingColumn;
@Test
public void testDefaultOnCommitHandlerTablePositionWithNullTable() {
String edited = "edited";
TreeTablePosition<String, String> pos = new TreeTablePosition<>(null, 1, editingColumn);
CellEditEvent<String, String> event = new CellEditEvent<>(table, pos, editCommitEvent(), edited);
Event.fireEvent(editingColumn, event);
}
@Test
public void testDefaultOnCommitHandlerNullTablePosition() {
String edited = "edited";
CellEditEvent<String, String> event = new CellEditEvent<>(table, null, editCommitEvent(), edited);
Event.fireEvent(editingColumn, event);
}
@Test
public void testNullTablePositionGetTableView() {
CellEditEvent<String, String> ev = new CellEditEvent<>(table, null, editAnyEvent(), null);
assertNull("treeTable must be null if pos is null", ev.getTreeTableView());
}
@Test
public void testNullTablePositionGetTableColumn() {
CellEditEvent<String, String> ev = new CellEditEvent<>(table, null, editAnyEvent(), null);
assertNull("column must be null for null pos", ev.getTableColumn());
}
@Test
public void testNullTablePositionGetOldValue() {
CellEditEvent<String, String> ev = new CellEditEvent<>(table, null, editAnyEvent(), null);
assertNull("oldValue must be null for null pos", ev.getOldValue());
}
@Test
public void testNullTablePositionGetRowValue() {
CellEditEvent<String, String> ev = new CellEditEvent<>(table, null, editAnyEvent(), null);
assertNull("rowValue must be null for null pos", ev.getRowValue());
}
@Test
public void testNullTablePositionGetNewValue() {
String editedValue = "edited";
CellEditEvent<String, String> ev = new CellEditEvent<>(table, null, editAnyEvent(), editedValue);
assertEquals("editedValue must be available for null pos", editedValue, ev.getNewValue());
}
@Test
public void testTablePositionWithNullTable() {
String editedValue = "edited";
TreeTablePosition<String, String> pos = new TreeTablePosition<>(null, 1, editingColumn);
CellEditEvent<String, String> ev = new CellEditEvent<>(table, pos, editAnyEvent(), editedValue);
assertNull("rowValue must be null for null pos", ev.getRowValue());
}
@Test
public void testNullTable() {
new CellEditEvent<Object, Object>(null,
new TreeTablePosition<>(null, -1, null), editAnyEvent(), null);
}
@Test
public void testCellEditEventDifferentSource() {
assertCellEditEvent(new TreeTableView<>());
}
@Test
public void testCellEditEventSameSource() {
assertCellEditEvent(table);
}
@Test
public void testCellEditEventNullSource() {
assertCellEditEvent(null);
}
private void assertCellEditEvent(TreeTableView<String> source) {
int editingRow = 1;
String editedValue = "edited";
TreeItem<String> rowValue = table.getTreeItem(editingRow);
String oldValue = rowValue.getValue();
TreeTablePosition<String, String> pos = new TreeTablePosition<>(table, editingRow, editingColumn);
CellEditEvent<String,String> ev = new CellEditEvent<String, String>(source, pos, editAnyEvent(), editedValue);
if (source != null) {
assertEquals(source, ev.getSource());
}
assertCellEditEventState(ev, table, editingColumn, pos, editedValue, oldValue, rowValue);
}
private <S, T> void assertCellEditEventState(CellEditEvent<S, T> event,
TreeTableView<S> table, TreeTableColumn<S, T> tableColumn, TreeTablePosition<S, T> pos,
T newValue, T oldValue, TreeItem<S> rowValue) {
assertEquals(newValue, event.getNewValue());
assertEquals(oldValue, event.getOldValue());
assertEquals(rowValue, event.getRowValue());
assertEquals(tableColumn, event.getTableColumn());
assertEquals(pos, event.getTreeTablePosition());
assertEquals(table, event.getTreeTableView());
}
@Before public void setup() {
Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
if (throwable instanceof RuntimeException) {
throw (RuntimeException)throwable;
} else {
Thread.currentThread().getThreadGroup().uncaughtException(thread, throwable);
}
});
TreeItem<String> root = new TreeItem<>("root");
root.setExpanded(true);
ObservableList<String> model = FXCollections.observableArrayList("Four", "Five", "Fear");
root.getChildren().addAll(model.stream().map(TreeItem::new).collect(Collectors.toList()));
table = new TreeTableView<String>(root);
editingColumn = new TreeTableColumn<>("TEST");
table.getColumns().addAll(editingColumn);
editingColumn.setCellValueFactory(e -> e.getValue().valueProperty());
}
@After
public void cleanup() {
Thread.currentThread().setUncaughtExceptionHandler(null);
}
}
