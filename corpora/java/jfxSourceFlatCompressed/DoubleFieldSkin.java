package com.sun.javafx.scene.control.skin;
import com.sun.javafx.scene.control.DoubleField;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.scene.Node;
public class DoubleFieldSkin extends InputFieldSkin {
private InvalidationListener doubleFieldValueListener;
public DoubleFieldSkin(final DoubleField control) {
super(control);
control.valueProperty().addListener(doubleFieldValueListener = observable -> {
updateText();
});
}
@Override public DoubleField getSkinnable() {
return (DoubleField) control;
}
@Override public Node getNode() {
return getTextField();
}
@Override
public void dispose() {
((DoubleField) control).valueProperty().removeListener(doubleFieldValueListener);
super.dispose();
}
protected boolean accept(String text) {
if (text.length() == 0) return true;
if (text.matches("[0-9\\.]*")) {
try {
Double.parseDouble(text);
return true;
} catch (NumberFormatException ex) { }
}
return false;
}
protected void updateText() {
getTextField().setText("" + ((DoubleField) control).getValue());
}
protected void updateValue() {
double value = ((DoubleField) control).getValue();
double newValue;
String text = getTextField().getText() == null ? "" : getTextField().getText().trim();
try {
newValue = Double.parseDouble(text);
if (newValue != value) {
((DoubleField) control).setValue(newValue);
}
} catch (NumberFormatException ex) {
((DoubleField) control).setValue(0);
Platform.runLater(() -> {
getTextField().positionCaret(1);
});
}
}
}
