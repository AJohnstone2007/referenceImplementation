package javafx.scene.control.skin;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
public class TextFieldSkinAndroid extends TextFieldSkin {
private static final char BULLET = '\u2022';
private final EventHandler<MouseEvent> mouseEventListener = e -> {
if (getSkinnable().isEditable() && getSkinnable().isFocused()) {
showSoftwareKeyboard();
}
};
private final ChangeListener<Boolean> focusChangeListener = (observable, wasFocused, isFocused) -> {
if (wasFocused && !isFocused) {
hideSoftwareKeyboard();
}
};
private final WeakChangeListener<Boolean> weakFocusChangeListener = new WeakChangeListener<>(focusChangeListener);
public TextFieldSkinAndroid(final TextField textField) {
super(textField);
textField.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventListener);
textField.focusedProperty().addListener(weakFocusChangeListener);
}
@Override protected String maskText(String txt) {
if (getSkinnable() instanceof PasswordField) {
return String.valueOf(BULLET).repeat(txt.length());
} else {
return super.maskText(txt);
}
}
@Override public void dispose() {
if (getSkinnable() == null) return;
getSkinnable().removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventListener);
getSkinnable().focusedProperty().removeListener(weakFocusChangeListener);
super.dispose();
}
native void showSoftwareKeyboard();
native void hideSoftwareKeyboard();
}
