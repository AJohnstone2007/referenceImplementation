package com.sun.javafx.scene.control;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.control.behavior.TwoLevelFocusPopupBehavior;
import com.sun.javafx.scene.control.skin.Utils;
import com.sun.javafx.scene.traversal.Direction;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
public class ContextMenuContent extends Region {
private static final String ITEM_STYLE_CLASS_LISTENER = "itemStyleClassListener";
private ContextMenu contextMenu;
private double maxGraphicWidth = 0;
private double maxRightWidth = 0;
private double maxLabelWidth = 0;
private double maxRowHeight = 0;
private double maxLeftWidth = 0;
private double oldWidth = 0;
private Rectangle clipRect;
MenuBox itemsContainer;
private ArrowMenuItem upArrow;
private ArrowMenuItem downArrow;
private int currentFocusedIndex = -1;
private boolean itemsDirty = true;
private InvalidationListener popupShowingListener = arg0 -> {
updateItems();
};
private WeakInvalidationListener weakPopupShowingListener =
new WeakInvalidationListener(popupShowingListener);
public ContextMenuContent(final ContextMenu popupMenu) {
this.contextMenu = popupMenu;
clipRect = new Rectangle();
clipRect.setSmooth(false);
itemsContainer = new MenuBox();
itemsContainer.setClip(clipRect);
upArrow = new ArrowMenuItem(this);
upArrow.setUp(true);
upArrow.setFocusTraversable(false);
downArrow = new ArrowMenuItem(this);
downArrow.setUp(false);
downArrow.setFocusTraversable(false);
getChildren().add(itemsContainer);
getChildren().add(upArrow);
getChildren().add(downArrow);
initialize();
setUpBinds();
updateItems();
popupMenu.showingProperty().addListener(weakPopupShowingListener);
if (Utils.isTwoLevelFocus()) {
new TwoLevelFocusPopupBehavior(this);
}
}
public VBox getItemsContainer() {
return itemsContainer;
}
int getCurrentFocusIndex() {
return currentFocusedIndex;
}
void setCurrentFocusedIndex(int index) {
if (index < itemsContainer.getChildren().size()) {
currentFocusedIndex = index;
}
}
private void updateItems() {
if (itemsDirty) {
updateVisualItems();
itemsDirty = false;
}
}
private void computeVisualMetrics() {
maxRightWidth = 0;
maxLabelWidth = 0;
maxRowHeight = 0;
maxGraphicWidth = 0;
maxLeftWidth = 0;
for (int i = 0; i < itemsContainer.getChildren().size(); i++) {
Node child = itemsContainer.getChildren().get(i);
if (child instanceof MenuItemContainer) {
final MenuItemContainer menuItemContainer = (MenuItemContainer)itemsContainer.getChildren().get(i);
if (! menuItemContainer.isVisible()) continue;
double alt = -1;
Node n = menuItemContainer.left;
if (n != null) {
if (n.getContentBias() == Orientation.VERTICAL) {
alt = snapSizeY(n.prefHeight(-1));
} else alt = -1;
maxLeftWidth = Math.max(maxLeftWidth, snapSizeX(n.prefWidth(alt)));
maxRowHeight = Math.max(maxRowHeight, n.prefHeight(-1));
}
n = menuItemContainer.graphic;
if (n != null) {
if (n.getContentBias() == Orientation.VERTICAL) {
alt = snapSizeY(n.prefHeight(-1));
} else alt = -1;
maxGraphicWidth = Math.max(maxGraphicWidth, snapSizeX(n.prefWidth(alt)));
maxRowHeight = Math.max(maxRowHeight, n.prefHeight(-1));
}
n = menuItemContainer.label;
if (n != null) {
if (n.getContentBias() == Orientation.VERTICAL) {
alt = snapSizeY(n.prefHeight(-1));
} else alt = -1;
maxLabelWidth = Math.max(maxLabelWidth, snapSizeX(n.prefWidth(alt)));
maxRowHeight = Math.max(maxRowHeight, n.prefHeight(-1));
}
n = menuItemContainer.right;
if (n != null) {
if (n.getContentBias() == Orientation.VERTICAL) {
alt = snapSizeY(n.prefHeight(-1));
} else alt = -1;
maxRightWidth = Math.max(maxRightWidth, snapSizeX(n.prefWidth(alt)));
maxRowHeight = Math.max(maxRowHeight, n.prefHeight(-1));
}
}
}
final double newWidth = maxRightWidth + maxLabelWidth + maxGraphicWidth + maxLeftWidth;
Window ownerWindow = contextMenu.getOwnerWindow();
if (ownerWindow instanceof ContextMenu) {
if (contextMenu.getX() < ownerWindow.getX()) {
if (oldWidth != newWidth) {
contextMenu.setX(contextMenu.getX() + oldWidth - newWidth);
}
}
}
oldWidth = newWidth;
}
private void updateVisualItems() {
ObservableList<Node> itemsContainerChilder = itemsContainer.getChildren();
disposeVisualItems();
for (int row = 0; row < getItems().size(); row++) {
final MenuItem item = getItems().get(row);
if (item instanceof CustomMenuItem && ((CustomMenuItem) item).getContent() == null) {
continue;
}
if (item instanceof SeparatorMenuItem) {
Node node = ((CustomMenuItem) item).getContent();
node.visibleProperty().bind(item.visibleProperty());
itemsContainerChilder.add(node);
node.getProperties().put(MenuItem.class, item);
} else {
MenuItemContainer menuItemContainer = new MenuItemContainer(item);
menuItemContainer.visibleProperty().bind(item.visibleProperty());
itemsContainerChilder.add(menuItemContainer);
}
}
if (getItems().size() > 0) {
final MenuItem item = getItems().get(0);
getProperties().put(Menu.class, item.getParentMenu());
}
NodeHelper.reapplyCSS(this);
}
private void disposeVisualItems() {
ObservableList<Node> itemsContainerChilder = itemsContainer.getChildren();
for (int i = 0, max = itemsContainerChilder.size(); i < max; i++) {
Node n = itemsContainerChilder.get(i);
if (n instanceof MenuItemContainer) {
MenuItemContainer container = (MenuItemContainer) n;
container.visibleProperty().unbind();
container.dispose();
}
}
itemsContainerChilder.clear();
}
public void dispose() {
disposeBinds();
disposeVisualItems();
disposeContextMenu(submenu);
submenu = null;
openSubmenu = null;
selectedBackground = null;
if (contextMenu != null) {
contextMenu.getItems().clear();
contextMenu = null;
}
}
public void disposeContextMenu(ContextMenu menu) {
if (menu == null) return;
Skin<?> skin = menu.getSkin();
if (skin == null) return;
ContextMenuContent cmContent = (ContextMenuContent)skin.getNode();
if (cmContent == null) return;
cmContent.dispose();
}
@Override protected void layoutChildren() {
if (itemsContainer.getChildren().size() == 0) return;
final double x = snappedLeftInset();
final double y = snappedTopInset();
final double w = getWidth() - x - snappedRightInset();
final double h = getHeight() - y - snappedBottomInset();
final double contentHeight = snapSizeY(getContentHeight());
itemsContainer.resize(w,contentHeight);
itemsContainer.relocate(x, y);
if (isFirstShow && ty == 0) {
upArrow.setVisible(false);
isFirstShow = false;
} else {
upArrow.setVisible(ty < y && ty < 0);
}
downArrow.setVisible(ty + contentHeight > (y + h));
clipRect.setX(0);
clipRect.setY(0);
clipRect.setWidth(w);
clipRect.setHeight(h);
if (upArrow.isVisible()) {
final double prefHeight = snapSizeY(upArrow.prefHeight(-1));
clipRect.setHeight(snapSizeY(clipRect.getHeight() - prefHeight));
clipRect.setY(snapSizeY(clipRect.getY()) + prefHeight);
upArrow.resize(snapSizeX(upArrow.prefWidth(-1)), prefHeight);
positionInArea(upArrow, x, y, w, prefHeight, 0,
HPos.CENTER, VPos.CENTER);
}
if (downArrow.isVisible()) {
final double prefHeight = snapSizeY(downArrow.prefHeight(-1));
clipRect.setHeight(snapSizeY(clipRect.getHeight()) - prefHeight);
downArrow.resize(snapSizeX(downArrow.prefWidth(-1)), prefHeight);
positionInArea(downArrow, x, (y + h - prefHeight), w, prefHeight, 0,
HPos.CENTER, VPos.CENTER);
}
}
@Override protected double computePrefWidth(double height) {
computeVisualMetrics();
double prefWidth = 0;
if (itemsContainer.getChildren().size() == 0) return 0;
for (Node n : itemsContainer.getChildren()) {
if (! n.isVisible()) continue;
prefWidth = Math.max(prefWidth, snapSizeX(n.prefWidth(-1)));
}
return snappedLeftInset() + snapSizeX(prefWidth) + snappedRightInset();
}
@Override protected double computePrefHeight(double width) {
if (itemsContainer.getChildren().size() == 0) return 0;
final double screenHeight = getScreenHeight();
final double contentHeight = getContentHeight();
double totalHeight = snappedTopInset() + snapSizeY(contentHeight) + snappedBottomInset();
double prefHeight = (screenHeight <= 0) ? (totalHeight) : (Math.min(totalHeight, screenHeight));
return prefHeight;
}
@Override protected double computeMinHeight(double width) {
return 0.0;
}
@Override protected double computeMaxHeight(double height) {
return getScreenHeight();
}
private double getScreenHeight() {
if (contextMenu == null || contextMenu.getOwnerWindow() == null ||
contextMenu.getOwnerWindow().getScene() == null) {
return -1;
}
return snapSizeY(com.sun.javafx.util.Utils.getScreen(
contextMenu.getOwnerWindow().getScene().getRoot()).getVisualBounds().getHeight());
}
private double getContentHeight() {
double h = 0.0d;
for (Node i : itemsContainer.getChildren()) {
if (i.isVisible()) {
h += snapSizeY(i.prefHeight(-1));
}
}
return h;
}
private void ensureFocusedMenuItemIsVisible(Node node) {
if (node == null) return;
final Bounds nodeBounds = node.getBoundsInParent();
final Bounds clipBounds = clipRect.getBoundsInParent();
if (nodeBounds.getMaxY() >= clipBounds.getMaxY()) {
scroll(-nodeBounds.getMaxY() + clipBounds.getMaxY());
} else if (nodeBounds.getMinY() <= clipBounds.getMinY()) {
scroll(-nodeBounds.getMinY() + clipBounds.getMinY());
}
}
protected ObservableList<MenuItem> getItems() {
return contextMenu.getItems();
}
private int findFocusedIndex() {
for (int i = 0; i < itemsContainer.getChildren().size(); i++) {
Node n = itemsContainer.getChildren().get(i);
if (n.isFocused()) {
return i;
}
}
return -1;
}
private boolean isFirstShow = true;
private double ty;
private void initialize() {
contextMenu.addEventHandler(Menu.ON_SHOWN, event -> {
currentFocusedIndex = -1;
for (Node child : itemsContainer.getChildren()) {
if (child instanceof MenuItemContainer) {
final MenuItem item = ((MenuItemContainer)child).item;
if ("choice-box-menu-item".equals(item.getId())) {
if (((RadioMenuItem)item).isSelected()) {
child.requestFocus();
break;
}
}
}
}
});
setOnKeyPressed(new EventHandler<KeyEvent>() {
@Override public void handle(KeyEvent ke) {
switch (ke.getCode()) {
case LEFT:
if (getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT) {
processRightKey(ke);
} else {
processLeftKey(ke);
}
break;
case RIGHT:
if (getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT) {
processLeftKey(ke);
} else {
processRightKey(ke);
}
break;
case CANCEL:
ke.consume();
break;
case ESCAPE:
final Node ownerNode = contextMenu.getOwnerNode();
if (! (ownerNode instanceof MenuBarButton)) {
contextMenu.hide();
ke.consume();
}
break;
case DOWN:
move(Direction.NEXT);
ke.consume();
break;
case UP:
move(Direction.PREVIOUS);
ke.consume();
break;
case SPACE:
case ENTER:
selectMenuItem();
ke.consume();
break;
default:
break;
}
if (!ke.isConsumed()) {
final Node ownerNode = contextMenu.getOwnerNode();
if (ownerNode instanceof MenuItemContainer) {
Parent parent = ownerNode.getParent();
while (parent != null && !(parent instanceof ContextMenuContent)) {
parent = parent.getParent();
}
if (parent instanceof ContextMenuContent) {
parent.getOnKeyPressed().handle(ke);
}
} else if (ownerNode instanceof MenuBarButton) {
}
}
}
});
addEventHandler(ScrollEvent.SCROLL, event -> {
final double textDeltaY = event.getTextDeltaY();
final double deltaY = event.getDeltaY();
if ((downArrow.isVisible() && (textDeltaY < 0.0 || deltaY < 0.0)) ||
(upArrow.isVisible() && (textDeltaY > 0.0 || deltaY > 0.0))) {
switch(event.getTextDeltaYUnits()) {
case LINES:
int focusedIndex = findFocusedIndex();
if (focusedIndex == -1) {
focusedIndex = 0;
}
double rowHeight = itemsContainer.getChildren().get(focusedIndex).prefHeight(-1);
scroll(textDeltaY * rowHeight);
break;
case PAGES:
scroll(textDeltaY * itemsContainer.getHeight());
break;
case NONE:
scroll(deltaY);
break;
}
event.consume();
}
});
}
private Optional<Node> getFocusedNode() {
final List<Node> children = itemsContainer.getChildren();
final boolean validIndex = currentFocusedIndex >= 0 && currentFocusedIndex < children.size();
return validIndex ? Optional.of(children.get(currentFocusedIndex)) : Optional.empty();
}
private void processLeftKey(KeyEvent ke) {
getFocusedNode().ifPresent(n -> {
if (n instanceof MenuItemContainer) {
MenuItem item = ((MenuItemContainer)n).item;
if (item instanceof Menu) {
final Menu menu = (Menu) item;
if (menu == openSubmenu && submenu != null && submenu.isShowing()) {
hideSubmenu();
ke.consume();
}
}
}
});
}
private void processRightKey(KeyEvent ke) {
getFocusedNode().ifPresent(n -> {
if (n instanceof MenuItemContainer) {
MenuItem item = ((MenuItemContainer)n).item;
if (item instanceof Menu) {
final Menu menu = (Menu) item;
if (menu.isDisable()) return;
selectedBackground = ((MenuItemContainer)n);
if (openSubmenu == menu && submenu != null && submenu.isShowing()) {
return;
}
showMenu(menu);
ke.consume();
}
}
});
}
private void showMenu(Menu menu) {
menu.show();
if (submenu == null) {
return;
}
ContextMenuContent cmContent = (ContextMenuContent)submenu.getSkin().getNode();
if (cmContent != null) {
if (cmContent.itemsContainer.getChildren().size() > 0) {
cmContent.itemsContainer.getChildren().get(0).requestFocus();
cmContent.currentFocusedIndex = 0;
} else {
cmContent.requestFocus();
}
}
}
private void selectMenuItem() {
getFocusedNode().ifPresent(n -> {
if (n instanceof MenuItemContainer) {
MenuItem item = ((MenuItemContainer)n).item;
if (item instanceof Menu) {
final Menu menu = (Menu) item;
if (openSubmenu != null) {
hideSubmenu();
}
if (menu.isDisable()) return;
selectedBackground = ((MenuItemContainer)n);
menu.show();
} else {
((MenuItemContainer)n).doSelect();
}
}
});
}
private void move(Direction dir) {
int startIndex = currentFocusedIndex != -1 ? currentFocusedIndex : itemsContainer.getChildren().size();
requestFocusOnIndex(findSibling(dir, startIndex));
}
private int findSibling(final Direction dir, final int startIndex) {
final int childCount = itemsContainer.getChildren().size();
int i = startIndex;
do {
if (dir.isForward() && i >= childCount - 1) {
i = 0;
} else if (!dir.isForward() && i == 0) {
i = childCount - 1;
} else {
i += (dir.isForward() ? 1 : -1);
}
Node n = itemsContainer.getChildren().get(i);
if (n instanceof MenuItemContainer && n.isVisible()) {
return i;
}
} while (i != startIndex);
return -1;
}
public void requestFocusOnIndex(int index) {
currentFocusedIndex = index;
Node n = itemsContainer.getChildren().get(index);
selectedBackground = ((MenuItemContainer)n);
n.requestFocus();
ensureFocusedMenuItemIsVisible(n);
}
public double getMenuYOffset(int menuIndex) {
double offset = 0;
if (itemsContainer.getChildren().size() > menuIndex) {
offset = snappedTopInset();
Node menuitem = itemsContainer.getChildren().get(menuIndex);
offset += menuitem.getLayoutY() + menuitem.prefHeight(-1);
}
return offset;
}
private void setUpBinds() {
updateMenuShowingListeners(contextMenu.getItems(), true);
contextMenu.getItems().addListener(contextMenuItemsListener);
}
private void disposeBinds() {
updateMenuShowingListeners(contextMenu.getItems(), false);
contextMenu.getItems().removeListener(contextMenuItemsListener);
}
private ChangeListener<Boolean> menuShowingListener = (observable, wasShowing, isShowing) -> {
ReadOnlyBooleanProperty isShowingProperty = (ReadOnlyBooleanProperty) observable;
Menu menu = (Menu) isShowingProperty.getBean();
if (wasShowing && ! isShowing) {
hideSubmenu();
} else if (! wasShowing && isShowing) {
showSubmenu(menu);
}
};
private ListChangeListener<MenuItem> contextMenuItemsListener = (ListChangeListener<MenuItem>) c -> {
while (c.next()) {
updateMenuShowingListeners(c.getRemoved(), false);
updateMenuShowingListeners(c.getAddedSubList(), true);
}
itemsDirty = true;
updateItems();
};
private ChangeListener<Boolean> menuItemVisibleListener = (observable, oldValue, newValue) -> {
requestLayout();
};
private void updateMenuShowingListeners(List<? extends MenuItem> items, boolean addListeners) {
for (MenuItem item : items) {
if (item instanceof Menu) {
final Menu menu = (Menu) item;
if (addListeners) {
menu.showingProperty().addListener(menuShowingListener);
} else {
menu.showingProperty().removeListener(menuShowingListener);
}
}
if (addListeners) {
item.visibleProperty().addListener(menuItemVisibleListener);
} else {
item.visibleProperty().removeListener(menuItemVisibleListener);
}
}
}
ContextMenu getSubMenu() {
return submenu;
}
Menu getOpenSubMenu() {
return openSubmenu;
}
private void createSubmenu() {
if (submenu == null) {
submenu = new ContextMenu();
submenu.showingProperty().addListener(new ChangeListener<Boolean>() {
@Override public void changed(ObservableValue<? extends Boolean> observable,
Boolean oldValue, Boolean newValue) {
if (!submenu.isShowing()) {
for (Node node : itemsContainer.getChildren()) {
if (node instanceof MenuItemContainer
&& ((MenuItemContainer)node).item instanceof Menu) {
Menu menu = (Menu)((MenuItemContainer)node).item;
if (menu.isShowing()) {
menu.hide();
}
}
}
}
}
});
}
}
private void showSubmenu(Menu menu) {
openSubmenu = menu;
createSubmenu();
submenu.getItems().setAll(menu.getItems());
submenu.show(selectedBackground, Side.RIGHT, 0, 0);
}
private void hideSubmenu() {
if (submenu == null) return;
submenu.hide();
openSubmenu = null;
disposeContextMenu(submenu);
submenu = null;
getFocusedNode().ifPresent(n -> {
requestFocus();
n.requestFocus();
});
}
private void hideAllMenus(MenuItem item) {
if (contextMenu != null) contextMenu.hide();
Menu parentMenu;
while ((parentMenu = item.getParentMenu()) != null) {
parentMenu.hide();
item = parentMenu;
}
if (item.getParentPopup() != null) {
item.getParentPopup().hide();
}
}
private Menu openSubmenu;
private ContextMenu submenu;
Region selectedBackground;
void scroll(double delta) {
double newTy = ty + delta;
if (ty == newTy) return;
if (newTy > 0.0) {
newTy = 0.0;
}
if (delta < 0 && (getHeight() - newTy) > itemsContainer.getHeight() - downArrow.getHeight()) {
newTy = getHeight() - itemsContainer.getHeight() - downArrow.getHeight();
}
ty = newTy;
itemsContainer.requestLayout();
}
@Override public Styleable getStyleableParent() {
return contextMenu;
}
private static class StyleableProperties {
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Region.getClassCssMetaData());
final List<CssMetaData<? extends Styleable, ?>> nodeStyleables = Node.getClassCssMetaData();
for(int n=0, max=nodeStyleables.size(); n<max; n++) {
CssMetaData<? extends Styleable, ?> styleable = nodeStyleables.get(n);
if ("effect".equals(styleable.getProperty())) {
styleables.add(styleable);
break;
}
}
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
public Label getLabelAt(int index) {
return ((MenuItemContainer)itemsContainer.getChildren().get(index)).getLabel();
}
class MenuBox extends VBox {
MenuBox() {
setAccessibleRole(AccessibleRole.CONTEXT_MENU);
}
@Override protected void layoutChildren() {
double yOffset = ty;
for (Node n : getChildren()) {
if (n.isVisible()) {
final double prefHeight = snapSizeY(n.prefHeight(-1));
n.resize(snapSizeX(getWidth()), prefHeight);
n.relocate(snappedLeftInset(), yOffset);
yOffset += prefHeight;
}
}
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case VISIBLE: return contextMenu.isShowing();
case PARENT_MENU: return contextMenu.getOwnerNode();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
}
class ArrowMenuItem extends StackPane {
private StackPane upDownArrow;
private ContextMenuContent popupMenuContent;
private boolean up = false;
public final boolean isUp() { return up; }
public void setUp(boolean value) {
up = value;
upDownArrow.getStyleClass().setAll(isUp() ? "menu-up-arrow" : "menu-down-arrow");
}
private Timeline scrollTimeline;
public ArrowMenuItem(ContextMenuContent pmc) {
getStyleClass().setAll("scroll-arrow");
upDownArrow = new StackPane();
this.popupMenuContent = pmc;
upDownArrow.setMouseTransparent(true);
upDownArrow.getStyleClass().setAll(isUp() ? "menu-up-arrow" : "menu-down-arrow");
addEventHandler(MouseEvent.MOUSE_ENTERED, me -> {
if (scrollTimeline != null && (scrollTimeline.getStatus() != Status.STOPPED)) {
return;
}
startTimeline();
});
addEventHandler(MouseEvent.MOUSE_EXITED, me -> {
stopTimeline();
});
setVisible(false);
setManaged(false);
getChildren().add(upDownArrow);
}
@Override protected double computePrefWidth(double height) {
return itemsContainer.getWidth();
}
@Override protected double computePrefHeight(double width) {
return snappedTopInset() + upDownArrow.prefHeight(-1) + snappedBottomInset();
}
@Override protected void layoutChildren() {
double w = snapSizeX(upDownArrow.prefWidth(-1));
double h = snapSizeY(upDownArrow.prefHeight(-1));
upDownArrow.resize(w, h);
positionInArea(upDownArrow, 0, 0, getWidth(), getHeight(),
0, HPos.CENTER, VPos.CENTER);
}
private void adjust() {
if(up) popupMenuContent.scroll(12); else popupMenuContent.scroll(-12);
}
private void startTimeline() {
scrollTimeline = new Timeline();
scrollTimeline.setCycleCount(Timeline.INDEFINITE);
KeyFrame kf = new KeyFrame(
Duration.millis(60),
event -> {
adjust();
}
);
scrollTimeline.getKeyFrames().clear();
scrollTimeline.getKeyFrames().add(kf);
scrollTimeline.play();
}
private void stopTimeline() {
scrollTimeline.stop();
scrollTimeline = null;
}
}
public class MenuItemContainer extends Region {
private final MenuItem item;
private Node left;
private Node graphic;
private Node label;
private Node right;
private final LambdaMultiplePropertyChangeListenerHandler listener =
new LambdaMultiplePropertyChangeListenerHandler();
private EventHandler<MouseEvent> mouseEnteredEventHandler;
private EventHandler<MouseEvent> mouseReleasedEventHandler;
private EventHandler<ActionEvent> actionEventHandler;
protected Label getLabel(){
return (Label) label;
}
public MenuItem getItem() {
return item;
}
public MenuItemContainer(MenuItem item){
if (item == null) {
throw new NullPointerException("MenuItem can not be null");
}
getStyleClass().addAll(item.getStyleClass());
setId(item.getId());
setFocusTraversable(!(item instanceof CustomMenuItem));
this.item = item;
createChildren();
ReadOnlyBooleanProperty pseudoProperty;
if (item instanceof Menu) {
pseudoProperty = ((Menu)item).showingProperty();
listener.registerChangeListener(pseudoProperty,
e -> pseudoClassStateChanged(SELECTED_PSEUDOCLASS_STATE, ((Menu) item).isShowing()));
pseudoClassStateChanged(SELECTED_PSEUDOCLASS_STATE, pseudoProperty.get());
setAccessibleRole(AccessibleRole.MENU);
} else if (item instanceof RadioMenuItem) {
pseudoProperty = ((RadioMenuItem)item).selectedProperty();
listener.registerChangeListener(pseudoProperty,
e -> pseudoClassStateChanged(CHECKED_PSEUDOCLASS_STATE, ((RadioMenuItem) item).isSelected()));
pseudoClassStateChanged(CHECKED_PSEUDOCLASS_STATE, pseudoProperty.get());
setAccessibleRole(AccessibleRole.RADIO_MENU_ITEM);
} else if (item instanceof CheckMenuItem) {
pseudoProperty = ((CheckMenuItem)item).selectedProperty();
listener.registerChangeListener(pseudoProperty,
e -> pseudoClassStateChanged(CHECKED_PSEUDOCLASS_STATE, ((CheckMenuItem) item).isSelected()));
pseudoClassStateChanged(CHECKED_PSEUDOCLASS_STATE, pseudoProperty.get());
setAccessibleRole(AccessibleRole.CHECK_MENU_ITEM);
} else {
setAccessibleRole(AccessibleRole.MENU_ITEM);
}
pseudoClassStateChanged(DISABLED_PSEUDOCLASS_STATE, item.disableProperty().get());
listener.registerChangeListener(item.disableProperty(),
e -> pseudoClassStateChanged(DISABLED_PSEUDOCLASS_STATE, item.isDisable()));
getProperties().put(MenuItem.class, item);
listener.registerChangeListener(item.graphicProperty(), e -> {
createChildren();
computeVisualMetrics();
});
actionEventHandler = e -> {
if (item instanceof Menu) {
final Menu menu = (Menu) item;
if (openSubmenu == menu && submenu.isShowing()) return;
if (openSubmenu != null) {
hideSubmenu();
}
selectedBackground = MenuItemContainer.this;
showMenu(menu);
} else {
doSelect();
}
};
addEventHandler(ActionEvent.ACTION, actionEventHandler);
}
public void dispose() {
if (item instanceof CustomMenuItem) {
Node node = ((CustomMenuItem)item).getContent();
if (node != null) {
node.removeEventHandler(MouseEvent.MOUSE_CLICKED, customMenuItemMouseClickedHandler);
}
}
listener.dispose();
removeEventHandler(ActionEvent.ACTION, actionEventHandler);
if (label != null) {
((Label)label).textProperty().unbind();
label.styleProperty().unbind();
label.idProperty().unbind();
ListChangeListener<String> itemStyleClassListener = (ListChangeListener<String>)item.getProperties().remove(ITEM_STYLE_CLASS_LISTENER);
if (itemStyleClassListener != null) {
item.getStyleClass().removeListener(itemStyleClassListener);
}
}
left = null;
graphic = null;
label = null;
right = null;
}
private void createChildren() {
getChildren().clear();
if (item instanceof CustomMenuItem) {
createNodeMenuItemChildren((CustomMenuItem)item);
if (mouseEnteredEventHandler == null) {
mouseEnteredEventHandler = event -> {
requestFocus();
};
} else {
removeEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredEventHandler);
}
addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredEventHandler);
} else {
Node leftNode = getLeftGraphic(item);
if (leftNode != null) {
StackPane leftPane = new StackPane();
leftPane.getStyleClass().add("left-container");
leftPane.getChildren().add(leftNode);
left = leftPane;
getChildren().add(left);
left.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
}
if (item.getGraphic() != null) {
Node graphicNode = item.getGraphic();
StackPane graphicPane = new StackPane();
graphicPane.getStyleClass().add("graphic-container");
graphicPane.getChildren().add(graphicNode);
graphic = graphicPane;
getChildren().add(graphic);
}
label = new MenuLabel(item, this);
((Label)label).textProperty().bind(item.textProperty());
label.styleProperty().bind(item.styleProperty());
label.idProperty().bind(item.styleProperty());
ListChangeListener<String> itemStyleClassListener = c -> {
while (c.next()) {
label.getStyleClass().removeAll(c.getRemoved());
label.getStyleClass().addAll(c.getAddedSubList());
}
};
item.getStyleClass().addListener(itemStyleClassListener);
item.getProperties().put(ITEM_STYLE_CLASS_LISTENER, itemStyleClassListener);
label.setMouseTransparent(true);
getChildren().add(label);
listener.unregisterChangeListeners(focusedProperty());
listener.registerChangeListener(focusedProperty(), e -> {
if (isFocused()) {
currentFocusedIndex = itemsContainer.getChildren().indexOf(MenuItemContainer.this);
}
});
if (item instanceof Menu) {
Region rightNode = new Region();
rightNode.setMouseTransparent(true);
rightNode.getStyleClass().add("arrow");
StackPane rightPane = new StackPane();
rightPane.setMaxWidth(Math.max(rightNode.prefWidth(-1), 10));
rightPane.setMouseTransparent(true);
rightPane.getStyleClass().add("right-container");
rightPane.getChildren().add(rightNode);
right = rightPane;
getChildren().add(rightPane);
if (mouseEnteredEventHandler == null) {
mouseEnteredEventHandler = event -> {
if (openSubmenu != null && item != openSubmenu) {
hideSubmenu();
}
selectedBackground = MenuItemContainer.this;
requestFocus();
final Menu menu = (Menu) item;
if (menu.isDisable()) return;
menu.show();
};
} else {
removeEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredEventHandler);
}
if (mouseReleasedEventHandler == null) {
mouseReleasedEventHandler = event -> {
item.fire();
};
} else {
removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedEventHandler);
}
addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredEventHandler);
addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedEventHandler);
} else {
listener.unregisterChangeListeners(item.acceleratorProperty());
updateAccelerator();
if (mouseEnteredEventHandler == null) {
mouseEnteredEventHandler = event -> {
if (openSubmenu != null) {
openSubmenu.hide();
}
requestFocus();
};
} else {
removeEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredEventHandler);
}
if (mouseReleasedEventHandler == null) {
mouseReleasedEventHandler = event -> {
doSelect();
};
} else {
removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedEventHandler);
}
addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredEventHandler);
addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedEventHandler);
listener.registerChangeListener(item.acceleratorProperty(), e -> updateAccelerator());
}
}
}
private void updateAccelerator() {
if (item.getAccelerator() != null) {
if (right != null) {
getChildren().remove(right);
}
String text = item.getAccelerator().getDisplayText();
right = new Label(text);
right.setStyle(item.getStyle());
right.getStyleClass().add("accelerator-text");
getChildren().add(right);
} else {
getChildren().remove(right);
}
}
void doSelect() {
if (item.isDisable()) return;
if (item instanceof CheckMenuItem) {
CheckMenuItem checkItem = (CheckMenuItem)item;
checkItem.setSelected(!checkItem.isSelected());
} else if (item instanceof RadioMenuItem) {
final RadioMenuItem radioItem = (RadioMenuItem) item;
radioItem.setSelected(radioItem.getToggleGroup() != null ? true : !radioItem.isSelected());
}
item.fire();
if (item instanceof CustomMenuItem) {
CustomMenuItem customMenuItem = (CustomMenuItem) item;
if (customMenuItem.isHideOnClick()) {
hideAllMenus(item);
}
} else {
hideAllMenus(item);
}
}
private EventHandler<MouseEvent> customMenuItemMouseClickedHandler;
private void createNodeMenuItemChildren(final CustomMenuItem item) {
Node node = item.getContent();
getChildren().add(node);
customMenuItemMouseClickedHandler = event -> {
if (item == null || item.isDisable()) return;
item.fire();
if (item.isHideOnClick()) {
hideAllMenus(item);
}
};
node.addEventHandler(MouseEvent.MOUSE_CLICKED, customMenuItemMouseClickedHandler);
}
@Override protected void layoutChildren() {
double xOffset;
final double prefHeight = prefHeight(-1);
if (left != null) {
xOffset = snappedLeftInset();
left.resize(left.prefWidth(-1), left.prefHeight(-1));
positionInArea(left, xOffset, 0,
maxLeftWidth, prefHeight, 0, HPos.LEFT, VPos.CENTER);
}
if (graphic != null) {
xOffset = snappedLeftInset() + maxLeftWidth;
graphic.resize(graphic.prefWidth(-1), graphic.prefHeight(-1));
positionInArea(graphic, xOffset, 0,
maxGraphicWidth, prefHeight, 0, HPos.LEFT, VPos.CENTER);
}
if (label != null) {
xOffset = snappedLeftInset() + maxLeftWidth + maxGraphicWidth;
label.resize(label.prefWidth(-1), label.prefHeight(-1));
positionInArea(label, xOffset, 0,
maxLabelWidth, prefHeight, 0, HPos.LEFT, VPos.CENTER);
}
if (right != null) {
xOffset = snappedLeftInset() + maxLeftWidth + maxGraphicWidth + maxLabelWidth;
right.resize(right.prefWidth(-1), right.prefHeight(-1));
positionInArea(right, xOffset, 0,
maxRightWidth, prefHeight, 0, HPos.RIGHT, VPos.CENTER);
}
if ( item instanceof CustomMenuItem) {
Node n = ((CustomMenuItem) item).getContent();
if (item instanceof SeparatorMenuItem) {
double width = prefWidth(-1) - (snappedLeftInset() + maxGraphicWidth + snappedRightInset());
n.resize(width, n.prefHeight(-1));
positionInArea(n, snappedLeftInset() + maxGraphicWidth, 0, prefWidth(-1), prefHeight, 0, HPos.LEFT, VPos.CENTER);
} else {
n.resize(n.prefWidth(-1), n.prefHeight(-1));
positionInArea(n, snappedLeftInset(), 0, getWidth(), prefHeight, 0, HPos.LEFT, VPos.CENTER);
}
}
}
@Override protected double computePrefHeight(double width) {
double prefHeight = 0;
if (item instanceof CustomMenuItem || item instanceof SeparatorMenuItem) {
prefHeight = (getChildren().isEmpty()) ? 0 : getChildren().get(0).prefHeight(-1);
} else {
prefHeight = Math.max(prefHeight, (left != null) ? left.prefHeight(-1) : 0);
prefHeight = Math.max(prefHeight, (graphic != null) ? graphic.prefHeight(-1) : 0);
prefHeight = Math.max(prefHeight, (label != null) ? label.prefHeight(-1) : 0);
prefHeight = Math.max(prefHeight, (right != null) ? right.prefHeight(-1) : 0);
}
return snappedTopInset() + prefHeight + snappedBottomInset();
}
@Override protected double computePrefWidth(double height) {
double nodeMenuItemWidth = 0;
if (item instanceof CustomMenuItem && !(item instanceof SeparatorMenuItem)) {
nodeMenuItemWidth = snappedLeftInset() + ((CustomMenuItem) item).getContent().prefWidth(-1) +
snappedRightInset();
}
return Math.max(nodeMenuItemWidth,
snappedLeftInset() + maxLeftWidth + maxGraphicWidth +
maxLabelWidth + maxRightWidth + snappedRightInset());
}
private Node getLeftGraphic(MenuItem item) {
if (item instanceof RadioMenuItem) {
final Region _graphic = new Region();
_graphic.getStyleClass().add("radio");
return _graphic;
} else if (item instanceof CheckMenuItem) {
final StackPane _graphic = new StackPane();
_graphic.getStyleClass().add("check");
return _graphic;
}
return null;
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case SELECTED:
if (item instanceof CheckMenuItem) {
return ((CheckMenuItem)item).isSelected();
}
if (item instanceof RadioMenuItem) {
return ((RadioMenuItem) item).isSelected();
}
return false;
case ACCELERATOR: return item.getAccelerator();
case TEXT: {
String title = "";
if (graphic != null) {
String t = (String)graphic.queryAccessibleAttribute(AccessibleAttribute.TEXT);
if (t != null) title += t;
}
final Label label = getLabel();
if (label != null) {
String t = (String)label.queryAccessibleAttribute(AccessibleAttribute.TEXT);
if (t != null) title += t;
}
if (item instanceof CustomMenuItem) {
Node content = ((CustomMenuItem) item).getContent();
if (content != null) {
String t = (String)content.queryAccessibleAttribute(AccessibleAttribute.TEXT);
if (t != null) title += t;
}
}
return title;
}
case MNEMONIC: {
final Label label = getLabel();
if (label != null) {
String mnemonic = (String)label.queryAccessibleAttribute(AccessibleAttribute.MNEMONIC);
if (mnemonic != null) return mnemonic;
}
return null;
}
case DISABLED: return item.isDisable();
case SUBMENU:
createSubmenu();
if (submenu.getSkin() == null) {
submenu.getStyleableNode().applyCss();
}
ContextMenuContent cmContent = (ContextMenuContent)submenu.getSkin().getNode();
return cmContent.itemsContainer;
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
@Override
public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case SHOW_MENU:{
if (item instanceof Menu) {
final Menu menuItem = (Menu) item;
if (menuItem.isShowing()) {
menuItem.hide();
} else {
menuItem.show();
}
}
break;
}
case FIRE:
doSelect();
break;
default: super.executeAccessibleAction(action);
}
}
}
private static final PseudoClass SELECTED_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("selected");
private static final PseudoClass DISABLED_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("disabled");
private static final PseudoClass CHECKED_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("checked");
private class MenuLabel extends Label {
public MenuLabel(MenuItem item, MenuItemContainer mic) {
super(item.getText());
setMnemonicParsing(item.isMnemonicParsing());
setLabelFor(mic);
}
}
}
