package test.javafx.scene.control;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import com.sun.javafx.tk.Toolkit;
import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyEvent.*;
import static org.junit.Assert.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import test.com.sun.javafx.pgstub.StubToolkit;
import test.com.sun.javafx.scene.control.infrastructure.KeyEventFirer;
@RunWith(Parameterized.class)
public abstract class DefaultCancelButtonTestBase<C extends Control> {
public static enum ButtonState {
DEFAULT(ENTER),
CANCEL(ESCAPE);
KeyCode key;
ButtonState(KeyCode key) {
this.key = key;
}
public KeyCode getCode() {
return key;
}
public EventHandler<KeyEvent> getConsumingHandler() {
return e -> {
if (getCode() == e.getCode()) e.consume();
};
}
public void configureButton(Button button) {
if (getCode() == ENTER) {
button.setDefaultButton(true);
} else if (getCode() == ESCAPE) {
button.setCancelButton(true);
}
}
}
public static class ButtonType {
Button button;
ButtonState type;
public ButtonType(ButtonState type) {
this.type = type;
button = new Button();
type.configureButton(button);
}
public Button getButton() {
return button;
}
public KeyCode getCode() {
return type.getCode();
}
public EventHandler<KeyEvent> getKeyHandler(boolean consuming) {
return consuming ? type.getConsumingHandler() : e -> {};
}
@Override
public String toString() {
return "" + type;
}
}
private Stage stage;
private VBox root;
private C control;
private Button fallback;
private Scene scene;
private ButtonType buttonType;
private boolean consume;
private boolean registerAfterShowing;
@Parameterized.Parameters
public static Collection<Object[]> data() {
Object[][] data = new Object[][] {
{new ButtonType(ButtonState.DEFAULT), true, true},
{new ButtonType(ButtonState.DEFAULT), true, false},
{new ButtonType(ButtonState.DEFAULT), false, true},
{new ButtonType(ButtonState.DEFAULT), false, false},
{new ButtonType(ButtonState.CANCEL), true, true},
{new ButtonType(ButtonState.CANCEL), true, false},
{new ButtonType(ButtonState.CANCEL), false, true},
{new ButtonType(ButtonState.CANCEL), false, false},
};
return Arrays.asList(data);
}
public DefaultCancelButtonTestBase(ButtonType buttonType, boolean consume,
boolean registerAfterShowing) {
this.buttonType = buttonType;
this.consume = consume;
this.registerAfterShowing = registerAfterShowing;
}
@Test
public void testFallbackFilter() {
registerHandlerAndAssertFallbackNotification(this::addEventFilter);
}
@Test
public void testFallbackHandler() {
registerHandlerAndAssertFallbackNotification(this::addEventHandler);
}
@Test
public void testFallbackSingletonHandler() {
registerHandlerAndAssertFallbackNotification(this::setOnKeyPressed);
}
@Test
public void testFallbackNoHandler() {
if (consume) return;
show();
assertTargetNotification(buttonType.getCode(), buttonType.getButton(), 1);
}
protected void registerHandlerAndAssertFallbackNotification(Consumer<EventHandler<KeyEvent>> consumer) {
if (registerAfterShowing) {
show();
}
consumer.accept(buttonType.getKeyHandler(consume));
if (!registerAfterShowing) {
show();
}
int expected = consume ? 0 : 1;
assertTargetNotification(buttonType.getCode(), buttonType.getButton(), expected);
}
protected void addEventHandler(EventHandler<KeyEvent> handler) {
control.addEventHandler(KEY_PRESSED, handler);
}
protected void setOnKeyPressed(EventHandler<KeyEvent> handler) {
control.setOnKeyPressed(handler);
}
protected void addEventFilter(EventHandler<KeyEvent> filter) {
control.addEventFilter(KEY_PRESSED, filter);
}
protected void assertTargetNotification(KeyCode key, Button target, int expected) {
List<ActionEvent> actions = new ArrayList<>();
target.setOnAction(actions::add);
KeyEventFirer keyFirer = new KeyEventFirer(control);
keyFirer.doKeyPress(key);
String exp = expected > 0 ? " must " : " must not ";
assertEquals(key + exp + " trigger ", expected, actions.size());
}
@Test
public void testInitial() {
show();
assertTrue(control.isFocused());
assertSame(root, control.getParent());
assertSame(root, fallback.getParent());
}
protected boolean isEnter() {
return buttonType.getCode() == ENTER;
}
protected abstract C createControl();
protected C getControl() {
return control;
};
protected void show() {
stage.show();
stage.requestFocus();
control.requestFocus();
}
private void initStage() {
@SuppressWarnings("unused")
Toolkit tk = (StubToolkit)Toolkit.getToolkit();
root = new VBox();
scene = new Scene(root);
stage = new Stage();
stage.setScene(scene);
}
@Before
public void setup() {
initStage();
control = createControl();
fallback = buttonType.getButton();
root.getChildren().addAll(control, fallback);
}
@After
public void cleanup() {
if (stage != null) {
stage.hide();
}
}
}
