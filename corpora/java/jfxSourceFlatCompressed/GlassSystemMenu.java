package com.sun.javafx.tk.quantum;
import com.sun.javafx.menu.CheckMenuItemBase;
import com.sun.javafx.menu.MenuBase;
import com.sun.javafx.menu.MenuItemBase;
import com.sun.javafx.menu.RadioMenuItemBase;
import com.sun.javafx.menu.SeparatorMenuItemBase;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.tk.TKSystemMenu;
import com.sun.glass.events.KeyEvent;
import com.sun.glass.ui.Application;
import com.sun.glass.ui.Menu;
import com.sun.glass.ui.MenuBar;
import com.sun.glass.ui.MenuItem;
import com.sun.glass.ui.Pixels;
import com.sun.javafx.tk.Toolkit;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.beans.InvalidationListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCodeCombination;
class GlassSystemMenu implements TKSystemMenu {
private List<MenuBase> systemMenus = null;
private MenuBar glassSystemMenuBar = null;
private InvalidationListener visibilityListener = valueModel -> {
if (systemMenus != null) {
setMenus(systemMenus);
}
};
protected void createMenuBar() {
if (glassSystemMenuBar == null) {
Application app = Application.GetApplication();
glassSystemMenuBar = app.createMenuBar();
app.installDefaultMenus(glassSystemMenuBar);
if (systemMenus != null) {
setMenus(systemMenus);
}
}
}
protected MenuBar getMenuBar() {
return glassSystemMenuBar;
}
@Override public boolean isSupported() {
return Application.GetApplication().supportsSystemMenu();
}
@Override public void setMenus(List<MenuBase> menus) {
systemMenus = menus;
if (glassSystemMenuBar != null) {
List<Menu> existingMenus = glassSystemMenuBar.getMenus();
int existingSize = existingMenus.size();
for (int index = existingSize - 1; index >= 1; index--) {
Menu menu = existingMenus.get(index);
clearMenu(menu);
glassSystemMenuBar.remove(index);
}
for (MenuBase menu : menus) {
addMenu(null, menu);
}
}
}
private void clearMenu(Menu menu) {
for (int i = menu.getItems().size() - 1; i >= 0; i--) {
Object o = menu.getItems().get(i);
if (o instanceof MenuItem) {
((MenuItem)o).setCallback(null);
} else if (o instanceof Menu) {
clearMenu((Menu) o);
}
}
menu.setEventHandler(null);
}
private void addMenu(final Menu parent, final MenuBase mb) {
if (parent != null) {
insertMenu(parent, mb, parent.getItems().size());
} else {
insertMenu(parent, mb, glassSystemMenuBar.getMenus().size());
}
}
private void insertMenu(final Menu parent, final MenuBase mb, int pos) {
Application app = Application.GetApplication();
final Menu glassMenu = app.createMenu(parseText(mb), ! mb.isDisable());
glassMenu.setEventHandler(new GlassMenuEventHandler(mb));
mb.visibleProperty().removeListener(visibilityListener);
mb.visibleProperty().addListener(visibilityListener);
if (!mb.isVisible()) {
return;
}
final ObservableList<MenuItemBase> items = mb.getItemsBase();
items.addListener((ListChangeListener.Change<? extends MenuItemBase> change) -> {
while (change.next()) {
int from = change.getFrom();
int to = change.getTo();
List<? extends MenuItemBase> removed = change.getRemoved();
for (int i = from + removed.size() - 1; i >= from ; i--) {
List<Object> menuItemList = glassMenu.getItems();
if (i >= 0 && menuItemList.size() > i) {
glassMenu.remove(i);
}
}
for (int i = from; i < to; i++) {
MenuItemBase item = change.getList().get(i);
if (item instanceof MenuBase) {
insertMenu(glassMenu, (MenuBase)item, i);
} else {
insertMenuItem(glassMenu, item, i);
}
}
}
});
for (MenuItemBase item : items) {
if (item instanceof MenuBase) {
addMenu(glassMenu, (MenuBase)item);
} else {
addMenuItem(glassMenu, item);
}
}
glassMenu.setPixels(getPixels(mb));
setMenuBindings(glassMenu, mb);
if (parent != null) {
parent.insert(glassMenu, pos);
} else {
glassSystemMenuBar.insert(glassMenu, pos);
}
}
private void setMenuBindings(final Menu glassMenu, final MenuBase mb) {
mb.textProperty().addListener(valueModel -> glassMenu.setTitle(parseText(mb)));
mb.disableProperty().addListener(valueModel -> glassMenu.setEnabled(!mb.isDisable()));
mb.mnemonicParsingProperty().addListener(valueModel -> glassMenu.setTitle(parseText(mb)));
}
private void addMenuItem(Menu parent, final MenuItemBase menuitem) {
insertMenuItem(parent, menuitem, parent.getItems().size());
}
private void insertMenuItem(final Menu parent, final MenuItemBase menuitem, int pos) {
Application app = Application.GetApplication();
menuitem.visibleProperty().removeListener(visibilityListener);
menuitem.visibleProperty().addListener(visibilityListener);
if (!menuitem.isVisible()) {
return;
}
if (menuitem instanceof SeparatorMenuItemBase) {
if (menuitem.isVisible()) {
parent.insert(MenuItem.Separator, pos);
}
} else {
MenuItem.Callback callback = new MenuItem.Callback() {
@Override public void action() {
if (menuitem instanceof CheckMenuItemBase) {
CheckMenuItemBase checkItem = (CheckMenuItemBase)menuitem;
checkItem.setSelected(!checkItem.isSelected());
} else if (menuitem instanceof RadioMenuItemBase) {
RadioMenuItemBase radioItem = (RadioMenuItemBase)menuitem;
radioItem.setSelected(true);
}
menuitem.fire();
}
@Override public void validate() {
Menu.EventHandler meh = parent.getEventHandler();
GlassMenuEventHandler gmeh = (GlassMenuEventHandler)meh;
if (gmeh.isMenuOpen()) {
return;
}
menuitem.fireValidation();
}
};
final MenuItem glassSubMenuItem = app.createMenuItem(parseText(menuitem), callback);
menuitem.textProperty().addListener(valueModel -> glassSubMenuItem.setTitle(parseText(menuitem)));
glassSubMenuItem.setPixels(getPixels(menuitem));
menuitem.graphicProperty().addListener(valueModel -> {
glassSubMenuItem.setPixels(getPixels(menuitem));
});
glassSubMenuItem.setEnabled(! menuitem.isDisable());
menuitem.disableProperty().addListener(valueModel -> glassSubMenuItem.setEnabled(!menuitem.isDisable()));
setShortcut(glassSubMenuItem, menuitem);
menuitem.acceleratorProperty().addListener(valueModel -> setShortcut(glassSubMenuItem, menuitem));
menuitem.mnemonicParsingProperty().addListener(valueModel -> glassSubMenuItem.setTitle(parseText(menuitem)));
if (menuitem instanceof CheckMenuItemBase) {
final CheckMenuItemBase checkItem = (CheckMenuItemBase)menuitem;
glassSubMenuItem.setChecked(checkItem.isSelected());
checkItem.selectedProperty().addListener(valueModel -> glassSubMenuItem.setChecked(checkItem.isSelected()));
} else if (menuitem instanceof RadioMenuItemBase) {
final RadioMenuItemBase radioItem = (RadioMenuItemBase)menuitem;
glassSubMenuItem.setChecked(radioItem.isSelected());
radioItem.selectedProperty().addListener(valueModel -> glassSubMenuItem.setChecked(radioItem.isSelected()));
}
parent.insert(glassSubMenuItem, pos);
}
}
private String parseText(MenuItemBase menuItem) {
String text = menuItem.getText();
if (text == null) {
return "";
} else if (!text.isEmpty() && menuItem.isMnemonicParsing()) {
return text.replaceFirst("_([^_])", "$1");
} else {
return text;
}
}
private Pixels getPixels(MenuItemBase menuItem) {
if (menuItem.getGraphic() instanceof ImageView) {
ImageView iv = (ImageView)menuItem.getGraphic();
Image im = iv.getImage();
if (im == null) return null;
String url = im.getUrl();
if (url == null || PixelUtils.supportedFormatType(url)) {
com.sun.prism.Image pi = (com.sun.prism.Image) Toolkit.getImageAccessor().getPlatformImage(im);
return pi == null ? null : PixelUtils.imageToPixels(pi);
}
}
return (null);
}
private void setShortcut(MenuItem glassSubMenuItem, MenuItemBase menuItem) {
final KeyCombination accelerator = menuItem.getAccelerator();
if (accelerator == null) {
glassSubMenuItem.setShortcut(0, 0);
} else if (accelerator instanceof KeyCodeCombination) {
KeyCodeCombination kcc = (KeyCodeCombination)accelerator;
KeyCode code = kcc.getCode();
assert PlatformUtil.isMac() || PlatformUtil.isLinux();
int modifier = glassModifiers(kcc);
if (PlatformUtil.isMac()) {
int finalCode = code.isLetterKey() ? code.getChar().toUpperCase().charAt(0)
: code.getCode();
glassSubMenuItem.setShortcut(finalCode, modifier);
} else if (PlatformUtil.isLinux()) {
String lower = code.getChar().toLowerCase();
if ((modifier & KeyEvent.MODIFIER_CONTROL) != 0) {
glassSubMenuItem.setShortcut(lower.charAt(0), modifier);
} else {
glassSubMenuItem.setShortcut(0, 0);
}
} else {
glassSubMenuItem.setShortcut(0, 0);
}
} else if (accelerator instanceof KeyCharacterCombination) {
KeyCharacterCombination kcc = (KeyCharacterCombination)accelerator;
String kchar = kcc.getCharacter();
glassSubMenuItem.setShortcut(kchar.charAt(0), glassModifiers(kcc));
}
}
private int glassModifiers(KeyCombination kcc) {
int ret = 0;
if (kcc.getShift() == KeyCombination.ModifierValue.DOWN) {
ret += KeyEvent.MODIFIER_SHIFT;
}
if (kcc.getControl() == KeyCombination.ModifierValue.DOWN) {
ret += KeyEvent.MODIFIER_CONTROL;
}
if (kcc.getAlt() == KeyCombination.ModifierValue.DOWN) {
ret += KeyEvent.MODIFIER_ALT;
}
if (kcc.getShortcut() == KeyCombination.ModifierValue.DOWN) {
if (PlatformUtil.isLinux()) {
ret += KeyEvent.MODIFIER_CONTROL;
} else if (PlatformUtil.isMac()) {
ret += KeyEvent.MODIFIER_COMMAND;
}
}
if (kcc.getMeta() == KeyCombination.ModifierValue.DOWN) {
if (PlatformUtil.isLinux()) {
ret += KeyEvent.MODIFIER_WINDOWS;
} else if (PlatformUtil.isMac()) {
ret += KeyEvent.MODIFIER_COMMAND;
}
}
if (kcc instanceof KeyCodeCombination) {
KeyCode kcode = ((KeyCodeCombination)kcc).getCode();
int code = kcode.getCode();
if (((code >= KeyCode.F1.getCode()) && (code <= KeyCode.F12.getCode())) ||
((code >= KeyCode.F13.getCode()) && (code <= KeyCode.F24.getCode()))) {
ret += KeyEvent.MODIFIER_FUNCTION;
}
}
return (ret);
}
}
