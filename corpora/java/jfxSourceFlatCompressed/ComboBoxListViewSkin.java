package javafx.scene.control.skin;
import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.behavior.ComboBoxListViewBehavior;
import java.util.List;
import java.util.function.Supplier;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.util.Callback;
import javafx.util.StringConverter;
public class ComboBoxListViewSkin<T> extends ComboBoxPopupControl<T> {
private static final String COMBO_BOX_ROWS_TO_MEASURE_WIDTH_KEY = "comboBoxRowsToMeasureWidth";
private final ComboBox<T> comboBox;
private ObservableList<T> comboBoxItems;
private ListCell<T> buttonCell;
private Callback<ListView<T>, ListCell<T>> cellFactory;
private final ListView<T> listView;
private ObservableList<T> listViewItems;
private boolean listSelectionLock = false;
private boolean listViewSelectionDirty = false;
private final ComboBoxListViewBehavior behavior;
private boolean itemCountDirty;
private final ListChangeListener<T> listViewItemsListener = new ListChangeListener<T>() {
@Override public void onChanged(ListChangeListener.Change<? extends T> c) {
itemCountDirty = true;
getSkinnable().requestLayout();
}
};
private final InvalidationListener itemsObserver;
private final WeakListChangeListener<T> weakListViewItemsListener =
new WeakListChangeListener<T>(listViewItemsListener);
public ComboBoxListViewSkin(final ComboBox<T> control) {
super(control);
this.behavior = new ComboBoxListViewBehavior<>(control);
this.comboBox = control;
updateComboBoxItems();
itemsObserver = observable -> {
updateComboBoxItems();
updateListViewItems();
};
control.itemsProperty().addListener(new WeakInvalidationListener(itemsObserver));
this.listView = createListView();
this.listView.setManaged(false);
getChildren().add(listView);
updateListViewItems();
updateCellFactory();
updateButtonCell();
updateValue();
registerChangeListener(control.itemsProperty(), e -> {
updateComboBoxItems();
updateListViewItems();
});
registerChangeListener(control.promptTextProperty(), e -> updateDisplayNode());
registerChangeListener(control.cellFactoryProperty(), e -> updateCellFactory());
registerChangeListener(control.visibleRowCountProperty(), e -> {
if (listView == null) return;
listView.requestLayout();
});
registerChangeListener(control.converterProperty(), e -> updateListViewItems());
registerChangeListener(control.buttonCellProperty(), e -> {
updateButtonCell();
updateDisplayArea();
});
registerChangeListener(control.valueProperty(), e -> {
updateValue();
control.fireEvent(new ActionEvent());
});
registerChangeListener(control.editableProperty(), e -> updateEditable());
if (comboBox.isShowing()) {
show();
}
comboBox.sceneProperty().addListener(o -> {
if (((ObservableValue)o).getValue() == null) {
comboBox.hide();
}
});
}
private final BooleanProperty hideOnClick = new SimpleBooleanProperty(this, "hideOnClick", true);
public final BooleanProperty hideOnClickProperty() {
return hideOnClick;
}
public final boolean isHideOnClick() {
return hideOnClick.get();
}
public final void setHideOnClick(boolean value) {
hideOnClick.set(value);
}
@Override public void dispose() {
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override protected TextField getEditor() {
return getSkinnable().isEditable() ? ((ComboBox)getSkinnable()).getEditor() : null;
}
@Override protected StringConverter<T> getConverter() {
return ((ComboBox)getSkinnable()).getConverter();
}
@Override public Node getDisplayNode() {
Node displayNode;
if (comboBox.isEditable()) {
displayNode = getEditableInputNode();
} else {
displayNode = buttonCell;
}
updateDisplayNode();
return displayNode;
}
@Override public Node getPopupContent() {
return listView;
}
@Override protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
reconfigurePopup();
return 50;
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
double superPrefWidth = super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
double listViewWidth = listView.prefWidth(height);
double pw = Math.max(superPrefWidth, listViewWidth);
reconfigurePopup();
return pw;
}
@Override protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
reconfigurePopup();
return super.computeMaxWidth(height, topInset, rightInset, bottomInset, leftInset);
}
@Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
reconfigurePopup();
return super.computeMinHeight(width, topInset, rightInset, bottomInset, leftInset);
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
reconfigurePopup();
return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
}
@Override protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
reconfigurePopup();
return super.computeMaxHeight(width, topInset, rightInset, bottomInset, leftInset);
}
@Override protected void layoutChildren(final double x, final double y,
final double w, final double h) {
if (listViewSelectionDirty) {
try {
listSelectionLock = true;
SingleSelectionModel<T> selectionModel = comboBox.getSelectionModel();
if (selectionModel != null) {
T item = selectionModel.getSelectedItem();
listView.getSelectionModel().clearSelection();
listView.getSelectionModel().select(item);
}
} finally {
listSelectionLock = false;
listViewSelectionDirty = false;
}
}
super.layoutChildren(x, y, w, h);
}
@Override void updateDisplayNode() {
if (getEditor() != null) {
super.updateDisplayNode();
} else {
T value = comboBox.getValue();
int index = getIndexOfComboBoxValueInItemsList();
if (index > -1) {
buttonCell.setItem(null);
buttonCell.updateIndex(index);
} else {
buttonCell.updateIndex(-1);
boolean empty = updateDisplayText(buttonCell, value, false);
buttonCell.pseudoClassStateChanged(PSEUDO_CLASS_EMPTY, empty);
buttonCell.pseudoClassStateChanged(PSEUDO_CLASS_FILLED, !empty);
buttonCell.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, true);
}
}
}
@Override ComboBoxBaseBehavior getBehavior() {
return behavior;
}
private void updateComboBoxItems() {
comboBoxItems = comboBox.getItems();
comboBoxItems = comboBoxItems == null ? FXCollections.<T>emptyObservableList() : comboBoxItems;
}
private void updateListViewItems() {
if (listViewItems != null) {
listViewItems.removeListener(weakListViewItemsListener);
}
this.listViewItems = comboBoxItems;
listView.setItems(listViewItems);
if (listViewItems != null) {
listViewItems.addListener(weakListViewItemsListener);
}
itemCountDirty = true;
getSkinnable().requestLayout();
}
private void updateValue() {
SingleSelectionModel<T> comboBoxSM = comboBox.getSelectionModel();
if (comboBoxSM == null) {
return;
}
T newValue = comboBox.getValue();
SelectionModel<T> listViewSM = listView.getSelectionModel();
final int indexOfNewValue = getIndexOfComboBoxValueInItemsList();
if (newValue == null && indexOfNewValue == -1) {
listViewSM.clearSelection();
} else {
if (indexOfNewValue == -1) {
listSelectionLock = true;
listViewSM.clearSelection();
listSelectionLock = false;
} else {
int index = comboBoxSM.getSelectedIndex();
if (index >= 0 && index < comboBoxItems.size()) {
T itemsObj = comboBoxItems.get(index);
if ((itemsObj != null && itemsObj.equals(newValue)) || (itemsObj == null && newValue == null)) {
listViewSM.select(index);
} else {
listViewSM.select(newValue);
}
} else {
int listViewIndex = comboBoxItems.indexOf(newValue);
if (listViewIndex == -1) {
updateDisplayNode();
} else {
listViewSM.select(listViewIndex);
}
}
}
}
}
private boolean updateDisplayText(ListCell<T> cell, T item, boolean empty) {
if (empty) {
if (cell == null) return true;
cell.setGraphic(null);
cell.setText(null);
return true;
} else if (item instanceof Node) {
Node currentNode = cell.getGraphic();
Node newNode = (Node) item;
if (currentNode == null || ! currentNode.equals(newNode)) {
cell.setText(null);
cell.setGraphic(newNode);
}
return newNode == null;
} else {
final StringConverter<T> c = comboBox.getConverter();
final String promptText = comboBox.getPromptText();
String s = item == null && promptText != null ? promptText :
c == null ? (item == null ? null : item.toString()) : c.toString(item);
cell.setText(s);
cell.setGraphic(null);
return s == null || s.isEmpty();
}
}
private int getIndexOfComboBoxValueInItemsList() {
T value = comboBox.getValue();
int index = comboBoxItems.indexOf(value);
return index;
}
private void updateButtonCell() {
buttonCell = comboBox.getButtonCell() != null ?
comboBox.getButtonCell() : getDefaultCellFactory().call(listView);
buttonCell.setMouseTransparent(true);
buttonCell.updateListView(listView);
buttonCell.setAccessibleRole(AccessibleRole.NODE);
}
private void updateCellFactory() {
Callback<ListView<T>, ListCell<T>> cf = comboBox.getCellFactory();
cellFactory = cf != null ? cf : getDefaultCellFactory();
listView.setCellFactory(cellFactory);
}
private Callback<ListView<T>, ListCell<T>> getDefaultCellFactory() {
return new Callback<ListView<T>, ListCell<T>>() {
@Override public ListCell<T> call(ListView<T> listView) {
return new ListCell<T>() {
@Override public void updateItem(T item, boolean empty) {
super.updateItem(item, empty);
updateDisplayText(this, item, empty);
}
};
}
};
}
private ListView<T> createListView() {
final ListView<T> _listView = new ListView<T>() {
{
getProperties().put("selectFirstRowByDefault", false);
getProperties().put("editableComboBox", (Supplier<Boolean>) () -> getSkinnable().isEditable());
}
@Override protected double computeMinHeight(double width) {
return 30;
}
@Override protected double computePrefWidth(double height) {
double pw;
if (getSkin() instanceof ListViewSkin) {
ListViewSkin<?> skin = (ListViewSkin<?>)getSkin();
if (itemCountDirty) {
skin.updateItemCount();
itemCountDirty = false;
}
int rowsToMeasure = -1;
if (comboBox.getProperties().containsKey(COMBO_BOX_ROWS_TO_MEASURE_WIDTH_KEY)) {
rowsToMeasure = (Integer) comboBox.getProperties().get(COMBO_BOX_ROWS_TO_MEASURE_WIDTH_KEY);
}
pw = Math.max(comboBox.getWidth(), skin.getMaxCellWidth(rowsToMeasure) + 30);
} else {
pw = Math.max(100, comboBox.getWidth());
}
if (getItems().isEmpty() && getPlaceholder() != null) {
pw = Math.max(super.computePrefWidth(height), pw);
}
return Math.max(50, pw);
}
@Override protected double computePrefHeight(double width) {
return getListViewPrefHeight();
}
};
_listView.setId("list-view");
_listView.placeholderProperty().bind(comboBox.placeholderProperty());
_listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
_listView.setFocusTraversable(false);
_listView.getSelectionModel().selectedIndexProperty().addListener(o -> {
if (listSelectionLock) return;
SingleSelectionModel<T> selectionModel = comboBox.getSelectionModel();
if (selectionModel == null) return;
int index = listView.getSelectionModel().getSelectedIndex();
selectionModel.select(index);
updateDisplayNode();
comboBox.notifyAccessibleAttributeChanged(AccessibleAttribute.TEXT);
});
SingleSelectionModel<T> selectionModel = comboBox.getSelectionModel();
if (selectionModel != null) {
selectionModel.selectedItemProperty().addListener(o -> {
listViewSelectionDirty = true;
});
}
_listView.addEventFilter(MouseEvent.MOUSE_RELEASED, t -> {
EventTarget target = t.getTarget();
if (target instanceof Parent) {
List<String> s = ((Parent) target).getStyleClass();
if (s.contains("thumb")
|| s.contains("track")
|| s.contains("decrement-arrow")
|| s.contains("increment-arrow")) {
return;
}
}
if (isHideOnClick()) {
comboBox.hide();
}
});
_listView.setOnKeyPressed(t -> {
if (t.getCode() == KeyCode.ENTER ||
t.getCode() == KeyCode.SPACE ||
t.getCode() == KeyCode.ESCAPE) {
comboBox.hide();
}
});
return _listView;
}
private double getListViewPrefHeight() {
double ph;
if (listView.getSkin() instanceof VirtualContainerBase) {
int maxRows = comboBox.getVisibleRowCount();
VirtualContainerBase<?,?> skin = (VirtualContainerBase<?,?>)listView.getSkin();
ph = skin.getVirtualFlowPreferredHeight(maxRows);
} else {
double ch = comboBoxItems.size() * 25;
ph = Math.min(ch, 200);
}
return ph;
}
ListView<T> getListView() {
return listView;
}
private static final PseudoClass PSEUDO_CLASS_SELECTED =
PseudoClass.getPseudoClass("selected");
private static final PseudoClass PSEUDO_CLASS_EMPTY =
PseudoClass.getPseudoClass("empty");
private static final PseudoClass PSEUDO_CLASS_FILLED =
PseudoClass.getPseudoClass("filled");
@Override public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case FOCUS_ITEM: {
if (comboBox.isShowing()) {
return listView.queryAccessibleAttribute(attribute, parameters);
}
return null;
}
case TEXT: {
String accText = comboBox.getAccessibleText();
if (accText != null && !accText.isEmpty()) return accText;
String title = comboBox.isEditable() ? getEditor().getText() : buttonCell.getText();
if (title == null || title.isEmpty()) {
title = comboBox.getPromptText();
}
return title;
}
case SELECTION_START:
return (getEditor() != null) ? getEditor().getSelection().getStart() : null;
case SELECTION_END:
return (getEditor() != null) ? getEditor().getSelection().getEnd() : null;
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
}
