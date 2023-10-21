package test.javafx.scene.control;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextInputControlShim;
import static test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
public class PasswordFieldTest {
private PasswordField pwdField;
@Before public void setup() {
pwdField = new PasswordField();
}
@Test public void defaultConstructorShouldHaveEmptyString() {
assertEquals("", pwdField.getText());
}
@Test public void checkCut() {
pwdField.setText("sample");
pwdField.selectRange(0, pwdField.getLength());
pwdField.cut();
assertNotNull(pwdField.getText());
assertTrue(pwdField.getLength() != 0);
assertEquals(pwdField.getText().toString(), TextInputControlShim.getContent_get(pwdField, 0, pwdField.getLength()));
}
@Test public void checkCopy() {
pwdField.setText("sample");
pwdField.selectRange(0, pwdField.getLength());
pwdField.copy();
assertNotNull(pwdField.getText());
assertTrue(pwdField.getLength() != 0);
assertEquals(pwdField.getText().toString(), TextInputControlShim.getContent_get(pwdField, 0, pwdField.getLength()));
}
@Test public void defaultConstructorShouldSetStyleClassTo_passwordfield() {
assertStyleClassContains(pwdField, "password-field");
}
@Test public void lengthMatchesStringLengthExcludingControlCharacters() {
final String string = "Hello\n";
pwdField.setText(string);
assertEquals(string.length()-1, pwdField.getLength());
}
}
