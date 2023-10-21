package javafx.scene.control;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.behavior.ListCellBehavior;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.css.StyleableDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Orientation;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import javafx.css.StyleableObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.converter.EnumConverter;
import javafx.collections.WeakListChangeListener;
import javafx.css.converter.SizeConverter;
import javafx.scene.control.skin.ListViewSkin;
import java.lang.ref.WeakReference;
import javafx.css.PseudoClass;
import javafx.beans.DefaultProperty;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.util.Pair;
@DefaultProperty("items")
public class ListView<T> extends Control {
@SuppressWarnings("unchecked")
public static <T> EventType<ListView.EditEvent<T>> editAnyEvent() {
return (EventType<ListView.EditEvent<T>>) EDIT_ANY_EVENT;
}
private static final EventType<?> EDIT_ANY_EVENT =
new EventType<>(Event.ANY, "LIST_VIEW_EDIT");
@SuppressWarnings("unchecked")
public static <T> EventType<ListView.EditEvent<T>> editStartEvent() {
return (EventType<ListView.EditEvent<T>>) EDIT_START_EVENT;
}
private static final EventType<?> EDIT_START_EVENT =
new EventType<>(editAnyEvent(), "EDIT_START");
@SuppressWarnings("unchecked")
public static <T> EventType<ListView.EditEvent<T>> editCancelEvent() {
return (EventType<ListView.EditEvent<T>>) EDIT_CANCEL_EVENT;
}
private static final EventType<?> EDIT_CANCEL_EVENT =
new EventType<>(editAnyEvent(), "EDIT_CANCEL");
@SuppressWarnings("unchecked")
public static <T> EventType<ListView.EditEvent<T>> editCommitEvent() {
return (EventType<ListView.EditEvent<T>>) EDIT_COMMIT_EVENT;
}
private static final EventType<?> EDIT_COMMIT_EVENT =
new EventType<>(editAnyEvent(), "EDIT_COMMIT");
private boolean selectFirstRowByDefault = true;
public ListView() {
this(FXCollections.<T>observableArrayList());
}
public ListView(ObservableList<T> items) {
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.LIST_VIEW);
setItems(items);
setSelectionModel(new ListView.ListViewBitSetSelectionModel<T>(this));
setFocusModel(new ListView.ListViewFocusModel<T>(this));
setOnEditCommit(DEFAULT_EDIT_COMMIT_HANDLER);
getProperties().addListener((MapChangeListener<Object, Object>) change -> {
if (change.wasAdded() && "selectFirstRowByDefault".equals(change.getKey())) {
Boolean _selectFirstRowByDefault = (Boolean) change.getValueAdded();
if (_selectFirstRowByDefault == null) return;
selectFirstRowByDefault = _selectFirstRowByDefault;
}
});
pseudoClassStateChanged(PSEUDO_CLASS_VERTICAL, true);
}
private EventHandler<ListView.EditEvent<T>> DEFAULT_EDIT_COMMIT_HANDLER = t -> {
int index = t.getIndex();
List<T> list = getItems();
if (index < 0 || index >= list.size()) return;
list.set(index, t.getNewValue());
};
private ObjectProperty<ObservableList<T>> items;
public final void setItems(ObservableList<T> value) {
itemsProperty().set(value);
}
public final ObservableList<T> getItems() {
return items == null ? null : items.get();
}
public final ObjectProperty<ObservableList<T>> itemsProperty() {
if (items == null) {
items = new SimpleObjectProperty<>(this, "items");
}
return items;
}
private ObjectProperty<Node> placeholder;
public final ObjectProperty<Node> placeholderProperty() {
if (placeholder == null) {
placeholder = new SimpleObjectProperty<Node>(this, "placeholder");
}
return placeholder;
}
public final void setPlaceholder(Node value) {
placeholderProperty().set(value);
}
public final Node getPlaceholder() {
return placeholder == null ? null : placeholder.get();
}
private ObjectProperty<MultipleSelectionModel<T>> selectionModel = new SimpleObjectProperty<MultipleSelectionModel<T>>(this, "selectionModel");
public final void setSelectionModel(MultipleSelectionModel<T> value) {
selectionModelProperty().set(value);
}
public final MultipleSelectionModel<T> getSelectionModel() {
return selectionModel == null ? null : selectionModel.get();
}
public final ObjectProperty<MultipleSelectionModel<T>> selectionModelProperty() {
return selectionModel;
}
private ObjectProperty<FocusModel<T>> focusModel;
public final void setFocusModel(FocusModel<T> value) {
focusModelProperty().set(value);
}
public final FocusModel<T> getFocusModel() {
return focusModel == null ? null : focusModel.get();
}
public final ObjectProperty<FocusModel<T>> focusModelProperty() {
if (focusModel == null) {
focusModel = new SimpleObjectProperty<FocusModel<T>>(this, "focusModel");
}
return focusModel;
}
private ObjectProperty<Orientation> orientation;
public final void setOrientation(Orientation value) {
orientationProperty().set(value);
};
public final Orientation getOrientation() {
return orientation == null ? Orientation.VERTICAL : orientation.get();
}
public final ObjectProperty<Orientation> orientationProperty() {
if (orientation == null) {
orientation = new StyleableObjectProperty<Orientation>(Orientation.VERTICAL) {
@Override public void invalidated() {
final boolean active = (get() == Orientation.VERTICAL);
pseudoClassStateChanged(PSEUDO_CLASS_VERTICAL, active);
pseudoClassStateChanged(PSEUDO_CLASS_HORIZONTAL, !active);
}
@Override
public CssMetaData<ListView<?>,Orientation> getCssMetaData() {
return ListView.StyleableProperties.ORIENTATION;
}
@Override
public Object getBean() {
return ListView.this;
}
@Override
public String getName() {
return "orientation";
}
};
}
return orientation;
}
private ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactory;
public final void setCellFactory(Callback<ListView<T>, ListCell<T>> value) {
cellFactoryProperty().set(value);
}
public final Callback<ListView<T>, ListCell<T>> getCellFactory() {
return cellFactory == null ? null : cellFactory.get();
}
public final ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactoryProperty() {
if (cellFactory == null) {
cellFactory = new SimpleObjectProperty<Callback<ListView<T>, ListCell<T>>>(this, "cellFactory");
}
return cellFactory;
}
private DoubleProperty fixedCellSize;
public final void setFixedCellSize(double value) {
fixedCellSizeProperty().set(value);
}
public final double getFixedCellSize() {
return fixedCellSize == null ? Region.USE_COMPUTED_SIZE : fixedCellSize.get();
}
public final DoubleProperty fixedCellSizeProperty() {
if (fixedCellSize == null) {
fixedCellSize = new StyleableDoubleProperty(Region.USE_COMPUTED_SIZE) {
@Override public CssMetaData<ListView<?>,Number> getCssMetaData() {
return StyleableProperties.FIXED_CELL_SIZE;
}
@Override public Object getBean() {
return ListView.this;
}
@Override public String getName() {
return "fixedCellSize";
}
};
}
return fixedCellSize;
}
private BooleanProperty editable;
public final void setEditable(boolean value) {
editableProperty().set(value);
}
public final boolean isEditable() {
return editable == null ? false : editable.get();
}
public final BooleanProperty editableProperty() {
if (editable == null) {
editable = new SimpleBooleanProperty(this, "editable", false);
}
return editable;
}
private ReadOnlyIntegerWrapper editingIndex;
private void setEditingIndex(int value) {
editingIndexPropertyImpl().set(value);
}
public final int getEditingIndex() {
return editingIndex == null ? -1 : editingIndex.get();
}
public final ReadOnlyIntegerProperty editingIndexProperty() {
return editingIndexPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyIntegerWrapper editingIndexPropertyImpl() {
if (editingIndex == null) {
editingIndex = new ReadOnlyIntegerWrapper(this, "editingIndex", -1);
}
return editingIndex;
}
private ObjectProperty<EventHandler<ListView.EditEvent<T>>> onEditStart;
public final void setOnEditStart(EventHandler<ListView.EditEvent<T>> value) {
onEditStartProperty().set(value);
}
public final EventHandler<ListView.EditEvent<T>> getOnEditStart() {
return onEditStart == null ? null : onEditStart.get();
}
public final ObjectProperty<EventHandler<ListView.EditEvent<T>>> onEditStartProperty() {
if (onEditStart == null) {
onEditStart = new ObjectPropertyBase<EventHandler<ListView.EditEvent<T>>>() {
@Override protected void invalidated() {
setEventHandler(ListView.<T>editStartEvent(), get());
}
@Override
public Object getBean() {
return ListView.this;
}
@Override
public String getName() {
return "onEditStart";
}
};
}
return onEditStart;
}
private ObjectProperty<EventHandler<ListView.EditEvent<T>>> onEditCommit;
public final void setOnEditCommit(EventHandler<ListView.EditEvent<T>> value) {
onEditCommitProperty().set(value);
}
public final EventHandler<ListView.EditEvent<T>> getOnEditCommit() {
return onEditCommit == null ? null : onEditCommit.get();
}
public final ObjectProperty<EventHandler<ListView.EditEvent<T>>> onEditCommitProperty() {
if (onEditCommit == null) {
onEditCommit = new ObjectPropertyBase<EventHandler<ListView.EditEvent<T>>>() {
@Override protected void invalidated() {
setEventHandler(ListView.<T>editCommitEvent(), get());
}
@Override
public Object getBean() {
return ListView.this;
}
@Override
public String getName() {
return "onEditCommit";
}
};
}
return onEditCommit;
}
private ObjectProperty<EventHandler<ListView.EditEvent<T>>> onEditCancel;
public final void setOnEditCancel(EventHandler<ListView.EditEvent<T>> value) {
onEditCancelProperty().set(value);
}
public final EventHandler<ListView.EditEvent<T>> getOnEditCancel() {
return onEditCancel == null ? null : onEditCancel.get();
}
public final ObjectProperty<EventHandler<ListView.EditEvent<T>>> onEditCancelProperty() {
if (onEditCancel == null) {
onEditCancel = new ObjectPropertyBase<EventHandler<ListView.EditEvent<T>>>() {
@Override protected void invalidated() {
setEventHandler(ListView.<T>editCancelEvent(), get());
}
@Override
public Object getBean() {
return ListView.this;
}
@Override
public String getName() {
return "onEditCancel";
}
};
}
return onEditCancel;
}
public void edit(int itemIndex) {
if (!isEditable()) return;
setEditingIndex(itemIndex);
}
public void scrollTo(int index) {
ControlUtils.scrollToIndex(this, index);
}
public void scrollTo(T object) {
if( getItems() != null ) {
int idx = getItems().indexOf(object);
if( idx >= 0 ) {
ControlUtils.scrollToIndex(this, idx);
}
}
}
private ObjectProperty<EventHandler<ScrollToEvent<Integer>>> onScrollTo;
public void setOnScrollTo(EventHandler<ScrollToEvent<Integer>> value) {
onScrollToProperty().set(value);
}
public EventHandler<ScrollToEvent<Integer>> getOnScrollTo() {
if( onScrollTo != null ) {
return onScrollTo.get();
}
return null;
}
public ObjectProperty<EventHandler<ScrollToEvent<Integer>>> onScrollToProperty() {
if( onScrollTo == null ) {
onScrollTo = new ObjectPropertyBase<EventHandler<ScrollToEvent<Integer>>>() {
@Override protected void invalidated() {
setEventHandler(ScrollToEvent.scrollToTopIndex(), get());
}
@Override public Object getBean() {
return ListView.this;
}
@Override public String getName() {
return "onScrollTo";
}
};
}
return onScrollTo;
}
@Override protected Skin<?> createDefaultSkin() {
return new ListViewSkin<T>(this);
}
public void refresh() {
getProperties().put(Properties.RECREATE, Boolean.TRUE);
}
private static final String DEFAULT_STYLE_CLASS = "list-view";
private static class StyleableProperties {
private static final CssMetaData<ListView<?>,Orientation> ORIENTATION =
new CssMetaData<ListView<?>,Orientation>("-fx-orientation",
new EnumConverter<Orientation>(Orientation.class),
Orientation.VERTICAL) {
@Override
public Orientation getInitialValue(ListView<?> node) {
return node.getOrientation();
}
@Override
public boolean isSettable(ListView<?> n) {
return n.orientation == null || !n.orientation.isBound();
}
@SuppressWarnings("unchecked")
@Override
public StyleableProperty<Orientation> getStyleableProperty(ListView<?> n) {
return (StyleableProperty<Orientation>)n.orientationProperty();
}
};
private static final CssMetaData<ListView<?>,Number> FIXED_CELL_SIZE =
new CssMetaData<ListView<?>,Number>("-fx-fixed-cell-size",
SizeConverter.getInstance(),
Region.USE_COMPUTED_SIZE) {
@Override public Double getInitialValue(ListView<?> node) {
return node.getFixedCellSize();
}
@Override public boolean isSettable(ListView<?> n) {
return n.fixedCellSize == null || !n.fixedCellSize.isBound();
}
@Override public StyleableProperty<Number> getStyleableProperty(ListView<?> n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.fixedCellSizeProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
styleables.add(ORIENTATION);
styleables.add(FIXED_CELL_SIZE);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
return getClassCssMetaData();
}
private static final PseudoClass PSEUDO_CLASS_VERTICAL =
PseudoClass.getPseudoClass("vertical");
private static final PseudoClass PSEUDO_CLASS_HORIZONTAL =
PseudoClass.getPseudoClass("horizontal");
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case MULTIPLE_SELECTION: {
MultipleSelectionModel<T> sm = getSelectionModel();
return sm != null && sm.getSelectionMode() == SelectionMode.MULTIPLE;
}
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
public static class EditEvent<T> extends Event {
private final T newValue;
private final int editIndex;
private final ListView<T> source;
private static final long serialVersionUID = 20130724L;
public static final EventType<?> ANY = EDIT_ANY_EVENT;
public EditEvent(ListView<T> source,
EventType<? extends ListView.EditEvent<T>> eventType,
T newValue,
int editIndex) {
super(source, Event.NULL_SOURCE_TARGET, eventType);
this.source = source;
this.editIndex = editIndex;
this.newValue = newValue;
}
@Override public ListView<T> getSource() {
return source;
}
public int getIndex() {
return editIndex;
}
public T getNewValue() {
return newValue;
}
@Override public String toString() {
return "ListViewEditEvent [ newValue: " + getNewValue() + ", ListView: " + getSource() + " ]";
}
}
static class ListViewBitSetSelectionModel<T> extends MultipleSelectionModelBase<T> {
public ListViewBitSetSelectionModel(final ListView<T> listView) {
if (listView == null) {
throw new IllegalArgumentException("ListView can not be null");
}
this.listView = listView;
itemsObserver = new InvalidationListener() {
private WeakReference<ObservableList<T>> weakItemsRef = new WeakReference<>(listView.getItems());
@Override public void invalidated(Observable observable) {
ObservableList<T> oldItems = weakItemsRef.get();
weakItemsRef = new WeakReference<>(listView.getItems());
updateItemsObserver(oldItems, listView.getItems());
}
};
this.listView.itemsProperty().addListener(new WeakInvalidationListener(itemsObserver));
if (listView.getItems() != null) {
this.listView.getItems().addListener(weakItemsContentObserver);
}
updateItemCount();
updateDefaultSelection();
}
private final ListChangeListener<T> itemsContentObserver = new ListChangeListener<T>() {
@Override public void onChanged(Change<? extends T> c) {
updateItemCount();
boolean doSelectionUpdate = true;
while (c.next()) {
final T selectedItem = getSelectedItem();
final int selectedIndex = getSelectedIndex();
if (listView.getItems() == null || listView.getItems().isEmpty()) {
selectedItemChange = c;
clearSelection();
selectedItemChange = null;
} else if (selectedIndex == -1 && selectedItem != null) {
int newIndex = listView.getItems().indexOf(selectedItem);
if (newIndex != -1) {
setSelectedIndex(newIndex);
doSelectionUpdate = false;
}
} else if (c.wasRemoved() &&
c.getRemovedSize() == 1 &&
! c.wasAdded() &&
selectedItem != null &&
selectedItem.equals(c.getRemoved().get(0))) {
if (getSelectedIndex() < getItemCount()) {
final int previousRow = selectedIndex == 0 ? 0 : selectedIndex - 1;
T newSelectedItem = getModelItem(previousRow);
if (! selectedItem.equals(newSelectedItem)) {
startAtomic();
clearSelection(selectedIndex);
stopAtomic();
select(newSelectedItem);
}
}
}
}
if (doSelectionUpdate) {
updateSelection(c);
}
}
};
private final InvalidationListener itemsObserver;
private WeakListChangeListener<T> weakItemsContentObserver =
new WeakListChangeListener<>(itemsContentObserver);
private final ListView<T> listView;
private int itemCount = 0;
private int previousModelSize = 0;
private void updateSelection(Change<? extends T> c) {
c.reset();
List<Pair<Integer, Integer>> shifts = new ArrayList<>();
while (c.next()) {
if (c.wasReplaced()) {
if (c.getList().isEmpty()) {
clearSelection();
} else {
int index = getSelectedIndex();
if (previousModelSize == c.getRemovedSize()) {
clearSelection();
} else if (index < getItemCount() && index >= 0) {
startAtomic();
clearSelection(index);
stopAtomic();
select(index);
} else {
clearSelection();
}
}
} else if (c.wasAdded() || c.wasRemoved()) {
int shift = c.wasAdded() ? c.getAddedSize() : -c.getRemovedSize();
shifts.add(new Pair<>(c.getFrom(), shift));
} else if (c.wasPermutated()) {
int length = c.getTo() - c.getFrom();
HashMap<Integer, Integer> pMap = new HashMap<Integer, Integer>(length);
for (int i = c.getFrom(); i < c.getTo(); i++) {
pMap.put(i, c.getPermutation(i));
}
List<Integer> selectedIndices = new ArrayList<Integer>(getSelectedIndices());
clearSelection();
List<Integer> newIndices = new ArrayList<Integer>(getSelectedIndices().size());
for (int i = 0; i < selectedIndices.size(); i++) {
int oldIndex = selectedIndices.get(i);
if (pMap.containsKey(oldIndex)) {
Integer newIndex = pMap.get(oldIndex);
newIndices.add(newIndex);
}
}
if (!newIndices.isEmpty()) {
if (newIndices.size() == 1) {
select(newIndices.get(0));
} else {
int[] ints = new int[newIndices.size() - 1];
for (int i = 0; i < newIndices.size() - 1; i++) {
ints[i] = newIndices.get(i + 1);
}
selectIndices(newIndices.get(0), ints);
}
}
}
}
if (!shifts.isEmpty()) {
shiftSelection(shifts, null);
}
previousModelSize = getItemCount();
}
@Override public void selectAll() {
final int anchor = ListCellBehavior.getAnchor(listView, -1);
super.selectAll();
ListCellBehavior.setAnchor(listView, anchor, false);
}
@Override public void clearAndSelect(int row) {
ListCellBehavior.setAnchor(listView, row, false);
super.clearAndSelect(row);
}
@Override protected void focus(int row) {
if (listView.getFocusModel() == null) return;
listView.getFocusModel().focus(row);
listView.notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM);
}
@Override protected int getFocusedIndex() {
if (listView.getFocusModel() == null) return -1;
return listView.getFocusModel().getFocusedIndex();
}
@Override protected int getItemCount() {
return itemCount;
}
@Override protected T getModelItem(int index) {
List<T> items = listView.getItems();
if (items == null) return null;
if (index < 0 || index >= itemCount) return null;
return items.get(index);
}
private void updateItemCount() {
if (listView == null) {
itemCount = -1;
} else {
List<T> items = listView.getItems();
itemCount = items == null ? -1 : items.size();
}
}
private void updateItemsObserver(ObservableList<T> oldList, ObservableList<T> newList) {
if (oldList != null) {
oldList.removeListener(weakItemsContentObserver);
}
if (newList != null) {
newList.addListener(weakItemsContentObserver);
}
updateItemCount();
updateDefaultSelection();
}
private void updateDefaultSelection() {
int newSelectionIndex = -1;
int newFocusIndex = -1;
if (listView.getItems() != null) {
T selectedItem = getSelectedItem();
if (selectedItem != null) {
newSelectionIndex = listView.getItems().indexOf(selectedItem);
newFocusIndex = newSelectionIndex;
}
if (listView.selectFirstRowByDefault && newFocusIndex == -1) {
newFocusIndex = listView.getItems().size() > 0 ? 0 : -1;
}
}
clearSelection();
select(newSelectionIndex);
}
}
static class ListViewFocusModel<T> extends FocusModel<T> {
private final ListView<T> listView;
private int itemCount = 0;
public ListViewFocusModel(final ListView<T> listView) {
if (listView == null) {
throw new IllegalArgumentException("ListView can not be null");
}
this.listView = listView;
itemsObserver = new InvalidationListener() {
private WeakReference<ObservableList<T>> weakItemsRef = new WeakReference<>(listView.getItems());
@Override public void invalidated(Observable observable) {
ObservableList<T> oldItems = weakItemsRef.get();
weakItemsRef = new WeakReference<>(listView.getItems());
updateItemsObserver(oldItems, listView.getItems());
}
};
this.listView.itemsProperty().addListener(new WeakInvalidationListener(itemsObserver));
if (listView.getItems() != null) {
this.listView.getItems().addListener(weakItemsContentListener);
}
updateItemCount();
updateDefaultFocus();
focusedIndexProperty().addListener(o -> {
listView.notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM);
});
}
private void updateItemsObserver(ObservableList<T> oldList, ObservableList<T> newList) {
if (oldList != null) oldList.removeListener(weakItemsContentListener);
if (newList != null) newList.addListener(weakItemsContentListener);
updateItemCount();
updateDefaultFocus();
}
private final InvalidationListener itemsObserver;
private final ListChangeListener<T> itemsContentListener = c -> {
updateItemCount();
while (c.next()) {
int from = c.getFrom();
if (c.wasReplaced() || c.getAddedSize() == getItemCount()) {
updateDefaultFocus();
return;
}
if (getFocusedIndex() == -1 || from > getFocusedIndex()) {
return;
}
c.reset();
boolean added = false;
boolean removed = false;
int addedSize = 0;
int removedSize = 0;
while (c.next()) {
added |= c.wasAdded();
removed |= c.wasRemoved();
addedSize += c.getAddedSize();
removedSize += c.getRemovedSize();
}
if (added && !removed) {
focus(Math.min(getItemCount() - 1, getFocusedIndex() + addedSize));
} else if (!added && removed) {
focus(Math.max(0, getFocusedIndex() - removedSize));
}
}
};
private WeakListChangeListener<T> weakItemsContentListener
= new WeakListChangeListener<T>(itemsContentListener);
@Override protected int getItemCount() {
return itemCount;
}
@Override protected T getModelItem(int index) {
if (isEmpty()) return null;
if (index < 0 || index >= itemCount) return null;
return listView.getItems().get(index);
}
private boolean isEmpty() {
return itemCount == -1;
}
private void updateItemCount() {
if (listView == null) {
itemCount = -1;
} else {
List<T> items = listView.getItems();
itemCount = items == null ? -1 : items.size();
}
}
private void updateDefaultFocus() {
int newValueIndex = -1;
if (listView.getItems() != null) {
T focusedItem = getFocusedItem();
if (focusedItem != null) {
newValueIndex = listView.getItems().indexOf(focusedItem);
}
if (newValueIndex == -1) {
newValueIndex = listView.getItems().size() > 0 ? 0 : -1;
}
}
focus(newValueIndex);
}
}
}
