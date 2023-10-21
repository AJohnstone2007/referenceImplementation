package javafx.scene.control.skin;
import com.sun.javafx.scene.control.ContextMenuContent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Skin;
import javafx.scene.control.skin.MenuBarSkin;
import javafx.scene.control.skin.MenuButtonSkinBase;
import javafx.scene.layout.VBox;
public class MenuBarSkinShim {
public static MenuButton getNodeForMenu(MenuBarSkin skin, int i) {
return skin.getNodeForMenu(i);
}
public static Skin getPopupSkin(MenuButton mb) {
return ((MenuButtonSkinBase)mb.getSkin()).popup.getSkin();
}
public static ContextMenuContent getMenuContent(MenuButton mb) {
ContextMenuContent cmc = (ContextMenuContent)getPopupSkin(mb).getNode();
return cmc;
}
public static int getFocusedMenuIndex(MenuBarSkin skin) {
return skin.getFocusedMenuIndex();
}
public static void setFocusedMenuIndex(MenuBarSkin skin, int index) {
skin.setFocusedMenuIndex(index);
}
}
