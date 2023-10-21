package javafx.scene.control.skin;
import static com.sun.javafx.FXPermissions.ACCESS_WINDOW_LIST_PERMISSION;
import com.sun.javafx.scene.traversal.Direction;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import com.sun.javafx.scene.control.MenuBarButton;
import com.sun.javafx.scene.control.skin.Utils;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.beans.value.WritableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import static javafx.scene.input.KeyCode.*;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import com.sun.javafx.menu.MenuBase;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.SceneHelper;
import com.sun.javafx.scene.control.GlobalMenuAdapter;
import com.sun.javafx.tk.Toolkit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.stage.Window;
import javafx.util.Pair;
import java.security.AccessController;
import java.security.PrivilegedAction;
public class MenuBarSkin extends SkinBase<MenuBar> {
private static final ObservableList<Window> stages;
static {
final Predicate<Window> findStage = (w) -> w instanceof Stage;
@SuppressWarnings("removal")
ObservableList<Window> windows = AccessController.doPrivileged(
(PrivilegedAction<ObservableList<Window>>) () -> Window.getWindows(),
null,
ACCESS_WINDOW_LIST_PERMISSION);
stages = windows.filtered(findStage);
}
private final HBox container;
private Menu openMenu;
private MenuBarButton openMenuButton;
private Menu focusedMenu;
private int focusedMenuIndex = -1;
private static WeakHashMap<Stage, Reference<MenuBarSkin>> systemMenuMap;
private static List<MenuBase> wrappedDefaultMenus = new ArrayList<>();
private static Stage currentMenuBarStage;
private List<MenuBase> wrappedMenus;
private WeakEventHandler<KeyEvent> weakSceneKeyEventHandler;
private WeakEventHandler<MouseEvent> weakSceneMouseEventHandler;
private WeakEventHandler<KeyEvent> weakSceneAltKeyEventHandler;
private WeakChangeListener<Boolean> weakWindowFocusListener;
private WeakChangeListener<Window> weakWindowSceneListener;
private EventHandler<KeyEvent> keyEventHandler;
private EventHandler<KeyEvent> altKeyEventHandler;
private EventHandler<MouseEvent> mouseEventHandler;
private ChangeListener<Boolean> menuBarFocusedPropertyListener;
private ChangeListener<Scene> sceneChangeListener;
private ChangeListener<Boolean> menuVisibilityChangeListener;
private boolean pendingDismiss = false;
private boolean altKeyPressed = false;
private EventHandler<ActionEvent> menuActionEventHandler = t -> {
if (t.getSource() instanceof CustomMenuItem) {
CustomMenuItem cmi = (CustomMenuItem)t.getSource();
if (!cmi.isHideOnClick()) return;
}
unSelectMenus();
};
private ListChangeListener<MenuItem> menuItemListener = (c) -> {
while (c.next()) {
for (MenuItem mi : c.getAddedSubList()) {
updateActionListeners(mi, true);
}
for (MenuItem mi: c.getRemoved()) {
updateActionListeners(mi, false);
}
}
};
Runnable firstMenuRunnable = new Runnable() {
public void run() {
if (container.getChildren().size() > 0) {
if (container.getChildren().get(0) instanceof MenuButton) {
if (focusedMenuIndex != 0) {
unSelectMenus();
menuModeStart(0);
openMenuButton = ((MenuBarButton)container.getChildren().get(0));
openMenuButton.setHover();
}
else {
unSelectMenus();
}
}
}
}
};
public MenuBarSkin(final MenuBar control) {
super(control);
container = new HBox();
container.getStyleClass().add("container");
getChildren().add(container);
keyEventHandler = event -> {
if (focusedMenu != null) {
switch (event.getCode()) {
case LEFT: {
boolean isRTL = control.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
if (control.getScene().getWindow().isFocused()) {
if (openMenu != null && !openMenu.isShowing()) {
if (isRTL) {
moveToMenu(Direction.NEXT, false);
} else {
moveToMenu(Direction.PREVIOUS, false);
}
event.consume();
return;
}
if (isRTL) {
moveToMenu(Direction.NEXT, true);
} else {
moveToMenu(Direction.PREVIOUS, true);
}
}
event.consume();
break;
}
case RIGHT:
{
boolean isRTL = control.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
if (control.getScene().getWindow().isFocused()) {
if (openMenu != null && !openMenu.isShowing()) {
if (isRTL) {
moveToMenu(Direction.PREVIOUS, false);
} else {
moveToMenu(Direction.NEXT, false);
}
event.consume();
return;
}
if (isRTL) {
moveToMenu(Direction.PREVIOUS, true);
} else {
moveToMenu(Direction.NEXT, true);
}
}
event.consume();
break;
}
case DOWN:
if (control.getScene().getWindow().isFocused()) {
if (focusedMenuIndex != -1) {
Menu menuToOpen = getSkinnable().getMenus().get(focusedMenuIndex);
showMenu(menuToOpen, true);
event.consume();
}
}
break;
case ESCAPE:
unSelectMenus();
event.consume();
break;
default:
break;
}
}
};
menuBarFocusedPropertyListener = (ov, t, t1) -> {
unSelectMenus();
if (t1 && !container.getChildren().isEmpty()) {
menuModeStart(0);
openMenuButton = ((MenuBarButton)container.getChildren().get(0));
setFocusedMenuIndex(0);
openMenuButton.setHover();
}
};
weakSceneKeyEventHandler = new WeakEventHandler<KeyEvent>(keyEventHandler);
Utils.executeOnceWhenPropertyIsNonNull(control.sceneProperty(), (Scene scene) -> {
scene.addEventFilter(KeyEvent.KEY_PRESSED, weakSceneKeyEventHandler);
});
mouseEventHandler = t -> {
Bounds containerScreenBounds = container.localToScreen(container.getLayoutBounds());
if (containerScreenBounds == null || !containerScreenBounds.contains(t.getScreenX(), t.getScreenY())) {
unSelectMenus();
}
};
weakSceneMouseEventHandler = new WeakEventHandler<MouseEvent>(mouseEventHandler);
Utils.executeOnceWhenPropertyIsNonNull(control.sceneProperty(), (Scene scene) -> {
scene.addEventFilter(MouseEvent.MOUSE_CLICKED, weakSceneMouseEventHandler);
});
weakWindowFocusListener = new WeakChangeListener<Boolean>((ov, t, t1) -> {
if (!t1) {
unSelectMenus();
}
});
Utils.executeOnceWhenPropertyIsNonNull(control.sceneProperty(), (Scene scene) -> {
if (scene.getWindow() != null) {
scene.getWindow().focusedProperty().addListener(weakWindowFocusListener);
} else {
ChangeListener<Window> sceneWindowListener = (observable, oldValue, newValue) -> {
if (oldValue != null)
oldValue.focusedProperty().removeListener(weakWindowFocusListener);
if (newValue != null)
newValue.focusedProperty().addListener(weakWindowFocusListener);
};
weakWindowSceneListener = new WeakChangeListener<>(sceneWindowListener);
scene.windowProperty().addListener(weakWindowSceneListener);
}
});
menuVisibilityChangeListener = (ov, t, t1) -> {
rebuildUI();
};
rebuildUI();
control.getMenus().addListener((ListChangeListener<Menu>) c -> {
rebuildUI();
});
if (Toolkit.getToolkit().getSystemMenu().isSupported()) {
control.useSystemMenuBarProperty().addListener(valueModel -> {
rebuildUI();
});
}
final KeyCombination acceleratorKeyCombo;
if (com.sun.javafx.util.Utils.isMac()) {
acceleratorKeyCombo = KeyCombination.keyCombination("ctrl+F10");
} else {
acceleratorKeyCombo = KeyCombination.keyCombination("F10");
}
altKeyEventHandler = e -> {
if (e.getEventType() == KeyEvent.KEY_PRESSED) {
altKeyPressed = false;
if (e.getCode() == ALT && !e.isConsumed()) {
if (focusedMenuIndex == -1) {
altKeyPressed = true;
}
unSelectMenus();
}
} else if (e.getEventType() == KeyEvent.KEY_RELEASED) {
if (altKeyPressed && e.getCode() == ALT && !e.isConsumed()) {
firstMenuRunnable.run();
}
altKeyPressed = false;
}
};
weakSceneAltKeyEventHandler = new WeakEventHandler<>(altKeyEventHandler);
Utils.executeOnceWhenPropertyIsNonNull(control.sceneProperty(), (Scene scene) -> {
scene.getAccelerators().put(acceleratorKeyCombo, firstMenuRunnable);
scene.addEventHandler(KeyEvent.ANY, weakSceneAltKeyEventHandler);
});
ParentTraversalEngine engine = new ParentTraversalEngine(getSkinnable());
engine.addTraverseListener((node, bounds) -> {
if (openMenu != null) openMenu.hide();
setFocusedMenuIndex(0);
});
ParentHelper.setTraversalEngine(getSkinnable(), engine);
control.sceneProperty().addListener((ov, t, t1) -> {
if (t != null) {
if (weakSceneKeyEventHandler != null) {
t.removeEventFilter(KeyEvent.KEY_PRESSED, weakSceneKeyEventHandler);
}
if (weakSceneMouseEventHandler != null) {
t.removeEventFilter(MouseEvent.MOUSE_CLICKED, weakSceneMouseEventHandler);
}
if (weakSceneAltKeyEventHandler != null) {
t.removeEventHandler(KeyEvent.ANY, weakSceneAltKeyEventHandler);
}
}
if (t != null) {
t.getAccelerators().remove(acceleratorKeyCombo);
}
if (t1 != null ) {
t1.getAccelerators().put(acceleratorKeyCombo, firstMenuRunnable);
}
});
}
private void showMenu(Menu menu) {
showMenu(menu, false);
}
private void showMenu(Menu menu, boolean selectFirstItem) {
if (openMenu == menu) return;
if (openMenu != null) {
openMenu.hide();
}
openMenu = menu;
if (!menu.isShowing() && !isMenuEmpty(menu)) {
if (selectFirstItem) {
MenuButton menuButton = getNodeForMenu(focusedMenuIndex);
Skin<?> skin = menuButton.getSkin();
if (skin instanceof MenuButtonSkinBase) {
((MenuButtonSkinBase)skin).requestFocusOnFirstMenuItem();
}
}
openMenu.show();
}
}
void setFocusedMenuIndex(int index) {
focusedMenuIndex = (index >= -1 && index < getSkinnable().getMenus().size()) ? index : -1;
focusedMenu = (focusedMenuIndex != -1) ? getSkinnable().getMenus().get(index) : null;
if (focusedMenuIndex != -1) {
openMenuButton = (MenuBarButton)container.getChildren().get(focusedMenuIndex);
openMenuButton.setHover();
}
}
public static void setDefaultSystemMenuBar(final MenuBar menuBar) {
if (Toolkit.getToolkit().getSystemMenu().isSupported()) {
wrappedDefaultMenus.clear();
for (Menu menu : menuBar.getMenus()) {
wrappedDefaultMenus.add(GlobalMenuAdapter.adapt(menu));
}
menuBar.getMenus().addListener((ListChangeListener<Menu>) c -> {
wrappedDefaultMenus.clear();
for (Menu menu : menuBar.getMenus()) {
wrappedDefaultMenus.add(GlobalMenuAdapter.adapt(menu));
}
});
}
}
private static MenuBarSkin getMenuBarSkin(Stage stage) {
if (systemMenuMap == null) return null;
Reference<MenuBarSkin> skinRef = systemMenuMap.get(stage);
return skinRef == null ? null : skinRef.get();
}
private static void setSystemMenu(Stage stage) {
if (stage != null && stage.isFocused()) {
while (stage != null && stage.getOwner() instanceof Stage) {
MenuBarSkin skin = getMenuBarSkin(stage);
if (skin != null && skin.wrappedMenus != null) {
break;
} else {
stage = (Stage)stage.getOwner();
}
}
} else {
stage = null;
}
if (stage != currentMenuBarStage) {
List<MenuBase> menuList = null;
if (stage != null) {
MenuBarSkin skin = getMenuBarSkin(stage);
if (skin != null) {
menuList = skin.wrappedMenus;
}
}
if (menuList == null) {
menuList = wrappedDefaultMenus;
}
Toolkit.getToolkit().getSystemMenu().setMenus(menuList);
currentMenuBarStage = stage;
}
}
private static void initSystemMenuBar() {
systemMenuMap = new WeakHashMap<>();
final InvalidationListener focusedStageListener = ov -> {
setSystemMenu((Stage)((ReadOnlyProperty<?>)ov).getBean());
};
for (Window stage : stages) {
stage.focusedProperty().addListener(focusedStageListener);
}
stages.addListener((ListChangeListener<Window>) c -> {
while (c.next()) {
for (Window stage : c.getRemoved()) {
stage.focusedProperty().removeListener(focusedStageListener);
}
for (Window stage : c.getAddedSubList()) {
stage.focusedProperty().addListener(focusedStageListener);
setSystemMenu((Stage) stage);
}
}
});
}
private DoubleProperty spacing;
public final void setSpacing(double value) {
spacingProperty().set(snapSpaceX(value));
}
public final double getSpacing() {
return spacing == null ? 0.0 : snapSpaceX(spacing.get());
}
public final DoubleProperty spacingProperty() {
if (spacing == null) {
spacing = new StyleableDoubleProperty() {
@Override
protected void invalidated() {
final double value = get();
container.setSpacing(value);
}
@Override
public Object getBean() {
return MenuBarSkin.this;
}
@Override
public String getName() {
return "spacing";
}
@Override
public CssMetaData<MenuBar,Number> getCssMetaData() {
return SPACING;
}
};
}
return spacing;
}
private ObjectProperty<Pos> containerAlignment;
public final void setContainerAlignment(Pos value) {
containerAlignmentProperty().set(value);
}
public final Pos getContainerAlignment() {
return containerAlignment == null ? Pos.TOP_LEFT : containerAlignment.get();
}
public final ObjectProperty<Pos> containerAlignmentProperty() {
if (containerAlignment == null) {
containerAlignment = new StyleableObjectProperty<Pos>(Pos.TOP_LEFT) {
@Override
public void invalidated() {
final Pos value = get();
container.setAlignment(value);
}
@Override
public Object getBean() {
return MenuBarSkin.this;
}
@Override
public String getName() {
return "containerAlignment";
}
@Override
public CssMetaData<MenuBar,Pos> getCssMetaData() {
return ALIGNMENT;
}
};
}
return containerAlignment;
}
@Override public void dispose() {
cleanUpSystemMenu();
super.dispose();
}
@Override protected double snappedTopInset() {
return container.getChildren().isEmpty() ? 0 : super.snappedTopInset();
}
@Override protected double snappedBottomInset() {
return container.getChildren().isEmpty() ? 0 : super.snappedBottomInset();
}
@Override protected double snappedLeftInset() {
return container.getChildren().isEmpty() ? 0 : super.snappedLeftInset();
}
@Override protected double snappedRightInset() {
return container.getChildren().isEmpty() ? 0 : super.snappedRightInset();
}
@Override protected void layoutChildren(final double x, final double y,
final double w, final double h) {
container.resizeRelocate(x, y, w, h);
}
@Override protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return container.minWidth(height) + snappedLeftInset() + snappedRightInset();
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return container.prefWidth(height) + snappedLeftInset() + snappedRightInset();
}
@Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return container.minHeight(width) + snappedTopInset() + snappedBottomInset();
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return container.prefHeight(width) + snappedTopInset() + snappedBottomInset();
}
@Override protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return getSkinnable().prefHeight(-1);
}
MenuButton getNodeForMenu(int i) {
if (i < container.getChildren().size()) {
return (MenuBarButton)container.getChildren().get(i);
}
return null;
}
int getFocusedMenuIndex() {
return focusedMenuIndex;
}
private boolean menusContainCustomMenuItem() {
for (Menu menu : getSkinnable().getMenus()) {
if (menuContainsCustomMenuItem(menu)) {
System.err.println("Warning: MenuBar ignored property useSystemMenuBar because menus contain CustomMenuItem");
return true;
}
}
return false;
}
private boolean menuContainsCustomMenuItem(Menu menu) {
for (MenuItem mi : menu.getItems()) {
if (mi instanceof CustomMenuItem && !(mi instanceof SeparatorMenuItem)) {
return true;
} else if (mi instanceof Menu) {
if (menuContainsCustomMenuItem((Menu)mi)) {
return true;
}
}
}
return false;
}
private int getMenuBarButtonIndex(MenuBarButton m) {
for (int i= 0; i < container.getChildren().size(); i++) {
MenuBarButton menuButton = (MenuBarButton)container.getChildren().get(i);
if (m == menuButton) {
return i;
}
}
return -1;
}
private void updateActionListeners(MenuItem item, boolean add) {
if (item instanceof Menu) {
Menu menu = (Menu) item;
if (add) {
menu.getItems().addListener(menuItemListener);
} else {
menu.getItems().removeListener(menuItemListener);
}
for (MenuItem mi : menu.getItems()) {
updateActionListeners(mi, add);
}
} else {
if (add) {
item.addEventHandler(ActionEvent.ACTION, menuActionEventHandler);
} else {
item.removeEventHandler(ActionEvent.ACTION, menuActionEventHandler);
}
}
}
private void rebuildUI() {
getSkinnable().focusedProperty().removeListener(menuBarFocusedPropertyListener);
for (Menu m : getSkinnable().getMenus()) {
updateActionListeners(m, false);
m.visibleProperty().removeListener(menuVisibilityChangeListener);
}
for (Node n : container.getChildren()) {
MenuBarButton menuButton = (MenuBarButton)n;
menuButton.hide();
menuButton.menu.showingProperty().removeListener(menuButton.menuListener);
menuButton.disableProperty().unbind();
menuButton.textProperty().unbind();
menuButton.graphicProperty().unbind();
menuButton.styleProperty().unbind();
menuButton.dispose();
menuButton.setSkin(null);
menuButton = null;
}
container.getChildren().clear();
if (Toolkit.getToolkit().getSystemMenu().isSupported()) {
final Scene scene = getSkinnable().getScene();
if (scene != null) {
if (sceneChangeListener == null) {
sceneChangeListener = (observable, oldValue, newValue) -> {
if (oldValue != null) {
if (oldValue.getWindow() instanceof Stage) {
final Stage stage = (Stage) oldValue.getWindow();
final MenuBarSkin curMBSkin = getMenuBarSkin(stage);
if (curMBSkin == MenuBarSkin.this) {
curMBSkin.wrappedMenus = null;
systemMenuMap.remove(stage);
if (currentMenuBarStage == stage) {
currentMenuBarStage = null;
setSystemMenu(stage);
}
} else {
if (getSkinnable().isUseSystemMenuBar() &&
curMBSkin != null && curMBSkin.getSkinnable() != null &&
curMBSkin.getSkinnable().isUseSystemMenuBar()) {
curMBSkin.getSkinnable().setUseSystemMenuBar(false);
}
}
}
}
if (newValue != null) {
if (getSkinnable().isUseSystemMenuBar() && !menusContainCustomMenuItem()) {
if (newValue.getWindow() instanceof Stage) {
final Stage stage = (Stage) newValue.getWindow();
if (systemMenuMap == null) {
initSystemMenuBar();
}
wrappedMenus = new ArrayList<>();
systemMenuMap.put(stage, new WeakReference<>(this));
for (Menu menu : getSkinnable().getMenus()) {
wrappedMenus.add(GlobalMenuAdapter.adapt(menu));
}
currentMenuBarStage = null;
setSystemMenu(stage);
getSkinnable().requestLayout();
javafx.application.Platform.runLater(() -> getSkinnable().requestLayout());
}
}
}
};
getSkinnable().sceneProperty().addListener(sceneChangeListener);
}
sceneChangeListener.changed(getSkinnable().sceneProperty(), scene, scene);
if (currentMenuBarStage != null ? getMenuBarSkin(currentMenuBarStage) == MenuBarSkin.this : getSkinnable().isUseSystemMenuBar()) {
return;
}
} else {
if (currentMenuBarStage != null) {
final MenuBarSkin curMBSkin = getMenuBarSkin(currentMenuBarStage);
if (curMBSkin == MenuBarSkin.this) {
setSystemMenu(null);
}
}
}
}
getSkinnable().focusedProperty().addListener(menuBarFocusedPropertyListener);
for (final Menu menu : getSkinnable().getMenus()) {
menu.visibleProperty().addListener(menuVisibilityChangeListener);
if (!menu.isVisible()) continue;
final MenuBarButton menuButton = new MenuBarButton(this, menu);
menuButton.setFocusTraversable(false);
menuButton.getStyleClass().add("menu");
menuButton.setStyle(menu.getStyle());
menuButton.getItems().setAll(menu.getItems());
container.getChildren().add(menuButton);
menuButton.menuListener = (observable, oldValue, newValue) -> {
if (menu.isShowing()) {
menuButton.show();
menuModeStart(container.getChildren().indexOf(menuButton));
} else {
menuButton.hide();
}
};
menuButton.menu = menu;
menu.showingProperty().addListener(menuButton.menuListener);
menuButton.disableProperty().bindBidirectional(menu.disableProperty());
menuButton.textProperty().bind(menu.textProperty());
menuButton.graphicProperty().bind(menu.graphicProperty());
menuButton.styleProperty().bind(menu.styleProperty());
menuButton.getProperties().addListener((MapChangeListener<Object, Object>) c -> {
if (c.wasAdded() && MenuButtonSkin.AUTOHIDE.equals(c.getKey())) {
menuButton.getProperties().remove(MenuButtonSkin.AUTOHIDE);
menu.hide();
}
});
menuButton.showingProperty().addListener((observable, oldValue, isShowing) -> {
if (isShowing) {
if(openMenuButton == null && focusedMenuIndex != -1)
openMenuButton = (MenuBarButton)container.getChildren().get(focusedMenuIndex);
if (openMenuButton != null && openMenuButton != menuButton) {
openMenuButton.clearHover();
}
openMenuButton = menuButton;
showMenu(menu);
} else {
openMenu = null;
openMenuButton = null;
}
});
menuButton.setOnMousePressed(event -> {
pendingDismiss = menuButton.isShowing();
if (menuButton.getScene().getWindow().isFocused()) {
showMenu(menu);
menuModeStart(getMenuBarButtonIndex(menuButton));
}
});
menuButton.setOnMouseReleased(event -> {
if (menuButton.getScene().getWindow().isFocused()) {
if (pendingDismiss) {
resetOpenMenu();
}
}
pendingDismiss = false;
});
menuButton.setOnMouseEntered(event -> {
if (menuButton.getScene() != null && menuButton.getScene().getWindow() != null &&
menuButton.getScene().getWindow().isFocused()) {
if (openMenuButton != null && openMenuButton != menuButton) {
openMenuButton.clearHover();
openMenuButton = null;
openMenuButton = menuButton;
}
updateFocusedIndex();
if (openMenu != null && openMenu != menu) {
showMenu(menu);
}
}
});
updateActionListeners(menu, true);
}
getSkinnable().requestLayout();
}
private void cleanUpSystemMenu() {
if (sceneChangeListener != null && getSkinnable() != null) {
getSkinnable().sceneProperty().removeListener(sceneChangeListener);
sceneChangeListener = null;
}
if (currentMenuBarStage != null && getMenuBarSkin(currentMenuBarStage) == MenuBarSkin.this) {
setSystemMenu(null);
}
if (systemMenuMap != null) {
Iterator<Map.Entry<Stage,Reference<MenuBarSkin>>> iterator = systemMenuMap.entrySet().iterator();
while (iterator.hasNext()) {
Map.Entry<Stage,Reference<MenuBarSkin>> entry = iterator.next();
Reference<MenuBarSkin> ref = entry.getValue();
MenuBarSkin skin = ref != null ? ref.get() : null;
if (skin == null || skin == MenuBarSkin.this) {
iterator.remove();
}
}
}
}
private boolean isMenuEmpty(Menu menu) {
boolean retVal = true;
if (menu != null) {
for (MenuItem m : menu.getItems()) {
if (m != null && m.isVisible()) retVal = false;
}
}
return retVal;
}
private void resetOpenMenu() {
if (openMenu != null) {
openMenu.hide();
openMenu = null;
}
}
private void unSelectMenus() {
clearMenuButtonHover();
if (focusedMenuIndex == -1) return;
if (openMenu != null) {
openMenu.hide();
openMenu = null;
}
if (openMenuButton != null) {
openMenuButton.clearHover();
openMenuButton = null;
}
menuModeEnd();
}
private void menuModeStart(int newIndex) {
if (focusedMenuIndex == -1) {
SceneHelper.getSceneAccessor().setTransientFocusContainer(getSkinnable().getScene(), getSkinnable());
}
setFocusedMenuIndex(newIndex);
}
private void menuModeEnd() {
if (focusedMenuIndex != -1) {
SceneHelper.getSceneAccessor().setTransientFocusContainer(getSkinnable().getScene(), null);
getSkinnable().notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_NODE);
}
setFocusedMenuIndex(-1);
}
private void moveToMenu(Direction dir, boolean doShow) {
boolean showNextMenu = doShow && focusedMenu.isShowing();
findSibling(dir, focusedMenuIndex).ifPresent(p -> {
setFocusedMenuIndex(p.getValue());
if (showNextMenu) {
showMenu(p.getKey(), false);
}
});
}
private Optional<Pair<Menu,Integer>> findSibling(Direction dir, int startIndex) {
if (startIndex == -1) {
return Optional.empty();
}
List<Menu> visibleMenus = getSkinnable().getMenus().stream().filter(Menu::isVisible)
.collect(Collectors.toList());
final int totalMenus = visibleMenus.size();
int i = 0;
int nextIndex = 0;
while (i < totalMenus) {
i++;
nextIndex = (startIndex + (dir.isForward() ? 1 : -1)) % totalMenus;
if (nextIndex == -1) {
nextIndex = totalMenus - 1;
}
if (visibleMenus.get(nextIndex).isDisable()) {
startIndex = nextIndex;
} else {
break;
}
}
clearMenuButtonHover();
return Optional.of(new Pair<>(visibleMenus.get(nextIndex), nextIndex));
}
private void updateFocusedIndex() {
int index = 0;
for(Node n : container.getChildren()) {
if (n.isHover()) {
setFocusedMenuIndex(index);
return;
}
index++;
}
menuModeEnd();
}
private void clearMenuButtonHover() {
for(Node n : container.getChildren()) {
if (n.isHover()) {
((MenuBarButton)n).clearHover();
((MenuBarButton)n).disarm();
return;
}
}
}
private static final CssMetaData<MenuBar,Number> SPACING =
new CssMetaData<MenuBar,Number>("-fx-spacing",
SizeConverter.getInstance(), 0.0) {
@Override
public boolean isSettable(MenuBar n) {
final MenuBarSkin skin = (MenuBarSkin) n.getSkin();
return skin.spacing == null || !skin.spacing.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(MenuBar n) {
final MenuBarSkin skin = (MenuBarSkin) n.getSkin();
return (StyleableProperty<Number>)(WritableValue<Number>)skin.spacingProperty();
}
};
private static final CssMetaData<MenuBar,Pos> ALIGNMENT =
new CssMetaData<MenuBar,Pos>("-fx-alignment",
new EnumConverter<Pos>(Pos.class), Pos.TOP_LEFT ) {
@Override
public boolean isSettable(MenuBar n) {
final MenuBarSkin skin = (MenuBarSkin) n.getSkin();
return skin.containerAlignment == null || !skin.containerAlignment.isBound();
}
@Override
public StyleableProperty<Pos> getStyleableProperty(MenuBar n) {
final MenuBarSkin skin = (MenuBarSkin) n.getSkin();
return (StyleableProperty<Pos>)(WritableValue<Pos>)skin.containerAlignmentProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(SkinBase.getClassCssMetaData());
final String alignmentProperty = ALIGNMENT.getProperty();
for (int n=0, nMax=styleables.size(); n<nMax; n++) {
final CssMetaData<?,?> prop = styleables.get(n);
if (alignmentProperty.equals(prop.getProperty())) styleables.remove(prop);
}
styleables.add(SPACING);
styleables.add(ALIGNMENT);
STYLEABLES = Collections.unmodifiableList(styleables);
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return STYLEABLES;
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
@Override protected Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case FOCUS_NODE: return openMenuButton;
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
}
