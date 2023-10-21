package com.sun.javafx.webkit.drt;
import com.sun.webkit.LoadListenerClient;
import com.sun.webkit.UIClient;
import com.sun.webkit.WebPage;
import com.sun.webkit.graphics.WCImage;
import com.sun.webkit.graphics.WCRectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
final class UIClientImpl implements UIClient {
private WebPage webPage;
private final List<UIClient> clients = new ArrayList<UIClient>();
private WCRectangle bounds = new WCRectangle(0, 0, 800, 600);
UIClientImpl() {
}
void setWebPage(WebPage webPage) {
this.webPage = webPage;
}
@Override
public WebPage createPage(boolean menu, boolean status, boolean toolbar,
boolean resizable)
{
UIClientImpl client = new UIClientImpl();
final WebPage page = new WebPage(null, client, null, null, new DumpRenderTree.ThemeClientImplStub(), false);
client.setWebPage(page);
page.setBounds(0, 0, 800, 600);
page.addLoadListenerClient(new LoadListenerClient() {
@Override
public void dispatchLoadEvent(long frame, int state, String url, String contentType, double progress, int errorCode) {
if (state == DOCUMENT_AVAILABLE) {
DumpRenderTree.drt.dumpUnloadListeners(page, frame);
}
}
@Override
public void dispatchResourceLoadEvent(long frame, int state, String url, String contentType, double progress, int errorCode) {
}
});
page.resetToConsistentStateBeforeTesting();
page.getMainFrame();
clients.add(client);
return client.webPage;
}
@Override
public void closePage() {
Iterator<UIClient> it = clients.iterator();
while (it.hasNext()) {
it.next().closePage();
it.remove();
}
if (webPage.getMainFrame() != 0) {
webPage.dispose();
}
}
@Override
public void showView() {
}
@Override
public WCRectangle getViewBounds() {
return bounds;
}
@Override
public void setViewBounds(WCRectangle bounds) {
this.bounds = bounds;
}
@Override
public void setStatusbarText(String text) {
}
@Override
public void alert(String text) {
if (!DumpRenderTree.drt.complete()) {
DumpRenderTree.out.printf("ALERT: %s\n", text);
}
}
@Override
public boolean confirm(String text) {
if (!DumpRenderTree.drt.complete()) {
DumpRenderTree.out.printf("CONFIRM: %s\n", text);
}
return false;
}
@Override
public String prompt(String text, String defaultValue) {
if (!DumpRenderTree.drt.complete()) {
DumpRenderTree.out.printf("PROMPT: %s, default text: %s\n", text, defaultValue);
}
return defaultValue;
}
@Override
public boolean canRunBeforeUnloadConfirmPanel() {
return true;
}
@Override
public boolean runBeforeUnloadConfirmPanel(String message) {
if (!DumpRenderTree.drt.complete()) {
DumpRenderTree.out.printf("CONFIRM NAVIGATION: %s\n", message);
}
return !DumpRenderTree.drt.shouldStayOnPageAfterHandlingBeforeUnload();
}
@Override
public String[] chooseFile(String initialFileName, boolean multiple, String mimeFilters) {
if (DumpRenderTree.drt.complete()) {
return null;
}
DumpRenderTree.out.printf("OPEN FILE PANEL\n");
String[] openPanelFiles = DumpRenderTree.drt.openPanelFiles();
if (openPanelFiles == null || openPanelFiles.length == 0) {
return null;
}
final File testURLFile = new File(DumpRenderTree.drt.getTestURL());
String testURLFileParent = testURLFile.getParent();
if (multiple) {
String[] result = new String[openPanelFiles.length];
for (int i = 0; i < openPanelFiles.length; i++) {
result[i] = new File(testURLFileParent, openPanelFiles[i]).getAbsolutePath();
}
return result;
} else {
return new String[] { new File(testURLFileParent, openPanelFiles[0]).getAbsolutePath() };
}
}
@Override
public void print() {
throw new UnsupportedOperationException("Not supported yet");
}
@Override
public void startDrag(WCImage frame, int imageOffsetX, int imageOffsetY,
int eventPosX, int eventPosY, String[] mimeTypes, Object[] values, boolean isImageSource)
{
throw new UnsupportedOperationException("Not supported yet");
}
@Override
public void confirmStartDrag() {
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
public boolean isDragConfirmed() {
return false;
}
}
