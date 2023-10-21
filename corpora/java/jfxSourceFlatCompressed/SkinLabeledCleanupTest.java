package test.javafx.scene.control.skin;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static javafx.scene.control.ControlShim.*;
import static org.junit.Assert.*;
import static test.com.sun.javafx.scene.control.infrastructure.ControlSkinFactory.*;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.shape.Rectangle;
@RunWith(Parameterized.class)
public class SkinLabeledCleanupTest {
private Class<Labeled> labeledClass;
private Labeled labeled;
@Test
public void testLabeledGraphicDispose() {
Rectangle graphic = (Rectangle) labeled.getGraphic();
installDefaultSkin(labeled);
labeled.getSkin().dispose();
graphic.setWidth(500);
}
@Test
public void testMemoryLeakAlternativeSkin() {
installDefaultSkin(labeled);
WeakReference<?> weakRef = new WeakReference<>(replaceSkin(labeled));
assertNotNull(weakRef.get());
attemptGC(weakRef);
assertEquals("Skin must be gc'ed", null, weakRef.get());
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
List<Class> labeledClasses = List.of(
Button.class,
CheckBox.class,
Hyperlink.class,
Label.class,
ToggleButton.class,
RadioButton.class,
TitledPane.class
);
return asArrays(labeledClasses);
}
public SkinLabeledCleanupTest(Class<Labeled> labeledClass) {
this.labeledClass = labeledClass;
}
@Test
public void testSetupState() {
assertNotNull(labeled);
assertNotNull(labeled.getGraphic());
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
labeled = createControl(labeledClass);
labeled.setGraphic(new Rectangle());
}
}
