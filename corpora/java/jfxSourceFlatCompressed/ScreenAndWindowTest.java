package test.javafx.scene.web;
import com.sun.javafx.util.Utils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.junit.Test;
import static org.junit.Assert.*;
import netscape.javascript.*;
public class ScreenAndWindowTest extends TestBase {
private void checkScreenProperties(Rectangle2D screenSize, Rectangle2D availSize) {
JSObject screen = (JSObject) getEngine().executeScript("screen");
int depth = (Integer) screen.getMember("colorDepth");
int width = (Integer) screen.getMember("width");
int height = (Integer) screen.getMember("height");
int availWidth = (Integer) screen.getMember("availWidth");
int availHeight = (Integer) screen.getMember("availHeight");
assertEquals("screen.width", (int)screenSize.getWidth(), width);
assertEquals("screen.height", (int)screenSize.getHeight(), height);
assertEquals("screen.availWidth", (int)availSize.getWidth(), availWidth);
assertEquals("screen.availHeight", (int)availSize.getHeight(), availHeight);
assertTrue("screen.depth >= 0", depth >= 0);
assertTrue("screen.width >= screen.availWidth", width >= availWidth);
assertTrue("screen.height >= screen.availHeight", height >= availHeight);
}
private void checkWindowProperties(int windowWidth, int windowHeight) {
JSObject window = (JSObject)getEngine().executeScript("window");
int innerWidth = (Integer)window.getMember("innerWidth");
int innerHeight = (Integer)window.getMember("innerHeight");
int outerWidth = (Integer)window.getMember("outerWidth");
int outerHeight = (Integer)window.getMember("outerHeight");
if (windowWidth >= 0) {
assertEquals("window.outerWidth", windowWidth, outerWidth);
}
if (windowHeight >= 0) {
assertEquals("window.outerHeight", windowHeight, outerHeight);
}
assertTrue("window.outerWidth >= window.innerWidth", outerWidth >= innerWidth);
assertTrue("window.outerHeight >= window.innerHeight", outerHeight >= innerHeight);
}
private void checkProperties(Rectangle2D screenSize, Rectangle2D availSize,
int windowWidth, int windowHeight) {
checkScreenProperties(screenSize, availSize);
checkWindowProperties(windowWidth, windowHeight);
}
@Test public void test() throws InterruptedException {
submit(new Runnable() { public void run() {
Node view = getView();
checkWindowProperties(-1, -1);
Scene scene = new Scene(new Group(view));
checkWindowProperties(-1, -1);
Stage stage = new Stage();
stage.setScene(scene);
stage.setWidth(0);
stage.setHeight(0);
Screen screen = Utils.getScreen(view);
Rectangle2D screenSize = screen.getBounds();
Rectangle2D availSize = screen.getVisualBounds();
checkProperties(screenSize, availSize, 0, 0);
stage.setWidth(400);
stage.setHeight(300);
checkProperties(screenSize, availSize, 400, 300);
}});
}
}
