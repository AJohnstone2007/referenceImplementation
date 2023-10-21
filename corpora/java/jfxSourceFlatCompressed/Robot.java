package javafx.scene.robot;
import static com.sun.javafx.FXPermissions.CREATE_ROBOT_PERMISSION;
import java.util.Objects;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import com.sun.glass.ui.Application;
import com.sun.glass.ui.GlassRobot;
import com.sun.javafx.tk.Toolkit;
public final class Robot {
private final GlassRobot peer;
public Robot() {
Application.checkEventThread();
@SuppressWarnings("removal")
final SecurityManager sm = System.getSecurityManager();
if (sm != null) {
sm.checkPermission(CREATE_ROBOT_PERMISSION);
}
peer = Toolkit.getToolkit().createRobot();
peer.create();
}
public void keyPress(KeyCode keyCode) {
Objects.requireNonNull(keyCode, "keyCode must not be null");
peer.keyPress(keyCode);
}
public void keyRelease(KeyCode keyCode) {
Objects.requireNonNull(keyCode, "keyCode must not be null");
peer.keyRelease(keyCode);
}
public void keyType(KeyCode keyCode) {
Objects.requireNonNull(keyCode, "keyCode must not be null");
keyPress(keyCode);
keyRelease(keyCode);
}
public double getMouseX() {
return peer.getMouseX();
}
public double getMouseY() {
return peer.getMouseY();
}
public Point2D getMousePosition() {
return new Point2D(getMouseX(), getMouseY());
}
public void mouseMove(double x, double y) {
peer.mouseMove(x, y);
}
public final void mouseMove(Point2D location) {
Objects.requireNonNull(location);
mouseMove(location.getX(), location.getY());
}
public void mousePress(MouseButton... buttons) {
Objects.requireNonNull(buttons, "buttons must not be null");
peer.mousePress(buttons);
}
public void mouseRelease(MouseButton... buttons) {
Objects.requireNonNull(buttons, "buttons must not be null");
peer.mouseRelease(buttons);
}
public void mouseClick(MouseButton... buttons) {
Objects.requireNonNull(buttons, "buttons must not be null");
mousePress(buttons);
mouseRelease(buttons);
}
public void mouseWheel(int wheelAmt) {
peer.mouseWheel(wheelAmt);
}
public Color getPixelColor(double x, double y) {
return peer.getPixelColor(x, y);
}
public Color getPixelColor(Point2D location) {
return getPixelColor(location.getX(), location.getY());
}
public WritableImage getScreenCapture(WritableImage image, double x, double y,
double width, double height, boolean scaleToFit) {
return peer.getScreenCapture(image, x, y, width, height, scaleToFit);
}
public WritableImage getScreenCapture(WritableImage image, double x, double y,
double width, double height) {
return getScreenCapture(image, x, y, width, height, true);
}
public WritableImage getScreenCapture(WritableImage image, Rectangle2D region) {
Objects.requireNonNull(region);
return getScreenCapture(image, region.getMinX(), region.getMinY(),
region.getWidth(), region.getHeight(), true);
}
public WritableImage getScreenCapture(WritableImage image, Rectangle2D region, boolean scaleToFit) {
Objects.requireNonNull(region);
return getScreenCapture(image, region.getMinX(), region.getMinY(),
region.getWidth(), region.getHeight(), scaleToFit);
}
}
