package javafx.scene.control;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.control.skin.ColorPickerSkin;
public class ColorPicker extends ComboBoxBase<Color> {
public static final String STYLE_CLASS_BUTTON = "button";
public static final String STYLE_CLASS_SPLIT_BUTTON = "split-button";
private ObservableList<Color> customColors = FXCollections.<Color>observableArrayList();
public final ObservableList<Color> getCustomColors() {
return customColors;
}
public ColorPicker() {
this(Color.WHITE);
}
public ColorPicker(Color color) {
setValue(color);
getStyleClass().add(DEFAULT_STYLE_CLASS);
}
@Override protected Skin<?> createDefaultSkin() {
return new ColorPickerSkin(this);
}
private static final String DEFAULT_STYLE_CLASS = "color-picker";
}
