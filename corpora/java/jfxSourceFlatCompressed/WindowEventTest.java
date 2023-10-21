package test.javafx.stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.stage.WindowShim;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import org.junit.Test;
public class WindowEventTest {
@Test public void testConstructor() {
Window w = new WindowShim();
WindowEvent e = new WindowEvent(w, WindowEvent.WINDOW_HIDING);
assertSame(WindowEvent.WINDOW_HIDING, e.getEventType());
assertFalse(e.isConsumed());
assertSame(w, e.getSource());
assertSame(w, e.getTarget());
}
}
