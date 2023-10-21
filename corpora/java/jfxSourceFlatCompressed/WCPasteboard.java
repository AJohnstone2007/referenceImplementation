package com.sun.webkit;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.webkit.graphics.WCImageFrame;
final class WCPasteboard {
private final static PlatformLogger log =
PlatformLogger.getLogger(WCPasteboard.class.getName());
private static final Pasteboard pasteboard;
static {
pasteboard = Utilities.getUtilities().createPasteboard();
};
private WCPasteboard() {
}
private static String getPlainText() {
log.fine("getPlainText()");
return pasteboard.getPlainText();
}
private static String getHtml() {
log.fine("getHtml()");
return pasteboard.getHtml();
}
private static void writePlainText(String text) {
log.fine("writePlainText(): text = {0}", new Object[] {text});
pasteboard.writePlainText(text);
}
private static void writeSelection(boolean canSmartCopyOrDelete, String text, String html)
{
log.fine("writeSelection(): canSmartCopyOrDelete = {0},\n text = \n{1}\n html=\n{2}",
new Object[] {canSmartCopyOrDelete, text, html});
pasteboard.writeSelection(canSmartCopyOrDelete, text, html);
}
private static void writeImage(WCImageFrame img) {
log.fine("writeImage(): img = {0}", new Object[] {img});
pasteboard.writeImage(img);
}
private static void writeUrl(String url, String markup) {
log.fine("writeUrl(): url = {0}, markup = {1}",
new Object[] {url, markup});
pasteboard.writeUrl(url, markup);
}
}
