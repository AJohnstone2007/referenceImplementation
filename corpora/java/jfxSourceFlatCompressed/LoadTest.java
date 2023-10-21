package test.javafx.scene.web;
import static javafx.concurrent.Worker.State.READY;
import static javafx.concurrent.Worker.State.RUNNING;
import static javafx.concurrent.Worker.State.FAILED;
import static javafx.concurrent.Worker.State.SUCCEEDED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
public class LoadTest extends TestBase {
private State getLoadState() {
return submit(() -> getEngine().getLoadWorker().getState());
}
@Test public void testLoadGoodUrl() {
final String FILE = "src/test/resources/test/html/ipsum.html";
load(new File(FILE));
WebEngine web = getEngine();
assertTrue("Load task completed successfully", getLoadState() == SUCCEEDED);
assertTrue("Location.endsWith(FILE)", web.getLocation().endsWith(FILE));
assertNotNull("Document should not be null", web.getDocument());
}
@Test public void testLoadBadUrl() {
final String URL = "bad://url";
load(URL);
WebEngine web = getEngine();
assertTrue("Load task failed", getLoadState() == FAILED);
assertEquals("Location", URL, web.getLocation());
assertNull("Document should be null", web.getDocument());
}
@Test public void testLoadHtmlContent() {
final String TITLE = "In a Silent Way";
loadContent("<html><head><title>" + TITLE + "</title></head></html>");
WebEngine web = getEngine();
assertTrue("Load task completed successfully", getLoadState() == SUCCEEDED);
assertEquals("Location", "", web.getLocation());
assertNotNull("Document should not be null", web.getDocument());
}
@Test public void testLoadPlainContent() {
final String TEXT =
"<html><head><title>Hidden Really Well</title></head></html>";
loadContent(TEXT, "text/plain");
final WebEngine web = getEngine();
assertTrue("Load task completed successfully", getLoadState() == SUCCEEDED);
assertEquals("Location", "", web.getLocation());
submit(() -> {
Document doc = web.getDocument();
assertNotNull("Document should not be null", doc);
Node el =
doc.getDocumentElement().getLastChild().getFirstChild().getFirstChild();
String text = ((Text)el).getNodeValue();
assertEquals("Plain text should not be interpreted as HTML",
TEXT, text);
});
}
@Test public void testLoadEmpty() {
testLoadEmpty(null);
testLoadEmpty("");
testLoadEmpty("about:blank");
}
private void testLoadEmpty(String url) {
load(url);
final WebEngine web = getEngine();
assertTrue("Load task completed successfully", getLoadState() == SUCCEEDED);
assertEquals("Location", "about:blank", web.getLocation());
assertNull("Title should be null", web.getTitle());
submit(() -> {
Document doc = web.getDocument();
assertNotNull("Document should not be null", doc);
Element html = doc.getDocumentElement();
assertNotNull("There should be an HTML element", html);
assertEquals("HTML element should have tag HTML", "HTML", html.getTagName());
NodeList htmlNodes = html.getChildNodes();
assertNotNull("HTML element should have two children", htmlNodes);
assertEquals("HTML element should have two children", 2, htmlNodes.getLength());
Element head = (Element) htmlNodes.item(0);
NodeList headNodes = head.getChildNodes();
assertNotNull("There should be a HEAD element", head);
assertEquals("HEAD element should have tag HEAD", "HEAD", head.getTagName());
assertTrue("HEAD element should have no children",
headNodes == null || headNodes.getLength() == 0);
Element body = (Element) htmlNodes.item(1);
NodeList bodyNodes = body.getChildNodes();
assertNotNull("There should be a BODY element", body);
assertEquals("BODY element should have tag BODY", "BODY", body.getTagName());
assertTrue("BODY element should have no children",
bodyNodes == null || bodyNodes.getLength() == 0);
});
}
@Test public void testLoadUrlWithEncodedSpaces() {
final String URL = "http://localhost/test.php?param=a%20b%20c";
load(URL);
WebEngine web = getEngine();
assertEquals("Unexpected location", URL, web.getLocation());
}
@Test public void testLoadUrlWithUnencodedSpaces() {
final String URL = "http://localhost/test.php?param=a b c";
load(URL);
WebEngine web = getEngine();
assertEquals("Unexpected location",
URL.replace(" ", "%20"), web.getLocation());
}
@Test public void testLoadContentWithLocalScript() {
WebEngine webEngine = getEngine();
final StringBuilder result = new StringBuilder();
webEngine.setOnAlert(event -> {
result.append("ALERT: ").append(event.getData());
});
String scriptUrl =
new File("src/test/resources/test/html/invoke-alert.js").toURI().toASCIIString();
String html =
"<html>\n" +
"<head><script src=\"" + scriptUrl + "\"></script></head>\n" +
"<body><script>invokeAlert('foo');</script></body>\n" +
"</html>";
loadContent(html);
assertEquals("Unexpected result", "ALERT: foo", result.toString());
assertEquals("Unexpected load state", SUCCEEDED, getLoadState());
assertEquals("Unexpected location", "", webEngine.getLocation());
assertNotNull("Document is null", webEngine.getDocument());
}
@Test public void testLoadLocalCSS() {
load(new File("src/test/resources/test/html/dom.html"));
submit(() -> {
assertEquals("Font weight should be bold", "bold", (String) getEngine().executeScript(
"window.getComputedStyle(document.getElementById('p3')).getPropertyValue('font-weight')"));
assertEquals("font style should be italic", "italic", (String) getEngine().executeScript(
"window.getComputedStyle(document.getElementById('p3')).getPropertyValue('font-style')"));
});
}
@Test public void testLoadTitleChanged() {
final String FILE = "src/test/resources/test/html/ipsum.html";
final CountDownLatch latch = new CountDownLatch(1);
submit(() -> {
WebEngine webEngine = new WebEngine();
webEngine.titleProperty().addListener((observable, oldValue, newValue) -> {
assertTrue("loadContent in SUCCEEDED State", webEngine.getLoadWorker().getState() == SUCCEEDED);
assertEquals("Title", "Lorem Ipsum", webEngine.getTitle());
latch.countDown();
});
webEngine.load(new File(FILE).toURI().toASCIIString());
});
try {
latch.await();
} catch (InterruptedException ex) {
throw new AssertionError(ex);
}
}
@Test public void testLoadContentTitleChanged() {
final String TITLE = "Title Test";
final CountDownLatch latch = new CountDownLatch(1);
submit(() -> {
WebEngine webEngine = new WebEngine();
webEngine.titleProperty().addListener((observable, oldValue, newValue) -> {
assertTrue("loadContent in SUCCEEDED State", webEngine.getLoadWorker().getState() == SUCCEEDED);
assertEquals("Title", TITLE, webEngine.getTitle());
latch.countDown();
});
webEngine.loadContent("<html><head><title>" + TITLE + "</title></head></html>");
});
try {
latch.await();
} catch (InterruptedException ex) {
throw new AssertionError(ex);
}
}
@Test public void loadContentOnLocationChange() throws Exception {
final CountDownLatch latch = new CountDownLatch(2);
submit(() -> {
WebEngine webEngine = new WebEngine();
webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
if (newValue.equalsIgnoreCase("about:blank")) {
webEngine.loadContent("");
assertTrue("loadContent in READY State", webEngine.getLoadWorker().getState() == READY);
}
});
webEngine.getLoadWorker().stateProperty().addListener(((observable, oldValue, newValue) -> {
if (newValue == SUCCEEDED) {
latch.countDown();
}
}));
webEngine.load("");
assertTrue("load task completed successfully", webEngine.getLoadWorker().getState() == SUCCEEDED);
});
try {
latch.await();
} catch (InterruptedException ex) {
throw new AssertionError(ex);
}
}
@Test public void loadUrlOnLocationChange() throws Exception {
final CountDownLatch latch = new CountDownLatch(1);
submit(() -> {
WebEngine webEngine = new WebEngine();
webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
if (newValue.equalsIgnoreCase("")) {
webEngine.load("");
assertTrue("Load in READY State", webEngine.getLoadWorker().getState() == READY);
}
});
webEngine.getLoadWorker().stateProperty().addListener(((observable, oldValue, newValue) -> {
if (newValue == SUCCEEDED) {
latch.countDown();
}
}));
webEngine.loadContent("");
assertTrue("loadContent task running", webEngine.getLoadWorker().getState() == RUNNING);
});
try {
latch.await();
} catch (InterruptedException ex) {
throw new AssertionError(ex);
}
}
@Test public void loadJarFile() throws Exception {
load("jar:" + new File(System.getProperties().get("WEB_ARCHIVE_JAR_TEST_DIR").toString()
+ "/webArchiveJar.jar").toURI().toASCIIString() + "!/archive-root0.html");
assertEquals("archive-root0.html failed to load src='archive-r0.js'",
"loaded", executeScript("jsr0()").toString());
assertEquals("archive-root0.html failed to load src='c/archive-c0.js'",
"loaded", executeScript("jsc0()").toString());
load("jar:" + new File(System.getProperties().get("WEB_ARCHIVE_JAR_TEST_DIR").toString()
+ "/webArchiveJar.jar").toURI().toASCIIString() + "!/archive-root1.html");
assertEquals("archive-root1.html failed to load src='./archive-r0.js'",
"loaded", executeScript("jsr0()").toString());
assertEquals("archive-root1.html failed to load src='./c/archive-c0.js'",
"loaded", executeScript("jsc0()").toString());
load("jar:" + new File(System.getProperties().get("WEB_ARCHIVE_JAR_TEST_DIR").toString()
+ "/webArchiveJar.jar").toURI().toASCIIString() + "!/archive-root2.html");
assertEquals("archive-root2.html failed to load src='./c/../archive-r0.js'",
"loaded", executeScript("jsr0()").toString());
assertEquals("archive-root2.html failed to load src='./c/./././archive-c0.js'",
"loaded", executeScript("jsc0()").toString());
}
public final class ImageEvent {
public void onLoad() {
++loaded;
}
public void onError() {
++failed;
}
int loaded;
int failed;
}
@Test(timeout = 30000) public void loadJrtResource() throws Exception {
assumeTrue(isJigsawMode());
final String[] jrtResources = {
"jrt:/javafx.web/javafx/scene/web/AlignLeft_16x16_JFX.png",
"jrt:/javafx.web/./javafx/scene/web/Strikethrough_16x16_JFX.png",
"jrt:/javafx.web/./javafx/scene/../../javafx/scene/web/FontColor_16x16_JFX.png",
"jrt:/javafx.web/./javafx/./scene/./web/./DrawHorizontalLine_16x16_JFX.png",
"jrt:/javafx.web/javafx/scene/web/../../../javafx/scene/web/OrderedListNumbers_16x16_JFX-rtl.png"
};
load(jrtResources[0]);
assertEquals("Failed to load " + jrtResources[0],
1, executeScript("document.getElementsByTagName('img').length"));
assertEquals("Failed to Render " + jrtResources[0],
16, executeScript("document.getElementsByTagName('img')[0].naturalWidth"));
final ImageEvent imageEvent = new ImageEvent();
final CountDownLatch latch = new CountDownLatch(1);
submit(() -> {
WebEngine webEngine = new WebEngine();
webEngine.getLoadWorker().stateProperty().addListener(((observable, oldValue, newValue) -> {
if (newValue == SUCCEEDED) {
final String msg = String.format(
"Failed to load : %d / %d resources",
imageEvent.failed, jrtResources.length);
assertTrue(msg, imageEvent.loaded == jrtResources.length);
latch.countDown();
}
}));
StringBuffer jrtContent = new StringBuffer();
for (int i = 0; i < jrtResources.length; ++i) {
jrtContent.append("<img src=" + jrtResources[i] +
" onload='imageStatus.onLoad()'" +
" onerror='imageStatus.onError()'/>");
}
final JSObject window = (JSObject) webEngine.executeScript("window");
window.setMember("imageStatus", imageEvent );
webEngine.loadContent(jrtContent.toString());
});
try {
latch.await();
} catch (InterruptedException ex) {
throw new AssertionError(ex);
}
}
@Test public void jsRegexpTrapTest() {
final String FILE = "src/test/resources/test/html/unicode.html";
load(new File(FILE));
WebEngine web = getEngine();
assertTrue("Load task completed successfully", getLoadState() == SUCCEEDED);
}
}
