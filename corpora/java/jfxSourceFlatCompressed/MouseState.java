package com.sun.glass.ui.monocle;
import com.sun.glass.events.KeyEvent;
import com.sun.glass.events.MouseEvent;
class MouseState {
static final int WHEEL_NONE = 0;
static final int WHEEL_UP = 1;
static final int WHEEL_DOWN = -1 ;
private int x;
private int y;
private int wheel;
private MonocleWindow window;
private IntSet buttonsPressed = new IntSet();
int getX() {
return x;
}
void setX(int x) {
this.x = x;
}
int getY() {
return y;
}
void setY(int y) {
this.y = y;
}
int getWheel() {
return wheel;
}
void setWheel(int wheel) {
this.wheel = wheel;
}
void pressButton(int button) {
buttonsPressed.addInt(button);
}
void releaseButton(int button) {
buttonsPressed.removeInt(button);
}
MonocleWindow getWindow(boolean recalculateCache, MonocleWindow fallback) {
if (recalculateCache) {
window = (MonocleWindow)
MonocleWindowManager.getInstance().getWindowForLocation(x, y);
}
if (window == null) {
window = fallback;
}
return window;
}
int getButton() {
return buttonsPressed.isEmpty()
? MouseEvent.BUTTON_NONE
: buttonsPressed.get(0);
}
int getModifiers() {
int modifiers = KeyEvent.MODIFIER_NONE;
for (int i = 0; i < buttonsPressed.size(); i++) {
switch(buttonsPressed.get(i)) {
case MouseEvent.BUTTON_LEFT:
modifiers |= KeyEvent.MODIFIER_BUTTON_PRIMARY;
break;
case MouseEvent.BUTTON_OTHER:
modifiers |= KeyEvent.MODIFIER_BUTTON_MIDDLE;
break;
case MouseEvent.BUTTON_RIGHT:
modifiers |= KeyEvent.MODIFIER_BUTTON_SECONDARY;
break;
case MouseEvent.BUTTON_BACK:
modifiers |= KeyEvent.MODIFIER_BUTTON_BACK;
break;
case MouseEvent.BUTTON_FORWARD:
modifiers |= KeyEvent.MODIFIER_BUTTON_FORWARD;
break;
}
}
return modifiers;
}
void copyTo(MouseState target) {
target.x = x;
target.y = y;
target.wheel = wheel;
buttonsPressed.copyTo(target.buttonsPressed);
target.window = window;
}
IntSet getButtonsPressed() {
return buttonsPressed;
}
public String toString() {
return "MouseState[x="
+ x + ",y=" + y
+ ",wheel=" + wheel
+ ",buttonsPressed=" + buttonsPressed + "]";
}
boolean canBeFoldedWith(MouseState ms) {
return ms.buttonsPressed.equals(buttonsPressed) && ms.wheel == wheel;
}
}
