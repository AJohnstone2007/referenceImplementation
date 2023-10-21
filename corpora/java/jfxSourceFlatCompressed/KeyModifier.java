package test.com.sun.javafx.scene.control.infrastructure;
import com.sun.javafx.util.Utils;
import test.com.sun.javafx.pgstub.StubToolkit;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.input.KeyCode;
public enum KeyModifier {
SHIFT,
CTRL,
ALT,
META;
public static KeyModifier getShortcutKey() {
if (Toolkit.getToolkit() instanceof StubToolkit) {
((StubToolkit)Toolkit.getToolkit()).setPlatformShortcutKey(Utils.isMac() ? KeyCode.META : KeyCode.CONTROL);
}
switch (Toolkit.getToolkit().getPlatformShortcutKey()) {
case SHIFT:
return SHIFT;
case CONTROL:
return CTRL;
case ALT:
return ALT;
case META:
return META;
default:
return null;
}
}
public static KeyModifier getWordTraversalKey() {
return Utils.isMac() ? ALT : getShortcutKey();
}
}
