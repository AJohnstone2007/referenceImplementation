package com.sun.javafx.scene.control.inputmap;
import com.sun.javafx.util.Utils;
import com.sun.javafx.tk.Toolkit;
import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.Objects;
import static com.sun.javafx.scene.control.inputmap.KeyBinding.OptionalBoolean.*;
public class KeyBinding {
private final KeyCode code;
private final EventType<KeyEvent> eventType;
private OptionalBoolean shift = FALSE;
private OptionalBoolean ctrl = FALSE;
private OptionalBoolean alt = FALSE;
private OptionalBoolean meta = FALSE;
public KeyBinding(KeyCode code) {
this(code, null);
}
public KeyBinding(EventType<KeyEvent> type) {
this(null, type);
}
public KeyBinding(KeyCode code, EventType<KeyEvent> type) {
this.code = code;
this.eventType = type != null ? type : KeyEvent.KEY_PRESSED;
}
public final KeyBinding shift() {
return shift(TRUE);
}
public final KeyBinding shift(OptionalBoolean value) {
shift = value;
return this;
}
public final KeyBinding ctrl() {
return ctrl(TRUE);
}
public final KeyBinding ctrl(OptionalBoolean value) {
ctrl = value;
return this;
}
public final KeyBinding alt() {
return alt(TRUE);
}
public final KeyBinding alt(OptionalBoolean value) {
alt = value;
return this;
}
public final KeyBinding meta() {
return meta(TRUE);
}
public final KeyBinding meta(OptionalBoolean value) {
meta = value;
return this;
}
public final KeyBinding shortcut() {
if (Toolkit.getToolkit().getClass().getName().endsWith("StubToolkit")) {
if (Utils.isMac()) {
return meta();
} else {
return ctrl();
}
} else {
switch (Toolkit.getToolkit().getPlatformShortcutKey()) {
case SHIFT:
return shift();
case CONTROL:
return ctrl();
case ALT:
return alt();
case META:
return meta();
default:
return this;
}
}
}
public final KeyCode getCode() { return code; }
public final EventType<KeyEvent> getType() { return eventType; }
public final OptionalBoolean getShift() { return shift; }
public final OptionalBoolean getCtrl() { return ctrl; }
public final OptionalBoolean getAlt() { return alt; }
public final OptionalBoolean getMeta() { return meta; }
public int getSpecificity(KeyEvent event) {
int s = 0;
if (code != null && code != event.getCode()) return 0; else s = 1;
if (!shift.equals(event.isShiftDown())) return 0; else if (shift != ANY) s++;
if (!ctrl.equals(event.isControlDown())) return 0; else if (ctrl != ANY) s++;
if (!alt.equals(event.isAltDown())) return 0; else if (alt != ANY) s++;
if (!meta.equals(event.isMetaDown())) return 0; else if (meta != ANY) s++;
if (eventType != null && eventType != event.getEventType()) return 0; else s++;
return s;
}
@Override public String toString() {
return "KeyBinding [code=" + code + ", shift=" + shift +
", ctrl=" + ctrl + ", alt=" + alt +
", meta=" + meta + ", type=" + eventType + "]";
}
@Override public boolean equals(Object o) {
if (this == o) return true;
if (!(o instanceof KeyBinding)) return false;
KeyBinding that = (KeyBinding) o;
return Objects.equals(getCode(), that.getCode()) &&
Objects.equals(eventType, that.eventType) &&
Objects.equals(getShift(), that.getShift()) &&
Objects.equals(getCtrl(), that.getCtrl()) &&
Objects.equals(getAlt(), that.getAlt()) &&
Objects.equals(getMeta(), that.getMeta());
}
@Override public int hashCode() {
return Objects.hash(getCode(), eventType, getShift(), getCtrl(), getAlt(), getMeta());
}
public static KeyBinding toKeyBinding(KeyEvent keyEvent) {
KeyBinding newKeyBinding = new KeyBinding(keyEvent.getCode(), keyEvent.getEventType());
if (keyEvent.isShiftDown()) newKeyBinding.shift();
if (keyEvent.isControlDown()) newKeyBinding.ctrl();
if (keyEvent.isAltDown()) newKeyBinding.alt();
if (keyEvent.isShortcutDown()) newKeyBinding.shortcut();
return newKeyBinding;
}
public enum OptionalBoolean {
TRUE,
FALSE,
ANY;
public boolean equals(boolean b) {
if (this == ANY) return true;
if (b && this == TRUE) return true;
if (!b && this == FALSE) return true;
return false;
}
}
}
