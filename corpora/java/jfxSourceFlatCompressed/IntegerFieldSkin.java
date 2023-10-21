package com.sun.javafx.scene.control.skin;
import com.sun.javafx.scene.control.IntegerField;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.scene.Node;
public class IntegerFieldSkin extends InputFieldSkin {
private InvalidationListener integerFieldValueListener;
public IntegerFieldSkin(final IntegerField control) {
super(control);
control.valueProperty().addListener(integerFieldValueListener = observable -> {
updateText();
});
}
@Override public IntegerField getSkinnable() {
return (IntegerField) control;
}
@Override public Node getNode() {
return getTextField();
}
@Override
public void dispose() {
((IntegerField) control).valueProperty().removeListener(integerFieldValueListener);
super.dispose();
}
@Override protected boolean accept(String text) {
if (text.length() == 0) return true;
if (text.matches("[0-9]*")) {
try {
Integer.parseInt(text);
int value = Integer.parseInt(text);
int maxValue = ((IntegerField) control).getMaxValue();
return (maxValue != -1) ? (value <= maxValue ) : true;
} catch (NumberFormatException ex) { }
}
return false;
}
@Override protected void updateText() {
getTextField().setText("" + ((IntegerField) control).getValue());
}
@Override protected void updateValue() {
int value = ((IntegerField) control).getValue();
int newValue;
String text = getTextField().getText() == null ? "" : getTextField().getText().trim();
try {
newValue = Integer.parseInt(text);
if (newValue != value) {
((IntegerField) control).setValue(newValue);
}
} catch (NumberFormatException ex) {
((IntegerField) control).setValue(0);
Platform.runLater(() -> {
getTextField().positionCaret(1);
});
}
}
}
