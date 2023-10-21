package javafx.scene.control.skin;
import java.util.List;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
public class TabPaneSkinShim {
public static ContextMenu getTabsMenu(TabPaneSkin tpSkin) {
return tpSkin.test_getTabsMenu();
}
public static void disableAnimations(TabPaneSkin tpSkin) {
tpSkin.test_disableAnimations();
}
public static List<Node> getTabHeaders(TabPane tabPane) {
StackPane headersRegion = (StackPane) tabPane.lookup(".headers-region");
return headersRegion.getChildren();
}
public static double getHeaderAreaScrollOffset(TabPane tabPane) {
TabPaneSkin skin = (TabPaneSkin) tabPane.getSkin();
return skin.test_getHeaderAreaScrollOffset();
}
public static void setHeaderAreaScrollOffset(TabPane tabPane, double offset) {
TabPaneSkin skin = (TabPaneSkin) tabPane.getSkin();
skin.test_setHeaderAreaScrollOffset(offset);
}
public static boolean isTabsFit(TabPane tabPane) {
TabPaneSkin skin = (TabPaneSkin) tabPane.getSkin();
return skin.test_isTabsFit();
}
}
