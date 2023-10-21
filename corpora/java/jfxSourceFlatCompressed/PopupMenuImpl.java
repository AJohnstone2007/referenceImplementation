package com.sun.javafx.webkit.theme;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.web.WebView;
import com.sun.webkit.Invoker;
import com.sun.webkit.graphics.WCFont;
import com.sun.webkit.graphics.WCPoint;
import com.sun.webkit.WebPage;
import com.sun.webkit.WebPageClient;
public final class PopupMenuImpl extends com.sun.webkit.PopupMenu {
private final static PlatformLogger log = PlatformLogger.getLogger(PopupMenuImpl.class.getName());
private final ContextMenu popupMenu;
public PopupMenuImpl() {
popupMenu = new ContextMenu();
popupMenu.setOnHidden(t1 -> {
log.finer("onHidden");
Invoker.getInvoker().postOnEventThread(() -> {
log.finer("onHidden: notifying");
notifyPopupClosed();
});
});
popupMenu.setOnAction(t -> {
MenuItem item = (MenuItem) t.getTarget();
log.fine("onAction: item={0}", item);
notifySelectionCommited(popupMenu.getItems().indexOf(item));
});
}
@Override protected void show(WebPage page, final int x, final int y, final int width) {
if (log.isLoggable(Level.FINE)) {
log.fine("show at [{0}, {1}], width={2}", new Object[] {x, y, width});
}
popupMenu.setPrefWidth(width);
popupMenu.setPrefHeight(popupMenu.getHeight());
doShow(popupMenu, page, x, y);
}
@Override protected void hide() {
log.fine("hiding");
popupMenu.hide();
}
@Override protected void appendItem(String itemText, boolean isLabel,
boolean isSeparator, boolean isEnabled,
int bgColor, int fgColor, WCFont font)
{
if (log.isLoggable(Level.FINEST)) {
log.finest("itemText={0}, isLabel={1}, isSeparator={2}, isEnabled={3}, " +
"bgColor={4}, fgColor={5}, font={6}", new Object[] {itemText, isLabel,
isSeparator, isEnabled, bgColor, fgColor, font});
}
MenuItem item;
if (isSeparator) {
item = new ContextMenuImpl.SeparatorImpl(null);
} else {
item = new MenuItem(itemText);
item.setDisable(!isEnabled);
}
item.setMnemonicParsing(false);
popupMenu.getItems().add(item);
}
@Override protected void setSelectedItem(int index) {
log.finest("index={0}", index);
}
static void doShow(final ContextMenu popup, WebPage page, int anchorX, int anchorY) {
WebPageClient<WebView> client = page.getPageClient();
assert (client != null);
WCPoint pt = client.windowToScreen(new WCPoint(anchorX, anchorY));
popup.show(client.getContainer().getScene().getWindow(), pt.getX(), pt.getY());
}
}
