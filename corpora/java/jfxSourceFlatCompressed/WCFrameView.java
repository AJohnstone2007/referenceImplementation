package com.sun.webkit;
final class WCFrameView extends WCWidget {
WCFrameView(WebPage page) {
super(page);
}
@Override protected void requestFocus() {
WebPageClient pageClient = getPage().getPageClient();
if (pageClient != null) {
pageClient.setFocus(true);
}
}
@Override protected void setCursor(long cursorID) {
WebPageClient pageClient = getPage().getPageClient();
if (pageClient != null) {
pageClient.setCursor(cursorID);
}
}
}
