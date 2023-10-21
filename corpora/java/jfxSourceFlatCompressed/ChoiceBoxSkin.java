package javafx.scene.control.skin;
import com.sun.javafx.scene.control.ContextMenuContent;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import javafx.beans.WeakInvalidationListener;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;
import javafx.util.StringConverter;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import com.sun.javafx.scene.control.behavior.ChoiceBoxBehavior;
import javafx.collections.WeakListChangeListener;
public class ChoiceBoxSkin<T> extends SkinBase<ChoiceBox<T>> {
private ObservableList<T> choiceBoxItems;
private ContextMenu popup;
private StackPane openButton;
private final ToggleGroup toggleGroup = new ToggleGroup();
private SelectionModel<T> selectionModel;
private Label label;
private final BehaviorBase<ChoiceBox<T>> behavior;
private final ListChangeListener<T> choiceBoxItemsListener = new ListChangeListener<T>() {
@Override public void onChanged(Change<? extends T> c) {
while (c.next()) {
if (c.getRemovedSize() > 0 || c.wasPermutated()) {
toggleGroup.getToggles().clear();
popup.getItems().clear();
int i = 0;
for (T obj : c.getList()) {
addPopupItem(obj, i);
i++;
}
} else {
for (int i = c.getFrom(); i < c.getTo(); i++) {
final T obj = c.getList().get(i);
addPopupItem(obj, i);
}
}
}
updateSelection();
getSkinnable().requestLayout();
}
};
private final WeakListChangeListener<T> weakChoiceBoxItemsListener =
new WeakListChangeListener<T>(choiceBoxItemsListener);
private final InvalidationListener itemsObserver;
public ChoiceBoxSkin(ChoiceBox<T> control) {
super(control);
behavior = new ChoiceBoxBehavior<>(control);
initialize();
itemsObserver = observable -> updateChoiceBoxItems();
control.itemsProperty().addListener(itemsObserver);
control.requestLayout();
registerChangeListener(control.selectionModelProperty(), e -> updateSelectionModel());
registerChangeListener(control.showingProperty(), e -> {
if (getSkinnable().isShowing()) {
SelectionModel<T> sm = getSkinnable().getSelectionModel();
if (sm == null) return;
long currentSelectedIndex = sm.getSelectedIndex();
getSkinnable().autosize();
double y = 0;
if (popup.getSkin() != null) {
ContextMenuContent cmContent = (ContextMenuContent)popup.getSkin().getNode();
if (cmContent != null && currentSelectedIndex != -1) {
y = -(cmContent.getMenuYOffset((int)currentSelectedIndex));
}
}
popup.show(getSkinnable(), Side.BOTTOM, 2, y);
} else {
popup.hide();
}
});
registerChangeListener(control.itemsProperty(), e -> {
updateChoiceBoxItems();
updatePopupItems();
updateSelectionModel();
updateSelection();
});
registerChangeListener(control.converterProperty(), e -> {
updateChoiceBoxItems();
updatePopupItems();
updateLabelText();
});
registerChangeListener(control.valueProperty(), e -> {
updateLabelText();
});
}
@Override public void dispose() {
if (getSkinnable() == null) return;
getSkinnable().itemsProperty().removeListener(itemsObserver);
if (choiceBoxItems != null) {
choiceBoxItems.removeListener(weakChoiceBoxItemsListener);
choiceBoxItems = null;
}
if (selectionModel != null) {
selectionModel.selectedIndexProperty().removeListener(selectionChangeListener);
selectionModel = null;
}
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override protected void layoutChildren(final double x, final double y,
final double w, final double h) {
double obw = openButton.prefWidth(-1);
label.resizeRelocate(x, y, w, h);
openButton.resize(obw, openButton.prefHeight(-1));
positionInArea(openButton, (x+w) - obw,
y, obw, h, 0, HPos.CENTER, VPos.CENTER);
}
@Override protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
final double boxWidth = label.minWidth(-1) + openButton.minWidth(-1);
final double popupWidth = popup.minWidth(-1);
return leftInset + Math.max(boxWidth, popupWidth) + rightInset;
}
@Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
final double displayHeight = label.minHeight(-1);
final double openButtonHeight = openButton.minHeight(-1);
return topInset + Math.max(displayHeight, openButtonHeight) + bottomInset;
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
final double boxWidth = label.prefWidth(-1)
+ openButton.prefWidth(-1);
double popupWidth = popup.prefWidth(-1);
if (popupWidth <= 0) {
if (popup.getItems().size() > 0){
popupWidth = (new Text(((MenuItem)popup.getItems().get(0)).getText())).prefWidth(-1);
}
}
return (popup.getItems().size() == 0) ? 50 : leftInset + Math.max(boxWidth, popupWidth)
+ rightInset;
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
final double displayHeight = label.prefHeight(-1);
final double openButtonHeight = openButton.prefHeight(-1);
return topInset
+ Math.max(displayHeight, openButtonHeight)
+ bottomInset;
}
@Override protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return getSkinnable().prefHeight(width);
}
@Override protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return getSkinnable().prefWidth(height);
}
private void initialize() {
updateChoiceBoxItems();
label = new Label();
label.setMnemonicParsing(false);
openButton = new StackPane();
openButton.getStyleClass().setAll("open-button");
StackPane region = new StackPane();
region.getStyleClass().setAll("arrow");
openButton.getChildren().clear();
openButton.getChildren().addAll(region);
popup = new ContextMenu();
popup.showingProperty().addListener((o, ov, nv) -> {
if (!nv) {
getSkinnable().hide();
}
});
popup.setId("choice-box-popup-menu");
getChildren().setAll(label, openButton);
updatePopupItems();
updateSelectionModel();
updateSelection();
updateLabelText();
}
private void updateLabelText() {
T value = getSkinnable().getValue();
label.setText(getDisplayText(value));
}
private String getDisplayText(T value) {
if (getSkinnable().getConverter() != null) {
return getSkinnable().getConverter().toString(value);
}
return value == null ? "" : value.toString();
}
private void updateChoiceBoxItems() {
if (choiceBoxItems != null) {
choiceBoxItems.removeListener(weakChoiceBoxItemsListener);
}
choiceBoxItems = getSkinnable().getItems();
if (choiceBoxItems != null) {
choiceBoxItems.addListener(weakChoiceBoxItemsListener);
}
}
String getChoiceBoxSelectedText() {
return label.getText();
}
ContextMenu getChoiceBoxPopup() {
return popup;
}
private void addPopupItem(final T o, int i) {
MenuItem popupItem = null;
if (o instanceof Separator) {
popupItem = new SeparatorMenuItem();
} else if (o instanceof SeparatorMenuItem) {
popupItem = (SeparatorMenuItem) o;
} else {
final RadioMenuItem item = new RadioMenuItem(getDisplayText(o));
item.setId("choice-box-menu-item");
item.setToggleGroup(toggleGroup);
item.setOnAction(e -> {
if (selectionModel == null) return;
int index = getSkinnable().getItems().indexOf(o);
selectionModel.select(index);
item.setSelected(true);
});
popupItem = item;
}
popupItem.setMnemonicParsing(false);
popup.getItems().add(i, popupItem);
}
private void updatePopupItems() {
toggleGroup.getToggles().clear();
popup.getItems().clear();
toggleGroup.selectToggle(null);
for (int i = 0; i < choiceBoxItems.size(); i++) {
T o = choiceBoxItems.get(i);
addPopupItem(o, i);
}
}
private void updateSelectionModel() {
if (selectionModel != null) {
selectionModel.selectedIndexProperty().removeListener(selectionChangeListener);
}
this.selectionModel = getSkinnable().getSelectionModel();
if (selectionModel != null) {
selectionModel.selectedIndexProperty().addListener(selectionChangeListener);
}
}
private InvalidationListener selectionChangeListener = observable -> {
updateSelection();
};
private void updateSelection() {
if (selectionModel == null || selectionModel.isEmpty()) {
toggleGroup.selectToggle(null);
} else {
int selectedIndex = selectionModel.getSelectedIndex();
if (selectedIndex == -1 || selectedIndex > popup.getItems().size()) {
return;
}
if (selectedIndex < popup.getItems().size()) {
MenuItem selectedItem = popup.getItems().get(selectedIndex);
if (selectedItem instanceof RadioMenuItem) {
((RadioMenuItem) selectedItem).setSelected(true);
} else {
toggleGroup.selectToggle(null);
}
}
}
}
}
