package test.com.sun.javafx.scene.control.infrastructure;
import java.util.Arrays;
import java.util.Collection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import com.sun.javafx.tk.Toolkit;
import static javafx.scene.layout.AnchorPane.*;
import static org.junit.Assert.*;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
@RunWith(Parameterized.class)
public class MouseEventFirerTest {
private Scene scene;
private Stage stage;
private AnchorPane content;
private static final double EPS = 1;
private final double VERTICAL_DISTANCE = 100.;
private final double HORIZONTAL_DISTANCE = 20.;
private Node topLeft, center, bottomRight;
@Test
public void testLocalStandaloneDeltaNegative() {
Button button = new Button("standalone button, a bit longish");
assertLocal(button, - 10, - 5);
}
@Test
public void testLocalStandaloneDelta() {
Button button = new Button("standalone button, a bit longish");
assertLocal(button, 10, 5);
}
@Test
public void testLocalStandalone() {
Button button = new Button("standalone button, a bit longish");
assertLocal(button, 0, 0);
}
@Test
public void testMouseCoordinatesStandaloneDeltaNegative() {
Button button = new Button("standalone button, a bit longish");
assertMouseCoordinatesDelta(button, - 10, - 5);
}
@Test
public void testMouseCoordinatesStandaloneDelta() {
Button button = new Button("standalone button, a bit longish");
assertMouseCoordinatesDelta(button, 10, 5);
}
@Test
public void testMouseCoordinatesStandalone() {
Button button = new Button("standalone button, a bit longish");
assertMouseCoordinatesDelta(button, 0, 0);
}
@Test
public void testLocalDeltaNegative() {
content.getChildren().forEach(child -> assertLocal(child, - 10, - 5));
}
@Test
public void testLocalDelta() {
content.getChildren().forEach(child -> assertLocal(child, 10, 5));
}
@Test
public void testLocal() {
content.getChildren().forEach(child -> assertLocal(child, 0, 0));
}
protected void assertLocal(Node target, double deltaX, double deltaY) {
MouseEventFirer firer = new MouseEventFirer(target, useAlternative);
String text = target instanceof Labeled ? ((Labeled) target).getText() : target.getId();
target.setOnMousePressed(e -> {
double width = target.getLayoutBounds().getWidth();
double height = target.getLayoutBounds().getHeight();
assertEquals("local x of " + text, width /2 + deltaX, e.getX(), EPS);
assertEquals("local y of " + text, height / 2 + deltaY, e.getY(), EPS);
});
firer.fireMousePressed(deltaX, deltaY);
}
@Test
public void testMouseCoordinatesDeltaNegative() {
content.getChildren().forEach(child -> assertMouseCoordinatesDelta(child, - 10, - 5));
}
@Test
public void testMouseCoordinatesDelta() {
content.getChildren().forEach(child -> assertMouseCoordinatesDelta(child, 10, 5));
}
@Test
public void testMouseCoordinates() {
content.getChildren().forEach(child -> assertMouseCoordinatesDelta(child, 0, 0));
}
protected void assertMouseCoordinatesDelta(Node target, double deltaX, double deltaY) {
MouseEventFirer firer = new MouseEventFirer(target, useAlternative);
target.setOnMousePressed(this::assertMouseEventCoordinates);
firer.fireMousePressed(deltaX, deltaY);
}
protected void assertMouseEventCoordinates(MouseEvent mouse) {
assertSame(mouse.getTarget(), mouse.getSource());
Node receiver = (Node) mouse.getTarget();
String text = receiver instanceof Labeled ? ((Labeled) receiver).getText() : receiver.getId();
Point2D sceneP = receiver.localToScene(mouse.getX(), mouse.getY());
assertEquals("sceneX of " + text, sceneP.getX(), mouse.getSceneX(), EPS);
assertEquals("sceneY of " + text, sceneP.getY(), mouse.getSceneY(), EPS);
Point2D screenP = receiver.localToScreen(mouse.getX(), mouse.getY());
assertEquals("screenX of " + text, screenP.getX(), mouse.getScreenX(), EPS);
assertEquals("screenY of " + text, screenP.getY(), mouse.getScreenY(), EPS);
}
private boolean useAlternative;
@Parameterized.Parameters
public static Collection<Object[]> data() {
Object[][] data = new Object[][] {
{true},
};
return Arrays.asList(data);
}
public MouseEventFirerTest(boolean useAlternative) {
this.useAlternative = useAlternative;
}
@Test
public void testFirer() {
new MouseEventFirer(topLeft, true);
assertSame("sanity: firer must not change hierarchy", scene, topLeft.getScene());
assertSame("sanity: firer must not change hierarchy", stage, topLeft.getScene().getWindow());
}
@Test
public void testAnchorRight() {
setLeftAnchor(topLeft, null);
setRightAnchor(topLeft, 0.);
Toolkit.getToolkit().firePulse();
assertEquals(content.getWidth() - topLeft.prefWidth(-1), topLeft.getBoundsInParent().getMinX(), EPS);
}
@Test
public void testLayoutBounds() {
Bounds initial = topLeft.getLayoutBounds();
setLeftAnchor(topLeft, null);
setRightAnchor(topLeft, 0.);
Toolkit.getToolkit().firePulse();
assertEquals("sanity: layout bounds unchanged", initial, topLeft.getLayoutBounds());
}
@Test
public void testContentLayout() {
assertTrue(stage.isShowing());
assertEquals(2* VERTICAL_DISTANCE + center.prefHeight(-1), content.getHeight(), EPS);
assertEquals(2* HORIZONTAL_DISTANCE + center.prefWidth(-1), content.getWidth(), EPS);
assertEquals(HORIZONTAL_DISTANCE, center.getBoundsInParent().getMinX(), EPS);
assertEquals(VERTICAL_DISTANCE, center.getBoundsInParent().getMinY(),EPS);
assertEquals(0, topLeft.getBoundsInParent().getMinX(), EPS);
assertEquals(0, topLeft.getBoundsInParent().getMinY(), EPS);
assertEquals(0, bottomRight.getBoundsInParent().getMinX(), EPS);
assertEquals(content.getHeight() - bottomRight.prefHeight(-1), bottomRight.getBoundsInParent().getMinY(), EPS);
}
@Before
public void setup() {
Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
if (throwable instanceof RuntimeException) {
throw (RuntimeException)throwable;
} else {
Thread.currentThread().getThreadGroup().uncaughtException(thread, throwable);
}
});
topLeft = new Button("topLeft");
setTopAnchor(topLeft, 0.);
setLeftAnchor(topLeft, 0.);
center = new Button("center: a longish text nearly filling horizontally");
setTopAnchor(center, VERTICAL_DISTANCE);
setBottomAnchor(center, VERTICAL_DISTANCE);
setLeftAnchor(center, HORIZONTAL_DISTANCE);
setRightAnchor(center, HORIZONTAL_DISTANCE);
bottomRight = new Button("botRight");
setBottomAnchor(bottomRight, 0.);
setLeftAnchor(bottomRight, 0.);
content = new AnchorPane(topLeft, center, bottomRight);
scene = new Scene(content);
stage = new Stage();
stage.setScene(scene);
stage.show();
}
@After
public void tearDown() {
if (stage != null) stage.hide();
Thread.currentThread().setUncaughtExceptionHandler(null);
}
}
