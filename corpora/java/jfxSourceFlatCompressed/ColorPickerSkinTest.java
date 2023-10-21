package test.javafx.scene.control.skin;
import com.sun.javafx.scene.SceneHelper;
import test.com.sun.javafx.scene.control.infrastructure.KeyEventFirer;
import test.com.sun.javafx.scene.control.infrastructure.MouseEventFirer;
import test.com.sun.javafx.scene.control.infrastructure.MouseEventGenerator;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.skin.ColorPickerPaletteShim;
import javafx.scene.control.skin.ColorPickerSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
public class ColorPickerSkinTest {
private ColorPicker colorPicker;
private Toolkit tk;
private Stage stage;
@Before public void setup() {
tk = Toolkit.getToolkit();
colorPicker = new ColorPicker();
Scene scene = new Scene(new VBox(20), 800, 600);
VBox box = (VBox)scene.getRoot();
box.getChildren().add(colorPicker);
stage = new Stage();
stage.setScene(scene);
stage.show();
tk.firePulse();
}
@Test public void ensureCanSelectColorFromPalette() {
final MouseEventGenerator generator = new MouseEventGenerator();
ColorPickerSkin skin = (ColorPickerSkin)colorPicker.getSkin();
assertTrue(skin != null);
colorPicker.show();
tk.firePulse();
assertTrue(colorPicker.isShowing());
GridPane grid = ColorPickerPaletteShim.getColorGrid(colorPicker);
double xval = grid.getBoundsInLocal().getMinX();
double yval = grid.getBoundsInLocal().getMinY();
Scene paletteScene = ColorPickerPaletteShim.getPopup(colorPicker).getScene();
paletteScene.getWindow().requestFocus();
SceneHelper.processMouseEvent(paletteScene,
generator.generateMouseEvent(MouseEvent.MOUSE_PRESSED, xval+85, yval+65));
SceneHelper.processMouseEvent(paletteScene,
generator.generateMouseEvent(MouseEvent.MOUSE_RELEASED, xval+85, yval+65));
tk.firePulse();
assertEquals(colorPicker.getValue().toString(), "0x330033ff");
}
@Test public void testEscapeClosesCustomColorDialog() {
ColorPickerSkin skin = (ColorPickerSkin)colorPicker.getSkin();
assertTrue(skin != null);
colorPicker.show();
tk.firePulse();
assertTrue(colorPicker.isShowing());
Hyperlink link = ColorPickerPaletteShim.ColorPallette_getCustomColorLink(colorPicker);
Scene paletteScene = ColorPickerPaletteShim.getPopup(colorPicker).getScene();
paletteScene.getWindow().requestFocus();
Hyperlink hyperlink = ColorPickerPaletteShim.ColorPallette_getCustomColorLink(colorPicker);
MouseEventFirer mouse = new MouseEventFirer(hyperlink);
mouse.fireMousePressAndRelease();
mouse.dispose();
Stage dialog = ColorPickerPaletteShim.ColorPallette_getCustomColorDialog(colorPicker);
assertNotNull(dialog);
assertTrue(dialog.isShowing());
dialog.requestFocus();
tk.firePulse();
KeyEventFirer keyboard = new KeyEventFirer(dialog);
keyboard.doKeyPress(KeyCode.ESCAPE);
tk.firePulse();
assertTrue(!dialog.isShowing());
}
}
