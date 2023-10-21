package test.com.sun.javafx.scene.control.infrastructure;
import com.sun.javafx.scene.SceneHelper;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
public class KeyEventFirer {
private final EventTarget target;
private final Scene scene;
public KeyEventFirer(EventTarget target) {
this(Objects.requireNonNull(target), null);
}
public KeyEventFirer(EventTarget target, Scene scene) {
this.target = target;
this.scene = scene;
if (target == null && scene == null) {
throw new NullPointerException("both target and scene are null");
}
}
public void doUpArrowPress(KeyModifier... modifiers) {
doKeyPress(KeyCode.UP, modifiers);
}
public void doDownArrowPress(KeyModifier... modifiers) {
doKeyPress(KeyCode.DOWN, modifiers);
}
public void doLeftArrowPress(KeyModifier... modifiers) {
doKeyPress(KeyCode.LEFT, modifiers);
}
public void doRightArrowPress(KeyModifier... modifiers) {
doKeyPress(KeyCode.RIGHT, modifiers);
}
public void doKeyPress(KeyCode keyCode, KeyModifier... modifiers) {
fireEvents(createMirroredEvents(keyCode, modifiers));
}
public void doKeyTyped(KeyCode keyCode, KeyModifier... modifiers) {
fireEvents(createEvent(keyCode, KeyEvent.KEY_TYPED, modifiers));
}
private void fireEvents(KeyEvent... events) {
for (KeyEvent evt : events) {
if (scene != null) {
SceneHelper.processKeyEvent(scene, evt);
} else {
Event.fireEvent(target, evt);
}
}
}
private KeyEvent[] createMirroredEvents(KeyCode keyCode, KeyModifier... modifiers) {
KeyEvent[] events = new KeyEvent[2];
events[0] = createEvent(keyCode, KeyEvent.KEY_PRESSED, modifiers);
events[1] = createEvent(keyCode, KeyEvent.KEY_RELEASED, modifiers);
return events;
}
private KeyEvent createEvent(KeyCode keyCode, EventType<KeyEvent> evtType, KeyModifier... modifiers) {
List<KeyModifier> ml = Arrays.asList(modifiers);
return new KeyEvent(null,
target,
evtType,
evtType == KeyEvent.KEY_TYPED ? keyCode.getChar() : null,
keyCode.getChar(),
keyCode,
ml.contains(KeyModifier.SHIFT),
ml.contains(KeyModifier.CTRL),
ml.contains(KeyModifier.ALT),
ml.contains(KeyModifier.META)
);
}
}
