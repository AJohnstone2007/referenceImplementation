package test.com.sun.javafx.scene.control.infrastructure;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyEvent.*;
import static org.junit.Assert.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class KeyEventFirerTest {
private TextField textField;
private Button button;
private Pane root;
private Stage stage;
private Scene scene;
@Test
public void testFireViaScene() {
showAndFocus(button);
List<KeyEvent> buttonEvents = new ArrayList<>();
button.addEventHandler(KEY_PRESSED, buttonEvents::add);
List<KeyEvent> textFieldEvents = new ArrayList<>();
textField.addEventHandler(KEY_PRESSED, textFieldEvents::add);
KeyEventFirer firer = new KeyEventFirer(textField, scene);
firer.doKeyPress(A);
assertEquals("button must have received the key", 1, buttonEvents.size());
assertEquals("textField must not have received the key", 0, textFieldEvents.size());
}
@Test
public void testFireViaSceneNullTarget() {
showAndFocus(button);
List<KeyEvent> buttonEvents = new ArrayList<>();
button.addEventHandler(KEY_PRESSED, buttonEvents::add);
List<KeyEvent> textFieldEvents = new ArrayList<>();
textField.addEventHandler(KEY_PRESSED, textFieldEvents::add);
KeyEventFirer firer = new KeyEventFirer(null, scene);
firer.doKeyPress(A);
assertEquals("button must have received the key", 1, buttonEvents.size());
assertEquals("textField must not have received the key", 0, textFieldEvents.size());
}
@Test
public void testFireTargetFalseGreen() {
showAndFocus(button);
List<KeyEvent> buttonEvents = new ArrayList<>();
button.addEventHandler(KEY_PRESSED, buttonEvents::add);
List<KeyEvent> textFieldEvents = new ArrayList<>();
textField.addEventHandler(KEY_PRESSED, textFieldEvents::add);
KeyEventFirer incorrectFirer = new KeyEventFirer(textField);
incorrectFirer.doKeyPress(A);
int falseTextFieldNotification = textFieldEvents.size();
int falseButtonNotification = buttonEvents.size();
assertEquals("false green - textField must have received the key", 1, textFieldEvents.size());
assertEquals("false green - button must not have received the key", 0, buttonEvents.size());
textFieldEvents.clear();
buttonEvents.clear();
KeyEventFirer correctFirer = new KeyEventFirer(null, scene);
correctFirer.doKeyPress(A);
assertEquals(falseTextFieldNotification - 1, textFieldEvents.size());
assertEquals(falseButtonNotification + 1, buttonEvents.size());
}
@Test (expected= NullPointerException.class)
public void testTwoParamConstructorNPE() {
new KeyEventFirer(null, null);
}
@Test (expected= NullPointerException.class)
public void testSingleParamConstructorNPE() {
new KeyEventFirer(null);
}
@Test
public void testUIState() {
assertEquals(List.of(button, textField), root.getChildren());
stage.show();
stage.requestFocus();
button.requestFocus();
assertEquals(button, scene.getFocusOwner());
assertTrue(button.isFocused());
}
private void showAndFocus(Node focused) {
stage.show();
stage.requestFocus();
if (focused != null) {
focused.requestFocus();
assertTrue(focused.isFocused());
assertSame(focused, scene.getFocusOwner());
}
}
@Before
public void setup() {
root = new VBox();
scene = new Scene(root);
stage = new Stage();
stage.setScene(scene);
button = new Button("I'm a button");
textField = new TextField("some text");
root.getChildren().addAll(button, textField);
}
@After
public void cleanup() {
if (stage != null) {
stage.hide();
}
}
}
