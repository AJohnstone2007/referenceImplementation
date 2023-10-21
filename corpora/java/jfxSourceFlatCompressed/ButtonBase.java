package javafx.scene.control;
import javafx.css.PseudoClass;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.AccessibleAction;
import javafx.scene.Node;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
public abstract class ButtonBase extends Labeled {
public ButtonBase() { }
public ButtonBase(String text) {
super(text);
}
public ButtonBase(String text, Node graphic) {
super(text, graphic);
}
public final ReadOnlyBooleanProperty armedProperty() { return armed.getReadOnlyProperty(); }
private void setArmed(boolean value) { armed.set(value); }
public final boolean isArmed() { return armedProperty().get(); }
private ReadOnlyBooleanWrapper armed = new ReadOnlyBooleanWrapper() {
@Override protected void invalidated() {
pseudoClassStateChanged(ARMED_PSEUDOCLASS_STATE, get());
}
@Override
public Object getBean() {
return ButtonBase.this;
}
@Override
public String getName() {
return "armed";
}
};
public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() { return onAction; }
public final void setOnAction(EventHandler<ActionEvent> value) { onActionProperty().set(value); }
public final EventHandler<ActionEvent> getOnAction() { return onActionProperty().get(); }
private ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<EventHandler<ActionEvent>>() {
@Override protected void invalidated() {
setEventHandler(ActionEvent.ACTION, get());
}
@Override
public Object getBean() {
return ButtonBase.this;
}
@Override
public String getName() {
return "onAction";
}
};
public void arm() {
setArmed(true);
}
public void disarm() {
setArmed(false);
}
public abstract void fire();
private static final PseudoClass ARMED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("armed");
@Override public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case FIRE:
fire();
break;
default: super.executeAccessibleAction(action);
}
}
}
