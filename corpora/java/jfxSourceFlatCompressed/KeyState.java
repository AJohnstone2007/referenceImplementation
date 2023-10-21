package com.sun.glass.ui.monocle;
import com.sun.glass.events.KeyEvent;
class KeyState {
private IntSet keysPressed = new IntSet();
private MonocleWindow window;
private int modifiers;
void clear() {
keysPressed.clear();
modifiers = 0;
}
void pressKey(int virtualKeyCode) {
keysPressed.addInt(virtualKeyCode);
modifiers |= getModifier(virtualKeyCode);
}
void releaseKey(int virtualKeyCode) {
keysPressed.removeInt(virtualKeyCode);
modifiers &= ~getModifier(virtualKeyCode);
}
void copyTo(KeyState target) {
keysPressed.copyTo(target.keysPressed);
target.window = window;
target.modifiers = modifiers;
}
IntSet getKeysPressed() {
return keysPressed;
}
MonocleWindow getWindow(boolean recalculateCache) {
if (window == null || recalculateCache) {
window = (MonocleWindow)
MonocleWindowManager.getInstance().getFocusedWindow();
}
return window;
}
private static int getModifier(int virtualKeyCode) {
switch (virtualKeyCode) {
case KeyEvent.VK_SHIFT: return KeyEvent.MODIFIER_SHIFT;
case KeyEvent.VK_CONTROL: return KeyEvent.MODIFIER_CONTROL;
case KeyEvent.VK_ALT: return KeyEvent.MODIFIER_ALT;
case KeyEvent.VK_COMMAND: return KeyEvent.MODIFIER_COMMAND;
case KeyEvent.VK_WINDOWS: return KeyEvent.MODIFIER_WINDOWS;
default: return KeyEvent.MODIFIER_NONE;
}
}
int getModifiers() {
return modifiers;
}
boolean isShiftPressed() {
return (modifiers & KeyEvent.MODIFIER_SHIFT) != 0;
}
boolean isControlPressed() {
return (modifiers & KeyEvent.MODIFIER_CONTROL) != 0;
}
public String toString() {
return "KeyState[modifiers=" + modifiers + ",keys=" + keysPressed + "]";
}
}
