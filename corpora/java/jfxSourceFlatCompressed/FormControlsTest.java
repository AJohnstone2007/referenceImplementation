package test.javafx.scene.web;
import com.sun.webkit.WebPage;
import com.sun.webkit.WebPageShim;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.web.WebEngineShim;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
@RunWith(Parameterized.class)
public final class FormControlsTest extends TestBase {
private static final PrintStream ERR = System.err;
private final String element;
private final String selector;
@Parameters
public static Collection<String[]> data() {
return Arrays.asList(new String[][] {
{"<input type='checkbox'/>", "check-box"},
{"<input type='radio'/>", "radio-button"},
{"<input type='button'/>", "button"},
{"<input type='text'/>", "text-field"},
{"<meter value='06'>60%</meter>", "progress-bar"},
{"<input type='range'/>", "slider"},
});
}
public FormControlsTest(final String element, final String selector) {
this.element = element;
this.selector = selector;
}
private void printWithFormControl(final Runnable testBody) {
final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
System.setErr(new PrintStream(errStream));
loadContent(String.format("<body>%s</body>", element));
submit(testBody);
System.setErr(ERR);
final String exMessage = errStream.toString();
assertFalse(String.format("%s:Test failed with exception:\n%s", selector, exMessage),
exMessage.contains("Exception") || exMessage.contains("Error"));
}
@Test
public void testRendering() {
final Runnable testBody = () -> {
final WebPage page = WebEngineShim.getPage(getEngine());
assertNotNull(page);
WebPageShim.mockPrint(page, 0, 0, 800, 600);
final Set<Node> elements = getView().lookupAll("." + selector);
assertEquals(
String.format("%s control doesn't exists as child of WebView", selector),
1,
elements.size());
final Node node = (Node) elements.toArray()[0];
assertTrue(
String.format("%s styleClass=%s is incorrect", node.getTypeSelector(), selector),
node.getStyleClass().contains(selector));
};
printWithFormControl(testBody);
}
@Test
public void testPrint() {
final Runnable testBody = () -> {
final WebPage page = WebEngineShim.getPage(getEngine());
assertNotNull(page);
WebPageShim.mockPrint(page, 0, 0, 800, 600);
};
printWithFormControl(testBody);
}
@Test
public void testPrintByPageNumber() {
final Runnable testBody = () -> {
final WebPage page = WebEngineShim.getPage(getEngine());
assertNotNull(page);
WebPageShim.mockPrintByPage(page, 0, 0, 0, 800, 600);
};
printWithFormControl(testBody);
}
@After
public void teardown() {
System.setErr(ERR);
}
}
