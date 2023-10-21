package com.sun.javafx.scene.control;
import com.sun.javafx.scene.control.ContextMenuContent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Skin;
import javafx.scene.control.skin.MenuBarSkin;
import javafx.scene.control.skin.MenuButtonSkinBase;
import javafx.scene.layout.VBox;
public class MenuBarMenuButtonShim {
public static ContextMenu getSubMenu(ContextMenuContent cmc) {
return cmc.getSubMenu();
}
public static ContextMenuContent getSubMenuContent(ContextMenuContent cmc) {
ContextMenu cm = cmc.getSubMenu();
return (cm != null) ? (ContextMenuContent)cm.getSkin().getNode() : null;
}
public static ContextMenuContent.MenuItemContainer getDisplayNodeForMenuItem(ContextMenuContent cmc, int i) {
VBox itemsContainer = cmc.getItemsContainer();
return (i < itemsContainer.getChildren().size()) ?
(ContextMenuContent.MenuItemContainer)itemsContainer.getChildren().get(i) : null;
}
public static void setCurrentFocusedIndex(ContextMenuContent cmc, int i) {
cmc.setCurrentFocusedIndex(i);
}
}
