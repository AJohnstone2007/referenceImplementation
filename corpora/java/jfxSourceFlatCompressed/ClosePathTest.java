package test.javafx.scene.shape;
import com.sun.javafx.sg.prism.NGPath;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.ClosePathShim;
import org.junit.Test;
import static org.junit.Assert.*;
public class ClosePathTest {
@Test public void testAddTo() throws Exception {
final StubPathImpl pgPath = new StubPathImpl();
ClosePath closePath = new ClosePath();
ClosePathShim.addTo(closePath, pgPath);
assertTrue(pgPath.isClosed());
}
@Test public void toStringShouldReturnNonEmptyString() {
String s = new ClosePath().toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
private class StubPathImpl extends NGPath {
boolean closed = false;
@Override public void addClosePath() {
closed = true;
}
public boolean isClosed() {
return closed;
}
}
}
