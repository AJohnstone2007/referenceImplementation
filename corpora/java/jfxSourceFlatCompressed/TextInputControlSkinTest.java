package test.javafx.scene.control.skin;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.geometry.Point2D;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.skin.TextAreaSkin;
import javafx.scene.control.skin.TextFieldSkin;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
public class TextInputControlSkinTest {
@Test public void caretStopsAnimatingWhenTextFieldLosesFocus() {
final AtomicBoolean caretAnimating = new AtomicBoolean(false);
FocusableTextField textField = new FocusableTextField();
TextFieldSkin skin = new TextFieldSkin(textField) {
@Override public void setCaretAnimating(boolean value) {
caretAnimating.set(value);
super.setCaretAnimating(value);
}
};
textField.setSkin(skin);
textField.setFocus(true);
assertTrue(caretAnimating.get());
textField.setFocus(false);
assertFalse(caretAnimating.get());
}
@Test public void caretStopsAnimatingWhenTextAreaLosesFocus() {
final AtomicBoolean caretAnimating = new AtomicBoolean(false);
FocusableTextArea textArea = new FocusableTextArea();
TextAreaSkin skin = new TextAreaSkin(textArea) {
@Override public void setCaretAnimating(boolean value) {
caretAnimating.set(value);
super.setCaretAnimating(value);
}
};
textArea.setSkin(skin);
textArea.setFocus(true);
assertTrue(caretAnimating.get());
textArea.setFocus(false);
assertFalse(caretAnimating.get());
}
@Test public void skinsCanHandleNullValues_RT34178() {
TextArea textArea = new TextArea();
textArea.setSkin(new TextAreaSkin(textArea));
textArea.setText(null);
TextField textField = new TextField();
textField.setSkin(new TextFieldSkin(textField));
textField.setText(null);
PasswordField passwordField = new PasswordField();
passwordField.setSkin(new TextFieldSkin(passwordField));
passwordField.setText(null);
}
@Test public void noNullPointerIfTextInputNotInScene() {
TextField textField = new TextField();
TextFieldSkin skin = new TextFieldSkin(textField);
textField.setSkin(skin);
Point2D point = textField.getInputMethodRequests().getTextLocation(0);
assertEquals(new Point2D(0, 0), point);
}
public class FocusableTextField extends TextField {
public void setFocus(boolean value) {
super.setFocused(value);
}
}
public class FocusableTextArea extends TextArea {
public void setFocus(boolean value) {
super.setFocused(value);
}
}
}
