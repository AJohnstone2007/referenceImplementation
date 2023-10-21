package test.javafx.stage;
import com.sun.javafx.stage.WindowHelper;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertNotNull;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.com.sun.javafx.pgstub.StubStage;
import test.com.sun.javafx.pgstub.StubToolkit;
import com.sun.javafx.tk.TKStage;
import com.sun.javafx.tk.Toolkit;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.util.concurrent.atomic.AtomicInteger;
public final class WindowTest {
private StubToolkit toolkit;
private Stage testWindow;
@Before
public void setUp() {
toolkit = (StubToolkit) Toolkit.getToolkit();
testWindow = new Stage();
}
@After
public void afterTest() {
testWindow.hide();
testWindow = null;
}
@Test
public void testOpacityBind() {
final DoubleProperty variable = new SimpleDoubleProperty(0.5);
testWindow.show();
final StubStage peer = getPeer(testWindow);
testWindow.opacityProperty().bind(variable);
toolkit.fireTestPulse();
assertEquals(0.5f, peer.opacity);
variable.set(1.0f);
toolkit.fireTestPulse();
assertEquals(1.0f, peer.opacity);
}
@Test public void testProperties() {
javafx.collections.ObservableMap<Object, Object> properties = testWindow.getProperties();
assertNotNull(properties);
properties.put("MyKey", "MyValue");
assertEquals("MyValue", properties.get("MyKey"));
javafx.collections.ObservableMap<Object, Object> properties2 = testWindow.getProperties();
assertEquals(properties2, properties);
assertEquals("MyValue", properties2.get("MyKey"));
}
private static StubStage getPeer(final Window window) {
final TKStage unkPeer = WindowHelper.getPeer(window);
assertTrue(unkPeer instanceof StubStage);
return (StubStage) unkPeer;
}
@Test public void testGetWindowsIsObservable() {
ObservableList<Window> windows = Window.getWindows();
final int initialWindowCount = windows.size();
AtomicInteger windowCount = new AtomicInteger(initialWindowCount);
InvalidationListener listener = o -> windowCount.set(windows.size());
windows.addListener(listener);
assertEquals(initialWindowCount + 0, windowCount.get());
testWindow.show();
assertEquals(initialWindowCount + 1, windowCount.get());
Stage anotherTestWindow = new Stage();
anotherTestWindow.show();
assertEquals(initialWindowCount + 2, windowCount.get());
testWindow.hide();
assertEquals(initialWindowCount + 1, windowCount.get());
anotherTestWindow.hide();
assertEquals(initialWindowCount + 0, windowCount.get());
windows.removeListener(listener);
}
@Test(expected = UnsupportedOperationException.class)
public void testGetWindowsIsUnmodifiable_add() {
Stage anotherTestWindow = new Stage();
Window.getWindows().add(anotherTestWindow);
}
@Test(expected = UnsupportedOperationException.class)
public void testGetWindowsIsUnmodifiable_removeShowingWindow() {
testWindow.show();
Window.getWindows().remove(testWindow);
}
@Test public void testGetWindowsIsUnmodifiable_removeNonShowingWindow_emptyList() {
Stage anotherTestWindow = new Stage();
Window.getWindows().remove(anotherTestWindow);
}
@Test public void testGetWindowsIsUnmodifiable_removeNonShowingWindow_nonEmptyList() {
ObservableList<Window> windows = Window.getWindows();
final int initialWindowCount = windows.size();
testWindow.show();
assertEquals(initialWindowCount + 1, windows.size());
Stage anotherTestWindow = new Stage();
assertEquals(initialWindowCount + 1, windows.size());
windows.remove(anotherTestWindow);
assertEquals(initialWindowCount + 1, windows.size());
}
}
