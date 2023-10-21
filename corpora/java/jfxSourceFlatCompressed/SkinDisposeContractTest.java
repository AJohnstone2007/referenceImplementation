package test.javafx.scene.control.skin;
import java.util.Collection;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static javafx.scene.control.ControlShim.*;
import static test.com.sun.javafx.scene.control.infrastructure.ControlSkinFactory.*;
import javafx.scene.control.Control;
@RunWith(Parameterized.class)
public class SkinDisposeContractTest {
private Control control;
private Class<Control> controlClass;
@Test
public void testDefaultDispose() {
installDefaultSkin(control);
control.getSkin().dispose();
control.getSkin().dispose();
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
List<Class<Control>> controlClasses = getControlClasses();
return asArrays(controlClasses);
}
public SkinDisposeContractTest(Class<Control> controlClass) {
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
}
}
