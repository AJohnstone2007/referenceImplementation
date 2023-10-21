package test.javafx.scene.web;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.io.File;
import java.util.concurrent.FutureTask;
import javafx.event.Event;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngineShim;
import javafx.scene.web.WebView;
import org.junit.Test;
public class WebViewTest extends TestBase {
final static float SCALE = 1.78f;
final static float ZOOM = 2.71f;
final static float DELTA = 1e-3f;
@Test public void testTextScale() throws Exception {
WebView view = getView();
setFontScale(view, SCALE);
checkFontScale(view, SCALE);
setZoom(view, ZOOM);
checkZoom(view, ZOOM);
load(new File("src/test/resources/test/html/ipsum.html"));
checkFontScale(view, SCALE);
checkZoom(view, ZOOM);
}
@Test public void testForwardMouseButton() {
WebView view = getView();
Event forward = new MouseEvent(MouseEvent.MOUSE_PRESSED, 0, 0, 0, 0, MouseButton.FORWARD, 1, false, false, false, false, false, false, false, false, true, true, false, true, null);
view.fireEvent(forward);
}
@Test public void testBackMouseButton() {
WebView view = getView();
Event back = new MouseEvent(MouseEvent.MOUSE_PRESSED, 0, 0, 0, 0, MouseButton.BACK, 1, false, false, false, false, false, false, false, true, false, true, false, true, null);
view.fireEvent(back);
}
void checkFontScale(WebView view, float scale) {
assertEquals("WebView.fontScale", scale, view.getFontScale(), DELTA);
assertEquals("WebPage.zoomFactor",
scale, WebEngineShim.getPage(view.getEngine()).getZoomFactor(true), DELTA);
}
private void setFontScale(final WebView view, final float scale) throws Exception {
submit(() -> {
view.setFontScale(scale);
});
}
void checkZoom(WebView view, float zoom) {
assertEquals("WebView.zoom", zoom, view.getZoom(), DELTA);
}
private void setZoom(final WebView view, final float zoom) throws Exception {
submit(() -> {
view.setZoom(zoom);
});
}
@Test public void testFontWeights() {
loadContent(
"<!DOCTYPE html><html><head></head>" +
"<body>" +
"   <div style=\"font: 19px system-ui\">" +
"       <div style=\"font-style: italic;\">" +
"           <span id=\"six\" style=\"font-weight: 600;\">Hello, World</span>" +
"           <span id=\"nine\" style=\"font-weight: 900;\">Hello, World</span>" +
"       </div>" +
"   </div>" +
"</body> </html>"
);
submit(() -> {
assertFalse("Font weight test failed ",
(Boolean) getEngine().executeScript(
"document.getElementById('six').offsetWidth == document.getElementById('nine').offsetWidth"));
});
}
}
