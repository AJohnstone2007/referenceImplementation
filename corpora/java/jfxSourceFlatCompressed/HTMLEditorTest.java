package test.javafx.scene.web;
import com.sun.javafx.PlatformUtil;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import test.com.sun.javafx.scene.control.infrastructure.KeyEventFirer;
import test.com.sun.javafx.scene.control.infrastructure.KeyModifier;
import test.util.Util;
import static javafx.concurrent.Worker.State.SUCCEEDED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
public class HTMLEditorTest {
private static final CountDownLatch launchLatch = new CountDownLatch(1);
static HTMLEditorTestApp htmlEditorTestApp;
private HTMLEditor htmlEditor;
private WebView webView;
private Scene scene;
public static class HTMLEditorTestApp extends Application {
Stage primaryStage = null;
public HTMLEditorTestApp() {
super();
}
@Override
public void init() {
HTMLEditorTest.htmlEditorTestApp = this;
}
@Override
public void start(Stage primaryStage) throws Exception {
Platform.setImplicitExit(false);
this.primaryStage = primaryStage;
launchLatch.countDown();
}
}
@BeforeClass
public static void setupOnce() {
new Thread(() -> Application.launch(HTMLEditorTestApp.class,
(String[]) null)).start();
Font.loadFont(
HTMLEditorTest.class.getResource("WebKit_Layout_Tests_2.ttf").toExternalForm(),
10
);
assertTrue("Timeout waiting for FX runtime to start", Util.await(launchLatch));
}
@AfterClass
public static void tearDownOnce() {
Platform.exit();
}
@Before
public void setupTestObjects() {
Platform.runLater(() -> {
htmlEditor = new HTMLEditor();
scene = new Scene(htmlEditor);
htmlEditorTestApp.primaryStage.setScene(scene);
htmlEditorTestApp.primaryStage.show();
webView = (WebView) htmlEditor.lookup(".web-view");
assertNotNull(webView);
webView.getEngine().getLoadWorker().cancel();
});
}
@Test @Ignore("JDK-8202542")
public void checkFocusChange() throws Exception {
final CountDownLatch editorStateLatch = new CountDownLatch(1);
final AtomicReference<String> result = new AtomicReference<>();
Platform.runLater(() -> {
webView.getEngine().getLoadWorker().stateProperty().
addListener((observable, oldValue, newValue) -> {
if (newValue == SUCCEEDED) {
webView.getEngine().executeScript(
"document.body.style.backgroundColor='red';" +
"document.body.onfocusout = function() {" +
"document.body.style.backgroundColor = 'yellow';" +
"}");
htmlEditor.requestFocus();
}
});
htmlEditor.setHtmlText(htmlEditor.getHtmlText());
KeyEvent tabKeyEvent = new KeyEvent(null, webView,
KeyEvent.KEY_PRESSED, "", "",
KeyCode.TAB, false, false, false, false);
webView.focusedProperty().
addListener((observable, oldValue, newValue) -> {
if (newValue) {
webView.getEngine().
executeScript("document.body.focus();");
for (int i = 0; i < 10; ++i) {
Event.fireEvent(webView, tabKeyEvent);
}
result.set(webView.getEngine().
executeScript("document.body.style.backgroundColor").
toString());
htmlEditorTestApp.primaryStage.hide();
editorStateLatch.countDown();
}
});
});
assertTrue("Timeout when waiting for focus change ", Util.await(editorStateLatch));
assertEquals("Focus Change with design mode enabled ", "red", result.get());
}
@Test
public void checkStyleWithCSS() throws Exception {
final CountDownLatch editorStateLatch = new CountDownLatch(1);
final String editorCommand1 =
"document.execCommand('bold', false, 'true');" +
"document.execCommand('italic', false, 'true');" +
"document.execCommand('insertText', false, 'Hello World');";
final String editorCommand2 =
"document.execCommand('selectAll', false, 'true');" +
"document.execCommand('delete', false, 'true');" +
"document.execCommand('bold', false, 'false');" +
"document.execCommand('italic', false, 'false');" +
"document.execCommand('underline', false, 'true');" +
"document.execCommand('forecolor', false," +
" 'rgba(255, 155, 0, 0.4)');" +
"document.execCommand('backcolor', false," +
" 'rgba(150, 90, 5, 0.5)');" +
"document.execCommand('insertText', false, 'Hello HTMLEditor');";
final String expectedHTML = "<html dir=\"ltr\"><head></head><body " +
"contenteditable=\"true\"><span style=\"font-weight: bold; " +
"font-style: italic;\">Hello World</span></body></html>";
Util.runAndWait(() -> {
webView.getEngine().getLoadWorker().stateProperty().
addListener((observable, oldValue, newValue) -> {
if (newValue == SUCCEEDED) {
htmlEditor.requestFocus();
}
});
htmlEditor.setHtmlText(htmlEditor.getHtmlText());
assertNotNull(webView);
webView.focusedProperty().
addListener((observable, oldValue, newValue) -> {
if (newValue) {
editorStateLatch.countDown();
}
});
});
assertTrue("Timeout when waiting for focus change ", Util.await(editorStateLatch));
Util.runAndWait(() -> {
webView.getEngine().executeScript("document.body.focus();");
webView.getEngine().executeScript(editorCommand1);
assertEquals(expectedHTML, htmlEditor.getHtmlText());
webView.getEngine().executeScript(editorCommand2);
assertEquals(webView.getEngine().executeScript(
"document.getElementsByTagName('span')[0].style.textDecoration")
.toString(),
"underline");
assertEquals(webView.getEngine().executeScript(
"document.getElementsByTagName('span')[0].style.fontWeight")
.toString(), "");
assertEquals(webView.getEngine().executeScript(
"document.getElementsByTagName('span')[0].style.fontStyle")
.toString(), "");
testColorEquality("rgba(255, 155, 0, 0.4)",
webView.getEngine().executeScript(
"document.getElementsByTagName('span')[0].style.color")
.toString(), 0.01);
testColorEquality("rgba(150, 90, 5, 0.5)",
webView.getEngine().executeScript(
"document.getElementsByTagName('span')[0].style.backgroundColor")
.toString(), 0.01);
htmlEditorTestApp.primaryStage.hide();
});
}
private void testColorEquality(String expectedColor, String actualColor,
double delta) {
assertTrue(actualColor.startsWith("rgba"));
final String[] actualValues =
actualColor.substring(actualColor.indexOf('(') + 1,
actualColor.lastIndexOf(')')).split(",");
final String[] expectedValues =
expectedColor.substring(expectedColor.indexOf('(') + 1,
expectedColor.lastIndexOf(')')).split(",");
for (int i = 0; i < 3; i++) {
assertEquals(Integer.parseInt(actualValues[i].trim()),
Integer.parseInt(expectedValues[i].trim()));
}
assertEquals(Double.parseDouble(actualValues[3].trim()),
Double.parseDouble(expectedValues[3].trim()), delta);
}
@Test
public void checkStyleProperty() throws Exception {
final CountDownLatch editorStateLatch = new CountDownLatch(1);
final AtomicReference<String> result = new AtomicReference<>();
Util.runAndWait(() -> {
webView.getEngine().getLoadWorker().stateProperty().
addListener((observable, oldValue, newValue) -> {
if (newValue == SUCCEEDED) {
htmlEditor.requestFocus();
}
});
htmlEditor.setHtmlText("<body style='font-weight: bold'> <p> Test </p> </body>");
webView.focusedProperty().
addListener((observable, oldValue, newValue) -> {
if (newValue) {
webView.getEngine().
executeScript("document.body.focus();");
webView.getEngine().
executeScript("document.execCommand('selectAll', false, 'true');");
webView.getEngine().
executeScript("document.execCommand('removeFormat', false, null);");
result.set(webView.getEngine().
executeScript("document.body.style.fontWeight").
toString());
editorStateLatch.countDown();
}
});
});
assertTrue("Timeout when waiting for focus change ", Util.await(editorStateLatch));
assertNotNull("result must have a valid reference ", result.get());
assertEquals("document.body.style.fontWeight must be bold ", "bold", result.get());
}
@Test
public void selectFontFamilyWithSpace() {
final CountDownLatch editorStateLatch = new CountDownLatch(1);
final AtomicReference<String> result = new AtomicReference<>();
Util.runAndWait(() -> {
webView.getEngine().getLoadWorker().stateProperty().
addListener((observable, oldValue, newValue) -> {
if (newValue == SUCCEEDED) {
htmlEditor.requestFocus();
}
});
htmlEditor.setHtmlText("<body>Sample Text</body>");
webView.focusedProperty().
addListener((observable, oldValue, newValue) -> {
if (newValue) {
ComboBox<String> fontFamilyComboBox = null;
int i = 0;
for (Node comboBox : htmlEditor.lookupAll(".font-menu-button")) {
if (i == 1) {
assertTrue("fontFamilyComboBox must be ComboBox",
comboBox instanceof ComboBox);
fontFamilyComboBox = (ComboBox<String>) comboBox;
assertNotNull("fontFamilyComboBox must not be null",
fontFamilyComboBox);
}
i++;
}
webView.getEngine().
executeScript("document.execCommand('selectAll', false, 'true');");
fontFamilyComboBox.getSelectionModel().select("WebKit Layout Tests 2");
result.set(htmlEditor.getHtmlText());
editorStateLatch.countDown();
}
});
});
assertTrue("Timeout when waiting for focus change ", Util.await(editorStateLatch));
assertNotNull("result must have a valid reference ", result.get());
assertTrue("font-family must be 'WebKit Layout Test 2' ", result.get().
contains("font-family: &quot;WebKit Layout Tests 2&quot;"));
}
@Test
public void checkFontSizeOnSelectAll_ctrl_A() throws Exception {
final CountDownLatch editorStateLatch = new CountDownLatch(1);
final String editorCommand1 =
"document.execCommand('fontSize', false, '7');" +
"document.execCommand('insertText', false, 'First_word ');";
final String editorCommand2 =
"document.execCommand('fontSize', false, '1');" +
"document.execCommand('insertText', false, 'Second_word');";
Util.runAndWait(() -> {
webView.getEngine().getLoadWorker().stateProperty().
addListener((observable, oldValue, newValue) -> {
if (newValue == SUCCEEDED) {
htmlEditor.requestFocus();
}
});
htmlEditor.setHtmlText(htmlEditor.getHtmlText());
webView.focusedProperty().addListener((observable, oldValue, newValue) -> {
if (newValue) {
webView.getEngine().executeScript("document.body.focus();");
webView.getEngine().executeScript(editorCommand1);
webView.getEngine().executeScript(editorCommand2);
editorStateLatch.countDown();
}
});
});
assertTrue("Timeout while waiting for test html text setup", Util.await(editorStateLatch));
String expectedHtmlText = htmlEditor.getHtmlText();
Util.runAndWait(() -> {
KeyEventFirer keyboard = new KeyEventFirer(htmlEditor, scene);
keyboard.doKeyPress(KeyCode.A,
PlatformUtil.isMac()? KeyModifier.META : KeyModifier.CTRL);
});
String actualHtmlText = htmlEditor.getHtmlText();
assertEquals("Expected and Actual HTML text does not match. ", expectedHtmlText, actualHtmlText);
}
@Test
public void checkFontSizeOnSelectAll_Shift_LeftArrowKey() throws Exception {
final CountDownLatch editorStateLatch = new CountDownLatch(1);
final String editorCommand1 =
"document.execCommand('fontSize', false, '7');" +
"document.execCommand('insertText', false, 'Hello');";
final String editorCommand2 =
"document.execCommand('fontSize', false, '1');" +
"document.execCommand('insertText', false, 'World');";
Util.runAndWait(() -> {
webView.getEngine().getLoadWorker().stateProperty().
addListener((observable, oldValue, newValue) -> {
if (newValue == SUCCEEDED) {
htmlEditor.requestFocus();
}
});
htmlEditor.setHtmlText(htmlEditor.getHtmlText());
webView.focusedProperty().addListener((observable, oldValue, newValue) -> {
if (newValue) {
webView.getEngine().executeScript("document.body.focus();");
webView.getEngine().executeScript(editorCommand1);
webView.getEngine().executeScript(editorCommand2);
editorStateLatch.countDown();
}
});
});
assertTrue("Timeout while waiting for test html text setup", Util.await(editorStateLatch));
String expectedHtmlText = htmlEditor.getHtmlText();
Util.runAndWait(() -> {
KeyEventFirer keyboard = new KeyEventFirer(htmlEditor, scene);
for (int i = 0; i < 10; i++) {
keyboard.doLeftArrowPress(KeyModifier.SHIFT);
}
});
String actualHtmlText = htmlEditor.getHtmlText();
assertEquals("Expected and Actual HTML text does not match. ", expectedHtmlText, actualHtmlText);
}
}
