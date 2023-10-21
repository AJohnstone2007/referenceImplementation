package test.javafx.event;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import org.junit.Test;
public class EventTest {
@Test(expected=IllegalArgumentException.class)
public void testTypeConflict() {
new EventType(Event.ANY, ActionEvent.ACTION.getName());
}
@Test
public void testTypeNew() {
new EventType(Event.ANY, "TEST");
}
}
