package test.javafx.scene.transform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventTarget;
import javafx.scene.transform.TransformChangedEvent;
import org.junit.Test;
import static org.junit.Assert.*;
public class TransformChangedEventTest {
@Test
public void testDefaultConstructor() {
TransformChangedEvent e = new TransformChangedEvent();
assertSame(TransformChangedEvent.TRANSFORM_CHANGED, e.getEventType());
assertSame(Event.NULL_SOURCE_TARGET, e.getSource());
assertSame(Event.NULL_SOURCE_TARGET, e.getTarget());
}
@Test
public void testConstructor() {
final EventTarget src = tail -> null;
final EventTarget trg = tail -> null;
TransformChangedEvent e = new TransformChangedEvent(src, trg);
assertSame(TransformChangedEvent.TRANSFORM_CHANGED, e.getEventType());
assertSame(src, e.getSource());
assertSame(trg, e.getTarget());
}
@Test
public void canCreateActionEventToo() {
TransformChangedEvent event = new TransformChangedEvent(null, null);
ActionEvent actionEvent = new ActionEvent(null, null);
}
}
