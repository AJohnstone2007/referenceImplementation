package test.javafx.scene.web;
import com.sun.javafx.PlatformUtil;
import javafx.event.Event;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.Test;
import static javafx.concurrent.Worker.State.SUCCEEDED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
public class HTMLEditingTest extends TestBase {
@Test public void clipboardGetDataOnPaste() {
String defaultText = "Default";
loadContent(
"<input id='srcInput' value=" + defaultText + " autofocus>" +
"<input id='pasteTarget'></input>" +
"<script>"+
"srcInput.onpaste = function(e) {" +
"pasteTarget.value = e.clipboardData.getData('text/plain');}" +
"</script>");
submit(() -> {
assertTrue("LoadContent completed successfully",
getEngine().getLoadWorker().getState() == SUCCEEDED);
String clipboardData = "Clipboard text";
ClipboardContent content = new ClipboardContent();
content.putString(clipboardData);
Clipboard.getSystemClipboard().setContent(content);
Event.fireEvent(getView(),
new KeyEvent(null,getView(),
KeyEvent.KEY_PRESSED,
"", "", KeyCode.V,
false, !PlatformUtil.isMac(),
false, PlatformUtil.isMac()));
assertEquals("Source Default value",getEngine().
executeScript("srcInput.defaultValue").toString(),
defaultText);
assertEquals("Source clipboard onpaste data", getEngine().
executeScript("srcInput.value").toString(), clipboardData + defaultText);
assertEquals("Target onpaste data", getEngine().
executeScript("pasteTarget.value").toString(),
clipboardData);
});
}
}
