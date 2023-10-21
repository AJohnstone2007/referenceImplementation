package test.javafx.scene.input;
import com.sun.javafx.scene.input.KeyCodeMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import javafx.scene.Node;
import javafx.event.EventTarget;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.Test;
public class KeyEventTest {
private final Node node1 = new TestNode();
private final Node node2 = new TestNode();
public KeyEvent testKeyEvent(EventTarget target, String character, KeyCode code, boolean shiftDown, boolean controlDown, boolean altDown, boolean metaDown) {
return new KeyEvent(null, target, KeyEvent.KEY_PRESSED, character, null, code, shiftDown, controlDown, altDown, metaDown);
}
@Test
public void shouldCreateKeyTypedEvent() {
KeyEvent event = new KeyEvent(null, node1, KeyEvent.KEY_TYPED, "A", "A", KeyCodeMap.valueOf(0x41), true,
false, true, false);
assertSame(node1, event.getTarget());
assertEquals("A", event.getCharacter());
assertTrue(event.getText().isEmpty());
assertSame(KeyCode.UNDEFINED, event.getCode());
assertTrue(event.isShiftDown());
assertFalse(event.isControlDown());
assertTrue(event.isAltDown());
assertFalse(event.isMetaDown());
assertSame(KeyEvent.KEY_TYPED, event.getEventType());
}
@Test
public void shouldCreateKeyReleasedEvent() {
KeyEvent event = new KeyEvent(null, node1, KeyEvent.KEY_RELEASED, "A", "A", KeyCodeMap.valueOf(0x41), false,
true, false, true);
assertSame(node1, event.getTarget());
assertEquals(KeyEvent.CHAR_UNDEFINED, event.getCharacter());
assertEquals("A", event.getText());
assertSame(KeyCode.A, event.getCode());
assertFalse(event.isShiftDown());
assertTrue(event.isControlDown());
assertFalse(event.isAltDown());
assertTrue(event.isMetaDown());
assertSame(KeyEvent.KEY_RELEASED, event.getEventType());
}
@Test
public void shouldCopyKeyTypedEvent() {
KeyEvent original = new KeyEvent(null, node1, KeyEvent.KEY_TYPED, "A", "A", KeyCodeMap.valueOf(0x41), true,
false, false, false);
KeyEvent event = original.copyFor(null, node2);
assertSame(node2, event.getTarget());
assertEquals("A", event.getCharacter());
assertTrue(event.getText().isEmpty());
assertSame(KeyCode.UNDEFINED, event.getCode());
assertTrue(event.isShiftDown());
assertFalse(event.isControlDown());
assertFalse(event.isAltDown());
assertFalse(event.isMetaDown());
assertSame(KeyEvent.KEY_TYPED, event.getEventType());
}
@Test
public void shouldGetNonEmptyDescription() {
KeyEvent event1 = testKeyEvent(node1, "A", KeyCode.A,
false, false, false, false);
KeyEvent event2 = testKeyEvent(node1, "", KeyCode.UNDEFINED,
true, true, true, true);
String s1 = event1.toString();
String s2 = event2.toString();
assertNotNull(s1);
assertNotNull(s2);
assertFalse(s1.isEmpty());
assertFalse(s2.isEmpty());
}
}
