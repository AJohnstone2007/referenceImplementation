package javafx.scene.web;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.scene.web.Debugger;
import com.sun.javafx.scene.web.Printable;
import com.sun.javafx.tk.TKPulseListener;
import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.webkit.*;
import com.sun.javafx.webkit.prism.PrismGraphicsManager;
import com.sun.javafx.webkit.prism.PrismInvoker;
import com.sun.javafx.webkit.prism.theme.PrismRenderer;
import com.sun.javafx.webkit.theme.RenderThemeImpl;
import com.sun.javafx.webkit.theme.Renderer;
import com.sun.webkit.*;
import com.sun.webkit.graphics.WCGraphicsManager;
import com.sun.webkit.network.URLs;
import com.sun.webkit.network.Util;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Rectangle2D;
import javafx.print.JobSettings;
import javafx.print.PageLayout;
import javafx.print.PageRange;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.util.Callback;
import org.w3c.dom.Document;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import static java.lang.String.format;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import static com.sun.webkit.LoadListenerClient.*;
final public class WebEngine {
static {
Accessor.setPageAccessor(w -> w == null ? null : w.getPage());
Invoker.setInvoker(new PrismInvoker());
Renderer.setRenderer(new PrismRenderer());
WCGraphicsManager.setGraphicsManager(new PrismGraphicsManager());
CursorManager.setCursorManager(new CursorManagerImpl());
com.sun.webkit.EventLoop.setEventLoop(new EventLoopImpl());
ThemeClient.setDefaultRenderTheme(new RenderThemeImpl());
Utilities.setUtilities(new UtilitiesImpl());
}
private static final PlatformLogger logger =
PlatformLogger.getLogger(WebEngine.class.getName());
private static int instanceCount = 0;
private final ObjectProperty<WebView> view = new SimpleObjectProperty<WebView>(this, "view");
private final LoadWorker loadWorker = new LoadWorker();
private final WebPage page;
private final SelfDisposer disposer;
private final DebuggerImpl debugger = new DebuggerImpl();
private boolean userDataDirectoryApplied = false;
public final Worker<Void> getLoadWorker() {
return loadWorker;
}
private final DocumentProperty document = new DocumentProperty();
public final Document getDocument() { return document.getValue(); }
public final ReadOnlyObjectProperty<Document> documentProperty() {
return document;
}
private final ReadOnlyStringWrapper location = new ReadOnlyStringWrapper(this, "location");
public final String getLocation() { return location.getValue(); }
public final ReadOnlyStringProperty locationProperty() { return location.getReadOnlyProperty(); }
private void updateLocation(String value) {
this.location.set(value);
this.document.invalidate(false);
this.title.set(null);
}
private final ReadOnlyStringWrapper title = new ReadOnlyStringWrapper(this, "title");
public final String getTitle() { return title.getValue(); }
public final ReadOnlyStringProperty titleProperty() { return title.getReadOnlyProperty(); }
private void updateTitle() {
title.set(page.getTitle(page.getMainFrame()));
}
private BooleanProperty javaScriptEnabled;
public final void setJavaScriptEnabled(boolean value) {
javaScriptEnabledProperty().set(value);
}
public final boolean isJavaScriptEnabled() {
return javaScriptEnabled == null ? true : javaScriptEnabled.get();
}
public final BooleanProperty javaScriptEnabledProperty() {
if (javaScriptEnabled == null) {
javaScriptEnabled = new BooleanPropertyBase(true) {
@Override public void invalidated() {
checkThread();
page.setJavaScriptEnabled(get());
}
@Override public Object getBean() {
return WebEngine.this;
}
@Override public String getName() {
return "javaScriptEnabled";
}
};
}
return javaScriptEnabled;
}
private StringProperty userStyleSheetLocation;
public final void setUserStyleSheetLocation(String value) {
userStyleSheetLocationProperty().set(value);
}
public final String getUserStyleSheetLocation() {
return userStyleSheetLocation == null ? null : userStyleSheetLocation.get();
}
private byte[] readFully(BufferedInputStream in) throws IOException {
final int BUF_SIZE = 4096;
int outSize = 0;
final List<byte[]> outList = new ArrayList<>();
byte[] buffer = new byte[BUF_SIZE];
while (true) {
int nBytes = in.read(buffer);
if (nBytes < 0) break;
byte[] chunk;
if (nBytes == buffer.length) {
chunk = buffer;
buffer = new byte[BUF_SIZE];
} else {
chunk = new byte[nBytes];
System.arraycopy(buffer, 0, chunk, 0, nBytes);
}
outList.add(chunk);
outSize += nBytes;
}
final byte[] out = new byte[outSize];
int outPos = 0;
for (byte[] chunk : outList) {
System.arraycopy(chunk, 0, out, outPos, chunk.length);
outPos += chunk.length;
}
return out;
}
public final StringProperty userStyleSheetLocationProperty() {
if (userStyleSheetLocation == null) {
userStyleSheetLocation = new StringPropertyBase(null) {
private final static String DATA_PREFIX = "data:text/css;charset=utf-8;base64,";
@Override public void invalidated() {
checkThread();
String url = get();
String dataUrl;
if (url == null || url.length() <= 0) {
dataUrl = null;
} else if (url.startsWith(DATA_PREFIX)) {
dataUrl = url;
} else if (url.startsWith("file:") ||
url.startsWith("jar:") ||
url.startsWith("jrt:") ||
url.startsWith("data:"))
{
try {
URLConnection conn = URLs.newURL(url).openConnection();
conn.connect();
BufferedInputStream in =
new BufferedInputStream(conn.getInputStream());
byte[] inBytes = readFully(in);
String out = Base64.getMimeEncoder().encodeToString(inBytes);
dataUrl = DATA_PREFIX + out;
} catch (IOException e) {
throw new RuntimeException(e);
}
} else {
throw new IllegalArgumentException("Invalid stylesheet URL");
}
page.setUserStyleSheetLocation(dataUrl);
}
@Override public Object getBean() {
return WebEngine.this;
}
@Override public String getName() {
return "userStyleSheetLocation";
}
};
}
return userStyleSheetLocation;
}
private final ObjectProperty<File> userDataDirectory =
new SimpleObjectProperty<>(this, "userDataDirectory");
public final File getUserDataDirectory() {
return userDataDirectory.get();
}
public final void setUserDataDirectory(File value) {
userDataDirectory.set(value);
}
public final ObjectProperty<File> userDataDirectoryProperty() {
return userDataDirectory;
}
private StringProperty userAgent;
public final void setUserAgent(String value) {
userAgentProperty().set(value);
}
public final String getUserAgent() {
return userAgent == null ? page.getUserAgent() : userAgent.get();
}
public final StringProperty userAgentProperty() {
if (userAgent == null) {
userAgent = new StringPropertyBase(page.getUserAgent()) {
@Override public void invalidated() {
checkThread();
page.setUserAgent(get());
}
@Override public Object getBean() {
return WebEngine.this;
}
@Override public String getName() {
return "userAgent";
}
};
}
return userAgent;
}
private final ObjectProperty<EventHandler<WebEvent<String>>> onAlert
= new SimpleObjectProperty<EventHandler<WebEvent<String>>>(this, "onAlert");
public final EventHandler<WebEvent<String>> getOnAlert() { return onAlert.get(); }
public final void setOnAlert(EventHandler<WebEvent<String>> handler) { onAlert.set(handler); }
public final ObjectProperty<EventHandler<WebEvent<String>>> onAlertProperty() { return onAlert; }
private final ObjectProperty<EventHandler<WebEvent<String>>> onStatusChanged
= new SimpleObjectProperty<EventHandler<WebEvent<String>>>(this, "onStatusChanged");
public final EventHandler<WebEvent<String>> getOnStatusChanged() { return onStatusChanged.get(); }
public final void setOnStatusChanged(EventHandler<WebEvent<String>> handler) { onStatusChanged.set(handler); }
public final ObjectProperty<EventHandler<WebEvent<String>>> onStatusChangedProperty() { return onStatusChanged; }
private final ObjectProperty<EventHandler<WebEvent<Rectangle2D>>> onResized
= new SimpleObjectProperty<EventHandler<WebEvent<Rectangle2D>>>(this, "onResized");
public final EventHandler<WebEvent<Rectangle2D>> getOnResized() { return onResized.get(); }
public final void setOnResized(EventHandler<WebEvent<Rectangle2D>> handler) { onResized.set(handler); }
public final ObjectProperty<EventHandler<WebEvent<Rectangle2D>>> onResizedProperty() { return onResized; }
private final ObjectProperty<EventHandler<WebEvent<Boolean>>> onVisibilityChanged
= new SimpleObjectProperty<EventHandler<WebEvent<Boolean>>>(this, "onVisibilityChanged");
public final EventHandler<WebEvent<Boolean>> getOnVisibilityChanged() { return onVisibilityChanged.get(); }
public final void setOnVisibilityChanged(EventHandler<WebEvent<Boolean>> handler) { onVisibilityChanged.set(handler); }
public final ObjectProperty<EventHandler<WebEvent<Boolean>>> onVisibilityChangedProperty() { return onVisibilityChanged; }
private final ObjectProperty<Callback<PopupFeatures, WebEngine>> createPopupHandler
= new SimpleObjectProperty<Callback<PopupFeatures, WebEngine>>(this, "createPopupHandler",
p -> WebEngine.this);
public final Callback<PopupFeatures, WebEngine> getCreatePopupHandler() { return createPopupHandler.get(); }
public final void setCreatePopupHandler(Callback<PopupFeatures, WebEngine> handler) { createPopupHandler.set(handler); }
public final ObjectProperty<Callback<PopupFeatures, WebEngine>> createPopupHandlerProperty() { return createPopupHandler; }
private final ObjectProperty<Callback<String, Boolean>> confirmHandler
= new SimpleObjectProperty<Callback<String, Boolean>>(this, "confirmHandler");
public final Callback<String, Boolean> getConfirmHandler() { return confirmHandler.get(); }
public final void setConfirmHandler(Callback<String, Boolean> handler) { confirmHandler.set(handler); }
public final ObjectProperty<Callback<String, Boolean>> confirmHandlerProperty() { return confirmHandler; }
private final ObjectProperty<Callback<PromptData, String>> promptHandler
= new SimpleObjectProperty<Callback<PromptData, String>>(this, "promptHandler");
public final Callback<PromptData, String> getPromptHandler() { return promptHandler.get(); }
public final void setPromptHandler(Callback<PromptData, String> handler) { promptHandler.set(handler); }
public final ObjectProperty<Callback<PromptData, String>> promptHandlerProperty() { return promptHandler; }
private final ObjectProperty<EventHandler<WebErrorEvent>> onError =
new SimpleObjectProperty<>(this, "onError");
public final EventHandler<WebErrorEvent> getOnError() {
return onError.get();
}
public final void setOnError(EventHandler<WebErrorEvent> handler) {
onError.set(handler);
}
public final ObjectProperty<EventHandler<WebErrorEvent>> onErrorProperty() {
return onError;
}
public WebEngine() {
this(null, false);
}
public WebEngine(String url) {
this(url, true);
}
private WebEngine(String url, boolean callLoad) {
checkThread();
Accessor accessor = new AccessorImpl(this);
page = new WebPage(
new WebPageClientImpl(accessor),
new UIClientImpl(accessor),
null,
new InspectorClientImpl(this),
new ThemeClientImpl(accessor),
false);
page.addLoadListenerClient(new PageLoadListener(this));
history = new WebHistory(page);
disposer = new SelfDisposer(page);
Disposer.addRecord(this, disposer);
if (callLoad) {
load(url);
}
if (instanceCount == 0 &&
Timer.getMode() == Timer.Mode.PLATFORM_TICKS)
{
PulseTimer.start();
}
instanceCount++;
}
public void load(String url) {
checkThread();
loadWorker.cancelAndReset();
if (url == null || url.equals("") || url.equals("about:blank")) {
url = "";
} else {
try {
url = Util.adjustUrlForWebKit(url);
} catch (MalformedURLException e) {
loadWorker.dispatchLoadEvent(getMainFrame(),
PAGE_STARTED, url, null, 0.0, 0);
loadWorker.dispatchLoadEvent(getMainFrame(),
LOAD_FAILED, url, null, 0.0, MALFORMED_URL);
return;
}
}
applyUserDataDirectory();
page.open(page.getMainFrame(), url);
}
public void loadContent(String content) {
loadContent(content, "text/html");
}
public void loadContent(String content, String contentType) {
checkThread();
loadWorker.cancelAndReset();
applyUserDataDirectory();
page.load(page.getMainFrame(), content, contentType);
}
public void reload() {
checkThread();
page.refresh(page.getMainFrame());
}
private final WebHistory history;
public WebHistory getHistory() {
return history;
}
public Object executeScript(String script) {
checkThread();
applyUserDataDirectory();
return page.executeScript(page.getMainFrame(), script);
}
private long getMainFrame() {
return page.getMainFrame();
}
WebPage getPage() {
return page;
}
void setView(WebView view) {
this.view.setValue(view);
}
private void stop() {
checkThread();
page.stop(page.getMainFrame());
}
private void applyUserDataDirectory() {
if (userDataDirectoryApplied) {
return;
}
userDataDirectoryApplied = true;
File nominalUserDataDir = getUserDataDirectory();
while (true) {
File userDataDir;
String displayString;
if (nominalUserDataDir == null) {
userDataDir = defaultUserDataDirectory();
displayString = format("null (%s)", userDataDir);
} else {
userDataDir = nominalUserDataDir;
displayString = userDataDir.toString();
}
logger.fine("Trying to apply user data directory [{0}]", displayString);
String errorMessage;
EventType<WebErrorEvent> errorType;
Throwable error;
try {
userDataDir = DirectoryLock.canonicalize(userDataDir);
File localStorageDir = new File(userDataDir, "localstorage");
File[] dirs = new File[] {
userDataDir,
localStorageDir,
};
for (File dir : dirs) {
createDirectories(dir);
File test = new File(dir, ".test");
if (test.createNewFile()) {
test.delete();
}
}
disposer.userDataDirectoryLock = new DirectoryLock(userDataDir);
page.setLocalStorageDatabasePath(localStorageDir.getPath());
page.setLocalStorageEnabled(true);
logger.fine("User data directory [{0}] has "
+ "been applied successfully", displayString);
return;
} catch (DirectoryLock.DirectoryAlreadyInUseException ex) {
errorMessage = "User data directory [%s] is already in use";
errorType = WebErrorEvent.USER_DATA_DIRECTORY_ALREADY_IN_USE;
error = ex;
} catch (IOException ex) {
errorMessage = "An I/O error occurred while setting up "
+ "user data directory [%s]";
errorType = WebErrorEvent.USER_DATA_DIRECTORY_IO_ERROR;
error = ex;
} catch (SecurityException ex) {
errorMessage = "A security error occurred while setting up "
+ "user data directory [%s]";
errorType = WebErrorEvent.USER_DATA_DIRECTORY_SECURITY_ERROR;
error = ex;
}
errorMessage = format(errorMessage, displayString);
logger.fine("{0}, calling error handler", errorMessage);
File oldNominalUserDataDir = nominalUserDataDir;
fireError(errorType, errorMessage, error);
nominalUserDataDir = getUserDataDirectory();
if (Objects.equals(nominalUserDataDir, oldNominalUserDataDir)) {
logger.fine("Error handler did not modify user data directory, "
+ "continuing without user data directory");
return;
} else {
logger.fine("Error handler has set user data directory to [{0}], "
+ "retrying", nominalUserDataDir);
continue;
}
}
}
private static File defaultUserDataDirectory() {
return new File(
com.sun.glass.ui.Application.GetApplication()
.getDataDirectory(),
"webview");
}
private static void createDirectories(File directory) throws IOException {
Path path = directory.toPath();
try {
Files.createDirectories(path, PosixFilePermissions.asFileAttribute(
PosixFilePermissions.fromString("rwx------")));
} catch (UnsupportedOperationException ex) {
Files.createDirectories(path);
}
}
private void fireError(EventType<WebErrorEvent> eventType, String message,
Throwable exception)
{
EventHandler<WebErrorEvent> handler = getOnError();
if (handler != null) {
handler.handle(new WebErrorEvent(this, eventType,
message, exception));
}
}
void dispose() {
disposer.dispose();
}
private static final class SelfDisposer implements DisposerRecord {
private WebPage page;
private DirectoryLock userDataDirectoryLock;
private SelfDisposer(WebPage page) {
this.page = page;
}
@Override public void dispose() {
if (page == null) {
return;
}
page.dispose();
page = null;
if (userDataDirectoryLock != null) {
userDataDirectoryLock.close();
}
instanceCount--;
if (instanceCount == 0 &&
Timer.getMode() == Timer.Mode.PLATFORM_TICKS)
{
PulseTimer.stop();
}
}
}
private static final class AccessorImpl extends Accessor {
private final WeakReference<WebEngine> engine;
private AccessorImpl(WebEngine w) {
this.engine = new WeakReference<WebEngine>(w);
}
@Override public WebEngine getEngine() {
return engine.get();
}
@Override public WebPage getPage() {
WebEngine w = getEngine();
return w == null ? null : w.page;
}
@Override public WebView getView() {
WebEngine w = getEngine();
return w == null ? null : w.view.get();
}
@Override public void addChild(Node child) {
WebView view = getView();
if (view != null) {
view.getChildren().add(child);
}
}
@Override public void removeChild(Node child) {
WebView view = getView();
if (view != null) {
view.getChildren().remove(child);
}
}
@Override public void addViewListener(InvalidationListener l) {
WebEngine w = getEngine();
if (w != null) {
w.view.addListener(l);
}
}
}
private static final class PulseTimer {
private static final AnimationTimer animation =
new AnimationTimer() {
@Override public void handle(long l) {}
};
private static final TKPulseListener listener =
() -> {
Platform.runLater(() -> Timer.getTimer().notifyTick());
};
private static void start(){
Toolkit.getToolkit().addSceneTkPulseListener(listener);
animation.start();
}
private static void stop() {
Toolkit.getToolkit().removeSceneTkPulseListener(listener);
animation.stop();
}
}
static void checkThread() {
Toolkit.getToolkit().checkFxUserThread();
}
private static final class PageLoadListener implements LoadListenerClient {
private final WeakReference<WebEngine> engine;
private PageLoadListener(WebEngine engine) {
this.engine = new WeakReference<WebEngine>(engine);
}
@Override public void dispatchLoadEvent(long frame, int state,
String url, String contentType, double progress, int errorCode)
{
WebEngine w = engine.get();
if (w != null) {
w.loadWorker.dispatchLoadEvent(frame, state, url,
contentType, progress, errorCode);
}
}
@Override public void dispatchResourceLoadEvent(long frame,
int state, String url, String contentType, double progress,
int errorCode)
{
}
}
private final class LoadWorker implements Worker<Void> {
private final ReadOnlyObjectWrapper<State> state = new ReadOnlyObjectWrapper<State>(this, "state", State.READY);
@Override public final State getState() { checkThread(); return state.get(); }
@Override public final ReadOnlyObjectProperty<State> stateProperty() { checkThread(); return state.getReadOnlyProperty(); }
private void updateState(State value) {
checkThread();
this.state.set(value);
running.set(value == State.SCHEDULED || value == State.RUNNING);
}
private final ReadOnlyObjectWrapper<Void> value = new ReadOnlyObjectWrapper<Void>(this, "value", null);
@Override public final Void getValue() { checkThread(); return value.get(); }
@Override public final ReadOnlyObjectProperty<Void> valueProperty() { checkThread(); return value.getReadOnlyProperty(); }
private final ReadOnlyObjectWrapper<Throwable> exception = new ReadOnlyObjectWrapper<Throwable>(this, "exception");
@Override public final Throwable getException() { checkThread(); return exception.get(); }
@Override public final ReadOnlyObjectProperty<Throwable> exceptionProperty() { checkThread(); return exception.getReadOnlyProperty(); }
private final ReadOnlyDoubleWrapper workDone = new ReadOnlyDoubleWrapper(this, "workDone", -1);
@Override public final double getWorkDone() { checkThread(); return workDone.get(); }
@Override public final ReadOnlyDoubleProperty workDoneProperty() { checkThread(); return workDone.getReadOnlyProperty(); }
private final ReadOnlyDoubleWrapper totalWorkToBeDone = new ReadOnlyDoubleWrapper(this, "totalWork", -1);
@Override public final double getTotalWork() { checkThread(); return totalWorkToBeDone.get(); }
@Override public final ReadOnlyDoubleProperty totalWorkProperty() { checkThread(); return totalWorkToBeDone.getReadOnlyProperty(); }
private final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper(this, "progress", -1);
@Override public final double getProgress() { checkThread(); return progress.get(); }
@Override public final ReadOnlyDoubleProperty progressProperty() { checkThread(); return progress.getReadOnlyProperty(); }
private void updateProgress(double p) {
totalWorkToBeDone.set(100.0);
workDone.set(p * 100.0);
progress.set(p);
}
private final ReadOnlyBooleanWrapper running = new ReadOnlyBooleanWrapper(this, "running", false);
@Override public final boolean isRunning() { checkThread(); return running.get(); }
@Override public final ReadOnlyBooleanProperty runningProperty() { checkThread(); return running.getReadOnlyProperty(); }
private final ReadOnlyStringWrapper message = new ReadOnlyStringWrapper(this, "message", "");
@Override public final String getMessage() { return message.get(); }
@Override public final ReadOnlyStringProperty messageProperty() { return message.getReadOnlyProperty(); }
private final ReadOnlyStringWrapper title = new ReadOnlyStringWrapper(this, "title", "WebEngine Loader");
@Override public final String getTitle() { return title.get(); }
@Override public final ReadOnlyStringProperty titleProperty() { return title.getReadOnlyProperty(); }
@Override public boolean cancel() {
if (isRunning()) {
stop();
return true;
} else {
return false;
}
}
private void cancelAndReset() {
cancel();
exception.set(null);
message.set("");
totalWorkToBeDone.set(-1);
workDone.set(-1);
progress.set(-1);
updateState(State.READY);
running.set(false);
}
private void dispatchLoadEvent(long frame, int state,
String url, String contentType, double workDone, int errorCode)
{
if (frame != getMainFrame()) {
return;
}
switch (state) {
case PAGE_STARTED:
message.set("Loading " + url);
updateLocation(url);
updateProgress(0.0);
updateState(State.SCHEDULED);
updateState(State.RUNNING);
break;
case PAGE_REDIRECTED:
message.set("Loading " + url);
updateLocation(url);
break;
case PAGE_REPLACED:
message.set("Replaced " + url);
WebEngine.this.location.set(url);
break;
case PAGE_FINISHED:
message.set("Loading complete");
updateProgress(1.0);
updateState(State.SUCCEEDED);
break;
case LOAD_FAILED:
message.set("Loading failed");
exception.set(describeError(errorCode));
updateState(State.FAILED);
break;
case LOAD_STOPPED:
message.set("Loading stopped");
updateState(State.CANCELLED);
break;
case PROGRESS_CHANGED:
updateProgress(workDone);
break;
case TITLE_RECEIVED:
updateTitle();
break;
case DOCUMENT_AVAILABLE:
if (this.state.get() != State.RUNNING) {
dispatchLoadEvent(frame, PAGE_STARTED, url, contentType, workDone, errorCode);
}
document.invalidate(true);
break;
}
}
private Throwable describeError(int errorCode) {
String reason = "Unknown error";
switch (errorCode) {
case UNKNOWN_HOST:
reason = "Unknown host";
break;
case MALFORMED_URL:
reason = "Malformed URL";
break;
case SSL_HANDSHAKE:
reason = "SSL handshake failed";
break;
case CONNECTION_REFUSED:
reason = "Connection refused by server";
break;
case CONNECTION_RESET:
reason = "Connection reset by server";
break;
case NO_ROUTE_TO_HOST:
reason = "No route to host";
break;
case CONNECTION_TIMED_OUT:
reason = "Connection timed out";
break;
case PERMISSION_DENIED:
reason = "Permission denied";
break;
case INVALID_RESPONSE:
reason = "Invalid response from server";
break;
case TOO_MANY_REDIRECTS:
reason = "Too many redirects";
break;
case FILE_NOT_FOUND:
reason = "File not found";
break;
}
return new Throwable(reason);
}
}
private final class DocumentProperty
extends ReadOnlyObjectPropertyBase<Document> {
private boolean available;
private Document document;
private void invalidate(boolean available) {
if (this.available || available) {
this.available = available;
this.document = null;
fireValueChangedEvent();
}
}
public Document get() {
if (!this.available) {
return null;
}
if (this.document == null) {
this.document = page.getDocument(page.getMainFrame());
if (this.document == null) {
this.available = false;
}
}
return this.document;
}
public Object getBean() {
return WebEngine.this;
}
public String getName() {
return "document";
}
}
Debugger getDebugger() {
return debugger;
}
private final class DebuggerImpl implements Debugger {
private boolean enabled;
private Callback<String,Void> messageCallback;
@Override
public boolean isEnabled() {
checkThread();
return enabled;
}
@Override
public void setEnabled(boolean enabled) {
checkThread();
if (enabled != this.enabled) {
if (enabled) {
page.setDeveloperExtrasEnabled(true);
page.connectInspectorFrontend();
} else {
page.disconnectInspectorFrontend();
page.setDeveloperExtrasEnabled(false);
}
this.enabled = enabled;
}
}
@Override
public void sendMessage(String message) {
checkThread();
if (!enabled) {
throw new IllegalStateException("Debugger is not enabled");
}
if (message == null) {
throw new NullPointerException("message is null");
}
page.dispatchInspectorMessageFromFrontend(message);
}
@Override
public Callback<String,Void> getMessageCallback() {
checkThread();
return messageCallback;
}
@Override
public void setMessageCallback(Callback<String,Void> callback) {
checkThread();
messageCallback = callback;
}
}
private static final class InspectorClientImpl implements InspectorClient {
private final WeakReference<WebEngine> engine;
private InspectorClientImpl(WebEngine engine) {
this.engine = new WeakReference<WebEngine>(engine);
}
@SuppressWarnings("removal")
@Override
public boolean sendMessageToFrontend(final String message) {
boolean result = false;
WebEngine webEngine = engine.get();
if (webEngine != null) {
final Callback<String,Void> messageCallback =
webEngine.debugger.messageCallback;
if (messageCallback != null) {
AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
messageCallback.call(message);
return null;
}, webEngine.page.getAccessControlContext());
result = true;
}
}
return result;
}
}
private static final boolean printStatusOK(PrinterJob job) {
switch (job.getJobStatus()) {
case NOT_STARTED:
case PRINTING:
return true;
default:
return false;
}
}
public void print(PrinterJob job) {
if (!printStatusOK(job)) {
return;
}
PageLayout pl = job.getJobSettings().getPageLayout();
float width = (float) pl.getPrintableWidth();
float height = (float) pl.getPrintableHeight();
int pageCount = page.beginPrinting(width, height);
JobSettings jobSettings = job.getJobSettings();
if (jobSettings.getPageRanges() != null) {
PageRange[] pageRanges = jobSettings.getPageRanges();
for (PageRange p : pageRanges) {
for (int i = p.getStartPage(); i <= p.getEndPage() && i <= pageCount; ++i) {
if (printStatusOK(job)) {
Node printable = new Printable(page, i - 1, width);
job.printPage(printable);
}
}
}
} else {
for (int i = 0; i < pageCount; i++) {
if (printStatusOK(job)) {
Node printable = new Printable(page, i, width);
job.printPage(printable);
}
}
}
page.endPrinting();
}
}
