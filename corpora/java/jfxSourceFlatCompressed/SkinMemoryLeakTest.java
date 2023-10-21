package test.javafx.scene.control.skin;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import com.sun.javafx.tk.Toolkit;
import static javafx.scene.control.ControlShim.*;
import static org.junit.Assert.*;
import static test.com.sun.javafx.scene.control.infrastructure.ControlSkinFactory.*;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Pagination;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
@RunWith(Parameterized.class)
public class SkinMemoryLeakTest {
private Class<Control> controlClass;
private Control control;
@Test
public void testMemoryLeakAlternativeSkin() {
installDefaultSkin(control);
WeakReference<?> weakRef = new WeakReference<>(replaceSkin(control));
assertNotNull(weakRef.get());
attemptGC(weakRef);
assertEquals("Skin must be gc'ed", null, weakRef.get());
}
@Test
public void testMemoryLeakAlternativeSkinShowing() {
showControl(control, true);
Skin<?> replacedSkin = replaceSkin(control);
WeakReference<?> weakRef = new WeakReference<>(replacedSkin);
assertNotNull(weakRef.get());
Toolkit.getToolkit().firePulse();
replacedSkin = null;
attemptGC(weakRef);
assertEquals("Skin must be gc'ed", null, weakRef.get());
}
@Test
public void testControlChildren() {
installDefaultSkin(control);
int childCount = control.getChildrenUnmodifiable().size();
String skinClass = control.getSkin().getClass().getSimpleName();
replaceSkin(control);
assertEquals(skinClass + " must remove direct children that it has added",
childCount, control.getChildrenUnmodifiable().size());
}
@Parameterized.Parameters
public static Collection<Object[]> data() {
List<Class<Control>> controlClasses = getControlClasses();
List<Class<? extends Control>> leakingClasses = List.of(
Accordion.class,
ButtonBar.class,
ColorPicker.class,
ComboBox.class,
DatePicker.class,
MenuBar.class,
MenuButton.class,
Pagination.class,
PasswordField.class,
ScrollBar.class,
ScrollPane.class,
Spinner.class,
SplitMenuButton.class,
SplitPane.class,
TableView.class,
TreeTableView.class
);
controlClasses.removeAll(leakingClasses);
return asArrays(controlClasses);
}
public SkinMemoryLeakTest(Class<Control> controlClass) {
this.controlClass = controlClass;
}
private Scene scene;
private Stage stage;
private Pane root;
protected void showControl(Control control, boolean focused) {
if (root == null) {
root = new VBox();
scene = new Scene(root);
stage = new Stage();
stage.setScene(scene);
}
if (!root.getChildren().contains(control)) {
root.getChildren().add(control);
}
stage.show();
if (focused) {
stage.requestFocus();
control.requestFocus();
assertTrue(control.isFocused());
assertSame(control, scene.getFocusOwner());
}
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
this.control = createControl(controlClass);
assertNotNull(control);
}
@After
public void cleanup() {
if (stage != null) stage.hide();
Thread.currentThread().setUncaughtExceptionHandler(null);
}
}
