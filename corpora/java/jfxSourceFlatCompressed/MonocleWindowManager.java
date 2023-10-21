package com.sun.glass.ui.monocle;
import com.sun.glass.events.WindowEvent;
import com.sun.glass.ui.Screen;
import com.sun.javafx.tk.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.application.Platform;
final class MonocleWindowManager {
private static MonocleWindowManager instance = new MonocleWindowManager();
private MonocleWindow[] windows = new MonocleWindow[0];
private int nextID = 1;
private MonocleWindow focusedWindow = null;
private MonocleWindowManager() {
}
static MonocleWindowManager getInstance() {
return instance;
}
private int getWindowIndex(MonocleWindow window) {
for (int i = 0; i < windows.length; i++) {
if (windows[i] == window) {
return i;
}
}
return -1;
}
void toBack(MonocleWindow window) {
int index = getWindowIndex(window);
if (index != 0 && index != -1) {
System.arraycopy(windows, 0, windows, 1, index);
windows[0] = window;
}
}
void toFront(MonocleWindow window) {
int index = getWindowIndex(window);
if (index != windows.length - 1 && index != -1) {
System.arraycopy(windows, index + 1, windows, index,
windows.length - index - 1);
windows[windows.length - 1] = window;
}
}
int addWindow(MonocleWindow window) {
int index = getWindowIndex(window);
if (index == -1) {
windows = Arrays.copyOf(windows, windows.length + 1);
windows[windows.length - 1] = window;
}
return nextID++;
}
boolean closeWindow(MonocleWindow window) {
int index = getWindowIndex(window);
if (index != -1) {
System.arraycopy(windows, index + 1, windows, index,
windows.length - index - 1);
windows = Arrays.copyOf(windows, windows.length - 1);
}
List<MonocleWindow> windowsToNotify = new ArrayList<MonocleWindow>();
for (MonocleWindow otherWindow : windows) {
if (otherWindow.getOwner() == window) {
windowsToNotify.add(otherWindow);
}
}
for (int i = 0; i < windowsToNotify.size(); i++) {
windowsToNotify.get(i).notifyClose();
}
window.notifyDestroy();
if (focusedWindow == window) {
focusedWindow = null;
}
return true;
}
boolean minimizeWindow(MonocleWindow window) {
return true;
}
boolean maximizeWindow(MonocleWindow window) {
return true;
}
boolean requestFocus(MonocleWindow window) {
int index = getWindowIndex(window);
if (index != -1) {
focusedWindow = window;
window.notifyFocus(WindowEvent.FOCUS_GAINED);
return true;
} else {
return false;
}
}
boolean grabFocus(MonocleWindow window) {
return true;
}
void ungrabFocus(MonocleWindow window) {
}
MonocleWindow getWindowForLocation(int x, int y) {
for (int i = windows.length - 1; i >=0 ; i--) {
MonocleWindow w = windows[i];
if (x >= w.getX() && y >= w.getY()
&& x < w.getX() + w.getWidth()
&& y < w.getY() + w.getHeight()
&& w.isEnabled()) {
return w;
}
}
return null;
}
void notifyFocusDisabled(MonocleWindow window) {
if (window != null) {
window._notifyFocusDisabled();
}
}
MonocleWindow getFocusedWindow() {
return focusedWindow;
}
void repaintAll() {
for (int i = 0; i < windows.length; i++) {
MonocleView view = (MonocleView) windows[i].getView();
if (view != null) {
view.notifyRepaint();
}
}
}
static void repaintFromNative(Screen screen) {
Platform.runLater(() -> {
Screen.notifySettingsChanged();
MonocleWindow focusedWindow = instance.getFocusedWindow();
if (focusedWindow != null) {
if (screen != null && screen.getNativeScreen() != focusedWindow.getScreen().getNativeScreen()) {
focusedWindow.notifyMoveToAnotherScreen(screen);
}
focusedWindow.setFullScreen(true);
}
instance.repaintAll();
Toolkit.getToolkit().requestNextPulse();
});
}
}
