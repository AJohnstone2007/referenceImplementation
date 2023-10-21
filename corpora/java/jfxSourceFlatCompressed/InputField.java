package com.sun.javafx.scene.control;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
public abstract class InputField extends Control {
public static final int DEFAULT_PREF_COLUMN_COUNT = 12;
private BooleanProperty editable = new SimpleBooleanProperty(this, "editable", true);
public final boolean isEditable() { return editable.getValue(); }
public final void setEditable(boolean value) { editable.setValue(value); }
public final BooleanProperty editableProperty() { return editable; }
private StringProperty promptText = new StringPropertyBase("") {
@Override protected void invalidated() {
String txt = get();
if (txt != null && txt.contains("\n")) {
txt = txt.replace("\n", "");
set(txt);
}
}
@Override public Object getBean() { return InputField.this; }
@Override public String getName() { return "promptText"; }
};
public final StringProperty promptTextProperty() { return promptText; }
public final String getPromptText() { return promptText.get(); }
public final void setPromptText(String value) { promptText.set(value); }
private IntegerProperty prefColumnCount = new IntegerPropertyBase(DEFAULT_PREF_COLUMN_COUNT) {
private int oldValue = get();
@Override
protected void invalidated() {
int value = get();
if (value < 0) {
if (isBound()) {
unbind();
}
set(oldValue);
throw new IllegalArgumentException("value cannot be negative.");
}
oldValue = value;
}
@Override public Object getBean() { return InputField.this; }
@Override public String getName() { return "prefColumnCount"; }
};
public final IntegerProperty prefColumnCountProperty() { return prefColumnCount; }
public final int getPrefColumnCount() { return prefColumnCount.getValue(); }
public final void setPrefColumnCount(int value) { prefColumnCount.setValue(value); }
private ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<EventHandler<ActionEvent>>() {
@Override protected void invalidated() {
setEventHandler(ActionEvent.ACTION, get());
}
@Override public Object getBean() { return InputField.this; }
@Override public String getName() { return "onAction"; }
};
public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() { return onAction; }
public final EventHandler<ActionEvent> getOnAction() { return onActionProperty().get(); }
public final void setOnAction(EventHandler<ActionEvent> value) { onActionProperty().set(value); }
public InputField() {
getStyleClass().setAll("input-field");
}
}
