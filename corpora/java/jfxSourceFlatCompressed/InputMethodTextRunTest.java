package test.javafx.scene.input;
import javafx.scene.input.InputMethodHighlight;
import javafx.scene.input.InputMethodTextRun;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import org.junit.Test;
public class InputMethodTextRunTest {
private final InputMethodTextRun imtr =
new InputMethodTextRun("Text",
InputMethodHighlight.SELECTED_RAW);
@Test
public void shouldCreateInputMethodTextRun() {
assertEquals("Text", imtr.getText());
assertSame(InputMethodHighlight.SELECTED_RAW, imtr.getHighlight());
}
@Test
public void shouldGetNonEmptyDescription() {
String s = imtr.toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
}
