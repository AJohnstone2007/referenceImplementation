package com.sun.webkit;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.webkit.graphics.WCRectangle;
class WCWidget {
private final static PlatformLogger log = PlatformLogger.getLogger(WCWidget.class.getName());
static {
initIDs();
}
private int x;
private int y;
private int width;
private int height;
private final WebPage page;
WCWidget(WebPage page) {
this.page = page;
}
WebPage getPage() {
return page;
}
WCRectangle getBounds() {
return new WCRectangle(x, y, width, height);
}
void setBounds(int x, int y, int width, int height) {
this.x = x;
this.y = y;
this.width = width;
this.height = height;
}
protected void destroy() {}
protected void requestFocus() {}
protected void setCursor(long cursorID) {}
protected void setVisible(boolean visible) {}
private void fwkDestroy() {
log.finer("destroy");
destroy();
}
private void fwkSetBounds(int x, int y, int w, int h) {
if (log.isLoggable(Level.FINER)) {
log.finer("setBounds({0}, {1}, {2}, {3})",
new Object[] { x, y, w, h });
}
setBounds(x, y, w, h);
}
private void fwkRequestFocus() {
log.finer("requestFocus");
requestFocus();
}
private void fwkSetCursor(long cursorID) {
if (log.isLoggable(Level.FINER)) {
log.finer("setCursor({0})", cursorID);
}
setCursor(cursorID);
}
private void fwkSetVisible(boolean visible) {
if (log.isLoggable(Level.FINER)) {
log.finer("setVisible({0})", visible);
}
setVisible(visible);
}
protected int fwkGetScreenDepth() {
log.finer("getScreenDepth");
WebPageClient pageClient = page.getPageClient();
return pageClient != null
? pageClient.getScreenDepth()
: 24;
}
protected WCRectangle fwkGetScreenRect(boolean available) {
if (log.isLoggable(Level.FINER)) {
log.finer("getScreenRect({0})", available);
}
WebPageClient pageClient = page.getPageClient();
return pageClient != null
? pageClient.getScreenBounds(available)
: null;
}
private static native void initIDs();
}
