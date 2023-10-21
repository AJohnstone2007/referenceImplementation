package javafx.scene.control.skin;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
public class TextAreaSkinIos extends TextAreaSkin {
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
public TextAreaSkinIos(final TextArea textArea) {
super(textArea);
textArea.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventListener);
textArea.focusedProperty().addListener(weakFocusChangeListener);
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
