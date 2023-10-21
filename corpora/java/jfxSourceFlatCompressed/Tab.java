package javafx.scene.control;
import com.sun.javafx.beans.IDProperty;
import com.sun.javafx.scene.control.ControlAcceleratorSupport;
import javafx.collections.ObservableSet;
import javafx.css.CssMetaData;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.css.Styleable;
import com.sun.javafx.event.EventHandlerManager;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableMap;
@DefaultProperty("content")
@IDProperty("id")
public class Tab implements EventTarget, Styleable {
public Tab() {
this(null);
}
public Tab(String text) {
this(text, null);
}
public Tab(String text, Node content) {
setText(text);
setContent(content);
styleClass.addAll(DEFAULT_STYLE_CLASS);
}
private StringProperty id;
public final void setId(String value) { idProperty().set(value); }
@Override
public final String getId() { return id == null ? null : id.get(); }
public final StringProperty idProperty() {
if (id == null) {
id = new SimpleStringProperty(this, "id");
}
return id;
}
private StringProperty style;
public final void setStyle(String value) { styleProperty().set(value); }
@Override
public final String getStyle() { return style == null ? null : style.get(); }
public final StringProperty styleProperty() {
if (style == null) {
style = new SimpleStringProperty(this, "style");
}
return style;
}
private ReadOnlyBooleanWrapper selected;
final void setSelected(boolean value) {
selectedPropertyImpl().set(value);
}
public final boolean isSelected() {
return selected == null ? false : selected.get();
}
public final ReadOnlyBooleanProperty selectedProperty() {
return selectedPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyBooleanWrapper selectedPropertyImpl() {
if (selected == null) {
selected = new ReadOnlyBooleanWrapper() {
@Override protected void invalidated() {
if (getOnSelectionChanged() != null) {
Event.fireEvent(Tab.this, new Event(SELECTION_CHANGED_EVENT));
}
}
@Override
public Object getBean() {
return Tab.this;
}
@Override
public String getName() {
return "selected";
}
};
}
return selected;
}
private ReadOnlyObjectWrapper<TabPane> tabPane;
final void setTabPane(TabPane value) {
tabPanePropertyImpl().set(value);
}
public final TabPane getTabPane() {
return tabPane == null ? null : tabPane.get();
}
public final ReadOnlyObjectProperty<TabPane> tabPaneProperty() {
return tabPanePropertyImpl().getReadOnlyProperty();
}
private ReadOnlyObjectWrapper<TabPane> tabPanePropertyImpl() {
if (tabPane == null) {
tabPane = new ReadOnlyObjectWrapper<TabPane>(this, "tabPane") {
private WeakReference<TabPane> oldParent;
@Override protected void invalidated() {
if(oldParent != null && oldParent.get() != null) {
oldParent.get().disabledProperty().removeListener(parentDisabledChangedListener);
}
updateDisabled();
TabPane newParent = get();
if (newParent != null) {
newParent.disabledProperty().addListener(parentDisabledChangedListener);
}
oldParent = new WeakReference<TabPane>(newParent);
super.invalidated();
}
};
}
return tabPane;
}
private final InvalidationListener parentDisabledChangedListener = valueModel -> {
updateDisabled();
};
private StringProperty text;
public final void setText(String value) {
textProperty().set(value);
}
public final String getText() {
return text == null ? null : text.get();
}
public final StringProperty textProperty() {
if (text == null) {
text = new SimpleStringProperty(this, "text");
}
return text;
}
private ObjectProperty<Node> graphic;
public final void setGraphic(Node value) {
graphicProperty().set(value);
}
public final Node getGraphic() {
return graphic == null ? null : graphic.get();
}
public final ObjectProperty<Node> graphicProperty() {
if (graphic == null) {
graphic = new SimpleObjectProperty<Node>(this, "graphic");
}
return graphic;
}
private ObjectProperty<Node> content;
public final void setContent(Node value) {
contentProperty().set(value);
}
public final Node getContent() {
return content == null ? null : content.get();
}
public final ObjectProperty<Node> contentProperty() {
if (content == null) {
content = new SimpleObjectProperty<Node>(this, "content") {
@Override protected void invalidated() {
updateDisabled();
}
};
}
return content;
}
private ObjectProperty<ContextMenu> contextMenu;
public final void setContextMenu(ContextMenu value) {
contextMenuProperty().set(value);
}
public final ContextMenu getContextMenu() {
return contextMenu == null ? null : contextMenu.get();
}
public final ObjectProperty<ContextMenu> contextMenuProperty() {
if (contextMenu == null) {
contextMenu = new SimpleObjectProperty<ContextMenu>(this, "contextMenu") {
private WeakReference<ContextMenu> contextMenuRef;
@Override protected void invalidated() {
ContextMenu oldMenu = contextMenuRef == null ? null : contextMenuRef.get();
if (oldMenu != null) {
ControlAcceleratorSupport.removeAcceleratorsFromScene(oldMenu.getItems(), Tab.this);
}
ContextMenu ctx = get();
contextMenuRef = new WeakReference<>(ctx);
if (ctx != null) {
ControlAcceleratorSupport.addAcceleratorsIntoScene(ctx.getItems(), Tab.this);
}
}
};
}
return contextMenu;
}
private BooleanProperty closable;
public final void setClosable(boolean value) {
closableProperty().set(value);
}
public final boolean isClosable() {
return closable == null ? true : closable.get();
}
public final BooleanProperty closableProperty() {
if (closable == null) {
closable = new SimpleBooleanProperty(this, "closable", true);
}
return closable;
}
public static final EventType<Event> SELECTION_CHANGED_EVENT =
new EventType<Event> (Event.ANY, "SELECTION_CHANGED_EVENT");
private ObjectProperty<EventHandler<Event>> onSelectionChanged;
public final void setOnSelectionChanged(EventHandler<Event> value) {
onSelectionChangedProperty().set(value);
}
public final EventHandler<Event> getOnSelectionChanged() {
return onSelectionChanged == null ? null : onSelectionChanged.get();
}
public final ObjectProperty<EventHandler<Event>> onSelectionChangedProperty() {
if (onSelectionChanged == null) {
onSelectionChanged = new ObjectPropertyBase<EventHandler<Event>>() {
@Override protected void invalidated() {
setEventHandler(SELECTION_CHANGED_EVENT, get());
}
@Override
public Object getBean() {
return Tab.this;
}
@Override
public String getName() {
return "onSelectionChanged";
}
};
}
return onSelectionChanged;
}
public static final EventType<Event> CLOSED_EVENT = new EventType<Event>(Event.ANY, "TAB_CLOSED");
private ObjectProperty<EventHandler<Event>> onClosed;
public final void setOnClosed(EventHandler<Event> value) {
onClosedProperty().set(value);
}
public final EventHandler<Event> getOnClosed() {
return onClosed == null ? null : onClosed.get();
}
public final ObjectProperty<EventHandler<Event>> onClosedProperty() {
if (onClosed == null) {
onClosed = new ObjectPropertyBase<EventHandler<Event>>() {
@Override protected void invalidated() {
setEventHandler(CLOSED_EVENT, get());
}
@Override
public Object getBean() {
return Tab.this;
}
@Override
public String getName() {
return "onClosed";
}
};
}
return onClosed;
}
private ObjectProperty<Tooltip> tooltip;
public final void setTooltip(Tooltip value) { tooltipProperty().setValue(value); }
public final Tooltip getTooltip() { return tooltip == null ? null : tooltip.getValue(); }
public final ObjectProperty<Tooltip> tooltipProperty() {
if (tooltip == null) {
tooltip = new SimpleObjectProperty<Tooltip>(this, "tooltip");
}
return tooltip;
}
private final ObservableList<String> styleClass = FXCollections.observableArrayList();
private BooleanProperty disable;
public final void setDisable(boolean value) {
disableProperty().set(value);
}
public final boolean isDisable() { return disable == null ? false : disable.get(); }
public final BooleanProperty disableProperty() {
if (disable == null) {
disable = new BooleanPropertyBase(false) {
@Override
protected void invalidated() {
updateDisabled();
}
@Override
public Object getBean() {
return Tab.this;
}
@Override
public String getName() {
return "disable";
}
};
}
return disable;
}
private ReadOnlyBooleanWrapper disabled;
private final void setDisabled(boolean value) {
disabledPropertyImpl().set(value);
}
public final boolean isDisabled() {
return disabled == null ? false : disabled.get();
}
public final ReadOnlyBooleanProperty disabledProperty() {
return disabledPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyBooleanWrapper disabledPropertyImpl() {
if (disabled == null) {
disabled = new ReadOnlyBooleanWrapper() {
@Override
public Object getBean() {
return Tab.this;
}
@Override
public String getName() {
return "disabled";
}
};
}
return disabled;
}
private void updateDisabled() {
boolean disabled = isDisable() || (getTabPane() != null && getTabPane().isDisabled());
setDisabled(disabled);
Node content = getContent();
if (content != null) {
content.setDisable(disabled);
}
}
public static final EventType<Event> TAB_CLOSE_REQUEST_EVENT = new EventType<Event> (Event.ANY, "TAB_CLOSE_REQUEST_EVENT");
private ObjectProperty<EventHandler<Event>> onCloseRequest;
public final ObjectProperty<EventHandler<Event>> onCloseRequestProperty() {
if (onCloseRequest == null) {
onCloseRequest = new ObjectPropertyBase<EventHandler<Event>>() {
@Override protected void invalidated() {
setEventHandler(TAB_CLOSE_REQUEST_EVENT, get());
}
@Override public Object getBean() {
return Tab.this;
}
@Override public String getName() {
return "onCloseRequest";
}
};
}
return onCloseRequest;
}
public EventHandler<Event> getOnCloseRequest() {
if( onCloseRequest == null ) {
return null;
}
return onCloseRequest.get();
}
public void setOnCloseRequest(EventHandler<Event> value) {
onCloseRequestProperty().set(value);
}
private static final Object USER_DATA_KEY = new Object();
private ObservableMap<Object, Object> properties;
public final ObservableMap<Object, Object> getProperties() {
if (properties == null) {
properties = FXCollections.observableMap(new HashMap<Object, Object>());
}
return properties;
}
public boolean hasProperties() {
return properties != null && !properties.isEmpty();
}
public void setUserData(Object value) {
getProperties().put(USER_DATA_KEY, value);
}
public Object getUserData() {
return getProperties().get(USER_DATA_KEY);
}
@Override
public ObservableList<String> getStyleClass() {
return styleClass;
}
private final EventHandlerManager eventHandlerManager =
new EventHandlerManager(this);
@Override
public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
return tail.prepend(eventHandlerManager);
}
<E extends Event> void setEventHandler(EventType<E> eventType, EventHandler<E> eventHandler) {
eventHandlerManager.setEventHandler(eventType, eventHandler);
}
Node lookup(String selector) {
if (selector == null) return null;
Node n = null;
if (getContent() != null) {
n = getContent().lookup(selector);
}
if (n == null && getGraphic() != null) {
n = getGraphic().lookup(selector);
}
return n;
}
List<Node> lookupAll(String selector) {
final List<Node> results = new ArrayList<>();
if (getContent() != null) {
Set set = getContent().lookupAll(selector);
if (!set.isEmpty()) {
results.addAll(set);
}
}
if (getGraphic() != null) {
Set set = getGraphic().lookupAll(selector);
if (!set.isEmpty()) {
results.addAll(set);
}
}
return results;
}
private static final String DEFAULT_STYLE_CLASS = "tab";
@Override
public String getTypeSelector() {
return "Tab";
}
@Override
public Styleable getStyleableParent() {
return getTabPane();
}
public final ObservableSet<PseudoClass> getPseudoClassStates() {
return FXCollections.emptyObservableSet();
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return Collections.emptyList();
}
}
