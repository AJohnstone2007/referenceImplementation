package com.sun.javafx.stage;
import javafx.stage.PopupWindow;
import com.sun.javafx.tk.FocusCause;
public class PopupWindowPeerListener extends WindowPeerListener {
private final PopupWindow popupWindow;
public PopupWindowPeerListener(PopupWindow popupWindow) {
super(popupWindow);
this.popupWindow = popupWindow;
}
public void changedFocused(boolean cf, FocusCause cause) {
WindowHelper.setFocused(popupWindow, cf);
}
public void closing() {
}
public void changedLocation(float x, float y) {
}
public void changedIconified(boolean iconified) {
}
public void changedMaximized(boolean maximized) {
}
public void changedResizable(boolean resizable) {
}
public void changedFullscreen(boolean fs) {
}
@Override public void focusUngrab() {
}
}
