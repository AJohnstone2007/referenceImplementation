package test.javafx.scene.web;
import org.junit.Test;
import org.junit.Before;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.collections.ObservableList;
import javafx.scene.web.WebHistory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
public class HistoryStateTest extends TestBase {
private static final CountDownLatch historyStateLatch = new CountDownLatch(3);
final AtomicInteger historyListenerIndex = new AtomicInteger(-1);
private static final String resourcePath= "test/html/";
private static final String initialLoadUrl = "archive-root0.html";
private static final String firstLoadUrl = "archive-root1.html";
private static final String secondLoadUrl = "archive-root2.html";
private static final String replaceLoadUrl = "archive-root3.html";
private static final String historyPushScript1 = "history.pushState({push1key : 1}, '', '?" +
firstLoadUrl + "');";
private static final String historyPushScript2 = "history.pushState({push2key : 2}, '', '?" +
secondLoadUrl + "');";
private static final String historyReplaceScript = "history.replaceState({replaceObject : 3}, '', '?" +
replaceLoadUrl + "');";
private static final String historyStateScript = "history.state";
private static final String historyLengthScript = "history.length";
private static final String historyGoBackScript = "history.go(-1)";
private static final String historyGoForwardScript = "history.go(1)";
private static final String historyBackcript = "history.back()";
private static final int TIMEOUT = 30;
@Before
public void before() {
load(HistoryStateTest.class.getClassLoader().getResource(
resourcePath + initialLoadUrl).toExternalForm());
}
@Test
public void pushAndReplaceTest() throws Exception {
assertNull(historyStateScript + " : Failed",
executeScript(historyStateScript));
assertEquals(historyLengthScript + " : Failed",
1, executeScript(historyLengthScript));
executeScript(historyPushScript1);
assertNotNull(historyStateScript + " : Failed",
executeScript(historyStateScript));
assertEquals("history.state.push1key Failed",
1, executeScript("history.state.push1key"));
assertEquals(historyLengthScript + " : Failed",
2, executeScript(historyLengthScript));
assertTrue(historyPushScript1 + " : Failed",
getEngine().getLocation().endsWith(firstLoadUrl));
executeScript(historyPushScript2);
assertEquals("history.state.push1key Failed",
2, executeScript("history.state.push2key"));
assertEquals(historyLengthScript + " : Failed",
3, executeScript(historyLengthScript));
assertTrue(historyPushScript2 + " : Failed",
getEngine().getLocation().endsWith(secondLoadUrl));
executeScript(historyReplaceScript);
assertEquals(historyLengthScript + " : Failed",
3, executeScript(historyLengthScript));
assertEquals("history.state.replaceObject Failed",
3, executeScript("history.state.replaceObject"));
assertTrue(historyPushScript2 + " : Failed",
getEngine().getLocation().endsWith(replaceLoadUrl));
submit(() -> {
getEngine().locationProperty().addListener((observable, previousUrl, newUrl) -> {
switch(historyListenerIndex.incrementAndGet()) {
case 0:
assertTrue(newUrl.endsWith(firstLoadUrl));
getEngine().executeScript(historyGoForwardScript);
break;
case 1:
assertTrue(newUrl.endsWith(replaceLoadUrl));
getEngine().executeScript(historyBackcript);
break;
case 2:
assertTrue(newUrl.endsWith(firstLoadUrl));
break;
default:
fail();
}
historyStateLatch.countDown();
});
getEngine().executeScript(historyGoBackScript);
});
try {
historyStateLatch.await(TIMEOUT, TimeUnit.SECONDS);
} catch (InterruptedException ex) {
throw new AssertionError(ex);
} finally {
assertEquals("history navigation using javascript failed", 2, historyListenerIndex.get());
}
}
@Test
public void testDocumentExistenceAfterPushState() {
final ObservableList<WebHistory.Entry> history = getEngine().getHistory().getEntries();
final int initialHistorySize = history.size();
load(HistoryStateTest.class.getClassLoader().getResource(
resourcePath + initialLoadUrl).toExternalForm());
assertNotNull(getEngine().getDocument());
executeScript("history.pushState('push', 'title', 'pushState.html')");
assertNotNull("Document shouldn't be null after history.pushState", getEngine().getDocument());
assertTrue("location must end with pushState.html", getEngine().getLocation().endsWith("pushState.html"));
assertEquals("history count should be incremented", initialHistorySize + 1, history.size());
}
@Test
public void testDocumentExistenceAfterReplaceState() {
final ObservableList<WebHistory.Entry> history = getEngine().getHistory().getEntries();
final int initialHistorySize = history.size();
load(HistoryStateTest.class.getClassLoader().getResource(
resourcePath + initialLoadUrl).toExternalForm());
assertNotNull(getEngine().getDocument());
executeScript("history.replaceState('push', 'title', 'replaceState.html')");
assertNotNull("Document shouldn't be null after history.replaceState", getEngine().getDocument());
assertTrue("location must end with replaceState.html", getEngine().getLocation().endsWith("replaceState.html"));
assertEquals("history count shouldn't be incremented", initialHistorySize, history.size());
}
}
