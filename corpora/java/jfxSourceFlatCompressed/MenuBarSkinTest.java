package test.javafx.scene.control.skin;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.sun.javafx.menu.MenuBase;
import com.sun.javafx.stage.WindowHelper;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.skin.MenuBarSkin;
import javafx.scene.control.skin.MenuBarSkinShim;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import test.com.sun.javafx.pgstub.StubToolkit;
import test.com.sun.javafx.scene.control.infrastructure.KeyEventFirer;
public class MenuBarSkinTest {
private MenuBar menubar;
private MenuBarSkinMock skin;
private static Toolkit tk;
private Scene scene;
private Stage stage;
@BeforeClass public static void initToolKit() {
tk = Toolkit.getToolkit();
}
@Before public void setup() {
menubar = new MenuBar();
menubar.setUseSystemMenuBar(false);
menubar.getMenus().addAll(new Menu("File"), new Menu("Edit"));
scene = new Scene(new Group(menubar));
skin = new MenuBarSkinMock(menubar);
menubar.setSkin(skin);
menubar.setPadding(new Insets(10, 10, 10, 10));
stage = new Stage();
stage.setScene(scene);
WindowHelper.setFocused(stage, true);
}
@Test public void maxHeightTracksPreferred() {
menubar.setPrefHeight(100);
assertEquals(100, menubar.maxHeight(-1), 0);
}
@Test public void testDispose() {
if (tk.getSystemMenu().isSupported()) {
menubar.setUseSystemMenuBar(true);
assertEquals(menubar.getMenus().size(), getSystemMenus().size());
}
menubar.setSkin(null);
if (tk.getSystemMenu().isSupported()) {
assertEquals(0, getSystemMenus().size());
}
}
@Test public void testSetUseSystemMenuBar() {
if (tk.getSystemMenu().isSupported()) {
menubar.setUseSystemMenuBar(true);
assertEquals(menubar.getMenus().size(), getSystemMenus().size());
menubar.setUseSystemMenuBar(false);
assertEquals(0, getSystemMenus().size());
menubar.setUseSystemMenuBar(true);
assertEquals(menubar.getMenus().size(), getSystemMenus().size());
}
}
@Test public void testSystemMenuBarUpdatesWhenMenusChange() {
if (tk.getSystemMenu().isSupported()) {
menubar.setUseSystemMenuBar(true);
assertEquals(menubar.getMenus().size(), getSystemMenus().size());
menubar.getMenus().add(new Menu("testSystemMenuBarUpdatesWhenMenusChange"));
assertEquals(menubar.getMenus().size(), getSystemMenus().size());
}
}
@Test public void testRT_36554() {
if (tk.getSystemMenu().isSupported()) {
menubar.setUseSystemMenuBar(true);
assertEquals(menubar.getMenus().size(), getSystemMenus().size());
((Group)scene.getRoot()).getChildren().remove(menubar);
assertEquals(0, getSystemMenus().size());
((Group)scene.getRoot()).getChildren().add(menubar);
assertEquals(menubar.getMenus().size(), getSystemMenus().size());
((Group)scene.getRoot()).getChildren().remove(menubar);
assertEquals(0, getSystemMenus().size());
menubar.setUseSystemMenuBar(false);
((Group)scene.getRoot()).getChildren().add(menubar);
assertEquals(0, getSystemMenus().size());
menubar.setUseSystemMenuBar(true);
assertEquals(menubar.getMenus().size(), getSystemMenus().size());
}
}
@Test public void testModifyingNonSystemMenuBar() {
if (tk.getSystemMenu().isSupported()) {
menubar.setUseSystemMenuBar(true);
MenuBar secondaryMenuBar = new MenuBar(
new Menu("Menu 1", null, new MenuItem("Item 1")),
new Menu("Menu 2", null, new MenuItem("Item 2")));
secondaryMenuBar.setSkin(new MenuBarSkin(secondaryMenuBar));
((Group)scene.getRoot()).getChildren().add(secondaryMenuBar);
assertTrue(menubar.isUseSystemMenuBar());
secondaryMenuBar.getMenus().remove(1);
assertTrue(menubar.isUseSystemMenuBar());
}
}
@Test
public void testInvisibleMenuNavigation() {
menubar.getMenus().get(0).setVisible(false);
MenuBarSkinShim.setFocusedMenuIndex(skin, 0);
KeyEventFirer keyboard = new KeyEventFirer(menubar);
keyboard.doKeyPress(KeyCode.LEFT);
tk.firePulse();
}
public static final class MenuBarSkinMock extends MenuBarSkin {
boolean propertyChanged = false;
int propertyChangeCount = 0;
public MenuBarSkinMock(MenuBar menubar) {
super(menubar);
}
public void addWatchedProperty(ObservableValue<?> p) {
p.addListener(o -> {
propertyChanged = true;
propertyChangeCount++;
});
}
}
private List<MenuBase> getSystemMenus() {
return ((StubToolkit.StubSystemMenu)tk.getSystemMenu()).getMenus();
}
}
