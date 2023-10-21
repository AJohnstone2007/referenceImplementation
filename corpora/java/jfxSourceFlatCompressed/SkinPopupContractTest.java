package test.javafx.scene.control.skin;
import org.junit.Test;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tooltip;
import javafx.scene.control.skin.ContextMenuSkin;
import javafx.scene.control.skin.TooltipSkin;
public class SkinPopupContractTest {
@Test
public void testTooltipSkinDispose() {
Tooltip tooltip = new Tooltip();
tooltip.setSkin(new TooltipSkin(tooltip));
tooltip.getSkin().dispose();
tooltip.getSkin().dispose();
}
@Test
public void testContextMenuSkinDispose() {
ContextMenu contextMenu = new ContextMenu();
contextMenu.setSkin(new ContextMenuSkin(contextMenu));
contextMenu.getSkin().dispose();
contextMenu.getSkin().dispose();
}
}
