package test.javafx.scene.control;
import java.util.Arrays;
import java.util.Collection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.*;
import com.sun.javafx.tk.Toolkit;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.skin.ToolBarSkin;
import javafx.scene.input.KeyCode;
import test.com.sun.javafx.pgstub.StubToolkit;
import test.com.sun.javafx.scene.control.infrastructure.KeyEventFirer;
import test.com.sun.javafx.scene.control.infrastructure.KeyModifier;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;
@RunWith(Parameterized.class)
public class ToolBarHorizontalArrowsTest {
@Parameterized.Parameters
public static Collection<?> implementations() {
return Arrays.asList(new Object[][] {
{NodeOrientation.LEFT_TO_RIGHT},
{NodeOrientation.RIGHT_TO_LEFT}
});
}
private ToolBar toolBar;
private Button btn1;
private Button btn2;
private Button btn3;
private Button btn4;
private Button btn5;
private KeyEventFirer keyboard;
private StageLoader stageLoader;
private NodeOrientation orientation;
public ToolBarHorizontalArrowsTest(NodeOrientation val) {
orientation = val;
}
@Before public void setup() {
toolBar = new ToolBar();
toolBar.setNodeOrientation(orientation);
btn1 = new Button("Btn1");
btn2 = new Button("Btn2");
btn3 = new Button("Btn3");
btn4 = new Button("Btn4");
btn5 = new Button("Btn5");
toolBar.getItems().addAll(btn1, btn2, btn3, btn4, btn5);
ToolBarSkin toolbarSkin = new ToolBarSkin(toolBar);
toolBar.setSkin(toolbarSkin);
stageLoader = new StageLoader(toolBar);
stageLoader.getStage().show();
((StubToolkit)Toolkit.getToolkit()).firePulse();
toolBar.setFocusTraversable(true);
keyboard = new KeyEventFirer(toolBar, toolBar.getScene());
}
@After
public void tearDown() {
toolBar.getSkin().dispose();
stageLoader.dispose();
}
private void toggleNodeOrientation() {
orientation = (orientation == NodeOrientation.LEFT_TO_RIGHT?
NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
toolBar.setNodeOrientation(orientation);
}
private void forward(KeyModifier... modifiers) {
if (orientation == NodeOrientation.LEFT_TO_RIGHT) {
keyboard.doRightArrowPress(modifiers);
} else if (orientation == NodeOrientation.RIGHT_TO_LEFT) {
keyboard.doLeftArrowPress(modifiers);
}
}
private void backward(KeyModifier... modifiers) {
if (orientation == NodeOrientation.LEFT_TO_RIGHT) {
keyboard.doLeftArrowPress(modifiers);
} else if (orientation == NodeOrientation.RIGHT_TO_LEFT) {
keyboard.doRightArrowPress(modifiers);
}
}
@Test
public void testForwardFocus() {
assertTrue(toolBar.isFocusTraversable());
toolBar.getScene().getWindow().requestFocus();
assertTrue(btn1.isFocused());
keyboard.doKeyPress(KeyCode.TAB);
assertTrue(btn2.isFocused());
keyboard.doKeyPress(KeyCode.TAB);
assertTrue(btn3.isFocused());
keyboard.doKeyPress(KeyCode.TAB);
assertTrue(btn4.isFocused());
keyboard.doKeyPress(KeyCode.TAB);
assertTrue(btn5.isFocused());
}
@Test
public void testBackwardFocus() {
assertTrue(toolBar.isFocusTraversable());
toolBar.getScene().getWindow().requestFocus();
btn5.requestFocus();
assertTrue(btn5.isFocused());
keyboard.doKeyPress(KeyCode.TAB, KeyModifier.SHIFT);
assertTrue(btn4.isFocused());
keyboard.doKeyPress(KeyCode.TAB, KeyModifier.SHIFT);
assertTrue(btn3.isFocused());
keyboard.doKeyPress(KeyCode.TAB, KeyModifier.SHIFT);
assertTrue(btn2.isFocused());
keyboard.doKeyPress(KeyCode.TAB, KeyModifier.SHIFT);
assertTrue(btn1.isFocused());
}
@Test
public void testForwardFocusArrows() {
assertTrue(toolBar.isFocusTraversable());
toolBar.getScene().getWindow().requestFocus();
assertTrue(btn1.isFocused());
forward();
assertTrue(btn2.isFocused());
forward();
assertTrue(btn3.isFocused());
forward();
assertTrue(btn4.isFocused());
forward();
assertTrue(btn5.isFocused());
}
@Test
public void testBackwardFocusArrows() {
assertTrue(toolBar.isFocusTraversable());
toolBar.getScene().getWindow().requestFocus();
btn5.requestFocus();
assertTrue(btn5.isFocused());
backward();
assertTrue(btn4.isFocused());
backward();
assertTrue(btn3.isFocused());
backward();
assertTrue(btn2.isFocused());
backward();
assertTrue(btn1.isFocused());
}
@Test
public void testForwardFocusArrows_toggleOrientation() {
assertTrue(toolBar.isFocusTraversable());
toolBar.getScene().getWindow().requestFocus();
assertTrue(btn1.isFocused());
forward();
assertTrue(btn2.isFocused());
toggleNodeOrientation();
forward();
assertTrue(btn3.isFocused());
forward();
assertTrue(btn4.isFocused());
toggleNodeOrientation();
forward();
assertTrue(btn5.isFocused());
}
@Test
public void testBackwardFocusArrows_toggleOrientation() {
assertTrue(toolBar.isFocusTraversable());
toolBar.getScene().getWindow().requestFocus();
btn5.requestFocus();
assertTrue(btn5.isFocused());
backward();
assertTrue(btn4.isFocused());
toggleNodeOrientation();
backward();
assertTrue(btn3.isFocused());
backward();
assertTrue(btn2.isFocused());
toggleNodeOrientation();
backward();
assertTrue(btn1.isFocused());
}
@Test
public void testMixedFocusArrows_toggleOrientation() {
assertTrue(toolBar.isFocusTraversable());
toolBar.getScene().getWindow().requestFocus();
assertTrue(btn1.isFocused());
forward();
assertTrue(btn2.isFocused());
toggleNodeOrientation();
forward();
assertTrue(btn3.isFocused());
backward();
assertTrue(btn2.isFocused());
toggleNodeOrientation();
backward();
assertTrue(btn1.isFocused());
}
@Test
public void testFocusExtremeNodesOfToolBar() {
assertTrue(toolBar.isFocusTraversable());
toolBar.getScene().getWindow().requestFocus();
assertTrue(btn1.isFocused());
backward();
assertTrue(btn1.isFocused());
btn5.requestFocus();
assertTrue(btn5.isFocused());
forward();
assertTrue(btn5.isFocused());
}
}
