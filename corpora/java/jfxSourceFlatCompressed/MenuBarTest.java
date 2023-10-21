package test.javafx.scene.control;
import com.sun.javafx.scene.SceneHelper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.skin.MenuBarSkinShim;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.com.sun.javafx.pgstub.StubToolkit;
import test.com.sun.javafx.scene.control.infrastructure.KeyEventFirer;
import test.com.sun.javafx.scene.control.infrastructure.KeyModifier;
import test.com.sun.javafx.scene.control.infrastructure.MouseEventGenerator;
import com.sun.javafx.scene.control.ContextMenuContent;
import com.sun.javafx.scene.control.MenuBarMenuButtonShim;
import javafx.scene.control.skin.MenuBarSkin;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
public class MenuBarTest {
private MenuBar menuBar;
private Toolkit tk;
private Scene scene;
private Stage stage;
@Before public void setup() {
setUncaughtExceptionHandler();
tk = (StubToolkit)Toolkit.getToolkit();
menuBar = new MenuBar();
menuBar.setUseSystemMenuBar(false);
}
@After public void cleanup() {
if (stage != null) {
stage.hide();
}
removeUncaughtExceptionHandler();
}
private void setUncaughtExceptionHandler() {
Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
if (throwable instanceof RuntimeException) {
throw (RuntimeException)throwable;
} else {
Thread.currentThread().getThreadGroup().uncaughtException(thread, throwable);
}
});
}
private void removeUncaughtExceptionHandler() {
Thread.currentThread().setUncaughtExceptionHandler(null);
}
protected void startApp(Parent root) {
scene = new Scene(root,800,600);
stage = new Stage();
stage.setX(0);
stage.setY(0);
stage.setScene(scene);
stage.show();
tk.firePulse();
}
@Test public void defaultConstructorHasFalseFocusTraversable() {
assertFalse(menuBar.isFocusTraversable());
}
@Test public void defaultConstructorButSetTrueFocusTraversable() {
menuBar.setFocusTraversable(true);
assertTrue(menuBar.isFocusTraversable());
}
@Test public void testFocusOnEmptyMenubar() {
menuBar.setFocusTraversable(true);
AnchorPane root = new AnchorPane();
root.getChildren().add(menuBar);
startApp(root);
MenuBarSkin skin = (MenuBarSkin)menuBar.getSkin();
assertTrue(skin != null);
menuBar.getScene().getWindow().requestFocus();
int focusedIndex = MenuBarSkinShim.getFocusedMenuIndex(skin);
assertEquals(-1, focusedIndex);
}
@Test public void testSimulateTraverseIntoEmptyMenubar() {
menuBar.setFocusTraversable(true);
AnchorPane root = new AnchorPane();
root.getChildren().add(menuBar);
startApp(root);
MenuBarSkin skin = (MenuBarSkin)menuBar.getSkin();
assertTrue(skin != null);
MenuBarSkinShim.setFocusedMenuIndex(skin, 0);
int focusedIndex = MenuBarSkinShim.getFocusedMenuIndex(skin);
assertEquals(-1, focusedIndex);
}
@Test public void getMenusHasSizeZero() {
assertEquals(0, menuBar.getMenus().size());
}
@Test public void getMenusIsAddable() {
menuBar.getMenus().add(new Menu(""));
assertTrue(menuBar.getMenus().size() > 0);
}
@Test public void getMenusIsClearable() {
menuBar.getMenus().add(new Menu(""));
menuBar.getMenus().clear();
assertEquals(0, menuBar.getMenus().size());
}
@Test public void getMenusIsRemovable() {
menuBar.getMenus().add(new Menu("blah"));
menuBar.getMenus().add(new Menu("foo"));
menuBar.getMenus().remove(0);
assertEquals(1, menuBar.getMenus().size());
}
@Test public void testMenuShowHideWithMenuBarWithXYTranslation() {
final MouseEventGenerator generator = new MouseEventGenerator();
AnchorPane root = new AnchorPane();
Menu menu = new Menu("Menu");
menu.getItems().add(new MenuItem("MenuItem"));
menuBar.getMenus().add(menu);
menuBar.setLayoutX(100);
menuBar.setLayoutY(100);
root.getChildren().add(menuBar);
startApp(root);
tk.firePulse();
MenuBarSkin skin = (MenuBarSkin)menuBar.getSkin();
assertTrue(skin != null);
double xval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinX();
double yval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinY();
MenuButton mb = MenuBarSkinShim.getNodeForMenu(skin, 0);
mb.getScene().getWindow().requestFocus();
SceneHelper.processMouseEvent(scene,
generator.generateMouseEvent(MouseEvent.MOUSE_PRESSED, xval+20, yval+20));
SceneHelper.processMouseEvent(scene,
generator.generateMouseEvent(MouseEvent.MOUSE_RELEASED, xval+20, yval+20));
assertTrue(menu.isShowing());
}
@Test public void testSubMenuDismissalWithKeyNavigation() {
final MouseEventGenerator generator = new MouseEventGenerator();
AnchorPane root = new AnchorPane();
Menu menu = new Menu("Menu");
Menu menu1 = new Menu("Menu With SubMenu");
menu.getItems().add(menu1);
MenuItem menuItem1 = new MenuItem("MenuItem1");
MenuItem menuItem2 = new MenuItem("MenuItem2");
menu1.getItems().addAll(menuItem1, menuItem2);
menuBar.getMenus().add(menu);
menuBar.setLayoutX(100);
menuBar.setLayoutY(100);
root.getChildren().add(menuBar);
startApp(root);
tk.firePulse();
MenuBarSkin skin = (MenuBarSkin)menuBar.getSkin();
assertTrue(skin != null);
double xval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinX();
double yval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinY();
MenuButton mb = MenuBarSkinShim.getNodeForMenu(skin, 0);
mb.getScene().getWindow().requestFocus();
SceneHelper.processMouseEvent(scene,
generator.generateMouseEvent(MouseEvent.MOUSE_PRESSED, xval+20, yval+20));
SceneHelper.processMouseEvent(scene,
generator.generateMouseEvent(MouseEvent.MOUSE_RELEASED, xval+20, yval+20));
assertTrue(menu.isShowing());
ContextMenuContent menuContent = MenuBarSkinShim.getMenuContent(mb);
Node displayNode = MenuBarMenuButtonShim.getDisplayNodeForMenuItem(menuContent, 0);
displayNode.getScene().getWindow().requestFocus();
assertTrue(displayNode.getScene().getWindow().isFocused());
displayNode.requestFocus();
assertTrue(displayNode.isFocused());
MenuBarMenuButtonShim.setCurrentFocusedIndex(menuContent, 0);
KeyEventFirer keyboard = new KeyEventFirer(menuContent);
keyboard.doKeyPress(KeyCode.ENTER);
tk.firePulse();
assertTrue(menu1.isShowing());
ContextMenuContent subMenuContent = MenuBarMenuButtonShim.getSubMenuContent(menuContent);
subMenuContent.getScene().getWindow().requestFocus();
assertTrue(subMenuContent.getScene().getWindow().isFocused());
displayNode = MenuBarMenuButtonShim.getDisplayNodeForMenuItem(subMenuContent, 0);
displayNode.requestFocus();
assertTrue(displayNode.isFocused());
MenuBarMenuButtonShim.setCurrentFocusedIndex(subMenuContent, 0);
keyboard = new KeyEventFirer(subMenuContent);
keyboard.doKeyPress(KeyCode.ENTER);
tk.firePulse();
assertTrue(!menu1.isShowing());
assertTrue(!menu.isShowing());
}
@Test public void checkMenuBarMenusSelectionResetAfterMenuItemIsSelected() {
final MouseEventGenerator generator = new MouseEventGenerator();
AnchorPane root = new AnchorPane();
Menu menu = new Menu("Menu");
MenuItem menuItem = new MenuItem("MenuItem");
menu.getItems().add(menuItem);
menuBar.getMenus().add(menu);
menuBar.setLayoutX(100);
menuBar.setLayoutY(100);
root.getChildren().addAll(menuBar);
startApp(root);
tk.firePulse();
MenuBarSkin skin = (MenuBarSkin)menuBar.getSkin();
assertTrue(skin != null);
MenuButton mb = MenuBarSkinShim.getNodeForMenu(skin, 0);
mb.getScene().getWindow().requestFocus();
double xval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinX();
double yval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinY();
SceneHelper.processMouseEvent(scene,
generator.generateMouseEvent(MouseEvent.MOUSE_PRESSED, xval+20, yval+20));
SceneHelper.processMouseEvent(scene,
generator.generateMouseEvent(MouseEvent.MOUSE_RELEASED, xval+20, yval+20));
assertTrue(menu.isShowing());
ContextMenuContent menuContent = MenuBarSkinShim.getMenuContent(mb);
Node displayNode = MenuBarMenuButtonShim.getDisplayNodeForMenuItem(menuContent, 0);
displayNode.getScene().getWindow().requestFocus();
assertTrue(displayNode.getScene().getWindow().isFocused());
displayNode.requestFocus();
assertTrue(displayNode.isFocused());
KeyEventFirer keyboard = new KeyEventFirer(menuContent);
keyboard.doKeyPress(KeyCode.ENTER);
tk.firePulse();
assertTrue(!menu.isShowing());
keyboard.doKeyPress(KeyCode.LEFT);
tk.firePulse();
int focusedIndex = MenuBarSkinShim.getFocusedMenuIndex(skin);
assertEquals(-1, focusedIndex);
}
@Test public void testMenuOnShownEventFiringWithKeyNavigation() {
final MouseEventGenerator generator = new MouseEventGenerator();
VBox root = new VBox();
Menu menu = new Menu("Menu");
MenuItem menuItem1 = new MenuItem("MenuItem1");
MenuItem menuItem2 = new MenuItem("MenuItem2");
menu.getItems().addAll(menuItem1, menuItem2);
menuBar.getMenus().add(menu);
menuBar.setLayoutX(100);
menuBar.setLayoutY(100);
final CheckBox cb = new CheckBox("showing");
root.getChildren().addAll(cb,menuBar);
startApp(root);
tk.firePulse();
MenuBarSkin skin = (MenuBarSkin)menuBar.getSkin();
assertTrue(skin != null);
MenuButton mb = MenuBarSkinShim.getNodeForMenu(skin, 0);
mb.getScene().getWindow().requestFocus();
assertTrue(mb.getScene().getWindow().isFocused());
KeyEventFirer keyboard = new KeyEventFirer(mb.getScene());
keyboard.doKeyPress(KeyCode.TAB);
tk.firePulse();
mb.requestFocus();
assertTrue(mb.isFocused());
keyboard = new KeyEventFirer(mb);
keyboard.doDownArrowPress();
tk.firePulse();
assertEquals(menu.showingProperty().get(), true);
}
@Test public void testKeyNavigationWithDisabledMenuItem() {
VBox root = new VBox();
Menu menu1 = new Menu("Menu1");
Menu menu2 = new Menu("Menu2");
Menu menu3 = new Menu("Menu3");
MenuItem menuItem1 = new MenuItem("MenuItem1");
MenuItem menuItem2 = new MenuItem("MenuItem2");
MenuItem menuItem3 = new MenuItem("MenuItem3");
menu1.getItems().add(menuItem1);
menu2.getItems().add(menuItem2);
menu3.getItems().add(menuItem3);
menuBar.getMenus().addAll(menu1, menu2, menu3);
menu2.setDisable(true);
root.getChildren().addAll(menuBar);
startApp(root);
tk.firePulse();
MenuBarSkin skin = (MenuBarSkin)menuBar.getSkin();
assertTrue(skin != null);
double xval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinX();
double yval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinY();
MenuButton mb = MenuBarSkinShim.getNodeForMenu(skin, 0);
mb.getScene().getWindow().requestFocus();
SceneHelper.processMouseEvent(scene,
MouseEventGenerator.generateMouseEvent(MouseEvent.MOUSE_PRESSED, xval+20, yval+20));
SceneHelper.processMouseEvent(scene,
MouseEventGenerator.generateMouseEvent(MouseEvent.MOUSE_RELEASED, xval+20, yval+20));
assertTrue(menu1.isShowing());
KeyEventFirer keyboard = new KeyEventFirer(mb.getScene());
keyboard.doKeyPress(KeyCode.RIGHT);
tk.firePulse();
assertTrue(menu3.isShowing());
}
@Test public void testKeyNavigationForward() {
VBox root = new VBox();
Menu menu1 = new Menu("Menu1");
Menu menu2 = new Menu("Menu2");
Menu menu3 = new Menu("Menu3");
MenuItem menuItem1 = new MenuItem("MenuItem1");
MenuItem menuItem2 = new MenuItem("MenuItem2");
MenuItem menuItem3 = new MenuItem("MenuItem3");
menu1.getItems().add(menuItem1);
menu2.getItems().add(menuItem2);
menu3.getItems().add(menuItem3);
menuBar.getMenus().addAll(menu1, menu2, menu3);
menu2.setDisable(true);
root.getChildren().addAll(menuBar);
startApp(root);
tk.firePulse();
MenuBarSkin skin = (MenuBarSkin)menuBar.getSkin();
assertTrue(skin != null);
double xval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinX();
double yval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinY();
MenuButton mb = MenuBarSkinShim.getNodeForMenu(skin, 0);
mb.getScene().getWindow().requestFocus();
SceneHelper.processMouseEvent(scene,
MouseEventGenerator.generateMouseEvent(MouseEvent.MOUSE_PRESSED, xval+20, yval+20));
SceneHelper.processMouseEvent(scene,
MouseEventGenerator.generateMouseEvent(MouseEvent.MOUSE_RELEASED, xval+20, yval+20));
assertTrue(menu1.isShowing());
KeyEventFirer keyboard = new KeyEventFirer(mb.getScene());
keyboard.doKeyPress(KeyCode.RIGHT);
tk.firePulse();
assertTrue(menu3.isShowing());
keyboard.doKeyPress(KeyCode.RIGHT);
tk.firePulse();
assertTrue(menu1.isShowing());
}
@Test public void testKeyNavigationBackward() {
VBox root = new VBox();
Menu menu1 = new Menu("Menu1");
Menu menu2 = new Menu("Menu2");
Menu menu3 = new Menu("Menu3");
MenuItem menuItem1 = new MenuItem("MenuItem1");
MenuItem menuItem2 = new MenuItem("MenuItem2");
MenuItem menuItem3 = new MenuItem("MenuItem3");
menu1.getItems().add(menuItem1);
menu2.getItems().add(menuItem2);
menu3.getItems().add(menuItem3);
menuBar.getMenus().addAll(menu1, menu2, menu3);
menu2.setDisable(true);
root.getChildren().addAll(menuBar);
startApp(root);
tk.firePulse();
MenuBarSkin skin = (MenuBarSkin)menuBar.getSkin();
assertTrue(skin != null);
double xval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinX();
double yval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinY();
MenuButton mb = MenuBarSkinShim.getNodeForMenu(skin, 0);
mb.getScene().getWindow().requestFocus();
SceneHelper.processMouseEvent(scene,
MouseEventGenerator.generateMouseEvent(MouseEvent.MOUSE_PRESSED, xval+20, yval+20));
SceneHelper.processMouseEvent(scene,
MouseEventGenerator.generateMouseEvent(MouseEvent.MOUSE_RELEASED, xval+20, yval+20));
assertTrue(menu1.isShowing());
KeyEventFirer keyboard = new KeyEventFirer(mb.getScene());
keyboard.doKeyPress(KeyCode.LEFT);
tk.firePulse();
assertTrue(menu3.isShowing());
keyboard.doKeyPress(KeyCode.LEFT);
tk.firePulse();
assertTrue(menu1.isShowing());
}
@Test public void testKeyNavigationWithAllDisabledMenuItems() {
VBox root = new VBox();
Menu menu1 = new Menu("Menu1");
MenuItem menuItem1 = new MenuItem("MenuItem1");
menu1.getItems().add(menuItem1);
menuBar.getMenus().addAll(menu1);
menu1.setDisable(true);
root.getChildren().addAll(menuBar);
startApp(root);
tk.firePulse();
MenuBarSkin skin = (MenuBarSkin)menuBar.getSkin();
assertTrue(skin != null);
MenuButton mb = MenuBarSkinShim.getNodeForMenu(skin, 0);
mb.getScene().getWindow().requestFocus();
KeyEventFirer keyboard = new KeyEventFirer(mb.getScene());
keyboard.doKeyPress(KeyCode.RIGHT, KeyModifier.ALT);
tk.firePulse();
assertFalse(menu1.isShowing());
Menu menu2 = new Menu("Menu2");
Menu menu3 = new Menu("Menu3");
MenuItem menuItem2 = new MenuItem("MenuItem2");
MenuItem menuItem3 = new MenuItem("MenuItem3");
menu2.getItems().add(menuItem2);
menu3.getItems().add(menuItem3);
menuBar.getMenus().add(menu2);
menuBar.getMenus().add(menu3);
menu2.setDisable(true);
menu3.setDisable(true);
keyboard.doKeyPress(KeyCode.RIGHT, KeyModifier.ALT);
tk.firePulse();
assertFalse(menu1.isShowing());
assertFalse(menu2.isShowing());
assertFalse(menu3.isShowing());
}
@Test public void testMenuOnShowingEventFiringWithMenuHideOperation() {
VBox root = new VBox();
Menu menu = new Menu("Menu");
MenuItem menuItem1 = new MenuItem("MenuItem1");
menu.getItems().addAll(menuItem1);
menuBar.getMenus().add(menu);
menuBar.setLayoutX(100);
menuBar.setLayoutY(100);
root.getChildren().addAll(menuBar);
startApp(root);
tk.firePulse();
MenuBarSkin skin = (MenuBarSkin)menuBar.getSkin();
assertTrue(skin != null);
double xval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinX();
double yval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinY();
boolean click = true;
final Boolean firstClick = click;
menu.setOnShowing(t -> {
assertEquals(firstClick.booleanValue(), true);
});
MenuButton mb = MenuBarSkinShim.getNodeForMenu(skin, 0);
mb.getScene().getWindow().requestFocus();
mb.requestFocus();
assertTrue(mb.isFocused());
SceneHelper.processMouseEvent(scene,
MouseEventGenerator.generateMouseEvent(MouseEvent.MOUSE_PRESSED, xval+20, yval+20));
SceneHelper.processMouseEvent(scene,
MouseEventGenerator.generateMouseEvent(MouseEvent.MOUSE_RELEASED, xval+20, yval+20));
tk.firePulse();
assertEquals(menu.showingProperty().get(), true);
click = false;
SceneHelper.processMouseEvent(scene,
MouseEventGenerator.generateMouseEvent(MouseEvent.MOUSE_PRESSED, xval+20, yval+20));
SceneHelper.processMouseEvent(scene,
MouseEventGenerator.generateMouseEvent(MouseEvent.MOUSE_RELEASED, xval+20, yval+20));
tk.firePulse();
assertEquals(menu.showingProperty().get(), false);
}
@Test public void testMenuBarUpdateOnMenuVisibilityChange() {
VBox root = new VBox();
Menu menu1 = new Menu("Menu1");
Menu menu2 = new Menu("Menu2");
menuBar.getMenus().addAll(menu1, menu2);
root.getChildren().addAll(menuBar);
startApp(root);
tk.firePulse();
MenuBarSkin skin = (MenuBarSkin)menuBar.getSkin();
assertTrue(skin != null);
int x = Double.compare(menuBar.getHeight(), 0.0);
assertTrue(x > 0);
menu1.setVisible(false);
menu2.setVisible(false);
tk.firePulse();
assertEquals(menuBar.getHeight(), 0.0, 0.0001);
Menu menu3 = new Menu("Menu3");
menuBar.getMenus().add(menu3);
tk.firePulse();
x = Double.compare(menuBar.getHeight(), 0.0);
assertTrue(x > 0);
menu3.setVisible(false);
tk.firePulse();
assertEquals(menuBar.getHeight(), 0.0, 0.0001);
}
@Test public void testRemovingMenuItemFromMenuNotInScene() {
VBox root = new VBox();
Menu menu = new Menu("Menu");
MenuItem menuItem1 = new MenuItem("MenuItem1");
menu.getItems().addAll(menuItem1);
menuBar.getMenus().add(menu);
root.getChildren().addAll(menuBar);
startApp(root);
tk.firePulse();
menuBar.getMenus().remove(menu);
menu.getItems().remove(menuItem1);
assertEquals(true, menu.getItems().isEmpty());
}
@Test public void test_rt_37118() {
MenuBar menuBar = new MenuBar();
MenuBarSkin menuBarSkin = new MenuBarSkin(menuBar);
}
@Test public void testMenuButtonMouseSelection() {
VBox root = new VBox();
Menu menu1 = new Menu("Menu1");
MenuItem menuItem1 = new MenuItem("MenuItem1");
menu1.getItems().add(menuItem1);
menuBar.getMenus().addAll(menu1);
root.getChildren().addAll(menuBar);
startApp(root);
tk.firePulse();
MenuBarSkin skin = (MenuBarSkin)menuBar.getSkin();
assertTrue(skin != null);
double xval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinX();
double yval = (menuBar.localToScene(menuBar.getLayoutBounds())).getMinY();
MenuButton mb = MenuBarSkinShim.getNodeForMenu(skin, 0);
mb.getScene().getWindow().requestFocus();
SceneHelper.processMouseEvent(scene,
MouseEventGenerator.generateMouseEvent(MouseEvent.MOUSE_PRESSED, xval+20, yval+20));
SceneHelper.processMouseEvent(scene,
MouseEventGenerator.generateMouseEvent(MouseEvent.MOUSE_RELEASED, xval+20, yval+20));
assertTrue(menu1.isShowing());
SceneHelper.processMouseEvent(scene,
MouseEventGenerator.generateMouseEvent(MouseEvent.MOUSE_PRESSED, xval+20, yval+20));
SceneHelper.processMouseEvent(scene,
MouseEventGenerator.generateMouseEvent(MouseEvent.MOUSE_RELEASED, xval+20, yval+20));
assertFalse(menu1.isShowing());
int focusedIndex = MenuBarSkinShim.getFocusedMenuIndex(skin);
assertEquals(0, focusedIndex);
}
}
