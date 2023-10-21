package com.sun.webkit;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import com.oracle.dalvik.InternalWebView;
public class NativeWebView {
private static List<NativeWebView> views = new ArrayList<NativeWebView>();
private int id;
private WebPage page;
public NativeWebView(WebPage page) {
id = _createAndroidWebView();
this.page = page;
views.add(this);
}
public void moveToTop() {
_moveToTop(this.id);
}
public void moveAndResize(int x, int y, int width, int height) {
_moveAndResize(this.id, x, y, width, height);
}
public void setVisible(boolean visible) {
_setVisible(this.id, visible);
}
void loadUrl(String url) {
_loadUrl(this.id, url);
}
void loadContent(String content, String contentType) {
_loadContent(this.id, content, contentType);
}
void setEncoding(String encoding) {
_setEncoding(this.id, encoding);
}
void dispose() {
_dispose(this.id);
views.remove(this);
}
String getHtmlContent() {
return InternalWebView.getHtmlContent(this.id);
}
private static NativeWebView getViewByID(int id) {
for (NativeWebView wvp : views) {
if (id == wvp.id) {
return wvp;
}
}
System.err.println("Accesing nonexisting/disposed NativewWebView id: " + id);
return null;
}
public static void fire_load_event(final int id, final int frameID, final int state,
final String url, final String contenType,
final int progress, final int errorCode) {
final NativeWebView nwv = NativeWebView.getViewByID(id);
if (nwv == null) {
return;
}
Invoker.getInvoker().invokeOnEventThread(new Runnable() {
@Override
public void run() {
double dprogress = progress / 100.0;
nwv.page.fireLoadEvent(frameID, state, url, contenType, dprogress, errorCode);
}
});
}
static Map<Integer, InternalWebView> webViews = new HashMap<>();
private void _moveAndResize(int id, int x, int y, int width, int height) {
InternalWebView.moveAndResize(id, x, y, width, height);
}
private void _setVisible(int id, boolean visible) {
InternalWebView.setVisible(id, visible);
}
private int _createAndroidWebView() {
InternalWebView internalWebView = new InternalWebView();
int id = internalWebView.getInternalID();
webViews.put(id, internalWebView);
return id;
}
private void _moveToTop(int id) {
}
private void _loadUrl(int id, String url) {
InternalWebView.loadUrl(id, url);
}
private void _dispose(int id) {
InternalWebView.dispose(id);
}
private void _loadContent(int id, String content, String contentType) {
InternalWebView.loadContent(id, content, contentType);
}
private native void _setEncoding(int id, String encoding);
}
