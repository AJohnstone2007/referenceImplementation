package test.javafx.event;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.event.EventTypeShim;
import javafx.event.EventTypeShim.EventTypeSerializationShim;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class EventSerializationTest {
private ByteArrayOutputStream byteArrayOutputStream;
private ObjectOutputStream objectOutputStream;
private ObjectInputStream objectInputStream;
@Before
public void setUp() throws IOException {
byteArrayOutputStream = new ByteArrayOutputStream();
objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
}
public void turnToInput() throws IOException {
objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
}
@Test
public void testPreDefinedEventSerialization() throws IOException, ClassNotFoundException {
ActionEvent a = new ActionEvent();
objectOutputStream.writeObject(a);
turnToInput();
ActionEvent ra = (ActionEvent) objectInputStream.readObject();
assertEquals(a.getEventType(), ra.getEventType());
assertEquals(a.isConsumed(), ra.isConsumed());
assertEquals(Event.NULL_SOURCE_TARGET, ra.getSource());
assertEquals(Event.NULL_SOURCE_TARGET, ra.getTarget());
}
@Test
public void testNewEventTypeSerialization() throws IOException, ClassNotFoundException {
EventType<Event> eventType = new EventType<Event>(Event.ANY, "MY_TYPE");
Event e = new Event(eventType);
objectOutputStream.writeObject(e);
turnToInput();
Event re = (Event) objectInputStream.readObject();
assertEquals(e.getEventType(), re.getEventType());
assertEquals(e.isConsumed(), re.isConsumed());
assertEquals(Event.NULL_SOURCE_TARGET, re.getSource());
assertEquals(Event.NULL_SOURCE_TARGET, re.getTarget());
}
@Test(expected=InvalidObjectException.class)
public void testUnknownEventTypeSerialization() throws IOException, ClassNotFoundException {
List<String> l = new ArrayList<String>();
l.add("UNKNOWN");
Object e = EventTypeShim.getEventTypeSerialization(l);
objectOutputStream.writeObject(e);
turnToInput();
EventType eType = (EventType) objectInputStream.readObject();
}
}
