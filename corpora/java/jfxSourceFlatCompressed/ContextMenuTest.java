package test.javafx.scene.control;
import com.sun.javafx.scene.control.ContextMenuContent;
import com.sun.javafx.scene.control.ContextMenuContentShim;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static com.sun.javafx.scene.control.ContextMenuContentShim.*;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import test.com.sun.javafx.scene.control.infrastructure.KeyEventFirer;
import test.com.sun.javafx.scene.control.infrastructure.MouseEventFirer;
public class ContextMenuTest {
public static void pressDownKey(ContextMenu menu) {
Optional<ContextMenuContent> showingMenuContent = ContextMenuContentShim.getShowingMenuContent(menu);
if (showingMenuContent.isPresent()) {
ContextMenuContent content = showingMenuContent.get();
new KeyEventFirer(content).doDownArrowPress();
}
}
public static void pressUpKey(ContextMenu menu) {
Optional<ContextMenuContent> showingMenuContent = ContextMenuContentShim.getShowingMenuContent(menu);
if (showingMenuContent.isPresent()) {
ContextMenuContent content = showingMenuContent.get();
new KeyEventFirer(content).doUpArrowPress();
}
}
public static void pressRightKey(ContextMenu menu) {
Optional<ContextMenuContent> showingMenuContent = ContextMenuContentShim.getShowingMenuContent(menu);
if (showingMenuContent.isPresent()) {
ContextMenuContent content = showingMenuContent.get();
new KeyEventFirer(content).doRightArrowPress();
}
}
public static void pressEnterKey(ContextMenu menu) {
Optional<ContextMenuContent> showingMenuContent = ContextMenuContentShim.getShowingMenuContent(menu);
if (showingMenuContent.isPresent()) {
ContextMenuContent content = showingMenuContent.get();
new KeyEventFirer(content).doKeyPress(KeyCode.ENTER);
}
}
public static void pressLeftKey(ContextMenu menu) {
Optional<ContextMenuContent> showingMenuContent = ContextMenuContentShim.getShowingMenuContent(menu);
if (showingMenuContent.isPresent()) {
ContextMenuContent content = showingMenuContent.get();
new KeyEventFirer(content).doLeftArrowPress();
}
}
public static void pressMouseButton(ContextMenu menu) {
Optional<ContextMenuContent> showingMenuContent = ContextMenuContentShim.getShowingMenuContent(menu);
if (showingMenuContent.isPresent()) {
ContextMenuContent.MenuItemContainer itemContainer = (ContextMenuContent.MenuItemContainer)
ContextMenuContentShim.get_selectedBackground(showingMenuContent.get());
MenuItem item = itemContainer.getItem();
if (item instanceof CustomMenuItem) {
Node customContent = ((CustomMenuItem) item).getContent();
new MouseEventFirer(customContent).fireMouseClicked();
} else {
new MouseEventFirer(itemContainer).fireMousePressAndRelease();
}
}
}
private MenuItem menuItem0, menuItem1, menuItem2, menuItem3;
private ContextMenu contextMenu;
private ContextMenu contextMenuWithOneItem;
private ContextMenu contextMenuWithManyItems;
private StageLoader sl;
private Button anchorBtn;
@Before public void setup() {
menuItem0 = new MenuItem("0");
menuItem1 = new MenuItem("1");
menuItem2 = new MenuItem("2");
menuItem3 = new MenuItem("3");
contextMenu = new ContextMenu();
contextMenuWithOneItem = new ContextMenu(menuItem0);
contextMenuWithManyItems = new ContextMenu(menuItem1, menuItem2, menuItem3);
menuItem = new MenuItem("MenuItem 1");
subMenu = new Menu("submenu");
subMenuItem1 = new MenuItem("SubMenuItem 1");
customMenuItem = new CustomMenuItem(new Label("CustomMenuItem 1"));
subMenu.getItems().setAll(subMenuItem1, customMenuItem);
anchorBtn = new Button("Anchor");
sl = new StageLoader(anchorBtn);
}
@After public void after() {
sl.dispose();
}
@Test public void defaultGetId() {
assertNull(contextMenu.getId());
}
@Test public void getStyleClassNotNull() {
assertNotNull(contextMenu.getStyleClass());
}
@Test public void shouldBeAutoHideOn() {
assertTrue(contextMenu.isAutoHide());
}
@Test public void shouldHaveZeroItems() {
assertEquals(0, contextMenu.getItems().size());
}
@Test public void shouldHaveOneItem() {
assertEquals(1, contextMenuWithOneItem.getItems().size());
}
@Test public void shouldHaveManyItems() {
assertEquals(3, contextMenuWithManyItems.getItems().size());
}
@Test public void getDefaultSetOnActionHandler() {
assertNull(contextMenu.getOnAction());
}
@Test public void getSpecifiedSetOnActionHandler() {
EventHandlerStub handler = new EventHandlerStub();
contextMenu.setOnAction(handler);
assertEquals(handler, contextMenu.getOnAction());
}
@Test public void setTwiceAndGetSpecifiedSetOnActionHandler() {
EventHandlerStub handler1 = new EventHandlerStub();
EventHandlerStub handler2 = new EventHandlerStub();
contextMenu.setOnAction(handler1);
contextMenu.setOnAction(handler2);
assertEquals(handler2, contextMenu.getOnAction());
}
@Test public void getNullSetOnActionHandler() {
contextMenu.setOnAction(null);
assertNull(contextMenu.getOnAction());
}
@Test public void defaultOnActionPropertyNotNull() {
assertNotNull(contextMenu.onActionProperty());
}
@Test public void getOnActionPropertyBean() {
assertEquals(contextMenu, contextMenu.onActionProperty().getBean());
}
@Test public void getOnActionPropertyName() {
assertEquals("onAction", contextMenu.onActionProperty().getName());
}
@Test public void removedItemsAreChanged() {
contextMenuWithManyItems.getItems().remove(menuItem2);
assertNull(menuItem2.getParentPopup());
}
@Test public void addedItemsAreChanged() {
MenuItem addedMenuItem = new MenuItem();
contextMenuWithManyItems.getItems().add(addedMenuItem);
assertEquals(contextMenuWithManyItems, addedMenuItem.getParentPopup());
}
@Test public void test_rt_34106_menus_should_not_be_reused() {
MenuItem item1 = new MenuItem("MenuItem 1");
Menu menu = new Menu("Menu");
menu.getItems().addAll(item1);
ContextMenu cm1 = new ContextMenu(menu);
assertEquals(1, cm1.getItems().size());
assertEquals(menu, cm1.getItems().get(0));
assertEquals(cm1, menu.getParentPopup());
assertEquals(cm1, item1.getParentPopup());
ContextMenu cm2 = new ContextMenu(menu);
assertEquals(0, cm1.getItems().size());
assertEquals(1, cm2.getItems().size());
assertEquals(menu, cm2.getItems().get(0));
assertEquals(cm2, menu.getParentPopup());
assertEquals(cm2, item1.getParentPopup());
}
public static final class EventHandlerStub implements EventHandler<ActionEvent> {
boolean called = false;
@Override public void handle(ActionEvent event) {
called = true;
}
};
private MenuItem menuItem;
private Menu subMenu;
private MenuItem subMenuItem1;
private CustomMenuItem customMenuItem;
private ContextMenu createContextMenu(boolean showMenu) {
ContextMenu contextMenu = new ContextMenu(menuItem, subMenu);
if (showMenu) {
contextMenu.show(anchorBtn, Side.RIGHT, 0, 0);
}
return contextMenu;
}
private void showMenu(ContextMenu cm, MenuItem... browseTo) {
cm.show(anchorBtn, Side.RIGHT, 0, 0);
if (browseTo == null) return;
for (int i = 0; i < browseTo.length; i++) {
MenuItem item = browseTo[i];
boolean found = false;
while (true) {
MenuItem focusedItem = getCurrentFocusedItem(cm);
if (item == focusedItem) {
found = true;
break;
}
pressDownKey(cm);
}
if (! found) {
break;
} else {
if (item instanceof Menu) {
pressRightKey(cm);
}
}
}
}
private ContextMenu createContextMenuAndShowSubMenu() {
ContextMenu cm = createContextMenu(true);
pressDownKey(cm);
pressDownKey(cm);
assertFalse(subMenu.isShowing());
pressRightKey(cm);
assertTrue(subMenu.isShowing());
assertEquals(subMenu, getShowingSubMenu(cm));
MenuItem focusedItem = getCurrentFocusedItem(cm);
assertEquals(0, getCurrentFocusedIndex(cm));
assertEquals("Expected " + subMenuItem1.getText() + ", found " + focusedItem.getText(),
subMenuItem1, focusedItem);
return cm;
}
@Test public void test_showAndHide() {
ContextMenu cm = createContextMenu(false);
assertFalse(cm.isShowing());
cm.show(anchorBtn, Side.RIGHT, 0, 0);
assertTrue(cm.isShowing());
cm.hide();
assertFalse(cm.isShowing());
}
@Test public void test_navigateMenu_downwards() {
ContextMenu cm = createContextMenu(true);
assertNotNull(getShowingMenuContent(cm));
assertEquals(-1, getCurrentFocusedIndex(cm));
pressDownKey(cm);
MenuItem focusedItem = getCurrentFocusedItem(cm);
assertEquals(0, getCurrentFocusedIndex(cm));
assertEquals("Expected " + menuItem.getText() + ", found " + focusedItem.getText(),
menuItem, focusedItem);
pressDownKey(cm);
focusedItem = getCurrentFocusedItem(cm);
assertEquals(1, getCurrentFocusedIndex(cm));
assertEquals("Expected " + subMenu.getText() + ", found " + focusedItem.getText(),
subMenu, focusedItem);
assertFalse(subMenu.isShowing());
pressDownKey(cm);
focusedItem = getCurrentFocusedItem(cm);
assertEquals(0, getCurrentFocusedIndex(cm));
assertEquals("Expected " + menuItem.getText() + ", found " + focusedItem.getText(),
menuItem, focusedItem);
}
@Test public void test_navigateMenu_upwards() {
ContextMenu cm = createContextMenu(true);
assertNotNull(getShowingMenuContent(cm));
assertEquals(-1, getCurrentFocusedIndex(cm));
pressUpKey(cm);
MenuItem focusedItem = getCurrentFocusedItem(cm);
assertEquals(1, getCurrentFocusedIndex(cm));
assertEquals("Expected " + subMenu.getText() + ", found " + focusedItem.getText(),
subMenu, focusedItem);
pressDownKey(cm);
focusedItem = getCurrentFocusedItem(cm);
assertEquals(0, getCurrentFocusedIndex(cm));
assertEquals("Expected " + menuItem.getText() + ", found " + focusedItem.getText(),
menuItem, focusedItem);
}
@Test public void test_navigateMenu_showSubMenu() {
createContextMenuAndShowSubMenu();
}
@Test public void test_navigateSubMenu_downwards() {
ContextMenu cm = createContextMenuAndShowSubMenu();
assertNotNull(getShowingMenuContent(cm));
MenuItem focusedItem = getCurrentFocusedItem(cm);
assertEquals(0, getCurrentFocusedIndex(cm));
assertEquals("Expected " + subMenuItem1.getText() + ", found " + focusedItem.getText(),
subMenuItem1, focusedItem);
pressDownKey(cm);
focusedItem = getCurrentFocusedItem(cm);
assertEquals(1, getCurrentFocusedIndex(cm));
assertEquals("Expected " + customMenuItem.getText() + ", found " + focusedItem.getText(),
customMenuItem, focusedItem);
pressDownKey(cm);
focusedItem = getCurrentFocusedItem(cm);
assertEquals(0, getCurrentFocusedIndex(cm));
assertEquals("Expected " + subMenuItem1.getText() + ", found " + focusedItem.getText(),
subMenuItem1, focusedItem);
}
@Test public void test_navigateSubMenu_upwards() {
ContextMenu cm = createContextMenuAndShowSubMenu();
assertNotNull(getShowingMenuContent(cm));
MenuItem focusedItem = getCurrentFocusedItem(cm);
assertEquals(0, getCurrentFocusedIndex(cm));
assertEquals("Expected " + subMenuItem1.getText() + ", found " + focusedItem.getText(),
subMenuItem1, focusedItem);
pressUpKey(cm);
focusedItem = getCurrentFocusedItem(cm);
assertEquals(1, getCurrentFocusedIndex(cm));
assertEquals("Expected " + customMenuItem.getText() + ", found " + focusedItem.getText(),
customMenuItem, focusedItem);
pressUpKey(cm);
focusedItem = getCurrentFocusedItem(cm);
assertEquals(0, getCurrentFocusedIndex(cm));
assertEquals("Expected " + subMenuItem1.getText() + ", found " + focusedItem.getText(),
subMenuItem1, focusedItem);
}
@Test public void test_navigateSubMenu_rightKeyDoesNothing() {
ContextMenu cm = createContextMenuAndShowSubMenu();
pressRightKey(cm);
assertNotNull(getShowingMenuContent(cm));
MenuItem focusedItem = getCurrentFocusedItem(cm);
assertEquals(0, getCurrentFocusedIndex(cm));
assertEquals("Expected " + subMenuItem1.getText() + ", found " + focusedItem.getText(),
subMenuItem1, focusedItem);
}
@Test public void test_emptySubMenu_rightKeyDoesNothing() {
Menu testMenu = new Menu("Menu1");
ContextMenu testCM = new ContextMenu();
testCM.getItems().addAll(testMenu);
testCM.show(anchorBtn, Side.RIGHT, 0, 0);
assertNotNull(getShowingMenuContent(testCM));
pressDownKey(testCM);
pressRightKey(testCM);
}
@Test public void test_navigateSubMenu_leftKeyClosesSubMenu() {
ContextMenu cm = createContextMenuAndShowSubMenu();
pressLeftKey(cm);
assertNotNull(getShowingMenuContent(cm));
MenuItem focusedItem = getCurrentFocusedItem(cm);
assertEquals(1, getCurrentFocusedIndex(cm));
assertEquals("Expected " + subMenu.getText() + ", found " + focusedItem.getText(),
subMenu, focusedItem);
}
private int rt_37127_count = 0;
@Test public void test_rt_37127_keyboard() {
ContextMenu cm = createContextMenuAndShowSubMenu();
customMenuItem.setOnAction(event -> rt_37127_count++);
pressDownKey(cm);
MenuItem focusedItem = getCurrentFocusedItem(cm);
assertEquals(1, getCurrentFocusedIndex(cm));
assertEquals(customMenuItem, focusedItem);
assertEquals(0, rt_37127_count);
pressEnterKey(cm);
assertEquals(1, rt_37127_count);
showMenu(cm, subMenu, customMenuItem);
pressEnterKey(cm);
assertEquals(2, rt_37127_count);
showMenu(cm, subMenu, customMenuItem);
pressEnterKey(cm);
assertEquals(3, rt_37127_count);
}
@Test public void test_rt_37127_mouse() {
ContextMenu cm = createContextMenuAndShowSubMenu();
customMenuItem.setOnAction(event -> rt_37127_count++);
pressDownKey(cm);
MenuItem focusedItem = getCurrentFocusedItem(cm);
assertEquals(1, getCurrentFocusedIndex(cm));
assertEquals(customMenuItem, focusedItem);
assertEquals(0, rt_37127_count);
pressMouseButton(cm);
assertEquals(1, rt_37127_count);
showMenu(cm, subMenu, customMenuItem);
pressMouseButton(cm);
assertEquals(2, rt_37127_count);
showMenu(cm, subMenu, customMenuItem);
pressMouseButton(cm);
assertEquals(3, rt_37127_count);
}
@Test public void test_rt_37102() {
ContextMenu cm = createContextMenuAndShowSubMenu();
pressLeftKey(cm);
showMenu(cm, subMenu);
}
@Test public void test_rt_37091() {
ContextMenu cm = createContextMenuAndShowSubMenu();
assertEquals(subMenu, getShowingSubMenu(cm));
assertEquals(subMenu, getOpenSubMenu(cm));
cm.hide();
assertNull(getOpenSubMenu(cm));
cm.getItems().clear();
cm.getItems().add(subMenu);
assertNull(getOpenSubMenu(cm));
cm.show(anchorBtn, Side.RIGHT, 0, 0);
pressDownKey(cm);
pressDownKey(cm);
pressRightKey(cm);
assertEquals(subMenu, getShowingSubMenu(cm));
assertEquals(subMenuItem1, getCurrentFocusedItem(cm));
}
@Test public void test_navigateMenu_withInvisibleItems_rt40689() {
ContextMenu cm = contextMenuWithManyItems;
cm.show(anchorBtn, Side.RIGHT, 0, 0);
menuItem2.setVisible(false);
assertNotNull(getShowingMenuContent(cm));
assertEquals(-1, getCurrentFocusedIndex(cm));
pressDownKey(cm);
MenuItem focusedItem = getCurrentFocusedItem(cm);
assertEquals("Expected " + menuItem1.getText() + ", found " + focusedItem.getText(), menuItem1, focusedItem);
pressDownKey(cm);
focusedItem = getCurrentFocusedItem(cm);
assertEquals("Expected " + menuItem3.getText() + ", found " + focusedItem.getText(), menuItem3, focusedItem);
pressUpKey(cm);
focusedItem = getCurrentFocusedItem(cm);
assertEquals("Expected " + menuItem1.getText() + ", found " + focusedItem.getText(), menuItem1, focusedItem);
}
@Test public void test_jdk_8167132_issue_1() {
ContextMenu cm = createContextMenu(true);
MenuItem item1, item2, item3, item4;
cm.getItems().setAll(
item1 = new MenuItem("Item 1"),
item2 = new MenuItem("Item 2"),
item3 = new MenuItem("Item 3"),
item4 = new MenuItem("Item 4"));
assertEquals(-1, getCurrentFocusedIndex(cm));
pressDownKey(cm);
MenuItem focusedItem = getCurrentFocusedItem(cm);
assertEquals(0, getCurrentFocusedIndex(cm));
assertEquals("Expected " + item1.getText() + ", found " + focusedItem.getText(),
item1, focusedItem);
pressDownKey(cm);
focusedItem = getCurrentFocusedItem(cm);
assertEquals(1, getCurrentFocusedIndex(cm));
assertEquals("Expected " + item2.getText() + ", found " + focusedItem.getText(),
item2, focusedItem);
cm.hide();
cm.show(anchorBtn, Side.RIGHT, 0, 0);
focusedItem = getCurrentFocusedItem(cm);
assertEquals(-1, getCurrentFocusedIndex(cm));
assertNull(focusedItem);
pressDownKey(cm);
focusedItem = getCurrentFocusedItem(cm);
assertEquals(0, getCurrentFocusedIndex(cm));
assertEquals("Expected " + item1.getText() + ", found " + focusedItem.getText(),
item1, focusedItem);
}
@Test public void test_position_showOnScreen() {
ContextMenu cm = createContextMenu(false);
cm.show(anchorBtn, 100, 100);
assertEquals(100, cm.getAnchorX(), 0.0);
assertEquals(100, cm.getAnchorY(), 0.0);
}
@Test public void test_position_showOnTop() throws InterruptedException {
ContextMenu cm = createContextMenu(false);
cm.show(anchorBtn, Side.TOP, 0, 0);
Bounds anchorBounds = anchorBtn.localToScreen(anchorBtn.getLayoutBounds());
Node cmNode = cm.getScene().getRoot();
Bounds cmBounds = cm.getScene().getRoot().localToScreen(cmNode.getLayoutBounds());
assertEquals(anchorBounds.getMinX(), cmBounds.getMinX(), 0.0);
assertEquals(anchorBounds.getMinY(), cmBounds.getMaxY(), 0.0);
}
@Test public void test_position_showOnTopOffset() throws InterruptedException {
ContextMenu cm = createContextMenu(false);
cm.show(anchorBtn, Side.TOP, 3, 5);
Bounds anchorBounds = anchorBtn.localToScreen(anchorBtn.getLayoutBounds());
Node cmNode = cm.getScene().getRoot();
Bounds cmBounds = cm.getScene().getRoot().localToScreen(cmNode.getLayoutBounds());
assertEquals(anchorBounds.getMinX() + 3, cmBounds.getMinX(), 0.0);
assertEquals(anchorBounds.getMinY() + 5, cmBounds.getMaxY(), 0.0);
}
@Test public void test_position_withOrientationTop() throws InterruptedException {
ContextMenu cm = createContextMenu(false);
anchorBtn.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
cm.show(anchorBtn, Side.TOP, 0, 0);
Bounds anchorBounds = anchorBtn.localToScreen(anchorBtn.getLayoutBounds());
Node cmNode = cm.getScene().getRoot();
Bounds cmBounds = cm.getScene().getRoot().localToScreen(cmNode.getLayoutBounds());
assertEquals(anchorBounds.getMaxX(), cmBounds.getMaxX(), 0.0);
assertEquals(anchorBounds.getMinY(), cmBounds.getMaxY(), 0.0);
}
@Test public void test_position_withOrientationLeft() throws InterruptedException {
ContextMenu cm = createContextMenu(false);
anchorBtn.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
cm.show(anchorBtn, Side.LEFT, 0, 0);
Bounds anchorBounds = anchorBtn.localToScreen(anchorBtn.getLayoutBounds());
Node cmNode = cm.getScene().getRoot();
Bounds cmBounds = cm.getScene().getRoot().localToScreen(cmNode.getLayoutBounds());
assertEquals(anchorBounds.getMaxX(), cmBounds.getMinX(), 0.0);
assertEquals(anchorBounds.getMinY(), cmBounds.getMinY(), 0.0);
}
@Test public void test_position_withCSS() throws InterruptedException {
anchorBtn.getScene().getStylesheets().add(
getClass().getResource("test_position_showOnTopWithCSS.css").toExternalForm()
);
test_position_showOnTop();
test_position_showOnRight();
test_position_showOnLeft();
test_position_showOnBottom();
}
@Test public void test_position_showOnRight() {
ContextMenu cm = createContextMenu(false);
cm.show(anchorBtn, Side.RIGHT, 0, 0);
Bounds anchorBounds = anchorBtn.localToScreen(anchorBtn.getLayoutBounds());
Node cmNode = cm.getScene().getRoot();
Bounds cmBounds = cm.getScene().getRoot().localToScreen(cmNode.getLayoutBounds());
assertEquals(anchorBounds.getMaxX(), cmBounds.getMinX(), 0.0);
assertEquals(anchorBounds.getMinY(), cmBounds.getMinY(), 0.0);
}
@Test public void test_position_showOnRightOffset() {
ContextMenu cm = createContextMenu(false);
cm.show(anchorBtn, Side.RIGHT, 3, 5);
Bounds anchorBounds = anchorBtn.localToScreen(anchorBtn.getLayoutBounds());
Node cmNode = cm.getScene().getRoot();
Bounds cmBounds = cm.getScene().getRoot().localToScreen(cmNode.getLayoutBounds());
assertEquals(anchorBounds.getMaxX() + 3, cmBounds.getMinX(), 0.0);
assertEquals(anchorBounds.getMinY() + 5, cmBounds.getMinY(), 0.0);
}
@Test public void test_position_showOnBottom() {
ContextMenu cm = createContextMenu(false);
cm.show(anchorBtn, Side.BOTTOM, 0, 0);
Bounds anchorBounds = anchorBtn.localToScreen(anchorBtn.getLayoutBounds());
Node cmNode = cm.getScene().getRoot();
Bounds cmBounds = cm.getScene().getRoot().localToScreen(cmNode.getLayoutBounds());
assertEquals(anchorBounds.getMinX(), cmBounds.getMinX(), 0.0);
assertEquals(anchorBounds.getMaxY(), cmBounds.getMinY(), 0.0);
}
@Test public void test_position_showOnLeft() {
ContextMenu cm = createContextMenu(false);
cm.show(anchorBtn, Side.LEFT, 0, 0);
Bounds anchorBounds = anchorBtn.localToScreen(anchorBtn.getLayoutBounds());
Node cmNode = cm.getScene().getRoot();
Bounds cmBounds = cm.getScene().getRoot().localToScreen(cmNode.getLayoutBounds());
assertEquals(anchorBounds.getMinX(), cmBounds.getMaxX(), 0.0);
assertEquals(anchorBounds.getMinY(), cmBounds.getMinY(), 0.0);
}
@Test public void test_graphic_padding_onDialogPane() {
DialogPane dialogPane = new DialogPane();
anchorBtn.setGraphic(dialogPane);
dialogPane.pseudoClassStateChanged(PseudoClass.getPseudoClass("no-header"), true);
final ImageView graphic = new ImageView(new Image(ContextMenuTest.class.getResource("icon.png").toExternalForm()));
final MenuItem menuItem = new MenuItem("Menu Item Text", graphic);
final ContextMenu contextMenu = new ContextMenu(menuItem);
contextMenu.show(dialogPane, 0, 0);
final Insets padding = ((StackPane) graphic.getParent()).getPadding();
final double fontSize = Font.getDefault().getSize();
assertEquals(0, padding.getTop(), 0.0);
assertEquals(0.333 * fontSize, padding.getRight(), 0.01);
assertEquals(0, padding.getBottom(), 0.0);
assertEquals(0, padding.getLeft(), 0.0);
anchorBtn.setGraphic(null);
}
}
