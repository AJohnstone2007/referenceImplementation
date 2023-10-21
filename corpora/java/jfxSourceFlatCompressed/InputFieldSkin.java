package com.sun.javafx.scene.control.skin;
import com.sun.javafx.event.EventDispatchChainImpl;
import com.sun.javafx.scene.control.InputField;
import javafx.beans.InvalidationListener;
import javafx.event.EventDispatchChain;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
public abstract class InputFieldSkin implements Skin<InputField> {
protected InputField control;
private InnerTextField textField;
private InvalidationListener InputFieldFocusListener;
private InvalidationListener InputFieldStyleClassListener;
public InputFieldSkin(final InputField control) {
this.control = control;
textField = new InnerTextField() {
@Override public void replaceText(int start, int end, String text) {
String t = textField.getText() == null ? "" : textField.getText();
t = t.substring(0, start) + text + t.substring(end);
if (accept(t)) {
super.replaceText(start, end, text);
}
}
@Override public void replaceSelection(String text) {
String t = textField.getText() == null ? "" : textField.getText();
int start = Math.min(textField.getAnchor(), textField.getCaretPosition());
int end = Math.max(textField.getAnchor(), textField.getCaretPosition());
t = t.substring(0, start) + text + t.substring(end);
if (accept(t)) {
super.replaceSelection(text);
}
}
};
textField.setId("input-text-field");
textField.setFocusTraversable(false);
control.getStyleClass().addAll(textField.getStyleClass());
textField.getStyleClass().setAll(control.getStyleClass());
control.getStyleClass().addListener(InputFieldStyleClassListener = observable -> {
textField.getStyleClass().setAll(control.getStyleClass());
});
textField.promptTextProperty().bind(control.promptTextProperty());
textField.prefColumnCountProperty().bind(control.prefColumnCountProperty());
textField.textProperty().addListener(observable -> {
updateValue();
});
control.focusedProperty().addListener(InputFieldFocusListener = observable -> {
textField.handleFocus(control.isFocused());
});
updateText();
}
@Override public InputField getSkinnable() {
return control;
}
@Override public Node getNode() {
return textField;
}
@Override
public void dispose() {
control.getStyleClass().removeListener(InputFieldStyleClassListener);
control.focusedProperty().removeListener(InputFieldFocusListener);
textField = null;
}
protected abstract boolean accept(String text);
protected abstract void updateText();
protected abstract void updateValue();
protected TextField getTextField() {
return textField;
}
private class InnerTextField extends TextField {
public void handleFocus(boolean b) {
setFocused(b);
}
@Override public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
EventDispatchChain chain = new EventDispatchChainImpl();
chain.append(textField.getEventDispatcher());
return chain;
}
}
}
