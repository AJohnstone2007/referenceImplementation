package test.javafx.event;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.event.EventTypeShim;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
public class EventSerializationEventExists {
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
@Ignore
public void testSerializationEventExists() throws IOException, ClassNotFoundException {
EventType ev = null;
try {
ev = new EventType(Event.ANY, "ACTION");
} catch (IllegalArgumentException e) {
fail("Test error: Event type already registered before the test");
}
List<String> l = new ArrayList<String>();
l.add("ACTION");
EventTypeShim.EventTypeSerializationShim e = new EventTypeShim.EventTypeSerializationShim(l);
objectOutputStream.writeObject(e);
turnToInput();
Class.forName("javafx.event.ActionEvent");
EventType eType = (EventType) objectInputStream.readObject();
assertNotSame(ev, eType);
}
}
