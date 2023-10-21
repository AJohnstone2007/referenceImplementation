package com.sun.glass.ui.ios;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import com.sun.glass.ui.Application;
import com.sun.glass.ui.GlassRobot;
final class IosRobot extends GlassRobot {
private long ptr = 0;
private native long _init();
@Override public void create() {
Application.checkEventThread();
ptr = _init();
}
private native void _destroy(long ptr);
@Override public void destroy() {
Application.checkEventThread();
_destroy(ptr);
ptr = 0;
}
private native void _keyPress(long ptr, int code);
@Override public void keyPress(KeyCode code) {
Application.checkEventThread();
if (ptr == 0) {
return;
}
_keyPress(ptr, code.getCode());
}
private native void _keyRelease(long ptr, int code);
@Override public void keyRelease(KeyCode code) {
Application.checkEventThread();
if (ptr == 0) {
return;
}
_keyRelease(ptr, code.getCode());
}
private native void _mouseMove(long ptr, int x, int y);
@Override public void mouseMove(double x, double y) {
Application.checkEventThread();
if (ptr == 0) {
return;
}
_mouseMove(ptr, (int) x, (int) y);
}
private native void _mousePress(long ptr, int buttons);
@Override
public void mousePress(MouseButton... buttons) {
Application.checkEventThread();
if (ptr == 0) {
return;
}
_mousePress(ptr, GlassRobot.convertToRobotMouseButton(buttons));
}
private native void _mouseRelease(long ptr, int buttons);
@Override
public void mouseRelease(MouseButton... buttons) {
Application.checkEventThread();
if (ptr == 0) {
return;
}
_mouseRelease(ptr, GlassRobot.convertToRobotMouseButton(buttons));
}
private native void _mouseWheel(long ptr, int wheelAmt);
@Override public void mouseWheel(int wheelAmt) {
Application.checkEventThread();
if (ptr == 0) {
return;
}
_mouseWheel(ptr, wheelAmt);
}
private native int _getMouseX(long ptr);
@Override public double getMouseX() {
Application.checkEventThread();
if (ptr == 0) {
return 0;
}
return _getMouseX(ptr);
}
private native int _getMouseY(long ptr);
@Override public double getMouseY() {
Application.checkEventThread();
if (ptr == 0) {
return 0;
}
return _getMouseY(ptr);
}
private native int _getPixelColor(long ptr, int x, int y);
@Override public Color getPixelColor(double x, double y) {
Application.checkEventThread();
if (ptr == 0) {
return GlassRobot.convertFromIntArgb(0);
}
return GlassRobot.convertFromIntArgb(_getPixelColor(ptr, (int) x, (int) y));
}
private native void _getScreenCapture(long ptr, int x, int y, int width, int height, int[] data);
@Override
public void getScreenCapture(int x, int y, int width, int height, int[] data, boolean scaleToFit) {
Application.checkEventThread();
if (ptr == 0) {
return;
}
_getScreenCapture(ptr, x, y, width, height, data);
}
}
