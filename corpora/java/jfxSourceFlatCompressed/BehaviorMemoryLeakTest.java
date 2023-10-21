package test.com.sun.javafx.scene.control.behavior;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import static org.junit.Assert.*;
import static test.com.sun.javafx.scene.control.infrastructure.ControlSkinFactory.*;
import javafx.scene.control.Control;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
@RunWith(Parameterized.class)
public class BehaviorMemoryLeakTest {
private Class<Control> controlClass;
private Control control;
@Test
public void testMemoryLeakDisposeBehavior() {
WeakReference<BehaviorBase<?>> weakRef = new WeakReference<>(createBehavior(control));
assertNotNull(weakRef.get());
weakRef.get().dispose();
attemptGC(weakRef);
assertNull("behavior must be gc'ed", weakRef.get());
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
List<Class<Control>> controlClasses = getControlClassesWithBehavior();
List<Class<? extends Control>> leakingClasses = List.of(
PasswordField.class,
TableView.class,
TreeTableView.class
);
controlClasses.removeAll(leakingClasses);
return asArrays(controlClasses);
}
public BehaviorMemoryLeakTest(Class<Control> controlClass) {
this.controlClass = controlClass;
}
@After
public void cleanup() {
Thread.currentThread().setUncaughtExceptionHandler(null);
}
@Before
public void setup() {
Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
if (throwable instanceof RuntimeException) {
throw (RuntimeException)throwable;
} else {
Thread.currentThread().getThreadGroup().uncaughtException(thread, throwable);
}
});
control = createControl(controlClass);
assertNotNull(control);
}
}
