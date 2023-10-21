package test.javafx.scene.web;
import static java.util.Arrays.asList;
import com.sun.webkit.WebPage;
import com.sun.webkit.WebPageShim;
import javafx.scene.web.WebEngineShim;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
public class SVGPointerEventsTest extends TestBase {
@Test
public void testClickOnStrokePointerEventsStroke() throws Exception {
load(SVGPointerEventsTest.class.getClassLoader().getResource("test/html/pointerevents-stroke.html").toExternalForm());
submit(() -> {
final WebPage page = WebEngineShim.getPage(getEngine());
WebPageShim.paint(page, 0, 0, 800, 600);
WebPageShim.click(page, 130, 80);
WebPageShim.click(page, 330, 80);
WebPageShim.click(page, 530, 80);
WebPageShim.click(page, 130, 280);
WebPageShim.click(page, 330, 280);
WebPageShim.click(page, 530, 280);
WebPageShim.click(page, 70, 410);
for (String s : asList("polyline", "path", "rect", "circle", "ellipse", "polygon", "dashed")) {
assertTrue("Expected element '" + s + "' to be activated", (boolean) getEngine().executeScript("isActivated('" + s + "')"));
}
});
}
@Test
public void testClickOnFillPointerEventsStroke() throws Exception {
load(SVGPointerEventsTest.class.getClassLoader().getResource("test/html/pointerevents-stroke.html").toExternalForm());
submit(() -> {
final WebPage page = WebEngineShim.getPage(getEngine());
WebPageShim.paint(page, 0, 0, 800, 600);
WebPageShim.click(page, 80, 80);
WebPageShim.click(page, 280, 80);
WebPageShim.click(page, 480, 80);
WebPageShim.click(page, 80, 280);
WebPageShim.click(page, 280, 280);
WebPageShim.click(page, 480, 280);
WebPageShim.click(page, 30, 410);
for (String s : asList("polyline", "path", "rect", "circle", "ellipse", "polygon", "dashed")) {
assertFalse("Expected element '" + s + "' not to be activated", (boolean) getEngine().executeScript("isActivated('" + s + "')"));
}
});
}
}
