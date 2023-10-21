package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevice;
import test.robot.com.sun.glass.ui.monocle.input.devices.TestTouchDevices;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Parameterized;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import test.com.sun.glass.ui.monocle.TestRunnable;
public class TouchButtonTest extends ParameterizedTestBase {
private Node button1;
private Node button2;
private Node button3;
public TouchButtonTest(TestTouchDevice device) {
super(device);
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
return TestTouchDevices.getTouchDeviceParameters(1);
}
public Node createButton(String text, int x, int y, boolean setListeners) {
final Node button = new Rectangle(100.0, 20.0);
button.setId(text);
button.setLayoutX(x);
button.setLayoutY(y);
button.setOnMousePressed((e) -> button.requestFocus());
if (setListeners) {
button.addEventHandler(MouseEvent.ANY, e ->
TestLogShim.log(e.getEventType().getName() +": "
+ (int) e.getScreenX()
+ ", " + (int) e.getScreenY()));
button.focusedProperty().addListener((observable, oldValue, newValue) ->
TestLogShim.log(button.getId() + " isFocused=" + newValue));
}
return button;
}
@Before
public void createButtons() throws Exception {
TestRunnable.invokeAndWait(() -> {
int X = (int) width / 2;
int Y = (int) height / 2;
button1 = createButton("button1", X, Y - 100, true);
button2 = createButton("button2", X, Y + 100, true);
button3 = createButton("button3", 0, 0, false);
TestApplication.getRootGroup().getChildren().clear();
TestApplication.getRootGroup().getChildren().addAll(
button1, button2, button3);
button3.requestFocus();
});
TestApplication.waitForLayout();
}
@Test
public void tapOnButton() throws Exception {
Point2D clickAt = tapInsideButton(button1);
waitForFocusGainOn("button1");
waitForMouseEnteredAt(clickAt);
waitForMouseClickAt(clickAt);
}
@Test
public void tapOn2Buttons() throws Exception {
Point2D clickAt = tapInsideButton(button1);
waitForFocusGainOn("button1");
waitForMouseEnteredAt(clickAt);
waitForMouseClickAt(clickAt);
clickAt = tapInsideButton(button2);
waitForFocusLostOn("button1");
waitForFocusGainOn("button2");
waitForMouseEnteredAt(clickAt);
waitForMouseClickAt(clickAt);
}
@Test
public void tapOutAndInButton() throws Exception {
tapOutSideButton();
TestLogShim.reset();
Point2D clickAt = tapInsideButton(button1);
waitForMouseClickAt(clickAt);
waitForFocusGainOn("button1");
}
@Test
public void tapOutInAndOutButton() throws Exception {
tapOutSideButton();
TestLogShim.reset();
Point2D clickAt = tapInsideButton(button1);
waitForMouseClickAt(clickAt);
waitForFocusGainOn("button1");
tapOutSideButton();
tapInsideButton(button3);
waitForFocusLostOn("button1");
}
@Test
public void tapInAndOutLoop() throws Exception {
tapOutSideButton();
TestLogShim.reset();
for (int i = 0 ; i < 2 ; i++) {
tapOutSideButton();
tapInsideButton(button3);
TestLogShim.reset();
Point2D clickAt = tapInsideButton(button1);
waitForFocusGainOn("button1");
waitForMouseEnteredAt(clickAt);
waitForMouseClickAt(clickAt);
tapOutSideButton();
tapInsideButton(button3);
waitForFocusLostOn("button1");
TestLogShim.reset();
clickAt = tapInsideButton(button2);
waitForFocusGainOn("button2");
waitForMouseEnteredAt(clickAt);
waitForMouseClickAt(clickAt);
TestLogShim.reset();
tapOutSideButton();
tapInsideButton(button3);
waitForFocusLostOn("button2");
}
}
@Test
public void tapAndDrag() throws Exception {
Bounds buttonBounds = getButtonBounds(button2);
int x = (int) buttonBounds.getMaxX() - 1;
int y = (int) (buttonBounds.getMinY() + buttonBounds.getMaxY()) / 2;
int p = device.addPoint(x, y);
device.sync();
waitForFocusGainOn("button2");
for (; x > buttonBounds.getMinX(); x-- ) {
device.setPoint(p, x, y);
device.sync();
}
device.removePoint(p);
device.sync();
TestLogShim.waitForLogContaining("MOUSE_CLICKED:", 3000l);
TestLogShim.waitForLogContaining("MOUSE_RELEASED:", 3000l);
}
@Ignore("RT-34625")
@Test
public void tapAndDrag_fail() throws Exception {
Bounds buttonBounds = getButtonBounds(button2);
int x = (int) buttonBounds.getMaxX() - 1;
int y = (int) (buttonBounds.getMinY() + buttonBounds.getMaxY()) / 2;
int p = device.addPoint(x, y);
device.sync();
waitForFocusGainOn("button2");
for (; x > buttonBounds.getMinX() - device.getTapRadius() - 10; x-- ) {
device.setPoint(p, x, y);
device.sync();
}
device.removePoint(p);
device.sync();
TestLogShim.waitForLogContaining("MOUSE_CLICKED:", 3000l);
}
@Test
public void tapping_oneButtonOnScreen () throws Exception {
AtomicReference<Node> buttonRef = new AtomicReference<>();
TestRunnable.invokeAndWait(() -> {
Node button4 = createButton("button4", 0, 0, true);
buttonRef.set(button4);
TestApplication.getRootGroup().getChildren().clear();
TestApplication.getRootGroup().getChildren().addAll(button4);
});
TestApplication.waitForLayout();
for (int i = 0; i < 5; i++) {
Point2D clickAt = tapInsideButton(buttonRef.get());
waitForMouseClickAt(clickAt);
TestLogShim.reset();
}
}
public Bounds getButtonBounds(Node button) throws Exception {
AtomicReference<Bounds> ref = new AtomicReference<>();
TestRunnable.invokeAndWait(() -> {
ref.set(button.localToScreen(
new BoundingBox(0, 0,
button.getBoundsInParent().getWidth(),
button.getBoundsInParent().getHeight())));
TestLogShim.log("Bounds for " + button.getId() + " are " + ref.get());
});
return ref.get();
}
public Point2D getCenterOfButton(Node button) throws Exception {
Bounds buttonBounds = getButtonBounds(button);
Point2D clickAt = new Point2D(
buttonBounds.getMinX()+ buttonBounds.getWidth() / 2,
buttonBounds.getMinY()+ buttonBounds.getHeight() / 2);
return clickAt;
}
public Point2D tapInsideButton(Node button) throws Exception {
Point2D clickAt = getCenterOfButton(button);
int p = device.addPoint(clickAt.getX(), clickAt.getY());
device.sync();
device.removePoint(p);
device.sync();
TestLogShim.waitForLog("Mouse clicked: %.0f, %.0f", clickAt.getX(), clickAt.getY());
return clickAt;
}
public void tapOutSideButton() throws Exception {
Bounds buttonBounds = getButtonBounds(button3);
double x = buttonBounds.getMaxX() + device.getTapRadius() + 10;
double y = buttonBounds.getMaxY() + device.getTapRadius() + 10;
int p = device.addPoint(x, y);
device.sync();
device.removePoint(p);
device.sync();
TestLogShim.waitForLog("Mouse clicked: %.0f, %.0f", x, y);
}
public void waitForMouseClickAt(Point2D clickAt) throws Exception{
TestLogShim.waitForLog("MOUSE_CLICKED: %d, %d",
Math.round(clickAt.getX()),
Math.round(clickAt.getY()));
}
public void waitForMouseEnteredAt(Point2D clickAt) throws Exception{
TestLogShim.waitForLog("MOUSE_ENTERED: %d, %d",
Math.round(clickAt.getX()),
Math.round(clickAt.getY()));
}
public void waitForFocus(String id, boolean focusState) throws Exception {
TestLogShim.waitForLog("%s isFocused=%b", id, focusState);
}
public void waitForFocusGainOn(String id) throws Exception{
waitForFocus(id, true);
}
public void waitForFocusLostOn(String id) throws Exception{
waitForFocus(id, false);
}
}
