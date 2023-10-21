package test.javafx.scene.control;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.util.List;
import test.com.sun.javafx.pgstub.StubToolkit;
import test.com.sun.javafx.scene.control.infrastructure.ContextMenuEventFirer;
import test.com.sun.javafx.scene.control.infrastructure.KeyEventFirer;
import test.com.sun.javafx.scene.control.infrastructure.MouseEventFirer;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Skin;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils.assertPseudoClassDoesNotExist;
import static test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils.assertPseudoClassExists;
import static test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils.assertStyleClassContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
public class ButtonTest {
private Button btn;
private Toolkit tk;
private Scene scene;
private Stage stage;
private StackPane root;
private MouseEventFirer mouse;
@Before public void setup() {
btn = new Button();
tk = (StubToolkit)Toolkit.getToolkit();
root = new StackPane();
scene = new Scene(root);
stage = new Stage();
stage.setScene(scene);
}
@After public void after() {
stage.hide();
if (mouse != null) {
mouse.dispose();
}
}
private void show() {
stage.show();
}
@Test public void defaultConstructorShouldHaveNoGraphicAndEmptyString() {
assertNull(btn.getGraphic());
assertEquals("", btn.getText());
}
@Test public void oneArgConstructorShouldHaveNoGraphicAndSpecifiedString() {
Button b2 = new Button(null);
assertNull(b2.getGraphic());
assertNull(b2.getText());
b2 = new Button("");
assertNull(b2.getGraphic());
assertEquals("", b2.getText());
b2 = new Button("Hello");
assertNull(b2.getGraphic());
assertEquals("Hello", b2.getText());
}
@Test public void twoArgConstructorShouldHaveSpecifiedGraphicAndSpecifiedString() {
Button b2 = new Button(null, null);
assertNull(b2.getGraphic());
assertNull(b2.getText());
Rectangle rect = new Rectangle();
b2 = new Button("Hello", rect);
assertSame(rect, b2.getGraphic());
assertEquals("Hello", b2.getText());
}
@Test public void defaultConstructorShouldSetStyleClassTo_button() {
assertStyleClassContains(btn, "button");
}
@Test public void oneArgConstructorShouldSetStyleClassTo_button() {
Button b2 = new Button(null);
assertStyleClassContains(b2, "button");
}
@Test public void twoArgConstructorShouldSetStyleClassTo_button() {
Button b2 = new Button(null, null);
assertStyleClassContains(b2, "button");
}
@Test public void defaultButtonIsFalseByDefault() {
assertFalse(btn.isDefaultButton());
assertFalse(btn.defaultButtonProperty().getValue());
}
@Test public void defaultButtonCanBeSet() {
btn.setDefaultButton(true);
assertTrue(btn.isDefaultButton());
}
@Test public void defaultButtonSetToNonDefaultValueIsReflectedInModel() {
btn.setDefaultButton(true);
assertTrue(btn.defaultButtonProperty().getValue());
}
@Test public void defaultButtonCanBeCleared() {
btn.setDefaultButton(true);
btn.setDefaultButton(false);
assertFalse(btn.isDefaultButton());
}
@Test public void defaultButtonCanBeBound() {
BooleanProperty other = new SimpleBooleanProperty(true);
btn.defaultButtonProperty().bind(other);
assertTrue(btn.isDefaultButton());
}
@Test public void settingDefaultButtonSetsPseudoClass() {
btn.setDefaultButton(true);
assertPseudoClassExists(btn, "default");
}
@Test public void clearingDefaultButtonClearsPseudoClass() {
btn.setDefaultButton(true);
btn.setDefaultButton(false);
assertPseudoClassDoesNotExist(btn, "default");
}
@Test public void defaultButtonSetToTrueViaBindingSetsPseudoClass() {
BooleanProperty other = new SimpleBooleanProperty(true);
btn.defaultButtonProperty().bind(other);
assertPseudoClassExists(btn, "default");
}
@Test public void defaultButtonSetToFalseViaBindingClearsPseudoClass() {
BooleanProperty other = new SimpleBooleanProperty(true);
btn.defaultButtonProperty().bind(other);
other.setValue(false);
assertPseudoClassDoesNotExist(btn, "default");
}
@Test public void cannotSpecifyDefaultButtonViaCSS() {
btn.setStyle("-fx-default-button: true;");
btn.applyCss();
assertFalse(btn.isDefaultButton());
btn.setDefaultButton(true);
assertTrue(btn.isDefaultButton());
btn.setStyle("-fx-default-button: false;");
btn.applyCss();
assertTrue(btn.isDefaultButton());
}
@Test public void defaultButtonPropertyHasBeanReference() {
assertSame(btn, btn.defaultButtonProperty().getBean());
}
@Test public void defaultButtonPropertyHasName() {
assertEquals("defaultButton", btn.defaultButtonProperty().getName());
}
@Test public void disabledDefaultButtonCannotGetInvoked_RT20929() {
root.getChildren().add(btn);
btn.setOnAction(actionEvent -> {
fail();
});
btn.setDefaultButton(true);
btn.setDisable(true);
show();
KeyEventFirer keyboard = new KeyEventFirer(btn);
keyboard.doKeyPress(KeyCode.ENTER);
tk.firePulse();
}
@Test public void defaultButtonCanBeInvokeAfterRemovingFromTheScene_RT22106() {
btn.setDefaultButton(true);
btn.setOnAction(actionEvent -> {
fail();
});
root.getChildren().add(btn);
show();
root.getChildren().remove(btn);
KeyEventFirer keyboard = new KeyEventFirer(root);
keyboard.doKeyPress(KeyCode.ENTER);
tk.firePulse();
}
@Test public void defaultButtonSceneAccelerators() {
assertEquals("Scene.getAccelerators() should contain no accelerators.",
0, scene.getAccelerators().size());
HBox btnParent = new HBox();
btnParent.getChildren().add(btn);
root.getChildren().add(btnParent);
btn.setDefaultButton(true);
show();
assertEquals("Scene.getAccelerators() should contain one accelerator" +
" for Default button.", 1, scene.getAccelerators().size());
root.getChildren().remove(btnParent);
assertEquals("Default button accelerator should be removed from" +
" Scene.getAccelerators().", 0, scene.getAccelerators().size());
tk.firePulse();
}
@Test public void cancelButtonIsFalseByDefault() {
assertFalse(btn.isCancelButton());
assertFalse(btn.cancelButtonProperty().getValue());
}
@Test public void cancelButtonCanBeSet() {
btn.setCancelButton(true);
assertTrue(btn.isCancelButton());
}
@Test public void cancelButtonSetToNonDefaultValueIsReflectedInModel() {
btn.setCancelButton(true);
assertTrue(btn.cancelButtonProperty().getValue());
}
@Test public void cancelButtonCanBeCleared() {
btn.setCancelButton(true);
btn.setCancelButton(false);
assertFalse(btn.isCancelButton());
}
@Test public void cancelButtonCanBeBound() {
BooleanProperty other = new SimpleBooleanProperty(true);
btn.cancelButtonProperty().bind(other);
assertTrue(btn.isCancelButton());
}
@Test public void settingCancelButtonSetsPseudoClass() {
btn.setCancelButton(true);
assertPseudoClassExists(btn, "cancel");
}
@Test public void clearingCancelButtonClearsPseudoClass() {
btn.setCancelButton(true);
btn.setCancelButton(false);
assertPseudoClassDoesNotExist(btn, "cancel");
}
@Test public void cancelButtonSetToTrueViaBindingSetsPseudoClass() {
BooleanProperty other = new SimpleBooleanProperty(true);
btn.cancelButtonProperty().bind(other);
assertPseudoClassExists(btn, "cancel");
}
@Test public void cancelButtonSetToFalseViaBindingClearsPseudoClass() {
BooleanProperty other = new SimpleBooleanProperty(true);
btn.cancelButtonProperty().bind(other);
other.setValue(false);
assertPseudoClassDoesNotExist(btn, "cancel");
}
@Test public void cancelButtonSceneAccelerators() {
assertEquals("Scene.getAccelerators() should contain no accelerators.",
0, scene.getAccelerators().size());
HBox btnParent = new HBox();
btnParent.getChildren().add(btn);
root.getChildren().add(btnParent);
btn.setCancelButton(true);
show();
assertEquals("Scene.getAccelerators() should contain one accelerator" +
" for Cancel button.", 1, scene.getAccelerators().size());
root.getChildren().remove(btnParent);
assertEquals("Cancel button accelerator should be removed from" +
" Scene.getAccelerators().", 0, scene.getAccelerators().size());
tk.firePulse();
}
@Test public void cannotSpecifyCancelButtonViaCSS() {
btn.setStyle("-fx-cancel-button: true;");
btn.applyCss();
assertFalse(btn.isCancelButton());
btn.setCancelButton(true);
assertTrue(btn.isCancelButton());
btn.setStyle("-fx-cancel-button: false;");
btn.applyCss();
assertTrue(btn.isCancelButton());
}
@Test public void cancelButtonPropertyHasBeanReference() {
assertSame(btn, btn.cancelButtonProperty().getBean());
}
@Test public void cancelButtonPropertyHasName() {
assertEquals("cancelButton", btn.cancelButtonProperty().getName());
}
@Test public void cancelButtonCanBeInvokeAfterRemovingFromTheScene_RT22106() {
btn.setCancelButton(true);
btn.setOnAction(actionEvent -> {
fail();
});
root.getChildren().add(btn);
show();
root.getChildren().remove(btn);
KeyEventFirer keyboard = new KeyEventFirer(root);
keyboard.doKeyPress(KeyCode.ESCAPE);
tk.firePulse();
}
@Test public void conextMenuShouldntShowOnAction() {
ContextMenu popupMenu = new ContextMenu();
MenuItem item1 = new MenuItem("_About");
popupMenu.getItems().add(item1);
popupMenu.setOnShown(w -> {
fail();
});
btn.setContextMenu(popupMenu);
btn.setDefaultButton(true);
root.getChildren().add(btn);
show();
KeyEventFirer keyboard = new KeyEventFirer(btn);
keyboard.doKeyPress(KeyCode.ENTER);
btn.fireEvent(new ActionEvent());
btn.fire();
mouse = new MouseEventFirer(btn);
mouse.fireMousePressed();
mouse.fireMouseReleased();
mouse.fireMouseClicked();
}
private int count = 0;
@Test public void contextMenuShouldShowOnInSomeCircumstances() {
ContextMenu popupMenu = new ContextMenu();
MenuItem item1 = new MenuItem("_About");
popupMenu.getItems().add(item1);
popupMenu.setOnShown(w -> {
count++;
});
btn.setContextMenu(popupMenu);
btn.setDefaultButton(true);
root.getChildren().add(btn);
show();
btn.setOnAction(event -> {
fail();
});
assertEquals(0, count);
mouse = new MouseEventFirer(btn);
mouse.fireMousePressed(MouseButton.SECONDARY);
assertEquals(0, count);
mouse.fireMouseClicked(MouseButton.SECONDARY);
assertEquals(0, count);
mouse.fireMouseReleased(MouseButton.SECONDARY);
assertEquals(0, count);
ContextMenuEventFirer.fireContextMenuEvent(btn);
assertEquals(1, count);
}
static class MyButton extends Button {
MyButton(String text) {
super(text);
}
void setHoverPseudoclassState(boolean b) {
setHover(b);
}
}
List<Stop> getStops(Button button) {
Skin skin = button.getSkin();
Region region = (Region)skin.getNode();
List<BackgroundFill> fills = region.getBackground().getFills();
BackgroundFill top = fills.get(fills.size()-1);
LinearGradient topFill = (LinearGradient)top.getFill();
return topFill.getStops();
}
@Test
public void testRT_23207() {
HBox hBox = new HBox();
hBox.setSpacing(5);
hBox.setTranslateY(30);
MyButton red = new MyButton("Red");
red.setStyle("-fx-base: red;");
MyButton green = new MyButton("Green");
green.setStyle("-fx-base: green;");
hBox.getChildren().add(red);
hBox.getChildren().add(green);
Scene scene = new Scene(hBox, 500, 300);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
Toolkit.getToolkit().firePulse();
List<Stop> redStops0 = getStops(red);
List<Stop> greenStops0 = getStops(green);
red.setHoverPseudoclassState(true);
Toolkit.getToolkit().firePulse();
List<Stop> redStops1 = getStops(red);
List<Stop> greenStops1 = getStops(green);
red.setHoverPseudoclassState(false);
green.setHoverPseudoclassState(true);
Toolkit.getToolkit().firePulse();
List<Stop> redStops2 = getStops(red);
List<Stop> greenStops2 = getStops(green);
green.setHoverPseudoclassState(false);
Toolkit.getToolkit().firePulse();
List<Stop> redStops3 = getStops(red);
List<Stop> greenStops3 = getStops(green);
assertFalse(redStops0.equals(redStops1));
assertTrue(redStops0.equals(redStops2));
assertTrue(redStops0.equals(redStops3));
assertTrue(greenStops0.equals(greenStops1));
assertFalse(greenStops0.equals(greenStops2));
assertTrue(greenStops0.equals(greenStops3));
}
}
