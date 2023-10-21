package test.javafx.scene.control;
import static test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils.*;
import test.com.sun.javafx.pgstub.StubToolkit;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.tk.Toolkit;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.shape.Rectangle;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import test.util.memory.JMemoryBuddy;
public class ToggleButtonTest {
private ToggleGroup toggleGroup;
private ToggleButton toggle;
private ToggleButton toggleWithText;
private ToggleButton toggleWithGraphic;
private Node node;
private Toolkit tk;
@Before public void setup() {
tk = (StubToolkit)Toolkit.getToolkit();
node = new Rectangle();
toggleGroup = new ToggleGroup();
toggle = new ToggleButton();
toggleWithText = new ToggleButton("text");
toggleWithGraphic = new ToggleButton("graphic", node);
}
@Test public void defaultConstructorShouldSetStyleClassTo_togglebutton() {
assertStyleClassContains(toggle, "toggle-button");
}
@Test public void defaultOneArgConstructorShouldSetStyleClassTo_togglebutton() {
assertStyleClassContains(toggleWithText, "toggle-button");
}
@Test public void defaultTwoArgConstructorShouldSetStyleClassTo_togglebutton() {
assertStyleClassContains(toggleWithGraphic, "toggle-button");
}
@Test public void defaultConstructorTextGraphicCheck() {
assertEquals(toggle.getText(), "");
assertNull(toggle.getGraphic());
}
@Test public void defaultOneArgConstructorTextGraphicCheck() {
assertEquals(toggleWithText.getText(), "text");
assertNull(toggleWithText.getGraphic());
}
@Test public void defaultTwoArgConstructorTextGraphicCheck() {
assertEquals(toggleWithGraphic.getText(), "graphic");
assertSame(toggleWithGraphic.getGraphic(), node);
}
@Test public void defaultSelected() {
assertFalse(toggle.isSelected());
}
@Test public void defaultAlignment() {
assertSame(toggle.getAlignment(), Pos.CENTER);
}
@Test public void defaultMnemonicParsing() {
assertTrue(toggle.isMnemonicParsing());
}
@Test public void selectedPropertyHasBeanReference() {
assertSame(toggle, toggle.selectedProperty().getBean());
}
@Test public void selectedPropertyHasName() {
assertEquals("selected", toggle.selectedProperty().getName());
}
@Test public void toggleGroupPropertyHasBeanReference() {
assertSame(toggle, toggle.toggleGroupProperty().getBean());
}
@Test public void toggleGroupPropertyHasName() {
assertEquals("toggleGroup", toggle.toggleGroupProperty().getName());
}
@Test public void settingSelectedSetsPseudoClass() {
toggle.setSelected(true);
assertPseudoClassExists(toggle, "selected");
}
@Test public void clearingSelectedClearsPseudoClass() {
toggle.setSelected(true);
toggle.setSelected(false);
assertPseudoClassDoesNotExist(toggle, "selected");
}
@Test public void setToggleGroupAndSeeValueIsReflectedInModel() {
toggle.setToggleGroup(toggleGroup);
assertSame(toggle.toggleGroupProperty().getValue(), toggleGroup);
}
@Test public void setToggleGroupAndSeeValue() {
toggle.setToggleGroup(toggleGroup);
assertSame(toggle.getToggleGroup(), toggleGroup);
}
@Test public void toggleGroupViaGroupAddAndRemoveClearsReference() {
JMemoryBuddy.memoryTest(checker -> {
toggleGroup.getToggles().add(toggle);
toggleGroup.getToggles().clear();
checker.assertCollectable(toggle);
toggle = null;
});
}
@Test public void toggleGroupViaToggleSetClearsReference() {
JMemoryBuddy.memoryTest(checker -> {
toggle.setToggleGroup(toggleGroup);
toggle.setToggleGroup(null);
checker.assertCollectable(toggle);
toggle = null;
});
}
@Test public void toggleGroupViaToggleThenGroupClearsReference() {
JMemoryBuddy.memoryTest(checker -> {
toggle.setToggleGroup(toggleGroup);
toggleGroup.getToggles().clear();
checker.assertCollectable(toggle);
toggle = null;
});
}
@Test public void toggleGroupViaGroupThenToggleClearsReference() {
JMemoryBuddy.memoryTest(checker -> {
toggleGroup.getToggles().add(toggle);
toggle.setToggleGroup(null);
checker.assertCollectable(toggle);
toggle = null;
});
}
@Test public void toggleGroupSwitchingClearsReference() {
JMemoryBuddy.memoryTest(checker -> {
ToggleGroup anotherToggleGroup = new ToggleGroup();
toggle.setToggleGroup(toggleGroup);
toggle.setToggleGroup(anotherToggleGroup);
toggle.setToggleGroup(null);
checker.assertCollectable(toggle);
toggle = null;
});
}
@Test public void setSelectedAndSeeValueIsReflectedInModel() {
toggle.setSelected(true);
assertTrue(toggle.selectedProperty().getValue());
}
@Test public void setSelectedAndSeeValue() {
toggle.setSelected(false);
assertFalse(toggle.isSelected());
}
@Test public void fireAndCheckSelectionToggled() {
toggle.fire();
assertTrue(toggle.isSelected());
toggle.fire();
assertFalse(toggle.isSelected());
}
@Test public void fireAndCheckActionEventFired() {
final Boolean []flag = new Boolean[1];
flag[0] = false;
toggle.addEventHandler(EventType.ROOT, event -> {
if (event != null && event instanceof ActionEvent) {
flag[0] = true;
}
});
toggle.fire();
try {
Thread.sleep(2000);
} catch (InterruptedException ex) {
PlatformLogger.getLogger(ToggleButtonTest.class.getName()).severe(null, ex);
}
assertTrue("fire() doesnt emit ActionEvent!", flag[0]);
}
}
