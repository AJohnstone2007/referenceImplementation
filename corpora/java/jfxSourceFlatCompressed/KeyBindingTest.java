package test.com.sun.javafx.scene.control.inputmap;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.Test;
import static org.junit.Assert.*;
public class KeyBindingTest {
@Test public void getSpecificity() {
final KeyCode code = KeyCode.ENTER;
int expect = 5;
KeyBinding uut = new KeyBinding(code).shift().ctrl(KeyBinding.OptionalBoolean.ANY);
KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, null,
null, code, true, false, false, false);
assertEquals(expect, uut.getSpecificity(event));
uut = new KeyBinding(code).shift(KeyBinding.OptionalBoolean.ANY).ctrl();
event = new KeyEvent(KeyEvent.KEY_PRESSED, null,
null, code, false, true, false, false);
assertEquals(expect, uut.getSpecificity(event));
}
}
