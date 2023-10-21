package test.javafx.scene;
import com.sun.javafx.scene.SceneHelper;
import test.com.sun.javafx.test.MouseEventGenerator;
import javafx.collections.ObservableMap;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import org.junit.Before;
import org.junit.Test;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.scene.Group;
import javafx.scene.Scene;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
public class AcceleratorsTest {
private Scene scene;
private ObservableMap<KeyCombination, Runnable> accelerators;
@Before
public void setUp() {
scene = new Scene(new Group());
accelerators = scene.getAccelerators();
}
@Test
public void testAcceleratorExecuted() {
AtomicBoolean executed = new AtomicBoolean();
accelerators.put(KeyCombination.keyCombination("Alt + A"), () -> executed.set(true));
SceneHelper.processKeyEvent(scene, new KeyEvent(KeyEvent.KEY_PRESSED, "A", "A", KeyCode.A, false, false, true, false));
assertTrue(executed.get());
}
@Test
public void testAcceleratorRemovedWhenExecuted() {
AtomicBoolean executed = new AtomicBoolean();
final KeyCombination altA = KeyCombination.keyCombination("Alt + A");
final KeyCombination altB = KeyCombination.keyCombination("Alt + B");
accelerators.put(altA, () -> accelerators.remove(altA));
accelerators.put(altB, () -> executed.set(true));
assertEquals(2, accelerators.size());
SceneHelper.processKeyEvent(scene, new KeyEvent(KeyEvent.KEY_PRESSED, "A", "A", KeyCode.A, false, false, true, false));
assertEquals(1, accelerators.size());
assertFalse(executed.get());
SceneHelper.processKeyEvent(scene, new KeyEvent(KeyEvent.KEY_PRESSED, "B", "B", KeyCode.B, false, false, true, false));
assertTrue(executed.get());
}
@Test(expected = ConcurrentModificationException.class)
public void testAcceleratorComodification() {
final KeyCombination altA = KeyCombination.keyCombination("Alt + A");
final KeyCombination altB = KeyCombination.keyCombination("Alt + B");
accelerators.put(altA, () -> {
});
accelerators.put(altB, () -> {
});
final Iterator<Map.Entry<KeyCombination, Runnable>> iterator = accelerators.entrySet().iterator();
iterator.next();
final Iterator<Map.Entry<KeyCombination, Runnable>> iterator1 = accelerators.entrySet().iterator();
iterator1.next();
iterator1.remove();
iterator.next();
}
}
