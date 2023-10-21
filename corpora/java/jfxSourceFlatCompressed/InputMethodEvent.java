package javafx.scene.input;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;
public final class InputMethodEvent extends InputEvent{
private static final long serialVersionUID = 20121107L;
public static final EventType<InputMethodEvent> INPUT_METHOD_TEXT_CHANGED =
new EventType<InputMethodEvent>(InputEvent.ANY, "INPUT_METHOD_TEXT_CHANGED");
public static final EventType<InputMethodEvent> ANY = INPUT_METHOD_TEXT_CHANGED;
public InputMethodEvent(@NamedArg("source") Object source, @NamedArg("target") EventTarget target, @NamedArg("eventType") EventType<InputMethodEvent> eventType,
@NamedArg("composed") List<InputMethodTextRun> composed, @NamedArg("committed") String committed,
@NamedArg("caretPosition") int caretPosition) {
super(source, target, eventType);
this.composed = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(composed));
this.committed = committed;
this.caretPosition = caretPosition;
}
public InputMethodEvent(@NamedArg("eventType") EventType<InputMethodEvent> eventType,
@NamedArg("composed") List<InputMethodTextRun> composed, @NamedArg("committed") String committed,
@NamedArg("caretPosition") int caretPosition) {
this(null, null, eventType, composed, committed, caretPosition);
}
private transient ObservableList<InputMethodTextRun> composed;
public final ObservableList<InputMethodTextRun> getComposed() {
return composed;
}
private final String committed;
public final String getCommitted() {
return committed;
}
private final int caretPosition;
public final int getCaretPosition() {
return caretPosition;
}
@Override public String toString() {
final StringBuilder sb = new StringBuilder("InputMethodEvent [");
sb.append("source = ").append(getSource());
sb.append(", target = ").append(getTarget());
sb.append(", eventType = ").append(getEventType());
sb.append(", consumed = ").append(isConsumed());
sb.append(", composed = ").append(getComposed());
sb.append(", committed = ").append(getCommitted());
sb.append(", caretPosition = ").append(getCaretPosition());
return sb.append("]").toString();
}
@Override
public InputMethodEvent copyFor(Object newSource, EventTarget newTarget) {
return (InputMethodEvent) super.copyFor(newSource, newTarget);
}
@Override
public EventType<InputMethodEvent> getEventType() {
return (EventType<InputMethodEvent>) super.getEventType();
}
private void writeObject(ObjectOutputStream oos) throws IOException {
oos.defaultWriteObject();
oos.writeObject(new ArrayList(composed));
}
private void readObject(ObjectInputStream ois) throws IOException,
ClassNotFoundException {
ois.defaultReadObject();
ArrayList<InputMethodTextRun> o = (ArrayList)ois.readObject();
composed = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(o));
}
}
