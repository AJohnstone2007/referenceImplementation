package javafx.scene.control;
import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.Comparator;
import com.sun.javafx.beans.IDProperty;
import com.sun.javafx.scene.control.ControlAcceleratorSupport;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;
import com.sun.javafx.event.EventHandlerManager;
import com.sun.javafx.scene.control.TableColumnBaseHelper;
import java.util.HashMap;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import com.sun.javafx.scene.control.skin.Utils;
@IDProperty("id")
public abstract class TableColumnBase<S,T> implements EventTarget, Styleable {
static {
TableColumnBaseHelper.setTableColumnBaseAccessor(
new TableColumnBaseHelper.TableColumnBaseAccessor() {
@Override
public void setWidth(TableColumnBase tableColumnBase, double width) {
tableColumnBase.doSetWidth(width);
}
});
}
static final double DEFAULT_WIDTH = 80.0F;
static final double DEFAULT_MIN_WIDTH = 10.0F;
static final double DEFAULT_MAX_WIDTH = 5000.0F;
public static final Comparator DEFAULT_COMPARATOR = (obj1, obj2) -> {
if (obj1 == null && obj2 == null) return 0;
if (obj1 == null) return -1;
if (obj2 == null) return 1;
if (obj1 instanceof Comparable && (obj1.getClass() == obj2.getClass() || obj1.getClass().isAssignableFrom(obj2.getClass()))) {
return (obj1 instanceof String) ? Collator.getInstance().compare(obj1, obj2) : ((Comparable)obj1).compareTo(obj2);
}
return Collator.getInstance().compare(obj1.toString(), obj2.toString());
};
protected TableColumnBase() {
this("");
}
protected TableColumnBase(String text) {
setText(text);
}
final EventHandlerManager eventHandlerManager = new EventHandlerManager(this);
private StringProperty text = new SimpleStringProperty(this, "text", "");
public final StringProperty textProperty() { return text; }
public final void setText(String value) { text.set(value); }
public final String getText() { return text.get(); }
private BooleanProperty visible = new SimpleBooleanProperty(this, "visible", true) {
@Override protected void invalidated() {
for (TableColumnBase<S,?> col : getColumns()) {
col.setVisible(isVisible());
}
}
};
public final void setVisible(boolean value) { visibleProperty().set(value); }
public final boolean isVisible() { return visible.get(); }
public final BooleanProperty visibleProperty() { return visible; }
private ReadOnlyObjectWrapper<TableColumnBase<S,?>> parentColumn;
void setParentColumn(TableColumnBase<S,?> value) { parentColumnPropertyImpl().set(value); }
public final TableColumnBase<S,?> getParentColumn() {
return parentColumn == null ? null : parentColumn.get();
}
public final ReadOnlyObjectProperty<TableColumnBase<S,?>> parentColumnProperty() {
return parentColumnPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyObjectWrapper<TableColumnBase<S,?>> parentColumnPropertyImpl() {
if (parentColumn == null) {
parentColumn = new ReadOnlyObjectWrapper<TableColumnBase<S,?>>(this, "parentColumn");
}
return parentColumn;
}
private ObjectProperty<ContextMenu> contextMenu;
public final void setContextMenu(ContextMenu value) { contextMenuProperty().set(value); }
public final ContextMenu getContextMenu() { return contextMenu == null ? null : contextMenu.get(); }
public final ObjectProperty<ContextMenu> contextMenuProperty() {
if (contextMenu == null) {
contextMenu = new SimpleObjectProperty<ContextMenu>(this, "contextMenu") {
private WeakReference<ContextMenu> contextMenuRef;
@Override protected void invalidated() {
ContextMenu oldMenu = contextMenuRef == null ? null : contextMenuRef.get();
if (oldMenu != null) {
ControlAcceleratorSupport.removeAcceleratorsFromScene(oldMenu.getItems(), TableColumnBase.this);
}
ContextMenu ctx = get();
contextMenuRef = new WeakReference<>(ctx);
if (ctx != null) {
ControlAcceleratorSupport.addAcceleratorsIntoScene(ctx.getItems(), TableColumnBase.this);
}
}
};
}
return contextMenu;
}
private StringProperty id;
public final void setId(String value) { idProperty().set(value); }
@Override public final String getId() { return id == null ? null : id.get(); }
public final StringProperty idProperty() {
if (id == null) {
id = new SimpleStringProperty(this, "id");
}
return id;
}
private StringProperty style;
public final void setStyle(String value) { styleProperty().set(value); }
@Override public final String getStyle() { return style == null ? "" : style.get(); }
public final StringProperty styleProperty() {
if (style == null) {
style = new SimpleStringProperty(this, "style");
}
return style;
}
private final ObservableList<String> styleClass = FXCollections.observableArrayList();
@Override public ObservableList<String> getStyleClass() {
return styleClass;
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
private ObjectProperty<Node> sortNode = new SimpleObjectProperty<Node>(this, "sortNode");
public final void setSortNode(Node value) { sortNodeProperty().set(value); }
public final Node getSortNode() { return sortNode.get(); }
public final ObjectProperty<Node> sortNodeProperty() { return sortNode; }
public final ReadOnlyDoubleProperty widthProperty() { return width.getReadOnlyProperty(); }
public final double getWidth() { return width.get(); }
void setWidth(double value) { width.set(value); }
private ReadOnlyDoubleWrapper width = new ReadOnlyDoubleWrapper(this, "width", DEFAULT_WIDTH);
private DoubleProperty minWidth;
public final void setMinWidth(double value) { minWidthProperty().set(value); }
public final double getMinWidth() { return minWidth == null ? DEFAULT_MIN_WIDTH : minWidth.get(); }
public final DoubleProperty minWidthProperty() {
if (minWidth == null) {
minWidth = new SimpleDoubleProperty(this, "minWidth", DEFAULT_MIN_WIDTH) {
@Override protected void invalidated() {
if (getMinWidth() < 0) {
setMinWidth(0.0F);
}
doSetWidth(getWidth());
}
};
}
return minWidth;
}
public final DoubleProperty prefWidthProperty() { return prefWidth; }
public final void setPrefWidth(double value) { prefWidthProperty().set(value); }
public final double getPrefWidth() { return prefWidth.get(); }
private final DoubleProperty prefWidth = new SimpleDoubleProperty(this, "prefWidth", DEFAULT_WIDTH) {
@Override protected void invalidated() {
doSetWidth(getPrefWidth());
}
};
public final DoubleProperty maxWidthProperty() { return maxWidth; }
public final void setMaxWidth(double value) { maxWidthProperty().set(value); }
public final double getMaxWidth() { return maxWidth.get(); }
private DoubleProperty maxWidth = new SimpleDoubleProperty(this, "maxWidth", DEFAULT_MAX_WIDTH) {
@Override protected void invalidated() {
doSetWidth(getWidth());
}
};
private BooleanProperty resizable;
public final BooleanProperty resizableProperty() {
if (resizable == null) {
resizable = new SimpleBooleanProperty(this, "resizable", true);
}
return resizable;
}
public final void setResizable(boolean value) {
resizableProperty().set(value);
}
public final boolean isResizable() {
return resizable == null ? true : resizable.get();
}
private BooleanProperty sortable;
public final BooleanProperty sortableProperty() {
if (sortable == null) {
sortable = new SimpleBooleanProperty(this, "sortable", true);
}
return sortable;
}
public final void setSortable(boolean value) {
sortableProperty().set(value);
}
public final boolean isSortable() {
return sortable == null ? true : sortable.get();
}
private BooleanProperty reorderable;
public final BooleanProperty reorderableProperty() {
if (reorderable == null) {
reorderable = new SimpleBooleanProperty(this, "reorderable", true);
}
return reorderable;
}
public final void setReorderable(boolean value) {
reorderableProperty().set(value);
}
public final boolean isReorderable() {
return reorderable == null ? true : reorderable.get();
}
private ObjectProperty<Comparator<T>> comparator;
public final ObjectProperty<Comparator<T>> comparatorProperty() {
if (comparator == null) {
comparator = new SimpleObjectProperty<Comparator<T>>(this, "comparator", DEFAULT_COMPARATOR);
}
return comparator;
}
public final void setComparator(Comparator<T> value) {
comparatorProperty().set(value);
}
public final Comparator<T> getComparator() {
return comparator == null ? DEFAULT_COMPARATOR : comparator.get();
}
private BooleanProperty editable;
public final void setEditable(boolean value) {
editableProperty().set(value);
}
public final boolean isEditable() {
return editable == null ? true : editable.get();
}
public final BooleanProperty editableProperty() {
if (editable == null) {
editable = new SimpleBooleanProperty(this, "editable", true);
}
return editable;
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
return properties != null && ! properties.isEmpty();
}
public void setUserData(Object value) {
getProperties().put(USER_DATA_KEY, value);
}
public Object getUserData() {
return getProperties().get(USER_DATA_KEY);
}
public abstract ObservableList<? extends TableColumnBase<S,?>> getColumns();
public final T getCellData(final int index) {
ObservableValue<T> result = getCellObservableValue(index);
return result == null ? null : result.getValue();
}
public final T getCellData(final S item) {
ObservableValue<T> result = getCellObservableValue(item);
return result == null ? null : result.getValue();
}
public abstract ObservableValue<T> getCellObservableValue(int index);
public abstract ObservableValue<T> getCellObservableValue(S item);
@Override public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
return tail.prepend(eventHandlerManager);
}
public <E extends Event> void addEventHandler(EventType<E> eventType, EventHandler<E> eventHandler) {
eventHandlerManager.addEventHandler(eventType, eventHandler);
}
public <E extends Event> void removeEventHandler(EventType<E> eventType, EventHandler<E> eventHandler) {
eventHandlerManager.removeEventHandler(eventType, eventHandler);
}
void doSetWidth(double width) {
setWidth(Utils.boundedSize(width, getMinWidth(), getMaxWidth()));
}
void updateColumnWidths() {
if (! getColumns().isEmpty()) {
double _minWidth = 0.0f;
double _prefWidth = 0.0f;
double _maxWidth = 0.0f;
for (TableColumnBase<S, ?> col : getColumns()) {
col.setParentColumn(this);
_minWidth += col.getMinWidth();
_prefWidth += col.getPrefWidth();
_maxWidth += col.getMaxWidth();
}
setMinWidth(_minWidth);
setPrefWidth(_prefWidth);
setMaxWidth(_maxWidth);
}
}
public final ObservableSet<PseudoClass> getPseudoClassStates() {
return FXCollections.emptyObservableSet();
}
}
