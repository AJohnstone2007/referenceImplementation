package com.sun.javafx.scene.control.skin;
import java.util.Locale;
import com.sun.javafx.scene.control.WebColorField;
import javafx.beans.InvalidationListener;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.skin.ColorPickerSkin;
import javafx.scene.paint.Color;
public class WebColorFieldSkin extends InputFieldSkin {
private InvalidationListener integerFieldValueListener;
private boolean noChangeInValue = false;
public WebColorFieldSkin(final WebColorField control) {
super(control);
control.valueProperty().addListener(integerFieldValueListener = observable -> {
updateText();
});
getTextField().setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
}
@Override public WebColorField getSkinnable() {
return (WebColorField) control;
}
@Override public Node getNode() {
return getTextField();
}
@Override public void dispose() {
((WebColorField) control).valueProperty().removeListener(integerFieldValueListener);
super.dispose();
}
protected boolean accept(String text) {
if (text.length() == 0) return true;
if (text.matches("#[a-fA-F0-9]{0,6}") || text.matches("[a-fA-F0-9]{0,6}")) {
return true;
}
return false;
}
protected void updateText() {
Color color = ((WebColorField) control).getValue();
if (color == null) color = Color.BLACK;
getTextField().setText(Utils.formatHexString(color));
}
protected void updateValue() {
if (noChangeInValue) return;
Color value = ((WebColorField) control).getValue();
String text = getTextField().getText() == null ? "" : getTextField().getText().trim().toUpperCase(Locale.ROOT);
if (text.matches("#[A-F0-9]{6}") || text.matches("[A-F0-9]{6}")) {
try {
Color newValue = (text.charAt(0) == '#')? Color.web(text) : Color.web("#"+text);
if (!newValue.equals(value)) {
((WebColorField) control).setValue(newValue);
} else {
noChangeInValue = true;
getTextField().setText(Utils.formatHexString(newValue));
noChangeInValue = false;
}
} catch (java.lang.IllegalArgumentException ex) {
System.out.println("Failed to parse ["+text+"]");
}
}
}
}
