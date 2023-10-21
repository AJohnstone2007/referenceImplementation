package test.javafx.scene.input;
import javafx.event.Event;
import javafx.scene.input.InputEvent;
import javafx.scene.shape.Rectangle;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
public class InputEventTest {
@Test public void testShortConstructor() {
Rectangle node = new Rectangle(10, 10);
node.setTranslateX(3);
node.setTranslateY(2);
node.setTranslateZ(50);
InputEvent e = new InputEvent(InputEvent.ANY);
assertSame(InputEvent.ANY, e.getEventType());
assertFalse(e.isConsumed());
assertSame(Event.NULL_SOURCE_TARGET, e.getSource());
assertSame(Event.NULL_SOURCE_TARGET, e.getTarget());
}
@Test public void testLongConstructor() {
Rectangle n1 = new Rectangle(10, 10);
Rectangle n2 = new Rectangle(10, 10);
InputEvent e = new InputEvent(n1, n2, InputEvent.ANY);
assertSame(n1, e.getSource());
assertSame(n2, e.getTarget());
assertSame(InputEvent.ANY, e.getEventType());
assertFalse(e.isConsumed());
}
}
