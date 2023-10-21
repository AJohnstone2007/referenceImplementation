package com.sun.glass.ui.win;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import com.sun.glass.ui.Application;
import com.sun.glass.ui.GlassRobot;
final class WinRobot extends GlassRobot {
@Override
public void create() {
}
@Override
public void destroy() {
}
native protected void _keyPress(int code);
@Override
public void keyPress(KeyCode code) {
Application.checkEventThread();
_keyPress(code.getCode());
}
native protected void _keyRelease(int code);
@Override
public void keyRelease(KeyCode code) {
Application.checkEventThread();
_keyRelease(code.getCode());
}
native protected void _mouseMove(int x, int y);
@Override
public void mouseMove(double x, double y) {
Application.checkEventThread();
_mouseMove((int) x, (int) y);
}
native protected void _mousePress(int buttons);
@Override
public void mousePress(MouseButton... buttons) {
Application.checkEventThread();
_mousePress(GlassRobot.convertToRobotMouseButton(buttons));
}
native protected void _mouseRelease(int buttons);
@Override
public void mouseRelease(MouseButton... buttons) {
Application.checkEventThread();
_mouseRelease(GlassRobot.convertToRobotMouseButton(buttons));
}
native protected void _mouseWheel(int wheelAmt);
@Override
public void mouseWheel(int wheelAmt) {
Application.checkEventThread();
_mouseWheel(wheelAmt);
}
native protected float _getMouseX();
@Override
public double getMouseX() {
Application.checkEventThread();
return _getMouseX();
}
native protected float _getMouseY();
@Override
public double getMouseY() {
Application.checkEventThread();
return _getMouseY();
}
native protected int _getPixelColor(int x, int y);
@Override
public Color getPixelColor(double x, double y) {
Application.checkEventThread();
return GlassRobot.convertFromIntArgb(_getPixelColor((int) x, (int) y));
}
native protected void _getScreenCapture(int x, int y, int width, int height, int[] data);
@Override
public void getScreenCapture(int x, int y, int width, int height, int[] data, boolean scaleToFit) {
Application.checkEventThread();
_getScreenCapture(x, y, width, height, data);
}
}
