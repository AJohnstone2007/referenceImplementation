package test.javafx.scene.input;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodTextRun;
import org.junit.Test;
public class InputMethodEventTest {
private final Node node1 = new TestNode();
private final Node node2 = new TestNode();
private final ObservableList<InputMethodTextRun> observableArrayList =
javafx.collections.FXCollections.<InputMethodTextRun>observableArrayList();
private final InputMethodEvent event =
new InputMethodEvent(null, node1, InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, observableArrayList, "Text", 2);
@Test
public void shouldCreateInputMethodEvent() {
assertSame(node1, event.getTarget());
assertEquals(observableArrayList, event.getComposed());
assertEquals("Text", event.getCommitted());
assertEquals(2, event.getCaretPosition());
}
@Test
public void shouldCopyInputMethodEvent() {
InputMethodEvent copied = event.copyFor(null, node2);
assertSame(node2, copied.getTarget());
assertEquals(observableArrayList, copied.getComposed());
assertEquals("Text", copied.getCommitted());
assertEquals(2, copied.getCaretPosition());
}
@Test
public void shouldGetNonEmptyDescription() {
String s = event.toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
}
