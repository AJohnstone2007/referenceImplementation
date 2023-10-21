package test.javafx.scene.input;
import com.sun.javafx.scene.input.KeyCodeMap;
import javafx.scene.input.KeyCode;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import org.junit.Test;
public class KeyCodeTest {
@Test
public void shouldGetCharacter() {
for (KeyCode code : KeyCode.values()) {
String chr = code.getChar();
assertNotNull(chr);
assertFalse(chr.isEmpty());
}
}
@Test
public void shouldFindCorrectCode() {
for (KeyCode code : KeyCode.values()) {
assertSame(code, KeyCodeMap.valueOf(code.getCode()));
}
}
}
