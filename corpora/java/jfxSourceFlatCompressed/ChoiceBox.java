package javafx.scene.control;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.util.StringConverter;
import javafx.css.PseudoClass;
import javafx.scene.control.skin.ChoiceBoxSkin;
import javafx.beans.DefaultProperty;
@DefaultProperty("items")
public class ChoiceBox<T> extends Control {
public static final EventType<Event> ON_SHOWING =
new EventType<Event>(Event.ANY, "CHOICE_BOX_ON_SHOWING");
public static final EventType<Event> ON_SHOWN =
new EventType<Event>(Event.ANY, "CHOICE_BOX_ON_SHOWN");
public static final EventType<Event> ON_HIDING =
new EventType<Event>(Event.ANY, "CHOICE_BOX_ON_HIDING");
public static final EventType<Event> ON_HIDDEN =
new EventType<Event>(Event.ANY, "CHOICE_BOX_ON_HIDDEN");
public ChoiceBox() {
this(FXCollections.<T>observableArrayList());
}
public ChoiceBox(ObservableList<T> items) {
getStyleClass().setAll("choice-box");
setAccessibleRole(AccessibleRole.COMBO_BOX);
setItems(items);
setSelectionModel(new ChoiceBoxSelectionModel<T>(this));
valueProperty().addListener((ov, t, t1) -> {
if (getItems() == null) return;
SingleSelectionModel<T> sm = getSelectionModel();
if (sm == null) return;
int index = getItems().indexOf(t1);
if (index > -1) {
sm.select(index);
}
});
}
private ObjectProperty<SingleSelectionModel<T>> selectionModel =
new SimpleObjectProperty<SingleSelectionModel<T>>(this, "selectionModel") {
private SelectionModel<T> oldSM = null;
@Override protected void invalidated() {
if (oldSM != null) {
oldSM.selectedItemProperty().removeListener(selectedItemListener);
}
SelectionModel<T> sm = get();
oldSM = sm;
if (sm != null) {
sm.selectedItemProperty().addListener(selectedItemListener);
if (!valueProperty().isBound()) {
ChoiceBox.this.setValue(sm.getSelectedItem());
}
}
}
};
private ChangeListener<T> selectedItemListener = (ov, t, t1) -> {
if (! valueProperty().isBound()) {
setValue(t1);
}
};
public final void setSelectionModel(SingleSelectionModel<T> value) { selectionModel.set(value); }
public final SingleSelectionModel<T> getSelectionModel() { return selectionModel.get(); }
public final ObjectProperty<SingleSelectionModel<T>> selectionModelProperty() { return selectionModel; }
private ReadOnlyBooleanWrapper showing = new ReadOnlyBooleanWrapper() {
@Override protected void invalidated() {
pseudoClassStateChanged(SHOWING_PSEUDOCLASS_STATE, get());
notifyAccessibleAttributeChanged(AccessibleAttribute.EXPANDED);
}
@Override
public Object getBean() {
return ChoiceBox.this;
}
@Override
public String getName() {
return "showing";
}
};
public final boolean isShowing() { return showing.get(); }
public final ReadOnlyBooleanProperty showingProperty() { return showing.getReadOnlyProperty(); }
private void setShowing(boolean value) {
Event.fireEvent(this, value ? new Event(ON_SHOWING) :
new Event(ON_HIDING));
showing.set(value);
Event.fireEvent(this, value ? new Event(ON_SHOWN) :
new Event(ON_HIDDEN));
}
private ObjectProperty<ObservableList<T>> items = new ObjectPropertyBase<ObservableList<T>>() {
ObservableList<T> old;
@Override protected void invalidated() {
final ObservableList<T> newItems = get();
if (old != newItems) {
if (old != null) old.removeListener(itemsListener);
if (newItems != null) newItems.addListener(itemsListener);
final SingleSelectionModel<T> sm = getSelectionModel();
if (sm != null) {
if (newItems != null && newItems.isEmpty()) {
sm.clearSelection();
} else if (sm.getSelectedIndex() == -1 && sm.getSelectedItem() != null) {
int newIndex = getItems().indexOf(sm.getSelectedItem());
if (newIndex != -1) {
sm.setSelectedIndex(newIndex);
}
} else sm.clearSelection();
}
old = newItems;
}
}
@Override
public Object getBean() {
return ChoiceBox.this;
}
@Override
public String getName() {
return "items";
}
};
public final void setItems(ObservableList<T> value) { items.set(value); }
public final ObservableList<T> getItems() { return items.get(); }
public final ObjectProperty<ObservableList<T>> itemsProperty() { return items; }
private final ListChangeListener<T> itemsListener = c -> {
final SingleSelectionModel<T> sm = getSelectionModel();
if (sm!= null) {
if (getItems() == null || getItems().isEmpty()) {
sm.clearSelection();
} else {
int newIndex = getItems().indexOf(sm.getSelectedItem());
sm.setSelectedIndex(newIndex);
}
}
if (sm != null) {
final T selectedItem = sm.getSelectedItem();
while (c.next()) {
if (selectedItem != null && c.getRemoved().contains(selectedItem)) {
sm.clearSelection();
break;
}
}
}
};
public ObjectProperty<StringConverter<T>> converterProperty() { return converter; }
private ObjectProperty<StringConverter<T>> converter =
new SimpleObjectProperty<StringConverter<T>>(this, "converter", null);
public final void setConverter(StringConverter<T> value) { converterProperty().set(value); }
public final StringConverter<T> getConverter() {return converterProperty().get(); }
public ObjectProperty<T> valueProperty() { return value; }
private ObjectProperty<T> value = new SimpleObjectProperty<T>(this, "value") {
@Override protected void invalidated() {
super.invalidated();
fireEvent(new ActionEvent());
final SingleSelectionModel<T> sm = getSelectionModel();
if (sm != null) {
sm.select(super.getValue());
}
notifyAccessibleAttributeChanged(AccessibleAttribute.TEXT);
}
};
public final void setValue(T value) { valueProperty().set(value); }
public final T getValue() { return valueProperty().get(); }
public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() { return onAction; }
public final void setOnAction(EventHandler<ActionEvent> value) { onActionProperty().set(value); }
public final EventHandler<ActionEvent> getOnAction() { return onActionProperty().get(); }
private ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<EventHandler<ActionEvent>>() {
@Override protected void invalidated() {
setEventHandler(ActionEvent.ACTION, get());
}
@Override
public Object getBean() {
return ChoiceBox.this;
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
return ChoiceBox.this;
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
return ChoiceBox.this;
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
return ChoiceBox.this;
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
return ChoiceBox.this;
}
@Override public String getName() {
return "onHidden";
}
};
public void show() {
if (!isDisabled()) setShowing(true);
}
public void hide() {
setShowing(false);
}
@Override protected Skin<?> createDefaultSkin() {
return new ChoiceBoxSkin<T>(this);
}
private static final PseudoClass SHOWING_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("showing");
static class ChoiceBoxSelectionModel<T> extends SingleSelectionModel<T> {
private final ChoiceBox<T> choiceBox;
private ChangeListener<ObservableList<T>> itemsObserver;
private ListChangeListener<T> itemsContentObserver;
private WeakListChangeListener<T> weakItemsContentObserver;
public ChoiceBoxSelectionModel(final ChoiceBox<T> cb) {
if (cb == null) {
throw new NullPointerException("ChoiceBox can not be null");
}
this.choiceBox = cb;
itemsContentObserver = c -> {
if (choiceBox.getItems() == null || choiceBox.getItems().isEmpty()) {
setSelectedIndex(-1);
} else if (getSelectedIndex() == -1 && getSelectedItem() != null) {
int newIndex = choiceBox.getItems().indexOf(getSelectedItem());
if (newIndex != -1) {
setSelectedIndex(newIndex);
}
}
};
weakItemsContentObserver = new WeakListChangeListener<>(itemsContentObserver);
if (this.choiceBox.getItems() != null) {
this.choiceBox.getItems().addListener(weakItemsContentObserver);
}
itemsObserver = (valueModel, oldList, newList) -> {
if (oldList != null) {
oldList.removeListener(weakItemsContentObserver);
}
if (newList != null) {
newList.addListener(weakItemsContentObserver);
}
setSelectedIndex(-1);
if (getSelectedItem() != null) {
int newIndex = choiceBox.getItems().indexOf(getSelectedItem());
if (newIndex != -1) {
setSelectedIndex(newIndex);
}
}
};
this.choiceBox.itemsProperty().addListener(new WeakChangeListener<>(itemsObserver));
}
@Override protected T getModelItem(int index) {
final ObservableList<T> items = choiceBox.getItems();
if (items == null) return null;
if (index < 0 || index >= items.size()) return null;
return items.get(index);
}
@Override protected int getItemCount() {
final ObservableList<T> items = choiceBox.getItems();
return items == null ? 0 : items.size();
}
@Override public void select(int index) {
super.select(index);
if (choiceBox.isShowing()) {
choiceBox.hide();
}
}
@Override
public void select(T obj) {
super.select(obj);
if (obj != null && !choiceBox.getItems().contains(obj)) {
setSelectedIndex(-1);
}
}
@Override public void selectPrevious() {
int index = getSelectedIndex() - 1;
while (index >= 0) {
final T value = getModelItem(index);
if (value instanceof Separator) {
index--;
} else {
select(index);
break;
}
}
}
@Override public void selectNext() {
int index = getSelectedIndex() + 1;
while (index < getItemCount()) {
final T value = getModelItem(index);
if (value instanceof Separator) {
index++;
} else {
select(index);
break;
}
}
}
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch(attribute) {
case TEXT:
String accText = getAccessibleText();
if (accText != null && !accText.isEmpty()) return accText;
Object title = super.queryAccessibleAttribute(attribute, parameters);
if (title != null) return title;
StringConverter<T> converter = getConverter();
if (converter == null) {
return getValue() != null ? getValue().toString() : "";
}
return converter.toString(getValue());
case EXPANDED: return isShowing();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
@Override
public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case COLLAPSE: hide(); break;
case EXPAND: show(); break;
default: super.executeAccessibleAction(action); break;
}
}
}
