package com.sun.javafx.webkit;
import com.sun.javafx.scene.SceneHelper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.scene.input.ExtendedInputMethodRequests;
import com.sun.webkit.Invoker;
import javafx.geometry.Point2D;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodHighlight;
import javafx.scene.input.InputMethodTextRun;
import javafx.scene.web.WebView;
import com.sun.webkit.InputMethodClient;
import com.sun.webkit.WebPage;
import com.sun.webkit.event.WCInputMethodEvent;
import com.sun.webkit.graphics.WCPoint;
public final class InputMethodClientImpl
implements InputMethodClient, ExtendedInputMethodRequests
{
private static final PlatformLogger log =
PlatformLogger.getLogger(InputMethodClientImpl.class.getName());
private final WeakReference<WebView> wvRef;
private final WebPage webPage;
private boolean state;
public InputMethodClientImpl(WebView wv, WebPage webPage) {
this.wvRef = new WeakReference<WebView>(wv);
this.webPage = webPage;
if (webPage != null) {
webPage.setInputMethodClient(this);
}
}
public void activateInputMethods(final boolean doActivate) {
WebView wv = wvRef.get();
if (wv != null && wv.getScene() != null) {
SceneHelper.enableInputMethodEvents(wv.getScene(), doActivate);
}
state = doActivate;
}
public boolean getInputMethodState() {
return state;
}
public static WCInputMethodEvent convertToWCInputMethodEvent(InputMethodEvent ie) {
List<Integer> underlines = new ArrayList<Integer>();
StringBuilder composed = new StringBuilder();
int pos = 0;
for (InputMethodTextRun run : ie.getComposed()) {
String rawText = run.getText();
InputMethodHighlight imh = run.getHighlight();
underlines.add(pos);
underlines.add(pos + rawText.length());
underlines.add((imh == InputMethodHighlight.SELECTED_CONVERTED ||
imh == InputMethodHighlight.SELECTED_RAW) ? 1 : 0);
pos += rawText.length();
composed.append(rawText);
}
int size = underlines.size();
if (size == 0) {
underlines.add(0);
underlines.add(pos);
underlines.add(0);
size = underlines.size();
}
int[] attributes = new int[size];
for (int i = 0; i < size; i++) {
attributes[i] = underlines.get(i);
}
return new WCInputMethodEvent(ie.getCommitted(), composed.toString(),
attributes, ie.getCaretPosition());
}
public Point2D getTextLocation(int offset) {
FutureTask<Point2D> f = new FutureTask<>(() -> {
int[] loc = webPage.getClientTextLocation(offset);
WCPoint point = webPage.getPageClient().windowToScreen(
new WCPoint(loc[0], loc[1] + loc[3]));
return new Point2D(point.getIntX(), point.getIntY());
});
Invoker.getInvoker().invokeOnEventThread(f);
Point2D result = null;
try {
result = f.get();
} catch (ExecutionException ex) {
log.severe("InputMethodClientImpl.getTextLocation " + ex);
} catch (InterruptedException ex) {
log.severe("InputMethodClientImpl.getTextLocation InterruptedException" + ex);
}
return result;
}
public int getLocationOffset(int x, int y) {
FutureTask<Integer> f = new FutureTask<>(() -> {
WCPoint point = webPage.getPageClient().windowToScreen(new WCPoint(0, 0));
return webPage.getClientLocationOffset(x - point.getIntX(), y - point.getIntY());
});
Invoker.getInvoker().invokeOnEventThread(f);
int location = 0;
try {
location = f.get();
} catch (ExecutionException ex) {
log.severe("InputMethodClientImpl.getLocationOffset " + ex);
} catch (InterruptedException ex) {
log.severe("InputMethodClientImpl.getTextLocation InterruptedException" + ex);
}
return location;
}
public void cancelLatestCommittedText() {
}
public String getSelectedText() {
return webPage.getClientSelectedText();
}
@Override
public int getInsertPositionOffset() {
return webPage.getClientInsertPositionOffset();
}
@Override
public String getCommittedText(int begin, int end) {
try {
return webPage.getClientCommittedText().substring(begin, end);
} catch (StringIndexOutOfBoundsException e) {
throw new IllegalArgumentException(e);
}
}
@Override
public int getCommittedTextLength() {
return webPage.getClientCommittedTextLength();
}
}
