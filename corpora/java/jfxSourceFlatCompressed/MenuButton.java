package javafx.scene.control;
import javafx.css.PseudoClass;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Side;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.skin.MenuButtonSkin;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
public class MenuButton extends ButtonBase {
public static final EventType<Event> ON_SHOWING =
new EventType<Event>(Event.ANY, "MENU_BUTTON_ON_SHOWING");
public static final EventType<Event> ON_SHOWN =
new EventType<Event>(Event.ANY, "MENU_BUTTON_ON_SHOWN");
public static final EventType<Event> ON_HIDING =
new EventType<Event>(Event.ANY, "MENU_BUTTON_ON_HIDING");
public static final EventType<Event> ON_HIDDEN =
new EventType<Event>(Event.ANY, "MENU_BUTTON_ON_HIDDEN");
public MenuButton() {
this(null, null);
}
public MenuButton(String text) {
this(text, null);
}
public MenuButton(String text, Node graphic) {
this(text, graphic, (MenuItem[])null);
}
public MenuButton(String text, Node graphic, MenuItem... items) {
if (text != null) {
setText(text);
}
if (graphic != null) {
setGraphic(graphic);
}
if (items != null) {
getItems().addAll(items);
}
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.MENU_BUTTON);
setMnemonicParsing(true);
pseudoClassStateChanged(PSEUDO_CLASS_OPENVERTICALLY, true);
}
private final ObservableList<MenuItem> items = FXCollections.<MenuItem>observableArrayList();
public final ObservableList<MenuItem> getItems() {
return items;
}
private ReadOnlyBooleanWrapper showing = new ReadOnlyBooleanWrapper(this, "showing", false) {
@Override protected void invalidated() {
pseudoClassStateChanged(PSEUDO_CLASS_SHOWING, get());
super.invalidated();
}
};
private void setShowing(boolean value) {
Event.fireEvent(this, value ? new Event(ON_SHOWING) :
new Event(ON_HIDING));
showing.set(value);
Event.fireEvent(this, value ? new Event(ON_SHOWN) :
new Event(ON_HIDDEN));
}
public final boolean isShowing() { return showing.get(); }
public final ReadOnlyBooleanProperty showingProperty() { return showing.getReadOnlyProperty(); }
private ObjectProperty<Side> popupSide;
public final void setPopupSide(Side value) {
popupSideProperty().set(value);
}
public final Side getPopupSide() {
return popupSide == null ? Side.BOTTOM : popupSide.get();
}
public final ObjectProperty<Side> popupSideProperty() {
if (popupSide == null) {
popupSide = new ObjectPropertyBase<Side>(Side.BOTTOM) {
@Override protected void invalidated() {
final Side side = get();
final boolean active = (side == Side.TOP) || (side == Side.BOTTOM);
pseudoClassStateChanged(PSEUDO_CLASS_OPENVERTICALLY, active);
}
@Override
public Object getBean() {
return MenuButton.this;
}
@Override
public String getName() {
return "popupSide";
}
};
}
return popupSide;
}
public final ObjectProperty<EventHandler<Event>> onShowingProperty() { return onShowing; }
public final void setOnShowing(EventHandler<Event> value) { onShowingProperty().set(value); }
public final EventHandler<Event> getOnShowing() { return onShowingProperty().get(); }
private ObjectProperty<EventHandler<Event>> onShowing = new ObjectPropertyBase<EventHandler<Event>>() {
@Override protected void invalidated() {
setEventHandler(ON_SHOWING, get());
}
@Override public Object getBean() {
return MenuButton.this;
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
return MenuButton.this;
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
return MenuButton.this;
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
return MenuButton.this;
}
@Override public String getName() {
return "onHidden";
}
};
public void show() {
if (!isDisabled() && !showing.isBound()) {
setShowing(true);
}
}
public void hide() {
if (!showing.isBound()) {
setShowing(false);
}
}
@Override
public void fire() {
if (!isDisabled()) {
fireEvent(new ActionEvent());
}
}
@Override protected Skin<?> createDefaultSkin() {
return new MenuButtonSkin(this);
}
private static final String DEFAULT_STYLE_CLASS = "menu-button";
private static final PseudoClass PSEUDO_CLASS_OPENVERTICALLY =
PseudoClass.getPseudoClass("openvertically");
private static final PseudoClass PSEUDO_CLASS_SHOWING =
PseudoClass.getPseudoClass("showing");
@Override
public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case FIRE:
if (isShowing()) {
hide();
} else {
show();
}
break;
default: super.executeAccessibleAction(action);
}
}
}
