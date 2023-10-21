package com.sun.javafx.webkit.drt;
import com.sun.webkit.WebPage;
import com.sun.javafx.webkit.KeyCodeMap;
import com.sun.webkit.event.WCKeyEvent;
import com.sun.webkit.event.WCMouseEvent;
import com.sun.webkit.event.WCMouseWheelEvent;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.input.KeyCode;
final class EventSender {
private static final int ALT = 1;
private static final int CTRL = 2;
private static final int META = 4;
private static final int SHIFT = 8;
private static final int PRESSED = 16;
private static final int CAPS_LOCK = 32;
private static final float ZOOM = 1.2f;
private static final float SCROLL = 40f;
private static final Map<Object, KeyCode> MAP = new HashMap<Object, KeyCode>();
static {
MAP.put("\r", KeyCode.ENTER);
MAP.put("pageUp", KeyCode.PAGE_UP);
MAP.put("pageDown", KeyCode.PAGE_DOWN);
MAP.put("leftArrow", KeyCode.LEFT);
MAP.put("upArrow", KeyCode.UP);
MAP.put("rightArrow", KeyCode.RIGHT);
MAP.put("downArrow", KeyCode.DOWN);
MAP.put("printScreen", KeyCode.PRINTSCREEN);
MAP.put("menu", KeyCode.CONTEXT_MENU);
for (KeyCode code : KeyCode.values()) {
MAP.put(code.getCode(), code);
MAP.put(code.getName().toLowerCase(), code);
MAP.put(code.getName(), code);
}
}
private final WebPage webPage;
private boolean dragMode = true;
private int mousePositionX;
private int mousePositionY;
private boolean mousePressed;
private int mouseButton = WCMouseEvent.NOBUTTON;
private long timeOffset;
private int modifiers;
private long lastClickTime;
private int lastMouseClickPositionX, lastMouseClickPositionY;
private int clickCount = 1;
EventSender(WebPage webPage) {
this.webPage = webPage;
}
private void keyDown(String key, int modifiers) {
String keyChar = null;
KeyCode code = MAP.get(key);
if (1 == key.length()) {
if (code == null) {
code = MAP.get(Integer.valueOf(Character.toUpperCase(
key.charAt(0))));
}
keyChar = key;
}
if (code == null) {
System.err.println("unexpected key = " + key);
}
else {
KeyCodeMap.Entry keyCodeEntry = KeyCodeMap.lookup(code);
String keyIdentifier = keyCodeEntry.getKeyIdentifier();
int windowsVirtualKeyCode = keyCodeEntry.getWindowsVirtualKeyCode();
dispatchKeyEvent(WCKeyEvent.KEY_PRESSED, null, keyIdentifier,
windowsVirtualKeyCode, modifiers);
dispatchKeyEvent(WCKeyEvent.KEY_TYPED, keyChar, null,
0, modifiers);
dispatchKeyEvent(WCKeyEvent.KEY_RELEASED, null, keyIdentifier,
windowsVirtualKeyCode, modifiers);
}
}
private void updateClickCountForButton(int buttonNumber) {
if ((getEventTime() - lastClickTime >= 1000) ||
!(mousePositionX == lastMouseClickPositionX && mousePositionY == lastMouseClickPositionY) ||
mouseButton != buttonNumber) {
clickCount = 1;
} else {
clickCount++;
}
}
private void mouseUpDown(int button, int modifiers) {
mousePressed = isSet(modifiers, PRESSED);
if (mousePressed) {
updateClickCountForButton(button);
}
mouseButton = button;
dispatchMouseEvent(mousePressed
? WCMouseEvent.MOUSE_PRESSED
: WCMouseEvent.MOUSE_RELEASED, button, clickCount, modifiers);
if (mousePressed) {
lastClickTime = getEventTime();
lastMouseClickPositionX = mousePositionX;
lastMouseClickPositionY = mousePositionY;
}
}
private void mouseMoveTo(int x, int y) {
mousePositionX = x;
mousePositionY = y;
dispatchMouseEvent(mousePressed
? WCMouseEvent.MOUSE_DRAGGED
: WCMouseEvent.MOUSE_MOVED,
(mousePressed ? mouseButton : WCMouseEvent.NOBUTTON), 0, 0);
}
private void mouseScroll(float x, float y, boolean continuous) {
if (continuous) {
x /= SCROLL;
y /= SCROLL;
}
webPage.dispatchMouseWheelEvent(new WCMouseWheelEvent(
mousePositionX, mousePositionY,
mousePositionX, mousePositionY,
getEventTime(),
false,
false,
false,
false,
x, y
));
}
private void leapForward(int timeOffset) {
this.timeOffset += timeOffset;
}
private void contextClick() {
dispatchMouseEvent(WCMouseEvent.MOUSE_PRESSED, WCMouseEvent.BUTTON2, 1, 0);
dispatchMouseEvent(WCMouseEvent.MOUSE_RELEASED, WCMouseEvent.BUTTON2, 1, 0);
}
private void scheduleAsynchronousClick() {
dispatchMouseEvent(WCMouseEvent.MOUSE_PRESSED, WCMouseEvent.BUTTON1, 1, 0);
dispatchMouseEvent(WCMouseEvent.MOUSE_RELEASED, WCMouseEvent.BUTTON1, 1, 0);
}
private void touchStart() {
throw new UnsupportedOperationException("touchStart");
}
private void touchCancel() {
throw new UnsupportedOperationException("touchCancel");
}
private void touchMove() {
throw new UnsupportedOperationException("touchMove");
}
private void touchEnd() {
throw new UnsupportedOperationException("touchEnd");
}
private void addTouchPoint(int x, int y) {
throw new UnsupportedOperationException("addTouchPoint");
}
private void updateTouchPoint(int i, int x, int y) {
throw new UnsupportedOperationException("updateTouchPoint");
}
private void cancelTouchPoint(int i) {
throw new UnsupportedOperationException("cancelTouchPoint");
}
private void releaseTouchPoint(int i) {
throw new UnsupportedOperationException("releaseTouchPoint");
}
private void clearTouchPoints() {
throw new UnsupportedOperationException("clearTouchPoints");
}
private void setTouchModifier(int modifier, boolean set) {
modifiers = set ? (modifiers | modifier) : (modifiers & ~modifier);
}
private void scalePageBy(float scale, int x, int y) {
throw new UnsupportedOperationException("scalePageBy(" + scale + "); x=" + x + "; y=" + y);
}
private void zoom(boolean in, boolean textOnly) {
float factor = webPage.getZoomFactor(textOnly);
webPage.setZoomFactor(in ? (factor * ZOOM) : (factor / ZOOM), textOnly);
}
private void beginDragWithFiles(String[] names) {
StringBuilder sb = new StringBuilder("beginDragWithFiles");
for (String name : names) {
sb.append(", ").append(name);
}
throw new UnsupportedOperationException(sb.append('.').toString());
}
private boolean getDragMode() {
return dragMode;
}
private void setDragMode(boolean mode) {
dragMode = mode;
}
private long getEventTime() {
return timeOffset + System.currentTimeMillis();
}
private void dispatchKeyEvent(int type, String text, String keyIdentifier,
int windowsVirtualKeyCode, int modifiers)
{
webPage.dispatchKeyEvent(new WCKeyEvent(
type, text, keyIdentifier, windowsVirtualKeyCode,
isSet(modifiers, SHIFT),
isSet(modifiers, CTRL),
isSet(modifiers, ALT),
isSet(modifiers, META),
getEventTime()
));
}
private void dispatchMouseEvent(int type, int button, int clicks, int modifiers) {
webPage.dispatchMouseEvent(new WCMouseEvent(
type, button, clicks,
mousePositionX, mousePositionY,
mousePositionX, mousePositionY,
getEventTime(),
isSet(modifiers, SHIFT),
isSet(modifiers, CTRL),
isSet(modifiers, ALT),
isSet(modifiers, META),
false
));
}
private static boolean isSet(int modifiers, int modifier) {
return modifier == (modifier & modifiers);
}
}
