package test.javafx.scene.control;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.ToggleButton;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.assertTrue;
@RunWith(Parameterized.class)
public class FireButtonBaseTest {
@SuppressWarnings("rawtypes")
@Parameterized.Parameters public static Collection implementations() {
return Arrays.asList(new Object[][]{
{Button.class},
{CheckBox.class},
{Hyperlink.class},
{RadioButton.class},
{MenuButton.class},
{SplitMenuButton.class},
{ToggleButton.class}
});
}
private ButtonBase btn;
private Class type;
public FireButtonBaseTest(Class type) {
this.type = type;
}
@Before public void setup() throws Exception {
btn = (ButtonBase) type.getDeclaredConstructor().newInstance();
}
@Test public void onActionCalledWhenButtonIsFired() {
final EventHandlerStub handler = new EventHandlerStub();
btn.setOnAction(handler);
btn.fire();
assertTrue(handler.called);
}
@Test public void onActionCalledWhenNullWhenButtonIsFiredIsNoOp() {
btn.fire();
}
public static final class EventHandlerStub implements EventHandler<ActionEvent> {
boolean called = false;
@Override public void handle(ActionEvent event) {
called = true;
}
};
}
