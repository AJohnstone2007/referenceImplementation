package javafx.scene.control.skin;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
public class ColorPickerPaletteShim {
private static ColorPalette getColorPalette(ColorPicker cp) {
ColorPickerSkin cpSkin = (ColorPickerSkin)cp.getSkin();
return (ColorPalette)cpSkin.getPopupContent();
}
public static GridPane getColorGrid(ColorPicker cp) {
ColorPalette pal = getColorPalette(cp);
return pal.getColorGrid();
}
public static PopupControl getPopup(ColorPicker cp) {
ColorPickerSkin cpSkin = (ColorPickerSkin)cp.getSkin();
return cpSkin.getPopup();
}
public static Hyperlink ColorPallette_getCustomColorLink(ColorPicker cp) {
return getColorPalette(cp).customColorLink;
}
public static Stage ColorPallette_getCustomColorDialog(ColorPicker cp) {
ColorPalette pal = getColorPalette(cp);
if (pal.customColorDialog != null) return pal.customColorDialog.getDialog();
return null;
}
}
