package javafx.scene.control;
import javafx.collections.MapChangeListener;
import javafx.css.PseudoClass;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
public abstract class ComboBoxBase<T> extends Control {
public static final EventType<Event> ON_SHOWING =
new EventType<Event>(Event.ANY, "COMBO_BOX_BASE_ON_SHOWING");
public static final EventType<Event> ON_SHOWN =
new EventType<Event>(Event.ANY, "COMBO_BOX_BASE_ON_SHOWN");
public static final EventType<Event> ON_HIDING =
new EventType<Event>(Event.ANY, "COMBO_BOX_BASE_ON_HIDING");
public static final EventType<Event> ON_HIDDEN =
new EventType<Event>(Event.ANY, "COMBO_BOX_BASE_ON_HIDDEN");
public ComboBoxBase() {
getStyleClass().add(DEFAULT_STYLE_CLASS);
getProperties().addListener((MapChangeListener<Object, Object>) change -> {
if (change.wasAdded()) {
if (change.getKey() == "FOCUSED") {
setFocused((Boolean)change.getValueAdded());
getProperties().remove("FOCUSED");
}
}
});
}
public ObjectProperty<T> valueProperty() { return value; }
private ObjectProperty<T> value = new SimpleObjectProperty<T>(this, "value");
public final void setValue(T value) { valueProperty().set(value); }
public final T getValue() { return valueProperty().get(); }
public BooleanProperty editableProperty() { return editable; }
public final void setEditable(boolean value) { editableProperty().set(value); }
public final boolean isEditable() { return editableProperty().get(); }
private BooleanProperty editable = new SimpleBooleanProperty(this, "editable", false) {
@Override protected void invalidated() {
pseudoClassStateChanged(PSEUDO_CLASS_EDITABLE, get());
}
};
private ReadOnlyBooleanWrapper showing;
public ReadOnlyBooleanProperty showingProperty() { return showingPropertyImpl().getReadOnlyProperty(); }
public final boolean isShowing() { return showingPropertyImpl().get(); }
private void setShowing(boolean value) {
Event.fireEvent(this, value ? new Event(ComboBoxBase.ON_SHOWING) :
new Event(ComboBoxBase.ON_HIDING));
showingPropertyImpl().set(value);
Event.fireEvent(this, value ? new Event(ComboBoxBase.ON_SHOWN) :
new Event(ComboBoxBase.ON_HIDDEN));
}
private ReadOnlyBooleanWrapper showingPropertyImpl() {
if (showing == null) {
showing = new ReadOnlyBooleanWrapper(false) {
@Override protected void invalidated() {
pseudoClassStateChanged(PSEUDO_CLASS_SHOWING, get());
notifyAccessibleAttributeChanged(AccessibleAttribute.EXPANDED);
}
@Override
public Object getBean() {
return ComboBoxBase.this;
}
@Override
public String getName() {
return "showing";
}
};
}
return showing;
}
private StringProperty promptText = new SimpleStringProperty(this, "promptText", null) {
@Override protected void invalidated() {
String txt = get();
if (txt != null && txt.contains("\n")) {
txt = txt.replace("\n", "");
set(txt);
}
}
};
public final StringProperty promptTextProperty() { return promptText; }
public final String getPromptText() { return promptText.get(); }
public final void setPromptText(String value) { promptText.set(value); }
public BooleanProperty armedProperty() { return armed; }
private final void setArmed(boolean value) { armedProperty().set(value); }
public final boolean isArmed() { return armedProperty().get(); }
private BooleanProperty armed = new SimpleBooleanProperty(this, "armed", false) {
@Override protected void invalidated() {
pseudoClassStateChanged(PSEUDO_CLASS_ARMED, get());
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
return ComboBoxBase.this;
}
@Override
public String getName() {
return "onAction";
}
};
public final ObjectProperty<EventHandler<Event>> onShowingProperty() { return onShowing; }
public final void setOnShowing(EventHandler<Event> value) { onShowingProperty().set(value); }
public final EventHandler<Event> getOnShowing() { return onShowingProperty().get(); }
private ObjectProperty<EventHandler<Event>> onShowing = new ObjectPropertyBase<EventHandler<Event>>() {
@Override protected void invalidated() {
setEventHandler(ON_SHOWING, get());
}
@Override public Object getBean() {
return ComboBoxBase.this;
}
@Override public String getName() {
return "onShowing";
}
};
public final ObjectProperty<EventHandler<Event>> onShownProperty() { return onShown; }
public final void setOnShown(EventHandler<Event> value) { onShownProperty().set(value); }
public final EventHandler<Event> getOnShown() { return onShownProperty().get(); }
private ObjectProperty<EventHandler<Event>> onShown = new ObjectPropertyBase<EventHandler<Event>>() {
@Override protected void invalidated() {
setEventHandler(ON_SHOWN, get());
}
@Override public Object getBean() {
return ComboBoxBase.this;
}
@Override public String getName() {
return "onShown";
}
};
public final ObjectProperty<EventHandler<Event>> onHidingProperty() { return onHiding; }
public final void setOnHiding(EventHandler<Event> value) { onHidingProperty().set(value); }
public final EventHandler<Event> getOnHiding() { return onHidingProperty().get(); }
private ObjectProperty<EventHandler<Event>> onHiding = new ObjectPropertyBase<EventHandler<Event>>() {
@Override protected void invalidated() {
setEventHandler(ON_HIDING, get());
}
@Override public Object getBean() {
return ComboBoxBase.this;
}
@Override public String getName() {
return "onHiding";
}
};
public final ObjectProperty<EventHandler<Event>> onHiddenProperty() { return onHidden; }
public final void setOnHidden(EventHandler<Event> value) { onHiddenProperty().set(value); }
public final EventHandler<Event> getOnHidden() { return onHiddenProperty().get(); }
private ObjectProperty<EventHandler<Event>> onHidden = new ObjectPropertyBase<EventHandler<Event>>() {
@Override protected void invalidated() {
setEventHandler(ON_HIDDEN, get());
}
@Override public Object getBean() {
return ComboBoxBase.this;
}
@Override public String getName() {
return "onHidden";
}
};
public void show() {
if (!isDisabled()) {
setShowing(true);
}
}
public void hide() {
if (isShowing()) {
setShowing(false);
}
}
public void arm() {
if (! armedProperty().isBound()) {
setArmed(true);
}
}
public void disarm() {
if (! armedProperty().isBound()) {
setArmed(false);
}
}
private static final String DEFAULT_STYLE_CLASS = "combo-box-base";
private static final PseudoClass PSEUDO_CLASS_EDITABLE =
PseudoClass.getPseudoClass("editable");
private static final PseudoClass PSEUDO_CLASS_SHOWING =
PseudoClass.getPseudoClass("showing");
private static final PseudoClass PSEUDO_CLASS_ARMED =
PseudoClass.getPseudoClass("armed");
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case EXPANDED: return isShowing();
case EDITABLE: return isEditable();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
@Override
public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case EXPAND: show(); break;
case COLLAPSE: hide(); break;
default: super.executeAccessibleAction(action); break;
}
}
}
