package javafx.scene.control.skin;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.control.ContextMenuContent;
import com.sun.javafx.scene.control.ControlAcceleratorSupport;
import com.sun.javafx.scene.control.LabeledImpl;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.input.Mnemonic;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import com.sun.javafx.scene.control.behavior.MenuButtonBehaviorBase;
import java.util.ArrayList;
import java.util.List;
public class MenuButtonSkinBase<C extends MenuButton> extends SkinBase<C> {
final LabeledImpl label;
final StackPane arrow;
final StackPane arrowButton;
ContextMenu popup;
boolean behaveLikeButton = false;
private ListChangeListener<MenuItem> itemsChangedListener;
private final ChangeListener<? super Scene> sceneChangeListener;
public MenuButtonSkinBase(final C control) {
super(control);
if (control.getOnMousePressed() == null) {
control.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
MenuButtonBehaviorBase behavior = getBehavior();
if (behavior != null) {
behavior.mousePressed(e, behaveLikeButton);
}
});
}
if (control.getOnMouseReleased() == null) {
control.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
MenuButtonBehaviorBase behavior = getBehavior();
if (behavior != null) {
behavior.mouseReleased(e, behaveLikeButton);
}
});
}
label = new MenuLabeledImpl(getSkinnable());
label.setMnemonicParsing(control.isMnemonicParsing());
label.setLabelFor(control);
arrow = new StackPane();
arrow.getStyleClass().setAll("arrow");
arrow.setMaxWidth(Region.USE_PREF_SIZE);
arrow.setMaxHeight(Region.USE_PREF_SIZE);
arrowButton = new StackPane();
arrowButton.getStyleClass().setAll("arrow-button");
arrowButton.getChildren().add(arrow);
popup = new ContextMenu();
popup.getItems().clear();
popup.getItems().addAll(getSkinnable().getItems());
getChildren().clear();
getChildren().addAll(label, arrowButton);
getSkinnable().requestLayout();
itemsChangedListener = c -> {
while (c.next()) {
popup.getItems().removeAll(c.getRemoved());
popup.getItems().addAll(c.getFrom(), c.getAddedSubList());
}
};
control.getItems().addListener(itemsChangedListener);
if (getSkinnable().getScene() != null) {
ControlAcceleratorSupport.addAcceleratorsIntoScene(getSkinnable().getItems(), getSkinnable());
}
List<Mnemonic> mnemonics = new ArrayList<>();
sceneChangeListener = (scene, oldValue, newValue) -> {
if (oldValue != null) {
ControlAcceleratorSupport.removeAcceleratorsFromScene(getSkinnable().getItems(), oldValue);
removeMnemonicsFromScene(mnemonics, oldValue);
}
if (getSkinnable() != null && getSkinnable().getScene() != null) {
ControlAcceleratorSupport.addAcceleratorsIntoScene(getSkinnable().getItems(), getSkinnable());
}
};
control.sceneProperty().addListener(sceneChangeListener);
registerChangeListener(control.showingProperty(), e -> {
if (getSkinnable().isShowing()) {
show();
} else {
hide();
}
});
registerChangeListener(control.focusedProperty(), e -> {
if (!getSkinnable().isFocused() && getSkinnable().isShowing()) {
hide();
}
if (!getSkinnable().isFocused() && popup.isShowing()) {
hide();
}
});
registerChangeListener(control.mnemonicParsingProperty(), e -> {
label.setMnemonicParsing(getSkinnable().isMnemonicParsing());
getSkinnable().requestLayout();
});
registerChangeListener(popup.showingProperty(), e -> {
if (!popup.isShowing() && getSkinnable().isShowing()) {
getSkinnable().hide();
}
if (popup.isShowing()) {
boolean showMnemonics = NodeHelper.isShowMnemonics(getSkinnable());
Utils.addMnemonics(popup, getSkinnable().getScene(), showMnemonics, mnemonics);
} else {
Scene scene = getSkinnable().getScene();
if (scene != null) {
removeMnemonicsFromScene(mnemonics, scene);
}
}
});
}
@Override public void dispose() {
if (getSkinnable() == null) return;
if (getSkinnable().getScene() != null) {
ControlAcceleratorSupport.removeAcceleratorsFromScene(getSkinnable().getItems(), getSkinnable().getScene());
}
getSkinnable().sceneProperty().removeListener(sceneChangeListener);
getSkinnable().getItems().removeListener(itemsChangedListener);
super.dispose();
if (popup != null ) {
if (popup.getSkin() != null && popup.getSkin().getNode() != null) {
ContextMenuContent cmContent = (ContextMenuContent)popup.getSkin().getNode();
cmContent.dispose();
}
popup.setSkin(null);
popup = null;
}
}
@Override protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return leftInset
+ label.minWidth(height)
+ snapSizeX(arrowButton.minWidth(height))
+ rightInset;
}
@Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return topInset
+ Math.max(label.minHeight(width), snapSizeY(arrowButton.minHeight(-1)))
+ bottomInset;
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return leftInset
+ label.prefWidth(height)
+ snapSizeX(arrowButton.prefWidth(height))
+ rightInset;
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return topInset
+ Math.max(label.prefHeight(width), snapSizeY(arrowButton.prefHeight(-1)))
+ bottomInset;
}
@Override protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return getSkinnable().prefWidth(height);
}
@Override protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return getSkinnable().prefHeight(width);
}
@Override protected void layoutChildren(final double x, final double y,
final double w, final double h) {
final double arrowButtonWidth = snapSizeX(arrowButton.prefWidth(-1));
label.resizeRelocate(x, y, w - arrowButtonWidth, h);
arrowButton.resizeRelocate(x + (w - arrowButtonWidth), y, arrowButtonWidth, h);
}
MenuButtonBehaviorBase<C> getBehavior() {
return null;
}
private void show() {
if (!popup.isShowing()) {
popup.show(getSkinnable(), getSkinnable().getPopupSide(), 0, 0);
}
}
private void hide() {
if (popup.isShowing()) {
popup.hide();
}
}
private void removeMnemonicsFromScene(List<Mnemonic> mnemonics, Scene scene) {
List<Mnemonic> mnemonicsToRemove = new ArrayList<>(mnemonics);
mnemonics.clear();
Platform.runLater(() -> mnemonicsToRemove.forEach(scene::removeMnemonic));
}
boolean requestFocusOnFirstMenuItem = false;
void requestFocusOnFirstMenuItem() {
this.requestFocusOnFirstMenuItem = true;
}
void putFocusOnFirstMenuItem() {
Skin<?> popupSkin = popup.getSkin();
if (popupSkin instanceof ContextMenuSkin) {
Node node = popupSkin.getNode();
if (node instanceof ContextMenuContent) {
((ContextMenuContent)node).requestFocusOnIndex(0);
}
}
}
private static class MenuLabeledImpl extends LabeledImpl {
MenuButton button;
public MenuLabeledImpl(MenuButton b) {
super(b);
button = b;
addEventHandler(ActionEvent.ACTION, e -> {
button.fireEvent(new ActionEvent());
e.consume();
});
}
}
}
