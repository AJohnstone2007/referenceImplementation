package javafx.scene.input;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.NamedArg;
import javafx.event.EventTarget;
import javafx.event.EventType;
import com.sun.javafx.scene.input.KeyCodeMap;
import javafx.event.Event;
import javafx.scene.input.ScrollEvent.HorizontalTextScrollUnits;
import javafx.scene.input.ScrollEvent.VerticalTextScrollUnits;
public final class KeyEvent extends InputEvent {
private static final long serialVersionUID = 20121107L;
public static final EventType<KeyEvent> ANY =
new EventType<KeyEvent>(InputEvent.ANY, "KEY");
public static final EventType<KeyEvent> KEY_PRESSED =
new EventType<KeyEvent>(KeyEvent.ANY, "KEY_PRESSED");
public static final EventType<KeyEvent> KEY_RELEASED =
new EventType<KeyEvent>(KeyEvent.ANY, "KEY_RELEASED");
public static final EventType<KeyEvent> KEY_TYPED =
new EventType<KeyEvent>(KeyEvent.ANY, "KEY_TYPED");
public KeyEvent(@NamedArg("source") Object source, @NamedArg("target") EventTarget target, @NamedArg("eventType") EventType<KeyEvent> eventType, @NamedArg("character") String character,
@NamedArg("text") String text, @NamedArg("code") KeyCode code, @NamedArg("shiftDown") boolean shiftDown, @NamedArg("controlDown") boolean controlDown,
@NamedArg("altDown") boolean altDown, @NamedArg("metaDown") boolean metaDown) {
super(source, target, eventType);
boolean isKeyTyped = eventType == KEY_TYPED;
this.character = isKeyTyped ? character : KeyEvent.CHAR_UNDEFINED;
this.text = isKeyTyped ? "" : text;
this.code = isKeyTyped ? KeyCode.UNDEFINED : code;
this.shiftDown = shiftDown;
this.controlDown = controlDown;
this.altDown = altDown;
this.metaDown = metaDown;
}
public KeyEvent(@NamedArg("eventType") EventType<KeyEvent> eventType, @NamedArg("character") String character,
@NamedArg("text") String text, @NamedArg("code") KeyCode code, @NamedArg("shiftDown") boolean shiftDown, @NamedArg("controlDown") boolean controlDown,
@NamedArg("altDown") boolean altDown, @NamedArg("metaDown") boolean metaDown) {
super(eventType);
boolean isKeyTyped = eventType == KEY_TYPED;
this.character = isKeyTyped ? character : KeyEvent.CHAR_UNDEFINED;
this.text = isKeyTyped ? "" : text;
this.code = isKeyTyped ? KeyCode.UNDEFINED : code;
this.shiftDown = shiftDown;
this.controlDown = controlDown;
this.altDown = altDown;
this.metaDown = metaDown;
}
public static final String CHAR_UNDEFINED = KeyCode.UNDEFINED.ch;
private final String character;
public final String getCharacter() {
return character;
}
private final String text;
public final String getText() {
return text;
}
private final KeyCode code;
public final KeyCode getCode() {
return code;
}
private final boolean shiftDown;
public final boolean isShiftDown() {
return shiftDown;
}
private final boolean controlDown;
public final boolean isControlDown() {
return controlDown;
}
private final boolean altDown;
public final boolean isAltDown() {
return altDown;
}
private final boolean metaDown;
public final boolean isMetaDown() {
return metaDown;
}
public final boolean isShortcutDown() {
switch (Toolkit.getToolkit().getPlatformShortcutKey()) {
case SHIFT:
return shiftDown;
case CONTROL:
return controlDown;
case ALT:
return altDown;
case META:
return metaDown;
default:
return false;
}
}
@Override public String toString() {
final StringBuilder sb = new StringBuilder("KeyEvent [");
sb.append("source = ").append(getSource());
sb.append(", target = ").append(getTarget());
sb.append(", eventType = ").append(getEventType());
sb.append(", consumed = ").append(isConsumed());
sb.append(", character = ").append(getCharacter());
sb.append(", text = ").append(getText());
sb.append(", code = ").append(getCode());
if (isShiftDown()) {
sb.append(", shiftDown");
}
if (isControlDown()) {
sb.append(", controlDown");
}
if (isAltDown()) {
sb.append(", altDown");
}
if (isMetaDown()) {
sb.append(", metaDown");
}
if (isShortcutDown()) {
sb.append(", shortcutDown");
}
return sb.append("]").toString();
}
@Override
public KeyEvent copyFor(Object newSource, EventTarget newTarget) {
return (KeyEvent) super.copyFor(newSource, newTarget);
}
public KeyEvent copyFor(Object source, EventTarget target, EventType<KeyEvent> type) {
KeyEvent e = copyFor(source, target);
e.eventType = type;
return e;
}
@Override
public EventType<KeyEvent> getEventType() {
return (EventType<KeyEvent>) super.getEventType();
}
}
