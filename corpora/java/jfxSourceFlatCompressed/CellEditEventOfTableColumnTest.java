package test.javafx.scene.control;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static javafx.scene.control.TableColumn.*;
import static org.junit.Assert.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
public class CellEditEventOfTableColumnTest {
private TableView<String> table;
private TableColumn<String, String> editingColumn;
@Test
public void testDefaultOnCommitHandlerTablePositionWithNullTable() {
String edited = "edited";
TablePosition<String, String> pos = new TablePosition<>(null, 1, editingColumn);
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
assertNull("table must be null for null pos", ev.getTableView());
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
TablePosition<String, String> pos = new TablePosition<>(null, 1, editingColumn);
CellEditEvent<String, String> ev = new CellEditEvent<>(table, pos, editAnyEvent(), editedValue);
assertNull("rowValue must be null for null pos", ev.getRowValue());
}
@Test
public void testNullTable() {
new CellEditEvent<Object, Object>(null,
new TablePosition<>(null, -1, null), editAnyEvent(), null);
}
@Test
public void testCellEditEventDifferentSource() {
assertCellEditEvent(new TableView<>());
}
@Test
public void testCellEditEventSameSource() {
assertCellEditEvent(table);
}
@Test
public void testCellEditEventNullSource() {
assertCellEditEvent(null);
}
private void assertCellEditEvent(TableView<String> source) {
int editingRow = 1;
String editedValue = "edited";
String rowValue = table.getItems().get(editingRow);
String oldValue = editingColumn.getCellData(editingRow);
TablePosition<String, String> pos = new TablePosition<>(table, editingRow, editingColumn);
CellEditEvent<String, String> event = new CellEditEvent<>(source, pos, editAnyEvent(), editedValue);
if (source != null) {
assertEquals(source, event.getSource());
}
assertCellEditEventState(event, table, editingColumn, pos, editedValue, oldValue, rowValue);
}
private <S, T> void assertCellEditEventState(CellEditEvent<S, T> event,
TableView<S> table, TableColumn<S, T> tableColumn, TablePosition<S, T> pos,
T newValue, T oldValue, S rowValue) {
assertEquals(newValue, event.getNewValue());
assertEquals(oldValue, event.getOldValue());
assertEquals(rowValue, event.getRowValue());
assertEquals(tableColumn, event.getTableColumn());
assertEquals(pos, event.getTablePosition());
assertEquals(table, event.getTableView());
}
@Before public void setup() {
Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
if (throwable instanceof RuntimeException) {
throw (RuntimeException)throwable;
} else {
Thread.currentThread().getThreadGroup().uncaughtException(thread, throwable);
}
});
ObservableList<String> model = FXCollections.observableArrayList("Four", "Five", "Fear");
table = new TableView<String>(model);
editingColumn = new TableColumn<>("TEST");
editingColumn.setCellValueFactory(e -> new SimpleStringProperty(e.getValue()));
table.getColumns().addAll(editingColumn);
}
@After
public void cleanup() {
Thread.currentThread().setUncaughtExceptionHandler(null);
}
}
