package com.sun.javafx.stage;
import javafx.event.Event;
import javafx.stage.Window;
import com.sun.javafx.tk.FocusCause;
import com.sun.javafx.tk.TKStageListener;
import javafx.stage.WindowEvent;
public class WindowPeerListener implements TKStageListener {
private final Window window;
public WindowPeerListener(Window window) {
this.window = window;
}
@Override
public void changedLocation(float x, float y) {
WindowHelper.notifyLocationChanged(window, x, y);
}
@Override
public void changedSize(float width, float height) {
WindowHelper.notifySizeChanged(window, width, height);
}
@Override
public void changedScale(float xScale, float yScale) {
WindowHelper.notifyScaleChanged(window, xScale, yScale);
}
public void changedFocused(boolean focused, FocusCause cause) {
WindowHelper.setFocused(window, focused);
}
public void changedIconified(boolean iconified) {
}
public void changedMaximized(boolean maximized) {
}
public void changedResizable(boolean resizable) {
}
public void changedFullscreen(boolean fs) {
}
public void changedAlwaysOnTop(boolean aot) {
}
public void changedScreen(Object from, Object to) {
WindowHelper.getWindowAccessor().notifyScreenChanged(window, from, to);
}
@Override
public void closing() {
Event.fireEvent(window,
new WindowEvent(window,
WindowEvent.WINDOW_CLOSE_REQUEST));
}
@Override
public void closed() {
if (window.isShowing()) {
window.hide();
}
}
@Override public void focusUngrab() {
Event.fireEvent(window, new FocusUngrabEvent());
}
}
