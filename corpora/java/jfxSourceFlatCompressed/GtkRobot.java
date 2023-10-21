package com.sun.glass.ui.gtk;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import com.sun.glass.ui.Application;
import com.sun.glass.ui.GlassRobot;
import com.sun.glass.ui.Screen;
final class GtkRobot extends GlassRobot {
@Override
public void create() {
}
@Override
public void destroy() {
}
@Override
public void keyPress(KeyCode code) {
Application.checkEventThread();
_keyPress(code.getCode());
}
protected native void _keyPress(int code);
@Override
public void keyRelease(KeyCode code) {
Application.checkEventThread();
_keyRelease(code.getCode());
}
protected native void _keyRelease(int code);
public native void _mouseMove(int x, int y);
@Override
public void mouseMove(double x, double y) {
Application.checkEventThread();
_mouseMove((int) x, (int) y);
}
@Override
public void mousePress(MouseButton... buttons) {
Application.checkEventThread();
_mousePress(GlassRobot.convertToRobotMouseButton(buttons));
}
protected native void _mousePress(int button);
@Override
public void mouseRelease(MouseButton... buttons) {
Application.checkEventThread();
_mouseRelease(GlassRobot.convertToRobotMouseButton(buttons));
}
protected native void _mouseRelease(int buttons);
@Override
public void mouseWheel(int wheelAmt) {
Application.checkEventThread();
_mouseWheel(wheelAmt);
}
protected native void _mouseWheel(int wheelAmt);
@Override
public double getMouseX() {
Application.checkEventThread();
return _getMouseX();
}
protected native int _getMouseX();
@Override
public double getMouseY() {
Application.checkEventThread();
return _getMouseY();
}
protected native int _getMouseY();
@Override
public Color getPixelColor(double x, double y) {
Application.checkEventThread();
Screen mainScreen = Screen.getMainScreen();
x = (int) Math.floor((x + 0.5) * mainScreen.getPlatformScaleX());
y = (int) Math.floor((y + 0.5) * mainScreen.getPlatformScaleY());
int[] result = new int[1];
_getScreenCapture((int) x, (int) y, 1, 1, result);
return GlassRobot.convertFromIntArgb(result[0]);
}
protected native void _getScreenCapture(int x, int y, int width, int height, int[] data);
@Override
public void getScreenCapture(int x, int y, int width, int height, int[] data, boolean scaleToFit) {
Application.checkEventThread();
_getScreenCapture(x, y, width, height, data);
}
}
