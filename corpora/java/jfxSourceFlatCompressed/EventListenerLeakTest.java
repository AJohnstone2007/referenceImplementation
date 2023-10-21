package test.javafx.scene.web;
import com.sun.javafx.application.PlatformImpl;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import static org.junit.Assert.*;
import org.junit.Before;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
public class EventListenerLeakTest {
static List<WeakReference<?>> listenerRefs;
static List<WeakReference<?>> webViewRefs;
WebView webView1;
WebView webView2;
List<EventTarget> domNodes1;
List<EventTarget> domNodes2;
static class MyListener implements EventListener {
private final AtomicInteger clickCount = new AtomicInteger(0);
private MyListener() {
}
int getClickCount() {
return clickCount.get();
}
static MyListener create() {
MyListener listener = new MyListener();
listenerRefs.add(new WeakReference<>(listener));
return listener;
}
@Override
public void handleEvent(Event evt) {
clickCount.incrementAndGet();
}
}
@BeforeClass
public static void setupOnce() throws Exception {
final CountDownLatch startupLatch = new CountDownLatch(1);
PlatformImpl.startup(() -> {
startupLatch.countDown();
});
assertTrue("Timeout waiting for FX runtime to start",
startupLatch.await(15, TimeUnit.SECONDS));
}
void submit(Runnable job) {
final FutureTask<Void> future = new FutureTask<>(job, null);
Platform.runLater(future);
try {
future.get();
} catch (ExecutionException e) {
Throwable cause = e.getCause();
if (cause instanceof AssertionError) {
throw (AssertionError) e.getCause();
} else if (cause instanceof RuntimeException) {
throw (RuntimeException) cause;
}
throw new AssertionError(cause);
} catch (InterruptedException e) {
throw new AssertionError(e);
}
}
protected void loadContent(final WebView webView, final String content) {
final CountDownLatch loadLatch = new CountDownLatch(1);
Platform.runLater(() -> {
final AtomicReference<ChangeListener<Worker.State>> stateListener
= new AtomicReference<>();
stateListener.set((obs, oldState, newState) -> {
WebEngine engine = webView.getEngine();
if (newState == Worker.State.SUCCEEDED) {
engine.getLoadWorker().stateProperty()
.removeListener(stateListener.get());
stateListener.set(null);
loadLatch.countDown();
}
});
webView.getEngine().getLoadWorker().stateProperty()
.addListener(stateListener.get());
webView.getEngine().loadContent(content, "text/html");
});
try {
assertTrue("Timeout waiting for content to load",
loadLatch.await(5, TimeUnit.SECONDS));
} catch (InterruptedException ex) {
throw new RuntimeException("Unexpected exception", ex);
}
}
private List<EventTarget> getDomNodes(WebView webView) {
final List<EventTarget> nodes = new ArrayList<>();
Document doc = webView.getEngine().getDocument();
assertNotNull("Document", doc);
NodeList nodeList = doc.getElementsByTagName("a");
assertNotNull("DOM nodes", nodeList);
for (int i = 0; i < nodeList.getLength(); i++) {
EventTarget node = (EventTarget) nodeList.item(i);
nodes.add(node);
}
return nodes;
}
void click(WebView webView, int link) {
webView.getEngine().executeScript("document.getElementById(\"link"
+ link + "\").click()");
}
void assertNumActive(String msg, List<WeakReference<?>> refs, int exCount)
throws InterruptedException {
int count = -1;
for (int i = 0; i < 10; i++) {
System.gc();
count = (int) refs.stream()
.filter(e -> e.get() != null)
.count();
if (exCount == 0 && count == 0) {
break;
}
Thread.sleep(250);
}
assertEquals("Active references (" + msg + ")", exCount, count);
}
@Before
public void initEach() {
listenerRefs = new ArrayList<>();
webViewRefs = new ArrayList<>();
submit(() -> {
webView1 = new WebView();
webViewRefs.add(new WeakReference<>(webView1));
webView2 = new WebView();
webViewRefs.add(new WeakReference<>(webView2));
});
}
private static final String HTML =
"<body><html>" +
"Link: <a id=\"link0\" href=click>click me 0</a><br>" +
"Link: <a id=\"link1\" href=click>click me 1</a><br>" +
"Link: <a id=\"link2\" href=click>click me 2</a><br>" +
"Link: <a id=\"link3\" href=click>click me 3</a><br>" +
"</html></body>";
private static final String HTML2 =
"<body><html>" +
"Link: <a id=\"link0\" href=click>click me 0</a><br>" +
"</html></body>";
private static final int NUM_DOM_NODES = 4;
@Test
public void oneWebViewSingleListenerNoRelease() throws Exception {
webView2 = null;
loadContent(webView1, HTML);
final List<MyListener> listeners = new ArrayList<>();
submit(() -> {
domNodes1 = getDomNodes(webView1);
assertEquals(NUM_DOM_NODES, domNodes1.size());
listeners.add(MyListener.create());
domNodes1.get(0).addEventListener("click", listeners.get(0), false);
click(webView1, 0);
});
Thread.sleep(100);
assertEquals("Click count", 1, listeners.get(0).getClickCount());
listeners.clear();
domNodes1.clear();
assertNumActive("MyListener", listenerRefs, 1);
}
@Test
public void oneWebViewSingleListenerExplicitRelease() throws Exception {
webView2 = null;
loadContent(webView1, HTML);
final List<WeakReference<MyListener>> listeners = new ArrayList<>();
submit(() -> {
domNodes1 = getDomNodes(webView1);
assertEquals(NUM_DOM_NODES, domNodes1.size());
MyListener myListener = MyListener.create();
listeners.add(new WeakReference<>(myListener));
domNodes1.get(0).addEventListener("click", listeners.get(0).get(), false);
click(webView1, 0);
});
assertNumActive("MyListener", listenerRefs, 1);
Thread.sleep(100);
assertNotNull(listeners.get(0).get());
assertEquals("Click count", 1, listeners.get(0).get().getClickCount());
submit(() -> {
assertNotNull(listeners.get(0).get());
domNodes1.get(0).removeEventListener("click", listeners.get(0).get(), false);
});
domNodes1.clear();
assertNumActive("MyListener", listenerRefs, 0);
}
@Test
public void oneWebViewMultipleListenersExplicitRelease() throws Exception {
webView2 = null;
loadContent(webView1, HTML);
final List<WeakReference<MyListener>> listeners = new ArrayList<>();
submit(() -> {
domNodes1 = getDomNodes(webView1);
assertEquals(NUM_DOM_NODES, domNodes1.size());
MyListener listenerA = MyListener.create();
MyListener listenerB = MyListener.create();
listeners.add(new WeakReference<>(listenerA));
listeners.add(new WeakReference<>(listenerB));
listeners.add(new WeakReference<>(listenerA));
for (int i = 0; i < 3; i++) {
domNodes1.get(i).addEventListener("click", listeners.get(i).get(), false);
}
});
assertSame(listeners.get(0).get(), listeners.get(2).get());
assertNumActive("MyListener", listenerRefs, 2);
assertNotNull(listeners.get(0).get());
assertNotNull(listeners.get(1).get());
assertNotNull(listeners.get(2).get());
submit(() -> {
click(webView1, 0);
click(webView1, 1);
click(webView1, 2);
});
Thread.sleep(100);
assertEquals("Click count", 2, listeners.get(0).get().getClickCount());
assertEquals("Click count", 1, listeners.get(1).get().getClickCount());
assertEquals("Click count", 2, listeners.get(2).get().getClickCount());
submit(() -> {
assertNotNull(listeners.get(0).get());
domNodes1.get(0).removeEventListener("click", listeners.get(0).get(), false);
domNodes1.set(0, null);
});
assertNumActive("MyListener", listenerRefs, 2);
assertNotNull(listeners.get(0).get());
assertNotNull(listeners.get(1).get());
assertNotNull(listeners.get(2).get());
submit(() -> {
click(webView1, 0);
click(webView1, 1);
click(webView1, 2);
});
Thread.sleep(100);
assertEquals("Click count", 3, listeners.get(0).get().getClickCount());
assertEquals("Click count", 2, listeners.get(1).get().getClickCount());
assertEquals("Click count", 3, listeners.get(2).get().getClickCount());
submit(() -> {
assertNotNull(listeners.get(1).get());
domNodes1.get(1).removeEventListener("click", listeners.get(1).get(), false);
domNodes1.set(1, null);
});
assertNumActive("MyListener", listenerRefs, 1);
assertNotNull(listeners.get(0).get());
assertNull(listeners.get(1).get());
assertNotNull(listeners.get(2).get());
submit(() -> {
click(webView1, 0);
click(webView1, 1);
click(webView1, 2);
});
Thread.sleep(100);
assertEquals("Click count", 4, listeners.get(0).get().getClickCount());
assertEquals("Click count", 4, listeners.get(2).get().getClickCount());
submit(() -> {
assertNotNull(listeners.get(2).get());
domNodes1.get(2).removeEventListener("click", listeners.get(2).get(), false);
domNodes1.set(2, null);
});
assertNumActive("MyListener", listenerRefs, 0);
assertNull(listeners.get(0).get());
assertNull(listeners.get(1).get());
assertNull(listeners.get(2).get());
submit(() -> {
click(webView1, 0);
click(webView1, 1);
click(webView1, 2);
});
assertNumActive("MyListener", listenerRefs, 0);
}
@Test
public void oneWebViewSingleListenerImplicitRelease() throws Exception {
webView2 = null;
loadContent(webView1, HTML);
final List<MyListener> listeners = new ArrayList<>();
submit(() -> {
domNodes1 = getDomNodes(webView1);
assertEquals(NUM_DOM_NODES, domNodes1.size());
listeners.add(MyListener.create());
domNodes1.get(0).addEventListener("click", listeners.get(0), false);
});
WeakReference<MyListener> ref = new WeakReference<>(listeners.get(0));
listeners.clear();
domNodes1.clear();
assertNumActive("listeners", listenerRefs, 1);
submit(() -> {
click(webView1, 0);
});
Thread.sleep(100);
listeners.add(ref.get());
assertNotNull(listeners.get(0));
assertEquals("Click count", 1, listeners.get(0).getClickCount());
listeners.clear();
webView1 = null;
assertNumActive("WebView", webViewRefs, 0);
assertNumActive("MyListener", listenerRefs, 0);
}
@Test
public void twoWebViewSingleListenerExplicitRelease() throws Exception {
loadContent(webView1, HTML);
loadContent(webView2, HTML);
final List<MyListener> listeners1 = new ArrayList<>();
final List<MyListener> listeners2 = new ArrayList<>();
submit(() -> {
domNodes1 = getDomNodes(webView1);
assertEquals(NUM_DOM_NODES, domNodes1.size());
domNodes2 = getDomNodes(webView2);
assertEquals(NUM_DOM_NODES, domNodes2.size());
listeners1.add(MyListener.create());
domNodes1.get(0).addEventListener("click", listeners1.get(0), false);
listeners2.add(MyListener.create());
domNodes2.get(0).addEventListener("click", listeners2.get(0), false);
click(webView1, 0);
});
Thread.sleep(100);
assertEquals("Click count", 1, listeners1.get(0).getClickCount());
assertEquals("Click count", 0, listeners2.get(0).getClickCount());
submit(() -> {
click(webView2, 0);
});
Thread.sleep(100);
assertEquals("Click count", 1, listeners1.get(0).getClickCount());
assertEquals("Click count", 1, listeners2.get(0).getClickCount());
submit(() -> {
domNodes1.get(0).removeEventListener("click", listeners1.get(0), false);
});
submit(() -> {
click(webView1, 0);
click(webView2, 0);
});
assertEquals("Click count", 1, listeners1.get(0).getClickCount());
assertEquals("Click count", 2, listeners2.get(0).getClickCount());
listeners1.clear();
domNodes1.clear();
assertNumActive("MyListener", listenerRefs, 1);
submit(() -> {
domNodes2.get(0).removeEventListener("click", listeners2.get(0), false);
});
submit(() -> {
click(webView2, 0);
});
Thread.sleep(100);
assertEquals("Click count", 2, listeners2.get(0).getClickCount());
listeners2.clear();
domNodes2.clear();
assertNumActive("MyListener", listenerRefs, 0);
}
@Test
public void testStrongRefNewContentLoad() throws Exception {
webView2 = null;
loadContent(webView1, HTML);
final List<MyListener> listeners= new ArrayList<>();
submit(() -> {
domNodes1 = getDomNodes(webView1);
listeners.add(MyListener.create());
domNodes1.get(0).addEventListener("click", listeners.get(0), false);
click(webView1, 0);
});
Thread.sleep(100);
assertEquals("Click count", 1, listeners.get(0).getClickCount());
loadContent(webView1, HTML2);
submit(() -> {
click(webView1, 0);
});
Thread.sleep(100);
assertEquals("Click count", 1, listeners.get(0).getClickCount());
assertNumActive("MyListener", listenerRefs, 1);
listeners.clear();
domNodes1.clear();
assertNumActive("MyListener", listenerRefs, 0);
}
@Test
public void oneWebViewRefCountTest() throws Exception {
webView2 = null;
loadContent(webView1, HTML);
final List<WeakReference<MyListener>> listeners = new ArrayList<>();
submit(() -> {
domNodes1 = getDomNodes(webView1);
assertEquals(NUM_DOM_NODES, domNodes1.size());
MyListener listener = MyListener.create();
listeners.add(new WeakReference<>(listener));
for (int i = 0; i < 3; i++) {
domNodes1.get(i).addEventListener("click", listeners.get(0).get(), false);
}
});
submit(() -> {
click(webView1, 0);
click(webView1, 1);
click(webView1, 2);
});
Thread.sleep(100);
assertEquals("Click count", 3, listeners.get(0).get().getClickCount());
MyListener tmpListener = listeners.get(0).get();
submit(() -> {
for (int i = 0; i < 3; i++) {
domNodes1.get(i).removeEventListener("click", listeners.get(0).get(), false);
}
});
submit(() -> {
click(webView1, 0);
click(webView1, 1);
click(webView1, 2);
});
Thread.sleep(100);
assertEquals("Click count", 3, listeners.get(0).get().getClickCount());
submit(() -> {
domNodes1 = getDomNodes(webView1);
assertEquals(NUM_DOM_NODES, domNodes1.size());
MyListener listener = MyListener.create();
listeners.add(new WeakReference<>(listener));
for (int i = 0; i < 3; i++) {
domNodes1.get(i).addEventListener("click", listeners.get(1).get(), false);
}
});
tmpListener = null;
MyListener tmpListener1 = listeners.get(0).get();
submit(() -> {
click(webView1, 0);
click(webView1, 1);
click(webView1, 2);
});
Thread.sleep(100);
assertEquals("Click count", 6, listeners.get(1).get().getClickCount() + listeners.get(0).get().getClickCount());
submit(() -> {
domNodes1 = getDomNodes(webView1);
MyListener listener = MyListener.create();
listeners.add(new WeakReference<>(listener));
for (int i = 0; i < 3; i++) {
domNodes1.get(i).removeEventListener("click", listeners.get(1).get(), false);
}
});
submit(() -> {
click(webView1, 0);
click(webView1, 1);
click(webView1, 2);
});
Thread.sleep(100);
assertEquals("Click count", 6, listeners.get(1).get().getClickCount() + listeners.get(0).get().getClickCount());
listeners.clear();
domNodes1.clear();
tmpListener1 = null;
assertNumActive("MyListener", listenerRefs, 0);
}
@Test
public void oneWebViewMultipleListenersImplicitRelease() throws Exception {
webView2 = null;
loadContent(webView1, HTML);
final List<WeakReference<MyListener>> listeners = new ArrayList<>();
submit(() -> {
domNodes1 = getDomNodes(webView1);
assertEquals(NUM_DOM_NODES, domNodes1.size());
MyListener listenerA = MyListener.create();
MyListener listenerB = MyListener.create();
listeners.add(new WeakReference<>(listenerA));
listeners.add(new WeakReference<>(listenerB));
listeners.add(new WeakReference<>(listenerA));
for (int i = 0; i < 3; i++) {
domNodes1.get(i).addEventListener("click", listeners.get(i).get(), false);
}
});
assertSame(listeners.get(0).get(), listeners.get(2).get());
assertNumActive("MyListener", listenerRefs, 2);
assertNotNull(listeners.get(0).get());
assertNotNull(listeners.get(1).get());
assertNotNull(listeners.get(2).get());
submit(() -> {
click(webView1, 0);
click(webView1, 1);
click(webView1, 2);
});
Thread.sleep(100);
assertEquals("Click count", 2, listeners.get(0).get().getClickCount());
domNodes1.clear();
webView1 = null;
Thread.sleep(100);
assertNumActive("MyListener", listenerRefs, 0);
}
@Test
public void multipleWebViewMultipleListenersImplicitRelease() throws Exception {
loadContent(webView1, HTML);
loadContent(webView2, HTML);
final List<WeakReference<MyListener>> listeners = new ArrayList<>();
submit(() -> {
domNodes1 = getDomNodes(webView1);
assertEquals(NUM_DOM_NODES, domNodes1.size());
MyListener listenerA = MyListener.create();
MyListener listenerB = MyListener.create();
listeners.add(new WeakReference<>(listenerA));
listeners.add(new WeakReference<>(listenerB));
listeners.add(new WeakReference<>(listenerA));
for (int i = 0; i < 3; i++) {
domNodes1.get(i).addEventListener("click", listeners.get(i).get(), false);
}
});
submit(() -> {
domNodes2 = getDomNodes(webView2);
assertEquals(NUM_DOM_NODES, domNodes1.size());
MyListener listenerA = MyListener.create();
MyListener listenerB = MyListener.create();
listeners.add(new WeakReference<>(listenerA));
listeners.add(new WeakReference<>(listenerB));
domNodes2.get(0).addEventListener("click", listeners.get(3).get(), false);
domNodes2.get(1).addEventListener("click", listeners.get(4).get(), false);
listeners.add(listeners.get(0));
domNodes2.get(2).addEventListener("click", listeners.get(0).get(), false);
});
Thread.sleep(100);
assertNumActive("MyListener", listenerRefs, 4);
submit(() -> {
click(webView1, 0);
click(webView1, 1);
click(webView1, 2);
click(webView2, 0);
click(webView2, 1);
click(webView2, 2);
});
Thread.sleep(100);
assertEquals("Click count", 3, listeners.get(0).get().getClickCount());
domNodes1.clear();
webView1 = null;
submit(() -> {
click(webView2, 0);
click(webView2, 1);
click(webView2, 2);
});
Thread.sleep(100);
assertEquals("Click count", 4, listeners.get(0).get().getClickCount());
assertEquals("Click count", 4, listeners.get(2).get().getClickCount());
assertNumActive("listeners", listenerRefs, 3);
submit(() -> {
domNodes2 = getDomNodes(webView2);
domNodes2.get(2).removeEventListener("click", listeners.get(4).get(), false);
});
submit(() -> {
click(webView2, 2);
});
Thread.sleep(100);
assertEquals("Click count", 2, listeners.get(4).get().getClickCount());
listeners.clear();
domNodes2.clear();
webView2 = null;
assertNumActive("MyListener", listenerRefs, 0);
}
@Test
public void oneWebViewMultipleListenerSameNode() throws Exception {
webView2 = null;
loadContent(webView1, HTML);
final List<MyListener> listeners = new ArrayList<>();
submit(() -> {
domNodes1 = getDomNodes(webView1);
assertEquals(NUM_DOM_NODES, domNodes1.size());
MyListener myListener1 = MyListener.create();
MyListener myListener2 = MyListener.create();
listeners.add(myListener1);
listeners.add(myListener2);
domNodes1.get(0).addEventListener("click", listeners.get(0), false);
domNodes1.get(0).addEventListener("click", listeners.get(1), false);
click(webView1, 0);
});
Thread.sleep(100);
assertEquals("Click count", 1, listeners.get(0).getClickCount());
assertEquals("Click count", 1, listeners.get(1).getClickCount());
submit(() -> {
domNodes1.get(0).removeEventListener("click", listeners.get(0), false);
click(webView1, 0);
});
Thread.sleep(100);
assertEquals("Click count", 1, listeners.get(0).getClickCount());
assertEquals("Click count", 2, listeners.get(1).getClickCount());
assertNumActive("MyListener", listenerRefs, 2);
domNodes1.clear();
webView1 = null;
listeners.clear();
assertNumActive("MyListener", listenerRefs, 0);
}
}
