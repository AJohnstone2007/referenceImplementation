package com.sun.javafx.scene.control;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.skin.ContextMenuSkin;
import java.util.Optional;
import javafx.scene.layout.Region;
public class ContextMenuContentShim {
private ContextMenuContentShim() {
}
public static Region get_selectedBackground(ContextMenuContent menu) {
return menu.selectedBackground;
}
public static Menu getOpenSubMenu(ContextMenu menu) {
ContextMenuContent content = getMenuContent(menu);
return content.getOpenSubMenu();
}
public static Menu getShowingSubMenu(ContextMenu menu) {
ContextMenuContent content = getMenuContent(menu);
Menu showingSubMenu = content.getOpenSubMenu();
ContextMenu subContextMenu = content.getSubMenu();
while (showingSubMenu != null) {
content = getMenuContent(subContextMenu);
Menu newShowingMenu = content == null ? null : content.getOpenSubMenu();
subContextMenu = content == null ? null : content.getSubMenu();
if (newShowingMenu == null) {
break;
}
}
return showingSubMenu;
}
public static ObservableList<MenuItem> getShowingMenuItems(ContextMenu menu) {
ContextMenuContent content = getMenuContent(menu);
Menu showingSubMenu = content.getOpenSubMenu();
ContextMenu subContextMenu = content.getSubMenu();
if (showingSubMenu == null || subContextMenu == null) {
return menu.getItems();
}
while (showingSubMenu != null) {
content = getMenuContent(subContextMenu);
Menu newShowingMenu = content == null ? null : content.getOpenSubMenu();
subContextMenu = content == null ? null : content.getSubMenu();
if (newShowingMenu == null) {
break;
}
}
return showingSubMenu.getItems();
}
public static Optional<ContextMenuContent> getShowingMenuContent(ContextMenu menu) {
ContextMenuContent content = getMenuContent(menu);
Menu showingSubMenu = content.getOpenSubMenu();
ContextMenu subContextMenu = content.getSubMenu();
return showingSubMenu != null &&
subContextMenu != null &&
subContextMenu.isShowing() ? getShowingMenuContent(subContextMenu) : Optional.of(content);
}
private static ContextMenuContent getMenuContent(ContextMenu menu) {
ContextMenuSkin skin = (ContextMenuSkin) menu.getSkin();
Node node = skin.getNode();
if (node instanceof ContextMenuContent) {
return (ContextMenuContent) node;
}
return null;
}
public static int getCurrentFocusedIndex(ContextMenu menu) {
Optional<ContextMenuContent> showingMenuContent = getShowingMenuContent(menu);
if (showingMenuContent.isPresent()) {
ContextMenuContent content = showingMenuContent.get();
return content.getCurrentFocusIndex();
}
return -1;
}
public static MenuItem getCurrentFocusedItem(ContextMenu menu) {
ObservableList<MenuItem> showingMenuItems = getShowingMenuItems(menu);
Optional<ContextMenuContent> showingMenuContent = getShowingMenuContent(menu);
if (showingMenuContent.isPresent()) {
ContextMenuContent content = showingMenuContent.get();
int currentFocusIndex = content.getCurrentFocusIndex();
return currentFocusIndex == -1 ? null : showingMenuItems.get(currentFocusIndex);
}
return null;
}
}
